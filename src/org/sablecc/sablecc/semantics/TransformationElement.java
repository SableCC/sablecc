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

package org.sablecc.sablecc.semantics;

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class TransformationElement {

    Grammar grammar;

    private Type type;

    private TransformationElement(
            Grammar grammar) {

        this.grammar = grammar;
    }

    static TransformationElement createDeclaredNullTransformationElement(
            Grammar grammar,
            ANullTransformationElement declaration) {

        return new NullTransformation(grammar, declaration);
    }

    static TransformationElement createDeclaredReferenceTransformationElement(
            Grammar grammar,
            AReferenceTransformationElement declaration) {

        return new ReferenceTransformation(grammar, declaration);
    }

    static TransformationElement createDeclaredDeleteTransformationElement(
            Grammar grammar,
            ADeleteTransformationElement declaration) {

        return new DeleteTransformation(grammar, declaration);
    }

    static TransformationElement createDeclaredNewTransformationElement(
            Grammar grammar,
            ANewTransformationElement declaration) {

        return new NewTransformation(grammar, declaration);
    }

    static TransformationElement createDeclaredListTransformationElement(
            Grammar grammar,
            AListTransformationElement declaration) {

        return new ListTransformation(grammar, declaration);
    }

    static TransformationElement createDeclaredLeftTransformationElement(
            Grammar grammar,
            ALeftTransformationElement declaration) {

        return new LeftTransformation(grammar, declaration);
    }

    static TransformationElement createDeclaredRightTransformationElement(
            Grammar grammar,
            ARightTransformationElement declaration) {

        return new RightTransformation(grammar, declaration);
    }

    void setType(
            Type type) {

        this.type = type;
    }

    public Type getType() {

        return this.type;
    }

    public abstract Token getLocation();

    public static class NullTransformation
            extends TransformationElement {

        private ANullTransformationElement declaration;

        private Token location;

        private NullTransformation(
                Grammar grammar,
                ANullTransformationElement declaration) {

            super(grammar);
            this.declaration = declaration;

            this.location = this.declaration.getNullKeyword();

            computeType();
        }

        private void computeType() {

            setType(new Type(false, null, null, BigInteger.ZERO,
                    BigInteger.ZERO));
        }

        @Override
        public Token getLocation() {

            if (this.declaration == null) {
                throw new InternalException("synthetic transformation element");
            }

            return this.location;
        }
    }

    public static class ReferenceTransformation
            extends TransformationElement {

        private AReferenceTransformationElement declaration;

        private Token location;

        private ElementReference elementReference;

        private ReferenceTransformation(
                Grammar grammar,
                AReferenceTransformationElement declaration) {

            super(grammar);
            this.declaration = declaration;

            this.elementReference = this.grammar
                    .getElementReferenceResolution(this.declaration
                            .getElementReference());

            this.location = this.elementReference.getLocation();

            Element element = this.elementReference.getAssociateParserElement();
            element.addReferenceTransformation(this);

            computeType();
        }

        private void computeType() {

            setType(this.elementReference.getType());
        }

        @Override
        public Token getLocation() {

            if (this.declaration == null) {
                throw new InternalException("synthetic transformation element");
            }

            return this.location;
        }
    }

    public static class DeleteTransformation
            extends TransformationElement {

        private ADeleteTransformationElement declaration;

        private Token location;

        private final List<TransformationElement> children = new LinkedList<TransformationElement>();

        private DeleteTransformation(
                Grammar grammar,
                ADeleteTransformationElement declaration) {

            super(grammar);
            this.declaration = declaration;

            this.location = this.declaration.getDeleteKeyword();

            for (PTransformationElement pTransformationElement : this.declaration
                    .getTransformationElements()) {
                TransformationElement transformationElement = this.grammar
                        .getTransformationElementResolution(pTransformationElement);
                this.children.add(transformationElement);
            }

            computeType();
        }

        private void computeType() {

            setType(null);
        }

        @Override
        public Token getLocation() {

            if (this.declaration == null) {
                throw new InternalException("synthetic transformation element");
            }

            return this.location;
        }
    }

    public static class NewTransformation
            extends TransformationElement {

        private ANewTransformationElement declaration;

        private Token location;

        private Alternative alternative;

        private final List<TransformationElement> children = new LinkedList<TransformationElement>();

        private NewTransformation(
                Grammar grammar,
                ANewTransformationElement declaration) {

            super(grammar);
            this.declaration = declaration;

            this.location = this.declaration.getNewKeyword();

            AlternativeReference alternativeReference = this.grammar
                    .getAlternativeReferenceResolution(this.declaration
                            .getAlternativeReference());
            this.alternative = alternativeReference.getAlternative();

            for (PTransformationElement pTransformationElement : this.declaration
                    .getTransformationElements()) {
                TransformationElement transformationElement = this.grammar
                        .getTransformationElementResolution(pTransformationElement);
                this.children.add(transformationElement);
            }

            computeType();
        }

        private void computeType() {

            Iterator<Element> elementIterator = this.alternative.getElements()
                    .iterator();
            for (TransformationElement transformationElement : this.children) {
                Type transformationElementType = transformationElement
                        .getType();
                // skip deleted elements (type == null)
                if (transformationElementType != null) {
                    if (!elementIterator.hasNext()) {
                        throw SemanticException.semanticError(
                                "Unexpected extra transformation element.",
                                transformationElement.getLocation());
                    }
                    Element element = elementIterator.next();
                    Type elementType = element.getType();
                    if (!transformationElementType.isAssignableTo(elementType)) {
                        throw SemanticException.semanticError(
                                "Expecting a tranformation element of type "
                                        + elementType + ", instead of "
                                        + transformationElementType + ".",
                                transformationElement.getLocation());
                    }
                }
            }

            if (elementIterator.hasNext()) {
                Element element = elementIterator.next();
                Type elementType = element.getType();
                throw SemanticException
                        .semanticError(
                                "Expecting a tranformation element of type "
                                        + elementType + ".",
                                this.declaration.getRPar());
            }

            setType(new Type(false, this.alternative.getProduction(), null,
                    BigInteger.ONE, BigInteger.ONE));
        }

        @Override
        public Token getLocation() {

            if (this.declaration == null) {
                throw new InternalException("synthetic transformation element");
            }

            return this.location;
        }
    }

    public static class ListTransformation
            extends TransformationElement {

        private AListTransformationElement declaration;

        private Token location;

        private final List<TransformationElement> children = new LinkedList<TransformationElement>();

        private ListTransformation(
                Grammar grammar,
                AListTransformationElement declaration) {

            super(grammar);
            this.declaration = declaration;

            this.location = this.declaration.getListKeyword();

            for (PTransformationElement pTransformationElement : this.declaration
                    .getTransformationElements()) {
                TransformationElement transformationElement = this.grammar
                        .getTransformationElementResolution(pTransformationElement);
                this.children.add(transformationElement);
            }

            computeType();
        }

        private void computeType() {

            Declaration base = null;
            Declaration separator = null;
            BigInteger minMultiplicity = null;
            BigInteger maxMultiplicity = null;

            boolean baseAndSeparatorIdentified = false;
            boolean expectingTrailingBase = false;

            for (TransformationElement child : this.children) {

                Type childType = child.getType();

                // skip deleted elements, nulls, and empty lists
                if (childType != null && childType.getBase() != null) {

                    Declaration childBase = childType.getBase();
                    Declaration childSeparator = childType.getSeparator();
                    BigInteger childMinMultiplicity = childType
                            .getMinMultiplicity();
                    BigInteger childMaxMultiplicity = childType
                            .getMaxMultiplicity();

                    if (baseAndSeparatorIdentified) {

                        if (separator == null) {
                            // non-separated list

                            if (childBase != base || childSeparator != null) {
                                throw SemanticException.semanticError(
                                        "The tranformation element is incompatible with the "
                                                + new Type(true, base,
                                                        separator,
                                                        minMultiplicity,
                                                        maxMultiplicity)
                                                + " prefix.", child
                                                .getLocation());
                            }

                            minMultiplicity = minMultiplicity
                                    .add(childMinMultiplicity);
                            if (childMaxMultiplicity == null) {
                                maxMultiplicity = null;
                            }
                            else if (maxMultiplicity != null) {
                                maxMultiplicity = maxMultiplicity
                                        .add(childMaxMultiplicity);
                            }
                        }
                        else if (expectingTrailingBase) {
                            // separated list

                            expectingTrailingBase = false;

                            if (childMinMultiplicity.equals(BigInteger.ZERO)
                                    || childBase != base
                                    || childSeparator != null
                                    && childSeparator != separator
                                    || childSeparator == null
                                    && !childMaxMultiplicity
                                            .equals(BigInteger.ONE)) {
                                throw SemanticException.semanticError(
                                        "The tranformation element is incompatible with the "
                                                + new Type(true, base,
                                                        separator,
                                                        minMultiplicity,
                                                        maxMultiplicity)
                                                + " prefix.", child
                                                .getLocation());
                            }

                            minMultiplicity = minMultiplicity
                                    .add(childMinMultiplicity
                                            .subtract(BigInteger.ONE));
                            if (childMaxMultiplicity == null) {
                                maxMultiplicity = null;
                            }
                            else if (maxMultiplicity != null) {
                                maxMultiplicity = maxMultiplicity
                                        .add(childMaxMultiplicity
                                                .subtract(BigInteger.ONE));
                            }
                        }
                        else {
                            // separated list

                            // expecting separator

                            expectingTrailingBase = true;

                            if (childMinMultiplicity.equals(BigInteger.ZERO)
                                    || childBase != separator
                                    || childSeparator != null
                                    && childSeparator != base
                                    || childSeparator == null
                                    && !childMaxMultiplicity
                                            .equals(BigInteger.ONE)) {
                                throw SemanticException.semanticError(
                                        "The tranformation element is incompatible with the "
                                                + new Type(true, base,
                                                        separator,
                                                        minMultiplicity,
                                                        maxMultiplicity)
                                                + " prefix.", child
                                                .getLocation());
                            }

                            minMultiplicity = minMultiplicity
                                    .add(childMinMultiplicity);
                            if (childMaxMultiplicity == null) {
                                maxMultiplicity = null;
                            }
                            else if (maxMultiplicity != null) {
                                maxMultiplicity = maxMultiplicity
                                        .add(childMaxMultiplicity);
                            }
                        }
                    }
                    else if (base != null) {
                        // base and separator have not been identified, yet

                        // base != null
                        // separator == null
                        // minMultiplicity == 1
                        // maxMultiplicity == 1

                        if (childBase == base) {
                            // it's a non-separated list

                            baseAndSeparatorIdentified = true;

                            if (childSeparator != null) {
                                throw SemanticException.semanticError(
                                        "The tranformation element is incompatible with the "
                                                + new Type(true, base,
                                                        separator,
                                                        minMultiplicity,
                                                        maxMultiplicity)
                                                + " prefix.", child
                                                .getLocation());
                            }

                            minMultiplicity = minMultiplicity
                                    .add(childMinMultiplicity);

                            if (childMaxMultiplicity == null) {
                                maxMultiplicity = null;
                            }
                            else if (maxMultiplicity != null) {
                                maxMultiplicity = maxMultiplicity
                                        .add(childMaxMultiplicity);
                            }
                        }
                        else if (childSeparator == null) {
                            // it's a separated list

                            baseAndSeparatorIdentified = true;
                            expectingTrailingBase = true;
                            separator = childBase;

                            // childBase != base

                            if (!childMinMultiplicity.equals(BigInteger.ONE)
                                    || childMaxMultiplicity == null
                                    || !childMaxMultiplicity
                                            .equals(BigInteger.ONE)) {
                                throw SemanticException.semanticError(
                                        "The tranformation element is incompatible with the "
                                                + new Type(true, base,
                                                        separator,
                                                        minMultiplicity,
                                                        maxMultiplicity)
                                                + " prefix.", child
                                                .getLocation());
                            }

                            minMultiplicity = minMultiplicity
                                    .add(BigInteger.ONE);
                            maxMultiplicity = maxMultiplicity
                                    .add(BigInteger.ONE);
                        }
                        else {
                            // it's a separated list

                            baseAndSeparatorIdentified = true;
                            expectingTrailingBase = true;
                            separator = childBase;

                            // childBase != base
                            // childSeparator != null

                            if (childMinMultiplicity.equals(BigInteger.ZERO)
                                    || childSeparator != base) {
                                throw SemanticException.semanticError(
                                        "The tranformation element is incompatible with the "
                                                + new Type(true, base,
                                                        separator,
                                                        minMultiplicity,
                                                        maxMultiplicity)
                                                + " prefix.", child
                                                .getLocation());
                            }

                            minMultiplicity = minMultiplicity
                                    .add(childMinMultiplicity);
                            if (childMaxMultiplicity == null) {
                                maxMultiplicity = null;
                            }
                            else {
                                maxMultiplicity = maxMultiplicity
                                        .add(childMaxMultiplicity);
                            }
                        }
                    }
                    else {
                        // base and separator have not been identified, yet

                        // base == null
                        // separator == null
                        // minMultiplicity == null
                        // maxMultiplicity == null

                        base = childBase;
                        separator = childSeparator;
                        minMultiplicity = childMinMultiplicity;
                        maxMultiplicity = childMaxMultiplicity;

                        // a non-separated list of one element is ambiguous
                        if (separator == null
                                && minMultiplicity.equals(BigInteger.ONE)
                                && maxMultiplicity != null
                                && maxMultiplicity.equals(BigInteger.ONE)) {
                            baseAndSeparatorIdentified = false;
                        }
                        else {
                            baseAndSeparatorIdentified = true;
                        }
                    }
                }
            }

            if (expectingTrailingBase) {
                throw SemanticException.semanticError(
                        "Expecting a tranformation element of type "
                                + new Type(false, base, null, BigInteger.ONE,
                                        BigInteger.ONE) + ".",
                        this.declaration.getRPar());
            }

            if (base == null) {
                minMultiplicity = BigInteger.ZERO;
                maxMultiplicity = BigInteger.ZERO;
            }

            setType(new Type(true, base, separator, minMultiplicity,
                    maxMultiplicity));
        }

        @Override
        public Token getLocation() {

            if (this.declaration == null) {
                throw new InternalException("synthetic transformation element");
            }

            return this.location;
        }
    }

    public static class LeftTransformation
            extends TransformationElement {

        private ALeftTransformationElement declaration;

        private Token location;

        private final TransformationElement child;

        private LeftTransformation(
                Grammar grammar,
                ALeftTransformationElement declaration) {

            super(grammar);
            this.declaration = declaration;

            this.location = this.declaration.getLeftKeyword();

            this.child = this.grammar
                    .getTransformationElementResolution(declaration
                            .getTransformationElement());

            computeType();
        }

        private void computeType() {

            Type childType = this.child.getType();
            if (!childType.isList() || childType.getSeparator() == null) {
                throw SemanticException
                        .semanticError("Expecting a separated list.",
                                this.child.getLocation());
            }
            setType(new Type(true, childType.getBase(), null,
                    childType.getMinMultiplicity(),
                    childType.getMaxMultiplicity()));
        }

        @Override
        public Token getLocation() {

            if (this.declaration == null) {
                throw new InternalException("synthetic transformation element");
            }

            return this.location;
        }
    }

    public static class RightTransformation
            extends TransformationElement {

        private ARightTransformationElement declaration;

        private Token location;

        private final TransformationElement child;

        private RightTransformation(
                Grammar grammar,
                ARightTransformationElement declaration) {

            super(grammar);
            this.declaration = declaration;

            this.location = this.declaration.getRightKeyword();

            this.child = this.grammar
                    .getTransformationElementResolution(declaration
                            .getTransformationElement());

            computeType();
        }

        private void computeType() {

            Type childType = this.child.getType();
            if (!childType.isList() || childType.getSeparator() == null) {
                throw SemanticException
                        .semanticError("Expecting a separated list.",
                                this.child.getLocation());
            }

            BigInteger min = childType.getMinMultiplicity();
            if (min.compareTo(BigInteger.ZERO) > 0) {
                min = min.subtract(BigInteger.ONE);
            }

            BigInteger max = childType.getMaxMultiplicity();
            if (max != null && max.compareTo(BigInteger.ZERO) > 0) {
                max = max.subtract(BigInteger.ONE);
            }

            setType(new Type(true, childType.getSeparator(), null, min, max));
        }

        @Override
        public Token getLocation() {

            if (this.declaration == null) {
                throw new InternalException("synthetic transformation element");
            }

            return this.location;
        }
    }
}
