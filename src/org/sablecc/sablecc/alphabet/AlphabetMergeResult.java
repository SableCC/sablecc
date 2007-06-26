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

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sablecc.sablecc.exception.InternalException;

/**
 * An instance of this class encapsulates the result of merging two alphabets.
 * It allows for retrieving the new alphabet and for mapping old symbols to sets
 * of new symbols.
 */
public final class AlphabetMergeResult<T extends Comparable<? super T>> {

    /** The new alphabet. */
    private final Alphabet<T> newAlphabet;

    /** The first merged alphabet. */
    private final Alphabet<T> mergedAlphabet1;

    /**
     * The symbol map of the first alphabet. It maps each old symbol to a set of
     * new symbols that cover the same intervals.
     */
    private final SortedMap<Symbol<T>, SortedSet<Symbol<T>>> mergedAlphabet1SymbolMap;

    /** The second merged alphabet. */
    private final Alphabet<T> mergedAlphabet2;

    /**
     * The symbol map of the second alphabet. It maps each old symbol to a set
     * of new symbols that cover the same intervals.
     */
    private final SortedMap<Symbol<T>, SortedSet<Symbol<T>>> mergedAlphabet2SymbolMap;

    /**
     * Constructs a new instance for the result of merging an alphabet with
     * itself.
     * 
     * @param alphabet
     *            the alphabet.
     * @throws InternalException
     *             if the alphabet is <code>null</code>.
     */
    AlphabetMergeResult(
            Alphabet<T> alphabet) {

        if (alphabet == null) {
            throw new InternalException("alphabet may not be null");
        }

        this.newAlphabet = alphabet;

        this.mergedAlphabet1 = null;
        this.mergedAlphabet1SymbolMap = null;

        this.mergedAlphabet2 = null;
        this.mergedAlphabet2SymbolMap = null;
    }

    /**
     * Constructs a new instance for the result of merging two distinct
     * alphabets.
     * 
     * @param newAlphabet
     *            the new alphabet.
     * @param mergedAlphabet1
     *            the first merged alphabet.
     * @param mergedAlphabet1SymbolMap
     *            the symbol map of the first alphabet.
     * @param mergedAlphabet2
     *            the second merged alphabet.
     * @param mergedAlphabet2SymbolMap
     *            the symbol map of the second alphabet.
     * @throws InternalException
     *             if any parameter is <code>null</code>, or if the merged
     *             alphabets are not distinct.
     */
    AlphabetMergeResult(
            Alphabet<T> newAlphabet,
            Alphabet<T> mergedAlphabet1,
            SortedMap<Symbol<T>, SortedSet<Symbol<T>>> mergedAlphabet1SymbolMap,
            Alphabet<T> mergedAlphabet2,
            SortedMap<Symbol<T>, SortedSet<Symbol<T>>> mergedAlphabet2SymbolMap) {

        if (newAlphabet == null) {
            throw new InternalException("newAlphabet may not be null");
        }

        if (mergedAlphabet1 == null) {
            throw new InternalException("mergedAlphabet1 may not be null");
        }

        if (mergedAlphabet1SymbolMap == null) {
            throw new InternalException(
                    "mergedAlphabet1SymbolMap may not be null");
        }

        for (Symbol<T> oldSymbol : mergedAlphabet1.getSymbols()) {
            if (mergedAlphabet1SymbolMap.get(oldSymbol) == null) {
                throw new InternalException(
                        "mergedAlphabet1SymbolMap is invalid");
            }
        }

        if (mergedAlphabet2 == null) {
            throw new InternalException("mergedAlphabet2 may not be null");
        }

        if (mergedAlphabet2SymbolMap == null) {
            throw new InternalException(
                    "mergedAlphabet2SymbolMap may not be null");
        }

        if (mergedAlphabet1 == mergedAlphabet2) {
            throw new InternalException("wrong constructor");
        }

        for (Symbol<T> oldSymbol : mergedAlphabet2.getSymbols()) {
            if (mergedAlphabet2SymbolMap.get(oldSymbol) == null) {
                throw new InternalException(
                        "mergedAlphabet2SymbolMap is invalid");
            }
        }

        this.newAlphabet = newAlphabet;

        this.mergedAlphabet1 = mergedAlphabet1;
        this.mergedAlphabet1SymbolMap = mergedAlphabet1SymbolMap;

        this.mergedAlphabet2 = mergedAlphabet2;
        this.mergedAlphabet2SymbolMap = mergedAlphabet2SymbolMap;
    }

    /**
     * Returns the new alphabet resulting from the merge.
     * 
     * @return the new alphabet.
     */
    public Alphabet<T> getNewAlphabet() {

        return this.newAlphabet;
    }

    /**
     * Returns the set of new symbols covering the same intervals as the
     * provided old symbol from the provided merged alphabet.
     * 
     * @param oldSymbol
     *            the symbol.
     * @param mergedAlphabet
     *            the alphabet.
     * @return the set of new symbols.
     * @throws InternalException
     *             if the old symbol or the old alphabet is <code>null</code>.
     */
    public SortedSet<Symbol<T>> getNewSymbols(
            Symbol<T> oldSymbol,
            Alphabet<T> mergedAlphabet) {

        if (oldSymbol == null) {
            throw new InternalException("oldSymbol may not be null");
        }

        if (mergedAlphabet == null) {
            throw new InternalException("mergedAlphabet may not be null");
        }

        if (!mergedAlphabet.getSymbols().contains(oldSymbol)) {
            throw new InternalException(
                    "oldSymbol is not an element of mergedAlphabet");
        }

        // special case for an alphabet merged with itslef
        if (this.mergedAlphabet1 == null) {
            if (mergedAlphabet != this.newAlphabet) {
                throw new InternalException("mergedAlphabet is invalid");
            }

            TreeSet<Symbol<T>> set = new TreeSet<Symbol<T>>();
            set.add(oldSymbol);
            return set;
        }

        if (mergedAlphabet == this.mergedAlphabet1) {
            return this.mergedAlphabet1SymbolMap.get(oldSymbol);
        }

        if (mergedAlphabet == this.mergedAlphabet2) {
            return this.mergedAlphabet2SymbolMap.get(oldSymbol);
        }

        throw new InternalException("mergedAlphabet is invalid");
    }
}
