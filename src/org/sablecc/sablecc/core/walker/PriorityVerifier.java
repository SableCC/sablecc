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

package org.sablecc.sablecc.core.walker;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.Parser.ParserElement.ElementType;
import org.sablecc.sablecc.core.Parser.ParserPriority.LeftPriority;
import org.sablecc.sablecc.core.Parser.ParserPriority.RightPriority;
import org.sablecc.sablecc.core.Parser.ParserPriority.UnaryPriority;
import org.sablecc.sablecc.core.Parser.ParserProduction;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.util.*;

public class PriorityVerifier
        extends GrammarVisitor {

    private final Grammar grammar;

    private Parser.ParserProduction production;

    public PriorityVerifier(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }
        this.grammar = grammar;
    }

    @Override
    public void visitParserProduction(
            ParserProduction node) {

        this.production = node;

        for (Parser.ParserPriority priority : node.getPriorities()) {
            priority.apply(this);
        }

        if (node.getPriorities().size() == 1
                && node.getPriorities().get(0) instanceof Parser.ParserPriority.UnaryPriority) {
            throw SemanticException.singleUnaryPriority(node.getPriorities()
                    .get(0));
        }
    }

    @Override
    public void visitLeftParserPriority(
            LeftPriority node) {

        for (Parser.ParserAlternative alternative : node.getAlternatives()) {

            if (alternative.getElements().size() < 3) {
                throw SemanticException.leftBadPattern(alternative, node);
            }

            Parser.ParserElement firstElement = alternative.getElements()
                    .getFirst();
            Parser.ParserElement lastElement = alternative.getElements()
                    .getLast();

            if (!(firstElement.getElementType() == ElementType.NORMAL && ((Parser.ParserElement.SingleElement) firstElement)
                    .getReference() == this.production)
                    || !(lastElement.getElementType() == ElementType.NORMAL && ((Parser.ParserElement.SingleElement) lastElement)
                            .getReference() == this.production)) {

                throw SemanticException.leftBadRecursion(alternative, node);

            }

            // First element verification
            if (!((Parser.ParserElement.SingleElement) firstElement)
                    .getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                throw SemanticException.leftQualifiedRecursiveElement(
                        alternative, firstElement, node);
            }

            // Second element verification
            if (alternative.getElements().get(1).getElementType() == ElementType.NORMAL) {
                Parser.ParserElement.SingleElement secondElement = (Parser.ParserElement.SingleElement) alternative
                        .getElements().get(1);

                if (!secondElement.getCardinality().equals(
                        CardinalityInterval.ONE_ONE)
                        || !(secondElement.getReference() == null || secondElement
                                .getReference() instanceof IToken)) {
                    throw SemanticException.leftBadSecondElement(alternative,
                            secondElement, node);
                }
            }
            else {
                throw SemanticException.leftNonNormalSecondElement(alternative,
                        alternative.getElements().get(1), node);
            }

            // Last element verification
            if (!((Parser.ParserElement.SingleElement) lastElement)
                    .getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                throw SemanticException.leftQualifiedRecursiveElement(
                        alternative, lastElement, node);
            }

        }

    }

    @Override
    public void visitRightParserPriority(
            RightPriority node) {

        for (Parser.ParserAlternative alternative : node.getAlternatives()) {

            if (alternative.getElements().size() < 3) {
                throw SemanticException.rightBadPattern(alternative, node);
            }

            Parser.ParserElement firstElement = alternative.getElements()
                    .getFirst();
            Parser.ParserElement lastElement = alternative.getElements()
                    .getLast();

            if (!(firstElement.getElementType() == ElementType.NORMAL && ((Parser.ParserElement.SingleElement) firstElement)
                    .getReference() == this.production)
                    || !(lastElement.getElementType() == ElementType.NORMAL && ((Parser.ParserElement.SingleElement) lastElement)
                            .getReference() == this.production)) {

                throw SemanticException.rightBadRecursion(alternative, node);

            }

            // First element verification
            if (!((Parser.ParserElement.SingleElement) firstElement)
                    .getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                throw SemanticException.rightQualifiedRecursiveElement(
                        alternative, firstElement, node);
            }

            // Second element verification
            if (alternative.getElements().get(1).getElementType() == ElementType.NORMAL) {
                Parser.ParserElement.SingleElement secondElement = (Parser.ParserElement.SingleElement) alternative
                        .getElements().get(1);

                if (!secondElement.getCardinality().equals(
                        CardinalityInterval.ONE_ONE)
                        || !(secondElement.getReference() == null || secondElement
                                .getReference() instanceof IToken)) {
                    throw SemanticException.rightBadSecondElement(alternative,
                            secondElement, node);
                }
            }
            else {
                throw SemanticException.rightNonNormalSecondElement(
                        alternative, alternative.getElements().get(1), node);
            }

            // Last element verification
            if (!((Parser.ParserElement.SingleElement) lastElement)
                    .getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                throw SemanticException.rightQualifiedRecursiveElement(
                        alternative, lastElement, node);
            }
        }
    }

    @Override
    public void visitUnaryParserPriority(
            UnaryPriority node) {

        boolean leftUnary = false;
        boolean rightUnary = false;

        for (Parser.ParserAlternative alternative : node.getAlternatives()) {

            boolean leftRecursive = false;

            Parser.ParserElement firstElement = alternative.getElements()
                    .getFirst();

            if (alternative.getElements().size() < 2) {
                throw SemanticException.unaryBadPattern(alternative, node);
            }

            // First be : determine whether or not the alternative
            // is well recursive.

            if (firstElement.getElementType() == ElementType.NORMAL
                    && ((Parser.ParserElement.SingleElement) firstElement)
                            .getReference() == this.production) {
                leftRecursive = true;
            }

            Parser.ParserElement lastElement = alternative.getElements()
                    .getLast();

            if (!((firstElement.getElementType() == ElementType.NORMAL && ((Parser.ParserElement.SingleElement) lastElement)
                    .getReference() == this.production) ^ leftRecursive)) {
                throw SemanticException.unaryBadRecursion(alternative, node);
            }

            // Second : verify recursive element cardinality and tokens
            // requirements.

            if (leftRecursive) {

                if (!((Parser.ParserElement.SingleElement) firstElement)
                        .getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                    throw SemanticException.unaryQualifiedRecursiveElement(
                            alternative, firstElement, node);
                }

                if (alternative.getElements().size() > 2) {
                    if (alternative.getElements().get(1).getElementType() == ElementType.NORMAL) {
                        Parser.ParserElement.SingleElement secondElement = (Parser.ParserElement.SingleElement) alternative
                                .getElements().get(1);

                        if (!secondElement.getCardinality().equals(
                                CardinalityInterval.ONE_ONE)
                                || !(secondElement.getReference() == null || secondElement
                                        .getReference() instanceof IToken)) {
                            throw SemanticException.unaryBadSecondElement(
                                    alternative, secondElement, node);
                        }
                    }
                    else {
                        throw SemanticException.unaryNonNormalSecondElement(
                                alternative, alternative.getElements().get(1),
                                node);
                    }
                }

                if (lastElement.getElementType() == ElementType.NORMAL) {
                    Parser.ParserElement.SingleElement lastNormalElement = (Parser.ParserElement.SingleElement) alternative
                            .getElements().getLast();

                    if (!lastElement.getCardinality().equals(
                            CardinalityInterval.ONE_ONE)
                            || !(lastNormalElement.getReference() == null || lastNormalElement
                                    .getReference() instanceof IToken)) {
                        throw SemanticException.unarybadLastElement(
                                alternative, lastElement, node);
                    }
                }
                else {
                    throw SemanticException.unaryNonNormalLastElement(
                            alternative, lastElement, node);
                }

            }
            else {

                if (!((Parser.ParserElement.SingleElement) lastElement)
                        .getCardinality().equals(CardinalityInterval.ONE_ONE)) {
                    throw SemanticException.unaryQualifiedRecursiveElement(
                            alternative, lastElement, node);
                }

                if (firstElement.getElementType() == ElementType.NORMAL) {
                    Parser.ParserElement.SingleElement firstNormalElement = (Parser.ParserElement.SingleElement) alternative
                            .getElements().getFirst();

                    if (!lastElement.getCardinality().equals(
                            CardinalityInterval.ONE_ONE)
                            || !(firstNormalElement.getReference() == null || firstNormalElement
                                    .getReference() instanceof IToken)) {
                        throw SemanticException.unaryBadFirstElement(
                                alternative, firstNormalElement, node);
                    }
                }
                else {
                    throw SemanticException.unaryNonNormalFirstElement(
                            alternative, firstElement, node);
                }
            }

            // Third : check the compatibility with previous alternatives.
            if (leftRecursive) {

                if (leftUnary || !rightUnary) {
                    leftUnary = true;
                }
                else {
                    throw SemanticException.mixedUnaryPriorities(node);
                }
            }
            else {
                if (rightUnary || !leftUnary) {
                    rightUnary = true;
                }
                else {
                    throw SemanticException.mixedUnaryPriorities(node);
                }
            }

        }

    }
}
