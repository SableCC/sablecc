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

public abstract class AlternativeTransformation
        implements IVisitableGrammarPart {

    private final Grammar grammar;

    private Type.CompositeType type;

    public AlternativeTransformation(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    public Grammar getGrammar() {

        return this.grammar;
    }

    public Type.CompositeType getType() {

        if (this.type == null) {
            LinkedList<Type> elementsType = new LinkedList<Type>();
            for (AlternativeTransformationElement element : getTransformationElements()) {
                elementsType.add(element.getType());
            }

            this.type = new Type.CompositeType(elementsType);
        }

        return this.type;
    }

    public abstract List<AlternativeTransformationElement> getTransformationElements();

    public abstract Parser.ParserAlternative getAlternativeReference();

    public static class ImplicitAlternativeTransformation
            extends AlternativeTransformation {

        private final Parser.ParserAlternative alternative;

        private final List<AlternativeTransformationElement> transformationElements;

        public ImplicitAlternativeTransformation(
                Parser.ParserAlternative alternative,
                AlternativeTransformationElement.NewElement newElement,
                Grammar grammar) {

            super(grammar);

            if (alternative == null) {
                throw new InternalException("alternative may not be null");
            }

            if (newElement == null) {
                throw new InternalException("newElement may not be null");
            }

            this.alternative = alternative;
            this.transformationElements = new LinkedList<AlternativeTransformationElement>();
            this.transformationElements.add(newElement);
        }

        public ImplicitAlternativeTransformation(
                Parser.ParserAlternative alternative,
                AlternativeTransformationElement.ImplicitNullElement nullElement,
                Grammar grammar) {

            super(grammar);

            if (alternative == null) {
                throw new InternalException("alternative may not be null");
            }

            if (nullElement == null) {
                throw new InternalException("newElement may not be null");
            }

            this.alternative = alternative;
            this.transformationElements = new LinkedList<AlternativeTransformationElement>();
            this.transformationElements.add(nullElement);
        }

        @Override
        public Parser.ParserAlternative getAlternativeReference() {

            return this.alternative;
        }

        @Override
        public List<AlternativeTransformationElement> getTransformationElements() {

            return this.transformationElements;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformation(this);
        }

    }

    public static class ExplicitAlternativeTransformation
            extends AlternativeTransformation {

        private final AAlternativeTransformation declaration;

        private TIdentifier productionNameIdentifier;

        private TIdentifier alternativeNameIdentifier;

        private String name;

        private Parser.ParserAlternative alternative;

        private final List<AlternativeTransformationElement> transformationElements = new LinkedList<AlternativeTransformationElement>();

        public ExplicitAlternativeTransformation(
                AAlternativeTransformation declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException(
                        "alternativeTransformation may not be null");
            }

            this.declaration = declaration;

            for (PTransformationElement element : declaration
                    .getTransformationElements()) {
                this.transformationElements.add(newElement(element, grammar));
            }
        }

        public AAlternativeTransformation getDeclaration() {

            return this.declaration;
        }

        @Override
        public Parser.ParserAlternative getAlternativeReference() {

            if (this.alternative == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }
            return this.alternative;
        }

        public void addAlternativeReference(
                Parser.ParserAlternative alternative) {

            if (this.alternative == null) {
                this.alternative = alternative;
            }
            else {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }
        }

        public TIdentifier getProductionNameIdentifier() {

            if (this.productionNameIdentifier == null) {

                PAlternativeReference alternativeReference = this.declaration
                        .getAlternativeReference();

                if (alternativeReference instanceof ANamedAlternativeReference) {
                    this.productionNameIdentifier = ((ANamedAlternativeReference) alternativeReference)
                            .getProduction();
                }
                else {
                    this.productionNameIdentifier = ((AUnnamedAlternativeReference) alternativeReference)
                            .getProduction();
                }
            }
            return this.productionNameIdentifier;
        }

        public TIdentifier getAlternativeNameIdentifier() {

            if (this.alternativeNameIdentifier == null) {

                PAlternativeReference alternativeReference = this.declaration
                        .getAlternativeReference();

                if (alternativeReference instanceof ANamedAlternativeReference) {
                    this.alternativeNameIdentifier = ((ANamedAlternativeReference) alternativeReference)
                            .getAlternative();
                }

            }
            return this.alternativeNameIdentifier;
        }

        public String getName() {

            if (this.name == null) {
                this.name = getProductionNameIdentifier().getText();

                if (getAlternativeNameIdentifier() != null) {
                    this.name = this.name + "."
                            + getAlternativeNameIdentifier().getText();
                }
            }
            return this.name;
        }

        public String getNameType() {

            return "alternative transformation";
        }

        public Token getLocation() {

            return getProductionNameIdentifier();
        }

        @Override
        public List<AlternativeTransformationElement> getTransformationElements() {

            return this.transformationElements;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformation(this);

        }

        private static AlternativeTransformationElement newElement(
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

                private AlternativeTransformationElement alternativeElement;

                private AlternativeTransformationListElement listElement;

                private void visit(
                        Node node) {

                    if (node != null) {
                        node.apply(this);
                    }
                }

                public AlternativeTransformationElement getTransformationElement(
                        PTransformationElement node) {

                    this.alternativeElement = null;
                    visit(node);
                    AlternativeTransformationElement element = this.alternativeElement;
                    this.alternativeElement = null;
                    return element;
                }

                public AlternativeTransformationListElement getListElement(
                        PListElement node) {

                    this.alternativeElement = null;
                    visit(node);
                    AlternativeTransformationListElement element = this.listElement;
                    this.alternativeElement = null;
                    return element;
                }

                @Override
                public void caseANullTransformationElement(
                        ANullTransformationElement node) {

                    this.alternativeElement = new AlternativeTransformationElement.ExplicitNullElement(
                            node, grammar);
                }

                @Override
                public void caseAReferenceTransformationElement(
                        AReferenceTransformationElement node) {

                    this.alternativeElement = new AlternativeTransformationElement.ExplicitReferenceElement(
                            node, grammar);
                }

                @Override
                public void caseANewTransformationElement(
                        ANewTransformationElement node) {

                    LinkedList<AlternativeTransformationElement> listElements = new LinkedList<AlternativeTransformationElement>();

                    for (PTransformationElement element : node
                            .getTransformationElements()) {
                        listElements.add(getTransformationElement(element));
                    }

                    this.alternativeElement = new AlternativeTransformationElement.ExplicitNewElement(
                            node, grammar, listElements);
                }

                @Override
                public void caseAListTransformationElement(
                        AListTransformationElement node) {

                    LinkedList<AlternativeTransformationListElement> listElements = new LinkedList<AlternativeTransformationListElement>();

                    for (PListElement element : node.getListElements()) {
                        listElements.add(getListElement(element));
                    }

                    this.alternativeElement = new AlternativeTransformationElement.ExplicitListElement(
                            node, grammar, listElements);
                }

                @Override
                public void caseAReferenceListElement(
                        AReferenceListElement node) {

                    this.listElement = new AlternativeTransformationListElement.ExplicitReferenceElement(
                            node, grammar);

                }

                @Override
                public void caseAListReferenceListElement(
                        AListReferenceListElement node) {

                    this.listElement = new AlternativeTransformationListElement.ExplicitNormalListElement(
                            node, grammar);
                }

                @Override
                public void caseALeftListReferenceListElement(
                        ALeftListReferenceListElement node) {

                    this.listElement = new AlternativeTransformationListElement.ExplicitLeftListElement(
                            node, grammar);
                }

                @Override
                public void caseARightListReferenceListElement(
                        ARightListReferenceListElement node) {

                    this.listElement = new AlternativeTransformationListElement.ExplicitRightListElement(
                            node, grammar);
                }

                @Override
                public void caseANewListElement(
                        ANewListElement node) {

                    LinkedList<AlternativeTransformationElement> listElements = new LinkedList<AlternativeTransformationElement>();

                    for (PTransformationElement element : node
                            .getTransformationElements()) {
                        listElements.add(getTransformationElement(element));
                    }

                    this.listElement = new AlternativeTransformationListElement.ExplicitNewElement(
                            node, grammar, listElements);
                }

            }

            return new TransformationBuilder()
                    .getTransformationElement(declaration);
        }

    }

}
