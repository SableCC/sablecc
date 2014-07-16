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

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Grammar
        extends Declaration {

    private AGrammar declaration;

    private Map<Node, Object> nodeMap = new HashMap<Node, Object>();

    private Map<TIdentifier, Declaration> declarationResolutionMap = new HashMap<TIdentifier, Declaration>();

    private Map<Token, Expression> inlinedExpressionResolutionMap = new HashMap<Token, Expression>();

    private Map<TIdentifier, Alternative> alternativeResolutionMap = new HashMap<TIdentifier, Alternative>();

    private Map<PAlternativeReference, AlternativeReference> alternativeReferenceResolutionMap = new HashMap<PAlternativeReference, AlternativeReference>();

    private Map<PElementReference, ElementReference> elementReferenceResolutionMap = new HashMap<PElementReference, ElementReference>();

    private Map<PElementBody, Type> typeResolutionMap = new HashMap<PElementBody, Type>();

    private Map<PTransformationElement, TransformationElement> transformationElementMap = new HashMap<PTransformationElement, TransformationElement>();

    private NameSpace parserNameSpace = new NameSpace();

    private NameSpace treeNameSpace = new NameSpace();

    private int nextInternalNameIndex = 1;

    // Cached values

    private String name;

    private Token location;

    Grammar(
            AGrammar declaration) {

        this.declaration = declaration;
        if (this.nodeMap.containsKey(declaration)) {
            throw new InternalException("it was already added.");
        }
        this.nodeMap.put(declaration, this);
        this.parserNameSpace.add(this);
        this.treeNameSpace.add(this);
    }

    @Override
    public String getName() {

        if (this.name == null) {
            this.name = getLocation().getText();
        }

        return this.name;
    }

    @Override
    public String getLookupName() {

        return getName();
    }

    @Override
    public String getDisplayName() {

        return getLocation().getText();
    }

    @Override
    public Token getLocation() {

        if (this.location == null) {
            this.location = this.declaration.getName();
        }

        return this.location;
    }

    public Expression getExpression(
            Node declaration) {

        return (Expression) this.nodeMap.get(declaration);
    }

    public Production getProduction(
            PParserProduction declaration) {

        return (Production) this.nodeMap.get(declaration);
    }

    public Production getProduction(
            PTreeProduction declaration) {

        return (Production) this.nodeMap.get(declaration);
    }

    public Alternative getAlternative(
            PAlternative declaration) {

        return (Alternative) this.nodeMap.get(declaration);
    }

    public Element getElement(
            PElement declaration) {

        return (Element) this.nodeMap.get(declaration);
    }

    public Declaration getDeclarationResolution(
            TIdentifier identifier) {

        return this.declarationResolutionMap.get(identifier);
    }

    public Expression getExpressionResolution(
            TIdentifierChar node) {

        return this.inlinedExpressionResolutionMap.get(node);
    }

    public Expression getExpressionResolution(
            TChar node) {

        return this.inlinedExpressionResolutionMap.get(node);
    }

    public Expression getExpressionResolution(
            TIdentifierString node) {

        return this.inlinedExpressionResolutionMap.get(node);
    }

    public Expression getExpressionResolution(
            TString node) {

        return this.inlinedExpressionResolutionMap.get(node);
    }

    public Expression getExpressionResolution(
            TEndKeyword node) {

        return this.inlinedExpressionResolutionMap.get(node);
    }

    public Alternative getAlternativeResolution(
            TIdentifier identifier) {

        return this.alternativeResolutionMap.get(identifier);
    }

    public AlternativeReference getAlternativeReferenceResolution(
            PAlternativeReference alternativeReference) {

        return this.alternativeReferenceResolutionMap.get(alternativeReference);
    }

    public ElementReference getElementReferenceResolution(
            PElementReference elementReference) {

        return this.elementReferenceResolutionMap.get(elementReference);
    }

    public Type getTypeResolution(
            PElementBody elementBody) {

        return this.typeResolutionMap.get(elementBody);
    }

    public TransformationElement getTransformationElementResolution(
            PTransformationElement transformationElement) {

        return this.transformationElementMap.get(transformationElement);
    }

    public Production getTreeProduction(
            String name) {

        return (Production) this.treeNameSpace.get(name);
    }

    void addExpression(
            Node declaration) {

        Expression expression = new Expression(this, declaration);
        expression.setInternalName(expression.getName());
        if (this.nodeMap.containsKey(declaration)) {
            throw new InternalException("it was already added.");
        }
        this.nodeMap.put(declaration, expression);
        this.parserNameSpace.add(expression);
        this.treeNameSpace.add(expression);
    }

    void addInlinedExpression(
            Node declaration) {

        Expression expression = new Expression(this, declaration);
        String name = expression.getName();
        if (name != null) {
            expression.setInternalName(name);
        }
        else {
            expression.setInternalName("." + this.nextInternalNameIndex++);
        }

        Declaration previousDeclaration = this.parserNameSpace.get(expression
                .getLookupName());

        // only add new expression if it's a new declaration or if it redeclares
        // a normal expression
        if (previousDeclaration == null
                || previousDeclaration.getLocation() instanceof TIdentifier) {
            if (this.nodeMap.containsKey(declaration)) {
                throw new InternalException("it was already added.");
            }
            this.nodeMap.put(declaration, expression);
            this.parserNameSpace.add(expression);
            this.treeNameSpace.add(expression);
        }
        else {
            if (this.nodeMap.containsKey(declaration)) {
                throw new InternalException("it was already added.");
            }
            this.nodeMap.put(declaration, previousDeclaration);
        }
    }

    void addProduction(
            AParserProduction declaration) {

        Production production = new Production(this, declaration);
        production.setInternalName(production.getName());
        if (this.nodeMap.containsKey(declaration)) {
            throw new InternalException("it was already added.");
        }
        this.nodeMap.put(declaration, production);
        this.parserNameSpace.add(production);
    }

    void addProduction(
            ATreeProduction declaration) {

        Production production = new Production(this, declaration);
        production.setInternalName(production.getName());
        if (this.nodeMap.containsKey(declaration)) {
            throw new InternalException("it was already added.");
        }
        this.nodeMap.put(declaration, production);
        this.treeNameSpace.add(production);
    }

    void addAlternative(
            Production production,
            AAlternative declaration) {

        Alternative alternative = new Alternative(this, production, declaration);
        if (this.nodeMap.containsKey(declaration)) {
            throw new InternalException("it was already added.");
        }
        this.nodeMap.put(declaration, alternative);
    }

    void addElement(
            Alternative alternative,
            AElement declaration) {

        Element element = new Element(this, alternative, declaration);
        if (this.nodeMap.containsKey(declaration)) {
            throw new InternalException("it was already added.");
        }
        this.nodeMap.put(declaration, element);
    }

    void resolveExpression(
            ANameExpression nameExpression) {

        TIdentifier nameIdentifier = nameExpression.getIdentifier();
        String name = nameIdentifier.getText();
        Declaration declaration = this.parserNameSpace.get(name);

        if (declaration == null) {
            declaration = this.treeNameSpace.get(name);

            if (declaration == null) {
                throw SemanticException.semanticError("No \"" + name
                        + "\" has been declared.", nameIdentifier);
            }

            if (!(declaration instanceof Expression)) {
                throw SemanticException.semanticError("\"" + name
                        + "\" is not an expression.", nameIdentifier);
            }

            throw new InternalException(
                    "an expression must be in both parser and tree name spaces");
        }

        if (!(declaration instanceof Expression)) {
            throw SemanticException.semanticError("\"" + name
                    + "\" is not an expression.", nameIdentifier);
        }

        if (this.declarationResolutionMap.containsKey(nameIdentifier)) {
            throw new InternalException("it was already resolved.");
        }
        this.declarationResolutionMap.put(nameIdentifier, declaration);
    }

    void resolveTreeNameUnit(
            ANameUnit nameUnit) {

        TIdentifier nameIdentifier = nameUnit.getIdentifier();
        String name = nameIdentifier.getText();
        Declaration declaration = this.treeNameSpace.get(name);

        if (declaration == null) {
            declaration = this.parserNameSpace.get(name);

            if (declaration == null) {
                throw SemanticException.semanticError("No \"" + name
                        + "\" has been declared.", nameIdentifier);
            }

            throw SemanticException.semanticError("\"" + name
                    + "\" is not a tree production.", nameIdentifier);
        }

        if (this.declarationResolutionMap.containsKey(nameIdentifier)) {
            throw new InternalException("it was already resolved.");
        }
        this.declarationResolutionMap.put(nameIdentifier, declaration);
    }

    void resolveIdentifierCharUnit(
            AIdentifierCharUnit identifierCharUnit) {

        TIdentifierChar identifierChar = identifierCharUnit.getIdentifierChar();
        String text = identifierChar.getText();
        String name = text.substring(1, text.length() - 1);
        resolveInlinedExpression(name, identifierChar);
    }

    void resolveCharUnit(
            ACharUnit charUnit) {

        TChar charToken = charUnit.getChar();
        String name = charToken.getText();
        resolveInlinedExpression(name, charToken);
    }

    void resolveIdentifierStringUnit(
            AIdentifierStringUnit identifierStringUnit) {

        TIdentifierString identifierString = identifierStringUnit
                .getIdentifierString();
        String text = identifierString.getText();
        String name = text.substring(1, text.length() - 1);
        resolveInlinedExpression(name, identifierString);
    }

    void resolveStringUnit(
            AStringUnit stringUnit) {

        TString stringToken = stringUnit.getString();
        String name = stringToken.getText();
        resolveInlinedExpression(name, stringToken);
    }

    void resolveEndUnit(
            AEndUnit endUnit) {

        TEndKeyword endKeyword = endUnit.getEndKeyword();
        resolveInlinedExpression("end", endKeyword);
    }

    void resolveParserIdentifier(
            TIdentifier identifier) {

        String name = identifier.getText();
        Declaration declaration = this.parserNameSpace.get(name);

        if (declaration == null) {
            declaration = this.treeNameSpace.get(name);

            if (declaration == null) {
                throw SemanticException.semanticError("No \"" + name
                        + "\" has been declared.", identifier);
            }

            throw SemanticException.semanticError("\"" + name
                    + "\" is not a parser production.", identifier);
        }

        if (!(declaration instanceof Production)
                && !(declaration instanceof Expression)) {
            throw SemanticException.semanticError("\"" + name
                    + "\" is not a production or an expression.", identifier);
        }

        if (this.declarationResolutionMap.containsKey(identifier)) {
            throw new InternalException("it was already resolved.");
        }
        this.declarationResolutionMap.put(identifier, declaration);
    }

    void resolveTreeIdentifier(
            TIdentifier identifier) {

        String name = identifier.getText();
        Declaration declaration = this.treeNameSpace.get(name);

        if (declaration == null) {
            declaration = this.parserNameSpace.get(name);

            if (declaration == null) {
                throw SemanticException.semanticError("No \"" + name
                        + "\" has been declared.", identifier);
            }

            throw SemanticException.semanticError("\"" + name
                    + "\" is not a tree production.", identifier);
        }

        if (!(declaration instanceof Production)
                && !(declaration instanceof Expression)) {
            throw SemanticException.semanticError("\"" + name
                    + "\" is not a production or an expression.", identifier);
        }

        if (this.declarationResolutionMap.containsKey(identifier)) {
            throw new InternalException("it was already resolved.");
        }
        this.declarationResolutionMap.put(identifier, declaration);
    }

    void resolveAlternativeIdentifiers(
            Production production,
            LinkedList<TIdentifier> identifiers) {

        for (TIdentifier identifier : identifiers) {
            resolveAlternativeIdentifier(production, identifier);
        }
    }

    void resolveAlternativeIdentifier(
            Production production,
            TIdentifier identifier) {

        String name = identifier.getText();
        Alternative alternative = production.getAlternative(name);

        if (alternative == null) {
            if (production.hasAlternative(name)) {
                throw SemanticException.semanticError("Production \""
                        + production.getName() + "\" has two \"" + name
                        + "\" alternatives (or more).", identifier);
            }

            throw SemanticException.semanticError(
                    "\"" + name + "\" is not an alternative of production \""
                            + production.getName() + "\".", identifier);
        }

        if (this.alternativeResolutionMap.containsKey(identifier)) {
            throw new InternalException("it was already resolved.");
        }
        this.alternativeResolutionMap.put(identifier, alternative);
    }

    void resolveAlternativeReference(
            AUnnamedAlternativeReference alternativeReference) {

        Production production = (Production) getDeclarationResolution(alternativeReference
                .getProduction());
        Alternative alternative = production.getAlternative("");

        if (alternative == null) {
            throw SemanticException.semanticError(
                    "The alternative name is missing.",
                    alternativeReference.getProduction());
        }

        if (this.alternativeReferenceResolutionMap
                .containsKey(alternativeReference)) {
            throw new InternalException("It was already resolved.");
        }
        this.alternativeReferenceResolutionMap.put(alternativeReference,
                AlternativeReference.createDeclaredAlternativeReference(this,
                        alternative, alternativeReference.getProduction()));
    }

    void resolveAlternativeReference(
            ANamedAlternativeReference alternativeReference) {

        if (this.alternativeReferenceResolutionMap
                .containsKey(alternativeReference)) {
            throw new InternalException("It was already resolved.");
        }
        this.alternativeReferenceResolutionMap.put(alternativeReference,
                AlternativeReference.createDeclaredAlternativeReference(this,
                        getAlternativeResolution(alternativeReference
                                .getAlternative()), alternativeReference
                                .getProduction()));
    }

    void resolveElementReference(
            ANaturalElementReference elementReference) {

        if (this.elementReferenceResolutionMap.containsKey(elementReference)) {
            throw new InternalException("It was already resolved.");
        }
        this.elementReferenceResolutionMap.put(elementReference,
                ElementReference.createDeclaredElementReference(this,
                        elementReference));
    }

    void resolveElementReference(
            ATransformedElementReference elementReference) {

        if (this.elementReferenceResolutionMap.containsKey(elementReference)) {
            throw new InternalException("It was already resolved.");
        }
        this.elementReferenceResolutionMap.put(elementReference,
                ElementReference.createDeclaredElementReference(this,
                        elementReference));
    }

    void resolveType(
            PElementBody node) {

        if (this.typeResolutionMap.containsKey(node)) {
            throw new InternalException("It was already resolved.");
        }
        this.typeResolutionMap.put(node, new Type(this, node));
    }

    void resolveTransformationElement(
            ANullTransformationElement node) {

        if (this.transformationElementMap.containsKey(node)) {
            throw new InternalException("It was already resolved.");
        }
        this.transformationElementMap.put(node, TransformationElement
                .createDeclaredNullTransformationElement(this, node));
    }

    void resolveTransformationElement(
            AReferenceTransformationElement node) {

        if (this.transformationElementMap.containsKey(node)) {
            throw new InternalException("It was already resolved.");
        }
        this.transformationElementMap.put(node, TransformationElement
                .createDeclaredReferenceTransformationElement(this, node));
    }

    void resolveTransformationElement(
            ADeleteTransformationElement node) {

        if (this.transformationElementMap.containsKey(node)) {
            throw new InternalException("It was already resolved.");
        }
        this.transformationElementMap.put(node, TransformationElement
                .createDeclaredDeleteTransformationElement(this, node));
    }

    void resolveTransformationElement(
            ANewTransformationElement node) {

        if (this.transformationElementMap.containsKey(node)) {
            throw new InternalException("It was already resolved.");
        }
        this.transformationElementMap.put(node, TransformationElement
                .createDeclaredNewTransformationElement(this, node));
    }

    void resolveTransformationElement(
            AListTransformationElement node) {

        if (this.transformationElementMap.containsKey(node)) {
            throw new InternalException("It was already resolved.");
        }
        this.transformationElementMap.put(node, TransformationElement
                .createDeclaredListTransformationElement(this, node));
    }

    void resolveTransformationElement(
            ALeftTransformationElement node) {

        if (this.transformationElementMap.containsKey(node)) {
            throw new InternalException("It was already resolved.");
        }
        this.transformationElementMap.put(node, TransformationElement
                .createDeclaredLeftTransformationElement(this, node));
    }

    void resolveTransformationElement(
            ARightTransformationElement node) {

        if (this.transformationElementMap.containsKey(node)) {
            throw new InternalException("It was already resolved.");
        }
        this.transformationElementMap.put(node, TransformationElement
                .createDeclaredRightTransformationElement(this, node));
    }

    private void resolveInlinedExpression(
            String name,
            Token location) {

        Declaration declaration = this.parserNameSpace.get(name);

        if (declaration == null) {
            declaration = this.treeNameSpace.get(name);

            if (declaration == null) {
                throw SemanticException.semanticError("No \"" + name
                        + "\" has been declared.", location);
            }

            if (!(declaration instanceof Expression)) {
                throw SemanticException.semanticError("\"" + name
                        + "\" is not an expression.", location);
            }

            throw new InternalException(
                    "an expression must be in both parser and tree name spaces");
        }

        if (!(declaration instanceof Expression)) {
            throw SemanticException.semanticError("\"" + name
                    + "\" is not an expression.", location);
        }

        if (this.inlinedExpressionResolutionMap.containsKey(location)) {
            throw new InternalException("it was already resolved.");
        }
        this.inlinedExpressionResolutionMap.put(location,
                (Expression) declaration);
    }
}
