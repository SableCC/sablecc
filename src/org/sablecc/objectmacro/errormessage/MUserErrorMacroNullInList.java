/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;
import java.util.*;

class MUserErrorMacroNullInList extends Macro{

    private String field_Index;

    private String field_ParamName;

    public MUserErrorMacroNullInList(String pIndex, String pParamName){

        this.setPIndex(pIndex);
        this.setPParamName(pParamName);

    }

    private void setPIndex( String pIndex ){
        if(pIndex == null){
            throw ObjectMacroException.parameterNull("Index");
        }

        this.field_Index = pIndex;
    }

    private void setPParamName( String pParamName ){
        if(pParamName == null){
            throw ObjectMacroException.parameterNull("ParamName");
        }

        this.field_ParamName = pParamName;
    }

    private String buildIndex(){

        return this.field_Index;
    }

    private String buildParamName(){

        return this.field_ParamName;
    }

    private String getIndex(){

        return this.field_Index;
    }

    private String getParamName(){

        return this.field_ParamName;
    }

    @Override
    void apply(
             InternalsInitializer internalsInitializer){

        internalsInitializer.setUserErrorMacroNullInList(this);
    }


    @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("UserErrorMacroNullInList");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        MObjectMacroUserErrorHead minsert_1 = new MObjectMacroUserErrorHead();

        sb0.append(minsert_1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("A macro is null at index ");
        sb0.append(buildIndex());
        sb0.append(" in the list '");
        sb0.append(buildParamName());
        sb0.append("'.");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }


    @Override
    String build(Context context) {
        return build();
    }
}