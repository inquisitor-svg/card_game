# German Learning Website

A Java + Spring Boot + React learning website for German learners.

## Features

- Placement test with German-to-Chinese and Chinese-to-German translation prompts.
- Flexible grading based on normalized text, accepted meanings, keywords, and similarity instead of one fixed answer.
- Study time tracking with weekly progress and practical learning suggestions.
- Listening, speaking, reading, and shadowing practice modules.
- Optional external speech/AI evaluation hook via backend configuration.

## Frontend Template Reference

The React dashboard layout is custom-built for this app, with visual inspiration from Creative Tim's free Material Tailwind Dashboard React template:
https://www.creative-tim.com/product/material-tailwind-dashboard-react

## Project Structure

```text
backend/   Spring Boot REST API
frontend/  React + Vite learning dashboard
docs/      Chinese project documentation
crawler/   CEFR German vocabulary crawler
```

Chinese file-by-file guide: `docs/文件说明.md`

## Run Backend

The backend targets Java 8 with Spring Boot 2.7.x.

```bash
cd backend
mvn spring-boot:run
```

Backend API: `http://localhost:8080/api`

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:5173`

## Run With Docker

Build and start the full stack:

```bash
docker compose up --build
```

Open:

```text
http://localhost:5173
```

Services:

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080/api`

Stop containers:

```bash
docker compose down
```

## Crawl CEFR Vocabulary

Run the demo crawler with a local sample page:

```bash
python crawler/cefr_word_crawler.py --sources crawler/sources.example.json --output data/vocabulary
```

On Windows, if `python` is not available, try:

```bash
py crawler/cefr_word_crawler.py --sources crawler/sources.example.json --output data/vocabulary
```

Crawler guide: `crawler/README.md`

## External API Hook

For production-level semantic grading or speech scoring, configure an external API endpoint:

```bash
EXTERNAL_EVALUATION_URL=https://your-provider.example/evaluate
EXTERNAL_EVALUATION_KEY=your_api_key
```

The app works without this configuration and falls back to local heuristic scoring.
