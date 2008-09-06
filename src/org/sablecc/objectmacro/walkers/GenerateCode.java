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

package org.sablecc.objectmacro.walkers;

import static org.sablecc.objectmacro.util.Utils.getVarName;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;

import org.sablecc.objectmacro.exception.ExitException;
import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.macro.M_macro;
import org.sablecc.objectmacro.macro.M_printable;
import org.sablecc.objectmacro.macro.M_root_macro;
import org.sablecc.objectmacro.macro.M_text_block;
import org.sablecc.objectmacro.macro.M_macro.M_expand_append;
import org.sablecc.objectmacro.macro.M_macro.M_submacro_creator;
import org.sablecc.objectmacro.structures.ExpandSignature;
import org.sablecc.objectmacro.structures.GlobalData;
import org.sablecc.objectmacro.structures.Macro;
import org.sablecc.objectmacro.structures.Param;
import org.sablecc.objectmacro.structures.Scope;
import org.sablecc.objectmacro.structures.TextBlock;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AEolMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEolTextBlockBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEscapeMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEscapeStringPart;
import org.sablecc.objectmacro.syntax3.node.AEscapeTextBlockBodyPart;
import org.sablecc.objectmacro.syntax3.node.AExpandMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AOption;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.ASourceFile;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;
import org.sablecc.objectmacro.syntax3.node.ATextBlockReference;
import org.sablecc.objectmacro.syntax3.node.ATextBlockReferenceStaticValue;
import org.sablecc.objectmacro.syntax3.node.ATextInsert;
import org.sablecc.objectmacro.syntax3.node.ATextInsertMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.ATextInsertTextBlockBodyPart;
import org.sablecc.objectmacro.syntax3.node.ATextMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.ATextStringPart;
import org.sablecc.objectmacro.syntax3.node.ATextTextBlockBodyPart;
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AVarTextBlockBodyPart;
import org.sablecc.objectmacro.syntax3.node.PParam;

public class GenerateCode
        extends DepthFirstAdapter {

    private final GlobalData globalData;

    private final File destinationDirectory;

    private final String destinationPackage;

    private Scope currentScope;

    private M_expand_append current_M_expand_append;

    private String current_option;

    public GenerateCode(
            GlobalData globalData,
            java.io.File destinationDirectory,
            String destinationPackage) {

        this.globalData = globalData;
        this.destinationDirectory = destinationDirectory;
        this.destinationPackage = destinationPackage;
    }

    @Override
    public void inASourceFile(
            ASourceFile node) {

        this.currentScope = this.globalData.getSourceFile();
    }

    @Override
    public void outASourceFile(
            ASourceFile node) {

        File outFile = new File(this.destinationDirectory, "Macro.java");

        try {
            FileWriter fw = new FileWriter(outFile);
            BufferedWriter bw = new BufferedWriter(fw);

            M_root_macro macro_root_macro = new M_root_macro();

            if (!this.destinationPackage.equals("")) {
                macro_root_macro.newM_package(this.destinationPackage);
            }

            bw.write(macro_root_macro.toString());

            bw.close();
            fw.close();

            outFile = new File(this.destinationDirectory, "Printable.java");

            fw = new FileWriter(outFile);
            bw = new BufferedWriter(fw);

            M_printable m_printable = new M_printable();

            if (!this.destinationPackage.equals("")) {
                m_printable.newM_package(this.destinationPackage);
            }

            bw.write(m_printable.toString());

            bw.close();
            fw.close();
        }
        catch (IOException e) {
            System.err.println("I/O ERROR: failed to write "
                    + outFile.getAbsolutePath());
            System.err.println(e.getMessage());
            throw new ExitException();
        }

        this.currentScope = this.currentScope.getParentScope();
    }

    @Override
    public void inAMacro(
            AMacro node) {

        Macro macro = this.globalData.getMacro(node);

        if (this.currentScope instanceof Macro) {
            Macro parentmacro = (Macro) this.currentScope;

            if (macro.isAutoexpand()) {
                parentmacro.getM_macro().newM_expand_append(
                        this.globalData.getExpandSignature(node).getName());
            }
        }

        macro.setM_macro(new M_macro(macro.getName()));

        if (!this.destinationPackage.equals("")) {
            macro.getM_macro().newM_package(this.destinationPackage);
        }

        if (macro.isAutoexpand() && macro.isTopLevel()) {
            macro.getM_macro().newM_public_top_level_constructor();
        }
        else if (macro.isTopLevel()) {
            macro.getM_macro().newM_package_top_level_constructor();
        }
        else {
            macro.getM_macro().newM_sub_level_constructor();
            macro.getM_macro()
                    .newM_sub_level_constructor_parent_initialisation();
        }

        if (node.getParams().size() > 0) {
            boolean first = macro.isTopLevel();
            for (PParam pParam : node.getParams()) {
                String paramName = ((AParam) pParam).getName().getText();

                macro.getM_macro().newM_param_declaration(paramName);
                macro.getM_macro().newM_constructor_param_initialisation(
                        paramName);
                macro.getM_macro().newM_local_param_accessor(paramName);

                if (first) {
                    first = false;

                    macro.getM_macro().newM_constructor_first_param(paramName);
                }
                else {
                    macro.getM_macro().newM_constructor_additional_param(
                            paramName);
                }
            }
        }

        for (Param referencedParam : macro.getReferencedParams()) {
            macro.getM_macro().newM_param_accessor(referencedParam.getName(),
                    referencedParam.getMacro().getName());
        }

        for (TextBlock referencedTextBlock : macro.getReferencedTextBlocks()) {
            if (referencedTextBlock.getParentScope() instanceof Macro) {
                Macro parentMacro = (Macro) referencedTextBlock
                        .getParentScope();
                macro.getM_macro().newM_sub_level_text_block_accessor(
                        referencedTextBlock.getName(), parentMacro.getName());
            }
            else {
                macro.getM_macro().newM_top_level_text_block_accessor(
                        referencedTextBlock.getName());
            }
        }

        for (ExpandSignature expandSignature : macro.getExpandSignatures()) {
            macro.getM_macro().newM_expand_declaration(
                    expandSignature.getName());
        }

        for (Macro referencedMacro : macro.getReferencedMacros()) {
            M_submacro_creator m_submacro_creator = macro.getM_macro()
                    .newM_submacro_creator(referencedMacro.getName());

            if (!referencedMacro.isTopLevel()) {
                m_submacro_creator.newM_submacro_new_this_param();
            }

            boolean first = true;

            for (PParam pParam : referencedMacro.getDefinition().getParams()) {
                String paramName = ((AParam) pParam).getName().getText();

                if (first && referencedMacro.isTopLevel()) {
                    m_submacro_creator.newM_submacro_new_first_param(paramName);
                }
                else {
                    m_submacro_creator
                            .newM_submacro_new_additional_param(paramName);
                }

                if (first) {
                    first = false;
                    m_submacro_creator.newM_submacro_first_parameter(paramName);
                }
                else {
                    m_submacro_creator
                            .newM_submacro_additional_parameter(paramName);
                }
            }

            for (ExpandSignature expandSignature : macro
                    .getExpandSignaturesOfReferencedMacro(referencedMacro)) {
                m_submacro_creator
                        .newM_add_to_expand(expandSignature.getName());
            }
        }

        this.currentScope = macro;
    }

    @Override
    public void outAMacro(
            AMacro node) {

        Macro macro = (Macro) this.currentScope;

        File outFile = new File(this.destinationDirectory, "M_"
                + macro.getName() + ".java");

        try {
            FileWriter fw = new FileWriter(outFile);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(macro.getM_macro().toString());

            bw.close();
            fw.close();
        }
        catch (IOException e) {
            System.err.println("I/O ERROR: failed to write "
                    + outFile.getAbsolutePath());
            System.err.println(e.getMessage());
            throw new ExitException();
        }

        this.currentScope = this.currentScope.getParentScope();
    }

    @Override
    public void inATextBlock(
            ATextBlock node) {

        TextBlock textBlock = this.globalData.getTextBlock(node);

        if (this.currentScope instanceof Macro) {
            Macro parentmacro = (Macro) this.currentScope;

            if (textBlock.isAutoexpand()) {
                parentmacro.getM_macro().newM_text_insert_append(
                        textBlock.getName());
            }
        }

        if (this.currentScope instanceof Macro) {
            Macro currentMacro = (Macro) this.currentScope;

            currentMacro.getM_macro().newM_text_block_declaration(
                    textBlock.getName());
            currentMacro.getM_macro()
                    .newM_constructor_text_block_initialisation(
                            textBlock.getName());
            currentMacro.getM_macro().newM_local_text_block_accessor(
                    textBlock.getName());
        }

        textBlock.setM_text_block(new M_text_block(textBlock.getName()));

        if (!this.destinationPackage.equals("")) {
            textBlock.getM_text_block().newM_package(this.destinationPackage);
        }

        if (textBlock.isTopLevel()) {
            textBlock.getM_text_block().newM_top_level_constructor();
        }
        else {
            textBlock.getM_text_block().newM_sub_level_constructor();
        }

        for (Param referencedParam : textBlock.getReferencedParams()) {
            textBlock.getM_text_block().newM_param_accessor(
                    referencedParam.getName(),
                    referencedParam.getMacro().getName());
        }

        for (TextBlock referencedTextBlock : textBlock
                .getReferencedTextBlocks()) {
            if (referencedTextBlock.getParentScope() instanceof Macro) {
                Macro parentMacro = (Macro) referencedTextBlock
                        .getParentScope();
                textBlock.getM_text_block().newM_sub_level_text_block_accessor(
                        referencedTextBlock.getName(), parentMacro.getName());
            }
            else {
                textBlock.getM_text_block().newM_top_level_text_block_accessor(
                        referencedTextBlock.getName());
            }
        }

        this.currentScope = textBlock;
    }

    @Override
    public void outATextBlock(
            ATextBlock node) {

        TextBlock textBlock = (TextBlock) this.currentScope;

        File outFile = new File(this.destinationDirectory, "T_"
                + textBlock.getName() + ".java");

        try {
            FileWriter fw = new FileWriter(outFile);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(textBlock.getM_text_block().toString());

            bw.close();
            fw.close();
        }
        catch (IOException e) {
            System.err.println("I/O ERROR: failed to write "
                    + outFile.getAbsolutePath());
            System.err.println(e.getMessage());
            throw new ExitException();
        }

        this.currentScope = this.currentScope.getParentScope();
    }

    @Override
    public void inATextMacroBodyPart(
            ATextMacroBodyPart node) {

        Macro macro = (Macro) this.currentScope;

        String text = node.getText().getText();
        text = text.replaceAll("\"", Matcher.quoteReplacement("\\\""));

        macro.getM_macro().newM_text_append(text);
    }

    @Override
    public void inAEolMacroBodyPart(
            AEolMacroBodyPart node) {

        Macro macro = (Macro) this.currentScope;

        macro.getM_macro().newM_eol_append();
    }

    @Override
    public void inAEscapeMacroBodyPart(
            AEscapeMacroBodyPart node) {

        Macro macro = (Macro) this.currentScope;

        String text;

        switch (node.getEscape().getText().charAt(1)) {
        case '\\':
            text = "\\\\";
            break;
        case '$':
            text = "$";
            break;
        default:
            throw new InternalException("invalid escape");
        }

        macro.getM_macro().newM_escape_append(text);
    }

    @Override
    public void inAVarMacroBodyPart(
            AVarMacroBodyPart node) {

        Macro macro = (Macro) this.currentScope;

        macro.getM_macro().newM_var_append(getVarName(node.getVar()));
    }

    @Override
    public void inAExpandMacroBodyPart(
            AExpandMacroBodyPart node) {

        Macro macro = (Macro) this.currentScope;

        ExpandSignature expandSignature = this.globalData
                .getExpandSignature(node.getExpand());

        this.current_M_expand_append = macro.getM_macro().newM_expand_append(
                expandSignature.getName());
    }

    @Override
    public void outAExpandMacroBodyPart(
            AExpandMacroBodyPart node) {

        this.current_M_expand_append = null;
    }

    @Override
    public void inAOption(
            AOption node) {

        this.current_option = node.getName().getText();
    }

    @Override
    public void outAOption(
            AOption node) {

        this.current_option = null;
    }

    @Override
    public void inATextBlockReferenceStaticValue(
            ATextBlockReferenceStaticValue node) {

        String textBlockName = ((ATextBlockReference) node
                .getTextBlockReference()).getName().getText();

        if (this.current_option.equals("none")) {
            this.current_M_expand_append
                    .newM_expand_append_none_text_block(textBlockName);
        }
        else if (this.current_option.equals("separator")) {
            this.current_M_expand_append
                    .newM_expand_append_separator_text_block(textBlockName);
        }
        else if (this.current_option.equals("before_first")) {
            this.current_M_expand_append
                    .newM_expand_append_before_first_text_block(textBlockName);
        }
        else if (this.current_option.equals("after_last")) {
            this.current_M_expand_append
                    .newM_expand_append_after_last_text_block(textBlockName);
        }
        else {
            throw new InternalException("unknown option");
        }
    }

    @Override
    public void inATextStringPart(
            ATextStringPart node) {

        String text = node.getText().getText();
        text = text.replaceAll("\"", Matcher.quoteReplacement("\\\""));

        if (this.current_option.equals("none")) {
            this.current_M_expand_append
                    .newM_expand_append_none_string_part(text);
        }
        else if (this.current_option.equals("separator")) {
            this.current_M_expand_append
                    .newM_expand_append_separator_string_part(text);
        }
        else if (this.current_option.equals("before_first")) {
            this.current_M_expand_append
                    .newM_expand_append_before_first_string_part(text);
        }
        else if (this.current_option.equals("after_last")) {
            this.current_M_expand_append
                    .newM_expand_append_after_last_string_part(text);
        }
        else {
            throw new InternalException("unknown option");
        }
    }

    @Override
    public void inAEscapeStringPart(
            AEscapeStringPart node) {

        String text;

        switch (node.getEscape().getText().charAt(1)) {
        case '\\':
            text = "\\\\";
            break;
        case '$':
            text = "$";
            break;
        case '"':
            text = "\\\"";
            break;
        default:
            throw new InternalException("invalid escape");
        }

        if (this.current_option.equals("none")) {
            this.current_M_expand_append
                    .newM_expand_append_none_string_part(text);
        }
        else if (this.current_option.equals("separator")) {
            this.current_M_expand_append
                    .newM_expand_append_separator_string_part(text);
        }
        else if (this.current_option.equals("before_first")) {
            this.current_M_expand_append
                    .newM_expand_append_before_first_string_part(text);
        }
        else if (this.current_option.equals("after_last")) {
            this.current_M_expand_append
                    .newM_expand_append_after_last_string_part(text);
        }
        else {
            throw new InternalException("unknown option");
        }
    }

    @Override
    public void inATextInsertMacroBodyPart(
            ATextInsertMacroBodyPart node) {

        Macro macro = (Macro) this.currentScope;

        String textInsertName = ((ATextBlockReference) ((ATextInsert) node
                .getTextInsert()).getTextBlockReference()).getName().getText();
        macro.getM_macro().newM_text_insert_append(textInsertName);
    }

    @Override
    public void inATextTextBlockBodyPart(
            ATextTextBlockBodyPart node) {

        TextBlock textBlock = (TextBlock) this.currentScope;

        String text = node.getText().getText();
        text = text.replaceAll("\"", Matcher.quoteReplacement("\\\""));

        textBlock.getM_text_block().newM_text_append(text);
    }

    @Override
    public void inAEolTextBlockBodyPart(
            AEolTextBlockBodyPart node) {

        TextBlock textBlock = (TextBlock) this.currentScope;

        textBlock.getM_text_block().newM_eol_append();
    }

    @Override
    public void inAEscapeTextBlockBodyPart(
            AEscapeTextBlockBodyPart node) {

        TextBlock textBlock = (TextBlock) this.currentScope;

        String text;

        switch (node.getEscape().getText().charAt(1)) {
        case '\\':
            text = "\\\\";
            break;
        case '$':
            text = "$";
            break;
        default:
            throw new InternalException("invalid escape");
        }

        textBlock.getM_text_block().newM_escape_append(text);
    }

    @Override
    public void inAVarTextBlockBodyPart(
            AVarTextBlockBodyPart node) {

        TextBlock textBlock = (TextBlock) this.currentScope;

        textBlock.getM_text_block().newM_var_append(getVarName(node.getVar()));
    }

    @Override
    public void inATextInsertTextBlockBodyPart(
            ATextInsertTextBlockBodyPart node) {

        TextBlock textBlock = (TextBlock) this.currentScope;

        String textInsertName = ((ATextBlockReference) ((ATextInsert) node
                .getTextInsert()).getTextBlockReference()).getName().getText();
        textBlock.getM_text_block().newM_text_insert_append(textInsertName);
    }
}
