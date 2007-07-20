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

import org.sablecc.sablecc.exception.InternalException;

/**
 * An interval is defined by two bounds (lower and upper). This class provides
 * various methods to maniplutate intervals.
 * <p>
 * Intervals are primarily created using
 * <code>AdjacencyRealm.createInterval()</code>.
 */
public final class Interval<T extends Comparable<? super T>>
        implements Comparable<Interval<T>> {

    /** The lower bound. */
    private final T lowerBound;

    /** The upper bound. */
    private final T upperBound;

    /** The adjacency realm of this interval. */
    private final AdjacencyRealm<T> adjacencyRealm;

    /** Cached hashcode. Is <code>null</code> when not yet computed. */
    private Integer hashCode;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /**
     * Constructs an interval with the provided lower and upper bounds within
     * the provided adjacency realm.
     * 
     * @param lowerBound
     *            the lower bound.
     * @param upperBound
     *            the upper bound.
     * @param adjacencyRealm
     *            the adjacency realm.
     * @throws InternalException
     *             if any bound is <code>null</code>, if the adjacency realm
     *             is <code>null</code>, or if <code>(lowerBound &gt;
     *             upperBound)</code>.
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
     * bounds within the provided adjacency realm.
     * 
     * @param bound
     *            the bound. Used as both upper and lower bound.
     * @param adjacencyRealm
     *            the adjacency realm.
     */
    Interval(
            T bound,
            AdjacencyRealm<T> adjacencyRealm) {

        this(bound, bound, adjacencyRealm);
    }

    /**
     * Returns the lower bound of this interval.
     * 
     * @return the lower bound.
     */
    public T getLowerBound() {

        return this.lowerBound;
    }

    /**
     * Returns the upper bound of this interval.
     * 
     * @return the upper bound.
     */
    public T getUpperBound() {

        return this.upperBound;
    }

    /**
     * Return the adjacency realm of this interval.
     * 
     * @return the adjacency realm.
     */
    public AdjacencyRealm<T> getAdjacencyRealm() {

        return this.adjacencyRealm;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they have equal lower and upper bounds and they belong to the
     * same adjacency realm.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this interval and the object are equal;
     *         <code>false</code> otherwise.
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
                && this.upperBound.equals(interval.upperBound)
                && this.adjacencyRealm == interval.adjacencyRealm;
    }

    /**
     * Returns the hash code of this interval.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            this.hashCode = this.lowerBound.hashCode() * 121
                    + this.upperBound.hashCode() * 11
                    + this.adjacencyRealm.hashCode();
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this interval.
     * 
     * @return the string representation.
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
     * Compares this interval to the provided one. This interval is smaller if
     * its <code>lowerBound</code> is smaller, or if its
     * <code>lowerBound</code> is equal and its <code>upperBound</code> is
     * smaller.
     * 
     * @param interval
     *            the interval to compare with.
     * @return an <code>int</code> value: <code>0</code> if the two
     *         intervals are the equals, a negative value if this interval is
     *         smaller, and a positive value if it is bigger.
     * @throws InternalException
     *             if both intervals do not share the same adjacency realms.
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
     * Tests whether this interval is adjacent to the provided interval. It is
     * adjacent if this interval's <code>uppeBound</code> is adjacent to the
     * <code>lowerBound</code> of the provided interval.
     * 
     * @param interval
     *            the interval to test for adjacency.
     * @return <code>true</code> if the two intervals are adjacent;
     *         <code>false</code> otherwise.
     * @throws InternalException
     *             if the interval is <code>null</code> or if both intervals
     *             do not share the same adjacency realms.
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
     * @throws InternalException
     *             if the interval is <code>null</code> or if both intervals
     *             do not share the same adjacency realms.
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
     * Creates a new interval representing the intersection between this
     * interval and the provided one. The result is <code>null</code> when
     * both intervals do not intersect.
     * 
     * @param interval
     *            the interval to intersect with.
     * @return the intersection of the two intervals; <code>null</code> if the
     *         intersection is empty.
     * @throws InternalException
     *             if the interval is <code>null</code> or if both intervals
     *             do not share the same adjacency realms.
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

        T lowerBound = AdjacencyRealm.max(this.lowerBound, interval.lowerBound);

        T upperBound = AdjacencyRealm.min(this.upperBound, interval.upperBound);

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
     * @return the new interval.
     * @throws InternalException
     *             if the interval is <code>null</code> or not adjacent to
     *             this one, or if both intervals do not share the same
     *             adjacency realms.
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
     * Returns the minimum of two intervals.
     * 
     * @param interval1
     *            the first interval.
     * @param interval2
     *            the second interval.
     * @return the smallest of the two intervals, or <code>interval1</code> in
     *         case of equality.
     * @throws InternalException
     *             if one of the two intervals is <code>null</code>.
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
     * Returns the maximum of two intervals.
     * 
     * @param interval1
     *            the first interval.
     * @param interval2
     *            the second interval.
     * @return the biggest of the two intervals, or <code>interval1</code> in
     *         case of equality.
     * @throws InternalException
     *             if one of the two intervals is <code>null</code>.
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
