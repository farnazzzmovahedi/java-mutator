package operators.polymorphism;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import utils.MutantSaver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PMD {
    public static void applyPMD(List<CompilationUnit> compilationUnits) {

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

        AtomicInteger i = new AtomicInteger(1);

        // Iterate over all CompilationUnits to look for member variable declarations with child class type
        for (CompilationUnit cu : compilationUnits) {
            // Find all member variables (fields) of the classes
            List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);

            fields.stream()
                    .filter(field -> {
                        // Ensure that the field's element type is a class or interface type
                        return field.getElementType().isClassOrInterfaceType() && childToParentMap.containsKey(field.getElementType().asClassOrInterfaceType());
                    })
                    .forEach(field -> {
                        // Get the element type and ensure it's a class or interface type
                        if (field.getElementType().isClassOrInterfaceType()) {
                            ClassOrInterfaceType childType = field.getElementType().asClassOrInterfaceType();

                            // Get the parent class for the child class
                            ClassOrInterfaceType parentType = childToParentMap.get(childType);

                            if (parentType != null) {
                                // Change the type of the member variable to the parent class
                                childType.setName(parentType.getNameAsString());

                                // Save the mutated code for the CompilationUnit where the mutation occurred
                                MutantSaver.save(cu, "D:\\University\\4031\\Software Testing\\Project\\py-mutator\\mutation_testing\\mutants\\PMD\\mutation"+i);
                                i.getAndIncrement();
                            }
                        } else {
                            // Handle primitive types or other non-ClassOrInterfaceType cases
                            // You can add custom logic here if needed
                            System.out.println("Field is not a ClassOrInterfaceType: " + field.getElementType());
                        }
                    });
        }
    }
}
