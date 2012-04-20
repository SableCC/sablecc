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
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.grammar.*;
import org.sablecc.sablecc.grammar.interfaces.*;

public abstract class SAlternativeTransformationListElement
        implements IVisitableTransformationPart {

    public abstract List<SAlternativeTransformationListElement> inline(
            Alternative inlinedAlternative,
            Map<Element, Element> oldToNewElement);

    @Override
    public abstract SAlternativeTransformationListElement clone();

    public static class ReferenceElement
            extends SAlternativeTransformationListElement {

        private IElement targetReference;

        private Element originReference;

        public ReferenceElement(
                Element originReference,
                IElement targetReference) {

            if (originReference == null || targetReference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.targetReference = targetReference;
            this.originReference = originReference;
        }

        @Override
        public List<SAlternativeTransformationListElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationListElement> inlineResult = new LinkedList<SAlternativeTransformationListElement>();

            if (this.targetReference instanceof Element.ProductionElement
                    && ((Element.ProductionElement) this.targetReference)
                            .getReference().equals(
                                    inlinedAlternative.getProduction())) {

                for (SAlternativeTransformationElement element : inlinedAlternative
                        .getTransformation().getElements()) {

                    for (SAlternativeTransformationElement newElement : element
                            .inline(inlinedAlternative, oldToNewElement)) {

                        if (newElement instanceof SAlternativeTransformationElement.ReferenceElement) {
                            SAlternativeTransformationElement.ReferenceElement referenceElement = (SAlternativeTransformationElement.ReferenceElement) newElement;
                            inlineResult
                                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                                            referenceElement
                                                    .getOriginReference(),
                                            referenceElement
                                                    .getTargetReference()));
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
                IElement target;
                if (this.targetReference instanceof Element) {
                    target = oldToNewElement.get(this.targetReference);
                }
                else {
                    target = this.targetReference;
                }
                inlineResult
                        .add(new SAlternativeTransformationListElement.ReferenceElement(
                                oldToNewElement.get(this.originReference),
                                target));
            }

            return inlineResult;
        }

        public IElement getTargetReference() {

            return this.targetReference;
        }

        public Element getOriginReference() {

            return this.originReference;
        }

        @Override
        public SAlternativeTransformationListElement clone() {

            return new ReferenceElement(this.originReference,
                    this.targetReference);
        }

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitReferenceListElement(this);

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
                return normalement.getProductionTransformation()
                        .getProduction().getName()
                        + "."
                        + (normalement.getName().equals("") ? "$"
                                + normalement.getIndex() : normalement
                                .getName());
            }
            else {
                throw new InternalException("Undhandel"
                        + this.targetReference.getClass());
            }
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

        public List<SAlternativeTransformationElement> getElements() {

            return this.elements;
        }

        @Override
        public SAlternativeTransformationListElement clone() {

            LinkedList<SAlternativeTransformationElement> newElements = new LinkedList<SAlternativeTransformationElement>();

            for (SAlternativeTransformationElement element : this.elements) {
                newElements.add(element.clone());
            }

            return new NewElement(this.alternative, newElements);
        }

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitNewListElement(this);

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

    public static class NormalListElement
            extends SAlternativeTransformationListElement {

        private SProductionTransformationElement targetReference;

        private Element originReference;

        public NormalListElement(
                Element originReference,
                SProductionTransformationElement targetReference) {

            if (originReference == null || targetReference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.targetReference = targetReference;
            this.originReference = originReference;
        }

        public SProductionTransformationElement getTargetReference() {

            return this.targetReference;
        }

        public Element getOriginReference() {

            return this.originReference;
        }

        @Override
        public List<SAlternativeTransformationListElement> inline(
                Alternative inlinedAlternative,
                Map<Element, Element> oldToNewElement) {

            LinkedList<SAlternativeTransformationListElement> inlineResult = new LinkedList<SAlternativeTransformationListElement>();

            if (this.originReference instanceof Element.ProductionElement
                    && ((Element.ProductionElement) this.originReference)
                            .getName().equals(
                                    inlinedAlternative.getProduction()
                                            .getName())) {

                for (SAlternativeTransformationElement element : inlinedAlternative
                        .getTransformation().getElements()) {
                    for (SAlternativeTransformationElement newElement : element
                            .inline(inlinedAlternative, oldToNewElement)) {

                        if (newElement instanceof SAlternativeTransformationElement.ReferenceElement) {
                            SAlternativeTransformationElement.ReferenceElement referenceElement = (SAlternativeTransformationElement.ReferenceElement) newElement;
                            inlineResult
                                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                                            referenceElement
                                                    .getOriginReference(),
                                            referenceElement
                                                    .getTargetReference()));
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
                                oldToNewElement.get(this.originReference),
                                this.targetReference));
            }

            return inlineResult;
        }

        @Override
        public SAlternativeTransformationListElement clone() {

            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitNormalListListElement(this);

        }

        @Override
        public String toString() {

            if (this.targetReference instanceof SProductionTransformationElement.NormalElement) {
                SProductionTransformationElement.NormalElement normalement = (SProductionTransformationElement.NormalElement) this.targetReference;
                return this.originReference.getName()
                        + "."
                        + (normalement.getName().equals("") ? "$"
                                + normalement.getIndex() : normalement
                                .getName()) + "...";
            }
            else if (this.targetReference instanceof SProductionTransformationElement.SeparatedElement
                    || this.targetReference instanceof SProductionTransformationElement.AlternatedElement) {
                SProductionTransformationElement separatedElement = this.targetReference;
                return this.originReference.getName() + "." + "$"
                        + separatedElement.getIndex() + "...";
            }
            else {
                throw new InternalException("Undhandel"
                        + this.targetReference.getClass());
            }

        }

    }

    public static class LeftListElement
            extends SAlternativeTransformationListElement {

        private SProductionTransformationElement targetReference;

        private Element originReference;

        public LeftListElement(
                Element originReference,
                SProductionTransformationElement targetReference) {

            if (targetReference == null || originReference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.targetReference = targetReference;
            this.originReference = originReference;
        }

        public SProductionTransformationElement getTargetReference() {

            return this.targetReference;
        }

        public Element getOriginReference() {

            return this.originReference;
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

            if (this.originReference instanceof Element.ProductionElement
                    && ((Element.ProductionElement) this.originReference)
                            .getName().equals(
                                    inlinedAlternative.getProduction()
                                            .getName())) {

                for (SAlternativeTransformationElement element : inlinedAlternative
                        .getTransformation().getElements()) {
                    for (SAlternativeTransformationElement newElement : element
                            .inline(inlinedAlternative, oldToNewElement)) {

                        if (newElement instanceof SAlternativeTransformationElement.ReferenceElement) {
                            SAlternativeTransformationElement.ReferenceElement referenceElement = (SAlternativeTransformationElement.ReferenceElement) newElement;
                            inlineResult
                                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                                            referenceElement
                                                    .getOriginReference(),
                                            referenceElement
                                                    .getTargetReference()));
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
                                oldToNewElement.get(this.originReference),
                                this.targetReference));
            }

            return inlineResult;
        }

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitLeftListListElement(this);

        }

        @Override
        public String toString() {

            if (this.targetReference instanceof SProductionTransformationElement.SeparatedElement
                    || this.targetReference instanceof SProductionTransformationElement.AlternatedElement) {
                SProductionTransformationElement doubleElement = this.targetReference;
                return this.originReference.getName() + "." + "$"
                        + doubleElement.getIndex() + ".Left";
            }
            else {
                throw new InternalException("Undhandle"
                        + this.targetReference.getClass());
            }
        }

    }

    public static class RightListElement
            extends SAlternativeTransformationListElement {

        private SProductionTransformationElement targetReference;

        private Element originReference;

        public RightListElement(
                Element originReference,
                SProductionTransformationElement targetReference) {

            if (targetReference == null || originReference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.targetReference = targetReference;
            this.originReference = originReference;
        }

        public SProductionTransformationElement getTargetReference() {

            return this.targetReference;
        }

        public Element getOriginReference() {

            return this.originReference;
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

            if (this.originReference instanceof Element.ProductionElement
                    && ((Element.ProductionElement) this.originReference)
                            .getName().equals(
                                    inlinedAlternative.getProduction()
                                            .getName())) {

                for (SAlternativeTransformationElement element : inlinedAlternative
                        .getTransformation().getElements()) {
                    for (SAlternativeTransformationElement newElement : element
                            .inline(inlinedAlternative, oldToNewElement)) {

                        if (newElement instanceof SAlternativeTransformationElement.ReferenceElement) {
                            SAlternativeTransformationElement.ReferenceElement referenceElement = (SAlternativeTransformationElement.ReferenceElement) newElement;
                            inlineResult
                                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                                            referenceElement
                                                    .getOriginReference(),
                                            referenceElement
                                                    .getTargetReference()));
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
                                oldToNewElement.get(this.originReference),
                                this.targetReference));
            }

            return inlineResult;
        }

        @Override
        public void apply(
                ITransformationVisitor visitor) {

            visitor.visitRightListListElement(this);

        }

        @Override
        public String toString() {

            if (this.targetReference instanceof SProductionTransformationElement.SeparatedElement
                    || this.targetReference instanceof SProductionTransformationElement.AlternatedElement) {
                SProductionTransformationElement doubleElement = this.targetReference;

                return this.originReference.getName() + "." + "$"
                        + doubleElement.getIndex() + ".Right";
            }
            else {
                throw new InternalException("Undhandle"
                        + this.targetReference.getClass());
            }

        }

    }

}
