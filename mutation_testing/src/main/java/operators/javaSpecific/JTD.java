package operators.javaSpecific;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.StaticJavaParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JTD {

    /**
     * Apply the JTD mutation operator step by step.
     *
     * @param compilationUnits  List of CompilationUnits representing the source code.
     * @param outputFolderPath  Path to save the resulting folders for each mutation step.
     */
    public static void applyJTD(List<CompilationUnit> compilationUnits, String outputFolderPath) {
        AtomicInteger mutationIndex = new AtomicInteger(1);

        for (CompilationUnit cu : compilationUnits) {
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                List<MethodDeclaration> methods = clazz.findAll(MethodDeclaration.class);

                for (MethodDeclaration method : methods) {
                    method.getBody().ifPresent(body -> {
                        List<ThisExpr> thisReferences = body.findAll(ThisExpr.class);

                        for (ThisExpr thisExpr : thisReferences) {
                            List<CompilationUnit> clonedUnits = cloneCompilationUnits(compilationUnits);

                            // Locate the cloned class
                            ClassOrInterfaceDeclaration clonedClass = clonedUnits.stream()
                                    .flatMap(unit -> unit.findAll(ClassOrInterfaceDeclaration.class).stream())
                                    .filter(cl -> cl.getNameAsString().equals(clazz.getNameAsString()))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Class not found in cloned units."));

                            // Locate the cloned method
                            MethodDeclaration clonedMethod = clonedClass.findAll(MethodDeclaration.class).stream()
                                    .filter(md -> md.getSignature().equals(method.getSignature()))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Method not found in cloned class."));

                            // Modify the method body in the cloned class
                            clonedMethod.getBody().ifPresent(clonedBody -> {
                                clonedBody.findAll(ThisExpr.class).forEach(expr -> {
                                    expr.getParentNode().ifPresent(parentNode -> {
                                        if (parentNode instanceof NameExpr || parentNode instanceof FieldAccessExpr) {
                                            String originalStatement = parentNode.toString();
                                            String modifiedStatement = originalStatement.replace("this.", "");
                                            parentNode.replace(StaticJavaParser.parseExpression(modifiedStatement));
                                        }
                                    });

                                    System.out.println("Replaced 'this.' in method '" + clonedMethod.getNameAsString()
                                            + "' in class '" + clonedClass.getNameAsString() + "'.");
                                });
                            });

                            // Create a folder for this mutation
                            File mutationFolder = new File(outputFolderPath, "mutation_" + mutationIndex.getAndIncrement());
                            if (!mutationFolder.exists()) {
                                mutationFolder.mkdirs();
                            }

                            // Save all modified compilation units to the folder
                            saveCompilationUnits(clonedUnits, mutationFolder);
                        }
                    });
                }
            }
        }
    }

    /**
     * Clone the list of CompilationUnits.
     *
     * @param compilationUnits  List of CompilationUnits to be cloned.
     * @return List of cloned CompilationUnits.
     */
    private static List<CompilationUnit> cloneCompilationUnits(List<CompilationUnit> compilationUnits) {
        List<CompilationUnit> clones = new ArrayList<>();
        for (CompilationUnit cu : compilationUnits) {
            clones.add(cu.clone());
        }
        return clones;
    }

    /**
     * Save a list of CompilationUnits to the specified folder.
     *
     * @param compilationUnits  List of CompilationUnits to be saved.
     * @param folder            Folder where the CompilationUnits will be saved.
     */
    private static void saveCompilationUnits(List<CompilationUnit> compilationUnits, File folder) {
        for (CompilationUnit cu : compilationUnits) {
            String className = cu.findFirst(ClassOrInterfaceDeclaration.class)
                    .map(ClassOrInterfaceDeclaration::getNameAsString)
                    .orElse("UnknownClass");
            try (FileWriter writer = new FileWriter(new File(folder, className + ".java"))) {
                writer.write(cu.toString());
            } catch (IOException e) {
                System.err.println("Error saving class " + className + ": " + e.getMessage());
            }
        }
    }
}
