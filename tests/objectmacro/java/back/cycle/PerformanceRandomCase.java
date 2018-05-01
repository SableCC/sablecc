package back.cycle;

import graph_generator.DynamicJavaExecutor;
import graph_generator.GraphGenerator;

import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class PerformanceRandomCase {

    private static BufferedWriter writer;

    private static Integer class_num = 0;

    static {
        try {
            writer = new BufferedWriter(new FileWriter("ResultRandomDelta.txt", true));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(
            String[] args)throws IOException {

        String delta_package = "back.delta";
        String naif_package = PerformanceRandomCase.class.getPackage().getName();
        String class_name = "RandomCase";
        String content = GraphGenerator.sequentialCase(naif_package, class_name, 1000);
        writer.write("RANDOM :\n");
        compileAndRun(delta_package, class_name, content, true);

        runTest(250, naif_package, class_name, true);
//        runTest(250, delta_package, class_name, false);
        runTest(500, naif_package, class_name, true);
//        runTest(500, delta_package, class_name, false);
        runTest(750, naif_package, class_name, true);
//        runTest(750, delta_package, class_name, false);

        runTest(1000, naif_package, class_name, true);
//        runTest(1000, delta_package, class_name, false);
        runTest(1250, naif_package, class_name, true);
//        runTest(1250, delta_package, class_name, false);
        runTest(1500, naif_package, class_name, true);
//        runTest(1500, delta_package, class_name, false);
        runTest(1750, naif_package, class_name, true);
//        runTest(1750, delta_package, class_name, false);
        runTest(2000, naif_package, class_name, true);
//        runTest(2000, delta_package, class_name, false);

        runTest(2250, naif_package, class_name, true);
//        runTest(2250, delta_package, class_name, false);
        runTest(2500, naif_package, class_name, true);
//        runTest(2500, delta_package, class_name, false);
        runTest(2750, naif_package, class_name, true);
//        runTest(2750, delta_package, class_name, false);
        runTest(3000, naif_package, class_name, true);
//        runTest(3000, delta_package, class_name, false);

        runTest(3250, naif_package, class_name, true);
//        runTest(3250, delta_package, class_name, false);
        runTest(3500, naif_package, class_name, true);
//        runTest(3500, delta_package, class_name, false);
        runTest(3750, naif_package, class_name, true);
//        runTest(3750, delta_package, class_name, false);
        runTest(4000, naif_package, class_name, true);
//        runTest(4000, delta_package, class_name, false);

        runTest(4250, naif_package, class_name, true);
//        runTest(4250, delta_package, class_name, false);
        runTest(4500, naif_package, class_name, true);
//        runTest(4500, delta_package, class_name, false);
        runTest(4750, naif_package, class_name, true);
//        runTest(4750, delta_package, class_name, false);
        runTest(5000, naif_package, class_name, true);
//        runTest(5000, delta_package, class_name, false);
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
            String class_name,
            boolean newGeneration) throws IOException{

        long total_time = 0;
        System.out.println("=========== RANDOM for " + nb_nodes + " nodes ==============");
        GraphGenerator.randomCase(package_name, class_name, nb_nodes, newGeneration);
        for(int i = class_num; i < class_num + 30; i++){
            String content = GraphGenerator.randomCase(package_name, class_name + i, nb_nodes, false);
            total_time += compileAndRun(package_name, class_name + i, content, false);
        }
        class_num += 30;

        writer.write( total_time / 30 + "\n");
        System.out.println("Total time taken : " + total_time + " ns");
        System.out.println("Average total time taken : " + total_time / 30 + " ns");
    }
}
