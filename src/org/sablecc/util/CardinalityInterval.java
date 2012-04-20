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

package org.sablecc.util;

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public final class CardinalityInterval
        extends DepthFirstAdapter {

    private Bound lowerBound;

    private Bound upperBound;

    public static final CardinalityInterval ZERO_ZERO = new CardinalityInterval(
            Bound.ZERO, Bound.ZERO);

    public static final CardinalityInterval ZERO_ONE = new CardinalityInterval(
            Bound.ZERO, Bound.ONE);

    public static final CardinalityInterval ONE_ONE = new CardinalityInterval(
            Bound.ONE, Bound.ONE);

    public static final CardinalityInterval ZERO_OR_MORE = new CardinalityInterval(
            Bound.ZERO);

    public static final CardinalityInterval ONE_OR_MORE = new CardinalityInterval(
            Bound.ONE);

    public CardinalityInterval(
            PUnaryOperator operator) {

        if (operator != null) {
            if (operator instanceof AZeroOrOneUnaryOperator) {
                this.lowerBound = Bound.ZERO;
                this.upperBound = Bound.ONE;
            }
            else {
                operator.apply(this);
            }
        }
        else {
            this.upperBound = Bound.ONE;
            this.lowerBound = Bound.ONE;
        }
    }

    public CardinalityInterval(
            PManyOperator operator) {

        operator.apply(this);
    }

    public CardinalityInterval(
            Bound lowerBound,
            Bound upperBound) {

        if (lowerBound == null) {
            throw new InternalException("lowBound shouldn't be null");
        }

        if (upperBound == null) {
            throw new InternalException("highBound shouldn't be null");
        }

        if (lowerBound.compareTo(Bound.ZERO) < 0) {
            throw new InternalException("lowBound shouldn't be negative");
        }

        if (upperBound.compareTo(Bound.ZERO) < 0) {
            throw new InternalException("lowBound shouldn't be negative");
        }

        if (upperBound.compareTo(lowerBound) < 0) {
            throw new InternalException(
                    "Lower bound can't be greater than upper bound");
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public CardinalityInterval(
            Bound lowerBound) {

        if (lowerBound == null) {
            throw new InternalException("lowBound shouldn't be null");
        }

        if (lowerBound.compareTo(Bound.ZERO) < 0) {
            throw new InternalException("lowBound shouldn't be negative");
        }

        this.lowerBound = lowerBound;
        this.upperBound = Bound.MAX;
    }

    public Bound getLowerBound() {

        return this.lowerBound;
    }

    public Bound getUpperBound() {

        return this.upperBound;
    }

    public boolean upperBoundIsInfinite() {

        return this.upperBound.equals(Bound.MAX);
    }

    public boolean isIncludedIn(
            CardinalityInterval anInterval) {

        if (this.lowerBound.compareTo(anInterval.getLowerBound()) >= 0
                && this.upperBound.compareTo(anInterval.getUpperBound()) <= 0) {
            return true;
        }

        return false;
    }

    public boolean isANumber() {

        return this.lowerBound.equals(this.upperBound);
    }

    public CardinalityInterval add(
            CardinalityInterval anInterval) {

        return new CardinalityInterval(
                this.lowerBound.add(anInterval.lowerBound),
                this.upperBound.add(anInterval.upperBound));
    }

    public CardinalityInterval union(
            CardinalityInterval anInterval) {

        return new CardinalityInterval(
                this.lowerBound.min(anInterval.lowerBound),
                this.upperBound.max(this.upperBound));
    }

    @Override
    public boolean equals(
            Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        CardinalityInterval interval = (CardinalityInterval) obj;

        return this.lowerBound.equals(interval.lowerBound)
                && this.upperBound.equals(interval.upperBound);
    }

    @Override
    public void inAZeroOrMoreManyOperator(
            AZeroOrMoreManyOperator node) {

        this.lowerBound = Bound.ZERO;
        this.upperBound = Bound.MAX;
    }

    @Override
    public void inAOneOrMoreManyOperator(
            AOneOrMoreManyOperator node) {

        this.lowerBound = Bound.ONE;
        this.upperBound = Bound.MAX;
    }

    @Override
    public void inANumberManyOperator(
            ANumberManyOperator node) {

        this.lowerBound = new Bound(node.getNumber().getText());
        this.upperBound = this.lowerBound;
    }

    @Override
    public void inAIntervalManyOperator(
            AIntervalManyOperator node) {

        this.lowerBound = new Bound(node.getFrom().getText());
        this.upperBound = new Bound(node.getTo().getText());
    }

    @Override
    public void inAAtLeastManyOperator(
            AAtLeastManyOperator node) {

        this.lowerBound = new Bound(node.getNumber().getText());
        this.upperBound = Bound.MAX;
    }

    @Override
    public String toString() {

        if (this.lowerBound.equals(Bound.ZERO)) {
            if (this.upperBound.equals(Bound.ONE)) {
                return "?";
            }
            else if (upperBoundIsInfinite()) {
                return "*";
            }
            else {
                return "^(" + this.lowerBound.getValue() + ".."
                        + this.upperBound.getValue() + ")";
            }
        }
        else if (this.lowerBound.equals(Bound.ONE) && upperBoundIsInfinite()) {
            return "+";
        }
        else if (this.lowerBound.equals(this.upperBound)) {
            return "^" + this.lowerBound.getValue();
        }
        else if (upperBoundIsInfinite()) {
            return "^" + this.lowerBound.getValue() + "..";
        }
        else {
            return "^(" + this.lowerBound.getValue() + ".."
                    + this.upperBound.getValue() + ")";
        }
    }
}
