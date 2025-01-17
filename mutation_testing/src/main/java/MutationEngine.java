import com.github.javaparser.ast.CompilationUnit;
import operators.encapsulation.AMC;
import operators.inheritance.IHI;
import operators.polymorphism.PNC;
import utils.CompilationUnits;

import java.io.File;
import java.util.List;

public class MutationEngine {
    private File originalFile;
    private List<String> mutantPaths;

    public MutationEngine(File originalFile) {
        this.originalFile = originalFile;
    }

    public void applyMutations(String[] operators, List<CompilationUnit> compilationUnits) {
        for (String operator : operators) {
            // Create mutants based on the operator
            switch (operator.trim().toUpperCase()) {
                case "AMC":
                    AMC.applyAMC(this.originalFile.toString(), "mutants/Example_AMC.java");
                    // Call the AMC mutation operator
                    break;
                case "IHI":
                    System.out.println("print IHI");
                    IHI.applyIHI(compilationUnits, "mutants/Example_IHI.java");
                    break;
                case "PNC":
                    PNC.applyPNC(compilationUnits);
                    break;
                case "PMD":
//                    PMD.applyPNC(compilationUnits);
                    break;
                // Add more cases for other operators
                default:
                    System.out.println("Unknown operator: " + operator);
            }
        }
    }

    public List<String> getMutantPaths() {
        return mutantPaths;
    }
}