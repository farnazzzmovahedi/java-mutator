
import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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

        // Apply mutations
        mutationEngine.applyMutations(operators);

        System.out.println("Mutations applied.");
    }
}
