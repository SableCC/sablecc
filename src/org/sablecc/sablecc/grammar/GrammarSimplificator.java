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

package org.sablecc.sablecc.grammar;

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.Parser.ParserAlternative;
import org.sablecc.sablecc.core.Parser.ParserElement.AlternatedElement;
import org.sablecc.sablecc.core.Parser.ParserElement.SeparatedElement;
import org.sablecc.sablecc.core.Parser.ParserPriority.LeftPriority;
import org.sablecc.sablecc.core.Parser.ParserPriority.RightPriority;
import org.sablecc.sablecc.core.Parser.ParserPriority.UnaryPriority;
import org.sablecc.sablecc.core.Parser.ParserProduction;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.grammar.transformation.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public class GrammarSimplificator
        extends GrammarVisitor {

    private List<Element> elements;

    private List<Alternative> alternatives;

    private static SGrammar grammar;

    private Production production;

    public GrammarSimplificator(
            SGrammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar shouldn't be null");
        }

        GrammarSimplificator.grammar = grammar;

    }

    @Override
    public void visitParserProduction(
            ParserProduction node) {

        this.alternatives = new LinkedList<Alternative>();

        if (node.getContext() instanceof Context.NamedContext) {
            throw new InternalException("Context are not supported yet");
        }

        String prodName = node.getName();
        this.production = GrammarSimplificator.grammar.getProduction(prodName);

        for (Parser.ParserPriority priority : node.getPriorities()) {
            priority.apply(this);
        }

        for (Parser.ParserAlternative alternative : node.getAlternatives()) {
            alternative.apply(this);
        }

        this.production.addAlternatives(this.alternatives);
    }

    @Override
    public void visitLeftParserPriority(
            LeftPriority node) {

        this.production.addPriority(new Priority.LeftPriority(this.production));
    }

    @Override
    public void visitRightParserPriority(
            RightPriority node) {

        this.production
                .addPriority(new Priority.RightPriority(this.production));
    }

    @Override
    public void visitUnaryParserPriority(
            UnaryPriority node) {

        this.production
                .addPriority(new Priority.UnaryPriority(this.production));
    }

    @Override
    public void visitParserAlternative(
            ParserAlternative node) {

        this.elements = new LinkedList<Element>();

        for (Parser.ParserElement element : node.getElements()) {
            element.apply(this);
        }

        Alternative alternative = new Alternative(this.production,
                this.elements);

        if (node.getTransformation() != null) {
            new AlternativeTransformationBuilder(alternative,
                    node.getTransformation());
        }
        else {
            LinkedList<SAlternativeTransformationElement> transformationElements = new LinkedList<SAlternativeTransformationElement>();

            LinkedList<SAlternativeTransformationElement> newElements = new LinkedList<SAlternativeTransformationElement>();

            for (Element element : alternative.getElements()) {
                newElements
                        .add(new SAlternativeTransformationElement.ReferenceElement(
                                element));
            }
            transformationElements
                    .add(new SAlternativeTransformationElement.NewElement(node,
                            newElements));
            SAlternativeTransformation transformation = new SAlternativeTransformation(
                    alternative, transformationElements);
            alternative.addTransformation(transformation);
        }

        // Resolve priority references
        for (int i = 0; i < node.getProduction().getPriorities().size(); i++) {

            if (node.getProduction().getPriorities().get(i).getAlternatives()
                    .contains(node)) {
                this.production.getPriorities().get(i)
                        .addAlternative(alternative);
            }
        }

        this.alternatives.add(alternative);
    }

    @Override
    public void visitParserNormalElement(
            Parser.ParserElement.NormalElement node) {

        if (!node.getCardinality().equals(CardinalityInterval.ZERO_ZERO)) {
            Element simpleElement;

            if (node.getReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) node.getDeclaration().getUnit())
                        .getIdentifier().getText();

                simpleElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                simpleElement = new Element.TokenElement(node.getDeclaration()
                        .getUnit());
            }

            if (node.getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                this.elements.add(simpleElement);
            }
            else {
                Element complexElement = new Element.ProductionElement(
                        newNormalProduction(node, simpleElement,
                                node.getCardinality()));
                this.elements.add(complexElement);
            }

        }

    }

    @Override
    public void visitParserSeparatedElement(
            SeparatedElement node) {

        if (!node.getCardinality().equals(CardinalityInterval.ZERO_ZERO)) {
            Element leftSimpleElement;

            if (node.getLeftReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) node.getDeclaration().getLeft())
                        .getIdentifier().getText();

                leftSimpleElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                leftSimpleElement = new Element.TokenElement(node
                        .getDeclaration().getLeft());
            }

            Element rightSimpleElement;

            if (node.getRightReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) node.getDeclaration().getRight())
                        .getIdentifier().getText();

                rightSimpleElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                rightSimpleElement = new Element.TokenElement(node
                        .getDeclaration().getRight());
            }

            if (node.getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                this.elements.add(leftSimpleElement);
            }
            else {
                Element complexElement = new Element.ProductionElement(
                        newSeparatedProduction(node, leftSimpleElement,
                                rightSimpleElement, node.getCardinality()));
                this.elements.add(complexElement);
            }
        }
    }

    @Override
    public void visitParserAlternatedELement(
            AlternatedElement node) {

        if (!node.getCardinality().equals(CardinalityInterval.ZERO_ZERO)) {
            Element leftSimpleElement;

            if (node.getLeftReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) node.getDeclaration().getLeft())
                        .getIdentifier().getText();

                leftSimpleElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                leftSimpleElement = new Element.TokenElement(node
                        .getDeclaration().getLeft());
            }

            Element rightSimpleElement;

            if (node.getRightReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) node.getDeclaration().getRight())
                        .getIdentifier().getText();

                rightSimpleElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                rightSimpleElement = new Element.TokenElement(node
                        .getDeclaration().getRight());
            }

            if (node.getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                this.elements.add(leftSimpleElement);
                this.elements.add(rightSimpleElement);
            }
            else {
                Element complexElement = new Element.ProductionElement(
                        newAlternatedProduction(node, leftSimpleElement,
                                rightSimpleElement, node.getCardinality()));
                this.elements.add(complexElement);
            }
        }
    }

    private static Production newNormalProduction(
            Parser.ParserElement.NormalElement node,
            Element element,
            CardinalityInterval cardinality) {

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newNormalProduction");
        }

        Production production;
        String name;

        if (cardinality.equals(CardinalityInterval.ZERO_ONE)) {
            name = element.getTypeName() + "_?";
        }
        else if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            name = element.getTypeName() + "_+";
        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name = element.getTypeName() + "_*";
        }
        else if (cardinality.isANumber()) {
            BigInteger number = cardinality.getLowerBound().getValue();
            name = element.getTypeName() + "_" + number.toString();
        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            name = element.getTypeName() + "_" + lowerBoundValue.toString()
                    + "...";
        }
        else {

            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            name = element.getTypeName() + "_" + lowerBoundValue.toString()
                    + ".." + upperBoundValue.toString();
        }

        if (grammar.containsProduction(name)) {
            production = grammar.getProduction(name);
        }
        else {
            production = new NormalProductionBuilder(node, element,
                    cardinality, grammar).getNewProduction();
        }

        return production;

    }

    private static Production newSeparatedProduction(
            Parser.ParserElement.SeparatedElement node,
            Element leftElement,
            Element rightElement,
            CardinalityInterval cardinality) {

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newNormalProduction");
        }

        String name;

        if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            name = leftElement.getTypeName() + " Separator "
                    + rightElement.getTypeName() + "_+";

        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name = leftElement.getTypeName() + " Separator "
                    + rightElement.getTypeName() + "_*";

        }
        else if (cardinality.equals(cardinality.isANumber())) {
            BigInteger number = cardinality.getLowerBound().getValue();
            name = leftElement + " Separator " + rightElement + "_"
                    + number.toString();
        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();

            name = leftElement.getTypeName() + " Separator "
                    + rightElement.getTypeName() + "_"
                    + lowerBoundValue.toString() + "...";
        }
        else {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            name = leftElement.getTypeName() + " Separator " + rightElement
                    + "_" + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

        }

        Production production;

        if (GrammarSimplificator.grammar.containsProduction(name)) {
            production = grammar.getProduction(name);
        }
        else {
            production = new SeparatedProductionBuilder(node, leftElement,
                    rightElement, cardinality, grammar).getNewProduction();
        }

        return production;
    }

    private static Production newAlternatedProduction(
            Parser.ParserElement node,
            Element leftElement,
            Element rightElement,
            CardinalityInterval cardinality) {

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newAlternatedProduction");
        }

        String name;

        if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            name = leftElement.getTypeName() + rightElement.getTypeName()
                    + "_+";
        }

        if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name = leftElement.getTypeName() + rightElement.getTypeName()
                    + "_*";
        }
        else if (cardinality.isANumber()) {
            BigInteger number = cardinality.getLowerBound().getValue();
            name = leftElement.getTypeName() + rightElement.getTypeName() + "_"
                    + number.toString();

        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            name = leftElement.getTypeName() + rightElement.getTypeName() + "_"
                    + lowerBoundValue.toString() + "...";
        }
        else {

            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            name = leftElement.getTypeName() + rightElement.getTypeName() + "_"
                    + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

        }

        if (grammar.containsProduction(name)) {
            return grammar.getProduction(name);
        }
        else {
            return new AlternatedProductionBuilder(node, leftElement,
                    rightElement, cardinality, grammar).getNewProduction();
        }
    }

    private static class NormalProductionBuilder {

        private final Parser.ParserElement.NormalElement parserElement;

        private final Element sElement;

        private final CardinalityInterval cardinality;

        private Production newProduction;

        private SGrammar grammar;

        public NormalProductionBuilder(
                Parser.ParserElement.NormalElement parserElement,
                Element sElement,
                CardinalityInterval cardinality,
                SGrammar grammar) {

            this.parserElement = parserElement;
            this.sElement = sElement;
            this.cardinality = cardinality;
            this.grammar = grammar;

            if (cardinality.equals(CardinalityInterval.ZERO_ONE)) {

                qmarkCase();
            }
            else if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
                plusCase();
            }
            else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
                starCase();
            }
            else if (cardinality.isANumber()) {
                numberCase();
            }
            else if (cardinality.upperBoundIsInfinite()) {
                atLeastCase();
            }
            else {
                intervalCase();
            }

            this.newProduction.addTransformation(new SProductionTransformation(
                    this.newProduction, this.parserElement.getElement(),
                    this.parserElement, this.cardinality));

            this.grammar.addProduction(this.newProduction);

        }

        public Production getNewProduction() {

            return this.newProduction;
        }

        private void qmarkCase() {

            String qmarkName = this.sElement.getTypeName() + "_?";

            Production qmarkProd = new Production(
                    this.grammar.getNextProductionId(), qmarkName);

            qmarkProd.addAlternative(newAlternative(qmarkProd,
                    this.sElement.clone()));
            qmarkProd.addAlternative(new Alternative(qmarkProd));

            this.newProduction = qmarkProd;

        }

        private void plusCase() {

            String plusName = this.sElement.getTypeName() + "_+";

            Production plusProd = new Production(
                    this.grammar.getNextProductionId(), plusName);
            // p = a_+ a | a;

            // First alternative
            LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            Element firstElement = new Element.ProductionElement(plusProd);
            firstAlternativeElements.add(firstElement);
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement));

            Element secondElement = this.sElement.clone();
            firstAlternativeElements.add(secondElement);
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            secondElement));

            plusProd.addAlternative(newListAlternative(plusProd,
                    firstAlternativeElements, firstAltTransformationElements));

            // Second alternative
            LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> secondAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            secondAlternativeElements.add(this.sElement.clone());
            secondAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            secondAlternativeElements.get(0)));
            plusProd.addAlternative(newListAlternative(plusProd,
                    secondAlternativeElements, secondAltTransformationElements));

            this.newProduction = plusProd;

        }

        private void starCase() {

            String starName = this.sElement.getTypeName() + "_*";

            Production starProd = new Production(
                    this.grammar.getNextProductionId(), starName);

            // First Alternative
            String plusName = this.sElement.getTypeName() + "_+";

            Element firstElement;
            if (this.grammar.containsProduction(plusName)) {
                firstElement = new Element.ProductionElement(
                        this.grammar.getProduction(plusName));
            }
            else {
                firstElement = new Element.ProductionElement(plusName,
                        newNormalProduction(this.parserElement, this.sElement,
                                CardinalityInterval.ONE_OR_MORE));
            }

            LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            firstAlternativeElements.add(firstElement);
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement));

            starProd.addAlternative(newListAlternative(starProd,
                    firstAlternativeElements, firstAltTransformationElements));

            // Second Alternative
            starProd.addAlternative(new Alternative(starProd));

            this.newProduction = starProd;

        }

        private void numberCase() {

            BigInteger number = this.cardinality.getLowerBound().getValue();
            String numberName = this.sElement.getTypeName() + "_"
                    + number.toString();

            Production numberProd = new Production(
                    this.grammar.getNextProductionId(), numberName);

            if (number.compareTo(BigInteger.valueOf(2L)) == 0) {

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(this.sElement.clone());
                alternativeElements.add(this.sElement.clone());

                LinkedList<SAlternativeTransformationListElement> alternativeTransformationElement = new LinkedList<SAlternativeTransformationListElement>();
                alternativeTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(0)));
                alternativeTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        alternativeElements, alternativeTransformationElement));
            }
            else {
                String previousNumberName = this.sElement.getTypeName() + "_"
                        + number.subtract(BigInteger.ONE).toString();

                Element firstElement;
                if (this.grammar.containsProduction(previousNumberName)) {
                    firstElement = new Element.ProductionElement(
                            this.grammar.getProduction(previousNumberName));
                }
                else {
                    Production previousNumberProd = newNormalProduction(
                            this.parserElement, this.sElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().subtract(BigInteger.ONE)));

                    firstElement = new Element.ProductionElement(
                            previousNumberProd);
                }

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(firstElement);
                alternativeElements.add(this.sElement.clone());

                LinkedList<SAlternativeTransformationListElement> alternativeTransformationElement = new LinkedList<SAlternativeTransformationListElement>();
                alternativeTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                firstElement));
                alternativeTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        alternativeElements, alternativeTransformationElement));
            }

            this.newProduction = numberProd;

        }

        private void atLeastCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            String atLeastName = this.sElement.getTypeName() + "_"
                    + lowerBoundValue.toString() + "...";

            Production atLeastProd = new Production(
                    this.grammar.getNextProductionId(), atLeastName);

            String numberName = this.sElement.getTypeName() + "_"
                    + lowerBoundValue.toString();

            Element firstElement;
            if (this.grammar.containsProduction(numberName)) {
                firstElement = new Element.ProductionElement(
                        this.grammar.getProduction(atLeastName));
            }
            else {
                firstElement = new Element.ProductionElement(
                        newNormalProduction(
                                this.parserElement,
                                this.sElement,
                                new CardinalityInterval(this.cardinality
                                        .getLowerBound(), this.cardinality
                                        .getLowerBound())));
            }

            String starName = this.sElement.getTypeName() + "_*";

            Element secondElement;
            if (this.grammar.containsProduction(starName)) {
                secondElement = new Element.ProductionElement(
                        this.grammar.getProduction(starName));
            }
            else {
                secondElement = new Element.ProductionElement(
                        newNormalProduction(this.parserElement, this.sElement,
                                CardinalityInterval.ZERO_OR_MORE));
            }

            LinkedList<Element> alternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            alternativeElements.add(firstElement);
            alternativeElements.add(secondElement);

            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement));
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            secondElement));

            atLeastProd.addAlternative(newListAlternative(atLeastProd,
                    alternativeElements, altTransformationElements));

            this.newProduction = atLeastProd;

        }

        private void intervalCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            BigInteger upperBoundValue = this.cardinality.getUpperBound()
                    .getValue();

            String intervalName = this.sElement.getTypeName() + "_"
                    + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

            Production intervalProd = new Production(
                    this.grammar.getNextProductionId(), intervalName);

            // p^(0..n) = p^(1..n) | Empty
            if (lowerBoundValue.equals(BigInteger.ZERO)) {

                // First Alternative

                String plusOneIntervalName = this.parserElement.getName() + "_"
                        + lowerBoundValue.add(BigInteger.ONE).toString() + ".."
                        + upperBoundValue.toString();

                LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
                LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
                Element firstElement;

                if (this.grammar.containsProduction(plusOneIntervalName)) {
                    firstElement = new Element.ProductionElement(
                            this.grammar.getProduction(plusOneIntervalName));

                    firstAltTransformationElements
                            .add(new SAlternativeTransformationListElement.NormalListElement(
                                    firstElement));
                }
                else {
                    firstElement = new Element.ProductionElement(
                            newNormalProduction(this.parserElement,
                                    this.sElement, new CardinalityInterval(
                                            Bound.ONE, new Bound(
                                                    upperBoundValue))));
                }

                firstAlternativeElements.add(firstElement);
                firstAltTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        firstAlternativeElements,
                        firstAltTransformationElements));

                // Second alternative

                intervalProd.addAlternative(new Alternative(intervalProd));
            }
            // p^(1..2) = p | pp
            else if (lowerBoundValue.equals(BigInteger.ONE)
                    && upperBoundValue.equals(BigInteger.valueOf(2L))) {

                // First alternative
                LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
                LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

                firstAlternativeElements.add(this.sElement.clone());
                firstAltTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                firstAlternativeElements.get(0)));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        firstAlternativeElements,
                        firstAltTransformationElements));

                // Second alternative
                LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
                LinkedList<SAlternativeTransformationListElement> secondAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

                secondAlternativeElements.add(this.sElement.clone());
                secondAlternativeElements.add(this.sElement.clone());
                secondAltTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                secondAlternativeElements.get(0)));
                secondAltTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                secondAlternativeElements.get(1)));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        secondAlternativeElements,
                        secondAltTransformationElements));
            }
            // p^(1..n) = p^(1..n-1) p
            else if (lowerBoundValue.equals(BigInteger.ONE)) {
                String smallerIntervalName = this.parserElement.getName() + "_"
                        + lowerBoundValue.toString() + ".."
                        + upperBoundValue.subtract(BigInteger.ONE).toString();

                Element firstElement;

                if (this.grammar.containsProduction(smallerIntervalName)) {
                    firstElement = new Element.ProductionElement(
                            this.grammar.getProduction(smallerIntervalName));
                }
                else {
                    firstElement = new Element.ProductionElement(
                            newNormalProduction(this.parserElement,
                                    this.sElement, new CardinalityInterval(
                                            this.cardinality.getLowerBound(),
                                            this.cardinality.getUpperBound()
                                                    .subtract(BigInteger.ONE))));
                }

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(firstElement);
                alternativeElements.add(this.sElement.clone());

                LinkedList<SAlternativeTransformationListElement> alternativeTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
                alternativeTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement));
                alternativeTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1)));

                intervalProd
                        .addAlternative(newListAlternative(intervalProd,
                                alternativeElements,
                                alternativeTransformationElements));
            }
            // p^(m..n) with n > 1 = p^m p^(0..(n-m))
            else {
                String lowerNumberName = this.sElement.getTypeName() + "_"
                        + lowerBoundValue.toString();

                Element firstElement;

                if (this.grammar.containsProduction(lowerNumberName)) {
                    firstElement = new Element.ProductionElement(
                            this.grammar.getProduction(lowerNumberName));
                }
                else {
                    firstElement = new Element.ProductionElement(
                            newNormalProduction(this.parserElement,
                                    this.sElement, new CardinalityInterval(
                                            this.cardinality.getLowerBound(),
                                            this.cardinality.getLowerBound())));
                }

                String zeroToIntervalWidth = this.sElement.getTypeName()
                        + "_0.."
                        + upperBoundValue.subtract(lowerBoundValue).toString();

                Element secondElement;

                if (this.grammar.containsProduction(zeroToIntervalWidth)) {
                    secondElement = new Element.ProductionElement(
                            this.grammar.getProduction(zeroToIntervalWidth));
                }
                else {
                    secondElement = new Element.ProductionElement(
                            newNormalProduction(this.parserElement,
                                    this.sElement,
                                    new CardinalityInterval(Bound.ZERO,
                                            this.cardinality.getLowerBound())));
                }

                LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
                LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

                firstAlternativeElements.add(firstElement);
                firstAlternativeElements.add(secondElement);

                firstAltTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement));
                firstAltTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                secondElement));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        firstAlternativeElements,
                        firstAltTransformationElements));
            }

            this.newProduction = intervalProd;

        }

        private Alternative newAlternative(
                Production production,
                Element element) {

            LinkedList<Element> elements = new LinkedList<Element>();
            elements.add(element);
            return newAlternative(production, elements);
        }

        private Alternative newAlternative(
                Production production,
                LinkedList<Element> elements) {

            Alternative alternative = new Alternative(production, elements);
            alternative.addTransformation(new SAlternativeTransformation(
                    alternative, elements));
            return alternative;
        }

        private Alternative newListAlternative(
                Production production,
                LinkedList<Element> elements,
                LinkedList<SAlternativeTransformationListElement> transformationElements) {

            Alternative alternative = new Alternative(production, elements);
            alternative.addTransformation(new SAlternativeTransformation(
                    transformationElements, alternative));
            return alternative;
        }

    }

    private static class SeparatedProductionBuilder {

        private final Element sLeftElement;

        private final Element sRightElement;

        private final CardinalityInterval cardinality;

        private Production newProduction;

        private SGrammar grammar;

        private final Parser.ParserElement.SeparatedElement parserElement;

        public SeparatedProductionBuilder(
                Parser.ParserElement.SeparatedElement parserElement,
                Element sLeftElement,
                Element sRightElement,
                CardinalityInterval cardinality,
                SGrammar grammar) {

            this.parserElement = parserElement;
            this.sLeftElement = sLeftElement;
            this.sRightElement = sRightElement;
            this.cardinality = cardinality;
            this.grammar = grammar;

            if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
                plusCase();
            }
            else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
                starCase();
            }
            else if (cardinality.isANumber()) {
                numberCase();
            }
            else if (cardinality.upperBoundIsInfinite()) {
                atLeastCase();
            }
            else {
                intervalCase();
            }

            this.newProduction.addTransformation(new SProductionTransformation(
                    this.newProduction, this.sLeftElement.getTypeName(),
                    this.sRightElement.getTypeName(), parserElement
                            .getLeftReference(), parserElement
                            .getRightReference(), cardinality, true));

            this.grammar.addProduction(this.newProduction);

        }

        public Production getNewProduction() {

            return this.newProduction;
        }

        private void plusCase() {

            String plusName = this.sLeftElement.getTypeName() + " Separator "
                    + this.sRightElement.getTypeName() + "_+";

            Production plusProd = new Production(
                    this.grammar.getNextProductionId(), plusName);
            Element firstElement;

            if (this.sLeftElement instanceof Element.TokenElement) {
                firstElement = new Element.TokenElement(
                        this.sLeftElement.getTypeName());
            }
            else {
                firstElement = new Element.ProductionElement(
                        ((Element.ProductionElement) this.sLeftElement)
                                .getReference());
            }

            String alternatedStarName = this.sRightElement.getTypeName()
                    + this.sLeftElement.getTypeName() + "_*";

            Element secondElement;

            if (GrammarSimplificator.grammar
                    .containsProduction(alternatedStarName)) {
                secondElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar
                                .getProduction(alternatedStarName));
            }
            else {
                secondElement = new Element.ProductionElement(
                        newAlternatedProduction(this.parserElement,
                                this.sLeftElement, this.sRightElement,
                                CardinalityInterval.ZERO_OR_MORE));
            }

            LinkedList<Element> alternativeElements = new LinkedList<Element>();
            alternativeElements.add(firstElement);
            alternativeElements.add(secondElement);

            LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            firstElement));
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            secondElement));

            plusProd.addAlternative(newListAlternative(plusProd,
                    alternativeElements, altTransformationElements));

            this.newProduction = plusProd;
        }

        private void starCase() {

            String starName = this.sLeftElement.getTypeName() + " Separator "
                    + this.sRightElement.getTypeName() + "_*";

            Production starProd = new Production(
                    this.grammar.getNextProductionId(), starName);

            // First alternative

            String separatedPlusName = this.sLeftElement.getTypeName()
                    + " Separator " + this.sRightElement.getTypeName() + "_+";

            Element firstElement;

            if (this.grammar.containsProduction(separatedPlusName)) {
                firstElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar
                                .getProduction(separatedPlusName));
            }
            else {
                firstElement = new Element.ProductionElement(
                        newAlternatedProduction(this.parserElement,
                                this.sRightElement, this.sLeftElement,
                                CardinalityInterval.ONE_OR_MORE));
            }

            LinkedList<Element> firstAltElements = new LinkedList<Element>();
            firstAltElements.add(firstElement);

            LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement));

            starProd.addAlternative(newListAlternative(starProd,
                    firstAltElements, firstAltTransformationElements));

            // Second alternative
            starProd.addAlternative(new Alternative(starProd));

            this.newProduction = starProd;

        }

        private void numberCase() {

            BigInteger number = this.cardinality.getLowerBound().getValue();
            String numberName = this.sLeftElement + " Separator "
                    + this.sRightElement + "_" + number.toString();

            LinkedList<Element> altElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            Production numberProd = new Production(
                    this.grammar.getNextProductionId(), numberName);

            if (number.compareTo(BigInteger.valueOf(2L)) == 0) {

                altElements.add(this.sLeftElement.clone());
                altElements.add(this.sRightElement.clone());
                altElements.add(this.sLeftElement.clone());

                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(0)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(1)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(2)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        altElements, altTransformationElements));
            }
            else {
                String previousNumberName = this.sLeftElement + " Separator "
                        + this.sRightElement + "_"
                        + number.subtract(BigInteger.ONE).toString();

                Element firstElement;

                if (GrammarSimplificator.grammar
                        .containsProduction(previousNumberName)) {
                    firstElement = new Element.ProductionElement(
                            GrammarSimplificator.grammar
                                    .getProduction(previousNumberName));
                }
                else {
                    Production previousNumberProd = newSeparatedProduction(
                            this.parserElement, this.sLeftElement,
                            this.sRightElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().subtract(BigInteger.ONE)));

                    firstElement = new Element.ProductionElement(
                            previousNumberProd);
                }

                altElements.add(firstElement);
                altElements.add(this.sLeftElement.clone());
                altElements.add(this.sRightElement.clone());

                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(1)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(2)));
            }

            numberProd.addAlternative(newListAlternative(numberProd,
                    altElements, altTransformationElements));

            this.newProduction = numberProd;

        }

        private void atLeastCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();

            String atLeastName = this.sLeftElement.getTypeName()
                    + " Separator " + this.sRightElement.getTypeName() + "_"
                    + lowerBoundValue.toString() + "...";

            Production atLeastProd = new Production(
                    this.grammar.getNextProductionId(), atLeastName);

            LinkedList<Element> altElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            String numberName = this.sLeftElement.getTypeName() + " Separator "
                    + this.sRightElement.getTypeName() + "_"
                    + lowerBoundValue.toString();

            Element firstElement;

            if (this.grammar.containsProduction(numberName)) {
                firstElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar.getProduction(atLeastName));
            }
            else {
                firstElement = new Element.ProductionElement(
                        newSeparatedProduction(
                                this.parserElement,
                                this.sLeftElement,
                                this.sRightElement,
                                new CardinalityInterval(this.cardinality
                                        .getLowerBound(), this.cardinality
                                        .getLowerBound())));
            }

            String starName = this.sLeftElement.getTypeName() + " Separator "
                    + this.sRightElement.getTypeName() + "_*";

            Element secondElement;

            if (this.grammar.containsProduction(starName)) {
                secondElement = new Element.ProductionElement(
                        GrammarSimplificator.grammar.getProduction(starName));
            }
            else {
                secondElement = new Element.ProductionElement(
                        newAlternatedProduction(this.parserElement,
                                this.sRightElement, this.sLeftElement,
                                CardinalityInterval.ZERO_OR_MORE));
            }

            altElements.add(firstElement);
            altElements.add(secondElement);

            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement));
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            secondElement));

            atLeastProd.addAlternative(newListAlternative(atLeastProd,
                    altElements, altTransformationElements));

            this.newProduction = atLeastProd;

        }

        private void intervalCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            BigInteger upperBoundValue = this.cardinality.getUpperBound()
                    .getValue();

            String intervalName = this.sLeftElement.getTypeName()
                    + " Separator " + this.sRightElement + "_"
                    + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

            Production intervalProd = new Production(
                    this.grammar.getNextProductionId(), intervalName);

            // (a Sep b)^(0..n) = (a Sepb b)^(1..n) | Empty
            if (lowerBoundValue.equals(BigInteger.ZERO)) {

                LinkedList<Element> firstAlternativeElement = new LinkedList<Element>();
                LinkedList<SAlternativeTransformationListElement> firstAltTransformationElement = new LinkedList<SAlternativeTransformationListElement>();

                // (a Sep b)^(0..1) = a?
                if (upperBoundValue.equals(BigInteger.ONE)) {
                    String qmarkLeftName = this.sLeftElement.getTypeName()
                            + "_?";

                    Element firstElement;

                    if (this.grammar.containsProduction(qmarkLeftName)) {
                        firstElement = new Element.ProductionElement(
                                this.grammar.getProduction(qmarkLeftName));
                    }
                    else {
                        Production qmarkProd = new Production(
                                this.grammar.getNextProductionId(),
                                qmarkLeftName);

                        qmarkProd.addAlternative(newAlternative(qmarkProd,
                                this.sLeftElement.clone()));
                        qmarkProd.addAlternative(new Alternative(qmarkProd));

                        firstElement = new Element.ProductionElement(qmarkProd);
                    }

                    firstAlternativeElement.add(firstElement);
                    firstAltTransformationElement
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    firstElement));

                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, firstAlternativeElement,
                            firstAltTransformationElement));
                }
                else {

                    // First alternative

                    String plusOneIntervalName = this.sLeftElement
                            .getTypeName()
                            + " Separator "
                            + this.sRightElement
                            + "_"
                            + lowerBoundValue.add(BigInteger.ONE).toString()
                            + ".." + upperBoundValue.toString();

                    Element firstElement;

                    if (this.grammar.containsProduction(plusOneIntervalName)) {
                        firstElement = new Element.ProductionElement(
                                this.grammar.getProduction(plusOneIntervalName));
                    }
                    else {
                        firstElement = new Element.ProductionElement(
                                newSeparatedProduction(
                                        this.parserElement,
                                        this.sLeftElement,
                                        this.sRightElement,
                                        new CardinalityInterval(Bound.ONE,
                                                this.cardinality
                                                        .getUpperBound())));
                    }

                    firstAlternativeElement.add(firstElement);

                    firstAltTransformationElement
                            .add(new SAlternativeTransformationListElement.NormalListElement(
                                    firstElement));

                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, firstAlternativeElement,
                            firstAltTransformationElement));

                    // Second alternative

                    intervalProd.addAlternative(new Alternative(intervalProd));
                }

            }
            // (a Sep b)^(1..n) = a (b a)^(0..n-1)
            else if (lowerBoundValue.equals(BigInteger.ONE)) {
                Element firstElement = this.sLeftElement.clone();

                String alternatedZeroToIntervalWidth = this.sRightElement
                        .getTypeName()
                        + this.sLeftElement.getTypeName()
                        + "_0.."
                        + upperBoundValue.subtract(lowerBoundValue).toString();

                Element secondElement;

                if (this.grammar
                        .containsProduction(alternatedZeroToIntervalWidth)) {
                    secondElement = new Element.ProductionElement(
                            this.grammar
                                    .getProduction(alternatedZeroToIntervalWidth));
                }
                else {
                    secondElement = new Element.ProductionElement(
                            newAlternatedProduction(this.parserElement,
                                    this.sRightElement, this.sLeftElement,
                                    new CardinalityInterval(Bound.ZERO,
                                            this.cardinality.getUpperBound()
                                                    .subtract(lowerBoundValue))));

                }

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(firstElement);
                alternativeElements.add(secondElement);

                LinkedList<SAlternativeTransformationListElement> alternativeTransformationListElement = new LinkedList<SAlternativeTransformationListElement>();
                alternativeTransformationListElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                firstElement));
                alternativeTransformationListElement
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                secondElement));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        alternativeElements,
                        alternativeTransformationListElement));

            }
            // (a Sep b)^(m..n) = (a Sep b)^m (b a)^(0..(n-m))
            else {
                String lowerNumberName = this.sLeftElement.getTypeName()
                        + " Separator " + this.sRightElement + "_"
                        + lowerBoundValue.toString();

                Element firstElement;

                if (this.grammar.containsProduction(lowerNumberName)) {
                    firstElement = new Element.ProductionElement(
                            this.grammar.getProduction(lowerNumberName));
                }
                else {
                    firstElement = new Element.ProductionElement(
                            newSeparatedProduction(
                                    this.parserElement,
                                    this.sLeftElement,
                                    this.sRightElement,
                                    new CardinalityInterval(this.cardinality
                                            .getLowerBound(), this.cardinality
                                            .getLowerBound())));
                }

                String zeroToLowerBoundName = this.sRightElement.getTypeName()
                        + this.sLeftElement.getTypeName() + "_0.."
                        + lowerBoundValue.toString();

                Element secondElement;

                if (this.grammar.containsProduction(zeroToLowerBoundName)) {
                    secondElement = new Element.ProductionElement(
                            this.grammar.getProduction(zeroToLowerBoundName));
                }
                else {
                    secondElement = new Element.ProductionElement(
                            newAlternatedProduction(this.parserElement,
                                    this.sRightElement, this.sLeftElement,
                                    new CardinalityInterval(Bound.ZERO,
                                            this.cardinality.getLowerBound())));
                }

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(firstElement);
                alternativeElements.add(secondElement);

                LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                secondElement));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        alternativeElements, altTransformationElements));
            }

            this.newProduction = intervalProd;
        }

        private Alternative newAlternative(
                Production production,
                Element element) {

            LinkedList<Element> elements = new LinkedList<Element>();
            elements.add(element);
            return newAlternative(production, elements);
        }

        private Alternative newAlternative(
                Production production,
                LinkedList<Element> elements) {

            Alternative alternative = new Alternative(production, elements);
            alternative.addTransformation(new SAlternativeTransformation(
                    alternative, elements));
            return alternative;
        }

        private Alternative newListAlternative(
                Production production,
                LinkedList<Element> elements,
                LinkedList<SAlternativeTransformationListElement> transformationElements) {

            Alternative alternative = new Alternative(production, elements);
            alternative.addTransformation(new SAlternativeTransformation(
                    transformationElements, alternative));
            return alternative;
        }
    }

    private static class AlternatedProductionBuilder {

        private final Parser.ParserElement parserElement;

        private final Element sLeftElement;

        private final Element sRightElement;

        private final CardinalityInterval cardinality;

        private Production newProduction;

        private SGrammar grammar;

        public AlternatedProductionBuilder(
                Parser.ParserElement parserElement,
                Element sLeftElement,
                Element sRightElement,
                CardinalityInterval cardinality,
                SGrammar grammar) {

            this.parserElement = parserElement;
            this.sLeftElement = sLeftElement;
            this.sRightElement = sRightElement;
            this.cardinality = cardinality;
            this.grammar = grammar;

            if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
                plusCase();
            }
            else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
                starCase();
            }
            else if (cardinality.isANumber()) {
                numberCase();
            }
            else if (cardinality.upperBoundIsInfinite()) {
                atLeastCase();
            }
            else {
                intervalCase();
            }

            if (parserElement instanceof Parser.ParserElement.AlternatedElement) {
                Parser.ParserElement.AlternatedElement alternatedElement = (Parser.ParserElement.AlternatedElement) parserElement;

                this.newProduction
                        .addTransformation(new SProductionTransformation(
                                this.newProduction, this.sLeftElement
                                        .getTypeName(), this.sRightElement
                                        .getTypeName(), alternatedElement
                                        .getLeftReference(), alternatedElement
                                        .getRightReference(), cardinality,
                                false));
            }
            else {
                Parser.ParserElement.SeparatedElement separatedElement = (Parser.ParserElement.SeparatedElement) parserElement;

                this.newProduction
                        .addTransformation(new SProductionTransformation(
                                this.newProduction, this.sLeftElement
                                        .getTypeName(), this.sRightElement
                                        .getTypeName(), separatedElement
                                        .getRightReference(), separatedElement
                                        .getLeftReference(), cardinality, false));
            }

            this.grammar.addProduction(this.newProduction);

        }

        public Production getNewProduction() {

            return this.newProduction;
        }

        private void plusCase() {

            String plusName = this.sLeftElement.getTypeName()
                    + this.sRightElement.getTypeName() + "_+";

            Production plusProd = new Production(
                    this.grammar.getNextProductionId(), plusName);

            LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            firstAlternativeElements
                    .add(new Element.ProductionElement(plusProd));
            firstAlternativeElements.add(this.sLeftElement.clone());
            firstAlternativeElements.add(this.sRightElement.clone());

            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstAlternativeElements.get(0)));
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            firstAlternativeElements.get(1)));
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            firstAlternativeElements.get(1)));
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            firstAlternativeElements.get(2)));

            plusProd.addAlternative(newListAlternative(plusProd,
                    firstAlternativeElements, firstAltTransformationElements));

            LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> secondAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            secondAlternativeElements.add(this.sLeftElement);
            secondAlternativeElements.add(this.sRightElement);

            secondAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            secondAlternativeElements.get(0)));
            secondAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            secondAlternativeElements.get(1)));

            plusProd.addAlternative(newListAlternative(plusProd,
                    secondAlternativeElements, secondAltTransformationElements));

            this.newProduction = plusProd;

        }

        private void starCase() {

            String starName = this.sLeftElement.getTypeName()
                    + this.sRightElement.getTypeName() + "_*";

            Production starProd = new Production(
                    this.grammar.getNextProductionId(), starName);

            String plusName = this.sLeftElement.getTypeName()
                    + this.sRightElement.getTypeName() + "_+";

            // First alternative
            Element firstElement;

            if (this.grammar.containsProduction(plusName)) {
                firstElement = new Element.ProductionElement(
                        this.grammar.getProduction(plusName));
            }
            else {
                firstElement = new Element.ProductionElement(plusName,
                        newAlternatedProduction(this.parserElement,
                                this.sLeftElement, this.sRightElement,
                                CardinalityInterval.ONE_OR_MORE));
            }

            LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            firstAlternativeElements.add(firstElement);
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement));

            starProd.addAlternative(newListAlternative(starProd,
                    firstAlternativeElements, firstAltTransformationElements));

            // Second alternative
            starProd.addAlternative(new Alternative(starProd));

            this.newProduction = starProd;
        }

        private void numberCase() {

            BigInteger number = this.cardinality.getLowerBound().getValue();
            String numberName = this.sLeftElement.getTypeName()
                    + this.sRightElement.getTypeName() + "_"
                    + number.toString();

            Production numberProd = new Production(
                    this.grammar.getNextProductionId(), numberName);

            LinkedList<SAlternativeTransformationListElement> altTransformationElement = new LinkedList<SAlternativeTransformationListElement>();
            LinkedList<Element> alternativeElements = new LinkedList<Element>();

            if (number.compareTo(BigInteger.ONE) == 0) {

                alternativeElements.add(this.sLeftElement.clone());
                alternativeElements.add(this.sRightElement.clone());

                altTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(0)));
                altTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        alternativeElements, altTransformationElement));
            }
            else {
                String previousNumberName = this.sLeftElement.getTypeName()
                        + this.sRightElement.getTypeName() + "_"
                        + number.subtract(BigInteger.ONE).toString();

                Element firstElement;
                if (this.grammar.containsProduction(previousNumberName)) {
                    firstElement = new Element.ProductionElement(
                            this.grammar.getProduction(previousNumberName));
                }
                else {
                    Production previousNumberProd = newAlternatedProduction(
                            this.parserElement, this.sLeftElement,
                            this.sRightElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().subtract(BigInteger.ONE)));

                    firstElement = new Element.ProductionElement(
                            previousNumberProd);
                }

                alternativeElements.add(firstElement);
                alternativeElements.add(this.sLeftElement.clone());
                alternativeElements.add(this.sRightElement.clone());

                altTransformationElement
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                alternativeElements.get(0)));
                altTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1)));
                altTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        alternativeElements, altTransformationElement));

            }

            this.newProduction = numberProd;

        }

        private void atLeastCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            String atLeastName = this.sLeftElement.getTypeName()
                    + this.sRightElement.getTypeName() + "_"
                    + lowerBoundValue.toString() + "...";

            Production atLeastProd = new Production(
                    this.grammar.getNextProductionId(), atLeastName);

            String numberName = this.sLeftElement.getTypeName()
                    + this.sRightElement.getTypeName() + "_"
                    + lowerBoundValue.toString();

            Element firstElement;

            if (this.grammar.containsProduction(numberName)) {
                firstElement = new Element.ProductionElement(
                        this.grammar.getProduction(atLeastName));
            }
            else {
                firstElement = new Element.ProductionElement(
                        newAlternatedProduction(
                                this.parserElement,
                                this.sLeftElement,
                                this.sRightElement,
                                new CardinalityInterval(this.cardinality
                                        .getLowerBound(), this.cardinality
                                        .getLowerBound())));
            }

            String starName = this.sLeftElement.getTypeName()
                    + this.sRightElement.getTypeName() + "_*";

            Element secondElement;

            if (this.grammar.containsProduction(starName)) {
                secondElement = new Element.ProductionElement(
                        this.grammar.getProduction(starName));
            }
            else {
                secondElement = new Element.ProductionElement(
                        newAlternatedProduction(this.parserElement,
                                this.sLeftElement, this.sRightElement,
                                CardinalityInterval.ZERO_OR_MORE));
            }

            LinkedList<Element> alternativeElements = new LinkedList<Element>();

            alternativeElements.add(firstElement);
            alternativeElements.add(secondElement);

            LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement));
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            secondElement));

            atLeastProd.addAlternative(newListAlternative(atLeastProd,
                    alternativeElements, altTransformationElements));

            this.newProduction = atLeastProd;

        }

        private void intervalCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            BigInteger upperBoundValue = this.cardinality.getUpperBound()
                    .getValue();

            String intervalName = this.sLeftElement.getTypeName()
                    + this.sRightElement.getTypeName() + "_"
                    + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

            Production intervalProd = new Production(
                    this.grammar.getNextProductionId(), intervalName);

            if (lowerBoundValue.equals(BigInteger.ZERO)) {

                LinkedList<Element> altElements = new LinkedList<Element>();
                LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

                // (ab)^0..1 = ab | Empty
                if (upperBoundValue.equals(BigInteger.ONE)) {

                    // First alternative
                    altElements.add(this.sLeftElement.clone());
                    altElements.add(this.sRightElement.clone());

                    altTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    altElements.get(0)));
                    altTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    altElements.get(1)));

                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, altElements,
                            altTransformationElements));
                }
                // (ab)^0..m with m > 1 = (ab)^(1..m) | Empty
                else {
                    Element firstElement;

                    String smallerIntervalName = this.sLeftElement
                            .getTypeName()
                            + this.sRightElement.getTypeName()
                            + "_"
                            + lowerBoundValue.add(BigInteger.ONE).toString()
                            + ".." + upperBoundValue.toString();

                    if (this.grammar.containsProduction(smallerIntervalName)) {
                        firstElement = new Element.ProductionElement(
                                this.grammar.getProduction(smallerIntervalName));
                    }
                    else {

                        firstElement = new Element.ProductionElement(
                                newAlternatedProduction(
                                        this.parserElement,
                                        this.sLeftElement,
                                        this.sRightElement,
                                        new CardinalityInterval(Bound.ONE,
                                                this.cardinality
                                                        .getUpperBound())));
                    }

                    altElements.add(firstElement);

                    altTransformationElements
                            .add(new SAlternativeTransformationListElement.NormalListElement(
                                    firstElement));
                }

                // Second alternative
                intervalProd.addAlternative(new Alternative(intervalProd));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        altElements, altTransformationElements));

            }
            // (ab)^(1..n) = (ab)^(1..(n-1)) ab;
            else if (lowerBoundValue.equals(BigInteger.ONE)) {
                // (ab)^(1..2) = ab | abab;
                if (upperBoundValue.equals(BigInteger.valueOf(2L))) {
                    // First alternative
                    LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
                    LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

                    firstAlternativeElements.add(this.sLeftElement.clone());
                    firstAlternativeElements.add(this.sRightElement.clone());

                    firstAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    firstAlternativeElements.get(0)));
                    firstAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    firstAlternativeElements.get(1)));

                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, firstAlternativeElements,
                            firstAltTransformationElements));

                    // Second alternative

                    LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
                    LinkedList<SAlternativeTransformationListElement> secondAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

                    secondAlternativeElements.add(this.sLeftElement.clone());
                    secondAlternativeElements.add(this.sRightElement.clone());
                    secondAlternativeElements.add(this.sLeftElement.clone());
                    secondAlternativeElements.add(this.sRightElement.clone());

                    secondAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    secondAlternativeElements.get(0)));
                    secondAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    secondAlternativeElements.get(1)));
                    secondAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    secondAlternativeElements.get(2)));
                    secondAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    secondAlternativeElements.get(3)));

                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, secondAlternativeElements,
                            secondAltTransformationElements));

                }
            }
            // (ab)^(m..n) with n > 1 = (ab)^m (ab)^(0..(n-m))
            else {
                String lowerNumberName = this.sLeftElement.getTypeName()
                        + this.sRightElement.getTypeName() + "_"
                        + lowerBoundValue.toString();

                LinkedList<Element> altElements = new LinkedList<Element>();
                LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
                Element firstElement;

                if (this.grammar.containsProduction(lowerNumberName)) {
                    firstElement = new Element.ProductionElement(
                            this.grammar.getProduction(lowerNumberName));
                }
                else {
                    firstElement = new Element.ProductionElement(
                            newAlternatedProduction(
                                    this.parserElement,
                                    this.sLeftElement,
                                    this.sRightElement,
                                    new CardinalityInterval(this.cardinality
                                            .getLowerBound(), this.cardinality
                                            .getLowerBound())));
                }

                Element secondElement;

                String zeroToIntervalWidthName = this.sLeftElement
                        .getTypeName()
                        + this.sRightElement.getTypeName()
                        + "_0.."
                        + upperBoundValue.subtract(lowerBoundValue).toString();

                if (this.grammar.containsProduction(zeroToIntervalWidthName)) {
                    secondElement = new Element.ProductionElement(
                            this.grammar.getProduction(zeroToIntervalWidthName));
                }
                else {
                    secondElement = new Element.ProductionElement(
                            newAlternatedProduction(this.parserElement,
                                    this.sLeftElement, this.sRightElement,
                                    new CardinalityInterval(Bound.ZERO,
                                            this.cardinality.getLowerBound())));

                }

                altElements.add(firstElement);
                altElements.add(secondElement);

                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                secondElement));
                intervalProd.addAlternative(newListAlternative(intervalProd,
                        altElements, altTransformationElements));
            }

            this.newProduction = intervalProd;
        }

        private Alternative newListAlternative(
                Production production,
                LinkedList<Element> elements,
                LinkedList<SAlternativeTransformationListElement> transformationElements) {

            Alternative alternative = new Alternative(production, elements);
            alternative.addTransformation(new SAlternativeTransformation(
                    transformationElements, alternative));
            return alternative;
        }

    }

}
