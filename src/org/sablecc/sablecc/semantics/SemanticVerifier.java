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

package org.sablecc.sablecc.semantics;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class SemanticVerifier {

    private Start ast;

    private Grammar grammar;

    private SemanticVerifier(
            Start ast) {

        this.ast = ast;
    }

    // TODO: change return type and return structure
    public static void verify(
            Start ast) {

        SemanticVerifier verifier = new SemanticVerifier(ast);

        verifier.collectDeclarations();
        verifier.collectAlternativesAndElements();
        verifier.resolveParserAndTreeReferences();
        verifier.resolveInlinedExpressions();
        verifier.resolveRemainingReferences();
        verifier.resolveTypes();
        verifier.createTransformations();

        // TODO: implement
    }

    private void collectDeclarations() {

        this.ast.apply(new TreeWalker() {

            @Override
            public void caseAGrammar(
                    AGrammar node) {

                SemanticVerifier.this.grammar = new Grammar(node);
                visit(node.getLexer());
                visit(node.getParser());
                visit(node.getTree());
            }

            @Override
            public void caseANamedExpression(
                    ANamedExpression node) {

                SemanticVerifier.this.grammar.addExpression(node);
            }

            // add production then visit subtree to collect implicit expressions
            @Override
            public void inAParserProduction(
                    AParserProduction node) {

                if (node.getAlternatives().size() == 0) {
                    throw SemanticException
                            .semanticError(
                                    "The production has no alternatives. An 'Empty' keyword is probably missing.",
                                    node.getSemicolon());
                }
                SemanticVerifier.this.grammar.addProduction(node);
            }

            @Override
            public void caseAIdentifierCharUnit(
                    AIdentifierCharUnit node) {

                SemanticVerifier.this.grammar.addInlinedExpression(node);
            }

            @Override
            public void caseACharUnit(
                    ACharUnit node) {

                SemanticVerifier.this.grammar.addInlinedExpression(node);
            }

            @Override
            public void caseAIdentifierStringUnit(
                    AIdentifierStringUnit node) {

                SemanticVerifier.this.grammar.addInlinedExpression(node);
            }

            @Override
            public void caseAStringUnit(
                    AStringUnit node) {

                SemanticVerifier.this.grammar.addInlinedExpression(node);
            }

            @Override
            public void caseAEndUnit(
                    AEndUnit node) {

                SemanticVerifier.this.grammar.addInlinedExpression(node);
            }

            // do not collect implicit expressions in transformation
            @Override
            public void caseATree(
                    ATree node) {

                for (PTreeProduction treeProduction : node.getTreeProductions()) {
                    visit(treeProduction);
                }
            }

            // do not collect implicit expressions in subtree
            @Override
            public void caseATreeProduction(
                    ATreeProduction node) {

                if (node.getAlternatives().size() == 0) {
                    throw SemanticException
                            .semanticError(
                                    "The production has no alternatives. An 'Empty' keyword is probably missing.",
                                    node.getSemicolon());
                }
                SemanticVerifier.this.grammar.addProduction(node);
            }
        });
    }

    private void collectAlternativesAndElements() {

        this.ast.apply(new TreeWalker() {

            Production currentProduction;

            Alternative currentAlternative;

            List<Element> elements;

            List<Alternative> alternatives;

            // do not visit transformation
            @Override
            public void caseATree(
                    ATree node) {

                for (PTreeProduction treeProduction : node.getTreeProductions()) {
                    visit(treeProduction);
                }
            }

            @Override
            public void inAParserProduction(
                    AParserProduction node) {

                this.currentProduction = SemanticVerifier.this.grammar
                        .getProduction(node);
                this.alternatives = new ArrayList<Alternative>(node
                        .getAlternatives().size());
            }

            @Override
            public void outAParserProduction(
                    AParserProduction node) {

                this.currentProduction.setAlternatives(this.alternatives);
                this.currentProduction = null;
                this.alternatives = null;
            }

            @Override
            public void inATreeProduction(
                    ATreeProduction node) {

                this.currentProduction = SemanticVerifier.this.grammar
                        .getProduction(node);
                this.alternatives = new ArrayList<Alternative>(node
                        .getAlternatives().size());
            }

            @Override
            public void outATreeProduction(
                    ATreeProduction node) {

                this.currentProduction.setAlternatives(this.alternatives);
                this.currentProduction = null;
                this.alternatives = null;
            }

            @Override
            public void inAAlternative(
                    AAlternative node) {

                SemanticVerifier.this.grammar.addAlternative(
                        this.currentProduction, node);
                this.currentAlternative = SemanticVerifier.this.grammar
                        .getAlternative(node);
                this.alternatives.add(this.currentAlternative);
                this.elements = new ArrayList<Element>(node.getElements()
                        .size());
            }

            @Override
            public void outAAlternative(
                    AAlternative node) {

                this.currentAlternative.setElements(this.elements);
                this.currentAlternative = null;
                this.elements = null;
            }

            @Override
            public void caseAElement(
                    AElement node) {

                SemanticVerifier.this.grammar.addElement(
                        this.currentAlternative, node);
                this.elements.add(SemanticVerifier.this.grammar
                        .getElement(node));
            }
        });
    }

    private void resolveParserAndTreeReferences() {

        this.ast.apply(new TreeWalker() {

            private boolean inTree;

            @Override
            public void caseANameExpression(
                    ANameExpression node) {

                SemanticVerifier.this.grammar.resolveExpression(node);
            }

            @Override
            public void caseATree(
                    ATree node) {

                this.inTree = true;
                for (PTreeProduction treeProduction : node.getTreeProductions()) {
                    visit(treeProduction);
                }
                this.inTree = false;
            }

            @Override
            public void caseANameUnit(
                    ANameUnit node) {

                if (!this.inTree) {
                    SemanticVerifier.this.grammar.resolveParserIdentifier(node
                            .getIdentifier());
                }
                else {
                    SemanticVerifier.this.grammar.resolveTreeIdentifier(node
                            .getIdentifier());
                }
            }
        });
    }

    private void resolveInlinedExpressions() {

        this.ast.apply(new TreeWalker() {

            @Override
            public void caseAIdentifierCharUnit(
                    AIdentifierCharUnit node) {

                SemanticVerifier.this.grammar.resolveIdentifierCharUnit(node);
            }

            @Override
            public void caseACharUnit(
                    ACharUnit node) {

                SemanticVerifier.this.grammar.resolveCharUnit(node);
            }

            @Override
            public void caseAIdentifierStringUnit(
                    AIdentifierStringUnit node) {

                SemanticVerifier.this.grammar.resolveIdentifierStringUnit(node);
            }

            @Override
            public void caseAStringUnit(
                    AStringUnit node) {

                SemanticVerifier.this.grammar.resolveStringUnit(node);
            }

            @Override
            public void caseAEndUnit(
                    AEndUnit node) {

                SemanticVerifier.this.grammar.resolveEndUnit(node);
            }
        });
    }

    private void resolveRemainingReferences() {

        this.ast.apply(new TreeWalker() {

            private Production currentProduction;

            private Set<String> alternativesWithPrecedence;

            @Override
            public void caseAContext(
                    AContext node) {

                for (PParserProduction parserProduction : node
                        .getParserProductions()) {
                    visit(parserProduction);
                }
            }

            @Override
            public void caseAParserProduction(
                    AParserProduction node) {

                this.currentProduction = SemanticVerifier.this.grammar
                        .getProduction(node);
                this.alternativesWithPrecedence = new TreeSet<String>();
                for (PPrecedenceRule precedenceRule : node.getPrecedenceRules()) {
                    visit(precedenceRule);
                }
                this.currentProduction = null;
                this.alternativesWithPrecedence = null;
            }

            private void detectMultiplePrecedences(
                    LinkedList<TIdentifier> identifiers) {

                for (TIdentifier identifier : identifiers) {
                    String name = identifier.getText();
                    if (this.alternativesWithPrecedence.contains(name)) {
                        throw SemanticException.semanticError(
                                "The precedence of \"" + name
                                        + "\" has already been set.",
                                identifier);
                    }
                    this.alternativesWithPrecedence.add(name);
                }
            }

            @Override
            public void caseALeftPrecedenceRule(
                    ALeftPrecedenceRule node) {

                SemanticVerifier.this.grammar.resolveAlternativeIdentifiers(
                        this.currentProduction, node.getIdentifiers());
                detectMultiplePrecedences(node.getIdentifiers());
            }

            @Override
            public void caseARightPrecedenceRule(
                    ARightPrecedenceRule node) {

                SemanticVerifier.this.grammar.resolveAlternativeIdentifiers(
                        this.currentProduction, node.getIdentifiers());
                detectMultiplePrecedences(node.getIdentifiers());
            }

            @Override
            public void caseANotPrecedenceRule(
                    ANotPrecedenceRule node) {

                SemanticVerifier.this.grammar.resolveAlternativeIdentifiers(
                        this.currentProduction, node.getIdentifiers());
                detectMultiplePrecedences(node.getIdentifiers());
            }

            @Override
            public void caseAUnaryPrecedenceRule(
                    AUnaryPrecedenceRule node) {

                SemanticVerifier.this.grammar.resolveAlternativeIdentifiers(
                        this.currentProduction, node.getIdentifiers());
                detectMultiplePrecedences(node.getIdentifiers());
            }

            @Override
            public void caseATree(
                    ATree node) {

                visit(node.getTransformation());
            }

            @Override
            public void caseAProductionTransformation(
                    AProductionTransformation node) {

                SemanticVerifier.this.grammar.resolveParserIdentifier(node
                        .getProduction());
                for (PElement element : node.getSubtrees()) {
                    visit(element);
                }
            }

            @Override
            public void caseANaturalElementReference(
                    ANaturalElementReference node) {

                // do not visit subtree
            }

            @Override
            public void caseATransformedElementReference(
                    ATransformedElementReference node) {

                // do not visit subtree
            }

            @Override
            public void caseANameUnit(
                    ANameUnit node) {

                SemanticVerifier.this.grammar.resolveTreeIdentifier(node
                        .getIdentifier());
            }

            @Override
            public void caseAUnnamedAlternativeReference(
                    AUnnamedAlternativeReference node) {

                Node parent = node.parent();
                if (parent instanceof AAlternativeTransformation) {
                    SemanticVerifier.this.grammar.resolveParserIdentifier(node
                            .getProduction());
                }
                else if (parent instanceof ANewTransformationElement) {
                    SemanticVerifier.this.grammar.resolveTreeIdentifier(node
                            .getProduction());
                }
                else {
                    throw new InternalException("unhandled case");
                }

                Declaration declaration = SemanticVerifier.this.grammar
                        .getDeclarationResolution(node.getProduction());
                if (!(declaration instanceof Production)) {
                    throw SemanticException.semanticError("\""
                            + node.getProduction().getText()
                            + "\" is not a production.", node.getProduction());
                }

                SemanticVerifier.this.grammar.resolveAlternativeReference(node);
            }

            @Override
            public void caseANamedAlternativeReference(
                    ANamedAlternativeReference node) {

                Node parent = node.parent();
                if (parent instanceof AAlternativeTransformation) {
                    SemanticVerifier.this.grammar.resolveParserIdentifier(node
                            .getProduction());
                }
                else if (parent instanceof ANewTransformationElement) {
                    SemanticVerifier.this.grammar.resolveTreeIdentifier(node
                            .getProduction());
                }
                else {
                    throw new InternalException("unhandled case");
                }

                Declaration declaration = SemanticVerifier.this.grammar
                        .getDeclarationResolution(node.getProduction());
                if (!(declaration instanceof Production)) {
                    throw SemanticException.semanticError("\""
                            + node.getProduction().getText()
                            + "\" is not a production.", node.getProduction());
                }
                Production production = (Production) declaration;
                SemanticVerifier.this.grammar.resolveAlternativeIdentifier(
                        production, node.getAlternative());

                SemanticVerifier.this.grammar.resolveAlternativeReference(node);
            }
        });
    }

    private void resolveTypes() {

        this.ast.apply(new TreeWalker() {

            @Override
            public void caseAElement(
                    AElement node) {

                SemanticVerifier.this.grammar.resolveType(node.getElementBody());
                Element element = SemanticVerifier.this.grammar
                        .getElement(node);
                if (element != null) {
                    element.setType(SemanticVerifier.this.grammar
                            .getTypeResolution(node.getElementBody()));
                }
            }
        });
    }

    private void createTransformations() {

        // create explicit production transformations
        this.ast.apply(new TreeWalker() {

            @Override
            public void caseAProductionTransformation(
                    AProductionTransformation node) {

                ProductionTransformation
                        .createDeclaredProductionTransformation(
                                SemanticVerifier.this.grammar, node);
            }

        });

        // create implicit production transformations
        this.ast.apply(new TreeWalker() {

            @Override
            public void caseAGrammar(
                    AGrammar node) {

                visit(node.getParser());
            }

            @Override
            public void caseAParserProduction(
                    AParserProduction node) {

                Production production = SemanticVerifier.this.grammar
                        .getProduction(node);
                if (production.getTransformation() == null) {
                    ProductionTransformation
                            .createImplicitProductionTransformation(
                                    SemanticVerifier.this.grammar, production);
                }
            }
        });

        // check that productions that are subject to complex (non-simple)
        // transformations are not referenced in a complex (non-simple) element.
        this.ast.apply(new TreeWalker() {

            @Override
            public void caseAGrammar(
                    AGrammar node) {

                visit(node.getParser());
            }

            @Override
            public void caseAElement(
                    AElement node) {

                Element element = SemanticVerifier.this.grammar
                        .getElement(node);
                element.checkTransformation();
            }
        });

        // create explicit alternative transformations
        this.ast.apply(new TreeWalker() {

            @Override
            public void caseAAlternativeTransformation(
                    AAlternativeTransformation node) {

                AlternativeTransformation
                        .createDeclaredAlternativeTransformation(
                                SemanticVerifier.this.grammar, node);
            }
        });
    }
}
