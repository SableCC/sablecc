/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

public class MUserErrorVersionNull
        extends Macro {

    public MUserErrorVersionNull() {

    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setUserErrorVersionNull(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("VersionNull");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("The version may not be null.");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(
            Context context) {

        return build();
    }
}
