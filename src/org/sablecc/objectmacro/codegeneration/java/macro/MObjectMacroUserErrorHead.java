/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;

class MObjectMacroUserErrorHead extends Macro{

    MObjectMacroUserErrorHead(){

    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setObjectMacroUserErrorHead(this);
    }

    @Override
    public String build(){

        CacheBuilder cache_builder = this.cacheBuilder;

        if(cache_builder == null){
            cache_builder = new CacheBuilder();
        }
        else{
            return cache_builder.getExpansion();
        }
        this.cacheBuilder = cache_builder;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("*** OBJECT MACRO ERROR ***");

        cache_builder.setExpansion(sb0.toString());
        return sb0.toString();
    }


    @Override
    String build(Context context) {
        return build();
    }


}