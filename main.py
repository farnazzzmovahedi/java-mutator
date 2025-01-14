import ast

from core.parser import parse_code
from core.mutant_generator import generate_mutants
from core.test_runner import run_tests
from core.MS_calculator import calculate_score
from operators.encapsulation import AccessModifierChange

def main():
    # Parse the input code
    tree = parse_code('example.py')

    # Define mutation operators
    operators = [AccessModifierChange]

    # Generate mutants
    mutants = generate_mutants(tree, operators)

    # Test mutants and calculate score
    killed_mutants = 0
    for i, mutant in enumerate(mutants):
        # Save each mutant to a file
        with open(f'mutants/mutant_{i + 1}.py', 'w') as file:
            file.write(ast.unparse(mutant))  # Use ast.unparse for Python 3.9+

        # Run tests on the mutant
        if not run_tests():
            killed_mutants += 1

    # Calculate mutation score
    score = calculate_score(len(mutants), killed_mutants)
    print(killed_mutants)
    print(f"Mutation Score: {score:.2f}%")

if __name__ == '__main__':
    main()
