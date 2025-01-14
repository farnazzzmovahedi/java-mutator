def calculate_score(total_mutants, killed_mutants):
    """
    Calculate the mutation score.
    """
    if total_mutants == 0:
        return 0.0
    return (killed_mutants / total_mutants) * 100
