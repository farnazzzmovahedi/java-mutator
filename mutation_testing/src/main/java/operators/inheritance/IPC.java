package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IPC {

    /**
     * Apply the IPC mutation operator step by step.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     */
    public static void applyIPC(List<CompilationUnit> compilationUnits) {
        // Maps to store parent-child relationships
        Map<String, List<ClassOrInterfaceDeclaration>> parentChildMap = new HashMap<>();

        // Identify parent-child relationships dynamically
        for (CompilationUnit cu : compilationUnits) {
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                clazz.getExtendedTypes().forEach(parent -> {
                    parentChildMap.computeIfAbsent(parent.getNameAsString(), k -> new ArrayList<>()).add(clazz);
                });
            }
        }

        AtomicInteger mutationIndex = new AtomicInteger(1);

        // Process parent-child relationships
        for (Map.Entry<String, List<ClassOrInterfaceDeclaration>> entry : parentChildMap.entrySet()) {
            String parentTypeName = entry.getKey();
            List<ClassOrInterfaceDeclaration> childClasses = entry.getValue();

            // Process each child class
            for (ClassOrInterfaceDeclaration childClass : childClasses) {
                // Create a copy of the original compilation units to avoid modifying other parts
                List<CompilationUnit> clonedUnits = cloneCompilationUnits(compilationUnits);

                ClassOrInterfaceDeclaration currentChild = clonedUnits.stream()
                        .flatMap(cu -> cu.findAll(ClassOrInterfaceDeclaration.class).stream())
                        .filter(clazz -> clazz.getNameAsString().equals(childClass.getNameAsString()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Child class not found in cloned units."));

                // Process constructors of the child class
                List<ConstructorDeclaration> childConstructors = currentChild.findAll(ConstructorDeclaration.class);

                for (ConstructorDeclaration constructor : childConstructors) {
                    // Look for explicit parent constructor invocations
                    Optional<ExplicitConstructorInvocationStmt> explicitInvocation = constructor.getBody()
                            .findFirst(ExplicitConstructorInvocationStmt.class);

                    explicitInvocation.ifPresent(invocation -> {
                        // Remove the explicit parent constructor call
                        boolean deletionSuccessful = constructor.getBody().remove(invocation);

                        if (deletionSuccessful) {
                            System.out.println("Removed explicit parent constructor call in constructor '" +
                                    constructor.getDeclarationAsString(false, false, false) +
                                    "' of class '" + currentChild.getNameAsString() + "'.");

                            String outputFolderPath = "mutants\\IPC";

                            // Save the mutated class only
                            File mutationFolder = new File(outputFolderPath, "mutation_" + mutationIndex.getAndIncrement());
                            if (!mutationFolder.exists()) {
                                mutationFolder.mkdirs();
                            }

                            // Save only the mutated class
                            saveMutatedClass(currentChild, mutationFolder);
                        }
                    });
                }
            }
        }
    }

    /**
     * Clone the list of CompilationUnits.
     */
    private static List<CompilationUnit> cloneCompilationUnits(List<CompilationUnit> compilationUnits) {
        List<CompilationUnit> clones = new ArrayList<>();
        for (CompilationUnit cu : compilationUnits) {
            clones.add(cu.clone());
        }
        return clones;
    }

    /**
     * Save only the mutated class to the specified folder.
     *
     * @param mutatedClass The mutated class to save.
     * @param folder       The folder where the file will be written.
     */
    private static void saveMutatedClass(ClassOrInterfaceDeclaration mutatedClass, File folder) {
        // Determine the file name using the class name
        CompilationUnit parentCompilationUnit = mutatedClass.findCompilationUnit()
                .orElseThrow(() -> new RuntimeException("Unable to find CompilationUnit for the mutated class."));
        String className = mutatedClass.getNameAsString();

        try (FileWriter writer = new FileWriter(new File(folder, "Mutant" + className + ".java"))) {
            writer.write(parentCompilationUnit.toString());
        } catch (IOException e) {
            System.err.println("Error saving class " + className + ": " + e.getMessage());
        }
    }
}