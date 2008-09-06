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

import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.structures.GlobalData;
import org.sablecc.objectmacro.structures.Macro;
import org.sablecc.objectmacro.structures.Scope;
import org.sablecc.objectmacro.structures.SourceFile;
import org.sablecc.objectmacro.structures.TextBlock;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.ASourceFile;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;

public class AddImplicitReferences
        extends DepthFirstAdapter {

    private GlobalData globalData;

    private Scope currentScope;

    public AddImplicitReferences(
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

        TextBlock textBlock = this.globalData.getTextBlock(node);
        if (textBlock.isAutoexpand()
                && !(this.currentScope instanceof SourceFile)) {
            this.currentScope.addReferencedTextBlock(textBlock);
        }

        this.currentScope = textBlock;
    }

    @Override
    public void outATextBlock(
            ATextBlock node) {

        this.currentScope = this.currentScope.getParentScope();
    }
}
