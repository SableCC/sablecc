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

public class Element
        extends LocalDeclaration {

    private Grammar grammar;

    private Alternative alternative;

    private AElement declaration;

    private Type type;

    // Cached values

    private boolean nameIsCached;

    private String name;

    private boolean isSelection;

    private Token location;

    private List<TransformationElement.ReferenceTransformation> referenceTransformations = new LinkedList<TransformationElement.ReferenceTransformation>();

    Element(
            Grammar grammar,
            Alternative alternative,
            AElement declaration) {

        this.grammar = grammar;
        this.alternative = alternative;
        this.declaration = declaration;

        if (declaration.getSelectionKeyword() != null) {
            this.isSelection = true;

            throw SemanticException.notImplementedError(declaration
                    .getSelectionKeyword());
        }
    }

    @Override
    public String getName() {

        if (!this.nameIsCached) {
            TElementName elementName = this.declaration.getElementName();
            if (elementName != null) {
                String text = elementName.getText();
                this.name = text.substring(1, text.length() - 2);
                this.nameIsCached = true;
            }
            else {
                this.declaration.apply(new TreeWalker() {

                    @Override
                    public void caseAElement(
                            AElement node) {

                        visit(node.getElementBody());
                    }

                    @Override
                    public void caseANormalElementBody(
                            ANormalElementBody node) {

                        PUnaryOperator unaryOperator = node.getUnaryOperator();
                        if (unaryOperator == null
                                || unaryOperator instanceof AZeroOrOneUnaryOperator) {
                            visit(node.getUnit());
                        }
                        else {
                            // anonymous
                            Element.this.nameIsCached = true;
                        }
                    }

                    @Override
                    public void caseASeparatedElementBody(
                            ASeparatedElementBody node) {

                        // anonymous
                        Element.this.nameIsCached = true;
                    }

                    @Override
                    public void caseANameUnit(
                            ANameUnit node) {

                        Element.this.name = node.getIdentifier().getText();
                        Element.this.nameIsCached = true;
                    }

                    @Override
                    public void caseAIdentifierCharUnit(
                            AIdentifierCharUnit node) {

                        String text = node.getIdentifierChar().getText();
                        Element.this.name = text.substring(1, text.length() - 1);
                        Element.this.nameIsCached = true;
                    }

                    @Override
                    public void caseACharUnit(
                            ACharUnit node) {

                        // anonymous
                        Element.this.nameIsCached = true;
                    }

                    @Override
                    public void caseAIdentifierStringUnit(
                            AIdentifierStringUnit node) {

                        String text = node.getIdentifierString().getText();
                        Element.this.name = text.substring(1, text.length() - 1);
                        Element.this.nameIsCached = true;
                    }

                    @Override
                    public void caseAStringUnit(
                            AStringUnit node) {

                        // anonymous
                        Element.this.nameIsCached = true;
                    }

                    @Override
                    public void caseAEndUnit(
                            AEndUnit node) {

                        Element.this.name = "end";
                        Element.this.nameIsCached = true;
                    }
                });

                if (!this.nameIsCached) {
                    throw new InternalException("unhandled case: "
                            + this.declaration);
                }
            }
        }

        return this.name;
    }

    @Override
    public String toString() {

        return "[" + getInternalName() + ":]";
    }

    public AElement getDeclaration() {

        return this.declaration;
    }

    public Type getType() {

        return this.type;
    }

    public Token getLocation() {

        if (this.location == null) {
            this.declaration.getElementBody().apply(new TreeWalker() {

                @Override
                public void defaultCase(
                        Node node) {

                    if (Element.this.location == null && node instanceof Token) {
                        Element.this.location = (Token) node;
                    }
                }
            });
        }

        return this.location;
    }

    public void checkTransformation() {

        Declaration base = this.type.getBase();
        Declaration separator = this.type.getSeparator();

        if (separator != null) {
            // in alternated lists, neither units can be a complex-transformed
            // production

            if (base instanceof Production) {
                ProductionTransformation productionTransformation = ((Production) base)
                        .getTransformation();
                if (!productionTransformation.isSimple()) {
                    throw SemanticException
                            .semanticError(
                                    "This complex transformations is not allowed because of the complex reference on line "
                                            + getLocation().getLine()
                                            + " char "
                                            + getLocation().getPos()
                                            + ".",
                                    productionTransformation.getLocation());
                }
            }

            if (separator instanceof Production) {
                ProductionTransformation productionTransformation = ((Production) separator)
                        .getTransformation();
                if (!productionTransformation.isSimple()) {
                    throw SemanticException
                            .semanticError(
                                    "This complex transformations is not allowed because of the complex reference on line "
                                            + getLocation().getLine()
                                            + " char "
                                            + getLocation().getPos()
                                            + ".",
                                    productionTransformation.getLocation());
                }
            }

            return;
        }

        if (base instanceof Production) {
            ProductionTransformation productionTransformation = ((Production) base)
                    .getTransformation();
            if (!productionTransformation.isSimple()) {
                // only allow complex transformations for non-list elements
                if (this.type.isList()) {
                    throw SemanticException
                            .semanticError(
                                    "This complex transformations is not allowed because of the complex reference on line "
                                            + getLocation().getLine()
                                            + " char "
                                            + getLocation().getPos()
                                            + ".",
                                    productionTransformation.getLocation());
                }
            }
        }
    }

    void setType(
            Type type) {

        this.type = type;
    }

    void addReferenceTransformation(
            TransformationElement.ReferenceTransformation referenceTransformation) {

        this.referenceTransformations.add(referenceTransformation);
    }
}
