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

package org.sablecc.sablecc;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.SemanticException;
import org.sablecc.sablecc.structure.Group;
import org.sablecc.sablecc.structure.Language;
import org.sablecc.sablecc.structure.Selector;
import org.sablecc.sablecc.structure.Token;
import org.sablecc.sablecc.syntax3.node.AGroup;
import org.sablecc.sablecc.syntax3.node.AHelper;
import org.sablecc.sablecc.syntax3.node.AInvestigator;
import org.sablecc.sablecc.syntax3.node.ASelection;
import org.sablecc.sablecc.syntax3.node.ASelectionToken;
import org.sablecc.sablecc.syntax3.node.ASelector;
import org.sablecc.sablecc.syntax3.node.ASimpleToken;
import org.sablecc.sablecc.syntax3.node.Start;
import org.sablecc.sablecc.syntax3.node.TIdentifier;

public class GlobalInformation {

    private final Verbosity verbosity;

    private final Start ast;

    private Language language;

    private SortedMap<String, Declaration> declarationMap = new TreeMap<String, Declaration>();

    private List<HelperDeclaration> helpers = new LinkedList<HelperDeclaration>();

    private List<SimpleTokenDeclaration> simpleTokens = new LinkedList<SimpleTokenDeclaration>();

    private List<SelectionTokenDeclaration> selectionTokens = new LinkedList<SelectionTokenDeclaration>();

    private List<GroupDeclaration> groups = new LinkedList<GroupDeclaration>();

    private SortedSet<String> methodNames = new TreeSet<String>();

    public GlobalInformation(
            Verbosity verbosity,
            Start ast) {

        if (verbosity == null) {
            throw new InternalException("verbosity may not be null");
        }

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        this.verbosity = verbosity;
        this.ast = ast;
    }

    public Verbosity getVerbosity() {

        return this.verbosity;
    }

    public Start getAst() {

        return this.ast;
    }

    public Language getLanguage() {

        if (this.language == null) {
            throw new InternalException("language is not set yet");
        }

        return this.language;
    }

    public void setLanguage(
            Language language) {

        if (language == null) {
            throw new InternalException("language may not be null");
        }

        if (this.language != null) {
            throw new InternalException("language is already set");
        }

        this.language = language;
    }

    public void addHelper(
            AHelper aHelper)
            throws SemanticException {

        if (aHelper == null) {
            throw new InternalException("aHelper may not be null");
        }

        if (this.declarationMap.containsKey(aHelper.getName().getText())) {
            throw new SemanticException("redeclaration of '"
                    + aHelper.getName().getText() + "'", aHelper.getName());
        }

        HelperDeclaration helperDeclaration = new HelperDeclaration(aHelper);

        this.declarationMap.put(aHelper.getName().getText(), helperDeclaration);
        this.helpers.add(helperDeclaration);
    }

    public void addGroup(
            AGroup aGroup)
            throws SemanticException {

        if (aGroup == null) {
            throw new InternalException("aGroup may not be null");
        }

        if (this.declarationMap.containsKey(aGroup.getName().getText())) {
            throw new SemanticException("redeclaration of '"
                    + aGroup.getName().getText() + "'", aGroup.getName());
        }

        GroupDeclaration groupDeclaration = new GroupDeclaration(aGroup);

        this.declarationMap.put(aGroup.getName().getText(), groupDeclaration);
        this.groups.add(groupDeclaration);

        this.language.addGroup(aGroup.getName().getText());
    }

    public void addSimpleToken(
            ASimpleToken aSimpleToken,
            AGroup aGroup)
            throws SemanticException {

        if (aSimpleToken == null) {
            throw new InternalException("aSimpleToken may not be null");
        }

        if (this.declarationMap.containsKey(aSimpleToken.getName().getText())) {
            throw new SemanticException("redeclaration of '"
                    + aSimpleToken.getName().getText() + "'", aSimpleToken
                    .getName());
        }

        SimpleTokenDeclaration simpleTokenDeclaration = new SimpleTokenDeclaration(
                aSimpleToken);

        this.declarationMap.put(aSimpleToken.getName().getText(),
                simpleTokenDeclaration);
        this.simpleTokens.add(simpleTokenDeclaration);

        Group group;

        if (aGroup == null) {
            group = this.language.getGroup(null);
        }
        else {
            group = this.language.getGroup(aGroup.getName().getText());
        }

        this.language.addToken(aSimpleToken.getName().getText(), group);
    }

    public void addSelectionToken(
            ASelectionToken aSelectionToken,
            AGroup aGroup)
            throws SemanticException {

        if (aSelectionToken == null) {
            throw new InternalException("aSelectionToken may not be null");
        }

        ASelection aSelection = (ASelection) aSelectionToken.getSelection();
        ASelector aSelector = (ASelector) aSelectionToken.getSelector();

        SelectionTokenDeclaration selectionTokenDeclaration = new SelectionTokenDeclaration(
                aSelectionToken);

        this.selectionTokens.add(selectionTokenDeclaration);

        if (this.methodNames.contains(aSelector.getName().getText())) {
            throw new SemanticException("redeclaration of '"
                    + aSelector.getName().getText() + "'", aSelector.getName());
        }

        this.methodNames.add(aSelector.getName().getText());

        this.language.addSelector(aSelector.getName().getText());
        Selector selector = this.language.getSelector(aSelector.getName()
                .getText());

        Group group;

        if (aGroup == null) {
            group = this.language.getGroup(null);
        }
        else {
            group = this.language.getGroup(aGroup.getName().getText());
        }

        for (TIdentifier tokenName : aSelection.getNames()) {

            if (this.declarationMap.containsKey(tokenName.getText())) {
                throw new SemanticException("redeclaration of '"
                        + tokenName.getText() + "'", tokenName);
            }

            this.declarationMap.put(tokenName.getText(),
                    selectionTokenDeclaration);

            this.language.addToken(tokenName.getText(), group);
            Token token = this.language.getToken(tokenName.getText());

            selector.addToken(token);
        }
    }

    public void addInvestigator(
            AInvestigator aInvestigator)
            throws SemanticException {

        if (aInvestigator == null) {
            throw new InternalException("aInvestigator may not be null");
        }

        if (this.methodNames.contains(aInvestigator.getName().getText())) {
            throw new SemanticException("redeclaration of '"
                    + aInvestigator.getName().getText() + "'", aInvestigator
                    .getName());
        }

        this.methodNames.add(aInvestigator.getName().getText());

        this.language.addInvestigator(aInvestigator.getName().getText());
    }
}
