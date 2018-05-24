/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public class MUnknownParam
        extends Macro {

    String field_Name;

    String field_Line;

    String field_Char;

    public MUnknownParam(
            String pName,
            String pLine,
            String pChar,
            Macros macros) {

        setMacros(macros);
        setPName(pName);
        setPLine(pLine);
        setPChar(pChar);
    }

    private void setPName(
            String pName) {

        if (pName == null) {
            throw ObjectMacroException.parameterNull("Name");
        }

        this.field_Name = pName;
    }

    private void setPLine(
            String pLine) {

        if (pLine == null) {
            throw ObjectMacroException.parameterNull("Line");
        }

        this.field_Line = pLine;
    }

    private void setPChar(
            String pChar) {

        if (pChar == null) {
            throw ObjectMacroException.parameterNull("Char");
        }

        this.field_Char = pChar;
    }

    String buildName() {

        return this.field_Name;
    }

    String buildLine() {

        return this.field_Line;
    }

    String buildChar() {

        return this.field_Char;
    }

    String getName() {

        return this.field_Name;
    }

    String getLine() {

        return this.field_Line;
    }

    String getChar() {

        return this.field_Char;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setUnknownParam(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("UnknownParam");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        MSemanticErrorHead m1 = getMacros().newSemanticErrorHead();

        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("Line: ");
        sb0.append(buildLine());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Char: ");
        sb0.append(buildChar());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Parameter \"");
        sb0.append(buildName());
        sb0.append("\" does not exist.");

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
