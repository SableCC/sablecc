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

public abstract class Transformation {

    public static class AlternativeTransformation
            extends Transformation {

        private AAlternativeTransformation declaration;

        private Grammar grammar;

        private List<AlternativeElement> transformationElements = new LinkedList<AlternativeElement>();

        public AlternativeTransformation(
                AAlternativeTransformation declaration,
                Grammar grammar) {

            if (declaration == null) {
                throw new InternalException(
                        "alternativeTransformation may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
            this.declaration = declaration;

            for (PTransformationElement element : declaration
                    .getTransformationElements()) {
                this.transformationElements.add(newElement(element, grammar));
            }
        }

        private static AlternativeElement newElement(
                PTransformationElement declaration,
                final Grammar grammar) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            class TransformationBuilder
                    extends DepthFirstAdapter {

                private AlternativeElement alternativeElement;

                private AlternativeListElement listElement;

                private void visit(
                        Node node) {

                    if (node != null) {
                        node.apply(this);
                    }
                }

                public AlternativeElement getTransformationElement(
                        PTransformationElement node) {

                    this.alternativeElement = null;
                    visit(node);
                    AlternativeElement element = this.alternativeElement;
                    this.alternativeElement = null;
                    return element;
                }

                public AlternativeListElement getListElement(
                        PListElement node) {

                    this.alternativeElement = null;
                    visit(node);
                    AlternativeListElement element = this.listElement;
                    this.alternativeElement = null;
                    return element;
                }

                @Override
                public void caseANullTransformationElement(
                        ANullTransformationElement node) {

                    this.alternativeElement = new AlternativeElement.NullElement(
                            node, grammar);
                }

                @Override
                public void caseAReferenceTransformationElement(
                        AReferenceTransformationElement node) {

                    this.alternativeElement = new AlternativeElement.ReferenceElement(
                            node, grammar);
                }

                @Override
                public void caseANewTransformationElement(
                        ANewTransformationElement node) {

                    LinkedList<AlternativeElement> listElements = new LinkedList<AlternativeElement>();

                    for (PTransformationElement element : node
                            .getTransformationElements()) {
                        listElements.add(getTransformationElement(element));
                    }

                    this.alternativeElement = new AlternativeElement.NewElement(
                            node, grammar, listElements);
                }

                @Override
                public void caseAListTransformationElement(
                        AListTransformationElement node) {

                    LinkedList<AlternativeListElement> listElements = new LinkedList<AlternativeListElement>();

                    for (PListElement element : node.getListElements()) {
                        listElements.add(getListElement(element));
                    }

                    this.alternativeElement = new AlternativeElement.ListElement(
                            node, grammar, listElements);
                }

                @Override
                public void caseAReferenceListElement(
                        AReferenceListElement node) {

                    this.listElement = new AlternativeListElement.ReferenceElement(
                            node, grammar);

                }

                @Override
                public void caseAListReferenceListElement(
                        AListReferenceListElement node) {

                    this.listElement = new AlternativeListElement.NormalListElement(
                            node, grammar);
                }

                @Override
                public void caseALeftListReferenceListElement(
                        ALeftListReferenceListElement node) {

                    this.listElement = new AlternativeListElement.LeftListElement(
                            node, grammar);
                }

                @Override
                public void caseARightListReferenceListElement(
                        ARightListReferenceListElement node) {

                    this.listElement = new AlternativeListElement.RightListElement(
                            node, grammar);
                }

                @Override
                public void caseANewListElement(
                        ANewListElement node) {

                    LinkedList<AlternativeElement> listElements = new LinkedList<AlternativeElement>();

                    for (PTransformationElement element : node
                            .getTransformationElements()) {
                        listElements.add(getTransformationElement(element));
                    }

                    this.listElement = new AlternativeListElement.NewElement(
                            node, grammar, listElements);
                }

            }

            return new TransformationBuilder()
                    .getTransformationElement(declaration);
        }

        public abstract static class AlternativeElement {

            private final Grammar grammar;

            public AlternativeElement(
                    Grammar grammar) {

                if (grammar == null) {
                    throw new InternalException("grammar may not be null");
                }

                this.grammar = grammar;
            }

            public Grammar getGrammar() {

                return this.grammar;
            }

            public static class NullElement
                    extends AlternativeElement {

                private final ANullTransformationElement declaration;

                public NullElement(
                        ANullTransformationElement declaration,
                        Grammar grammar) {

                    super(grammar);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;
                }
            }

            public static class NewElement
                    extends AlternativeElement {

                private final ANewTransformationElement declaration;

                private final List<AlternativeElement> transformationElements;

                public NewElement(
                        ANewTransformationElement declaration,
                        Grammar grammar,
                        List<AlternativeElement> transformationElements) {

                    super(grammar);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    if (transformationElements == null) {
                        throw new InternalException(
                                "transformationElements may not be null");
                    }

                    this.declaration = declaration;
                    this.transformationElements = transformationElements;
                }

                public List<AlternativeElement> getTransformationElement() {

                    return this.transformationElements;
                }

            }

            public static class ReferenceElement
                    extends AlternativeElement {

                private final AReferenceTransformationElement declaration;

                public ReferenceElement(
                        AReferenceTransformationElement declaration,
                        Grammar grammar) {

                    super(grammar);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;
                }
            }

            public static class ListElement
                    extends AlternativeElement {

                private final AListTransformationElement declaration;

                private final List<AlternativeListElement> listElements;

                public ListElement(
                        AListTransformationElement declaration,
                        Grammar grammar,
                        List<AlternativeListElement> listElements) {

                    super(grammar);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    if (listElements == null) {
                        throw new InternalException(
                                "listElements may not be null");
                    }

                    this.declaration = declaration;
                    this.listElements = listElements;

                }

                public List<AlternativeListElement> getListElements() {

                    return this.listElements;
                }
            }

        }

        public abstract static class AlternativeListElement {

            private final Grammar grammar;

            public AlternativeListElement(
                    Grammar grammar) {

                if (grammar == null) {
                    throw new InternalException("grammar may not be null");
                }

                this.grammar = grammar;
            }

            public static class ReferenceElement
                    extends AlternativeListElement {

                AReferenceListElement declaration;

                public ReferenceElement(
                        AReferenceListElement declaration,
                        Grammar grammar) {

                    super(grammar);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;
                }
            }

            public static class NewElement
                    extends AlternativeListElement {

                private final ANewListElement declaration;

                private final List<AlternativeElement> transformationElements;

                public NewElement(
                        ANewListElement declaration,
                        Grammar grammar,
                        List<AlternativeElement> transformationElements) {

                    super(grammar);

                    if (transformationElements == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    if (declaration == null) {
                        throw new InternalException(
                                "transformationElements may not be null");
                    }

                    this.declaration = declaration;
                    this.transformationElements = transformationElements;

                    System.err.println(declaration);

                }

                public List<AlternativeElement> getTransformationElement() {

                    return this.transformationElements;
                }
            }

            public static class NormalListElement
                    extends AlternativeListElement {

                AListReferenceListElement declaration;

                public NormalListElement(
                        AListReferenceListElement declaration,
                        Grammar grammar) {

                    super(grammar);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;
                }
            }

            public static class LeftListElement
                    extends AlternativeListElement {

                ALeftListReferenceListElement declaration;

                public LeftListElement(
                        ALeftListReferenceListElement declaration,
                        Grammar grammar) {

                    super(grammar);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;
                }
            }

            public static class RightListElement
                    extends AlternativeListElement {

                ARightListReferenceListElement declaration;

                public RightListElement(
                        ARightListReferenceListElement declaration,
                        Grammar grammar) {

                    super(grammar);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;
                }
            }

        }
    }

    public static class ProductionTransformation
            extends Transformation {

        private final AProductionTransformation declaration;

        /*private ParserProduction parserProduction;*/

        /*private TreeProduction treeProduction;*/

        private final Grammar grammar;

        private List<ProductionElement> elements = new LinkedList<ProductionElement>();

        public ProductionTransformation(
                AProductionTransformation declaration,
                Grammar grammar) {

            if (declaration == null) {
                throw new InternalException("grammar may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.declaration = declaration;
            this.grammar = grammar;

            findElements();
        }

        private void findElements() {

            this.declaration.apply(new DepthFirstAdapter() {

                @Override
                public void inANormalElement(
                        ANormalElement node) {

                    ProductionTransformation.this.elements
                            .add(new ProductionElement.NormalElement(
                                    ProductionTransformation.this.grammar,
                                    ProductionTransformation.this, node));
                };

                @Override
                public void inASeparatedElement(
                        ASeparatedElement node) {

                    ProductionTransformation.this.elements
                            .add(new ProductionElement.SeparatedElement(
                                    ProductionTransformation.this.grammar,
                                    ProductionTransformation.this, node));

                };

                @Override
                public void inAAlternatedElement(
                        AAlternatedElement node) {

                    ProductionTransformation.this.elements
                            .add(new ProductionElement.AlternatedElement(
                                    ProductionTransformation.this.grammar,
                                    ProductionTransformation.this, node));

                };
            });
        }

        public abstract static class ProductionElement {

            private final Grammar grammar;

            private final ProductionTransformation productionTransformation;

            public ProductionElement(
                    Grammar grammar,
                    ProductionTransformation productionTransformation) {

                if (grammar == null) {
                    throw new InternalException("grammar may not be null");
                }

                if (productionTransformation == null) {
                    throw new InternalException(
                            "alternativeTransformation may not be null");
                }

                this.grammar = grammar;
                this.productionTransformation = productionTransformation;
            }

            public static class NormalElement
                    extends ProductionElement {

                private ANormalElement declaration;

                public NormalElement(
                        Grammar grammar,
                        ProductionTransformation productionTransformation,
                        ANormalElement declaration) {

                    super(grammar, productionTransformation);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;

                }

            }

            public static class SeparatedElement
                    extends ProductionElement {

                private ASeparatedElement declaration;

                public SeparatedElement(
                        Grammar grammar,
                        ProductionTransformation productionTransformation,
                        ASeparatedElement declaration) {

                    super(grammar, productionTransformation);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;
                }

            }

            public static class AlternatedElement
                    extends ProductionElement {

                private AAlternatedElement declaration;

                public AlternatedElement(
                        Grammar grammar,
                        ProductionTransformation productionTransformation,
                        AAlternatedElement declaration) {

                    super(grammar, productionTransformation);

                    if (declaration == null) {
                        throw new InternalException(
                                "declaration may not be null");
                    }

                    this.declaration = declaration;
                }

            }

        }
    }

}
