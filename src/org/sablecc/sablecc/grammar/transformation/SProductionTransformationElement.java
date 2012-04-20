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

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.core.transformation.ProductionTransformationElement.ElementType;
import org.sablecc.sablecc.grammar.interfaces.*;
import org.sablecc.util.*;

public abstract class SProductionTransformationElement
        implements IElement {

    private final SProductionTransformation productionTransformation;

    public SProductionTransformationElement(
            SProductionTransformation productionTransformation) {

        if (productionTransformation == null) {
            throw new InternalException(
                    "alternativeTransformation may not be null");
        }

        this.productionTransformation = productionTransformation;
    }

    public abstract int getIndex();

    public abstract CardinalityInterval getCardinality();

    public SProductionTransformation getProductionTransformation() {

        return this.productionTransformation;
    }

    public static class NormalElement
            extends SProductionTransformationElement {

        private String name;

        private CardinalityInterval cardinality;

        private IReferencable coreReference;

        public NormalElement(
                SProductionTransformation productionTransformation,
                ProductionTransformationElement.SingleElement coreReference) {

            super(productionTransformation);

            if (coreReference == null) {
                throw new InternalException("coreReference shouldn't be null");
            }

            this.name = coreReference.getElement();

            this.cardinality = coreReference.getCardinality();

            this.coreReference = coreReference.getReference();
        }

        public NormalElement(
                SProductionTransformation productionTransformation,
                String name,
                IReferencable coreReference,
                CardinalityInterval cardinality) {

            super(productionTransformation);

            if (coreReference == null) {
                throw new InternalException("treeReference shouldn't be null");
            }

            if (cardinality == null) {
                throw new InternalException("cardinality shouldn't be null");
            }

            this.name = name;
            this.coreReference = coreReference;
            this.cardinality = cardinality;
        }

        public NormalElement(
                SProductionTransformation productionTransformation) {

            super(productionTransformation);

            this.name = productionTransformation.getProduction().getName();

            this.cardinality = CardinalityInterval.ONE_ONE;
        }

        @Override
        public CardinalityInterval getCardinality() {

            return this.cardinality;
        }

        public String getName() {

            return this.name;
        }

        public IReferencable getCoreReference() {

            return this.coreReference;
        }

        @Override
        public int getIndex() {

            return getProductionTransformation().getElements().indexOf(this);
        }

        @Override
        public String toString() {

            return this.name
                    + (this.cardinality.equals(CardinalityInterval.ONE_ONE) ? ""
                            : this.cardinality);
        }
    }

    public static class SeparatedElement
            extends SProductionTransformationElement {

        private String leftName;

        private String rightName;

        private CardinalityInterval cardinality;

        private IReferencable leftTreeReference;

        private IReferencable rightTreeReference;

        public SeparatedElement(
                SProductionTransformation productionTransformation,
                ProductionTransformationElement.DoubleElement coreReference) {

            super(productionTransformation);

            if (coreReference == null) {
                throw new InternalException("coreReference shouldn't be null");
            }

            if (coreReference.getElementType() != ElementType.SEPARATED) {
                throw new InternalException(
                        "The core reference must be separated in a simplified separated element");
            }

            this.leftName = coreReference.getLeft();

            this.rightName = coreReference.getRight();

            this.cardinality = coreReference.getCardinality();

            this.leftTreeReference = coreReference.getLeftReference();

            this.rightTreeReference = coreReference.getRightReference();
        }

        public SeparatedElement(
                SProductionTransformation productionTransformation,
                String leftName,
                String rightName,
                IReferencable leftReference,
                IReferencable rightReference,
                CardinalityInterval cardinality) {

            super(productionTransformation);

            if (leftReference == null) {
                throw new InternalException("leftReference shouldn't be null");
            }

            if (leftName == null) {
                throw new InternalException("leftName shouldn't be null");
            }

            if (rightName == null) {
                throw new InternalException("rightName shouldn't be null");
            }

            if (rightReference == null) {
                throw new InternalException("rightReference shouldn't be null");
            }

            if (cardinality == null) {
                throw new InternalException("cardinality shouldn't be null");
            }

            this.leftName = leftName;

            this.rightName = rightName;

            this.cardinality = cardinality;

            this.leftTreeReference = leftReference;

            this.rightTreeReference = rightReference;
        }

        public String getLeftName() {

            return this.leftName;
        }

        public String getRightName() {

            return this.rightName;
        }

        @Override
        public CardinalityInterval getCardinality() {

            return this.cardinality;
        }

        public IReferencable getLeftTreeReference() {

            return this.leftTreeReference;
        }

        public IReferencable getRightTreeReference() {

            return this.rightTreeReference;
        }

        @Override
        public int getIndex() {

            return getProductionTransformation().getElements().indexOf(this);
        }

        @Override
        public String toString() {

            return "("
                    + this.leftName
                    + " Separated "
                    + this.rightName
                    + ")"
                    + (this.cardinality.equals(CardinalityInterval.ONE_ONE) ? ""
                            : this.cardinality);
        }
    }

    public static class AlternatedElement
            extends SProductionTransformationElement {

        private String leftName;

        private String rightName;

        private CardinalityInterval cardinality;

        private IReferencable leftTreeReference;

        private IReferencable rightTreeReference;

        public String getLeftName() {

            return this.leftName;
        }

        public String getRightName() {

            return this.rightName;
        }

        @Override
        public CardinalityInterval getCardinality() {

            return this.cardinality;
        }

        public IReferencable getLeftTreeReference() {

            return this.leftTreeReference;
        }

        public IReferencable getRightTreeReference() {

            return this.rightTreeReference;
        }

        public AlternatedElement(
                SProductionTransformation productionTransformation,
                String leftName,
                String rightName,
                IReferencable leftReference,
                IReferencable rightReference,
                CardinalityInterval cardinality) {

            super(productionTransformation);

            if (leftReference == null) {
                throw new InternalException("leftReference shouldn't be null");
            }

            if (leftName == null) {
                throw new InternalException("leftName shouldn't be null");
            }

            if (rightName == null) {
                throw new InternalException("rightName shouldn't be null");
            }

            if (rightReference == null) {
                throw new InternalException("rightReference shouldn't be null");
            }

            if (cardinality == null) {
                throw new InternalException("cardinality shouldn't be null");
            }

            this.leftName = leftName;

            this.rightName = rightName;

            this.cardinality = cardinality;

            this.leftTreeReference = leftReference;

            this.rightTreeReference = rightReference;
        }

        public AlternatedElement(
                SProductionTransformation productionTransformation,
                ProductionTransformationElement.DoubleElement coreReference) {

            super(productionTransformation);

            if (coreReference == null) {
                throw new InternalException("coreReference shouldn't be null");
            }

            if (coreReference.getElementType() != ElementType.ALTERNATED) {
                throw new InternalException(
                        "The core reference must be alternated in an alternated element");
            }

            this.leftName = coreReference.getLeft();

            this.rightName = coreReference.getRight();

            this.cardinality = coreReference.getCardinality();

            this.leftTreeReference = coreReference.getLeftReference();

            this.rightTreeReference = coreReference.getRightReference();
        }

        @Override
        public int getIndex() {

            return getProductionTransformation().getElements().indexOf(this);
        }

        @Override
        public String toString() {

            return "("
                    + this.leftName
                    + " "
                    + this.rightName
                    + ")"
                    + (this.cardinality.equals(CardinalityInterval.ONE_ONE) ? ""
                            : this.cardinality);
        }

    }

}
