package operators.polymorphism;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import utils.MutantSaver;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Optional;


public class PNC {
    public static void applyPNC(List<CompilationUnit> compilationUnits) {
        Random random = new Random();

        // Maps to store parent-child relationships
        Map<ClassOrInterfaceType, List<ClassOrInterfaceDeclaration>> parentChildMap = new HashMap<>();

        // Iterate over all CompilationUnits to identify parent-child relationships dynamically
        for (CompilationUnit cu : compilationUnits) {
            // Find all classes in the CompilationUnit
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                // If the class extends another class (parent-child relationship)
                clazz.getExtendedTypes().forEach(parent -> {
                    // Store the parent-child relationship
                    parentChildMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(clazz);
                });
            }
        }

        // List to store all ObjectCreationExpr instances related to parent classes
        List<ObjectCreationExpr> allParentCreations = new ArrayList<>();

        // Iterate over all CompilationUnits to collect ObjectCreationExpr instances related to parents
        for (CompilationUnit cu : compilationUnits) {
            // Find all ObjectCreationExpr instances
            List<ObjectCreationExpr> objectCreations = cu.findAll(ObjectCreationExpr.class);

            objectCreations.stream()
                    .filter(objectCreation -> parentChildMap.containsKey(objectCreation.getType()))
                    .forEach(allParentCreations::add);
        }

        // If no valid parent class object creations found, return
        if (allParentCreations.isEmpty()) {
            System.out.println("No valid mutation points found.");
            return;
        }

        // Randomly select one ObjectCreationExpr from the list
        ObjectCreationExpr selectedMutation = allParentCreations.get(random.nextInt(allParentCreations.size()));

        // Get the parent class of the selected mutation
        ClassOrInterfaceType parentClassName = selectedMutation.getType();

        // Check if we have a child class for the parent class
        List<ClassOrInterfaceDeclaration> childClasses = parentChildMap.get(parentClassName);
        if (childClasses != null && !childClasses.isEmpty()) {
            // Randomly select a child class from the list of children
            ClassOrInterfaceDeclaration selectedChildClass = childClasses.get(random.nextInt(childClasses.size()));

            // Change the type of the selected ObjectCreationExpr to the selected child class
            selectedMutation.setType(selectedChildClass.getNameAsString());

            // Save the mutated code for the CompilationUnit where the mutation occurred
            Optional<Node> rootNode = Optional.ofNullable(selectedMutation.findRootNode());
            if (rootNode.isPresent() && rootNode.get() instanceof CompilationUnit) {
                CompilationUnit mutatedCU = (CompilationUnit) rootNode.get();
                MutantSaver.save(mutatedCU, "D:\\University\\4031\\Software Testing\\Project\\py-mutator\\mutation_testing\\src\\main\\java\\mutants\\Example_PNC");
            }
        }
    }
}
