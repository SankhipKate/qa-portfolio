# CountryMatcher — Production QA & Engineering Case Study

## Overview

CountryMatcher is a production immigration eligibility matching product that I design and maintain.

A user completes one profile questionnaire. The application evaluates that profile against structured migration requirements for all active countries and returns country- and route-level results.

The project is included in this QA portfolio because its main challenge is maintaining consistent, testable behavior across a growing set of country-specific requirements without turning the engine into a collection of country-specific exceptions.

## Product Links

- Live application: https://sankhipkate.github.io/countrymatcher/
- Source repository: https://github.com/SankhipKate/countrymatcher

## My Role

I work across the product lifecycle:

- requirements analysis;
- research workflow design;
- data-contract design;
- testability analysis;
- edge-case discovery;
- validation rules;
- regression design;
- automated tests;
- release verification;
- CI/CD checks;
- production behavior review.

AI assistants are used as QA and development tools for requirements analysis, edge-case discovery, test generation, code review, data validation, and investigation. Final project rules and release decisions remain explicit and testable in the repository.

## Core QA Challenge

CountryMatcher contains many country-specific facts but should behave as one product.

A weak implementation would gradually add special conditions directly into the matching engine. Instead, the project separates:

1. **Country facts and requirements** — structured Research Package data.
2. **Matching behavior** — one generic evaluation engine.
3. **Presentation behavior** — shared result and UI logic.
4. **Validation** — schemas, integrity checks, automated tests, and release gates.

This is also a QA decision: new countries must fit a shared contract and can be regression-tested against common behavior.

## Test Strategy

### Schema and Contract Validation

Country packages are validated against shared schema and runtime contracts.

Typical risks covered:

- missing required fields;
- unsupported operators;
- inconsistent package metadata;
- malformed family scenarios;
- invalid financial requirement structures;
- unexpected publishability state.

### Generic Engine Testing

The matching engine is tested independently from country presentation.

Coverage includes:

- financial capabilities;
- income and savings evidence;
- family composition;
- relationship scenarios;
- long-term-path handling;
- application methods;
- display-only conditions;
- conditions requiring a separate legal basis.

The goal is to prove that the same requirement type behaves consistently across countries.

### Country Package Regression

Country-specific tests protect researched country packages and their production activation.

When a new country or route is added, tests verify that it remains compatible with the shared contract and expected runtime rules.

### Cross-Country Regression

A new country can expose a gap in the generic engine.

The QA question is whether the newly discovered behavior should become:

- a reusable generic capability;
- a data-model improvement;
- a validation rule;
- a research clarification.

The preferred result is a reusable rule rather than a one-country patch.

### User Flow and Presentation

Automated coverage also protects behavior such as:

- questionnaire state;
- result generation;
- free/full-access funnel states;
- country switching;
- result headlines;
- family-result presentation;
- financial explanations;
- analytics consent behavior;
- release/version presentation.

## Shift-Left and Requirements Analysis

A large part of the QA work happens before implementation.

For a new requirement I check questions such as:

- Is the requirement measurable from the questionnaire?
- Does the source support a hard threshold or only a practical estimate?
- Is the condition required for eligibility or only informative?
- Does family eligibility depend on relationship formalization?
- Does a financial requirement accept income, savings, investment, or only one evidence type?
- Should the behavior live in the generic engine or only in country data?
- What happens when the questionnaire does not collect enough information to decide?

These questions convert research ambiguity into explicit product behavior before release.

## Release Verification

The repository has one canonical release verifier:

`bash ./verify`

The same release gate is used locally and in GitHub Actions. It coordinates package validation, automated tests, JavaScript syntax checks, dependency setup, and repository-specific release checks.

Using one release gate reduces the risk of local and CI verification drifting apart.

## Current Scale

As of this case-study update:

- 19 countries are active in production;
- production version is `19.0.1`;
- active countries share one generic matching engine;
- country research is stored in structured packages;
- GitHub Actions uses the same canonical verifier as local development;
- the application is deployed through GitHub Pages.

CountryMatcher is actively developed, so the live repository is the source of truth for the current country count and version.

## What This Demonstrates

From a QA perspective, CountryMatcher demonstrates:

- requirements analysis;
- Shift-Left QA;
- testability-driven architecture;
- complex business-rule validation;
- data-driven testing;
- schema and integrity validation;
- regression strategy;
- negative and edge-case analysis;
- automated testing;
- CI/CD release gates;
- production release verification;
- AI-assisted QA workflows;
- maintaining consistency as product scope grows.

## Why It Is in a QA Portfolio

CountryMatcher is not included simply because I built a website.

It is included because the project requires the same QA skills that matter in complex production systems: turning ambiguous requirements into explicit rules, separating data problems from logic problems, designing reusable validation, finding cross-feature regression risks, protecting behavior with automated tests, and maintaining release confidence as scope grows.
