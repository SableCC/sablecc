/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MInvalidInsert extends Macro{
    
    String field_Line;
    
    String field_Char;
    
    String field_Name;
    
    public MInvalidInsert(String pLine, String pChar, String pName, Macros macros){
        
        
        this.setMacros(macros);
        this.setPLine(pLine);
        this.setPChar(pChar);
        this.setPName(pName);
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
    
    private void setPName( String pName ){
        if(pName == null){
            throw ObjectMacroException.parameterNull("Name");
        }
    
        this.field_Name = pName;
    }
    
    String buildLine(){
    
        return this.field_Line;
    }
    
    String buildChar(){
    
        return this.field_Char;
    }
    
    String buildName(){
    
        return this.field_Name;
    }
    
    String getLine(){
    
        return this.field_Line;
    }
    
    String getChar(){
    
        return this.field_Char;
    }
    
    String getName(){
    
        return this.field_Name;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setInvalidInsert(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("InvalidInsert");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        MSemanticErrorHead m1 = this.getMacros().newSemanticErrorHead();
        
        
        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("Line : ");
        sb0.append(buildLine());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Char : ");
        sb0.append(buildChar());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Macro '");
        sb0.append(buildName());
        sb0.append("' cannot be inserted because it also has parameters.");
    
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