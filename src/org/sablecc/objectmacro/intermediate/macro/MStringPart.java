/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

public class MStringPart
        extends Macro {

    String field_Text;

    public MStringPart(
            String pText,
            Macros macros) {

        setMacros(macros);
        setPText(pText);
    }

    private void setPText(
            String pText) {

        if (pText == null) {
            throw ObjectMacroException.parameterNull("Text");
        }

        this.field_Text = pText;
    }

    String buildText() {

        return this.field_Text;
    }

    String getText() {

        return this.field_Text;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setStringPart(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("StringPart");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("'");
        sb0.append(buildText());
        sb0.append("'");

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
