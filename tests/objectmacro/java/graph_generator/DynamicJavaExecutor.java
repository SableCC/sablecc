package graph_generator;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

public class DynamicJavaExecutor {

    /** java File Object represents an in-memory java source file <br>
     * so there is no need to put the source file on hard disk  **/
    public static class InMemoryJavaFileObject extends SimpleJavaFileObject
    {
        private String contents = null;

        public InMemoryJavaFileObject(String className, String contents) throws Exception
        {
            super(URI.create(className.replace('.', '/')
                    + Kind.SOURCE.extension), Kind.SOURCE);

            this.contents = contents;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException
        {
            return contents;
        }
    }

    public static JavaFileObject getJavaFileObject(
            String package_name,
            String class_name,
            String content) {

        JavaFileObject so = null;
        try
        {
            so = new InMemoryJavaFileObject(package_name.concat(".").concat(class_name), content);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return so;
    }

    public static boolean compile(
            Iterable<? extends JavaFileObject> files){

        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticListener listener = new DiagnosticListener<JavaFileObject>() {

            @Override public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
                System.out.println("Line Number->" + diagnostic.getLineNumber());
                System.out.println("code->" + diagnostic.getCode());
                System.out.println("Message->"
                        + diagnostic.getMessage(Locale.FRENCH));
                System.out.println("Source->" + diagnostic.getSource());
            }
        };

        // for compilation diagnostic message processing on compilation WARNING/ERROR
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(listener
                , Locale.ENGLISH, null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", "classes/");
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                listener, options, null,
                files);
        Boolean result = task.call();

        return result;
    }

    public static long run(
            String package_name,
            String class_name){

        // Create a File object on the root of the directory
        // containing the class file
        File file = new File("classes/");
        Class thisClass;

        try
        {
            // Convert File to a URL
            URL url = file.toURI().toURL(); // file:/classes/demo
            URL[] urls = new URL[] { url };

            // Create a new class loader with the directory
            ClassLoader loader = new URLClassLoader(urls);

            thisClass = loader.loadClass(package_name.concat(".").concat(class_name));

            Method method = thisClass.getMethod("main", String[].class);

            method.invoke(null, (Object) null);
            Field field = thisClass.getDeclaredField("exec_time");
            return field.getLong(null);
        }
        catch(InvocationTargetException e){

        }
        catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException
                | NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
}
