package mutantManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MutantTester {

    private static final String PROJECT_DIR = "C:\\github-projects\\java-mutator\\RefrenceCode";// Your project directory
    private static final String SRC_DIR = PROJECT_DIR + "\\src\\main\\java\\org\\example\\"; // Source directory
    private static final String MUTANT_DIR = "mutants"; // Path to mutants directory
    private static final String BACKUP_DIR = PROJECT_DIR + "\\backup";

    public static void main(String[] args) {
        try {
            // Step 1: Backup the original classes (if not already backed up)
            backupOriginalClasses();

            // Step 2: Get all mutant files
            List<File> mutants = getAllMutants(new File(MUTANT_DIR));

            // Step 3: Test each mutant
            for (File mutant : mutants) {
                String className = extractClassName(mutant);
                System.out.println("Testing mutant for class: " + className);

                System.out.println("BEGA RAFTAM");
                // Replace the original class with the mutant
                replaceClassWithMutant(mutant, className);

                // Compile the project
                boolean compiled = compileProject();
                if (!compiled) {
                    System.err.println("Failed to compile mutant: " + mutant.getPath());
                    continue;
                }

                // Run tests
//                boolean testsPassed = runTests();
//
//                // Log results
//                if (testsPassed) {
//                    System.out.println("Mutant survived: " + mutant.getPath());
//                } else {
//                    System.out.println("Mutant killed: " + mutant.getPath());
//                }
                // Step 4: Restore the original classes
                restoreOriginalClasses();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void backupOriginalClasses() throws IOException {
        File srcDir = new File(SRC_DIR);
        File backupDir = new File(BACKUP_DIR);
        backupDir.mkdirs();
        if (!srcDir.exists()) {
            System.err.println("Source directory does not exist: " + SRC_DIR);
            return;
        }
        if (!srcDir.isDirectory()) {
            System.err.println("Source path is not a directory: " + SRC_DIR);
            return;
        }
        System.out.println("Source directory is valid: " + SRC_DIR);

        File[] files = srcDir.listFiles();
        if (files == null) {
            System.err.println("No files found in the source directory: " + SRC_DIR);
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                Path originalPath = file.toPath();
                Path backupPath = Paths.get(BACKUP_DIR, file.getName());
                Files.copy(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backed up: " + file.getName());
            }
        }


        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                Path originalPath = file.toPath();
                Path backupPath = backupDir.toPath().resolve(file.getName());
                Files.copy(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        System.out.println("All original classes backed up.");
    }

    private static List<File> getAllMutants(File root) {
        List<File> mutants = new ArrayList<>();
        if (root.isDirectory()) {
            for (File file : root.listFiles()) {
                mutants.addAll(getAllMutants(file)); // Recursively traverse subdirectories
            }
        } else if (root.getName().startsWith("Mutant_") && root.getName().endsWith(".java")) {
            mutants.add(root); // Add mutant file
        }
        return mutants;
    }

    private static String extractClassName(File mutant) {
        String mutantName = mutant.getName(); // e.g., "Mutant_Calculator.java"
        return mutantName.substring(7, mutantName.length() - 5); // Extract "Calculator"
    }

    private static void replaceClassWithMutant(File mutant, String className) throws IOException {
        Path mutantPath = mutant.toPath();
        Path originalPath = Paths.get(SRC_DIR + className + ".java");
        Files.copy(mutantPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Replaced original class with mutant: " + mutant.getPath());
    }

    private static boolean compileProject() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("C:\\Users\\iCFixer.ir\\Downloads\\apache-maven-3.9.9\\bin\\mvn.cmd", "clean", "compile");
        System.out.println("Executing command: " + String.join(" ", pb.command()));
        pb.directory(new File(PROJECT_DIR));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor();
        return process.exitValue() == 0; // Returns true if compile succeeded
    }

    private static boolean runTests() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("C:\\Users\\iCFixer.ir\\Downloads\\apache-maven-3.9.9\\bin\\mvn.cmd", "test");
        pb.directory(new File(PROJECT_DIR));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor();
        return process.exitValue() == 0; // Returns true if tests passed
    }

    private static void restoreOriginalClasses() throws IOException {
        File backupDir = new File(BACKUP_DIR);
        File srcDir = new File(SRC_DIR);

        if (!backupDir.exists() || !backupDir.isDirectory()) {
            System.err.println("Backup directory is invalid: " + BACKUP_DIR);
            return;
        }

        if (!srcDir.exists() || !srcDir.isDirectory()) {
            System.err.println("Source directory is invalid: " + SRC_DIR);
            return;
        }

        System.out.println("Restoring original classes to: " + SRC_DIR);

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".java")); // Filter only Java files
        if (backupFiles == null || backupFiles.length == 0) {
            System.err.println("No Java files found in the backup directory.");
            return;
        }

        for (File file : backupFiles) {
            System.out.println("BEGA");
            Path backupPath = file.toPath();
            Path originalPath = srcDir.toPath().resolve(file.getName());
            Files.copy(backupPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Restored: " + file.getName());
        }

        System.out.println("All original classes restored successfully.");
    }

}
