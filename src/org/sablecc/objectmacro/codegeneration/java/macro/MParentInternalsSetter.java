/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MParentInternalsSetter
        extends Macro {

    String field_Name;

    public MParentInternalsSetter(
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

        internalsInitializer.setParentInternalsSetter(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("ParentInternalsSetter");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("void set");
        sb0.append(buildName());
        sb0.append("(M");
        sb0.append(buildName());
        sb0.append(" m");
        sb0.append(buildName());
        sb0.append(")");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    throw ObjectMacroException.incorrectType(\"M");
        sb0.append(buildName());
        sb0.append("\", this._paramName);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");

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
