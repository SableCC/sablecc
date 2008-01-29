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
 * An instance of this class stores the result of merging two alphabets. It
 * allows for retrieving the new alphabet and for mapping old symbols to sets of
 * new symbols.
 */
public final class AlphabetMergeResult {

    /** The new alphabet. */
    private final Alphabet newAlphabet;

    /** The first merged alphabet. */
    private final Alphabet mergedAlphabet1;

    /**
     * The symbol map of the first alphabet. It maps each old symbol to a set of
     * new symbols that cover the same intervals.
     */
    private final SortedMap<Symbol, SortedSet<Symbol>> mergedAlphabet1SymbolMap;

    /** The second merged alphabet. */
    private final Alphabet mergedAlphabet2;

    /**
     * The symbol map of the second alphabet. It maps each old symbol to a set
     * of new symbols that cover the same intervals.
     */
    private final SortedMap<Symbol, SortedSet<Symbol>> mergedAlphabet2SymbolMap;

    /**
     * Constructs an instance to store the result of merging an alphabet with
     * itself.
     */
    AlphabetMergeResult(
            Alphabet alphabet) {

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
     * Constructs an instance to store the result of merging two distinct
     * alphabets.
     */
    AlphabetMergeResult(
            Alphabet newAlphabet,
            Alphabet mergedAlphabet1,
            SortedMap<Symbol, SortedSet<Symbol>> mergedAlphabet1SymbolMap,
            Alphabet mergedAlphabet2,
            SortedMap<Symbol, SortedSet<Symbol>> mergedAlphabet2SymbolMap) {

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

        for (Symbol oldSymbol : mergedAlphabet1.getSymbols()) {
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

        for (Symbol oldSymbol : mergedAlphabet2.getSymbols()) {
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
     */
    public Alphabet getNewAlphabet() {

        return this.newAlphabet;
    }

    /**
     * Returns the set of new symbols covering the same intervals as the
     * provided old symbol.
     */
    public SortedSet<Symbol> getNewSymbols(
            Symbol oldSymbol) {

        if (oldSymbol == null) {
            throw new InternalException("oldSymbol may not be null");
        }

        // special case for an alphabet merged with itslef
        if (this.mergedAlphabet1 == null) {

            if (!this.newAlphabet.getSymbols().contains(oldSymbol)) {
                throw new InternalException(
                        "oldSymbol is not an element of a merged alphabet");
            }

            TreeSet<Symbol> set = new TreeSet<Symbol>();
            set.add(oldSymbol);
            return set;
        }

        // check that one of the merged alphabet contains oldSymbol

        // Note that if both merged alphabets contain oldSymbol, it necessarily
        // maps to the same new symbol set.

        SortedMap<Symbol, SortedSet<Symbol>> mergedAlphabetSymbolMap;

        if (this.mergedAlphabet1.getSymbols().contains(oldSymbol)) {
            mergedAlphabetSymbolMap = this.mergedAlphabet1SymbolMap;
        }
        else if (this.mergedAlphabet2.getSymbols().contains(oldSymbol)) {
            mergedAlphabetSymbolMap = this.mergedAlphabet2SymbolMap;
        }
        else {
            throw new InternalException(
                    "oldSymbol is not an element of a merged alphabet");
        }

        return mergedAlphabetSymbolMap.get(oldSymbol);
    }
}
