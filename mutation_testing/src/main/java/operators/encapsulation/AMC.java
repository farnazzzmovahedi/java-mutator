package operators.encapsulation;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AMC {

    /**
     * Apply AMC mutation operator to a Java source file.
     * @param inputFilePath the path to the original Java file
     * @param outputFilePath the path to save the mutated Java file
     */
    public static void applyAMC(String inputFilePath, String outputFilePath) {
        try {
            // Parse the original source code file into a CompilationUnit
            CompilationUnit compilationUnit = StaticJavaParser.parse(new File(inputFilePath));

            // Collect all public fields and methods
            List<FieldDeclaration> publicFields = new ArrayList<>();
            List<MethodDeclaration> publicMethods = new ArrayList<>();

            compilationUnit.findAll(FieldDeclaration.class).forEach(field -> {
                if (field.isPublic()) {
                    publicFields.add(field);
                }
            });

            compilationUnit.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.isPublic()) {
                    publicMethods.add(method);
                }
            });

            // Combine fields and methods into a single list
            List<Object> publicMembers = new ArrayList<>();
            publicMembers.addAll(publicFields);
            publicMembers.addAll(publicMethods);

            // Randomly select one member and modify its visibility
            if (!publicMembers.isEmpty()) {
                Random random = new Random();
                Object selectedMember = publicMembers.get(random.nextInt(publicMembers.size()));

                if (selectedMember instanceof FieldDeclaration) {
                    ((FieldDeclaration) selectedMember).setModifiers(com.github.javaparser.ast.Modifier.Keyword.PRIVATE);
                } else if (selectedMember instanceof MethodDeclaration) {
                    ((MethodDeclaration) selectedMember).setModifiers(com.github.javaparser.ast.Modifier.Keyword.PRIVATE);
                }

                System.out.println("Modified one public member to private.");
            } else {
                System.out.println("No public members found to modify.");
            }

            // Save the mutated code to the output file
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                writer.write(compilationUnit.toString());
            }

            System.out.println("AMC mutation applied. Mutated file saved to: " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Error processing the file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java AMC <inputFilePath> <outputFilePath>");
            return;
        }

        String inputFilePath = args[0];
        String outputFilePath = args[1];

        // Apply the AMC mutation operator
        applyAMC(inputFilePath, outputFilePath);
    }
}
