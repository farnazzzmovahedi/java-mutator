from operators.encapsulation import AccessModifierChange

def generate_mutants(tree, operators):
    """
    Apply mutation operators to the AST to generate mutants.
    """
    mutants = []
    for operator in operators:
        transformer = operator()
        mutant = transformer.visit(tree)
        mutants.append(mutant)
    return mutants
