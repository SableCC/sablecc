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
import org.sablecc.util.*;

public abstract class ParserProduction
        implements INameDeclaration {

    private final AParserProduction declaration;

    private final Grammar grammar;

    private final LocalNamespace namespace;

    private final LinkedList<ParserAlternative> alternatives = new LinkedList<ParserAlternative>();

    private ParserProduction(
            AParserProduction declaration,
            Grammar grammar) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.declaration = declaration;
        this.grammar = grammar;

        findAlternatives();

        this.namespace = new LocalNamespace(this.alternatives);

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

        return "parser production";
    }

    static ParserProduction newParserProduction(
            AParserProduction declaration,
            Grammar grammar) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (declaration.getQualifier() == null) {
            return new NormalProduction(declaration, grammar);
        }

        if (declaration.getQualifier() instanceof ADanglingQualifier) {
            return new DanglingProduction(declaration, grammar);
        }

        if (declaration.getQualifier() instanceof ATokenQualifier) {
            return new TokenProduction(declaration, grammar);
        }

        throw new InternalException("unhandled case");
    }

    public static class NormalProduction
            extends ParserProduction {

        private NormalProduction(
                AParserProduction declaration,
                Grammar grammar) {

            super(declaration, grammar);
        }

    }

    public static class DanglingProduction
            extends ParserProduction {

        private DanglingProduction(
                AParserProduction declaration,
                Grammar grammar) {

            super(declaration, grammar);
        }

    }

    public static class TokenProduction
            extends ParserProduction
            implements INamedToken {

        private TokenProduction(
                AParserProduction declaration,
                Grammar grammar) {

            super(declaration, grammar);
        }

    }

    private void findAlternatives() {

        this.declaration.apply(new DepthFirstAdapter() {

            private final ParserProduction parserProduction = ParserProduction.this;

            private int nextIndex = 1;

            @Override
            public void inAParserAlternative(
                    AParserAlternative node) {

                ParserAlternative alternative = ParserAlternative
                        .newParserAlternative(node,
                                ParserProduction.this.grammar,
                                this.parserProduction, this.nextIndex);

                this.nextIndex += 1;

                this.parserProduction.alternatives.add(alternative);

            }
        });

    }

    private static class LocalNamespace
            extends ImplicitExplicitNamespace<ParserAlternative> {

        public LocalNamespace(
                final LinkedList<ParserAlternative> declarations) {

            super(declarations);
        }

        @Override
        protected void raiseDuplicateError(
                ParserAlternative declaration,
                ParserAlternative previousDeclaration) {

            throw SemanticException.duplicateAlternativeName(declaration,
                    previousDeclaration);

        }

    }
}
