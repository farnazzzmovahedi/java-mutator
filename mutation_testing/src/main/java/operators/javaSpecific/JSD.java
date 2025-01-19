package operators.javaSpecific;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import utils.MutantSaver;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JSD {

    /**
     * Apply the JSD mutation operator.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     */
    public static void applyJSD(List<CompilationUnit> compilationUnits) {
        AtomicInteger mutantIndex = new AtomicInteger(1);

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
                for (FieldDeclaration fieldToMutate : staticFields) {
                    // Clone the entire CompilationUnit
                    CompilationUnit clonedCU = clazz.findCompilationUnit().orElseThrow().clone();

                    // Find the corresponding class in the cloned CompilationUnit
                    ClassOrInterfaceDeclaration clonedClass = clonedCU.findFirst(ClassOrInterfaceDeclaration.class)
                            .orElseThrow();

                    // Find and update the corresponding field in the cloned class
                    clonedClass.findAll(FieldDeclaration.class).stream()
                            .filter(field -> field.equals(fieldToMutate))
                            .findFirst()
                            .ifPresent(field -> field.getModifiers().removeIf(
                                    modifier -> modifier.getKeyword().asString().equals("static")));

                    // Save the mutated CompilationUnit
                    String mutantPath = "mutants\\JSD\\mutation" + mutantIndex.getAndIncrement();
                    MutantSaver.save(clonedCU, mutantPath);
                    System.out.println("Saved mutant: " + mutantPath);
                }
            }
        }

        System.out.println("JSD mutation applied. Mutants saved to directory: mutants\\JSD\\");
    }
}
