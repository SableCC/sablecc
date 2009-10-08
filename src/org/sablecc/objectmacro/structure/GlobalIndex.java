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
import org.sablecc.util.*;

public class GlobalIndex {

    private final Set<Macro> allMacros = new LinkedHashSet<Macro>();

    private final Set<TextBlock> allTextBlocks = new LinkedHashSet<TextBlock>();

    private final SortedMap<String, Macro> macroMap = new TreeMap<String, Macro>();

    private final SortedMap<String, TextBlock> textBlockMap = new TreeMap<String, TextBlock>();

    private final Map<PExpand, Expand> expandMap = new HashMap<PExpand, Expand>();

    private final Map<PTextInsert, TextInsert> textInsertMap = new HashMap<PTextInsert, TextInsert>();

    private final Map<Set<Macro>, ExpandSignature> expandSignatureMap = new HashMap<Set<Macro>, ExpandSignature>();

    public Macro newTopMacro(
            PMacro pDeclaration) {

        if (pDeclaration == null) {
            throw new InternalException("pDeclaration may not be null");
        }

        if (!(pDeclaration.parent() instanceof AMacroSourceFilePart)) {
            throw new InternalException(
                    "pDeclaration must be a top-level macro");
        }

        return newMacro(pDeclaration, null);
    }

    Macro newMacro(
            PMacro pDeclaration,
            Macro parent) {

        if (pDeclaration == null) {
            throw new InternalException("pDeclaration may not be null");
        }

        AMacro declaration = (AMacro) pDeclaration;
        String name = declaration.getName().getText();

        TIdentifier duplicateDeclaration = declaration.getName();

        Macro firstMacro = getMacroOrNull(duplicateDeclaration);
        if (firstMacro != null) {
            TIdentifier firstDeclaration = firstMacro.getNameDeclaration();
            throw CompilerException.duplicateDeclaration(duplicateDeclaration,
                    firstDeclaration);
        }

        TextBlock firstTextBlock = getTextBlockOrNull(duplicateDeclaration);
        if (firstTextBlock != null) {
            TIdentifier firstDeclaration = firstTextBlock.getNameDeclaration();
            throw CompilerException.duplicateDeclaration(duplicateDeclaration,
                    firstDeclaration);
        }

        Macro macro = new Macro(this, declaration, parent);

        this.allMacros.add(macro);
        this.macroMap.put(name, macro);

        return macro;
    }

    private Macro getMacroOrNull(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        String name = identifier.getText();
        Macro macro = this.macroMap.get(name);

        return macro;
    }

    public Macro getTopMacro(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        Macro macro = getMacroOrNull(identifier);

        if (macro == null || macro.getParent() != null) {
            throw CompilerException.unknownMacro(identifier);
        }

        return macro;

    }

    public TextBlock newTopTextBlock(
            PTextBlock pDeclaration) {

        if (pDeclaration == null) {
            throw new InternalException("pDeclaration may not be null");
        }

        if (!(pDeclaration.parent() instanceof ATextBlockSourceFilePart)) {
            throw new InternalException(
                    "pDeclaration must be a top-level macro");
        }

        return newTextBlock(pDeclaration, null);
    }

    TextBlock newTextBlock(
            PTextBlock pDeclaration,
            Scope parent) {

        if (pDeclaration == null) {
            throw new InternalException("pDeclaration may not be null");
        }

        ATextBlock declaration = (ATextBlock) pDeclaration;
        String name = declaration.getName().getText();

        TIdentifier duplicateDeclaration = declaration.getName();

        Macro firstMacro = getMacroOrNull(duplicateDeclaration);
        if (firstMacro != null) {
            TIdentifier firstDeclaration = firstMacro.getNameDeclaration();
            throw CompilerException.duplicateDeclaration(duplicateDeclaration,
                    firstDeclaration);
        }

        TextBlock firstTextBlock = getTextBlockOrNull(duplicateDeclaration);
        if (firstTextBlock != null) {
            TIdentifier firstDeclaration = firstTextBlock.getNameDeclaration();
            throw CompilerException.duplicateDeclaration(duplicateDeclaration,
                    firstDeclaration);
        }

        TextBlock textBlock = new TextBlock(this, declaration, parent);

        this.allTextBlocks.add(textBlock);
        this.textBlockMap.put(name, textBlock);

        return textBlock;
    }

    private TextBlock getTextBlockOrNull(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        String name = identifier.getText();
        TextBlock textBlock = this.textBlockMap.get(name);

        return textBlock;
    }

    public TextBlock getTopTextBlock(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        TextBlock textBlock = getTextBlockOrNull(identifier);

        if (textBlock == null || textBlock.getParent() != null) {
            throw CompilerException.unknownTextBlock(identifier);
        }

        return textBlock;
    }

    Expand getExpand(
            PExpand declaration,
            Macro macro) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        Expand expand = this.expandMap.get(declaration);
        if (expand == null) {
            expand = new Expand(this, (AExpand) declaration, macro);
            this.expandMap.put(declaration, expand);
        }

        return expand;
    }

    TextInsert getTextInsert(
            PTextInsert declaration,
            Scope scope) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        TextInsert textInsert = this.textInsertMap.get(declaration);
        if (textInsert == null) {
            textInsert = new TextInsert((ATextInsert) declaration, scope);
            this.textInsertMap.put(declaration, textInsert);
        }

        return textInsert;
    }

    ExpandSignature getExpandSignature(
            Set<Macro> macroSet) {

        if (macroSet == null) {
            throw new InternalException("macroSet may not be null");
        }

        ExpandSignature expandSignature = this.expandSignatureMap.get(macroSet);
        if (expandSignature == null) {
            expandSignature = new ExpandSignature(macroSet);
            this.expandSignatureMap.put(macroSet, expandSignature);
        }

        return expandSignature;
    }

    public void computeIndirectlyReferencedTextBlocks() {

        Progeny<Scope> referencedTextBlockProgeny = new Progeny<Scope>() {

            public Set<Scope> getChildren(
                    Scope scope) {

                Set<Scope> children = new LinkedHashSet<Scope>();
                children.addAll(scope.getDirectlyReferencedTextBlocks());
                return Collections.unmodifiableSet(children);
            }
        };

        Set<Scope> scopes = new LinkedHashSet<Scope>();
        scopes.addAll(this.allMacros);
        scopes.addAll(this.allTextBlocks);
        scopes = Collections.unmodifiableSet(scopes);

        ComponentFinder<Scope> componentFinder = new ComponentFinder<Scope>(
                scopes, referencedTextBlockProgeny);
        for (Scope scope : scopes) {
            Set<TextBlock> reach = new LinkedHashSet<TextBlock>();
            for (Scope reachedTextBlock : componentFinder
                    .getReach(componentFinder.getRepresentative(scope))) {
                reach.add((TextBlock) reachedTextBlock);
            }

            scope.setIndirectlyReferencedTextBlocks(reach);
        }
    }

    public Set<Macro> getAllMacros() {

        return this.allMacros;
    }

    public Set<TextBlock> getAllTextBlocks() {

        return this.allTextBlocks;
    }
}
