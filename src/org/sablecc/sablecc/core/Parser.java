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
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;
import org.sablecc.util.interfaces.*;

public class Parser
        implements IVisitableGrammarPart {

    private final List<Investigator.ParserInvestigator> investigators = new LinkedList<Investigator.ParserInvestigator>();

    private final List<Selector.ParserSelector> selectors = new LinkedList<Selector.ParserSelector>();

    private final List<Parser.ParserProduction> productions = new LinkedList<Parser.ParserProduction>();

    private ARoot rootDeclaration;

    public Parser() {

    }

    public void addInvestigator(
            Investigator.ParserInvestigator investigator) {

        this.investigators.add(investigator);
    }

    public void addSelector(
            Selector.ParserSelector selector) {

        this.selectors.add(selector);
    }

    public void addProduction(
            Parser.ParserProduction production) {

        this.productions.add(production);
    }

    public void addRootDeclaration(
            ARoot rootDeclaration) {

        if (this.rootDeclaration != null) {
            throw new InternalException(
                    "A rootDeclaration as already been added");
        }

        this.rootDeclaration = rootDeclaration;
    }

    public ARoot getRootDeclaration() {

        return this.rootDeclaration;
    }

    public List<Investigator.ParserInvestigator> getInvestigators() {

        return this.investigators;
    }

    public List<Selector.ParserSelector> getSelectors() {

        return this.selectors;
    }

    public List<Parser.ParserProduction> getProductions() {

        return this.productions;
    }

    @Override
    public void apply(
            IGrammarVisitor visitor) {

        visitor.visitParser(this);

    }

    public static class ParserProduction
            implements INameDeclaration, IReferencable, IVisitableGrammarPart {

        private final AParserProduction declaration;

        private final Grammar grammar;

        private final LocalNamespace namespace;

        private final Context context;

        private final LinkedList<ParserAlternative> alternatives = new LinkedList<ParserAlternative>();

        private final List<ParserPriority> priorities = new LinkedList<Parser.ParserPriority>();

        private ProductionTransformation transformation;

        private ParserProduction(
                AParserProduction declaration,
                Context context,
                Grammar grammar) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            if (context == null) {
                throw new InternalException("context may not be null");
            }

            this.declaration = declaration;
            this.context = context;
            this.grammar = grammar;

            findAlternatives();
            findPriorities();

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

        public AParserProduction getDeclaration() {

            return this.declaration;
        }

        public Context getContext() {

            return this.context;
        }

        public LinkedList<ParserAlternative> getAlternatives() {

            return this.alternatives;
        }

        public List<ParserPriority> getPriorities() {

            return this.priorities;
        }

        public ParserAlternative getLocalReference(
                String reference) {

            if (this.namespace != null) {
                return this.namespace.get(reference);
            }
            else {
                Parser.ParserAlternative firstAlternative = this.alternatives
                        .get(0);
                if (firstAlternative.getName() != null
                        && firstAlternative.getName().equals(reference)) {
                    return firstAlternative;
                }
                return null;
            }
        }

        public void addTransformation(
                ProductionTransformation transformation) {

            if (this.transformation == null) {
                this.transformation = transformation;
            }
            else {
                throw new InternalException(
                        "addTransformation shouldn't be used twice");
            }
        }

        public ProductionTransformation getTransformation() {

            return this.transformation;
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
        public Token getLocation() {

            return this.declaration.getName();
        }

        public boolean isDangling() {

            return this.declaration.getQualifier() instanceof ADanglingQualifier;
        }

        public boolean isToken() {

            return this.declaration.getQualifier() instanceof ATokenQualifier;
        }

        public boolean isNormal() {

            return !isDangling() && !isToken();
        }

        static ParserProduction newParserProduction(
                AParserProduction declaration,
                Context context,
                Grammar grammar) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            return new ParserProduction(declaration, context, grammar);
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

        private void findPriorities() {

            this.declaration.apply(new DepthFirstAdapter() {

                private final ParserProduction parserProduction = ParserProduction.this;

                @Override
                public void inALeftParserPriority(
                        ALeftParserPriority node) {

                    ParserPriority.LeftPriority leftPriority = new ParserPriority.LeftPriority(
                            node, ParserProduction.this.grammar,
                            this.parserProduction);

                    ParserProduction.this.priorities.add(leftPriority);
                }

                @Override
                public void inARightParserPriority(
                        ARightParserPriority node) {

                    ParserPriority.RightPriority RightPriority = new ParserPriority.RightPriority(
                            node, ParserProduction.this.grammar,
                            this.parserProduction);

                    ParserProduction.this.priorities.add(RightPriority);
                }

                @Override
                public void inAUnaryParserPriority(
                        AUnaryParserPriority node) {

                    ParserPriority.UnaryPriority unaryPriority = new ParserPriority.UnaryPriority(
                            node, ParserProduction.this.grammar,
                            this.parserProduction);

                    ParserProduction.this.priorities.add(unaryPriority);
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

        public String getName_CamelCase() {

            return to_CamelCase(getName());
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitParserProduction(this);

        }

        @Override
        public String getNameType() {

            return "parser production";
        }
    }

    public static class ParserAlternative
            implements ImplicitExplicit, IReferencable, IVisitableGrammarPart {

        private final AParserAlternative declaration;

        private final Grammar grammar;

        private final ParserProduction production;

        private final LocalNamespace namespace;

        private int index;

        private Type.CompositeType type;

        private final LinkedList<ParserElement> elements = new LinkedList<ParserElement>();

        private AlternativeTransformation transformation;

        private String name;

        private Token nameToken;

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

            findElements();

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
                return new ParserAlternative(declaration, grammar, production,
                        index);
            }
            else {
                return new ParserAlternative(declaration, grammar, production,
                        index);
            }
        }

        public ParserProduction getProduction() {

            return this.production;
        }

        public int getIndex() {

            return this.index;
        }

        public ParserElement getLocalReference(
                String reference) {

            return this.namespace.get(reference);
        }

        public void addTransformation(
                AlternativeTransformation transformation) {

            if (this.transformation == null) {
                this.transformation = transformation;
            }
            else {
                throw new InternalException(
                        "addTransformation shouldn't be used twice");
            }
        }

        public AlternativeTransformation getTransformation() {

            return this.transformation;
        }

        public AParserAlternative getDeclaration() {

            return this.declaration;
        }

        public LinkedList<ParserElement> getElements() {

            return this.elements;
        }

        public Type.CompositeType getType() {

            if (this.type == null) {
                LinkedList<Type> elementsType = new LinkedList<Type>();
                for (Parser.ParserElement element : getElements()) {
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

        public String getName() {

            return this.name;
        }

        public Token getNameToken() {

            if (this.nameToken == null) {
                if (getExplicitName() != null
                        && getExplicitName().equals(this.name)) {
                    this.nameToken = getDeclaration().getAlternativeName();
                }
                else if (getImplicitName().equals(this.name)) {
                    ANormalElement firstElement = (ANormalElement) getDeclaration()
                            .getElements().getFirst();

                    this.nameToken = ((ANameUnit) firstElement.getUnit())
                            .getIdentifier();
                }
            }

            return this.nameToken;
        }

        public boolean isDangling() {

            return this.declaration.getDanglingElement() != null;
        }

        private void findElements() {

            this.declaration.apply(new DepthFirstAdapter() {

                private final ParserAlternative parserAlternative = ParserAlternative.this;

                @Override
                public void inANormalElement(
                        ANormalElement node) {

                    ParserElement.SingleElement normalElement = new ParserElement.SingleElement(
                            node, ParserAlternative.this.grammar,
                            this.parserAlternative);

                    this.parserAlternative.elements.add(normalElement);

                }

                @Override
                public void inASeparatedElement(
                        ASeparatedElement node) {

                    ParserElement.DoubleElement separatedElement = new ParserElement.DoubleElement(
                            node, ParserAlternative.this.grammar,
                            this.parserAlternative);

                    this.parserAlternative.elements.add(separatedElement);
                }

                @Override
                public void inAAlternatedElement(
                        AAlternatedElement node) {

                    ParserElement.DoubleElement alternatedElement = new ParserElement.DoubleElement(
                            node, ParserAlternative.this.grammar,
                            this.parserAlternative);

                    this.parserAlternative.elements.add(alternatedElement);
                }

                @Override
                public void inADanglingElement(
                        ADanglingElement node) {

                    ParserElement.SingleElement danglingElement = new ParserElement.SingleElement(
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

        public String getName_CamelCase() {

            String name = getName();
            return name == null ? null : to_CamelCase(name);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitParserAlternative(this);

        }
    }

    public static abstract class ParserElement
            implements ImplicitExplicit, IReferencable, IVisitableGrammarPart {

        public static enum ElementType {
            NORMAL,
            DANGLING,
            SEPARATED,
            ALTERNATED
        }

        private final Grammar grammar;

        private final ParserAlternative alternative;

        private final ElementType elementType;

        public ParserElement(
                Grammar grammar,
                ParserAlternative alternative,
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

        public ParserAlternative getAlternative() {

            return this.alternative;
        }

        public int getIndex() {

            return this.alternative.getElements().indexOf(this);
        }

        public abstract Node getDeclaration();

        public abstract String getName();

        public abstract Token getNameToken();

        public abstract String getNameType();

        public abstract String getElement();

        @Override
        public abstract Token getLocation();

        public abstract CardinalityInterval getCardinality();

        public abstract Type.SimpleType getType();

        public ElementType getElementType() {

            return this.elementType;
        }

        public static class SingleElement
                extends ParserElement {

            private final Node declaration;

            private String name;

            private Token nameToken;

            private IReferencable reference;

            private Token elementToken;

            private String element;

            private CardinalityInterval cardinality;

            private Type.SimpleType type;

            public SingleElement(
                    ANormalElement declaration,
                    Grammar grammar,
                    ParserAlternative alternative) {

                super(grammar, alternative, ElementType.NORMAL);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;

                this.cardinality = new CardinalityInterval(
                        declaration.getUnaryOperator());

                this.element = new InformationExtractor(this)
                        .getReferenceText();

                constructType();
            }

            public SingleElement(
                    ADanglingElement declaration,
                    Grammar grammar,
                    ParserAlternative alternative) {

                super(grammar, alternative, ElementType.DANGLING);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;

                this.element = new InformationExtractor(this)
                        .getReferenceText();

                this.cardinality = CardinalityInterval.ZERO_ONE;

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
            public Node getDeclaration() {

                return this.declaration;
            }

            @Override
            public String getImplicitName() {

                String implicitName = null;

                if (getElementType() == ElementType.NORMAL) {
                    PUnit unit = ((ANormalElement) this.declaration).getUnit();

                    if (unit instanceof ANameUnit
                            && this.cardinality
                                    .isIncludedIn(CardinalityInterval.ZERO_ONE)) {
                        implicitName = ((ANameUnit) unit).getIdentifier()
                                .getText();
                    }
                }
                else {
                    implicitName = ((ADanglingElement) this.declaration)
                            .getIdentifier().getText();
                }

                return implicitName;
            }

            @Override
            public String getExplicitName() {

                String explicitName = null;
                TElementName elementName = null;

                switch (getElementType()) {
                case NORMAL:
                    elementName = ((ANormalElement) this.declaration)
                            .getElementName();
                    break;
                case DANGLING:
                    elementName = ((ADanglingElement) this.declaration)
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

                if (this.nameToken == null) {
                    if (getExplicitName() != null
                            && getExplicitName().equals(this.name)) {
                        if (getElementType() == ElementType.NORMAL) {
                            this.nameToken = ((ANormalElement) this.declaration)
                                    .getElementName();
                        }
                        else {
                            this.nameToken = ((ANormalElement) this.declaration)
                                    .getElementName();
                        }

                    }
                    else if (getImplicitName().equals(this.name)) {

                        if (getElementType() == ElementType.NORMAL) {
                            this.nameToken = ((ANameUnit) ((ANormalElement) this.declaration)
                                    .getUnit()).getIdentifier();
                        }
                        else {
                            this.nameToken = ((ADanglingElement) this.declaration)
                                    .getElementName();
                        }
                    }
                }

                return this.nameToken;
            }

            @Override
            public String getNameType() {

                if (getElementType() == ElementType.NORMAL) {
                    return "parser normal element";
                }
                else {
                    return "parser dangling element";
                }

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

                visitor.visitParserSingleElement(this);

            }
        }

        public static class DoubleElement
                extends ParserElement {

            private final Node declaration;

            private String name;

            private IReferencable leftReference;

            private IReferencable rightReference;

            private Token elementToken;

            private String element;

            private CardinalityInterval cardinality;

            private Type.SimpleType type;

            public DoubleElement(
                    ASeparatedElement declaration,
                    Grammar grammar,
                    ParserAlternative alternative) {

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
                    ParserAlternative alternative) {

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

                switch (getElementType()) {
                case SEPARATED:
                    this.type = new Type.SimpleType.SeparatedType(
                            extractor.getLeftText(), extractor.getRightText(),
                            this.cardinality);
                    break;
                case ALTERNATED:
                    this.type = new Type.SimpleType.AlternatedType(
                            extractor.getLeftText(), extractor.getRightText(),
                            this.cardinality);
                    break;
                }
            }

            @Override
            public Node getDeclaration() {

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
            public String getNameType() {

                if (getElementType() == ElementType.SEPARATED) {
                    return "parser separated element";
                }
                else {
                    return "parser alternated element";
                }

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

                visitor.visitParserDoubleElement(this);
            }
        }

        private static class InformationExtractor
                extends DepthFirstAdapter {

            private String text = "";

            private String leftText = "";

            private String rightText = "";

            private Token token;

            public InformationExtractor(
                    Parser.ParserElement.SingleElement element) {

                element.getDeclaration().apply(this);
            }

            public InformationExtractor(
                    Parser.ParserElement.DoubleElement element) {

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
            public void caseADanglingElement(
                    ADanglingElement node) {

                this.text = node.getIdentifier().getText();
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

        public String getName_CamelCase() {

            String name = getName();
            return name == null ? null : to_CamelCase(name);
        }
    }

    public static abstract class ParserPriority
            implements IVisitableGrammarPart {

        private final Grammar grammar;

        private final ParserProduction production;

        public ParserPriority(
                Grammar grammar,
                ParserProduction production) {

            if (production == null) {
                throw new InternalException("production may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
            this.production = production;
        }

        public ParserProduction getProduction() {

            return this.production;
        }

        public abstract List<Parser.ParserAlternative> getAlternatives();

        public abstract Token getLocation();

        public static class LeftPriority
                extends ParserPriority {

            private final ALeftParserPriority declaration;

            private List<Parser.ParserAlternative> alternatives = new LinkedList<Parser.ParserAlternative>();

            public LeftPriority(
                    ALeftParserPriority declaration,
                    Grammar grammar,
                    ParserProduction production) {

                super(grammar, production);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;

            }

            public ALeftParserPriority getDeclaration() {

                return this.declaration;
            }

            public void addAlternative(
                    Parser.ParserAlternative alternative) {

                if (!getProduction().getAlternatives().contains(alternative)) {
                    throw new InternalException(
                            "the alternative added to a priority must belong to the production");
                }

                this.alternatives.add(alternative);
            }

            @Override
            public List<Parser.ParserAlternative> getAlternatives() {

                return this.alternatives;
            }

            @Override
            public void apply(
                    IGrammarVisitor visitor) {

                visitor.visitLeftParserPriority(this);

            }

            @Override
            public Token getLocation() {

                return this.declaration.getLeftKeyword();
            }

        }

        public static class RightPriority
                extends ParserPriority {

            private final ARightParserPriority declaration;

            private List<Parser.ParserAlternative> alternatives = new LinkedList<Parser.ParserAlternative>();

            public RightPriority(
                    ARightParserPriority declaration,
                    Grammar grammar,
                    ParserProduction production) {

                super(grammar, production);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;
            }

            public ARightParserPriority getDeclaration() {

                return this.declaration;
            }

            public void addAlternative(
                    Parser.ParserAlternative alternative) {

                if (!getProduction().getAlternatives().contains(alternative)) {
                    throw new InternalException(
                            "the alternative added to a priority must belong to the production");
                }

                this.alternatives.add(alternative);
            }

            @Override
            public List<Parser.ParserAlternative> getAlternatives() {

                return this.alternatives;
            }

            @Override
            public void apply(
                    IGrammarVisitor visitor) {

                visitor.visitRightParserPriority(this);

            }

            @Override
            public Token getLocation() {

                return this.declaration.getRightKeyword();
            }

        }

        public static class UnaryPriority
                extends ParserPriority {

            private final AUnaryParserPriority declaration;

            private List<Parser.ParserAlternative> alternatives = new LinkedList<Parser.ParserAlternative>();

            public UnaryPriority(
                    AUnaryParserPriority declaration,
                    Grammar grammar,
                    ParserProduction production) {

                super(grammar, production);

                if (declaration == null) {
                    throw new InternalException("declaration may not be null");
                }

                this.declaration = declaration;
            }

            public AUnaryParserPriority getDeclaration() {

                return this.declaration;
            }

            public void addAlternative(
                    Parser.ParserAlternative alternative) {

                if (!getProduction().getAlternatives().contains(alternative)) {
                    throw new InternalException(
                            "the alternative added to a priority must belong to the production");
                }

                this.alternatives.add(alternative);
            }

            @Override
            public List<Parser.ParserAlternative> getAlternatives() {

                return this.alternatives;
            }

            @Override
            public void apply(
                    IGrammarVisitor visitor) {

                visitor.visitUnaryParserPriority(this);

            }

            @Override
            public Token getLocation() {

                return this.declaration.getUnaryKeyword();
            }

        }
    }
}
