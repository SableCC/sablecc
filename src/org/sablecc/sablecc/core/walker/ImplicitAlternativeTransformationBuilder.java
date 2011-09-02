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

package org.sablecc.sablecc.core.walker;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.Parser.ParserElement.AlternatedElement;
import org.sablecc.sablecc.core.Parser.ParserElement.DanglingElement;
import org.sablecc.sablecc.core.Parser.ParserElement.NormalElement;
import org.sablecc.sablecc.core.Parser.ParserElement.SeparatedElement;
import org.sablecc.sablecc.core.Parser.ParserProduction.DanglingProduction;
import org.sablecc.sablecc.core.Parser.ParserProduction.NormalProduction;
import org.sablecc.sablecc.core.Parser.ParserProduction.TokenProduction;
import org.sablecc.sablecc.core.Tree.TreeProduction;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.core.transformation.ProductionTransformationElement.ExplicitNormalElement;
import org.sablecc.sablecc.core.transformation.ProductionTransformationElement.ImplicitNormalElement;
import org.sablecc.sablecc.syntax3.node.*;

public class ImplicitAlternativeTransformationBuilder
        extends GrammarVisitor {

    private final Grammar grammar;

    private ProductionTransformation productionTransformation;

    private Tree.TreeProduction treeProduction = null;

    private Tree.TreeAlternative treeAlternative = null;

    private Map<Integer, AlternativeTransformationElement> alternativeTransformationElements;

    public ImplicitAlternativeTransformationBuilder(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    @Override
    public void visitParser(
            Parser node) {

        for (Parser.ParserProduction parserProduction : node.getProductions()) {
            parserProduction.apply(this);
        }

    }

    @Override
    public void visitLexer(
            Lexer node) {

        // Do not visit subtree
    }

    @Override
    public void visitTree(
            Tree node) {

        // Do not visit subtree
    }

    @Override
    public void visitTransformation(
            Transformation node) {

        // Do not visit subtree
    }

    @Override
    public void visitParserNormalProduction(
            NormalProduction node) {

        if (node.getTransformation() != null) {
            for (Parser.ParserAlternative alternative : node.getAlternatives()) {
                this.productionTransformation = node.getTransformation();
                alternative.apply(this);
            }
        }
    }

    @Override
    public void visitParserDanglingProduction(
            DanglingProduction node) {

        if (node.getTransformation() != null) {
            for (Parser.ParserAlternative alternative : node.getAlternatives()) {
                this.productionTransformation = node.getTransformation();
                alternative.apply(this);
            }
        }
    }

    @Override
    public void visitParserTokenProduction(
            TokenProduction node) {

        if (node.getTransformation() != null) {
            for (Parser.ParserAlternative alternative : node.getAlternatives()) {
                this.productionTransformation = node.getTransformation();
                alternative.apply(this);
            }
        }
    }

    @Override
    public void visitParserAlternative(
            Parser.ParserAlternative node) {

        if (node.getTransformation() == null) {

            // First : check if the transformation is not too much complicated
            if (!isImplicitlyTransformable(this.productionTransformation)) {
                throw SemanticException

                        .nonTrivialProductionTransformation(
                                (ProductionTransformation.ExplicitProductionTransformation) this.productionTransformation,
                                node.getProduction());
            }
            // Second : deduce the target tree production
            findTargetTreeAlt(node);

            if (this.treeAlternative == null) {
                throw SemanticException.unmatchedAlternative(node,
                        this.treeProduction);
            }

            // Third match the named elements

            this.alternativeTransformationElements = new HashMap<Integer, AlternativeTransformationElement>();

            List<List<Parser.ParserElement>> anonymousConsecutiveElements = matchNamedElement(node);

            // Fourth Create the remainingTreeElement list

            List<Tree.TreeElement> remainingTreeElement = new LinkedList<Tree.TreeElement>();

            for (int i = 0; i < this.treeAlternative.getElements().size(); i++) {
                if (this.alternativeTransformationElements.get(i) == null) {
                    remainingTreeElement.add(this.treeAlternative.getElements()
                            .get(i));
                }
            }

            // Fifth match the anonymous elements

            remainingTreeElement = matchUnamedElement(
                    anonymousConsecutiveElements, remainingTreeElement);

            if (!remainingTreeElement.isEmpty()) {
                throw SemanticException.unmatchedElement(
                        remainingTreeElement.get(0), node);
            }

            List<AlternativeTransformationElement> finalElementList = new LinkedList<AlternativeTransformationElement>();

            for (int i = 0; i < this.treeAlternative.getElements().size(); i++) {
                finalElementList.add(this.alternativeTransformationElements
                        .get(i));
            }

            AlternativeTransformation alternativeTransforamtion = new AlternativeTransformation.ImplicitAlternativeTransformation(
                    node,
                    new AlternativeTransformationElement.ImplicitNewElement(
                            this.treeAlternative, this.grammar,
                            finalElementList), this.grammar);

            node.addTransformation(alternativeTransforamtion);
            this.grammar.getTransformation().addAlternativeTransformation(
                    alternativeTransforamtion);

        }
    }

    private void findTargetTreeAlt(
            Parser.ParserAlternative parserAlternative) {

        IReferencable treeReference = ((ProductionTransformationElement.NormalElement) this.productionTransformation
                .getElements().get(0)).getReference();

        if (treeReference instanceof Tree.TreeProduction) {
            this.treeProduction = (Tree.TreeProduction) treeReference;
        }
        else {
            throw new InternalException("Unexpected class : "
                    + treeReference.getClass());
        }

        if ((parserAlternative.getName() == null || parserAlternative.getName()
                .equals(""))
                && parserAlternative.getIndex() <= this.treeProduction
                        .getAlternatives().size()) {
            this.treeAlternative = this.treeProduction.getAlternatives().get(
                    parserAlternative.getIndex() - 1);
        }
        else {
            this.treeAlternative = this.treeProduction
                    .getLocalReference(parserAlternative.getName());
        }
    }

    private List<List<Parser.ParserElement>> matchNamedElement(
            Parser.ParserAlternative parserAlternative) {

        List<List<Parser.ParserElement>> anonymousConsecutiveElements = new LinkedList<List<Parser.ParserElement>>();
        LinkedList<Parser.ParserElement> anonymousElements = null;
        boolean lastWasNamed = true;

        for (Parser.ParserElement element : parserAlternative.getElements()) {
            if (element.getName() != null) {
                lastWasNamed = true;

                Tree.TreeElement treeElement = this.treeAlternative
                        .getLocalReference(element.getName());

                if (treeElement != null) {
                    int addIndex = this.treeAlternative.getElements().indexOf(
                            treeElement);

                    this.alternativeTransformationElements.put(addIndex,
                            newTransformationElement(element, treeElement));

                }// else the element is considered as missing in the tree

            }
            else {
                if (lastWasNamed) {
                    if (anonymousElements != null) {
                        anonymousConsecutiveElements.add(anonymousElements);
                    }

                    anonymousElements = new LinkedList<Parser.ParserElement>();
                    anonymousElements.add(element);
                }
                else {
                    anonymousElements.add(element);
                }
                lastWasNamed = false;
            }
        }

        if (anonymousElements != null) {
            anonymousConsecutiveElements.add(anonymousElements);
        }

        return anonymousConsecutiveElements;
    }

    private List<Tree.TreeElement> matchUnamedElement(
            List<List<Parser.ParserElement>> anonymousConsecutiveElements,
            List<Tree.TreeElement> remainingTreeElement) {

        LinkedList<Parser.ParserElement> anonymousElements = null;
        int treeElementIndex = 0;
        int elementsListIndex = 0;

        while (treeElementIndex < remainingTreeElement.size()
                && elementsListIndex < anonymousConsecutiveElements.size()) {
            anonymousElements = (LinkedList<Parser.ParserElement>) anonymousConsecutiveElements
                    .get(elementsListIndex);
            int parserElementIndex = 0;
            Parser.ParserElement currentElement;
            Tree.TreeElement previousTreeElement = null;

            while (parserElementIndex < anonymousElements.size()) {

                currentElement = anonymousElements.get(parserElementIndex);

                if (treeElementIndex < remainingTreeElement.size()
                        && new Matcher(this.grammar, currentElement,
                                remainingTreeElement.get(treeElementIndex))
                                .getMatchResult()) {

                    int addIndex = this.treeAlternative.getElements().indexOf(
                            remainingTreeElement.get(treeElementIndex));

                    this.alternativeTransformationElements
                            .put(addIndex,
                                    newTransformationElement(currentElement,
                                            remainingTreeElement
                                                    .get(treeElementIndex)));

                    previousTreeElement = remainingTreeElement
                            .get(treeElementIndex);
                    treeElementIndex++;
                }
                else if (previousTreeElement != null
                        && new Matcher(this.grammar, currentElement,
                                previousTreeElement).getMatchResult()) {

                    if (treeElementIndex == remainingTreeElement.size()) {
                        treeElementIndex--;
                    }

                    throw SemanticException.ambiguousAlternativeTransformation(
                            anonymousElements.get(parserElementIndex - 1),
                            currentElement,
                            remainingTreeElement.get(treeElementIndex));
                }
                parserElementIndex++;
            }
            elementsListIndex++;
        }

        return remainingTreeElement.subList(treeElementIndex,
                remainingTreeElement.size());
    }

    private static boolean isImplicitlyTransformable(
            ProductionTransformation transformation) {

        List<ProductionTransformationElement> transformationElements = transformation
                .getElements();

        if (transformationElements.get(0) instanceof ImplicitNormalElement) {
            return true;
        }

        if (transformationElements.size() != 1) {
            return false;
        }

        if (!(transformationElements.get(0) instanceof ProductionTransformationElement.NormalElement)) {
            return false;
        }

        if (!(transformationElements.get(0) instanceof ProductionTransformationElement.ImplicitNormalElement)) {
            return true;
        }

        ExplicitNormalElement firstElement = (ExplicitNormalElement) transformationElements
                .get(0);

        if (firstElement.getDeclaration().getUnaryOperator() != null) {
            PUnaryOperator operator = firstElement.getDeclaration()
                    .getUnaryOperator();

            if (!(operator instanceof AZeroOrOneUnaryOperator)) {
                return false;
            }
        }

        if (firstElement.getReference() == null) {
            return false;
        }

        if (!(firstElement.getReference() instanceof Tree.TreeProduction)) {
            return false;
        }

        return true;
    }

    public static class Matcher
            extends GrammarVisitor {

        private boolean matchResult = false;

        private final Tree.TreeElement treeElement;

        private final Parser.ParserElement parserElement;

        private final Grammar grammar;

        public Matcher(
                Grammar grammar,
                Parser.ParserElement parserElement,
                Tree.TreeElement treeElement) {

            super();

            this.grammar = grammar;
            this.treeElement = treeElement;
            this.parserElement = parserElement;

            this.parserElement.apply(this);
        }

        public boolean getMatchResult() {

            return this.matchResult;
        }

        @Override
        public void visitParserNormalElement(
                NormalElement node) {

            if (this.treeElement instanceof Tree.TreeElement.NormalElement) {

                if (node.getCardinality().equals(
                        this.treeElement.getCardinality())) {

                    Tree.TreeElement.NormalElement treeNormalElement = (Tree.TreeElement.NormalElement) this.treeElement;

                    if (match(node.getDeclaration().getUnit(),
                            treeNormalElement.getDeclaration().getUnit())) {
                        this.matchResult = true;
                    }

                }
            }
        }

        @Override
        public void visitParserSeparatedElement(
                SeparatedElement node) {

            if (this.treeElement instanceof Tree.TreeElement.SeparatedElement) {
                if (node.getCardinality().equals(
                        this.treeElement.getCardinality())) {

                    Tree.TreeElement.SeparatedElement treeSeparatedElement = (Tree.TreeElement.SeparatedElement) this.treeElement;

                    if (match(node.getDeclaration().getLeft(),
                            treeSeparatedElement.getDeclaration().getLeft())
                            && match(node.getDeclaration().getRight(),
                                    treeSeparatedElement.getDeclaration()
                                            .getRight())) {
                        this.matchResult = true;
                    }
                }
            }
        }

        @Override
        public void visitParserAlternatedELement(
                AlternatedElement node) {

            if (this.treeElement instanceof Tree.TreeElement.AlternatedElement) {
                if (node.getCardinality().equals(
                        this.treeElement.getCardinality())) {

                    Tree.TreeElement.AlternatedElement treeAlternatedElement = (Tree.TreeElement.AlternatedElement) this.treeElement;

                    if (match(node.getDeclaration().getLeft(),
                            treeAlternatedElement.getDeclaration().getLeft())
                            && match(node.getDeclaration().getRight(),
                                    treeAlternatedElement.getDeclaration()
                                            .getRight())) {
                        this.matchResult = true;
                    }
                }
            }
        }

        @Override
        public void visitParserDanglingElement(
                DanglingElement node) {

            if (this.treeElement instanceof Tree.TreeElement.NormalElement) {

                if (node.getCardinality().equals(
                        this.treeElement.getCardinality())) {

                    Tree.TreeElement.NormalElement treeNormalElement = (Tree.TreeElement.NormalElement) this.treeElement;

                    if (treeNormalElement.getDeclaration().getUnit() instanceof ANameUnit) {

                        ANameUnit treeUnit = (ANameUnit) treeNormalElement
                                .getDeclaration().getUnit();

                        if (node.getDeclaration().getIdentifier().getText()
                                .equals(treeUnit.getIdentifier().getText())) {
                            this.matchResult = true;
                        }
                    }

                }
            }
            ;
        }

        private boolean match(
                PUnit parserUnit,
                PUnit treeUnit) {

            boolean result = false;

            if (parserUnit instanceof ANameUnit
                    && treeUnit instanceof ANameUnit) {

                ANameUnit parserNamedUnit = (ANameUnit) parserUnit;
                ANameUnit treeNamedUnit = (ANameUnit) treeUnit;

                // Here handle the transformed reference
                INameDeclaration globalRef = this.grammar
                        .getGlobalReference(parserNamedUnit.getIdentifier()
                                .getText());

                if (globalRef instanceof Parser.ParserProduction) {
                    ProductionTransformation prodTransformation = ((Parser.ParserProduction) globalRef)
                            .getTransformation();

                    if (prodTransformation != null
                            && isImplicitlyTransformable(prodTransformation)) {
                        TreeProduction targetTreeProduction = (TreeProduction) ((ProductionTransformationElement.NormalElement) prodTransformation
                                .getElements().get(0)).getReference();

                        if (targetTreeProduction.getName().equals(
                                treeNamedUnit.getIdentifier().getText())) {
                            result = true;
                        }
                    }
                    else {
                        if (parserNamedUnit
                                .getIdentifier()
                                .getText()
                                .equals(treeNamedUnit.getIdentifier().getText())) {
                            result = true;
                        }
                    }
                }
                else {
                    if (parserNamedUnit.getIdentifier().getText()
                            .equals(treeNamedUnit.getIdentifier().getText())) {
                        result = true;
                    }
                }

            }
            else if (parserUnit instanceof AStringUnit
                    && treeUnit instanceof AStringUnit) {

                AStringUnit parserStringUnit = (AStringUnit) parserUnit;
                AStringUnit treeStringUnit = (AStringUnit) treeUnit;

                if (parserStringUnit.getString().getText()
                        .equals(treeStringUnit.getString().getText())) {
                    result = true;
                }
            }
            else if (parserUnit instanceof ACharacterUnit
                    && treeUnit instanceof ACharacterUnit) {

                PCharacter parserChar = ((ACharacterUnit) parserUnit)
                        .getCharacter();
                PCharacter treeChar = ((ACharacterUnit) treeUnit)
                        .getCharacter();

                if (parserChar instanceof ACharCharacter
                        && treeChar instanceof ACharCharacter) {

                    if (((ACharCharacter) parserChar)
                            .getChar()
                            .getText()
                            .equals(((ACharCharacter) treeChar).getChar()
                                    .getText())) {
                        result = true;
                    }
                }
                else if (parserChar instanceof ADecCharacter
                        && treeChar instanceof ADecCharacter) {

                    if (((ADecCharacter) parserChar)
                            .getDecChar()
                            .getText()
                            .equals(((ADecCharacter) treeChar).getDecChar()
                                    .getText())) {
                        result = true;
                    }
                }
                else if (parserChar instanceof AHexCharacter
                        && treeChar instanceof AHexCharacter) {

                    if (((AHexCharacter) parserChar)
                            .getHexChar()
                            .getText()
                            .equals(((AHexCharacter) treeChar).getHexChar()
                                    .getText())) {
                        result = true;
                    }
                }

            }
            else if (parserUnit instanceof AStartUnit
                    && treeUnit instanceof AStartUnit
                    || parserUnit instanceof AEndUnit
                    && treeUnit instanceof AEndUnit) {

                result = true;
            }

            return result;

        }
    }

    private AlternativeTransformationElement newTransformationElement(
            Parser.ParserElement parserElement,
            Tree.TreeElement treeElement) {

        if (getOperator(parserElement) == null
                || getOperator(parserElement) instanceof AZeroOrOneUnaryOperator) {
            return new AlternativeTransformationElement.ImplicitReferenceElement(
                    parserElement, this.grammar);
        }
        else {
            List<AlternativeTransformationListElement> listElements = new LinkedList<AlternativeTransformationListElement>();

            if (parserElement instanceof Parser.ParserElement.AlternatedElement
                    || parserElement instanceof Parser.ParserElement.SeparatedElement) {
                listElements
                        .add(new AlternativeTransformationListElement.ImplicitLeftListElement(
                                parserElement, this.grammar));
                listElements
                        .add(new AlternativeTransformationListElement.ImplicitRightListElement(
                                parserElement, this.grammar));

            }
            else {
                listElements
                        .add(new AlternativeTransformationListElement.ImplicitNormalListElement(
                                parserElement, this.grammar));
            }

            return new AlternativeTransformationElement.ImplicitListElement(
                    this.grammar, listElements);
        }
    }

    private Node getOperator(
            Parser.ParserElement element) {

        if (element instanceof Parser.ParserElement.NormalElement) {
            return ((Parser.ParserElement.NormalElement) element)
                    .getDeclaration().getUnaryOperator();
        }
        else if (element instanceof Parser.ParserElement.SeparatedElement) {
            return ((Parser.ParserElement.SeparatedElement) element)
                    .getDeclaration().getManyOperator();
        }
        else if (element instanceof Parser.ParserElement.AlternatedElement) {
            return ((Parser.ParserElement.AlternatedElement) element)
                    .getDeclaration().getManyOperator();
        }

        return null;
    }
}
