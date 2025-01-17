package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IHI {

    /**
     * Apply the IHI mutation operator.
     * @param compilationUnits List of CompilationUnits representing the source code.
     * @param outputFilePath Path to save the mutated child class.
     */
    public static void applyIHI(List<CompilationUnit> compilationUnits, String outputFilePath) {
        Random random = new Random();

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

                if (!parentFields.isEmpty()) {
                    // Randomly select a field from the parent class
                    FieldDeclaration fieldToHide = parentFields.get(random.nextInt(parentFields.size()));
                    String fieldName = fieldToHide.getVariable(0).getNameAsString();
                    String fieldType = fieldToHide.getVariable(0).getType().asString();

                    // Apply mutation to each child class
                    for (ClassOrInterfaceDeclaration childClass : childClasses) {
                        FieldDeclaration newField = new FieldDeclaration();
                        newField.addModifier(Modifier.Keyword.PUBLIC); // Make the field public
                        VariableDeclarator hiddenVariable = new VariableDeclarator(
                                fieldToHide.getVariable(0).getType(), // Same type as parent's field
                                fieldName // Same name as parent's field
                        );
                        hiddenVariable.setInitializer(String.valueOf(random.nextInt(100))); // Random value for the variable
                        newField.addVariable(hiddenVariable);

                        // Add the new field to the child class
                        childClass.addMember(newField);
                        System.out.println("Added hidden variable '" + fieldName + "' to child class '" + childClass.getNameAsString() + "'");

                        // Save only the mutated child class to the output file
                        try (FileWriter writer = new FileWriter(outputFilePath)) {
                            writer.write(childClass.toString());
                        } catch (IOException e) {
                            System.err.println("Error saving mutated code: " + e.getMessage());
                        }
                    }
                }
            }
        });
    }
}
