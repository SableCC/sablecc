/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MInitDirectiveCall extends Macro{

    private String field_ParamName;

    public MInitDirectiveCall(String pParamName){

        this.setPParamName(pParamName);
    }

    private void setPParamName(String pParamName){
        if(pParamName == null){
            throw ObjectMacroException.parameterNull("ParamName");
        }

        this.field_ParamName = pParamName;
    }

    private String buildParamName(){

        return this.field_ParamName;
    }

    private String getParamName(){

        return this.field_ParamName;
    }
    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setInitDirectiveCall(this);
    }

    @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("InitDirectiveCall");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        
        
        StringBuilder sb0 = new StringBuilder();

        sb0.append("init");
        sb0.append(buildParamName());
        sb0.append("Directives();");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(Context context) {
        return build();
    }
}
