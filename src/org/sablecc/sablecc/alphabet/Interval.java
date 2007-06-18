/* This file is part of SableCC (http://sablecc.org/).
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

import org.sablecc.sablecc.exception.InternalException;

/**
 * This class represents an interval.
 * 
 * An interval is defined by two bounds (upper and lower). This class provides
 * various methods to maniplutate intervals.
 */
public class Interval<T extends Comparable<? super T>>
        implements Comparable<Interval<T>> {

    /** The lower bound. */
    private final T lowerBound;

    /** The upper bound. */
    private final T upperBound;

    /** Adjacency realm of this interval. */
    private final AdjacencyRealm<T> adjacencyRealm;

    /** Cached hashcode. Is <code>null</code> when not yet computed. */
    private Integer hashCode;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /**
     * Constructs an interval with the provided lower and upper bounds.
     * Verifications are made that both bounds are provided and that
     * <code>lowerBound</code> <= <code>upperBound</code>.
     * 
     * @param lowerBound
     *            the lower bound.
     * @param upperBound
     *            the upper bound.
     * @param adjacencyRealm
     *            the adjacency realm of this interval.
     * @throws InternalException
     *             if any bound is <code>null</code> or if
     *             <code>lowerBound</code> > <code>upperBound</code>.
     */
    Interval(
            T lowerBound,
            T upperBound,
            AdjacencyRealm<T> adjacencyRealm) {

        if (lowerBound == null) {
            throw new InternalException("lower bound may not be null");
        }

        if (upperBound == null) {
            throw new InternalException("upper bound may not be null");
        }

        if (adjacencyRealm == null) {
            throw new InternalException("adjacency realm may not be null");
        }

        if (lowerBound.compareTo(upperBound) > 0) {
            throw new InternalException(
                    "lower bound must be smaller or equal to upper bound");
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.adjacencyRealm = adjacencyRealm;
    }

    /**
     * Constructs an interval with a single bound used as both lower and upper
     * bounds.
     * 
     * @param bound
     *            the bound. Used as both upper and lower bound.
     * @param adjacencyRealm
     *            the adjacency realm of this interval.
     */
    Interval(
            T bound,
            AdjacencyRealm<T> adjacencyRealm) {

        this(bound, bound, adjacencyRealm);
    }

    /**
     * Returns the lower bound of the interval.
     * 
     * @return an element T representing the <code>lowerBound</code>.
     */
    public T getLowerBound() {

        return this.lowerBound;
    }

    /**
     * Returns the upper bound of an interval.
     * 
     * @return an element T representing the <code>upperBound</code>.
     */
    public T getUpperBound() {

        return this.upperBound;
    }

    /**
     * Compares this interval with an object for equality. Returns
     * <code>true</code> if the object is an interval and if its bounds are
     * the same as those of this instance.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this interval and the object are equal;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(
            Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Interval)) {
            return false;
        }

        Interval interval = (Interval) obj;

        return this.lowerBound.equals(interval.lowerBound)
                && this.upperBound.equals(interval.upperBound)
                && this.adjacencyRealm == interval.adjacencyRealm;
    }

    /**
     * Returns a hash code value for this object.
     * 
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            this.hashCode = this.lowerBound.hashCode()
                    + this.upperBound.hashCode()
                    + this.adjacencyRealm.hashCode();
        }

        return this.hashCode;
    }

    /**
     * Returns a <code>String</code> representation for this interval. The
     * representation takes the following form:
     * <code>[lowerBound..upperBound]</code>
     * 
     * @return a <code>String</code> representing this interval.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "[" + this.lowerBound + ".." + this.upperBound
                    + "]";
        }

        return this.toString;
    }

    /**
     * Compares this interval to another one. This interval is smaller if its
     * <code>lowerBound</code> is smaller, or if its <code>lowerBound</code>
     * is equal and its <code>upperBound</code> is smaller.
     * 
     * @param interval
     *            the interval to compare with.
     * @return an <code>int</code> value: 0 if the two intervals are the
     *         equals, a negative value if this interval is smaller, and a
     *         positive value if it is bigger.
     */
    public int compareTo(
            Interval<T> interval) {

        if (this.adjacencyRealm != interval.adjacencyRealm) {
            throw new InternalException(
                    "cannot compare intervals from distinct realms");
        }

        int result = this.lowerBound.compareTo(interval.lowerBound);

        if (result == 0) {
            result = this.upperBound.compareTo(interval.upperBound);
        }

        return result;
    }

    /**
     * Tests whether the provided interval is adjacent to this one. It is
     * adjacent if this interval's <code>uppeBound</code> is adjacent to the
     * <code>lowerBound</code> of the provided interval.
     * 
     * @param interval
     *            the interval to test for adjacency.
     * @return <code>true</code> if the two intervals are adjacent;
     *         <code>false</code> otherwise.
     */
    public boolean isAdjacentTo(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        if (this.adjacencyRealm != interval.adjacencyRealm) {
            throw new InternalException(
                    "cannot test adjacency of intervals from distinct realms");
        }

        return this.adjacencyRealm.isAdjacent(this.upperBound,
                interval.lowerBound);
    }

    /**
     * Tests whether this interval intersects with the provided one. The two
     * intervals intersect if they share a commun subinterval.
     * 
     * @param interval
     *            the interval to compare with.
     * @return <code>true</code> if the two intervals intersect;
     *         <code>false</code> otherwise.
     */
    public boolean intersects(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        if (this.adjacencyRealm != interval.adjacencyRealm) {
            throw new InternalException(
                    "cannot intersect intervals from distinct realms");
        }

        return this.lowerBound.compareTo(interval.upperBound) <= 0
                && this.upperBound.compareTo(interval.lowerBound) >= 0;
    }

    /**
     * Creates an interval representing the intersection between this interval
     * and the provided one. The result is <code>null</code> when both
     * intervals do not intersect.
     * 
     * @param interval
     *            the interval to intersect with.
     * @return the intersection of the two intervals; <code>null</code> if
     *         it's not possible.
     */
    public Interval<T> intersection(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        if (this.adjacencyRealm != interval.adjacencyRealm) {
            throw new InternalException(
                    "cannot intersect intervals from distinct realms");
        }

        T lowerBound = max(this.lowerBound, interval.lowerBound);

        T upperBound = min(this.upperBound, interval.upperBound);

        if (lowerBound.compareTo(upperBound) <= 0) {
            return new Interval<T>(lowerBound, upperBound, this.adjacencyRealm);
        }

        return null;
    }

    /**
     * Creates a new interval that spans from this interval's
     * <code>lowerBound</code> to the provided interval's
     * <code>upperBound</code>. Merging fails if this interval is not
     * adjacent to the provided one.
     * 
     * @param interval
     *            the interval to merge this one with.
     * @return a new interval representing the merge of the two intervals.
     */
    public Interval<T> mergeWith(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        if (this.adjacencyRealm != interval.adjacencyRealm) {
            throw new InternalException(
                    "cannot merge intervals from distinct realms");
        }

        if (!isAdjacentTo(interval)) {
            throw new InternalException("cannot merge non-adjacent intervals");
        }

        return new Interval<T>(this.lowerBound, interval.upperBound,
                this.adjacencyRealm);
    }

    /**
     * Compares two bounds to find the minimum.
     * 
     * @param bound1
     *            a bound to compare.
     * @param bound2
     *            a bound to compare.
     * @return the lowest of the two bounds, or <code>bound1</code> in case of
     *         equality.
     */
    private static <T extends Comparable<? super T>> T min(
            T bound1,
            T bound2) {

        if (bound1 == null) {
            throw new InternalException("bound1 may not be null");
        }

        if (bound2 == null) {
            throw new InternalException("bound2 may not be null");
        }

        if (bound1.compareTo(bound2) <= 0) {
            return bound1;
        }

        return bound2;
    }

    /**
     * Compares two bounds to find the maximum.
     * 
     * @param bound1
     *            a bound to compare.
     * @param bound2
     *            a bound to compare.
     * @return the highest of the two bounds, or <code>bound1</code> in case
     *         of equality.
     */
    private static <T extends Comparable<? super T>> T max(
            T bound1,
            T bound2) {

        if (bound1 == null) {
            throw new InternalException("bound1 may not be null");
        }

        if (bound2 == null) {
            throw new InternalException("bound2 may not be null");
        }

        if (bound1.compareTo(bound2) >= 0) {
            return bound1;
        }

        return bound2;
    }

    /**
     * Compares two intervals and returns the lowest one (minimum).
     * 
     * @param interval1
     *            an interval to compare.
     * @param interval2
     *            an interval to compare.
     * @return the lowest of the two intervals, or <code>interval1</code> in
     *         case of equality.
     */
    public static <T extends Comparable<? super T>> Interval<T> min(
            Interval<T> interval1,
            Interval<T> interval2) {

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
     * Compares two intervals and returns the highest one (maximum).
     * 
     * @param interval1
     *            an interval to compare.
     * @param interval2
     *            an interval to compare.
     * @return the highest of the two intervals, or <code>interval1</code> in
     *         case of equality.
     */
    public static <T extends Comparable<? super T>> Interval<T> max(
            Interval<T> interval1,
            Interval<T> interval2) {

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
}
