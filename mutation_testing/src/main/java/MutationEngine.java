import com.github.javaparser.ast.CompilationUnit;
import operators.javaSpecific.JTD;
import operators.encapsulation.AMC;
import operators.inheritance.*;
import operators.javaSpecific.JSD;
import operators.javaSpecific.JSI;
import operators.polymorphism.PNC;
import operators.polymorphism.PMD;
import operators.polymorphism.PPD;

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
                    AMC.applyAMC(compilationUnits);
                    break;
                case "IHI":
                    IHI.applyIHI(compilationUnits);
                    break;
                case "IHD":
                    IHD.applyIHD(compilationUnits);
                    break;
                case "IOD":
                    IOD.applyIOD(compilationUnits, "mutants/");
                    break;
                case "PNC":
                    PNC.applyPNC(compilationUnits);
                    break;
                case "PMD":
                    PMD.applyPMD(compilationUnits);
                    break;
                case "PPD":
                    PPD.applyPPD(compilationUnits);
                    break;
                case "ISI":
                    ISI.applyISI(compilationUnits, "mutants/Example_ISI.java");
                    break;
                case "ISD":
                    ISD.applyISD(compilationUnits, "mutants/Example_ISD.java");
                    break;
                case "JSI":
                    JSI.applyJSI(compilationUnits, "mutants/");
                    break;
                case "JSD":
                    JSD.applyJSD(compilationUnits, "mutants/");
                    break;
                case "JTD":
                    JTD.applyJTD(compilationUnits, "mutants/Example_JTD.java");
                    break;
                case "IPC":
                    IPC.applyIPC(compilationUnits, "mutants/Example_IPC.java");
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