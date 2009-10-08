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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.syntax3.node.*;

public class Macro
        extends Scope {

    private final AMacro declaration;

    private final Macro parent;

    private final Set<Macro> macros = new LinkedHashSet<Macro>();

    private final Map<String, Macro> macroMap = new HashMap<String, Macro>();

    private final Set<Macro> explicitlyExpandedMacros = new LinkedHashSet<Macro>();

    private final Set<Macro> implicitlyExpandedMacros = new LinkedHashSet<Macro>();

    private final Set<ExpandSignature> expandSignatures = new LinkedHashSet<ExpandSignature>();

    private boolean isImplicitlyExpanded;

    private ExpandSignature implicitSignature;

    Macro(
            GlobalIndex globalIndex,
            AMacro declaration,
            Macro parent) {

        super(globalIndex);

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        this.declaration = declaration;
        this.parent = parent;

        if (!declaration.getRepeatName().getText().equals(
                declaration.getName().getText())) {
            throw CompilerException.endMismatch(declaration.getRepeatName(),
                    declaration.getName());
        }
    }

    public Macro newMacro(
            PMacro pDeclaration) {

        if (pDeclaration == null) {
            throw new InternalException("pDeclaration may not be null");
        }

        if (pDeclaration.parent() instanceof AMacroSourceFilePart) {
            throw new InternalException(
                    "pDeclaration may not be a top-level macro");
        }

        Macro macro = getGlobalIndex().newMacro(pDeclaration, this);

        this.macroMap.put(macro.getName(), macro);
        this.macros.add(macro);

        return macro;
    }

    public Macro getMacro(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        String name = identifier.getText();

        if (this.macroMap.containsKey(name)) {
            return this.macroMap.get(name);
        }

        if (this.parent != null) {
            return this.parent.getMacro(identifier);
        }

        return getGlobalIndex().getTopMacro(identifier);
    }

    public Expand getExpand(
            PExpand declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        Expand expand = getGlobalIndex().getExpand(declaration, this);

        if (expand.getEnclosingMacro() != this) {
            throw new InternalError(
                    "getExpand must be called on its enclosing macro");
        }

        ExpandSignature signature = expand.getSignature();
        this.expandSignatures.add(signature);

        for (Macro macro : signature.getMacroSet()) {
            this.explicitlyExpandedMacros.add(macro);
            macro.addCaller(this);
        }

        return expand;
    }

    public void computeImplicitExpansion() {

        this.isImplicitlyExpanded = this.parent != null
                && !this.parent.explicitlyExpandedMacros.contains(this);

        if (this.isImplicitlyExpanded) {
            Set<Macro> macroSet = new LinkedHashSet<Macro>();
            macroSet.add(this);
            this.implicitSignature = getGlobalIndex().getExpandSignature(
                    macroSet);
            this.parent.expandSignatures.add(this.implicitSignature);
            this.parent.implicitlyExpandedMacros.add(this);
            addCaller(this.parent);
        }
    }

    public AMacro getDeclaration() {

        return this.declaration;
    }

    @Override
    public TIdentifier getNameDeclaration() {

        return this.declaration.getName();
    }

    @Override
    public Macro getParent() {

        return this.parent;
    }

    public Set<ExpandSignature> getExpandSignatures() {

        return this.expandSignatures;
    }

    public Set<Macro> getExplicitlyExpandedMacros() {

        return this.explicitlyExpandedMacros;
    }

    public Set<Macro> getImplicitlyExpandedMacros() {

        return this.implicitlyExpandedMacros;
    }

    public boolean isImplicitlyExpanded() {

        return this.isImplicitlyExpanded;
    }

    public ExpandSignature getImplicitSignature() {

        return this.implicitSignature;
    }
}
