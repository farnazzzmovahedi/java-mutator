import ast

class AccessModifierChange(ast.NodeTransformer):
    """
    Mutation operator: Changes private methods to public by removing leading underscores.
    """
    def visit_FunctionDef(self, node):
        if node.name.startswith('_') and not node.name.startswith('__'):
            node.name = node.name.lstrip('_')
        return node
