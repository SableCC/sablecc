/* This file is part of SableCC ( http://sablecc.org ).
 *
 * See the NOTICE file distributed with this work for copyright information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sablecc.objectmacro.codegeneration.intermediate;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MExpandInsertPart;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MExpandedMacro;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MIntermediateRepresentation;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MMacro;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MText;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MTextInsert;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MTextInsertArg;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MTextInsertOption;
import org.sablecc.objectmacro.codegeneration.intermediate.macro.MTextInsertPart;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.AEolMacroPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AEolTextPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AExpandInsert;
import org.sablecc.objectmacro.intermediate.syntax3.node.AExpandedMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.AIntermediateRepresentation;
import org.sablecc.objectmacro.intermediate.syntax3.node.AMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParamInsertMacroPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParamInsertTextPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParamInsertValue;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParamRef;
import org.sablecc.objectmacro.intermediate.syntax3.node.AStringMacroPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AStringTextPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AStringValue;
import org.sablecc.objectmacro.intermediate.syntax3.node.AText;
import org.sablecc.objectmacro.intermediate.syntax3.node.ATextInsert;
import org.sablecc.objectmacro.intermediate.syntax3.node.ATextInsertMacroPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.ATextInsertTextPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.ATextInsertValue;
import org.sablecc.objectmacro.intermediate.syntax3.node.ATrueBoolean;
import org.sablecc.objectmacro.intermediate.syntax3.node.PBoolean;
import org.sablecc.objectmacro.intermediate.syntax3.node.PValue;
import org.sablecc.objectmacro.intermediate.syntax3.node.TString;

public class CodeGenerationWalker
        extends DepthFirstAdapter {

    private MIntermediateRepresentation intermediateRepresentation;

    private MText currentText;

    private MMacro currentMacro;

    private MTextInsertPart currentTextInsertPart;

    private MExpandInsertPart currentExpandInsertPart;

    private String currentOption;

    private MTextInsert currentTextInsert;

    private String currentIndent = "";

    private MTextInsertOption currentTextInsertOption;

    private MTextInsertArg currentTextInsertArg;

    public String getStringRepresentation() {

        return this.intermediateRepresentation.toString();
    }

    private String booleanString(
            PBoolean pBoolean) {

        if (pBoolean instanceof ATrueBoolean) {
            return "true";
        }
        return "false";
    }

    @Override
    public void inAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        this.intermediateRepresentation = new MIntermediateRepresentation();
    }

    @Override
    public void inAText(
            AText node) {

        this.currentText = this.intermediateRepresentation.newText(node
                .getName().getText(), booleanString(node.getSelfRef()));

        for (TString param : node.getParams()) {
            this.currentText.newParam(param.getText(), "");
        }

        for (TString ancestorRef : node.getAncestorRefs()) {
            this.currentText.newAncestorRef(ancestorRef.getText(), "");
        }
    }

    @Override
    public void outAText(
            AText node) {

        this.currentText = null;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.currentMacro = this.intermediateRepresentation.newMacro(node
                .getName().getText(), booleanString(node.getIsPublic()),
                booleanString(node.getSelfRef()));

        for (TString param : node.getParams()) {
            this.currentMacro.newParam(param.getText(), "");
        }

        for (TString ancestorRef : node.getAncestorRefs()) {
            this.currentMacro.newAncestorRef(ancestorRef.getText(), "");
        }

        for (TString expand : node.getExpands()) {
            this.currentMacro.newExpand(expand.getText(), "");
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentMacro = null;
    }

    @Override
    public void inAParamRef(
            AParamRef node) {

        if (this.currentText != null) {
            this.currentText.newParamRef(node.getName().getText(), node
                    .getContext().getText());
        }
        else if (this.currentMacro != null) {
            this.currentMacro.newParamRef(node.getName().getText(), node
                    .getContext().getText());
        }
        else {
            throw new InternalException("unhandled case");
        }
    }

    @Override
    public void inAExpandedMacro(
            AExpandedMacro node) {

        MExpandedMacro expandedMacro = this.currentMacro.newExpandedMacro(node
                .getName().getText());

        for (TString param : node.getParams()) {
            expandedMacro.newParam(param.getText(), "  ");
        }

        for (TString ancestorRef : node.getAncestorRefs()) {
            expandedMacro.newAncestorRef(ancestorRef.getText(), "  ");
        }

        for (TString expand : node.getExpands()) {
            expandedMacro.newExpand(expand.getText(), "  ");
        }
    }

    @Override
    public void inAStringTextPart(
            AStringTextPart node) {

        this.currentText.newStringPart(node.getString().getText());
    }

    @Override
    public void inAEolTextPart(
            AEolTextPart node) {

        this.currentText.newEolPart();
    }

    @Override
    public void inAParamInsertTextPart(
            AParamInsertTextPart node) {

        this.currentText.newParamInsertPart(node.getParamInsert().getText());
    }

    @Override
    public void inATextInsertTextPart(
            ATextInsertTextPart node) {

        this.currentTextInsertPart = this.currentText.newTextInsertPart();
    }

    @Override
    public void outATextInsertTextPart(
            ATextInsertTextPart node) {

        this.currentTextInsertPart = null;
    }

    @Override
    public void inAStringMacroPart(
            AStringMacroPart node) {

        this.currentMacro.newStringPart(node.getString().getText());
    }

    @Override
    public void inAEolMacroPart(
            AEolMacroPart node) {

        this.currentMacro.newEolPart();
    }

    @Override
    public void inAParamInsertMacroPart(
            AParamInsertMacroPart node) {

        this.currentMacro.newParamInsertPart(node.getParamInsert().getText());
    }

    @Override
    public void inATextInsertMacroPart(
            ATextInsertMacroPart node) {

        this.currentTextInsertPart = this.currentMacro.newTextInsertPart();
    }

    @Override
    public void outATextInsertMacroPart(
            ATextInsertMacroPart node) {

        this.currentTextInsertPart = null;
    }

    @Override
    public void caseAExpandInsert(
            AExpandInsert node) {

        this.currentExpandInsertPart = this.currentMacro
                .newExpandInsertPart(node.getName().getText());

        if (node.getNone() != null) {
            this.currentOption = "none";
            node.getNone().apply(this);
        }

        if (node.getSeparator() != null) {
            this.currentOption = "separator";
            node.getSeparator().apply(this);
        }

        if (node.getBeforeFirst() != null) {
            this.currentOption = "before_first";
            node.getBeforeFirst().apply(this);
        }

        if (node.getAfterLast() != null) {
            this.currentOption = "after_last";
            node.getAfterLast().apply(this);
        }

        this.currentExpandInsertPart = null;
        this.currentOption = null;
    }

    @Override
    public void caseATextInsert(
            ATextInsert node) {

        MTextInsert oldTextInsert = this.currentTextInsert;

        if (this.currentTextInsertArg != null) {
            this.currentTextInsert = this.currentTextInsertArg.newTextInsert(
                    node.getName().getText(), this.currentIndent);
        }
        else if (this.currentTextInsertOption != null) {
            this.currentTextInsert = this.currentTextInsertOption
                    .newTextInsert(node.getName().getText(), this.currentIndent);
        }
        else if (this.currentTextInsertPart != null) {
            this.currentTextInsert = this.currentTextInsertPart.newTextInsert(
                    node.getName().getText(), this.currentIndent);
        }
        else {
            throw new InternalException("unhandled case");
        }

        for (PValue arg : node.getArgs()) {
            arg.apply(this);
        }

        for (TString ancestorRef : node.getAncestorRefs()) {
            this.currentTextInsert.newAncestorRef(ancestorRef.getText(),
                    this.currentIndent + "  ");
        }

        this.currentTextInsert = oldTextInsert;
    }

    @Override
    public void inAStringValue(
            AStringValue node) {

        if (node.parent() instanceof AExpandInsert) {
            this.currentExpandInsertPart.newStringOption(this.currentOption,
                    node.getString().getText());
        }
        else if (node.parent() instanceof ATextInsert) {
            this.currentTextInsert.newStringArg(node.getString().getText(),
                    this.currentIndent);
        }
        else {
            throw new InternalException("unhandled case");
        }
    }

    @Override
    public void inAParamInsertValue(
            AParamInsertValue node) {

        if (node.parent() instanceof AExpandInsert) {
            this.currentExpandInsertPart.newParamInsertOption(
                    this.currentOption, node.getParamInsert().getText());
        }
        else if (node.parent() instanceof ATextInsert) {
            this.currentTextInsert.newParamInsertArg(node.getParamInsert()
                    .getText(), this.currentIndent);
        }
        else {
            throw new InternalException("unhandled case");
        }
    }

    @Override
    public void caseATextInsertValue(
            ATextInsertValue node) {

        if (node.parent() instanceof AExpandInsert) {

            this.currentTextInsertOption = this.currentExpandInsertPart
                    .newTextInsertOption(this.currentOption);
            String oldIndent = this.currentIndent;
            this.currentIndent += "    ";

            node.getTextInsert().apply(this);

            this.currentTextInsertOption = null;
            this.currentIndent = oldIndent;

        }
        else if (node.parent() instanceof ATextInsert) {

            MTextInsertArg oldTextInsertArg = this.currentTextInsertArg;

            this.currentTextInsertArg = this.currentTextInsert
                    .newTextInsertArg(this.currentIndent);

            String oldIndent = this.currentIndent;
            this.currentIndent += "    ";

            node.getTextInsert().apply(this);

            this.currentTextInsertArg = oldTextInsertArg;
            this.currentIndent = oldIndent;
        }
        else {
            throw new InternalException("unhandled case");
        }
    }
}
