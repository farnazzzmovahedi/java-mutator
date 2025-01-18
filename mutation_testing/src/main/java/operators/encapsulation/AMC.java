package operators.encapsulation;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import utils.MutantSaver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AMC {

    /**
     * Apply AMC mutation operator to a list of CompilationUnits.
     * Generates one mutant for each public field or method by changing its visibility to private.
     *
     * @param compilationUnits List of CompilationUnits representing the source code.
     */
    public static void applyAMC(List<CompilationUnit> compilationUnits) {
        int mutantIndex = 1;

        for (CompilationUnit compilationUnit : compilationUnits) {
            // Collect all public fields and methods
            List<FieldDeclaration> publicFields = new ArrayList<>();
            List<MethodDeclaration> publicMethods = new ArrayList<>();
            AtomicInteger mutationIndex = new AtomicInteger(1);

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

            // Generate mutants for each public field
            for (FieldDeclaration field : publicFields) {
                CompilationUnit clonedCU = compilationUnit.clone();
                clonedCU.findAll(FieldDeclaration.class).stream()
                        .filter(f -> f.equals(field))
                        .forEach(f -> f.setModifiers(com.github.javaparser.ast.Modifier.Keyword.PRIVATE));

                // Save the mutated CompilationUnit
                MutantSaver.save(clonedCU, "mutants\\AMC\\mutation" + mutantIndex);
                mutantIndex++;
            }

            // Generate mutants for each public method
            for (MethodDeclaration method : publicMethods) {
                CompilationUnit clonedCU = compilationUnit.clone();
                clonedCU.findAll(MethodDeclaration.class).stream()
                        .filter(m -> m.equals(method))
                        .forEach(m -> m.setModifiers(com.github.javaparser.ast.Modifier.Keyword.PRIVATE));

                // Save the mutated CompilationUnit
                MutantSaver.save(clonedCU, "mutants\\AMC\\mutation" + mutantIndex);
                mutantIndex++;
            }
        }

    }
}
