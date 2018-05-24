/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;
import java.util.*;

class MUserErrorCannotModify extends Macro{

    private String field_MacroName;

    public MUserErrorCannotModify(String pMacroName){

        this.setPMacroName(pMacroName);
    }

    private void setPMacroName( String pMacroName ){
        if(pMacroName == null){
            throw ObjectMacroException.parameterNull("MacroName");
        }

        this.field_MacroName = pMacroName;
    }

    private String buildMacroName(){

        return this.field_MacroName;
    }

    private String getMacroName(){

        return this.field_MacroName;
    }

    @Override
    void apply(
         InternalsInitializer internalsInitializer){

        internalsInitializer.setUserErrorCannotModify(this);
    }

    @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("UserErrorCannotModify");
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
        sb0.append("Instance of M");
        sb0.append(buildMacroName());
        sb0.append(" cannot be updated after the macro has been built.");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }


    @Override
    String build(Context context) {
        return build();
    }
}