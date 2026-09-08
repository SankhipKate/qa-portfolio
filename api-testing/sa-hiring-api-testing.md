# API Testing Artifact — SA Hiring Flow

## Purpose

This artifact demonstrates API testing of a multi-step hiring workflow.

The commercial flow included candidate application, automated filtering, vacancy matching, AI recruiter screening, onboarding forms, document review, state transitions, notifications, and training scheduling.

For portfolio safety, endpoint paths, IDs, payloads, and response examples below are sanitized abstractions rather than exports of production APIs.

## Testing Goals

The API layer should preserve the same business state as the candidate-facing UI and connected systems.

Main goals:

- validate request and response contracts;
- cover positive and negative scenarios;
- verify business-rule enforcement;
- verify state transitions;
- detect duplicate or inconsistent state;
- verify backend persistence;
- correlate API requests with logs and database state.

## Test Data Strategy

Public examples use synthetic data:

```json
{
  "first_name": "Test",
  "last_name": "Candidate",
  "email": "qa+candidate@example.com",
  "phone": "+639171234567",
  "city": "Baguio",
  "province": "Benguet",
  "source": "advertising_campaign"
}
```

Real customer data and production identifiers are excluded.

## API-01 — Create Candidate: Valid Request

**Request**

```http
POST /applications
Content-Type: application/json
```

Expected:

- HTTP `201`;
- response contains a candidate identifier;
- initial status is `Video/Test`;
- submitted city, province, and source are stored;
- the candidate is available for downstream processing.

**Backend check**

```sql
SELECT id, email, city, province, source, status
FROM candidates
WHERE email = '<test_email>'
ORDER BY created_at DESC
LIMIT 1;
```

Expected: one matching record and values consistent with the request.

## API-02 — Missing Required Field

Send an application request without `email`.

Expected:

- validation error is returned;
- candidate record is not created;
- downstream Jira/SMS actions are not triggered;
- response identifies the invalid or missing field.

This verifies server-side validation independently from UI validation.

## API-03 — Duplicate Active Candidate

Precondition: an active candidate already exists with the same email or phone.

Repeat the application request.

Expected:

- duplicate active application is rejected;
- a second active candidate is not created;
- no duplicate downstream ticket or notification chain is created.

**SQL check**

```sql
SELECT email, phone, COUNT(*) AS active_candidate_count
FROM candidates
WHERE status NOT IN ('Rejected', 'Deployed')
  AND (email = '<test_email>' OR phone = '<test_phone>')
GROUP BY email, phone
HAVING COUNT(*) > 1;
```

Expected: zero rows.

## API-04 — Video/Test Result Changes Candidate State

**Request**

```http
POST /candidates/<candidate_id>/video-test
```

```json
{
  "result": "passed"
}
```

Expected:

- result is accepted and stored;
- candidate moves to the next valid state according to matching rules;
- API response, database state, and connected workflow state remain consistent;
- the expected notification is generated when required.

Negative checks:

- unknown candidate ID;
- repeated result submission;
- incompatible candidate state;
- unsupported result value;
- malformed request body.

## API-05 — Buffer Candidate Release

When a relevant vacancy becomes available, the release process should move only eligible candidates forward.

Checks:

- city/province rules are respected;
- unrelated candidates remain in Buffer;
- one release produces one state transition;
- retrying the process does not duplicate downstream actions;
- candidate state and notification history remain consistent.

## API-06 — Document Resubmission

During HR review, one document can be accepted while another is rejected.

Checks:

- an `Invalid` document can be replaced;
- a `Verified` document remains unchanged;
- candidate returns to the correct review state;
- duplicate document records are not created accidentally;
- audit timestamps are updated correctly.

Negative checks:

- attempt to replace a Verified document;
- unknown document ID;
- invalid candidate state;
- empty file reference;
- duplicate resubmission request.

## Contract Validation

For each request, validation goes beyond HTTP status.

Typical checks include:

- response schema;
- required fields;
- field types;
- nullability;
- allowed enum values;
- state transition;
- idempotency;
- side effects;
- error object structure;
- backend persistence;
- consistency with connected systems.

## Technical Evidence

Useful defect evidence can include:

- request body;
- response body;
- HTTP status;
- timestamp;
- trace ID;
- candidate/test entity ID;
- relevant SQL result;
- sanitized log excerpt;
- Jira/workflow status;
- screenshot or video when UI is involved.

## Postman Collection

The accompanying Postman collection is intentionally portfolio-safe:

- endpoints are generic examples;
- `base_url` points to a local/mock environment by default;
- credentials are absent;
- customer data is absent;
- assertions demonstrate expected API QA patterns.

## What This Demonstrates

- REST API testing;
- Postman test design;
- positive and negative scenarios;
- request/response validation;
- business-rule testing;
- backend state validation;
- SQL cross-checks;
- idempotency thinking;
- integration testing;
- evidence-based defect investigation.
