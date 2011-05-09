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
            final Grammar grammar) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        class ExpressionBuilder
                extends DepthFirstAdapter {

            private Expression expression;

            private Lookahead lookahead;

            private Lookback lookback;

            private void visit(
                    Node node) {

                if (node != null) {
                    node.apply(this);
                }
            }

            public Expression getExpression(
                    PExpression node) {

                this.expression = null;
                visit(node);
                Expression expression = this.expression;
                this.expression = null;
                return expression;
            }

            public Expression getExpression(
                    PUnit node) {

                this.expression = null;
                visit(node);
                Expression expression = this.expression;
                this.expression = null;
                return expression;
            }

            public CharacterUnit getExpression(
                    PCharacter node) {

                this.expression = null;
                visit(node);
                Expression expression = this.expression;
                this.expression = null;
                return (CharacterUnit) expression;
            }

            public Lookback getLookback(
                    PLookback node) {

                this.lookback = null;
                visit(node);
                Lookback lookback = this.lookback;
                this.lookback = null;
                return lookback;
            }

            public Lookahead getLookahead(
                    PLookahead node) {

                this.lookahead = null;
                visit(node);
                Lookahead lookahead = this.lookahead;
                this.lookahead = null;
                return lookahead;
            }

            @Override
            public void caseAOrExpression(
                    AOrExpression node) {

                Expression left = getExpression(node.getLeft());
                Expression right = getExpression(node.getRight());
                this.expression = new Or(node, grammar, left, right);
            }

            @Override
            public void caseAConcatenationExpression(
                    AConcatenationExpression node) {

                Expression left = getExpression(node.getLeft());
                Expression right = getExpression(node.getRight());
                this.expression = new Concatenation(node, grammar, left, right);
            }

            @Override
            public void caseALookExpression(
                    ALookExpression node) {

                Expression expression = getExpression(node.getExpression());
                Lookback lookback = getLookback(node.getLookback());
                Lookahead lookahead = getLookahead(node.getLookahead());
                this.expression = new Look(node, grammar, expression, lookback,
                        lookahead);
            }

            @Override
            public void caseAShortestExpression(
                    AShortestExpression node) {

                Expression expression = getExpression(node.getExpression());
                this.expression = new Shortest(node, grammar, expression);
            }

            @Override
            public void caseALongestExpression(
                    ALongestExpression node) {

                Expression expression = getExpression(node.getExpression());
                this.expression = new Longest(node, grammar, expression);
            }

            @Override
            public void caseASubtractionExpression(
                    ASubtractionExpression node) {

                Expression left = getExpression(node.getLeft());
                Expression right = getExpression(node.getRight());
                this.expression = new Subtraction(node, grammar, left, right);
            }

            @Override
            public void caseAExceptExpression(
                    AExceptExpression node) {

                Expression left = getExpression(node.getLeft());
                Expression right = getExpression(node.getRight());
                this.expression = new Except(node, grammar, left, right);
            }

            @Override
            public void caseAIntersectionExpression(
                    AIntersectionExpression node) {

                Expression left = getExpression(node.getLeft());
                Expression right = getExpression(node.getRight());
                this.expression = new Intersection(node, grammar, left, right);
            }

            @Override
            public void caseAUnaryOperatorExpression(
                    AUnaryOperatorExpression node) {

                Expression expression = getExpression(node.getExpression());
                UnaryOperator operator = UnaryOperator.newUnaryOperator(
                        node.getUnaryOperator(), grammar);
                this.expression = new Unary(node, grammar, expression, operator);
            }

            @Override
            public void caseASeparatedExpression(
                    ASeparatedExpression node) {

                Expression base = getExpression(node.getBase());
                Expression separator = getExpression(node.getSeparator());
                ManyOperator operator = ManyOperator.newManyOperator(
                        node.getManyOperator(), grammar);
                this.expression = new Separated(node, grammar, base, separator,
                        operator);
            }

            @Override
            public void caseAEpsilonExpression(
                    AEpsilonExpression node) {

                this.expression = new Epsilon(node, grammar);
            }

            @Override
            public void caseAIntervalExpression(
                    AIntervalExpression node) {

                CharacterUnit from = getExpression(node.getFrom());
                CharacterUnit to = getExpression(node.getTo());
                this.expression = new Interval(node, grammar, from, to);
            }

            @Override
            public void caseAAnyExpression(
                    AAnyExpression node) {

                this.expression = new Any(node, grammar);
            }

            @Override
            public void caseALookback(
                    ALookback node) {

                Expression expression = getExpression(node.getExpression());
                this.lookback = new Lookback(node, grammar, expression);
            }

            @Override
            public void caseALookahead(
                    ALookahead node) {

                Expression expression = getExpression(node.getExpression());
                this.lookahead = new Lookahead(node, grammar, expression);
            }

            @Override
            public void caseACharCharacter(
                    ACharCharacter node) {

                this.expression = new CharUnit(node, grammar);
            }

            @Override
            public void caseADecCharacter(
                    ADecCharacter node) {

                this.expression = new DecCharUnit(node, grammar);
            }

            @Override
            public void caseAHexCharacter(
                    AHexCharacter node) {

                this.expression = new HexCharUnit(node, grammar);
            }

            @Override
            public void caseANameUnit(
                    ANameUnit node) {

                this.expression = new NameUnit(node, grammar);
            }

            @Override
            public void caseAStringUnit(
                    AStringUnit node) {

                this.expression = new StringUnit(node, grammar);
            }

            @Override
            public void caseAStartUnit(
                    AStartUnit node) {

                this.expression = new StartUnit(node, grammar);
            }

            @Override
            public void caseAEndUnit(
                    AEndUnit node) {

                this.expression = new EndUnit(node, grammar);
            }
        }

        return new ExpressionBuilder().getExpression(declaration);
    }

    public static class Or
            extends Expression {

        private final AOrExpression declaration;

        private final Expression left;

        private final Expression right;

        private Or(
                AOrExpression declaration,
                Grammar grammar,
                Expression left,
                Expression right) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.left = left;
            this.right = right;
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
                Grammar grammar,
                Expression left,
                Expression right) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.left = left;
            this.right = right;
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
                Grammar grammar,
                Expression expression,
                Lookback lookback,
                Lookahead lookahead) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.expression = expression;
            this.lookback = lookback;
            this.lookahead = lookahead;
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
                Grammar grammar,
                Expression expression) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
            this.declaration = declaration;
            this.not = declaration.getNotKeyword() != null;
            this.expression = expression;
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
                Grammar grammar,
                Expression expression) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
            this.declaration = declaration;
            this.not = declaration.getNotKeyword() != null;
            this.expression = expression;
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
                Grammar grammar,
                Expression expression) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.expression = expression;
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
                Grammar grammar,
                Expression expression) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.expression = expression;
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
                Grammar grammar,
                Expression left,
                Expression right) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.left = left;
            this.right = right;
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
                Grammar grammar,
                Expression left,
                Expression right) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.left = left;
            this.right = right;
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
                Grammar grammar,
                Expression left,
                Expression right) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.left = left;
            this.right = right;
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
                Grammar grammar,
                Expression expression,
                UnaryOperator operator) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.expression = expression;
            this.operator = operator;
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
                Grammar grammar,
                Expression base,
                Expression separator,
                ManyOperator operator) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.base = base;
            this.separator = separator;
            this.operator = operator;
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

        public abstract BigInteger getValue();
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
        }

        @Override
        public BigInteger getValue() {

            String text = this.declaration.getChar().getText();
            // remove enclosing quotes
            text = text.substring(1, text.length() - 1);
            // replace escape chars
            text = text.replace("\\\\", "\\");
            text = text.replace("\\'", "'");
            if (text.length() != 1) {
                throw new InternalException("unhandled character escape");
            }
            return new BigInteger("" + (int) text.charAt(0));
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
        }

        @Override
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
        }

        @Override
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
        }
    }

    public static class Interval
            extends Expression {

        private final AIntervalExpression declaration;

        private final CharacterUnit from;

        private final CharacterUnit to;

        private Interval(
                AIntervalExpression declaration,
                Grammar grammar,
                CharacterUnit from,
                CharacterUnit to) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
            this.from = from;
            this.to = to;
        }
    }

    public static class Any
            extends Expression {

        private final AAnyExpression declaration;

        private Any(
                AAnyExpression declaration,
                Grammar grammar) {

            super(grammar);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }
    }
}
