/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public class MSyntaxError
        extends Macro {

    String field_Line;

    String field_Char;

    String field_TokenType;

    String field_TokenText;

    String field_Message;

    public MSyntaxError(
            String pLine,
            String pChar,
            String pTokenType,
            String pTokenText,
            String pMessage,
            Macros macros) {

        setMacros(macros);
        setPLine(pLine);
        setPChar(pChar);
        setPTokenType(pTokenType);
        setPTokenText(pTokenText);
        setPMessage(pMessage);
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

    private void setPTokenType(
            String pTokenType) {

        if (pTokenType == null) {
            throw ObjectMacroException.parameterNull("TokenType");
        }

        this.field_TokenType = pTokenType;
    }

    private void setPTokenText(
            String pTokenText) {

        if (pTokenText == null) {
            throw ObjectMacroException.parameterNull("TokenText");
        }

        this.field_TokenText = pTokenText;
    }

    private void setPMessage(
            String pMessage) {

        if (pMessage == null) {
            throw ObjectMacroException.parameterNull("Message");
        }

        this.field_Message = pMessage;
    }

    String buildLine() {

        return this.field_Line;
    }

    String buildChar() {

        return this.field_Char;
    }

    String buildTokenType() {

        return this.field_TokenType;
    }

    String buildTokenText() {

        return this.field_TokenText;
    }

    String buildMessage() {

        return this.field_Message;
    }

    String getLine() {

        return this.field_Line;
    }

    String getChar() {

        return this.field_Char;
    }

    String getTokenType() {

        return this.field_TokenType;
    }

    String getTokenText() {

        return this.field_TokenText;
    }

    String getMessage() {

        return this.field_Message;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setSyntaxError(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("SyntaxError");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("*** SYNTAX ERROR ***");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("Line: ");
        sb0.append(buildLine());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Char: ");
        sb0.append(buildChar());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Syntax error on unexpected ");
        sb0.append(buildTokenType());
        sb0.append(" token \"");
        sb0.append(buildTokenText());
        sb0.append("\":");
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildMessage());
        sb0.append(".");

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
