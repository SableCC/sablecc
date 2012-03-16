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

package org.sablecc.sablecc.core.transformation;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public abstract class ProductionTransformation
        implements INameDeclaration, IVisitableGrammarPart {

    private final Grammar grammar;

    private Type.CompositeType type;

    public ProductionTransformation(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    public abstract List<ProductionTransformationElement> getElements();

    public abstract IReferencable getProductionReference();

    public Grammar getGrammar() {

        return this.grammar;
    }

    @Override
    public String getNameType() {

        return "production transformation";
    }

    public Type.CompositeType getType() {

        if (this.type == null) {
            LinkedList<Type> elementsType = new LinkedList<Type>();
            for (ProductionTransformationElement element : getElements()) {
                elementsType.add(element.getType());
            }

            this.type = new Type.CompositeType(elementsType);
        }

        return this.type;
    }

    public abstract Token getLocation();

    public static class ExplicitProductionTransformation
            extends ProductionTransformation {

        private final AProductionTransformation declaration;

        private final List<ProductionTransformationElement> elements = new LinkedList<ProductionTransformationElement>();

        private final LocalNamespace namespace;

        private IReferencable reference;

        public ExplicitProductionTransformation(
                AProductionTransformation declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("grammar may not be null");
            }

            this.declaration = declaration;

            findElements();

            this.namespace = new LocalNamespace(
                    (LinkedList<ProductionTransformationElement>) this.elements);
        }

        public AProductionTransformation getDeclaration() {

            return this.declaration;
        }

        @Override
        public IReferencable getProductionReference() {

            if (this.reference == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }
            return this.reference;

        }

        public void addReference(
                IReferencable reference) {

            if (this.reference == null) {
                this.reference = reference;
            }
            else {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }
        }

        public ProductionTransformationElement getLocalReference(
                String reference) {

            return this.namespace.get(reference);
        }

        @Override
        public List<ProductionTransformationElement> getElements() {

            return this.elements;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitProductionTransformation(this);

        }

        private void findElements() {

            this.declaration.apply(new DepthFirstAdapter() {

                Grammar grammar = ExplicitProductionTransformation.this
                        .getGrammar();

                ExplicitProductionTransformation productionTransformation = ExplicitProductionTransformation.this;

                @Override
                public void inANormalElement(
                        ANormalElement node) {

                    ExplicitProductionTransformation.this.elements
                            .add(new ProductionTransformationElement.ExplicitSingleElement(
                                    this.grammar,
                                    this.productionTransformation, node));
                };

                @Override
                public void inASeparatedElement(
                        ASeparatedElement node) {

                    this.productionTransformation.elements
                            .add(new ProductionTransformationElement.DoubleElement(
                                    this.grammar,
                                    this.productionTransformation, node));

                };

                @Override
                public void inAAlternatedElement(
                        AAlternatedElement node) {

                    this.productionTransformation.elements
                            .add(new ProductionTransformationElement.DoubleElement(
                                    this.grammar,
                                    this.productionTransformation, node));

                };
            });
        }

        private static class LocalNamespace
                extends
                ImplicitExplicitNamespace<ProductionTransformationElement> {

            public LocalNamespace(
                    final LinkedList<ProductionTransformationElement> declarations) {

                super(declarations);
            }

            @Override
            protected void raiseDuplicateError(
                    ProductionTransformationElement declaration,
                    ProductionTransformationElement previousDeclaration) {

                throw SemanticException
                        .duplicateProductionTransformationElementName(
                                declaration, previousDeclaration);

            }

        }

        @Override
        public TIdentifier getNameIdentifier() {

            return this.declaration.getProduction();
        }

        @Override
        public String getName() {

            return getNameIdentifier().getText();
        }

        @Override
        public Token getLocation() {

            return this.declaration.getProduction();
        }
    }

    public static class ImplicitProductionTransformation
            extends ProductionTransformation {

        private final List<ProductionTransformationElement> elements = new LinkedList<ProductionTransformationElement>();

        private final IReferencable reference;

        public ImplicitProductionTransformation(
                Grammar grammar,
                Parser.ParserProduction parserProduction,
                Tree.TreeProduction treeProduction) {

            super(grammar);

            this.reference = parserProduction;

            this.elements
                    .add(new ProductionTransformationElement.ImplicitSingleElement(
                            grammar, this, treeProduction));
        }

        public ImplicitProductionTransformation(
                Grammar grammar,
                Parser.ParserProduction parserProduction) {

            super(grammar);

            this.reference = parserProduction;
        }

        @Override
        public IReferencable getProductionReference() {

            return this.reference;
        }

        @Override
        public List<ProductionTransformationElement> getElements() {

            return this.elements;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitProductionTransformation(this);

        }

        @Override
        public TIdentifier getNameIdentifier() {

            return null;
        }

        @Override
        public String getName() {

            // TODO Handle selector case
            if (this.reference instanceof Parser.ParserProduction) {
                return ((Parser.ParserProduction) this.reference).getName();
            }

            return null;
        }

        @Override
        public Token getLocation() {

            return getElements().get(0).getLocation();
        }
    }
}
