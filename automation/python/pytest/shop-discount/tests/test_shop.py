import pytest
from shop import calculate_price

@pytest.mark.parametrize(
    "price, discount, expected_price",
    [
        (1500, 500, 1000),
        (1500, 0, 1500),
        (500, 700, 0),
        (800,300, 500),
    ],
    ids=[
        "regular discount",
        "zero discount",
        "discount greater than price",
        "another regular discount"
    ]
)
def test_calculate_price(price, discount, expected_price):
    actual_price = calculate_price(price, discount)
    assert actual_price == expected_price
