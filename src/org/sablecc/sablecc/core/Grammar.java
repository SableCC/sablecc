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

    private final NameSpace nameSpace = new NameSpace();

    private final Map<Node, NameDeclaration> nodeToNameDeclarationMap = new HashMap<Node, NameDeclaration>();

    private final Map<Node, Context.AnonymousContext> nodeToAnonymousContextMap = new HashMap<Node, Context.AnonymousContext>();

    private final Map<Node, Expression> nodeToExpressionMap = new HashMap<Node, Expression>();

    private final Map<Node, Lookback> nodeToLookbackMap = new HashMap<Node, Lookback>();

    private final Map<Node, Lookahead> nodeToLookaheadMap = new HashMap<Node, Lookahead>();

    Grammar(
            Start ast) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        fillGlobalNameSpace(ast);
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

    private void fillGlobalNameSpace(
            Start ast) {

        // add all top-level names, excluding AST names

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

                this.nameSpace.add(new NormalExpression(node, this.grammar));
            }

            @Override
            public void inASelectionNamedExpression(
                    ASelectionNamedExpression node) {

                LexerSelector lexerSelector = new LexerSelector(node,
                        this.grammar);
                for (LexerSelector.Selection selection : lexerSelector
                        .getSelections()) {
                    this.nameSpace.add(selection);
                }
                this.nameSpace.add(lexerSelector);
            }

            @Override
            public void inAGroup(
                    AGroup node) {

                this.nameSpace.add(new Group(node, this.grammar));
            }

            @Override
            public void inALexerInvestigator(
                    ALexerInvestigator node) {

                this.nameSpace.add(new LexerInvestigator(node, this.grammar));
            }

            @Override
            public void inALexerContext(
                    ALexerContext node) {

                if (node.getName() != null) {
                    this.nameSpace.add(new Context.NamedContext(node,
                            this.grammar));
                }
            }

            @Override
            public void inAParserContext(
                    AParserContext node) {

                if (node.getName() != null) {
                    String name = node.getName().getText();
                    NameDeclaration nameDeclaration = this.nameSpace.get(name);
                    if (nameDeclaration != null
                            && nameDeclaration instanceof Context.NamedContext) {
                        Context.NamedContext namedContext = (Context.NamedContext) nameDeclaration;
                        namedContext.addDeclaration(node);
                    }
                    else {
                        this.nameSpace.add(new Context.NamedContext(node,
                                this.grammar));
                    }
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
            Context.AnonymousContext anonymousContext) {

        if (this.nodeToAnonymousContextMap.containsKey(declaration)) {
            throw new InternalException("multiple mappings for a single node");
        }

        this.nodeToAnonymousContextMap.put(declaration, anonymousContext);
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

        private NameDeclaration get(
                String name) {

            if (name == null) {
                throw new InternalException("name may not be null");
            }

            return this.nameMap.get(name);
        }
    }
}
