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
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class Expression
        implements IVisitableGrammarPart {

    final Grammar grammar;

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

    public abstract Automaton getAutomaton();

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

        public AOrExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitOrExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.left.getAutomaton().or(this.right.getAutomaton());
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

        public AConcatenationExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitConcatenationExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.left.getAutomaton().concat(this.right.getAutomaton());
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

        public ALookExpression getDeclaration() {

            return this.declaration;
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

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitLookExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            Automaton automaton = this.expression.getAutomaton();

            if (this.lookback != null) {
                throw new InternalException("not implemented");
            }

            if (this.lookahead != null) {
                if (this.lookahead.getNot()) {
                    automaton = automaton.lookNot(this.lookahead
                            .getExpression().getAutomaton());
                }
                else {
                    automaton = automaton.look(this.lookahead.getExpression()
                            .getAutomaton());
                }
            }

            return automaton;
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

        public ALookback getDeclaration() {

            return this.declaration;
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

        public ALookahead getDeclaration() {

            return this.declaration;
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

        public AShortestExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getExpression() {

            return this.expression;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitShortestExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.expression.getAutomaton().shortest();
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

        public ALongestExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getExpression() {

            return this.expression;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitLongestExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.expression.getAutomaton().longest();
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

        public ASubtractionExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitSubtractionExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.left.getAutomaton().subtract(this.right.getAutomaton());
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

        public AExceptExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitExceptExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.left.getAutomaton().except(this.right.getAutomaton());
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

        public AIntersectionExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getLeft() {

            return this.left;
        }

        public Expression getRight() {

            return this.right;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitIntersectionExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.left.getAutomaton().and(this.right.getAutomaton());
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

        public AUnaryOperatorExpression getDeclaration() {

            return this.declaration;
        }

        public Expression getExpression() {

            return this.expression;
        }

        public UnaryOperator getOperator() {

            return this.operator;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitUnaryExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.operator.getAutomaton(this.expression.getAutomaton());
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

        public ASeparatedExpression getDeclaration() {

            return this.declaration;
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

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitSeparatedExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return this.operator.getAutomaton(this.base.getAutomaton(),
                    this.separator.getAutomaton());
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

        public abstract Automaton getAutomaton(
                Automaton automaton);
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

        public AZeroOrOneUnaryOperator getDeclaration() {

            return this.declaration;
        }

        @Override
        public Automaton getAutomaton(
                Automaton automaton) {

            return automaton.zeroOrOne();
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

        public abstract Automaton getAutomaton(
                Automaton base,
                Automaton separator);
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

        public AZeroOrMoreManyOperator getDeclaration() {

            return this.declaration;
        }

        @Override
        public Automaton getAutomaton(
                Automaton automaton) {

            return automaton.zeroOrMore();
        }

        @Override
        public Automaton getAutomaton(
                Automaton base,
                Automaton separator) {

            return base.zeroOrMoreWithSeparator(separator);
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

        public AOneOrMoreManyOperator getDeclaration() {

            return this.declaration;
        }

        @Override
        public Automaton getAutomaton(
                Automaton automaton) {

            return automaton.oneOrMore();
        }

        @Override
        public Automaton getAutomaton(
                Automaton base,
                Automaton separator) {

            return base.oneOrMoreWithSeparator(separator);
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

        public ANumberManyOperator getDeclaration() {

            return this.declaration;
        }

        @Override
        public Automaton getAutomaton(
                Automaton automaton) {

            return automaton.nTimes(new BigInteger(this.declaration.getNumber()
                    .getText()));
        }

        @Override
        public Automaton getAutomaton(
                Automaton base,
                Automaton separator) {

            return base.nTimesWithSeparator(separator, new BigInteger(
                    this.declaration.getNumber().getText()));
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

        public AIntervalManyOperator getDeclaration() {

            return this.declaration;
        }

        @Override
        public Automaton getAutomaton(
                Automaton automaton) {

            return automaton.nToM(new BigInteger(this.declaration.getFrom()
                    .getText()), new BigInteger(this.declaration.getTo()
                    .getText()));
        }

        @Override
        public Automaton getAutomaton(
                Automaton base,
                Automaton separator) {

            return base.nToMWithSeparator(separator, new BigInteger(
                    this.declaration.getFrom().getText()), new BigInteger(
                    this.declaration.getTo().getText()));
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

        public AAtLeastManyOperator getDeclaration() {

            return this.declaration;
        }

        @Override
        public Automaton getAutomaton(
                Automaton automaton) {

            return automaton.nOrMore(new BigInteger(this.declaration
                    .getNumber().getText()));
        }

        @Override
        public Automaton getAutomaton(
                Automaton base,
                Automaton separator) {

            return base.nOrMoreWithSeparator(separator, new BigInteger(
                    this.declaration.getNumber().getText()));
        }

    }

    public static class NameUnit
            extends Expression {

        private final ANameUnit declaration;

        private LexerExpression reference;

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

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitNameUnitExpression(this);
        }

        public ANameUnit getDeclaration() {

            return this.declaration;
        }

        public LexerExpression getReference() {

            LexerExpression reference = this.reference;

            if (reference == null) {
                TIdentifier referenceIdentifier = getDeclaration()
                        .getIdentifier();

                INameDeclaration declaration = this.grammar
                        .getGlobalReference(referenceIdentifier.getText());

                if (declaration == null) {
                    throw SemanticException
                            .undefinedReference(referenceIdentifier);
                }

                if (!(declaration instanceof LexerExpression)) {
                    String[] expectedNames = { "Expression" };
                    throw SemanticException.badReference(referenceIdentifier,
                            declaration.getNameType(), expectedNames);
                }

                reference = (LexerExpression) declaration;
                this.reference = reference;
            }

            return reference;
        }

        @Override
        public Automaton getAutomaton() {

            return getReference().getAutomaton();
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

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitStringUnitExpression(this);
        }

        public AStringUnit getDeclaration() {

            return this.declaration;
        }

        @Override
        public Automaton getAutomaton() {

            String string = getString();
            Automaton automaton = Automaton.getSymbolLookAnyStarEnd(new Symbol(
                    string.charAt(0)));
            string = string.substring(1);

            while (string.length() > 0) {
                automaton = automaton.concat(Automaton
                        .getSymbolLookAnyStarEnd(new Symbol(string.charAt(0))));
                string = string.substring(1);
            }

            return automaton;
        }

    }

    public static abstract class CharacterUnit
            extends Expression {

        private CharacterUnit(
                Grammar grammar) {

            super(grammar);
        }

        public abstract BigInteger getValue();

        @Override
        public Automaton getAutomaton() {

            return Automaton.getSymbolLookAnyStarEnd(new Symbol(new Bound(
                    getValue())));
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
        }

        public ACharCharacter getDeclaration() {

            return this.declaration;
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

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitCharUnitExpression(this);
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

        public ADecCharacter getDeclaration() {

            return this.declaration;
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

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitDecCharUnitExpression(this);
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

        public AHexCharacter getDeclaration() {

            return this.declaration;
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

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitHexCharUnitExpression(this);
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

        public AStartUnit getDeclaration() {

            return this.declaration;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitStartUnitExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            throw new InternalException("not implemented");
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

        public AEndUnit getDeclaration() {

            return this.declaration;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitEndUnitExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return Automaton.getEpsilonLookEnd();
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

        public AEpsilonExpression getDeclaration() {

            return this.declaration;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitEpsilonExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return Automaton.getEpsilonLookAnyStarEnd();
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

        public CharacterUnit getFrom() {

            return this.from;
        }

        public CharacterUnit getTo() {

            return this.to;
        }

        public AIntervalExpression getDeclaration() {

            return this.declaration;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitIntervalExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return Automaton.getSymbolLookAnyStarEnd(new Symbol(
                    new org.sablecc.sablecc.alphabet.Interval(new Bound(
                            this.from.getValue()),
                            new Bound(this.to.getValue()))));
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

        public AAnyExpression getDeclaration() {

            return this.declaration;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitAnyExpression(this);
        }

        @Override
        public Automaton getAutomaton() {

            return Automaton.getSymbolLookAnyStarEnd(new Symbol(
                    new org.sablecc.sablecc.alphabet.Interval(Bound.MIN,
                            Bound.MAX)));
        }
    }
}
