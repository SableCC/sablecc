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
import java.util.Iterator;

import org.sablecc.objectmacro.macro.Macro_append;
import org.sablecc.objectmacro.macro.Macro_constructor;
import org.sablecc.objectmacro.macro.Macro_macro;
import org.sablecc.objectmacro.macro.Macro_macro_parts;
import org.sablecc.objectmacro.macro.Macro_nested_macro;
import org.sablecc.objectmacro.macro.Macro_root_macro;
import org.sablecc.objectmacro.structures.Expand;
import org.sablecc.objectmacro.structures.Macro;
import org.sablecc.objectmacro.structures.Param;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.ADQuoteMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEolMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEscapeMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AExpandMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AFile;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.ATextMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.PMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.PParam;
import org.sablecc.sablecc.exception.ExitException;
import org.sablecc.sablecc.exception.InternalException;

public class GenerateCode
        extends DepthFirstAdapter {

    private final File destinationDirectory;

    private final String destinationPackage;

    private Macro_macro current_macro_macro;

    private Macro_macro_parts current_macro_macro_parts;

    private String current_indent;

    public GenerateCode(
            java.io.File destinationDirectory,
            String destinationPackage) {

        this.destinationDirectory = destinationDirectory;
        this.destinationPackage = destinationPackage;
    }

    @Override
    public void outAFile(
            AFile node) {

        File outFile = new File(this.destinationDirectory, "Macro.java");

        try {
            FileWriter fw = new FileWriter(outFile);
            BufferedWriter bw = new BufferedWriter(fw);

            Macro_root_macro macro_root_macro = new Macro_root_macro();
            if (!this.destinationPackage.equals("")) {
                macro_root_macro
                        .newMacro_root_macro_package(this.destinationPackage);
            }

            bw.write(macro_root_macro.toString());

            bw.close();
            fw.close();
        }
        catch (IOException e) {
            System.err.println("I/O ERROR: failed to write "
                    + outFile.getAbsolutePath());
            System.err.println(e.getMessage());
            throw new ExitException();
        }
    }

    @Override
    public void caseAMacro(
            AMacro node) {

        Macro macro = Macro.getMacro(node);

        if (macro.isTopLevel()) {
            this.current_macro_macro = new Macro_macro();

            if (!this.destinationPackage.equals("")) {
                this.current_macro_macro
                        .newMacro_macro_package(this.destinationPackage);
            }

            this.current_macro_macro_parts = this.current_macro_macro
                    .newMacro_macro_parts();

            this.current_indent = "";
        }

        this.current_macro_macro_parts.newMacro_macro_class_head(macro
                .getName(), this.current_indent);

        for (PParam pParam : node.getParameters()) {
            Param param = Param.getParam(pParam);

            this.current_macro_macro_parts.newMacro_parameter_declaration(param
                    .getName(), this.current_indent);
        }

        for (PMacroBodyPart part : node.getParts()) {
            if (part instanceof AMacroMacroBodyPart) {
                AMacroMacroBodyPart macroPart = (AMacroMacroBodyPart) part;

                Macro subMacro = Macro.getMacro(macroPart.getMacro());

                if (subMacro.isImplicitlyExpanded()) {
                    this.current_macro_macro_parts.newMacro_macro_declaration(
                            subMacro.getName(), this.current_indent);
                }
            }
            else if (part instanceof AExpandMacroBodyPart) {
                AExpandMacroBodyPart expandPart = (AExpandMacroBodyPart) part;

                Expand expand = Expand.getExpand(expandPart.getExpand());

                this.current_macro_macro_parts.newMacro_expand_declaration(
                        expand.getName(), this.current_indent);
            }
        }

        Macro_constructor macro_constructor = this.current_macro_macro_parts
                .newMacro_constructor(macro.getName(), this.current_indent);
        if (macro.isTopLevel()) {
            macro_constructor.newMacro_public();
        }

        {
            boolean first = true;
            for (PParam pParam : node.getParameters()) {
                Param param = Param.getParam(pParam);

                if (first) {
                    first = false;
                    macro_constructor
                            .newMacro_constructor_first_parameter(param
                                    .getName());
                }
                else {
                    macro_constructor
                            .newMacro_constructor_additional_parameter(param
                                    .getName());
                }
                macro_constructor.newMacro_parameter_initialisation(param
                        .getName());
            }
        }

        for (Iterator<Macro> i = macro.getSubMacrosIterator(); i.hasNext();) {
            Macro subMacro = i.next();

            Macro_nested_macro macro_nested_macro = this.current_macro_macro_parts
                    .newMacro_nested_macro(subMacro.getName(),
                            this.current_indent);

            {
                boolean first = true;
                for (PParam pParam : subMacro.getDefinition().getParameters()) {
                    Param param = Param.getParam(pParam);

                    if (first) {
                        first = false;
                        macro_nested_macro
                                .newMacro_nested_macro_first_parameter(param
                                        .getName());
                        macro_nested_macro.newMacro_new_first_parameter(param
                                .getName());
                    }
                    else {
                        macro_nested_macro
                                .newMacro_nested_macro_additional_parameter(param
                                        .getName());
                        macro_nested_macro
                                .newMacro_new_additional_parameter(param
                                        .getName());
                    }
                }
            }

            if (subMacro.isImplicitlyExpanded()) {
                macro_nested_macro.newMacro_add_to_macro();
            }
            else {
                for (Iterator<Expand> j = subMacro
                        .getReferringExpandsIterator(); j.hasNext();) {
                    Expand expand = j.next();

                    if (expand.getMacro() == macro) {
                        macro_nested_macro.newMacro_add_to_expand(expand
                                .getName());
                    }
                }
            }
        }

        Macro_append macro_append = this.current_macro_macro_parts
                .newMacro_append(this.current_indent);
        for (PMacroBodyPart part : node.getParts()) {
            if (part instanceof AVarMacroBodyPart) {
                AVarMacroBodyPart varPart = (AVarMacroBodyPart) part;

                macro_append.newMacro_instruction().newMacro_var_instruction(
                        getVarName(varPart.getVar()));
            }
            else if (part instanceof ATextMacroBodyPart) {
                ATextMacroBodyPart textPart = (ATextMacroBodyPart) part;

                macro_append.newMacro_instruction().newMacro_text_instruction(
                        textPart.getText().getText());
            }
            else if (part instanceof ADQuoteMacroBodyPart) {
                macro_append.newMacro_instruction()
                        .newMacro_dquote_instruction();
            }
            else if (part instanceof AEolMacroBodyPart) {
                macro_append.newMacro_instruction().newMacro_eol_instruction();
            }
            else if (part instanceof AEscapeMacroBodyPart) {
                AEscapeMacroBodyPart escapePart = (AEscapeMacroBodyPart) part;

                char c = escapePart.getEscape().getText().charAt(1);
                switch (c) {
                case '\\':
                    macro_append.newMacro_instruction()
                            .newMacro_escape_instruction("\\\\");
                    break;
                case '$':
                    macro_append.newMacro_instruction()
                            .newMacro_escape_instruction("$");
                    break;
                default:
                    throw new InternalException("escape char");
                }
            }
            else if (part instanceof AMacroMacroBodyPart) {
                AMacroMacroBodyPart macroPart = (AMacroMacroBodyPart) part;

                Macro subMacro = Macro.getMacro(macroPart.getMacro());

                if (subMacro.isImplicitlyExpanded()) {
                    macro_append.newMacro_instruction()
                            .newMacro_macro_instruction(subMacro.getName());
                }
            }
            else if (part instanceof AExpandMacroBodyPart) {
                AExpandMacroBodyPart expandPart = (AExpandMacroBodyPart) part;

                Expand expand = Expand.getExpand(expandPart.getExpand());

                macro_append.newMacro_instruction()
                        .newMacro_expand_instruction(expand.getName());
            }
            else {
                throw new InternalException("unexpected part type");
            }
        }

        String oldIndent = this.current_indent;
        this.current_indent = this.current_indent + "  ";
        this.current_macro_macro_parts = this.current_macro_macro
                .newMacro_macro_parts();

        for (PMacroBodyPart part : node.getParts()) {
            part.apply(this);
        }

        this.current_indent = oldIndent;
        this.current_macro_macro_parts = this.current_macro_macro
                .newMacro_macro_parts();

        this.current_macro_macro_parts
                .newMacro_macro_class_tail(this.current_indent);

        if (macro.isTopLevel()) {
            File outFile = new File(this.destinationDirectory, "M"
                    + macro.getName() + ".java");

            try {
                FileWriter fw = new FileWriter(outFile);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(this.current_macro_macro.toString());

                bw.close();
                fw.close();
            }
            catch (IOException e) {
                System.err.println("I/O ERROR: failed to write "
                        + outFile.getAbsolutePath());
                System.err.println(e.getMessage());
                throw new ExitException();
            }
        }

        this.current_macro_macro_parts = this.current_macro_macro
                .newMacro_macro_parts();
    }
}
