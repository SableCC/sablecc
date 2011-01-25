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

import java.math.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class Expression {

    private final Grammar grammar;

    private Expression(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    static Expression newExpression(
            PExpression declaration,
            Grammar grammar) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        throw new InternalException("not implemented");
    }

    public static class Or
            extends Expression {

        private final AOrExpression declaration;

        private final Expression left;

        private final Expression right;

        private Or(
                AOrExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.left = grammar.getExpressionMapping(declaration.getLeft());
            this.right = grammar.getExpressionMapping(declaration.getRight());
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }
    }

    public static class Concatenation
            extends Expression {

        private final AConcatenationExpression declaration;

        private final Expression left;

        private final Expression right;

        private Concatenation(
                AConcatenationExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.left = grammar.getExpressionMapping(declaration.getLeft());
            this.right = grammar.getExpressionMapping(declaration.getRight());
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }
    }

    public static class Look
            extends Expression {

        private final ALookExpression declaration;

        private final Expression expression;

        private final Lookback lookback;

        private final Lookahead lookahead;

        private Look(
                ALookExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());

            if (declaration.getLookback() != null) {
                this.lookback = grammar.getLookbackMapping(declaration
                        .getLookback());
            }
            else {
                this.lookback = null;
            }

            if (declaration.getLookahead() != null) {
                this.lookahead = grammar.getLookaheadMapping(declaration
                        .getLookahead());
            }
            else {
                this.lookahead = null;
            }

            if (this.lookahead == null && this.lookback == null) {
                throw new InternalException(
                        "lookahead and lookback may not be both null");
            }
        }

        public Expression getExpression() {

            return this.expression;
        }

        public Lookback getLookback() {

            return this.lookback;
        }

        public Lookahead getLookahead() {

            return this.lookahead;
        }
    }

    public static class Lookback {

        private final Grammar grammar;

        private final ALookback declaration;

        private boolean not;

        private final Expression expression;

        private Lookback(
                ALookback declaration,
                Grammar grammar) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.not = declaration.getNotKeyword() != null;
            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());
        }

        public boolean getNot() {

            return this.not;
        }

        public Expression getExpression() {

            return this.expression;
        }
    }

    public static class Lookahead {

        private final Grammar grammar;

        private final ALookahead declaration;

        private boolean not;

        private final Expression expression;

        private Lookahead(
                ALookahead declaration,
                Grammar grammar) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
            this.declaration = declaration;
            grammar.addMapping(declaration, this);
            this.not = declaration.getNotKeyword() != null;
            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());
        }

        public boolean getNot() {

            return this.not;
        }

        public Expression getExpression() {

            return this.expression;
        }
    }

    public static class Shortest
            extends Expression {

        private final AShortestExpression declaration;

        private final Expression expression;

        private Shortest(
                AShortestExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());
        }

        public Expression getExpression() {

            return this.expression;
        }
    }

    public static class Longest
            extends Expression {

        private final ALongestExpression declaration;

        private final Expression expression;

        private Longest(
                ALongestExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());
        }

        public Expression getExpression() {

            return this.expression;
        }
    }

    public static class Subtraction
            extends Expression {

        private final ASubtractionExpression declaration;

        private final Expression left;

        private final Expression right;

        private Subtraction(
                ASubtractionExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.left = grammar.getExpressionMapping(declaration.getLeft());
            this.right = grammar.getExpressionMapping(declaration.getRight());
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }
    }

    public static class Except
            extends Expression {

        private final AExceptExpression declaration;

        private final Expression left;

        private final Expression right;

        private Except(
                AExceptExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.left = grammar.getExpressionMapping(declaration.getLeft());
            this.right = grammar.getExpressionMapping(declaration.getRight());
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }
    }

    public static class Intersection
            extends Expression {

        private final AIntersectionExpression declaration;

        private final Expression left;

        private final Expression right;

        private Intersection(
                AIntersectionExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.left = grammar.getExpressionMapping(declaration.getLeft());
            this.right = grammar.getExpressionMapping(declaration.getRight());
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }
    }

    public static class Unary
            extends Expression {

        private final AUnaryOperatorExpression declaration;

        private final Expression expression;

        private final UnaryOperator operator;

        private Unary(
                AUnaryOperatorExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());
            this.operator = UnaryOperator.newUnaryOperator(
                    declaration.getUnaryOperator(), grammar);
        }

        public Expression getExpression() {

            return this.expression;
        }

        public UnaryOperator getOperator() {

            return this.operator;
        }
    }

    public static class Separated
            extends Expression {

        private final ASeparatedExpression declaration;

        private final Expression base;

        private final Expression separator;

        private final ManyOperator operator;

        private Separated(
                ASeparatedExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.base = grammar.getExpressionMapping(declaration.getBase());
            this.separator = grammar.getExpressionMapping(declaration
                    .getSeparator());
            this.operator = ManyOperator.newManyOperator(
                    declaration.getManyOperator(), grammar);
        }

        public Expression getBase() {

            return this.base;
        }

        public Expression getSeparator() {

            return this.separator;
        }

        public ManyOperator getOperator() {

            return this.operator;
        }
    }

    public static abstract class UnaryOperator {

        private final Grammar grammar;

        private UnaryOperator(
                Grammar grammar) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
        }

        private static UnaryOperator newUnaryOperator(
                PUnaryOperator unaryOperator,
                Grammar grammar) {

            if (unaryOperator == null) {
                throw new InternalException("unaryOperator may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            if (unaryOperator instanceof AZeroOrOneUnaryOperator) {
                return new ZeroOrOneOperator(
                        (AZeroOrOneUnaryOperator) unaryOperator, grammar);
            }

            return ManyOperator.newManyOperator(
                    ((AManyUnaryOperator) unaryOperator).getManyOperator(),
                    grammar);
        }
    }

    public static class ZeroOrOneOperator
            extends UnaryOperator {

        private final AZeroOrOneUnaryOperator declaration;

        private ZeroOrOneOperator(
                AZeroOrOneUnaryOperator declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

    }

    public static abstract class ManyOperator
            extends UnaryOperator {

        private ManyOperator(
                Grammar grammar) {

            super(grammar);
        }

        private static ManyOperator newManyOperator(
                PManyOperator manyOperator,
                final Grammar grammar) {

            if (manyOperator == null) {
                throw new InternalException("manyOperator may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            class Result {

                ManyOperator manyOperator;
            }

            final Result result = new Result();

            manyOperator.apply(new AnalysisAdapter() {

                @Override
                public void caseAZeroOrMoreManyOperator(
                        AZeroOrMoreManyOperator node) {

                    result.manyOperator = new ZeroOrMoreOperator(node, grammar);
                }

                @Override
                public void caseAOneOrMoreManyOperator(
                        AOneOrMoreManyOperator node) {

                    result.manyOperator = new OneOrMoreOperator(node, grammar);
                }

                @Override
                public void caseANumberManyOperator(
                        ANumberManyOperator node) {

                    result.manyOperator = new NumberExponentOperator(node,
                            grammar);
                }

                @Override
                public void caseAIntervalManyOperator(
                        AIntervalManyOperator node) {

                    result.manyOperator = new IntervalExponentOperator(node,
                            grammar);
                }

                @Override
                public void caseAAtLeastManyOperator(
                        AAtLeastManyOperator node) {

                    result.manyOperator = new AtLeastOperator(node, grammar);
                }

                @Override
                public void defaultCase(
                        Node node) {

                    throw new InternalException("missing case");
                }
            });

            if (result.manyOperator == null) {
                throw new InternalException("missing case");
            }

            return result.manyOperator;
        }
    }

    public static class ZeroOrMoreOperator
            extends ManyOperator {

        private final AZeroOrMoreManyOperator declaration;

        private ZeroOrMoreOperator(
                AZeroOrMoreManyOperator declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }
    }

    public static class OneOrMoreOperator
            extends ManyOperator {

        private final AOneOrMoreManyOperator declaration;

        private OneOrMoreOperator(
                AOneOrMoreManyOperator declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }
    }

    public static class NumberExponentOperator
            extends ManyOperator {

        private final ANumberManyOperator declaration;

        private NumberExponentOperator(
                ANumberManyOperator declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }
    }

    public static class IntervalExponentOperator
            extends ManyOperator {

        private final AIntervalManyOperator declaration;

        private IntervalExponentOperator(
                AIntervalManyOperator declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }
    }

    public static class AtLeastOperator
            extends ManyOperator {

        private final AAtLeastManyOperator declaration;

        private AtLeastOperator(
                AAtLeastManyOperator declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }
    }

    public static class NameUnit
            extends Expression {

        private final ANameUnit declaration;

        private NameUnit(
                ANameUnit declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }

        public TIdentifier getNameIdentifier() {

            return this.declaration.getIdentifier();
        }

        public String getName() {

            return getNameIdentifier().getText();
        }
    }

    public static class StringUnit
            extends Expression {

        private final AStringUnit declaration;

        private StringUnit(
                AStringUnit declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }

        public String getString() {

            String text = this.declaration.getString().getText();
            // remove enclosing quotes
            text = text.substring(1, text.length() - 1);
            // replace escape chars
            text = text.replace("\\\\", "\\");
            text = text.replace("\\'", "'");
            return text;
        }
    }

    public static abstract class CharacterUnit
            extends Expression {

        private CharacterUnit(
                Grammar grammar) {

            super(grammar);
        }
    }

    public static class CharUnit
            extends CharacterUnit {

        private final ACharCharacter declaration;

        private CharUnit(
                ACharCharacter declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }

        public char getChar() {

            String text = this.declaration.getChar().getText();
            // remove enclosing quotes
            text = text.substring(1, text.length() - 1);
            // replace escape chars
            text = text.replace("\\\\", "\\");
            text = text.replace("\\'", "'");
            if (text.length() != 1) {
                throw new InternalException("unhandled character escape");
            }
            return text.charAt(0);
        }
    }

    public static class DecCharUnit
            extends CharacterUnit {

        private final ADecCharacter declaration;

        private DecCharUnit(
                ADecCharacter declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }

        public BigInteger getValue() {

            String text = this.declaration.getDecChar().getText();
            // remove '#' prefix
            text = text.substring(1);
            // remove leading '+' if there is one
            if (text.charAt(0) == '+') {
                text = text.substring(1);
            }

            return new BigInteger(text);
        }
    }

    public static class HexCharUnit
            extends CharacterUnit {

        private final AHexCharacter declaration;

        private HexCharUnit(
                AHexCharacter declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }

        public BigInteger getValue() {

            String text = this.declaration.getHexChar().getText();
            // remove '#x' prefix
            text = text.substring(2);
            // remove leading '+' if there is one
            if (text.charAt(0) == '+') {
                text = text.substring(1);
            }

            return new BigInteger(text, 16);
        }
    }

    public static class StartUnit
            extends Expression {

        private final AStartUnit declaration;

        private StartUnit(
                AStartUnit declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }
    }

    public static class EndUnit
            extends Expression {

        private final AEndUnit declaration;

        private EndUnit(
                AEndUnit declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }
    }

    public static class Epsilon
            extends Expression {

        private final AEpsilonExpression declaration;

        private Epsilon(
                AEpsilonExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            grammar.addMapping(declaration, this);
        }
    }
}
