package operators.polymorphism;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.body.MethodDeclaration;
import utils.MutantSaver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PPD {

    public static void applyPPD(List<CompilationUnit> compilationUnits) {
        // Map to store child-to-parent relationships
        Map<ClassOrInterfaceType, ClassOrInterfaceType> parentToChildMap = new HashMap<>();

        // Step 1: Identify parent-to-child relationships
        for (CompilationUnit cu : compilationUnits) {
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration clazz : classes) {
                clazz.getExtendedTypes().forEach(parent -> {
                    // Store the parent-to-child relationship
                    parentToChildMap.put(parent, new ClassOrInterfaceType(null, clazz.getNameAsString()));
                });
            }
        }

        // Step 2: Iterate over all methods and update parameter types
        for (CompilationUnit cu : compilationUnits) {
            List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

            for (MethodDeclaration method : methods) {
                List<Parameter> parameters = method.getParameters();

                for (Parameter parameter : parameters) {
                    Type parameterType = parameter.getType();

                    if (parameterType.isClassOrInterfaceType()) {
                        ClassOrInterfaceType parentType = parameterType.asClassOrInterfaceType();

                        // Check if there is a child class for the parent type
                        if (parentToChildMap.containsKey(parentType)) {
                            ClassOrInterfaceType childType = parentToChildMap.get(parentType);

                            // Change the parameter type to the child class type
                            parameter.setType(childType);

                            // Save the mutated code for the CompilationUnit where the mutation occurred
                            MutantSaver.save(cu,  "D:\\University\\4031\\Software Testing\\Project\\py-mutator\\mutation_testing\\src\\main\\java\\mutants\\Example_PPD");

                        }
                    }
                }
            }
        }
    }

}
