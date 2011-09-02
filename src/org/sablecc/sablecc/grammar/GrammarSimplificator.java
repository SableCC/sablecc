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

    private List<Element> elements = new LinkedList<Element>();

    private List<Alternative> alternatives = new LinkedList<Alternative>();

    private SGrammar grammar;

    private Production production;

    public GrammarSimplificator() {

    }

    @Override
    public void visitParserProduction(
            ParserProduction node) {

        if (node.getContext() instanceof Context.NamedContext) {
            throw new InternalException("Context are not supported yet");
        }

        String prodName = node.getName();
        this.production = this.grammar.getProduction(prodName);

        for (Parser.ParserAlternative alternative : node.getAlternatives()) {
            alternative.apply(this);
        }

        this.production.addAlternatives(this.alternatives);
    }

    @Override
    public void visitParserAlternative(
            ParserAlternative node) {

        for (Parser.ParserElement element : node.getElements()) {
            element.apply(this);
        }

        Alternative alternative = new Alternative(this.production,
                this.elements);

        new AlternativeTransformationBuilder(alternative,
                node.getTransformation());

        this.alternatives.add(alternative);

        this.elements.clear();
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
                        this.grammar.getProduction(prodName));
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
                        newNormalProduction(simpleElement,
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
                        this.grammar.getProduction(prodName));
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
                        this.grammar.getProduction(prodName));
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
                        newSeparatedProduction(leftSimpleElement,
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
                        this.grammar.getProduction(prodName));
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
                        this.grammar.getProduction(prodName));
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
                        newAlternatedProduction(leftSimpleElement,
                                rightSimpleElement, node.getCardinality()));
                this.elements.add(complexElement);
            }
        }
    }

    private Production newNormalProduction(
            Element element,
            CardinalityInterval cardinality) {

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newNormalProduction");
        }

        if (cardinality.equals(CardinalityInterval.ZERO_ONE)) {
            String qmarkName = element.getName() + "_?";

            if (this.grammar.containsProduction(qmarkName)) {
                return this.grammar.getProduction(qmarkName);
            }
            else {
                Production qmarkProd = new Production(qmarkName);
                qmarkProd.addAlternative(newAlternative(qmarkProd,
                        element.clone()));
                qmarkProd.addAlternative(new Alternative(qmarkProd));

                this.grammar.addProduction(qmarkProd);

                return qmarkProd;
            }
        }
        else if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            String plusName = element.getName() + "_+";

            if (this.grammar.containsProduction(plusName)) {
                return this.grammar.getProduction(plusName);
            }
            else {
                Production plusProd = new Production(plusName);

                LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
                firstAlternativeElements.add(new Element.ProductionElement(
                        plusProd));
                firstAlternativeElements.add(element.clone());

                plusProd.addAlternative(newAlternative(plusProd,
                        firstAlternativeElements));
                plusProd.addAlternative(newAlternative(plusProd,
                        element.clone()));

                this.grammar.addProduction(plusProd);

                return plusProd;
            }
        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            String starName = element.getName() + "_*";

            if (this.grammar.containsProduction(starName)) {
                return this.grammar.getProduction(starName);
            }
            else {
                Production starProd = new Production(starName);

                String plusName = element.getName() + "_+";

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
                                    newNormalProduction(element,
                                            CardinalityInterval.ONE_OR_MORE))));
                }

                starProd.addAlternative(new Alternative(starProd));

                this.grammar.addProduction(starProd);

                return starProd;
            }
        }
        else if (cardinality.isANumber()) {
            BigInteger number = cardinality.getLowerBound().getValue();
            String numberName = element.getName() + "_" + number.toString();

            if (this.grammar.containsProduction(numberName)) {
                return this.grammar.getProduction(numberName);
            }
            else {
                Production numberProd = new Production(numberName);

                if (number.compareTo(BigInteger.valueOf(2L)) == 0) {

                    LinkedList<Element> alternativeElements = new LinkedList<Element>();
                    alternativeElements.add(element.clone());
                    alternativeElements.add(element.clone());

                    numberProd.addAlternative(newAlternative(numberProd,
                            alternativeElements));
                }
                else {
                    String previousNumberName = element.getName() + "_"
                            + number.subtract(BigInteger.ONE).toString();

                    if (this.grammar.containsProduction(previousNumberName)) {
                        numberProd.addAlternative(newAlternative(
                                numberProd,
                                new Element.ProductionElement(this.grammar
                                        .getProduction(previousNumberName))));
                    }
                    else {
                        Production previousNumberProd = newNormalProduction(
                                element,
                                new CardinalityInterval(cardinality
                                        .getLowerBound().subtract(
                                                BigInteger.ONE)));

                        numberProd.addAlternative(newAlternative(numberProd,
                                new Element.ProductionElement(
                                        previousNumberProd)));
                    }
                }

                this.grammar.addProduction(numberProd);

                return numberProd;
            }
        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            String atLeastName = element.getName() + "_"
                    + lowerBoundValue.toString() + "...";

            if (this.grammar.containsProduction(atLeastName)) {
                return this.grammar.getProduction(atLeastName);
            }
            else {
                Production atLeastProd = new Production(atLeastName);

                String numberName = element.getName() + "_"
                        + lowerBoundValue.toString();

                if (this.grammar.containsProduction(numberName)) {
                    atLeastProd.addAlternative(new Alternative(atLeastProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(atLeastName))));
                }
                else {
                    atLeastProd.addAlternative(newAlternative(
                            atLeastProd,
                            new Element.ProductionElement(newNormalProduction(
                                    element, new CardinalityInterval(
                                            cardinality.getLowerBound(),
                                            cardinality.getLowerBound())))));
                }

                String starName = element.getName() + "_*";

                if (this.grammar.containsProduction(starName)) {
                    atLeastProd.addAlternative(newAlternative(
                            atLeastProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(starName))));
                }
                else {
                    atLeastProd
                            .addAlternative(newAlternative(
                                    atLeastProd,
                                    new Element.ProductionElement(
                                            newNormalProduction(
                                                    element,
                                                    CardinalityInterval.ZERO_OR_MORE))));
                }

                this.grammar.addProduction(atLeastProd);

                return atLeastProd;
            }

        }
        else {

            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            String intervalName = element.getName() + "_"
                    + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

            if (this.grammar.containsProduction(intervalName)) {
                return this.grammar.getProduction(intervalName);
            }
            else {
                Production intervalProd = new Production(intervalName);

                if (lowerBoundValue.equals(BigInteger.ZERO)) {

                    String smallerIntervalName = element.getName()
                            + "_"
                            + lowerBoundValue.toString()
                            + ".."
                            + upperBoundValue.subtract(BigInteger.ZERO)
                                    .toString();

                    if (this.grammar.containsProduction(smallerIntervalName)) {
                        intervalProd.addAlternative(newAlternative(
                                intervalProd,
                                new Element.ProductionElement(this.grammar
                                        .getProduction(smallerIntervalName))));
                    }
                    else {

                        if (upperBoundValue.equals(BigInteger.valueOf(2L))) {

                            Alternative alternative;
                            String qmarkName = element.getName() + "_?";

                            if (this.grammar.containsProduction(qmarkName)) {
                                alternative = newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                this.grammar
                                                        .getProduction(qmarkName)));
                            }
                            else {
                                alternative = newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                newNormalProduction(
                                                        element,
                                                        CardinalityInterval.ZERO_ONE)));
                            }

                            intervalProd.addAlternative(alternative);

                        }
                        else {
                            Alternative alternative = newAlternative(
                                    intervalProd,
                                    new Element.ProductionElement(
                                            newNormalProduction(
                                                    element,
                                                    new CardinalityInterval(
                                                            Bound.ZERO,
                                                            new Bound(
                                                                    upperBoundValue
                                                                            .subtract(BigInteger.ONE))))));
                            intervalProd.addAlternative(alternative);

                        }

                        String upperNumberName = element.getName() + "_"
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
                                                    newNormalProduction(
                                                            element,
                                                            new CardinalityInterval(
                                                                    cardinality
                                                                            .getUpperBound(),
                                                                    cardinality
                                                                            .getUpperBound())))));
                        }
                    }
                }
                else {
                    String lowerNumberName = element.getName() + "_"
                            + lowerBoundValue.toString();

                    Alternative alternative = new Alternative(intervalProd);

                    if (this.grammar.containsProduction(lowerNumberName)) {
                        alternative.addElement(new Element.ProductionElement(
                                this.grammar.getProduction(lowerNumberName)));
                    }
                    else {
                        alternative.addElement(new Element.ProductionElement(
                                newNormalProduction(
                                        element,
                                        new CardinalityInterval(cardinality
                                                .getLowerBound(), cardinality
                                                .getLowerBound()))));
                    }

                    String zeroToLowerBoundName = element.getName() + "_0.."
                            + lowerBoundValue.toString();

                    if (this.grammar.containsProduction(zeroToLowerBoundName)) {
                        alternative.addElement(new Element.ProductionElement(
                                this.grammar
                                        .getProduction(zeroToLowerBoundName)));
                    }
                    else {
                        alternative.addElement(new Element.ProductionElement(
                                newNormalProduction(element,
                                        new CardinalityInterval(Bound.ZERO,
                                                cardinality.getLowerBound()))));

                    }

                    intervalProd.addAlternative(alternative);
                }

                return intervalProd;
            }
        }

    }

    private Production newSeparatedProduction(
            Element leftElement,
            Element rightElement,
            CardinalityInterval cardinality) {

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newNormalProduction");
        }

        if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            String plusName = leftElement.getName() + " Separator "
                    + rightElement.getName() + "_+";

            if (this.grammar.containsProduction(plusName)) {
                return this.grammar.getProduction(plusName);
            }
            else {
                Production plusProd = new Production(plusName);
                Element firstElement;

                if (leftElement instanceof Element.TokenElement) {
                    firstElement = new Element.TokenElement(
                            leftElement.getName());
                }
                else {
                    firstElement = new Element.ProductionElement(
                            ((Element.ProductionElement) leftElement)
                                    .getReference());
                }

                String alternatedStarName = rightElement.getName()
                        + leftElement.getName() + "_*";

                Element secondElement;

                if (this.grammar.containsProduction(alternatedStarName)) {
                    secondElement = new Element.ProductionElement(
                            this.grammar.getProduction(alternatedStarName));
                }
                else {
                    secondElement = new Element.ProductionElement(
                            newAlternatedProduction(leftElement, rightElement,
                                    CardinalityInterval.ZERO_OR_MORE));
                }

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(firstElement);
                alternativeElements.add(secondElement);

                plusProd.addAlternative(newAlternative(plusProd,
                        alternativeElements));

                return plusProd;

            }
        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            String starName = leftElement.getName() + " Separator "
                    + rightElement.getName() + "_*";

            if (this.grammar.containsProduction(starName)) {
                return this.grammar.getProduction(starName);
            }
            else {
                Production starProd = new Production(starName);

                String separatedPlusName = leftElement.getName()
                        + " Separator " + rightElement.getName() + "_+";

                if (this.grammar.containsProduction(separatedPlusName)) {
                    starProd.addAlternative(newAlternative(
                            starProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(separatedPlusName))));
                }
                else {
                    starProd.addAlternative(newAlternative(
                            starProd,
                            new Element.ProductionElement(
                                    newAlternatedProduction(rightElement,
                                            leftElement,
                                            CardinalityInterval.ONE_OR_MORE))));
                }

                return starProd;

            }
        }
        else if (cardinality.equals(cardinality.isANumber())) {
            BigInteger number = cardinality.getLowerBound().getValue();
            String numberName = leftElement + " Separator " + rightElement
                    + "_" + number.toString();

            if (this.grammar.containsProduction(numberName)) {
                return this.grammar.getProduction(numberName);
            }
            else {
                Production numberProd = new Production(numberName);

                if (number.compareTo(BigInteger.valueOf(2L)) == 0) {

                    LinkedList<Element> alternativeElements = new LinkedList<Element>();
                    alternativeElements.add(leftElement.clone());
                    alternativeElements.add(rightElement.clone());
                    alternativeElements.add(leftElement.clone());

                    numberProd.addAlternative(newAlternative(numberProd,
                            alternativeElements));
                }
                else {
                    String previousNumberName = leftElement + " Separator "
                            + rightElement + "_"
                            + number.subtract(BigInteger.ONE).toString();

                    if (this.grammar.containsProduction(previousNumberName)) {
                        numberProd.addAlternative(newAlternative(
                                numberProd,
                                new Element.ProductionElement(this.grammar
                                        .getProduction(previousNumberName))));
                    }
                    else {
                        Production previousNumberProd = newSeparatedProduction(
                                leftElement,
                                rightElement,
                                new CardinalityInterval(cardinality
                                        .getLowerBound().subtract(
                                                BigInteger.ONE)));

                        numberProd.addAlternative(newAlternative(numberProd,
                                new Element.ProductionElement(
                                        previousNumberProd)));
                    }
                }

                this.grammar.addProduction(numberProd);

                return numberProd;
            }
        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();

            String atLeastName = leftElement.getName() + " Separator "
                    + rightElement.getName() + "_" + lowerBoundValue.toString()
                    + "...";

            if (this.grammar.containsProduction(atLeastName)) {
                return this.grammar.getProduction(atLeastName);
            }
            else {
                Production atLeastProd = new Production(atLeastName);

                String numberName = leftElement.getName() + " Separator "
                        + rightElement.getName() + "_"
                        + lowerBoundValue.toString();

                if (this.grammar.containsProduction(numberName)) {
                    atLeastProd.addAlternative(newAlternative(
                            atLeastProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(atLeastName))));
                }
                else {
                    atLeastProd
                            .addAlternative(newAlternative(
                                    atLeastProd,
                                    new Element.ProductionElement(
                                            newSeparatedProduction(
                                                    leftElement,
                                                    rightElement,
                                                    new CardinalityInterval(
                                                            cardinality
                                                                    .getLowerBound(),
                                                            cardinality
                                                                    .getLowerBound())))));
                }

                String starName = leftElement.getName() + " Separator "
                        + rightElement.getName() + "_*";

                if (this.grammar.containsProduction(starName)) {
                    atLeastProd.addAlternative(newAlternative(
                            atLeastProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(starName))));
                }
                else {
                    atLeastProd
                            .addAlternative(newAlternative(
                                    atLeastProd,
                                    new Element.ProductionElement(
                                            newAlternatedProduction(
                                                    rightElement,
                                                    leftElement,
                                                    CardinalityInterval.ZERO_OR_MORE))));
                }

                this.grammar.addProduction(atLeastProd);

                return atLeastProd;
            }
        }
        else {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            String intervalName = leftElement.getName() + " Separator "
                    + rightElement + "_" + lowerBoundValue.toString() + ".."
                    + upperBoundValue.toString();

            if (this.grammar.containsProduction(intervalName)) {
                return this.grammar.getProduction(intervalName);
            }
            else {
                Production intervalProd = new Production(intervalName);

                if (lowerBoundValue.equals(BigInteger.ZERO)) {

                    String smallerIntervalName = leftElement.getName()
                            + " Separator "
                            + rightElement
                            + "_"
                            + lowerBoundValue.toString()
                            + ".."
                            + upperBoundValue.subtract(BigInteger.ZERO)
                                    .toString();

                    if (this.grammar.containsProduction(smallerIntervalName)) {
                        intervalProd.addAlternative(newAlternative(
                                intervalProd,
                                new Element.ProductionElement(this.grammar
                                        .getProduction(smallerIntervalName))));
                    }
                    else {

                        if (upperBoundValue.equals(BigInteger.valueOf(2L))) {
                            String qmarkName = leftElement.getName()
                                    + " Separator " + rightElement + "_?";
                            Alternative alternative;

                            if (this.grammar.containsProduction(qmarkName)) {
                                alternative = newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                this.grammar
                                                        .getProduction(qmarkName)));
                            }
                            else {
                                alternative = newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                newSeparatedProduction(
                                                        leftElement,
                                                        rightElement,
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
                                                            leftElement,
                                                            rightElement,
                                                            new CardinalityInterval(
                                                                    Bound.ZERO,
                                                                    new Bound(
                                                                            upperBoundValue
                                                                                    .subtract(BigInteger.ONE)))))));
                        }

                        String upperNumberName = leftElement.getName()
                                + " Separator " + rightElement + "_"
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
                                                            leftElement,
                                                            rightElement,
                                                            new CardinalityInterval(
                                                                    cardinality
                                                                            .getUpperBound(),
                                                                    cardinality
                                                                            .getUpperBound())))));
                        }
                    }

                }
                else {
                    String lowerNumberName = leftElement.getName()
                            + " Separator " + rightElement + "_"
                            + lowerBoundValue.toString();

                    LinkedList<Element> alternativeElements = new LinkedList<Element>();

                    if (this.grammar.containsProduction(lowerNumberName)) {
                        alternativeElements.add(new Element.ProductionElement(
                                this.grammar.getProduction(lowerNumberName)));
                    }
                    else {
                        alternativeElements.add(new Element.ProductionElement(
                                newSeparatedProduction(leftElement,
                                        rightElement, new CardinalityInterval(
                                                cardinality.getLowerBound(),
                                                cardinality.getLowerBound()))));
                    }

                    String zeroToLowerBoundName = rightElement.getName()
                            + leftElement.getName() + "_0.."
                            + lowerBoundValue.toString();

                    if (this.grammar.containsProduction(zeroToLowerBoundName)) {
                        alternativeElements.add(new Element.ProductionElement(
                                this.grammar
                                        .getProduction(zeroToLowerBoundName)));
                    }
                    else {
                        alternativeElements.add(new Element.ProductionElement(
                                newAlternatedProduction(rightElement,
                                        leftElement,
                                        new CardinalityInterval(Bound.ZERO,
                                                cardinality.getLowerBound()))));

                    }

                    intervalProd.addAlternative(newAlternative(intervalProd,
                            alternativeElements));
                }

                return intervalProd;
            }
        }
    }

    private Production newAlternatedProduction(
            Element leftElement,
            Element rightElement,
            CardinalityInterval cardinality) {

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newAlternatedProduction");
        }

        if (cardinality.equals(CardinalityInterval.ZERO_ONE)) {
            String qmarkName = leftElement.getName() + rightElement.getName()
                    + "_?";

            if (this.grammar.containsProduction(qmarkName)) {
                return this.grammar.getProduction(qmarkName);
            }
            else {
                Production qmarkProd = new Production(qmarkName);
                LinkedList<Element> elements = new LinkedList<Element>();
                elements.add(leftElement.clone());
                elements.add(rightElement.clone());
                qmarkProd.addAlternative(newAlternative(qmarkProd, elements));
                qmarkProd.addAlternative(new Alternative(qmarkProd));

                this.grammar.addProduction(qmarkProd);

                return qmarkProd;
            }
        }
        else if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            String plusName = leftElement.getName() + rightElement.getName()
                    + "_+";

            if (this.grammar.containsProduction(plusName)) {
                return this.grammar.getProduction(plusName);
            }
            else {
                Production plusProd = new Production(plusName);

                LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
                firstAlternativeElements.add(new Element.ProductionElement(
                        plusProd));
                firstAlternativeElements.add(leftElement.clone());
                firstAlternativeElements.add(rightElement.clone());

                plusProd.addAlternative(newAlternative(plusProd,
                        firstAlternativeElements));

                LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
                secondAlternativeElements.add(leftElement);
                secondAlternativeElements.add(rightElement);
                plusProd.addAlternative(newAlternative(plusProd,
                        secondAlternativeElements));

                this.grammar.addProduction(plusProd);

                return plusProd;
            }
        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            String starName = leftElement.getName() + rightElement.getName()
                    + "_*";

            if (this.grammar.containsProduction(starName)) {
                return this.grammar.getProduction(starName);
            }
            else {
                Production starProd = new Production(starName);

                String plusName = leftElement.getName()
                        + rightElement.getName() + "_+";

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
                                    newAlternatedProduction(leftElement,
                                            rightElement,
                                            CardinalityInterval.ONE_OR_MORE))));
                }

                starProd.addAlternative(new Alternative(starProd));

                this.grammar.addProduction(starProd);

                return starProd;
            }
        }
        else if (cardinality.isANumber()) {
            BigInteger number = cardinality.getLowerBound().getValue();
            String numberName = leftElement.getName() + rightElement.getName()
                    + "_" + number.toString();

            if (this.grammar.containsProduction(numberName)) {
                return this.grammar.getProduction(numberName);
            }
            else {
                Production numberProd = new Production(numberName);

                if (number.compareTo(BigInteger.ONE) == 0) {

                    LinkedList<Element> alternativeElements = new LinkedList<Element>();
                    alternativeElements.add(leftElement.clone());
                    alternativeElements.add(rightElement.clone());

                    numberProd.addAlternative(newAlternative(numberProd,
                            alternativeElements));
                }
                else {
                    String previousNumberName = leftElement.getName()
                            + rightElement.getName() + "_"
                            + number.subtract(BigInteger.ONE).toString();

                    if (this.grammar.containsProduction(previousNumberName)) {
                        numberProd.addAlternative(newAlternative(
                                numberProd,
                                new Element.ProductionElement(this.grammar
                                        .getProduction(previousNumberName))));
                    }
                    else {
                        Production previousNumberProd = newAlternatedProduction(
                                leftElement,
                                rightElement,
                                new CardinalityInterval(cardinality
                                        .getLowerBound().subtract(
                                                BigInteger.ONE)));

                        numberProd.addAlternative(newAlternative(numberProd,
                                new Element.ProductionElement(
                                        previousNumberProd)));
                    }
                }

                this.grammar.addProduction(numberProd);

                return numberProd;
            }
        }
        else if (cardinality.upperBoundIsInfinite()) {
            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            String atLeastName = leftElement.getName() + rightElement.getName()
                    + "_" + lowerBoundValue.toString() + "...";

            if (this.grammar.containsProduction(atLeastName)) {
                return this.grammar.getProduction(atLeastName);
            }
            else {
                Production atLeastProd = new Production(atLeastName);

                String numberName = leftElement.getName()
                        + rightElement.getName() + "_"
                        + lowerBoundValue.toString();

                if (this.grammar.containsProduction(numberName)) {
                    atLeastProd.addAlternative(newAlternative(
                            atLeastProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(atLeastName))));
                }
                else {
                    atLeastProd
                            .addAlternative(newAlternative(
                                    atLeastProd,
                                    new Element.ProductionElement(
                                            newAlternatedProduction(
                                                    leftElement,
                                                    rightElement,
                                                    new CardinalityInterval(
                                                            cardinality
                                                                    .getLowerBound(),
                                                            cardinality
                                                                    .getLowerBound())))));
                }

                String starName = leftElement.getName()
                        + rightElement.getName() + "_*";

                if (this.grammar.containsProduction(starName)) {
                    atLeastProd.addAlternative(newAlternative(
                            atLeastProd,
                            new Element.ProductionElement(this.grammar
                                    .getProduction(starName))));
                }
                else {
                    atLeastProd
                            .addAlternative(newAlternative(
                                    atLeastProd,
                                    new Element.ProductionElement(
                                            newSeparatedProduction(
                                                    leftElement,
                                                    rightElement,
                                                    CardinalityInterval.ZERO_OR_MORE))));
                }

                this.grammar.addProduction(atLeastProd);

                return atLeastProd;
            }

        }
        else {

            BigInteger lowerBoundValue = cardinality.getLowerBound().getValue();
            BigInteger upperBoundValue = cardinality.getUpperBound().getValue();

            String intervalName = leftElement.getName()
                    + rightElement.getName() + "_" + lowerBoundValue.toString()
                    + ".." + upperBoundValue.toString();

            if (this.grammar.containsProduction(intervalName)) {
                return this.grammar.getProduction(intervalName);
            }
            else {
                Production intervalProd = new Production(intervalName);

                if (lowerBoundValue.equals(BigInteger.ZERO)) {

                    String smallerIntervalName = leftElement.getName()
                            + rightElement.getName()
                            + "_"
                            + lowerBoundValue.toString()
                            + ".."
                            + upperBoundValue.subtract(BigInteger.ZERO)
                                    .toString();

                    if (this.grammar.containsProduction(smallerIntervalName)) {
                        intervalProd.addAlternative(newAlternative(
                                intervalProd,
                                new Element.ProductionElement(this.grammar
                                        .getProduction(smallerIntervalName))));
                    }
                    else {

                        if (upperBoundValue.equals(BigInteger.valueOf(2L))) {

                            Alternative alternative;
                            String qmarkName = leftElement.getName()
                                    + rightElement.getName() + "_?";

                            if (this.grammar.containsProduction(qmarkName)) {
                                alternative = newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                this.grammar
                                                        .getProduction(qmarkName)));
                            }
                            else {
                                alternative = newAlternative(
                                        intervalProd,
                                        new Element.ProductionElement(
                                                newAlternatedProduction(
                                                        leftElement,
                                                        rightElement,
                                                        CardinalityInterval.ZERO_ONE)));
                            }

                            intervalProd.addAlternative(alternative);

                        }
                        else {
                            Alternative alternative = newAlternative(
                                    intervalProd,
                                    new Element.ProductionElement(
                                            newAlternatedProduction(
                                                    leftElement,
                                                    rightElement,
                                                    new CardinalityInterval(
                                                            Bound.ZERO,
                                                            new Bound(
                                                                    upperBoundValue
                                                                            .subtract(BigInteger.ONE))))));
                            intervalProd.addAlternative(alternative);

                        }

                        String upperNumberName = leftElement.getName()
                                + rightElement.getName() + "_"
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
                                                            leftElement,
                                                            rightElement,
                                                            new CardinalityInterval(
                                                                    cardinality
                                                                            .getUpperBound(),
                                                                    cardinality
                                                                            .getUpperBound())))));
                        }
                    }
                }
                else {
                    String lowerNumberName = leftElement.getName()
                            + rightElement.getName() + "_"
                            + lowerBoundValue.toString();

                    LinkedList<Element> alternativeElements = new LinkedList<Element>();

                    if (this.grammar.containsProduction(lowerNumberName)) {
                        alternativeElements.add(new Element.ProductionElement(
                                this.grammar.getProduction(lowerNumberName)));
                    }
                    else {
                        alternativeElements.add(new Element.ProductionElement(
                                newAlternatedProduction(leftElement,
                                        rightElement, new CardinalityInterval(
                                                cardinality.getLowerBound(),
                                                cardinality.getLowerBound()))));
                    }

                    String zeroToLowerBoundName = leftElement.getName()
                            + rightElement.getName() + "_0.."
                            + lowerBoundValue.toString();

                    if (this.grammar.containsProduction(zeroToLowerBoundName)) {
                        alternativeElements.add(new Element.ProductionElement(
                                this.grammar
                                        .getProduction(zeroToLowerBoundName)));
                    }
                    else {
                        alternativeElements.add(new Element.ProductionElement(
                                newAlternatedProduction(leftElement,
                                        rightElement,
                                        new CardinalityInterval(Bound.ZERO,
                                                cardinality.getLowerBound()))));

                    }

                    intervalProd.addAlternative(newAlternative(intervalProd,
                            alternativeElements));
                }

                return intervalProd;
            }
        }
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
