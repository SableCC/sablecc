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
import org.sablecc.sablecc.syntax3.node.*;

public abstract class Expression {

    private final Grammar grammar;

    private Expression(
            Grammar grammar) {

        this.grammar = grammar;
    }

    public static Expression newExpression(
            PExpression declaration,
            Grammar grammar) {

        throw new InternalException("not implemented");
    }

    public static class Or
            extends Expression {

        private final AOrExpression declaration;

        private final Expression left;

        private final Expression right;

        public Or(
                AOrExpression declaration,
                Grammar grammar) {

            super(grammar);
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

        public Concatenation(
                AConcatenationExpression declaration,
                Grammar grammar) {

            super(grammar);
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

        public Look(
                ALookExpression declaration,
                Grammar grammar) {

            super(grammar);
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

        public Lookback(
                ALookback declaration,
                Grammar grammar) {

            this.grammar = grammar;
            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.not = declaration.getNotKeyword() != null;
            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());
        }
    }

    public static class Lookahead {

        private final Grammar grammar;

        private final ALookahead declaration;

        private boolean not;

        private final Expression expression;

        public Lookahead(
                ALookahead declaration,
                Grammar grammar) {

            this.grammar = grammar;
            this.declaration = declaration;
            grammar.addMapping(declaration, this);
            this.not = declaration.getNotKeyword() != null;
            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());
        }
    }

    public static class Shortest
            extends Expression {

        private final AShortestExpression declaration;

        private final Expression expression;

        public Shortest(
                AShortestExpression declaration,
                Grammar grammar) {

            super(grammar);
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

        public Longest(
                ALongestExpression declaration,
                Grammar grammar) {

            super(grammar);
            this.declaration = declaration;
            grammar.addMapping(declaration, this);

            this.expression = grammar.getExpressionMapping(declaration
                    .getExpression());
        }

        public Expression getExpression() {

            return this.expression;
        }
    }
}
