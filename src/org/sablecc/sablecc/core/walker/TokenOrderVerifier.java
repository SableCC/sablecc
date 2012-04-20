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
import org.sablecc.sablecc.syntax3.node.*;

public class TokenOrderVerifier
        extends GrammarVisitor {

    private final Grammar grammar;

    private Parser.ParserAlternative alternative;

    private int previousIndex = -1;

    private Map<String, Integer> transformedProductionPreviousIndex = new HashMap<String, Integer>();

    public TokenOrderVerifier(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    @Override
    public void visitParserAlternative(
            Parser.ParserAlternative node) {

        this.alternative = node;
        this.previousIndex = -1;
        this.transformedProductionPreviousIndex.clear();
        node.getTransformation().apply(this);

    }

    @Override
    public void visitAlternativeTransformationReferenceElement(
            AlternativeTransformationElement.ReferenceElement node) {

        if (node.getTargetReference() instanceof Parser.ParserElement) {
            Parser.ParserElement element = (Parser.ParserElement) node
                    .getTargetReference();

            element.apply(this);
        }
        else {
            // Transformed reference aren't used by the alternative
            // transformation generation algorithm.

            // 1) Verify that the order is respected in comparison to parser
            // alternative
            String productionReference = "";

            AlternativeTransformationElement.ExplicitReferenceElement element = (AlternativeTransformationElement.ExplicitReferenceElement) node;

            productionReference = ((ATransformedElementReference) element
                    .getDeclaration().getElementReference()).getElement()
                    .getText();

            this.alternative.getLocalReference(productionReference).apply(this);

            // 2) Verify that the order is respected in comparison to production
            // transformation
            String transformationElementReference = "";

            transformationElementReference = ((ATransformedElementReference) element
                    .getDeclaration().getElementReference()).getPart()
                    .getText();

            Parser.ParserProduction transformedProduction = (Parser.ParserProduction) ((Parser.ParserElement.SingleElement) this.alternative
                    .getLocalReference(productionReference)).getReference();

            ProductionTransformationElement transformationElement = ((ProductionTransformation.ExplicitProductionTransformation) transformedProduction
                    .getTransformation())
                    .getLocalReference(transformationElementReference);

            transformationElement.apply(this);

        }
    }

    @Override
    public void visitAlternativeTransformationListElement(
            AlternativeTransformationListElement node) {

        if (node.getTargetReference() instanceof Parser.ParserElement) {
            Parser.ParserElement element = (Parser.ParserElement) node
                    .getTargetReference();

            element.apply(this);
        }
        else {
            // Transformed reference aren't used by the alternative
            // transformation generation algorithm.

            String referenceName = "";

            if (node instanceof AlternativeTransformationListElement.ExplicitReferenceElement) {
                AlternativeTransformationListElement.ExplicitReferenceElement element = (AlternativeTransformationListElement.ExplicitReferenceElement) node;

                referenceName = ((ATransformedElementReference) element
                        .getDeclaration().getElementReference()).getElement()
                        .getText();
            }
            else if (node instanceof AlternativeTransformationListElement.ExplicitNormalListElement) {
                AlternativeTransformationListElement.ExplicitNormalListElement element = (AlternativeTransformationListElement.ExplicitNormalListElement) node;
                referenceName = ((ATransformedElementReference) element
                        .getDeclaration().getElementReference()).getElement()
                        .getText();
            }
            else if (node instanceof AlternativeTransformationListElement.ExplicitLeftListElement) {
                AlternativeTransformationListElement.ExplicitLeftListElement element = (AlternativeTransformationListElement.ExplicitLeftListElement) node;
                referenceName = ((ATransformedElementReference) element
                        .getDeclaration().getElementReference()).getElement()
                        .getText();
            }
            else if (node instanceof AlternativeTransformationListElement.ExplicitRightListElement) {
                AlternativeTransformationListElement.ExplicitRightListElement element = (AlternativeTransformationListElement.ExplicitRightListElement) node;
                referenceName = ((ATransformedElementReference) element
                        .getDeclaration().getElementReference()).getElement()
                        .getText();
            }

            if (this.alternative.getLocalReference(referenceName) != null) {
                this.alternative.getLocalReference(referenceName).apply(this);
            }
        }
    }

    @Override
    public void visitParserElement(
            Parser.ParserElement node) {

        int index = this.alternative.getElements().indexOf(node);

        if (this.previousIndex != -1 && index < this.previousIndex) {
            throw SemanticException.elementReordered(this.alternative);
        }

        this.previousIndex = index;
    }

    @Override
    public void visitProductionTransformationElement(
            ProductionTransformationElement node) {

        ProductionTransformation transformation = node
                .getProductionTransformation();
        int index = transformation.getElements().indexOf(node);

        if (this.transformedProductionPreviousIndex.containsKey(transformation
                .getName())) {

            if (index < this.transformedProductionPreviousIndex
                    .get(transformation.getName())) {
                throw SemanticException.transformationElementReordered(
                        this.alternative, transformation);
            }
        }
        else {
            this.transformedProductionPreviousIndex.put(
                    transformation.getName(), index);
        }
    }

}
