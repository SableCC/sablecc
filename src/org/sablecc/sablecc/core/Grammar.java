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

package org.sablecc.sablecc.core;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.Expression.Lookahead;
import org.sablecc.sablecc.core.Expression.Lookback;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Grammar
        implements NameDeclaration {

    private AGrammar declaration;

    private NameSpace nameSpace;

    private final Map<Node, NameDeclaration> nodeToNameDeclarationMap = new HashMap<Node, NameDeclaration>();

    private final Map<Node, Expression> nodeToExpressionMap = new HashMap<Node, Expression>();

    private final Map<Node, Lookback> nodeToLookbackMap = new HashMap<Node, Lookback>();

    private final Map<Node, Lookahead> nodeToLookaheadMap = new HashMap<Node, Lookahead>();

    Grammar(
            Start ast) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        initializeFrom(ast);
    }

    public TIdentifier getNameIdentifier() {

        return this.declaration.getName();
    }

    public String getName() {

        return getNameIdentifier().getText();
    }

    public String getNameType() {

        return "grammar";
    }

    private void initializeFrom(
            Start ast) {

        this.nameSpace = new NameSpace();

        // the global name space includes all top-level names, excluding AST
        // names.

        ast.apply(new DepthFirstAdapter() {

            private final Grammar grammar = Grammar.this;

            private final NameSpace nameSpace = this.grammar.nameSpace;

            @Override
            public void inAGrammar(
                    AGrammar node) {

                this.grammar.declaration = node;
                this.nameSpace.add(this.grammar);
            }

            @Override
            public void inANormalNamedExpression(
                    ANormalNamedExpression node) {

                this.nameSpace.add(new NamedExpression(node, this.grammar));
            }

            @Override
            public void inASelectionNamedExpression(
                    ASelectionNamedExpression node) {

                LexerSelector lexerSelector = new LexerSelector(node,
                        this.grammar);
                this.nameSpace.add(lexerSelector);

                for (LexerSelector.LexerSelection lexerSelection : lexerSelector
                        .getLexerSelections()) {
                    this.nameSpace.add(lexerSelection);
                }
            }
        });

        throw new InternalException("not implemented");
    }

    void addMapping(
            Node declaration,
            NameDeclaration nameDeclaration) {

        if (this.nodeToNameDeclarationMap.containsKey(declaration)) {
            throw new InternalException("multiple mappings for a single node");
        }

        this.nodeToNameDeclarationMap.put(declaration, nameDeclaration);
    }

    void addMapping(
            Node declaration,
            Expression expression) {

        if (this.nodeToExpressionMap.containsKey(declaration)) {
            throw new InternalException("multiple mappings for a single node");
        }

        this.nodeToExpressionMap.put(declaration, expression);
    }

    void addMapping(
            Node declaration,
            Lookback lookback) {

        if (this.nodeToLookbackMap.containsKey(declaration)) {
            throw new InternalException("multiple mappings for a single node");
        }

        this.nodeToLookbackMap.put(declaration, lookback);
    }

    void addMapping(
            Node declaration,
            Lookahead lookahead) {

        if (this.nodeToLookaheadMap.containsKey(declaration)) {
            throw new InternalException("multiple mappings for a single node");
        }

        this.nodeToLookaheadMap.put(declaration, lookahead);
    }

    public Expression getExpressionMapping(
            Node node) {

        if (!this.nodeToExpressionMap.containsKey(node)) {
            throw new InternalException("missing mapping");
        }

        return this.nodeToExpressionMap.get(node);
    }

    public Lookback getLookbackMapping(
            Node node) {

        if (!this.nodeToLookbackMap.containsKey(node)) {
            throw new InternalException("missing mapping");
        }

        return this.nodeToLookbackMap.get(node);
    }

    public Lookahead getLookaheadMapping(
            Node node) {

        if (!this.nodeToLookaheadMap.containsKey(node)) {
            throw new InternalException("missing mapping");
        }

        return this.nodeToLookaheadMap.get(node);
    }

    private static class NameSpace {

        private Map<String, NameDeclaration> nameMap = new HashMap<String, NameDeclaration>();

        private void add(
                NameDeclaration nameDeclaration) {

            String name = nameDeclaration.getName();
            if (this.nameMap.containsKey(name)) {
                throw SemanticException.duplicateDeclaration(nameDeclaration,
                        this.nameMap.get(name));
            }
            this.nameMap.put(name, nameDeclaration);
        }
    }
}
