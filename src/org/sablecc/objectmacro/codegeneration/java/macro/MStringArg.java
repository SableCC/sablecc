/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MStringArg
        extends Macro {

    String field_Name;

    public MStringArg(
            String pName,
            Macros macros) {

        setMacros(macros);
        setPName(pName);
    }

    private void setPName(
            String pName) {

        if (pName == null) {
            throw ObjectMacroException.parameterNull("Name");
        }

        this.field_Name = pName;
    }

    String buildName() {

        return this.field_Name;
    }

    String getName() {

        return this.field_Name;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setStringArg(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("StringArg");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("field_");
        sb0.append(buildName());

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(
            Context context) {

        return build();
    }

    private void setMacros(
            Macros macros) {

        if (macros == null) {
            throw new InternalException("macros cannot be null");
        }

        this.macros = macros;
    }
}
