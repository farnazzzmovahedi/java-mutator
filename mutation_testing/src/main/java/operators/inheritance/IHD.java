package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IHD {

    /**
     * Apply the IHD mutation operator.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     * @param outputFilePath   Path to save the mutated child class.
     */
    public static void applyIHD(List<CompilationUnit> compilationUnits, String outputFilePath) {
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

                    // Iterate through fields in the child class
                    for (FieldDeclaration childField : new ArrayList<>(childFields)) { // Avoid concurrent modification
                        for (VariableDeclarator childVariable : childField.getVariables()) {
                            String childFieldName = childVariable.getNameAsString();

                            // Check if the field hides a field from the parent class
                            if (parentFields.stream()
                                    .flatMap(parentField -> parentField.getVariables().stream())
                                    .anyMatch(parentVariable -> parentVariable.getNameAsString().equals(childFieldName))) {

                                // Remove the field
                                childField.remove();

                                System.out.println("Commented and removed field '" + childFieldName + "' in child class '" + childClass.getNameAsString() + "'");

                                // Save only the mutated child class to the output file
                                try (FileWriter writer = new FileWriter(outputFilePath)) {
                                    writer.write(childClass.toString());
                                } catch (IOException e) {
                                    System.err.println("Error saving mutated code: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
