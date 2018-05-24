/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

class MUserErrorParameterNull
        extends Macro {

    private String field_ParamName;

    public MUserErrorParameterNull(
            String pParamName) {

        setPParamName(pParamName);
    }

    private void setPParamName(
            String pParamName) {

        if (pParamName == null) {
            throw ObjectMacroException.parameterNull("ParamName");
        }

        this.field_ParamName = pParamName;
    }

    private String buildParamName() {

        return this.field_ParamName;
    }

    private String getParamName() {

        return this.field_ParamName;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setUserErrorParameterNull(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException
                    .cyclicReference("UserErrorParameterNull");
        }
        else {
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
        sb0.append("Parameter '");
        sb0.append(buildParamName());
        sb0.append("' cannot be null.");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(
            Context context) {

        return build();
    }
}
