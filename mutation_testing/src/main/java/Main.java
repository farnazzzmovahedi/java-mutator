
import com.github.javaparser.ast.CompilationUnit;
import utils.CompilationUnits;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);

        // Input file path
        System.out.println("Enter the path to the Java program:");
        String filePath = scanner.nextLine();

        // Input mutation operators
        System.out.println("Enter the mutation operators to apply (comma-separated):");
        String[] operators = scanner.nextLine().split(",");

        File originalFile = new File(filePath);
        if (!originalFile.exists()) {
            System.out.println("File not found!");
            return;
        }

        // Instantiate the MutationEngine
        MutationEngine mutationEngine = new MutationEngine(originalFile);

        List<CompilationUnit> compilationUnits = new CompilationUnits(filePath).get();

        // Apply mutations
        mutationEngine.applyMutations(operators, compilationUnits);

        System.out.println("Mutations applied.");
    }
}
