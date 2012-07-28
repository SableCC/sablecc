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

import static org.sablecc.util.CamelCase.*;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.core.walker.*;
import org.sablecc.sablecc.grammar.*;
import org.sablecc.sablecc.launcher.*;
import org.sablecc.sablecc.oldlrautomaton.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public class Grammar
        implements INameDeclaration, IVisitableGrammarPart {

    private AGrammar declaration;

    private final NameSpace globalNameSpace = new NameSpace();

    private final TreeNameSpace treeNameSpace = new TreeNameSpace(
            this.globalNameSpace);

    private Context globalAnonymousContext;

    private LexerExpression.StartExpression globalStartExpression;

    private LexerExpression.EndExpression globalEndExpression;

    private final List<Context> namedContexts = new LinkedList<Context>();

    private final Lexer lexer = new Lexer();

    private final Parser parser = new Parser();

    private final Transformation transformation = new Transformation();

    private final Tree tree = new Tree();

    private int nextAnonymousTokenId;

    private SGrammar simplifiedGrammar;

    public Grammar(
            Start ast) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        if (GrammarCompiler.RESTRICTED_SYNTAX) {
            findAnonymousContexts(ast);
            fillGlobalNameSpace(ast);
            findInlineExpressions(ast);
            findLexerPriorities(ast);
            verifyandResolveReferences();
            verifyPriorities();
        }
        else {
            findAnonymousContexts(ast);
            fillGlobalNameSpace(ast);
            fillTreeNameSpace(ast);
            findInlineExpressions(ast);
            findTransformations(ast);
            findLexerPriorities(ast);

            verifyandResolveReferences();
            verifyPriorities();

            if (hasATree()) {
                buildImplicitTransformations();
                resolveUnknowTypes();
                verifyAssignability();
            }

        }

        if (this.globalAnonymousContext == null && this.namedContexts.isEmpty()) {
            throw SemanticException
                    .genericError("The Lexer and the Parser are both empty.");
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

    public String getName_camelCase() {

        return to_camelCase(getName());
    }

    public String getName_CamelCase() {

        return to_CamelCase(getName());
    }

    @Override
    public String getNameType() {

        return "grammar";
    }

    public List<Context> getNamedContexts() {

        return this.namedContexts;
    }

    public Context getGlobalAnonymousContext() {

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
            }

            @Override
            public void inALexerContext(
                    ALexerContext node) {

                if (node.getName() != null) {
                    Context namedContext = new Context(this.grammar, node);
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
                    Context namedContext = this.globalNameSpace
                            .getNamedContext(name);

                    if (namedContext != null) {
                        namedContext.addDeclaration(node);
                    }
                    else {
                        namedContext = new Context(this.grammar, node);
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

                    Grammar.this.globalAnonymousContext = new Context(
                            this.grammar, node);
                }
            }

            @Override
            public void inAParserContext(
                    AParserContext node) {

                if (node.getName() == null) {
                    if (Grammar.this.globalAnonymousContext == null) {
                        Grammar.this.globalAnonymousContext = new Context(
                                this.grammar, node);
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

    private void verifyandResolveReferences() {

        // The reference solvers should call in this
        // order to sucessfully complete.

        if (GrammarCompiler.RESTRICTED_SYNTAX) {
            apply(new ReferenceVerifier.LexerReferenceVerifier(this));
            apply(new ReferenceVerifier.ParserReferenceVerifier(this));
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

    private void verifyPriorities() {

        apply(new PriorityVerifier(this));
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

        this.globalNameSpace.add(stringExpression);
        this.lexer.addInlineExpression(stringExpression);
    }

    void addCharExpression(
            LexerExpression.CharExpression charExpression) {

        this.globalNameSpace.add(charExpression);
        this.lexer.addInlineExpression(charExpression);
    }

    void addDecExpression(
            LexerExpression.DecExpression decExpression) {

        this.globalNameSpace.add(decExpression);
        this.lexer.addInlineExpression(decExpression);
    }

    void addHexExpression(
            LexerExpression.HexExpression hexExpression) {

        this.globalNameSpace.add(hexExpression);
        this.lexer.addInlineExpression(hexExpression);
    }

    void addStartExpression(
            LexerExpression.StartExpression startExpression) {

        this.globalStartExpression = startExpression;
        this.lexer.addInlineExpression(startExpression);
    }

    void addEndExpression(
            LexerExpression.EndExpression endExpression) {

        this.globalEndExpression = endExpression;
        this.lexer.addInlineExpression(endExpression);
    }

    public LexerExpression.StringExpression getStringExpression(
            String text) {

        return this.globalNameSpace.getStringExpression(text);
    }

    public LexerExpression.CharExpression getCharExpression(
            String text) {

        return this.globalNameSpace.getCharExpression(text);
    }

    public LexerExpression.DecExpression getDecExpression(
            String text) {

        return this.globalNameSpace.getDecExpression(text);
    }

    public LexerExpression.HexExpression getHexExpression(
            String text) {

        return this.globalNameSpace.getHexExpression(text);
    }

    public LexerExpression.StartExpression getStartExpression() {

        return this.globalStartExpression;
    }

    public LexerExpression.EndExpression getEndExpression() {

        return this.globalEndExpression;
    }

    public LexerExpression getLexerExpression(
            String expressionName) {

        return this.lexer.getExpression(expressionName);
    }

    public void compileLexer(
            Trace trace,
            Strictness strictness) {

        Automaton automaton;
        if (this.globalAnonymousContext != null) {
            automaton = this.globalAnonymousContext.computeAutomaton()
                    .minimal().longest().minimal();
            automaton = checkAndApplyLexerPrecedence(automaton, trace,
                    strictness).minimal();
            this.lexer.setAutomaton(automaton.withMarkers().minimal());
        }
        else {
            throw new InternalException("not implemented");
        }

        for (Context context : this.namedContexts) {
            context.computeAutomaton();
        }

        // Look for useless LexerExpression
        for (LexerExpression lexerExpression : this.lexer.getExpressions()) {
            // If their is no automaton saved it means that the LexerExpression
            // was not used to build the big automaton.
            if (lexerExpression.getSavedAutomaton() == null) {
                if (strictness == Strictness.STRICT) {
                    throw SemanticException.genericError("The "
                            + lexerExpression.getExpressionName()
                            + " expression is useless.");
                }
                else {
                    trace.verboseln("    The "
                            + lexerExpression.getExpressionName()
                            + " expression is useless.");
                }
            }
        }

        for (LexerExpression lexerExpression : this.lexer.getExpressions()) {
            // Note: getting the automaton forces the validation of the semantic
            // validity of (eg. cirularity)
            Automaton lexerAutomaton = lexerExpression.getAutomaton();
        }

        for (LexerExpression lexerExpression : this.globalAnonymousContext
                .getLexerExpressionTokensAndIgnored()) {
            // Note: The big automaton has to be minimal (thus with the unused
            // acceptations removed)
            if (!automaton.getAcceptations().contains(
                    lexerExpression.getAcceptation())) {
                if (strictness == Strictness.STRICT) {
                    throw SemanticException.genericError("The "
                            + lexerExpression.getExpressionName()
                            + " token does not match anything.");
                }
                else {
                    trace.verboseln("    The "
                            + lexerExpression.getExpressionName()
                            + " token does not match anything.");
                }
            }

            Automaton expressionAutomaton = lexerExpression.getAutomaton();
            for (RichSymbol richSymbol : expressionAutomaton.getStartState()
                    .getTransitions().keySet()) {
                if (richSymbol.isLookahead()) {
                    // We have a lookahead transition from the start state.
                    // Note: this works since the result of getAutomaton() is
                    // minimized.
                    throw SemanticException.genericError("The "
                            + lexerExpression.getExpressionName()
                            + " token matches the empty string.");
                }
            }
        }
    }

    /**
     * Add a priority between high and low. Helper function for
     * checkAndApplyLexerPrecedence.
     */
    static private void addPriority(
            Map<Acceptation, Set<Acceptation>> priorities,
            Acceptation high,
            Acceptation low) {

        Set<Acceptation> set;
        set = priorities.get(high);
        if (set == null) {
            set = new HashSet<Acceptation>();
            priorities.put(high, set);
        }
        set.add(low);
    }

    /**
     * Is there a priority between high and low? Helper function for
     * checkAndApplyLexerPrecedence.
     */
    static private boolean hasPriority(
            Map<Acceptation, Set<Acceptation>> priorities,
            Acceptation high,
            Acceptation low) {

        Set<Acceptation> set;
        set = priorities.get(high);
        return set != null && set.contains(low);
    }

    /*
     * Check and apply implicit and explicit lexical precedence rules. Display
     * errors and infos for the human user during the process.
     *
     * @param automaton
     *            is the automaton to check. In order to have the explicit
     *            priorities applied, it is required that the automaton is
     *            tagged with the acceptation of the LexerExpression.
     * @return a new automaton where only the right acceptation tags remains.
     */
    public Automaton checkAndApplyLexerPrecedence(
            Automaton automaton,
            Trace trace,
            Strictness strictness) {

        automaton = automaton.minimal();
        Map<State, String> words = automaton.collectShortestWords();
        Map<Acceptation, Set<State>> accepts = automaton
                .collectAcceptationStates();

        // Associate each acceptation with the ones it share at least a common
        // state.
        Map<Acceptation, Set<Acceptation>> conflicts = new HashMap<Acceptation, Set<Acceptation>>();

        // Associate each acceptation with the ones it supersedes.
        Map<Acceptation, Set<Acceptation>> priorities = new HashMap<Acceptation, Set<Acceptation>>();

        // Fill the priorities structure with the implicit inclusion rule
        for (Acceptation acc1 : automaton.getAcceptations()) {
            if (acc1 == Acceptation.ACCEPT) {
                continue;
            }

            // FIXME: empty LexerExpressions are not detected here since
            // their acceptation tag is not in the automaton.

            // Collect all the conflicts
            Set<State> set1 = accepts.get(acc1);
            Set<Acceptation> confs = new TreeSet<Acceptation>();
            for (State s : set1) {
                confs.addAll(s.getAcceptations());
            }
            conflicts.put(acc1, confs);

            // Check for implicit priority for each conflict
            for (Acceptation acc2 : confs) {
                if (acc2 == Acceptation.ACCEPT) {
                    continue;
                }
                if (acc1 == acc2) {
                    continue;
                }
                Set<State> set2 = accepts.get(acc2);
                if (set2.equals(set1)) {
                    if (!conflicts.containsKey(acc2)) {
                        throw SemanticException.genericError("The "
                                + acc1.getName() + " and " + acc2.getName()
                                + " tokens are equivalent.");
                    }
                }
                else if (set2.containsAll(set1)) {
                    addPriority(priorities, acc1, acc2);
                    State example = null;
                    for (State s : set2) {
                        if (!set1.contains(s)) {
                            example = s;
                            break;
                        }
                    }
                    // Note: Since set1 is strictly included in set2, example
                    // cannot be null
                    trace.verboseln("    The " + acc1.getName()
                            + " token is included in the " + acc2.getName()
                            + " token. (Example of divergence: '"
                            + words.get(example) + "'.)");
                }
            }
        }

        // Collect new acceptation states and see if a conflict still exists
        Map<State, Acceptation> newAccepts = new HashMap<State, Acceptation>();
        for (State s : automaton.getStates()) {
            if (s.getAcceptations().isEmpty()) {
                continue;
            }
            Acceptation candidate = s.getAcceptations().first();
            for (Acceptation challenger : s.getAcceptations()) {
                if (candidate == challenger) {
                    continue;
                }
                if (hasPriority(priorities, candidate, challenger)) {
                    // nothing. keep the candidate
                }
                else if (hasPriority(priorities, challenger, candidate)) {
                    candidate = challenger;
                }
                else {
                    throw SemanticException
                            .genericError("The "
                                    + candidate.getName()
                                    + " token and the "
                                    + challenger.getName()
                                    + " token conflict on the string '"
                                    + words.get(s)
                                    + "'. You should specify a precedence between them.");
                }
            }
            newAccepts.put(s, candidate);
        }

        // Ask for a new automaton with the correct acceptation states.
        return automaton.resetAcceptations(newAccepts);
    }

    static class NameSpace {

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
                Context context) {

            if (!context.isNamed()) {
                throw new InternalException(
                        "The anonymous context shouldn't be added to the global namespace");
            }

            internalAdd(context);
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

        private void add(
                LexerExpression.InlineExpression inlineExpression) {

            internalAdd(inlineExpression);
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

        private Context getNamedContext(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof Context) {
                return (Context) nameDeclaration;
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

        private LexerExpression.StringExpression getStringExpression(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof LexerExpression.StringExpression) {
                return (LexerExpression.StringExpression) nameDeclaration;
            }
            return null;
        }

        private LexerExpression.CharExpression getCharExpression(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof LexerExpression.CharExpression) {
                return (LexerExpression.CharExpression) nameDeclaration;
            }
            return null;
        }

        private LexerExpression.DecExpression getDecExpression(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof LexerExpression.DecExpression) {
                return (LexerExpression.DecExpression) nameDeclaration;
            }
            return null;
        }

        private LexerExpression.HexExpression getHexExpression(
                String name) {

            INameDeclaration nameDeclaration = getNameDeclaration(name);
            if (nameDeclaration instanceof LexerExpression.HexExpression) {
                return (LexerExpression.HexExpression) nameDeclaration;
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

    public String getNextAnonymousTokenName() {

        return "t$" + this.nextAnonymousTokenId++;
    }

    public boolean hasATree() {

        return this.tree.getProductions().size() > 0;
    }

    public void compileParser(
            Trace trace,
            Strictness strictness) {

        SGrammar sGrammar = new SGrammar(this);
        OldGrammar oldGrammar = null;
        for (Production production : sGrammar.getProductions()) {

            if (oldGrammar == null) {
                oldGrammar = new OldGrammar(this, production.getName(),
                        production);
            }

            OldProduction oldProduction = oldGrammar.getProduction(
                    production.getName(), production);

            for (Alternative alternative : production.getAlternatives()) {

                OldAlternative oldAlternative = oldProduction.addAlternative(
                        "", alternative);

                for (Element element : alternative.getElements()) {
                    OldElement oldElement;
                    if (element instanceof Element.TokenElement) {
                        Element.TokenElement tokenElement = (Element.TokenElement) element;
                        oldElement = oldAlternative
                                .addTokenElement(this, element.getName(),
                                        oldGrammar.getToken(tokenElement
                                                .getTypeName()), tokenElement);
                    }
                    else {
                        Element.ProductionElement productionElement = (Element.ProductionElement) element;
                        oldElement = oldAlternative.addProductionElement(
                                element.getName(), oldGrammar.getProduction(
                                        element.getTypeName(),
                                        productionElement.getReference()),
                                productionElement);
                    }
                    sGrammar.addOldElement(element, oldElement);
                }
            }
        }

        oldGrammar.stabilize();
        oldGrammar.computeShortestLengthAndDetectUselessProductions();
        sGrammar.setLRAutomaton(new LRAutomaton(oldGrammar, trace));
        this.simplifiedGrammar = sGrammar;
    }

    public SGrammar getSimplifiedGrammar() {

        return this.simplifiedGrammar;
    }
}
