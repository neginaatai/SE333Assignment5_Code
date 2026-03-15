# SE333 Assignment 5

![Build Status](https://github.com/neginaatai/SE333Assignment5_Code/actions/workflows/SE333_CI.yml/badge.svg)

## Overview
This project contains unit, integration, and UI tests for the Amazon shopping cart and DePaul bookstore website.

## Tests
- **AmazonUnitTest** — 15 unit tests using Mockito
- **AmazonIntegrationTest** — 10 integration tests using HSQLDB
- **BookstoreTest** — 7 Playwright UI tests (manual)
- **BookstoreAITest** — 7 Playwright UI tests (AI-generated with GitHub Copilot)

## CI/CD
GitHub Actions runs on every push:
1. Checkstyle static analysis
2. All tests
3. JaCoCo coverage report
