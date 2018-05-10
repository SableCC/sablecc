/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MIndentPart extends Macro{
    
    private String field_IndexBuilder;
    
    
    private String field_IndexIndent;
    
    
    public MIndentPart(String pIndexBuilder, String pIndexIndent){
        
                this.setPIndexBuilder(pIndexBuilder);        this.setPIndexIndent(pIndexIndent);
        
    }
    
    private void setPIndexBuilder( String pIndexBuilder ){
        if(pIndexBuilder == null){
            throw ObjectMacroException.parameterNull("IndexBuilder");
        }
    
        this.field_IndexBuilder = pIndexBuilder;
    }
    private void setPIndexIndent( String pIndexIndent ){
        if(pIndexIndent == null){
            throw ObjectMacroException.parameterNull("IndexIndent");
        }
    
        this.field_IndexIndent = pIndexIndent;
    }
    
    private String buildIndexBuilder(){
    
        return this.field_IndexBuilder;
    }
    private String buildIndexIndent(){
    
        return this.field_IndexIndent;
    }
    
    private String getIndexBuilder(){
    
        return this.field_IndexBuilder;
    }
    private String getIndexIndent(){
    
        return this.field_IndexIndent;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setIndentPart(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("IndentPart");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
    
    
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("sb");
        sb0.append(buildIndexBuilder());
        sb0.append(".append(applyIndent(sb");
        sb0.append(buildIndexIndent());
        sb0.append(".toString(), indentations.remove(indentations.size() - 1)));");
    
        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }
    
    @Override
     String build(Context context) {
         return build();
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