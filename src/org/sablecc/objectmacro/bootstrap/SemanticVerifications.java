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
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class SemanticVerifications
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

        MacroParent parent = this.parentStack.getFirst();
        parent.addMacro(node);

        Macro parentMacro = parent instanceof Macro ? (Macro) parent : null;

        Macro macro = new Macro(node, parentMacro);

        for (TIdentifier parameter : node.getParameters()) {
            macro.addParameter(parameter);
        }

        if (!node.getRepeatName().getText().equals(node.getName().getText())) {
            throw new SemanticException("macro name \""
                    + node.getRepeatName().getText()
                    + "\" does not match declaration at (line:"
                    + node.getName().getLine() + ",pos:"
                    + node.getName().getPos() + ")", node.getRepeatName());
        }

        this.parentStack.addFirst(macro);
        this.currentMacro = macro;
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.parentStack.removeFirst();

        MacroParent parent = this.parentStack.getFirst();
        Macro parentMacro = parent instanceof Macro ? (Macro) parent : null;
        this.currentMacro = parentMacro;
    }

    @Override
    public void inAVarMacroBodyPart(
            AVarMacroBodyPart node) {

        Macro macro = this.currentMacro;
        macro.checkVar(node.getVar());
    }
}
