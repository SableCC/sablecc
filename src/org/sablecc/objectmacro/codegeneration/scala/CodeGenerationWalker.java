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

package org.sablecc.objectmacro.codegeneration.scala;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.codegeneration.IntermediateRepresentation;
import org.sablecc.objectmacro.codegeneration.scala.macro.MAfterLast;
import org.sablecc.objectmacro.codegeneration.scala.macro.MAfterMany;
import org.sablecc.objectmacro.codegeneration.scala.macro.MAfterOne;
import org.sablecc.objectmacro.codegeneration.scala.macro.MBeforeFirst;
import org.sablecc.objectmacro.codegeneration.scala.macro.MBeforeMany;
import org.sablecc.objectmacro.codegeneration.scala.macro.MBeforeOne;
import org.sablecc.objectmacro.codegeneration.scala.macro.MExpandInsertPart;
import org.sablecc.objectmacro.codegeneration.scala.macro.MMacro;
import org.sablecc.objectmacro.codegeneration.scala.macro.MMacroCreator;
import org.sablecc.objectmacro.codegeneration.scala.macro.MNone;
import org.sablecc.objectmacro.codegeneration.scala.macro.MSeparator;
import org.sablecc.objectmacro.codegeneration.scala.macro.MText;
import org.sablecc.objectmacro.codegeneration.scala.macro.MTextInsert;
import org.sablecc.objectmacro.codegeneration.scala.macro.MTextInsertPart;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.AEolMacroPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AEolTextPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AExpandInsert;
import org.sablecc.objectmacro.intermediate.syntax3.node.AExpandedMacro;
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
import org.sablecc.objectmacro.intermediate.syntax3.node.ATrueBoolean;
import org.sablecc.objectmacro.intermediate.syntax3.node.PBoolean;
import org.sablecc.objectmacro.intermediate.syntax3.node.PValue;
import org.sablecc.objectmacro.intermediate.syntax3.node.TString;

public class CodeGenerationWalker
        extends DepthFirstAdapter {

    private final IntermediateRepresentation ir;

    private final File packageDirectory;

    private MText currentText;

    private MMacro currentMacro;

    private MTextInsertPart currentTextInsertPart;

    private MNone currentNone;

    private MSeparator currentSeparator;

    private MBeforeFirst currentBeforeFirst;

    private MAfterLast currentAfterLast;

    private MBeforeOne currentBeforeOne;

    private MAfterOne currentAfterOne;

    private MBeforeMany currentBeforeMany;

    private MAfterMany currentAfterMany;

    private MTextInsert currentTextInsert;

    public CodeGenerationWalker(
            IntermediateRepresentation ir,
            File packageDirectory) {

        this.ir = ir;
        this.packageDirectory = packageDirectory;
    }

    private String string(
            TString tString) {

        String string = tString.getText();
        int length = string.length();
        return string.substring(1, length - 1);
    }

    private String escapedString(
            TString tString) {

        StringBuilder sb = new StringBuilder();
        String s = string(tString);
        boolean escaped = false;
        for (char c : s.toCharArray()) {
            if (escaped) {
                escaped = false;

                if (c == '\\') {
                    sb.append('\\');
                    sb.append('\\');
                }
                else if (c == '\'') {
                    sb.append('\'');
                }
                else {
                    throw new InternalException("unhandled case");
                }
            }
            else if (c == '\\') {
                escaped = true;
            }
            else if (c == '\"') {
                sb.append('\\');
                sb.append('\"');
            }
            else {
                sb.append(c);
            }
        }

        if (escaped) {
            throw new InternalException("incomplete escape");
        }

        return sb.toString();
    }

    private boolean bool(
            PBoolean pBoolean) {

        if (pBoolean instanceof ATrueBoolean) {
            return true;
        }
        return false;
    }

    @Override
    public void inAText(
            AText node) {

        this.currentText = new MText(string(node.getName()));

        if (!this.ir.getDestinationPackage().equals("")) {
            this.currentText.newPackageDeclaration(this.ir
                    .getDestinationPackage());
        }

        for (TString param : node.getParams()) {
            this.currentText.newParamClass(string(param));
        }

        if (bool(node.getSelfRef())) {
            this.currentText.newSelfRefText();
        }

        for (TString ancersorRef : node.getAncestorRefs()) {
            this.currentText.newAncestorParamClass(string(ancersorRef));
        }

    }

    @Override
    public void outAText(
            AText node) {

        File destination = new File(this.packageDirectory, "M"
                + string(node.getName()) + ".scala");

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(this.currentText.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        this.currentText = null;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.currentMacro = new MMacro(string(node.getName()));

        if (!this.ir.getDestinationPackage().equals("")) {
            this.currentMacro.newPackageDeclaration(this.ir
                    .getDestinationPackage());
        }

        // inverse logic than in Java: by default (no keyword) the class is
        // public. The package visibility modifier corresponds to
        // "protected[<package name>]"
        if (!bool(node.getIsPublic())
                && !this.ir.getDestinationPackage().equals("")) {
            String name = this.ir.getDestinationPackage();
            if (name.contains(".")) {
                // take the last part of the package name (where the generated
                // class will be located)
                name = name.substring(name.lastIndexOf('.') + 1);
            }
            this.currentMacro.newProtected(name);
        }

        if (node.getExpands().size() > 0) {
            this.currentMacro.newImports();
        }

        for (TString param : node.getParams()) {
            this.currentMacro.newParamClass(string(param));
            // check that the given parameters are not null
            this.currentMacro.newCheckParamNotNull(string(param));
        }

        if (bool(node.getSelfRef())) {
            this.currentMacro.newSelfRefMacro();
        }

        for (TString ancersorRef : node.getAncestorRefs()) {
            this.currentMacro.newAncestorParamClass(string(ancersorRef));
            // check that the given parameters are not null
            this.currentMacro.newCheckAncestorParamNotNull(string(ancersorRef));
        }

        for (TString expand : node.getExpands()) {
            this.currentMacro.newExpandField(string(expand));
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        File destination = new File(this.packageDirectory, "M"
                + string(node.getName()) + ".scala");

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(this.currentMacro.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        this.currentMacro = null;
    }

    @Override
    public void outAParamRef(
            AParamRef node) {

        if (this.currentText != null) {
            this.currentText.newParamRef(string(node.getName()), string(node
                    .getContext()));
        }
        else if (this.currentMacro != null) {
            this.currentMacro.newParamRef(string(node.getName()), string(node
                    .getContext()));
        }
        else {
            throw new InternalException("unhandled case");
        }
    }

    @Override
    public void outAExpandedMacro(
            AExpandedMacro node) {

        MMacroCreator macroCreator = this.currentMacro
                .newMacroCreator(string(node.getName()));

        for (TString param : node.getParams()) {
            macroCreator.newParamParam(string(param));
            macroCreator.newParamArg(string(param));
        }

        for (TString ancestorRef : node.getAncestorRefs()) {
            macroCreator.newAncestorArg(string(ancestorRef));
        }

        for (TString expand : node.getExpands()) {
            macroCreator.newAddToExpand(string(expand));
        }
    }

    @Override
    public void outAStringTextPart(
            AStringTextPart node) {

        this.currentText.newStringPart(escapedString(node.getString()));
    }

    @Override
    public void outAEolTextPart(
            AEolTextPart node) {

        this.currentText.newEolPart();
    }

    @Override
    public void outAParamInsertTextPart(
            AParamInsertTextPart node) {

        this.currentText.newParamInsertPart(string(node.getParamInsert()));
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
    public void outAStringMacroPart(
            AStringMacroPart node) {

        this.currentMacro.newStringPart(escapedString(node.getString()));
    }

    @Override
    public void outAEolMacroPart(
            AEolMacroPart node) {

        this.currentMacro.newEolPart();
    }

    @Override
    public void outAParamInsertMacroPart(
            AParamInsertMacroPart node) {

        this.currentMacro.newParamInsertPart(string(node.getParamInsert()));
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

        MExpandInsertPart currentExpandInsertPart = this.currentMacro
                .newExpandInsertPart(string(node.getName()));

        if (node.getNone() != null) {
            this.currentNone = currentExpandInsertPart.newNone();
            node.getNone().apply(this);
            this.currentNone = null;
        }

        if (node.getSeparator() != null) {
            this.currentSeparator = currentExpandInsertPart.newSeparator();
            node.getSeparator().apply(this);
            this.currentSeparator = null;
        }
        else {
            currentExpandInsertPart.newNoSeparator();
        }

        if (node.getBeforeFirst() != null) {
            this.currentBeforeFirst = currentExpandInsertPart.newBeforeFirst();
            node.getBeforeFirst().apply(this);
            this.currentBeforeFirst = null;
        }

        if (node.getAfterLast() != null) {
            this.currentAfterLast = currentExpandInsertPart.newAfterLast();
            node.getAfterLast().apply(this);
            this.currentAfterLast = null;
        }

        if (node.getBeforeOne() != null) {
            this.currentBeforeOne = currentExpandInsertPart.newBeforeOne();
            node.getBeforeOne().apply(this);
            this.currentBeforeOne = null;
        }

        if (node.getAfterOne() != null) {
            this.currentAfterOne = currentExpandInsertPart.newAfterOne();
            node.getAfterOne().apply(this);
            this.currentAfterOne = null;
        }

        if (node.getBeforeMany() != null) {
            this.currentBeforeMany = currentExpandInsertPart.newBeforeMany();
            node.getBeforeMany().apply(this);
            this.currentBeforeMany = null;
        }

        if (node.getAfterMany() != null) {
            this.currentAfterMany = currentExpandInsertPart.newAfterMany();
            node.getAfterMany().apply(this);
            this.currentAfterMany = null;
        }
    }

    @Override
    public void caseATextInsert(
            ATextInsert node) {

        MTextInsert oldTextInsert = this.currentTextInsert;

        if (oldTextInsert != null) {
            this.currentTextInsert = oldTextInsert.newTextInsert(string(node
                    .getName()));
        }
        else if (this.currentNone != null) {
            this.currentTextInsert = this.currentNone.newTextInsert(string(node
                    .getName()));
        }
        else if (this.currentSeparator != null) {
            this.currentTextInsert = this.currentSeparator
                    .newTextInsert(string(node.getName()));
        }
        else if (this.currentBeforeFirst != null) {
            this.currentTextInsert = this.currentBeforeFirst
                    .newTextInsert(string(node.getName()));
        }
        else if (this.currentAfterLast != null) {
            this.currentTextInsert = this.currentAfterLast
                    .newTextInsert(string(node.getName()));
        }
        else if (this.currentBeforeOne != null) {
            this.currentTextInsert = this.currentBeforeOne
                    .newTextInsert(string(node.getName()));
        }
        else if (this.currentAfterOne != null) {
            this.currentTextInsert = this.currentAfterOne
                    .newTextInsert(string(node.getName()));
        }
        else if (this.currentBeforeMany != null) {
            this.currentTextInsert = this.currentBeforeMany
                    .newTextInsert(string(node.getName()));
        }
        else if (this.currentAfterMany != null) {
            this.currentTextInsert = this.currentAfterMany
                    .newTextInsert(string(node.getName()));
        }
        else if (this.currentTextInsertPart != null) {
            this.currentTextInsert = this.currentTextInsertPart
                    .newTextInsert(string(node.getName()));
        }
        else {
            throw new InternalException("unhandled case");
        }

        for (PValue arg : node.getArgs()) {
            arg.apply(this);
        }

        for (TString ancestorRef : node.getAncestorRefs()) {
            this.currentTextInsert.newTextInsertAncestor(string(ancestorRef));
        }

        this.currentTextInsert = oldTextInsert;
    }

    @Override
    public void outAStringValue(
            AStringValue node) {

        if (this.currentTextInsert != null) {
            this.currentTextInsert.newString(escapedString(node.getString()));
        }
        else if (this.currentNone != null) {
            this.currentNone.newString(escapedString(node.getString()));
        }
        else if (this.currentSeparator != null) {
            this.currentSeparator.newString(escapedString(node.getString()));
        }
        else if (this.currentBeforeFirst != null) {
            this.currentBeforeFirst.newString(escapedString(node.getString()));
        }
        else if (this.currentAfterLast != null) {
            this.currentAfterLast.newString(escapedString(node.getString()));
        }
        else if (this.currentBeforeOne != null) {
            this.currentBeforeOne.newString(escapedString(node.getString()));
        }
        else if (this.currentAfterOne != null) {
            this.currentAfterOne.newString(escapedString(node.getString()));
        }
        else if (this.currentBeforeMany != null) {
            this.currentBeforeMany.newString(escapedString(node.getString()));
        }
        else if (this.currentAfterMany != null) {
            this.currentAfterMany.newString(escapedString(node.getString()));
        }
        else {
            throw new InternalException("unhandled case");
        }
    }

    @Override
    public void outAParamInsertValue(
            AParamInsertValue node) {

        if (this.currentTextInsert != null) {
            this.currentTextInsert
                    .newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentNone != null) {
            this.currentNone.newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentSeparator != null) {
            this.currentSeparator.newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentBeforeFirst != null) {
            this.currentBeforeFirst
                    .newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentAfterLast != null) {
            this.currentAfterLast.newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentBeforeOne != null) {
            this.currentBeforeOne.newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentAfterOne != null) {
            this.currentAfterOne.newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentBeforeMany != null) {
            this.currentBeforeOne.newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentAfterMany != null) {
            this.currentAfterOne.newParamInsert(string(node.getParamInsert()));
        }
        else {
            throw new InternalException("unhandled case");
        }
    }
}
