const axios = require('axios');
const fs = require('fs');
const path = require('path');
const { JSDOM } = require('jsdom');

const DAYS = [
  "आईतवार",
  "सोमवार",
  "मंगलवार",
  "बुधवार",
  "बिहीवार",
  "शुक्रवार",
  "शनिवार",
];

const safeText = (node, selector) => {
  try {
    const el = selector ? node.querySelector(selector) : node;
    return (el?.textContent || '').trim();
  } catch {
    return '';
  }
};

const safeAttr = (node, selector, attr) => {
  try {
    const el = selector ? node.querySelector(selector) : node;
    return (el?.getAttribute(attr) || '').trim();
  } catch {
    return '';
  }
};

const ensureDir = (dirPath) => {
  if (!fs.existsSync(dirPath)) {
    fs.mkdirSync(dirPath, { recursive: true });
  }
};

const readIfExists = (filePath) => {
  if (fs.existsSync(filePath)) {
    try {
      const raw = fs.readFileSync(filePath, 'utf-8');
      return JSON.parse(raw);
    } catch (e) {
      // fall through and rescrape if parse fails
    }
  }
  return null;
};

const DATA_DIR = path.join(__dirname, '..', 'data');

const _fetchData = async (year, month) => {
  try {
    console.log('fetching', year, month);
    const yearDir = path.join(DATA_DIR, String(year));
    const outPath = path.join(yearDir, `${month}.json`);

    // return cached if exists
    const cached = readIfExists(outPath);
    if (cached) {
      console.log('cache hit');
      return cached;
    }

    const { data: html } = await axios({
      method: 'post',
      url: 'https://nepalicalendar.rat32.com/index_nep.php',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      data: `selYear=${year}&selMonth=${month}&viewCalander=View+Calander`,
    });

    let dom = new JSDOM(html);
    const doc = dom.window.document;

    // metadata
    const metadata = {
      en: safeText(doc, '#entarikYr'),
      np: safeText(doc, '#yren'),
    };

    // holidays & festivals
    try { doc.querySelector('#holi b')?.remove(); } catch {}
    try { doc.querySelector('#holi a')?.remove(); } catch {}
    const holiFest = safeText(doc, '#holi')
      .split('\n')
      .map((s) => s.trim())
      .filter(Boolean);

    // marriage date
    try { doc.querySelector('#bibah b')?.remove(); } catch {}
    const marriage = safeText(doc, '#bibah')
      .split('\n')
      .map((s) => s.trim())
      .filter(Boolean);

    // bratabandha date
    try { doc.querySelector('#bratabandha b')?.remove(); } catch {}
    const bratabandha = safeText(doc, '#bratabandha')
      .split('\n')
      .map((s) => s.trim())
      .filter(Boolean);

    const days = [];
    let dayCount = 1; // 1..7
    const cells = Array.from(doc.querySelectorAll('.cells'));
    cells.forEach((cell) => {
      const innerDom = new JSDOM(cell.innerHTML);
      const innerDoc = innerDom.window.document;

      const nday = safeText(innerDoc, '#nday'); // Nepali day number
      const eday = safeText(innerDoc, '#eday'); // English day number
      const tithi = safeText(innerDoc, '#dashi');
      const fest = safeText(innerDoc, '#fest');
      const ndFontColor = safeAttr(innerDoc, '#nday font', 'color');
      const isHoliday = (ndFontColor || '').toLowerCase() === 'red';

      days.push({
        n: nday,
        e: eday,
        t: tithi,
        f: fest,
        h: isHoliday,
        d: dayCount,
        wd: DAYS[(dayCount - 1) % 7],
      });

      dayCount += 1;
      if (dayCount > 7) dayCount = 1;
    });

    const result = { metadata, days, holiFest, marriage, bratabandha };

    // write to file
    ensureDir(yearDir);
    fs.writeFileSync(outPath, JSON.stringify(result, null, 2), 'utf-8');
    console.log('Done', year, month);

    return result;
  } catch (e) {
    console.log('scrape error', e?.message || e);
    throw e;
  }
};

const scrapeData = async (year, month) => {
  const data = await _fetchData(year, month);
  console.log('Data fetch completed');
  return data;
};

module.exports = { scrapeData };
