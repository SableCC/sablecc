package graph_generator;

import graph_generator.long_sequential.*;
import graph_generator.random_case.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GraphGenerator {

    private static List<Integer> macro_names = new LinkedList<>();

    public static String sequentialCase(
            String package_name,
            String class_name,
            int nbNodes){

        MClassMain main = new MClassMain(class_name, String.valueOf(nbNodes));

        if(package_name != null){
            main.addPackage(new MPackage(package_name));
        }

        return main.build();
    }

    public static String randomCase(
            String package_name,
            String class_name,
            int nbNodes,
            boolean newGeneration){

        MMain main = new MMain(class_name);

        if(package_name != null){
            main.addPackage(new MPackageDeclaration(package_name));
        }

        for(int i = 1; i <= nbNodes; i++){
            main.addMacroDeclarations(new MNewMacro("" + i));
            main.addInitMacros(new MInitMacro("" + i));
        }

        MLinkMethod mLinkMethod = new MLinkMethod("" + 0);
        main.addLinkMethods(mLinkMethod);
        main.addCallsLinkFunction(new MCallLinkFunction("" + 0));
        int nbAdd = 0;
        int nbMethod = 1;
        MAddMacro addMacro;

        if(newGeneration){
            macro_names = new LinkedList<>();
            macro_names.add(0);
        }

        for(int i = 1; i <= nbNodes; i++) {
            if(nbAdd >= 250){
                nbMethod++;
                mLinkMethod = new MLinkMethod("" + nbMethod);
                main.addLinkMethods(mLinkMethod);
                main.addCallsLinkFunction(new MCallLinkFunction("" + nbMethod));
                nbAdd = 0;
            }
            int randomNum = 0;
            if(newGeneration){
                do{
                    randomNum = ThreadLocalRandom.current().nextInt(1, nbNodes);
                }
                while(randomNum == i);
                macro_names.add(randomNum);
            }
            else{
                randomNum = macro_names.get(i);
            }

            if(i != randomNum){
                addMacro = new MAddMacro("" + i, "" + randomNum);
                mLinkMethod.addMacroLinks(addMacro);
                nbAdd++;
            }
        }

        return main.build();
    }
}
