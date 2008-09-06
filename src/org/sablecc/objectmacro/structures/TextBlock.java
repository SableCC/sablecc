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

package org.sablecc.objectmacro.structures;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.exception.SemanticException;
import org.sablecc.objectmacro.macro.M_text_block;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class TextBlock
        extends Scope {

    private final ATextBlock definition;

    private final GlobalData globalData;

    private boolean autoexpand = true;

    // for code generation

    private M_text_block m_text_block;

    private final Set<TextBlock> inserts = new LinkedHashSet<TextBlock>();

    TextBlock(
            ATextBlock definition,
            Scope parentScope,
            GlobalData globalData)
            throws SemanticException {

        super(parentScope, globalData);

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        if (parentScope == null) {
            throw new InternalException("parentScope may not be null");
        }

        if (globalData == null) {
            throw new InternalException("globalData may not be null");
        }

        this.definition = definition;
        this.globalData = globalData;

        if (!definition.getRepeatName().getText().equals(
                definition.getName().getText())) {
            throw new SemanticException("does not match "
                    + definition.getName().getText(), definition
                    .getRepeatName());
        }

        globalData.addTextBlock(this);
    }

    public ATextBlock getDefinition() {

        return this.definition;
    }

    public String getName() {

        return this.definition.getName().getText();
    }

    public boolean isTopLevel() {

        return getParentScope() == this.globalData.getSourceFile();
    }

    @Override
    Macro addMacro(
            AMacro definition) {

        throw new InternalException("a text block does not have macros");
    }

    @Override
    TextBlock addTextBlock(
            ATextBlock definition) {

        throw new InternalException("a text block does not have text blocks");
    }

    @Override
    Param addParam(
            AParam definition) {

        throw new InternalException("a text block does not have parameters");
    }

    public void addTextInsert(
            TextBlock textBlock,
            TIdentifier name)
            throws SemanticException {

        if (textBlock == null) {
            throw new InternalException("textBlock may not be null");
        }

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (this.inserts.add(textBlock)) {
            Set<TextBlock> visitedTextBlocks = new HashSet<TextBlock>();
            detectCycles(textBlock, this, visitedTextBlocks, name);
        }
    }

    private void detectCycles(
            TextBlock textBlock,
            TextBlock target,
            Set<TextBlock> visitedTextBlocks,
            TIdentifier name)
            throws SemanticException {

        if (textBlock == target) {
            throw new SemanticException("cyclic text block reference", name);
        }

        if (visitedTextBlocks.contains(textBlock)) {
            return;
        }

        visitedTextBlocks.add(textBlock);

        for (TextBlock insert : textBlock.inserts) {
            detectCycles(insert, target, visitedTextBlocks, name);
        }
    }

    public void unsetAutoexpand() {

        this.autoexpand = false;
    }

    public boolean isAutoexpand() {

        return this.autoexpand;
    }

    public M_text_block getM_text_block() {

        return this.m_text_block;
    }

    public void setM_text_block(
            M_text_block m_text_block) {

        this.m_text_block = m_text_block;
    }
}
