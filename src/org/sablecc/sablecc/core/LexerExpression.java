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
import org.sablecc.sablecc.syntax3.node.*;

public abstract class LexerExpression
        implements IToken {

    private final Grammar grammar;

    LexerExpression(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    StringExpression declareInlineExpression(
            AStringUnit declaration) {

        String text = declaration.getString().getText();
        StringExpression stringExpression = this.grammar
                .getStringExpression(text);
        if (stringExpression == null) {
            stringExpression = new StringExpression(declaration, this.grammar);
        }
        else {
            stringExpression.addDeclaration(declaration);
        }
        return stringExpression;
    }

    CharExpression declareInlineExpression(
            ACharCharacter declaration) {

        String text = declaration.getChar().getText();
        CharExpression charExpression = this.grammar.getCharExpression(text);
        if (charExpression == null) {
            charExpression = new CharExpression(declaration, this.grammar);
        }
        else {
            charExpression.addDeclaration(declaration);
        }
        return charExpression;
    }

    DecExpression declareInlineExpression(
            ADecCharacter declaration) {

        String text = declaration.getDecChar().getText();
        DecExpression decExpression = this.grammar.getDecExpression(text);
        if (decExpression == null) {
            decExpression = new DecExpression(declaration, this.grammar);
        }
        else {
            decExpression.addDeclaration(declaration);
        }
        return decExpression;
    }

    HexExpression declareInlineExpression(
            AHexCharacter declaration) {

        String text = declaration.getHexChar().getText();
        HexExpression hexExpression = this.grammar.getHexExpression(text);
        if (hexExpression == null) {
            hexExpression = new HexExpression(declaration, this.grammar);
        }
        else {
            hexExpression.addDeclaration(declaration);
        }
        return hexExpression;
    }

    StartExpression declareInlineExpression(
            AStartUnit declaration) {

        StartExpression startExpression = this.grammar.getStartExpression();
        if (startExpression == null) {
            startExpression = new StartExpression(declaration, this.grammar);
        }
        else {
            startExpression.addDeclaration(declaration);
        }
        return startExpression;
    }

    EndExpression declareInlineExpression(
            AEndUnit declaration) {

        EndExpression endExpression = this.grammar.getEndExpression();
        if (endExpression == null) {
            endExpression = new EndExpression(declaration, this.grammar);
        }
        else {
            endExpression.addDeclaration(declaration);
        }
        return endExpression;
    }

    public static class NamedExpression
            extends LexerExpression
            implements INamedToken {

        private final ANamedExpression declaration;

        NamedExpression(
                ANamedExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }

        public TIdentifier getNameIdentifier() {

            return this.declaration.getName();
        }

        public String getName() {

            return getNameIdentifier().getText();
        }

        public String getNameType() {

            return "regular expression";
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
            grammar.addMapping(declaration, this);
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

    }

    public static class CharExpression
            extends InlineExpression {

        private Set<ACharCharacter> declarations = new LinkedHashSet<ACharCharacter>();

        private CharExpression(
                ACharCharacter declaration,
                Grammar grammar) {

            super(declaration.getChar().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addMapping(declaration, this);
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
    }

    public static class DecExpression
            extends InlineExpression {

        private Set<ADecCharacter> declarations = new LinkedHashSet<ADecCharacter>();

        private DecExpression(
                ADecCharacter declaration,
                Grammar grammar) {

            super(declaration.getDecChar().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addMapping(declaration, this);
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
    }

    public static class HexExpression
            extends InlineExpression {

        private Set<AHexCharacter> declarations = new LinkedHashSet<AHexCharacter>();

        private HexExpression(
                AHexCharacter declaration,
                Grammar grammar) {

            super(declaration.getHexChar().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addMapping(declaration, this);
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
    }

    public static class StartExpression
            extends InlineExpression {

        private Set<AStartUnit> declarations = new LinkedHashSet<AStartUnit>();

        private StartExpression(
                AStartUnit declaration,
                Grammar grammar) {

            super(declaration.getStartKeyword().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addMapping(declaration, this);
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
    }

    public static class EndExpression
            extends InlineExpression {

        private Set<AEndUnit> declarations = new LinkedHashSet<AEndUnit>();

        private EndExpression(
                AEndUnit declaration,
                Grammar grammar) {

            super(declaration.getEndKeyword().getText(), grammar);

            this.declarations.add(declaration);
            grammar.addMapping(declaration, this);
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
    }

}
