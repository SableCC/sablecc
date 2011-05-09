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
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Grammar
        implements INameDeclaration {

    private AGrammar declaration;

    private final NameSpace globalNameSpace = new NameSpace();

    private final TreeNameSpace treeNameSpace = new TreeNameSpace(
            this.globalNameSpace);

    private final Map<String, LexerExpression.StringExpression> stringToStringExpression = new HashMap<String, LexerExpression.StringExpression>();

    private final Map<String, LexerExpression.CharExpression> stringToCharExpression = new HashMap<String, LexerExpression.CharExpression>();

    private final Map<String, LexerExpression.DecExpression> stringToDecExpression = new HashMap<String, LexerExpression.DecExpression>();

    private final Map<String, LexerExpression.HexExpression> stringToHexExpression = new HashMap<String, LexerExpression.HexExpression>();

    private Context.AnonymousContext globalAnonymousContext;

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

    @Override
    public TIdentifier getNameIdentifier() {

        return this.declaration.getName();
    }

    @Override
    public String getName() {

        return getNameIdentifier().getText();
    }

    @Override
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
                    Context.NamedContext namedContext = this.globalNameSpace
                            .getNamedContext(name);
                    if (namedContext != null) {
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
                    selector = new Selector.LexerSelector(node, this.grammar);
                }
                else {
                    selector = new Selector.ParserSelector(node, this.grammar);
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
                    this.globalNameSpace
                            .add(new Investigator.LexerInvestigator(node,
                                    this.grammar));
                }
                else {
                    this.globalNameSpace
                            .add(new Investigator.ParserInvestigator(node,
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
                    }
                    else {
                        Grammar.this.globalAnonymousContext
                                .addDeclaration(node);
                    }
                }
            }
        });
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

    public LexerExpression.StringExpression getStringExpression(
            String text) {

        return this.stringToStringExpression.get(text);
    }

    public LexerExpression.CharExpression getCharExpression(
            String text) {

        return this.stringToCharExpression.get(text);
    }

    public LexerExpression.DecExpression getDecExpression(
            String text) {

        return this.stringToDecExpression.get(text);
    }

    public LexerExpression.HexExpression getHexExpression(
            String text) {

        return this.stringToHexExpression.get(text);
    }

    public LexerExpression.StartExpression getStartExpression() {

        return this.globalStartExpression;
    }

    public LexerExpression.EndExpression getEndExpression() {

        return this.globalEndExpression;
    }

    private static class NameSpace {

        private final Map<String, INameDeclaration> nameMap = new HashMap<String, INameDeclaration>();

        private void internalAdd(
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

        private INameDeclaration getNameDeclaration(
                String name) {

            if (name == null) {
                throw new InternalException("name may not be null");
            }

            return this.nameMap.get(name);
        }

        private void add(
                Grammar grammar) {

            internalAdd(grammar);
        }

        private void add(
                LexerExpression.NamedExpression namedExpression) {

            internalAdd(namedExpression);
        }

        private void add(
                Group group) {

            internalAdd(group);
        }

        private void add(
                Context.NamedContext namedContext) {

            internalAdd(namedContext);
        }

        private void add(
                ParserProduction parserProduction) {

            internalAdd(parserProduction);
        }

        private void add(
                Selector selector) {

            internalAdd(selector);
        }

        private void add(
                Selector.Selection selection) {

            internalAdd(selection);
        }

        private void add(
                Investigator investigator) {

            internalAdd(investigator);
        }

        private Grammar getGrammar(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Grammar) {
                return (Grammar) nameDeclaration;
            }
            return null;
        }

        private LexerExpression.NamedExpression getNamedExpression(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof LexerExpression.NamedExpression) {
                return (LexerExpression.NamedExpression) nameDeclaration;
            }
            return null;
        }

        private Group getGroup(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Group) {
                return (Group) nameDeclaration;
            }
            return null;
        }

        private Context.NamedContext getNamedContext(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Context.NamedContext) {
                return (Context.NamedContext) nameDeclaration;
            }
            return null;
        }

        private ParserProduction getParserProduction(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof ParserProduction) {
                return (ParserProduction) nameDeclaration;
            }
            return null;
        }

        private Selector.LexerSelector getLexerSelector(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Selector.LexerSelector) {
                return (Selector.LexerSelector) nameDeclaration;
            }
            return null;
        }

        private Selector.ParserSelector getParserSelector(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Selector.ParserSelector) {
                return (Selector.ParserSelector) nameDeclaration;
            }
            return null;
        }

        private Selector.LexerSelector.Selection getLexerSelection(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Selector.LexerSelector.Selection) {
                return (Selector.LexerSelector.Selection) nameDeclaration;
            }
            return null;
        }

        private Selector.ParserSelector.Selection getParserSelection(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Selector.ParserSelector.Selection) {
                return (Selector.ParserSelector.Selection) nameDeclaration;
            }
            return null;
        }

        private Investigator.LexerInvestigator getLexerInvestigator(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Investigator.LexerInvestigator) {
                return (Investigator.LexerInvestigator) nameDeclaration;
            }
            return null;
        }

        private Investigator.ParserInvestigator getParserInvestigator(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Investigator.ParserInvestigator) {
                return (Investigator.ParserInvestigator) nameDeclaration;
            }
            return null;
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
            INameDeclaration nameDeclaration = this.globalNameSpace
                    .getNameDeclaration(name);
            if (nameDeclaration == null
                    || nameDeclaration instanceof ParserProduction
                    || nameDeclaration instanceof Selector.ParserSelector.Selection) {
                this.nameMap.put(name, treeProduction);
            }
            else {
                throw SemanticException.duplicateDeclaration(treeProduction,
                        nameDeclaration);
            }
        }

        private INameDeclaration getNameDeclaration(
                String name) {

            if (name == null) {
                throw new InternalException("name may not be null");
            }

            INameDeclaration nameDeclaration = this.nameMap.get(name);
            if (nameDeclaration == null) {
                nameDeclaration = this.globalNameSpace.getNameDeclaration(name);
            }
            return nameDeclaration;
        }

        private TreeProduction getTreeProduction(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof TreeProduction) {
                return (TreeProduction) nameDeclaration;
            }
            return null;
        }
    }
}
