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
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.Tree.TreeAlternative;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.grammar.*;
import org.sablecc.sablecc.grammar.interfaces.*;

public abstract class SAlternativeTransformationElement {

    public abstract List<SAlternativeTransformationElement> inline(
            Alternative inlinedAlternative,
            Map<Element, Element> oldToNewElement);

    @Override
    public abstract SAlternativeTransformationElement clone();

    public static class NullElement
            extends SAlternativeTransformationElement {

        public NullElement() {

        }

        @Override
        public List<SAlternativeTransformationElement> inline(
                Alternative alternative,
                Map<Element, Element> oldToNewElement) {

            List<SAlternativeTransformationElement> inlineResult = new LinkedList<SAlternativeTransformationElement>();
            inlineResult.add(clone());
            return inlineResult;
        }

        @Override
        public SAlternativeTransformationElement clone() {

            return new NullElement();
        }

    }

    public static class ReferenceElement
            extends SAlternativeTransformationElement {

        private IElement reference;

        public ReferenceElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }
            this.reference = reference;
        }

        public IElement getReference() {

            return this.reference;
        }

        @Override
        public List<SAlternativeTransformationElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationElement> inlineResult = new LinkedList<SAlternativeTransformationElement>();

            if (this.reference instanceof Element.ProductionElement) {
                Element.ProductionElement productionElement = (Element.ProductionElement) this.reference;

                if (productionElement.getReference().equals(
                        inlinedAlternative.getProduction())) {

                    for (SAlternativeTransformationElement element : inlinedAlternative
                            .getTransformation().getElements()) {

                        inlineResult.addAll(element.inline(inlinedAlternative,
                                oldToNewElement));
                    }

                }
                else {
                    inlineResult
                            .add(new SAlternativeTransformationElement.ReferenceElement(
                                    oldToNewElement.get(this.reference)));
                }
            }
            else {
                inlineResult
                        .add(new SAlternativeTransformationElement.ReferenceElement(
                                oldToNewElement.get(this.reference)));
            }

            return inlineResult;
        }

        @Override
        public SAlternativeTransformationElement clone() {

            return new ReferenceElement(this.reference);
        }

    }

    public static class NewElement
            extends SAlternativeTransformationElement {

        private IReferencable alternative;

        private List<SAlternativeTransformationElement> elements;

        public NewElement(
                TreeAlternative treeAlternative,
                List<SAlternativeTransformationElement> elements) {

            if (treeAlternative == null) {
                throw new InternalException("treeAlternative shouldn't be null");
            }

            if (elements == null) {
                throw new InternalException("elements shouldn't be null");
            }

            this.alternative = treeAlternative;
            this.elements = elements;
        }

        public NewElement(
                Parser.ParserAlternative parserAlternative,
                List<SAlternativeTransformationElement> elements) {

            if (parserAlternative == null) {
                throw new InternalException("treeAlternative shouldn't be null");
            }

            if (elements == null) {
                throw new InternalException("elements shouldn't be null");
            }

            this.alternative = parserAlternative;

            this.elements = elements;
        }

        @Override
        public List<SAlternativeTransformationElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationElement> newElements = new LinkedList<SAlternativeTransformationElement>();

            for (SAlternativeTransformationElement element : this.elements) {
                newElements.addAll(element.inline(inlinedAlternative,
                        oldToNewElement));
            }

            LinkedList<SAlternativeTransformationElement> inlineResult = new LinkedList<SAlternativeTransformationElement>();

            if (this.alternative instanceof TreeAlternative) {
                inlineResult
                        .add(new SAlternativeTransformationElement.NewElement(
                                (Tree.TreeAlternative) this.alternative,
                                newElements));
            }
            else {
                inlineResult
                        .add(new SAlternativeTransformationElement.NewElement(
                                (Parser.ParserAlternative) this.alternative,
                                newElements));
            }

            return inlineResult;
        }

        public IReferencable getAlternative() {

            return this.alternative;
        }

        public List<SAlternativeTransformationElement> getElements() {

            return this.elements;
        }

        @Override
        public SAlternativeTransformationElement clone() {

            LinkedList<SAlternativeTransformationElement> newElements = new LinkedList<SAlternativeTransformationElement>();

            for (SAlternativeTransformationElement element : this.elements) {
                newElements.add(element.clone());
            }

            if (this.alternative instanceof Tree.TreeAlternative) {
                return new NewElement((Tree.TreeAlternative) this.alternative,
                        newElements);
            }
            else {
                return new NewElement(
                        (Parser.ParserAlternative) this.alternative,
                        newElements);
            }
        }
    }

    public static class ListElement
            extends SAlternativeTransformationElement {

        private List<SAlternativeTransformationListElement> elements;

        public ListElement(
                List<SAlternativeTransformationListElement> elements) {

            if (elements == null) {
                throw new InternalException("elements shouldn't be null");
            }

            this.elements = elements;
        }

        @Override
        public List<SAlternativeTransformationElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationListElement> listElements = new LinkedList<SAlternativeTransformationListElement>();

            for (SAlternativeTransformationListElement element : this.elements) {
                listElements.addAll(element.inline(inlinedAlternative,
                        oldToNewElement));
            }

            LinkedList<SAlternativeTransformationElement> inlineResult = new LinkedList<SAlternativeTransformationElement>();
            inlineResult.add(new SAlternativeTransformationElement.ListElement(
                    listElements));

            return inlineResult;
        }

        public List<SAlternativeTransformationListElement> getElements() {

            return this.elements;
        }

        @Override
        public SAlternativeTransformationElement clone() {

            LinkedList<SAlternativeTransformationListElement> newElements = new LinkedList<SAlternativeTransformationListElement>();

            for (SAlternativeTransformationListElement element : this.elements) {
                newElements.add(element.clone());
            }

            return new ListElement(newElements);
        }

    }

}
