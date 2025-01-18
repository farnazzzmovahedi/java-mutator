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
import java.util.Optional;


public class PNC {
    public static void applyPNC(List<CompilationUnit> compilationUnits) {
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

        int i = 1;

        // Iterate through all found ObjectCreationExpr to create mutants
        for (ObjectCreationExpr sMutation : allParentCreations) {

            // Get the parent class of the selected mutation
            ClassOrInterfaceType parentClassName = sMutation.getType();

            // Check if we have a child class for the parent class
            List<ClassOrInterfaceDeclaration> childClasses = parentChildMap.get(parentClassName);
            if (childClasses != null && !childClasses.isEmpty()) {
                for (ClassOrInterfaceDeclaration selectedChildClass : childClasses) {

                    // Get the root CompilationUnit of the current mutation
                    Node rootNode = sMutation.findRootNode();
                    if (rootNode instanceof CompilationUnit) {
                        CompilationUnit originalCU = (CompilationUnit) rootNode;

                        // Clone the entire CompilationUnit
                        CompilationUnit clonedCU = originalCU.clone();

                        // Find the corresponding ObjectCreationExpr in the cloned CompilationUnit
                        Optional<ObjectCreationExpr> clonedMutationOpt = clonedCU.findFirst(ObjectCreationExpr.class,
                                expr -> expr.getRange().equals(sMutation.getRange())); // Match by position

                        if (clonedMutationOpt.isPresent()) {
                            ObjectCreationExpr clonedMutation = clonedMutationOpt.get();

                            // Apply the mutation to the cloned ObjectCreationExpr
                            clonedMutation.setType(selectedChildClass.getNameAsString());

                            // Save the mutated CompilationUnit
                            MutantSaver.save(clonedCU, "mutants\\PNC\\mutation" + i);
                            i++;
                        }
                    }
                }
            }
        }

    }
}
