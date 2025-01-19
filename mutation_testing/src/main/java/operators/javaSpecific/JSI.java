package operators.javaSpecific;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import utils.MutantSaver;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JSI {

    /**
     * Apply the JSI mutation operator.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     */
    public static void applyJSI(List<CompilationUnit> compilationUnits) {
        AtomicInteger mutantIndex = new AtomicInteger(1);

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
                for (FieldDeclaration fieldToMutate : instanceFields) {
                    // Clone the entire CompilationUnit
                    CompilationUnit clonedCU = clazz.findCompilationUnit().orElseThrow().clone();

                    // Find the corresponding class in the cloned CompilationUnit
                    ClassOrInterfaceDeclaration clonedClass = clonedCU.findFirst(ClassOrInterfaceDeclaration.class)
                            .orElseThrow();

                    // Find and update the corresponding field in the cloned class
                    clonedClass.findAll(FieldDeclaration.class).stream()
                            .filter(field -> field.equals(fieldToMutate))
                            .findFirst()
                            .ifPresent(field -> field.addModifier(com.github.javaparser.ast.Modifier.Keyword.STATIC));

                    // Save the mutated CompilationUnit
                    String mutantPath = "mutants\\JSI\\mutation" + mutantIndex.getAndIncrement();
                    MutantSaver.save(clonedCU, mutantPath);
                    System.out.println("Saved mutant: " + mutantPath);
                }
            }
        }

        System.out.println("JSI mutation applied. Mutants saved to directory: mutants\\JSI\\");
    }
}
