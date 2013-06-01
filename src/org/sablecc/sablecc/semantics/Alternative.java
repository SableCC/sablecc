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

public class Alternative
        extends LocalDeclaration {

    private Grammar grammar;

    private AAlternative declaration;

    private List<Element> elements;

    private LocalNameSpace<Element> localNameSpace;

    // Cached values

    private String name;

    Alternative(
            Grammar grammar,
            AAlternative declaration) {

        this.grammar = grammar;
        this.declaration = declaration;
    }

    @Override
    public String getName() {

        if (this.name == null) {
            TAlternativeName alternativeName = this.declaration
                    .getAlternativeName();
            if (alternativeName != null) {
                String text = alternativeName.getText();
                this.name = text.substring(1, text.length() - 2);
            }
            else {
                Node parent = this.declaration.parent();
                if (parent instanceof AParserProduction) {
                    AParserProduction production = (AParserProduction) parent;
                    if (production.getAlternatives().size() == 1) {
                        this.name = "";
                    }
                }
                else {
                    ATreeProduction production = (ATreeProduction) parent;
                    if (production.getAlternatives().size() == 1) {
                        this.name = "";
                    }
                }

                if (this.name == null) {
                    this.declaration.apply(new TreeWalker() {

                        @Override
                        public void caseAAlternative(
                                AAlternative node) {

                            if (node.getElements().size() == 0) {
                                Alternative.this.name = "empty";
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
                                Alternative.this.name = "";
                            }
                        }

                        @Override
                        public void caseASeparatedElementBody(
                                ASeparatedElementBody node) {

                            Alternative.this.name = "";
                        }

                        @Override
                        public void caseANameUnit(
                                ANameUnit node) {

                            Alternative.this.name = node.getIdentifier()
                                    .getText();
                        }

                        @Override
                        public void caseAIdentifierCharUnit(
                                AIdentifierCharUnit node) {

                            String text = node.getIdentifierChar().getText();
                            Alternative.this.name = text.substring(1,
                                    text.length() - 1);
                        }

                        @Override
                        public void caseACharUnit(
                                ACharUnit node) {

                            Alternative.this.name = "";
                        }

                        @Override
                        public void caseAIdentifierStringUnit(
                                AIdentifierStringUnit node) {

                            String text = node.getIdentifierString().getText();
                            Alternative.this.name = text.substring(1,
                                    text.length() - 1);
                        }

                        @Override
                        public void caseAStringUnit(
                                AStringUnit node) {

                            Alternative.this.name = "";
                        }

                        @Override
                        public void caseAEndUnit(
                                AEndUnit node) {

                            Alternative.this.name = "end";
                        }
                    });

                    if (this.name == null) {
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

    void setElements(
            List<Element> elements) {

        if (this.elements != null) {
            throw new InternalException("elements is already set");
        }

        this.elements = elements;
        this.localNameSpace = new LocalNameSpace<Element>(elements);
    }
}
