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
import org.sablecc.sablecc.grammar.*;
import org.sablecc.sablecc.grammar.interfaces.*;

public abstract class SAlternativeTransformationListElement {

    public abstract List<SAlternativeTransformationListElement> inline(
            Alternative inlinedAlternative,
            Map<Element, Element> oldToNewElement);

    @Override
    public abstract SAlternativeTransformationListElement clone();

    public static class ReferenceElement
            extends SAlternativeTransformationListElement {

        private IElement reference;

        public ReferenceElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.reference = reference;
        }

        @Override
        public List<SAlternativeTransformationListElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationListElement> inlineResult = new LinkedList<SAlternativeTransformationListElement>();

            if (this.reference instanceof Element.ProductionElement) {
                Element.ProductionElement productionElement = (Element.ProductionElement) this.reference;

                if (productionElement.getReference().equals(
                        inlinedAlternative.getProduction())) {

                    for (SAlternativeTransformationElement element : inlinedAlternative
                            .getTransformation().getElements()) {

                        for (SAlternativeTransformationElement newElement : element
                                .inline(inlinedAlternative, oldToNewElement)) {

                            if (newElement instanceof SAlternativeTransformationElement.ReferenceElement) {
                                inlineResult
                                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                                ((SAlternativeTransformationElement.ReferenceElement) newElement)
                                                        .getReference()));
                            }
                            else if (newElement instanceof SAlternativeTransformationElement.NewElement) {
                                SAlternativeTransformationElement.NewElement newNewElemnt = (SAlternativeTransformationElement.NewElement) newElement;
                                inlineResult
                                        .add(new SAlternativeTransformationListElement.NewElement(
                                                newNewElemnt.getAlternative(),
                                                newNewElemnt.getElements()));
                            }
                            else if (newElement instanceof SAlternativeTransformationElement.ListElement) {
                                inlineResult
                                        .addAll(((SAlternativeTransformationElement.ListElement) newElement)
                                                .getElements());
                            }
                        }

                    }

                }
                else {
                    inlineResult
                            .add(new SAlternativeTransformationListElement.ReferenceElement(
                                    oldToNewElement.get(this.reference)));
                }
            }
            else {
                inlineResult
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                oldToNewElement.get(this.reference)));
            }

            return inlineResult;
        }

        @Override
        public SAlternativeTransformationListElement clone() {

            return new ReferenceElement(this.reference);
        }

    }

    public static class NewElement
            extends SAlternativeTransformationListElement {

        private IReferencable alternative;

        private List<SAlternativeTransformationElement> elements = new LinkedList<SAlternativeTransformationElement>();

        public NewElement(
                IReferencable alternative,
                List<SAlternativeTransformationElement> elements) {

            if (alternative == null) {
                throw new InternalException("reference shouldn't be null");
            }

            if (elements == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.alternative = alternative;
            this.elements = elements;
        }

        @Override
        public List<SAlternativeTransformationListElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            List<SAlternativeTransformationElement> newElements = new LinkedList<SAlternativeTransformationElement>();

            for (SAlternativeTransformationElement element : this.elements) {
                newElements.addAll(element.inline(inlinedAlternative,
                        oldToNewElement));
            }

            List<SAlternativeTransformationListElement> inlineResult = new LinkedList<SAlternativeTransformationListElement>();

            inlineResult
                    .add(new SAlternativeTransformationListElement.NewElement(
                            this.alternative, newElements));

            return inlineResult;
        }

        public IReferencable getAlternative() {

            return this.alternative;
        }

        @Override
        public SAlternativeTransformationListElement clone() {

            LinkedList<SAlternativeTransformationElement> newElements = new LinkedList<SAlternativeTransformationElement>();

            for (SAlternativeTransformationElement element : this.elements) {
                newElements.add(element.clone());
            }

            return new NewElement(this.alternative, newElements);
        }

    }

    public static class NormalListElement
            extends SAlternativeTransformationListElement {

        private IElement reference;

        public NormalListElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.reference = reference;
        }

        @Override
        public List<SAlternativeTransformationListElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationListElement> inlineResult = new LinkedList<SAlternativeTransformationListElement>();

            if (this.reference instanceof Element.ProductionElement) {
                Element.ProductionElement productionElement = (Element.ProductionElement) this.reference;

                if (productionElement.getReference().equals(
                        inlinedAlternative.getProduction())) {

                    for (SAlternativeTransformationElement element : inlinedAlternative
                            .getTransformation().getElements()) {
                        for (SAlternativeTransformationElement newElement : element
                                .inline(inlinedAlternative, oldToNewElement)) {

                            if (newElement instanceof SAlternativeTransformationElement.ReferenceElement) {
                                inlineResult
                                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                                ((SAlternativeTransformationElement.ReferenceElement) newElement)
                                                        .getReference()));
                            }
                            else if (newElement instanceof SAlternativeTransformationElement.NewElement) {
                                SAlternativeTransformationElement.NewElement newNewElemnt = (SAlternativeTransformationElement.NewElement) newElement;
                                inlineResult
                                        .add(new SAlternativeTransformationListElement.NewElement(
                                                newNewElemnt.getAlternative(),
                                                newNewElemnt.getElements()));
                            }
                            else if (newElement instanceof SAlternativeTransformationElement.ListElement) {
                                inlineResult
                                        .addAll(((SAlternativeTransformationElement.ListElement) newElement)
                                                .getElements());
                            }
                        }
                    }

                }
                else {
                    inlineResult
                            .add(new SAlternativeTransformationListElement.NormalListElement(
                                    oldToNewElement.get(this.reference)));
                }
            }
            else {
                inlineResult
                        .add(new SAlternativeTransformationListElement.NormalListElement(
                                oldToNewElement.get(this.reference)));
            }

            return inlineResult;
        }

        @Override
        public SAlternativeTransformationListElement clone() {

            // TODO Auto-generated method stub
            return null;
        }

    }

    public static class LeftListElement
            extends SAlternativeTransformationListElement {

        private IElement reference;

        public LeftListElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.reference = reference;
        }

        @Override
        public SAlternativeTransformationListElement clone() {

            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<SAlternativeTransformationListElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationListElement> inlineResult = new LinkedList<SAlternativeTransformationListElement>();

            if (this.reference instanceof Element.ProductionElement) {
                Element.ProductionElement productionElement = (Element.ProductionElement) this.reference;

                if (productionElement.getReference().equals(
                        inlinedAlternative.getProduction())) {

                    for (SAlternativeTransformationElement element : inlinedAlternative
                            .getTransformation().getElements()) {
                        for (SAlternativeTransformationElement newElement : element
                                .inline(inlinedAlternative, oldToNewElement)) {

                            if (newElement instanceof SAlternativeTransformationElement.ReferenceElement) {
                                inlineResult
                                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                                ((SAlternativeTransformationElement.ReferenceElement) newElement)
                                                        .getReference()));
                            }
                            else if (newElement instanceof SAlternativeTransformationElement.NewElement) {
                                SAlternativeTransformationElement.NewElement newNewElemnt = (SAlternativeTransformationElement.NewElement) newElement;
                                inlineResult
                                        .add(new SAlternativeTransformationListElement.NewElement(
                                                newNewElemnt.getAlternative(),
                                                newNewElemnt.getElements()));
                            }
                            else if (newElement instanceof SAlternativeTransformationElement.ListElement) {
                                inlineResult
                                        .addAll(((SAlternativeTransformationElement.ListElement) newElement)
                                                .getElements());
                            }
                        }
                    }

                }
                else {
                    inlineResult
                            .add(new SAlternativeTransformationListElement.LeftListElement(
                                    oldToNewElement.get(this.reference)));
                }
            }
            else {
                inlineResult
                        .add(new SAlternativeTransformationListElement.LeftListElement(
                                oldToNewElement.get(this.reference)));
            }

            return inlineResult;
        }

    }

    public static class RightListElement
            extends SAlternativeTransformationListElement {

        private IElement reference;

        public RightListElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.reference = reference;
        }

        @Override
        public SAlternativeTransformationListElement clone() {

            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<SAlternativeTransformationListElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationListElement> inlineResult = new LinkedList<SAlternativeTransformationListElement>();

            if (this.reference instanceof Element.ProductionElement) {
                Element.ProductionElement productionElement = (Element.ProductionElement) this.reference;

                if (productionElement.getReference().equals(
                        inlinedAlternative.getProduction())) {

                    for (SAlternativeTransformationElement element : inlinedAlternative
                            .getTransformation().getElements()) {
                        for (SAlternativeTransformationElement newElement : element
                                .inline(inlinedAlternative, oldToNewElement)) {

                            if (newElement instanceof SAlternativeTransformationElement.ReferenceElement) {
                                inlineResult
                                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                                ((SAlternativeTransformationElement.ReferenceElement) newElement)
                                                        .getReference()));
                            }
                            else if (newElement instanceof SAlternativeTransformationElement.NewElement) {
                                SAlternativeTransformationElement.NewElement newNewElemnt = (SAlternativeTransformationElement.NewElement) newElement;
                                inlineResult
                                        .add(new SAlternativeTransformationListElement.NewElement(
                                                newNewElemnt.getAlternative(),
                                                newNewElemnt.getElements()));
                            }
                            else if (newElement instanceof SAlternativeTransformationElement.ListElement) {
                                inlineResult
                                        .addAll(((SAlternativeTransformationElement.ListElement) newElement)
                                                .getElements());
                            }
                        }
                    }

                }
                else {
                    inlineResult
                            .add(new SAlternativeTransformationListElement.RightListElement(
                                    oldToNewElement.get(this.reference)));
                }
            }
            else {
                inlineResult
                        .add(new SAlternativeTransformationListElement.RightListElement(
                                oldToNewElement.get(this.reference)));
            }

            return inlineResult;
        }

    }

}
