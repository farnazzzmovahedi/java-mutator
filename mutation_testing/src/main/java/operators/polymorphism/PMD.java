package operators.polymorphism;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import utils.CompilationUnits;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PMD {
    public static void applyPMD(List<CompilationUnit> compilationUnits) {
        Random random = new Random();

        // Map to store child-to-parent relationships
        Map<ClassOrInterfaceType, ClassOrInterfaceType> childToParentMap = new HashMap<>();

        // Iterate over all CompilationUnits to identify child-to-parent relationships dynamically
        for (CompilationUnit cu : compilationUnits) {
            // Find all classes in the CompilationUnit
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                // If the class extends another class (parent-child relationship)
                clazz.getExtendedTypes().forEach(parent -> {
                    // Store the child-to-parent relationship
                    childToParentMap.put(new ClassOrInterfaceType(null, clazz.getNameAsString()), parent);
                });
            }
        }

        // Iterate over all CompilationUnits to look for member variable declarations with child class type
        for (CompilationUnit cu : compilationUnits) {
            // Find all member variables (fields) of the classes
            List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);

            fields.stream()
                    .filter(field -> childToParentMap.containsKey(field.getElementType().asClassOrInterfaceType())) // Check if it's of child type
                    .forEach(field -> {
                        // Get the child class type of the field
                        ClassOrInterfaceType childType = (ClassOrInterfaceType) field.getElementType();

                        // Get the parent class for the child class
                        ClassOrInterfaceType parentType = childToParentMap.get(childType);

                        if (parentType != null) {
                            // Change the type of the member variable to the parent class
                            childType.setName(parentType.getNameAsString());

                            // Save the mutated code for the CompilationUnit where the mutation occurred
                            saveMutatedCode(cu);
                        }
                    });
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
        applyPMD(compilationUnits);
    }
    
}
