# QA Engineering Portfolio

This repository contains practical examples of my QA work: production-oriented case studies, test strategy, API and backend testing, SQL validation, bug reporting, test design, and automation samples.

## About Me

I am a QA Engineer with 3 years of commercial experience in FinTech and AI/LLM products.

My strongest areas are API and backend testing, complex business workflows, SQL/PostgreSQL validation, regression and integration testing, requirements analysis, Shift-Left QA, test documentation, and evidence-based defect investigation.

I have worked as a sole QA Engineer, building QA processes and coverage from scratch and working closely with Product, Development, and business stakeholders.

## Featured Work

### 1. CountryMatcher — Production QA & Engineering Case Study

CountryMatcher is a production immigration eligibility matching product that I design and maintain.

The project uses structured country data and one generic matching engine instead of accumulating country-specific matching logic. From a QA perspective, it demonstrates requirements analysis, testability-driven architecture, schema and integrity validation, regression design, automated tests, CI/CD release gates, and production release verification.

At the time of this portfolio update:

- 19 countries are active in production;
- production version is `19.0.1`;
- country requirements are stored in structured Research Package data;
- active countries share one generic matching engine;
- automated tests cover engine behavior, country packages, family and financial rules, funnel behavior, access flow, analytics consent, and UI contracts;
- the same canonical verifier is used locally and in GitHub Actions;
- the application is deployed through GitHub Pages.

**Links**

- [QA & Engineering Case Study](case-studies/countrymatcher-production-qa.md)
- [Live Product](https://sankhipkate.github.io/countrymatcher/)
- [Source Repository](https://github.com/SankhipKate/countrymatcher)

### 2. FinTech Hiring Flow — End-to-End QA Case Study

An anonymized commercial QA case based on a multi-step hiring product with complex routing, AI recruiter screening, document processing, notifications, backend state transitions, and regression risks.

**Artifacts**

- [Case Study](case-studies/ai-assisted-sales-agent-hiring-flow.md)
- [Test Strategy](test-documentation/sa-hiring-test-strategy.md)
- [Critical Path Test Cases](test-cases/sa-hiring-critical-path-test-cases.md)
- [Functional Checklist](checklists/sa-hiring-functional-checklist.md)
- [Sample Bug Reports](bug-reports/sa-hiring-sample-bug-reports.md)
- [SQL Data Validation Queries](sql/sa-hiring-data-validation-queries.md)
- [API Testing Artifact](api-testing/sa-hiring-api-testing.md)
- [Postman Collection](api-testing/postman/SA-Hiring-API.postman_collection.json)

## API Testing

The API section demonstrates QA coverage beyond UI validation.

- [SA Hiring API Testing Artifact](api-testing/sa-hiring-api-testing.md)
- [Postman Collection](api-testing/postman/SA-Hiring-API.postman_collection.json)
- [Postman Environment](api-testing/postman/SA-Hiring-Portfolio.postman_environment.json)

The public examples use sanitized names, placeholder endpoints, and synthetic test data so the testing approach can be shown without exposing production systems.

## Automation Samples

### Kotlin + Selenium

- [SA Hiring Form Fill Automation Sample](automation/sa-hiring-form-fill-kotlin) — UI automation sample based on a candidate-facing hiring flow. It demonstrates dependent form controls, file upload, form submission, and UI state validation with Kotlin, JUnit 5, and Selenium WebDriver.

### Python + pytest

- [Shop Discount](automation/python/pytest/shop-discount) — a small pytest exercise demonstrating parametrized tests, HTML test reports, and code coverage reporting.

## Core QA Areas

- Manual, functional, regression, integration, smoke, and exploratory testing
- API and backend testing
- REST API, Postman, Swagger, JSON
- SQL / PostgreSQL data validation
- Kafka and microservice validation
- Complex business-rule testing
- Requirements analysis and Shift-Left QA
- Test design and test documentation
- Evidence-based bug reporting
- AI / LLM validation
- Web and mobile testing
- Git / CI/CD
- Kotlin / Java basics
- Python / pytest basics

## Portfolio Structure

| Section | Description |
|---|---|
| `case-studies/` | Production and anonymized commercial QA case studies |
| `api-testing/` | API test scenarios, negative testing, Postman examples, and backend validation |
| `bug-reports/` | Structured bug reports with technical evidence |
| `test-cases/` | Critical-path and business-flow test cases |
| `checklists/` | Functional and regression checklists |
| `sql/` | SQL data-validation examples |
| `automation/` | Kotlin/Selenium and Python/pytest automation samples |
| `test-documentation/` | Test strategy and broader QA documentation |

## Confidentiality

Commercial QA materials in this repository are anonymized and sanitized.

They do not contain real customer data, production credentials, private API endpoints, production URLs, internal authentication data, or confidential source documents.

Portfolio-safe examples may use synthetic identifiers, placeholder endpoints, and illustrative request/response structures where publishing original commercial data would be inappropriate.
