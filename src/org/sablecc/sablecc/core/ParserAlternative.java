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
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;
import org.sablecc.util.interfaces.*;

public abstract class ParserAlternative
        implements ImplicitExplicit {

    private final AParserAlternative declaration;

    private final Grammar grammar;

    private final ParserProduction production;

    private final LocalNamespace namespace;

    private int index;

    private final LinkedList<ParserElement> elements = new LinkedList<ParserElement>();

    public ParserAlternative(
            AParserAlternative declaration,
            Grammar grammar,
            ParserProduction production,
            int index) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        this.declaration = declaration;
        this.grammar = grammar;
        this.production = production;
        this.index = index;

        findAlternatives();

        this.namespace = new LocalNamespace(this.elements);
    }

    static ParserAlternative newParserAlternative(
            AParserAlternative declaration,
            Grammar grammar,
            ParserProduction production,
            int index) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        if (declaration.getDanglingElement() == null) {
            return new NormalAlternative(declaration, grammar, production,
                    index);
        }
        else {
            return new DanglingAlternative(declaration, grammar, production,
                    index);
        }
    }

    public ParserProduction getProduction() {

        return this.production;
    }

    public int getIndex() {

        return this.index;
    }

    public AParserAlternative getDeclaration() {

        return this.declaration;
    }

    public abstract String getName();

    public abstract Token getNameToken();

    public static class NormalAlternative
            extends ParserAlternative {

        private String name;

        private Token token;

        public NormalAlternative(
                AParserAlternative declaration,
                Grammar grammar,
                ParserProduction production,
                int index) {

            super(declaration, grammar, production, index);
        }

        @Override
        public String getImplicitName() {

            String implicitName = null;

            if (getDeclaration().getElements().getFirst() instanceof ANormalElement) {
                ANormalElement firstElement = (ANormalElement) getDeclaration()
                        .getElements().getFirst();

                if (firstElement.getElementName() != null) {
                    implicitName = firstElement.getElementName().getText();
                    implicitName = implicitName.substring(1,
                            implicitName.length() - 2);
                }
                else if (firstElement.getUnit() instanceof ANameUnit
                        && (firstElement.getUnaryOperator() == null || firstElement
                                .getUnaryOperator() instanceof AZeroOrOneUnaryOperator)) {
                    implicitName = ((ANameUnit) firstElement.getUnit())
                            .getIdentifier().getText();
                }

            }
            else if (getDeclaration().getElements().getFirst() instanceof ASeparatedElement) {
                ASeparatedElement firstElement = (ASeparatedElement) getDeclaration()
                        .getElements().getFirst();

                if (firstElement.getElementName() != null) {
                    implicitName = firstElement.getElementName().getText();
                    implicitName = implicitName.substring(1,
                            implicitName.length() - 2);
                }// else : separated element can't have an implicit name
            }
            else if (getDeclaration().getElements().getFirst() instanceof AAlternatedElement) {
                AAlternatedElement firstElement = (AAlternatedElement) getDeclaration()
                        .getElements().getFirst();

                if (firstElement.getElementName() != null) {
                    implicitName = firstElement.getElementName().getText();
                    implicitName = implicitName.substring(1,
                            implicitName.length() - 2);
                }// else : alternated element can't have an implicit name
            }
            else {
                throw new InternalException("Unhandled case");
            }

            return implicitName;
        }

        @Override
        public String getExplicitName() {

            String explicitName = null;

            if (getDeclaration().getAlternativeName() != null) {
                explicitName = getDeclaration().getAlternativeName().getText();
                explicitName = explicitName.substring(1,
                        explicitName.length() - 2);
                return explicitName;
            }

            return explicitName;

        }

        @Override
        public void setName(
                String name) {

            this.name = name;

        }

        @Override
        public String getName() {

            // TODO Null ok ?
            return this.name;
        }

        @Override
        public Token getNameToken() {

            if (this.token == null) {
                if (getExplicitName() != null
                        && getExplicitName().equals(this.name)) {
                    this.token = getDeclaration().getAlternativeName();
                }
                else if (getImplicitName().equals(this.name)) {
                    ANormalElement firstElement = (ANormalElement) getDeclaration()
                            .getElements().getFirst();

                    this.token = ((ANameUnit) firstElement.getUnit())
                            .getIdentifier();
                }
            }

            return this.token;
        }

    }

    public static class DanglingAlternative
            extends ParserAlternative {

        private String name;

        private Token token;

        public DanglingAlternative(
                AParserAlternative declaration,
                Grammar grammar,
                ParserProduction production,
                int index) {

            super(declaration, grammar, production, index);
        }

        @Override
        public String getImplicitName() {

            String implicitName = null;

            if (getDeclaration().getElements().getFirst() instanceof ANormalElement) {
                ANormalElement firstElement = (ANormalElement) getDeclaration()
                        .getElements().getFirst();

                if (firstElement.getElementName() != null) {
                    implicitName = firstElement.getElementName().getText();
                    implicitName = implicitName.substring(1,
                            implicitName.length() - 2);
                }
                else if (firstElement.getUnit() instanceof ANameUnit
                        && (firstElement.getUnaryOperator() == null || firstElement
                                .getUnaryOperator() instanceof AZeroOrOneUnaryOperator)) {
                    implicitName = ((ANameUnit) firstElement.getUnit())
                            .getIdentifier().getText();
                }

            }
            else if (getDeclaration().getElements().getFirst() instanceof ASeparatedElement) {
                ASeparatedElement firstElement = (ASeparatedElement) getDeclaration()
                        .getElements().getFirst();

                if (firstElement.getElementName() != null) {
                    implicitName = firstElement.getElementName().getText();
                    implicitName = implicitName.substring(1,
                            implicitName.length() - 2);
                }// else : separated element can't have an implicit name
            }
            else if (getDeclaration().getElements().getFirst() instanceof AAlternatedElement) {
                AAlternatedElement firstElement = (AAlternatedElement) getDeclaration()
                        .getElements().getFirst();

                if (firstElement.getElementName() != null) {
                    implicitName = firstElement.getElementName().getText();
                    implicitName = implicitName.substring(1,
                            implicitName.length() - 2);
                }// else : alternated element can't have an implicit name
            }
            else {
                throw new InternalException("Unhandled case");
            }

            return implicitName;
        }

        @Override
        public String getExplicitName() {

            String explicitName = null;

            if (getDeclaration().getAlternativeName() != null) {
                explicitName = getDeclaration().getAlternativeName().getText();
                explicitName = explicitName.substring(1,
                        explicitName.length() - 2);
                return explicitName;
            }

            return explicitName;

        }

        @Override
        public void setName(
                String name) {

            this.name = name;

        }

        @Override
        public String getName() {

            // TODO Null ok ?
            return this.name;
        }

        @Override
        public Token getNameToken() {

            if (this.token == null) {
                if (getExplicitName() != null
                        && getExplicitName().equals(this.name)) {
                    this.token = getDeclaration().getAlternativeName();
                }
                else if (getImplicitName().equals(this.name)) {
                    ANormalElement firstElement = (ANormalElement) getDeclaration()
                            .getElements().getFirst();

                    this.token = ((ANameUnit) firstElement.getUnit())
                            .getIdentifier();
                }
            }

            return this.token;
        }

    }

    private void findAlternatives() {

        this.declaration.apply(new DepthFirstAdapter() {

            private final ParserAlternative parserAlternative = ParserAlternative.this;

            @Override
            public void inANormalElement(
                    ANormalElement node) {

                ParserElement.NormalElement normalElement = new ParserElement.NormalElement(
                        node, ParserAlternative.this.grammar,
                        this.parserAlternative);

                this.parserAlternative.elements.add(normalElement);

            }

            @Override
            public void inASeparatedElement(
                    ASeparatedElement node) {

                ParserElement.SeparatedElement separatedElement = new ParserElement.SeparatedElement(
                        node, ParserAlternative.this.grammar,
                        this.parserAlternative);

                this.parserAlternative.elements.add(separatedElement);
            }

            @Override
            public void inAAlternatedElement(
                    AAlternatedElement node) {

                ParserElement.AlternatedElement alternatedElement = new ParserElement.AlternatedElement(
                        node, ParserAlternative.this.grammar,
                        this.parserAlternative);

                this.parserAlternative.elements.add(alternatedElement);
            }

            @Override
            public void inADanglingElement(
                    ADanglingElement node) {

                ParserElement.DanglingElement danglingElement = new ParserElement.DanglingElement(
                        node, ParserAlternative.this.grammar,
                        this.parserAlternative);

                this.parserAlternative.elements.add(danglingElement);
            }

        });

    }

    private static class LocalNamespace
            extends ImplicitExplicitNamespace<ParserElement> {

        public LocalNamespace(
                final LinkedList<ParserElement> declarations) {

            super(declarations);
        }

        @Override
        protected void raiseDuplicateError(
                ParserElement declaration,
                ParserElement previousDeclaration) {

            throw SemanticException.duplicateElementName(declaration,
                    previousDeclaration);

        }

    }
}
