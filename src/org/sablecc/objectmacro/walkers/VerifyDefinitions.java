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

import org.sablecc.objectmacro.exception.SemanticException;
import org.sablecc.objectmacro.exception.SemanticRuntimeException;
import org.sablecc.objectmacro.structures.Expand;
import org.sablecc.objectmacro.structures.File;
import org.sablecc.objectmacro.structures.Macro;
import org.sablecc.objectmacro.structures.Param;
import org.sablecc.objectmacro.structures.Scope;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AExpand;
import org.sablecc.objectmacro.syntax3.node.AFile;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.PMacro;
import org.sablecc.objectmacro.syntax3.node.PMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class VerifyDefinitions
        extends DepthFirstAdapter {

    private Scope currentScope;

    @Override
    public void inAFile(
            AFile node) {

        File file = File.getFile(node);

        for (PMacro pMacro : node.getMacros()) {
            Macro macro = Macro.getMacro(pMacro);
            try {
                file.addMacro(macro);
            }
            catch (SemanticException e) {
                throw new SemanticRuntimeException(e);
            }
            macro.setTopLevel();
        }

        this.currentScope = file;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        Macro macro = Macro.getMacro(node);

        if (!node.getRepeatName().getText().equals(macro.getName())) {
            throw new SemanticRuntimeException(new SemanticException(
                    "end does not match macro " + macro.getName(), node
                            .getRepeatName()));
        }

        for (PMacroBodyPart part : node.getParts()) {
            if (part instanceof AMacroMacroBodyPart) {
                AMacroMacroBodyPart macroPart = (AMacroMacroBodyPart) part;

                Macro subMacro = Macro.getMacro(macroPart.getMacro());
                try {
                    macro.addMacro(subMacro);
                }
                catch (SemanticException e) {
                    throw new SemanticRuntimeException(e);
                }
            }
        }

        this.currentScope = macro;
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentScope = Macro.getMacro(node).getParentScope();
    }

    @Override
    public void inAParam(
            AParam node) {

        Param param = Param.getParam(node);

        try {
            this.currentScope.addParam(param);
        }
        catch (SemanticException e) {
            throw new SemanticRuntimeException(e);
        }
    }

    @Override
    public void inAExpand(
            AExpand node) {

        Expand expand = Expand.getExpand(node);

        for (TIdentifier id : node.getMacroNames()) {

            String macroName = id.getText();

            Macro macro = this.currentScope.getMacro(macroName);

            if (macro == null) {
                throw new SemanticRuntimeException(new SemanticException(
                        "unknown macro " + macroName, id));
            }

            try {
                expand.addMacroReference(id);
            }
            catch (SemanticException e) {
                throw new SemanticRuntimeException(e);
            }

            macro.addReferringExpand(expand);
        }
    }

    @Override
    public void inAVarMacroBodyPart(
            AVarMacroBodyPart node) {

        String varName = getVarName(node.getVar());

        if (this.currentScope.getParam(varName) == null) {
            throw new SemanticRuntimeException(new SemanticException(
                    "unknown variable " + varName, node.getVar()));
        }
    }

}
