package operators.inheritance;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import utils.MutantSaver;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ISD {

    public static void applyISD(List<CompilationUnit> compilationUnits) {
        // Maps to store parent-child relationships
        Map<String, List<ClassOrInterfaceDeclaration>> parentChildMap = new HashMap<>();

        // Identify parent-child relationships dynamically
        for (CompilationUnit cu : compilationUnits) {
            // Find all classes in the current CompilationUnit
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                // Map parent classes to child classes
                clazz.getExtendedTypes().forEach(parent -> {
                    parentChildMap.computeIfAbsent(parent.getNameAsString(), k -> new ArrayList<>()).add(clazz);
                });
            }
        }

        // List to store overriding opportunities
        List<MethodOverridingMutation> overridingMutations = new ArrayList<>();

        // Identify `super` method calls
        for (Map.Entry<String, List<ClassOrInterfaceDeclaration>> entry : parentChildMap.entrySet()) {
            String parentTypeName = entry.getKey();
            List<ClassOrInterfaceDeclaration> childClasses = entry.getValue();

            // Find the parent class
            Optional<ClassOrInterfaceDeclaration> parentClassOpt = compilationUnits.stream()
                    .flatMap(cu -> cu.findAll(ClassOrInterfaceDeclaration.class).stream())
                    .filter(cls -> cls.getNameAsString().equals(parentTypeName))
                    .findFirst();

            if (parentClassOpt.isPresent()) {
                for (ClassOrInterfaceDeclaration childClass : childClasses) {
                    // Look for methods in the child class and identify methods using `super`
                    childClass.getMethods().stream()
                            .filter(method -> method.getBody().isPresent() && methodContainsSuperCall(method, parentTypeName))
                            .forEach(method -> overridingMutations.add(new MethodOverridingMutation(childClass, method)));
                }
            }
        }

        // Apply mutations to delete `super` method calls
        AtomicInteger mutationIndex = new AtomicInteger(1);
        for (MethodOverridingMutation mutation : overridingMutations) {
            // Clone the entire Compilation Unit to preserve the original data
            CompilationUnit originalCU = mutation.childClass.findCompilationUnit()
                    .orElseThrow(() -> new IllegalStateException("Child class is not contained in a CompilationUnit."));
            CompilationUnit clonedCU = originalCU.clone();

            // Find the cloned version of the child class in the cloned CompilationUnit
            Optional<ClassOrInterfaceDeclaration> clonedChildOpt = clonedCU.findFirst(ClassOrInterfaceDeclaration.class,
                    cls -> cls.getNameAsString().equals(mutation.childClass.getNameAsString()));

            if (clonedChildOpt.isPresent()) {
                ClassOrInterfaceDeclaration clonedChild = clonedChildOpt.get();

                // Locate the cloned version of the overriding method and remove `super` calls
                clonedChild.getMethods().stream()
                        .filter(method -> method.getNameAsString().equals(mutation.method.getNameAsString()))
                        .findFirst()
                        .ifPresent(clonedMethod -> {
                            removeSuperCalls(clonedMethod);
                            // Save the mutated CompilationUnit
                            MutantSaver.save(clonedCU, "mutants\\ISD\\mutation" + mutationIndex.getAndIncrement());
                        });
            }
        }
    }

    /**
     * Helper method to check if a method contains a `super` call.
     */
    private static boolean methodContainsSuperCall(MethodDeclaration method, String parentTypeName) {
        AtomicBoolean containsSuperCall = new AtomicBoolean(false);
        method.getBody().ifPresent(body ->
                body.accept(new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(BlockStmt n, Void arg) {
                        if (n.toString().contains("super.")) {
                            containsSuperCall.set(true);
                        }
                        super.visit(n, arg);
                    }
                }, null));
        return containsSuperCall.get();
    }

    /**
     * Helper method to remove `super` calls from a method.
     */
    private static void removeSuperCalls(MethodDeclaration method) {
        method.getBody().ifPresent(body ->
                body.accept(new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(BlockStmt n, Void arg) {
                        for (int i = 0; i < n.getStatements().size(); i++) {
                            Statement statement = n.getStatement(i);

                            // Replace "super." and "()" in the statement
                            String updatedStatement = statement.toString()
                                    .replace("super.", "")  // Remove "super."
                                    .replaceAll("\\(\\)", ""); // Remove "()"

                            // Update the statement only if changes were made
                            if (!updatedStatement.equals(statement.toString())) {
                                n.setStatement(i, StaticJavaParser.parseStatement(updatedStatement));
                            }
                        }
                        super.visit(n, arg);
                    }
                }, null));
    }



    /**
     * Helper class to store overriding mutation details.
     */
    private static class MethodOverridingMutation {
        ClassOrInterfaceDeclaration childClass;
        MethodDeclaration method;

        public MethodOverridingMutation(ClassOrInterfaceDeclaration childClass, MethodDeclaration method) {
            this.childClass = childClass;
            this.method = method;
        }
    }
}