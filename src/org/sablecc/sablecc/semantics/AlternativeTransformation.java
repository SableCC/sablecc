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

import java.util.*;

import org.sablecc.sablecc.syntax3.node.*;

public class AlternativeTransformation {

    private Grammar grammar;

    private AAlternativeTransformation declaration;

    private AlternativeReference alternativeReference;

    private AlternativeTransformation(
            Grammar grammar,
            AAlternativeTransformation declaration) {

        this.grammar = grammar;
        this.declaration = declaration;
        this.alternativeReference = grammar
                .getAlternativeReferenceResolution(declaration
                        .getAlternativeReference());
    }

    public Token getLocation() {

        return this.alternativeReference.getLocation();
    }

    static void createDeclaredAlternativeTransformation(
            Grammar grammar,
            AAlternativeTransformation node) {

        AlternativeTransformation alternativeTransformation = new AlternativeTransformation(
                grammar, node);

        alternativeTransformation.checkElementReferences();
        alternativeTransformation.createDeclaredTransformationElements();

        alternativeTransformation.alternativeReference.getAlternative()
                .setDeclaredTransformation(alternativeTransformation);
    }

    private void checkElementReferences() {

        // collect element references
        final List<ElementReference> elementReferences = new LinkedList<ElementReference>();
        this.declaration.apply(new TreeWalker() {

            @Override
            public void caseANaturalElementReference(
                    ANaturalElementReference node) {

                AlternativeTransformation.this.grammar
                        .resolveElementReference(node);
                elementReferences.add(AlternativeTransformation.this.grammar
                        .getElementReferenceResolution(node));
            }

            @Override
            public void caseATransformedElementReference(
                    ATransformedElementReference node) {

                AlternativeTransformation.this.grammar
                        .resolveElementReference(node);
                elementReferences.add(AlternativeTransformation.this.grammar
                        .getElementReferenceResolution(node));
            }
        });

        // check that element references are identical to elements of the
        // transformed alternative
        Iterator<ElementReference> elementReferenceIterator = elementReferences
                .iterator();
        for (Element element : this.alternativeReference.getAlternative()
                .getElements()) {

            Type type = element.getType();
            Declaration base = type.getBase();
            Declaration separator = type.getSeparator();

            if (separator != null) {
                // separated type
                simpleMatch(type, elementReferenceIterator, element);
            }
            else if (base instanceof Production) {
                // non-separated type
                ProductionTransformation productionTransformation = ((Production) base)
                        .getTransformation();
                ArrayList<Type> types = productionTransformation.getSignature()
                        .getTypes();
                if (types.size() == 0) {
                    simpleMatch(type, elementReferenceIterator, element);
                }
                else if (types.size() == 1 && types.get(0).isSimple()) {
                    simpleMatch(type, elementReferenceIterator, element);
                }
                else {
                    for (Type subtreeType : types) {
                        if (!elementReferenceIterator.hasNext()) {
                            throw SemanticException.semanticError(
                                    "Expecting : " + type + "." + subtreeType,
                                    this.declaration.getSemicolon());
                        }
                        ElementReference elementReference = elementReferenceIterator
                                .next();
                        if (elementReference.getSubtree() == null) {
                            throw SemanticException.semanticError(
                                    "Expecting : " + type + "." + subtreeType,
                                    elementReference.getLocation());
                        }
                        if (!type.equals(this.grammar
                                .getTypeResolution(elementReference
                                        .getElementBody()))) {
                            throw SemanticException.semanticError(
                                    "Expecting : " + type + "." + subtreeType,
                                    elementReference.getLocation());
                        }
                        if (!subtreeType.equals(this.grammar
                                .getTypeResolution(elementReference
                                        .getSubtree()))) {
                            throw SemanticException.semanticError(
                                    "Expecting : " + type + "." + subtreeType,
                                    elementReference.getLocation());
                        }

                        elementReference.associateTo(element);
                    }
                }
            }
            else {
                simpleMatch(type, elementReferenceIterator, element);
            }
        }

        if (elementReferenceIterator.hasNext()) {
            ElementReference elementReference = elementReferenceIterator.next();
            throw SemanticException.semanticError(
                    "Unexpected extra element reference.",
                    elementReference.getLocation());
        }
    }

    private void simpleMatch(
            Type type,
            Iterator<ElementReference> elementReferenceIterator,
            Element element) {

        if (!elementReferenceIterator.hasNext()) {
            throw SemanticException.semanticError("Expecting : " + type,
                    this.declaration.getSemicolon());
        }
        ElementReference elementReference = elementReferenceIterator.next();
        if (elementReference.getSubtree() != null) {
            throw SemanticException.semanticError("Expecting : " + type,
                    elementReference.getLocation());
        }
        if (!type.equals(this.grammar.getTypeResolution(elementReference
                .getElementBody()))) {
            throw SemanticException.semanticError("Expecting : " + type,
                    elementReference.getLocation());
        }

        elementReference.associateTo(element);
    }

    private void createDeclaredTransformationElements() {

        // create the transformation elements
        this.declaration.apply(new TreeWalker() {

            @Override
            public void outANullTransformationElement(
                    ANullTransformationElement node) {

                AlternativeTransformation.this.grammar
                        .resolveTransformationElement(node);
            }

            @Override
            public void outAReferenceTransformationElement(
                    AReferenceTransformationElement node) {

                AlternativeTransformation.this.grammar
                        .resolveTransformationElement(node);
            }

            @Override
            public void outADeleteTransformationElement(
                    ADeleteTransformationElement node) {

                AlternativeTransformation.this.grammar
                        .resolveTransformationElement(node);
            }

            @Override
            public void outANewTransformationElement(
                    ANewTransformationElement node) {

                AlternativeTransformation.this.grammar
                        .resolveTransformationElement(node);
            }

            @Override
            public void outAListTransformationElement(
                    AListTransformationElement node) {

                AlternativeTransformation.this.grammar
                        .resolveTransformationElement(node);
            }

            @Override
            public void outALeftTransformationElement(
                    ALeftTransformationElement node) {

                AlternativeTransformation.this.grammar
                        .resolveTransformationElement(node);
            }

            @Override
            public void outARightTransformationElement(
                    ARightTransformationElement node) {

                AlternativeTransformation.this.grammar
                        .resolveTransformationElement(node);
            }
        });

        // check compatibility with production transformation
        ProductionTransformation productionTransformation = this.alternativeReference
                .getAlternative().getProduction().getTransformation();
        Iterator<Type> signatureTypeIterator = productionTransformation
                .getSignature().getTypes().iterator();

        for (PTransformationElement pTransformationElement : this.declaration
                .getTransformationElements()) {
            TransformationElement transformationElement = this.grammar
                    .getTransformationElementResolution(pTransformationElement);
            Type transformationElementType = transformationElement.getType();

            // skip deleted elements (type == null)
            if (transformationElementType != null) {
                if (!signatureTypeIterator.hasNext()) {
                    throw SemanticException.semanticError(
                            "Unexpected extra transformation element.",
                            transformationElement.getLocation());
                }
                Type signatureType = signatureTypeIterator.next();
                if (!transformationElementType.isAssignableTo(signatureType)) {
                    throw SemanticException.semanticError(
                            "Expecting a tranformation element of type "
                                    + signatureType + ", instead of "
                                    + transformationElementType + ".",
                            transformationElement.getLocation());
                }
            }
        }

        if (signatureTypeIterator.hasNext()) {
            Type signatureType = signatureTypeIterator.next();
            throw SemanticException.semanticError(
                    "Expecting a tranformation element of type "
                            + signatureType + ".",
                    this.declaration.getSemicolon());
        }

    }
}
