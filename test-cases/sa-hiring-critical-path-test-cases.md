# Test Cases: SA Hiring Critical Paths

## TC-01 — Standard Candidate Reaches OIF After Successful Screening

**Goal:** verify the main candidate path from application submission to receiving and opening the OIF form.

**Preconditions:**

- Candidate source is advertising campaign.
- Candidate meets `<min_age>`, `<max_age>`, `<min_experience>` and location requirements.
- Candidate phone number and email have not been used for an active application before.
- The selected candidate city has an open Sales Ambassador vacancy.
- Active candidate-to-vacancy ratio allows candidate processing.

| Step | Action | Expected Result |
| --- | --- | --- |
| 1 | Candidate opens the Sales Ambassador application form. | The form opens; preferred work province, preferred work city, first name, last name, mobile number, personal email address, age, visible tattoos, education, work experience, sales experience, availability to start, source, captcha and consent block are available. |
| 2 | Candidate selects province and city with an open vacancy. | Selected province and city are saved in the form; candidate can continue filling out the application. |
| 3 | Candidate fills in all required fields with valid values, passes captcha and submits the form. | Success screen `Application successfully submitted` is shown; Jira ticket is created with candidate first name, last name, phone number, email, city, province, source and work experience. |
| 4 | System performs initial candidate filtering. | Candidate passes initial filtering; candidate status becomes `Video/Test`; video/test SMS is sent to the candidate. |
| 5 | Candidate opens the link from video/test SMS. | `Watch the Sales Ambassador Position Video` page opens; `Take a test` button is available. |
| 6 | Candidate watches role awareness video and opens the test. | `Role awareness test` opens; candidate first name, last name and mobile number are pre-filled from the application. |
| 7 | Candidate submits role awareness test with passing result. | Test result is saved; repeated test submission for the same application is unavailable. |
| 8 | System matches candidate with an open vacancy in the selected city. | Candidate status becomes `Screening Schedule`; AI recruiter interview SMS is sent to the candidate. |
| 9 | Candidate opens the link from AI recruiter interview SMS during available interview time. | Interview waiting room opens; candidate-specific interview code is displayed; candidate can enter the interview. |
| 10 | Candidate completes AI recruiter interview with score equal to or above `<pass_score_threshold>`. | Interview result is saved; candidate status becomes `OIF`; OIF SMS is sent to the candidate. |
| 11 | Candidate opens the link from OIF SMS. | OIF / employee information form opens; candidate personal fields are pre-filled from the application; the form contains a warning to use only the personal link. |
| 12 | Candidate fills in OIF, uploads required documents and submits the form. | OIF submission is saved; documents are available for review; candidate status becomes `HR Check`. |


## TC-02 — Candidate Moves to Buffer Through Waitlist When No Vacancy Exists in the Selected City

**Goal:** verify the candidate path to Buffer after successful `Video/Test` when there is no available vacancy in the selected city.

**Preconditions:**

- Candidate source is advertising campaign.
- Candidate meets `<min_age>`, `<max_age>`, `<min_experience>` and location requirements.
- Candidate phone number and email have not been used for an active application before.
- The selected candidate city has no open Sales Ambassador vacancy.
- Waitlist option is available for the selected city.

| Step | Action | Expected Result |
| --- | --- | --- |
| 1 | Candidate opens the Sales Ambassador application form. | The form opens; preferred work province, preferred work city, first name, last name, mobile number, personal email address, age, visible tattoos, education, work experience, sales experience, availability to start, source, captcha and consent block are available. |
| 2 | Candidate selects province and city without an open vacancy. | Selected province and city are saved in the form; candidate can continue filling out the application. |
| 3 | Candidate fills in all required fields with valid values, passes captcha and submits the form. | Success screen `Application successfully submitted` is shown; Jira ticket is created with candidate first name, last name, phone number, email, city, province, source and work experience. |
| 4 | System performs initial candidate filtering. | Candidate passes initial filtering; candidate status becomes `Video/Test`; video/test SMS is sent to the candidate. |
| 5 | Candidate opens the link from video/test SMS. | `Watch the Sales Ambassador Position Video` page opens; `Take a test` button is available. |
| 6 | Candidate watches role awareness video, opens the test and submits role awareness test with passing result. | Test result is saved; repeated test submission for the same application is unavailable. |
| 7 | System checks open Sales Ambassador vacancies in the selected city. | No open Sales Ambassador vacancy is found for the selected city. |
| 8 | Candidate opens `Available jobs` screen. | The screen shows a message that there are no available jobs in the selected city; waitlist option is available. |
| 9 | Candidate selects `Add me to the waitlist for my city` and clicks `Submit`. | Waitlist choice is saved; candidate remains linked to the selected province and city. |
| 10 | System processes waitlist submission. | Candidate status becomes `Buffer`; Jira ticket stores city, province, source, `Video/Test` result and waitlist choice. |
| 11 | An open Sales Ambassador vacancy appears in the selected city. | System finds a match between candidate selected city and the new vacancy. |
| 12 | Active candidate-to-vacancy ratio allows candidate release from Buffer. | Candidate status changes from `Buffer` to `Screening Schedule`; AI recruiter interview SMS with candidate-specific interview link is sent to the candidate. |


## TC-03 — Fast-Track Candidate From TSH Moves Directly to OIF

**Goal:** verify the fast-track candidate path from TSH without `Video/Test` and `Screening Schedule`.

**Preconditions:**

- Candidate source is TSH invitation.
- Candidate meets fast-track criteria: candidate was pre-screened by TSH and recommended for accelerated processing.
- Candidate phone number and email have not been used for an active application before.
- TSH has access to fast-track form.

| Step | Action | Expected Result |
| --- | --- | --- |
| 1 | TSH opens fast-track form. | Fast-track form opens; first name, last name, mobile number, personal email address, preferred work province, preferred work city and source fields are available. |
| 2 | TSH fills in fast-track form with valid candidate values and submits the form. | Success screen is shown; Jira ticket is created with candidate first name, last name, phone number, email, city, province and fast-track source. |
| 3 | System processes fast-track submission. | Candidate status becomes `OIF`; candidate does not move to `Video/Test`; candidate does not move to `Screening Schedule`. |
| 4 | System sends OIF SMS to the candidate. | OIF SMS contains candidate-specific link to OIF / employee information form. |
| 5 | Candidate opens the link from OIF SMS. | OIF / employee information form opens; candidate personal fields are pre-filled from fast-track form; the form contains a warning to use only the personal link. |
| 6 | Candidate fills in OIF, uploads required documents and submits the form. | OIF submission is saved; documents are available for review; candidate status becomes `HR Check`. |


## TC-04 — Candidate Resubmits Only Documents Rejected During HR Check

**Goal:** verify candidate return to document correction after HR Check and resubmission of only documents rejected by HR.

**Preconditions:**

- Candidate status is `HR Check`.
- Candidate OIF submission is saved.
- Candidate uploaded 2 required documents through OIF form.
- Candidate documents are available for HR review.
- HR has access to candidate document review.

| Step | Action | Expected Result |
| --- | --- | --- |
| 1 | HR opens candidate Jira ticket at `HR Check` stage. | Jira ticket shows candidate details, OIF submission and uploaded documents. |
| 2 | HR reviews candidate uploaded documents. | `Verified` and `Invalid` actions are available for each document. |
| 3 | HR marks one document as `Verified` and saves review result. | Selected document status is saved as `Verified`. |
| 4 | HR marks another document as `Invalid` and saves review result. | Selected document status is saved as `Invalid`. |
| 5 | HR changes candidate status from `HR Check` to `Document Resubmission`. | Candidate status is saved as `Document Resubmission`; document resubmission SMS is sent to the candidate. |
| 6 | Candidate opens the link from document resubmission SMS. | Document resubmission form opens; the form shows only the document marked by HR as `Invalid`; the document marked by HR as `Verified` is not displayed. |
| 7 | Candidate uploads corrected file for the document marked by HR as `Invalid` and submits the form. | Corrected file replaces the previously uploaded file for the rejected document; resubmission is saved. |
| 8 | System processes document resubmission form. | Candidate status changes from `Document Resubmission` to `HR Check`. |
| 9 | HR opens candidate Jira ticket again. | Jira ticket shows the updated file for the previously rejected document; the document marked by HR as `Verified` was not changed. |


## TC-05 — Candidate Returns to Training Scheduling After No-Show

**Goal:** verify training no-show handling and candidate return to new training slot selection.

**Preconditions:**

- Candidate status is `Training Scheduling`.
- Candidate EA is signed.
- Candidate has access to training scheduling link.
- At least one training slot is available in the schedule.
- TSH has access to save training attendance result.

| Step | Action | Expected Result |
| --- | --- | --- |
| 1 | Candidate opens training scheduling link. | Training scheduling page opens; candidate sees available training slots. |
| 2 | Candidate selects available training slot and confirms booking. | Training slot is saved for the candidate; training confirmation SMS is sent to the candidate. |
| 3 | Scheduled training time approaches. | Training reminder SMS is sent to the candidate 15 minutes before training starts. |
| 4 | Candidate does not attend scheduled training. | Candidate is absent from training; training attendance result is ready to be saved. |
| 5 | TSH saves training attendance result as `No-show`. | `No-show` result is saved in candidate Jira ticket; no-show SMS is sent to the candidate. |
| 6 | Candidate opens the link from no-show SMS. | Candidate return page for training scheduling opens. |
| 7 | Candidate confirms return to new training slot selection. | Candidate status returns to `Training Scheduling`; training slot selection is available to the candidate again. |
| 8 | Candidate selects a new available training slot and confirms booking. | New training slot is saved for the candidate; new training confirmation SMS is sent to the candidate. |
