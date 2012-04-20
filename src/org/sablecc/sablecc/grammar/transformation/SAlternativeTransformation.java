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

package org.sablecc.sablecc.grammar.transformation;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.grammar.*;
import org.sablecc.util.*;

public class SAlternativeTransformation {

    private final Alternative alternative;

    private List<SAlternativeTransformationElement> elements = new LinkedList<SAlternativeTransformationElement>();

    public SAlternativeTransformation(
            Alternative alternative,
            List<SAlternativeTransformationElement> elements) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        if (elements == null) {
            throw new InternalException("treeAlternative shouldn't be null");
        }

        this.alternative = alternative;
        this.elements = elements;
    }

    public SAlternativeTransformation(
            Alternative alternative,
            LinkedList<Element> elements) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        if (elements == null) {
            throw new InternalException("elements shouldn't be null");
        }

        this.alternative = alternative;

        for (Element element : elements) {
            if (element instanceof Element.TokenElement) {
                this.elements
                        .add(new SAlternativeTransformationElement.ReferenceElement(
                                element, element));
            }
            else {
                SProductionTransformation productionTransformation = ((Element.ProductionElement) element)
                        .getReference().getTransformation();
                SProductionTransformationElement firstElement = productionTransformation
                        .getElements().get(0);

                if (firstElement instanceof SProductionTransformationElement.NormalElement
                        && ((SProductionTransformationElement.NormalElement) firstElement)
                                .getCardinality().isIncludedIn(
                                        CardinalityInterval.ZERO_ONE)) {
                    this.elements
                            .add(new SAlternativeTransformationElement.ReferenceElement(
                                    element, productionTransformation
                                            .getElements().get(0)));
                }
                else {
                    LinkedList<SAlternativeTransformationListElement> transformationListElements = new LinkedList<SAlternativeTransformationListElement>();
                    transformationListElements
                            .add(new SAlternativeTransformationListElement.NormalListElement(
                                    element, productionTransformation
                                            .getElements().get(0)));
                    Type.SimpleType listType;

                    if (firstElement instanceof SProductionTransformationElement.NormalElement) {
                        listType = new Type.SimpleType.HomogeneousType(
                                ((SProductionTransformationElement.NormalElement) firstElement)
                                        .getName(), firstElement
                                        .getCardinality());
                    }
                    else if (firstElement instanceof SProductionTransformationElement.SeparatedElement) {
                        listType = new Type.SimpleType.SeparatedType(
                                ((SProductionTransformationElement.SeparatedElement) firstElement)
                                        .getLeftName(),
                                ((SProductionTransformationElement.SeparatedElement) firstElement)
                                        .getRightName(), firstElement
                                        .getCardinality());

                    }
                    else if (firstElement instanceof SProductionTransformationElement.AlternatedElement) {
                        listType = new Type.SimpleType.AlternatedType(
                                ((SProductionTransformationElement.AlternatedElement) firstElement)
                                        .getLeftName(),
                                ((SProductionTransformationElement.AlternatedElement) firstElement)
                                        .getRightName(), firstElement
                                        .getCardinality());
                    }
                    else {
                        throw new InternalException("Unhandle list type");
                    }

                    this.elements
                            .add(new SAlternativeTransformationElement.ListElement(
                                    transformationListElements, listType));
                }

            }

        }
    }

    public SAlternativeTransformation(
            LinkedList<SAlternativeTransformationListElement> elements,
            Alternative alternative,
            Type.SimpleType type) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        if (elements == null) {
            throw new InternalException("elements shouldn't be null");
        }

        this.alternative = alternative;

        this.elements.add(new SAlternativeTransformationElement.ListElement(
                elements, type));

    }

    public SAlternativeTransformation(
            Alternative alternative) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        this.alternative = alternative;

        this.elements.add(new SAlternativeTransformationElement.NullElement());
    }

    public Alternative getAlternative() {

        return this.alternative;
    }

    public List<SAlternativeTransformationElement> getElements() {

        return this.elements;
    }

    public SAlternativeTransformation buildInlinedTransformation(
            Alternative inlinedAlternative,
            Map<Element, Element> oldToNewElement) {

        List<SAlternativeTransformationElement> newAlternativeTransformationElements = new LinkedList<SAlternativeTransformationElement>();

        for (SAlternativeTransformationElement element : this.elements) {
            newAlternativeTransformationElements.addAll(element.inline(
                    inlinedAlternative, oldToNewElement));
        }

        return new SAlternativeTransformation(inlinedAlternative,
                newAlternativeTransformationElements);
    }

    @Override
    public String toString() {

        String transformationText = this.alternative.getProduction().getName()
                + "."
                + this.alternative.getProduction().getAlternatives()
                        .indexOf(this.alternative) + " -> ";

        for (SAlternativeTransformationElement element : this.elements) {
            transformationText += element.toString() + " ";
        }

        transformationText += ";";

        return transformationText;
    }
}
