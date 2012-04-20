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

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;
import org.sablecc.util.interfaces.*;

public abstract class ProductionTransformationElement
        implements ImplicitExplicit, IReferencable, IVisitableGrammarPart {

    public static enum ElementType {
        NORMAL,
        SEPARATED,
        ALTERNATED
    }

    private final Grammar grammar;

    private final ProductionTransformation productionTransformation;

    private final ElementType elementType;

    public ProductionTransformationElement(
            Grammar grammar,
            ProductionTransformation productionTransformation,
            ElementType elementType) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (productionTransformation == null) {
            throw new InternalException(
                    "alternativeTransformation may not be null");
        }

        this.grammar = grammar;
        this.productionTransformation = productionTransformation;
        this.elementType = elementType;
    }

    public int index() {

        return this.productionTransformation.getElements().indexOf(this);
    }

    public abstract String getName();

    public abstract Token getNameToken();

    public abstract String getElement();

    public abstract CardinalityInterval getCardinality();

    public abstract Type.SimpleType getType();

    public ProductionTransformation getProductionTransformation() {

        return this.productionTransformation;
    }

    public Grammar getGrammar() {

        return this.grammar;
    }

    public ElementType getElementType() {

        return this.elementType;
    }

    public static abstract class SingleElement
            extends ProductionTransformationElement {

        public SingleElement(
                Grammar grammar,
                ProductionTransformation productionTransformation) {

            super(grammar, productionTransformation, ElementType.NORMAL);

        }

        public abstract IReferencable getReference();

    }

    public static class ImplicitSingleElement
            extends SingleElement {

        private final Tree.TreeProduction reference;

        private Type.SimpleType type;

        public ImplicitSingleElement(
                Grammar grammar,
                ProductionTransformation productionTransformation,
                Tree.TreeProduction reference) {

            super(grammar, productionTransformation);

            this.reference = reference;

            this.type = new Type.SimpleType.SeparatedType(reference.getName(),
                    new CardinalityInterval(Bound.ONE, Bound.ONE));
        }

        @Override
        public IReferencable getReference() {

            return this.reference;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitProductionTransformationSingleElement(this);

        }

        @Override
        public String getImplicitName() {

            return null;
        }

        @Override
        public String getExplicitName() {

            return null;
        }

        @Override
        public void setName(
                String name) {

            throw new InternalException(
                    "Cannot set a name to an implicit element");
        }

        @Override
        public String getName() {

            return null;
        }

        @Override
        public Token getNameToken() {

            return null;
        }

        @Override
        public CardinalityInterval getCardinality() {

            return CardinalityInterval.ONE_ONE;
        }

        @Override
        public String getElement() {

            return this.reference.getName();
        }

        @Override
        public Type.SimpleType getType() {

            return this.type;
        }

        @Override
        public Token getLocation() {

            return this.reference.getLocation();
        }

    }

    public static class ExplicitSingleElement
            extends SingleElement {

        private final ANormalElement declaration;

        private String name;

        private Token token;

        private IReferencable reference;

        private Token elementToken;

        private String element;

        private CardinalityInterval cardinality;

        private Type.SimpleType type;

        public ExplicitSingleElement(

                Grammar grammar,
                ProductionTransformation productionTransformation,
                ANormalElement declaration) {

            super(grammar, productionTransformation);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;

            this.element = new InformationExtractor(this).getReferenceText();

            this.cardinality = new CardinalityInterval(
                    this.declaration.getUnaryOperator());

            constructType();

        }

        private void constructType() {

            Bound upperBound = this.cardinality.getUpperBound();

            if (this.cardinality.upperBoundIsInfinite()
                    || !upperBound.equals(this.cardinality.getLowerBound())) {
                this.type = new Type.SimpleType.HomogeneousType(this.element,
                        this.cardinality);
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
                    this.type = new Type.SimpleType.AlternatedType(
                            this.element, newInterval);
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
                    this.type = new Type.SimpleType.SeparatedType(this.element,
                            newInterval);
                }
            }
        }

        public ANormalElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public IReferencable getReference() {

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

        @Override
        public String getImplicitName() {

            String implicitName = null;

            if (this.declaration.getUnit() instanceof ANameUnit
                    && (this.declaration.getUnaryOperator() == null || this.declaration
                            .getUnaryOperator() instanceof AZeroOrOneUnaryOperator)) {

                implicitName = ((ANameUnit) this.declaration.getUnit())
                        .getIdentifier().getText();
            }

            return implicitName;
        }

        @Override
        public String getExplicitName() {

            String explicitName = null;

            if (this.declaration.getElementName() != null) {
                explicitName = this.declaration.getElementName().getText();
                explicitName = explicitName.substring(1,
                        explicitName.length() - 2);
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

            return this.name;
        }

        @Override
        public Token getNameToken() {

            if (this.token == null) {
                if (getExplicitName() != null
                        && getExplicitName().equals(this.name)) {
                    this.token = this.declaration.getElementName();
                }
                else if (getImplicitName().equals(this.name)) {
                    if (!(this.declaration.getUnit() instanceof ANameUnit)) {
                        throw new InternalException("unit may not be a "
                                + this.declaration.getUnit().getClass());
                    }
                    this.token = ((ANameUnit) this.declaration.getUnit())
                            .getIdentifier();
                }
            }

            return this.token;
        }

        @Override
        public Token getLocation() {

            if (this.elementToken == null) {
                this.elementToken = new InformationExtractor(this)
                        .getFirstToken();
            }

            return this.elementToken;
        }

        @Override
        public String getElement() {

            return this.element;
        }

        @Override
        public CardinalityInterval getCardinality() {

            return this.cardinality;
        }

        @Override
        public Type.SimpleType getType() {

            return this.type;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitProductionTransformationSingleElement(this);

        }
    }

    public static class DoubleElement
            extends ProductionTransformationElement {

        private final PElement declaration;

        private String name;

        private IReferencable leftReference;

        private IReferencable rightReference;

        private Token elementToken;

        private String element;

        private CardinalityInterval cardinality;

        private Type.SimpleType type;

        public DoubleElement(
                Grammar grammar,
                ProductionTransformation productionTransformation,
                ASeparatedElement declaration) {

            super(grammar, productionTransformation, ElementType.SEPARATED);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;

            this.cardinality = new CardinalityInterval(
                    declaration.getManyOperator());

            this.element = new InformationExtractor(this).getReferenceText();

            constructType();
        }

        public DoubleElement(
                Grammar grammar,
                ProductionTransformation productionTransformation,
                AAlternatedElement declaration) {

            super(grammar, productionTransformation, ElementType.ALTERNATED);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;

            this.cardinality = new CardinalityInterval(
                    declaration.getManyOperator());

            this.element = new InformationExtractor(this).getReferenceText();

            constructType();
        }

        private void constructType() {

            InformationExtractor extractor = new InformationExtractor(this);

            if (getElementType() == ElementType.SEPARATED) {
                this.type = new Type.SimpleType.SeparatedType(
                        extractor.getLeftText(), extractor.getRightText(),
                        this.cardinality);
            }
            else // ElementType.ALTERNATED
            {
                this.type = new Type.SimpleType.AlternatedType(
                        extractor.getLeftText(), extractor.getRightText(),
                        this.cardinality);
            }
        }

        public PElement getDeclaration() {

            return this.declaration;
        }

        public IReferencable getLeftReference() {

            if (this.leftReference == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }
            return this.leftReference;
        }

        public IReferencable getRightReference() {

            if (this.rightReference == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }
            return this.rightReference;
        }

        public void addLeftReference(
                IReferencable leftReference) {

            if (this.leftReference == null) {
                this.leftReference = leftReference;
            }
            else {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }
        }

        public void addRightReference(
                IReferencable rightReference) {

            if (this.rightReference == null) {
                this.rightReference = rightReference;
            }
            else {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }
        }

        @Override
        public String getImplicitName() {

            return null;
        }

        @Override
        public String getExplicitName() {

            String explicitName = null;
            TElementName elementName = null;

            switch (getElementType()) {
            case SEPARATED:
                elementName = ((ASeparatedElement) this.declaration)
                        .getElementName();
                break;
            case ALTERNATED:
                elementName = ((AAlternatedElement) this.declaration)
                        .getElementName();
                break;
            }

            if (elementName != null) {
                explicitName = elementName.getText();
                explicitName = explicitName.substring(1,
                        explicitName.length() - 2);
            }

            return explicitName;
        }

        public String getLeft() {

            return new InformationExtractor(this).getLeftText();
        }

        public String getRight() {

            return new InformationExtractor(this).getRightText();
        }

        @Override
        public String getName() {

            return this.name;
        }

        @Override
        public Token getNameToken() {

            TElementName elementName = null;

            switch (getElementType()) {
            case SEPARATED:
                elementName = ((ASeparatedElement) this.declaration)
                        .getElementName();
                break;
            case ALTERNATED:
                elementName = ((AAlternatedElement) this.declaration)
                        .getElementName();
            }

            return elementName;
        }

        @Override
        public Token getLocation() {

            if (this.elementToken == null) {
                this.elementToken = new InformationExtractor(this)
                        .getFirstToken();
            }

            return this.elementToken;
        }

        @Override
        public String getElement() {

            return this.element;
        }

        @Override
        public CardinalityInterval getCardinality() {

            return this.cardinality;
        }

        @Override
        public Type.SimpleType getType() {

            return this.type;
        }

        @Override
        public void setName(
                String name) {

            this.name = name;

        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitProductionTransformationDoubleElement(this);

        }

    }

    static class InformationExtractor
            extends DepthFirstAdapter {

        private String text = "";

        private String leftText = "";

        private String rightText = "";

        private Token token;

        public InformationExtractor(
                ProductionTransformationElement.ExplicitSingleElement element) {

            element.getDeclaration().apply(this);
        }

        public InformationExtractor(
                ProductionTransformationElement.DoubleElement element) {

            element.getDeclaration().apply(this);
        }

        public String getReferenceText() {

            return this.text;
        }

        public Token getFirstToken() {

            return this.token;
        }

        public String getLeftText() {

            return this.leftText;
        }

        public String getRightText() {

            return this.rightText;
        }

        @Override
        public void caseANormalElement(
                ANormalElement node) {

            node.getUnit().apply(this);

        }

        @Override
        public void caseAAlternatedElement(
                AAlternatedElement node) {

            node.getLeft().apply(this);
            this.leftText = this.text;
            this.text = "";
            node.getRight().apply(this);
            this.rightText = this.text;
            this.text = "";
            this.text += "(" + this.leftText + " " + this.rightText + ")";
        }

        @Override
        public void caseASeparatedElement(
                ASeparatedElement node) {

            node.getLeft().apply(this);
            this.leftText = this.text;
            this.text = "";
            node.getRight().apply(this);
            this.rightText = this.text;
            this.text = "";
            this.text += "(" + this.leftText + " Separtor " + this.rightText
                    + ")";
        }

        @Override
        public void caseANameUnit(
                ANameUnit node) {

            if (this.token == null) {
                this.token = node.getIdentifier();
            }

            this.text += node.getIdentifier().getText();
        }

        @Override
        public void caseAStringUnit(
                AStringUnit node) {

            if (this.token == null) {
                this.token = node.getString();
            }

            this.text += node.getString().getText();
        }

        @Override
        public void caseACharCharacter(
                ACharCharacter node) {

            if (this.token == null) {
                this.token = node.getChar();
            }

            this.text += node.getChar().getText();
        }

        @Override
        public void caseADecCharacter(
                ADecCharacter node) {

            if (this.token == null) {
                this.token = node.getDecChar();
            }

            this.text += node.getDecChar().getText();
        }

        @Override
        public void caseAHexCharacter(
                AHexCharacter node) {

            if (this.token == null) {
                this.token = node.getHexChar();
            }

            this.text += node.getHexChar().getText();
        }
    }
}
