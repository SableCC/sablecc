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

    public SProductionTransformation getProductionTransformation() {

        return this.productionTransformation;
    }

    public static class NormalElement
            extends SProductionTransformationElement {

        private String name;

        private CardinalityInterval cardinality;

        private IReferencable treeReference;

        public NormalElement(
                SProductionTransformation productionTransformation,
                ProductionTransformationElement.NormalElement coreReference) {

            super(productionTransformation);

            if (coreReference == null) {
                throw new InternalException("coreReference shouldn't be null");
            }

            this.name = coreReference.getElement();

            this.cardinality = coreReference.getCardinality();

            this.treeReference = coreReference.getReference();
        }

        public CardinalityInterval getCardinality() {

            return this.cardinality;
        }

        public IReferencable getTreeReference() {

            return this.treeReference;
        }

        public String getName() {

            return this.name;
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
                ProductionTransformationElement.SeparatedElement coreReference) {

            super(productionTransformation);

            if (coreReference == null) {
                throw new InternalException("coreReference shouldn't be null");
            }

            this.leftName = coreReference.getLeft();

            this.rightName = coreReference.getRight();

            this.cardinality = coreReference.getCardinality();

            this.leftTreeReference = coreReference.getLeftReference();

            this.rightTreeReference = coreReference.getRightReference();
        }

        public String getLeftName() {

            return this.leftName;
        }

        public String getRightName() {

            return this.rightName;
        }

        public CardinalityInterval getCardinality() {

            return this.cardinality;
        }

        public IReferencable getLeftTreeReference() {

            return this.leftTreeReference;
        }

        public IReferencable getRightTreeReference() {

            return this.rightTreeReference;
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
                ProductionTransformationElement.AlternatedElement coreReference) {

            super(productionTransformation);

            if (coreReference == null) {
                throw new InternalException("coreReference shouldn't be null");
            }

            this.leftName = coreReference.getLeft();

            this.rightName = coreReference.getRight();

            this.cardinality = coreReference.getCardinality();

            this.leftTreeReference = coreReference.getLeftReference();

            this.rightTreeReference = coreReference.getRightReference();
        }
    }

}
