package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import utils.MutantSaver;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IHI {

    /**
     * Apply the IHI mutation operator.
     * Generates one mutant for each combination of parent field and child class.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     */
    public static void applyIHI(List<CompilationUnit> compilationUnits) {
        AtomicInteger mutantIndex = new AtomicInteger(1);
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

                // Generate mutants for each parent field in each child class
                for (FieldDeclaration parentField : parentFields) {
                    VariableDeclarator parentVariable = parentField.getVariable(0); // Assuming single variable per field
                    String fieldName = parentVariable.getNameAsString();

                    for (ClassOrInterfaceDeclaration childClass : childClasses) {
                        // Clone the child class for mutation
                        CompilationUnit clonedCU = childClass.findCompilationUnit().orElseThrow().clone();
                        ClassOrInterfaceDeclaration clonedChildClass = clonedCU.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();

                        // Add a hidden field to the cloned child class
                        FieldDeclaration newField = new FieldDeclaration();
                        newField.addModifier(Modifier.Keyword.PUBLIC); // Make the field public

                        VariableDeclarator hiddenVariable = new VariableDeclarator(
                                parentVariable.getType(), // Same type as parent's field
                                fieldName // Same name as parent's field
                        );

                        // Set a random initializer based on the field's type
                        hiddenVariable.setInitializer(getRandomValueForType(parentVariable.getType().asString(), random));

                        newField.addVariable(hiddenVariable);

                        // Add the new field to the cloned child class
                        clonedChildClass.addMember(newField);
                        System.out.println("Added hidden variable '" + fieldName + "' to child class '" + childClass.getNameAsString() + "'");

                        // Save the mutated CompilationUnit
                        String mutantPath = "mutants\\IHI\\mutation" + mutantIndex;
                        MutantSaver.save(clonedCU, mutantPath);
                        mutantIndex.getAndIncrement();
                    }
                }
            }
        });

        System.out.println("IHI mutation applied. Mutants saved to directory: mutants\\IHI\\");
    }

    /**
     * Generate a random value based on the type of a variable.
     *
     * @param type   The type of the variable as a string.
     * @param random Random instance to generate values.
     * @return A string representation of a random value for the given type.
     */
    private static String getRandomValueForType(String type, Random random) {
        switch (type) {
            case "int":
            case "long":
            case "short":
            case "byte":
                return String.valueOf(random.nextInt(100)); // Random integer between 0 and 99
            case "double":
            case "float":
                return String.valueOf(random.nextDouble() * 100); // Random floating-point number
            case "boolean":
                return String.valueOf(random.nextBoolean()); // Random boolean value
            case "char":
                return "'" + (char) (random.nextInt(26) + 'a') + "'"; // Random lowercase letter
            case "String":
                return "\"" + getRandomString(random, 5) + "\""; // Random string of length 5
            default:
                return "null"; // Default value for unsupported types
        }
    }

    /**
     * Generate a random string of the specified length.
     *
     * @param random Random instance to generate characters.
     * @param length Length of the random string.
     * @return A random string of the given length.
     */
    private static String getRandomString(Random random, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (random.nextInt(26) + 'a')); // Random lowercase letter
        }
        return sb.toString();
    }
}

