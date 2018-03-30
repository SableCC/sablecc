package back.cycle;

import graph_generator.DynamicJavaExecutor;
import graph_generator.GraphGenerator;

import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class SequentialCase {

    private static BufferedWriter writer;

    private static Integer class_num = 0;

    static {
        try {
            writer = new BufferedWriter(new FileWriter("ResultSequentialNaive.txt", true));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(
            String[] args)throws IOException {

        String package_name = SequentialCase.class.getPackage().getName();
        String class_name = "LineCase";
        String content = GraphGenerator.sequentialCase(package_name, class_name, 1000);

        compileAndRun(package_name, class_name, content, true);

        runTest(250, package_name, class_name);
        runTest(500, package_name, class_name);
        runTest(750, package_name, class_name);

        runTest(1000, package_name, class_name);
        runTest(1250, package_name, class_name);
        runTest(1500, package_name, class_name);
        runTest(1750, package_name, class_name);
        runTest(2000, package_name, class_name);

        runTest(2250, package_name, class_name);
        runTest(2500, package_name, class_name);
        runTest(2750, package_name, class_name);
        runTest(3000, package_name, class_name);

        runTest(3250, package_name, class_name);
        runTest(3500, package_name, class_name);
        runTest(3750, package_name, class_name);
        runTest(4000, package_name, class_name);

        runTest(4250, package_name, class_name);
        runTest(4500, package_name, class_name);
        runTest(4750, package_name, class_name);
        runTest(5000, package_name, class_name);
        runTest(6000, package_name, class_name);
        writer.write("\n");
        writer.close();

        System.out.println();
        System.out.println();
    }

    private static long compileAndRun(
            String package_name,
            String class_name,
            String mainContent,
            boolean warm_up){

        JavaFileObject mainFile = DynamicJavaExecutor.getJavaFileObject(package_name, class_name, mainContent);
        Iterable<? extends JavaFileObject> files = Arrays.asList(mainFile);

        if (warm_up) {
            if(DynamicJavaExecutor.compile(files)){
                for(int i = 0; i < 100; i++){
                    DynamicJavaExecutor.run(package_name, class_name);
                }
            }
        }
        else{
            if(DynamicJavaExecutor.compile(files)){
                return DynamicJavaExecutor.run(package_name, class_name);
            }
        }

        return 0;
    }

    private static void runTest(
            int nb_nodes,
            String package_name,
            String class_name) throws IOException{

        long total_time = 0;
        System.out.println("=========== SEQUENTIAL for " + nb_nodes + " nodes ==============");
        for(int i = class_num; i < class_num + 30; i++){
            String content = GraphGenerator.sequentialCase(package_name, class_name + i, nb_nodes);
            total_time += compileAndRun(package_name, class_name + i, content, false);
        }
        class_num += 30;

        writer.write( total_time / 30 + "\n");
        System.out.println("Total time taken : " + total_time + " ns");
        System.out.println("Average total time taken : " + total_time / 30 + " ns");
    }
}


