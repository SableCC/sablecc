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
import org.sablecc.sablecc.core.transformation.*;

public interface IGrammarVisitor {

    void visitGrammar(
            Grammar node);

    void visitNamedContext(
            Context.NamedContext node);

    void visitAnonymousContext(
            Context.AnonymousContext node);

    void visitLexer(
            Lexer node);

    void visitNamedExpression(
            LexerExpression.NamedExpression node);

    void visitAnyExpression(
            Expression.Any node);

    void visitCharUnitExpression(
            Expression.CharUnit node);

    void visitConcatenationExpression(
            Expression.Concatenation node);

    void visitDecCharUnitExpression(
            Expression.DecCharUnit node);

    void visitEndUnitExpression(
            Expression.EndUnit node);

    void visitEpsilonExpression(
            Expression.Epsilon node);

    void visitExceptExpression(
            Expression.Except node);

    void visitHexCharUnitExpression(
            Expression.HexCharUnit node);

    void visitIntersectionExpression(
            Expression.Intersection node);

    void visitIntervalExpression(
            Expression.Interval node);

    void visitLongestExpression(
            Expression.Longest node);

    void visitLookExpression(
            Expression.Look node);

    void visitNameUnitExpression(
            Expression.NameUnit node);

    void visitOrExpression(
            Expression.Or node);

    void visitSeparatedExpression(
            Expression.Separated node);

    void visitShortestExpression(
            Expression.Shortest node);

    void visitStartUnitExpression(
            Expression.StartUnit node);

    void visitStringUnitExpression(
            Expression.StringUnit node);

    void visitSubtractionExpression(
            Expression.Subtraction node);

    void visitUnaryExpression(
            Expression.Unary node);

    void visitLexerPriority(
            Lexer.LexerPriority node);

    void visitLexerInvestigator(
            Investigator.LexerInvestigator node);

    void visitLexerSelector(
            Selector.LexerSelector node);

    void visitLexerSelectorSelection(
            Selector.LexerSelector.Selection node);

    void visitParser(
            Parser node);

    void visitParserProduction(
            Parser.ParserProduction node);

    void visitParserNormalProduction(
            Parser.ParserProduction.NormalProduction node);

    void visitParserDanglingProduction(
            Parser.ParserProduction.DanglingProduction node);

    void visitParserTokenProduction(
            Parser.ParserProduction.TokenProduction node);

    void visitParserAlternative(
            Parser.ParserAlternative node);

    void visitParserNormalAlternative(
            Parser.ParserAlternative.NormalAlternative node);

    void visitParserDanglingAlternative(
            Parser.ParserAlternative.DanglingAlternative node);

    void visitLeftParserPriority(
            Parser.ParserPriority.LeftPriority node);

    void visitRightParserPriority(
            Parser.ParserPriority.RightPriority node);

    void visitUnaryParserPriority(
            Parser.ParserPriority.UnaryPriority node);

    void visitParserElement(
            Parser.ParserElement node);

    void visitParserNormalElement(
            Parser.ParserElement.NormalElement node);

    void visitParserSeparatedElement(
            Parser.ParserElement.SeparatedElement node);

    void visitParserAlternatedELement(
            Parser.ParserElement.AlternatedElement node);

    void visitParserDanglingElement(
            Parser.ParserElement.DanglingElement node);

    void visitParserInvestigator(
            Investigator.ParserInvestigator node);

    void visitParserSelector(
            Selector.ParserSelector node);

    void visitParserSelectorSelection(
            Selector.ParserSelector.Selection node);

    void visitTransformation(
            Transformation node);

    void visitProductionTransformation(
            ProductionTransformation node);

    void visitProductionTransformationElement(
            ProductionTransformationElement node);

    void visitProductionTransformationNormalElement(
            ProductionTransformationElement.NormalElement node);

    void visitProductionTransformationSeparatedElement(
            ProductionTransformationElement.SeparatedElement node);

    void visitProductionTransformationAlternatedElement(
            ProductionTransformationElement.AlternatedElement node);

    void visitAlternativeTransformation(
            AlternativeTransformation node);

    void visitAlternativeTransformationElement(
            AlternativeTransformationElement node);

    void visitAlternativeTransformationNullElement(
            AlternativeTransformationElement.NullElement node);

    void visitAlternativeTransformationReferenceElement(
            AlternativeTransformationElement.ReferenceElement node);

    void visitAlternativeTransformationNewElement(
            AlternativeTransformationElement.NewElement node);

    void visitAlternativeTransformationListElement(
            AlternativeTransformationElement.ListElement node);

    void visitAlternativeTransformationListElement(
            AlternativeTransformationListElement node);

    void visitAlternativeTransformationReferenceListElement(
            AlternativeTransformationListElement.ReferenceElement node);

    void visitAlternativeTransformationNormalListReferenceListElement(
            AlternativeTransformationListElement.NormalListElement node);

    void visitAlternativeTransformationLeftListReferenceListElement(
            AlternativeTransformationListElement.LeftListElement node);

    void visitAlternativeTransformationRightListReferenceListElement(
            AlternativeTransformationListElement.RightListElement node);

    void visitAlternativeTransformationNewListElement(
            AlternativeTransformationListElement.NewElement node);

    void visitTree(
            Tree node);

    void visitTreeProduction(
            Tree.TreeProduction node);

    void visitTreeAlternative(
            Tree.TreeAlternative node);

    void visitTreeElement(
            Tree.TreeElement node);

    void visitTreeNormalElement(
            Tree.TreeElement.NormalElement node);

    void visitTreeSeparatedElement(
            Tree.TreeElement.SeparatedElement node);

    void visitTreeAlternatedElement(
            Tree.TreeElement.AlternatedElement node);

}
