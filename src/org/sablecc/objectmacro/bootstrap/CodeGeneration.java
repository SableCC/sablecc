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

package org.sablecc.objectmacro.bootstrap;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.sablecc.objectmacro.bootstrap.macro.Macro_macro_class;
import org.sablecc.objectmacro.bootstrap.macro.Macro_nested_macro;
import org.sablecc.objectmacro.bootstrap.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.bootstrap.syntax3.node.ADQuoteMacroBodyPart;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AEolMacroBodyPart;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AEscapeMacroBodyPart;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AFile;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AMacro;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AMacroMacroBodyPart;
import org.sablecc.objectmacro.bootstrap.syntax3.node.ATextMacroBodyPart;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.bootstrap.syntax3.node.PMacroBodyPart;
import org.sablecc.objectmacro.bootstrap.syntax3.node.TIdentifier;

public class CodeGeneration
        extends DepthFirstAdapter {

    private final LinkedList<MacroParent> parentStack = new LinkedList<MacroParent>();

    private Macro currentMacro;

    @Override
    public void inAFile(
            AFile node) {

        this.parentStack.addFirst(File.getFile());
    }

    @Override
    public void outAFile(
            AFile node) {

        this.parentStack.removeFirst();
    }

    @Override
    public void inAMacro(
            AMacro node) {

        Macro macro = Macro.getMacro(node);

        this.parentStack.addFirst(macro);
        this.currentMacro = macro;

        Macro_macro_class macro_macro_class = new Macro_macro_class(node
                .getName().getText(), macro.getParentMacro() == null ? "public"
                : "");

        {
            boolean first = true;
            Macro current = macro;

            while (current != null) {
                for (TIdentifier parameter : current.getDeclaration()
                        .getParameters()) {

                    macro_macro_class.newMacro_parameter_declaration(parameter
                            .getText());

                    if (first) {
                        first = false;
                        macro_macro_class
                                .newMacro_constructor_first_parameter(parameter
                                        .getText());
                    }
                    else {
                        macro_macro_class
                                .newMacro_constructor_additional_parameter(parameter
                                        .getText());
                    }

                    macro_macro_class
                            .newMacro_parameter_initialisation(parameter
                                    .getText());
                }

                current = current.getParentMacro();
            }
        }

        for (PMacroBodyPart part : node.getParts()) {
            if (part instanceof AMacroMacroBodyPart) {
                Macro nestedMacro = Macro
                        .getMacro((AMacro) ((AMacroMacroBodyPart) part)
                                .getMacro());

                macro_macro_class.newMacro_nested_macro_declaration(nestedMacro
                        .getDeclaration().getName().getText());

                Macro_nested_macro macro_nested_macro = macro_macro_class
                        .newMacro_nested_macro(nestedMacro.getDeclaration()
                                .getName().getText());

                {
                    boolean first = true;
                    for (TIdentifier parameter : nestedMacro.getDeclaration()
                            .getParameters()) {
                        if (first) {
                            first = false;
                            macro_nested_macro
                                    .newMacro_nested_macro_first_parameter(parameter
                                            .getText());
                        }
                        else {
                            macro_nested_macro
                                    .newMacro_nested_macro_additional_parameter(parameter
                                            .getText());
                        }
                    }
                }

                {
                    boolean first = true;
                    Macro current = nestedMacro;

                    while (current != null) {
                        for (TIdentifier parameter : current.getDeclaration()
                                .getParameters()) {
                            if (first) {
                                first = false;
                                macro_nested_macro
                                        .newMacro_new_first_parameter(parameter
                                                .getText());
                            }
                            else {
                                macro_nested_macro
                                        .newMacro_new_additional_parameter(parameter
                                                .getText());
                            }
                        }

                        current = current.getParentMacro();
                    }
                }
            }
        }

        for (PMacroBodyPart part : node.getParts()) {
            if (part instanceof AVarMacroBodyPart) {
                macro_macro_class.newMacro_instruction().newMacro_var(
                        Utils.getVarName(((AVarMacroBodyPart) part).getVar()));
            }
            else if (part instanceof ATextMacroBodyPart) {
                macro_macro_class.newMacro_instruction().newMacro_text(
                        ((ATextMacroBodyPart) part).getText().getText());
            }
            else if (part instanceof ADQuoteMacroBodyPart) {
                macro_macro_class.newMacro_instruction().newMacro_dquote();
            }
            else if (part instanceof AEolMacroBodyPart) {
                macro_macro_class.newMacro_instruction().newMacro_eol();
            }
            else if (part instanceof AEscapeMacroBodyPart) {
                char c = ((AEscapeMacroBodyPart) part).getEscape().getText()
                        .charAt(1);
                switch (c) {
                case '\\':
                    macro_macro_class.newMacro_instruction().newMacro_escape(
                            "\\\\");
                    break;
                case '$':
                    macro_macro_class.newMacro_instruction().newMacro_escape(
                            "$");
                    break;
                default:
                    throw new InternalException();
                }
            }
            else if (part instanceof AMacroMacroBodyPart) {
                Macro nestedMacro = Macro
                        .getMacro((AMacro) ((AMacroMacroBodyPart) part)
                                .getMacro());

                macro_macro_class.newMacro_instruction().newMacro_macro(
                        nestedMacro.getDeclaration().getName().getText());
            }
            else {
                throw new InternalException();
            }
        }

        try {
            FileWriter fw = new FileWriter("Macro_" + node.getName().getText()
                    + ".java");
            fw.write(macro_macro_class.toString());
            fw.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.parentStack.removeFirst();
        this.currentMacro = this.currentMacro.getParentMacro();
    }

}
