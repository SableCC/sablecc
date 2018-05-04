/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MInternalMacroSetter extends Macro{

    private String field_ParamName;

    public MInternalMacroSetter(String pParamName){

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

        internalsInitializer.setInternalMacroSetter(this);
    }

    @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("InternalMacroSetter");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        
        
        StringBuilder sb0 = new StringBuilder();

        sb0.append("    void set");
        sb0.append(buildParamName());
        sb0.append("(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("                Context context,");
        sb0.append(LINE_SEPARATOR);
        sb0.append("                InternalValue internal_value) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("            if(internal_value == null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("                throw new RuntimeException(\"macros cannot be null\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("            this.list_");
        sb0.append(buildParamName());
        sb0.append(".put(context, internal_value);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(Context context) {
        return build();
    }
}
