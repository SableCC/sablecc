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
import org.sablecc.util.*;

public abstract class SAlternativeTransformationElement
        implements IVisitableTransformationPart {

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

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitNullElement(this);

        }

        @Override
        public String toString() {

            return "null";
        }

    }

    public static class ReferenceElement
            extends SAlternativeTransformationElement {

        private Element originReference;

        private IElement targetReference;

        public ReferenceElement(
                Element originReference,
                IElement targetReference) {

            if (originReference == null || targetReference == null) {
                throw new InternalException("reference shouldn't be null");
            }
            this.originReference = originReference;
            this.targetReference = targetReference;
        }

        public IElement getTargetReference() {

            return this.targetReference;
        }

        public Element getOriginReference() {

            return this.originReference;
        }

        @Override
        public List<SAlternativeTransformationElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationElement> inlineResult = new LinkedList<SAlternativeTransformationElement>();

            if (this.originReference instanceof Element.ProductionElement
                    && ((Element.ProductionElement) this.originReference)
                            .getReference().equals(
                                    inlinedAlternative.getProduction())) {

                for (SAlternativeTransformationElement element : inlinedAlternative
                        .getTransformation().getElements()) {

                    inlineResult.addAll(element.inline(inlinedAlternative,
                            oldToNewElement));
                }
            }
            else {
                IElement target;
                if (this.targetReference instanceof Element) {
                    target = oldToNewElement.get(this.targetReference);
                }
                else {
                    target = this.targetReference;
                }
                inlineResult
                        .add(new SAlternativeTransformationElement.ReferenceElement(
                                oldToNewElement.get(this.originReference),
                                target));
            }

            return inlineResult;
        }

        @Override
        public SAlternativeTransformationElement clone() {

            return new ReferenceElement(this.originReference,
                    this.targetReference);
        }

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitReferenceElement(this);

        }

        @Override
        public String toString() {

            if (this.targetReference instanceof Element.ProductionElement) {
                Element.ProductionElement element = (Element.ProductionElement) this.targetReference;
                return element.getName().equals("") ? element.getReference()
                        .getName() : element.getName();
            }
            else if (this.targetReference instanceof Element.TokenElement) {
                Element.TokenElement element = (Element.TokenElement) this.targetReference;
                return element.getName().equals("") ? element.getTypeName()
                        : element.getName();
            }
            else if (this.targetReference instanceof SProductionTransformationElement.NormalElement) {
                SProductionTransformationElement.NormalElement normalement = (SProductionTransformationElement.NormalElement) this.targetReference;
                return this.originReference.getName()
                        + "."
                        + (normalement.getName().equals("") ? "$"
                                + normalement.getIndex() : normalement
                                .getName());
            }
            else if (this.targetReference instanceof SProductionTransformationElement.SeparatedElement) {
                SProductionTransformationElement.SeparatedElement separatedElement = (SProductionTransformationElement.SeparatedElement) this.targetReference;

                return this.originReference.getName() + "." + "$"
                        + separatedElement.getIndex();

            }
            else if (this.targetReference instanceof SProductionTransformationElement.AlternatedElement) {
                SProductionTransformationElement.AlternatedElement alternatedElement = (SProductionTransformationElement.AlternatedElement) this.targetReference;

                return this.originReference.getName() + "." + "$"
                        + alternatedElement.getIndex();

            }
            else {
                throw new InternalException("Undhandle"
                        + this.targetReference.getClass());
            }
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

        public String getAlternativeName() {

            if (this.alternative instanceof Tree.TreeAlternative) {
                return ((Tree.TreeAlternative) this.alternative).getName();
            }
            else {
                return ((Parser.ParserAlternative) this.alternative).getName();
            }
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

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitNewElement(this);

        }

        @Override
        public String toString() {

            String newText = "New ";

            if (this.alternative instanceof Tree.TreeAlternative) {
                newText += ((Tree.TreeAlternative) this.alternative)
                        .getProduction().getName();
                if (((Tree.TreeAlternative) this.alternative).getName() != "") {
                    newText += "."
                            + ((Tree.TreeAlternative) this.alternative)
                                    .getName();
                }

            }
            else {
                newText += ((Parser.ParserAlternative) this.alternative)
                        .getProduction().getName();

                if (((Parser.ParserAlternative) this.alternative).getName() != "") {
                    newText += "."
                            + ((Parser.ParserAlternative) this.alternative)
                                    .getName();
                }

            }

            newText += "(";
            for (SAlternativeTransformationElement element : this.elements) {
                newText += element.toString() + " ";
            }

            newText += ")";

            return newText;
        }
    }

    public static class ListElement
            extends SAlternativeTransformationElement {

        private List<SAlternativeTransformationListElement> elements;

        private Type.SimpleType type;

        public ListElement(
                List<SAlternativeTransformationListElement> elements,
                Type.SimpleType type) {

            if (elements == null) {
                throw new InternalException("elements shouldn't be null");
            }

            this.elements = elements;
            this.type = type;
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
                    listElements, this.type));

            return inlineResult;
        }

        public List<SAlternativeTransformationListElement> getElements() {

            return this.elements;
        }

        public Type.SimpleType getType() {

            return this.type;
        }

        @Override
        public SAlternativeTransformationElement clone() {

            LinkedList<SAlternativeTransformationListElement> newElements = new LinkedList<SAlternativeTransformationListElement>();

            for (SAlternativeTransformationListElement element : this.elements) {
                newElements.add(element.clone());
            }

            return new ListElement(newElements, this.type);
        }

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitListElement(this);

        }

        @Override
        public String toString() {

            String listText = "List(";

            for (SAlternativeTransformationListElement element : this.elements) {
                listText += element.toString() + " ";
            }
            listText += ")";
            return listText;
        }

    }

}
