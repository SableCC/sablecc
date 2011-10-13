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
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationElement.NewElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.LeftListElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.NormalListElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.ReferenceElement;
import org.sablecc.sablecc.core.transformation.AlternativeTransformationListElement.RightListElement;
import org.sablecc.sablecc.grammar.transformation.*;

public class AlternativeTransformationBuilder
        extends GrammarVisitor {

    private Stack<List<SAlternativeTransformationElement>> elementListStack = new Stack<List<SAlternativeTransformationElement>>();

    private Stack<List<SAlternativeTransformationListElement>> listElementListStack = new Stack<List<SAlternativeTransformationListElement>>();

    private Alternative alternative;

    public AlternativeTransformationBuilder(
            Alternative alternative,
            AlternativeTransformation transformation) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        if (transformation == null) {
            throw new InternalException("transformation shouldn't be null");
        }

        this.alternative = alternative;
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
                node.getReference(), this.elementListStack.pop());

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
                this.listElementListStack.pop()));

        this.elementListStack.push(currentList);

    }

    @Override
    public void visitAlternativeTransformationReferenceElement(
            AlternativeTransformationElement.ReferenceElement node) {

        if (node.getReference() instanceof Parser.ParserElement) {
            Parser.ParserElement reference = (Parser.ParserElement) node
                    .getReference();

            List<SAlternativeTransformationElement> currentList = this.elementListStack
                    .pop();

            currentList
                    .add(new SAlternativeTransformationElement.ReferenceElement(
                            this.alternative.getElements().get(
                                    reference.getIndex())));

            this.elementListStack.push(currentList);
        }
        else if (node.getReference() instanceof ProductionTransformationElement) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getReference();

            List<SAlternativeTransformationElement> currentList = this.elementListStack
                    .pop();

            SProductionTransformation productionTransformation = this.alternative
                    .getProduction().getTransformation();

            currentList
                    .add(new SAlternativeTransformationElement.ReferenceElement(
                            productionTransformation.getElements().get(
                                    reference.index())));

            this.elementListStack.push(currentList);
        }

    };

    @Override
    public void visitAlternativeTransformationReferenceListElement(
            ReferenceElement node) {

        if (node.getReference() instanceof Parser.ParserElement) {
            Parser.ParserElement reference = (Parser.ParserElement) node
                    .getReference();

            List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                    .pop();

            currentList
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            this.alternative.getElements().get(
                                    reference.getIndex())));

            this.listElementListStack.push(currentList);
        }
        else if (node.getReference() instanceof ProductionTransformationElement) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getReference();

            List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                    .pop();

            SProductionTransformation productionTransformation = this.alternative
                    .getProduction().getTransformation();

            currentList
                    .add(new SAlternativeTransformationListElement.ReferenceElement(
                            productionTransformation.getElements().get(
                                    reference.index())));

            this.listElementListStack.push(currentList);
        }
    }

    @Override
    public void visitAlternativeTransformationNormalListReferenceListElement(
            NormalListElement node) {

        if (node.getReference() instanceof Parser.ParserElement) {
            Parser.ParserElement reference = (Parser.ParserElement) node
                    .getReference();

            List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                    .pop();

            currentList
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            this.alternative.getElements().get(
                                    reference.getIndex())));

            this.listElementListStack.push(currentList);
        }
        else if (node.getReference() instanceof ProductionTransformationElement) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getReference();

            List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                    .pop();

            SProductionTransformation productionTransformation = this.alternative
                    .getProduction().getTransformation();

            currentList
                    .add(new SAlternativeTransformationListElement.NormalListElement(
                            productionTransformation.getElements().get(
                                    reference.index())));

            this.listElementListStack.push(currentList);
        }
    }

    @Override
    public void visitAlternativeTransformationLeftListReferenceListElement(
            LeftListElement node) {

        if (node.getReference() instanceof Parser.ParserElement) {
            Parser.ParserElement reference = (Parser.ParserElement) node
                    .getReference();

            List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                    .pop();

            currentList
                    .add(new SAlternativeTransformationListElement.LeftListElement(
                            this.alternative.getElements().get(
                                    reference.getIndex())));

            this.listElementListStack.push(currentList);
        }
        else if (node.getReference() instanceof ProductionTransformationElement) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getReference();

            List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                    .pop();

            SProductionTransformation productionTransformation = this.alternative
                    .getProduction().getTransformation();

            currentList
                    .add(new SAlternativeTransformationListElement.LeftListElement(
                            productionTransformation.getElements().get(
                                    reference.index())));

            this.listElementListStack.push(currentList);
        }
    }

    @Override
    public void visitAlternativeTransformationRightListReferenceListElement(
            RightListElement node) {

        if (node.getReference() instanceof Parser.ParserElement) {
            Parser.ParserElement reference = (Parser.ParserElement) node
                    .getReference();

            List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                    .pop();

            currentList
                    .add(new SAlternativeTransformationListElement.RightListElement(
                            this.alternative.getElements().get(
                                    reference.getIndex())));

            this.listElementListStack.push(currentList);
        }
        else if (node.getReference() instanceof ProductionTransformationElement) {
            ProductionTransformationElement reference = (ProductionTransformationElement) node
                    .getReference();

            List<SAlternativeTransformationListElement> currentList = this.listElementListStack
                    .pop();

            SProductionTransformation productionTransformation = this.alternative
                    .getProduction().getTransformation();

            currentList
                    .add(new SAlternativeTransformationListElement.RightListElement(
                            productionTransformation.getElements().get(
                                    reference.index())));

            this.listElementListStack.push(currentList);
        }
    }

}
