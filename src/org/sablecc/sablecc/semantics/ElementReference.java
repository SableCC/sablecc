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

import java.math.*;

import org.sablecc.sablecc.syntax3.node.*;

public class ElementReference {

    private Grammar grammar;

    private PElementBody elementBody;

    private PElementBody subtree;

    private Token location;

    private Type type;

    private Element associateParserElement;

    private ElementReference(
            Grammar grammar,
            PElementBody elementBody,
            PElementBody subtree) {

        this.grammar = grammar;
        this.elementBody = elementBody;
        this.subtree = subtree;
    }

    public PElementBody getElementBody() {

        return this.elementBody;
    }

    public PElementBody getSubtree() {

        return this.subtree;
    }

    public Token getLocation() {

        if (this.location == null) {
            this.elementBody.apply(new TreeWalker() {

                @Override
                public void defaultCase(
                        Node node) {

                    if (ElementReference.this.location == null
                            && node instanceof Token) {
                        ElementReference.this.location = (Token) node;
                    }
                }
            });
        }

        return this.location;
    }

    public Type getType() {

        if (this.type == null) {
            if (this.subtree == null) {
                Type bodyType = this.grammar
                        .getTypeResolution(this.elementBody);
                Declaration base = bodyType.getBase();
                Declaration separator = bodyType.getSeparator();
                if (separator == null) {
                    if (base instanceof Production) {
                        Production production = (Production) base;
                        ProductionTransformation productionTransformation = production
                                .getTransformation();
                        this.type = productionTransformation.getSignature()
                                .getTypes().get(0);
                    }
                    else {
                        this.type = bodyType;
                    }
                }
                else {
                    Type baseType;
                    Type separatorType;

                    if (base instanceof Production) {
                        Production baseProduction = (Production) base;
                        ProductionTransformation productionTransformation = baseProduction
                                .getTransformation();
                        baseType = productionTransformation.getSignature()
                                .getTypes().get(0);
                    }
                    else {
                        baseType = bodyType;
                    }

                    if (separator instanceof Production) {
                        Production separatorProduction = (Production) separator;
                        ProductionTransformation productionTransformation = separatorProduction
                                .getTransformation();
                        separatorType = productionTransformation.getSignature()
                                .getTypes().get(0);
                    }
                    else {
                        separatorType = bodyType;
                    }

                    this.type = new Type(true, baseType.getBase(),
                            separatorType.getBase(),
                            bodyType.getMinMultiplicity(),
                            bodyType.getMaxMultiplicity());
                }
            }
            else {
                Type bodyType = this.grammar
                        .getTypeResolution(this.elementBody);
                Type subtreeType = this.grammar.getTypeResolution(this.subtree);

                if (bodyType.getMinMultiplicity().equals(BigInteger.ZERO)) {
                    boolean isList = bodyType.isList() || subtreeType.isList();
                    BigInteger minMultiplicity = BigInteger.ZERO;
                    BigInteger maxMultiplicity = maxMultiplicity(
                            bodyType.getMaxMultiplicity(),
                            subtreeType.getMaxMultiplicity());

                    this.type = new Type(isList, subtreeType.getBase(),
                            subtreeType.getSeparator(), minMultiplicity,
                            maxMultiplicity);
                }
                else {
                    this.type = subtreeType;
                }
            }
        }

        return this.type;
    }

    private BigInteger maxMultiplicity(
            BigInteger x,
            BigInteger y) {

        if (x == null || y == null) {
            return null;
        }

        return x.max(y);
    }

    static ElementReference createDeclaredElementReference(
            final Grammar grammar,
            ANaturalElementReference declaration) {

        declaration.getElementBody().apply(new TreeWalker() {

            @Override
            public void caseANameUnit(
                    ANameUnit node) {

                grammar.resolveParserIdentifier(node.getIdentifier());
            }
        });

        grammar.resolveType(declaration.getElementBody());

        ElementReference elementReference = new ElementReference(grammar,
                declaration.getElementBody(), null);

        return elementReference;
    }

    static ElementReference createDeclaredElementReference(
            final Grammar grammar,
            ATransformedElementReference declaration) {

        declaration.getElementBody().apply(new TreeWalker() {

            @Override
            public void caseANameUnit(
                    ANameUnit node) {

                grammar.resolveParserIdentifier(node.getIdentifier());
            }
        });

        grammar.resolveType(declaration.getElementBody());

        declaration.getSubtree().apply(new TreeWalker() {

            @Override
            public void caseANameUnit(
                    ANameUnit node) {

                grammar.resolveTreeIdentifier(node.getIdentifier());
            }
        });

        grammar.resolveType(declaration.getSubtree());

        ElementReference elementReference = new ElementReference(grammar,
                declaration.getElementBody(), declaration.getSubtree());

        return elementReference;
    }

    public void associateTo(
            Element element) {

        this.associateParserElement = element;

    }

    public Element getAssociateParserElement() {

        return this.associateParserElement;
    }
}
