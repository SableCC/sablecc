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

package org.sablecc.sablecc.core;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.errormessage.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public class SemanticException
        extends CompilerException {

    private Token location;

    private SemanticException(
            String message,
            Token location) {

        super(message);

        if (location == null) {
            throw new InternalException("location may not be null");
        }

        this.location = location;
    }

    public static SemanticException notImplemented(
            Token token) {

        return new SemanticException(new MNotImplemented(token.getLine() + "",
                token.getPos() + "").toString(), token);
    }

    public static SemanticException duplicateDeclaration(
            INameDeclaration duplicateNameDeclaration,
            INameDeclaration olderNameDeclaration) {

        String name = duplicateNameDeclaration.getName();
        if (!name.equals(olderNameDeclaration.getName())) {
            throw new InternalException("names must be identical");
        }

        TIdentifier duplicateIdentifier = duplicateNameDeclaration
                .getNameIdentifier();
        TIdentifier olderIdentifier = olderNameDeclaration.getNameIdentifier();

        return new SemanticException(new MDuplicateDeclaration(name,
                duplicateNameDeclaration.getNameType(),
                duplicateIdentifier.getLine() + "",
                duplicateIdentifier.getPos() + "",
                olderNameDeclaration.getNameType(), olderIdentifier.getLine()
                        + "", olderIdentifier.getPos() + "").toString(),
                duplicateIdentifier);
    }

    public static SemanticException duplicateAlternativeName(
            Tree.TreeAlternative duplicateAlternative,
            Tree.TreeAlternative olderAlternative) {

        String name = duplicateAlternative.getName();
        if (!name.equals(olderAlternative.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateAlternative.getNameToken();
        Token olderToken = olderAlternative.getNameToken();

        return new SemanticException(
                new MDuplicateAlternativeName(name, duplicateAlternative
                        .getProduction().getName(), duplicateToken.getLine()
                        + "", duplicateToken.getPos() + "", olderToken
                        .getLine() + "", olderToken.getPos() + "").toString(),
                duplicateToken);
    }

    public static SemanticException duplicateAlternativeName(
            Parser.ParserAlternative duplicateAlternative,
            Parser.ParserAlternative olderAlternative) {

        String name = duplicateAlternative.getName();
        if (!name.equals(olderAlternative.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateAlternative.getNameToken();
        Token olderToken = olderAlternative.getNameToken();

        return new SemanticException(
                new MDuplicateAlternativeName(name, duplicateAlternative
                        .getProduction().getName(), duplicateToken.getLine()
                        + "", duplicateToken.getPos() + "", olderToken
                        .getLine() + "", olderToken.getPos() + "").toString(),
                duplicateToken);
    }

    public static SemanticException duplicateElementName(
            Tree.TreeElement duplicateElement,
            Tree.TreeElement olderElement) {

        String name = duplicateElement.getName();

        if (!name.equals(olderElement.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateElement.getNameToken();
        Token olderToken = olderElement.getNameToken();

        return new SemanticException(
                new MDuplicateElementName(name, duplicateElement
                        .getAlternative().getProduction().getName(),
                        duplicateElement.getAlternative().getIndex() + "",
                        duplicateToken.getLine() + "", duplicateToken.getPos()
                                + "", olderToken.getLine() + "",
                        olderToken.getPos() + "").toString(), duplicateToken);
    }

    public static SemanticException duplicateElementName(
            Parser.ParserElement duplicateElement,
            Parser.ParserElement olderElement) {

        String name = duplicateElement.getName();

        if (!name.equals(olderElement.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateElement.getNameToken();
        Token olderToken = olderElement.getNameToken();

        return new SemanticException(
                new MDuplicateElementName(name, duplicateElement
                        .getAlternative().getProduction().getName(),
                        duplicateElement.getAlternative().getIndex() + "",
                        duplicateToken.getLine() + "", duplicateToken.getPos()
                                + "", olderToken.getLine() + "",
                        olderToken.getPos() + "").toString(), duplicateToken);
    }

    public static SemanticException duplicateProductionTransformationElementName(
            ProductionTransformationElement duplicateElement,
            ProductionTransformationElement olderElement) {

        String elementName = duplicateElement.getName();

        if (!(duplicateElement.getProductionTransformation() instanceof ProductionTransformation.ExplicitProductionTransformation)) {
            throw new InternalException("transformation must be explicit");
        }

        String productionName = ((ProductionTransformation.ExplicitProductionTransformation) duplicateElement
                .getProductionTransformation()).getDeclaration()
                .getProduction().getText();

        if (!elementName.equals(olderElement.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateElement.getNameToken();
        Token olderToken = olderElement.getNameToken();

        return new SemanticException(
                new MDuplicateProductionTransformationElementName(elementName,
                        productionName, duplicateToken.getLine() + "",
                        duplicateToken.getPos() + "",
                        olderToken.getLine() + "", olderToken.getPos() + "")
                        .toString(),
                duplicateToken);
    }

    public static SemanticException duplicateProductionTransformationName(
            ProductionTransformation duplicateTransformation,
            ProductionTransformation olderTransformation) {

        String duplicateName = duplicateTransformation.getName();

        if (!duplicateName.equals(olderTransformation.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateTransformation.getNameIdentifier();
        Token olderToken = olderTransformation.getNameIdentifier();

        return new SemanticException(new MDuplicateTransformationDeclaration(
                duplicateName, duplicateToken.getLine() + "",
                duplicateToken.getPos() + "", olderToken.getLine() + "",
                olderToken.getPos() + "").toString(), duplicateToken);
    }

    public static SemanticException duplicateAlternativeTransformationName(
            AlternativeTransformation.ExplicitAlternativeTransformation duplicateTransformation,
            AlternativeTransformation.ExplicitAlternativeTransformation olderTransformation) {

        String duplicateName = duplicateTransformation.getName();

        if (!duplicateName.equals(olderTransformation.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateTransformation
                .getProductionNameIdentifier();
        Token olderToken = olderTransformation.getProductionNameIdentifier();

        return new SemanticException(new MDuplicateTransformationDeclaration(
                duplicateName, duplicateToken.getLine() + "",
                duplicateToken.getPos() + "", olderToken.getLine() + "",
                olderToken.getPos() + "").toString(), duplicateToken);
    }

    public static SemanticException undefinedReference(
            TIdentifier undefinedNameToken) {

        return new SemanticException(new MUndefinedReference(
                undefinedNameToken.getText(),
                undefinedNameToken.getLine() + "", undefinedNameToken.getPos()
                        + "").toString(), undefinedNameToken);
    }

    public static SemanticException undefinedAlternativeReference(
            TIdentifier undefinedNameToken,
            Parser.ParserProduction production) {

        String productionName = production.getNameIdentifier().getText();

        return new SemanticException(new MUndefinedAlternativeReference(
                undefinedNameToken.getText(), productionName,
                undefinedNameToken.getLine() + "", undefinedNameToken.getPos()
                        + "").toString(), undefinedNameToken);
    }

    public static SemanticException undefinedTreeAlternativeReference(
            TIdentifier undefinedNameToken,
            Tree.TreeProduction production) {

        String productionName = production.getNameIdentifier().getText();

        return new SemanticException(new MUndefinedAlternativeReference(
                undefinedNameToken.getText(), productionName,
                undefinedNameToken.getLine() + "", undefinedNameToken.getPos()
                        + "").toString(), undefinedNameToken);
    }

    public static SemanticException undefinedElementReference(
            TIdentifier undefinedNameToken,
            Parser.ParserProduction production,
            Parser.ParserAlternative alternative) {

        String productionName = production.getNameIdentifier().getText();
        String alternativeName;
        if (alternative.getName() == null || alternative.getName().equals("")) {
            alternativeName = "{" + alternative.getIndex() + "}";
        }
        else {
            alternativeName = alternative.getName();
        }

        return new SemanticException(new MUndefinedElementReference(
                undefinedNameToken.getText(), productionName, alternativeName,
                undefinedNameToken.getLine() + "", undefinedNameToken.getPos()
                        + "").toString(), undefinedNameToken);
    }

    public static SemanticException undefinedAlternativeTransformationReference(
            TIdentifier undefinedNameToken,
            ProductionTransformation.ExplicitProductionTransformation productionTransformation) {

        String productionName = productionTransformation.getDeclaration()
                .getProduction().getText();

        return new SemanticException(
                new MUndefinedAlternativeTransformationReference(
                        undefinedNameToken.getText(), productionName,
                        undefinedNameToken.getLine() + "",
                        undefinedNameToken.getPos() + "").toString(),
                undefinedNameToken);
    }

    public static SemanticException badReference(
            TIdentifier referenceToken,
            String className,
            String[] expectedNames) {

        if (expectedNames.length < 1) {
            throw new InternalException(
                    "expectedNames must have at least 1 name");
        }
        String namesString = "\"" + expectedNames[0] + "\"";

        for (int i = 1; i < expectedNames.length; i++) {
            namesString = namesString + " or " + "\"" + expectedNames[i] + "\"";
        }
        return new SemanticException(
                new MBadReference(className, namesString,
                        referenceToken.getLine() + "", referenceToken.getPos()
                                + "").toString(), referenceToken);
    }

    public static SemanticException badProductionReference(
            TIdentifier referenceToken,
            Parser.ParserProduction production) {

        return new SemanticException(
                new MBadProductionReference(referenceToken.getText(),
                        production.getName(), referenceToken.getLine() + "",
                        referenceToken.getPos() + "").toString(),
                referenceToken);
    }

    public static SemanticException badAlternativeReference(
            TIdentifier referenceToken,
            Parser.ParserProduction production) {

        return new SemanticException(
                new MBadAlternativeReference(referenceToken.getText(),
                        production.getName(), referenceToken.getLine() + "",
                        referenceToken.getPos() + "").toString(),
                referenceToken);

    }

    public static SemanticException badTreeAlternativeReference(
            TIdentifier referenceToken,
            Tree.TreeProduction production) {

        return new SemanticException(
                new MBadAlternativeReference(referenceToken.getText(),
                        production.getName(), referenceToken.getLine() + "",
                        referenceToken.getPos() + "").toString(),
                referenceToken);

    }

    public static SemanticException badAlternativeTransformationReference(
            TIdentifier typeToken,
            TIdentifier partToken,
            Parser.ParserProduction production) {

        String fullReferenceName = typeToken.getText() + "."
                + partToken.getText();

        return new SemanticException(
                new MBadAlternativeTransformationReference(fullReferenceName,
                        production.getName(), partToken.getLine() + "",
                        partToken.getPos() + "").toString(), partToken);
    }

    public static SemanticException impossibleNaturalReference(
            ANaturalElementReference reference,
            ProductionTransformation transformation) {

        return new SemanticException(new MImpossibleNaturalReference(reference
                .getElement().getText(), reference.getElement().getLine() + "",
                reference.getElement().getPos() + "", transformation
                        .getNameIdentifier().getLine() + "", transformation
                        .getNameIdentifier().getPos() + "").toString(),
                reference.getElement());
    }

    public static SemanticException unmatchedAlternative(
            Parser.ParserAlternative parserAlternative,
            Tree.TreeProduction treeProduction) {

        String altName;
        String parserProdName = parserAlternative.getProduction().getName();

        if (parserAlternative.getName() == null
                || parserAlternative.getName().equals("")) {
            altName = "{" + parserAlternative.getIndex() + "}";
        }
        else {
            altName = parserAlternative.getName();
        }

        Token errorLocation = treeProduction.getLocation();
        return new SemanticException(
                new MUnmatchedAlternative(altName, parserProdName,
                        treeProduction.getName(), errorLocation.getLine() + "",
                        errorLocation.getPos() + "").toString(), errorLocation);
    }

    public static SemanticException nonTrivialProductionTransformation(
            ProductionTransformation.ExplicitProductionTransformation transformation,
            Parser.ParserProduction production) {

        Token location = transformation.getLocation();

        return new SemanticException(new MNonTrivialProductionTransformation(
                production.getName(), location.getLine() + "",
                location.getPos() + "").toString(),
                transformation.getLocation());

    }

    public static SemanticException unmatchedElement(
            Tree.TreeElement element,
            Parser.ParserAlternative targetAlternative) {

        String treeElemName;
        String treeAltName;
        String parserAltName;

        if (element.getName() != null) {
            treeElemName = element.getName();
        }
        else {
            treeElemName = element.getElement();
        }

        if (element.getAlternative().getName() == null
                || element.getAlternative().getName().equals("")) {
            treeAltName = "{" + element.getAlternative().getIndex() + "}";
        }
        else {
            treeAltName = element.getAlternative().getName();
        }

        if (targetAlternative.getName() == null
                || targetAlternative.getName().equals("")) {
            parserAltName = "{" + targetAlternative.getIndex() + "}";
        }
        else {
            parserAltName = targetAlternative.getName();
        }

        return new SemanticException(new MUnmatchedElement(treeElemName,
                treeAltName,
                element.getAlternative().getProduction().getName(),
                parserAltName, targetAlternative.getProduction().getName(),
                element.getLocation().getLine() + "", element.getLocation()
                        .getPos() + "").toString(), element.getLocation());

    }

    public static SemanticException ambiguousAlternativeTransformation(
            Parser.ParserElement firstParserElement,
            Parser.ParserElement secondParserElement,
            Tree.TreeElement treeElement) {

        String altName;

        if (firstParserElement.getAlternative().getName() != null
                && !firstParserElement.getAlternative().getName().equals("")) {
            altName = firstParserElement.getAlternative().getName();
        }
        else {
            altName = "{" + firstParserElement.getAlternative().getIndex()
                    + "}";
        }

        return new SemanticException(new MAmbiguousAlternativeTransformation(
                firstParserElement.getElement(), altName, firstParserElement
                        .getAlternative().getProduction().getName(),
                treeElement.getLocation().getLine() + "", treeElement
                        .getLocation().getPos() + "", firstParserElement
                        .getLocation().getLine() + "", firstParserElement
                        .getLocation().getPos() + "", secondParserElement
                        .getLocation().getPos() + "").toString(),
                treeElement.getLocation());

    }

    public static SemanticException elementAssignementError(
            AlternativeTransformationElement transformationElement,
            Tree.TreeElement treeElement) {

        if (!(transformationElement instanceof IReferencable)) {
            throw new InternalException(
                    "transformationElement shouldn't be implicit");
        }

        CardinalityInterval fromInterval = transformationElement.getType()
                .getCardinality();
        String cardinalityFrom = "(" + fromInterval.getLowerBound().getValue()
                + "..";

        if (!fromInterval.getUpperBound().equals(Bound.MAX)) {
            cardinalityFrom += fromInterval.getUpperBound().getValue();
        }
        else {
            cardinalityFrom += ".";
        }

        cardinalityFrom += ")";

        CardinalityInterval toInterval = treeElement.getCardinality();
        String cardinalityTo = "(" + toInterval.getLowerBound().getValue()
                + "..";

        if (!toInterval.getUpperBound().equals(Bound.MAX)) {
            cardinalityTo += toInterval.getUpperBound().getValue();
        }
        else {
            cardinalityTo += ".";
        }

        cardinalityTo += ")";

        return new SemanticException(new MElementAssignationError(
                transformationElement.getElement(), treeElement.getElement(),
                cardinalityFrom, cardinalityTo,
                ((IReferencable) transformationElement).getLocation().getLine()
                        + "", ((IReferencable) transformationElement)
                        .getLocation().getPos() + "", treeElement.getLocation()
                        .getLine() + "", treeElement.getLocation().getPos()
                        + "").toString(),
                ((IReferencable) transformationElement).getLocation());

    }

    public static SemanticException elementAssignementError(
            ProductionTransformationElement productionElement,
            AlternativeTransformationElement alternativeElement) {

        if (!(alternativeElement instanceof IReferencable)) {
            throw new InternalException(
                    "transformationElement shouldn't be implicit");
        }

        CardinalityInterval fromInterval = alternativeElement.getType()
                .getCardinality();
        String cardinalityFrom = "(" + fromInterval.getLowerBound().getValue()
                + "..";

        if (!fromInterval.getUpperBound().equals(Bound.MAX)) {
            cardinalityFrom += fromInterval.getUpperBound().getValue();
        }
        else {
            cardinalityFrom += ".";
        }

        cardinalityFrom += ")";

        CardinalityInterval toInterval = productionElement.getCardinality();
        String cardinalityTo = "(" + toInterval.getLowerBound().getValue()
                + "..";

        if (!toInterval.getUpperBound().equals(Bound.MAX)) {
            cardinalityTo += toInterval.getUpperBound().getValue();
        }
        else {
            cardinalityTo += ".";
        }

        cardinalityTo += ")";

        return new SemanticException(new MElementAssignationError(
                alternativeElement.getElement(),
                productionElement.getElement(), cardinalityFrom, cardinalityTo,
                ((IReferencable) alternativeElement).getLocation().getLine()
                        + "", ((IReferencable) alternativeElement)
                        .getLocation().getPos() + "", productionElement
                        .getLocation().getLine() + "", productionElement
                        .getLocation().getPos() + "").toString(),
                ((IReferencable) alternativeElement).getLocation());

    }

    public static SemanticException badNewSignature(
            AlternativeTransformationElement.ExplicitNewElement newElement,
            Tree.TreeAlternative treeAlternative,
            int nbNewElement,
            int nbTreeElement) {

        String altName = treeAlternative.getProduction().getName() + ".";

        if (treeAlternative.getName() == null
                || treeAlternative.getName().equals("")) {
            altName += "{" + treeAlternative.getIndex() + "}";
        }
        else {
            altName += treeAlternative.getName();
        }

        Token transformationLocation = newElement.getLocation();
        Token treeLocation = treeAlternative.getLocation();

        return new SemanticException(new MBadNewSignature(altName, nbNewElement
                + "", nbTreeElement + "",
                transformationLocation.getLine() + "",
                transformationLocation.getPos() + "", treeLocation.getLine()
                        + "", treeLocation.getPos() + "").toString(),
                transformationLocation);
    }

    public static SemanticException badAlternativeTransformationSignature(
            ProductionTransformation productionTransformation,
            AlternativeTransformation.ExplicitAlternativeTransformation alternativeTransformation,
            int nbProdTransfElem,
            int nbAltTransfElem) {

        throw new SemanticException(new MBadAlternativeTransformationSignature(
                alternativeTransformation.getName(), nbAltTransfElem + "",
                nbProdTransfElem + "", alternativeTransformation.getLocation()
                        .getLine() + "", alternativeTransformation
                        .getLocation().getPos() + "", productionTransformation
                        .getLocation().getLine() + "", productionTransformation
                        .getLocation().getPos() + "").toString(),
                alternativeTransformation.getLocation());

    }

    public static SemanticException uncompatibleListElement(
            AlternativeTransformationListElement firstElement,
            AlternativeTransformationListElement secondElement) {

        throw new SemanticException(new MUncompatibleListElement(
                firstElement.getElement(), secondElement.getElement(),
                firstElement.getLocation().getLine() + "", firstElement
                        .getLocation().getPos() + "", secondElement
                        .getLocation().getLine() + "", secondElement
                        .getLocation().getPos() + "").toString(),
                secondElement.getLocation());
    }

    public static SemanticException badRootElementTransformation(
            Parser.ParserProduction production) {

        return new SemanticException(new MBadRootElementTransformation(
                production.getName(), production.getLocation().getLine() + "",
                production.getLocation().getPos() + "").toString(),
                production.getLocation());
    }

    public static SemanticException badSyntacticTokenTransformation(
            Parser.ParserProduction.TokenProduction production) {

        return new SemanticException(new MBadSyntacticTokenTransformation(
                production.getName(), production.getLocation().getLine() + "",
                production.getLocation().getPos() + "").toString(),
                production.getLocation());
    }

    public static SemanticException multipleListExpansion(
            AlternativeTransformationListElement firstExpansion,
            AlternativeTransformationListElement secondExpansion) {

        String name;

        if (firstExpansion instanceof AlternativeTransformationListElement.NormalListElement) {
            name = firstExpansion.getElement().substring(0,
                    firstExpansion.getElement().length() - 4);
        }
        else if (firstExpansion instanceof AlternativeTransformationListElement.LeftListElement) {
            name = firstExpansion.getElement().substring(0,
                    firstExpansion.getElement().length() - 8);
        }
        else if (firstExpansion instanceof AlternativeTransformationListElement.RightListElement) {
            name = firstExpansion.getElement().substring(0,
                    firstExpansion.getElement().length() - 9);
        }
        else {
            throw new InternalException(
                    "multipleListExpansion exception shouldn't be used with a non list listElement");
        }

        return new SemanticException(new MMultipleListExpansion(name,
                firstExpansion.getLocation().getLine() + "", firstExpansion
                        .getLocation().getPos() + "", secondExpansion
                        .getLocation().getLine() + "", secondExpansion
                        .getLocation().getPos() + "").toString(),
                secondExpansion.getLocation());
    }

    public static SemanticException elementReordered(
            Parser.ParserAlternative alternative) {

        String altName;

        if (alternative.getName() != null && !alternative.getName().equals("")) {
            altName = alternative.getName();
        }
        else {
            altName = "{" + alternative.getIndex() + "}";
        }

        return new SemanticException(new MElementReordered(altName, alternative
                .getProduction().getName(), alternative.getLocation().getLine()
                + "", alternative.getLocation().getPos() + "").toString(),
                alternative.getLocation());
    }

    public static SemanticException transformationElementReordered(
            Parser.ParserAlternative alternative,
            ProductionTransformation production) {

        String altName;

        if (alternative.getName() != null && !alternative.getName().equals("")) {
            altName = alternative.getName();
        }
        else {
            altName = "{" + alternative.getIndex() + "}";
        }

        Token location = alternative.getTransformation()
                .getTransformationElements().get(0).getLocation();

        return new SemanticException(new MTransformationElementReordered(
                production.getName(), altName, alternative.getProduction()
                        .getName(), location.getLine() + "", location.getPos()
                        + "").toString(), location);
    }

    public static SemanticException listExpansionMissing(
            AlternativeTransformationListElement element) {

        throw new SemanticException(new MListExpansionMissing(
                element.getElement(), element.getLocation().getLine() + "",
                element.getLocation().getPos() + "").toString(),
                element.getLocation());
    }

    public static SemanticException spuriousParserNamedContextDeclaration(
            AParserContext declaration,
            Context.NamedContext namedContext) {

        String name = declaration.getName().getText();
        if (!name.equals(namedContext.getName())) {
            throw new InternalException("names must be identical");
        }

        TIdentifier duplicateIdentifier = declaration.getName();
        TIdentifier olderIdentifier = namedContext.getParserDeclaration()
                .getName();

        return new SemanticException(
                new MDuplicateDeclaration(name, "context",
                        duplicateIdentifier.getLine() + "",
                        duplicateIdentifier.getPos() + "", "context",
                        olderIdentifier.getLine() + "",
                        olderIdentifier.getPos() + "").toString(),
                duplicateIdentifier);
    }

}
