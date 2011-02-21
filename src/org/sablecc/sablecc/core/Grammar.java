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
        implements INameDeclaration {

    private AGrammar declaration;

    private final NameSpace globalNameSpace = new NameSpace();

    private final TreeNameSpace treeNameSpace = new TreeNameSpace(
            this.globalNameSpace);

    private final Map<Node, INameDeclaration> nodeToNameDeclarationMap = new HashMap<Node, INameDeclaration>();

    private final Map<Node, Context.AnonymousContext> nodeToAnonymousContextMap = new HashMap<Node, Context.AnonymousContext>();

    private final Map<Node, Expression> nodeToExpressionMap = new HashMap<Node, Expression>();

    private final Map<Node, Lookback> nodeToLookbackMap = new HashMap<Node, Lookback>();

    private final Map<Node, Lookahead> nodeToLookaheadMap = new HashMap<Node, Lookahead>();

    private Context.AnonymousContext anonymousContext;

    Grammar(
            Start ast) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        fillGlobalNameSpace(ast);
        fillTreeNameSpace(ast);
        fillContexts(ast);

        throw new InternalException("not implemented");
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

        // add all top-level names, excluding tree names

        ast.apply(new DepthFirstAdapter() {

            private final Grammar grammar = Grammar.this;

            private final NameSpace globalNameSpace = this.grammar.globalNameSpace;

            @Override
            public void inAGrammar(
                    AGrammar node) {

                this.grammar.declaration = node;
                this.globalNameSpace.add(this.grammar);
            }

            @Override
            public void inANormalNamedExpression(
                    ANormalNamedExpression node) {

                this.globalNameSpace
                        .add(new LexerExpression(node, this.grammar));
            }

            @Override
            public void inASelectionNamedExpression(
                    ASelectionNamedExpression node) {

                LexerSelector lexerSelector = new LexerSelector(node,
                        this.grammar);
                for (LexerSelector.Selection selection : lexerSelector
                        .getSelections()) {
                    this.globalNameSpace.add(selection);
                }
                this.globalNameSpace.add(lexerSelector);
            }

            @Override
            public void inAGroup(
                    AGroup node) {

                this.globalNameSpace.add(new Group(node, this.grammar));
            }

            @Override
            public void inALexerInvestigator(
                    ALexerInvestigator node) {

                this.globalNameSpace.add(new LexerInvestigator(node,
                        this.grammar));
            }

            @Override
            public void inALexerContext(
                    ALexerContext node) {

                if (node.getName() != null) {
                    this.globalNameSpace.add(new Context.NamedContext(node,
                            this.grammar));
                }
            }

            @Override
            public void inAParserContext(
                    AParserContext node) {

                if (node.getName() != null) {
                    String name = node.getName().getText();
                    INameDeclaration nameDeclaration = this.globalNameSpace
                            .get(name);
                    if (nameDeclaration != null
                            && nameDeclaration instanceof Context.NamedContext) {
                        Context.NamedContext namedContext = (Context.NamedContext) nameDeclaration;
                        namedContext.addDeclaration(node);
                    }
                    else {
                        this.globalNameSpace.add(new Context.NamedContext(node,
                                this.grammar));
                    }
                }
            }

            @Override
            public void inANormalParserProduction(
                    ANormalParserProduction node) {

                this.globalNameSpace.add(new ParserProduction(node,
                        this.grammar));
            }

            @Override
            public void inASelectionParserProduction(
                    ASelectionParserProduction node) {

                ParserSelector parserSelector = new ParserSelector(node,
                        this.grammar);
                for (ParserSelector.Selection selection : parserSelector
                        .getSelections()) {
                    this.globalNameSpace.add(selection);
                }
                this.globalNameSpace.add(parserSelector);
            }

            @Override
            public void inAParserInvestigator(
                    AParserInvestigator node) {

                this.globalNameSpace.add(new ParserInvestigator(node,
                        this.grammar));
            }
        });
    }

    private void fillTreeNameSpace(
            Start ast) {

        ast.apply(new DepthFirstAdapter() {

            private final Grammar grammar = Grammar.this;

            private final TreeNameSpace treeNameSpace = this.grammar.treeNameSpace;

            @Override
            public void inATreeProduction(
                    ATreeProduction node) {

                this.treeNameSpace.add(new TreeProduction(node, this.grammar));
            }
        });
    }

    private void fillContexts(
            Start ast) {

        ast.apply(new DepthFirstAdapter() {

            private final Grammar grammar = Grammar.this;

            private final NameSpace globalNameSpace = this.grammar.globalNameSpace;

            private Context currentContext;

            @Override
            public void inALexerContext(
                    ALexerContext node) {

                if (node.getName() == null) {
                    if (Grammar.this.anonymousContext != null) {
                        throw new InternalException(
                                "anonymousContext should not have been created yet");
                    }
                    Grammar.this.anonymousContext = new Context.AnonymousContext(
                            node, this.grammar);
                    this.currentContext = Grammar.this.anonymousContext;
                }
                else {
                    this.currentContext = (Context) getNameDeclarationMapping(node);

                    if (this.currentContext == null) {
                        throw new InternalException("missing mapping");
                    }
                }
            }

            @Override
            public void outALexerContext(
                    ALexerContext node) {

                this.currentContext = null;
            }

            @Override
            public void inAParserContext(
                    AParserContext node) {

                if (node.getName() == null) {
                    this.currentContext = Grammar.this.anonymousContext;
                    if (this.currentContext == null) {
                        Grammar.this.anonymousContext = new Context.AnonymousContext(
                                node, this.grammar);
                        this.grammar.addMapping(node,
                                Grammar.this.anonymousContext);
                        this.currentContext = Grammar.this.anonymousContext;
                    }
                }
                else {
                    this.currentContext = (Context) getNameDeclarationMapping(node);

                    if (this.currentContext == null) {
                        throw new InternalException("missing mapping");
                    }
                }
            }

            @Override
            public void outAParserContext(
                    AParserContext node) {

                this.currentContext = null;
            }
        });

        throw new InternalException("not implemented");
    }

    void addMapping(
            Node declaration,
            INameDeclaration nameDeclaration) {

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

    public INameDeclaration getNameDeclarationMapping(
            Node node) {

        if (!this.nodeToNameDeclarationMap.containsKey(node)) {
            throw new InternalException("missing mapping");
        }

        return this.nodeToNameDeclarationMap.get(node);
    }

    public Context.AnonymousContext getAnonymousContextMapping(
            Node node) {

        if (!this.nodeToAnonymousContextMap.containsKey(node)) {
            throw new InternalException("missing mapping");
        }

        return this.nodeToAnonymousContextMap.get(node);
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

        private final Map<String, INameDeclaration> nameMap = new HashMap<String, INameDeclaration>();

        private void add(
                INameDeclaration nameDeclaration) {

            if (nameDeclaration == null) {
                throw new InternalException("nameDeclaration may not be null");
            }

            String name = nameDeclaration.getName();
            if (this.nameMap.containsKey(name)) {
                throw SemanticException.duplicateDeclaration(nameDeclaration,
                        this.nameMap.get(name));
            }
            this.nameMap.put(name, nameDeclaration);
        }

        private INameDeclaration get(
                String name) {

            if (name == null) {
                throw new InternalException("name may not be null");
            }

            return this.nameMap.get(name);
        }
    }

    private static class TreeNameSpace {

        private final NameSpace globalNameSpace;

        private final Map<String, TreeProduction> nameMap = new HashMap<String, TreeProduction>();

        private TreeNameSpace(
                NameSpace globalNameSpace) {

            if (globalNameSpace == null) {
                throw new InternalException("globalNameSpace may not be null");
            }

            this.globalNameSpace = globalNameSpace;
        }

        private void add(
                TreeProduction treeProduction) {

            if (treeProduction == null) {
                throw new InternalException("treeProduction may not be null");
            }

            String name = treeProduction.getName();
            INameDeclaration nameDeclaration = this.globalNameSpace.get(name);
            if (nameDeclaration == null
                    || nameDeclaration instanceof ParserProduction
                    || nameDeclaration instanceof ParserSelector.Selection) {
                this.nameMap.put(name, treeProduction);
            }
            else {
                throw SemanticException.duplicateDeclaration(treeProduction,
                        this.nameMap.get(name));
            }
        }

        private INameDeclaration get(
                String name) {

            if (name == null) {
                throw new InternalException("name may not be null");
            }

            INameDeclaration nameDeclaration = this.globalNameSpace.get(name);
            if (nameDeclaration == null
                    || nameDeclaration instanceof ParserProduction
                    || nameDeclaration instanceof ParserSelector.Selection) {
                nameDeclaration = this.nameMap.get(name);
            }

            return nameDeclaration;
        }
    }
}
