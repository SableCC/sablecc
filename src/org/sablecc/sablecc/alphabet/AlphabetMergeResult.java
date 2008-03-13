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
 * allows for retrieving the new alphabet. It also maps each symbol, in a merged
 * alphabet, to the equivalent set of symbols in the new alphabet.
 */
public final class AlphabetMergeResult {

    /** The new alphabet. */
    private final Alphabet newAlphabet;

    /**
     * The symbol map. It maps each old symbol (in a merged alphabet) to a set
     * of new symbols that cover the same intervals.
     */
    private final SortedMap<Symbol, SortedSet<Symbol>> mergedAlphabetSymbolMap;

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
        this.mergedAlphabetSymbolMap = null;
    }

    /**
     * Constructs an instance to store the result of merging two distinct
     * alphabets.
     */
    AlphabetMergeResult(
            Alphabet newAlphabet,
            SortedMap<Symbol, SortedSet<Symbol>> mergedAlphabetSymbolMap) {

        if (newAlphabet == null) {
            throw new InternalException("newAlphabet may not be null");
        }

        if (mergedAlphabetSymbolMap == null) {
            throw new InternalException(
                    "mergedAlphabetSymbolMap may not be null");
        }

        this.newAlphabet = newAlphabet;
        this.mergedAlphabetSymbolMap = mergedAlphabetSymbolMap;
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
        if (this.mergedAlphabetSymbolMap == null) {

            TreeSet<Symbol> set = new TreeSet<Symbol>();
            set.add(oldSymbol);
            return set;
        }

        if (!this.mergedAlphabetSymbolMap.containsKey(oldSymbol)) {
            throw new InternalException("oldSymbol is not valid");
        }

        return this.mergedAlphabetSymbolMap.get(oldSymbol);
    }
}
