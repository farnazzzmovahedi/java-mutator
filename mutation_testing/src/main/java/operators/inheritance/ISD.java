package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import utils.MutantSaver;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ISD {

    public static void applyISD(List<CompilationUnit> compilationUnits) {
        AtomicInteger mutationCounter = new AtomicInteger(1);

        // Iterate through all the compilation units for processing
        for (CompilationUnit cu : compilationUnits) {
            boolean isMutated = false; // Flag to track mutation for current CompilationUnit

            // Find all "super" expressions
            List<SuperExpr> superExprs = cu.findAll(SuperExpr.class);

            for (SuperExpr superExpr : superExprs) {
                // Remove "super" expressions' parent nodes in AST
                if (superExpr.getParentNode().isPresent()) {
                    superExpr.getParentNode().get().remove(superExpr);
                    isMutated = true;
                }
            }

            // Handle method calls like "super.methodName()"
            List<MethodCallExpr> methodCalls = cu.findAll(MethodCallExpr.class);
            for (MethodCallExpr methodCall : methodCalls) {
                if (methodCall.getScope().isPresent() && methodCall.getScope().get() instanceof SuperExpr) {
                    // Remove the "super" scope, leaving the method call without "super."
                    methodCall.removeScope();
                    isMutated = true;
                }
            }

            // Save the mutated CompilationUnit only if mutations occurred
            if (isMutated) {
                MutantSaver.save(cu, "mutants\\ISD\\mutation" + mutationCounter.getAndIncrement());
            }
        }
    }
}