import ast

def parse_code(file_path):
    """
    Parse a Python file into an Abstract Syntax Tree (AST).
    """
    with open(file_path, 'r') as file:
        return ast.parse(file.read())