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

public class ProductionTransformation {

    private Grammar grammar;

    private AProductionTransformation declaration;

    private Signature signature;

    private ProductionTransformation(
            Grammar grammar,
            AProductionTransformation declaration) {

        this.grammar = grammar;
        this.declaration = declaration;
    }

    public Token getLocation() {

        return this.declaration.getProduction();
    }

    static void createDeclaredProductionTransformation(
            Grammar grammar,
            AProductionTransformation node) {

        ProductionTransformation productionTransformation = new ProductionTransformation(
                grammar, node);

        productionTransformation.computeSignature();

        Declaration declaration = grammar.getDeclarationResolution(node
                .getProduction());

        if (!(declaration instanceof Production)) {
            throw SemanticException.semanticError("\""
                    + node.getProduction().getText()
                    + "\" is not a production.", node.getProduction());
        }

        Production production = (Production) declaration;
        production.setDeclaredTransformation(productionTransformation);
    }

    private void computeSignature() {

        ArrayList<Type> subtreeTypes = new ArrayList<Type>();
        for (PElement pSubtree : this.declaration.getSubtrees()) {
            AElement subtree = (AElement) pSubtree;
            subtreeTypes.add(new Type(this.grammar, subtree.getElementBody()));
        }

        this.signature = new Signature(subtreeTypes);
    }
}
