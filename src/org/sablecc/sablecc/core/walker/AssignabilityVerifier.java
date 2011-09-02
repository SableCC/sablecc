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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.transformation.*;

public class AssignabilityVerifier
        extends GrammarVisitor {

    private ProductionTransformation productionTransformation;

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
    public void visitAlternativeTransformation(
            AlternativeTransformation node) {

        for (AlternativeTransformationElement alternativeElement : node
                .getTransformationElements()) {
            alternativeElement.apply(this);
        }

        ProductionTransformation productionTransformation = node
                .getAlternativeReference().getProduction().getTransformation();

        if (node.getTransformationElements().size() != productionTransformation
                .getElements().size()) {
            if (node instanceof AlternativeTransformation.ImplicitAlternativeTransformation) {
                throw new InternalException(
                        "There shouldn't be an error on implicit alternative transformation");
            }
            throw SemanticException
                    .badAlternativeTransformationSignature(
                            productionTransformation,
                            (AlternativeTransformation.ExplicitAlternativeTransformation) node,
                            productionTransformation.getElements().size(), node
                                    .getTransformationElements().size());
        }

        for (int i = 0; i < node.getTransformationElements().size(); i++) {
            if (!node
                    .getTransformationElements()
                    .get(i)
                    .getType()
                    .isAssignableTo(
                            productionTransformation.getElements().get(i)
                                    .getType())) {
                throw SemanticException.elementAssignementError(
                        productionTransformation.getElements().get(i), node
                                .getTransformationElements().get(i));
            }
        }
    }

    @Override
    public void visitAlternativeTransformationNewElement(
            AlternativeTransformationElement.NewElement node) {

        List<AlternativeTransformationElement> transformationElements = node
                .getParameters();
        List<Tree.TreeElement> treeElements = node.getReference().getElements();
        if (transformationElements.size() != treeElements.size()) {

            if (node instanceof AlternativeTransformationElement.ImplicitNewElement) {
                throw new InternalException(
                        "There shouldn't be an error on implicit new element");
            }
            throw SemanticException.badNewSignature(
                    (AlternativeTransformationElement.ExplicitNewElement) node,
                    node.getReference(), transformationElements.size(),
                    treeElements.size());
        }

        for (int i = 0; i < node.getParameters().size(); i++) {
            if (!transformationElements.get(i).getType()
                    .isAssignableTo(treeElements.get(i).getType())) {
                throw SemanticException.elementAssignementError(
                        transformationElements.get(i), treeElements.get(i));
            }
        }
    }
}
