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

import java.util.LinkedList;

import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AFile;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroHead;
import org.sablecc.objectmacro.syntax3.node.AMacroTail;
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class SemanticVerifications
        extends DepthFirstAdapter {

    private final LinkedList<MacroParent> parentStack = new LinkedList<MacroParent>();

    private final LinkedList<Macro> currentMacroStack = new LinkedList<Macro>();

    @Override
    public void inAFile(
            AFile node) {

        this.parentStack.addFirst(new File());
    }

    @Override
    public void outAFile(
            AFile node) {

        this.parentStack.removeFirst();
    }

    @Override
    public void inAMacro(
            AMacro node) {

        MacroParent parent = this.parentStack.getFirst();
        parent.addMacro(node);

        Macro macro = new Macro(node, this.currentMacroStack.size() == 0 ? null
                : this.currentMacroStack.get(0));
        this.currentMacroStack.addFirst(macro);
        this.parentStack.addFirst(macro);
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.parentStack.removeFirst();
        this.currentMacroStack.removeFirst();
    }

    @Override
    public void inAMacroHead(
            AMacroHead node) {

        Macro macro = this.currentMacroStack.get(0);
        macro.setName(node.getName());
        for (TIdentifier parameter : node.getParameters()) {
            macro.addParameter(parameter);
        }
    }

    @Override
    public void inAMacroTail(
            AMacroTail node) {

        Macro macro = this.currentMacroStack.get(0);
        if (!node.getName().getText().equals(macro.getName().getText())) {
            throw new SemanticException("macro name \""
                    + node.getName().getText()
                    + "\" doed not match declaration at (line:"
                    + macro.getName().getLine() + ",pos:"
                    + macro.getName().getPos() + ")", node.getName());
        }
    }

    @Override
    public void inAVarMacroBodyPart(
            AVarMacroBodyPart node) {

        Macro macro = this.currentMacroStack.get(0);
        macro.checkVar(node.getVar());
    }
}
