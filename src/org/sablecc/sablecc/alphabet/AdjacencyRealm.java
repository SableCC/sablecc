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
 * This class serves to create adjacency realms for intervals. It provides
 * methods to create intervals, to determine whether two T values (bounds) are
 * adjacent, and to compute the previous and next T value.
 */

public abstract class AdjacencyRealm<T extends Comparable<? super T>> {

    /**
     * Returns whether two bounds are adjacents. For example, if T is an integer
     * type, it returns <code>true</code> when
     * <code>(bound1 + 1) == bound2</code>.
     * 
     * @param bound1
     *            a bound to compare.
     * @param bound2
     *            a bound to compare.
     * @return <code>true</code> if the two bounds are adjacents;
     *         <code>false</code> otherwise.
     */
    public abstract boolean isAdjacent(
            T bound1,
            T bound2);

    /**
     * Returns the element T preceding the current instance. Generally bound - 1
     * (or its equivalent).
     * 
     * Throws an exception when <code>isSequential</code> returns
     * <code>false</code>.
     * 
     * @param bound
     *            a bound to compare.
     * @return a T previous to the current instance.
     */
    public abstract T previous(
            T bound);

    /**
     * Returns the element T following the current instance. Generally bound + 1
     * (or its equivalent).
     * 
     * Throws an exception when <code>isSequential</code> returns
     * <code>false</code>.
     * 
     * @param bound
     *            a bound to compare.
     * @return a T following the current instance.
     */
    public abstract T next(
            T bound);

    /**
     * Compares two bounds to find the minimum.
     * 
     * @param bound1
     *            a bound to compare.
     * @param bound2
     *            a bound to compare.
     * @return the lowest of the two bounds, or <code>bound1</code> in case of
     *         equality.
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
     * Compares two bounds to find the maximum.
     * 
     * @param bound1
     *            a bound to compare.
     * @param bound2
     *            a bound to compare.
     * @return the highest of the two bounds, or <code>bound1</code> in case
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
     * Returns a new interval with the provided lower and upper bounds.
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
     * Returns a new interval with a single bound used as both lower and upper
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
