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

package org.sablecc.sablecc.alphabet;

import org.sablecc.exception.*;

/**
 * An interval is defined by a lower bound and an upper bound.
 */
public class Interval
        implements Comparable<Interval> {

    /**
     * The lower bound of this interval.
     */
    private final Bound lowerBound;

    /**
     * The upper bound of this interval.
     */
    private final Bound upperBound;

    /**
     * The cached hash code of this interval. It is <code>null</code> when not
     * yet computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this interval. It is
     * <code>null</code> when not yet computed.
     */
    private String toString;

    /**
     * Constructs an interval with the provided lower and upper bounds.
     */
    public Interval(
            Bound lowerBound,
            Bound upperBound) {

        if (lowerBound == null) {
            throw new InternalException("lower bound may not be null");
        }

        if (upperBound == null) {
            throw new InternalException("upper bound may not be null");
        }

        if (lowerBound.compareTo(upperBound) > 0) {
            throw new InternalException(
                    "lower bound must be smaller or equal to upper bound");
        }

        if (lowerBound == Bound.MAX) {
            throw new InternalException("lower bound may not be MAX");
        }

        if (upperBound == Bound.MIN) {
            throw new InternalException("upper bound may not be MIN");
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Constructs an interval with the provided bound used as both lower bound
     * and upper bound.
     */
    public Interval(
            Bound bound) {

        this(bound, bound);
    }

    /**
     * Constructs an interval with the provided bound used as both lower bound
     * and upper bound.
     */
    public Interval(
            char bound) {

        this(new Bound(bound));
    }

    /**
     * Constructs an interval with the provided bound used as both lower bound
     * and upper bound.
     */
    public Interval(
            String bound) {

        this(new Bound(bound));
    }

    /**
     * Constructs an interval with the provided bound used as both lower bound
     * and upper bound.
     */
    public Interval(
            String bound,
            int radix) {

        this(new Bound(bound, radix));
    }

    /**
     * Returns the lower bound of this interval.
     */
    public Bound getLowerBound() {

        return this.lowerBound;
    }

    /**
     * Returns the upper bound of this interval.
     */
    public Bound getUpperBound() {

        return this.upperBound;
    }

    /**
     * Returns <code>true</code> when the provided object is equal to this
     * interval.
     */
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

        Interval interval = (Interval) obj;

        return this.lowerBound.equals(interval.lowerBound)
                && this.upperBound.equals(interval.upperBound);
    }

    /**
     * Returns the hash code of this interval.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            this.hashCode = this.lowerBound.hashCode() * 103
                    + this.upperBound.hashCode() * 107;
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this interval.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            if (this.lowerBound.equals(this.upperBound)) {
                this.toString = this.lowerBound.toString();
            }
            else {
                this.toString = "[" + this.lowerBound + ".." + this.upperBound
                        + "]";
            }
        }

        return this.toString;
    }

    /**
     * Compares this interval to the provided interval. This interval is smaller
     * if its <code>lowerBound</code> is smaller, or if its
     * <code>lowerBound</code> is equal and its <code>upperBound</code> is
     * smaller.
     */
    @Override
    public int compareTo(
            Interval interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        int result = this.lowerBound.compareTo(interval.lowerBound);

        if (result == 0) {
            result = this.upperBound.compareTo(interval.upperBound);
        }

        return result;
    }

    /**
     * Tests whether this interval is adjacent to the provided interval. It is
     * adjacent if the <code>upperBound</code> of this interval is adjacent to
     * the <code>lowerBound</code> of the provided interval.
     */
    public boolean isAdjacentTo(
            Interval interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        if (this.upperBound.compareTo(interval.lowerBound) >= 0) {
            return false;
        }

        return this.upperBound.getSuccessor().equals(interval.lowerBound);
    }

    /**
     * Tests whether this interval intersects with the provided one.
     */
    public boolean intersects(
            Interval interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        return this.lowerBound.compareTo(interval.upperBound) <= 0
                && this.upperBound.compareTo(interval.lowerBound) >= 0;
    }

    /**
     * Creates a new interval representing the intersection between this
     * interval and the provided one. The result is <code>null</code> when they
     * do not intersect.
     */
    public Interval intersectWith(
            Interval interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        Bound lowerBound = Bound.max(this.lowerBound, interval.lowerBound);

        Bound upperBound = Bound.min(this.upperBound, interval.upperBound);

        if (lowerBound.compareTo(upperBound) <= 0) {
            return new Interval(lowerBound, upperBound);
        }

        return null;
    }

    /**
     * Creates a new interval that spans from the <code>lowerBound</code> of
     * this interval to the <code>upperBound</code> of the provided interval.
     * Merging fails if this interval is not adjacent to the provided interval.
     */
    public Interval mergeWith(
            Interval interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        if (!isAdjacentTo(interval)) {
            throw new InternalException("cannot merge non-adjacent intervals");
        }

        return new Interval(this.lowerBound, interval.upperBound);
    }

    /**
     * Returns the minimum of two intervals.
     */
    public static Interval min(
            Interval interval1,
            Interval interval2) {

        if (interval1 == null) {
            throw new InternalException("interval1 may not be null");
        }

        if (interval2 == null) {
            throw new InternalException("interval2 may not be null");
        }

        if (interval1.compareTo(interval2) <= 0) {
            return interval1;
        }

        return interval2;
    }

    /**
     * Returns the maximum of two intervals.
     */
    public static Interval max(
            Interval interval1,
            Interval interval2) {

        if (interval1 == null) {
            throw new InternalException("interval1 may not be null");
        }

        if (interval2 == null) {
            throw new InternalException("interval2 may not be null");
        }

        if (interval1.compareTo(interval2) >= 0) {
            return interval1;
        }

        return interval2;
    }

    public String getSimpleName() {

        return this.lowerBound.getSimpleName();
    }
}
