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
import org.sablecc.sablecc.core.Context.NamedContext;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.core.walker.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Grammar
        implements INameDeclaration, IVisitableGrammarPart {

    private AGrammar declaration;

    private final NameSpace globalNameSpace = new NameSpace();

    private final TreeNameSpace treeNameSpace = new TreeNameSpace(
            this.globalNameSpace);

    private final Map<String, LexerExpression.StringExpression> stringToStringExpression = new HashMap<String, LexerExpression.StringExpression>();

    private final Map<String, LexerExpression.CharExpression> stringToCharExpression = new HashMap<String, LexerExpression.CharExpression>();

    private final Map<String, LexerExpression.DecExpression> stringToDecExpression = new HashMap<String, LexerExpression.DecExpression>();

    private final Map<String, LexerExpression.HexExpression> stringToHexExpression = new HashMap<String, LexerExpression.HexExpression>();

    private final Map<String, LexerExpression> stringToLexerExpression = new HashMap<String, LexerExpression>();

    private Context.AnonymousContext globalAnonymousContext;

    private LexerExpression.StartExpression globalStartExpression;

    private LexerExpression.EndExpression globalEndExpression;

    private final List<Context.NamedContext> namedContexts = new LinkedList<Context.NamedContext>();

    private final Lexer lexer = new Lexer();

    private final Parser parser = new Parser();

    private final Transformation transformation = new Transformation();

    private final Tree tree = new Tree();

    Grammar(
            Start ast) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        if (GrammarCompiler.RESTRICTED_SYNTAX) {
            findAnonymousContexts(ast);
            fillGlobalNameSpace(ast);
            findInlineExpressions(ast);
            findLexerPriorities(ast);
            verifyReferences();
        }
        else {
            findAnonymousContexts(ast);
            fillGlobalNameSpace(ast);
            fillTreeNameSpace(ast);
            findInlineExpressions(ast);
            findTransformations(ast);
            findLexerPriorities(ast);

            verifyReferences();
            buildImplicitTransformations();
            resolveUnknowTypes();
            verifyAssignability();

            apply(new GrammarVisitor());

            throw new InternalException("not implemented");
        }
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

    public List<Context.NamedContext> getNamedContexts() {

        return this.namedContexts;
    }

    public Context.AnonymousContext getGlobalAnonymousContext() {

        return this.globalAnonymousContext;
    }

    public Lexer getLexer() {

        return this.lexer;
    }

    public Parser getParser() {

        return this.parser;
    }

    public Transformation getTransformation() {

        return this.transformation;
    }

    public Tree getTree() {

        return this.tree;
    }

    @Override
    public void apply(
            IGrammarVisitor visitor) {

        visitor.visitGrammar(this);

    }

    public INameDeclaration getTreeReference(
            String reference) {

        if (this.treeNameSpace.contains(reference)) {
            return this.treeNameSpace.getNameDeclaration(reference);
        }
        else {
            return this.globalNameSpace.getNameDeclaration(reference);
        }
    }

    public INameDeclaration getGlobalReference(
            String reference) {

        if (this.globalNameSpace.contains(reference)) {
            return this.globalNameSpace.getNameDeclaration(reference);
        }
        else {
            return this.treeNameSpace.getNameDeclaration(reference);
        }
    }

    private void fillGlobalNameSpace(
            Start ast) {

        // add all top-level names, excluding tree names

        ast.apply(new DepthFirstAdapter() {

            private final Grammar grammar = Grammar.this;

            private final NameSpace globalNameSpace = this.grammar.globalNameSpace;

            private boolean inLexer;

            private Context currentContext = Grammar.this.globalAnonymousContext;

            @Override
            public void inAGrammar(
                    AGrammar node) {

                this.grammar.declaration = node;
                this.globalNameSpace.add(this.grammar);
            }

            @Override
            public void inANamedExpression(
                    ANamedExpression node) {

                LexerExpression.NamedExpression namedExpression = new LexerExpression.NamedExpression(
                        node, this.grammar);

                this.globalNameSpace.add(namedExpression);
                Grammar.this.lexer.addNamedExpression(namedExpression);
                Grammar.this.stringToLexerExpression.put(
                        namedExpression.getExpressionName(), namedExpression);
            }

            @Override
            public void inALexerContext(
                    ALexerContext node) {

                if (node.getName() != null) {
                    Context.NamedContext namedContext = new Context.NamedContext(
                            node, this.grammar);
                    this.globalNameSpace.add(namedContext);
                    Grammar.this.namedContexts.add(namedContext);
                }
            }

            @Override
            public void inARoot(
                    ARoot node) {

                Grammar.this.parser.addRootDeclaration(node);
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
                        namedContext = new Context.NamedContext(node,
                                this.grammar);
                        this.globalNameSpace.add(namedContext);
                        Grammar.this.namedContexts.add(namedContext);
                    }

                    this.currentContext = namedContext;
                }
            }

            @Override
            public void outAParserContext(
                    AParserContext node) {

                this.currentContext = Grammar.this.globalAnonymousContext;
            }

            @Override
            public void inAParserProduction(
                    AParserProduction node) {

                Parser.ParserProduction parserProduction = Parser.ParserProduction
                        .newParserProduction(node, this.currentContext,
                                this.grammar);

                this.globalNameSpace.add(parserProduction);
                Grammar.this.parser.addProduction(parserProduction);
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
                    Grammar.this.lexer
                            .addSelector((Selector.LexerSelector) selector);
                }
                else {
                    selector = new Selector.ParserSelector(node, this.grammar);
                    Grammar.this.parser
                            .addSelector((Selector.ParserSelector) selector);
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
                    Investigator.LexerInvestigator investigator = new Investigator.LexerInvestigator(
                            node, this.grammar);
                    this.globalNameSpace.add(investigator);

                    Grammar.this.lexer.addInvestigator(investigator);
                }
                else {
                    Investigator.ParserInvestigator investigator = new Investigator.ParserInvestigator(
                            node, this.grammar);

                    this.globalNameSpace.add(investigator);
                    Grammar.this.parser.addInvestigator(investigator);
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

                Tree.TreeProduction treeProduction = new Tree.TreeProduction(
                        node, this.grammar);

                this.treeNameSpace.add(treeProduction);
                Grammar.this.tree.addProduction(treeProduction);
            }
        });
    }

    private void findInlineExpressions(
            Start ast) {

        ast.apply(new DeclarationFinder.InlineExpressionsFinder(this));
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

    private void findTransformations(
            Node ast) {

        ast.apply(new DeclarationFinder.TransformationsFinder(this));
    }

    public void findLexerPriorities(
            Node ast) {

        ast.apply(new DeclarationFinder.LexerPrioritiesFinder(this));
    }

    private void verifyReferences() {

        if (GrammarCompiler.RESTRICTED_SYNTAX) {
            apply(new ReferenceVerifier.LexerReferenceVerifier(this));
        }
        else {
            apply(new ReferenceVerifier.LexerReferenceVerifier(this));
            apply(new ReferenceVerifier.ParserReferenceVerifier(this));
            apply(new ReferenceVerifier.TreeReferenceVerifier(this));
            apply(new ReferenceVerifier.TransformationReferenceVerifier(this));
            apply(new ReferenceVerifier.RootVerifier(this));
        }
    }

    private void verifyAssignability() {

        apply(new AssignabilityVerifier());
    }

    private void buildImplicitTransformations() {

        apply(new ImplicitProductionTransformationBuilder(this));
        apply(new ImplicitAlternativeTransformationBuilder(this));

        this.parser.apply(new TokenOrderVerifier(this));

    }

    private void resolveUnknowTypes() {

        this.transformation.apply(new GrammarVisitor() {

            private boolean inANewElement = false;

            @Override
            public void visitAlternativeTransformationElement(
                    AlternativeTransformationElement node) {

                node.constructType();

            }

            @Override
            public void visitAlternativeTransformationNewElement(
                    AlternativeTransformationElement.NewElement node) {

                this.inANewElement = true;
                node.constructType();
                this.inANewElement = false;
                // Do not visit elements
            }

            @Override
            public void visitAlternativeTransformationListElement(
                    AlternativeTransformationElement.ListElement node) {

                if (!this.inANewElement) {
                    node.constructType();
                }
                // Do not visit elements
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
        this.stringToLexerExpression.put(stringExpression.getExpressionName(),
                stringExpression);
    }

    void addCharExpression(
            LexerExpression.CharExpression charExpression) {

        String text = charExpression.getText();

        if (this.stringToCharExpression.containsKey(text)) {
            throw new InternalException("multiple mappings for " + text);
        }

        this.stringToCharExpression.put(text, charExpression);
        this.stringToLexerExpression.put(charExpression.getExpressionName(),
                charExpression);
    }

    void addDecExpression(
            LexerExpression.DecExpression decExpression) {

        String text = decExpression.getText();

        if (this.stringToDecExpression.containsKey(text)) {
            throw new InternalException("multiple mappings for " + text);
        }

        this.stringToDecExpression.put(text, decExpression);
        this.stringToLexerExpression.put(decExpression.getExpressionName(),
                decExpression);
    }

    void addHexExpression(
            LexerExpression.HexExpression hexExpression) {

        String text = hexExpression.getText();

        if (this.stringToHexExpression.containsKey(text)) {
            throw new InternalException("multiple mappings for " + text);
        }

        this.stringToHexExpression.put(text, hexExpression);
        this.stringToLexerExpression.put(hexExpression.getExpressionName(),
                hexExpression);
    }

    void addStartExpression(
            LexerExpression.StartExpression startExpression) {

        if (this.globalStartExpression != null) {
            throw new InternalException("multiple starts");
        }

        this.globalStartExpression = startExpression;
        this.stringToLexerExpression.put(startExpression.getExpressionName(),
                startExpression);
    }

    void addEndExpression(
            LexerExpression.EndExpression endExpression) {

        if (this.globalEndExpression != null) {
            throw new InternalException("multiple ends");
        }

        this.globalEndExpression = endExpression;
        this.stringToLexerExpression.put(endExpression.getExpressionName(),
                endExpression);
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

    public LexerExpression getLexerExpression(
            String expressionName) {

        return this.stringToLexerExpression.get(expressionName);
    }

    public void compileLexer() {

        if (this.globalAnonymousContext != null) {
            this.globalAnonymousContext.computeAutomaton();
        }

        for (NamedContext context : this.namedContexts) {
            context.computeAutomaton();
        }
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
                Context.NamedContext namedContext) {

            internalAdd(namedContext);
        }

        private void add(
                Parser.ParserProduction parserProduction) {

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

        private Context.NamedContext getNamedContext(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Context.NamedContext) {
                return (Context.NamedContext) nameDeclaration;
            }
            return null;
        }

        private Parser.ParserProduction getParserProduction(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Parser.ParserProduction) {
                return (Parser.ParserProduction) nameDeclaration;
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

        public boolean contains(
                String name) {

            return this.nameMap.containsKey(name);
        }
    }

    private static class TreeNameSpace {

        private final NameSpace globalNameSpace;

        private final Map<String, Tree.TreeProduction> nameMap = new HashMap<String, Tree.TreeProduction>();

        private TreeNameSpace(
                NameSpace globalNameSpace) {

            if (globalNameSpace == null) {
                throw new InternalException("globalNameSpace may not be null");
            }

            this.globalNameSpace = globalNameSpace;
        }

        private void add(
                Tree.TreeProduction treeProduction) {

            if (treeProduction == null) {
                throw new InternalException("treeProduction may not be null");
            }

            String name = treeProduction.getName();
            INameDeclaration nameDeclaration = this.globalNameSpace
                    .getNameDeclaration(name);
            if (nameDeclaration == null
                    || nameDeclaration instanceof Parser.ParserProduction
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

        private Tree.TreeProduction getTreeProduction(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Tree.TreeProduction) {
                return (Tree.TreeProduction) nameDeclaration;
            }
            return null;
        }

        public boolean contains(
                String name) {

            boolean result = this.nameMap.containsKey(name);
            return this.nameMap.containsKey(name);
        }
    }
}
