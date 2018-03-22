/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MDirectiveFields extends Macro{

    private String field_ParamName;


    public MDirectiveFields(String pParamName){

        this.setPParamName(pParamName);


    }

    private void setPParamName( String pParamName ){
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

        internalsInitializer.setDirectiveFields(this);
    }

   @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("DirectiveFields");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        

        

        StringBuilder sb0 = new StringBuilder();

        sb0.append("    private DSeparator ");
        sb0.append(buildParamName());
        sb0.append("Separator;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private DBeforeFirst ");
        sb0.append(buildParamName());
        sb0.append("BeforeFirst;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private DAfterLast ");
        sb0.append(buildParamName());
        sb0.append("AfterLast;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private DNone ");
        sb0.append(buildParamName());
        sb0.append("None;");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(Context context) {
        return build();
    }
}