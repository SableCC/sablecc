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
import org.sablecc.sablecc.core.Investigator.LexerInvestigator;
import org.sablecc.sablecc.core.Investigator.ParserInvestigator;
import org.sablecc.sablecc.core.Selector.LexerSelector;
import org.sablecc.sablecc.core.Selector.ParserSelector;
import org.sablecc.sablecc.core.interfaces.*;
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

    private final Map<Node, LexerExpression.InlineExpression> nodeToInlineExpressionMap = new HashMap<Node, LexerExpression.InlineExpression>();

    private final Map<Node, Expression> nodeToExpressionMap = new HashMap<Node, Expression>();

    private final Map<Node, Lookback> nodeToLookbackMap = new HashMap<Node, Lookback>();

    private final Map<Node, Lookahead> nodeToLookaheadMap = new HashMap<Node, Lookahead>();

    private Context.AnonymousContext globalAnonymousContext;

    private final Map<String, LexerExpression.StringExpression> stringToStringExpression = new HashMap<String, LexerExpression.StringExpression>();

    private final Map<String, LexerExpression.CharExpression> stringToCharExpression = new HashMap<String, LexerExpression.CharExpression>();

    private final Map<String, LexerExpression.DecExpression> stringToDecExpression = new HashMap<String, LexerExpression.DecExpression>();

    private final Map<String, LexerExpression.HexExpression> stringToHexExpression = new HashMap<String, LexerExpression.HexExpression>();

    private LexerExpression.StartExpression globalStartExpression;

    private LexerExpression.EndExpression globalEndExpression;

    Grammar(
            Start ast) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        fillGlobalNameSpace(ast);
        fillTreeNameSpace(ast);
        findInlineExpressions(ast);
        findAnonymousContexts(ast);

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

            private boolean inLexer;

            @Override
            public void inAGrammar(
                    AGrammar node) {

                this.grammar.declaration = node;
                this.globalNameSpace.add(this.grammar);
            }

            @Override
            public void inANamedExpression(
                    ANamedExpression node) {

                this.globalNameSpace.add(new LexerExpression.NamedExpression(
                        node, this.grammar));
            }

            @Override
            public void inAGroup(
                    AGroup node) {

                this.globalNameSpace.add(new Group(node, this.grammar));
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
            public void inAParserProduction(
                    AParserProduction node) {

                this.globalNameSpace.add(ParserProduction.newParserProduction(
                        node, this.grammar));
            }

            @Override
            public void inALexer(
                    ALexer node) {

                this.inLexer = true;
            }

            @Override
            public void outALexer(
                    ALexer node) {

                this.inLexer = false;
            }

            @Override
            public void inASelector(
                    ASelector node) {

                Selector selector;

                if (this.inLexer) {
                    selector = new LexerSelector(node, this.grammar);
                }
                else {
                    selector = new ParserSelector(node, this.grammar);
                }

                for (Selector.Selection selection : selector.getSelections()) {
                    this.globalNameSpace.add(selection);
                }
                this.globalNameSpace.add(selector);
            }

            @Override
            public void inAInvestigator(
                    AInvestigator node) {

                if (this.inLexer) {
                    this.globalNameSpace.add(new LexerInvestigator(node,
                            this.grammar));
                }
                else {
                    this.globalNameSpace.add(new ParserInvestigator(node,
                            this.grammar));
                }
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

    private void findInlineExpressions(
            Start ast) {

        ast.apply(new DepthFirstAdapter() {

            private final Grammar grammar = Grammar.this;

            @Override
            public void caseANamedExpression(
                    ANamedExpression node) {

                // Do not visit subtree
            }

            @Override
            public void inAStringUnit(
                    AStringUnit node) {

                LexerExpression.declareInlineExpression(node, this.grammar);
            }

            @Override
            public void inACharCharacter(
                    ACharCharacter node) {

                LexerExpression.declareInlineExpression(node, this.grammar);
            }

            @Override
            public void inADecCharacter(
                    ADecCharacter node) {

                LexerExpression.declareInlineExpression(node, this.grammar);
            }

            @Override
            public void inAHexCharacter(
                    AHexCharacter node) {

                LexerExpression.declareInlineExpression(node, this.grammar);
            }

            @Override
            public void inAStartUnit(
                    AStartUnit node) {

                LexerExpression.declareInlineExpression(node, this.grammar);
            }

            @Override
            public void inAEndUnit(
                    AEndUnit node) {

                LexerExpression.declareInlineExpression(node, this.grammar);
            }
        });
    }

    private void findAnonymousContexts(
            Start ast) {

        ast.apply(new DepthFirstAdapter() {

            private final Grammar grammar = Grammar.this;

            @Override
            public void inALexerContext(
                    ALexerContext node) {

                if (node.getName() == null) {
                    if (Grammar.this.globalAnonymousContext != null) {
                        throw new InternalException(
                                "globalAnonymousContext should not have been created yet");
                    }
                    Grammar.this.globalAnonymousContext = new Context.AnonymousContext(
                            node, this.grammar);
                }
            }

            @Override
            public void inAParserContext(
                    AParserContext node) {

                if (node.getName() == null) {
                    if (Grammar.this.globalAnonymousContext == null) {
                        Grammar.this.globalAnonymousContext = new Context.AnonymousContext(
                                node, this.grammar);
                        this.grammar.addMapping(node,
                                Grammar.this.globalAnonymousContext);
                    }
                }
            }
        });
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
            LexerExpression.InlineExpression inlineExpression) {

        if (this.nodeToInlineExpressionMap.containsKey(declaration)) {
            throw new InternalException("multiple mappings for a single node");
        }

        this.nodeToInlineExpressionMap.put(declaration, inlineExpression);
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

    public LexerExpression.InlineExpression getInlineExpressionMapping(
            Node node) {

        if (!this.nodeToInlineExpressionMap.containsKey(node)) {
            throw new InternalException("missing mapping");
        }

        return this.nodeToInlineExpressionMap.get(node);
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

    void addStringExpression(
            LexerExpression.StringExpression stringExpression) {

        String text = stringExpression.getText();

        if (this.stringToStringExpression.containsKey(text)) {
            throw new InternalException("multiple mappings for " + text);
        }

        this.stringToStringExpression.put(text, stringExpression);
    }

    void addCharExpression(
            LexerExpression.CharExpression charExpression) {

        String text = charExpression.getText();

        if (this.stringToCharExpression.containsKey(text)) {
            throw new InternalException("multiple mappings for " + text);
        }

        this.stringToCharExpression.put(text, charExpression);
    }

    void addDecExpression(
            LexerExpression.DecExpression decExpression) {

        String text = decExpression.getText();

        if (this.stringToDecExpression.containsKey(text)) {
            throw new InternalException("multiple mappings for " + text);
        }

        this.stringToDecExpression.put(text, decExpression);
    }

    void addHexExpression(
            LexerExpression.HexExpression hexExpression) {

        String text = hexExpression.getText();

        if (this.stringToHexExpression.containsKey(text)) {
            throw new InternalException("multiple mappings for " + text);
        }

        this.stringToHexExpression.put(text, hexExpression);
    }

    void addStartExpression(
            LexerExpression.StartExpression startExpression) {

        if (this.globalStartExpression != null) {
            throw new InternalException("multiple starts");
        }

        this.globalStartExpression = startExpression;
    }

    void addEndExpression(
            LexerExpression.EndExpression endExpression) {

        if (this.globalEndExpression != null) {
            throw new InternalException("multiple ends");
        }

        this.globalEndExpression = endExpression;
    }

    LexerExpression.StringExpression getStringExpression(
            String text) {

        return this.stringToStringExpression.get(text);
    }

    LexerExpression.CharExpression getCharExpression(
            String text) {

        return this.stringToCharExpression.get(text);
    }

    LexerExpression.DecExpression getDecExpression(
            String text) {

        return this.stringToDecExpression.get(text);
    }

    LexerExpression.HexExpression getHexExpression(
            String text) {

        return this.stringToHexExpression.get(text);
    }

    LexerExpression.StartExpression getStartExpression() {

        return this.globalStartExpression;
    }

    LexerExpression.EndExpression getEndExpression() {

        return this.globalEndExpression;
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
