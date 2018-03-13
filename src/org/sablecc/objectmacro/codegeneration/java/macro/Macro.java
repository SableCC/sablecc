/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;

public abstract class Macro {

    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    public String expansion;

    public Map<Context, String> expansions = new LinkedHashMap<>();

    public String build(){

        throw new RuntimeException("build cannot be invoked here");
    }

    String build(
            Context context){

        throw new RuntimeException("build cannot be invoked here");
    }

    void apply(
            InternalsInitializer internalsInitializer){

        throw new RuntimeException("apply cannot be called here");
    }

}
