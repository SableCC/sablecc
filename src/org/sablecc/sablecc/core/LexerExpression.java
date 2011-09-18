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
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class LexerExpression
        implements IToken {

    private final Grammar grammar;

    private Automaton savedAutomaton;

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

    void saveAutomaton(
            Automaton automaton) {

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (this.savedAutomaton != null) {
            throw new InternalException("automaton is already saved");
        }

        this.savedAutomaton = automaton.minimal();

        System.out.println(automaton);
    }

    Automaton getSavedAutomaton() {

        return this.savedAutomaton;
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
                    throw new InternalException(
                            "TODO: raise recursive dependence semantic error");
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

    }

    public static abstract class InlineExpression
            extends LexerExpression {

        private final String text;

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

    }

    public static class StringExpression
            extends InlineExpression {

        private Set<AStringUnit> declarations = new LinkedHashSet<AStringUnit>();

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

    }

    public static class CharExpression
            extends InlineExpression {

        private Set<ACharCharacter> declarations = new LinkedHashSet<ACharCharacter>();

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
    }

    public static class DecExpression
            extends InlineExpression {

        private Set<ADecCharacter> declarations = new LinkedHashSet<ADecCharacter>();

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
    }

    public static class HexExpression
            extends InlineExpression {

        private Set<AHexCharacter> declarations = new LinkedHashSet<AHexCharacter>();

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
    }

    public static class StartExpression
            extends InlineExpression {

        private Set<AStartUnit> declarations = new LinkedHashSet<AStartUnit>();

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
    }

    public static class EndExpression
            extends InlineExpression {

        private Set<AEndUnit> declarations = new LinkedHashSet<AEndUnit>();

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
    }
}
