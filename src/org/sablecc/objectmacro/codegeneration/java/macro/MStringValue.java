/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MStringValue extends Macro{

    private String field_String;


    public MStringValue(String pString){

        this.setPString(pString);


    }

    private void setPString( String pString ){
        if(pString == null){
            throw ObjectMacroException.parameterNull("String");
        }

        this.field_String = pString;
    }

    private String buildString(){

        return this.field_String;
    }

    private String getString(){

        return this.field_String;
    }


    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setStringValue(this);
    }

   @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("StringValue");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        

        

        StringBuilder sb0 = new StringBuilder();

        sb0.append("\"");
        sb0.append(buildString());
        sb0.append("\"");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(Context context) {
        return build();
    }
}