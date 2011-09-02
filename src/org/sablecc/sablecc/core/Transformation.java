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

package org.sablecc.sablecc.core;

import java.util.*;

import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;

public class Transformation
        implements IVisitableGrammarPart {

    private final List<ProductionTransformation> productionTransformations = new LinkedList<ProductionTransformation>();

    private final List<AlternativeTransformation> alternativeTransformations = new LinkedList<AlternativeTransformation>();

    private final TransformationNamespace transformationNamespace = new TransformationNamespace();

    public Transformation() {

    }

    public List<ProductionTransformation> getProductionTransformations() {

        return this.productionTransformations;
    }

    public List<AlternativeTransformation> getAlternativeTransformations() {

        return this.alternativeTransformations;
    }

    public void addProductionTransformation(
            ProductionTransformation productionTransformation) {

        this.productionTransformations.add(productionTransformation);
        if (productionTransformation instanceof ProductionTransformation.ExplicitProductionTransformation) {
            this.transformationNamespace
                    .addProductionTransformation(productionTransformation);
        }
    }

    public void addAlternativeTransformation(
            AlternativeTransformation alternativeTransformation) {

        this.alternativeTransformations.add(alternativeTransformation);

        if (alternativeTransformation instanceof AlternativeTransformation.ExplicitAlternativeTransformation) {
            this.transformationNamespace
                    .addAlternativeTransformation((AlternativeTransformation.ExplicitAlternativeTransformation) alternativeTransformation);
        }

    }

    @Override
    public void apply(
            IGrammarVisitor visitor) {

        visitor.visitTransformation(this);

    }

    private static class TransformationNamespace {

        private Map<String, ProductionTransformation> productionNameMap = new HashMap<String, ProductionTransformation>();

        private Map<String, AlternativeTransformation.ExplicitAlternativeTransformation> alternativeNameMap = new HashMap<String, AlternativeTransformation.ExplicitAlternativeTransformation>();

        public TransformationNamespace() {

        }

        public void addProductionTransformation(
                ProductionTransformation productionTransformation) {

            if (!this.productionNameMap.containsKey(productionTransformation
                    .getName())) {
                this.productionNameMap.put(productionTransformation.getName(),
                        productionTransformation);
            }
            else {
                throw SemanticException.duplicateProductionTransformationName(
                        productionTransformation, this.productionNameMap
                                .get(productionTransformation.getName()));
            }
        }

        public void addAlternativeTransformation(
                AlternativeTransformation.ExplicitAlternativeTransformation alternativeTransformation) {

            if (!this.alternativeNameMap.containsKey(alternativeTransformation
                    .getName())) {
                this.alternativeNameMap.put(
                        alternativeTransformation.getName(),
                        alternativeTransformation);
            }
            else {
                throw SemanticException.duplicateAlternativeTransformationName(
                        alternativeTransformation, this.alternativeNameMap
                                .get(alternativeTransformation.getName()));
            }
        }

        public ProductionTransformation getProductionTransformation(
                String name) {

            return this.productionNameMap.get(name);
        }

        public AlternativeTransformation.ExplicitAlternativeTransformation getAlternativeTransformation(
                String name) {

            return this.alternativeNameMap.get(name);
        }
    }
}
