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
2. Select preferred work province.
3. Verify preferred work city becomes enabled.
4. Select preferred work city.
5. Verify alternate work city becomes enabled.
6. Fill candidate personal data.
7. Select age, education, work experience and sales experience.
8. Upload dummy resume file.
9. Select scooter / motorcycle access.
10. Select availability to start.
11. Select candidate source.
12. Accept consent.
13. Submit the form.
14. Verify success screen.
15. Verify submitted candidate data in the local result block.

## Production vs UAT Note

The production public form included reCAPTCHA.

The UAT flow did not include captcha, so UAT form submission could be tested end-to-end in a controlled environment.

This portfolio sample does not automate the live production form and does not bypass captcha.

## How to Run

From this folder:

```bash
gradle test
```

The test uses Chrome in headless mode.

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
