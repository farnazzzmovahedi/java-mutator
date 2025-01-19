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
                    IOD.applyIOD(compilationUnits);
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
                    ISI.applyISI(compilationUnits);
                    break;
                case "ISD":
                    ISD.applyISD(compilationUnits);
                    break;
                case "JTD":
                    JTD.applyJTD(compilationUnits, "");
                    break;
                case "IPC":
                    IPC.applyIPC(compilationUnits);
                    break;
                case "JSI":
                    JSI.applyJSI(compilationUnits);
                    break;
                case "JSD":
                    JSD.applyJSD(compilationUnits);
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