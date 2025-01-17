package operators.inheritance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.Modifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ISD {

    /**
     * Apply the ISI mutation operator.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     * @param outputFolderPath Path to save the parent and child class files.
     */
    public static void applyISD(List<CompilationUnit> compilationUnits, String outputFolderPath) {
        Random random = new Random();

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

        // Process parent-child relationships
        parentChildMap.forEach((parentTypeName, childClasses) -> {
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
                    // Randomly select a method from the parent class
                    MethodDeclaration methodToShadow = parentMethods.get(random.nextInt(parentMethods.size()));
                    String methodName = methodToShadow.getNameAsString();
                    String methodReturnType = methodToShadow.getTypeAsString();

                    // Create output folder if it doesn't exist
                    File outputFolder = new File(outputFolderPath);
                    if (!outputFolder.exists()) {
                        outputFolder.mkdirs();
                    }

                    // Save the parent class to a file
                    try (FileWriter parentWriter = new FileWriter(new File(outputFolder, parentClass.getNameAsString() + ".java"))) {
                        parentWriter.write(parentClass.toString());
                    } catch (IOException e) {
                        System.err.println("Error saving parent class: " + e.getMessage());
                    }


                    // Apply mutation to each child class
                    for (ClassOrInterfaceDeclaration childClass : childClasses) {
                        childClass.getMethods().stream()
                                .filter(method -> method.getNameAsString().equals(methodName))
                                .filter(method -> method.getBody().isPresent()
                                        && method.getBody().get().toString().contains("return"))
                                .forEach(childClass::remove);

                        MethodDeclaration overriddenMethod = new MethodDeclaration();
                        overriddenMethod.setName(methodName);
                        overriddenMethod.setType(methodReturnType);
                        overriddenMethod.addModifier(Modifier.Keyword.PUBLIC);

                        methodToShadow.getParameters().forEach(overriddenMethod::addParameter);

                        overriddenMethod.setBody(new com.github.javaparser.ast.stmt.BlockStmt()
                                .addStatement("return " + methodName + "();"));

                        childClass.addMember(overriddenMethod);

                        System.out.println("Added shadowing method '" + methodName + "' in child class '" + childClass.getNameAsString() + "'");

                        try (FileWriter childWriter = new FileWriter(new File(outputFolder, childClass.getNameAsString() + ".java"))) {
                            childWriter.write(childClass.toString());
                        } catch (IOException e) {
                            System.err.println("Error saving child class: " + e.getMessage());
                        }
                    }

                }
            }
        });
    }
}