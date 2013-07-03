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

import org.sablecc.sablecc.syntax3.node.*;

public class ElementReference {

    private Grammar grammar;

    private PElementBody elementBody;

    private PElementBody subtree;

    private Token location;

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
}
