class Calculator:
    """
    A simple calculator class with basic arithmetic operations.
    """

    def __init__(self, value=0):
        self._value = value

    def add(self, x):
        self._value += x
        return self._value

    def subtract(self, x):
        self._value -= x
        return self._value

    def multiply(self, x):
        self._value *= x
        return self._value

    def reset(self):
        """
        Reset the calculator value to zero (private method).
        """
        self._value = 0