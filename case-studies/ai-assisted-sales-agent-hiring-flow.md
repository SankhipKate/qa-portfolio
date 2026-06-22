# Case Study: AI-Assisted Sales Agent Hiring Flow

## Overview

This is an anonymized case study based on my work as a QA Engineer in a fintech mass-hiring flow for Sales Ambassadors.

The product supported a multi-step hiring process: candidates applied through a web form, passed automated filtering, moved through video and test steps, went through AI recruiter screening, submitted onboarding documents, passed HR check, signed an employment agreement, scheduled training, and moved toward deployment.

The flow contained many conditional branches depending on candidate answers, location, vacancy availability, candidate volume, test results, document status, and notification timing.

## Product Context

Candidates could enter the flow through several channels:

- advertising campaigns;
- referral links;
- fast-track links from internal field teams.

After submitting the application form, candidates were filtered automatically based on their answers. Suitable candidates then moved through different paths depending on their city, province, available vacancies, and candidate-to-vacancy ratio.

If there were available vacancies and the candidate matched the requirements, the candidate could move toward screening. If there were no suitable vacancies or too many candidates for the current vacancy volume, the candidate could be placed into a buffer or waitlist and invited later when a relevant vacancy became available.

The later stages included video and test steps, AI recruiter interview, onboarding information form submission, HR document review, employment agreement signing, training scheduling, and deployment preparation.

## Business Flow

The main flow included:

- application form submission;
- candidate filtering based on form answers;
- city and province matching;
- vacancy availability check;
- buffer / waitlist logic;
- video and test step;
- AI recruiter interview;
- score-based decision after interview;
- OIF submission;
- HR document check;
- resubmission of invalid documents;
- EA signing;
- training scheduling;
- deployment preparation;
- candidate and internal team notifications.

## My Role

I was the sole QA Engineer on the project, responsible for testing the hiring flow, documenting business logic, creating and maintaining QA documentation, validating status transitions, testing notifications, and supporting defect investigation.

## Main QA Challenges

Key QA challenges included:

- many candidate paths depending on city, province, vacancy availability, and candidate volume;
- buffer and waitlist logic;
- fast-track flow;
- AI recruiter interview flow;
- score-based decisions;
- status transitions across the hiring pipeline;
- OIF submission and document resubmission logic;
- HR check statuses;
- SMS and email notification timing;
- UAT limitations;
- regression coverage for a changing business process.

## What I Tested

The testing scope included:

- application form validation;
- positive and negative candidate paths;
- candidate filtering based on form answers;
- city and province matching;
- vacancy availability logic;
- multiple-cities selection flow;
- buffer and release-from-buffer scenarios;
- video and test submission;
- AI recruiter interview invitation;
- interview result handling;
- OIF document submission;
- resubmission of invalid documents after HR check;
- HR check status handling;
- training scheduling;
- SMS and email notification logic;
- candidate movement across Jira statuses.

## QA Artifacts

QA artifacts I created and maintained included:

- flow documentation;
- business logic notes;
- checklists;
- test cases in TestOps;
- UAT testing notes;
- notification testing notes;
- OIF resubmission testing documentation;
- regression coverage proposal;
- ideal testing process proposal.

## Testing Process Improvement

I also documented a proposed testing process for this product area.

The proposed approach separated testing into two stages:

1. Full UI testing on UAT  
   The goal was to cover business logic, all meaningful application form input combinations, negative scenarios, and flow variations depending on city, province, vacancy availability, and candidate source.

2. Limited production smoke after release  
   The goal was to verify that the released form and integrations worked correctly in production. Because captcha was enabled in production, the automated part could stop before captcha, and the tester would complete captcha manually and then verify the next pages and critical flow continuation.

This helped make the testing strategy more realistic and better aligned with the limitations of UAT and production environments.

## Result

As a result of the QA work:

- the hiring flow became better documented;
- business logic became easier to review and discuss;
- complex candidate paths became easier to validate;
- regression testing became more structured;
- notification scenarios became easier to track;
- defects became easier to reproduce and investigate;
- QA coverage became more visible for the product and engineering teams.

## Confidentiality Note

This case study is fully anonymized.

It does not include internal URLs, credentials, real candidate data, production screenshots, real trace IDs, private API endpoints, financial data, or confidential business information.
