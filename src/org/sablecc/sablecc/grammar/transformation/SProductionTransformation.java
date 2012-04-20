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
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.grammar.*;
import org.sablecc.util.*;

public class SProductionTransformation {

    private final List<SProductionTransformationElement> elements = new LinkedList<SProductionTransformationElement>();

    private Production production;

    public SProductionTransformation(
            ProductionTransformation coreReference,
            Production production) {

        if (coreReference == null) {
            throw new InternalException("coreReference shoudln't be null");
        }

        if (production == null) {
            throw new InternalException("production shouldn't be null");
        }

        this.production = production;

        generateElements(coreReference);

    }

    public SProductionTransformation(
            Production production) {

        this.production = production;
    }

    public SProductionTransformation(
            Production production,
            String name,
            IReferencable reference,
            CardinalityInterval cardinality) {

        if (reference == null) {
            throw new InternalException("elements shouldn't be null");
        }

        this.production = production;

        this.elements.add(new SProductionTransformationElement.NormalElement(
                this, name, reference, cardinality));
    }

    public SProductionTransformation(
            Production production,
            String leftName,
            String rightName,
            IReferencable leftReference,
            IReferencable rightReference,
            CardinalityInterval cardinality,
            boolean separated) {

        if (leftReference == null) {
            throw new InternalException("elements shouldn't be null");
        }

        if (rightReference == null) {
            throw new InternalException("elements shouldn't be null");
        }

        this.production = production;

        if (separated) {
            this.elements
                    .add(new SProductionTransformationElement.SeparatedElement(
                            this, leftName, rightName, leftReference,
                            rightReference, cardinality));
        }
        else {
            this.elements
                    .add(new SProductionTransformationElement.AlternatedElement(
                            this, leftName, rightName, leftReference,
                            rightReference, cardinality));

        }

    }

    public List<SProductionTransformationElement> getElements() {

        return this.elements;
    }

    public void addElement(
            SProductionTransformationElement element) {

        this.elements.add(element);
    }

    public void addElement(
            ProductionTransformationElement element,
            CardinalityInterval cardinality) {

        if (cardinality == null) {
            cardinality = element.getCardinality();
        }
        switch (element.getElementType()) {
        case NORMAL:
            this.elements
                    .add(new SProductionTransformationElement.NormalElement(
                            this,
                            ((ProductionTransformationElement.SingleElement) element)
                                    .getElement(),
                            ((ProductionTransformationElement.SingleElement) element)
                                    .getReference(), cardinality));

            break;
        case SEPARATED:
            this.elements
                    .add(new SProductionTransformationElement.SeparatedElement(
                            this,
                            ((ProductionTransformationElement.DoubleElement) element)
                                    .getLeft(),
                            ((ProductionTransformationElement.DoubleElement) element)
                                    .getRight(),
                            ((ProductionTransformationElement.DoubleElement) element)
                                    .getLeftReference(),
                            ((ProductionTransformationElement.DoubleElement) element)
                                    .getRightReference(), cardinality));
            break;
        case ALTERNATED:
            this.elements
                    .add(new SProductionTransformationElement.AlternatedElement(
                            this,
                            ((ProductionTransformationElement.DoubleElement) element)
                                    .getLeft(),
                            ((ProductionTransformationElement.DoubleElement) element)
                                    .getRight(),
                            ((ProductionTransformationElement.DoubleElement) element)
                                    .getLeftReference(),
                            ((ProductionTransformationElement.DoubleElement) element)
                                    .getRightReference(), cardinality));
            break;
        }
    }

    public Production getProduction() {

        return this.production;
    }

    public void addProduction(
            Production production) {

        this.production = production;
    }

    private void generateElements(
            ProductionTransformation coreReference) {

        for (ProductionTransformationElement element : coreReference
                .getElements()) {

            this.addElement(element, null);
        }

    }

    @Override
    public String toString() {

        String transformationText = this.production.getName() + " -> ";

        for (SProductionTransformationElement element : this.elements) {
            transformationText += element.toString() + " ";
        }

        return transformationText + ";";
    }

}
