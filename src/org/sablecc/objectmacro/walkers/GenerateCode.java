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

import org.sablecc.objectmacro.macro.M_macro_file;
import org.sablecc.objectmacro.macro.M_root_macro;
import org.sablecc.objectmacro.macro.M_macro_file.M_macro;
import org.sablecc.objectmacro.macro.M_macro_file.M_macro.M_nested_macro;
import org.sablecc.objectmacro.structures.Expand;
import org.sablecc.objectmacro.structures.Macro;
import org.sablecc.objectmacro.structures.Param;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.ADQuoteMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEolMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEscapeMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AExpand;
import org.sablecc.objectmacro.syntax3.node.AFile;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.ATextMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.PParam;
import org.sablecc.sablecc.exception.ExitException;
import org.sablecc.sablecc.exception.InternalException;

public class GenerateCode
        extends DepthFirstAdapter {

    private final File destinationDirectory;

    private final String destinationPackage;

    private M_macro_file current_Mmacro_file;

    private Macro current_macro;

    private String current_indent = "";

    public GenerateCode(
            java.io.File destinationDirectory,
            String destinationPackage) {

        this.destinationDirectory = destinationDirectory;
        this.destinationPackage = destinationPackage;
    }

    @Override
    public void inAFile(
            AFile node) {

        org.sablecc.objectmacro.structures.File file = org.sablecc.objectmacro.structures.File
                .getFile(node);

        file.setMmacro(null);
    }

    @Override
    public void outAFile(
            AFile node) {

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
        }
        catch (IOException e) {
            System.err.println("I/O ERROR: failed to write "
                    + outFile.getAbsolutePath());
            System.err.println(e.getMessage());
            throw new ExitException();
        }
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.current_macro = Macro.getMacro(node);

        if (this.current_macro.isTopLevel()) {
            this.current_Mmacro_file = new M_macro_file();

            if (!this.destinationPackage.equals("")) {
                this.current_Mmacro_file.newM_package(this.destinationPackage);
            }

            this.current_indent = "";

            this.current_macro.setMmacro(this.current_Mmacro_file.newM_macro(
                    this.current_macro.getName(), this.current_indent));
            this.current_macro.getMmacro().newM_public_constructor();
        }
        else {
            this.current_indent += "  ";
            this.current_macro.setMmacro(this.current_macro.getParentScope()
                    .getMmacro().newM_macro(this.current_macro.getName(),
                            this.current_indent));
        }

        M_macro mmacro = this.current_macro.getMmacro();

        for (Iterator<Macro> i = this.current_macro.getSubMacrosIterator(); i
                .hasNext();) {
            Macro subMacro = i.next();

            M_nested_macro mnested_macro = mmacro.newM_nested_macro(subMacro
                    .getName());

            for (PParam pParam : subMacro.getDefinition().getParameters()) {
                Param param = Param.getParam(pParam);

                if (param.isFirst()) {
                    mnested_macro.newM_nested_macro_first_parameter(param
                            .getName());
                    mnested_macro.newM_new_first_parameter(param.getName());
                }
                else {
                    mnested_macro.newM_nested_macro_additional_parameter(param
                            .getName());
                    mnested_macro
                            .newM_new_additional_parameter(param.getName());
                }
            }

            if (subMacro.isImplicitlyExpanded()) {
                mnested_macro.newM_add_to_nested_macro();
            }
            else {
                for (Iterator<Expand> j = subMacro
                        .getReferringExpandsIterator(); j.hasNext();) {
                    Expand expand = j.next();

                    if (expand.getMacro() == this.current_macro) {
                        mnested_macro.newM_add_to_expand(expand.getName());
                    }
                }
            }
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        if (this.current_macro.isTopLevel()) {
            File outFile = new File(this.destinationDirectory, "M_"
                    + this.current_macro.getName() + ".java");

            try {
                FileWriter fw = new FileWriter(outFile);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(this.current_Mmacro_file.toString());

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
        else {
            this.current_indent = this.current_indent.substring(2);
            this.current_macro = (Macro) this.current_macro.getParentScope();
        }

    }

    @Override
    public void outAParam(
            AParam node) {

        Param param = Param.getParam(node);

        M_macro mmacro = this.current_macro.getMmacro();

        if (param.isFirst()) {
            mmacro.newM_constructor_first_parameter(param.getName());
        }
        else {
            mmacro.newM_constructor_additional_parameter(param.getName());
        }

        mmacro.newM_parameter_declaration(param.getName());
        mmacro.newM_constructor_initialisation(param.getName());
    }

    @Override
    public void outAVarMacroBodyPart(
            AVarMacroBodyPart node) {

        M_macro mmacro = this.current_macro.getMmacro();

        mmacro.newM_var_append(getVarName(node.getVar()));
    }

    @Override
    public void outATextMacroBodyPart(
            ATextMacroBodyPart node) {

        M_macro mmacro = this.current_macro.getMmacro();

        mmacro.newM_text_append(node.getText().getText());
    }

    @Override
    public void outADQuoteMacroBodyPart(
            ADQuoteMacroBodyPart node) {

        M_macro mmacro = this.current_macro.getMmacro();

        mmacro.newM_dquote_append();
    }

    @Override
    public void outAEolMacroBodyPart(
            AEolMacroBodyPart node) {

        M_macro mmacro = this.current_macro.getMmacro();

        mmacro.newM_eol_append();
    }

    @Override
    public void outAEscapeMacroBodyPart(
            AEscapeMacroBodyPart node) {

        M_macro mmacro = this.current_macro.getMmacro();

        char c = node.getEscape().getText().charAt(1);
        switch (c) {
        case '\\':
            mmacro.newM_escape_append("\\\\");
            break;
        case '$':
            mmacro.newM_escape_append("$");
            break;
        default:
            throw new InternalException("unknown escape char");
        }
    }

    @Override
    public void outAMacroMacroBodyPart(
            AMacroMacroBodyPart node) {

        M_macro mmacro = this.current_macro.getMmacro();

        Macro subMacro = Macro.getMacro(node.getMacro());

        if (subMacro.isImplicitlyExpanded()) {
            mmacro.newM_nested_macro_declaration(subMacro.getName());
            mmacro.newM_nested_macro_append(subMacro.getName());
        }
    }

    @Override
    public void outAExpand(
            AExpand node) {

        M_macro mmacro = this.current_macro.getMmacro();

        Expand expand = Expand.getExpand(node);

        mmacro.newM_expand_declaration(expand.getName());
        mmacro.newM_expand_append(expand.getName());
    }

}
