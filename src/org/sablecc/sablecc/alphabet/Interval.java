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
 * This class represent an interval and implements its major services.
 */
public class Interval<T extends Comparable<? super T>>
        implements Comparable<Interval<T>> {

    private final T lowerBound;

    private final T upperBound;

    private Integer hashCode;

    private String toString;

    /**
     * Main constructor of an Interval.
     * 
     * Constructs an Interval with 2 bounds. Verifications are made for both
     * bounds to be provided and for lowerbound to be <= than the upperbound.
     * 
     * @param lowerBound
     *            the lower bound of the new Interval.
     * @param upperBound
     *            the upper bound of the new Interval.
     * @throws IllegalArgumentException
     *             if bounds are null or if lowerbound > upperbound.
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
     * Secondary construtor of an Interval.
     * 
     * Constructs an Interval with only one bound. The main construtor is called
     * with the single bound as lower and upper bound.
     * 
     * @param bound
     *            the lower and upper bound of the new Interval.
     */
    public Interval(
            T bound) {

        this(bound, bound);
    }

    /**
     * Returns the lower bound of an Interval.
     * 
     * @return an element T representing the lowerbound.
     */
    public T getLowerBound() {

        return this.lowerBound;
    }

    /**
     * Returns the upper bound of an Interval.
     * 
     * @return an element T representing the upperbound.
     */
    public T getUpperBound() {

        return this.upperBound;
    }

    /**
     * Compare an Interval with an object for equality. Returns true if the
     * object is an Interval and if its bounds are the same as the current
     * Interval instance.
     * 
     * @param obj
     *            the object to compare with.
     * @return true if the Interval and the object are the same; false
     *         otherwise.
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
     * Returns a hash code value for this object. The result is the addition of
     * both hashCode of the two bounds of this Interval.
     * 
     * @return a hash code value for this object.
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
     * Returns a String representation of this Interval. The representation
     * takes the following form: [ lowerbound .. upperbound ]
     * 
     * @return a String representing this Interval.
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
     * Compare two Intervals by looking at their bounds. It starts by looking at
     * their lower bounds and if they're equals compare their upper bounds.
     * 
     * @param interval
     *            the Interval to compare with.
     * @return an int value of 0 if the two Intervals are the equals, less than
     *         0 if this Interval's lowerbound is smaller and more than 0 if it
     *         is bigger.
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
     * Test two intervals for adjacency by testing the upperbound of this
     * Interval with the lower bound of another Interval. It uses an Adjacency
     * object to do so.
     * 
     * @param interval
     *            the Interval to test adjacency with.
     * @param adjacency
     *            an object of type Adjacency<T>.
     * @return true if the two Intervals are adjacent; false otherwise.
     */
    public boolean isAdjacentTo(
            Interval<T> interval,
            Adjacency<T> adjacency) {

        return adjacency.isAdjacent(this.upperBound, interval.lowerBound);
    }

    /**
     * Test if this Interal intersect with another one. Test the lowerbound of
     * one with the upperbound of the other.
     * 
     * @param interval
     *            the Interval to compare with.
     * @return true if the two Intervals intersect; false otherwise.
     */
    public boolean intersects(
            Interval<T> interval) {

        return this.lowerBound.compareTo(interval.upperBound) <= 0
                && this.upperBound.compareTo(interval.lowerBound) >= 0;
    }

    /**
     * Return the Interval of the intersection of this Interval and another one.
     * Null if this intersection does not exist.
     * 
     * @param interval
     *            the Interval to intersect with.
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
     * Makes a new Interval by merging the two Intervals if this Interval is
     * adjacent to the other one.
     * 
     * @param interval
     *            the Interval to merge this one with.
     * @param adjacency
     *            an object of type Adjacency<T>.
     * @return the merge of the two Intervals.
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
     * Compare two bounds to find the minimum.
     * 
     * @param bound1
     * @param bound2
     * @return the lowest of the two bounds.
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
     * Compare two bounds to find the maximum.
     * 
     * @param bound1
     * @param bound2
     * @return the highest of the two bounds.
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
     * Compare two intervals and returns the lowest one (minimum).
     * 
     * @param interval1
     *            an interval to compare.
     * @param interval2
     *            an interval to compare.
     * @return an Interval being the minimum between the two Intervals.
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
     * Compare two intervals and returns the highest one (maximum).
     * 
     * @param interval1
     *            an interval to compare.
     * @param interval2
     *            an interval to compare.
     * @return an Interval being the maximum between the two Intervals.
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
