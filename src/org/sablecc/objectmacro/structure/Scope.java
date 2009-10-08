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
import org.sablecc.objectmacro.util.*;

public abstract class Scope {

    private final GlobalIndex globalIndex;

    private final Set<Param> params = new LinkedHashSet<Param>();

    private final Map<String, Param> paramMap = new HashMap<String, Param>();

    private final Set<TextBlock> textBlocks = new LinkedHashSet<TextBlock>();

    private final Map<String, TextBlock> textBlockMap = new HashMap<String, TextBlock>();

    private final Set<Scope> referencedAncestors = new LinkedHashSet<Scope>();

    private boolean referencesSelf;

    private final Set<Param> referencedParams = new LinkedHashSet<Param>();

    private final Set<TextBlock> referencedTextBlocks = new LinkedHashSet<TextBlock>();

    private Set<TextBlock> indirectlyReferencedTextBlocks;

    private final Set<Scope> callers = new LinkedHashSet<Scope>();

    Scope(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    public TextBlock newTextBlock(
            ATextBlock pDeclaration) {

        if (pDeclaration == null) {
            throw new InternalException("pDeclaration may not be null");
        }

        if (pDeclaration.parent() instanceof PSourceFilePart) {
            throw new InternalException(
                    "pDeclaration may not be a top-level text block");
        }

        TextBlock textBlock = this.globalIndex.newTextBlock(pDeclaration, this);

        this.textBlockMap.put(textBlock.getName(), textBlock);
        this.textBlocks.add(textBlock);

        return textBlock;
    }

    public TextBlock getTextBlock(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        String name = identifier.getText();

        if (this.textBlockMap.containsKey(name)) {
            return this.textBlockMap.get(name);
        }

        if (getParent() != null) {
            return getParent().getTextBlock(identifier);
        }

        return this.globalIndex.getTopTextBlock(identifier);
    }

    public Param newParam(
            PParam pDeclaration) {

        if (pDeclaration == null) {
            throw new InternalException("pDeclaration may not be null");
        }

        AParam declaration = (AParam) pDeclaration;
        TIdentifier nameId = declaration.getName();
        String name = nameId.getText();

        Param firstParam = getParamOrNull(name);
        if (firstParam != null) {
            throw CompilerException.duplicateDeclaration(nameId, firstParam
                    .getNameDeclaration());
        }

        Param param = new Param(declaration, this);

        this.paramMap.put(name, param);
        this.params.add(param);

        return param;
    }

    public Param getParam(
            TVar var) {

        if (var == null) {
            throw new InternalException("var may not be null");
        }

        Param param = getParamOrNull(Utils.getVarName(var));

        if (param == null) {
            throw CompilerException.unknownParam(var);
        }

        Scope paramScope = param.getScope();
        if (paramScope == this) {
            this.referencesSelf = true;
        }
        else {
            this.referencedAncestors.add(paramScope);
        }

        this.referencedParams.add(param);

        param.setUsed();
        return param;
    }

    private Param getParamOrNull(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        Param param = this.paramMap.get(name);

        if (param == null && getParent() != null) {
            param = getParent().getParamOrNull(name);
        }

        return param;
    }

    public TextInsert getTextInsert(
            PTextInsert declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        TextInsert textInsert = this.globalIndex.getTextInsert(declaration,
                this);

        if (textInsert.getEnclosingScope() != this) {
            throw new InternalError(
                    "getTextInsert must be called on its enclosing scope");
        }

        textInsert.getInsertedTextBlock().addCaller(this);
        this.referencedTextBlocks.add(textInsert.getInsertedTextBlock());

        return textInsert;
    }

    public boolean propagateAncestorReferences() {

        boolean modified = false;

        for (Scope caller : this.callers) {
            for (Scope ancestor : getReferencedAncestors()) {
                if (caller == ancestor) {
                    if (!caller.referencesSelf()) {
                        caller.referencesSelf = true;
                        modified = true;
                    }
                }
                else if (!caller.getReferencedAncestors().contains(ancestor)) {
                    caller.getReferencedAncestors().add(ancestor);
                    modified = true;
                }
            }
        }

        return modified;
    }

    void addCaller(
            Scope scope) {

        if (scope == null) {
            throw new InternalException("scope may not be null");
        }

        this.callers.add(scope);
    }

    public String getName() {

        return getNameDeclaration().getText();
    }

    public String getCamelCaseName() {

        return Utils.toCamelCase(getNameDeclaration());
    }

    public Set<Param> getParams() {

        return this.params;
    }

    public Set<Scope> getReferencedAncestors() {

        return this.referencedAncestors;
    }

    public boolean referencesSelf() {

        return this.referencesSelf;
    }

    public Set<Param> getReferencedParams() {

        return this.referencedParams;
    }

    public Set<TextBlock> getIndirectlyReferencedTextBlocks() {

        return this.indirectlyReferencedTextBlocks;
    }

    GlobalIndex getGlobalIndex() {

        return this.globalIndex;
    }

    Set<TextBlock> getDirectlyReferencedTextBlocks() {

        return this.referencedTextBlocks;
    }

    void setIndirectlyReferencedTextBlocks(
            Set<TextBlock> indirectlyReferencedTextBlocks) {

        if (indirectlyReferencedTextBlocks == null) {
            throw new InternalException(
                    "indirectlyReferencedTextBlocks may not be null");
        }

        if (this.indirectlyReferencedTextBlocks != null) {
            throw new InternalException(
                    "indirectlyReferencedTextBlocks is already set");
        }

        this.indirectlyReferencedTextBlocks = Collections
                .unmodifiableSet(indirectlyReferencedTextBlocks);
    }

    public abstract Scope getParent();

    public abstract TIdentifier getNameDeclaration();

}
