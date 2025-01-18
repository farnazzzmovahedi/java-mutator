package operators.JavaSpecific;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.Statement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class performs mutation testing by modifying "this." references in Java methods.
 */
public class JTD {

    private static final String INPUT_FILE_PATH = "C:\\Users\\Shojaei\\Documents\\my project\\aliminia\\py-mutator\\mutation_testing\\src\\main\\java\\JTDRefrencedCode\\Person.java";

    public static void applyJTD(String outputFolderPath) {
        CompilationUnit compilationUnit = parseSourceFile(INPUT_FILE_PATH);
        if (compilationUnit == null) {
            System.err.println("Failed to parse the input file.");
            return;
        }

        AtomicInteger mutationIndex = new AtomicInteger(1);

        // Iterate through all methods in the compilation unit
        for (MethodDeclaration method : compilationUnit.findAll(MethodDeclaration.class)) {
            if (hasThisReferences(method)) {
                processMethodMutations(compilationUnit, method, outputFolderPath, mutationIndex);
            }
        }
    }

    private static CompilationUnit parseSourceFile(String filePath) {
        try {
            return StaticJavaParser.parse(new File(filePath));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    private static boolean hasThisReferences(MethodDeclaration method) {
        return method.findAll(ThisExpr.class).size() > 0; // Check for "this." references using the AST
    }

    private static void processMethodMutations(CompilationUnit compilationUnit, MethodDeclaration method,
                                               String outputFolderPath, AtomicInteger mutationIndex) {
        method.getBody().ifPresent(body -> {
            // Perform mutations on all "this." references in the method body
            method.findAll(ThisExpr.class).forEach(thisExpr -> {
                // Perform the mutation by removing "this."
                Node targetNode = thisExpr.getParentNode().orElseThrow().clone();
                if (!(targetNode instanceof Expression)) {
                    throw new IllegalStateException("Parent node is not an Expression");
                }
                Expression target = (Expression) targetNode;
                thisExpr.replace(target);

                // Save the mutated version
                File mutationFolder = createMutationFolder(outputFolderPath, mutationIndex.getAndIncrement());
                writeCompilationUnitToFile(compilationUnit, mutationFolder);
            });
        });
    }

    private static File createMutationFolder(String outputFolderPath, int mutationIndex) {
        File mutationFolder = new File(outputFolderPath, "mutation_" + mutationIndex);
        if (!mutationFolder.exists() && !mutationFolder.mkdirs()) {
            throw new RuntimeException("Failed to create mutation folder: " + mutationFolder.getAbsolutePath());
        }
        return mutationFolder;
    }

    private static void writeCompilationUnitToFile(CompilationUnit cu, File folder) {
        String className = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .map(ClassOrInterfaceDeclaration::getNameAsString)
                .orElse("UnknownClass");

        File outputFile = new File(folder, className + ".java");
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(cu.toString());
        } catch (IOException e) {
            System.err.println("Error saving class to file: " + e.getMessage());
        }
    }
}