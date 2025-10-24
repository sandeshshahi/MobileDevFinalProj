# Nepali Calendar API

A small Express API that scrapes Nepali calendar data and returns JSON for a given year and month.

## Endpoints

- GET `/health` — simple health check
- GET `/api/scrape?year=2081&month=1` — scrape and return JSON for the given Nepali calendar year and month.
  - Also caches results to `./data/<year>/<month>.json`
  - Serves cached files statically under `/data` (e.g., `/data/2081/1.json`)

## Install and run

Using npm (recommended if npm is available):

```bash
npm install
npm start
# Server runs at http://localhost:3000
```

If npm is unavailable, use pnpm via Corepack (Node 18+):

```bash
corepack enable
corepack prepare pnpm@9 --activate
pnpm install
pnpm run start
```

## Response shape

```json
{
  "metadata": { "en": "...", "np": "..." },
  "days": [
    {
      "n": "...",
      "e": "...",
      "t": "...",
      "f": "...",
      "h": true,
      "d": 1,
      "wd": "आईतवार"
    }
  ],
  "holiFest": ["..."],
  "marriage": ["..."],
  "bratabandha": ["..."]
}
```

## Notes

- The scraper targets `https://nepalicalendar.rat32.com/index_nep.php` and may break if the site structure changes.
- Results are cached on disk in the `data/` folder.
- The project uses CommonJS modules to align with `require(...)` in the scraper.
- `parse5@6` is enforced to avoid ESM-related runtime issues with `jsdom`.
