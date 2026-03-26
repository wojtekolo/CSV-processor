package pl.wojtekolo.studia.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyClassLoader extends ClassLoader{
    private final String classPath;

    public MyClassLoader(String classPath) {
        this.classPath = classPath;
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            String fileName = name.replace('.', '/') + ".class";
            Path path = Paths.get(classPath, fileName);

            byte[] bytes = Files.readAllBytes(path);

            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Nie znaleziono klasy: " + name, e);
        }
    }
}
