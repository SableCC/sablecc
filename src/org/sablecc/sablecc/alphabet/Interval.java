/* This file is part of SableCC (http://sablecc.org/).
 * 
 * See the NOTICE file distributed with this work for copyright information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sablecc.sablecc.alphabet;

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

    /** Cached hashcode. Is null when not yet computed. */
    private Integer hashCode;

    /** Cached string representation. Is null when not yet computed. */
    private String toString;

    /**
     * Constructs an interval with the provided lower and upper bounds.
     * Verifications are made that both bounds are provided and that lowerbound <=
     * upperbound.
     * 
     * @param lowerBound
     *            the lower bound.
     * @param upperBound
     *            the upper bound.
     * @throws IllegalArgumentException
     *             if any bound is null or if lowerBound > upperBound.
     */
    public Interval(
            T lowerBound,
            T upperBound) {

        if (lowerBound == null || upperBound == null) {
            throw new IllegalArgumentException(
                    "Lower and upper bounds must be provided.");
        }

        if (lowerBound.compareTo(upperBound) > 0) {
            throw new IllegalArgumentException(
                    "Lower bound must be smaller or equal to upper bound.");
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Constructs an interval with a single bound used as both lower and upper
     * bounds.
     * 
     * @param bound
     *            the bound. Used as both upper and lower bound.
     */
    public Interval(
            T bound) {

        this(bound, bound);
    }

    /**
     * Returns the lower bound of the interval.
     * 
     * @return an element T representing the lowerBound.
     */
    public T getLowerBound() {

        return this.lowerBound;
    }

    /**
     * Returns the upper bound of an interval.
     * 
     * @return an element T representing the upperBound.
     */
    public T getUpperBound() {

        return this.upperBound;
    }

    /**
     * Compares this interval with an object for equality. Returns true if the
     * object is an Interval and if its bounds are the same as those of this
     * instance.
     * 
     * @param obj
     *            the object to compare with.
     * @return true if this interval and the object are equal; false otherwise.
     */
    @Override
    public boolean equals(
            Object obj) {

        if (!(obj instanceof Interval)) {
            return false;
        }

        Interval interval = (Interval) obj;

        return this.lowerBound.equals(interval.lowerBound)
                && this.upperBound.equals(interval.upperBound);
    }

    /**
     * Returns a hash code value for this object. The result is the sum of the
     * respective hash codes of the two bounds of this interval.
     * 
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            this.hashCode = this.lowerBound.hashCode()
                    + this.upperBound.hashCode();
        }

        return this.hashCode;
    }

    /**
     * Returns a String representation for this interval. The representation
     * takes the following form: <code>[lowerBound..upperBound]</code>
     * 
     * @return a String representing this interval.
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
     * lower bound is smaller, or if its lowerbound is equal and its upper bound
     * is smaller.
     * 
     * @param interval
     *            the Interval to compare with.
     * @return an int value: 0 if the two intervals are the equals, a negative
     *         value if this interval is smaller, and a positive value if it is
     *         bigger.
     */
    public int compareTo(
            Interval<T> interval) {

        int result = this.lowerBound.compareTo(interval.lowerBound);

        if (result == 0) {
            result = this.upperBound.compareTo(interval.upperBound);
        }

        return result;
    }

    /**
     * Tests whether the provided interval is adjacent to this one. It is
     * adjacent if this interval's upper bound is adjacent to the lower bound of
     * the provided interval according to the provided Adjacency instance.
     * 
     * @param interval
     *            the interval to test for adjacency.
     * @param adjacency
     *            an instance that implements Adjacency&lt;T&gt;.
     * @return true if the two intervals are adjacent; false otherwise.
     */
    public boolean isAdjacentTo(
            Interval<T> interval,
            Adjacency<T> adjacency) {

        return adjacency.isAdjacent(this.upperBound, interval.lowerBound);
    }

    /**
     * Tests whether this interval intersects with the provided one. The two
     * intervals intersect if they share a commun subinterval.
     * 
     * @param interval
     *            the Interval to compare with.
     * @return true if the two intervals intersect; false otherwise.
     */
    public boolean intersects(
            Interval<T> interval) {

        return this.lowerBound.compareTo(interval.upperBound) <= 0
                && this.upperBound.compareTo(interval.lowerBound) >= 0;
    }

    /**
     * Creates an interval representing the intersection between this interval
     * and the provided one. The result is null when both intervals do not
     * intersect.
     * 
     * @param interval
     *            the interval to intersect with.
     * @return the intersection of the two Intervals; null if it's not possible.
     */
    public Interval<T> intersection(
            Interval<T> interval) {

        T lowerBound = max(this.lowerBound, interval.lowerBound);

        T upperBound = min(this.upperBound, interval.upperBound);

        if (lowerBound.compareTo(upperBound) <= 0) {
            return new Interval<T>(lowerBound, upperBound);
        }

        return null;
    }

    /**
     * Creates a new interval that spans from this interval's lower bound to the
     * provided interval's upper bound. Merging fails if if this interval is not
     * adjacent to the provided one.
     * 
     * @param interval
     *            the interval to merge this one with.
     * @param adjacency
     *            an instance that implements Adjacency&lt;T&gt;.
     * @return a new interval representing the merge of the two intervals.
     */
    public Interval<T> mergeWith(
            Interval<T> interval,
            Adjacency<T> adjacency) {

        if (!isAdjacentTo(interval, adjacency)) {
            throw new IllegalArgumentException("Interval must be adjacent.");
        }

        return new Interval<T>(this.lowerBound, interval.upperBound);
    }

    /**
     * Compares two bounds to find the minimum.
     * 
     * @param bound1
     *            a bound to compare.
     * @param bound2
     *            a bound to compare.
     * @return the lowest of the two bounds, or bound 1 in case of equality.
     */
    private static <T extends Comparable<? super T>> T min(
            T bound1,
            T bound2) {

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
     * @return the highest of the two bounds, or bound 1 in case of equality.
     */
    private static <T extends Comparable<? super T>> T max(
            T bound1,
            T bound2) {

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
     * @return the lowest of the two intervals, or interval1 in case of
     *         equality.
     */
    public static <T extends Comparable<? super T>> Interval<T> min(
            Interval<T> interval1,
            Interval<T> interval2) {

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
     * @return the highest of the two intervals, or interval1 in case of
     *         equality.
     */
    public static <T extends Comparable<? super T>> Interval<T> max(
            Interval<T> interval1,
            Interval<T> interval2) {

        if (interval1.compareTo(interval2) >= 0) {
            return interval1;
        }

        return interval2;
    }
}
