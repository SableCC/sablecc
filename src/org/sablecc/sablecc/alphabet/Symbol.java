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

import static org.sablecc.util.UsefulStaticImports.*;

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;

/**
 * A symbol is a non-empty set of non-intersecting, non-adjacent intervals.
 */
public class Symbol
        implements Comparable<Symbol> {

    /**
     * The sorted set of non-intersecting, non-adjacent intervals of this
     * symbol.
     */
    private final SortedSet<Interval> intervals;

    /**
     * The normal rich symbol associated with this symbol.
     */
    private final RichSymbol normalRichSymbol = new RichSymbol(this, false);

    /**
     * The lookahead rich symbol associated with this symbol.
     */
    private final RichSymbol lookaheadRichSymbol = new RichSymbol(this, true);

    /**
     * The cached hash code of this symbol. It is <code>null</code> when not yet
     * computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this symbol. It is <code>null</code>
     * when not yet computed.
     */
    private String toString;

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

    /**
     * Constructs a symbol with the provided bound.
     */
    public Symbol(
            char bound) {

        this(new Interval(bound));
    }

    /**
     * Constructs a symbol with the provided bound.
     */
    public Symbol(
            String bound) {

        this(new Interval(bound));
    }

    /**
     * Constructs a symbol with the provided bound.
     */
    public Symbol(
            String bound,
            int radix) {

        this(new Interval(bound, radix));
    }

    /**
     * Returns the set of intervals of this symbol.
     */
    public SortedSet<Interval> getIntervals() {

        return this.intervals;
    }

    /**
     * Returns the normal rich symbol associated with this symbol.
     */
    public RichSymbol getNormalRichSymbol() {

        return this.normalRichSymbol;
    }

    /**
     * Returns the lookahead rich symbol associated with this symbol.
     */
    public RichSymbol getLookaheadRichSymbol() {

        return this.lookaheadRichSymbol;
    }

    /**
     * Returns <code>true</code> when the provided object is equal to this
     * symbol.
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

        if (this.intervals.size() != symbol.intervals.size()) {
            return false;
        }

        Iterator<Interval> i = symbol.intervals.iterator();
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

            for (Interval interval : this.intervals) {
                hashCode *= 113;
                hashCode += interval.hashCode();
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

            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    public String getSimpleName() {

        return this.intervals.first().getSimpleName();
    }

    /**
     * Return an example of a character symbolized. Prefer printable character.
     * If a printable character is available, then the returned string contains
     * only one printable character. If a printable character is non available,
     * then the returned string contains the two character "\x" followed by the
     * hexadecimal value of a symbolized character. Note. You can check the
     * size() of the returned string to know.
     * */
    public String getExample() {

        for (Interval interval : this.intervals) {
            Bound b = interval.getLowerBound();
            if (b == Bound.MIN) {
                continue;
            }
            BigInteger i = b.getValue();
            if (i.compareTo(BI_32) > 0 || i.compareTo(BI_126) < 0) {
                return String.valueOf((char) i.intValue());
            }
        }
        for (Interval interval : this.intervals) {
            BigInteger i = interval.getLowerBound().getValue();
            if (i.compareTo(BI_32) >= 0 || i.compareTo(BI_126) < 0) {
                return String.valueOf((char) i.intValue());
            }
        }
        Interval interval = this.intervals.first();
        if (interval.getLowerBound() == Bound.MIN) {
            return "\\xNaN"; // FIXME Eh! What to do?
        }
        return "\\x" + interval.getLowerBound().getValue().toString(16);
    }

    /**
     * Compares this symbol to the provided one.
     */
    @Override
    public int compareTo(
            Symbol symbol) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
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
     * Creates a new symbol by merging together the symbols in the provided
     * collection. The new symbol includes all the intervals of merged symbols.
     * Adjacent intervals are merged. Fails if two intervals intersect.
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
