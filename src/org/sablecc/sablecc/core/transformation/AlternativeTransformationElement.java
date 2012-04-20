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
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public abstract class AlternativeTransformationElement
        implements IVisitableGrammarPart {

    private final Grammar grammar;

    public AlternativeTransformationElement(
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

    public abstract Token getLocation();

    public static abstract class NullElement
            extends AlternativeTransformationElement {

        private Type.SimpleType type;

        public NullElement(
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
        public void constructType() {

            this.type = new Type.SimpleType.NullType();
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationNullElement(this);

        }

        @Override
        public String getElement() {

            return "Null";
        }

    }

    public static class ImplicitNullElement
            extends NullElement {

        public ImplicitNullElement(
                Grammar grammar) {

            super(grammar);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationNullElement(this);

        }

        @Override
        public Token getLocation() {

            return null;
        }
    }

    public static class ExplicitNullElement
            extends NullElement
            implements IReferencable {

        private final ANullTransformationElement declaration;

        public ExplicitNullElement(
                ANullTransformationElement declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        public ANullTransformationElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public Token getLocation() {

            return this.declaration.getNullKeyword();
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationNullElement(this);

        }
    }

    public static abstract class ReferenceElement
            extends AlternativeTransformationElement {

        private Parser.ParserElement originReference;

        private IReferencable targetReference;

        private Type.SimpleType type;

        public ReferenceElement(
                Grammar grammar) {

            super(grammar);
        }

        public Parser.ParserElement getOriginReference() {

            return this.originReference;
        }

        public IReferencable getTargetReference() {

            return this.targetReference;
        }

        @Override
        public Type.SimpleType getType() {

            if (this.type == null) {
                throw new InternalException(
                        "type shouldn't be null, use constructType()");
            }
            return this.type;
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
            extends AlternativeTransformationElement.ReferenceElement {

        private String element;

        public ImplicitReferenceElement(
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

            visitor.visitAlternativeTransformationReferenceElement(this);

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
            extends AlternativeTransformationElement.ReferenceElement
            implements IReferencable {

        private final AReferenceTransformationElement declaration;

        private String element;

        public ExplicitReferenceElement(
                AReferenceTransformationElement declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        public AReferenceTransformationElement getDeclaration() {

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
        public Parser.ParserElement getOriginReference() {

            if (super.getOriginReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getOriginReference();

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

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationReferenceElement(this);

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
    }

    public static abstract class NewElement
            extends AlternativeTransformationElement {

        private Type.SimpleType type;

        private Tree.TreeAlternative reference;

        public NewElement(
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

        public Tree.TreeAlternative getReference() {

            return this.reference;
        }

        protected void addReference(
                Tree.TreeAlternative reference) {

            this.reference = reference;
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

            for (AlternativeTransformationElement element : getParameters()) {
                element.constructType();
            }
        }

        public abstract List<AlternativeTransformationElement> getParameters();

    }

    public static class ImplicitNewElement
            extends AlternativeTransformationElement.NewElement {

        private final List<AlternativeTransformationElement> transformationElements;

        public ImplicitNewElement(
                Tree.TreeAlternative reference,
                Grammar grammar,
                List<AlternativeTransformationElement> transformationElements) {

            super(grammar);

            if (transformationElements == null) {
                throw new InternalException(
                        "transformationElements may not be null");
            }

            if (reference == null) {
                throw new InternalException("reference may not be null");
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

            visitor.visitAlternativeTransformationNewElement(this);

        }

        @Override
        public String getElement() {

            return getReference().getName();
        }

        @Override
        public Token getLocation() {

            return getReference().getLocation();
        }
    }

    public static class ExplicitNewElement
            extends AlternativeTransformationElement.NewElement
            implements IReferencable {

        private final ANewTransformationElement declaration;

        private final List<AlternativeTransformationElement> transformationElements;

        public ExplicitNewElement(
                ANewTransformationElement declaration,
                Grammar grammar,
                List<AlternativeTransformationElement> transformationElements) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (transformationElements == null) {
                throw new InternalException(
                        "transformationElements may not be null");
            }

            this.declaration = declaration;
            this.transformationElements = transformationElements;
        }

        public ANewTransformationElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public Tree.TreeAlternative getReference() {

            if (super.getReference() == null) {
                throw new InternalException(
                        "reference should have been initialized using addReference");
            }

            return super.getReference();
        }

        @Override
        public void addReference(
                Tree.TreeAlternative reference) {

            if (super.getReference() != null) {
                throw new InternalException(
                        "addReference shouldn't be used twice");
            }

            super.addReference(reference);
        }

        @Override
        public List<AlternativeTransformationElement> getParameters() {

            return this.transformationElements;
        }

        @Override
        public Token getLocation() {

            return this.declaration.getNewKeyword();
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationNewElement(this);

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
    }

    public static abstract class ListElement
            extends AlternativeTransformationElement {

        private Type.SimpleType type;

        public ListElement(
                Grammar grammar) {

            super(grammar);

        }

        public abstract List<AlternativeTransformationListElement> getListElements();

        @Override
        public void constructType() {

            this.type = new Type.SimpleType.EmptyListType();

            for (int i = 0; i < getListElements().size(); i++) {
                AlternativeTransformationListElement element = getListElements()
                        .get(i);

                element.constructType();
                this.type = (Type.SimpleType) this.type.add(element.getType());

                if (this.type == null) {
                    throw SemanticException.uncompatibleListElement(
                            getListElements().get(i - 1), getListElements()
                                    .get(i));
                }
            }
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

    public static class ImplicitListElement
            extends AlternativeTransformationElement.ListElement {

        private final List<AlternativeTransformationListElement> listElements;

        public ImplicitListElement(
                Grammar grammar,
                List<AlternativeTransformationListElement> listElements) {

            super(grammar);

            if (listElements == null) {
                throw new InternalException("listElements may not be null");
            }

            this.listElements = listElements;

        }

        @Override
        public List<AlternativeTransformationListElement> getListElements() {

            return this.listElements;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationListElement(this);

        }

        @Override
        public String getElement() {

            if (getType() instanceof Type.SimpleType.HomogeneousType) {
                return ((Type.SimpleType.HomogeneousType) getType()).getName();
            }
            else if (getType() instanceof Type.SimpleType.SeparatedType) {
                Type.SimpleType.SeparatedType separatedType = (Type.SimpleType.SeparatedType) getType();

                return "(" + separatedType.getRightElementName()
                        + " Separator " + separatedType.getLeftElementName()
                        + ")";
            }
            else if (getType() instanceof Type.SimpleType.AlternatedType) {
                Type.SimpleType.AlternatedType alternatedType = (Type.SimpleType.AlternatedType) getType();

                return "(" + alternatedType.getRightElementName() + " "
                        + alternatedType.getLeftElementName() + ")";
            }
            else if (getType() instanceof Type.SimpleType.EmptyListType) {
                return "";
            }

            throw new InternalException(
                    "Unexpected type for an alternative transformation element");
        }

        @Override
        public Token getLocation() {

            return getListElements().get(0).getLocation();
        }

    }

    public static class ExplicitListElement
            extends AlternativeTransformationElement.ListElement
            implements IReferencable {

        private final AListTransformationElement declaration;

        private final List<AlternativeTransformationListElement> listElements;

        public ExplicitListElement(
                AListTransformationElement declaration,
                Grammar grammar,
                List<AlternativeTransformationListElement> listElements) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (listElements == null) {
                throw new InternalException("listElements may not be null");
            }

            this.declaration = declaration;
            this.listElements = listElements;

        }

        public AListTransformationElement getDeclaration() {

            return this.declaration;
        }

        @Override
        public List<AlternativeTransformationListElement> getListElements() {

            return this.listElements;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAlternativeTransformationListElement(this);
        }

        @Override
        public Token getLocation() {

            return this.declaration.getListKeyword();
        }

        @Override
        public String getElement() {

            String elementText = "List(";
            for (AlternativeTransformationListElement listElement : this.listElements) {
                elementText += listElement.getElement() + " ";
            }
            elementText = elementText.substring(0, elementText.length() - 1);
            elementText += ")";

            return elementText;
        }
    }
}
