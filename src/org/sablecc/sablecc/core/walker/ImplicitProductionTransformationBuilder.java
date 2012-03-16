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

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;

public class ImplicitProductionTransformationBuilder
        extends GrammarVisitor {

    private final Grammar grammar;

    public ImplicitProductionTransformationBuilder(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    @Override
    public void visitParser(
            Parser node) {

        for (Parser.ParserProduction parserProduction : node.getProductions()) {
            parserProduction.apply(this);
        }

    }

    @Override
    public void visitLexer(
            Lexer node) {

        // Do not visit subtree
    }

    @Override
    public void visitTree(
            Tree node) {

        // Do not visit subtree
    }

    @Override
    public void visitTransformation(
            Transformation node) {

        // Do not visit subtree
    }

    @Override
    public void visitParserProduction(
            Parser.ParserProduction node) {

        if (node.getTransformation() == null) {

            INameDeclaration treeDeclaration = this.grammar
                    .getTreeReference(node.getName());

            ProductionTransformation productionTransformation;

            if (treeDeclaration instanceof Tree.TreeProduction) {

                Tree.TreeProduction treeProduction = (Tree.TreeProduction) treeDeclaration;

                productionTransformation = new ProductionTransformation.ImplicitProductionTransformation(
                        this.grammar, node, treeProduction);

            }
            else {
                productionTransformation = new ProductionTransformation.ImplicitProductionTransformation(
                        this.grammar, node);
            }

            node.addTransformation(productionTransformation);

            this.grammar.getTransformation().addProductionTransformation(
                    productionTransformation);

        }
    }
}
