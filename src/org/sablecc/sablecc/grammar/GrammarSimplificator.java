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

        for (Parser.ParserAlternative alternative : node.getAlternatives()) {
            alternative.apply(this);
        }

        this.production.addAlternatives(this.alternatives);
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
            transformationElements
                    .add(new SAlternativeTransformationElement.NewElement(
                            alternative));
            SAlternativeTransformation transformation = new SAlternativeTransformation(
                    alternative, transformationElements);
            alternative.addTransformation(transformation);
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
            name = element.getName() + "_?";
        }
        else if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            name = element.getName() + "_+";
        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name = element.getName() + "_*";
        }
        else if (cardinality.isANumber()) {
            BigInteger number = cardinality.getLowerBound().getValue();
            name = element.getName() + "_" + number.toString();
        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            name = element.getName() + "_" + lowerBoundValue.toString() + "...";
        }
        else {

            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            name = element.getName() + "_" + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();
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
            name = leftElement.getName() + " Separator "
                    + rightElement.getName() + "_+";

        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name = leftElement.getName() + " Separator "
                    + rightElement.getName() + "_*";

        }
        else if (cardinality.equals(cardinality.isANumber())) {
            BigInteger number = cardinality.getLowerBound().getValue();
            name = leftElement + " Separator " + rightElement + "_"
                    + number.toString();
        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();

            name = leftElement.getName() + " Separator "
                    + rightElement.getName() + "_" + lowerBoundValue.toString()
                    + "...";
        }
        else {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            name = leftElement.getName() + " Separator " + rightElement + "_"
                    + lowerBoundValue.toString() + ".."
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
            name = leftElement.getName() + rightElement.getName() + "_+";
        }

        if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name = leftElement.getName() + rightElement.getName() + "_*";
        }
        else if (cardinality.isANumber()) {
            BigInteger number = cardinality.getLowerBound().getValue();
            name = leftElement.getName() + rightElement.getName() + "_"
                    + number.toString();

        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            name = leftElement.getName() + rightElement.getName() + "_"
                    + lowerBoundValue.toString() + "...";
        }
        else {

            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            name = leftElement.getName() + rightElement.getName() + "_"
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

            String qmarkName = this.sElement.getName() + "_?";

            Production qmarkProd = new Production(qmarkName);

            qmarkProd.addAlternative(newAlternative(qmarkProd,
                    this.sElement.clone()));
            qmarkProd.addAlternative(new Alternative(qmarkProd));

            this.newProduction = qmarkProd;

        }

        private void plusCase() {

            String plusName = this.sElement.getName() + "_+";

            Production plusProd = new Production(plusName);
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

            String starName = this.sElement.getName() + "_*";

            Production starProd = new Production(starName);

            // First Alternative
            String plusName = this.sElement.getName() + "_+";

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
            String numberName = this.sElement.getName() + "_"
                    + number.toString();

            Production numberProd = new Production(numberName);

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
                String previousNumberName = this.sElement.getName() + "_"
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
            String atLeastName = this.sElement.getName() + "_"
                    + lowerBoundValue.toString() + "...";

            Production atLeastProd = new Production(atLeastName);

            String numberName = this.sElement.getName() + "_"
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

            String starName = this.sElement.getName() + "_*";

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

            String intervalName = this.sElement.getName() + "_"
                    + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

            Production intervalProd = new Production(intervalName);

            if (lowerBoundValue.equals(BigInteger.ZERO)) {

                // First Alternative

                String smallerIntervalName = this.parserElement.getName() + "_"
                        + lowerBoundValue.toString() + ".."
                        + upperBoundValue.subtract(BigInteger.ZERO).toString();

                LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
                LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
                Element firstElement;

                if (this.grammar.containsProduction(smallerIntervalName)) {
                    firstElement = new Element.ProductionElement(
                            this.grammar.getProduction(smallerIntervalName));

                    firstAltTransformationElements
                            .add(new SAlternativeTransformationListElement.NormalListElement(
                                    firstElement));
                }
                else {

                    if (upperBoundValue.equals(BigInteger.valueOf(2L))) {

                        String qmarkName = this.parserElement.getName() + "_?";

                        if (this.grammar.containsProduction(qmarkName)) {
                            firstElement = new Element.ProductionElement(
                                    this.grammar.getProduction(qmarkName));
                        }
                        else {
                            firstElement = new Element.ProductionElement(
                                    newNormalProduction(this.parserElement,
                                            this.sElement,
                                            CardinalityInterval.ZERO_ONE));
                        }

                        firstAltTransformationElements
                                .add(new SAlternativeTransformationListElement.ReferenceElement(
                                        firstElement));
                    }
                    else {
                        firstElement = new Element.ProductionElement(
                                newNormalProduction(
                                        this.parserElement,
                                        this.sElement,
                                        new CardinalityInterval(
                                                Bound.ZERO,
                                                new Bound(
                                                        upperBoundValue
                                                                .subtract(BigInteger.ONE)))));

                        firstAltTransformationElements
                                .add(new SAlternativeTransformationListElement.NormalListElement(
                                        firstElement));

                    }

                    firstAlternativeElements.add(firstElement);
                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, firstAlternativeElements,
                            firstAltTransformationElements));

                    // Second alternative

                    String upperNumberName = this.sElement.getName() + "_"
                            + upperBoundValue.toString();

                    LinkedList<SAlternativeTransformationListElement> secondAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
                    LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
                    Element firstElementBis;

                    if (this.grammar.containsProduction(upperNumberName)) {
                        firstElementBis = new Element.ProductionElement(
                                this.grammar.getProduction(upperNumberName));
                    }
                    else {
                        firstElementBis = new Element.ProductionElement(
                                newNormalProduction(
                                        this.parserElement,
                                        this.sElement,
                                        new CardinalityInterval(
                                                this.cardinality
                                                        .getUpperBound(),
                                                this.cardinality
                                                        .getUpperBound())));
                    }

                    secondAlternativeElements.add(firstElementBis);
                    secondAltTransformationElements
                            .add(new SAlternativeTransformationListElement.NormalListElement(
                                    firstElementBis));
                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, secondAlternativeElements,
                            secondAltTransformationElements));
                }
            }
            else {
                String lowerNumberName = this.sElement.getName() + "_"
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

                String zeroToLowerBoundName = this.sElement.getName() + "_0.."
                        + lowerBoundValue.toString();

                Element secondElement;

                if (this.grammar.containsProduction(zeroToLowerBoundName)) {
                    secondElement = new Element.ProductionElement(
                            this.grammar.getProduction(zeroToLowerBoundName));
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
                    this.newProduction, this.sLeftElement.getName(),
                    this.sRightElement.getName(), parserElement
                            .getLeftReference(), parserElement
                            .getRightReference(), cardinality, true));

            this.grammar.addProduction(this.newProduction);

        }

        public Production getNewProduction() {

            return this.newProduction;
        }

        private void plusCase() {

            String plusName = this.sLeftElement.getName() + " Separator "
                    + this.sRightElement.getName() + "_+";

            Production plusProd = new Production(plusName);
            Element firstElement;

            if (this.sLeftElement instanceof Element.TokenElement) {
                firstElement = new Element.TokenElement(
                        this.sLeftElement.getName());
            }
            else {
                firstElement = new Element.ProductionElement(
                        ((Element.ProductionElement) this.sLeftElement)
                                .getReference());
            }

            String alternatedStarName = this.sRightElement.getName()
                    + this.sLeftElement.getName() + "_*";

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

            plusProd.addAlternative(newAlternative(plusProd,
                    alternativeElements));

            this.newProduction = plusProd;
        }

        private void starCase() {

            String starName = this.sLeftElement.getName() + " Separator "
                    + this.sRightElement.getName() + "_*";

            Production starProd = new Production(starName);

            String separatedPlusName = this.sLeftElement.getName()
                    + " Separator " + this.sRightElement.getName() + "_+";

            if (GrammarSimplificator.grammar
                    .containsProduction(separatedPlusName)) {
                starProd.addAlternative(newAlternative(
                        starProd,
                        new Element.ProductionElement(
                                GrammarSimplificator.grammar
                                        .getProduction(separatedPlusName))));
            }
            else {
                starProd.addAlternative(newAlternative(
                        starProd,
                        new Element.ProductionElement(newAlternatedProduction(
                                this.parserElement, this.sRightElement,
                                this.sLeftElement,
                                CardinalityInterval.ONE_OR_MORE))));
            }

            this.newProduction = starProd;

        }

        private void numberCase() {

            BigInteger number = this.cardinality.getLowerBound().getValue();
            String numberName = this.sLeftElement + " Separator "
                    + this.sRightElement + "_" + number.toString();

            Production numberProd = new Production(numberName);

            if (number.compareTo(BigInteger.valueOf(2L)) == 0) {

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(this.sLeftElement.clone());
                alternativeElements.add(this.sRightElement.clone());
                alternativeElements.add(this.sLeftElement.clone());

                numberProd.addAlternative(newAlternative(numberProd,
                        alternativeElements));
            }
            else {
                String previousNumberName = this.sLeftElement + " Separator "
                        + this.sRightElement + "_"
                        + number.subtract(BigInteger.ONE).toString();

                if (GrammarSimplificator.grammar
                        .containsProduction(previousNumberName)) {
                    numberProd
                            .addAlternative(newAlternative(
                                    numberProd,
                                    new Element.ProductionElement(
                                            GrammarSimplificator.grammar
                                                    .getProduction(previousNumberName))));
                }
                else {
                    Production previousNumberProd = newSeparatedProduction(
                            this.parserElement, this.sLeftElement,
                            this.sRightElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().subtract(BigInteger.ONE)));

                    numberProd.addAlternative(newAlternative(numberProd,
                            new Element.ProductionElement(previousNumberProd)));
                }
            }

            this.newProduction = numberProd;

        }

        private void atLeastCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();

            String atLeastName = this.sLeftElement.getName() + " Separator "
                    + this.sRightElement.getName() + "_"
                    + lowerBoundValue.toString() + "...";

            Production atLeastProd = new Production(atLeastName);

            String numberName = this.sLeftElement.getName() + " Separator "
                    + this.sRightElement.getName() + "_"
                    + lowerBoundValue.toString();

            if (GrammarSimplificator.grammar.containsProduction(numberName)) {
                atLeastProd.addAlternative(newAlternative(
                        atLeastProd,
                        new Element.ProductionElement(
                                GrammarSimplificator.grammar
                                        .getProduction(atLeastName))));
            }
            else {
                atLeastProd.addAlternative(newAlternative(
                        atLeastProd,
                        new Element.ProductionElement(newSeparatedProduction(
                                this.parserElement, this.sLeftElement,
                                this.sRightElement, new CardinalityInterval(
                                        this.cardinality.getLowerBound(),
                                        this.cardinality.getLowerBound())))));
            }

            String starName = this.sLeftElement.getName() + " Separator "
                    + this.sRightElement.getName() + "_*";

            if (GrammarSimplificator.grammar.containsProduction(starName)) {
                atLeastProd.addAlternative(newAlternative(
                        atLeastProd,
                        new Element.ProductionElement(
                                GrammarSimplificator.grammar
                                        .getProduction(starName))));
            }
            else {
                atLeastProd.addAlternative(newAlternative(
                        atLeastProd,
                        new Element.ProductionElement(newAlternatedProduction(
                                this.parserElement, this.sRightElement,
                                this.sLeftElement,
                                CardinalityInterval.ZERO_OR_MORE))));
            }

            this.newProduction = atLeastProd;

        }

        private void intervalCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            BigInteger upperBoundValue = this.cardinality.getUpperBound()
                    .getValue();

            String intervalName = this.sLeftElement.getName() + " Separator "
                    + this.sRightElement + "_" + lowerBoundValue.toString()
                    + ".." + upperBoundValue.toString();

            Production intervalProd = new Production(intervalName);

            if (lowerBoundValue.equals(BigInteger.ZERO)) {

                String smallerIntervalName = this.sLeftElement.getName()
                        + " Separator " + this.sRightElement + "_"
                        + lowerBoundValue.toString() + ".."
                        + upperBoundValue.subtract(BigInteger.ZERO).toString();

                if (this.grammar.containsProduction(smallerIntervalName)) {
                    intervalProd.addAlternative(newAlternative(
                            intervalProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(smallerIntervalName))));
                }
                else {

                    if (upperBoundValue.equals(BigInteger.valueOf(2L))) {
                        String qmarkName = this.sLeftElement.getName()
                                + " Separator " + this.sRightElement + "_?";
                        Alternative alternative;

                        if (this.grammar.containsProduction(qmarkName)) {
                            alternative = newAlternative(
                                    intervalProd,
                                    new Element.ProductionElement(this.grammar
                                            .getProduction(qmarkName)));
                        }
                        else {
                            alternative = newAlternative(
                                    intervalProd,
                                    new Element.ProductionElement(
                                            newSeparatedProduction(
                                                    this.parserElement,
                                                    this.sLeftElement,
                                                    this.sRightElement,
                                                    CardinalityInterval.ZERO_ONE)));
                        }

                        intervalProd.addAlternative(alternative);
                    }
                    else {
                        intervalProd
                                .addAlternative(newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                newSeparatedProduction(
                                                        this.parserElement,
                                                        this.sLeftElement,
                                                        this.sRightElement,
                                                        new CardinalityInterval(
                                                                Bound.ZERO,
                                                                new Bound(
                                                                        upperBoundValue
                                                                                .subtract(BigInteger.ONE)))))));
                    }

                    String upperNumberName = this.sLeftElement.getName()
                            + " Separator " + this.sRightElement + "_"
                            + upperBoundValue.toString();

                    if (this.grammar.containsProduction(upperNumberName)) {
                        intervalProd.addAlternative(newAlternative(
                                intervalProd,
                                new Element.ProductionElement(this.grammar
                                        .getProduction(upperNumberName))));
                    }
                    else {
                        intervalProd
                                .addAlternative(newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                newSeparatedProduction(
                                                        this.parserElement,
                                                        this.sLeftElement,
                                                        this.sRightElement,
                                                        new CardinalityInterval(
                                                                this.cardinality
                                                                        .getUpperBound(),
                                                                this.cardinality
                                                                        .getUpperBound())))));
                    }
                }

            }
            else {
                String lowerNumberName = this.sLeftElement.getName()
                        + " Separator " + this.sRightElement + "_"
                        + lowerBoundValue.toString();

                LinkedList<Element> alternativeElements = new LinkedList<Element>();

                if (this.grammar.containsProduction(lowerNumberName)) {
                    alternativeElements.add(new Element.ProductionElement(
                            this.grammar.getProduction(lowerNumberName)));
                }
                else {
                    alternativeElements.add(new Element.ProductionElement(
                            newSeparatedProduction(
                                    this.parserElement,
                                    this.sLeftElement,
                                    this.sRightElement,
                                    new CardinalityInterval(this.cardinality
                                            .getLowerBound(), this.cardinality
                                            .getLowerBound()))));
                }

                String zeroToLowerBoundName = this.sRightElement.getName()
                        + this.sLeftElement.getName() + "_0.."
                        + lowerBoundValue.toString();

                if (this.grammar.containsProduction(zeroToLowerBoundName)) {
                    alternativeElements.add(new Element.ProductionElement(
                            this.grammar.getProduction(zeroToLowerBoundName)));
                }
                else {
                    alternativeElements
                            .add(new Element.ProductionElement(
                                    newAlternatedProduction(
                                            this.parserElement,
                                            this.sRightElement,
                                            this.sLeftElement,
                                            new CardinalityInterval(Bound.ZERO,
                                                    this.cardinality
                                                            .getLowerBound()))));

                }

                intervalProd.addAlternative(newAlternative(intervalProd,
                        alternativeElements));
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
                                this.newProduction,
                                this.sLeftElement.getName(), this.sRightElement
                                        .getName(), alternatedElement
                                        .getLeftReference(), alternatedElement
                                        .getRightReference(), cardinality,
                                false));
            }
            else {
                Parser.ParserElement.SeparatedElement separatedElement = (Parser.ParserElement.SeparatedElement) parserElement;

                this.newProduction
                        .addTransformation(new SProductionTransformation(
                                this.newProduction,
                                this.sLeftElement.getName(), this.sRightElement
                                        .getName(), separatedElement
                                        .getRightReference(), separatedElement
                                        .getLeftReference(), cardinality, false));
            }

            this.grammar.addProduction(this.newProduction);

        }

        public Production getNewProduction() {

            return this.newProduction;
        }

        private void plusCase() {

            String plusName = this.sLeftElement.getName()
                    + this.sRightElement.getName() + "_+";

            Production plusProd = new Production(plusName);

            LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
            firstAlternativeElements
                    .add(new Element.ProductionElement(plusProd));
            firstAlternativeElements.add(this.sLeftElement.clone());
            firstAlternativeElements.add(this.sRightElement.clone());

            plusProd.addAlternative(newAlternative(plusProd,
                    firstAlternativeElements));

            LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
            secondAlternativeElements.add(this.sLeftElement);
            secondAlternativeElements.add(this.sRightElement);
            plusProd.addAlternative(newAlternative(plusProd,
                    secondAlternativeElements));

            this.newProduction = plusProd;

        }

        private void starCase() {

            String starName = this.sLeftElement.getName()
                    + this.sRightElement.getName() + "_*";

            Production starProd = new Production(starName);

            String plusName = this.sLeftElement.getName()
                    + this.sRightElement.getName() + "_+";

            if (this.grammar.containsProduction(plusName)) {
                starProd.addAlternative(newAlternative(
                        starProd,
                        new Element.ProductionElement(this.grammar
                                .getProduction(plusName))));
            }
            else {
                starProd.addAlternative(newAlternative(
                        starProd,
                        new Element.ProductionElement(plusName,
                                newAlternatedProduction(this.parserElement,
                                        this.sLeftElement, this.sRightElement,
                                        CardinalityInterval.ONE_OR_MORE))));
            }

            starProd.addAlternative(new Alternative(starProd));

            this.newProduction = starProd;
        }

        private void numberCase() {

            BigInteger number = this.cardinality.getLowerBound().getValue();
            String numberName = this.sLeftElement.getName()
                    + this.sRightElement.getName() + "_" + number.toString();

            Production numberProd = new Production(numberName);

            if (number.compareTo(BigInteger.ONE) == 0) {

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(this.sLeftElement.clone());
                alternativeElements.add(this.sRightElement.clone());

                numberProd.addAlternative(newAlternative(numberProd,
                        alternativeElements));
            }
            else {
                String previousNumberName = this.sLeftElement.getName()
                        + this.sRightElement.getName() + "_"
                        + number.subtract(BigInteger.ONE).toString();

                if (this.grammar.containsProduction(previousNumberName)) {
                    numberProd.addAlternative(newAlternative(
                            numberProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(previousNumberName))));
                }
                else {
                    Production previousNumberProd = newAlternatedProduction(
                            this.parserElement, this.sLeftElement,
                            this.sRightElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().subtract(BigInteger.ONE)));

                    numberProd.addAlternative(newAlternative(numberProd,
                            new Element.ProductionElement(previousNumberProd)));
                }
            }

            this.newProduction = numberProd;

        }

        private void atLeastCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            String atLeastName = this.sLeftElement.getName()
                    + this.sRightElement.getName() + "_"
                    + lowerBoundValue.toString() + "...";

            Production atLeastProd = new Production(atLeastName);

            String numberName = this.sLeftElement.getName()
                    + this.sRightElement.getName() + "_"
                    + lowerBoundValue.toString();

            if (this.grammar.containsProduction(numberName)) {
                atLeastProd.addAlternative(newAlternative(
                        atLeastProd,
                        new Element.ProductionElement(this.grammar
                                .getProduction(atLeastName))));
            }
            else {
                atLeastProd.addAlternative(newAlternative(
                        atLeastProd,
                        new Element.ProductionElement(newAlternatedProduction(
                                this.parserElement, this.sLeftElement,
                                this.sRightElement, new CardinalityInterval(
                                        this.cardinality.getLowerBound(),
                                        this.cardinality.getLowerBound())))));
            }

            String starName = this.sLeftElement.getName()
                    + this.sRightElement.getName() + "_*";

            if (this.grammar.containsProduction(starName)) {
                atLeastProd.addAlternative(newAlternative(
                        atLeastProd,
                        new Element.ProductionElement(this.grammar
                                .getProduction(starName))));
            }
            else {
                atLeastProd.addAlternative(newAlternative(
                        atLeastProd,
                        new Element.ProductionElement(newAlternatedProduction(
                                this.parserElement, this.sLeftElement,
                                this.sRightElement,
                                CardinalityInterval.ZERO_OR_MORE))));
            }

            this.newProduction = atLeastProd;

        }

        private void intervalCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            BigInteger upperBoundValue = this.cardinality.getUpperBound()
                    .getValue();

            String intervalName = this.sLeftElement.getName()
                    + this.sRightElement.getName() + "_"
                    + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

            Production intervalProd = new Production(intervalName);

            if (lowerBoundValue.equals(BigInteger.ZERO)) {

                String smallerIntervalName = this.sLeftElement.getName()
                        + this.sRightElement.getName() + "_"
                        + lowerBoundValue.toString() + ".."
                        + upperBoundValue.subtract(BigInteger.ZERO).toString();

                if (this.grammar.containsProduction(smallerIntervalName)) {
                    intervalProd.addAlternative(newAlternative(
                            intervalProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(smallerIntervalName))));
                }
                else {

                    if (upperBoundValue.equals(BigInteger.valueOf(2L))) {

                        Alternative alternative;
                        String qmarkName = this.sLeftElement.getName()
                                + this.sRightElement.getName() + "_?";

                        if (this.grammar.containsProduction(qmarkName)) {
                            alternative = newAlternative(
                                    intervalProd,
                                    new Element.ProductionElement(this.grammar
                                            .getProduction(qmarkName)));
                        }
                        else {
                            alternative = newAlternative(
                                    intervalProd,
                                    new Element.ProductionElement(
                                            newAlternatedProduction(
                                                    this.parserElement,
                                                    this.sLeftElement,
                                                    this.sRightElement,
                                                    CardinalityInterval.ZERO_ONE)));
                        }

                        intervalProd.addAlternative(alternative);

                    }
                    else {
                        Alternative alternative = newAlternative(
                                intervalProd,
                                new Element.ProductionElement(
                                        newAlternatedProduction(
                                                this.parserElement,
                                                this.sLeftElement,
                                                this.sRightElement,
                                                new CardinalityInterval(
                                                        Bound.ZERO,
                                                        new Bound(
                                                                upperBoundValue
                                                                        .subtract(BigInteger.ONE))))));
                        intervalProd.addAlternative(alternative);

                    }

                    String upperNumberName = this.sLeftElement.getName()
                            + this.sRightElement.getName() + "_"
                            + upperBoundValue.toString();

                    if (this.grammar.containsProduction(upperNumberName)) {
                        intervalProd.addAlternative(newAlternative(
                                intervalProd,
                                new Element.ProductionElement(this.grammar
                                        .getProduction(upperNumberName))));
                    }
                    else {
                        intervalProd
                                .addAlternative(newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                newAlternatedProduction(
                                                        this.parserElement,
                                                        this.sLeftElement,
                                                        this.sRightElement,
                                                        new CardinalityInterval(
                                                                this.cardinality
                                                                        .getUpperBound(),
                                                                this.cardinality
                                                                        .getUpperBound())))));
                    }
                }
            }
            else {
                String lowerNumberName = this.sLeftElement.getName()
                        + this.sRightElement.getName() + "_"
                        + lowerBoundValue.toString();

                LinkedList<Element> alternativeElements = new LinkedList<Element>();

                if (this.grammar.containsProduction(lowerNumberName)) {
                    alternativeElements.add(new Element.ProductionElement(
                            this.grammar.getProduction(lowerNumberName)));
                }
                else {
                    alternativeElements.add(new Element.ProductionElement(
                            newAlternatedProduction(
                                    this.parserElement,
                                    this.sLeftElement,
                                    this.sRightElement,
                                    new CardinalityInterval(this.cardinality
                                            .getLowerBound(), this.cardinality
                                            .getLowerBound()))));
                }

                String zeroToLowerBoundName = this.sLeftElement.getName()
                        + this.sRightElement.getName() + "_0.."
                        + lowerBoundValue.toString();

                if (this.grammar.containsProduction(zeroToLowerBoundName)) {
                    alternativeElements.add(new Element.ProductionElement(
                            this.grammar.getProduction(zeroToLowerBoundName)));
                }
                else {
                    alternativeElements
                            .add(new Element.ProductionElement(
                                    newAlternatedProduction(
                                            this.parserElement,
                                            this.sLeftElement,
                                            this.sRightElement,
                                            new CardinalityInterval(Bound.ZERO,
                                                    this.cardinality
                                                            .getLowerBound()))));

                }

                intervalProd.addAlternative(newAlternative(intervalProd,
                        alternativeElements));
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

    }

}
