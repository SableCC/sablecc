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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.exception.SemanticException;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;

public abstract class Scope {

    private final Scope parentScope;

    private final GlobalData globalData;

    private final Map<String, Macro> localMacroMap = new HashMap<String, Macro>();

    private final Map<String, TextBlock> localTextBlockMap = new HashMap<String, TextBlock>();

    private final Map<String, Param> localParamMap = new HashMap<String, Param>();

    private final Set<TextBlock> referencedTextBlocks = new LinkedHashSet<TextBlock>();

    private final Set<Param> referencedParams = new LinkedHashSet<Param>();

    Scope(
            Scope parentScope,
            GlobalData globalData) {

        if (parentScope == null && !(this instanceof SourceFile)) {

            throw new InternalException("parentScope may not be null");
        }

        if (globalData == null) {
            throw new InternalException("globalData may not be null");
        }

        this.parentScope = parentScope;
        this.globalData = globalData;
    }

    public Macro getMacro(
            String macroName) {

        if (macroName == null) {
            throw new InternalException("macroName may not be null");
        }

        Macro macro = this.localMacroMap.get(macroName);

        if (macro == null) {
            if (this.parentScope != null) {
                return this.parentScope.getMacro(macroName);
            }
        }

        return macro;
    }

    Macro addMacro(
            AMacro definition)
            throws SemanticException {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        this.globalData.addGlobalName(definition.getName());

        Macro macro = new Macro(definition, this, this.globalData);
        this.localMacroMap.put(macro.getName(), macro);
        return macro;
    }

    public TextBlock getTextBlock(
            String textBlockName) {

        if (textBlockName == null) {
            throw new InternalException("textBlockName may not be null");
        }

        TextBlock textBlock = this.localTextBlockMap.get(textBlockName);

        if (textBlock == null) {
            if (this.parentScope != null) {
                return this.parentScope.getTextBlock(textBlockName);
            }
        }

        return textBlock;
    }

    TextBlock addTextBlock(
            ATextBlock definition)
            throws SemanticException {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        this.globalData.addGlobalName(definition.getName());

        TextBlock textBlock = new TextBlock(definition, this, this.globalData);
        this.localTextBlockMap.put(textBlock.getName(), textBlock);
        return textBlock;
    }

    public Param getParam(
            String paramName) {

        if (paramName == null) {
            throw new InternalException("paramName may not be null");
        }

        Param param = this.localParamMap.get(paramName);

        if (param == null) {
            if (this.parentScope != null) {
                return this.parentScope.getParam(paramName);
            }
        }

        return param;
    }

    Param addParam(
            AParam definition)
            throws SemanticException {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        String paramName = definition.getName().getText();

        if (getParam(paramName) != null) {
            throw new SemanticException("duplicate definition of " + paramName,
                    definition.getName());
        }

        Param param = new Param(definition, this.globalData);
        this.localParamMap.put(param.getName(), param);
        return param;
    }

    public Scope getParentScope() {

        return this.parentScope;
    }

    public void addReferencedTextBlock(
            TextBlock textBlock) {

        this.referencedTextBlocks.add(textBlock);
    }

    public Set<TextBlock> getReferencedTextBlocks() {

        return this.referencedTextBlocks;
    }

    public void addReferencedParam(
            Param param) {

        this.referencedParams.add(param);
    }

    public Set<Param> getReferencedParams() {

        return this.referencedParams;
    }
}
