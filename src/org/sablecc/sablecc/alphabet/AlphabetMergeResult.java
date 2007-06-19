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

import java.util.SortedMap;
import java.util.SortedSet;

import org.sablecc.sablecc.exception.InternalException;

public class AlphabetMergeResult<T extends Comparable<? super T>> {

    private final Alphabet<T> newAlphabet;

    private final Alphabet<T> oldAlphabet1;

    private final SortedMap<Symbol<T>, SortedSet<Symbol<T>>> oldAlphabet1SymbolMap;

    private final Alphabet<T> oldAlphabet2;

    private final SortedMap<Symbol<T>, SortedSet<Symbol<T>>> oldAlphabet2SymbolMap;

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

    public Alphabet<T> getNewAlphabet() {

        return this.newAlphabet;
    }

    public SortedMap<Symbol<T>, SortedSet<Symbol<T>>> getSymbolMap(
            Alphabet<T> alphabet) {

        if (alphabet == this.oldAlphabet1) {
            return this.oldAlphabet1SymbolMap;
        }

        if (alphabet == this.oldAlphabet2) {
            return this.oldAlphabet2SymbolMap;
        }

        throw new InternalException("invalid alphabet");
    }
}
