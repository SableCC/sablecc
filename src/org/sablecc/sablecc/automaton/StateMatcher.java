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

/**
 * A state matcher as a <code>Dfa</code> and a <code>Nfa</code> and is used to
 * determine whether a <code>DfaState</code> and a <code>NfaState</code> match.
 */
class StateMatcher {

    /** The <code>Dfa</code> of this state matcher. */
    private final Dfa dfa;

    /** The <code>Nfa</code> of this state matcher. */
    private final Nfa nfa;

    /**
     * A <code>Map</code> that maps a state contained in the <code>Dfa</code> to
     * a corresponding set of its states in the <code>Nfa</code>.
     */
    private final Map<DfaState, SortedSet<NfaState>> dfaToNfaSetMap = new HashMap<DfaState, SortedSet<NfaState>>();

    /**
     * A <code>Map</code> that maps a set of states of the <code>Nfa</code> to a
     * corresponding state in the <code>Dfa</code>.
     */
    private final Map<SortedSet<NfaState>, DfaState> nfaSetToDfaMap = new HashMap<SortedSet<NfaState>, DfaState>();

    /**
     * Constructs a state matcher with the provided <code>Nfa</code> and
     * <code>Dfa</code>.
     */
    StateMatcher(
            final Dfa dfa,
            final Nfa nfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        this.dfa = dfa;
        this.nfa = nfa;
    }

    /**
     * Returns the <code>DfaState</code> corresponding to the provided
     * <code>SortedSet</code> of <code>NfaSate</code>.
     */
    DfaState getDfaState(
            SortedSet<NfaState> nfaStates) {

        if (nfaStates == null) {
            throw new InternalException("nfaStates may not be null");
        }

        DfaState dfaState = this.nfaSetToDfaMap.get(nfaStates);

        if (dfaState == null) {

            for (NfaState state : nfaStates) {
                if (!this.nfa.getStates().contains(state)) {
                    throw new InternalException(
                            "invalid nfa state in nfaStates");
                }
            }

            dfaState = new DfaState(this.dfa);

            SortedSet<NfaState> unmodifiableNfaStates = Collections
                    .unmodifiableSortedSet(nfaStates);
            this.nfaSetToDfaMap.put(unmodifiableNfaStates, dfaState);
            this.dfaToNfaSetMap.put(dfaState, unmodifiableNfaStates);
        }

        return dfaState;
    }

    /**
     * Returns the <code>SortedSet</code> of <code>NfaSate</code> corresponding
     * to the provided <code>DfaSate</code>
     */
    SortedSet<NfaState> getNfaStates(
            DfaState dfaState) {

        if (dfaState == null) {
            throw new InternalException("dfaState may not be null");
        }

        if (!this.dfa.getUnstableStates().contains(dfaState)) {
            throw new InternalException("invalid dfaState");
        }

        SortedSet<NfaState> nfaStates = this.dfaToNfaSetMap.get(dfaState);
        if (nfaStates == null) {
            throw new InternalException(
                    "corrupted internal data structures detected");
        }

        return nfaStates;
    }

    /**
     * Returns whether the provided <code>DfaState</code> and
     * <code>NfaState<code> match.
     */
    boolean match(
            DfaState dfaState,
            NfaState nfaState) {

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

        SortedSet<NfaState> nfaStates = this.dfaToNfaSetMap.get(dfaState);
        if (nfaStates == null) {
            throw new InternalException(
                    "corrupted internal data structures detected");
        }

        return nfaStates.contains(nfaState);
    }
}
