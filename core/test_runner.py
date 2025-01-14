import subprocess

def run_tests():
    """
    Run the test suite and return whether all tests passed.
    """
    result = subprocess.run(['pytest', '--maxfail=1'], capture_output=True, text=True)
    return result.returncode == 0  # True if all tests pass
