package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IOD {

    /**
     * Apply the IOD mutation operator.
     * @param compilationUnits List of CompilationUnits representing the source code.
     * @param outputDirectory Path to save the mutated child class files.
     */
    public static void applyIOD(List<CompilationUnit> compilationUnits, String outputDirectory) {
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
                    for (int i = 0; i < overriddenMethods.size(); i++) {
                        // Clone the child class to create a mutant
                        ClassOrInterfaceDeclaration mutatedChildClass = childClass.clone();

                        // Remove the overridden method from the cloned class
                        MethodDeclaration methodToDelete = overriddenMethods.get(i);
                        mutatedChildClass.getMethodsByName(methodToDelete.getNameAsString())
                                .stream()
                                .filter(method -> method.getType().equals(methodToDelete.getType()) &&
                                        method.getParameters().equals(methodToDelete.getParameters()))
                                .findFirst()
                                .ifPresent(mutatedChildClass::remove);

                        // Save the mutated child class to a file
                        String mutatedFileName = outputDirectory + "/" + mutatedChildClass.getNameAsString() + "_IOD_Mutant" + (i + 1) + ".java";
                        try (FileWriter writer = new FileWriter(mutatedFileName)) {
                            writer.write(mutatedChildClass.toString());
                            System.out.println("Saved mutant: " + mutatedFileName);
                        } catch (IOException e) {
                            System.err.println("Error saving mutated code: " + e.getMessage());
                        }
                    }
                }
            }
        });
    }
}
