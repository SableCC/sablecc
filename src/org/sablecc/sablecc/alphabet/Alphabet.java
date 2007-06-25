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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sablecc.sablecc.exception.InternalException;

/**
 * An alphabet is a set of symbols. Two symbols of an alphabet may not contain
 * overlapping intervals.
 */

public final class Alphabet<T extends Comparable<? super T>> {

    /** The sorted set of symbols of this alphabet. */
    private SortedSet<Symbol<T>> symbols;

    /**
     * A <code>SortedMap</code> that maps each interval contained in a symbol
     * of this alphabet to its symbol.
     */
    private SortedMap<Interval<T>, Symbol<T>> intervalMap;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /**
     * Constructs a new alphabet with the provided collection of symbols.
     * 
     * @param symbols
     *            the collection of symbols.
     * @throws InternalException
     *             if the collection of symbols is <code>null</code>.
     */
    public Alphabet(
            Collection<Symbol<T>> symbols) {

        if (symbols == null) {
            throw new InternalException("symbols may not be null");
        }

        init(symbols);
    }

    /**
     * Constructs a new alphabet with the provided symbol.
     * 
     * @param symbol
     *            the symbol.
     * @throws InternalException
     *             if the symbol is <code>null</code>.
     */
    public Alphabet(
            Symbol<T> symbol) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        Collection<Symbol<T>> symbols = new LinkedList<Symbol<T>>();
        symbols.add(symbol);

        init(symbols);
    }

    /**
     * Constructs a new alphabet with the provided interval.
     * 
     * @param interval
     *            the interval.
     * @throws InternalException
     *             if the interval is <code>null</code>.
     */
    public Alphabet(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        Collection<Symbol<T>> symbols = new LinkedList<Symbol<T>>();
        symbols.add(new Symbol<T>(interval));

        init(symbols);
    }

    /**
     * Constructs an empty alphabet.
     */
    public Alphabet() {

        init(new LinkedList<Symbol<T>>());
    }

    /**
     * Initializes this alphabet using the provided collection of symbols. This
     * method must be called by all constructors. It fills the
     * <code>symbols</code> and <code>intervalMap</code> instance variables
     * and detects overlapping intervals.
     * 
     * @param symbols
     *            the collection of symbols.
     * @throws InternalException
     *             if two distinct symbols have overlapping intervals or if a
     *             symbol is <code>null</code>.
     */
    private void init(
            Collection<Symbol<T>> symbols) {

        this.symbols = Collections
                .unmodifiableSortedSet(new TreeSet<Symbol<T>>(symbols));

        // compute interval map
        TreeMap<Interval<T>, Symbol<T>> intervalMap = new TreeMap<Interval<T>, Symbol<T>>();

        for (Symbol<T> symbol : symbols) {
            if (symbol == null) {
                throw new InternalException("symbol may not be null");
            }

            for (Interval<T> interval : symbol.getIntervals()) {
                if (intervalMap.put(interval, symbol) != null) {
                    throw new InternalException(
                            "distinct symbols may not have overlapping intervals");
                }
            }
        }

        this.intervalMap = Collections.unmodifiableSortedMap(intervalMap);

        // check for overlapping intervals
        Interval<T> previous = null;
        for (Interval<T> current : this.intervalMap.keySet()) {
            if (previous != null && previous.intersects(current)) {
                throw new InternalException(
                        "distinct symbols may not have overlapping intervals");
            }

            previous = current;
        }
    }

    /**
     * Returns the set of symbols of this alphabet.
     * 
     * @return the set of symbols.
     */
    public SortedSet<Symbol<T>> getSymbols() {

        return this.symbols;
    }

    /**
     * Returns the interval map of this alphabet. The <code>SortedMap</code>
     * maps each interval contained in a symbol of this alphabet to its symbol.
     * 
     * @return the interval map.
     */
    public SortedMap<Interval<T>, Symbol<T>> getIntervalMap() {

        return this.intervalMap;
    }

    /**
     * Returns the string representation of this alphabet.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("Alphabet:{ ");

            boolean first = true;
            for (Symbol<T> symbol : this.symbols) {
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
     * Merges this alphabet with the provided one.
     * 
     * Merging two alphabets <code>A</code> and <code>B</code> consists of
     * creating a new alphabet <code>C</code> containing a minimal number of
     * symbols, with the following property: For every symbol <code>x</code>
     * element of <code>(A union B)</code>, there exists a corresponding
     * subset <code>S</code> of <code>C</code>, such that:
     * <code>merge(S) == x</code>.
     * 
     * @param alphabet
     *            the alphabet to merge this one with.
     * @return an instance of <code>AlphabetMergeResult</code> containing the
     *         merge result.
     * @throws InternalException
     *             if the provided alphabet is <code>null</code>.
     */
    public AlphabetMergeResult<T> mergeWith(
            Alphabet<T> alphabet) {

        if (alphabet == null) {
            throw new InternalException("alphabet may not be null");
        }

        // no need to really merge, when merging with self
        if (alphabet == this) {
            return new AlphabetMergeResult<T>(this);
        }

        /*
         * In theoretical terms, an alphabet is a set of symbols.
         * 
         * Merging two alphabets A and B consists of creating a new alphabet C
         * containing a minimal number of symbols, with the following property:
         * 
         * For every symbol x element of (A union B), there exists a
         * corresponding subset S of C, such that: merge(S) == x.
         * 
         * As a direct consequence, every new symbol w element of C is related
         * to a pair (x,y) where x is element of (A union {null}) and y is
         * element of (B union {null}).
         * 
         * Our algorithm proceeds by finding these pairs to identify the symbols
         * of the new alphabet.
         * 
         */

        // First, we compute a map of (symbol pair,interval set)
        Map<SymbolPair<T>, SortedSet<Interval<T>>> symbolPairIntervalSetMap = computeSymbolPairIntervalSetMap(
                this, alphabet);

        // list of new symbols
        Collection<Symbol<T>> newSymbols = new LinkedList<Symbol<T>>();

        // SortedMaps to map old symbols to sets of new symbols
        SortedMap<Symbol<T>, SortedSet<Symbol<T>>> alphabet1SymbolMap = new TreeMap<Symbol<T>, SortedSet<Symbol<T>>>();
        SortedMap<Symbol<T>, SortedSet<Symbol<T>>> alphabet2SymbolMap = new TreeMap<Symbol<T>, SortedSet<Symbol<T>>>();

        for (Map.Entry<SymbolPair<T>, SortedSet<Interval<T>>> entry : symbolPairIntervalSetMap
                .entrySet()) {

            // we can make a new symbol that relates to the pair
            Symbol<T> newSymbol = new Symbol<T>(entry.getValue());

            Symbol<T> oldSymbol1 = entry.getKey().getSymbol1();
            Symbol<T> oldSymbol2 = entry.getKey().getSymbol2();

            // if no old symbol matches, don't create a symbol
            if (oldSymbol1 == null && oldSymbol2 == null) {
                continue;
            }

            newSymbols.add(newSymbol);

            // we add the associations in the (old symol, set of new symbols)
            // maps

            if (oldSymbol1 != null) {
                SortedSet<Symbol<T>> collection = alphabet1SymbolMap
                        .get(oldSymbol1);

                if (collection == null) {
                    collection = new TreeSet<Symbol<T>>();
                    alphabet1SymbolMap.put(oldSymbol1, collection);
                }

                collection.add(newSymbol);
            }

            if (oldSymbol2 != null) {
                SortedSet<Symbol<T>> collection = alphabet2SymbolMap
                        .get(oldSymbol2);

                if (collection == null) {
                    collection = new TreeSet<Symbol<T>>();
                    alphabet2SymbolMap.put(oldSymbol2, collection);
                }

                collection.add(newSymbol);
            }
        }

        return new AlphabetMergeResult<T>(new Alphabet<T>(newSymbols), this,
                alphabet1SymbolMap, alphabet, alphabet2SymbolMap);
    }

    /**
     * Computes a <code>Map</code> that maps each symbol pair
     * <code>(x,y)</code> to a set of shared intervals, where <code>x</code>
     * is a symbol of <code>alphabet1</code> or <code>null</code>, and
     * <code>y</code> is a symbol of <code>alphabet2</code> or
     * <code>null</code>. A <code>null</code> symbol represents a
     * hypothetical symbol which includes all the intervals that are not covered
     * by symbols of its related alphabet.
     * <p>
     * The particular property of this implementation is that it does so in
     * linear time by only mapping pairs that have a non-empty shared interval
     * set. The intuitive algorithm would have analyzed all possible pairs,
     * leading to quadratic running time.
     * 
     * @param alphabet1
     *            the first alphabet.
     * @param alphabet2
     *            the second alphabet.
     * @return the (symbol pair,interval set) map.
     */
    private static <T extends Comparable<? super T>> Map<SymbolPair<T>, SortedSet<Interval<T>>> computeSymbolPairIntervalSetMap(
            Alphabet<T> alphabet1,
            Alphabet<T> alphabet2) {

        Map<SymbolPair<T>, SortedSet<Interval<T>>> symbolPairIntervalSetMap = new LinkedHashMap<SymbolPair<T>, SortedSet<Interval<T>>>();

        /*
         * We find all intervals of new symbols by analyzing the space starting
         * from the smallest lower bound of an interval to the highest upper
         * bound.
         */

        // currently analyzed sorted map entries
        Map.Entry<Interval<T>, Symbol<T>> entry1 = null;
        Map.Entry<Interval<T>, Symbol<T>> entry2 = null;

        // iterators
        Iterator<Map.Entry<Interval<T>, Symbol<T>>> i1 = alphabet1.intervalMap
                .entrySet().iterator();
        Iterator<Map.Entry<Interval<T>, Symbol<T>>> i2 = alphabet2.intervalMap
                .entrySet().iterator();

        AdjacencyRealm<T> adjacencyRealm = null;
        T lastUpperBound = null;

        while (entry1 != null || entry2 != null || i1.hasNext() || i2.hasNext()) {
            // if possible, make sure that entry1 and entry2 are filled
            if (entry1 == null && i1.hasNext()) {
                entry1 = i1.next();
            }

            if (entry2 == null && i2.hasNext()) {
                entry2 = i2.next();
            }

            // Compute the lower bound of the new interval
            T lowerBound;

            if (lastUpperBound == null) {
                // On the first iteration we need to apply a special treatment

                // We get the adjacencyRealm
                if (entry1 != null) {
                    adjacencyRealm = entry1.getKey().getAdjacencyRealm();
                }
                else {
                    adjacencyRealm = entry2.getKey().getAdjacencyRealm();
                }

                // We pick the smallest lower bound
                if (entry1 == null) {
                    lowerBound = entry2.getKey().getLowerBound();
                }
                else if (entry2 == null) {
                    lowerBound = entry1.getKey().getLowerBound();
                }
                else {
                    lowerBound = AdjacencyRealm.min(entry1.getKey()
                            .getLowerBound(), entry2.getKey().getLowerBound());
                }
            }
            else {
                lowerBound = adjacencyRealm.next(lastUpperBound);
            }

            // compute the upper bound of the new interval
            T upperBound;

            {
                T upperBoundCandidate1 = null;
                T upperBoundCandidate2 = null;

                if (entry1 != null) {
                    if (lowerBound.compareTo(entry1.getKey().getLowerBound()) < 0) {
                        upperBoundCandidate1 = adjacencyRealm.previous(entry1
                                .getKey().getLowerBound());
                    }
                    else {
                        upperBoundCandidate1 = entry1.getKey().getUpperBound();
                    }
                }

                if (entry2 != null) {
                    if (lowerBound.compareTo(entry2.getKey().getLowerBound()) < 0) {
                        upperBoundCandidate2 = adjacencyRealm.previous(entry2
                                .getKey().getLowerBound());
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
                    upperBound = AdjacencyRealm.min(upperBoundCandidate1,
                            upperBoundCandidate2);
                }
            }

            // create new interval, and related symbol pair
            Interval<T> newInterval = adjacencyRealm.createInterval(lowerBound,
                    upperBound);

            Symbol<T> symbol1;
            if (entry1 != null && newInterval.intersects(entry1.getKey())) {
                symbol1 = entry1.getValue();
            }
            else {
                symbol1 = null;
            }

            Symbol<T> symbol2;
            if (entry2 != null && newInterval.intersects(entry2.getKey())) {
                symbol2 = entry2.getValue();
            }
            else {
                symbol2 = null;
            }

            SymbolPair<T> symbolPair = new SymbolPair<T>(symbol1, symbol2);

            // add interval in (symbol pair,interval set) map
            SortedSet<Interval<T>> intervalSet = symbolPairIntervalSetMap
                    .get(symbolPair);
            if (intervalSet == null) {
                intervalSet = new TreeSet<Interval<T>>();
                symbolPairIntervalSetMap.put(symbolPair, intervalSet);
            }

            intervalSet.add(newInterval);

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

        return symbolPairIntervalSetMap;
    }
}
