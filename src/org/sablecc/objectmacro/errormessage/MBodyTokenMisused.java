/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MBodyTokenMisused extends Macro{
    
    String field_Line;
    
    String field_Char;
    
    public MBodyTokenMisused(String pLine, String pChar, Macros macros){
        
        
        this.setMacros(macros);
        this.setPLine(pLine);
        this.setPChar(pChar);
    }
    
    private void setPLine( String pLine ){
        if(pLine == null){
            throw ObjectMacroException.parameterNull("Line");
        }
    
        this.field_Line = pLine;
    }
    
    private void setPChar( String pChar ){
        if(pChar == null){
            throw ObjectMacroException.parameterNull("Char");
        }
    
        this.field_Char = pChar;
    }
    
    String buildLine(){
    
        return this.field_Line;
    }
    
    String buildChar(){
    
        return this.field_Char;
    }
    
    String getLine(){
    
        return this.field_Line;
    }
    
    String getChar(){
    
        return this.field_Char;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setBodyTokenMisused(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("BodyTokenMisused");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("*** SYNTAX ERROR ***");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("Line: ");
        sb0.append(buildLine());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Char: ");
        sb0.append(buildChar());
        sb0.append(LINE_SEPARATOR);
        sb0.append("The token ");
        sb0.append("{");
        sb0.append("Body} must be at the beginning of the line, at position 0.");
    
        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }
    
    @Override
    String build(Context context) {
     return build();
    }
    
    
    private void setMacros(Macros macros){
        if(macros == null){
            throw new InternalException("macros cannot be null");
        }
    
        this.macros = macros;
    }
}