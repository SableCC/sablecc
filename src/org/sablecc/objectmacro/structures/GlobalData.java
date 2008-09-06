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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.exception.SemanticException;
import org.sablecc.objectmacro.syntax3.node.AExpand;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AOption;
import org.sablecc.objectmacro.syntax3.node.ASourceFile;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;
import org.sablecc.objectmacro.syntax3.node.PExpand;
import org.sablecc.objectmacro.syntax3.node.PMacro;
import org.sablecc.objectmacro.syntax3.node.PSourceFile;
import org.sablecc.objectmacro.syntax3.node.PStaticValue;
import org.sablecc.objectmacro.syntax3.node.PTextBlock;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class GlobalData {

    private final SourceFile sourceFile;

    private final Set<String> globalNames = new HashSet<String>();

    private final Map<AMacro, Macro> macroMap = new HashMap<AMacro, Macro>();

    private final Map<ATextBlock, TextBlock> textBlockMap = new HashMap<ATextBlock, TextBlock>();

    private final Map<Set<Macro>, ExpandSignature> macroSet2ExpandSignatureMap = new HashMap<Set<Macro>, ExpandSignature>();

    private final Map<AExpand, ExpandSignature> expandDefinition2ExpandSignatureMap = new HashMap<AExpand, ExpandSignature>();

    private final Map<AMacro, ExpandSignature> macroDefinition2ExpandSignatureMap = new HashMap<AMacro, ExpandSignature>();

    private final Map<AExpand, Map<String, PStaticValue>> optionMap = new HashMap<AExpand, Map<String, PStaticValue>>();

    private int nextSignatureId = 0;

    public GlobalData(
            PSourceFile sourceFile)
            throws SemanticException {

        this.sourceFile = new SourceFile((ASourceFile) sourceFile, this);
    }

    public SourceFile getSourceFile() {

        return this.sourceFile;
    }

    void addGlobalName(
            TIdentifier nameToken)
            throws SemanticException {

        if (nameToken == null) {
            throw new InternalException("nameToken may not be null");
        }

        String name = nameToken.getText();

        if (this.globalNames.contains(name)) {
            throw new SemanticException("duplicate definition of " + name,
                    nameToken);
        }

        this.globalNames.add(name);
    }

    void addMacro(
            Macro macro)
            throws SemanticException {

        if (macro == null) {
            throw new InternalException("macro may not be null");
        }

        this.macroMap.put(macro.getDefinition(), macro);
    }

    public Macro getMacro(
            PMacro definition) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        Macro macro = this.macroMap.get(definition);
        if (macro == null) {
            throw new InternalException("macro should not be null");
        }

        return macro;
    }

    void addTextBlock(
            TextBlock textBlock)
            throws SemanticException {

        if (textBlock == null) {
            throw new InternalException("textBlock may not be null");
        }

        this.textBlockMap.put(textBlock.getDefinition(), textBlock);
    }

    public TextBlock getTextBlock(
            PTextBlock definition) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        TextBlock textBlock = this.textBlockMap.get(definition);
        if (textBlock == null) {
            throw new InternalException("textBlock should not be null");
        }

        return textBlock;
    }

    public ExpandSignature addExpandSignature(
            AExpand definition,
            Set<Macro> macroSet) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        if (macroSet == null) {
            throw new InternalException("macroSet may not be null");
        }

        ExpandSignature expandSignature = this.macroSet2ExpandSignatureMap
                .get(macroSet);

        if (expandSignature == null) {
            expandSignature = new ExpandSignature(macroSet, this);
            this.macroSet2ExpandSignatureMap.put(macroSet, expandSignature);
        }

        this.expandDefinition2ExpandSignatureMap.put(definition,
                expandSignature);

        return expandSignature;
    }

    public ExpandSignature addExpandSignature(
            AMacro definition,
            Set<Macro> macroSet) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        if (macroSet == null) {
            throw new InternalException("macroSet may not be null");
        }

        ExpandSignature expandSignature = this.macroSet2ExpandSignatureMap
                .get(macroSet);

        if (expandSignature == null) {
            expandSignature = new ExpandSignature(macroSet, this);
            this.macroSet2ExpandSignatureMap.put(macroSet, expandSignature);
        }

        this.macroDefinition2ExpandSignatureMap
                .put(definition, expandSignature);

        return expandSignature;
    }

    public void addExpandOption(
            AExpand expand,
            AOption option)
            throws SemanticException {

        Map<String, PStaticValue> valueMap = this.optionMap.get(expand);

        if (valueMap == null) {
            valueMap = new HashMap<String, PStaticValue>();
            this.optionMap.put(expand, valueMap);
        }

        String name = option.getName().getText();

        if (valueMap.containsKey(name)) {
            throw new SemanticException("duplicate option " + name, option
                    .getName());
        }

        if (name.equals("none") || name.equals("separator")
                || name.equals("before_first") || name.endsWith("after_last")) {
            valueMap.put(name, option.getStaticValue());
        }
        else {
            throw new SemanticException("unknown option " + name, option
                    .getName());
        }
    }

    int getNextSignatureId() {

        return this.nextSignatureId++;
    }

    public ExpandSignature getExpandSignature(
            PExpand expand) {

        return this.expandDefinition2ExpandSignatureMap.get(expand);
    }

    public ExpandSignature getExpandSignature(
            PMacro macro) {

        return this.macroDefinition2ExpandSignatureMap.get(macro);
    }
}
