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
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Alternative
        extends LocalDeclaration {

    private Grammar grammar;

    private Production production;

    private AAlternative declaration;

    private List<Element> elements;

    private LocalNameSpace<Element> localNameSpace;

    private AlternativeTransformation alternativeTransformation;

    // Cached values

    private boolean nameIsCached;

    private String name;

    private Token location;

    Alternative(
            Grammar grammar,
            Production production,
            AAlternative declaration) {

        this.grammar = grammar;
        this.production = production;
        this.declaration = declaration;
    }

    @Override
    public String getName() {

        if (!this.nameIsCached) {
            TAlternativeName alternativeName = this.declaration
                    .getAlternativeName();
            if (alternativeName != null) {
                String text = alternativeName.getText();
                this.name = text.substring(1, text.length() - 2);
                this.nameIsCached = true;
            }
            else {
                Node parent = this.declaration.parent();
                if (parent instanceof AParserProduction) {
                    AParserProduction production = (AParserProduction) parent;
                    if (production.getAlternatives().size() == 1) {
                        this.name = "";
                        this.nameIsCached = true;
                    }
                }
                else {
                    ATreeProduction production = (ATreeProduction) parent;
                    if (production.getAlternatives().size() == 1) {
                        this.name = "";
                        this.nameIsCached = true;
                    }
                }

                if (!this.nameIsCached) {
                    this.declaration.apply(new TreeWalker() {

                        @Override
                        public void caseAAlternative(
                                AAlternative node) {

                            if (node.getElements().size() == 0) {
                                Alternative.this.name = "empty";
                                Alternative.this.nameIsCached = true;
                            }
                            else {
                                visit(node.getElements().getFirst());
                            }
                        }

                        @Override
                        public void caseAElement(
                                AElement node) {

                            TElementName elementName = node.getElementName();
                            if (elementName != null) {
                                String text = elementName.getText();
                                Alternative.this.name = text.substring(1,
                                        text.length() - 2);
                                Alternative.this.nameIsCached = true;
                            }
                            else {
                                visit(node.getElementBody());
                            }
                        }

                        @Override
                        public void caseANormalElementBody(
                                ANormalElementBody node) {

                            PUnaryOperator unaryOperator = node
                                    .getUnaryOperator();
                            if (unaryOperator == null
                                    || unaryOperator instanceof AZeroOrOneUnaryOperator) {
                                visit(node.getUnit());
                            }
                            else {
                                // anonymous
                                Alternative.this.nameIsCached = true;
                            }
                        }

                        @Override
                        public void caseASeparatedElementBody(
                                ASeparatedElementBody node) {

                            // anonymous
                            Alternative.this.nameIsCached = true;
                        }

                        @Override
                        public void caseANameUnit(
                                ANameUnit node) {

                            Alternative.this.name = node.getIdentifier()
                                    .getText();
                            Alternative.this.nameIsCached = true;
                        }

                        @Override
                        public void caseAIdentifierCharUnit(
                                AIdentifierCharUnit node) {

                            String text = node.getIdentifierChar().getText();
                            Alternative.this.name = text.substring(1,
                                    text.length() - 1);
                            Alternative.this.nameIsCached = true;
                        }

                        @Override
                        public void caseACharUnit(
                                ACharUnit node) {

                            // anonymous
                            Alternative.this.nameIsCached = true;
                        }

                        @Override
                        public void caseAIdentifierStringUnit(
                                AIdentifierStringUnit node) {

                            String text = node.getIdentifierString().getText();
                            Alternative.this.name = text.substring(1,
                                    text.length() - 1);
                            Alternative.this.nameIsCached = true;
                        }

                        @Override
                        public void caseAStringUnit(
                                AStringUnit node) {

                            // anonymous
                            Alternative.this.nameIsCached = true;
                        }

                        @Override
                        public void caseAEndUnit(
                                AEndUnit node) {

                            Alternative.this.name = "end";
                            Alternative.this.nameIsCached = true;
                        }
                    });

                    if (!this.nameIsCached) {
                        throw new InternalException("unhandled case: "
                                + this.declaration);
                    }
                }
            }
        }

        return this.name;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(getInternalName());
        sb.append(":}");
        for (Element element : this.elements) {
            sb.append(" " + element);
        }
        return sb.toString();
    }

    public Production getProduction() {

        return this.production;
    }

    public List<Element> getElements() {

        return this.elements;
    }

    public Token getLocation() {

        if (this.location == null) {
            this.location = this.declaration.getAlternativeName();
            if (this.location == null) {
                this.location = this.declaration.getEmptyKeyword();
                if (this.location == null) {
                    AElement element = (AElement) this.declaration
                            .getElements().get(0);
                    this.location = element.getSelectionKeyword();
                    if (this.location == null) {
                        this.location = element.getElementName();
                        if (this.location == null) {
                            PElementBody pElementBody = element
                                    .getElementBody();
                            if (pElementBody instanceof ANormalElementBody) {
                                ANormalElementBody elementBody = (ANormalElementBody) pElementBody;
                                PUnit unit = elementBody.getUnit();
                                unit.apply(new DepthFirstAdapter() {

                                    @Override
                                    public void defaultCase(
                                            Node node) {

                                        Alternative.this.location = (Token) node;
                                    }
                                });
                            }
                            else {
                                ASeparatedElementBody elementBody = (ASeparatedElementBody) pElementBody;
                                this.location = elementBody.getLPar();
                            }
                        }
                    }
                }
            }
        }

        return this.location;
    }

    void setElements(
            List<Element> elements) {

        if (this.elements != null) {
            throw new InternalException("elements is already set");
        }

        this.elements = elements;
        this.localNameSpace = new LocalNameSpace<Element>(elements);
    }

    void setDeclaredTransformation(
            AlternativeTransformation alternativeTransformation) {

        if (this.alternativeTransformation != null) {
            Token location = this.alternativeTransformation.getLocation();
            throw SemanticException.semanticError("The alternative "
                    + getProduction().getName() + "." + getName()
                    + " was already transformed on line " + location.getLine()
                    + " char " + location.getPos() + ".",
                    alternativeTransformation.getLocation());
        }

        this.alternativeTransformation = alternativeTransformation;
    }
}
