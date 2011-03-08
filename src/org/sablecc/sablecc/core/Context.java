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

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class Context {

    final Grammar grammar;

    private Context(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    public static class NamedContext
            extends Context
            implements INameDeclaration {

        private ALexerContext lexerDeclaration;

        private AParserContext parserDeclaration;

        NamedContext(
                ALexerContext declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (declaration.getName() == null) {
                throw new InternalException(
                        "anonymous contexts are not allowed");
            }

            this.lexerDeclaration = declaration;
            grammar.addMapping(declaration, this);
        }

        NamedContext(
                AParserContext declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (declaration.getName() == null) {
                throw new InternalException(
                        "anonymous contexts are not allowed");
            }

            this.parserDeclaration = declaration;
            grammar.addMapping(declaration, this);
        }

        void addDeclaration(
                AParserContext declaration) {

            if (this.parserDeclaration != null) {
                throw SemanticException.spuriousParserNamedContextDeclaration(
                        declaration, this);
            }

            if (declaration.getName() == null
                    || !declaration.getName().getText().equals(getName())) {
                throw new InternalException("invalid context declaration");
            }

            this.parserDeclaration = declaration;
            this.grammar.addMapping(declaration, this);
        }

        public TIdentifier getNameIdentifier() {

            if (this.lexerDeclaration != null) {
                return this.lexerDeclaration.getName();
            }

            return this.parserDeclaration.getName();
        }

        public String getName() {

            return getNameIdentifier().getText();
        }

        public String getNameType() {

            return "context";
        }

        public AParserContext getParserDeclaration() {

            return this.parserDeclaration;
        }

    }

    public static class AnonymousContext
            extends Context {

        private ALexerContext lexerDeclaration;

        private AParserContext parserDeclaration;

        AnonymousContext(
                ALexerContext declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (declaration.getName() != null) {
                throw new InternalException("named contexts are not allowed");
            }

            this.lexerDeclaration = declaration;
            grammar.addMapping(declaration, this);
        }

        AnonymousContext(
                AParserContext declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (declaration.getName() != null) {
                throw new InternalException("named contexts are not allowed");
            }

            this.parserDeclaration = declaration;
            grammar.addMapping(declaration, this);
        }

        void addDeclaration(
                AParserContext declaration) {

            if (this.parserDeclaration != null) {
                throw new InternalException(
                        "the anonymous context may not have two parser declarations");
            }

            if (declaration.getName() != null) {
                throw new InternalException("invalid context declaration");
            }

            this.parserDeclaration = declaration;
            this.grammar.addMapping(declaration, this);
        }
    }

}
