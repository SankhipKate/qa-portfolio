# Test Strategy: SA Hiring Flow

## 1. Purpose

This document describes the testing approach for the SA Hiring flow: mass hiring of Sales Ambassadors through a public candidate-facing application form, automated filtering, vacancy matching, AI recruiter interview, OIF, HR document review, EA signing, training scheduling and deployment preparation.

The strategy demonstrates:

- risk-based testing of a multi-step candidate flow;
- Jira status transitions and backend state validation;
- SMS communication checks;
- SQL-based data validation;
- API and integration checks with trace ID monitoring;
- UAT validation and production smoke testing;
- realistic automation limitations;
- evidence-based bug reporting;
- non-functional considerations around performance, reliability and access-control risks.

## 2. Scope

Testing covered the following areas.

### Candidate Journey

- Public application form for Sales Ambassador candidates
- Candidate sources:
  - advertising campaign
  - referral link from existing Sales Ambassador
  - TSH fast-track form
- Initial candidate filtering
- `Video/Test`
- City / province / vacancy matching
- `Buffer` / waitlist
- Release from `Buffer` when a relevant vacancy appears
- `Screening Schedule`
- AI recruiter interview
- `OIF`
- `HR Check`
- `Document Resubmission`
- `EA Signing`
- `Training Scheduling`
- Training no-show handling
- `To Deploy`
- `Deployed`

### Backend and Integration

- Jira ticket creation and lifecycle transitions
- SMS notification triggers and template selection
- Candidate data persistence
- Vacancy matching logic
- Buffer release criteria
- Document status state machine
- Training attendance result saving

### Data and Reporting

- Candidate record creation after application submission
- Data validation via SQL queries
- SMS delivery tracking
- Training attendance logging
- No-show recording

## 3. Out of Scope for Public Portfolio

The public version of this artifact does not disclose:

- real production URLs or internal domains;
- database credentials or connection strings;
- real candidate PII;
- production trace IDs;
- actual request logs;
- production screenshots;
- private API endpoints;
- exact score thresholds;
- confidential business rules;
- Workforce Planner as a tested portfolio case;
- SMS provider API internals.

Workforce Planner is not used as a tested portfolio case in this repository.

## 4. Testing Goals

Key testing goals:

1. Valid candidate with eligible age, experience and location reaches `OIF` and `HR Check`.
2. Candidate below `<min_age>` or without `<min_experience>` does not move further than allowed by filtering rules.
3. Fast-track candidate referred by TSH skips `Video/Test` and moves directly to `OIF`.
4. Candidate without a relevant vacancy moves to `Buffer`.
5. Candidate leaves `Buffer` when a relevant vacancy appears and candidate-to-vacancy ratio allows release.
6. Each status transition triggers the expected SMS template with candidate-specific link.
7. SMS link opens the correct candidate-facing page with candidate-specific data.
8. Jira ticket contains the fields required for next-stage processing.
9. Documents marked as `Verified` are not shown on the document resubmission form.
10. Documents marked as `Invalid` are available for resubmission.
11. Training no-show is saved in database and Jira.
12. Candidate can select a new training slot after no-show.
13. Application form data, Jira ticket data and database values are consistent.

## 5. Risk-Based Test Approach

Testing was prioritized by candidate impact and downstream business risk.

### Critical Risks

- Valid candidate cannot submit application form.
- Candidate remains in `Buffer` after a relevant vacancy appears.
- Fast-track candidate is routed to `Video/Test`.
- Jira ticket is created without fields required by HR or TSH.
- Candidate cannot proceed to `OIF` after successful AI recruiter interview.

### High Risks

- Candidate receives wrong SMS template.
- Verified document is shown on document resubmission form.
- Training no-show result is not saved.
- OIF data is not saved.
- Candidate status in Jira does not match backend status.

### Medium Risks

- Timezone is displayed incorrectly in training SMS.
- Province/city mismatch affects vacancy matching.
- Duplicate candidate record is created after repeated form submit.
- SMS link contains outdated candidate status token.

### Risk-Based Priority Matrix

| Risk Level | Scope | Test Type | Automation |
| --- | --- | --- | --- |
| Critical | Critical path, fast-track, Buffer release | Functional, API, SQL | UAT and smoke |
| High | Document flow, SMS, no-show | Functional, SQL | UAT |
| Medium | Edge cases, timezone, repeated submissions | Exploratory, SQL | Manual and spot checks |
| Low | UI cosmetics, minor text issues | UAT only | Not automated |

## 6. Shift-Left Activities

Before and during development, QA activities focused on requirement gaps and unclear transitions.

### Flow Mapping

A state diagram was created for the full candidate flow.

Identified gap:

- Requirements did not describe what happens when candidate opens an old SMS link after candidate status changed.

Resolution:

- Candidate-facing endpoints should validate candidate status before opening the linked form.

### Threshold Clarification

Identified gap:

- Experience rules were not clearly defined.

Resolution:

- `<min_experience>` and related role rules were clarified with product stakeholders.

### Buffer Release Rules

Identified gap:

- "Relevant vacancy appears" was not specific enough.

Resolution:

- Release criteria were clarified: city match, province match, available slot count and candidate-to-vacancy ratio.
- Buffer release job interval was clarified as 5 minutes.

### Document Status State Machine

Document lifecycle was mapped:

```text
Submitted -> Processing -> Verified
Submitted -> Processing -> Invalid
Invalid -> Candidate resubmits -> Submitted -> Verified
Verified -> No resubmission requested
```

Identified gap:

- Requirements did not describe what happens if candidate tries to resubmit a verified document.

Resolution:

- Verified documents should not be shown in document resubmission form.

### SMS Template Matrix

Each candidate status was mapped to SMS template and link destination:

| Candidate Status | SMS Purpose | Link Destination |
| --- | --- | --- |
| `Video/Test` | video/test SMS | role awareness video and test |
| `Buffer` | waitlist / matching SMS | waitlist or read-only status page |
| `Screening Schedule` | AI recruiter interview SMS | interview waiting room |
| `OIF` | OIF SMS | employee information form |
| `Document Resubmission` | document resubmission SMS | document resubmission form |
| `EA Signing` | EA signing SMS | EA signing page |
| `Training Scheduling` | training scheduling SMS | training slot selection |
| `Deployed` | deployed feedback SMS | feedback form |

### No-Show Recovery

Identified gap:

- It was unclear whether candidate can book another slot after missing training.

Resolution:

- After no-show, candidate can return to `Training Scheduling` and select another available training slot.

## 7. Test Levels and Types

### 7.1 Functional Testing

#### Application Form Testing

```text
Scenario: Valid candidate submits application
1. Open public candidate-facing application form.
2. Fill required fields: first name, last name, email, phone, age, city, province, work experience, sales experience, availability to start, source, consent checkbox.
3. Submit form.
4. Verify success screen.
5. Verify candidate record is created in database.
6. Verify Jira ticket is created.
7. Verify candidate status is `Video/Test`.
8. Verify video/test SMS is sent.

Expected:
- Candidate application is submitted successfully.
- Candidate record exists in database.
- Jira ticket contains candidate name, phone number, email, city, province, source and work experience.
- Candidate status is `Video/Test`.
- Video/test SMS contains candidate-specific link.
```

#### Video/Test Flow

```text
Scenario: Candidate passes Video/Test and selected city has vacancy
1. Candidate opens link from video/test SMS.
2. Candidate watches role awareness video.
3. Candidate opens role awareness test.
4. Candidate submits test with score >= <pass_score_threshold>.
5. Verify candidate status.
6. Verify SMS history.

Expected:
- Test result is saved.
- Repeated test submission for the same application is unavailable.
- Candidate status changes to `Screening Schedule`.
- AI recruiter interview SMS is sent.
```

```text
Scenario: Candidate fails Video/Test
1. Candidate submits role awareness test with score < <pass_score_threshold>.
2. Verify candidate status.
3. Verify SMS history.
4. Verify Jira ticket.

Expected:
- Candidate status becomes `Rejected`.
- Test rejection SMS is sent.
- Rejection reason is saved in Jira ticket.
- Candidate does not move to `Screening Schedule`.
```

#### AI Recruiter Interview Flow

```text
Scenario: Candidate passes AI recruiter interview
1. Candidate opens link from AI recruiter interview SMS.
2. Candidate enters interview waiting room.
3. Candidate completes AI recruiter interview.
4. Interview score is saved as >= <pass_score_threshold>.
5. Verify candidate status.
6. Verify SMS history.

Expected:
- Interview result is saved.
- Candidate status becomes `OIF`.
- OIF SMS is sent.
- OIF SMS contains candidate-specific OIF link.
```

#### Buffer and Release Logic

```text
Scenario: Candidate enters Buffer when no matching vacancy exists
1. Candidate passes Video/Test in city="<city_1>" where no Sales Ambassador vacancy exists.
2. Verify database candidate status.
3. Verify Jira ticket status.
4. Verify SMS history.

Expected:
- Candidate status becomes `Buffer`.
- Candidate remains linked to selected city and province.
- Jira ticket status is `Buffer`.
- Buffer / waitlist SMS is sent.
```

```text
Scenario: Candidate is released from Buffer when relevant vacancy appears
1. Candidate is in `Buffer`.
2. Admin creates or activates vacancy in candidate selected city and province.
3. Wait 5 minutes for background job to process Buffer candidates.
4. Verify candidate status in database.
5. Verify Jira ticket status.
6. Verify SMS history.

Expected:
- Candidate status changes from `Buffer` to `Screening Schedule`.
- Jira ticket status changes from `Buffer` to `Screening Schedule`.
- AI recruiter interview SMS is sent.
```

#### Document Resubmission

```text
Scenario: Candidate resubmits only document rejected by HR
1. Candidate status is `HR Check`.
2. HR reviews uploaded OIF documents.
3. HR marks one document as `Verified`.
4. HR marks another document as `Invalid`.
5. HR changes candidate status to `Document Resubmission`.
6. Candidate opens document resubmission link from SMS.
7. Candidate uploads corrected file for the rejected document.
8. Candidate submits document resubmission form.
9. Verify candidate status and document records.

Expected:
- Document marked as `Verified` is not shown in document resubmission form.
- Document marked as `Invalid` is shown in document resubmission form.
- Corrected file replaces the rejected document file.
- Candidate status changes back to `HR Check`.
- Jira ticket shows updated file for the previously rejected document.
```

#### Training Scheduling and No-Show

```text
Scenario: Candidate returns to training scheduling after no-show
1. Candidate status is `Training Scheduling`.
2. Candidate books available training slot.
3. Training confirmation SMS is sent.
4. Training reminder SMS is sent 15 minutes before training.
5. Candidate does not attend scheduled training.
6. TSH saves training attendance result as `No-show`.
7. Candidate opens no-show SMS link.
8. Candidate confirms return to training scheduling.
9. Candidate books new available training slot.

Expected:
- Training slot is saved for candidate.
- Training no-show result is saved in Jira ticket.
- No-show SMS is sent.
- Candidate status returns to `Training Scheduling`.
- Candidate can book a new training slot.
- New training confirmation SMS is sent.
```

### 7.2 API and Integration Testing

API and integration checks validated that UI actions and system events created the expected backend state.

#### Application Submission

```http
POST /<application-submission-endpoint>
```

Sanitized request body:

```json
{
  "first_name": "Test",
  "last_name": "Candidate",
  "email": "qa+candidate@example.com",
  "phone": "<test_phone>",
  "city": "<city>",
  "province": "<province>",
  "source": "advertising_campaign"
}
```

Expected checks:

- response status is `201 Created`;
- response contains `<candidate_id>`;
- Jira ticket is created;
- candidate status is `Video/Test`;
- video/test SMS is queued.

#### Video/Test Result

```http
POST /<video-test-result-endpoint>
```

Expected checks:

- passing score moves candidate to `Screening Schedule`;
- failing score moves candidate to `Rejected`;
- rejection reason is saved for failed candidate;
- expected SMS template is selected.

#### Buffer Release

```http
POST /<buffer-release-job-endpoint>
```

Expected checks:

- candidates in `Buffer` are matched against open vacancies;
- city and province match are used;
- candidate-to-vacancy ratio is checked;
- eligible candidate moves to `Screening Schedule`;
- AI recruiter interview SMS is queued.

#### Document Resubmission

```http
POST /<document-resubmission-endpoint>
```

Expected checks:

- only document marked as `Invalid` is updated;
- document marked as `Verified` remains unchanged;
- candidate returns to `HR Check`;
- HR can review the corrected document.

### 7.3 SQL Data Validation

SQL checks were used to validate backend state after key flow actions.

The queries below use sanitized table and column names.

#### Candidate Record Exists After Application Submission

```sql
SELECT
  c.id,
  c.email,
  c.city,
  c.province,
  c.status,
  c.created_at
FROM candidates c
WHERE c.created_at > NOW() - INTERVAL '5 minutes'
  AND c.email = '<test_email>'
LIMIT 1;
```

Expected result:

- one row is returned;
- status is `Video/Test`;
- city and province match submitted form values.

#### Candidate Status Matches Jira Status

```sql
SELECT
  c.id,
  c.status AS db_status,
  jt.status AS jira_status,
  c.updated_at,
  jt.updated_at AS jira_updated_at
FROM candidates c
JOIN jira_tickets jt ON c.jira_ticket_id = jt.id
WHERE c.updated_at > NOW() - INTERVAL '1 hour'
  AND c.status <> jt.status;
```

Expected result:

- zero rows are returned.

Rows returned by this query indicate database/Jira status mismatch.

#### Buffer Release Validation

```sql
SELECT
  c.id,
  c.city,
  c.province,
  v.id AS vacancy_id,
  c.held_since,
  c.status,
  EXTRACT(EPOCH FROM (c.updated_at - c.held_since)) / 60 AS minutes_in_buffer
FROM candidates c
LEFT JOIN vacancies v
  ON c.city = v.city
 AND c.province = v.province
WHERE c.status = 'Screening Schedule'
  AND c.held_since IS NOT NULL
  AND c.updated_at > NOW() - INTERVAL '10 minutes';
```

Expected result:

- released candidates have matching vacancy;
- `vacancy_id` is not null.

#### Document Status State Machine

```sql
SELECT
  d.id,
  d.candidate_id,
  d.document_type,
  d.status,
  d.created_at,
  d.resubmitted_at,
  d.verified_at,
  CASE
    WHEN d.status = 'Verified' AND d.resubmitted_at > d.verified_at
      THEN 'ERROR: resubmitted after verification'
    WHEN d.status = 'Invalid' AND d.resubmitted_at IS NULL
      THEN 'WARN: invalid but not resubmitted'
    ELSE 'OK'
  END AS state_check
FROM candidate_documents d
WHERE d.created_at > NOW() - INTERVAL '1 day';
```

Expected result:

- all rows show `OK` or expected `WARN`;
- `ERROR` rows indicate document state machine violation.

#### SMS Sent for Status Transition

```sql
SELECT
  c.id AS candidate_id,
  c.status,
  COUNT(s.id) AS sms_count,
  MAX(s.sent_at) AS last_sms_sent
FROM candidates c
LEFT JOIN sms_history s
  ON c.id = s.candidate_id
 AND s.sent_at > c.status_updated_at - INTERVAL '5 minutes'
WHERE c.status_updated_at > NOW() - INTERVAL '1 hour'
GROUP BY c.id, c.status
HAVING COUNT(s.id) = 0;
```

Expected result:

- zero rows are returned for statuses that require SMS.

Rows returned by this query indicate missing SMS after status transition.

### 7.4 Regression Testing

Regression testing was performed after changes in shared flow logic:

- candidate filtering rules;
- vacancy matching algorithm;
- Buffer release job;
- SMS template selection;
- Jira status transitions;
- OIF submission;
- document resubmission;
- training slot booking;
- no-show handling.

Sample regression focus:

```text
Test: Critical path from valid application to OIF
Setup: candidate city has open vacancy

1. Submit application.
2. Pass Video/Test.
3. Verify status = Screening Schedule.
4. Complete AI recruiter interview.
5. Verify status = OIF.
6. Submit OIF.
7. Verify status = HR Check.

Run after:
- filtering changes
- vacancy matching changes
- SMS template changes
- OIF form changes
```

### 7.5 Exploratory Testing Findings

#### Finding 1: Timezone Bug in SMS Scheduling

```text
Scenario:
Candidate books training at 14:00 local time.

Expected:
SMS shows training time in candidate timezone.

Actual:
SMS shows server timezone.

Impact:
High — candidate may miss training.

Root cause:
SMS template used server time instead of candidate timezone.

Test added:
timezone_sms_scheduling
```

#### Finding 2: Duplicate Candidate on Repeated Form Submit

```text
Scenario:
Candidate submits form, network times out, candidate retries submit.

Expected:
Duplicate prevention blocks second active application or returns existing success state.

Actual:
Two candidate records are created with the same email.

Impact:
Medium — candidate confusion and duplicated Jira tickets.

Root cause:
No idempotency check on form submission.

Test added:
idempotent_form_submission
```

#### Finding 3: Buffer Release With No Available Training Slots

```text
Scenario:
Buffer candidate is released, but training slots are already full.

Expected:
Candidate sees next available training date or controlled no-slots message.

Actual:
Candidate-facing page shows error.

Impact:
Medium-High — candidate cannot proceed without manual intervention.

Root cause:
Backend did not handle released candidate with full training schedule.

Test added:
buffer_release_no_available_slots
```

#### Finding 4: Document Resubmission Showing Verified Documents

```text
Scenario:
HR marks one document as Invalid and another document as Verified.

Expected:
Only Invalid document is shown for resubmission.

Actual:
Both documents are shown.

Impact:
Medium — candidate can overwrite a document already approved by HR.

Root cause:
Frontend loads all documents without filtering by status.

Test added:
document_resubmission_status_filter
```

## 8. Non-Functional Considerations

Non-functional testing was handled as focused checks around the highest-risk parts of the flow, not as a separate full-scale performance or security testing project.

### 8.1 Performance Checks

#### Application Form Load Time

```text
Test: Form page load time with 3G throttling
Expected: First Contentful Paint (FCP) <= 2 sec
Measure: real device testing
Finding: FCP = 3.2 sec
Cause: unoptimized city/province dropdown with large option list
Fix: autocomplete + lazy loading for city/province fields
```

#### Candidate Status Transition Latency

```text
Test: Video/Test completion to SMS delivery time
Expected: status updated and SMS sent within 10 seconds
Measure: trace ID analysis and database timestamps

Measurement:
- Video/Test score saved: T+0ms
- Status updated in DB: T+450ms
- SMS queued: T+620ms
- SMS sent via provider: T+1800ms

Result: PASS
```

#### Buffer Release Job Performance

```text
Test: background job processing Buffer queue with 1000+ candidates
Expected: process Buffer candidates and release eligible ones within 5 minutes

Setup:
- 1500 Buffer candidates
- relevant vacancies available for part of the queue

Finding:
- first 500 candidates processed in 2 minutes
- remaining 1000 candidates caused query timeout at 4 minutes
- job restarted and partial releases were duplicated

Fix:
- batch processing
- lock timeout
- idempotency check
```

### 8.2 Security-Related Checks

Security checks were limited to application-level validation and access-control risks visible during QA testing. Dedicated penetration testing was outside QA scope.

#### Input Validation

```text
Test: submit application form with SQL-like payload in text field
Input: test@example.com'; DROP TABLE candidates; --
Expected: input is handled as plain text; database query is not executed as SQL command
Actual: PASS
```

#### Candidate-Facing Link Access

```text
Test: change candidate identifier in SMS link
Expected: access is blocked for another candidate record
Actual: PASS

Checked:
- link token is candidate-specific
- changed candidate_id returns access error
- candidate cannot access another candidate form
```

### 8.3 Reliability Checks

#### Form Submission Load

```text
Test: concurrent form submissions during peak campaign traffic
Setup: 100 concurrent requests

Result:
- form submissions: 100% success rate
- response time: p50=240ms, p95=580ms, p99=1200ms
- database: 0 timeouts
- Jira ticket creation: 100% success

Conclusion: PASS
```

#### SMS Delivery Monitoring

```text
Test: SMS delivery monitoring over 24 hours

Result:
- SMS provider delivery: 99.2%
- retry logic: 10 sec, 1 min, 5 min
- final delivery rate after retries: 99.8%

Expected: >= 98%
Actual: PASS
```

## 9. UAT Testing Approach

UAT was used for full end-to-end validation without risk to production candidate data.

### UAT Environment Setup

- UAT flow did not include captcha.
- Test SMS configuration was used for controlled SMS checks.
- Test Jira project was used for candidate tickets.
- Test candidate records were separated from production candidate records.
- Test data was marked as test data.
- Production analytics were not affected.

### UAT Test Suite 1: Critical Path — Valid Candidate

```text
Setup:
- Candidate: TEST-CP-001
- City: <city_1> with open Sales Ambassador vacancy
- Candidate meets <min_age>, <max_age>, <min_experience> and location requirements

Steps:
1. Submit application form.
2. Verify success screen.
3. Verify candidate record in database.
4. Verify Jira ticket creation.
5. Verify candidate status = `Video/Test`.
6. Verify video/test SMS.
7. Complete Video/Test with passing score.
8. Verify candidate status = `Screening Schedule`.
9. Verify AI recruiter interview SMS.
10. Complete AI recruiter interview with score >= <pass_score_threshold>.
11. Verify candidate status = `OIF`.
12. Verify OIF SMS.
13. Submit OIF form and required documents.
14. Verify candidate status = `HR Check`.

Expected:
- Candidate reaches `HR Check`.
- Jira ticket contains required candidate data.
- SMS history contains expected templates.
- Database status matches Jira status.
```

### UAT Test Suite 2: Buffer Flow

```text
Setup:
- Candidate: TEST-BUF-001
- Candidate selected city has no open Sales Ambassador vacancy

Steps:
1. Submit application form.
2. Pass Video/Test.
3. Verify candidate status = `Buffer`.
4. Create or activate relevant vacancy in selected city.
5. Wait 5 minutes for Buffer release job.
6. Verify candidate status = `Screening Schedule`.
7. Verify AI recruiter interview SMS.

Expected:
- Candidate enters `Buffer` when no relevant vacancy exists.
- Candidate leaves `Buffer` after relevant vacancy appears.
- Candidate receives AI recruiter interview SMS after release.
```

### UAT Test Suite 3: Document Resubmission

```text
Setup:
- Candidate status = `HR Check`
- Candidate uploaded 2 required documents through OIF form

Steps:
1. HR marks one document as `Verified`.
2. HR marks another document as `Invalid`.
3. HR changes candidate status to `Document Resubmission`.
4. Candidate opens document resubmission form from SMS.
5. Candidate uploads corrected file for the invalid document.
6. Candidate submits document resubmission form.
7. Verify candidate status = `HR Check`.

Expected:
- Only document marked as `Invalid` is available for resubmission.
- Document marked as `Verified` is not shown.
- Corrected file replaces rejected file.
- Candidate returns to `HR Check`.
```

## 10. Production Smoke Testing Approach

Production smoke testing was limited to safe checks that did not create unnecessary real candidates or trigger uncontrolled downstream actions.

Production smoke covered:

- public candidate-facing form availability;
- page load and basic UI rendering;
- province and city field availability;
- required field visibility;
- consent block visibility;
- absence of critical UI errors;
- candidate-facing pages opening through approved test links.

Production smoke did not include:

- mass creation of test candidates;
- captcha bypass;
- unnecessary SMS triggering;
- creation of junk Jira tickets;
- real candidate data;
- production vacancy changes;
- uncontrolled production status changes.

Production flow included captcha. UAT flow did not include captcha.

## 11. Bug Reporting with Sanitized Examples

### Bug Report Example 1: Critical — Buffer Release Not Triggered

```text
Title: Candidate remains in Buffer after matching vacancy is created
Severity: Critical
Priority: P1
Environment: UAT
Build: <build_version>

Preconditions:
- Candidate: <candidate_id>
- Video/Test passed with score above <pass_score_threshold>
- City: <city>
- Province: <province>
- No vacancy existed for selected city at the time of Video/Test completion

Steps to Reproduce:
1. Check candidate status in UAT database.
2. Create or activate vacancy for candidate selected city and province.
3. Wait 5 minutes for Buffer release job.
4. Check candidate status again.
5. Check SMS history.

Expected Result:
- Candidate status changes to `Screening Schedule`.
- AI recruiter interview SMS is sent.

Actual Result:
- Candidate remains in `Buffer`.
- No AI recruiter interview SMS is sent.

Evidence:
- Trace ID: <trace_id>
- SQL query result: candidate status remains `Buffer`
- SQL query result: matching vacancy exists
- Log excerpt: VacancyMatcher job failed before status update

Regression Risk:
- Changes to vacancy matching affect `Video/Test` -> `Screening Schedule` path.
- Add regression test for fresh vacancy matching after Buffer release job.
```

### Bug Report Example 2: High — Verified Documents Visible on Resubmission Form

```text
Title: Verified document is visible on document resubmission form
Severity: High
Priority: P2
Environment: UAT

Preconditions:
- Candidate: <candidate_id>
- OIF submitted with 2 required documents
- HR marked document_1 as `Verified`
- HR marked document_2 as `Invalid`
- HR changed candidate status to `Document Resubmission`

Steps to Reproduce:
1. Open candidate document resubmission link from SMS.
2. Check documents displayed in the form.

Expected Result:
- Only document marked as `Invalid` is displayed.
- Document marked as `Verified` is hidden.

Actual Result:
- Both documents are displayed.
- Candidate can upload a new file for the document already marked as `Verified`.

Evidence:
- Screenshot: anonymized_document_resubmission_form.png
- SQL query result: document_1 = `Verified`, document_2 = `Invalid`
- Trace ID: <trace_id>
- Log excerpt: resubmission form loads all documents without status filter

Impact:
- Candidate can overwrite a document already approved by HR.
- HR workflow becomes inconsistent.
```

## 12. Automation Approach

Automation was limited to safe UAT-level and local demo scenarios.

### Automated Areas

Suitable areas for automation:

- UAT application form submission;
- required field validation;
- successful candidate application submission;
- success screen validation;
- candidate data saving in UAT-safe result;
- API-level UAT checks;
- SQL data consistency checks.

### Not Automated in Production

Full production submission was not automated because it could:

- create real candidate records;
- trigger real SMS events;
- create Jira tickets;
- affect hiring analytics;
- interact with production flow without human control.

Production flow included captcha, while UAT flow did not include captcha.

### Portfolio Automation Sample

A separate Kotlin automation sample can be provided in `/automation`.

The sample should:

- use Kotlin, JUnit 5 and Selenium;
- run against a local demo form based on the real public candidate-facing application flow;
- submit a valid application in a UAT-like flow;
- verify success screen;
- verify submitted candidate data in a local mock result block.

The sample must not use:

- live production form;
- real candidate data;
- internal URLs;
- credentials;
- private endpoints;
- captcha bypass.

## 13. Reporting and Evidence

Bug reports included evidence sufficient for faster developer pickup:

- clear title;
- environment;
- preconditions;
- exact steps to reproduce;
- actual result;
- expected result;
- severity and priority;
- trace ID;
- screenshots;
- video;
- SQL check;
- sanitized log excerpt;
- Jira ticket reference.

For public portfolio, evidence is shown in sanitized format:

- `<trace_id>` instead of real trace ID;
- `<candidate_id>` instead of real candidate ID;
- `<jira_ticket_id>` instead of real Jira ticket;
- sanitized SQL queries;
- sanitized log excerpts;
- anonymized screenshot or video filenames without production files.

## 14. Exit Criteria and Release Readiness

### Exit Criteria Checklist

- 100% Critical scenarios passed.
- 95% High scenarios passed.
- 0 Critical bugs open.
- All High bugs reviewed by Product.
- SQL data validation shows 0 unresolved anomalies.
- Production smoke tests prepared.
- Stakeholder sign-off received.

### Release Readiness

Release readiness was shared with product and business stakeholders after:

- critical path validation;
- risk review;
- bug triage;
- SQL validation;
- production smoke preparation;
- known issues review.

## 15. Public Portfolio Safety

This public version does not disclose:

- internal URLs;
- credentials;
- production screenshots;
- real candidate data;
- real trace IDs;
- private API endpoints;
- confidential business details;
- exact thresholds and internal rules when sensitive.

The goal of this public document is to demonstrate QA thinking, test coverage, risk-based approach, evidence-based bug reporting and understanding of end-to-end fintech hiring flow without exposing confidential information.
