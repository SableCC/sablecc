/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MNewBuildState extends Macro{

    public MNewBuildState(){
    }
    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setNewBuildState(this);
    }

    @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("NewBuildState");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        
        
        StringBuilder sb0 = new StringBuilder();

        sb0.append("this.build_states.put(context, buildState)");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(Context context) {
        return build();
    }
}
