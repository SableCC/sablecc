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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.util.*;

/**
 * An alphabet is a set of symbols. Two symbols of an alphabet may not contain
 * intersecting intervals.
 */

public class Alphabet {

    /**
     * The sorted set of symbols of this alphabet.
     */
    private SortedSet<Symbol> symbols;

    /**
     * A mapping from each interval contained in a symbol of this alphabet to
     * its symbol.
     */
    private SortedMap<Interval, Symbol> intervalToSymbolMap;

    /**
     * The cached hash code of this alphabet. It is <code>null</code> when not
     * yet computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this alphabet. It is
     * <code>null</code> when not yet computed.
     */
    private String toString;

    /**
     * Constructs an alphabet with the provided collection of symbols.
     */
    public Alphabet(
            Collection<Symbol> symbols) {

        if (symbols == null) {
            throw new InternalException("symbols may not be null");
        }

        init(symbols);
    }

    /**
     * Constructs an alphabet with the provided symbol.
     */
    public Alphabet(
            Symbol symbol) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        Collection<Symbol> symbols = new LinkedList<Symbol>();
        symbols.add(symbol);

        init(symbols);
    }

    /**
     * Constructs an alphabet with the provided interval.
     */
    public Alphabet(
            Interval interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        Collection<Symbol> symbols = new LinkedList<Symbol>();
        symbols.add(new Symbol(interval));

        init(symbols);
    }

    /**
     * Constructs an alphabet with the provided bound.
     */
    public Alphabet(
            Bound bound) {

        if (bound == null) {
            throw new InternalException("bound may not be null");
        }

        Collection<Symbol> symbols = new LinkedList<Symbol>();
        symbols.add(new Symbol(bound));

        init(symbols);
    }

    /**
     * Constructs an alphabet with the provided bound.
     */
    public Alphabet(
            char bound) {

        this(new Symbol(bound));
    }

    /**
     * Constructs an alphabet with the provided bound.
     */
    public Alphabet(
            String bound) {

        if (bound == null) {
            throw new InternalException("bound may not be null");
        }

        Collection<Symbol> symbols = new LinkedList<Symbol>();
        symbols.add(new Symbol(bound));

        init(symbols);
    }

    /**
     * Constructs an alphabet with the provided bound.
     */
    public Alphabet(
            String bound,
            int radix) {

        if (bound == null) {
            throw new InternalException("bound may not be null");
        }

        Collection<Symbol> symbols = new LinkedList<Symbol>();
        symbols.add(new Symbol(bound, radix));

        init(symbols);
    }

    /**
     * Constructs an empty alphabet.
     */
    public Alphabet() {

        Collection<Symbol> symbols = new LinkedList<Symbol>();

        init(symbols);
    }

    /**
     * Initializes this alphabet using the provided collection of symbols. This
     * method must be called by all constructors. It fills the
     * <code>symbols</code> and <code>intervalToSymbolMap</code> instance
     * variables and detects intersecting intervals.
     */
    private void init(
            Collection<Symbol> symbols) {

        this.symbols = Collections.unmodifiableSortedSet(new TreeSet<Symbol>(
                symbols));

        // compute interval map
        TreeMap<Interval, Symbol> intervalMap = new TreeMap<Interval, Symbol>();

        for (Symbol symbol : symbols) {
            if (symbol == null) {
                throw new InternalException("symbol may not be null");
            }

            for (Interval interval : symbol.getIntervals()) {
                if (intervalMap.put(interval, symbol) != null) {
                    throw new InternalException(
                            "distinct symbols may not have equal intervals");
                }
            }
        }

        this.intervalToSymbolMap = Collections
                .unmodifiableSortedMap(intervalMap);

        // check for intersecting intervals
        Interval previous = null;
        for (Interval current : this.intervalToSymbolMap.keySet()) {
            if (previous != null && previous.intersects(current)) {
                throw new InternalException(
                        "distinct symbols may not have intersecting intervals");
            }

            previous = current;
        }
    }

    /**
     * Returns the set of symbols of this alphabet.
     */
    public SortedSet<Symbol> getSymbols() {

        return this.symbols;
    }

    /**
     * Returns a mapping from each interval contained in a symbol of this
     * alphabet to its symbol.
     */
    public SortedMap<Interval, Symbol> getIntervalToSymbolMap() {

        return this.intervalToSymbolMap;
    }

    /**
     * Returns <code>true</code> when the provided object is equal to this
     * alphabet.
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

        Alphabet alphabet = (Alphabet) obj;

        if (this.symbols.size() != alphabet.symbols.size()) {
            return false;
        }

        Iterator<Symbol> i = alphabet.symbols.iterator();
        for (Symbol symbol : this.symbols) {
            if (!symbol.equals(i.next())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the hash code of this alphabet.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            int hashCode = 0;

            for (Symbol symbol : this.symbols) {
                hashCode *= 101;
                hashCode += symbol.hashCode();
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this alphabet.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("Alphabet:{ ");

            boolean first = true;
            for (Symbol symbol : this.symbols) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(", ");
                }

                sb.append(symbol);
            }

            sb.append(" }");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Merges this alphabet with the provided one. Merging two alphabets
     * <code>A</code> and <code>B</code> consists of creating a new alphabet
     * <code>C</code> containing a minimal number of symbols with the following
     * property: For every symbol <code>x</code> element of
     * <code>(A union B)</code> there exists a corresponding subset
     * <code>S</code> of <code>C</code> such that: <code>merge(S) == x</code>.
     */
    public AlphabetMergeResult mergeWith(
            Alphabet alphabet) {

        if (alphabet == null) {
            throw new InternalException("alphabet may not be null");
        }

        // special treatment when merging with an identical alphabet
        if (alphabet.equals(this)) {
            return new AlphabetMergeResult(this);
        }

        // In theoretical terms, an alphabet is a set of symbols.
        //
        // Merging two alphabets A and B consists of creating a new alphabet C
        // containing a minimal number of symbols, with the following property:
        //
        // For every symbol x element of (A union B), there exists a
        // corresponding subset S of C, such that: merge(S) == x.
        //
        // As a direct consequence, every new symbol w element of C is related
        // to a pair (x,y) where x is element of (A union {null}) and y is
        // element of (B union {null}).
        //
        // Our algorithm proceeds by finding these pairs to identify the symbols
        // of the new alphabet.

        // First, we compute a map of (symbol pair,interval set)
        Map<Pair<Symbol, Symbol>, SortedSet<Interval>> symbolPairToIntervalSetMap = computeSymbolPairToIntervalSetMap(
                this, alphabet);

        // list of new symbols
        Collection<Symbol> newSymbols = new LinkedList<Symbol>();

        // SortedMap to map old symbols to sets of new symbols
        SortedMap<Symbol, SortedSet<Symbol>> alphabetSymbolMap = new TreeMap<Symbol, SortedSet<Symbol>>();

        for (Map.Entry<Pair<Symbol, Symbol>, SortedSet<Interval>> entry : symbolPairToIntervalSetMap
                .entrySet()) {

            Symbol oldSymbol1 = entry.getKey().getLeft();
            Symbol oldSymbol2 = entry.getKey().getRight();

            // we create a new symbol that relates to the pair
            Symbol newSymbol = new Symbol(entry.getValue());

            newSymbols.add(newSymbol);

            // we add the associations in the (old symbol, set of new symbols)
            // maps

            if (oldSymbol1 != null) {
                SortedSet<Symbol> collection = alphabetSymbolMap
                        .get(oldSymbol1);

                if (collection == null) {
                    collection = new TreeSet<Symbol>();
                    alphabetSymbolMap.put(oldSymbol1, collection);
                }

                collection.add(newSymbol);
            }

            if (oldSymbol2 != null) {
                SortedSet<Symbol> collection = alphabetSymbolMap
                        .get(oldSymbol2);

                if (collection == null) {
                    collection = new TreeSet<Symbol>();
                    alphabetSymbolMap.put(oldSymbol2, collection);
                }

                collection.add(newSymbol);
            }
        }

        return new AlphabetMergeResult(new Alphabet(newSymbols),
                alphabetSymbolMap);
    }

    /**
     * Computes a <code>Map</code> that maps each symbol pair <code>(x,y)</code>
     * to a set of shared intervals, where <code>x</code> is a symbol of
     * <code>alphabet1</code> or <code>null</code>, and <code>y</code> is a
     * symbol of <code>alphabet2</code> or <code>null</code>, but both
     * <code>x</code> and <code>y</code> are not null.
     */
    private static Map<Pair<Symbol, Symbol>, SortedSet<Interval>> computeSymbolPairToIntervalSetMap(
            Alphabet alphabet1,
            Alphabet alphabet2) {

        /*
         * The particular property of this implementation is that it does so in
         * linear time by only mapping pairs that have a non-empty shared
         * interval set. The intuitive algorithm would have analyzed all
         * possible pairs, leading to quadratic running time.
         */

        Map<Pair<Symbol, Symbol>, SortedSet<Interval>> symbolPairToIntervalSetMap = new LinkedHashMap<Pair<Symbol, Symbol>, SortedSet<Interval>>();

        /*
         * We find all intervals of new symbols by analyzing the space starting
         * from the smallest lower bound of an interval to the highest upper
         * bound.
         */

        // currently analyzed sorted map entries
        Map.Entry<Interval, Symbol> entry1 = null;
        Map.Entry<Interval, Symbol> entry2 = null;

        // iterators
        Iterator<Map.Entry<Interval, Symbol>> i1 = alphabet1.intervalToSymbolMap
                .entrySet().iterator();
        Iterator<Map.Entry<Interval, Symbol>> i2 = alphabet2.intervalToSymbolMap
                .entrySet().iterator();

        Bound lastUpperBound = null;

        while (entry1 != null || entry2 != null || i1.hasNext() || i2.hasNext()) {
            // if possible, make sure that entry1 and entry2 are filled
            if (entry1 == null && i1.hasNext()) {
                entry1 = i1.next();
            }

            if (entry2 == null && i2.hasNext()) {
                entry2 = i2.next();
            }

            // Compute the lower bound of the new interval
            Bound lowerBound;

            if (lastUpperBound == null) {
                // On the first iteration we need to apply a special treatment

                // We pick the smallest lower bound
                if (entry1 == null) {
                    lowerBound = entry2.getKey().getLowerBound();
                }
                else if (entry2 == null) {
                    lowerBound = entry1.getKey().getLowerBound();
                }
                else {
                    lowerBound = Bound.min(entry1.getKey().getLowerBound(),
                            entry2.getKey().getLowerBound());
                }
            }
            else {
                lowerBound = lastUpperBound.getSuccessor();
            }

            // compute the upper bound of the new interval
            Bound upperBound;

            {
                Bound upperBoundCandidate1 = null;
                Bound upperBoundCandidate2 = null;

                if (entry1 != null) {
                    if (lowerBound.compareTo(entry1.getKey().getLowerBound()) < 0) {
                        upperBoundCandidate1 = entry1.getKey().getLowerBound()
                                .getPredecessor();
                    }
                    else {
                        upperBoundCandidate1 = entry1.getKey().getUpperBound();
                    }
                }

                if (entry2 != null) {
                    if (lowerBound.compareTo(entry2.getKey().getLowerBound()) < 0) {
                        upperBoundCandidate2 = entry2.getKey().getLowerBound()
                                .getPredecessor();
                    }
                    else {
                        upperBoundCandidate2 = entry2.getKey().getUpperBound();
                    }
                }

                if (upperBoundCandidate1 == null) {
                    upperBound = upperBoundCandidate2;
                }
                else if (upperBoundCandidate2 == null) {
                    upperBound = upperBoundCandidate1;
                }
                else {
                    upperBound = Bound.min(upperBoundCandidate1,
                            upperBoundCandidate2);
                }
            }

            // create new interval, and related symbol pair
            Interval newInterval = new Interval(lowerBound, upperBound);

            Symbol symbol1;
            if (entry1 != null && newInterval.intersects(entry1.getKey())) {
                symbol1 = entry1.getValue();
            }
            else {
                symbol1 = null;
            }

            Symbol symbol2;
            if (entry2 != null && newInterval.intersects(entry2.getKey())) {
                symbol2 = entry2.getValue();
            }
            else {
                symbol2 = null;
            }

            if (symbol1 != null || symbol2 != null) {
                Pair<Symbol, Symbol> symbolPair = new Pair<Symbol, Symbol>(
                        symbol1, symbol2);

                // add interval in (symbol pair,interval set) map
                SortedSet<Interval> intervalSet = symbolPairToIntervalSetMap
                        .get(symbolPair);
                if (intervalSet == null) {
                    intervalSet = new TreeSet<Interval>();
                    symbolPairToIntervalSetMap.put(symbolPair, intervalSet);
                }

                intervalSet.add(newInterval);
            }

            // save last upper bound
            lastUpperBound = upperBound;

            // update current entries
            if (entry1 != null
                    && lastUpperBound
                            .compareTo(entry1.getKey().getUpperBound()) >= 0) {
                entry1 = null;
            }
            if (entry2 != null
                    && lastUpperBound
                            .compareTo(entry2.getKey().getUpperBound()) >= 0) {
                entry2 = null;
            }
        }

        return symbolPairToIntervalSetMap;
    }
}
