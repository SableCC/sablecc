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
import org.sablecc.sablecc.syntax3.node.*;

public abstract class LexerExpression
        implements IToken {

    final Grammar grammar;

    private Automaton savedAutomaton;

    private Acceptation acceptation;

    LexerExpression(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    public static void declareInlineExpression(
            AStringUnit declaration,
            Grammar grammar) {

        String text = declaration.getString().getText();
        StringExpression stringExpression = grammar.getStringExpression(text);
        if (stringExpression == null) {
            stringExpression = new StringExpression(declaration, grammar);
        }
        else {
            stringExpression.addDeclaration(declaration);
        }
    }

    public static void declareInlineExpression(
            ACharCharacter declaration,
            Grammar grammar) {

        String text = declaration.getChar().getText();
        CharExpression charExpression = grammar.getCharExpression(text);
        if (charExpression == null) {
            charExpression = new CharExpression(declaration, grammar);
        }
        else {
            charExpression.addDeclaration(declaration);
        }
    }

    public static void declareInlineExpression(
            ADecCharacter declaration,
            Grammar grammar) {

        String text = declaration.getDecChar().getText();
        DecExpression decExpression = grammar.getDecExpression(text);
        if (decExpression == null) {
            decExpression = new DecExpression(declaration, grammar);
        }
        else {
            decExpression.addDeclaration(declaration);
        }
    }

    public static void declareInlineExpression(
            AHexCharacter declaration,
            Grammar grammar) {

        String text = declaration.getHexChar().getText();
        HexExpression hexExpression = grammar.getHexExpression(text);
        if (hexExpression == null) {
            hexExpression = new HexExpression(declaration, grammar);
        }
        else {
            hexExpression.addDeclaration(declaration);
        }
    }

    public static void declareInlineExpression(
            AStartUnit declaration,
            Grammar grammar) {

        StartExpression startExpression = grammar.getStartExpression();
        if (startExpression == null) {
            startExpression = new StartExpression(declaration, grammar);
        }
        else {
            startExpression.addDeclaration(declaration);
        }
    }

    public static void declareInlineExpression(
            AEndUnit declaration,
            Grammar grammar) {

        EndExpression endExpression = grammar.getEndExpression();
        if (endExpression == null) {
            endExpression = new EndExpression(declaration, grammar);
        }
        else {
            endExpression.addDeclaration(declaration);
        }
    }

    public abstract Automaton getAutomaton();

    public abstract String getExpressionName();

    void saveAutomaton(
            Automaton automaton) {

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (this.savedAutomaton != null) {
            throw new InternalException("automaton is already saved");
        }

        this.savedAutomaton = automaton.minimal();
    }

    Automaton getSavedAutomaton() {

        return this.savedAutomaton;
    }

    /**
     * The associated Acceptation object. Each LexerExpression is associated
     * with a Acceptation object used to refer itself in an automaton.
     */
    public Acceptation getAcceptation() {

        if (this.acceptation == null) {
            this.acceptation = new Acceptation(getExpressionName());
        }
        return this.acceptation;
    }

    public static class NamedExpression
            extends LexerExpression
            implements INamedToken, IReferencable, IVisitableGrammarPart {

        private final ANamedExpression declaration;

        private final Expression expression;

        private boolean recursionGuard;

        NamedExpression(
                ANamedExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;

            this.expression = Expression.newExpression(
                    declaration.getExpression(), grammar);
        }

        public ANamedExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getExpression() {

            return this.expression;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitNamedExpression(this);

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

            return "regular expression";
        }

        @Override
        public Token getLocation() {

            return this.declaration.getName();
        }

        @Override
        public Automaton getAutomaton() {

            Automaton automaton = getSavedAutomaton();

            if (automaton == null) {
                if (this.recursionGuard) {
                    throw SemanticException.genericLocatedError("The "
                            + getExpressionName()
                            + " token definition is recursive.",
                            getNameIdentifier());
                }
                else {
                    this.recursionGuard = true;
                }

                automaton = this.expression.getAutomaton();
                saveAutomaton(automaton);

                this.recursionGuard = false;
            }

            return automaton;
        }

        @Override
        public String getExpressionName() {

            return getName();
        }

    }

    public static abstract class InlineExpression
            extends LexerExpression
            implements INameDeclaration, IReferencable {

        private final String text;

        private String internalName;

        private InlineExpression(
                String text,
                Grammar grammar) {

            super(grammar);

            if (text == null) {
                throw new InternalException("text may not be null");
            }

            this.text = text;
        }

        public String getText() {

            return this.text;
        }

        @Override
        public String getExpressionName() {

            return this.text;
        }

        public String getInternalName() {

            String internalName = this.internalName;
            if (internalName == null) {
                internalName = this.grammar.getNextAnonymousTokenName();
                this.internalName = internalName;
            }
            return internalName;
        }

        public String getInternalName_camelCase() {

            return to_camelCase(getInternalName());
        }

        public String getInternalName_CamelCase() {

            return to_CamelCase(getInternalName());
        }

        @Override
        public Token getLocation() {

            throw new InternalException("Not implemented");
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            throw new InternalException("Not implemented");
        }
    }

    public static class StringExpression
            extends InlineExpression {

        private Set<AStringUnit> declarations = new LinkedHashSet<AStringUnit>();

        private String firstName;

        private TString firstToken;

        private StringExpression(
                AStringUnit declaration,
                Grammar grammar) {

            super(declaration.getString().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addStringExpression(this);
        }

        private void addDeclaration(
                AStringUnit declaration) {

            if (!declaration.getString().getText().equals(getText())) {
                throw new InternalException("inappropriate declaration");
            }

            if (this.declarations.contains(declaration)) {
                throw new InternalException("declaration was already added");
            }

            this.declarations.add(declaration);
        }

        @Override
        public Automaton getAutomaton() {

            Automaton automaton = getSavedAutomaton();

            if (automaton == null) {

                automaton = Automaton.getEpsilonLookAnyStarEnd();

                String text = getText();

                // remove enclosing quotes
                text = text.substring(1, text.length() - 1);
                // replace escape chars
                text = text.replace("\\\\", "\\");
                text = text.replace("\\'", "'");

                do {
                    char c = text.charAt(0);
                    text = text.substring(1);
                    automaton = automaton.concat(Automaton
                            .getSymbolLookAnyStarEnd(new Symbol(c)));
                }
                while (text.length() > 0);

                saveAutomaton(automaton);
            }

            return automaton;
        }

        @Override
        public TString getNameIdentifier() {

            if (this.firstToken == null) {
                AStringUnit firstOccurence = this.declarations.iterator()
                        .next();
                this.firstToken = firstOccurence.getString();
            }

            return this.firstToken;

        }

        @Override
        public String getName() {

            if (this.firstName == null) {
                AStringUnit firstOccurence = this.declarations.iterator()
                        .next();
                this.firstName = firstOccurence.getString().getText();
            }

            return this.firstName;
        }

        @Override
        public String getNameType() {

            return "String Inline Expression";
        }
    }

    public static class CharExpression
            extends InlineExpression {

        private Set<ACharCharacter> declarations = new LinkedHashSet<ACharCharacter>();

        private String firstName;

        private TChar firstToken;

        private CharExpression(
                ACharCharacter declaration,
                Grammar grammar) {

            super(declaration.getChar().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addCharExpression(this);
        }

        private void addDeclaration(
                ACharCharacter declaration) {

            if (!declaration.getChar().getText().equals(getText())) {
                throw new InternalException("inappropriate declaration");
            }

            if (this.declarations.contains(declaration)) {
                throw new InternalException("declaration was already added");
            }

            this.declarations.add(declaration);
        }

        @Override
        public Automaton getAutomaton() {

            Automaton automaton = getSavedAutomaton();

            if (automaton == null) {
                String text = getText();

                // remove enclosing quotes
                text = text.substring(1, text.length() - 1);
                // replace escape chars
                text = text.replace("\\\\", "\\");
                text = text.replace("\\'", "'");

                char c = text.charAt(0);
                automaton = Automaton.getSymbolLookAnyStarEnd(new Symbol(c));
                saveAutomaton(automaton);
            }
            return automaton;
        }

        @Override
        public TChar getNameIdentifier() {

            if (this.firstToken == null) {
                ACharCharacter firstOccurence = this.declarations.iterator()
                        .next();
                this.firstToken = firstOccurence.getChar();
            }

            return this.firstToken;
        }

        @Override
        public String getName() {

            if (this.firstName == null) {
                ACharCharacter firstOccurence = this.declarations.iterator()
                        .next();
                this.firstName = firstOccurence.getChar().getText();
            }

            return this.firstName;
        }

        @Override
        public String getNameType() {

            return "char inline expression";
        }
    }

    public static class DecExpression
            extends InlineExpression {

        private Set<ADecCharacter> declarations = new LinkedHashSet<ADecCharacter>();

        private String firstName;

        private TDecChar firstToken;

        private DecExpression(
                ADecCharacter declaration,
                Grammar grammar) {

            super(declaration.getDecChar().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addDecExpression(this);
        }

        private void addDeclaration(
                ADecCharacter declaration) {

            if (!declaration.getDecChar().getText().equals(getText())) {
                throw new InternalException("inappropriate declaration");
            }

            if (this.declarations.contains(declaration)) {
                throw new InternalException("declaration was already added");
            }

            this.declarations.add(declaration);
        }

        @Override
        public Automaton getAutomaton() {

            Automaton automaton = getSavedAutomaton();

            if (automaton == null) {
                String text = getText().substring(1);

                automaton = Automaton.getSymbolLookAnyStarEnd(new Symbol(text));
                saveAutomaton(automaton);
            }

            return automaton;
        }

        @Override
        public TDecChar getNameIdentifier() {

            if (this.firstToken == null) {
                ADecCharacter firstOccurence = this.declarations.iterator()
                        .next();
                this.firstToken = firstOccurence.getDecChar();
            }

            return this.firstToken;
        }

        @Override
        public String getName() {

            if (this.firstName == null) {
                ADecCharacter firstOccurence = this.declarations.iterator()
                        .next();
                this.firstName = firstOccurence.getDecChar().getText();
            }

            return this.firstName;
        }

        @Override
        public String getNameType() {

            return "dec char inline expression";
        }
    }

    public static class HexExpression
            extends InlineExpression {

        private Set<AHexCharacter> declarations = new LinkedHashSet<AHexCharacter>();

        private String firstName;

        private THexChar firstToken;

        private HexExpression(
                AHexCharacter declaration,
                Grammar grammar) {

            super(declaration.getHexChar().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addHexExpression(this);
        }

        private void addDeclaration(
                AHexCharacter declaration) {

            if (!declaration.getHexChar().getText().equals(getText())) {
                throw new InternalException("inappropriate declaration");
            }

            if (this.declarations.contains(declaration)) {
                throw new InternalException("declaration was already added");
            }

            this.declarations.add(declaration);
        }

        @Override
        public Automaton getAutomaton() {

            Automaton automaton = getSavedAutomaton();

            if (automaton == null) {
                String text = getText().substring(2);

                automaton = Automaton.getSymbolLookAnyStarEnd(new Symbol(text,
                        16));
                saveAutomaton(automaton);
            }

            return automaton;
        }

        @Override
        public THexChar getNameIdentifier() {

            if (this.firstToken == null) {
                AHexCharacter firstOccurence = this.declarations.iterator()
                        .next();
                this.firstToken = firstOccurence.getHexChar();
            }

            return this.firstToken;
        }

        @Override
        public String getName() {

            if (this.firstName == null) {
                AHexCharacter firstOccurence = this.declarations.iterator()
                        .next();
                this.firstName = firstOccurence.getHexChar().getText();
            }

            return this.firstName;
        }

        @Override
        public String getNameType() {

            return "hex char inline expression";
        }
    }

    public static class StartExpression
            extends InlineExpression {

        private Set<AStartUnit> declarations = new LinkedHashSet<AStartUnit>();

        private String firstName;

        private TStartKeyword firstToken;

        private StartExpression(
                AStartUnit declaration,
                Grammar grammar) {

            super(declaration.getStartKeyword().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addStartExpression(this);
        }

        private void addDeclaration(
                AStartUnit declaration) {

            if (!declaration.getStartKeyword().getText().equals(getText())) {
                throw new InternalException("inappropriate declaration");
            }

            if (this.declarations.contains(declaration)) {
                throw new InternalException("declaration was already added");
            }

            this.declarations.add(declaration);
        }

        @Override
        public Automaton getAutomaton() {

            throw new InternalException("not implemented");

        }

        @Override
        public TStartKeyword getNameIdentifier() {

            if (this.firstToken == null) {
                AStartUnit firstOccurence = this.declarations.iterator().next();
                this.firstToken = firstOccurence.getStartKeyword();
            }

            return this.firstToken;
        }

        @Override
        public String getName() {

            if (this.firstName == null) {
                AStartUnit firstOccurence = this.declarations.iterator().next();
                this.firstName = firstOccurence.getStartKeyword().getText();
            }

            return this.firstName;
        }

        @Override
        public String getNameType() {

            return "start expression";
        }
    }

    public static class EndExpression
            extends InlineExpression {

        private Set<AEndUnit> declarations = new LinkedHashSet<AEndUnit>();

        private String firstName;

        private TEndKeyword firstToken;

        private EndExpression(
                AEndUnit declaration,
                Grammar grammar) {

            super(declaration.getEndKeyword().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addEndExpression(this);
        }

        private void addDeclaration(
                AEndUnit declaration) {

            if (!declaration.getEndKeyword().getText().equals(getText())) {
                throw new InternalException("inappropriate declaration");
            }

            if (this.declarations.contains(declaration)) {
                throw new InternalException("declaration was already added");
            }

            this.declarations.add(declaration);
        }

        @Override
        public Automaton getAutomaton() {

            Automaton automaton = getSavedAutomaton();

            if (automaton == null) {
                automaton = Automaton.getEpsilonLookEnd();
                saveAutomaton(automaton);
            }

            return automaton;
        }

        @Override
        public TEndKeyword getNameIdentifier() {

            if (this.firstToken == null) {
                AEndUnit firstOccurence = this.declarations.iterator().next();
                this.firstToken = firstOccurence.getEndKeyword();
            }

            return this.firstToken;
        }

        @Override
        public String getName() {

            if (this.firstName == null) {
                AEndUnit firstOccurence = this.declarations.iterator().next();
                this.firstName = firstOccurence.getEndKeyword().getText();
            }

            return this.firstName;
        }

        @Override
        public String getNameType() {

            return "end inline expression";
        }
    }
}
