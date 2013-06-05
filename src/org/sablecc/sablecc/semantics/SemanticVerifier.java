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
        verifier.resolveReferences();

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

                SemanticVerifier.this.grammar.addProduction(node);
            }
        });
    }

    private void collectAlternativesAndElements() {

        this.ast.apply(new TreeWalker() {

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

                this.alternatives = new ArrayList<Alternative>(node
                        .getAlternatives().size());
            }

            @Override
            public void outAParserProduction(
                    AParserProduction node) {

                Production production = SemanticVerifier.this.grammar
                        .getProduction(node);
                production.setAlternatives(this.alternatives);
                this.alternatives = null;
            }

            @Override
            public void inATreeProduction(
                    ATreeProduction node) {

                this.alternatives = new ArrayList<Alternative>(node
                        .getAlternatives().size());
            }

            @Override
            public void outATreeProduction(
                    ATreeProduction node) {

                Production production = SemanticVerifier.this.grammar
                        .getProduction(node);
                production.setAlternatives(this.alternatives);
                this.alternatives = null;
            }

            @Override
            public void inAAlternative(
                    AAlternative node) {

                this.elements = new ArrayList<Element>(node.getElements()
                        .size());
            }

            @Override
            public void outAAlternative(
                    AAlternative node) {

                SemanticVerifier.this.grammar.addAlternative(node);
                Alternative alternative = SemanticVerifier.this.grammar
                        .getAlternative(node);
                this.alternatives.add(alternative);

                alternative.setElements(this.elements);
                this.elements = null;
            }

            @Override
            public void caseAElement(
                    AElement node) {

                SemanticVerifier.this.grammar.addElement(node);
                this.elements.add(SemanticVerifier.this.grammar
                        .getElement(node));
            }
        });
    }

    private void resolveReferences() {

        this.ast.apply(new TreeWalker() {

            @Override
            public void caseANameExpression(
                    ANameExpression node) {

                SemanticVerifier.this.grammar.resolveExpression(node);
            }
        });
    }
}
