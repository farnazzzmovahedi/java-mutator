package operators.polymorphism;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import utils.CompilationUnits;

public class PNC {
    public static void applyPNC(List<CompilationUnit> compilationUnits) {
        Random random = new Random();

        // List to store all ObjectCreationExpr instances related to "Parent"
        List<ObjectCreationExpr> allParentCreations = new ArrayList<>();

        // Iterate over all CompilationUnits to collect relevant ObjectCreationExpr instances
        for (CompilationUnit cu : compilationUnits) {
            List<ObjectCreationExpr> objectCreations = cu.findAll(ObjectCreationExpr.class);

            // Add only those ObjectCreationExpr instances with type "Parent"
            allParentCreations.addAll(
                    objectCreations.stream()
                            .filter(objectCreation -> objectCreation.getType().toString().equals("Parent"))
                            .toList()
            );
        }

        // If no mutation points are found, exit without performing any mutation
        if (allParentCreations.isEmpty()) {
            System.out.println("No valid mutation points found.");
            return;
        }

        // Randomly select one ObjectCreationExpr from the list
        ObjectCreationExpr selectedMutation = allParentCreations.get(random.nextInt(allParentCreations.size()));

        // Change the type of the selected ObjectCreationExpr to "Child"
        selectedMutation.setType("Child");

        // Save the mutated code for the CompilationUnit where the mutation occurred
        Optional<Node> rootNode = Optional.ofNullable(selectedMutation.findRootNode());
        if (rootNode.isPresent() && rootNode.get() instanceof CompilationUnit) {
            CompilationUnit mutatedCU = (CompilationUnit) rootNode.get();
            saveMutatedCode(mutatedCU);
        }


    }

    private static void saveMutatedCode(CompilationUnit cu) {
        try {
            File mutantsDir = new File("D:\\University\\4031\\Software Testing\\Project\\py-mutator\\mutation_testing\\src\\main\\java\\mutants");
            if (!mutantsDir.exists()) {
                mutantsDir.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(new File(mutantsDir, "Mutant_" + cu.getStorage().get().getFileName()));
            out.write(cu.toString().getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        String inputFilePath = "D:\\University\\4031\\Software Testing\\Project\\py-mutator\\mutation_testing\\src\\main\\java\\RefrenceCode";
        List<CompilationUnit> compilationUnits = new CompilationUnits(inputFilePath).get();

        // Apply the AMC mutation operator
        applyPNC(compilationUnits);
    }

}
