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

import static org.sablecc.util.CamelCase.*;

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;
import org.sablecc.util.interfaces.*;

public class Tree
        implements IVisitableGrammarPart {

    private final List<Tree.TreeProduction> productions = new LinkedList<Tree.TreeProduction>();

    public Tree() {

    }

    public List<Tree.TreeProduction> getProductions() {

        return this.productions;
    }

    public void addProduction(
            Tree.TreeProduction production) {

        this.productions.add(production);
    }

    @Override
    public void apply(
            IGrammarVisitor visitor) {

        visitor.visitTree(this);

    }

    public static class TreeProduction
            implements INameDeclaration, IReferencable, IVisitableGrammarPart {

        private final ATreeProduction declaration;

        private final Grammar grammar;

        private final LocalNamespace namespace;

        private final LinkedList<TreeAlternative> alternatives = new LinkedList<TreeAlternative>();

        public TreeProduction(
                ATreeProduction declaration,
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

            // When there is only one alternative, it doesn't require an
            // implicit
            // name
            if (this.alternatives.size() == 1) {
                String explicitName = this.alternatives.get(0)
                        .getExplicitName();
                if (explicitName != null) {
                    this.alternatives.get(0).setName(explicitName);
                }
                else {
                    this.alternatives.get(0).setName("");
                }

                this.namespace = null;
            }
            else {
                this.namespace = new LocalNamespace(this.alternatives);
            }

        }

        public LinkedList<TreeAlternative> getAlternatives() {

            return this.alternatives;
        }

        public Tree.TreeAlternative getLocalReference(
                String reference) {

            if (this.namespace != null) {
                return this.namespace.get(reference);
            }
            else {
                Tree.TreeAlternative firstAlternative = this.alternatives
                        .get(0);
                if (firstAlternative.getName() != null
                        && firstAlternative.equals(reference)) {
                    return firstAlternative;
                }
                return null;
            }

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

            return "tree production";
        }

        public String getName_CamelCase() {

            return to_CamelCase(getName());
        }

        @Override
        public Token getLocation() {

            return this.declaration.getName();
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitTreeProduction(this);

        }

        private void findAlternatives() {

            this.declaration.apply(new DepthFirstAdapter() {

                private final TreeProduction treeProduction = TreeProduction.this;

                private int nextIndex = 1;

                @Override
                public void inATreeAlternative(
                        ATreeAlternative node) {

                    TreeAlternative alternative = new TreeAlternative(node,
                            TreeProduction.this.grammar, this.treeProduction,
                            this.nextIndex);

                    this.nextIndex += 1;

                    this.treeProduction.alternatives.add(alternative);
                }
            });

        }

        private static class LocalNamespace
                extends ImplicitExplicitNamespace<TreeAlternative> {

            public LocalNamespace(
                    final LinkedList<TreeAlternative> declarations) {

                super(declarations);
            }

            @Override
            protected void raiseDuplicateError(
                    TreeAlternative declaration,
                    TreeAlternative previousDeclaration) {

                throw SemanticException.duplicateAlternativeName(declaration,
                        previousDeclaration);

            }

        }

    }

    public static class TreeAlternative
            implements ImplicitExplicit, IReferencable, IVisitableGrammarPart {

        private final Grammar grammar;

        private final TreeProduction production;

        private final ATreeAlternative declaration;

        private int index;

        private String name;

        private Token token;

        private final LocalNamespace namespace;

        private final LinkedList<TreeElement> elements = new LinkedList<TreeElement>();

        private Type.CompositeType type;

        public TreeAlternative(
                ATreeAlternative declaration,
                Grammar grammar,
                TreeProduction production,
                int index) {

            if (declaration == null) {
                throw new InternalException("grammar may not be null");
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

            findElements();

            this.namespace = new LocalNamespace(this.elements);
        }

        public TreeProduction getProduction() {

            return this.production;
        }

        public int getIndex() {

            return this.index;
        }

        public ATreeAlternative getDeclaration() {

            return this.declaration;
        }

        public LinkedList<TreeElement> getElements() {

            return this.elements;
        }

        public Tree.TreeElement getLocalReference(
                String reference) {

            return this.namespace.get(reference);
        }

        @Override
        public String getImplicitName() {

            String implicitName = null;

            if (this.declaration.getElements().getFirst() instanceof ANormalElement) {
                ANormalElement firstElement = (ANormalElement) this.declaration
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
            else if (this.declaration.getElements().getFirst() instanceof ASeparatedElement) {
                ASeparatedElement firstElement = (ASeparatedElement) this.declaration
                        .getElements().getFirst();

                if (firstElement.getElementName() != null) {
                    implicitName = firstElement.getElementName().getText();
                    implicitName = implicitName.substring(1,
                            implicitName.length() - 2);
                }// else : separated element can't have an implicit name
            }
            else if (this.declaration.getElements().getFirst() instanceof AAlternatedElement) {
                AAlternatedElement firstElement = (AAlternatedElement) this.declaration
                        .getElements().getFirst();

                if (firstElement.getElementName() != null) {
                    implicitName = firstElement.getElementName().getText();
                    implicitName = implicitName.substring(1,
                            implicitName.length() - 2);
                }// else : alternated element can't have an implicit name
            }

            return implicitName;
        }

        @Override
        public String getExplicitName() {

            String explicitName = null;

            if (this.declaration.getAlternativeName() != null) {
                explicitName = this.declaration.getAlternativeName().getText();
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
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitTreeAlternative(this);

        }

        public String getName() {

            return this.name;
        }

        public String getName_CamelCase() {

            String name = getName();
            return name == null ? null : to_CamelCase(name);
        }

        public Token getNameToken() {

            if (this.token == null) {
                if (getExplicitName() != null
                        && getExplicitName().equals(this.name)) {
                    this.token = this.declaration.getAlternativeName();
                }
                else if (getImplicitName().equals(this.name)) {
                    ANormalElement firstElement = (ANormalElement) this.declaration
                            .getElements().getFirst();

                    this.token = ((ANameUnit) firstElement.getUnit())
                            .getIdentifier();
                }
            }

            return this.token;
        }

        public Type.CompositeType getType() {

            if (this.type == null) {
                LinkedList<Type> elementsType = new LinkedList<Type>();
                for (Tree.TreeElement element : this.elements) {
                    elementsType.add(element.getType());
                }

                this.type = new Type.CompositeType(elementsType);
            }

            return this.type;
        }

        @Override
        public Token getLocation() {

            if (this.declaration.getAlternativeName() != null) {
                return this.declaration.getAlternativeName();
            }
            else {
                // TODO Handle selector case
                return this.elements.get(0).getLocation();
            }
        }

        protected void findElements() {

            this.declaration.apply(new DepthFirstAdapter() {

                @Override
                public void inANormalElement(
                        ANormalElement node) {

                    TreeElement.SingleElement element = new TreeElement.SingleElement(
                            node, TreeAlternative.this.grammar,
                            TreeAlternative.this);

                    TreeAlternative.this.elements.add(element);
                }

                @Override
                public void inASeparatedElement(
                        ASeparatedElement node) {

                    TreeElement.DoubleElement element = new TreeElement.DoubleElement(
                            node, TreeAlternative.this.grammar,
                            TreeAlternative.this);

                    TreeAlternative.this.elements.add(element);

                }

                @Override
                public void inAAlternatedElement(
                        AAlternatedElement node) {

                    TreeElement.DoubleElement element = new TreeElement.DoubleElement(
                            node, TreeAlternative.this.grammar,
                            TreeAlternative.this);

                    TreeAlternative.this.elements.add(element);
                }
            });

        }

        private static class LocalNamespace
                extends ImplicitExplicitNamespace<TreeElement> {

            public LocalNamespace(
                    final LinkedList<TreeElement> declarations) {

                super(declarations);
            }

            @Override
            protected void raiseDuplicateError(
                    TreeElement declaration,
                    TreeElement previousDeclaration) {

                throw SemanticException.duplicateElementName(declaration,
                        previousDeclaration);

            }

        }

    }

    public static abstract class TreeElement
            implements ImplicitExplicit, IReferencable, IVisitableGrammarPart {

        public static enum ElementType {
            NORMAL,
            SEPARATED,
            ALTERNATED
        }

        private final Grammar grammar;

        private final TreeAlternative alternative;

        private final ElementType elementType;

        private TreeElement(
                Grammar grammar,
                TreeAlternative alternative,
                ElementType elementType) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            if (alternative == null) {
                throw new InternalException("alternative may not be null");
            }

            this.grammar = grammar;
            this.alternative = alternative;
            this.elementType = elementType;
        }

        public TreeAlternative getAlternative() {

            return this.alternative;
        }

        public ElementType getElementType() {

            return this.elementType;
        }

        public int getIndex() {

            return this.alternative.getElements().indexOf(this);
        }

        public abstract Node getDeclaration();

        public abstract String getName();

        public abstract Token getNameToken();

        public abstract String getElement();

        public abstract CardinalityInterval getCardinality();

        public abstract Type.SimpleType getType();

        public static class SingleElement
                extends TreeElement {

            private final ANormalElement declaration;

            private String name;

            private Token nameToken;

            private String element;

            private Token elementToken;

            private IReferencable reference;

            private CardinalityInterval cardinality;

            private Type.SimpleType type;

            public SingleElement(
                    ANormalElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative, ElementType.NORMAL);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;

                this.element = new InformationExtractor(this)
                        .getReferenceText();

                this.cardinality = new CardinalityInterval(
                        this.declaration.getUnaryOperator());

                constructType();
            }

            private void constructType() {

                Bound upperBound = this.cardinality.getUpperBound();

                if (this.cardinality.upperBoundIsInfinite()
                        || !upperBound.equals(this.cardinality.getLowerBound())) {
                    this.type = new Type.SimpleType.HomogeneousType(
                            this.element, this.cardinality);
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
                        this.type = new Type.SimpleType.SeparatedType(
                                this.element, newInterval);
                    }
                }
            }

            @Override
            public ANormalElement getDeclaration() {

                return this.declaration;
            }

            public IReferencable getReference() {

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

                if (this.nameToken == null) {
                    if (getExplicitName() != null
                            && getExplicitName().equals(this.name)) {
                        this.nameToken = this.declaration.getElementName();
                    }
                    else if (getImplicitName().equals(this.name)) {
                        if (!(this.declaration.getUnit() instanceof ANameUnit)) {
                            throw new InternalException("unit may not be a "
                                    + this.declaration.getUnit().getClass());
                        }
                        this.nameToken = ((ANameUnit) this.declaration
                                .getUnit()).getIdentifier();
                    }
                }

                return this.nameToken;
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

                visitor.visitTreeSingleElement(this);

            }

        }

        public static class DoubleElement
                extends TreeElement {

            private final PElement declaration;

            private String name;

            private IReferencable leftReference;

            private IReferencable rightReference;

            private String element;

            private Token elementToken;

            private CardinalityInterval cardinality;

            private Type.SimpleType type;

            public DoubleElement(
                    ASeparatedElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative, ElementType.SEPARATED);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;

                this.cardinality = new CardinalityInterval(
                        declaration.getManyOperator());

                constructType();
            }

            public DoubleElement(
                    AAlternatedElement declaration,
                    Grammar grammar,
                    TreeAlternative alternative) {

                super(grammar, alternative, ElementType.ALTERNATED);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;

                this.cardinality = new CardinalityInterval(
                        declaration.getManyOperator());

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

            @Override
            public PElement getDeclaration() {

                return this.declaration;
            }

            public IReferencable getLeftReference() {

                return this.leftReference;
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

            public IReferencable getRightReference() {

                return this.rightReference;
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

                if (this.element == null) {
                    this.element = new InformationExtractor(this)
                            .getReferenceText();
                }

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

                visitor.visitTreeDoubleElement(this);

            }

        }

        public String getName_CamelCase() {

            String name = getName();
            return name == null ? null : to_CamelCase(name);
        }

        private static class InformationExtractor
                extends DepthFirstAdapter {

            private String text = "";

            private String leftText = "";

            private String rightText = "";

            private Token token;

            public InformationExtractor(
                    Tree.TreeElement.SingleElement element) {

                element.getDeclaration().apply(this);
            }

            public InformationExtractor(
                    Tree.TreeElement.DoubleElement element) {

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
                this.text += "(" + this.leftText + " Separtor "
                        + this.rightText + ")";

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

}
