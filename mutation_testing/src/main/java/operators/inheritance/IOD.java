package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import utils.MutantSaver;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IOD {

    /**
     * Apply the IOD mutation operator.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     */
    public static void applyIOD(List<CompilationUnit> compilationUnits) {
        AtomicInteger mutantIndex = new AtomicInteger(1);

        // Maps to store parent-child relationships
        Map<ClassOrInterfaceType, List<ClassOrInterfaceDeclaration>> parentChildMap = new HashMap<>();

        // Identify parent-child relationships dynamically
        for (CompilationUnit cu : compilationUnits) {
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                clazz.getExtendedTypes().forEach(parent -> {
                    parentChildMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(clazz);
                });
            }
        }

        // Process parent-child relationships
        parentChildMap.forEach((parentType, childClasses) -> {
            // Find the parent class
            Optional<ClassOrInterfaceDeclaration> parentClassOpt = compilationUnits.stream()
                    .flatMap(cu -> cu.findAll(ClassOrInterfaceDeclaration.class).stream())
                    .filter(clazz -> clazz.getNameAsString().equals(parentType.getNameAsString()))
                    .findFirst();

            if (parentClassOpt.isPresent()) {
                ClassOrInterfaceDeclaration parentClass = parentClassOpt.get();

                // Get all methods in the parent class
                List<MethodDeclaration> parentMethods = parentClass.findAll(MethodDeclaration.class);

                for (ClassOrInterfaceDeclaration childClass : childClasses) {
                    // Get all overridden methods in the child class
                    List<MethodDeclaration> overriddenMethods = childClass.findAll(MethodDeclaration.class).stream()
                            .filter(childMethod -> parentMethods.stream().anyMatch(parentMethod ->
                                    parentMethod.getNameAsString().equals(childMethod.getNameAsString()) &&
                                            parentMethod.getType().equals(childMethod.getType()) &&
                                            parentMethod.getParameters().equals(childMethod.getParameters())
                            ))
                            .toList();

                    // Generate mutants for each overridden method
                    for (MethodDeclaration methodToDelete : overriddenMethods) {
                        // Clone the child class for mutation
                        CompilationUnit clonedCU = childClass.findCompilationUnit().orElseThrow().clone();
                        ClassOrInterfaceDeclaration clonedChildClass = clonedCU.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();

                        // Remove the overridden method from the cloned class
                        clonedChildClass.getMethodsByName(methodToDelete.getNameAsString())
                                .stream()
                                .filter(method -> method.getType().equals(methodToDelete.getType()) &&
                                        method.getParameters().equals(methodToDelete.getParameters()))
                                .findFirst()
                                .ifPresent(clonedChildClass::remove);

                        // Save the mutated CompilationUnit
                        String mutantPath = "mutants\\IOD\\mutation" + mutantIndex;
                        MutantSaver.save(clonedCU, mutantPath);
                        System.out.println("Saved mutant: " + mutantPath);

                        mutantIndex.getAndIncrement();
                    }
                }
            }
        });

        System.out.println("IOD mutation applied. Mutants saved to directory: mutants\\IOD\\");
    }
}
