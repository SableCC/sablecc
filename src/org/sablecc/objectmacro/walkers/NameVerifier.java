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

import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.exception.SemanticException;
import org.sablecc.objectmacro.exception.SemanticRuntimeException;
import org.sablecc.objectmacro.structures.GlobalData;
import org.sablecc.objectmacro.structures.Macro;
import org.sablecc.objectmacro.structures.Param;
import org.sablecc.objectmacro.structures.Scope;
import org.sablecc.objectmacro.structures.TextBlock;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AExpand;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroReference;
import org.sablecc.objectmacro.syntax3.node.AOption;
import org.sablecc.objectmacro.syntax3.node.ASourceFile;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;
import org.sablecc.objectmacro.syntax3.node.ATextBlockReference;
import org.sablecc.objectmacro.syntax3.node.TVar;

public class NameVerifier
        extends DepthFirstAdapter {

    private GlobalData globalData;

    private Scope currentScope;

    public NameVerifier(
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

        this.currentScope = this.globalData.getMacro(node);
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
    public void inAOption(
            AOption node) {

        try {
            this.globalData.addExpandOption((AExpand) node.parent(), node);
        }
        catch (SemanticException e) {
            throw new SemanticRuntimeException(e);
        }
    }

    @Override
    public void caseTVar(
            TVar node) {

        String name = getVarName(node);
        Param param = this.currentScope.getParam(name);
        if (param == null) {
            throw new SemanticRuntimeException(new SemanticException(
                    "unknown parameter " + name, node));
        }

        this.currentScope.addReferencedParam(param);
    }

    @Override
    public void caseAMacroReference(
            AMacroReference node) {

        String name = node.getName().getText();
        Macro macro = this.currentScope.getMacro(name);
        if (macro == null) {
            throw new SemanticRuntimeException(new SemanticException(
                    "unknown macro " + name, node.getName()));
        }

        // Unset auto expansion when the referred-to macro is not an ancestor
        // scope
        if (!this.currentScope.hasAncestorScoppe(macro)) {
            macro.unsetAutoexpand();
        }
    }

    @Override
    public void caseATextBlockReference(
            ATextBlockReference node) {

        String name = node.getName().getText();
        TextBlock textBlock = this.currentScope.getTextBlock(name);
        if (textBlock == null) {
            throw new SemanticRuntimeException(new SemanticException(
                    "unknown text block " + name, node.getName()));
        }

        textBlock.unsetUnused();
        this.currentScope.addReferencedTextBlock(textBlock);
    }
}
