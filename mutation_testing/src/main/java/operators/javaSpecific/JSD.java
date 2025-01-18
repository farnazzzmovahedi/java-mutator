package operators.javaSpecific;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSD {

    /**
     * Apply the JSD mutation operator.
     * @param compilationUnits List of CompilationUnits representing the source code.
     * @param outputDirectory Path to save the mutated class files.
     */
    public static void applyJSD(List<CompilationUnit> compilationUnits, String outputDirectory) {
        // Iterate over all compilation units
        for (CompilationUnit cu : compilationUnits) {
            // Find all classes or interfaces in the compilation unit
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                // Find all static fields
                List<FieldDeclaration> staticFields = clazz.findAll(FieldDeclaration.class).stream()
                        .filter(FieldDeclaration::isStatic) // Select only static fields
                        .toList();

                // Generate mutants by removing the static modifier from each static field
                for (int i = 0; i < staticFields.size(); i++) {
                    // Clone the class to create a mutant
                    ClassOrInterfaceDeclaration mutatedClass = clazz.clone();

                    // Get the specific field to mutate
                    FieldDeclaration fieldToMutate = staticFields.get(i);

                    // Find and update the corresponding field in the cloned class
                    mutatedClass.findAll(FieldDeclaration.class).stream()
                            .filter(field -> field.equals(fieldToMutate))
                            .findFirst()
                            .ifPresent(field -> field.getModifiers().removeIf(modifier -> modifier.getKeyword().asString().equals("static")));

                    // Save the mutated class to a file
                    String mutatedFileName = outputDirectory + "/" + mutatedClass.getNameAsString() + "_JSD_Mutant" + (i + 1) + ".java";
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
