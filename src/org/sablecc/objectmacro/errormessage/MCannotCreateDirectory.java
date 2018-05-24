/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MCannotCreateDirectory extends Macro{
    
    String field_Location;
    
    public MCannotCreateDirectory(String pLocation, Macros macros){
        
        
        this.setMacros(macros);
        this.setPLocation(pLocation);
    }
    
    private void setPLocation( String pLocation ){
        if(pLocation == null){
            throw ObjectMacroException.parameterNull("Location");
        }
    
        this.field_Location = pLocation;
    }
    
    String buildLocation(){
    
        return this.field_Location;
    }
    
    String getLocation(){
    
        return this.field_Location;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setCannotCreateDirectory(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("CannotCreateDirectory");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        MCommandLineErrorHead m1 = this.getMacros().newCommandLineErrorHead();
        
        
        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("The directory \"");
        sb0.append(buildLocation());
        sb0.append("\" could not be created.");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        MCommandLineErrorTail m2 = this.getMacros().newCommandLineErrorTail();
        
        
        sb0.append(m2.build(null));
    
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