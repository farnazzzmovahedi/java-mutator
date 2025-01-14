import unittest
from example import Calculator

class TestCalculator(unittest.TestCase):
    """
    Test cases for the Calculator class.
    """

    def setUp(self):
        """
        Set up a new Calculator instance before each test.
        """
        self.calc = Calculator()

    def test_add(self):
        """
        Test the add method.
        """
        self.assertEqual(self.calc.add(5), 5)
        self.assertEqual(self.calc.add(3), 8)

    def test_subtract(self):
        """
        Test the subtract method.
        """
        self.calc.add(10)
        self.assertEqual(self.calc.subtract(4), 6)
        self.assertEqual(self.calc.subtract(2), 4)

    def test_multiply(self):
        """
        Test the multiply method.
        """
        self.calc.add(2)
        self.assertEqual(self.calc.multiply(3), 6)
        self.assertEqual(self.calc.multiply(0), 0)

    def test_reset(self):
        """
        Test the private _reset method.
        """
        self.calc.add(10)
        self.calc._reset()
        self.assertEqual(self.calc.add(0), 0)

if __name__ == '__main__':
    unittest.main()
