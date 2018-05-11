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

package org.sablecc.objectmacro.structure;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.syntax3.node.AMacroReference;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class Param {

    private final GlobalIndex globalIndex;

    private final Macro parent;

    private final Set<AMacroReference> macroReferences = new LinkedHashSet<>();

    private final Map<String, AMacroReference> macroReferencesName = new HashMap<>();

    private final Map<String, Param> paramReferences = new LinkedHashMap<>();

    private boolean isUsed;

    private boolean isString;

    Param(
            Macro macro,
            GlobalIndex globalIndex) {

        if (macro == null) {
            throw new InternalException("scope may not be null");
        }

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.parent = macro;
        this.globalIndex = globalIndex;
    }

    public void addMacroReference(
            AMacroReference macroRef) {

        if (macroRef == null) {
            throw new InternalException("Macro reference cannot be null");
        }

        TIdentifier identifier = macroRef.getName();

        if (this.globalIndex.getMacro(identifier) == null) {
            throw CompilerException.unknownMacro(identifier);
        }

        if (this.macroReferencesName.containsKey(identifier.getText())) {
            throw CompilerException.duplicateMacroRef(macroRef.getName(),
                    getNameDeclaration());
        }

        this.macroReferences.add(macroRef);
        this.macroReferencesName.put(identifier.getText(), macroRef);
    }

    public void addParamReference(
            TIdentifier paramName) {

        if (paramName == null) {
            throw new InternalException("param cannot be null");
        }

        String name = paramName.getText();
        if (name.equals(getName())) {
            throw CompilerException.selfReference(paramName,
                    getNameDeclaration());
        }

        Param newParamRef = this.parent.getParam(paramName);
        if (newParamRef == null) {
            throw new InternalException("parameter may not be null");
        }

        this.paramReferences.put(name, newParamRef);
    }

    public Set<AMacroReference> getMacroReferences() {

        return this.macroReferences;
    }

    public TIdentifier getNameDeclaration() {

        return null;
    }

    public String getName() {

        return null;
    }

    public boolean isUsed() {

        return this.isUsed;
    }

    void setUsed() {

        this.isUsed = true;
    }

    public boolean isString() {

        return this.isString;
    }

    void setString() {

        this.isString = true;
    }

    Set<Param> getDirectParamReferences() {

        Set<Param> directlyParams = new HashSet<>();
        for (Param param : this.paramReferences.values()) {
            directlyParams.add(param);
        }

        return Collections.unmodifiableSet(directlyParams);
    }

    public Macro getParent() {

        return this.parent;
    }
}
