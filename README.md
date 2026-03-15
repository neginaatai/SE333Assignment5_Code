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



## Reflection

### Manual vs AI-Generated Playwright Tests

#### Overview
For this assignment, I wrote two sets of Playwright UI tests for the DePaul bookstore website-one manually in the `playwrightTraditional` package and one generated using GitHub Copilot with the Playwright MCP tool in the `playwrightLLM` package. Both sets contained 7 test cases covering the same scenarios: homepage loading, earbuds search, category page verification, JBL Black earbuds product page, price verification, add to cart, and cart verification.

#### Writing the Manual Tests
Writing the traditional tests manually required me to first explore the website and understand its structure. I had to identify which URLs were stable, what content appeared on each page, and what selectors would work reliably. For example, I discovered that the site's search bar used hidden inputs that Playwright could not interact with, so I switched to direct URL navigation instead. This debugging process took time but gave me a deep understanding of how the site worked. The manual tests were more focused and precise because I knew exactly what I was testing and why.

#### Using AI to Generate Tests
Using GitHub Copilot with the Playwright MCP tool was significantly faster for generating the initial test structure. Within seconds, Copilot produced a complete test file with 7 tests, helper methods, fallback selectors, and error handling. However, the AI-generated tests had several issues out of the box. Copilot used dynamic selectors that did not match the real website, attempted to find product links by crawling the page rather than using direct URLs, and launched the browser in non-headless mode which broke GitHub Actions CI. I had to guide Copilot through multiple rounds of fixes using follow-up prompts before the tests passed.

#### Key Differences
The most notable difference was reliability vs speed. Manual tests required more upfront effort but produced cleaner, more targeted assertions from the start. The AI-generated tests were produced faster but required significant human intervention to fix selector issues, headless mode configuration, and flaky assertions. The AI also generated much more complex code than necessary, including many helper methods and fallback strategies that added noise without adding value.

Another key difference was understanding. When I wrote tests manually, I understood every line of code and why it was there. With the AI-generated tests, some of the logic was opaque and required careful review to verify correctness.

#### What I Learned
This exercise showed me that AI tools like Copilot are most useful as a starting point rather than a complete solution for UI testing. They can quickly generate boilerplate and structure, but a human tester still needs to verify, refine, and fix the output. The Playwright MCP integration was impressive in how it could understand the task and generate syntactically correct Java code, but it lacked the contextual knowledge of the actual website being tested. In a real project, the best approach would be to use AI to generate the initial test skeleton and then manually refine the selectors and assertions based on real browser behavior.

## Workflow
[View GitHub Actions Workflow](https://github.com/neginaatai/SE333Assignment5_Code/actions/workflows/SE333_CI.yml)

## Status
All GitHub Actions steps passed successfully including static analysis (Checkstyle), all 39 tests, and JaCoCo coverage report. 
