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
import org.sablecc.objectmacro.macro.M_macro;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;
import org.sablecc.objectmacro.syntax3.node.ATextBlockMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.PMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.PParam;

public class Macro
        extends Scope {

    private final AMacro definition;

    private final GlobalData globalData;

    private boolean autoexpand = true;

    private final Map<Macro, Set<ExpandSignature>> macro2ExpandSignatureSetMap = new HashMap<Macro, Set<ExpandSignature>>();

    private final Set<Macro> referencedMacros = new LinkedHashSet<Macro>();

    private final Set<ExpandSignature> expandSignatures = new LinkedHashSet<ExpandSignature>();

    // for code generation

    private M_macro m_macro;

    Macro(
            AMacro definition,
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

        globalData.addMacro(this);

        for (PParam param : definition.getParams()) {
            addParam((AParam) param);
        }

        for (PMacroBodyPart part : definition.getParts()) {
            if (part instanceof AMacroMacroBodyPart) {
                AMacroMacroBodyPart macroPart = (AMacroMacroBodyPart) part;
                addMacro((AMacro) macroPart.getMacro());
            }
            else if (part instanceof ATextBlockMacroBodyPart) {
                ATextBlockMacroBodyPart textBlockPart = (ATextBlockMacroBodyPart) part;
                addTextBlock((ATextBlock) textBlockPart.getTextBlock());
            }
        }

    }

    public AMacro getDefinition() {

        return this.definition;
    }

    public String getName() {

        return this.definition.getName().getText();
    }

    public boolean isTopLevel() {

        return getParentScope() == this.globalData.getSourceFile();
    }

    public void unsetAutoexpand() {

        this.autoexpand = false;
    }

    public boolean isAutoexpand() {

        return this.autoexpand;
    }

    public void addExpandSignature(
            ExpandSignature expandSignature) {

        if (expandSignature == null) {
            throw new InternalException("expandSignature may not be null");
        }

        if (this.expandSignatures.add(expandSignature)) {
            for (Macro macro : expandSignature.getMacroSet()) {
                this.referencedMacros.add(macro);
                Set<ExpandSignature> expandSignatureSet = this.macro2ExpandSignatureSetMap
                        .get(macro);
                if (expandSignatureSet == null) {
                    expandSignatureSet = new LinkedHashSet<ExpandSignature>();
                    this.macro2ExpandSignatureSetMap.put(macro,
                            expandSignatureSet);
                }
                expandSignatureSet.add(expandSignature);
            }
        }
    }

    public M_macro getM_macro() {

        return this.m_macro;
    }

    public void setM_macro(
            M_macro m_macro) {

        this.m_macro = m_macro;
    }

    public Set<ExpandSignature> getExpandSignatures() {

        return this.expandSignatures;
    }

    public Set<Macro> getReferencedMacros() {

        return this.referencedMacros;
    }

    public Set<ExpandSignature> getExpandSignaturesOfReferencedMacro(
            Macro referencedMacro) {

        return this.macro2ExpandSignatureSetMap.get(referencedMacro);
    }
}
