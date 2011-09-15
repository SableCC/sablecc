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
import org.sablecc.sablecc.core.Parser.ParserProduction.TokenProduction;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public abstract class ReferenceVerifier
        extends GrammarVisitor {

    public static class LexerReferenceVerifier
            extends ReferenceVerifier {

        private final Grammar grammar;

        public LexerReferenceVerifier(
                Grammar grammar) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
        }

        @Override
        public void visitNameUnitExpression(
                Expression.NameUnit node) {

            TIdentifier referenceIdentifier = node.getDeclaration()
                    .getIdentifier();

            INameDeclaration declaration = findGlobalDeclaration(this.grammar,
                    referenceIdentifier);

            if (!(declaration instanceof LexerExpression)) {
                String[] expectedNames = { "Expression" };
                throw SemanticException.badReference(referenceIdentifier,
                        declaration.getNameType(), expectedNames);
            }

        }

        @Override
        public void visitLexerPriority(
                Lexer.LexerPriority node) {

            PUnit highUnit = node.getDeclaration().getHigh();

            if (highUnit instanceof ANameUnit) {
                tokenExpected(this.grammar,
                        ((ANameUnit) highUnit).getIdentifier());
            }

            PUnit lowUnit = node.getDeclaration().getLow();

            if (highUnit instanceof ANameUnit) {
                tokenExpected(this.grammar,
                        ((ANameUnit) lowUnit).getIdentifier());
            }

        }

        @Override
        public void visitLexerInvestigator(
                Investigator.LexerInvestigator node) {

            tokenExpected(this.grammar, node.getDeclaration().getParameter());

        }

        @Override
        public void visitLexerSelector(
                Selector.LexerSelector node) {

            tokenExpected(this.grammar, node.getDeclaration().getParameter());
        }

        @Override
        public void visitNamedContext(
                Context.NamedContext node) {

            if (node.getTokensDeclaration() != null) {
                for (PUnit unit : node.getTokensDeclaration().getUnit()) {
                    if (unit instanceof ANameUnit) {

                        tokenExpected(this.grammar,
                                ((ANameUnit) unit).getIdentifier());
                    }
                }
            }

            if (node.getIgnoredeDeclaration() != null) {
                for (PUnit unit : node.getIgnoredeDeclaration().getUnit()) {
                    if (unit instanceof ANameUnit) {
                        tokenExpected(this.grammar,
                                ((ANameUnit) unit).getIdentifier());
                    }
                }
            }

        }

        @Override
        public void visitAnonymousContext(
                Context.AnonymousContext node) {

            if (node.getTokensDeclaration() != null) {
                for (PUnit unit : node.getTokensDeclaration().getUnit()) {
                    if (unit instanceof ANameUnit) {
                        tokenExpected(this.grammar,
                                ((ANameUnit) unit).getIdentifier());
                    }
                }
            }

            if (node.getIgnoredeDeclaration() != null) {
                for (PUnit unit : node.getIgnoredeDeclaration().getUnit()) {
                    if (unit instanceof ANameUnit) {
                        tokenExpected(this.grammar,
                                ((ANameUnit) unit).getIdentifier());
                    }
                }
            }
        }
    }

    public static class ParserReferenceVerifier
            extends ReferenceVerifier {

        private final Grammar grammar;

        public ParserReferenceVerifier(
                Grammar grammar) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
        }

        @Override
        public void visitParser(
                Parser node) {

            if (node.getRootDeclaration() != null) {
                for (TIdentifier identifier : node.getRootDeclaration()
                        .getIdentifiers()) {
                    INameDeclaration declaration = findGlobalDeclaration(
                            this.grammar, identifier);

                    if (declaration instanceof Parser.ParserProduction.DanglingProduction
                            || declaration instanceof Parser.ParserProduction.TokenProduction) {
                        String[] expectedNames = { "normal parser production" };

                        throw SemanticException.badReference(identifier,
                                declaration.getNameType(), expectedNames);
                    }
                    else if (!(declaration instanceof Selector.ParserSelector.Selection)
                            && !(declaration instanceof Parser.ParserProduction.NormalProduction)) {

                        String[] expectedNames = { "parser production" };
                        throw SemanticException.badReference(identifier,
                                declaration.getNameType(), expectedNames);
                    }
                }
            }
            else {
                Parser.ParserProduction firstProduction = node.getProductions()
                        .get(0);

                if (firstProduction instanceof Parser.ParserProduction.DanglingProduction
                        || firstProduction instanceof Parser.ParserProduction.TokenProduction) {
                    String[] expectedNames = { "normal parser production" };

                    throw SemanticException.badReference(
                            firstProduction.getNameIdentifier(),
                            firstProduction.getNameType(), expectedNames);
                }
                else if (!(firstProduction instanceof Parser.ParserProduction.NormalProduction)) {

                    String[] expectedNames = { "parser production" };
                    throw SemanticException.badReference(
                            firstProduction.getNameIdentifier(),
                            firstProduction.getNameType(), expectedNames);
                }
            }

            super.visitParser(node);

        }

        @Override
        public void visitParserNormalElement(
                Parser.ParserElement.NormalElement node) {

            PUnit unit = node.getDeclaration().getUnit();

            if (unit instanceof ANameUnit) {
                IReferencable reference = tokenOrParserProductionExpected(
                        this.grammar, ((ANameUnit) unit).getIdentifier());
                node.addReference(reference);
            }

        }

        @Override
        public void visitParserSeparatedElement(
                Parser.ParserElement.SeparatedElement node) {

            PUnit leftUnit = node.getDeclaration().getLeft();

            if (leftUnit instanceof ANameUnit) {

                IReferencable reference = tokenOrParserProductionExpected(
                        this.grammar, ((ANameUnit) leftUnit).getIdentifier());
                node.addLeftReference(reference);
            }

            PUnit rightUnit = node.getDeclaration().getRight();

            if (rightUnit instanceof ANameUnit) {

                IReferencable reference = tokenOrParserProductionExpected(
                        this.grammar, ((ANameUnit) rightUnit).getIdentifier());
                node.addRightReference(reference);
            }

        }

        @Override
        public void visitParserAlternatedELement(
                Parser.ParserElement.AlternatedElement node) {

            PUnit leftUnit = node.getDeclaration().getLeft();

            if (leftUnit instanceof ANameUnit) {

                IReferencable reference = tokenOrParserProductionExpected(
                        this.grammar, ((ANameUnit) leftUnit).getIdentifier());
                node.addLeftReference(reference);
            }

            PUnit rightUnit = node.getDeclaration().getRight();

            if (rightUnit instanceof ANameUnit) {

                IReferencable reference = tokenOrParserProductionExpected(
                        this.grammar, ((ANameUnit) rightUnit).getIdentifier());
                node.addRightReference(reference);
            }

        }

        @Override
        public void visitParserDanglingElement(
                Parser.ParserElement.DanglingElement node) {

            TIdentifier elementIdentifier = node.getDeclaration()
                    .getIdentifier();

            INameDeclaration declaration = findGlobalDeclaration(this.grammar,
                    elementIdentifier);

            if (!(declaration instanceof Parser.ParserProduction.DanglingProduction)) {
                String[] expectedNames = { "dangling parser production" };
                throw SemanticException.badReference(elementIdentifier,
                        declaration.getNameType(), expectedNames);
            }

            node.addReference((Parser.ParserProduction.DanglingProduction) declaration);

        }

        @Override
        public void visitLeftParserPriority(
                Parser.ParserPriority.LeftPriority node) {

            for (TIdentifier reference : node.getDeclaration().getIdentifiers()) {
                Parser.ParserAlternative declaration = node.getProduction()
                        .getLocalReference(reference.getText());

                if (declaration == null) {
                    throw SemanticException.undefinedReference(reference);
                }
            }
        }

        @Override
        public void visitRightParserPriority(
                Parser.ParserPriority.RightPriority node) {

            for (TIdentifier reference : node.getDeclaration().getIdentifiers()) {
                Parser.ParserAlternative declaration = node.getProduction()
                        .getLocalReference(reference.getText());

                if (declaration == null) {
                    throw SemanticException.undefinedReference(reference);
                }
            }
        }

        @Override
        public void visitUnaryParserPriority(
                Parser.ParserPriority.UnaryPriority node) {

            for (TIdentifier reference : node.getDeclaration().getIdentifiers()) {
                Parser.ParserAlternative declaration = node.getProduction()
                        .getLocalReference(reference.getText());

                if (declaration == null) {
                    throw SemanticException.undefinedReference(reference);
                }
            }
        }

        @Override
        public void visitParserInvestigator(
                Investigator.ParserInvestigator node) {

            selectorOrParserProductionExpected(this.grammar, node
                    .getDeclaration().getParameter());
        }

        @Override
        public void visitParserSelector(
                Selector.ParserSelector node) {

            selectorOrParserProductionExpected(this.grammar, node
                    .getDeclaration().getParameter());

        }
    }

    public static class RootVerifier
            extends ReferenceVerifier {

        private final Grammar grammar;

        public RootVerifier(
                Grammar grammar) {

            this.grammar = grammar;
        }

        @Override
        public void visitParser(
                Parser node) {

            if (node.getRootDeclaration() != null) {
                for (TIdentifier identifier : node.getRootDeclaration()
                        .getIdentifiers()) {
                    INameDeclaration declaration = findGlobalDeclaration(
                            this.grammar, identifier);

                    Parser.ParserProduction rootProd = (Parser.ParserProduction) declaration;

                    if (rootProd.getTransformation() != null) {
                        if (rootProd.getTransformation().getElements().size() != 1
                                || !rootProd.getTransformation().getElements()
                                        .get(0).getCardinality()
                                        .equals(CardinalityInterval.ONE_ONE)) {

                            throw SemanticException
                                    .badRootElementTransformation(rootProd);

                        }
                    }
                    else {
                        if (!(findTreeDeclaration(this.grammar, identifier) instanceof Tree.TreeProduction)) {
                            throw SemanticException
                                    .badRootElementTransformation(rootProd);
                        }
                    }
                }
            }
            else {
                Parser.ParserProduction firstProduction = node.getProductions()
                        .get(0);

                if (firstProduction.getTransformation() != null) {
                    if (firstProduction.getTransformation().getElements()
                            .size() != 1
                            || !firstProduction.getTransformation()
                                    .getElements().get(0).getCardinality()
                                    .equals(CardinalityInterval.ONE_ONE)) {

                        throw SemanticException
                                .badRootElementTransformation(firstProduction);

                    }
                }
                else {
                    if (!(findTreeDeclaration(this.grammar,
                            firstProduction.getNameIdentifier()) instanceof Tree.TreeProduction)) {
                        throw SemanticException
                                .badRootElementTransformation(firstProduction);
                    }
                }

            }

            super.visitParser(node);
        }

        @Override
        public void visitParserTokenProduction(
                TokenProduction node) {

            if (node.getTransformation() != null) {
                if (node.getTransformation().getElements().size() != 1
                        || !node.getTransformation().getElements().get(0)
                                .getCardinality()
                                .equals(CardinalityInterval.ONE_ONE)) {

                    throw SemanticException
                            .badSyntacticTokenTransformation(node);
                }
            }
            else {
                if (!(findTreeDeclaration(this.grammar,
                        node.getNameIdentifier()) instanceof Tree.TreeProduction)) {
                    throw SemanticException
                            .badSyntacticTokenTransformation(node);
                }
            }
        }
    }

    public static class TransformationReferenceVerifier
            extends ReferenceVerifier {

        private final Grammar grammar;

        private Parser.ParserAlternative currentAlternative = null;

        private Map<String, AlternativeTransformationListElement> leftOrRightListElements = new HashMap<String, AlternativeTransformationListElement>();

        public TransformationReferenceVerifier(
                Grammar grammar) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
        }

        @Override
        public void visitProductionTransformation(
                ProductionTransformation node) {

            if (node instanceof ProductionTransformation.ExplicitProductionTransformation) {
                ProductionTransformation.ExplicitProductionTransformation transformation = (ProductionTransformation.ExplicitProductionTransformation) node;

                TIdentifier targetProductionIdentifier = transformation
                        .getDeclaration().getProduction();

                INameDeclaration targetProduction = this.grammar
                        .getGlobalReference(targetProductionIdentifier
                                .getText());

                if (targetProduction == null) {
                    throw SemanticException
                            .undefinedReference(targetProductionIdentifier);
                }

                if (!(targetProduction instanceof Parser.ParserProduction || targetProduction instanceof Selector.ParserSelector.Selection)) {
                    String[] expectedNames = { "parser production",
                            "parser selection" };
                    throw SemanticException.badReference(
                            targetProductionIdentifier,
                            targetProduction.getNameType(), expectedNames);
                }

                // TODO Handle selector case

                ((Parser.ParserProduction) targetProduction)
                        .addTransformation(node);

                transformation
                        .addReference((Parser.ParserProduction) targetProduction);

                super.visitProductionTransformation(node);
            }
        }

        @Override
        public void visitProductionTransformationNormalElement(
                ProductionTransformationElement.NormalElement node) {

            if (node instanceof ProductionTransformationElement.ExplicitNormalElement) {
                ProductionTransformationElement.ExplicitNormalElement transforamtionElement = (ProductionTransformationElement.ExplicitNormalElement) node;

                PUnit unit = transforamtionElement.getDeclaration().getUnit();

                if (unit instanceof ANameUnit) {
                    TIdentifier identifier = ((ANameUnit) unit).getIdentifier();

                    INameDeclaration declaration = findTreeDeclaration(
                            this.grammar, identifier);

                    if (!(declaration instanceof Tree.TreeProduction)
                            && !(declaration instanceof IToken)) {
                        String[] expectedNames = { "tree production", "token" };
                        throw SemanticException.badReference(identifier,
                                declaration.getNameType(), expectedNames);

                    }

                    transforamtionElement
                            .addReference((IReferencable) declaration);
                }
            }

        }

        @Override
        public void visitProductionTransformationAlternatedElement(
                ProductionTransformationElement.AlternatedElement node) {

            PUnit leftUnit = node.getDeclaration().getLeft();

            if (leftUnit instanceof ANameUnit) {
                TIdentifier identifier = ((ANameUnit) leftUnit).getIdentifier();

                INameDeclaration declaration = findTreeDeclaration(
                        this.grammar, identifier);

                if (!(declaration instanceof Tree.TreeProduction)
                        && !(declaration instanceof IToken)) {
                    String[] expectedNames = { "tree production", "token" };
                    throw SemanticException.badReference(identifier,
                            declaration.getNameType(), expectedNames);

                }

                node.addLeftReference((IReferencable) declaration);
            }

            PUnit rightUnit = node.getDeclaration().getRight();
            if (rightUnit instanceof ANameUnit) {
                TIdentifier identifier = ((ANameUnit) rightUnit)
                        .getIdentifier();

                INameDeclaration declaration = findTreeDeclaration(
                        this.grammar, identifier);

                if (!(declaration instanceof Tree.TreeProduction)
                        && !(declaration instanceof IToken)) {
                    String[] expectedNames = { "tree production", "token" };
                    throw SemanticException.badReference(identifier,
                            declaration.getNameType(), expectedNames);

                }

                node.addRightReference((IReferencable) declaration);
            }
        }

        @Override
        public void visitProductionTransformationSeparatedElement(
                ProductionTransformationElement.SeparatedElement node) {

            PUnit leftUnit = node.getDeclaration().getLeft();

            if (leftUnit instanceof ANameUnit) {
                TIdentifier identifier = ((ANameUnit) leftUnit).getIdentifier();

                INameDeclaration declaration = findTreeDeclaration(
                        this.grammar, identifier);

                if (!(declaration instanceof Tree.TreeProduction)
                        && !(declaration instanceof IToken)) {
                    String[] expectedNames = { "tree production", "token" };
                    throw SemanticException.badReference(identifier,
                            declaration.getNameType(), expectedNames);

                }

                node.addLeftReference((IReferencable) declaration);
            }

            PUnit rightUnit = node.getDeclaration().getRight();
            if (rightUnit instanceof ANameUnit) {
                TIdentifier identifier = ((ANameUnit) rightUnit)
                        .getIdentifier();

                INameDeclaration declaration = findTreeDeclaration(
                        this.grammar, identifier);

                if (!(declaration instanceof Tree.TreeProduction)
                        && !(declaration instanceof IToken)) {
                    String[] expectedNames = { "tree production", "token" };
                    throw SemanticException.badReference(identifier,
                            declaration.getNameType(), expectedNames);

                }

                node.addRightReference((IReferencable) declaration);
            }
        }

        @Override
        public void visitAlternativeTransformation(
                AlternativeTransformation node) {

            this.leftOrRightListElements.clear();

            if (node instanceof AlternativeTransformation.ExplicitAlternativeTransformation) {
                AlternativeTransformation.ExplicitAlternativeTransformation transformation = (AlternativeTransformation.ExplicitAlternativeTransformation) node;

                this.currentAlternative = findParserAlternative(transformation
                        .getDeclaration().getAlternativeReference());
                this.currentAlternative.addTransformation(node);
                transformation.addAlternativeReference(this.currentAlternative);
            }

            super.visitAlternativeTransformation(node);
        }

        @Override
        public void visitAlternativeTransformationReferenceElement(
                AlternativeTransformationElement.ReferenceElement node) {

            if (node instanceof AlternativeTransformationElement.ExplicitReferenceElement) {
                AlternativeTransformationElement.ExplicitReferenceElement transformationElement = (AlternativeTransformationElement.ExplicitReferenceElement) node;

                PElementReference elementReference = transformationElement
                        .getDeclaration().getElementReference();

                IReferencable reference = resolveAlternativeElementReference(elementReference);

                transformationElement.addReference(reference);
            }

        }

        @Override
        public void visitAlternativeTransformationNewElement(
                AlternativeTransformationElement.NewElement node) {

            if (node instanceof AlternativeTransformationElement.ExplicitNewElement) {
                AlternativeTransformationElement.ExplicitNewElement transformationElement = (AlternativeTransformationElement.ExplicitNewElement) node;

                Tree.TreeAlternative alternativeReference = findTreeAlternative(transformationElement
                        .getDeclaration().getAlternativeReference());

                transformationElement.addReference(alternativeReference);
            }

            super.visitAlternativeTransformationNewElement(node);
        }

        @Override
        public void visitAlternativeTransformationListElement(
                AlternativeTransformationElement.ListElement node) {

            super.visitAlternativeTransformationListElement(node);
        }

        @Override
        public void visitAlternativeTransformationNewListElement(
                AlternativeTransformationListElement.NewElement node) {

            if (node instanceof AlternativeTransformationListElement.ExplicitNewElement) {
                AlternativeTransformationListElement.ExplicitNewElement transformationElement = (AlternativeTransformationListElement.ExplicitNewElement) node;

                Tree.TreeAlternative alternativeReference = findTreeAlternative(transformationElement
                        .getDeclaration().getAlternativeReference());

                transformationElement.addReference(alternativeReference);
            }

            super.visitAlternativeTransformationNewListElement(node);
        }

        @Override
        public void visitAlternativeTransformationReferenceListElement(
                AlternativeTransformationListElement.ReferenceElement node) {

            if (node instanceof AlternativeTransformationListElement.ExplicitReferenceElement) {
                AlternativeTransformationListElement.ExplicitReferenceElement listElement = (AlternativeTransformationListElement.ExplicitReferenceElement) node;

                PElementReference elementReference = listElement
                        .getDeclaration().getElementReference();

                IReferencable reference = resolveAlternativeElementReference(elementReference);

                if (reference instanceof Parser.ParserElement) {
                    if (((Parser.ParserElement) reference).getCardinality()
                            .getUpperBound().compareTo(Bound.ONE) > 0) {
                        throw SemanticException.listExpansionMissing(node);
                    }
                }
                else {
                    if (((ProductionTransformationElement) reference)
                            .getCardinality().getUpperBound()
                            .compareTo(Bound.ONE) > 0) {
                        throw SemanticException.listExpansionMissing(node);
                    }
                }
                listElement.addReference(reference);
            }

        }

        @Override
        public void visitAlternativeTransformationNormalListReferenceListElement(
                AlternativeTransformationListElement.NormalListElement node) {

            if (node instanceof AlternativeTransformationListElement.ExplicitNormalListElement) {
                AlternativeTransformationListElement.ExplicitNormalListElement listElement = (AlternativeTransformationListElement.ExplicitNormalListElement) node;

                PElementReference elementReference = listElement
                        .getDeclaration().getElementReference();

                IReferencable reference = resolveAlternativeElementReference(elementReference);

                listElement.addReference(reference);

                String name = listElement.getElement().substring(0,
                        listElement.getElement().length() - 4);

                if (this.leftOrRightListElements.containsKey(name)) {
                    throw SemanticException.multipleListExpansion(node,
                            this.leftOrRightListElements.get(name));
                }

                this.leftOrRightListElements.put(name, node);
            }

        }

        @Override
        public void visitAlternativeTransformationLeftListReferenceListElement(
                AlternativeTransformationListElement.LeftListElement node) {

            if (node instanceof AlternativeTransformationListElement.ExplicitLeftListElement) {
                AlternativeTransformationListElement.ExplicitLeftListElement listElement = (AlternativeTransformationListElement.ExplicitLeftListElement) node;

                PElementReference elementReference = listElement
                        .getDeclaration().getElementReference();

                IReferencable reference = resolveAlternativeElementReference(elementReference);

                listElement.addReference(reference);

                String name = listElement.getElement().substring(0,
                        listElement.getElement().length() - 8);

                if (this.leftOrRightListElements.containsKey(name)) {
                    throw SemanticException.multipleListExpansion(node,
                            this.leftOrRightListElements.get(name));
                }

                this.leftOrRightListElements.put(name, node);
            }

        }

        @Override
        public void visitAlternativeTransformationRightListReferenceListElement(
                AlternativeTransformationListElement.RightListElement node) {

            if (node instanceof AlternativeTransformationListElement.ExplicitRightListElement) {
                AlternativeTransformationListElement.ExplicitRightListElement listElement = (AlternativeTransformationListElement.ExplicitRightListElement) node;

                PElementReference elementReference = listElement
                        .getDeclaration().getElementReference();

                IReferencable reference = resolveAlternativeElementReference(elementReference);

                listElement.addReference(reference);

                String name = listElement.getElement().substring(0,
                        listElement.getElement().length() - 9);

                if (this.leftOrRightListElements.containsKey(name)) {
                    throw SemanticException.multipleListExpansion(node,
                            this.leftOrRightListElements.get(name));
                }

                this.leftOrRightListElements.put(name, node);
            }

        }

        private IReferencable resolveAlternativeElementReference(
                PElementReference elementReference) {

            IReferencable reference;

            if (elementReference instanceof ATransformedElementReference) {

                TIdentifier typeIdentifier = ((ATransformedElementReference) elementReference)
                        .getElement();

                TIdentifier transformationElementIdentifier = ((ATransformedElementReference) elementReference)
                        .getPart();

                ProductionTransformation.ExplicitProductionTransformation productionTransformation = findProductionTransformation(
                        typeIdentifier, transformationElementIdentifier);

                ProductionTransformationElement transformationElement = productionTransformation
                        .getLocalReference(transformationElementIdentifier
                                .getText());

                if (transformationElement == null) {
                    throw SemanticException
                            .undefinedAlternativeTransformationReference(
                                    transformationElementIdentifier,
                                    productionTransformation);
                }

                reference = transformationElement;

            }
            else {

                reference = findElement(
                        ((ANaturalElementReference) elementReference)
                                .getElement(),
                        this.currentAlternative);

                // Normal ref should only be used with non transformed prof

                INameDeclaration declaration = this.grammar
                        .getGlobalReference(((ANaturalElementReference) elementReference)
                                .getElement().getText());

                if (declaration instanceof Parser.ParserProduction) {

                    if (((Parser.ParserProduction) declaration)
                            .getTransformation() != null) {

                        throw SemanticException.impossibleNaturalReference(
                                (ANaturalElementReference) elementReference,
                                ((Parser.ParserProduction) declaration)
                                        .getTransformation());
                    }
                }

            }

            return reference;
        }

        private ProductionTransformation.ExplicitProductionTransformation findProductionTransformation(
                TIdentifier typeIdentifier,
                TIdentifier transformationElementIdentifier) {

            Parser.ParserElement element = findElement(typeIdentifier,
                    this.currentAlternative);

            if (!(element instanceof Parser.ParserElement.NormalElement)) {
                String[] expectedNames = { "normal parser element" };
                throw SemanticException.badReference(typeIdentifier,
                        element.getNameType(), expectedNames);
            }

            PUnit unit = ((Parser.ParserElement.NormalElement) element)
                    .getDeclaration().getUnit();

            if (!(unit instanceof ANameUnit)) {
                throw SemanticException.badProductionReference(typeIdentifier,
                        this.currentAlternative.getProduction());
            }

            Parser.ParserProduction production = (Parser.ParserProduction) this.grammar
                    .getGlobalReference(((ANameUnit) unit).getIdentifier()
                            .getText());

            ProductionTransformation.ExplicitProductionTransformation productionTransformation = (ProductionTransformation.ExplicitProductionTransformation) production
                    .getTransformation();

            if (productionTransformation == null) {
                throw SemanticException.badAlternativeTransformationReference(
                        typeIdentifier, transformationElementIdentifier,
                        production);
            }

            return productionTransformation;
        }

        private Parser.ParserAlternative findParserAlternative(
                PAlternativeReference alternativeReferenceDeclaration) {

            Parser.ParserAlternative alternativeReference;

            if (alternativeReferenceDeclaration instanceof ANamedAlternativeReference) {

                INameDeclaration productionDeclaration = findGlobalDeclaration(
                        this.grammar,
                        ((ANamedAlternativeReference) alternativeReferenceDeclaration)
                                .getProduction());

                if (!(productionDeclaration instanceof Parser.ParserProduction || productionDeclaration instanceof Selector.ParserSelector.Selection)) {

                    String[] expectedNames = { "parser production" };
                    throw SemanticException
                            .badReference(
                                    ((ANamedAlternativeReference) alternativeReferenceDeclaration)
                                            .getProduction(),
                                    productionDeclaration.getNameType(),
                                    expectedNames);

                }

                // TODO Handle selector case

                alternativeReference = findAlternative(
                        ((ANamedAlternativeReference) alternativeReferenceDeclaration)
                                .getAlternative(),
                        (Parser.ParserProduction) productionDeclaration);

            }
            else { // UnamedAlternativeReference

                INameDeclaration productionDeclaration = findGlobalDeclaration(
                        this.grammar,
                        ((AUnnamedAlternativeReference) alternativeReferenceDeclaration)
                                .getProduction());

                if (!(productionDeclaration instanceof Parser.ParserProduction || productionDeclaration instanceof Selector.ParserSelector.Selection)) {

                    String[] expectedNames = { "parser production" };
                    throw SemanticException
                            .badReference(
                                    ((AUnnamedAlternativeReference) alternativeReferenceDeclaration)
                                            .getProduction(),
                                    productionDeclaration.getNameType(),
                                    expectedNames);
                }

                // TODO Handle selector case
                Parser.ParserProduction parserProduction = (Parser.ParserProduction) productionDeclaration;

                alternativeReference = parserProduction.getAlternatives()
                        .get(0);

                if (parserProduction.getAlternatives().size() != 1
                        || alternativeReference.getDeclaration()
                                .getAlternativeName() != null) {

                    throw SemanticException
                            .badAlternativeReference(
                                    ((AUnnamedAlternativeReference) alternativeReferenceDeclaration)
                                            .getProduction(), parserProduction);
                }
            }

            return alternativeReference;
        }

        private Tree.TreeAlternative findTreeAlternative(
                PAlternativeReference alternativeReferenceDeclaration) {

            Tree.TreeAlternative alternativeReference;

            if (alternativeReferenceDeclaration instanceof ANamedAlternativeReference) {

                INameDeclaration productionDeclaration = findTreeDeclaration(
                        this.grammar,
                        ((ANamedAlternativeReference) alternativeReferenceDeclaration)
                                .getProduction());

                if (!(productionDeclaration instanceof Tree.TreeProduction)) {

                    String[] expectedNames = { "tree alternative" };
                    throw SemanticException
                            .badReference(
                                    ((ANamedAlternativeReference) alternativeReferenceDeclaration)
                                            .getProduction(),
                                    productionDeclaration.getNameType(),
                                    expectedNames);
                }

                alternativeReference = findAlternative(
                        ((ANamedAlternativeReference) alternativeReferenceDeclaration)
                                .getAlternative(),
                        (Tree.TreeProduction) productionDeclaration);

            }
            else { // UnamedAlternativeReference

                INameDeclaration productionDeclaration = findTreeDeclaration(
                        this.grammar,
                        ((AUnnamedAlternativeReference) alternativeReferenceDeclaration)
                                .getProduction());

                if (!(productionDeclaration instanceof Tree.TreeProduction)) {

                    String[] expectedNames = { "tree alternative" };
                    throw SemanticException.badReference(
                            productionDeclaration.getNameIdentifier(),
                            productionDeclaration.getNameType(), expectedNames);
                }

                // TODO Handle selector case
                Tree.TreeProduction treeProduction = (Tree.TreeProduction) productionDeclaration;

                alternativeReference = treeProduction.getAlternatives().get(0);

                if (treeProduction.getAlternatives().size() != 1
                        || alternativeReference.getDeclaration()
                                .getAlternativeName() != null) {

                    throw SemanticException
                            .badTreeAlternativeReference(
                                    ((AUnnamedAlternativeReference) alternativeReferenceDeclaration)
                                            .getProduction(), treeProduction);
                }
            }

            return alternativeReference;
        }
    }

    public static class TreeReferenceVerifier
            extends ReferenceVerifier {

        private final Grammar grammar;

        public TreeReferenceVerifier(
                Grammar grammar) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
        }

        @Override
        public void visitTreeNormalElement(
                Tree.TreeElement.NormalElement node) {

            PUnit unit = node.getDeclaration().getUnit();

            if (unit instanceof ANameUnit) {
                IReferencable reference = tokenOrTreeProductionExpected(
                        this.grammar, ((ANameUnit) unit).getIdentifier());
                node.addReference(reference);
            }

        }

        @Override
        public void visitTreeSeparatedElement(
                Tree.TreeElement.SeparatedElement node) {

            PUnit leftUnit = node.getDeclaration().getLeft();

            if (leftUnit instanceof ANameUnit) {
                IReferencable reference = tokenOrTreeProductionExpected(
                        this.grammar, ((ANameUnit) leftUnit).getIdentifier());
                node.addLeftReference(reference);
            }

            PUnit rightUnit = node.getDeclaration().getRight();
            if (rightUnit instanceof ANameUnit) {
                IReferencable reference = tokenOrTreeProductionExpected(
                        this.grammar, ((ANameUnit) rightUnit).getIdentifier());
                node.addRightReference(reference);
            }
        }

        @Override
        public void visitTreeAlternatedElement(
                Tree.TreeElement.AlternatedElement node) {

            PUnit leftUnit = node.getDeclaration().getLeft();

            if (leftUnit instanceof ANameUnit) {
                IReferencable reference = tokenOrTreeProductionExpected(
                        this.grammar, ((ANameUnit) leftUnit).getIdentifier());
                node.addLeftReference(reference);
            }

            PUnit rightUnit = node.getDeclaration().getRight();
            if (rightUnit instanceof ANameUnit) {
                IReferencable reference = tokenOrTreeProductionExpected(
                        this.grammar, ((ANameUnit) rightUnit).getIdentifier());
                node.addRightReference(reference);
            }
        }
    }

    private static INameDeclaration findGlobalDeclaration(
            Grammar grammar,
            TIdentifier identifier) {

        INameDeclaration declaration = grammar.getGlobalReference(identifier
                .getText());

        if (declaration == null) {
            throw SemanticException.undefinedReference(identifier);
        }
        return declaration;
    }

    private static INameDeclaration findTreeDeclaration(
            Grammar grammar,
            TIdentifier identifier) {

        INameDeclaration declaration = grammar.getTreeReference(identifier
                .getText());

        if (declaration == null) {
            throw SemanticException.undefinedReference(identifier);
        }
        return declaration;
    }

    private static Parser.ParserAlternative findAlternative(
            TIdentifier alternativeIdentifier,
            Parser.ParserProduction production) {

        Parser.ParserAlternative alternative = production
                .getLocalReference(alternativeIdentifier.getText());

        if (alternative == null) {
            throw SemanticException.undefinedAlternativeReference(
                    alternativeIdentifier, production);
        }

        return alternative;
    }

    private static Tree.TreeAlternative findAlternative(
            TIdentifier alternativeIdentifier,
            Tree.TreeProduction production) {

        Tree.TreeAlternative alternative = production
                .getLocalReference(alternativeIdentifier.getText());

        if (alternative == null) {
            throw SemanticException.undefinedTreeAlternativeReference(
                    alternativeIdentifier, production);
        }

        return alternative;
    }

    private static Parser.ParserElement findElement(
            TIdentifier elementIdentifier,
            Parser.ParserAlternative alternative) {

        Parser.ParserElement element = alternative
                .getLocalReference(elementIdentifier.getText());

        if (element == null) {

            throw SemanticException
                    .undefinedElementReference(elementIdentifier,
                            alternative.getProduction(), alternative);
        }

        return element;
    }

    private static boolean isATokenDeclaration(
            INameDeclaration declaration) {

        if (declaration instanceof LexerExpression.NamedExpression
                || declaration instanceof Selector.LexerSelector.Selection
                || declaration instanceof Parser.ParserProduction.TokenProduction) {
            return true;
        }

        return false;
    }

    private static void tokenExpected(
            Grammar grammar,
            TIdentifier reference) {

        INameDeclaration declaration = findGlobalDeclaration(grammar, reference);

        if (!isATokenDeclaration(declaration)) {

            String[] expectedNames = { "token" };
            throw SemanticException.badReference(reference,
                    declaration.getNameType(), expectedNames);
        }
    }

    private static IReferencable tokenOrParserProductionExpected(
            Grammar grammar,
            TIdentifier reference) {

        INameDeclaration declaration = findGlobalDeclaration(grammar, reference);

        if (declaration instanceof Parser.ParserProduction.DanglingProduction) {
            String[] expectedNames = { "token", "normal parser production" };

            throw SemanticException.badReference(reference,
                    declaration.getNameType(), expectedNames);
        }
        else if (!isATokenDeclaration(declaration)
                && !(declaration instanceof Selector.ParserSelector.Selection)
                && !(declaration instanceof Parser.ParserProduction.NormalProduction)
                && !(declaration instanceof Parser.ParserProduction.TokenProduction)) {

            String[] expectedNames = { "token", "parser production" };
            throw SemanticException.badReference(reference,
                    declaration.getNameType(), expectedNames);
        }

        return (IReferencable) declaration;
    }

    private static void selectorOrParserProductionExpected(
            Grammar grammar,
            TIdentifier reference) {

        INameDeclaration declaration = findGlobalDeclaration(grammar, reference);

        if (declaration instanceof Parser.ParserProduction.DanglingProduction
                || declaration instanceof Parser.ParserProduction.TokenProduction) {
            String[] expectedNames = { "normal parser production" };

            throw SemanticException.badReference(reference,
                    declaration.getNameType(), expectedNames);
        }
        else if (!(declaration instanceof Selector.ParserSelector.Selection)
                && !(declaration instanceof Parser.ParserProduction.NormalProduction)) {

            String[] expectedNames = { "parser production" };
            throw SemanticException.badReference(reference,
                    declaration.getNameType(), expectedNames);
        }
    }

    private static IReferencable tokenOrTreeProductionExpected(
            Grammar grammar,
            TIdentifier reference) {

        INameDeclaration declaration = findTreeDeclaration(grammar, reference);

        if (!isATokenDeclaration(declaration)
                && !(declaration instanceof Tree.TreeProduction)) {

            String[] expectedNames = { "token", "tree production" };
            throw SemanticException.badReference(reference,
                    declaration.getNameType(), expectedNames);
        }

        return (IReferencable) declaration;
    }

}
