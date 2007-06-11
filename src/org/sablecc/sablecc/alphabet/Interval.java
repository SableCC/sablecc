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

public class Interval<T extends Comparable<? super T>>
        implements Comparable<Interval<T>> {
    private final T lowerBound;

    private final T upperBound;

    private Integer hashCode;

    private String toString;

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

    public Interval(
            T bound) {
        this(bound, bound);
    }

    public T getLowerBound() {
        return this.lowerBound;
    }

    public T getUpperBound() {
        return this.upperBound;
    }

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

    @Override
    public int hashCode() {
        if (this.hashCode == null) {
            this.hashCode = this.lowerBound.hashCode()
                    + this.upperBound.hashCode();
        }

        return this.hashCode;
    }

    @Override
    public String toString() {
        if (this.toString == null) {
            this.toString = "[" + this.lowerBound + ".." + this.upperBound
                    + "]";
        }

        return this.toString;
    }

    public int compareTo(
            Interval<T> interval) {
        int result = this.lowerBound.compareTo(interval.lowerBound);

        if (result == 0) {
            result = this.upperBound.compareTo(interval.upperBound);
        }

        return result;
    }

    public boolean isAdjacentTo(
            Interval<T> interval,
            Adjacency<T> adjacency) {
        return adjacency.isAdjacent(this.upperBound, interval.lowerBound);
    }

    public boolean intersects(
            Interval<T> interval) {
        return this.lowerBound.compareTo(interval.upperBound) <= 0
                && this.upperBound.compareTo(interval.lowerBound) >= 0;
    }

    public Interval<T> intersection(
            Interval<T> interval) {

        T lowerBound = max(this.lowerBound, interval.lowerBound);

        T upperBound = min(this.upperBound, interval.upperBound);

        if (lowerBound.compareTo(upperBound) <= 0) {
            return new Interval<T>(lowerBound, upperBound);
        }

        return null;
    }

    public Interval<T> mergeWith(
            Interval<T> interval,
            Adjacency<T> adjacency) {
        if (!isAdjacentTo(interval, adjacency)) {
            throw new IllegalArgumentException("Interval must be adjacent.");
        }

        return new Interval<T>(this.lowerBound, interval.upperBound);
    }

    private static <T extends Comparable<? super T>> T min(
            T bound1,
            T bound2) {
        if (bound1.compareTo(bound2) <= 0) {
            return bound1;
        }

        return bound2;
    }

    private static <T extends Comparable<? super T>> T max(
            T bound1,
            T bound2) {
        if (bound1.compareTo(bound2) >= 0) {
            return bound1;
        }

        return bound2;
    }

    public static <T extends Comparable<? super T>> Interval<T> min(
            Interval<T> interval1,
            Interval<T> interval2) {
        if (interval1.compareTo(interval2) <= 0) {
            return interval1;
        }

        return interval2;
    }

    public static <T extends Comparable<? super T>> Interval<T> max(
            Interval<T> interval1,
            Interval<T> interval2) {
        if (interval1.compareTo(interval2) >= 0) {
            return interval1;
        }

        return interval2;
    }
}
