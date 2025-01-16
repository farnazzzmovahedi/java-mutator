import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CompilationUnits {
    private List<CompilationUnit> compilationUnits;
    private File sourceDir;

    public CompilationUnits(String pathname) {
        this.compilationUnits = new ArrayList<>();
        this.sourceDir = new File(pathname);
    }

    private void make() throws FileNotFoundException {
        // Setup Symbol Solver
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver()); // Standard Java types
        typeSolver.add(new JavaParserTypeSolver(sourceDir)); // Project-specific types

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        // Parse all Java files in the directory
        for (File file : sourceDir.listFiles((dir, name) -> name.endsWith(".java"))) {
            CompilationUnit cu = StaticJavaParser.parse(file);
            compilationUnits.add(cu);
        }
    }

    public List<CompilationUnit> get() throws FileNotFoundException {
        make();
        return compilationUnits;
    }
}
