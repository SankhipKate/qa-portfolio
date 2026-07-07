def calculate_price(price, discount):
    actual_price = price - discount

    if actual_price < 0:
        actual_price = 0
    return actual_price