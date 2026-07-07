# Shop Discount — Python Pytest Project

A small Python test automation project demonstrating basic `pytest` usage, parametrized tests, HTML test reports, and code coverage reporting.

## Project Goal

The goal of this project is to practice writing automated tests for a simple business rule using Python and pytest.

Tested business rule:

> The final price after applying a discount cannot be lower than 0.

## Tech Stack

- Python
- pytest
- pytest-html
- pytest-cov

## Project Structure

```text
shop-discount/
├── shop.py
├── tests/
│   └── test_shop.py
├── pytest.ini
├── requirements.txt
├── README.md
└── .gitignore
```

## Application Logic

The project contains a simple function:

```python
def calculate_price(price, discount):
    actual_price = price - discount

    if actual_price < 0:
        actual_price = 0

    return actual_price
```

The function calculates the final price after discount.

Example cases:

| Price | Discount | Expected Final Price |
|------:|---------:|---------------------:|
| 1500 | 500 | 1000 |
| 1500 | 0 | 1500 |
| 500 | 700 | 0 |
| 800 | 300 | 500 |

## Tests

The test suite uses `pytest.mark.parametrize` to run the same test with multiple data sets.

Covered scenarios:

- regular discount
- zero discount
- discount greater than price
- another regular discount case

## How to Run

Install dependencies:

```bash
python -m pip install -r requirements.txt
```

Run tests:

```bash
python -m pytest
```

## Reports

After running tests, the following reports are generated locally:

```text
reports/test-report.html
reports/coverage/index.html
```

Open reports on macOS:

```bash
open reports/test-report.html
open reports/coverage/index.html
```

## What This Project Demonstrates

This project demonstrates:

- basic pytest test structure
- parametrized tests
- separation of application code and test code
- testing business rules
- HTML test report generation
- code coverage reporting