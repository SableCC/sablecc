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

package org.sablecc.sablecc.structure;

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.lrautomaton.*;
import org.sablecc.sablecc.syntax3.node.*;

public class GlobalIndex {

    private Language language;

    private final ContextNameSpace contextNameSpace = new ContextNameSpace(this);

    private final MethodNameSpace methodNameSpace = new MethodNameSpace(this);

    private final ParserNameSpace parserNameSpace = new ParserNameSpace();

    private final TreeNameSpace treeNameSpace = new TreeNameSpace();

    private final Map<TNumber, BigInteger> bigIntegerValues = new LinkedHashMap<TNumber, BigInteger>();

    private final Map<PCharacter, BigInteger> characterValues = new LinkedHashMap<PCharacter, BigInteger>();

    private Set<NormalExpression> normalNamedExpressionLinearization;

    private Grammar grammar;

    public void setLanguage(
            AGrammar node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        if (this.language != null) {
            throw new InternalException("language is already set");
        }

        this.language = new Language(node);
    }

    public Language getLanguage() {

        return this.language;
    }

    public void addContext(
            ALexerContext node) {

        this.contextNameSpace.addContext(node);
    }

    public Context getContext(
            ALexerContext node) {

        return this.contextNameSpace.getContext(node);
    }

    public void addContext(
            AParserContext node) {

        this.contextNameSpace.addContext(node);
    }

    public Context getContext(
            AParserContext node) {

        return this.contextNameSpace.getContext(node);
    }

    public void addMethod(
            ASelectionNamedExpression node) {

        this.methodNameSpace.addMethod(node);
    }

    public Method getMethod(
            ASelectionNamedExpression node) {

        return this.methodNameSpace.getMethod(node);
    }

    public void addMethod(
            ALexerInvestigator node) {

        this.methodNameSpace.addMethod(node);
    }

    public Method getMethod(
            ALexerInvestigator node) {

        return this.methodNameSpace.getMethod(node);
    }

    public void addMethod(
            ASelectionParserProduction node) {

        this.methodNameSpace.addMethod(node);
    }

    public Method getMethod(
            ASelectionParserProduction node) {

        return this.methodNameSpace.getMethod(node);
    }

    public void addMethod(
            AParserInvestigator node) {

        this.methodNameSpace.addMethod(node);
    }

    public Method getMethod(
            AParserInvestigator node) {

        return this.methodNameSpace.getMethod(node);
    }

    public void addExpression(
            ANormalNamedExpression node) {

        this.parserNameSpace.addExpression(node);
        this.treeNameSpace.addExpression(node);
    }

    public NormalExpression getExpression(
            ANormalNamedExpression node) {

        return this.parserNameSpace.getExpression(node);
    }

    void addExpression(
            TIdentifier nameToken,
            LexerSelectorMethod lexerSelectorMethod) {

        this.parserNameSpace.addExpression(nameToken, lexerSelectorMethod);
        this.treeNameSpace.addExpression(nameToken, lexerSelectorMethod);
    }

    Expression getExpression(
            TIdentifier nameToken) {

        return this.parserNameSpace.getExpression(nameToken);
    }

    public void addGroup(
            AGroup node) {

        this.parserNameSpace.addGroup(node);
        this.treeNameSpace.addGroup(node);
    }

    public Group getGroup(
            AGroup node) {

        return this.parserNameSpace.getGroup(node);
    }

    public void addProduction(
            ANormalParserProduction node) {

        this.parserNameSpace.addProduction(node);
    }

    public Production getProduction(
            ANormalParserProduction node) {

        return this.parserNameSpace.getProduction(node);
    }

    void addProduction(
            TIdentifier nameToken,
            ParserSelectorMethod parserSelectorMethod) {

        this.parserNameSpace.addProduction(nameToken, parserSelectorMethod);
    }

    Production getProduction(
            TIdentifier nameToken) {

        return this.parserNameSpace.getProduction(nameToken);
    }

    public void addProduction(
            ATreeProduction node) {

        this.treeNameSpace.addProduction(node);
    }

    public Production getProduction(
            ATreeProduction node) {

        return this.treeNameSpace.getProduction(node);
    }

    public BigInteger getBigIntegerValue(
            TNumber number) {

        BigInteger value = this.bigIntegerValues.get(number);

        if (value == null) {
            value = new BigInteger(number.getText());
            this.bigIntegerValues.put(number, value);
        }

        return value;
    }

    public void setCharacterValue(
            PCharacter node,
            BigInteger value) {

        if (this.characterValues.containsKey(node)) {
            throw new InternalException("value is already set");
        }

        this.characterValues.put(node, value);
    }

    public BigInteger getCharacterValue(
            PCharacter character) {

        BigInteger value = this.characterValues.get(character);

        if (value == null) {
            throw new InternalException("value is not set");
        }

        return value;
    }

    public NameUnit getParserResolution(
            TIdentifier identifier) {

        return this.parserNameSpace.resolve(identifier);
    }

    public Set<NormalExpression> getNormalExpressions() {

        return this.parserNameSpace.getNormalExpressions();
    }

    public void setNormalNamedExpressionLinearization(
            Set<NormalExpression> linearization) {

        this.normalNamedExpressionLinearization = linearization;

    }

    public Set<NormalExpression> getNormalNamedExpressionLinearization() {

        return this.normalNamedExpressionLinearization;
    }

    public Set<Context> getContexts() {

        return this.contextNameSpace.getContexts();
    }

    public void setGrammar(
            Grammar grammar) {

        this.grammar = grammar;
    }

    public Grammar getGrammar() {

        return this.grammar;
    }
}
