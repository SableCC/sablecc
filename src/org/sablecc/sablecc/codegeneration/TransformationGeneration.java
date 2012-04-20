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

package org.sablecc.sablecc.codegeneration;

import static org.sablecc.util.CamelCase.*;

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.codegeneration.java.macro.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.grammar.*;
import org.sablecc.sablecc.grammar.Element.ProductionElement;
import org.sablecc.sablecc.grammar.Element.TokenElement;
import org.sablecc.sablecc.grammar.interfaces.*;
import org.sablecc.sablecc.grammar.transformation.*;
import org.sablecc.sablecc.oldlrautomaton.*;
import org.sablecc.util.Type.SimpleType.AlternatedType;
import org.sablecc.util.Type.SimpleType.HomogeneousType;
import org.sablecc.util.Type.SimpleType.SeparatedType;

public class TransformationGeneration
        implements ITransformationVisitor {

    private final Stack<Object> macroStack = new Stack<Object>();

    private final Stack<ListDescriptor> listStack = new Stack<ListDescriptor>();

    private final OldAlternative reducedAlternative;

    private final Grammar grammar;

    private Map<String, BigInteger> nameToVarNameMap = new HashMap<String, BigInteger>();

    private final Map<IReferencable, String> alternativeToCamelFullName;

    TransformationGeneration(
            Grammar grammar,
            OldAlternative reducedAlternative,
            MReduceDecision reduceDecision,
            Map<IReferencable, String> alternativeToCamelFullName) {

        this.grammar = grammar;
        this.reducedAlternative = reducedAlternative;
        this.macroStack.push(reduceDecision);
        this.alternativeToCamelFullName = alternativeToCamelFullName;

    }

    private String getNextVarId(
            String base) {

        BigInteger varCount = this.nameToVarNameMap.get(base);

        if (varCount == null) {
            if (base == "") {
                this.nameToVarNameMap.put(base, BigInteger.ONE);
                return to_camelCase("$" + BigInteger.ONE);
            }
            else {
                this.nameToVarNameMap.put(base, BigInteger.ZERO);
                return to_camelCase(base);
            }

        }
        else {
            varCount.add(BigInteger.ONE);
            this.nameToVarNameMap.put(base, varCount);
            return to_camelCase(base + "$" + varCount);
        }
    }

    @Override
    public void visitNewElement(
            SAlternativeTransformationElement.NewElement node) {

        String alt_CamelCase = this.alternativeToCamelFullName.get(node
                .getAlternative());

        String elementName = getNextVarId(alt_CamelCase);

        Object currentMacro = this.macroStack.peek();

        MNewTreeClass newMacro;

        if (currentMacro instanceof MReduceDecision) {
            newMacro = ((MReduceDecision) currentMacro).newNewTreeClass(
                    alt_CamelCase, elementName);
        }
        else if (currentMacro instanceof MNewTreeClass) {
            newMacro = ((MNewTreeClass) currentMacro).newNewTreeClass(
                    alt_CamelCase, elementName);
        }
        else {
            throw new InternalException("Unhandle " + currentMacro.getClass());
        }

        this.macroStack.push(newMacro);

        for (SAlternativeTransformationElement e : node.getElements()) {
            e.apply(this);
        }

        this.macroStack.pop();

        if (currentMacro instanceof MReduceDecision) {
            ((MReduceDecision) currentMacro).newAddNToForest(elementName);
        }
        else if (currentMacro instanceof MNewTreeClass) {
            newMacro.newNewParameter(elementName);
        }
        else {
            throw new InternalException("Unhandle " + currentMacro.getClass());
        }

    }

    @Override
    public void visitReferenceElement(
            SAlternativeTransformationElement.ReferenceElement node) {

        IElement reference = node.getTargetReference();
        Object currentMacro = this.macroStack.peek();

        String elementName = to_camelCase(this.grammar.getSimplifiedGrammar()
                .getOldElement(node.getOriginReference()).getName());

        if (reference instanceof TokenElement) {

            OldElement matchedElement = this.reducedAlternative
                    .getElement((TokenElement) reference);

            if (currentMacro instanceof MNewTreeClass) {
                ((MNewTreeClass) currentMacro).newNormalParameter(
                        to_CamelCase(matchedElement.getTypeName()),
                        elementName, "0");
            }
            else if (currentMacro instanceof MReduceDecision) {
                ((MReduceDecision) currentMacro).newAddPopToForest(elementName,
                        "0");
            }
            else {
                throw new InternalException("Unhandled "
                        + currentMacro.getClass());
            }

        }
        else if (reference instanceof ProductionElement) {
            OldElement matchedElement = this.reducedAlternative
                    .getElement((ProductionElement) reference);

            if (currentMacro instanceof MNewTreeClass) {
                ((MNewTreeClass) currentMacro).newNormalParameter(
                        to_CamelCase(matchedElement.getTypeName()),
                        elementName, "0");
            }
            else if (currentMacro instanceof MReduceDecision) {
                ((MReduceDecision) currentMacro).newAddPopToForest(elementName,
                        "0");
            }
            else {
                throw new InternalException("Unhandled "
                        + currentMacro.getClass());
            }
        }
        else if (reference instanceof SProductionTransformationElement) {

            if (reference instanceof SProductionTransformationElement.NormalElement) {
                SProductionTransformationElement.NormalElement normalElement = (SProductionTransformationElement.NormalElement) reference;

                if (normalElement.getCoreReference() instanceof Tree.TreeProduction) {
                    String prodCamelCaseType = ((Tree.TreeProduction) normalElement
                            .getCoreReference()).getName_CamelCase();

                    if (currentMacro instanceof MNewTreeClass) {
                        ((MNewTreeClass) currentMacro).newNormalParameter(
                                prodCamelCaseType, elementName,
                                normalElement.getIndex() + "");
                    }
                    else if (currentMacro instanceof MReduceDecision) {
                        ((MReduceDecision) currentMacro)
                                .newAddLToForest(elementName);
                    }
                    else {
                        throw new InternalException("Unhandled "
                                + currentMacro.getClass());
                    }
                }

            }
            else if (reference instanceof SProductionTransformationElement.SeparatedElement) {
                SProductionTransformationElement.SeparatedElement separatedElement = (SProductionTransformationElement.SeparatedElement) reference;

                String elementLeftName = computeElementType(separatedElement
                        .getLeftTreeReference());
                String elementRightName = computeElementType(separatedElement
                        .getRightTreeReference());

                if (currentMacro instanceof MNewTreeClass) {
                    ((MNewTreeClass) currentMacro).newSeparatedParameter(
                            elementLeftName,
                            elementRightName,
                            elementName,
                            ((SProductionTransformationElement) reference)
                                    .getIndex() + "");
                }
                else if (currentMacro instanceof MReduceDecision) {

                    ((MReduceDecision) currentMacro)
                            .newAddLToForest(elementName);
                }
                else {
                    throw new InternalException("Unhandled "
                            + currentMacro.getClass());
                }

            }
            else if (reference instanceof SProductionTransformationElement.AlternatedElement) {
                SProductionTransformationElement.AlternatedElement alternatedElement = (SProductionTransformationElement.AlternatedElement) reference;

                String elementLeftName = computeElementType(alternatedElement
                        .getLeftTreeReference());
                String elementRightName = computeElementType(alternatedElement
                        .getRightTreeReference());

                if (currentMacro instanceof MNewTreeClass) {
                    ((MNewTreeClass) currentMacro).newAlternatedParameter(
                            elementLeftName,
                            elementRightName,
                            elementName,
                            ((SProductionTransformationElement) reference)
                                    .getIndex() + "");
                }
                else if (currentMacro instanceof MReduceDecision) {

                    ((MReduceDecision) currentMacro)
                            .newAddLToForest(elementName);
                }
                else {
                    throw new InternalException("Unhandled "
                            + currentMacro.getClass());
                }

            }
            else {
                throw new InternalException("Unhandled "
                        + currentMacro.getClass());
            }

        }

    }

    private String computeElementType(
            IReferencable reference) {

        if (reference instanceof Tree.TreeProduction) {
            return ((Tree.TreeProduction) reference).getName_CamelCase();
        }
        else if (reference instanceof LexerExpression.NamedExpression) {
            return ((LexerExpression.NamedExpression) reference)
                    .getName_CamelCase();
        }
        else if (reference instanceof LexerExpression.InlineExpression) {
            return ((LexerExpression.InlineExpression) reference)
                    .getInternalName_CamelCase();
        }
        else {
            throw new InternalException("Unhandled reference type");
        }
    }

    private String computeListType(
            String typeName) {

        INameDeclaration nameDeclaration = this.grammar
                .getGlobalReference(typeName);

        if (nameDeclaration instanceof LexerExpression.NamedExpression) {
            LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) nameDeclaration;
            return namedToken.getName_CamelCase();
        }
        else if (nameDeclaration instanceof LexerExpression.InlineExpression) {
            LexerExpression.InlineExpression inlineToken = (LexerExpression.InlineExpression) nameDeclaration;
            return inlineToken.getInternalName_CamelCase();
        }
        else if (nameDeclaration instanceof Parser.ParserProduction) {
            return to_CamelCase(((Parser.ParserProduction) nameDeclaration)
                    .getName());
        }
        else {
            throw new InternalException("Unhandle "
                    + nameDeclaration.getClass());
        }
    }

    @Override
    public void visitListElement(
            SAlternativeTransformationElement.ListElement node) {

        Object currentMacro = this.macroStack.peek();
        String listName = getNextVarId("");
        MNewList newList;

        if (currentMacro instanceof MReduceDecision) {
            newList = ((MReduceDecision) currentMacro).newNewList(listName);
        }
        else if (currentMacro instanceof MNewTreeClass) {
            newList = ((MNewTreeClass) currentMacro).newNewList(listName);
        }
        else {
            throw new InternalException("Unhandle " + currentMacro.getClass());
        }

        if (node.getType() instanceof HomogeneousType) {

            HomogeneousType type = (HomogeneousType) node.getType();

            String listType = computeListType(type.getName());

            newList.newNormalDeclaration(listType);
            this.listStack.push(new NormalListDescriptor(listName, listType));

        }
        else {
            String leftListType;
            String rightListType;

            if (node.getType() instanceof SeparatedType) {
                SeparatedType type = (SeparatedType) node.getType();

                leftListType = computeListType(type.getLeftElementName());
                rightListType = computeListType(type.getRightElementName());

                newList.newSeparatedDeclaration(leftListType, rightListType);

                this.listStack.push(new DoubleListDescriptor(
                        DoubleListDescriptor.Type.SEPARATED, listName,
                        leftListType, rightListType));
            }
            else if (node.getType() instanceof AlternatedType) {
                AlternatedType type = (AlternatedType) node.getType();

                leftListType = computeListType(type.getLeftElementName());
                rightListType = computeListType(type.getRightElementName());

                newList.newAlternatedDeclaration(leftListType, rightListType);

                this.listStack.push(new DoubleListDescriptor(
                        DoubleListDescriptor.Type.ALTERNATED, listName,
                        leftListType, rightListType));
            }
            else {
                throw new InternalException("Unhandle type " + node.getClass());
            }

        }

        this.macroStack.push(newList);

        for (SAlternativeTransformationListElement e : node.getElements()) {
            e.apply(this);
        }

        this.listStack.pop();
        newList = (MNewList) this.macroStack.pop();

        newList.newStringParameter(node.getType().getCardinality()
                .getLowerBound().getValue()
                + "");

        if (!node.getType().getCardinality().upperBoundIsInfinite()) {
            newList.newStringParameter(node.getType().getCardinality()
                    .getUpperBound().getValue()
                    + "");
        }

        if (currentMacro instanceof MReduceDecision) {
            ((MReduceDecision) currentMacro).newAddNToForest(listName);
        }
        else if (currentMacro instanceof MNewTreeClass) {
            ((MNewTreeClass) currentMacro).newNewParameter(listName);
        }
        else {
            throw new InternalException("Unhandled " + currentMacro.getClass());
        }

    }

    @Override
    public void visitNullElement(
            SAlternativeTransformationElement.NullElement node) {

        Object currentMacro = this.macroStack.peek();

        if (currentMacro instanceof MReduceDecision) {

            ((MReduceDecision) currentMacro).newAddNullToForest();

        }
        else if (currentMacro instanceof MNewTreeClass) {
            ((MNewTreeClass) currentMacro).newNullParameter();
        }
        else {
            throw new InternalException("Unhandled " + currentMacro.getClass());
        }

    }

    @Override
    public void visitReferenceListElement(
            SAlternativeTransformationListElement.ReferenceElement node) {

        MNewList list = (MNewList) this.macroStack.peek();

        if (node.getTargetReference() instanceof Element) {
            String elementName = this.reducedAlternative.getElement(
                    (Element) node.getTargetReference()).getName();
            String elementType = to_CamelCase(this.reducedAlternative
                    .getElement((Element) node.getTargetReference())
                    .getTypeName());

            if (this.listStack.peek() instanceof NormalListDescriptor) {
                list.newAddPopElement(this.listStack.peek().getListName(),
                        elementName, elementType, "0");
            }
            else {
                DoubleListDescriptor listDescriptor = (DoubleListDescriptor) this.listStack
                        .peek();

                if (elementType.equals(listDescriptor.getLeftListType())) {
                    list.newAddPopElementLeft(this.listStack.peek()
                            .getListName(), elementName, elementType, "0");
                }
                else {
                    list.newAddPopElementRight(this.listStack.peek()
                            .getListName(), elementName, elementType, "0");
                }
            }

        }
        else if (node.getTargetReference() instanceof SProductionTransformationElement) {

            SProductionTransformationElement reference = (SProductionTransformationElement) node
                    .getTargetReference();

            String elementName = this.grammar.getSimplifiedGrammar()
                    .getOldElement(node.getOriginReference()).getName();
            String listName = this.listStack.peek().getListName();
            String elementType;

            if (this.listStack.peek() instanceof NormalListDescriptor) {
                elementType = ((NormalListDescriptor) this.listStack.peek())
                        .getListType();
            }
            else {

                if (reference instanceof SProductionTransformationElement.NormalElement) {

                    SProductionTransformationElement.NormalElement normalElement = (SProductionTransformationElement.NormalElement) reference;

                    if (normalElement.getCoreReference() instanceof Tree.TreeProduction) {
                        elementType = ((Tree.TreeProduction) normalElement
                                .getCoreReference()).getName_CamelCase();

                    }
                    else if (normalElement.getCoreReference() instanceof LexerExpression.NamedExpression) {
                        elementType = ((LexerExpression.NamedExpression) normalElement
                                .getCoreReference()).getName_CamelCase();
                    }
                    else if (normalElement.getCoreReference() instanceof LexerExpression.InlineExpression) {
                        elementType = ((LexerExpression.InlineExpression) normalElement
                                .getCoreReference())
                                .getInternalName_CamelCase();
                    }
                    else {
                        throw new InternalException("unhandle case");
                    }
                }
                else {
                    throw new InternalException("Unexpected reference "
                            + reference.getClass());
                }
            }

            list.newAddPopElement(listName, elementName, elementType,
                    reference.getIndex() + "");

        }
        else {
            throw new InternalException("Unhandled " + node.getClass());

        }

    }

    @Override
    public void visitNewListElement(
            SAlternativeTransformationListElement.NewElement node) {

        String alt_CamelCase = this.alternativeToCamelFullName.get(node
                .getAlternative());

        String elementName = getNextVarId(alt_CamelCase);

        MNewList list = (MNewList) this.macroStack.peek();
        MNewTreeClass newMacro = list.newNewTreeClass(alt_CamelCase,
                elementName);

        this.macroStack.push(newMacro);

        for (SAlternativeTransformationElement e : node.getElements()) {
            e.apply(this);
        }

        this.macroStack.pop();

        list.newAddNewElement(this.listStack.peek().getListName(), elementName);

    }

    @Override
    public void visitNormalListListElement(
            SAlternativeTransformationListElement.NormalListElement node) {

        MNewList list = (MNewList) this.macroStack.peek();

        String elementName = to_camelCase(this.grammar.getSimplifiedGrammar()
                .getOldElement(node.getOriginReference()).getName());

        if (this.listStack.peek() instanceof NormalListDescriptor) {

            if (node.getTargetReference() instanceof SProductionTransformationElement.NormalElement) {
                String elementType = ((NormalListDescriptor) this.listStack
                        .peek()).getListType();

                list.newAddPopList(this.listStack.peek().getListName(),
                        elementName, elementType, node.getTargetReference()
                                .getIndex() + "");

            }
            else {
                throw new InternalException("Unexpected case");
            }
        }
        else {
            DoubleListDescriptor listDescriptor = (DoubleListDescriptor) this.listStack
                    .peek();

            if (node.getTargetReference() instanceof SProductionTransformationElement.SeparatedElement) {

                SProductionTransformationElement.SeparatedElement separatedElement = (SProductionTransformationElement.SeparatedElement) node
                        .getTargetReference();

                String leftElementTransformationName = computeElementType(separatedElement
                        .getLeftTreeReference());

                if (leftElementTransformationName.equals(listDescriptor
                        .getLeftListType())) {
                    list.newAddPopSeparatedList(listDescriptor.getListName(),
                            elementName, listDescriptor.getLeftListType(),
                            listDescriptor.getRightListType(), node
                                    .getTargetReference().getIndex() + "");

                }
                else {
                    list.newAddPopReverseSeparatedList(
                            listDescriptor.getListName(), elementName,
                            listDescriptor.getRightListType(),
                            listDescriptor.getLeftListType(), node
                                    .getTargetReference().getIndex() + "");
                }

            }
            else if (node.getTargetReference() instanceof SProductionTransformationElement.AlternatedElement) {

                SProductionTransformationElement.AlternatedElement alternatedElement = (SProductionTransformationElement.AlternatedElement) node
                        .getTargetReference();

                String leftElementTransformationName = computeElementType(alternatedElement
                        .getLeftTreeReference());

                if (leftElementTransformationName.equals(listDescriptor
                        .getLeftListType())) {
                    list.newAddPopAlternatedList(listDescriptor.getListName(),
                            elementName, listDescriptor.getLeftListType(),
                            listDescriptor.getRightListType(), node
                                    .getTargetReference().getIndex() + "");

                }
                else {
                    list.newAddPopReverseAlternatedList(
                            listDescriptor.getListName(), elementName,
                            listDescriptor.getRightListType(),
                            listDescriptor.getLeftListType(), node
                                    .getTargetReference().getIndex() + "");

                }

            }
            else {
                throw new InternalException("Unexpected case");
            }

        }

    }

    @Override
    public void visitLeftListListElement(
            SAlternativeTransformationListElement.LeftListElement node) {

        MNewList list = (MNewList) this.macroStack.peek();

        if (this.listStack.peek() instanceof NormalListDescriptor) {

            String leftElementType;
            String rightElementType;
            String elementName = to_camelCase(this.grammar
                    .getSimplifiedGrammar()
                    .getOldElement(node.getOriginReference()).getName());

            if (node.getTargetReference() instanceof SProductionTransformationElement.AlternatedElement) {
                SProductionTransformationElement.AlternatedElement transformationElement = (SProductionTransformationElement.AlternatedElement) node
                        .getTargetReference();

                leftElementType = computeElementType(transformationElement
                        .getLeftTreeReference());
                rightElementType = computeElementType(transformationElement
                        .getRightTreeReference());

                MAddPopAlternatedList mAddPopAlternatedList = list
                        .newAddPopAlternatedList(this.listStack.peek()
                                .getListName(), elementName, leftElementType,
                                rightElementType,
                                transformationElement.getIndex() + "");
                mAddPopAlternatedList.newGetLeft();
            }
            else if (node.getTargetReference() instanceof SProductionTransformationElement.SeparatedElement) {
                SProductionTransformationElement.SeparatedElement transformationElement = (SProductionTransformationElement.SeparatedElement) node
                        .getTargetReference();

                leftElementType = computeElementType(transformationElement
                        .getLeftTreeReference());
                rightElementType = computeElementType(transformationElement
                        .getRightTreeReference());

                MAddPopSeparatedList mAddPopSeparatedList = list
                        .newAddPopSeparatedList(this.listStack.peek()
                                .getListName(), elementName, leftElementType,
                                rightElementType,
                                transformationElement.getIndex() + "");
                mAddPopSeparatedList.newGetLeft();
            }
            else {
                throw new InternalException("Unexpected case");
            }
        }

    }

    @Override
    public void visitRightListListElement(
            SAlternativeTransformationListElement.RightListElement node) {

        MNewList list = (MNewList) this.macroStack.peek();

        if (this.listStack.peek() instanceof NormalListDescriptor) {

            String leftElementType;
            String rightElementType;
            String elementName = to_camelCase(this.grammar
                    .getSimplifiedGrammar()
                    .getOldElement(node.getOriginReference()).getName());

            if (node.getTargetReference() instanceof SProductionTransformationElement.AlternatedElement) {
                SProductionTransformationElement.AlternatedElement transformationElement = (SProductionTransformationElement.AlternatedElement) node
                        .getTargetReference();

                leftElementType = computeElementType(transformationElement
                        .getLeftTreeReference());
                rightElementType = computeElementType(transformationElement
                        .getRightTreeReference());

                MAddPopAlternatedList mAddPopAlternatedList = list
                        .newAddPopAlternatedList(this.listStack.peek()
                                .getListName(), elementName, leftElementType,
                                rightElementType,
                                transformationElement.getIndex() + "");
                mAddPopAlternatedList.newGetRight();
            }
            else if (node.getTargetReference() instanceof SProductionTransformationElement.SeparatedElement) {
                SProductionTransformationElement.SeparatedElement transformationElement = (SProductionTransformationElement.SeparatedElement) node
                        .getTargetReference();

                leftElementType = computeElementType(transformationElement
                        .getLeftTreeReference());
                rightElementType = computeElementType(transformationElement
                        .getRightTreeReference());

                MAddPopSeparatedList mAddPopSeparatedList = list
                        .newAddPopSeparatedList(this.listStack.peek()
                                .getListName(), elementName, leftElementType,
                                rightElementType,
                                transformationElement.getIndex() + "");
                mAddPopSeparatedList.newGetRight();
            }
            else {
                throw new InternalException("Unexpected case");
            }
        }

    }

    private static abstract class ListDescriptor {

        private final String listName;

        public ListDescriptor(
                String listName) {

            this.listName = listName;
        }

        public String getListName() {

            return this.listName;
        }
    }

    private static class NormalListDescriptor
            extends ListDescriptor {

        private final String listType;

        public NormalListDescriptor(
                String listName,
                String listType) {

            super(listName);
            this.listType = listType;
        }

        public String getListType() {

            return this.listType;
        }
    }

    private static class DoubleListDescriptor
            extends ListDescriptor {

        private final String leftListType;

        private final String rightListType;

        private Type type;

        public static enum Type {
            SEPARATED,
            ALTERNATED
        };

        public DoubleListDescriptor(
                Type type,
                String listName,
                String leftlistType,
                String rightlistType) {

            super(listName);
            this.type = type;
            this.leftListType = leftlistType;
            this.rightListType = rightlistType;
        }

        public String getLeftListType() {

            return this.leftListType;
        }

        public String getRightListType() {

            return this.rightListType;
        }

        public Type getType() {

            return this.type;
        }
    }

}
