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

package org.sablecc.sablecc.grammar;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationElement.NewElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationElement.NullElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.LeftListElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.NormalListElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.ReferenceElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.RightListElement;
import org.sablecc.sablecc.grammar.interfaces.*;
import org.sablecc.sablecc.grammar.transformation.*;

public class AlternativeTransformationBuilder
        extends GrammarVisitor {

    private Stack<List<SAlternativeTransformationElement>> elementListStack = new Stack<List<SAlternativeTransformationElement>>();

    private Stack<List<SAlternativeTransformationListElement>> listElementListStack = new Stack<List<SAlternativeTransformationListElement>>();

    private Alternative alternative;

    private SGrammar sGrammar;

    public AlternativeTransformationBuilder(
            Alternative alternative,
            AlternativeTransformation transformation,
            SGrammar sGrammar) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        if (transformation == null) {
            throw new InternalException("transformation shouldn't be null");
        }

        this.alternative = alternative;
        this.sGrammar = sGrammar;
        transformation.apply(this);
    }

    @Override
    public void visitAlternativeTransformation(
            AlternativeTransformation node) {

        this.elementListStack
                .push(new LinkedList<SAlternativeTransformationElement>());

        for (AlternativeTransformationElement element : node
                .getTransformationElements()) {
            element.apply(this);
        }

        this.alternative.addTransformation(new SAlternativeTransformation(
                this.alternative, this.elementListStack.pop()));
    }

    @Override
    public void visitAlternativeTransformationNullElement(
            NullElement node) {

        List<SAlternativeTransformationElement> listElement = new LinkedList<SAlternativeTransformationElement>();

        SAlternativeTransformationElement.NullElement nullElement = new SAlternativeTransformationElement.NullElement();

        listElement.add(nullElement);

        this.elementListStack.push(listElement);
    }

    @Override
    public void visitAlternativeTransformationNewElement(
            NewElement node) {

        this.elementListStack
                .push(new LinkedList<SAlternativeTransformationElement>());

        for (AlternativeTransformationElement element : node.getParameters()) {
            element.apply(this);
        }

        SAlternativeTransformationElement.NewElement newElement = new SAlternativeTransformationElement.NewElement(
                node.getReference(), this.elementListStack.pop());

        List<SAlternativeTransformationElement> currentList = this.elementListStack
                .pop();

        currentList.add(newElement);

        this.elementListStack.push(currentList);
    }

    @Override
    public void visitAlternativeTransformationNewListElement(
            org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.NewElement node) {

        this.elementListStack
                .push(new LinkedList<SAlternativeTransformationElement>());

        for (AlternativeTransformationElement element : node.getParameters()) {
            element.apply(this);
        }

        SAlternativeTransformationListElement.NewElement newElement = new SAlternativeTransformationListElement.NewElement(
                node.getTargetReference(), this.elementListStack.pop());

        List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                .pop();

        currentList.add(newElement);

        this.listElementListStack.push(currentList);
    }

    @Override
    public void visitAlternativeTransformationListElement(
            AlternativeTransformationElement.ListElement node) {

        this.listElementListStack
                .push(new LinkedList<SAlternativeTransformationListElement>());

        for (AlternativeTransformationListElement element : node
                .getListElements()) {
            element.apply(this);
        }

        List<SAlternativeTransformationElement> currentList = this.elementListStack
                .pop();

        currentList.add(new SAlternativeTransformationElement.ListElement(
                this.listElementListStack.pop(), node.getType()));

        this.elementListStack.push(currentList);

    }

    @Override
    public void visitAlternativeTransformationReferenceElement(
            AlternativeTransformationElement.ReferenceElement node) {

        Element origin = this.alternative.getElements().get(
                node.getOriginReference().getIndex());
        IElement target;

        List<SAlternativeTransformationElement> currentList = this.elementListStack
                .pop();

        if (node.isTransformed()) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getTargetReference();

            SProductionTransformation productionTransformation = this.sGrammar
                    .getProduction(
                            reference.getProductionTransformation().getName())
                    .getTransformation();

            target = productionTransformation.getElements().get(
                    reference.index());

        }
        else {
            target = origin;
        }

        currentList.add(new SAlternativeTransformationElement.ReferenceElement(
                origin, target));

        this.elementListStack.push(currentList);

    };

    @Override
    public void visitAlternativeTransformationReferenceListElement(
            ReferenceElement node) {

        Element origin = this.alternative.getElements().get(
                node.getOriginReference().getIndex());
        IElement target;

        List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                .pop();

        if (node.isTransformed()) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getTargetReference();

            SProductionTransformation productionTransformation = this.sGrammar
                    .getProduction(
                            reference.getProductionTransformation().getName())
                    .getTransformation();

            target = productionTransformation.getElements().get(
                    reference.index());

        }
        else {
            target = origin;
        }

        currentList
                .add(new SAlternativeTransformationListElement.ReferenceElement(
                        origin, target));

        this.listElementListStack.push(currentList);
    }

    @Override
    public void visitAlternativeTransformationNormalListReferenceListElement(
            NormalListElement node) {

        Element origin = this.alternative.getElements().get(
                node.getOriginReference().getIndex());
        SProductionTransformationElement target;

        List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                .pop();

        if (node.isTransformed()) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getTargetReference();

            SProductionTransformation productionTransformation = this.sGrammar
                    .getProduction(
                            reference.getProductionTransformation().getName())
                    .getTransformation();

            target = productionTransformation.getElements().get(
                    reference.index());

        }
        else {
            if (this.alternative.getProduction().getTransformation()
                    .getElements().size() == 0) {
                throw new InternalException(
                        "Production transformation should always be generatated");
                // TODO remove this after debug
            }
            target = this.alternative.getProduction().getTransformation()
                    .getElements().get(0);
        }

        currentList
                .add(new SAlternativeTransformationListElement.NormalListElement(
                        origin, target));

        this.listElementListStack.push(currentList);
    }

    @Override
    public void visitAlternativeTransformationLeftListReferenceListElement(
            LeftListElement node) {

        Element.ProductionElement origin = (Element.ProductionElement) this.alternative
                .getElements().get(node.getOriginReference().getIndex());
        SProductionTransformationElement target;

        List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                .pop();

        if (node.isTransformed()) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getTargetReference();

            SProductionTransformation productionTransformation = this.sGrammar
                    .getProduction(
                            reference.getProductionTransformation().getName())
                    .getTransformation();

            target = productionTransformation.getElements().get(
                    reference.index());

        }
        else {
            target = origin.getReference().getTransformation().getElements()
                    .get(0);
        }

        currentList
                .add(new SAlternativeTransformationListElement.LeftListElement(
                        origin, target));

        this.listElementListStack.push(currentList);
    }

    @Override
    public void visitAlternativeTransformationRightListReferenceListElement(
            RightListElement node) {

        Element.ProductionElement origin = (Element.ProductionElement) this.alternative
                .getElements().get(node.getOriginReference().getIndex());
        SProductionTransformationElement target;

        List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                .pop();

        if (node.isTransformed()) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getTargetReference();

            SProductionTransformation productionTransformation = this.sGrammar
                    .getProduction(
                            reference.getProductionTransformation().getName())
                    .getTransformation();

            target = productionTransformation.getElements().get(
                    reference.index());

        }
        else {
            target = origin.getReference().getTransformation().getElements()
                    .get(0);
        }

        currentList
                .add(new SAlternativeTransformationListElement.RightListElement(
                        origin, target));

        this.listElementListStack.push(currentList);
    }

}
