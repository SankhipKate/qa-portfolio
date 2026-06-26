# Sample Bug Reports: SA Hiring Flow

## BUG-01 — Document Resubmission Form Shows Verified Document

**Title:** Document resubmission form shows document already marked as `Verified`

**Environment:**

- Environment: UAT
- Browser: Chrome `<browser_version>`
- Device: Desktop
- Candidate ID: `<candidate_id>`
- Jira ticket: `<jira_ticket_id>`
- Trace ID: `<trace_id>`

**Preconditions:**

- Candidate status is `Document Resubmission`.
- Candidate OIF submission is saved.
- Candidate uploaded 2 required documents through OIF form.
- HR marked one document as `Verified`.
- HR marked another document as `Invalid`.
- HR changed candidate status from `HR Check` to `Document Resubmission`.

**Steps to Reproduce:**

| Step | Action |
| --- | --- |
| 1 | Open candidate Jira ticket with status `Document Resubmission`. |
| 2 | Open document resubmission link from candidate-facing SMS. |
| 3 | Check the list of documents displayed in document resubmission form. |

**Actual Result:**

Document resubmission form shows both documents:

- document marked by HR as `Invalid`;
- document marked by HR as `Verified`.

Candidate can upload a new file for the document that was already verified.

**Expected Result:**

Document resubmission form shows only the document marked by HR as `Invalid`.

Document marked by HR as `Verified` is not displayed in the form and cannot be changed by candidate.

**Severity / Priority:**

- Severity: Major
- Priority: High

**Evidence / Attachments:**

- Screenshot: `anonymized_document_resubmission_form.png`
- Video: `anonymized_resubmission_flow.mp4`
- Trace ID: `<trace_id>`
- SQL check:

```sql
SELECT candidate_id, document_type, document_status
FROM candidate_documents
WHERE candidate_id = '<candidate_id>';
```

**Sanitized SQL Result:**

| candidate_id | document_type | document_status |
| --- | --- | --- |
| `<candidate_id>` | `<document_type_1>` | `Verified` |
| `<candidate_id>` | `<document_type_2>` | `Invalid` |

**Sanitized Log Excerpt:**

```text
trace_id=<trace_id>
candidate_id=<candidate_id>
status=Document Resubmission
invalid_documents=[<document_type_2>]
verified_documents=[<document_type_1>]
resubmission_form_documents=[<document_type_1>, <document_type_2>]
```

**Notes:**

The issue may cause candidates to overwrite documents that were already approved by HR.


## BUG-02 — Candidate Remains in Buffer After Relevant Vacancy Appears

**Title:** Candidate remains in `Buffer` after relevant vacancy appears in selected city

**Environment:**

- Environment: UAT
- Browser: Chrome `<browser_version>`
- Candidate ID: `<candidate_id>`
- Vacancy ID: `<vacancy_id>`
- Jira ticket: `<jira_ticket_id>`
- Trace ID: `<trace_id>`

**Preconditions:**

- Candidate passed initial filtering.
- Candidate passed `Video/Test`.
- Candidate status is `Buffer`.
- Candidate selected province = `<province>`.
- Candidate selected city = `<city>`.
- New open Sales Ambassador vacancy exists in the same province and city.
- Active candidate-to-vacancy ratio allows candidate release from Buffer.

**Steps to Reproduce:**

| Step | Action |
| --- | --- |
| 1 | Open candidate Jira ticket with status `Buffer`. |
| 2 | Create or activate Sales Ambassador vacancy for candidate selected city. |
| 3 | Run vacancy matching process. |
| 4 | Open candidate Jira ticket again. |
| 5 | Check candidate status and SMS history. |

**Actual Result:**

Candidate remains in `Buffer`.

Candidate does not move to `Screening Schedule`.

AI recruiter interview SMS is not sent.

**Expected Result:**

Candidate status changes from `Buffer` to `Screening Schedule`.

AI recruiter interview SMS with candidate-specific interview link is sent to the candidate.

**Severity / Priority:**

- Severity: Major
- Priority: High

**Evidence / Attachments:**

- Screenshot: `anonymized_candidate_buffer_status.png`
- Trace ID: `<trace_id>`
- SQL check:

```sql
SELECT candidate_id, candidate_status, selected_city, selected_province
FROM candidates
WHERE candidate_id = '<candidate_id>';

SELECT vacancy_id, city, province, vacancy_status
FROM vacancies
WHERE city = '<city>'
  AND province = '<province>'
  AND vacancy_status = 'Open';
```

**Sanitized SQL Result:**

| candidate_id | candidate_status | selected_city | selected_province |
| --- | --- | --- | --- |
| `<candidate_id>` | `Buffer` | `<city>` | `<province>` |

| vacancy_id | city | province | vacancy_status |
| --- | --- | --- | --- |
| `<vacancy_id>` | `<city>` | `<province>` | `Open` |

**Sanitized Log Excerpt:**

```text
trace_id=<trace_id>
candidate_id=<candidate_id>
candidate_status=Buffer
candidate_city=<city>
candidate_province=<province>
vacancy_id=<vacancy_id>
vacancy_status=Open
ratio_check=passed
status_update=skipped
```

**Notes:**

The issue blocks qualified candidates from moving to AI recruiter interview even when a relevant vacancy is available.


## BUG-03 — Fast-Track Candidate Incorrectly Receives Video/Test SMS

**Title:** Fast-track candidate receives video/test SMS instead of OIF SMS

**Environment:**

- Environment: UAT
- Browser: Chrome `<browser_version>`
- Candidate ID: `<candidate_id>`
- Jira ticket: `<jira_ticket_id>`
- Trace ID: `<trace_id>`

**Preconditions:**

- Candidate is submitted through TSH fast-track form.
- Candidate phone number and email have not been used for an active application before.
- Fast-track form is submitted successfully.
- Candidate should move directly to `OIF`.

**Steps to Reproduce:**

| Step | Action |
| --- | --- |
| 1 | TSH opens fast-track form. |
| 2 | TSH submits fast-track form with valid candidate data. |
| 3 | Open candidate Jira ticket. |
| 4 | Check candidate status. |
| 5 | Check SMS sent to candidate. |

**Actual Result:**

Candidate receives video/test SMS.

Candidate is routed to `Video/Test`.

OIF SMS is not sent.

**Expected Result:**

Candidate status becomes `OIF`.

Candidate does not move to `Video/Test`.

Candidate receives OIF SMS with candidate-specific OIF link.

**Severity / Priority:**

- Severity: Major
- Priority: High

**Evidence / Attachments:**

- Screenshot: `anonymized_fast_track_ticket_status.png`
- Screenshot: `anonymized_wrong_sms_template.png`
- Trace ID: `<trace_id>`
- SQL check:

```sql
SELECT candidate_id, source, candidate_status
FROM candidates
WHERE candidate_id = '<candidate_id>';

SELECT candidate_id, sms_template, sent_at
FROM sms_history
WHERE candidate_id = '<candidate_id>'
ORDER BY sent_at DESC;
```

**Sanitized SQL Result:**

| candidate_id | source | candidate_status |
| --- | --- | --- |
| `<candidate_id>` | `TSH Fast-Track` | `Video/Test` |

| candidate_id | sms_template | sent_at |
| --- | --- | --- |
| `<candidate_id>` | `video/test SMS` | `<HH:MM system time>` |

**Sanitized Log Excerpt:**

```text
trace_id=<trace_id>
candidate_id=<candidate_id>
source=TSH Fast-Track
expected_route=OIF
actual_route=Video/Test
sms_template_sent=video/test SMS
```

**Notes:**

The issue breaks the fast-track flow and forces candidates recommended by TSH to pass a step that should be skipped.
