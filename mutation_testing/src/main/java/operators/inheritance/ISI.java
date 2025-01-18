package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.stmt.BlockStmt;
import utils.MutantSaver;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ISI {

    public static void applyISI(List<CompilationUnit> compilationUnits) {
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

        // List to store parent-child shadowing opportunities
        List<MethodShadowingMutation> shadowingMutations = new ArrayList<>();

        // Identify shadowing opportunities
        for (Map.Entry<String, List<ClassOrInterfaceDeclaration>> entry : parentChildMap.entrySet()) {
            String parentTypeName = entry.getKey();
            List<ClassOrInterfaceDeclaration> childClasses = entry.getValue();

            // Find the parent class
            Optional<ClassOrInterfaceDeclaration> parentClassOpt = compilationUnits.stream()
                    .flatMap(cu -> cu.findAll(ClassOrInterfaceDeclaration.class).stream())
                    .filter(cls -> cls.getNameAsString().equals(parentTypeName))
                    .findFirst();

            if (parentClassOpt.isPresent()) {
                ClassOrInterfaceDeclaration parentClass = parentClassOpt.get();

                // Retrieve all methods declared in the parent class
                List<MethodDeclaration> parentMethods = parentClass.findAll(MethodDeclaration.class);

                // Save shadowing opportunities for child classes
                for (ClassOrInterfaceDeclaration childClass : childClasses) {
                    for (MethodDeclaration method : parentMethods) {
                        shadowingMutations.add(new MethodShadowingMutation(childClass, method));
                    }
                }
            }
        }

        // Apply mutations to each shadowing opportunity
        AtomicInteger mutationIndex = new AtomicInteger(1);
        for (MethodShadowingMutation mutation : shadowingMutations) {
            // Clone the entire Compilation Unit to preserve the original data
            CompilationUnit originalCU = mutation.childClass.findCompilationUnit()
                    .orElseThrow(() -> new IllegalStateException("Child class is not contained in a CompilationUnit."));
            CompilationUnit clonedCU = originalCU.clone();

            // Find the cloned version of the child class in the cloned CompilationUnit
            Optional<ClassOrInterfaceDeclaration> clonedChildOpt = clonedCU.findFirst(ClassOrInterfaceDeclaration.class,
                    cls -> cls.getNameAsString().equals(mutation.childClass.getNameAsString()));

            if (clonedChildOpt.isPresent()) {
                ClassOrInterfaceDeclaration clonedChild = clonedChildOpt.get();

                // Check if the child class already overrides the parent method
                boolean methodOverridden = clonedChild.getMethods().stream()
                        .anyMatch(method -> method.getNameAsString().equals(mutation.parentMethod.getNameAsString()) &&
                                method.getBody().isPresent() &&
                                method.getBody().get().toString().contains("super." + mutation.parentMethod.getNameAsString() + "()"));

                // If not overridden, add shadowing implementation
                if (!methodOverridden) {
                    MethodDeclaration overriddenMethod = mutation.parentMethod.clone();
                    overriddenMethod.setBody(new BlockStmt().addStatement("return super." + mutation.parentMethod.getNameAsString() + "();"));
                    overriddenMethod.setModifiers(Modifier.Keyword.PUBLIC);

                    clonedChild.addMember(overriddenMethod);

                    // Save the mutated CompilationUnit
                    MutantSaver.save(clonedCU, "mutants\\ISI\\mutation" + mutationIndex.getAndIncrement());
                }
            }
        }
    }

    /**
     * Helper class to store method shadowing mutation details.
     */
    private static class MethodShadowingMutation {
        ClassOrInterfaceDeclaration childClass;
        MethodDeclaration parentMethod;

        public MethodShadowingMutation(ClassOrInterfaceDeclaration childClass, MethodDeclaration parentMethod) {
            this.childClass = childClass;
            this.parentMethod = parentMethod;
        }
    }
}