package operators.JavaSpecific;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JTD {

    private static final String INPUT_FILE_PATH = "C:\\Users\\Shojaei\\Documents\\my project\\aliminia\\py-mutator\\mutation_testing\\src\\main\\java\\JTDRefrencedCode\\Person.java";

    public static void applyJTD(String outputFolderPath) {
        CompilationUnit compilationUnit = parseSourceFile(INPUT_FILE_PATH);
        if (compilationUnit == null) return;

        AtomicInteger mutationIndex = new AtomicInteger(1);

        for (MethodDeclaration method : compilationUnit.findAll(MethodDeclaration.class)) {
            if (hasThisReferences(method)) {
                mutationIndex = processMethodMutations(compilationUnit, method, outputFolderPath, mutationIndex);
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
        return method.getBody()
                .map(body -> body.toString().contains("this."))
                .orElse(false);
    }

    private static AtomicInteger processMethodMutations(CompilationUnit compilationUnit, MethodDeclaration method, String outputFolderPath,  AtomicInteger mutationIndex) {
        // Iterate over method body statements and identify mutations
        method.getBody().ifPresent(body -> {
            List<Statement> statements = body.getStatements();

            for (Statement statement : statements) {
                String statementStr = statement.toString();
                if (statementStr.contains("this.")) {
                    String mutatedBody = mutateStatement(statementStr);

                    // Create mutated statement and update method body
                    Statement mutatedStatement = new ExpressionStmt(new NameExpr(mutatedBody.trim()));
                    body.replace(statement, mutatedStatement);

                    // Create mutation folder and save the mutated compilation unit
                    File mutationFolder = createMutationFolder(outputFolderPath, mutationIndex.getAndIncrement());
                    writeCompilationUnitToFile(compilationUnit, mutationFolder);
                }
            }
        });

        return mutationIndex;
    }

    private static String mutateStatement(String statement) {
        // Remove one occurrence of "this."
        int thisIndex = statement.indexOf("this.");
        if (thisIndex != -1) {
            statement = statement.substring(0, thisIndex) + statement.substring(thisIndex + 4); // Remove "this."
        }
        return statement;
    }

    private static File createMutationFolder(String outputFolderPath, int mutationIndex) {
        File mutationFolder = new File(outputFolderPath, "mutation_" + mutationIndex);
        if (!mutationFolder.exists()) {
            mutationFolder.mkdirs();
        }
        return mutationFolder;
    }

    private static void writeCompilationUnitToFile(CompilationUnit cu, File folder) {
        String className = cu.findFirst(MethodDeclaration.class)
                .map(MethodDeclaration::getNameAsString)
                .orElse("UnknownClass");
        try (FileWriter writer = new FileWriter(new File(folder, className + ".java"))) {
            writer.write(cu.toString());
        } catch (IOException e) {
            System.err.println("Error saving class: " + e.getMessage());
        }
    }
}
