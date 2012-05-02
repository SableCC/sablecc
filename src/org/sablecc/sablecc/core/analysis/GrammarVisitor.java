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

package org.sablecc.sablecc.core.analysis;

import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.core.transformation.*;

public class GrammarVisitor
        implements IGrammarVisitor {

    @Override
    public void visit(
            IVisitableGrammarPart node) {

        if (node != null) {
            node.apply(this);
        }
    }

    @Override
    public void visitGrammar(
            Grammar node) {

        for (Context context : node.getNamedContexts()) {
            context.apply(this);
        }

        Context context = node.getGlobalAnonymousContext();

        if (context != null) {
            context.apply(this);
        }

        node.getLexer().apply(this);
        node.getParser().apply(this);
        node.getTransformation().apply(this);

        if (node.hasATree()) {
            node.getTree().apply(this);
        }

    }

    @Override
    public void visitContext(
            Context node) {

        // Leaf

    }

    @Override
    public void visitLexer(
            Lexer node) {

        for (Investigator.LexerInvestigator investigator : node
                .getInvestigators()) {
            investigator.apply(this);
        }

        for (Selector.LexerSelector selector : node.getSelectors()) {
            selector.apply(this);
        }

        for (LexerExpression.NamedExpression namedExpression : node
                .getNamedExpressions()) {
            namedExpression.apply(this);
        }
    }

    @Override
    public void visitNamedExpression(
            LexerExpression.NamedExpression node) {

        node.getExpression().apply(this);

    }

    @Override
    public void visitAnyExpression(
            Expression.Any node) {

        // Leaf

    }

    @Override
    public void visitCharUnitExpression(
            Expression.CharUnit node) {

        // Leaf

    }

    @Override
    public void visitConcatenationExpression(
            Expression.Concatenation node) {

        node.getLeft().apply(this);
        node.getRight().apply(this);

    }

    @Override
    public void visitDecCharUnitExpression(
            Expression.DecCharUnit node) {

        // Leaf

    }

    @Override
    public void visitEndUnitExpression(
            Expression.EndUnit node) {

        // Leaf

    }

    @Override
    public void visitEpsilonExpression(
            Expression.Epsilon node) {

        // Leaf

    }

    @Override
    public void visitExceptExpression(
            Expression.Except node) {

        node.getLeft().apply(this);
        node.getRight().apply(this);

    }

    @Override
    public void visitHexCharUnitExpression(
            Expression.HexCharUnit node) {

        // Leaf

    }

    @Override
    public void visitIntersectionExpression(
            Expression.Intersection node) {

        node.getLeft().apply(this);
        node.getRight().apply(this);

    }

    @Override
    public void visitIntervalExpression(
            Expression.Interval node) {

        node.getFrom().apply(this);
        node.getTo().apply(this);

    }

    @Override
    public void visitLongestExpression(
            Expression.Longest node) {

        node.getExpression().apply(this);
    }

    @Override
    public void visitLookExpression(
            Expression.Look node) {

        node.getExpression().apply(this);

    }

    @Override
    public void visitNameUnitExpression(
            Expression.NameUnit node) {

        // Leaf

    }

    @Override
    public void visitOrExpression(
            Expression.Or node) {

        node.getLeft().apply(this);
        node.getRight().apply(this);

    }

    @Override
    public void visitSeparatedExpression(
            Expression.Separated node) {

        node.getBase().apply(this);
        node.getSeparator().apply(this);

    }

    @Override
    public void visitShortestExpression(
            Expression.Shortest node) {

        node.getExpression().apply(this);

    }

    @Override
    public void visitStartUnitExpression(
            Expression.StartUnit node) {

        // Leaf

    }

    @Override
    public void visitStringUnitExpression(
            Expression.StringUnit node) {

        // Leaf

    }

    @Override
    public void visitSubtractionExpression(
            Expression.Subtraction node) {

        node.getLeft().apply(this);
        node.getRight().apply(this);

    }

    @Override
    public void visitUnaryExpression(
            Expression.Unary node) {

        node.getExpression().apply(this);

    }

    @Override
    public void visitLexerInvestigator(
            Investigator.LexerInvestigator node) {

        // Leaf
    }

    @Override
    public void visitLexerSelector(
            Selector.LexerSelector node) {

        for (Selector.Selection selection : node.getSelections()) {
            selection.apply(this);
        }

    }

    @Override
    public void visitLexerSelectorSelection(
            Selector.LexerSelector.Selection node) {

        // Leaf
    }

    @Override
    public void visitParser(
            Parser node) {

        for (Parser.ParserProduction parserProduction : node.getProductions()) {
            parserProduction.apply(this);
        }

        for (Investigator.ParserInvestigator parserInvestigator : node
                .getInvestigators()) {
            parserInvestigator.apply(this);
        }

        for (Selector.ParserSelector parserSelector : node.getSelectors()) {
            parserSelector.apply(this);
        }

    }

    @Override
    public void visitParserSelectorSelection(
            Selector.ParserSelector.Selection node) {

        // Leaf
    }

    @Override
    public void visitParserProduction(
            Parser.ParserProduction node) {

        for (Parser.ParserAlternative parserAlternative : node
                .getAlternatives()) {
            parserAlternative.apply(this);
        }

        for (Parser.ParserPriority parserPriority : node.getPriorities()) {
            parserPriority.apply(this);
        }
    }

    @Override
    public void visitParserAlternative(
            Parser.ParserAlternative node) {

        for (Parser.ParserElement parserElement : node.getElements()) {
            parserElement.apply(this);
        }

    }

    @Override
    public void visitLeftParserPriority(
            Parser.ParserPriority.LeftPriority node) {

        // Leaf

    }

    @Override
    public void visitRightParserPriority(
            Parser.ParserPriority.RightPriority node) {

        // Leaf
    }

    @Override
    public void visitUnaryParserPriority(
            Parser.ParserPriority.UnaryPriority node) {

        // Leaf

    }

    @Override
    public void visitParserElement(
            Parser.ParserElement node) {

        // Leaf
    }

    @Override
    public void visitParserSingleElement(
            Parser.ParserElement.SingleElement node) {

        visitParserElement(node);

    }

    @Override
    public void visitParserDoubleElement(
            Parser.ParserElement.DoubleElement node) {

        visitParserElement(node);

    }

    @Override
    public void visitParserInvestigator(
            Investigator.ParserInvestigator node) {

        // Leaf

    }

    @Override
    public void visitParserSelector(
            Selector.ParserSelector node) {

        for (Selector.Selection selection : node.getSelections()) {
            selection.apply(this);
        }

    }

    @Override
    public void visitTransformation(
            Transformation node) {

        for (ProductionTransformation productionTransformation : node
                .getProductionTransformations()) {
            productionTransformation.apply(this);
        }
        for (AlternativeTransformation alternativeTransformation : node
                .getAlternativeTransformations()) {
            alternativeTransformation.apply(this);
        }

    }

    @Override
    public void visitProductionTransformation(
            ProductionTransformation node) {

        for (ProductionTransformationElement productionElement : node
                .getElements()) {
            productionElement.apply(this);
        }

    }

    @Override
    public void visitProductionTransformationElement(
            ProductionTransformationElement node) {

        // Leaf

    }

    @Override
    public void visitProductionTransformationSingleElement(
            ProductionTransformationElement.SingleElement node) {

        visitProductionTransformationElement(node);

    }

    @Override
    public void visitProductionTransformationDoubleElement(
            ProductionTransformationElement.DoubleElement node) {

        visitProductionTransformationElement(node);

    }

    @Override
    public void visitAlternativeTransformation(
            AlternativeTransformation node) {

        for (AlternativeTransformationElement alternativeElement : node
                .getTransformationElements()) {
            alternativeElement.apply(this);
        }

    }

    @Override
    public void visitAlternativeTransformationElement(
            AlternativeTransformationElement node) {

        // Leaf

    }

    @Override
    public void visitAlternativeTransformationNullElement(
            AlternativeTransformationElement.NullElement node) {

        visitAlternativeTransformationElement(node);

    }

    @Override
    public void visitAlternativeTransformationReferenceElement(
            AlternativeTransformationElement.ReferenceElement node) {

        visitAlternativeTransformationElement(node);

    }

    @Override
    public void visitAlternativeTransformationNewElement(
            AlternativeTransformationElement.NewElement node) {

        visitAlternativeTransformationElement(node);

        for (AlternativeTransformationElement element : node.getParameters()) {
            element.apply(this);
        }
    }

    @Override
    public void visitAlternativeTransformationListElement(
            AlternativeTransformationElement.ListElement node) {

        visitAlternativeTransformationElement(node);

        for (AlternativeTransformationListElement element : node
                .getListElements()) {
            element.apply(this);
        }

    }

    @Override
    public void visitAlternativeTransformationListElement(
            AlternativeTransformationListElement node) {

        // Leaf

    }

    @Override
    public void visitAlternativeTransformationReferenceListElement(
            AlternativeTransformationListElement.ReferenceElement node) {

        visitAlternativeTransformationListElement(node);

    }

    @Override
    public void visitAlternativeTransformationNormalListReferenceListElement(
            AlternativeTransformationListElement.NormalListElement node) {

        visitAlternativeTransformationListElement(node);

    }

    @Override
    public void visitAlternativeTransformationLeftListReferenceListElement(
            AlternativeTransformationListElement.LeftListElement node) {

        visitAlternativeTransformationListElement(node);

    }

    @Override
    public void visitAlternativeTransformationRightListReferenceListElement(
            AlternativeTransformationListElement.RightListElement node) {

        visitAlternativeTransformationListElement(node);

    }

    @Override
    public void visitAlternativeTransformationNewListElement(
            AlternativeTransformationListElement.NewElement node) {

        visitAlternativeTransformationListElement(node);

        for (AlternativeTransformationElement element : node.getParameters()) {
            element.apply(this);
        }

    }

    @Override
    public void visitTree(
            Tree node) {

        for (Tree.TreeProduction treeProduction : node.getProductions()) {
            treeProduction.apply(this);
        }
    }

    @Override
    public void visitTreeProduction(
            Tree.TreeProduction node) {

        for (Tree.TreeAlternative treeAlternative : node.getAlternatives()) {
            treeAlternative.apply(this);
        }

    }

    @Override
    public void visitTreeAlternative(
            Tree.TreeAlternative node) {

        for (Tree.TreeElement treeElement : node.getElements()) {
            treeElement.apply(this);
        }

    }

    @Override
    public void visitTreeElement(
            Tree.TreeElement node) {

        // Leaf

    }

    @Override
    public void visitTreeSingleElement(
            Tree.TreeElement.SingleElement node) {

        visitTreeElement(node);

    }

    @Override
    public void visitTreeDoubleElement(
            Tree.TreeElement.DoubleElement node) {

        visitTreeElement(node);

    }

}
