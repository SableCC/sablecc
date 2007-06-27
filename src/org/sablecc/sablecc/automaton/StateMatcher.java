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

package org.sablecc.sablecc.automaton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.sablecc.sablecc.exception.InternalException;

class StateMatcher<T extends Comparable<? super T>> {

    private final Dfa<T> dfa;

    private final Nfa<T> nfa;

    private final Map<DfaState<T>, SortedSet<NfaState<T>>> dfaToNfaSetMap = new HashMap<DfaState<T>, SortedSet<NfaState<T>>>();

    private final Map<SortedSet<NfaState<T>>, DfaState<T>> nfaSetToDfaMap = new HashMap<SortedSet<NfaState<T>>, DfaState<T>>();

    StateMatcher(
            final Dfa<T> dfa,
            final Nfa<T> nfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        this.dfa = dfa;
        this.nfa = nfa;
    }

    DfaState<T> getDfaState(
            SortedSet<NfaState<T>> nfaStates) {

        if (nfaStates == null) {
            throw new InternalException("nfaStates may not be null");
        }

        DfaState<T> dfaState = this.nfaSetToDfaMap.get(nfaStates);

        if (dfaState == null) {

            for (NfaState<T> state : nfaStates) {
                if (!this.nfa.getStates().contains(state)) {
                    throw new InternalException(
                            "invalid nfa state in nfaStates");
                }
            }

            dfaState = new DfaState<T>(this.dfa);

            SortedSet<NfaState<T>> unmodifiableNfaStates = Collections
                    .unmodifiableSortedSet(nfaStates);
            this.nfaSetToDfaMap.put(unmodifiableNfaStates, dfaState);
            this.dfaToNfaSetMap.put(dfaState, unmodifiableNfaStates);
        }

        return dfaState;
    }

    SortedSet<NfaState<T>> getNfaStates(
            DfaState<T> dfaState) {

        if (dfaState == null) {
            throw new InternalException("dfaState may not be null");
        }

        if (!this.dfa.getUnstableStates().contains(dfaState)) {
            throw new InternalException("invalid dfaState");
        }

        SortedSet<NfaState<T>> nfaStates = this.dfaToNfaSetMap.get(dfaState);
        if (nfaStates == null) {
            throw new InternalException(
                    "corrupted internal data structures detected");
        }

        return nfaStates;
    }

    boolean match(
            DfaState<T> dfaState,
            NfaState<T> nfaState) {

        if (dfaState == null) {
            throw new InternalException("dfaState may not be null");
        }

        if (nfaState == null) {
            throw new InternalException("nfaState may not be null");
        }

        if (!this.dfa.getUnstableStates().contains(dfaState)) {
            throw new InternalException("invalid dfaState");
        }

        if (!this.nfa.getStates().contains(nfaState)) {
            throw new InternalException("invalid nfaState");
        }

        SortedSet<NfaState<T>> nfaStates = this.dfaToNfaSetMap.get(dfaState);
        if (nfaStates == null) {
            throw new InternalException(
                    "corrupted internal data structures detected");
        }

        return nfaStates.contains(nfaState);
    }
}
