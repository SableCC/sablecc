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

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.Parser.ParserElement.ElementType;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public abstract class AlternativeTransformationListElement
        implements IVisitableGrammarPart {

    private final Grammar grammar;

    public AlternativeTransformationListElement(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    public Grammar getGrammar() {

        return this.grammar;
    }

    public abstract Type.SimpleType getType();

    public abstract void constructType();

    public abstract String getElement();

    public abstract IReferencable getTargetReference();

    public abstract Token getLocation();

    public static abstract class ReferenceElement
            extends AlternativeTransformationListElement {

        private Type.SimpleType type;

        private IReferencable targetReference;

        private Parser.ParserElement originReference;

        public ReferenceElement(
                Grammar grammar) {

            super(grammar);
        }

        @Override
        public Type.SimpleType getType() {

            if (this.type == null) {
                throw new InternalException(
                        "type shouldn't be null, use constructType()");
            }

            return this.type;
        }

        @Override
        public IReferencable getTargetReference() {

            return this.targetReference;
        }

        protected void addTargetReference(
                IReferencable reference) {

            this.targetReference = reference;
        }

        public Parser.ParserElement getOriginReference() {

            return this.originReference;
        }

        protected void addOriginReference(
                Parser.ParserElement reference) {

            this.originReference = reference;
        }

        @Override
        public void constructType() {

            if (this.type != null) {
                throw new InternalException(
                        "constructType shouldn't be used twice");
            }

            if (this.targetReference == null) {
                throw new InternalException(
                        "constructType shouldn't be called before reference resolution");
            }

            if (this.targetReference instanceof Parser.ParserElement) {
                this.type = ((Parser.ParserElement) this.targetReference)
                        .getType();
            }
            else {
                this.type = ((ProductionTransformationElement) this.targetReference)
                        .getType();
            }

        }

        public boolean isTransformed() {

            return this.targetReference instanceof ProductionTransformationElement;
        }

    }

    public static class ImplicitReferenceElement
            extends AlternativeTransformationListElement.ReferenceElement {

        private String element;

        public ImplicitReferenceElement(
                Parser.ParserElement reference,
                Grammar grammar) {

            super(grammar);

            if (reference == null) {
                throw new InternalException("referenceText may not be null");
            }

            super.addOriginReference(reference);
            super.addTargetReference(reference);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationReferenceListElement(this);

        }

        @Override
        public String getElement() {

            if (getTargetReference() instanceof INameDeclaration) {
                this.element = ((INameDeclaration) getTargetReference())
                        .getName();
            }
            else if (getTargetReference() instanceof Parser.ParserAlternative) {
                this.element = ((Parser.ParserAlternative) getTargetReference())
                        .getName();
                if (this.element == null) {
                    this.element = "{"
                            + ((Parser.ParserAlternative) getTargetReference())
                                    .getIndex() + "}";
                }
            }
            else if (getTargetReference() instanceof Tree.TreeAlternative) {
                this.element = ((Tree.TreeAlternative) getTargetReference())
                        .getName();
                if (this.element == null) {
                    this.element = "{"
                            + ((Tree.TreeAlternative) getTargetReference())
                                    .getIndex() + "}";
                }
            }
            else if (getTargetReference() instanceof Parser.ParserElement) {
                this.element = ((Parser.ParserElement) getTargetReference())
                        .getElement();
            }
            else if (getTargetReference() instanceof Tree.TreeElement) {
                this.element = ((Tree.TreeElement) getTargetReference())
                        .getElement();
            }

            return this.element;
        }

        @Override
        public Token getLocation() {

            return getTargetReference().getLocation();
        }
    }

    public static class ExplicitReferenceElement
            extends AlternativeTransformationListElement.ReferenceElement {

        private final AReferenceListElement declaration;

        private String element;

        public ExplicitReferenceElement(
                AReferenceListElement declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        public AReferenceListElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public IReferencable getTargetReference() {

            if (super.getTargetReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getTargetReference();

        }

        @Override
        public void addTargetReference(
                IReferencable reference) {

            if (super.getTargetReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addTargetReference(reference);
        }

        @Override
        public void addOriginReference(
                Parser.ParserElement reference) {

            if (super.getOriginReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addOriginReference(reference);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationReferenceListElement(this);

        }

        @Override
        public String getElement() {

            if (this.element == null) {
                if (this.declaration.getElementReference() instanceof ANaturalElementReference) {
                    ANaturalElementReference naturalRef = (ANaturalElementReference) this.declaration
                            .getElementReference();
                    this.element = naturalRef.getElement().getText();
                }
                else {
                    ATransformedElementReference naturalRef = (ATransformedElementReference) this.declaration
                            .getElementReference();
                    this.element = naturalRef.getElement().getText()
                            + naturalRef.getDot().getText()
                            + naturalRef.getPart().getText();
                }
            }
            return this.element;
        }

        @Override
        public Token getLocation() {

            if (this.declaration.getElementReference() instanceof ANaturalElementReference) {
                return ((ANaturalElementReference) this.declaration
                        .getElementReference()).getElement();
            }
            else {
                return ((ATransformedElementReference) this.declaration
                        .getElementReference()).getElement();
            }
        }
    }

    public static abstract class NewElement
            extends AlternativeTransformationListElement {

        private Type.SimpleType type;

        private Tree.TreeAlternative reference;

        public NewElement(
                Grammar grammar) {

            super(grammar);
        }

        protected void addReference(
                Tree.TreeAlternative reference) {

            this.reference = reference;
        }

        public abstract List<AlternativeTransformationElement> getParameters();

        @Override
        public Tree.TreeAlternative getTargetReference() {

            return this.reference;
        }

        @Override
        public void constructType() {

            if (this.type != null) {
                throw new InternalException(
                        "constructType shouldn't be used twice");
            }

            if (this.reference == null) {
                throw new InternalException(
                        "constructType shouldn't be called before reference resolution");
            }

            this.type = new Type.SimpleType.SeparatedType(this.reference
                    .getProduction().getName(), CardinalityInterval.ONE_ONE);
        }

        @Override
        public Type.SimpleType getType() {

            if (this.type == null) {
                throw new InternalException(
                        "type shouldn't be null, use constructType()");
            }
            return this.type;
        }

    }

    public static class ImplicitNewElement
            extends AlternativeTransformationListElement.NewElement {

        private final List<AlternativeTransformationElement> transformationElements;

        public ImplicitNewElement(
                Tree.TreeAlternative reference,
                Grammar grammar,
                List<AlternativeTransformationElement> transformationElements) {

            super(grammar);

            if (transformationElements == null) {
                throw new InternalException("declaration may not be null");
            }

            if (reference == null) {
                throw new InternalException(
                        "transformationElements may not be null");
            }

            super.addReference(reference);
            this.transformationElements = transformationElements;

        }

        @Override
        public List<AlternativeTransformationElement> getParameters() {

            return this.transformationElements;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationNewListElement(this);

        }

        @Override
        public String getElement() {

            return getTargetReference().getName();
        }

        @Override
        public Token getLocation() {

            return getTargetReference().getLocation();
        }
    }

    public static class ExplicitNewElement
            extends AlternativeTransformationListElement.NewElement {

        private final ANewListElement declaration;

        private final List<AlternativeTransformationElement> transformationElements;

        public ExplicitNewElement(
                ANewListElement declaration,
                Grammar grammar,
                List<AlternativeTransformationElement> transformationElements) {

            super(grammar);

            if (transformationElements == null) {
                throw new InternalException("declaration may not be null");
            }

            if (declaration == null) {
                throw new InternalException(
                        "transformationElements may not be null");
            }

            this.declaration = declaration;
            this.transformationElements = transformationElements;

        }

        public ANewListElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public List<AlternativeTransformationElement> getParameters() {

            return this.transformationElements;
        }

        @Override
        public Tree.TreeAlternative getTargetReference() {

            if (super.getTargetReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getTargetReference();
        }

        @Override
        public void addReference(
                Tree.TreeAlternative reference) {

            if (super.getTargetReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addReference(reference);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationNewListElement(this);

        }

        @Override
        public String getElement() {

            if (this.declaration.getAlternativeReference() instanceof ANamedAlternativeReference) {
                ANamedAlternativeReference namedRef = (ANamedAlternativeReference) this.declaration
                        .getAlternativeReference();

                return namedRef.getProduction().getText()
                        + namedRef.getDot().getText()
                        + namedRef.getAlternative().getText();
            }
            else {
                return ((AUnnamedAlternativeReference) this.declaration
                        .getAlternativeReference()).getProduction().getText();
            }
        }

        @Override
        public Token getLocation() {

            return this.declaration.getNewKeyword();
        }
    }

    public static abstract class NormalListElement
            extends AlternativeTransformationListElement {

        private Type.SimpleType type;

        private IReferencable targetReference;

        private Parser.ParserElement originReference;

        public NormalListElement(
                Grammar grammar) {

            super(grammar);
        }

        @Override
        public Type.SimpleType getType() {

            if (this.type == null) {
                throw new InternalException(
                        "type shouldn't be null, use constructType()");
            }
            return this.type;
        }

        @Override
        public IReferencable getTargetReference() {

            return this.targetReference;
        }

        public Parser.ParserElement getOriginReference() {

            return this.originReference;
        }

        protected void addTargetReference(
                IReferencable reference) {

            this.targetReference = reference;
        }

        protected void addOriginReference(
                Parser.ParserElement reference) {

            this.originReference = reference;
        }

        @Override
        public void constructType() {

            if (this.type != null) {
                throw new InternalException(
                        "constructType shouldn't be used twice");
            }

            if (this.targetReference == null) {
                throw new InternalException(
                        "constructType shouldn't be called before reference resolution");
            }

            CardinalityInterval cardinality;
            String rightName;
            String leftName = null;

            if (this.targetReference instanceof Parser.ParserElement) {
                Parser.ParserElement parserElement = (Parser.ParserElement) this.targetReference;
                cardinality = parserElement.getCardinality();

                switch (parserElement.getElementType()) {
                case NORMAL:
                    rightName = ((Parser.ParserElement.SingleElement) this.targetReference)
                            .getElement();
                    break;
                case SEPARATED:
                    rightName = ((Parser.ParserElement.DoubleElement) this.targetReference)
                            .getRight();
                    leftName = ((Parser.ParserElement.DoubleElement) this.targetReference)
                            .getLeft();
                    break;
                case ALTERNATED:
                    rightName = ((Parser.ParserElement.DoubleElement) this.targetReference)
                            .getRight();
                    leftName = ((Parser.ParserElement.DoubleElement) this.targetReference)
                            .getLeft();
                    break;
                default:
                    throw new InternalException("Unhandled element type "
                            + parserElement.getNameType());
                }
            }
            else {

                ProductionTransformationElement transformationElement = (ProductionTransformationElement) this.targetReference;
                cardinality = transformationElement.getCardinality();

                if (transformationElement instanceof ProductionTransformationElement.SingleElement) {
                    rightName = ((ProductionTransformationElement) this.targetReference)
                            .getElement();
                }
                else {
                    rightName = ((ProductionTransformationElement.DoubleElement) transformationElement)
                            .getRight();
                    leftName = ((ProductionTransformationElement.DoubleElement) transformationElement)
                            .getLeft();
                }

            }

            if (this.targetReference instanceof Parser.ParserElement
                    && ((Parser.ParserElement) this.targetReference)
                            .getElementType() == Parser.ParserElement.ElementType.SEPARATED
                    || this.targetReference instanceof ProductionTransformationElement
                    && ((ProductionTransformationElement) this.targetReference)
                            .getElementType() == ProductionTransformationElement.ElementType.SEPARATED) {
                if (leftName == null) {
                    this.type = new Type.SimpleType.SeparatedType(rightName,
                            cardinality);
                }
                else {
                    this.type = new Type.SimpleType.SeparatedType(leftName,
                            rightName, cardinality);
                }
            }
            else if (this.targetReference instanceof Parser.ParserElement
                    && ((Parser.ParserElement) this.targetReference)
                            .getElementType() == ElementType.ALTERNATED
                    || this.targetReference instanceof ProductionTransformationElement
                    && ((ProductionTransformationElement) this.targetReference)
                            .getElementType() == ProductionTransformationElement.ElementType.ALTERNATED) {
                if (leftName == null) {
                    this.type = new Type.SimpleType.AlternatedType(rightName,
                            cardinality);
                }
                else {
                    this.type = new Type.SimpleType.AlternatedType(leftName,
                            rightName, cardinality);
                }
            }
            else {

                Bound upperBound = cardinality.getUpperBound();

                if (cardinality.upperBoundIsInfinite()
                        || !upperBound.equals(cardinality.getLowerBound())) {
                    this.type = new Type.SimpleType.HomogeneousType(rightName,
                            cardinality);
                }
                else {
                    Bound newCardinality;
                    CardinalityInterval newInterval;
                    BigInteger upperBoundValue = upperBound.getValue();

                    if (upperBoundValue.mod(BigInteger.valueOf(2L)).compareTo(
                            BigInteger.ZERO) == 0) {

                        newCardinality = new Bound(
                                upperBoundValue.divide(BigInteger.valueOf(2L)));

                        newInterval = new CardinalityInterval(newCardinality,
                                newCardinality);
                        this.type = new Type.SimpleType.AlternatedType(
                                rightName, newInterval);
                    }
                    else {
                        if (upperBoundValue.compareTo(BigInteger.ONE) > 0) {

                            newCardinality = new Bound(upperBoundValue.divide(
                                    BigInteger.valueOf(2L)).add(BigInteger.ONE));
                        }
                        else {
                            newCardinality = upperBound;
                        }

                        newInterval = new CardinalityInterval(newCardinality,
                                newCardinality);
                        if (leftName == null) {
                            this.type = new Type.SimpleType.SeparatedType(
                                    rightName, newInterval);
                        }
                        else {
                            this.type = new Type.SimpleType.SeparatedType(
                                    leftName, rightName, newInterval);
                        }

                    }
                }
            }
        }

        public boolean isTransformed() {

            return this.targetReference instanceof ProductionTransformationElement;
        }

    }

    public static class ImplicitNormalListElement
            extends AlternativeTransformationListElement.NormalListElement {

        private String element;

        public ImplicitNormalListElement(
                Parser.ParserElement reference,
                Grammar grammar) {

            super(grammar);

            if (reference == null) {
                throw new InternalException("reference may not be null");
            }

            super.addTargetReference(reference);
            super.addOriginReference(reference);

        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationNormalListReferenceListElement(this);

        }

        @Override
        public String getElement() {

            if (getTargetReference() instanceof INameDeclaration) {
                this.element = ((INameDeclaration) getTargetReference())
                        .getName();
            }
            else if (getTargetReference() instanceof Parser.ParserAlternative) {
                this.element = ((Parser.ParserAlternative) getTargetReference())
                        .getName();
                if (this.element == null) {
                    this.element = "{"
                            + ((Parser.ParserAlternative) getTargetReference())
                                    .getIndex() + "}";
                }
            }
            else if (getTargetReference() instanceof Tree.TreeAlternative) {
                this.element = ((Tree.TreeAlternative) getTargetReference())
                        .getName();
                if (this.element == null) {
                    this.element = "{"
                            + ((Tree.TreeAlternative) getTargetReference())
                                    .getIndex() + "}";
                }
            }
            else if (getTargetReference() instanceof Parser.ParserElement) {
                this.element = ((Parser.ParserElement) getTargetReference())
                        .getElement();
            }
            else if (getTargetReference() instanceof Tree.TreeElement) {
                this.element = ((Tree.TreeElement) getTargetReference())
                        .getElement();
            }

            this.element += "...";

            return this.element;
        }

        @Override
        public Token getLocation() {

            return getTargetReference().getLocation();
        }
    }

    public static class ExplicitNormalListElement
            extends AlternativeTransformationListElement.NormalListElement {

        private final AListReferenceListElement declaration;

        private String element;

        public ExplicitNormalListElement(
                AListReferenceListElement declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        public AListReferenceListElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public IReferencable getTargetReference() {

            if (super.getTargetReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getTargetReference();

        }

        @Override
        public void addTargetReference(
                IReferencable reference) {

            if (super.getTargetReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addTargetReference(reference);
        }

        @Override
        public Parser.ParserElement getOriginReference() {

            if (super.getOriginReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getOriginReference();

        }

        @Override
        public void addOriginReference(
                Parser.ParserElement reference) {

            if (super.getOriginReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addOriginReference(reference);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationNormalListReferenceListElement(this);

        }

        @Override
        public String getElement() {

            if (this.element == null) {
                if (this.declaration.getElementReference() instanceof ANaturalElementReference) {
                    ANaturalElementReference naturalRef = (ANaturalElementReference) this.declaration
                            .getElementReference();
                    this.element = naturalRef.getElement().getText();
                }
                else {
                    ATransformedElementReference naturalRef = (ATransformedElementReference) this.declaration
                            .getElementReference();
                    this.element = naturalRef.getElement().getText()
                            + naturalRef.getDot().getText()
                            + naturalRef.getPart().getText();
                }

                this.element += "...";
            }
            return this.element;
        }

        @Override
        public Token getLocation() {

            if (this.declaration.getElementReference() instanceof ANaturalElementReference) {
                return ((ANaturalElementReference) this.declaration
                        .getElementReference()).getElement();
            }
            else {
                return ((ATransformedElementReference) this.declaration
                        .getElementReference()).getElement();
            }
        }
    }

    public static abstract class LeftListElement
            extends AlternativeTransformationListElement {

        private IReferencable targetReference;

        private Parser.ParserElement originReference;

        private Type.SimpleType type;

        public LeftListElement(
                Grammar grammar) {

            super(grammar);
        }

        @Override
        public Type.SimpleType getType() {

            if (this.type == null) {
                throw new InternalException(
                        "type shouldn't be null, use constructType()");
            }
            return this.type;
        }

        @Override
        public IReferencable getTargetReference() {

            return this.targetReference;
        }

        public Parser.ParserElement getOriginReference() {

            return this.originReference;
        }

        protected void addTargetReference(
                IReferencable reference) {

            this.targetReference = reference;
        }

        protected void addOriginReference(
                Parser.ParserElement reference) {

            this.originReference = reference;
        }

        @Override
        public void constructType() {

            if (this.type != null) {
                throw new InternalException(
                        "constructType shouldn't be used twice");
            }

            if (this.targetReference == null) {
                throw new InternalException(
                        "constructType shouldn't be called before reference resolution");
            }

            Type referencedType;

            if (this.targetReference instanceof Parser.ParserElement) {
                referencedType = ((Parser.ParserElement) this.targetReference)
                        .getType();
            }
            else {
                referencedType = ((ProductionTransformationElement) this.targetReference)
                        .getType();
            }

            // Calculate the cardinality of the left element

            String elementName;
            CardinalityInterval intermediateInterval;

            if (referencedType instanceof Type.SimpleType.AlternatedType) {
                elementName = ((Type.SimpleType.AlternatedType) referencedType)
                        .getLeftElementName();
                intermediateInterval = ((Type.SimpleType.AlternatedType) referencedType)
                        .getCardinality();
            }
            else if (referencedType instanceof Type.SimpleType.SeparatedType) {
                elementName = ((Type.SimpleType.SeparatedType) referencedType)
                        .getLeftElementName();
                CardinalityInterval referencedInteval = ((Type.SimpleType.SeparatedType) referencedType)
                        .getCardinality();

                Bound lowerBound;
                Bound upperBound;

                if (referencedInteval.getUpperBound().equals(Bound.MAX)) {
                    upperBound = Bound.MAX;
                }
                else {
                    upperBound = referencedInteval.getUpperBound().subtract(
                            BigInteger.ONE);
                }

                if (referencedInteval.getLowerBound().equals(Bound.ZERO)) {
                    lowerBound = Bound.ZERO;
                }
                else {
                    lowerBound = referencedInteval.getLowerBound().subtract(
                            BigInteger.ONE);
                }

                intermediateInterval = new CardinalityInterval(lowerBound,
                        upperBound);
            }
            else {
                throw new InternalException(
                        "Type shouldn't be different than Alternated or Separated");
            }

            // Calculate the type of the left element

            Bound upperBound = intermediateInterval.getUpperBound();

            if (intermediateInterval.upperBoundIsInfinite()
                    || !upperBound.equals(intermediateInterval.getLowerBound())) {
                this.type = new Type.SimpleType.HomogeneousType(elementName,
                        intermediateInterval);
            }
            else {
                BigInteger upperBoundValue = upperBound.getValue();
                Bound newCardinality;
                CardinalityInterval newInterval;

                if (upperBoundValue.mod(BigInteger.valueOf(2L)).compareTo(
                        BigInteger.ZERO) == 0) {

                    newCardinality = new Bound(
                            upperBoundValue.divide(BigInteger.valueOf(2L)));

                    newInterval = new CardinalityInterval(newCardinality,
                            newCardinality);
                    this.type = new Type.SimpleType.AlternatedType(elementName,
                            newInterval);
                }
                else {
                    if (upperBoundValue.compareTo(BigInteger.ONE) > 0) {

                        newCardinality = new Bound(upperBoundValue.divide(
                                BigInteger.valueOf(2L)).add(BigInteger.ONE));
                    }
                    else {
                        newCardinality = upperBound;
                    }

                    newInterval = new CardinalityInterval(newCardinality,
                            newCardinality);
                    this.type = new Type.SimpleType.AlternatedType(elementName,
                            newInterval);
                }

            }

        }

        public boolean isTransformed() {

            return this.targetReference instanceof ProductionTransformationElement;
        }

    }

    public static class ImplicitLeftListElement
            extends AlternativeTransformationListElement.LeftListElement {

        private String element;

        public ImplicitLeftListElement(
                Parser.ParserElement reference,
                Grammar grammar) {

            super(grammar);

            if (reference == null) {
                throw new InternalException("reference may not be null");
            }

            super.addTargetReference(reference);
            super.addOriginReference(reference);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationLeftListReferenceListElement(this);

        }

        @Override
        public String getElement() {

            if (getTargetReference() instanceof INameDeclaration) {
                this.element = ((INameDeclaration) getTargetReference())
                        .getName();
            }
            else if (getTargetReference() instanceof Parser.ParserAlternative) {
                this.element = ((Parser.ParserAlternative) getTargetReference())
                        .getName();
                if (this.element == null) {
                    this.element = "{"
                            + ((Parser.ParserAlternative) getTargetReference())
                                    .getIndex() + "}";
                }
            }
            else if (getTargetReference() instanceof Tree.TreeAlternative) {
                this.element = ((Tree.TreeAlternative) getTargetReference())
                        .getName();
                if (this.element == null) {
                    this.element = "{"
                            + ((Tree.TreeAlternative) getTargetReference())
                                    .getIndex() + "}";
                }
            }
            else if (getTargetReference() instanceof Parser.ParserElement) {
                this.element = ((Parser.ParserElement) getTargetReference())
                        .getElement();
            }
            else if (getTargetReference() instanceof Tree.TreeElement) {
                this.element = ((Tree.TreeElement) getTargetReference())
                        .getElement();
            }

            this.element += ".Left...";

            return this.element;
        }

        @Override
        public Token getLocation() {

            return getTargetReference().getLocation();
        }
    }

    public static class ExplicitLeftListElement
            extends AlternativeTransformationListElement.LeftListElement {

        private final ALeftListReferenceListElement declaration;

        private String element;

        public ExplicitLeftListElement(
                ALeftListReferenceListElement declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        public ALeftListReferenceListElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public IReferencable getTargetReference() {

            if (super.getTargetReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getTargetReference();

        }

        @Override
        public void addTargetReference(
                IReferencable reference) {

            if (super.getTargetReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addTargetReference(reference);
        }

        @Override
        public Parser.ParserElement getOriginReference() {

            if (super.getOriginReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getOriginReference();

        }

        @Override
        public void addOriginReference(
                Parser.ParserElement reference) {

            if (super.getOriginReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addOriginReference(reference);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationLeftListReferenceListElement(this);

        }

        @Override
        public String getElement() {

            if (this.element == null) {
                if (this.declaration.getElementReference() instanceof ANaturalElementReference) {
                    ANaturalElementReference naturalRef = (ANaturalElementReference) this.declaration
                            .getElementReference();
                    this.element = naturalRef.getElement().getText();
                }
                else {
                    ATransformedElementReference naturalRef = (ATransformedElementReference) this.declaration
                            .getElementReference();
                    this.element = naturalRef.getElement().getText()
                            + naturalRef.getDot().getText()
                            + naturalRef.getPart().getText();
                }

                this.element += ".Left...";
            }
            return this.element;
        }

        @Override
        public Token getLocation() {

            if (this.declaration.getElementReference() instanceof ANaturalElementReference) {
                return ((ANaturalElementReference) this.declaration
                        .getElementReference()).getElement();
            }
            else {
                return ((ATransformedElementReference) this.declaration
                        .getElementReference()).getElement();
            }
        }
    }

    public static abstract class RightListElement
            extends AlternativeTransformationListElement {

        private IReferencable targetReference;

        private Parser.ParserElement originReference;

        private Type.SimpleType type;

        public RightListElement(
                Grammar grammar) {

            super(grammar);
        }

        @Override
        public Type.SimpleType getType() {

            if (this.type == null) {
                throw new InternalException(
                        "type shouldn't be null, use constructType()");
            }
            return this.type;
        }

        @Override
        public IReferencable getTargetReference() {

            return this.targetReference;
        }

        public Parser.ParserElement getOriginReference() {

            return this.originReference;
        }

        protected void addTargetReference(
                IReferencable reference) {

            this.targetReference = reference;
        }

        protected void addOriginReference(
                Parser.ParserElement reference) {

            this.originReference = reference;
        }

        @Override
        public void constructType() {

            if (this.type != null) {
                throw new InternalException(
                        "constructType shouldn't be used twice");
            }

            if (this.targetReference == null) {
                throw new InternalException(
                        "constructType shouldn't be called before reference resolution");
            }

            Type referencedType;

            if (this.targetReference instanceof Parser.ParserElement) {
                referencedType = ((Parser.ParserElement) this.targetReference)
                        .getType();
            }
            else {
                referencedType = ((ProductionTransformationElement) this.targetReference)
                        .getType();
            }

            // Calculate the cardinality of the left element

            String elementName;
            CardinalityInterval intermediateInterval;

            if (referencedType instanceof Type.SimpleType.AlternatedType) {
                elementName = ((Type.SimpleType.AlternatedType) referencedType)
                        .getRightElementName();
                intermediateInterval = ((Type.SimpleType.AlternatedType) referencedType)
                        .getCardinality();
            }
            else if (referencedType instanceof Type.SimpleType.SeparatedType) {
                elementName = ((Type.SimpleType.SeparatedType) referencedType)
                        .getRightElementName();

                intermediateInterval = ((Type.SimpleType.SeparatedType) referencedType)
                        .getCardinality();
            }
            else {
                throw new InternalException(
                        "Type shouldn't be different than Alternated or Separated");
            }

            // Calculate the type of the left element

            Bound upperBound = intermediateInterval.getUpperBound();

            if (intermediateInterval.upperBoundIsInfinite()
                    || !upperBound.equals(intermediateInterval.getLowerBound())) {
                this.type = new Type.SimpleType.HomogeneousType(elementName,
                        intermediateInterval);
            }
            else {
                BigInteger upperBoundValue = upperBound.getValue();
                Bound newCardinality;
                CardinalityInterval newInterval;

                if (upperBoundValue.mod(BigInteger.valueOf(2L)).compareTo(
                        BigInteger.ZERO) == 0) {

                    newCardinality = new Bound(
                            upperBoundValue.divide(BigInteger.valueOf(2L)));

                    newInterval = new CardinalityInterval(newCardinality,
                            newCardinality);
                    this.type = new Type.SimpleType.AlternatedType(elementName,
                            newInterval);
                }
                else {
                    if (upperBoundValue.compareTo(BigInteger.ONE) > 0) {

                        newCardinality = new Bound(upperBoundValue.divide(
                                BigInteger.valueOf(2L)).add(BigInteger.ONE));
                    }
                    else {
                        newCardinality = upperBound;
                    }

                    newInterval = new CardinalityInterval(newCardinality,
                            newCardinality);
                    this.type = new Type.SimpleType.SeparatedType(elementName,
                            newInterval);
                }
            }

        }

        public boolean isTransformed() {

            return this.targetReference instanceof ProductionTransformationElement;
        }
    }

    public static class ImplicitRightListElement
            extends AlternativeTransformationListElement.RightListElement {

        private String element;

        public ImplicitRightListElement(
                Parser.ParserElement reference,
                Grammar grammar) {

            super(grammar);

            if (reference == null) {
                throw new InternalException("reference may not be null");
            }

            super.addTargetReference(reference);
            super.addOriginReference(reference);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationRightListReferenceListElement(this);

        }

        @Override
        public String getElement() {

            if (getTargetReference() instanceof INameDeclaration) {
                this.element = ((INameDeclaration) getTargetReference())
                        .getName();
            }
            else if (getTargetReference() instanceof Parser.ParserAlternative) {
                this.element = ((Parser.ParserAlternative) getTargetReference())
                        .getName();
                if (this.element == null) {
                    this.element = "{"
                            + ((Parser.ParserAlternative) getTargetReference())
                                    .getIndex() + "}";
                }
            }
            else if (getTargetReference() instanceof Tree.TreeAlternative) {
                this.element = ((Tree.TreeAlternative) getTargetReference())
                        .getName();
                if (this.element == null) {
                    this.element = "{"
                            + ((Tree.TreeAlternative) getTargetReference())
                                    .getIndex() + "}";
                }
            }
            else if (getTargetReference() instanceof Parser.ParserElement) {
                this.element = ((Parser.ParserElement) getTargetReference())
                        .getElement();
            }
            else if (getTargetReference() instanceof Tree.TreeElement) {
                this.element = ((Tree.TreeElement) getTargetReference())
                        .getElement();
            }

            this.element += ".Right...";

            return this.element;
        }

        @Override
        public Token getLocation() {

            return getTargetReference().getLocation();
        }
    }

    public static class ExplicitRightListElement
            extends AlternativeTransformationListElement.RightListElement {

        private final ARightListReferenceListElement declaration;

        private String element;

        public ExplicitRightListElement(
                ARightListReferenceListElement declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        public ARightListReferenceListElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public IReferencable getTargetReference() {

            if (super.getTargetReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getTargetReference();

        }

        @Override
        public void addTargetReference(
                IReferencable reference) {

            if (super.getTargetReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addTargetReference(reference);
        }

        @Override
        public Parser.ParserElement getOriginReference() {

            if (super.getOriginReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getOriginReference();

        }

        @Override
        public void addOriginReference(
                Parser.ParserElement reference) {

            if (super.getOriginReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addOriginReference(reference);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationRightListReferenceListElement(this);

        }

        @Override
        public String getElement() {

            if (this.element == null) {
                if (this.declaration.getElementReference() instanceof ANaturalElementReference) {
                    ANaturalElementReference naturalRef = (ANaturalElementReference) this.declaration
                            .getElementReference();
                    this.element = naturalRef.getElement().getText();
                }
                else {
                    ATransformedElementReference naturalRef = (ATransformedElementReference) this.declaration
                            .getElementReference();
                    this.element = naturalRef.getElement().getText()
                            + naturalRef.getDot().getText()
                            + naturalRef.getPart().getText();
                }

                this.element += ".Right...";
            }
            return this.element;
        }

        @Override
        public Token getLocation() {

            if (this.declaration.getElementReference() instanceof ANaturalElementReference) {
                return ((ANaturalElementReference) this.declaration
                        .getElementReference()).getElement();
            }
            else {
                return ((ATransformedElementReference) this.declaration
                        .getElementReference()).getElement();
            }
        }
    }
}
