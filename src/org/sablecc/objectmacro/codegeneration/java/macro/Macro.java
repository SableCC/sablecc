/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;

public abstract class Macro {

    final static String LINE_SEPARATOR = System.getProperty("line.separator");

    CacheBuilder cacheBuilder = null;

    final Map<Context, CacheBuilder> cacheBuilders = new LinkedHashMap<>();

    final LinkedList<Macro> children = new LinkedList<>();

    static final CycleDetector cycleDetector = new CycleDetector();

    Macros macros;

    abstract String build(Context context);

    void apply(
            InternalsInitializer internalsInitializer){

        throw new RuntimeException("apply cannot be called here");
    }

    LinkedList<Macro> getChildren(){
        return this.children;
    }

    Macros getMacros(){
        return this.macros;
    }

    String applyIndent(
                String macro,
                String indent){

        StringBuilder sb = new StringBuilder();
        String[] lines = macro.split( "\\n");

        if(lines.length > 1){
            for(int i = 0; i < lines.length; i++){
                String line = lines[i];
                sb.append(indent).append(line);

                if(i < lines.length - 1){
                    sb.append(LINE_SEPARATOR);
                }
            }
        }
        else{
            sb.append(indent).append(macro);
        }

        return sb.toString();
    }
}