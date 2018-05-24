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

    private final MacroInfo parent;

    private final Set<AMacroReference> macroReferences = new LinkedHashSet<>();

    private final Map<String, AMacroReference> macroReferencesName
            = new HashMap<>();

    private final Map<String, Param> paramReferences = new LinkedHashMap<>();

    private boolean isUsed;

    private boolean isString;

    Param(
            MacroInfo macro_info,
            GlobalIndex global_index) {

        if (macro_info == null) {
            throw new InternalException("scope may not be null");
        }

        if (global_index == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.parent = macro_info;
        this.globalIndex = global_index;
    }

    public void addMacroReference(
            AMacroReference macro_ref) {

        if (macro_ref == null) {
            throw new InternalException("Macro reference cannot be null");
        }

        TIdentifier identifier = macro_ref.getName();

        if(!this.globalIndex.macroExists(identifier)){
            throw CompilerException.unknownMacro(identifier);
        }

        if (this.macroReferencesName.containsKey(identifier.getText())) {
            throw CompilerException.duplicateMacroRef(macro_ref.getName(),
                    getNameDeclaration());
        }

        this.macroReferences.add(macro_ref);
        this.macroReferencesName.put(identifier.getText(), macro_ref);
    }

    public void addParamReference(
            TIdentifier param_name) {

        if (param_name == null) {
            throw new InternalException("param cannot be null");
        }

        String name = param_name.getText();
        if (name.equals(getName())) {
            throw CompilerException.selfReference(param_name,
                    getNameDeclaration());
        }

        Param newParamRef = this.parent.getParam(param_name);
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

    public MacroInfo getParent() {

        return this.parent;
    }
}
