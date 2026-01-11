# AI Portfolio Commentary

A full-stack AI-powered web application that imports an equity portfolio from CSV, computes portfolio analytics, and generates structured, non-advisory commentary using OpenAI.

**Live demo:**  
👉 https://<your-frontend>.onrender.com

---

## Overview

AI Portfolio Commentary is a production-deployed demo showcasing end-to-end full-stack development with AI integration.

Users can:
- Upload an equity portfolio via CSV
- View computed portfolio metrics (weights, concentration, exposure)
- Persist portfolio snapshots to a database
- Generate AI-written portfolio commentary with clear structure and disclaimers
- Revisit historical snapshots and commentary

The project is designed as a **portfolio-ready demo** for:
- Job applications
- Freelance work
- Technical interviews

---

## Key Features

### Portfolio Import & Analytics
- CSV upload (equity holdings)
- Automatic calculation of:
  - Holding weights
  - Concentration metrics
  - Sector, region, and currency exposure
- Snapshot-based persistence for reproducibility

### AI Commentary
- AI-generated portfolio commentary using OpenAI
- Structured sections:
  - Summary
  - Concentration & structure
  - Sector exposure
  - Geography & currency
  - Contextual note
  - Disclaimer (no investment advice)
- Commentary stored with:
  - Prompt version
  - Model identifier

### Persistence & Reliability
- PostgreSQL database
- Flyway migrations
- Snapshots, holdings, and commentary survive restarts

### Production UI
- React + TypeScript frontend
- Snapshot detail view with KPIs and tables
- Commentary generation and display
- Graceful error handling

---

## Architecture

