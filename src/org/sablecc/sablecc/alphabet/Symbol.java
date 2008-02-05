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

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sablecc.sablecc.exception.InternalException;

/**
 * A symbol is a non-empty set of non-intersecting, non-adjacent intervals. As a
 * special case, a complement symbol is implemented as a symbol with no
 * intervals. The complement symbol of an alphabet represents all the intervals
 * not covered by other symbols of the alphabet.
 */
public class Symbol
        implements Comparable<Symbol> {

    /**
     * The sorted set of non-intersecting, non-adjacent intervals of this
     * symbol. It is <code>null</code> when the symbol is a complement symbol.
     */
    private final SortedSet<Interval> intervals;

    /**
     * The cached hashcode of this symbol. It is <code>null</code> when not
     * yet computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this symbol. It is <code>null</code>
     * when not yet computed.
     */
    private String toString;

    /**
     * Constructs a complement symbol.
     */
    public Symbol() {

        this.intervals = null;
    }

    /**
     * Constructs a symbol with the provided collection of intervals. Adjacent
     * intervals are merged. Fails when two intervals intersect.
     */
    public Symbol(
            Collection<Interval> intervals) {

        if (intervals == null) {
            throw new InternalException("intervals may not be null");
        }

        if (intervals.isEmpty()) {
            throw new InternalException(
                    "intervals must contain at least one element");
        }

        // sort intervals
        SortedSet<Interval> originalSet = new TreeSet<Interval>(intervals);

        // compute minimal set
        SortedSet<Interval> minimalSet = new TreeSet<Interval>();

        Interval combinedInterval = null;
        for (Interval current : originalSet) {
            if (current == null) {
                throw new InternalException("null is not a valid interval");
            }

            if (combinedInterval != null
                    && combinedInterval.intersects(current)) {
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
        }

        assert combinedInterval != null;
        minimalSet.add(combinedInterval);

        this.intervals = Collections.unmodifiableSortedSet(minimalSet);
    }

    /**
     * Constructs a symbol with the provided interval.
     */
    public Symbol(
            Interval interval) {

        if (interval == null) {
            throw new InternalException("interval must be provided");
        }

        SortedSet<Interval> set = new TreeSet<Interval>();
        set.add(interval);
        this.intervals = Collections.unmodifiableSortedSet(set);
    }

    /**
     * Constructs a symbol with the provided bound.
     */
    public Symbol(
            Bound bound) {

        if (bound == null) {
            throw new InternalException("bound must be provided");
        }

        SortedSet<Interval> set = new TreeSet<Interval>();
        set.add(new Interval(bound));
        this.intervals = Collections.unmodifiableSortedSet(set);
    }

    public Symbol(
            char bound) {

        this(new Interval(bound));
    }

    public Symbol(
            int bound) {

        this(new Interval(bound));
    }

    public Symbol(
            BigInteger bound) {

        this(new Interval(bound));
    }

    public Symbol(
            String bound) {

        this(new Interval(bound));
    }

    /**
     * Returns the set of intervals of this symbol.
     */
    public SortedSet<Interval> getIntervals() {

        if (this.intervals == null) {
            throw new InternalException(
                    "complement symbols have no explicit intervals");
        }

        return this.intervals;
    }

    /**
     * Returns true if the provided object is equal to this symbol.
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

        Symbol symbol = (Symbol) obj;

        if (this.intervals == null || symbol.intervals == null) {
            return this.intervals == symbol.intervals;
        }

        if (this.intervals.size() != symbol.intervals.size()) {
            return false;
        }

        Iterator i = symbol.intervals.iterator();
        for (Interval interval : this.intervals) {
            if (!interval.equals(i.next())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the hash code of this symbol.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            int hashCode = 0;

            if (this.intervals != null) {
                for (Interval interval : this.intervals) {
                    hashCode *= 107;
                    hashCode += interval.hashCode();
                }
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this symbol.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("{");

            if (this.intervals != null) {
                boolean first = true;
                for (Interval interval : this.intervals) {
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
     * Compares this symbol to the provided one.
     */
    public int compareTo(
            Symbol symbol) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

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

        Iterator<Interval> i1 = this.intervals.iterator();
        Iterator<Interval> i2 = symbol.intervals.iterator();

        while (result == 0 && i1.hasNext() && i2.hasNext()) {
            Interval interval1 = i1.next();
            Interval interval2 = i2.next();

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
     * Returns true when this symbol is a complement symbol.
     */
    public boolean isComplement() {

        return this.intervals == null;
    }

    /**
     * Creates a new symbol by merging together the symbols in the provided
     * collection. The new symbol includes all the intervals of merged symbols.
     * Adjacent intervals are merged. Fails if two intervals intersect. If one
     * of the symbols is a complement symbol, the result is a complement symbol.
     */
    public static Symbol merge(
            Collection<Symbol> symbols) {

        if (symbols == null) {
            throw new InternalException("symbols may not be null");
        }

        if (symbols.isEmpty()) {
            throw new InternalException(
                    "symbols must contain at least one element");
        }

        // look for complement symbols

        boolean containsComplementSymbol = false;

        for (Symbol symbol : symbols) {
            if (symbol.isComplement()) {

                if (containsComplementSymbol) {
                    throw new InternalException(
                            "multiple complement symbols may not be merged.");
                }

                containsComplementSymbol = true;
            }
        }

        if (containsComplementSymbol) {
            return new Symbol();
        }

        // merge non-complement symbols.

        Collection<Interval> intervals = new LinkedList<Interval>();

        for (Symbol symbol : symbols) {
            intervals.addAll(symbol.getIntervals());
        }

        return new Symbol(intervals);
    }

    /**
     * Returns the minimum of two symbols.
     */
    public static Symbol min(
            Symbol symbol1,
            Symbol symbol2) {

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
     */
    public static Symbol max(
            Symbol symbol1,
            Symbol symbol2) {

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
