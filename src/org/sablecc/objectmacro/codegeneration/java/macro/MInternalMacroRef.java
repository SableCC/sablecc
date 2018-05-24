/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public  class MInternalMacroRef extends Macro{
    
    String field_ParamName;
    
    public MInternalMacroRef(String pParamName, Macros macros){
        
        
        this.setMacros(macros);
        this.setPParamName(pParamName);
    }
    
    private void setPParamName( String pParamName ){
        if(pParamName == null){
            throw ObjectMacroException.parameterNull("ParamName");
        }
    
        this.field_ParamName = pParamName;
    }
    
    String buildParamName(){
    
        return this.field_ParamName;
    }
    
    String getParamName(){
    
        return this.field_ParamName;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setInternalMacroRef(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("InternalMacroRef");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("private InternalValue get");
        sb0.append(buildParamName());
        sb0.append("(Context context)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return this.list_");
        sb0.append(buildParamName());
        sb0.append(".get(context);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");
    
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