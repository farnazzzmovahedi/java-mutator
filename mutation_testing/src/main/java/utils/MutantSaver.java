package utils;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MutantSaver {
    public static void save(CompilationUnit cu, String mutantPath) {
        try {
            File mutantsDir = new File(mutantPath);
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
}
