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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sablecc.sablecc.exception.InternalException;

/**
 * A symbol is a non-empty set of non-overlapped, non-adjacent intervals. This
 * class provides various methods to maniplutate symbols.
 */
public final class Symbol<T extends Comparable<? super T>>
        implements Comparable<Symbol<T>> {

    /**
     * The sorted set of non-overlapping, non-adjacent intervals. Is
     * <code>null</code> if the symbol is a complement symbol.
     */
    private final SortedSet<Interval<T>> intervals;

    /** Cached hashcode. Is <code>null</code> when not yet computed. */
    private Integer hashCode;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /**
     * Constructs a symbol with the provided collection of intervals. Adjacent
     * intervals are merged. Fails if two intervals intersect.
     * 
     * @param intervals
     *            the collection of intervals.
     * @throws InternalException
     *             if the collection is <code>null</code>, if the interval is
     *             empty, if an interval within the collection is
     *             <code>null</code>, or if two intervals intersect.
     */
    public Symbol(
            Collection<Interval<T>> intervals) {

        if (intervals == null) {
            throw new InternalException("intervals may not be null");
        }

        if (intervals.isEmpty()) {
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

    /**
     * Constructs a symbol with the provided interval.
     * 
     * @param interval
     *            the interval.
     * @throws InternalException
     *             if the interval is <code>null</code>.
     */
    public Symbol(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval must be provided");
        }

        SortedSet<Interval<T>> set = new TreeSet<Interval<T>>();
        set.add(interval);
        this.intervals = Collections.unmodifiableSortedSet(set);
    }

    /**
     * Constructs a special symbol representing the complement symbol of a
     * grammar.
     */
    public Symbol() {

        this.intervals = null;
    }

    /**
     * Returns the set of intervals of this symbol.
     * 
     * @return the set of intervals.
     */
    public SortedSet<Interval<T>> getIntervals() {

        if (this.intervals == null) {
            throw new InternalException("complement symbols have no intervals");
        }

        return this.intervals;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they have an identical set of intervals.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this symbol and the object are equal;
     *         <code>false</code> otherwise.
     */
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

        if (this.intervals == null || symbol.intervals == null) {
            return this.intervals == symbol.intervals;
        }

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

    /**
     * Returns the hash code of this symbol.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            int hashCode = 0;

            if (this.intervals != null) {
                for (Interval<T> interval : this.intervals) {
                    hashCode *= 7;
                    hashCode += interval.hashCode();
                }
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this symbol.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("{");

            if (this.intervals != null) {
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
            }
            else {
                sb.append("Complement");
            }

            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Compares this symbol to the provided one. The comparison proceeds by
     * iteratively comparing the intervals of both symbols until a difference is
     * found. If no difference is found, the size of interval sets is compared.
     * 
     * @param symbol
     *            the symbol to compare with.
     * @return an <code>int</code> value: 0 if the two symbols are equals, a
     *         negative value if this symbol is smaller, and a positive value if
     *         it is bigger.
     */
    public int compareTo(
            Symbol<T> symbol) {

        if (this.intervals == null || symbol.intervals == null) {

            if (this.intervals == symbol.intervals) {
                return 0;
            }

            if (this.intervals == null) {
                return -1;
            }

            return 1;
        }

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

    /**
     * Returns whether this symbol is a complement symbol or not.
     * 
     * @return <code>true</code> if this symbol is a complement symbol.
     */
    public boolean isComplement() {

        return this.intervals == null;
    }

    /**
     * Creates a new symbol by merging together the symbols in the provided
     * collection. The new symbol includes all the intervals of merged symbols.
     * Adjacent intervals are merged. Fails if two intervals intersect.
     * <p>
     * If one of the symbols is a complement symbol, the result is a complement
     * symbol.
     * 
     * @param symbols
     *            a collection of symbols to merge.
     * @return the new symbol.
     * @throws InternalException
     *             if the collection is <code>null</code>, if it is empty, or
     *             if it contains more than one complement symbol.
     */
    public static <T extends Comparable<? super T>> Symbol<T> merge(
            Collection<Symbol<T>> symbols) {

        if (symbols == null) {
            throw new InternalException("symbols may not be null");
        }

        if (symbols.isEmpty()) {
            throw new InternalException(
                    "symbols must contain at least one element");
        }

        // look for complement symbols

        boolean containsComplementSymbol = false;

        for (Symbol<T> symbol : symbols) {
            if (symbol.isComplement()) {

                if (containsComplementSymbol) {
                    throw new InternalException(
                            "multiple complement symbols may not be merged.");
                }

                containsComplementSymbol = true;
            }
        }

        if (containsComplementSymbol) {
            return new Symbol<T>();
        }

        // merge non-complement symbols.

        Collection<Interval<T>> intervals = new LinkedList<Interval<T>>();

        for (Symbol<T> symbol : symbols) {
            intervals.addAll(symbol.getIntervals());
        }

        return new Symbol<T>(intervals);
    }

    /**
     * Returns the minimum of two symbols.
     * 
     * @param symbol1
     *            the first symbol.
     * @param symbol2
     *            the second symbol.
     * @return the smallest of the two symbols, or <code>symbol1</code> in
     *         case of equality.
     * @throws InternalException
     *             if one of the two symbols is <code>null</code>.
     */
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

    /**
     * Returns the maximum of two symbols.
     * 
     * @param symbol1
     *            the first symbol.
     * @param symbol2
     *            the second symbol.
     * @return the biggest of the two symbols, or <code>symbol1</code> in case
     *         of equality.
     * @throws InternalException
     *             if one of the two symbols is <code>null</code>.
     */
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
