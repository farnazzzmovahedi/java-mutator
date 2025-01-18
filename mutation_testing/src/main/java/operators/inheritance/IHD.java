package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import utils.MutantSaver;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IHD {

    /**
     * Apply the IHD mutation operator.
     * Generates one mutant for each field in the child class that hides a field from the parent class.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     */
    public static void applyIHD(List<CompilationUnit> compilationUnits) {
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
                List<FieldDeclaration> parentFields = parentClass.findAll(FieldDeclaration.class);

                for (ClassOrInterfaceDeclaration childClass : childClasses) {
                    List<FieldDeclaration> childFields = childClass.findAll(FieldDeclaration.class);

                    // Generate mutants for each hiding field
                    for (FieldDeclaration childField : childFields) {
                        for (VariableDeclarator childVariable : childField.getVariables()) {
                            String childFieldName = childVariable.getNameAsString();

                            // Check if the field hides a field from the parent class
                            boolean isHidingField = parentFields.stream()
                                    .flatMap(parentField -> parentField.getVariables().stream())
                                    .anyMatch(parentVariable -> parentVariable.getNameAsString().equals(childFieldName));

                            if (isHidingField) {
                                // Clone the child class for mutation
                                CompilationUnit clonedCU = childClass.findCompilationUnit().orElseThrow().clone();
                                ClassOrInterfaceDeclaration clonedChildClass = clonedCU.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();

                                // Remove the specific hiding field in the clone
                                clonedChildClass.findAll(FieldDeclaration.class).stream()
                                        .filter(f -> f.getVariables().stream()
                                                .anyMatch(v -> v.getNameAsString().equals(childFieldName)))
                                        .forEach(FieldDeclaration::remove);

                                System.out.println("Removed hiding field '" + childFieldName + "' in child class '" + childClass.getNameAsString() + "'");

                                // Save the mutated child class
                                String mutantPath = "mutants\\IHD\\mutation" + mutantIndex;
                                MutantSaver.save(clonedCU, mutantPath);
                                mutantIndex.getAndIncrement();
                            }
                        }
                    }
                }
            }
        });

        System.out.println("IHD mutation applied. Mutants saved to directory: mutants\\IHD\\");
    }
}
