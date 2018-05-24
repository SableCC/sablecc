/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MInvalidArgumentCount extends Macro{
    
    
    public MInvalidArgumentCount(Macros macros){
        
        
        this.setMacros(macros);
    }
    
    
    
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setInvalidArgumentCount(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("InvalidArgumentCount");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        MCommandLineErrorHead m3 = this.getMacros().newCommandLineErrorHead();
        
        
        sb0.append(m3.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("A single macro file must be provided.");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        MCommandLineErrorTail m4 = this.getMacros().newCommandLineErrorTail();
        
        
        sb0.append(m4.build(null));
    
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