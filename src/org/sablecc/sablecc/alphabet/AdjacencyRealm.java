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
 * An adjacency realm defines adjacency rules for <code>T</code> elements. In
 * other words, it defines the value of <code>(bound + 1)</code>. This allows
 * for using non-integer types as interval bounds, as long as an adjacency realm
 * is provided.
 * <p>
 * An adjacency realm also serves as a <em>universe</em> (or realm) to create
 * <code>Interval</code> instances. Two intervals can only be compared when
 * they belong to the same realm.
 */

public abstract class AdjacencyRealm<T extends Comparable<? super T>> {

    /**
     * Creates a new instance.
     */
    public AdjacencyRealm() {

    }

    /**
     * Returns whether two bounds are adjacents. For example, if <code>T</code>
     * is an integer type, it returns <code>true</code> when
     * <code>(bound1 + 1) == bound2</code>.
     * 
     * @param bound1
     *            the first bound.
     * @param bound2
     *            the second bound.
     * @return <code>true</code> if the first bound is adjacent to the second;
     *         <code>false</code> otherwise.
     */
    public abstract boolean isAdjacent(
            T bound1,
            T bound2);

    /**
     * Returns the <code>T</code> element preceding the provided bound.
     * Generally <code>(bound - 1)</code> or its equivalent.
     * 
     * @param bound
     *            a bound.
     * @return the <code>T</code> element that precedes to the provided bound.
     */
    public abstract T previous(
            T bound);

    /**
     * Returns the <code>T</code> element following the provided bound.
     * Generally <code>(bound + 1)</code> or its equivalent.
     * 
     * @param bound
     *            a bound.
     * @return the <code>T</code> element that follows the the provided bound.
     */
    public abstract T next(
            T bound);

    /**
     * Returns the minimum of two bounds.
     * 
     * @param bound1
     *            the first bound.
     * @param bound2
     *            the second bound.
     * @return the smallest of the two bounds, or <code>bound1</code> in case
     *         of equality.
     * @throws InternalException
     *             if one of the two bounds is <code>null</code>.
     */
    public static <T extends Comparable<? super T>> T min(
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
     * Returns the maximum of two bounds.
     * 
     * @param bound1
     *            the first bound.
     * @param bound2
     *            the second bound.
     * @return the biggest of the two bounds, or <code>bound1</code> in case
     *         of equality.
     * @throws InternalException
     *             if one of the two bounds is <code>null</code>.
     */
    public static <T extends Comparable<? super T>> T max(
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
     * Creates a new interval with the provided lower and upper bounds.
     * 
     * @param lowerBound
     *            the lower bound.
     * @param upperBound
     *            the upper bound.
     * @return the newly created interval.
     */
    public Interval<T> createInterval(
            T lowerBound,
            T upperBound) {

        if (lowerBound == null) {
            throw new InternalException("lowerBound may not be null");
        }

        if (upperBound == null) {
            throw new InternalException("upperBound may not be null");
        }

        return new Interval<T>(lowerBound, upperBound, this);
    }

    /**
     * Creates a new interval with a single bound used as both lower and upper
     * bounds.
     * 
     * @param bound
     *            the bound. Used as both upper and lower bound.
     * @return the newly created interval.
     */
    public Interval<T> createInterval(
            T bound) {

        if (bound == null) {
            throw new InternalException("bound may not be null");
        }

        return new Interval<T>(bound, this);

    }
}
