# SA Hiring Form Fill Automation Sample

This Kotlin UI automation sample is based on a real public candidate-facing application form used in an SA Hiring advertising flow.

For portfolio safety, the test runs against a local demo form instead of the live public form. This avoids creating test candidates, triggering real SMS events, affecting hiring analytics, or creating Jira tickets.

## Stack

- Kotlin
- JUnit 5
- Selenium WebDriver
- WebDriverManager
- Gradle

## What the Test Covers

The automated test validates a UAT-like form submission flow:

1. Open local SA Hiring application form.
2. Verify dependent fields are disabled before required selections.
3. Select preferred work province.
4. Verify preferred work city becomes enabled.
5. Select preferred work city.
6. Verify alternate work city becomes enabled.
7. Fill candidate personal data.
8. Select age, education, work experience and sales experience.
9. Upload dummy resume file.
10. Select scooter / motorcycle access.
11. Select availability to start.
12. Select candidate source.
13. Accept consent.
14. Verify submit button becomes enabled.
15. Submit the form.
16. Verify success screen.
17. Verify submitted candidate data in the local result block.

## Demo Mode

This test intentionally opens Chrome in visible mode and pauses after key steps.

The pauses are included so reviewers can see the form state while the automated test is running:

- initial empty form;
- selected province and city;
- filled candidate personal data;
- full form before submit;
- success screen after submission.

## Production vs UAT Note

The production public form included reCAPTCHA.

The UAT flow did not include captcha, so UAT form submission could be tested end-to-end in a controlled environment.

This portfolio sample does not automate the live production form and does not bypass captcha.

## How to Run

From this folder:

```bash
gradle test
```

Chrome will open visibly and the test will pause after key steps.

## Public Portfolio Safety

This sample does not include:

- production URLs;
- internal URLs;
- credentials;
- private API endpoints;
- real candidate data;
- real Jira tickets;
- real SMS events;
- captcha bypass.
