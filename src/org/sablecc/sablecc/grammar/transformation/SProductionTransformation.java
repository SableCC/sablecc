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

        if (production == null) {
            throw new InternalException("production shouldn't be null");
        }

        this.production = production;

        this.elements.add(new SProductionTransformationElement.NormalElement(
                this));
    }

    public SProductionTransformation(
            Production production,
            String name,
            IReferencable reference,
            CardinalityInterval cardinality) {

        if (production == null) {
            throw new InternalException("production shouldn't be null");
        }

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

        if (production == null) {
            throw new InternalException("production shouldn't be null");
        }

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

    public Production getProduction() {

        return this.production;
    }

    private void generateElements(
            ProductionTransformation coreReference) {

        for (ProductionTransformationElement element : coreReference
                .getElements()) {
            if (element instanceof ProductionTransformationElement.NormalElement) {
                this.elements
                        .add(new SProductionTransformationElement.NormalElement(
                                this,
                                (ProductionTransformationElement.NormalElement) element));
            }
            else if (element instanceof ProductionTransformationElement.SeparatedElement) {
                this.elements
                        .add(new SProductionTransformationElement.SeparatedElement(
                                this,
                                (ProductionTransformationElement.SeparatedElement) element));
            }
            else if (element instanceof ProductionTransformationElement.AlternatedElement) {
                this.elements
                        .add(new SProductionTransformationElement.AlternatedElement(
                                this,
                                (ProductionTransformationElement.AlternatedElement) element));
            }
        }

    }

}
