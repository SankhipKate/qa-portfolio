# Checklist: SA Hiring Functional Flow

## Note

This checklist is anonymized for a public QA portfolio. Exact numeric values, thresholds, and time intervals are replaced with placeholders such as `<min_age>`, `<max_age>`, `<X days>`, `<Y days>`, `<pass_score_threshold>`. In the working project version, these values are taken from the internal eligibility matrix, notification schedule, scoring config, and business flow documentation.

## 1. Application Form — Candidate Entry

- Opening the application form from an advertising campaign link → the application form opens.
- Opening the application form from an existing Sales Ambassador referral link → the application form opens.
- Opening the application form from a TSH fast-track link → the application form opens.
- Submitting the advertising campaign form with valid required data → a Jira ticket is created with candidate name, phone number, email, city, province, and source.
- Submitting the Sales Ambassador referral form with valid required data → a Jira ticket is created, referral source is saved in the ticket.
- Submitting the TSH fast-track form with valid required data → a Jira ticket is created, fast-track source is saved in the ticket.
- Submitting the form with empty required fields → validation errors are shown for empty required fields.
- Submitting the form with an invalid phone number format → phone number validation error is shown.
- Submitting the form with an invalid email format → email validation error is shown.
- Submitting the form with valid phone number and email → phone number and email are saved in the Jira ticket.
- Submitting the form again with the same phone number → a new active duplicate Jira ticket is not created.

## 2. Initial Candidate Filtering

- Age below `<min_age>` from the internal eligibility matrix → candidate is rejected, rejection reason is saved in the Jira ticket.
- Age equal to `<min_age>` from the internal eligibility matrix → candidate moves to the video/test stage, Jira ticket status becomes `Video/Test`.
- Age inside the allowed range from the internal eligibility matrix → candidate moves to the video/test stage, Jira ticket status becomes `Video/Test`.
- Age equal to `<max_age>` from the internal eligibility matrix → candidate moves to the video/test stage, Jira ticket status becomes `Video/Test`.
- Age above `<max_age>` from the internal eligibility matrix → candidate is rejected, rejection reason is saved in the Jira ticket.
- Experience below `<min_experience>` from the internal eligibility matrix → candidate is rejected, rejection reason is saved in the Jira ticket.
- Experience equal to `<min_experience>` from the internal eligibility matrix → candidate moves to the video/test stage, Jira ticket status becomes `Video/Test`.
- Experience above `<min_experience>` from the internal eligibility matrix → candidate moves to the video/test stage, Jira ticket status becomes `Video/Test`.
- Eligibility question value matches a reject condition from the internal eligibility matrix → candidate is rejected, rejection reason is saved in the Jira ticket.
- All eligibility answers have a pass result in the internal eligibility matrix → candidate moves to the video/test stage, Jira ticket status becomes `Video/Test`.
- Candidate rejection is completed → rejection reason in the Jira ticket matches the rule-to-rejection-reason mapping from the internal eligibility matrix.

## 3. Video / Test Step

- Candidate reaches `Video/Test` → video/test SMS is generated with a video/test link.
- Opening the video/test link from SMS → video/test page opens.
- Candidate completes the video/test step within `<X days>` after video/test SMS → video/test result is saved in the Jira ticket.
- Candidate passes the test → video/test result is saved in the Jira ticket, city/province/vacancy matching rules are applied.
- Candidate does not complete the video/test step within `<X days>` after video/test SMS → video/test reminder SMS is generated with the video/test link.
- Candidate does not complete the video/test step within `<Y days>` after video/test reminder SMS → candidate is rejected, rejection reason is saved in the Jira ticket, test rejection SMS is sent.
- Candidate fails the test → candidate is rejected, rejection reason is saved in the Jira ticket, test rejection SMS is sent.
- Received video/test SMS → video/test link opens the video/test page, template variables are replaced with actual values.
- Received video/test reminder SMS → video/test link opens the video/test page, template variables are replaced with actual values.
- Received test rejection SMS → rejection reason matches the rejection reason in the Jira ticket.

## 4. City / Province / Vacancy Matching

- Candidate selects a city with a vacancy in `Open` status and `available slots > 0`, and active candidate count does not exceed `<candidate-to-vacancy ratio>` → candidate moves to `Screening Schedule`, Jira ticket status becomes `Screening Schedule`.
- Candidate selects a city with a vacancy in `Open` status and `available slots > 0`, but active candidate count exceeds `<candidate-to-vacancy ratio>` → candidate moves to `Buffer`, Jira ticket status becomes `Buffer`.
- Candidate selects a city without a vacancy in `Open` status and `available slots > 0`, while the selected province has other cities with open vacancies → `Multiple Cities` screen is shown.
- `Multiple Cities` screen is opened → the list contains only cities with vacancy status `Open` and `available slots > 0` in the selected province.
- Candidate selects an available city from the `Multiple Cities` screen → candidate moves to `Screening Schedule`, selected city/province are saved in the Jira ticket.
- Candidate selects buffer from the `Multiple Cities` screen → candidate moves to `Buffer`, Jira ticket status becomes `Buffer`.
- Candidate selects a province without open vacancies → candidate moves to `Buffer`, Jira ticket status becomes `Buffer`.
- City/province matching is completed → Jira ticket status becomes `Screening Schedule` or `Buffer` according to the selected branch.

## 5. Buffer Logic

- Candidate enters buffer because there are no open vacancies in the selected city/province → Jira ticket status becomes `Buffer`.
- Candidate enters buffer because `<candidate-to-vacancy ratio>` is exceeded → Jira ticket status becomes `Buffer`.
- A vacancy appears in the selected city/province with status `Open` and `available slots > 0`, and active candidate count no longer exceeds `<candidate-to-vacancy ratio>` → candidate moves from `Buffer` to `Screening Schedule`.
- An open vacancy appears in another city/province → candidate remains in `Buffer`, Jira ticket status remains `Buffer`.
- Candidate is released from buffer → buffer release SMS is sent.
- Candidate is released from buffer → existing Jira ticket is updated, no duplicate ticket is created.
- Received buffer release SMS → template variables are replaced with actual values, unresolved placeholders are absent.

## 6. Fast-Track Flow

- Candidate opens the application form from a TSH fast-track link → application form opens.
- Candidate submits the TSH fast-track form with eligible data → Jira ticket is created, fast-track source is saved.
- Fast-track form is submitted successfully → candidate moves to `OIF`, Jira ticket status becomes `OIF`.
- Candidate reaches `OIF` through fast track → OIF link opens.
- Candidate reaches `OIF` through fast track → AI recruiter interview SMS is not generated.
- Fast-track routing is completed → candidate status in the Jira ticket becomes `OIF`.

## 7. AI Recruiter Interview

- Candidate moves to `Screening Schedule` after video/test and city/province/vacancy matching → AI recruiter interview SMS is generated with an interview link.
- Opening the AI recruiter interview link from SMS → interview page opens.
- Candidate starts the interview within the allowed interview window from the notification schedule → AI recruiter interview screen opens.
- Candidate completes AI recruiter interview with score below `<pass_score_threshold>` from the scoring config → candidate is rejected, rejection reason is saved in the Jira ticket, AI interview rejection SMS is sent.
- Candidate completes AI recruiter interview with score equal to `<pass_score_threshold>` from the scoring config → candidate score and interview result are saved in the Jira ticket, candidate moves to `OIF`.
- Candidate completes AI recruiter interview with score above `<pass_score_threshold>` from the scoring config → candidate score and interview result are saved in the Jira ticket, candidate moves to `OIF`.
- AI interview is completed → candidate status in the Jira ticket reflects the interview result.
- Received AI recruiter interview SMS → interview link opens the interview page, template variables are replaced with actual values.
- Received AI interview rejection SMS → rejection reason matches the rejection reason in the Jira ticket.

## 8. OIF Submission

- Candidate reaches `OIF` after passing AI recruiter interview → OIF link is available.
- Candidate reaches `OIF` through TSH fast track → OIF link is available.
- Opening OIF link → OIF form opens.
- Submitting OIF with all required document fields → OIF is submitted, submitted document data are saved in the Jira ticket.
- Submitting OIF with a missing required document field → validation error is shown for the missing field.
- Submitting OIF with a document number → document number is saved in the Jira ticket.
- Uploading document image/file in OIF → uploaded file is available for HR check.
- OIF submission is completed → candidate moves to `HR Check`, Jira ticket status becomes `HR Check`.
- Candidate remains in `OIF` until `<HH:MM system time>` from the OIF reminder schedule → OIF reminder SMS is sent with OIF link.
- Candidate leaves `OIF` before `<HH:MM system time>` from the OIF reminder schedule → SMS log does not contain OIF reminder for the previous OIF status.
- Received OIF SMS → OIF link opens OIF form, template variables are replaced with actual values.
- Received OIF reminder SMS → OIF link opens OIF form, template variables are replaced with actual values.

## 9. HR Check

- HR marks all required documents as `Verified` → candidate status becomes `EA Signing`.
- HR marks one document as `Invalid` → candidate status becomes `Document Resubmission`.
- HR marks several documents as `Invalid` → candidate status becomes `Document Resubmission`.
- Document status is changed by HR → verification status is saved for each document.
- HR check is completed → Jira ticket reflects HR decision and document verification result.
- HR check is completed → document verification result is visible for each checked document.

## 10. Document Resubmission

- Candidate has one invalid document → resubmission form shows only that invalid document.
- Candidate has several invalid documents → resubmission form shows only those invalid documents.
- Candidate has verified documents → verified documents are not included in the resubmission form.
- Candidate submits corrected document number → new document number is saved in the Jira ticket.
- Candidate submits corrected document image/file → new document file is saved for HR review.
- Candidate submits resubmission form → updated document data are available for HR check.
- Resubmission is completed → candidate status returns to `HR Check`.
- Resubmission is completed → updated document data are displayed in the Jira ticket.

## 11. EA Signing

- Candidate reaches `EA Signing` → EA signing SMS is generated with EA signing link.
- Opening EA signing link from SMS → EA signing page opens.
- Candidate signs EA within `<X days>` after EA signing SMS → candidate status becomes `Training Scheduling`, EA status is updated in the Jira ticket.
- Candidate does not sign EA within `<X days>` after EA signing SMS → EA reminder SMS is generated with EA signing link.
- Candidate does not sign EA within `<Y days>` after EA reminder SMS → EA feedback SMS is generated.
- Received EA signing SMS → EA signing link opens EA signing page, template variables are replaced with actual values.
- Received EA reminder SMS → EA signing link opens EA signing page, template variables are replaced with actual values.

## 12. Training Scheduling

- Candidate reaches `Training Scheduling` → training scheduling SMS is generated with training scheduling link.
- Opening training scheduling link from SMS → training scheduling page opens.
- Candidate books an available training slot within `<X days>` after training scheduling SMS → training booking is saved, training confirmation SMS is generated.
- Received training confirmation SMS → SMS contains booked training date and booked training time.
- Candidate has booked training, and `<training reminder time>` from the training reminder schedule is reached → training reminder SMS is sent with booked training date and booked training time.
- Candidate does not book training within `<X days>` after training scheduling SMS → training scheduling reminder SMS is generated.
- Candidate does not book training within `<Y days>` after training scheduling reminder SMS → candidate is rejected, rejection reason is saved in the Jira ticket, training rejection SMS is sent.
- Candidate misses scheduled training → no-show result is saved in the Jira ticket, no-show SMS is sent with the next required candidate action from the approved SMS template.
- Candidate completes the required action from the no-show SMS within `<X days>` according to no-show rules → candidate returns to `Training Scheduling`, new training date is saved in the Jira ticket.
- Candidate does not complete the required action from the no-show SMS within `<Y days>` according to no-show rules → candidate is rejected, rejection reason is saved in the Jira ticket, no-show rejection SMS is sent.
- Candidate reschedules training within the allowed reschedule period from training schedule rules → new training date is saved in the Jira ticket.
- Training scheduling action is completed → training date, training status, and candidate status are updated in the Jira ticket.
- Received training scheduling SMS → link opens training scheduling page, template variables are replaced with actual values.
- Received training confirmation SMS → booked training date and booked training time match the selected training slot.
- Received training reminder SMS → booked training date and booked training time match the selected training slot.

## 13. Deployment Preparation

- Candidate completes `HR Check`, `EA Signing`, and `Training Scheduling` → candidate moves to `To Deploy`.
- Candidate moves to `To Deploy` → candidate-facing deployment SMS is generated with the next required candidate action from the approved SMS template.
- Candidate moves to `To Deploy` → TSH notification SMS is generated with candidate phone number, city, province, and required TSH action.
- TSH completes the required deployment action for a candidate in `To Deploy` → candidate status becomes `Deployed`.
- Candidate moves to `Deployed` → Jira ticket status becomes `Deployed`.
- Candidate moves to `Deployed` → deployed feedback SMS is generated.
- Candidate is rejected from `To Deploy` with a configured reason → rejection reason is saved in the Jira ticket, deployment rejection SMS is sent.
- Received candidate-facing deployment SMS → template variables are replaced with actual values, unresolved placeholders are absent.
- Received TSH notification SMS → candidate phone number, city, province, and required TSH action match the Jira ticket.
- Received deployed feedback SMS → template variables are replaced with actual values, unresolved placeholders are absent.
