package operators.javaSpecific;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSI {

    /**
     * Apply the JSI mutation operator.
     * @param compilationUnits List of CompilationUnits representing the source code.
     * @param outputDirectory Path to save the mutated class files.
     */
    public static void applyJSI(List<CompilationUnit> compilationUnits, String outputDirectory) {
        // Iterate over all compilation units
        for (CompilationUnit cu : compilationUnits) {
            // Find all classes or interfaces in the compilation unit
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                // Find all instance variables (fields) that are not already static
                List<FieldDeclaration> instanceFields = clazz.findAll(FieldDeclaration.class).stream()
                        .filter(field -> !field.isStatic()) // Exclude static fields
                        .toList();

                // Generate mutants by adding the static modifier to each instance variable
                for (int i = 0; i < instanceFields.size(); i++) {
                    // Clone the class to create a mutant
                    ClassOrInterfaceDeclaration mutatedClass = clazz.clone();

                    // Get the specific field to mutate
                    FieldDeclaration fieldToMutate = instanceFields.get(i);

                    // Find and update the corresponding field in the cloned class
                    mutatedClass.findAll(FieldDeclaration.class).stream()
                            .filter(field -> field.equals(fieldToMutate))
                            .findFirst()
                            .ifPresent(field -> field.addModifier(com.github.javaparser.ast.Modifier.Keyword.STATIC));

                    // Save the mutated class to a file
                    String mutatedFileName = outputDirectory + "/" + mutatedClass.getNameAsString() + "_JSI_Mutant" + (i + 1) + ".java";
                    try (FileWriter writer = new FileWriter(mutatedFileName)) {
                        writer.write(mutatedClass.toString());
                        System.out.println("Saved mutant: " + mutatedFileName);
                    } catch (IOException e) {
                        System.err.println("Error saving mutated code: " + e.getMessage());
                    }
                }
            }
        }
    }
}
