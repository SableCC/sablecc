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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sablecc.sablecc.exception.InternalException;

public class Symbol<T extends Comparable<? super T>>
        implements Comparable<Symbol<T>> {

    /** Unmodifiable, sorted set of intervals represented by this symbol. */
    private final SortedSet<Interval<T>> intervals;

    private Integer hashCode;

    private String toString;

    public Symbol(
            Collection<Interval<T>> intervals) {

        if (intervals == null) {
            throw new InternalException("intervals may not be null");
        }

        if (intervals.size() == 0) {
            throw new InternalException(
                    "intervals must contain at least one element");
        }

        // sort intervals
        SortedSet<Interval<T>> originalSet = new TreeSet<Interval<T>>(intervals);

        // compute minimal set
        SortedSet<Interval<T>> minimalSet = new TreeSet<Interval<T>>();

        Interval<T> previous = null;
        Interval<T> combinedInterval = null;
        for (Interval<T> current : originalSet) {
            if (current == null) {
                throw new InternalException("null is not a valid interval");
            }

            if (previous != null && previous.intersects(current)) {
                throw new InternalException("intervals may not intersect");
            }

            if (combinedInterval != null) {
                if (combinedInterval.isAdjacentTo(current)) {
                    combinedInterval = combinedInterval.mergeWith(current);
                }
                else {
                    minimalSet.add(combinedInterval);
                    combinedInterval = current;
                }
            }
            else {
                combinedInterval = current;
            }

            previous = current;
        }

        minimalSet.add(combinedInterval);

        this.intervals = Collections.unmodifiableSortedSet(minimalSet);
    }

    public Symbol(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval must be provided");
        }

        SortedSet<Interval<T>> set = new TreeSet<Interval<T>>();
        set.add(interval);
        this.intervals = Collections.unmodifiableSortedSet(set);
    }

    public SortedSet<Interval<T>> getIntervals() {

        return this.intervals;
    }

    @Override
    public boolean equals(
            Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Symbol)) {
            return false;
        }

        Symbol symbol = (Symbol) obj;

        if (this.intervals.size() != symbol.intervals.size()) {
            return false;
        }

        Iterator i = symbol.intervals.iterator();
        for (Interval<T> interval : this.intervals) {
            if (!interval.equals(i.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            int sum = 0;

            for (Interval<T> interval : this.intervals) {
                sum *= 7;
                sum += interval.hashCode();
            }

            this.hashCode = sum;
        }

        return this.hashCode;
    }

    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("{");

            boolean first = true;
            for (Interval<T> interval : this.intervals) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(",");
                }

                sb.append(interval);
            }

            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    public int compareTo(
            Symbol<T> symbol) {

        int result = 0;
        Iterator<Interval<T>> i1 = this.intervals.iterator();
        Iterator<Interval<T>> i2 = symbol.intervals.iterator();

        while (result == 0 && i1.hasNext() && i2.hasNext()) {
            Interval<T> interval1 = i1.next();
            Interval<T> interval2 = i2.next();

            result = interval1.compareTo(interval2);

            if (result != 0) {
                break;
            }
        }

        if (result == 0 && (i1.hasNext() || i2.hasNext())) {
            result = this.intervals.size() - symbol.intervals.size();
        }

        return result;
    }

    public static <T extends Comparable<? super T>> Symbol<T> merge(
            Collection<Symbol<T>> symbols) {

        if (symbols == null) {
            throw new InternalException("symbols may not be null");
        }

        if (symbols.size() == 0) {
            throw new InternalException(
                    "symbols must contain at least one element");
        }

        Collection<Interval<T>> intervals = new LinkedList<Interval<T>>();

        for (Symbol<T> symbol : symbols) {
            intervals.addAll(symbol.getIntervals());
        }

        return new Symbol<T>(intervals);
    }

    public static <T extends Comparable<? super T>> Symbol<T> min(
            Symbol<T> symbol1,
            Symbol<T> symbol2) {

        if (symbol1 == null) {
            throw new InternalException("symbol1 may not be null");
        }

        if (symbol2 == null) {
            throw new InternalException("symbol2 may not be null");
        }

        if (symbol1.compareTo(symbol2) <= 0) {
            return symbol1;
        }

        return symbol2;
    }

    public static <T extends Comparable<? super T>> Symbol<T> max(
            Symbol<T> symbol1,
            Symbol<T> symbol2) {

        if (symbol1 == null) {
            throw new InternalException("symbol1 may not be null");
        }

        if (symbol2 == null) {
            throw new InternalException("symbol2 may not be null");
        }

        if (symbol1.compareTo(symbol2) >= 0) {
            return symbol1;
        }

        return symbol2;
    }
}
