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

package org.sablecc.objectmacro.codegeneration.c;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.codegeneration.IntermediateRepresentation;
import org.sablecc.objectmacro.codegeneration.c.macro.MAbstractMacroH;
import org.sablecc.objectmacro.codegeneration.c.macro.MAfterLast;
import org.sablecc.objectmacro.codegeneration.c.macro.MAfterMany;
import org.sablecc.objectmacro.codegeneration.c.macro.MAfterOne;
import org.sablecc.objectmacro.codegeneration.c.macro.MBeforeFirst;
import org.sablecc.objectmacro.codegeneration.c.macro.MBeforeMany;
import org.sablecc.objectmacro.codegeneration.c.macro.MBeforeOne;
import org.sablecc.objectmacro.codegeneration.c.macro.MExpandInsertPart;
import org.sablecc.objectmacro.codegeneration.c.macro.MFile;
import org.sablecc.objectmacro.codegeneration.c.macro.MInlineText;
import org.sablecc.objectmacro.codegeneration.c.macro.MListC;
import org.sablecc.objectmacro.codegeneration.c.macro.MListH;
import org.sablecc.objectmacro.codegeneration.c.macro.MMacroC;
import org.sablecc.objectmacro.codegeneration.c.macro.MMacroCreator;
import org.sablecc.objectmacro.codegeneration.c.macro.MMacroCreatorPrototype;
import org.sablecc.objectmacro.codegeneration.c.macro.MMacroH;
import org.sablecc.objectmacro.codegeneration.c.macro.MNone;
import org.sablecc.objectmacro.codegeneration.c.macro.MSeparator;
import org.sablecc.objectmacro.codegeneration.c.macro.MTextC;
import org.sablecc.objectmacro.codegeneration.c.macro.MTextH;
import org.sablecc.objectmacro.codegeneration.c.macro.MTextInsert;
import org.sablecc.objectmacro.codegeneration.c.macro.MTextInsertPart;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.AEolInlineText;
import org.sablecc.objectmacro.intermediate.syntax3.node.AEolMacroPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AEolTextPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AExpandInsert;
import org.sablecc.objectmacro.intermediate.syntax3.node.AExpandedMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.AInlineTextValue;
import org.sablecc.objectmacro.intermediate.syntax3.node.AMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParamInsertMacroPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParamInsertTextPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParamInsertValue;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParamRef;
import org.sablecc.objectmacro.intermediate.syntax3.node.AStringInlineText;
import org.sablecc.objectmacro.intermediate.syntax3.node.AStringMacroPart;
import org.sablecc.objectmacro.intermediate.syntax3.node.AStringTextPart;
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

    private final File packageDirectory;

    private MFile currentFile;

    private MTextH currentTextH;

    private MTextC currentTextC;

    private MMacroH currentMacroH;

    private MMacroC currentMacroC;

    private MTextInsert currentTextInsert;

    private MTextInsertPart currentTextInsertPart;

    private MNone currentNone;

    private MSeparator currentSeparator;

    private MBeforeFirst currentBeforeFirst;

    private MAfterLast currentAfterLast;

    private MBeforeOne currentBeforeOne;

    private MAfterOne currentAfterOne;

    private MBeforeMany currentBeforeMany;

    private MAfterMany currentAfterMany;

    private MInlineText currentInlineText;

    private List<String> listImport;

    public CodeGenerationWalker(
            IntermediateRepresentation ir,
            File packageDirectory) {

        this.packageDirectory = packageDirectory;

        MListH mListH = new MListH();
        MListC mListC = new MListC();

        File destination = new File(this.packageDirectory, "MList.h");
        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(mListH.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        mListH = null;

        destination = new File(this.packageDirectory, "MList.c");
        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(mListC.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        mListC = null;

        MAbstractMacroH mAbstractMacroH = new MAbstractMacroH();

        destination = new File(this.packageDirectory, "MAbstractMacro.h");
        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(mAbstractMacroH.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        mAbstractMacroH = null;
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

        this.listImport = new LinkedList<String>();

        this.currentFile = new MFile(string(node.getName()));

        this.currentTextH = this.currentFile.newTextH();
        this.currentTextC = this.currentFile.newTextC();

        for (TString param : node.getParams()) {
            this.currentTextH.newParamField(string(param));
            this.currentTextH.newParamParam(string(param));
            this.currentTextH.newParamPrototype(string(param));

            this.currentTextC.newParamParam(string(param));
            this.currentTextC.newParamConstructorInit(string(param));
            this.currentTextC.newParam(string(param));
        }

        if (bool(node.getSelfRef())) {
            this.currentTextC.newSelfRefC();
            this.currentTextH.newSelfRefH();
        }

        for (TString ancersorRef : node.getAncestorRefs()) {
            this.currentTextH.newAncestorField(string(ancersorRef));
            this.currentTextH.newAncestorParam(string(ancersorRef));

            this.currentTextC.newAncestorParam(string(ancersorRef));
            this.currentTextC.newAncestorConstructorInit(string(ancersorRef));

            if (!this.listImport.contains(string(ancersorRef))) {
                this.currentMacroH.newInclude(string(ancersorRef));
                this.listImport.add(string(ancersorRef));
            }
        }
    }

    @Override
    public void outAText(
            AText node) {

        File destination = new File(this.packageDirectory, "M"
                + string(node.getName()) + ".h");

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(this.currentTextH.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        this.currentTextH = null;
        this.listImport = null;

        destination = new File(this.packageDirectory, "M"
                + string(node.getName()) + ".c");

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(this.currentTextC.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        this.currentTextC = null;
        this.listImport = null;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.listImport = new LinkedList<String>();

        this.currentFile = new MFile(string(node.getName()));

        this.currentMacroH = this.currentFile.newMacroH();
        this.currentMacroC = this.currentFile.newMacroC();

        for (TString param : node.getParams()) {
            this.currentMacroH.newParamField(string(param));
            this.currentMacroH.newParamPrototype(string(param));
            this.currentMacroH.newParamParam(string(param));

            this.currentMacroC.newParamParam(string(param));
            this.currentMacroC.newParamConstructorInit(string(param));
            this.currentMacroC.newParam(string(param));
        }

        if (bool(node.getSelfRef())) {
            this.currentMacroC.newSelfRefC();
            this.currentMacroH.newSelfRefH();
        }

        for (TString ancersorRef : node.getAncestorRefs()) {
            this.currentMacroH.newAncestorParam(string(ancersorRef));
            this.currentMacroH.newAncestorField(string(ancersorRef));

            this.currentMacroC.newAncestorParam(string(ancersorRef));
            this.currentMacroC.newAncestorConstructorInit(string(ancersorRef));

            if (!this.listImport.contains(string(ancersorRef))) {
                this.currentMacroH.newInclude(string(ancersorRef));
                this.listImport.add(string(ancersorRef));
            }
        }

        for (TString expand : node.getExpands()) {
            this.currentMacroH.newExpandField(string(expand));

            this.currentMacroC.newExpandConstructorInit(string(expand));
            this.currentMacroC.newExpandDestructor(string(expand));
        }

    }

    @Override
    public void outAMacro(
            AMacro node) {

        File destination = new File(this.packageDirectory, "M"
                + string(node.getName()) + ".h");

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(this.currentMacroH.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        this.currentMacroH = null;
        this.listImport = null;

        destination = new File(this.packageDirectory, "M"
                + string(node.getName()) + ".c");

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(this.currentMacroC.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }

        this.currentMacroC = null;
        this.listImport = null;
    }

    @Override
    public void outAParamRef(
            AParamRef node) {

        if (this.currentTextH != null || this.currentMacroH != null) {
            if (this.currentTextH != null) {
                this.currentTextH.newParamRefPrototype(string(node.getName()));
            }
            if (this.currentMacroH != null) {
                this.currentMacroH.newParamRefPrototype(string(node.getName()));
            }
        }
        else {
            throw new InternalException("unhandled case");
        }

        if (this.currentTextC != null || this.currentMacroC != null) {
            if (this.currentTextC != null) {
                this.currentTextC.newParamRef(string(node.getName()),
                        string(node.getContext()));
            }
            if (this.currentMacroC != null) {
                this.currentMacroC.newParamRef(string(node.getName()),
                        string(node.getContext()));
            }
        }
        else {
            throw new InternalException("unhandled case");
        }
    }

    @Override
    public void outAExpandedMacro(
            AExpandedMacro node) {

        if (!this.listImport.contains(string(node.getName()))) {
            this.currentMacroH.newInclude(string(node.getName()));
            this.listImport.add(string(node.getName()));
        }

        MMacroCreatorPrototype macroCreatorPrototype = this.currentMacroH
                .newMacroCreatorPrototype(string(node.getName()));
        MMacroCreator macroCreator = this.currentMacroC
                .newMacroCreator(string(node.getName()));

        for (TString param : node.getParams()) {
            macroCreator.newParamParam(string(param));
            macroCreator.newParamArg(string(param));

            macroCreatorPrototype.newParamParam(string(param));
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

        this.currentTextC.newStringPart(escapedString(node.getString()));
    }

    @Override
    public void outAEolTextPart(
            AEolTextPart node) {

        this.currentTextC.newEolPart();
    }

    @Override
    public void outAParamInsertTextPart(
            AParamInsertTextPart node) {

        this.currentTextC.newParamInsertPart(string(node.getParamInsert()));
    }

    @Override
    public void inATextInsertTextPart(
            ATextInsertTextPart node) {

        this.currentTextInsertPart = this.currentTextC.newTextInsertPart();
    }

    @Override
    public void outATextInsertTextPart(
            ATextInsertTextPart node) {

        this.currentTextInsertPart = null;
    }

    @Override
    public void outAStringMacroPart(
            AStringMacroPart node) {

        this.currentMacroC.newStringPart(escapedString(node.getString()));
    }

    @Override
    public void outAEolMacroPart(
            AEolMacroPart node) {

        this.currentMacroC.newEolPart();
    }

    @Override
    public void outAParamInsertMacroPart(
            AParamInsertMacroPart node) {

        this.currentMacroC.newParamInsertPart(string(node.getParamInsert()));
    }

    @Override
    public void inATextInsertMacroPart(
            ATextInsertMacroPart node) {

        this.currentTextInsertPart = this.currentMacroC.newTextInsertPart();
    }

    @Override
    public void outATextInsertMacroPart(
            ATextInsertMacroPart node) {

        this.currentTextInsertPart = null;

    }

    @Override
    public void caseAExpandInsert(
            AExpandInsert node) {

        MExpandInsertPart currentExpandInsertPart = this.currentMacroC
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
            this.currentNone.newTextInsertStr(string(node.getName()));
            this.currentNone.newTextInsertDest(string(node.getName()));
        }
        else if (this.currentSeparator != null) {
            this.currentTextInsert = this.currentSeparator
                    .newTextInsert(string(node.getName()));
            this.currentSeparator.newTextInsertStr(string(node.getName()));
            this.currentSeparator.newTextInsertDest(string(node.getName()));
            this.currentSeparator.newBrace();
            this.currentSeparator.newBraceEnd();
        }
        else if (this.currentBeforeFirst != null) {
            this.currentTextInsert = this.currentBeforeFirst
                    .newTextInsert(string(node.getName()));
            this.currentBeforeFirst.newTextInsertStr(string(node.getName()));
            this.currentBeforeFirst.newTextInsertDest(string(node.getName()));
        }
        else if (this.currentAfterLast != null) {
            this.currentTextInsert = this.currentAfterLast
                    .newTextInsert(string(node.getName()));
            this.currentAfterLast.newTextInsertStr(string(node.getName()));
            this.currentAfterLast.newTextInsertDest(string(node.getName()));
        }
        else if (this.currentBeforeOne != null) {
            this.currentTextInsert = this.currentBeforeOne
                    .newTextInsert(string(node.getName()));
            this.currentBeforeOne.newTextInsertStr(string(node.getName()));
            this.currentBeforeOne.newTextInsertDest(string(node.getName()));
        }
        else if (this.currentAfterOne != null) {
            this.currentTextInsert = this.currentAfterOne
                    .newTextInsert(string(node.getName()));
            this.currentAfterOne.newTextInsertStr(string(node.getName()));
            this.currentAfterOne.newTextInsertDest(string(node.getName()));
        }
        else if (this.currentBeforeMany != null) {
            this.currentTextInsert = this.currentBeforeMany
                    .newTextInsert(string(node.getName()));
            this.currentBeforeMany.newTextInsertStr(string(node.getName()));
            this.currentBeforeMany.newTextInsertDest(string(node.getName()));
        }
        else if (this.currentAfterMany != null) {
            this.currentTextInsert = this.currentAfterMany
                    .newTextInsert(string(node.getName()));
            this.currentAfterMany.newTextInsertStr(string(node.getName()));
            this.currentAfterMany.newTextInsertDest(string(node.getName()));
        }
        else if (this.currentTextInsertPart != null) {
            this.currentTextInsert = this.currentTextInsertPart
                    .newTextInsert(string(node.getName()));
            this.currentTextInsertPart.newTextInsertStr(string(node.getName()));
            this.currentTextInsertPart
                    .newTextInsertDest(string(node.getName()));
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

        if (!this.listImport.contains(string(node.getName()))) {
            this.currentMacroH.newInclude(string(node.getName()));
            this.listImport.add(string(node.getName()));
        }

        this.currentTextInsert = oldTextInsert;
    }

    @Override
    public void inAInlineTextValue(
            AInlineTextValue node) {

        if (this.currentTextInsert != null) {
            this.currentInlineText = this.currentTextInsert.newInlineText();
        }
        else if (this.currentNone != null) {
            this.currentInlineText = this.currentNone.newInlineText();
        }
        else if (this.currentSeparator != null) {
            this.currentInlineText = this.currentSeparator.newInlineText();
        }
        else if (this.currentBeforeFirst != null) {
            this.currentInlineText = this.currentBeforeFirst.newInlineText();
        }
        else if (this.currentAfterLast != null) {
            this.currentInlineText = this.currentAfterLast.newInlineText();
        }
        else if (this.currentBeforeOne != null) {
            this.currentInlineText = this.currentBeforeOne.newInlineText();
        }
        else if (this.currentAfterOne != null) {
            this.currentInlineText = this.currentAfterOne.newInlineText();
        }
        else if (this.currentBeforeMany != null) {
            this.currentInlineText = this.currentBeforeMany.newInlineText();
        }
        else if (this.currentAfterMany != null) {
            this.currentInlineText = this.currentAfterMany.newInlineText();
        }
        else {
            throw new InternalException("unhandled case");
        }
    }

    @Override
    public void outAInlineTextValue(
            AInlineTextValue node) {

        if (this.currentTextInsert != null) {
            this.currentInlineText = null;
        }
        else if (this.currentNone != null) {
            this.currentInlineText = null;
        }
        else if (this.currentSeparator != null) {
            this.currentInlineText = null;
        }
        else if (this.currentBeforeFirst != null) {
            this.currentInlineText = null;
        }
        else if (this.currentAfterLast != null) {
            this.currentInlineText = null;
        }
        else if (this.currentBeforeOne != null) {
            this.currentInlineText = null;
        }
        else if (this.currentAfterOne != null) {
            this.currentInlineText = null;
        }
        else if (this.currentBeforeMany != null) {
            this.currentInlineText = null;
        }
        else if (this.currentAfterMany != null) {
            this.currentInlineText = null;
        }
        else {
            throw new InternalException("unhandled case");
        }
    }

    @Override
    public void outAStringInlineText(
            AStringInlineText node) {

        this.currentInlineText.newInlineString(escapedString(node.getString()));
    }

    @Override
    public void outAEolInlineText(
            AEolInlineText node) {

        this.currentInlineText.newInlineEol();
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
            this.currentBeforeMany
                    .newParamInsert(string(node.getParamInsert()));
        }
        else if (this.currentAfterMany != null) {
            this.currentAfterMany.newParamInsert(string(node.getParamInsert()));
        }
        else {
            throw new InternalException("unhandled case");
        }
    }
}
