# SQL Data Validation Queries: SA Hiring Flow

This document contains sanitized SQL queries used to validate key data points in the SA Hiring flow.

The queries are written for portfolio purposes and use public-safe table names, column names and placeholders. They do not include real candidate data, production IDs, internal URLs, credentials or private schema details.

## Query 01 — Candidate Record Created After Application Submission

**Purpose:**

Verify that a submitted application creates a candidate record with the expected city, province, source and initial status.

```sql
SELECT
  c.id,
  c.first_name,
  c.last_name,
  c.email,
  c.phone,
  c.city,
  c.province,
  c.source,
  c.status,
  c.created_at
FROM candidates c
WHERE c.email = '<test_email>'
  AND c.created_at > NOW() - INTERVAL '5 minutes'
ORDER BY c.created_at DESC
LIMIT 1;
```

**Expected Result:**

- One candidate record is returned.
- `city` and `province` match submitted form values.
- `source` matches submitted source.
- `status` is `Video/Test`.

**Bug Signal:**

- No row is returned.
- More than one active candidate exists for the same email.
- Candidate status is not `Video/Test`.
- Submitted city, province or source was saved incorrectly.


## Query 02 — Candidate Status Matches Jira Status

**Purpose:**

Detect mismatches between backend candidate status and Jira ticket status.

```sql
SELECT
  c.id AS candidate_id,
  c.status AS db_status,
  jt.status AS jira_status,
  c.updated_at AS candidate_updated_at,
  jt.updated_at AS jira_updated_at
FROM candidates c
JOIN jira_tickets jt
  ON c.jira_ticket_id = jt.id
WHERE c.updated_at > NOW() - INTERVAL '1 hour'
  AND c.status <> jt.status;
```

**Expected Result:**

- Zero rows are returned.

**Bug Signal:**

- Any returned row means candidate status in database and Jira status are not synchronized.
- This can block HR, TSH or training team from processing the candidate correctly.


## Query 03 — Candidate Source Saved Correctly

**Purpose:**

Verify that candidate source is saved correctly for advertising, referral and TSH fast-track flows.

```sql
SELECT
  c.id AS candidate_id,
  c.source,
  c.created_by,
  c.status,
  c.created_at
FROM candidates c
WHERE c.id = '<candidate_id>';
```

**Expected Result:**

- Advertising candidate has `source = 'Advertising Campaign'`.
- Referral candidate has `source = 'Sales Ambassador Referral'`.
- Fast-track candidate has `source = 'TSH Fast-Track'`.
- Fast-track candidate status is `OIF`.

**Bug Signal:**

- Fast-track candidate is saved as advertising source.
- Referral source is empty.
- Candidate source does not match submitted form.
- Incorrect source routes candidate to the wrong flow.


## Query 04 — Buffer Candidate Has No Matching Vacancy

**Purpose:**

Verify that a candidate is placed in `Buffer` only when no relevant vacancy exists for the selected city and province.

```sql
SELECT
  c.id AS candidate_id,
  c.city AS candidate_city,
  c.province AS candidate_province,
  c.status AS candidate_status,
  v.id AS matching_vacancy_id,
  v.status AS vacancy_status
FROM candidates c
LEFT JOIN vacancies v
  ON c.city = v.city
 AND c.province = v.province
 AND v.status = 'Open'
WHERE c.id = '<candidate_id>'
  AND c.status = 'Buffer';
```

**Expected Result:**

- Candidate status is `Buffer`.
- `matching_vacancy_id` is `NULL`.

**Bug Signal:**

- Candidate is in `Buffer`, but `matching_vacancy_id` is not `NULL`.
- Candidate was held in Buffer even though a relevant open vacancy existed.


## Query 05 — Buffer Release Candidate Has Matching Vacancy

**Purpose:**

Verify that a candidate released from `Buffer` has a matching open vacancy in the selected city and province.

```sql
SELECT
  c.id AS candidate_id,
  c.city AS candidate_city,
  c.province AS candidate_province,
  c.status AS candidate_status,
  c.held_since,
  c.updated_at AS released_at,
  v.id AS vacancy_id,
  v.city AS vacancy_city,
  v.province AS vacancy_province,
  v.status AS vacancy_status
FROM candidates c
JOIN vacancies v
  ON c.city = v.city
 AND c.province = v.province
WHERE c.id = '<candidate_id>'
  AND c.status = 'Screening Schedule'
  AND c.held_since IS NOT NULL
  AND v.status = 'Open';
```

**Expected Result:**

- One row is returned.
- Candidate status is `Screening Schedule`.
- Vacancy status is `Open`.
- Candidate city and province match vacancy city and province.

**Bug Signal:**

- No row is returned after Buffer release.
- Candidate moved from `Buffer` without a matching vacancy.
- Candidate moved because of a vacancy in unrelated city or province.


## Query 06 — Document Statuses Saved After HR Check

**Purpose:**

Verify that HR document review results are saved correctly.

```sql
SELECT
  d.candidate_id,
  d.document_type,
  d.status AS document_status,
  d.reviewed_by,
  d.reviewed_at
FROM candidate_documents d
WHERE d.candidate_id = '<candidate_id>'
ORDER BY d.document_type;
```

**Expected Result:**

- Each uploaded document has a saved review status.
- Approved document has status `Verified`.
- Rejected document has status `Invalid`.
- `reviewed_by` and `reviewed_at` are populated after HR review.

**Bug Signal:**

- Document review status is empty.
- HR marked document in UI, but status was not saved.
- `reviewed_by` or `reviewed_at` is missing after review.


## Query 07 — Document Resubmission Updates Only Invalid Document

**Purpose:**

Verify that document resubmission updates only the document marked as `Invalid` and does not change documents marked as `Verified`.

```sql
SELECT
  d.candidate_id,
  d.document_type,
  d.status AS document_status,
  d.file_id,
  d.previous_file_id,
  d.resubmitted_at
FROM candidate_documents d
WHERE d.candidate_id = '<candidate_id>'
ORDER BY d.document_type;
```

**Expected Result:**

- Document marked as `Invalid` has updated `file_id`.
- Document marked as `Invalid` has populated `previous_file_id`.
- Document marked as `Invalid` has populated `resubmitted_at`.
- Document marked as `Verified` keeps the same `file_id`.
- Document marked as `Verified` has no new `resubmitted_at`.

**Bug Signal:**

- Verified document file was changed during resubmission.
- Invalid document was not updated.
- Candidate resubmission created duplicate document records instead of replacing the rejected file.


## Query 08 — SMS History Contains Expected Template

**Purpose:**

Verify that candidate status transition triggers the expected SMS template.

```sql
SELECT
  s.candidate_id,
  s.template_key,
  s.status AS sms_status,
  s.sent_at,
  s.delivery_status
FROM sms_history s
WHERE s.candidate_id = '<candidate_id>'
ORDER BY s.sent_at DESC;
```

**Expected Result:**

Expected SMS template is present for candidate status:

| Candidate Status | Expected SMS Template |
| --- | --- |
| `Video/Test` | `video/test SMS` |
| `Screening Schedule` | `AI recruiter interview SMS` |
| `OIF` | `OIF SMS` |
| `Document Resubmission` | `document resubmission SMS` |
| `EA Signing` | `EA signing SMS` |
| `Training Scheduling` | `training scheduling SMS` |
| `Deployed` | `deployed feedback SMS` |

**Bug Signal:**

- SMS was not created after status transition.
- Wrong template was selected.
- SMS was created but not sent.
- SMS was sent with failed delivery status.


## Query 09 — Training No-Show Result Saved

**Purpose:**

Verify that training no-show is saved and linked to the candidate ticket.

```sql
SELECT
  ta.candidate_id,
  ta.training_slot_id,
  ta.attendance_result,
  ta.saved_by,
  ta.saved_at,
  c.status AS candidate_status,
  jt.status AS jira_status
FROM training_attendance ta
JOIN candidates c
  ON ta.candidate_id = c.id
JOIN jira_tickets jt
  ON c.jira_ticket_id = jt.id
WHERE ta.candidate_id = '<candidate_id>'
ORDER BY ta.saved_at DESC
LIMIT 1;
```

**Expected Result:**

- `attendance_result` is `No-show`.
- `saved_by` is populated.
- `saved_at` is populated.
- Candidate status allows return to `Training Scheduling`.
- Jira ticket reflects no-show result.

**Bug Signal:**

- No attendance record is returned.
- No-show result was saved in UI but not in database.
- Candidate cannot return to `Training Scheduling`.
- Jira ticket does not show no-show result.


## Query 10 — Duplicate Active Candidate Check by Phone or Email

**Purpose:**

Detect duplicate active applications created with the same phone number or email.

```sql
SELECT
  c.email,
  c.phone,
  COUNT(*) AS active_candidate_count,
  ARRAY_AGG(c.id ORDER BY c.created_at DESC) AS candidate_ids
FROM candidates c
WHERE c.status NOT IN ('Rejected', 'Deployed')
  AND (
    c.email = '<test_email>'
    OR c.phone = '<test_phone>'
  )
GROUP BY c.email, c.phone
HAVING COUNT(*) > 1;
```

**Expected Result:**

- Zero rows are returned.

**Bug Signal:**

- More than one active candidate exists for the same phone or email.
- Repeated form submission created duplicate active applications.
- Candidate may receive conflicting SMS messages or multiple Jira tickets may be created.


## Public Portfolio Safety

This document uses sanitized SQL and placeholders:

- `<candidate_id>`
- `<test_email>`
- `<test_phone>`
- `<city>`
- `<province>`
- `<jira_ticket_id>`
- `<trace_id>`

The queries do not expose:

- production schema details;
- internal database names;
- real candidate data;
- production IDs;
- credentials;
- private endpoints;
- confidential business rules.
