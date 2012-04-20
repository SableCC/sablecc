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
import org.sablecc.sablecc.core.Parser.ParserElement.DoubleElement;
import org.sablecc.sablecc.core.Parser.ParserElement.ElementType;
import org.sablecc.sablecc.core.Parser.ParserElement.SingleElement;
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

    // This method find a valid name for character declared inline
    // that can't be used in a java identifier.
    private static String computeValidName(
            String name) {

        StringBuilder sb = new StringBuilder();

        // TODO Complete these list

        for (char c : name.toCharArray()) {

            switch (c) {

            case ',':
                sb.append("comma");
                break;
            case '_':
                sb.append("underscore");
                break;
            case '.':
                sb.append("period");
                break;
            case ':':
                sb.append("semicolon");
                break;
            case '?':
                sb.append("questionMark");
                break;
            case '+':
                sb.append("plus");
                break;
            case '-':
                sb.append("minus");
                break;
            case '*':
                sb.append("star");
                break;
            case '/':
                sb.append("slash");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String computeNewProductionName(
            Element sourceElement,
            CardinalityInterval cardinality) {

        String name = sourceElement.getTypeName();

        if (sourceElement.getTypeName().startsWith("'")) {
            name = computeValidName(name.substring(1, name.length() - 1));
        }

        name = "$" + name;

        if (cardinality.equals(CardinalityInterval.ZERO_ONE)) {
            name += "_qmark";
        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name += "_star";
        }
        else if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            name += "_plus";

        }
        else if (cardinality.isANumber()) {
            name += "_" + cardinality.getLowerBound().getValue();
        }
        else if (cardinality.upperBoundIsInfinite()) {
            name += "_" + cardinality.getLowerBound().getValue();
        }
        else {
            name += "_from" + cardinality.getLowerBound().getValue() + "to"
                    + cardinality.getUpperBound().getValue();
        }

        return name;
    }

    private static String computeNewSeparatedProductionName(
            Element leftSourceElement,
            Element rightSourceElement,
            CardinalityInterval cardinality) {

        String name = leftSourceElement.getTypeName();

        if (leftSourceElement.getTypeName().startsWith("'")) {
            name = computeValidName(name.substring(1, name.length() - 1));
        }

        name += "_SeparatedBy_";

        if (rightSourceElement.getTypeName().startsWith("'")) {
            name += computeValidName(rightSourceElement
                    .getTypeName()
                    .substring(1, rightSourceElement.getTypeName().length() - 1));
        }
        else {
            name += rightSourceElement.getTypeName();
        }

        name = "$" + name;

        if (cardinality.equals(CardinalityInterval.ZERO_ONE)) {
            name += "_qmark";
        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name += "_star";
        }
        else if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            name += "_plus";

        }
        else if (cardinality.isANumber()) {
            name += "_" + cardinality.getLowerBound().getValue();
        }
        else if (cardinality.upperBoundIsInfinite()) {
            name += "_" + cardinality.getLowerBound().getValue();
        }
        else {
            name += "_from" + cardinality.getLowerBound().getValue() + "to"
                    + cardinality.getUpperBound().getValue();
        }

        return name;
    }

    private static String computeNewAlternatedProductionName(
            Element leftSourceElement,
            Element rightSourceElement,
            CardinalityInterval cardinality) {

        String name = leftSourceElement.getTypeName();

        if (leftSourceElement.getTypeName().startsWith("'")) {
            name = computeValidName(name.substring(1, name.length() - 1));
        }

        name += "_";

        if (rightSourceElement.getTypeName().startsWith("'")) {
            name += computeValidName(rightSourceElement
                    .getTypeName()
                    .substring(1, rightSourceElement.getTypeName().length() - 1));
        }
        else {
            name += rightSourceElement.getTypeName();
        }

        name = "$" + name;

        if (cardinality.equals(CardinalityInterval.ZERO_ONE)) {
            name += "_qmark";
        }
        else if (cardinality.equals(CardinalityInterval.ZERO_OR_MORE)) {
            name += "_star";
        }
        else if (cardinality.equals(CardinalityInterval.ONE_OR_MORE)) {
            name += "_plus";

        }
        else if (cardinality.isANumber()) {
            name += "_" + cardinality.getLowerBound().getValue();
        }
        else if (cardinality.upperBoundIsInfinite()) {
            name += "_" + cardinality.getLowerBound().getValue();
        }
        else {
            name += "_from" + cardinality.getLowerBound().getValue() + "to"
                    + cardinality.getUpperBound().getValue();
        }

        return name;
    }

    @Override
    public void visitParserProduction(
            ParserProduction node) {

        this.alternatives = new LinkedList<Alternative>();

        if (node.getContext().isNamed()) {
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
                    node.getTransformation(), grammar);
        }
        else { // Grammar hasn't Tree
            LinkedList<SAlternativeTransformationElement> transformationElements = new LinkedList<SAlternativeTransformationElement>();

            LinkedList<SAlternativeTransformationElement> newElements = new LinkedList<SAlternativeTransformationElement>();

            for (Element element : alternative.getElements()) {
                if (element instanceof Element.ProductionElement) {
                    Element.ProductionElement productionElement = (Element.ProductionElement) element;

                    SProductionTransformationElement targetProdTransformation = productionElement
                            .getReference().getTransformation().getElements()
                            .get(0);

                    if (targetProdTransformation instanceof SProductionTransformationElement.NormalElement) {
                        SProductionTransformationElement.NormalElement normalElement = (SProductionTransformationElement.NormalElement) targetProdTransformation;

                        if (normalElement.getCardinality().equals(
                                CardinalityInterval.ONE_ONE)
                                || normalElement.getCardinality().equals(
                                        CardinalityInterval.ZERO_ONE)) {

                            if (normalElement.getName().equals(
                                    productionElement.getReference().getName())) {
                                newElements
                                        .add(new SAlternativeTransformationElement.ReferenceElement(
                                                element, element));
                            }
                            else {
                                newElements
                                        .add(new SAlternativeTransformationElement.ReferenceElement(
                                                element,
                                                targetProdTransformation));
                            }

                        }
                        else {

                            List<SAlternativeTransformationListElement> listElements = new LinkedList<SAlternativeTransformationListElement>();
                            listElements
                                    .add(new SAlternativeTransformationListElement.NormalListElement(
                                            element, targetProdTransformation));
                            newElements
                                    .add(new SAlternativeTransformationElement.ListElement(
                                            listElements,
                                            new Type.SimpleType.HomogeneousType(
                                                    normalElement.getName(),
                                                    normalElement
                                                            .getCardinality())));

                        }
                    }
                    else if (targetProdTransformation instanceof SProductionTransformationElement.SeparatedElement) {

                        SProductionTransformationElement.SeparatedElement separatedElement = (SProductionTransformationElement.SeparatedElement) targetProdTransformation;

                        List<SAlternativeTransformationListElement> listElements = new LinkedList<SAlternativeTransformationListElement>();
                        listElements
                                .add(new SAlternativeTransformationListElement.NormalListElement(
                                        element, targetProdTransformation));
                        newElements
                                .add(new SAlternativeTransformationElement.ListElement(
                                        listElements,
                                        new Type.SimpleType.SeparatedType(
                                                separatedElement.getLeftName(),
                                                separatedElement.getRightName(),
                                                separatedElement
                                                        .getCardinality())));

                    }
                    else if (targetProdTransformation instanceof SProductionTransformationElement.AlternatedElement) {

                        SProductionTransformationElement.AlternatedElement alternatedElement = (SProductionTransformationElement.AlternatedElement) targetProdTransformation;

                        List<SAlternativeTransformationListElement> listElements = new LinkedList<SAlternativeTransformationListElement>();
                        listElements
                                .add(new SAlternativeTransformationListElement.NormalListElement(
                                        element, targetProdTransformation));
                        newElements
                                .add(new SAlternativeTransformationElement.ListElement(
                                        listElements,
                                        new Type.SimpleType.AlternatedType(
                                                alternatedElement.getLeftName(),
                                                alternatedElement
                                                        .getRightName(),
                                                alternatedElement
                                                        .getCardinality())));

                    }
                    else {
                        throw new InternalException(
                                "Unexpected production transformation element type");
                    }
                }
                else {
                    newElements
                            .add(new SAlternativeTransformationElement.ReferenceElement(
                                    element, element));
                }

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
    public void visitParserSingleElement(
            SingleElement node) {

        if (node.getElementType() == ElementType.NORMAL) {
            if (!node.getCardinality().equals(CardinalityInterval.ZERO_ZERO)) {
                Element simpleElement;
                PUnit unit = ((ANormalElement) node.getDeclaration()).getUnit();
                if (node.getReference() instanceof Parser.ParserProduction) {
                    String prodName = ((ANameUnit) unit).getIdentifier()
                            .getText();

                    simpleElement = new Element.ProductionElement(
                            node.getName(),
                            GrammarSimplificator.grammar
                                    .getProduction(prodName));
                }
                else {
                    simpleElement = new Element.TokenElement(node.getName(),
                            unit);
                }

                if (node.getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                    this.elements.add(simpleElement);
                }
                else {
                    Element complexElement = new Element.ProductionElement(
                            node.getName(), newNormalProduction(node,
                                    simpleElement, node.getCardinality()));
                    this.elements.add(complexElement);
                }

            }
        }
    }

    @Override
    public void visitParserDoubleElement(
            DoubleElement node) {

        if (node.getElementType() == ElementType.SEPARATED) {
            caseSeparatedElement(node);
        }
        else {
            caseAlternatedElement(node);
        }
    }

    private void caseSeparatedElement(
            DoubleElement node) {

        if (!node.getCardinality().equals(CardinalityInterval.ZERO_ZERO)) {
            Element leftSimpleElement;
            PUnit leftUnit = ((ASeparatedElement) node.getDeclaration())
                    .getLeft();
            PUnit rightUnit = ((ASeparatedElement) node.getDeclaration())
                    .getRight();

            if (node.getLeftReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) leftUnit).getIdentifier()
                        .getText();

                leftSimpleElement = new Element.ProductionElement("",
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                leftSimpleElement = new Element.TokenElement("", leftUnit);
            }

            Element rightSimpleElement;

            if (node.getRightReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) rightUnit).getIdentifier()
                        .getText();

                rightSimpleElement = new Element.ProductionElement("",
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                rightSimpleElement = new Element.TokenElement("", rightUnit);
            }

            if (node.getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                this.elements.add(leftSimpleElement);
            }
            else {
                Element complexElement = new Element.ProductionElement("",
                        newSeparatedProduction(node, leftSimpleElement,
                                rightSimpleElement, node.getCardinality()));
                this.elements.add(complexElement);
            }
        }
    }

    private void caseAlternatedElement(
            DoubleElement node) {

        if (!node.getCardinality().equals(CardinalityInterval.ZERO_ZERO)) {
            Element leftSimpleElement;
            PUnit leftUnit = ((AAlternatedElement) node.getDeclaration())
                    .getLeft();
            PUnit rightUnit = ((AAlternatedElement) node.getDeclaration())
                    .getRight();

            if (node.getLeftReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) leftUnit).getIdentifier()
                        .getText();

                leftSimpleElement = new Element.ProductionElement("",
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                leftSimpleElement = new Element.TokenElement("", leftUnit);
            }

            Element rightSimpleElement;

            if (node.getRightReference() instanceof Parser.ParserProduction) {
                String prodName = ((ANameUnit) rightUnit).getIdentifier()
                        .getText();

                rightSimpleElement = new Element.ProductionElement("",
                        GrammarSimplificator.grammar.getProduction(prodName));
            }
            else {
                rightSimpleElement = new Element.TokenElement("", rightUnit);
            }

            if (node.getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                this.elements.add(leftSimpleElement);
                this.elements.add(rightSimpleElement);
            }
            else {
                Element complexElement = new Element.ProductionElement("",
                        newAlternatedProduction(node, leftSimpleElement,
                                rightSimpleElement, node.getCardinality()));
                this.elements.add(complexElement);
            }
        }
    }

    private static Production newNormalProduction(
            Parser.ParserElement.SingleElement node,
            Element element,
            CardinalityInterval cardinality) {

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newNormalProduction");
        }

        Production production;
        String name = computeNewProductionName(element, cardinality);

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
            Parser.ParserElement.DoubleElement node,
            Element leftElement,
            Element rightElement,
            CardinalityInterval cardinality) {

        if (node.getElementType() != ElementType.SEPARATED) {
            throw new InternalException("A " + node.getNameType()
                    + " should'nt be simplified in a separated production");
        }

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newNormalProduction");
        }

        String name = computeNewSeparatedProductionName(leftElement,
                rightElement, cardinality);

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
            Parser.ParserElement.DoubleElement node,
            Element leftElement,
            Element rightElement,
            CardinalityInterval cardinality) {

        if (cardinality.equals(CardinalityInterval.ONE_ONE)
                || cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
            throw new InternalException(
                    "cardinality shouldn't be (1,1) or (0,0) in newAlternatedProduction");
        }

        String name = computeNewAlternatedProductionName(leftElement,
                rightElement, cardinality);

        if (grammar.containsProduction(name)) {
            return grammar.getProduction(name);
        }
        else {
            return new AlternatedProductionBuilder(node, leftElement,
                    rightElement, cardinality, grammar).getNewProduction();
        }
    }

    private static class NormalProductionBuilder {

        private final Parser.ParserElement.SingleElement parserElement;

        private final Element sElement;

        private final CardinalityInterval cardinality;

        private Production newProduction;

        private SProductionTransformation newTransformation;

        private SGrammar grammar;

        public NormalProductionBuilder(
                Parser.ParserElement.SingleElement parserElement,
                Element sElement,
                CardinalityInterval cardinality,
                SGrammar grammar) {

            this.parserElement = parserElement;
            this.sElement = sElement;
            this.cardinality = cardinality;
            this.grammar = grammar;

            if (parserElement.getElementType() == ElementType.DANGLING) {
                throw new InternalException("The element shouldn't be dangling");
            }

            if (parserElement.getReference() instanceof Token) {
                this.newTransformation = new SProductionTransformation(null,
                        this.parserElement.getElement(), this.parserElement,
                        this.cardinality);
            }
            else if (parserElement.getReference() instanceof Parser.ParserProduction) {
                Parser.ParserProduction parserProduction = (Parser.ParserProduction) parserElement
                        .getReference();
                this.newTransformation = new SProductionTransformation(null);
                this.newTransformation.addElement(
                        parserProduction.getTransformation().getElements()
                                .get(0),
                        parserProduction.getTransformation().getElements()
                                .get(0).getCardinality().union(cardinality));
            }

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

            this.newTransformation.addProduction(this.newProduction);
            this.newProduction.addTransformation(this.newTransformation);

            this.grammar.addProduction(this.newProduction);

        }

        public Production getNewProduction() {

            return this.newProduction;
        }

        private void qmarkCase() {

            String qmarkName = computeNewProductionName(this.sElement,
                    this.cardinality);

            Production qmarkProd = new Production(
                    this.grammar.getNextProductionId(), qmarkName);

            LinkedList<Element> elements = new LinkedList<Element>();
            elements.add(this.sElement.clone());
            Alternative firstAlternative = new Alternative(qmarkProd, elements);

            firstAlternative.addTransformation(new SAlternativeTransformation(
                    firstAlternative, elements));

            qmarkProd.addAlternative(firstAlternative);
            qmarkProd.addAlternative(new Alternative(qmarkProd));

            this.newProduction = qmarkProd;

        }

        private void plusCase() {

            String plusName = computeNewProductionName(this.sElement,
                    this.cardinality);

            Production plusProd = new Production(
                    this.grammar.getNextProductionId(), plusName);
            // p = a_+ a | a;

            // First alternative
            LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            Element firstElement = new Element.ProductionElement("", plusProd);
            firstAlternativeElements.add(firstElement);
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement, this.newTransformation.getElements()
                                    .get(0)));

            Element secondElement = this.sElement.clone();
            firstAlternativeElements.add(secondElement);
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            secondElement, secondElement));

            plusProd.addAlternative(newListAlternative(plusProd,
                    firstAlternativeElements, firstAltTransformationElements));

            // Second alternative
            LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> secondAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            secondAlternativeElements.add(this.sElement.clone());
            secondAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            secondAlternativeElements.get(0),
                            secondAlternativeElements.get(0)));
            plusProd.addAlternative(newListAlternative(plusProd,
                    secondAlternativeElements, secondAltTransformationElements));

            this.newProduction = plusProd;

        }

        private void starCase() {

            String starName = computeNewProductionName(this.sElement,
                    this.cardinality);

            Production starProd = new Production(
                    this.grammar.getNextProductionId(), starName);

            // First Alternative
            String plusName = computeNewProductionName(this.sElement,
                    CardinalityInterval.ONE_OR_MORE);

            Element.ProductionElement firstElement;
            if (this.grammar.containsProduction(plusName)) {
                firstElement = new Element.ProductionElement("",
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
                            firstElement, firstElement.getReference()
                                    .getTransformation().getElements().get(0)));

            starProd.addAlternative(newListAlternative(starProd,
                    firstAlternativeElements, firstAltTransformationElements));

            // Second Alternative
            starProd.addAlternative(new Alternative(starProd));

            this.newProduction = starProd;

        }

        private void numberCase() {

            BigInteger number = this.cardinality.getLowerBound().getValue();
            String numberName = computeNewProductionName(this.sElement,
                    this.cardinality);

            Production numberProd = new Production(
                    this.grammar.getNextProductionId(), numberName);

            if (number.compareTo(BigInteger.valueOf(2L)) == 0) {

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(this.sElement.clone());
                alternativeElements.add(this.sElement.clone());

                LinkedList<SAlternativeTransformationListElement> alternativeTransformationElement = new LinkedList<SAlternativeTransformationListElement>();
                alternativeTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(0), alternativeElements
                                        .get(0)));
                alternativeTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1), alternativeElements
                                        .get(1)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        alternativeElements, alternativeTransformationElement));
            }
            else {
                String previousNumberName = computeNewProductionName(
                        this.sElement, this.cardinality);

                Element firstElement;
                if (this.grammar.containsProduction(previousNumberName)) {
                    firstElement = new Element.ProductionElement("",
                            this.grammar.getProduction(previousNumberName));
                }
                else {
                    Production previousNumberProd = newNormalProduction(
                            this.parserElement,
                            this.sElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().subtract(BigInteger.ONE),
                                    this.cardinality.getLowerBound().subtract(
                                            BigInteger.ONE)));

                    firstElement = new Element.ProductionElement("",
                            previousNumberProd);
                }

                LinkedList<Element> alternativeElements = new LinkedList<Element>();
                alternativeElements.add(firstElement);
                alternativeElements.add(this.sElement.clone());

                LinkedList<SAlternativeTransformationListElement> alternativeTransformationElement = new LinkedList<SAlternativeTransformationListElement>();
                alternativeTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                firstElement, firstElement));
                alternativeTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1), alternativeElements
                                        .get(1)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        alternativeElements, alternativeTransformationElement));
            }

            this.newProduction = numberProd;

        }

        private void atLeastCase() {

            String atLeastName = computeNewProductionName(this.sElement,
                    this.cardinality);

            Production atLeastProd = new Production(
                    this.grammar.getNextProductionId(), atLeastName);

            String numberName = computeNewProductionName(this.sElement,
                    new CardinalityInterval(this.cardinality.getLowerBound(),
                            this.cardinality.getLowerBound()));

            Element.ProductionElement firstElement;
            if (this.grammar.containsProduction(numberName)) {
                firstElement = new Element.ProductionElement("",
                        this.grammar.getProduction(atLeastName));
            }
            else {
                firstElement = new Element.ProductionElement("",
                        newNormalProduction(
                                this.parserElement,
                                this.sElement,
                                new CardinalityInterval(this.cardinality
                                        .getLowerBound(), this.cardinality
                                        .getLowerBound())));
            }

            String starName = computeNewProductionName(this.sElement,
                    CardinalityInterval.ZERO_OR_MORE);

            Element.ProductionElement secondElement;
            if (this.grammar.containsProduction(starName)) {
                secondElement = new Element.ProductionElement("",
                        this.grammar.getProduction(starName));
            }
            else {
                secondElement = new Element.ProductionElement("",
                        newNormalProduction(this.parserElement, this.sElement,
                                CardinalityInterval.ZERO_OR_MORE));
            }

            LinkedList<Element> alternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            alternativeElements.add(firstElement);
            alternativeElements.add(secondElement);

            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement, firstElement.getReference()
                                    .getTransformation().getElements().get(0)));
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            secondElement, firstElement.getReference()
                                    .getTransformation().getElements().get(0)));

            atLeastProd.addAlternative(newListAlternative(atLeastProd,
                    alternativeElements, altTransformationElements));

            this.newProduction = atLeastProd;

        }

        private void intervalCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            BigInteger upperBoundValue = this.cardinality.getUpperBound()
                    .getValue();

            String intervalName = computeNewProductionName(this.sElement,
                    this.cardinality);

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
                Element.ProductionElement firstElement;

                if (this.grammar.containsProduction(plusOneIntervalName)) {
                    firstElement = new Element.ProductionElement("",
                            this.grammar.getProduction(plusOneIntervalName));
                }
                else {
                    firstElement = new Element.ProductionElement("",
                            newNormalProduction(this.parserElement,
                                    this.sElement, new CardinalityInterval(
                                            Bound.ONE, new Bound(
                                                    upperBoundValue))));
                }

                firstAlternativeElements.add(firstElement);
                firstAltTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement, firstElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));

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
                                firstAlternativeElements.get(0),
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
                                secondAlternativeElements.get(0),
                                secondAlternativeElements.get(0)));
                secondAltTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                secondAlternativeElements.get(1),
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

                Element.ProductionElement firstElement;

                if (this.grammar.containsProduction(smallerIntervalName)) {
                    firstElement = new Element.ProductionElement("",
                            this.grammar.getProduction(smallerIntervalName));
                }
                else {
                    firstElement = new Element.ProductionElement("",
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
                                firstElement, firstElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));
                alternativeTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1), alternativeElements
                                        .get(1)));

                intervalProd
                        .addAlternative(newListAlternative(intervalProd,
                                alternativeElements,
                                alternativeTransformationElements));
            }
            // p^(m..n) with n > 1 = p^m p^(0..(n-m))
            else {
                String lowerNumberName = computeNewProductionName(
                        this.sElement,
                        new CardinalityInterval(this.cardinality
                                .getLowerBound(), this.cardinality
                                .getLowerBound()));

                Element.ProductionElement firstElement;

                if (this.grammar.containsProduction(lowerNumberName)) {
                    firstElement = new Element.ProductionElement("",
                            this.grammar.getProduction(lowerNumberName));
                }
                else {
                    firstElement = new Element.ProductionElement("",
                            newNormalProduction(this.parserElement,
                                    this.sElement, new CardinalityInterval(
                                            this.cardinality.getLowerBound(),
                                            this.cardinality.getLowerBound())));
                }

                String zeroToIntervalWidth = computeNewProductionName(
                        this.sElement,
                        new CardinalityInterval(Bound.ZERO, new Bound(
                                upperBoundValue.subtract(lowerBoundValue))));

                Element.ProductionElement secondElement;

                if (this.grammar.containsProduction(zeroToIntervalWidth)) {
                    secondElement = new Element.ProductionElement("",
                            this.grammar.getProduction(zeroToIntervalWidth));
                }
                else {
                    secondElement = new Element.ProductionElement("",
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
                                firstElement, firstElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));
                firstAltTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                secondElement, firstElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        firstAlternativeElements,
                        firstAltTransformationElements));
            }

            this.newProduction = intervalProd;

        }

        private Alternative newListAlternative(
                Production production,
                LinkedList<Element> elements,
                LinkedList<SAlternativeTransformationListElement> transformationElements) {

            Alternative alternative = new Alternative(production, elements);
            alternative.addTransformation(new SAlternativeTransformation(
                    transformationElements, alternative,
                    new Type.SimpleType.HomogeneousType(this.sElement
                            .getTypeName(), this.cardinality)));
            return alternative;
        }

    }

    private static class SeparatedProductionBuilder {

        private final Element sLeftElement;

        private final Element sRightElement;

        private final CardinalityInterval cardinality;

        private Production newProduction;

        private SProductionTransformation newTransformation;

        private SGrammar grammar;

        private final Parser.ParserElement.DoubleElement parserElement;

        public SeparatedProductionBuilder(
                Parser.ParserElement.DoubleElement parserElement,
                Element sLeftElement,
                Element sRightElement,
                CardinalityInterval cardinality,
                SGrammar grammar) {

            if (parserElement.getElementType() != ElementType.SEPARATED) {
                throw new InternalException("A " + parserElement.getNameType()
                        + " should'nt be simplified in a separated production");
            }

            this.parserElement = parserElement;
            this.sLeftElement = sLeftElement;
            this.sRightElement = sRightElement;
            this.cardinality = cardinality;
            this.grammar = grammar;

            this.newTransformation = new SProductionTransformation(null,
                    this.sLeftElement.getTypeName(),
                    this.sRightElement.getTypeName(),
                    parserElement.getLeftReference(),
                    parserElement.getRightReference(), cardinality, true);

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

            this.newTransformation.addProduction(this.newProduction);
            this.newProduction.addTransformation(this.newTransformation);

            this.grammar.addProduction(this.newProduction);

        }

        public Production getNewProduction() {

            return this.newProduction;
        }

        private void plusCase() {

            // (a Sep b)+ = a (b a)*;

            String plusName = computeNewSeparatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

            Production plusProd = new Production(
                    this.grammar.getNextProductionId(), plusName);
            Element firstElement;

            if (this.sLeftElement instanceof Element.TokenElement) {
                firstElement = new Element.TokenElement("",
                        this.sLeftElement.getTypeName());
            }
            else {
                firstElement = new Element.ProductionElement("",
                        ((Element.ProductionElement) this.sLeftElement)
                                .getReference());
            }

            String alternatedStarName = computeNewSeparatedProductionName(
                    this.sRightElement, this.sLeftElement,
                    CardinalityInterval.ZERO_OR_MORE);

            Element.ProductionElement secondElement;

            if (GrammarSimplificator.grammar
                    .containsProduction(alternatedStarName)) {
                secondElement = new Element.ProductionElement("",
                        GrammarSimplificator.grammar
                                .getProduction(alternatedStarName));
            }
            else {
                secondElement = new Element.ProductionElement("",
                        newAlternatedProduction(this.parserElement,
                                this.sRightElement, this.sLeftElement,
                                CardinalityInterval.ZERO_OR_MORE));
            }

            LinkedList<Element> alternativeElements = new LinkedList<Element>();
            alternativeElements.add(firstElement);
            alternativeElements.add(secondElement);

            LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            firstElement, firstElement));
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            secondElement, secondElement.getReference()
                                    .getTransformation().getElements().get(0)));

            plusProd.addAlternative(newListAlternative(plusProd,
                    alternativeElements, altTransformationElements));

            this.newProduction = plusProd;
        }

        private void starCase() {

            // (a Sep b)* = (a Sep b)+ | ;

            String starName = computeNewSeparatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

            Production starProd = new Production(
                    this.grammar.getNextProductionId(), starName);

            // First alternative

            String separatedPlusName = computeNewSeparatedProductionName(
                    this.sLeftElement, this.sRightElement,
                    CardinalityInterval.ONE_OR_MORE);

            Element.ProductionElement firstElement;

            if (this.grammar.containsProduction(separatedPlusName)) {
                firstElement = new Element.ProductionElement("",
                        GrammarSimplificator.grammar
                                .getProduction(separatedPlusName));
            }
            else {
                firstElement = new Element.ProductionElement("",
                        newSeparatedProduction(this.parserElement,
                                this.sLeftElement, this.sRightElement,
                                CardinalityInterval.ONE_OR_MORE));
            }

            LinkedList<Element> firstAltElements = new LinkedList<Element>();
            firstAltElements.add(firstElement);

            LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement, firstElement.getReference()
                                    .getTransformation().getElements().get(0)));

            starProd.addAlternative(newListAlternative(starProd,
                    firstAltElements, firstAltTransformationElements));

            // Second alternative
            starProd.addAlternative(new Alternative(starProd));

            this.newProduction = starProd;

        }

        private void numberCase() {

            BigInteger number = this.cardinality.getLowerBound().getValue();
            String numberName = computeNewSeparatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

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
                                altElements.get(0), altElements.get(0)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(1), altElements.get(1)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(2), altElements.get(2)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        altElements, altTransformationElements));
            }
            else {
                String previousNumberName = computeNewSeparatedProductionName(
                        this.sLeftElement,
                        this.sRightElement,
                        new CardinalityInterval(this.cardinality
                                .getLowerBound().subtract(Bound.ONE),
                                this.cardinality.getLowerBound().subtract(
                                        Bound.ONE)));

                Element.ProductionElement firstElement;

                if (GrammarSimplificator.grammar
                        .containsProduction(previousNumberName)) {
                    firstElement = new Element.ProductionElement("",
                            GrammarSimplificator.grammar
                                    .getProduction(previousNumberName));
                }
                else {
                    Production previousNumberProd = newSeparatedProduction(
                            this.parserElement,
                            this.sLeftElement,
                            this.sRightElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().subtract(BigInteger.ONE),
                                    this.cardinality.getLowerBound().subtract(
                                            BigInteger.ONE)));

                    firstElement = new Element.ProductionElement("",
                            previousNumberProd);
                }

                altElements.add(firstElement);
                altElements.add(this.sLeftElement.clone());
                altElements.add(this.sRightElement.clone());

                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement, firstElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(1), altElements.get(1)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                altElements.get(2), altElements.get(2)));
            }

            numberProd.addAlternative(newListAlternative(numberProd,
                    altElements, altTransformationElements));

            this.newProduction = numberProd;

        }

        private void atLeastCase() {

            String atLeastName = computeNewSeparatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

            Production atLeastProd = new Production(
                    this.grammar.getNextProductionId(), atLeastName);

            LinkedList<Element> altElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            String numberName = computeNewSeparatedProductionName(
                    this.sLeftElement, this.sRightElement,
                    new CardinalityInterval(this.cardinality.getLowerBound(),
                            this.cardinality.getLowerBound()));

            Element.ProductionElement firstElement;

            if (this.grammar.containsProduction(numberName)) {
                firstElement = new Element.ProductionElement("",
                        GrammarSimplificator.grammar.getProduction(numberName));
            }
            else {
                firstElement = new Element.ProductionElement("",
                        newSeparatedProduction(
                                this.parserElement,
                                this.sLeftElement,
                                this.sRightElement,
                                new CardinalityInterval(this.cardinality
                                        .getLowerBound(), this.cardinality
                                        .getLowerBound())));
            }

            String starName = computeNewSeparatedProductionName(
                    this.sLeftElement, this.sRightElement,
                    CardinalityInterval.ZERO_OR_MORE);

            Element.ProductionElement secondElement;

            if (this.grammar.containsProduction(starName)) {
                secondElement = new Element.ProductionElement("",
                        GrammarSimplificator.grammar.getProduction(starName));
            }
            else {
                secondElement = new Element.ProductionElement("",
                        newAlternatedProduction(this.parserElement,
                                this.sRightElement, this.sLeftElement,
                                CardinalityInterval.ZERO_OR_MORE));
            }

            altElements.add(firstElement);
            altElements.add(secondElement);

            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstElement, firstElement.getReference()
                                    .getTransformation().getElements().get(0)));
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            secondElement, secondElement.getReference()
                                    .getTransformation().getElements().get(0)));

            atLeastProd.addAlternative(newListAlternative(atLeastProd,
                    altElements, altTransformationElements));

            this.newProduction = atLeastProd;

        }

        private void intervalCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            BigInteger upperBoundValue = this.cardinality.getUpperBound()
                    .getValue();

            String intervalName = computeNewSeparatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

            Production intervalProd = new Production(
                    this.grammar.getNextProductionId(), intervalName);

            // (a Sep b)^(0..n) = (a Sepb b)^(1..n) | Empty
            if (lowerBoundValue.equals(BigInteger.ZERO)) {

                LinkedList<Element> firstAlternativeElement = new LinkedList<Element>();
                LinkedList<SAlternativeTransformationListElement> firstAltTransformationElement = new LinkedList<SAlternativeTransformationListElement>();

                // (a Sep b)^(0..1) = a?
                if (upperBoundValue.equals(BigInteger.ONE)) {
                    String qmarkLeftName = computeNewProductionName(
                            this.sLeftElement, CardinalityInterval.ZERO_ONE);

                    Element firstElement;

                    if (this.grammar.containsProduction(qmarkLeftName)) {
                        firstElement = new Element.ProductionElement("",
                                this.grammar.getProduction(qmarkLeftName));
                    }
                    else {
                        Production qmarkProd = new Production(
                                this.grammar.getNextProductionId(),
                                qmarkLeftName);

                        qmarkProd.addAlternative(newAlternative(qmarkProd,
                                this.sLeftElement.clone()));
                        qmarkProd.addAlternative(new Alternative(qmarkProd));

                        firstElement = new Element.ProductionElement("",
                                qmarkProd);
                    }

                    firstAlternativeElement.add(firstElement);
                    firstAltTransformationElement
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    firstElement, firstElement));

                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, firstAlternativeElement,
                            firstAltTransformationElement));
                }
                else {

                    // First alternative

                    String plusOneIntervalName = computeNewSeparatedProductionName(
                            this.sLeftElement, this.sRightElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().add(Bound.ONE),
                                    this.cardinality.getUpperBound()));

                    Element.ProductionElement firstElement;

                    if (this.grammar.containsProduction(plusOneIntervalName)) {
                        firstElement = new Element.ProductionElement("",
                                this.grammar.getProduction(plusOneIntervalName));
                    }
                    else {
                        firstElement = new Element.ProductionElement("",
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
                                    firstElement, firstElement.getReference()
                                            .getTransformation().getElements()
                                            .get(0)));

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

                String alternatedZeroToIntervalWidth = computeNewAlternatedProductionName(
                        this.sRightElement,
                        this.sLeftElement,
                        new CardinalityInterval(Bound.ZERO, this.cardinality
                                .getUpperBound().subtract(
                                        this.cardinality.getLowerBound())));

                Element.ProductionElement secondElement;

                if (this.grammar
                        .containsProduction(alternatedZeroToIntervalWidth)) {
                    secondElement = new Element.ProductionElement(
                            "",
                            this.grammar
                                    .getProduction(alternatedZeroToIntervalWidth));
                }
                else {
                    secondElement = new Element.ProductionElement(
                            "",
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
                                firstElement, firstElement));
                alternativeTransformationListElement
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                secondElement, secondElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));

                intervalProd.addAlternative(newListAlternative(intervalProd,
                        alternativeElements,
                        alternativeTransformationListElement));

            }
            // (a Sep b)^(m..n) = (a Sep b)^m (b a)^(0..(n-m))
            else {
                String lowerNumberName = computeNewSeparatedProductionName(
                        this.sLeftElement,
                        this.sRightElement,
                        new CardinalityInterval(this.cardinality
                                .getLowerBound(), this.cardinality
                                .getUpperBound()));

                Element.ProductionElement firstElement;

                if (this.grammar.containsProduction(lowerNumberName)) {
                    firstElement = new Element.ProductionElement("",
                            this.grammar.getProduction(lowerNumberName));
                }
                else {
                    firstElement = new Element.ProductionElement("",
                            newSeparatedProduction(
                                    this.parserElement,
                                    this.sLeftElement,
                                    this.sRightElement,
                                    new CardinalityInterval(this.cardinality
                                            .getLowerBound(), this.cardinality
                                            .getLowerBound())));
                }

                String zeroToLowerBoundName = computeNewSeparatedProductionName(
                        this.sRightElement,
                        this.sLeftElement,
                        new CardinalityInterval(Bound.ZERO, this.cardinality
                                .getLowerBound()));

                Element.ProductionElement secondElement;

                if (this.grammar.containsProduction(zeroToLowerBoundName)) {
                    secondElement = new Element.ProductionElement("",
                            this.grammar.getProduction(zeroToLowerBoundName));
                }
                else {
                    secondElement = new Element.ProductionElement("",
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
                                firstElement, firstElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                secondElement, secondElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));

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
                    transformationElements, alternative,
                    new Type.SimpleType.SeparatedType(this.sLeftElement
                            .getTypeName(), this.sRightElement.getTypeName(),
                            this.cardinality)));
            return alternative;
        }
    }

    private static class AlternatedProductionBuilder {

        private final Parser.ParserElement.DoubleElement parserElement;

        private final Element sLeftElement;

        private final Element sRightElement;

        private final CardinalityInterval cardinality;

        private Production newProduction;

        private SProductionTransformation newTransformation;

        private SGrammar grammar;

        public AlternatedProductionBuilder(
                Parser.ParserElement.DoubleElement parserElement,
                Element sLeftElement,
                Element sRightElement,
                CardinalityInterval cardinality,
                SGrammar grammar) {

            this.parserElement = parserElement;
            this.sLeftElement = sLeftElement;
            this.sRightElement = sRightElement;
            this.cardinality = cardinality;
            this.grammar = grammar;

            this.newTransformation = new SProductionTransformation(null,
                    this.sLeftElement.getTypeName(),
                    this.sRightElement.getTypeName(),
                    parserElement.getRightReference(),
                    parserElement.getLeftReference(), cardinality, false);

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

            this.newTransformation.addProduction(this.newProduction);
            this.newProduction.addTransformation(this.newTransformation);

            this.grammar.addProduction(this.newProduction);

        }

        public Production getNewProduction() {

            return this.newProduction;
        }

        private void plusCase() {

            String plusName = computeNewAlternatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

            Production plusProd = new Production(
                    this.grammar.getNextProductionId(), plusName);

            LinkedList<Element> firstAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> firstAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            firstAlternativeElements.add(new Element.ProductionElement("",
                    plusProd));
            firstAlternativeElements.add(this.sLeftElement.clone());
            firstAlternativeElements.add(this.sRightElement.clone());

            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            firstAlternativeElements.get(0),
                            this.newTransformation.getElements().get(0)));
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            firstAlternativeElements.get(1),
                            firstAlternativeElements.get(1)));
            firstAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            firstAlternativeElements.get(2),
                            firstAlternativeElements.get(2)));

            plusProd.addAlternative(newListAlternative(plusProd,
                    firstAlternativeElements, firstAltTransformationElements));

            LinkedList<Element> secondAlternativeElements = new LinkedList<Element>();
            LinkedList<SAlternativeTransformationListElement> secondAltTransformationElements = new LinkedList<SAlternativeTransformationListElement>();

            secondAlternativeElements.add(this.sLeftElement);
            secondAlternativeElements.add(this.sRightElement);

            secondAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            secondAlternativeElements.get(0),
                            secondAlternativeElements.get(0)));
            secondAltTransformationElements
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            secondAlternativeElements.get(1),
                            secondAlternativeElements.get(1)));

            plusProd.addAlternative(newListAlternative(plusProd,
                    secondAlternativeElements, secondAltTransformationElements));

            this.newProduction = plusProd;

        }

        private void starCase() {

            String starName = computeNewAlternatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

            Production starProd = new Production(
                    this.grammar.getNextProductionId(), starName);

            String plusName = computeNewAlternatedProductionName(
                    this.sLeftElement, this.sRightElement,
                    CardinalityInterval.ONE_OR_MORE);

            // First alternative
            Element.ProductionElement firstElement;

            if (this.grammar.containsProduction(plusName)) {
                firstElement = new Element.ProductionElement("",
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
                            firstElement, firstElement.getReference()
                                    .getTransformation().getElements().get(0)));

            starProd.addAlternative(newListAlternative(starProd,
                    firstAlternativeElements, firstAltTransformationElements));

            // Second alternative
            starProd.addAlternative(new Alternative(starProd));

            this.newProduction = starProd;
        }

        private void numberCase() {

            BigInteger number = this.cardinality.getLowerBound().getValue();
            String numberName = computeNewAlternatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

            Production numberProd = new Production(
                    this.grammar.getNextProductionId(), numberName);

            LinkedList<SAlternativeTransformationListElement> altTransformationElement = new LinkedList<SAlternativeTransformationListElement>();
            LinkedList<Element> alternativeElements = new LinkedList<Element>();

            if (number.compareTo(BigInteger.ONE) == 0) {

                alternativeElements.add(this.sLeftElement.clone());
                alternativeElements.add(this.sRightElement.clone());

                altTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(0), alternativeElements
                                        .get(0)));
                altTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1), alternativeElements
                                        .get(1)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        alternativeElements, altTransformationElement));
            }
            else {
                String previousNumberName = computeNewAlternatedProductionName(
                        this.sLeftElement,
                        this.sRightElement,
                        new CardinalityInterval(this.cardinality
                                .getLowerBound().subtract(Bound.ONE),
                                this.cardinality.getLowerBound().subtract(
                                        Bound.ONE)));

                Element.ProductionElement firstElement;
                if (this.grammar.containsProduction(previousNumberName)) {
                    firstElement = new Element.ProductionElement("",
                            this.grammar.getProduction(previousNumberName));
                }
                else {
                    Production previousNumberProd = newAlternatedProduction(
                            this.parserElement,
                            this.sLeftElement,
                            this.sRightElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().subtract(BigInteger.ONE),
                                    this.cardinality.getLowerBound().subtract(
                                            BigInteger.ONE)));

                    firstElement = new Element.ProductionElement("",
                            previousNumberProd);
                }

                alternativeElements.add(firstElement);
                alternativeElements.add(this.sLeftElement.clone());
                alternativeElements.add(this.sRightElement.clone());

                altTransformationElement
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                alternativeElements.get(0), firstElement
                                        .getReference().getTransformation()
                                        .getElements().get(0)));
                altTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1), alternativeElements
                                        .get(1)));
                altTransformationElement
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                alternativeElements.get(1), alternativeElements
                                        .get(1)));

                numberProd.addAlternative(newListAlternative(numberProd,
                        alternativeElements, altTransformationElement));

            }

            this.newProduction = numberProd;

        }

        private void atLeastCase() {

            String atLeastName = computeNewAlternatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

            Production atLeastProd = new Production(
                    this.grammar.getNextProductionId(), atLeastName);

            String numberName = computeNewAlternatedProductionName(
                    this.sLeftElement, this.sRightElement,
                    new CardinalityInterval(this.cardinality.getLowerBound(),
                            this.cardinality.getLowerBound()));

            Element.ProductionElement firstElement;

            if (this.grammar.containsProduction(numberName)) {
                firstElement = new Element.ProductionElement("",
                        this.grammar.getProduction(atLeastName));
            }
            else {
                firstElement = new Element.ProductionElement("",
                        newAlternatedProduction(
                                this.parserElement,
                                this.sLeftElement,
                                this.sRightElement,
                                new CardinalityInterval(this.cardinality
                                        .getLowerBound(), this.cardinality
                                        .getLowerBound())));
            }

            String starName = computeNewAlternatedProductionName(
                    this.sLeftElement, this.sRightElement,
                    CardinalityInterval.ZERO_OR_MORE);

            Element.ProductionElement secondElement;

            if (this.grammar.containsProduction(starName)) {
                secondElement = new Element.ProductionElement("",
                        this.grammar.getProduction(starName));
            }
            else {
                secondElement = new Element.ProductionElement("",
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
                            firstElement, firstElement.getReference()
                                    .getTransformation().getElements().get(0)));
            altTransformationElements
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            secondElement, secondElement.getReference()
                                    .getTransformation().getElements().get(0)));

            atLeastProd.addAlternative(newListAlternative(atLeastProd,
                    alternativeElements, altTransformationElements));

            this.newProduction = atLeastProd;

        }

        private void intervalCase() {

            BigInteger lowerBoundValue = this.cardinality.getLowerBound()
                    .getValue();
            BigInteger upperBoundValue = this.cardinality.getUpperBound()
                    .getValue();

            String intervalName = computeNewAlternatedProductionName(
                    this.sLeftElement, this.sRightElement, this.cardinality);

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
                                    altElements.get(0), altElements.get(0)));
                    altTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    altElements.get(1), altElements.get(1)));

                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, altElements,
                            altTransformationElements));
                }
                // (ab)^0..m with m > 1 = (ab)^(1..m) | Empty
                else {
                    Element.ProductionElement firstElement;

                    String smallerIntervalName = computeNewAlternatedProductionName(
                            this.sLeftElement, this.sRightElement,
                            new CardinalityInterval(this.cardinality
                                    .getLowerBound().add(Bound.ONE),
                                    this.cardinality.getUpperBound()));

                    if (this.grammar.containsProduction(smallerIntervalName)) {
                        firstElement = new Element.ProductionElement("",
                                this.grammar.getProduction(smallerIntervalName));
                    }
                    else {

                        firstElement = new Element.ProductionElement("",
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
                                    firstElement, firstElement.getReference()
                                            .getTransformation().getElements()
                                            .get(0)));
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
                                    firstAlternativeElements.get(0),
                                    firstAlternativeElements.get(0)));
                    firstAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    firstAlternativeElements.get(1),
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
                                    secondAlternativeElements.get(0),
                                    secondAlternativeElements.get(0)));
                    secondAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    secondAlternativeElements.get(1),
                                    secondAlternativeElements.get(1)));
                    secondAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    secondAlternativeElements.get(2),
                                    secondAlternativeElements.get(2)));
                    secondAltTransformationElements
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    secondAlternativeElements.get(3),
                                    secondAlternativeElements.get(3)));

                    intervalProd.addAlternative(newListAlternative(
                            intervalProd, secondAlternativeElements,
                            secondAltTransformationElements));

                }
            }
            // (ab)^(m..n) with n > 1 = (ab)^m (ab)^(0..(n-m))
            else {
                String lowerNumberName = computeNewAlternatedProductionName(
                        this.sLeftElement,
                        this.sRightElement,
                        new CardinalityInterval(this.cardinality
                                .getLowerBound(), this.cardinality
                                .getLowerBound()));

                LinkedList<Element> altElements = new LinkedList<Element>();
                LinkedList<SAlternativeTransformationListElement> altTransformationElements = new LinkedList<SAlternativeTransformationListElement>();
                Element.ProductionElement firstElement;

                if (this.grammar.containsProduction(lowerNumberName)) {
                    firstElement = new Element.ProductionElement("",
                            this.grammar.getProduction(lowerNumberName));
                }
                else {
                    firstElement = new Element.ProductionElement("",
                            newAlternatedProduction(
                                    this.parserElement,
                                    this.sLeftElement,
                                    this.sRightElement,
                                    new CardinalityInterval(this.cardinality
                                            .getLowerBound(), this.cardinality
                                            .getLowerBound())));
                }

                Element.ProductionElement secondElement;

                String zeroToIntervalWidthName = computeNewAlternatedProductionName(
                        this.sLeftElement,
                        this.sRightElement,
                        new CardinalityInterval(Bound.ZERO, this.cardinality
                                .getUpperBound().subtract(
                                        this.cardinality.getLowerBound())));

                if (this.grammar.containsProduction(zeroToIntervalWidthName)) {
                    secondElement = new Element.ProductionElement("",
                            this.grammar.getProduction(zeroToIntervalWidthName));
                }
                else {
                    secondElement = new Element.ProductionElement("",
                            newAlternatedProduction(this.parserElement,
                                    this.sLeftElement, this.sRightElement,
                                    new CardinalityInterval(Bound.ZERO,
                                            this.cardinality.getLowerBound())));

                }

                altElements.add(firstElement);
                altElements.add(secondElement);

                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                firstElement, firstElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));
                altTransformationElements
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                secondElement, secondElement.getReference()
                                        .getTransformation().getElements()
                                        .get(0)));
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
                    transformationElements, alternative,
                    new Type.SimpleType.AlternatedType(this.sLeftElement
                            .getTypeName(), this.sRightElement.getTypeName(),
                            this.cardinality)));
            return alternative;
        }

    }

}
