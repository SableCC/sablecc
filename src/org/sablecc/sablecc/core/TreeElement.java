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
import org.sablecc.sablecc.syntax3.node.*;

public abstract class TreeElement {

    private final Grammar grammar;

    private final TreeAlternative alternative;

    private TreeElement(
            Grammar grammar,
            TreeAlternative alternative) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (alternative == null) {
            throw new InternalException("alternative may not be null");
        }

        this.grammar = grammar;
        this.alternative = alternative;
    }

    public TreeAlternative getAlternative() {

        return this.alternative;
    }

    public abstract static class Anonymous
            extends TreeElement {

        public Anonymous(
                Grammar grammar,
                TreeAlternative alternative) {

            super(grammar, alternative);
        }

        public static class Normal
                extends Anonymous {

            private ANormalElement declaration;

            public Normal(
                    ANormalElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;
            }
        }

        public static class Separated
                extends Anonymous {

            private ASeparatedElement declaration;

            public Separated(
                    ASeparatedElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;
            }
        }

        public static class Alternated
                extends Anonymous {

            private AAlternatedElement declaration;

            public Alternated(
                    AAlternatedElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;
            }
        }

    }

    public abstract static class Named
            extends TreeElement {

        public abstract String getName();

        public abstract Token getNameToken();

        public Named(
                Grammar grammar,
                TreeAlternative alternative) {

            super(grammar, alternative);
        }

        public static abstract class Normal
                extends Named {

            private ANormalElement declaration;

            public Normal(
                    ANormalElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;
            }

            public ANormalElement getDeclaration() {

                return this.declaration;
            }

        }

        public static class ImplicitNormal
                extends Normal {

            private String name;

            private TIdentifier token;

            public ImplicitNormal(
                    ANormalElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(declaration, grammar, alternative);
            }

            @Override
            public String getName() {

                return getNameToken().getText();
            }

            @Override
            public TIdentifier getNameToken() {

                if (this.token == null) {
                    if (!(getDeclaration().getUnit() instanceof ANameUnit)) {
                        throw new InternalException("unit may not be a "
                                + getDeclaration().getUnit().getClass());
                    }
                    this.token = ((ANameUnit) getDeclaration().getUnit())
                            .getIdentifier();
                }

                return this.token;

            }

        }

        public static class ExplicitNormal
                extends Normal {

            private String name;

            public ExplicitNormal(
                    ANormalElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(declaration, grammar, alternative);
            }

            @Override
            public TElementName getNameToken() {

                return getDeclaration().getElementName();
            }

            @Override
            public String getName() {

                if (this.name == null) {
                    String name = getNameToken().getText();
                    name = name.substring(1, name.length() - 2);
                    this.name = name.trim();
                }

                return this.name;
            }
        }

        public static class Separated
                extends Named {

            private ASeparatedElement declaration;

            private String name;

            public Separated(
                    ASeparatedElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;
            }

            @Override
            public TElementName getNameToken() {

                return this.declaration.getElementName();
            }

            @Override
            public String getName() {

                if (this.name == null) {
                    String name = getNameToken().getText();
                    name = name.substring(1, name.length() - 2);
                    this.name = name;
                }

                return this.name;
            }
        }

        public static class Alternated
                extends Named {

            private AAlternatedElement declaration;

            private String name;

            public Alternated(
                    AAlternatedElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;

            }

            @Override
            public TElementName getNameToken() {

                return this.declaration.getElementName();
            }

            @Override
            public String getName() {

                if (this.name == null) {
                    String name = getNameToken().getText();
                    name = name.substring(1, name.length() - 2);
                    this.name = name;
                }

                return this.name;
            }
        }
    }
}
