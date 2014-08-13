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
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class ElementReference {

    private Grammar grammar;

    private PElementBody elementBody;

    private PElementBody subtree;

    private Token location;

    private boolean typeHasBeenComputed;

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

        if (!this.typeHasBeenComputed) {

            if (this.elementBody == null) {
                throw new InternalException(
                        "should only be used for semantic verification of explicit transformations");
            }

            Type bodyType = this.grammar.getTypeResolution(this.elementBody);

            boolean bodyIsList = bodyType.isList();
            Declaration bodyBase = bodyType.getBase();
            Declaration bodySeparator = bodyType.getSeparator();
            BigInteger bodyMinMultiplicity = bodyType.getMinMultiplicity();
            BigInteger bodyMaxMultiplicity = bodyType.getMaxMultiplicity();

            boolean isList;
            Declaration base;
            Declaration separator;
            BigInteger minMultiplicity;
            BigInteger maxMultiplicity;

            if (this.subtree == null) {

                // fill with an initial approximation
                isList = bodyIsList;
                base = bodyBase;
                separator = bodySeparator;
                minMultiplicity = bodyMinMultiplicity;
                maxMultiplicity = bodyMaxMultiplicity;

                boolean hasBase = base != null; // should be true
                boolean hasSeparator = separator != null;

                // override base if it is transformed
                if (bodyBase instanceof Production) {
                    Production production = (Production) bodyBase;
                    ProductionTransformation productionTransformation = production
                            .getTransformation();

                    // the transformation must be simple, as there is no subree

                    ArrayList<Type> types = productionTransformation
                            .getSignature().getTypes();
                    if (types.size() == 0) {
                        base = null;
                        hasBase = false;
                    }
                    else {
                        Type transformationType = types.get(0);
                        base = transformationType.getBase();
                    }
                }

                // override separator if it is transformed
                if (hasSeparator && bodySeparator instanceof Production) {
                    Production production = (Production) bodySeparator;
                    ProductionTransformation productionTransformation = production
                            .getTransformation();

                    // the transformation must be simple, as there is no subree

                    ArrayList<Type> types = productionTransformation
                            .getSignature().getTypes();
                    if (types.size() == 0) {
                        separator = null;
                        hasSeparator = false;
                    }
                    else {
                        Type transformationType = types.get(0);
                        separator = transformationType.getBase();
                    }
                }

                if (!hasBase) {
                    // base has been deleted

                    if (isList && hasSeparator) {
                        // the separator has survived

                        // make the separator into a base and decrement the
                        // multiplicity by one
                        base = separator;
                        separator = null;
                        hasBase = true;
                        hasSeparator = false;
                        if (minMultiplicity.compareTo(BigInteger.ZERO) > 0) {
                            minMultiplicity = minMultiplicity
                                    .subtract(BigInteger.ONE);
                        }
                        if (maxMultiplicity != null
                                && maxMultiplicity.compareTo(BigInteger.ZERO) > 0) {
                            maxMultiplicity = maxMultiplicity
                                    .subtract(BigInteger.ONE);
                        }
                    }
                }

                if (hasBase) {
                    this.type = new Type(isList, base, separator,
                            minMultiplicity, maxMultiplicity);
                }
                else {
                    this.type = null;
                }
            }
            else {
                Type subtreeType = this.grammar.getTypeResolution(this.subtree);

                boolean subtreeIsList = subtreeType.isList();
                Declaration subtreeBase = subtreeType.getBase();
                Declaration subtreeSeparator = subtreeType.getSeparator();
                BigInteger subtreeMinMultiplicity = subtreeType
                        .getMinMultiplicity();
                BigInteger subtreeMaxMultiplicity = subtreeType
                        .getMaxMultiplicity();

                // fill with an initial approximation
                isList = subtreeIsList;
                base = subtreeBase;
                separator = subtreeSeparator;
                minMultiplicity = subtreeMinMultiplicity;
                maxMultiplicity = subtreeMaxMultiplicity;

                // bodyIsList should be false
                // bodyBase should be a production
                // bodySeparator should be null
                // bodyMinMultiplicity should be 0 or 1
                // bodyMaxMultiplicity should be 1
                if (bodyMinMultiplicity.equals(BigInteger.ZERO)) {
                    minMultiplicity = BigInteger.ZERO;
                }

                this.type = new Type(isList, base, separator, minMultiplicity,
                        maxMultiplicity);
            }

            this.typeHasBeenComputed = true;
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
