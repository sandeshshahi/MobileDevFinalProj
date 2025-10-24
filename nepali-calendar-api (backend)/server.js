const express = require('express');
const path = require('path');
const fs = require('fs');
const { scrapeData } = require('./src/scraper');

const app = express();
const PORT = process.env.PORT || 3000;

app.get('/health', (_req, res) => {
  res.json({ status: 'ok' });
});

// Example: GET /api/scrape?year=2081&month=1
app.get('/api/scrape', async (req, res) => {
  try {
    const year = parseInt(req.query.year, 10);
    const month = parseInt(req.query.month, 10);

    if (!year || !month) {
      return res.status(400).json({ error: 'Missing or invalid query params: year, month' });
    }

    const result = await scrapeData(year, month);
    res.json(result);
  } catch (err) {
    console.error('Scrape error:', err);
    res.status(500).json({ error: 'Failed to scrape data' });
  }
});

// Serve any saved JSON files statically (optional convenience)
app.use('/data', express.static(path.join(__dirname, 'data')));

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
