/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

public class MParamName extends Macro{
    
    private Map<Context, String> field_Name = new LinkedHashMap<>();
    
    
    
    
    public MParamName(){
    
    
    }
    
    
        void setName(
                Context context,
                String value) {
    
            if(value == null){
                throw new RuntimeException("value cannot be null here");
            }
    
            this.field_Name.put(context, value);
        }
    
    
    private String buildName(Context context){
    
        return this.field_Name.get(context);
    }
    
    
    private String getName(Context context){
    
        return this.field_Name.get(context);
    }
    
    
    
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setParamName(this);
     }
    
    
    @Override
    public String build(Context context){
    
        BuildState buildState = this.build_states.get(context);
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("ParamName");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_states.put(context, buildState);
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("ParamName ");
        sb0.append("{");
        sb0.append(" Name = '");
        sb0.append(buildName(context));
        sb0.append("' }");
    
        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }
    private String applyIndent(
                            String macro,
                            String indent){

            StringBuilder sb = new StringBuilder();
            String[] lines = macro.split( "\n");

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