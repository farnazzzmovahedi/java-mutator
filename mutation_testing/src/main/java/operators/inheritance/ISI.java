package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.Modifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ISI {

    /**
     * Apply the ISI mutation operator step by step.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     * @param outputFolderPath Path to save the resulting folders for each mutation step.
     */
    public static void applyISI(List<CompilationUnit> compilationUnits, String outputFolderPath) {
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

            // Find the parent class
            Optional<ClassOrInterfaceDeclaration> parentClassOpt = compilationUnits.stream()
                    .flatMap(cu -> cu.findAll(ClassOrInterfaceDeclaration.class).stream())
                    .filter(clazz -> clazz.getNameAsString().equals(parentTypeName))
                    .findFirst();

            if (parentClassOpt.isPresent()) {
                ClassOrInterfaceDeclaration parentClass = parentClassOpt.get();

                // Retrieve all methods declared in the parent class
                List<MethodDeclaration> parentMethods = parentClass.findAll(MethodDeclaration.class);

                if (!parentMethods.isEmpty()) {
                    for (MethodDeclaration methodToShadow : parentMethods) {
                        String methodName = methodToShadow.getNameAsString();
                        String methodReturnType = methodToShadow.getTypeAsString();

                        for (ClassOrInterfaceDeclaration childClass : childClasses) {
                            // Create a copy of the original compilation units to avoid modifying other parts
                            List<CompilationUnit> clonedUnits = cloneCompilationUnits(compilationUnits);

                            ClassOrInterfaceDeclaration currentChild = clonedUnits.stream()
                                    .flatMap(cu -> cu.findAll(ClassOrInterfaceDeclaration.class).stream())
                                    .filter(clazz -> clazz.getNameAsString().equals(childClass.getNameAsString()))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Child class not found in cloned units."));

                            // Check for conditions before removing and overriding methods
                            currentChild.getMethods().stream()
                                    .filter(method -> method.getNameAsString().equals(methodName))
                                    .filter(method -> method.getBody().isPresent()
                                            && method.getBody().get().toString().contains("return")
                                            && !method.getBody().get().toString().contains("return super." + methodName + "()"))
                                    .forEach(method -> {
                                        boolean deletionSuccessful = currentChild.remove(method);

                                        if (deletionSuccessful) {
                                            MethodDeclaration overriddenMethod = new MethodDeclaration();
                                            overriddenMethod.setName(methodName);
                                            overriddenMethod.setType(methodReturnType);
                                            overriddenMethod.addModifier(Modifier.Keyword.PUBLIC);

                                            methodToShadow.getParameters().forEach(overriddenMethod::addParameter);

                                            overriddenMethod.setBody(new com.github.javaparser.ast.stmt.BlockStmt()
                                                    .addStatement("return super." + methodName + "();"));

                                            currentChild.addMember(overriddenMethod);

                                            System.out.println("Added shadowing method '" + methodName + "' in child class '" + currentChild.getNameAsString() + "'");

                                            // Create a separate folder for this mutation
                                            File mutationFolder = new File(outputFolderPath, "mutation_" + mutationIndex.getAndIncrement());
                                            if (!mutationFolder.exists()) {
                                                mutationFolder.mkdirs();
                                            }

                                            // Save all compilation units to the folder
                                            saveCompilationUnits(clonedUnits, mutationFolder);
                                        }
                                    });
                        }
                    }
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
     * Save a list of CompilationUnits to the specified folder.
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
