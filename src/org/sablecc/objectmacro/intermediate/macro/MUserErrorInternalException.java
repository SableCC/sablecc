/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

class MUserErrorInternalException
        extends Macro {

    private String field_StackTrace;

    private String field_Message;

    public MUserErrorInternalException(
            String pMessage) {

        setPMessage(pMessage);
    }

    private void setPMessage(
            String pMessage) {

        if (pMessage == null) {
            throw ObjectMacroException.parameterNull("Message");
        }

        this.field_Message = pMessage;
    }

    private String buildMessage() {

        return this.field_Message;
    }

    private String getMessage() {

        return this.field_Message;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setUserErrorInternalException(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException
                    .cyclicReference("UserErrorInternalException");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("*** INTERNAL ERROR ***");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("An internal error was raised with the following message:");
        sb0.append(LINE_SEPARATOR);
        sb0.append(" ");
        sb0.append(buildMessage());
        sb0.append(".");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "Please submit a defect ticket with the full error trace above on:");
        sb0.append(LINE_SEPARATOR);
        sb0.append(" http://sablecc.org/");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(
            Context context) {

        return build();
    }
}
