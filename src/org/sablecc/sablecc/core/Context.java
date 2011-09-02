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
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class Context
        implements IVisitableGrammarPart {

    final Grammar grammar;

    private ATokens tokensDeclaration;

    private AIgnored ignoredeDeclaration;

    private Context(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    public void addTokensDeclaration(
            ATokens tokensDeclaration) {

        this.tokensDeclaration = tokensDeclaration;
    }

    public void addIgnoredDeclaration(
            AIgnored ignoredDeclaration) {

        this.ignoredeDeclaration = ignoredDeclaration;
    }

    public ATokens getTokensDeclaration() {

        return this.tokensDeclaration;
    }

    public AIgnored getIgnoredeDeclaration() {

        return this.ignoredeDeclaration;
    }

    private static void findTokensAndIgnored(
            final Context context,
            Node contextDeclaration) {

        contextDeclaration.apply(new DepthFirstAdapter() {

            @Override
            public void inATokens(
                    ATokens node) {

                context.addTokensDeclaration(node);
            }

            @Override
            public void inAIgnored(
                    AIgnored node) {

                context.addIgnoredDeclaration(node);
            }
        });
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

            findTokensAndIgnored(this, this.lexerDeclaration);
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
        }

        @Override
        public TIdentifier getNameIdentifier() {

            if (this.lexerDeclaration != null) {
                return this.lexerDeclaration.getName();
            }

            return this.parserDeclaration.getName();
        }

        @Override
        public String getName() {

            return getNameIdentifier().getText();
        }

        @Override
        public String getNameType() {

            return "context";
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitNamedContext(this);

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

            findTokensAndIgnored(this, this.lexerDeclaration);
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
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAnonymousContext(this);

        }
    }

}
