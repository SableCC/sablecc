/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public  class MInitMacroInternal extends Macro{
    
    String field_Name;
    
    public MInitMacroInternal(String pName, Macros macros){
        
        
        this.setMacros(macros);
        this.setPName(pName);
    }
    
    private void setPName( String pName ){
        if(pName == null){
            throw ObjectMacroException.parameterNull("Name");
        }
    
        this.field_Name = pName;
    }
    
    String buildName(){
    
        return this.field_Name;
    }
    
    String getName(){
    
        return this.field_Name;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setInitMacroInternal(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("InitMacroInternal");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("this.list_");
        sb0.append(buildName());
        sb0.append(" = new LinkedHashMap<>();");
    
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