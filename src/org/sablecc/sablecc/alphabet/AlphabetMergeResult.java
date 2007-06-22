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

import org.sablecc.sablecc.exception.InternalException;

/**
 * The class represents the result of a merge of alphabets.
 * 
 * An alphabet merge result is represented by the new merged alphabet, the two
 * old alphabets and their symbol maps.
 */
public final class AlphabetMergeResult<T extends Comparable<? super T>> {

    /** The new merged alphabet. */
    private final Alphabet<T> newAlphabet;

    /** The first old alphabet to merge. */
    private final Alphabet<T> oldAlphabet1;

    /** The symbol map for first alphabet. */
    private final SortedMap<Symbol<T>, SortedSet<Symbol<T>>> oldAlphabet1SymbolMap;

    /** The second old alphabet to merge. */
    private final Alphabet<T> oldAlphabet2;

    /** The symbol map for second alphabet. */
    private final SortedMap<Symbol<T>, SortedSet<Symbol<T>>> oldAlphabet2SymbolMap;

    /**
     * Constructs the result of an alphabet merge.
     * 
     * @param newAlphabet
     *            the new merged alphabet.
     * @param oldAlphabet1
     *            the first old alphabet to merge.
     * @param oldAlphabet1SymbolMap
     *            the symbol map for first alphabet.
     * @param oldAlphabet2
     *            the second old alphabet to merge.
     * @param oldAlphabet2SymbolMap
     *            the symbol map for second alphabet.
     */
    AlphabetMergeResult(
            Alphabet<T> newAlphabet,
            Alphabet<T> oldAlphabet1,
            SortedMap<Symbol<T>, SortedSet<Symbol<T>>> oldAlphabet1SymbolMap,
            Alphabet<T> oldAlphabet2,
            SortedMap<Symbol<T>, SortedSet<Symbol<T>>> oldAlphabet2SymbolMap) {

        this.newAlphabet = newAlphabet;

        this.oldAlphabet1 = oldAlphabet1;
        this.oldAlphabet1SymbolMap = oldAlphabet1SymbolMap;

        this.oldAlphabet2 = oldAlphabet2;
        this.oldAlphabet2SymbolMap = oldAlphabet2SymbolMap;
    }

    /**
     * Returns the new alphabet created from the merge.
     * 
     * @return the new merged alphabet.
     */
    public Alphabet<T> getNewAlphabet() {

        return this.newAlphabet;
    }

    /**
     * Returns a set of the new symbols representing one old symbol after a
     * merge. Verifications are made for the provided alphabet and symbol not to
     * be <code>null</code> and for the symbol to be an element of the
     * alphabet.
     * 
     * @param oldSymbol
     * @param oldAlphabet
     * @return a set of the new symbols.
     * @throws InternalException
     *             if an old symbol or alphabet is <code>null</code>, if the
     *             provided symbol is not an element of the alphabet or if the
     *             alphabet is not valid.
     */
    public SortedSet<Symbol<T>> getNewSymbols(
            Symbol<T> oldSymbol,
            Alphabet<T> oldAlphabet) {

        if (oldSymbol == null) {
            throw new InternalException("oldSymbol may not be null");
        }

        if (oldAlphabet == null) {
            throw new InternalException("oldAlphabet may not be null");
        }

        if (!oldAlphabet.getSymbols().contains(oldSymbol)) {
            throw new InternalException(
                    "oldSymbol is not an element of oldAlphabet");
        }

        if (oldAlphabet == this.oldAlphabet1) {
            return this.oldAlphabet1SymbolMap.get(oldSymbol);
        }

        if (oldAlphabet == this.oldAlphabet2) {
            return this.oldAlphabet2SymbolMap.get(oldSymbol);
        }

        throw new InternalException("invalid alphabet");
    }
}
