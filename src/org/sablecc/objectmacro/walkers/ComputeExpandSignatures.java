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

import java.util.LinkedHashSet;
import java.util.Set;

import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.structures.ExpandSignature;
import org.sablecc.objectmacro.structures.GlobalData;
import org.sablecc.objectmacro.structures.Macro;
import org.sablecc.objectmacro.structures.Scope;
import org.sablecc.objectmacro.structures.SourceFile;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AExpand;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroReference;
import org.sablecc.objectmacro.syntax3.node.ASourceFile;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;

public class ComputeExpandSignatures
        extends DepthFirstAdapter {

    private GlobalData globalData;

    private Scope currentScope;

    private Set<Macro> macroSet;

    public ComputeExpandSignatures(
            GlobalData globalData) {

        if (globalData == null) {
            throw new InternalException("globalData may not be null");
        }

        this.globalData = globalData;
    }

    @Override
    public void inASourceFile(
            ASourceFile node) {

        this.currentScope = this.globalData.getSourceFile();
    }

    @Override
    public void outASourceFile(
            ASourceFile node) {

        this.currentScope = this.currentScope.getParentScope();
    }

    @Override
    public void inAMacro(
            AMacro node) {

        Macro macro = this.globalData.getMacro(node);
        if (macro.isAutoexpand() && !(this.currentScope instanceof SourceFile)) {
            Set<Macro> macroSet = new LinkedHashSet<Macro>();
            macroSet.add(macro);
            ExpandSignature expandSignature = this.globalData
                    .addExpandSignature(node, macroSet);

            if (!(this.currentScope instanceof Macro)) {
                throw new InternalException("current scope should be a macro");
            }

            ((Macro) this.currentScope).addExpandSignature(expandSignature);
        }

        this.currentScope = macro;
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentScope = this.currentScope.getParentScope();
    }

    @Override
    public void inATextBlock(
            ATextBlock node) {

        this.currentScope = this.globalData.getTextBlock(node);
    }

    @Override
    public void outATextBlock(
            ATextBlock node) {

        this.currentScope = this.currentScope.getParentScope();
    }

    @Override
    public void inAExpand(
            AExpand node) {

        this.macroSet = new LinkedHashSet<Macro>();
    }

    @Override
    public void caseAMacroReference(
            AMacroReference node) {

        Macro macro = this.currentScope.getMacro(node.getName().getText());
        this.macroSet.add(macro);
    }

    @Override
    public void outAExpand(
            AExpand node) {

        ExpandSignature expandSignature = this.globalData.addExpandSignature(
                node, this.macroSet);
        this.macroSet = null;

        if (!(this.currentScope instanceof Macro)) {
            throw new InternalException("current scope should be a macro");
        }

        ((Macro) this.currentScope).addExpandSignature(expandSignature);
    }
}
