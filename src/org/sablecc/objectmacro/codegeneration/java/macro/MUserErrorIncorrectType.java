/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

class MUserErrorIncorrectType
        extends Macro {

    private String field_Type;

    private String field_ParamName;

    public MUserErrorIncorrectType(
            String pType,
            String pParamName) {

        setPType(pType);
        setPParamName(pParamName);
    }

    private void setPType(
            String pType) {

        if (pType == null) {
            throw ObjectMacroException.parameterNull("Type");
        }

        this.field_Type = pType;
    }

    private void setPParamName(
            String pParamName) {

        if (pParamName == null) {
            throw ObjectMacroException.parameterNull("ParamName");
        }

        this.field_ParamName = pParamName;
    }

    private String buildType() {

        return this.field_Type;
    }

    private String buildParamName() {

        return this.field_ParamName;
    }

    private String getType() {

        return this.field_Type;
    }

    private String getParamName() {

        return this.field_ParamName;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setUserErrorIncorrectType(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException
                    .cyclicReference("UserErrorIncorrectType");
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
        sb0.append(buildType());
        sb0.append(" was not expected in parameter '");
        sb0.append(buildParamName());
        sb0.append("'.");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(
            Context context) {

        return build();
    }
}