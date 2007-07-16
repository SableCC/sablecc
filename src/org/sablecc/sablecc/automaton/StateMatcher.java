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
 * A state matcher as a <code>Dfa</code> and a <code>Nfa</code> and is used
 * to determine whether a <code>DfaState</code> and a <code>NfaState</code>
 * match.
 */
class StateMatcher<T extends Comparable<? super T>> {

    /** The <code>Dfa</code> of this state matcher. */
    private final Dfa<T> dfa;

    /** The <code>Nfa</code> of this state matcher. */
    private final Nfa<T> nfa;

    /**
     * A <code>Map</code> that maps a state contained in the <code>Dfa</code>
     * to a corresponding set of its states in the <code>Nfa</code>.
     */
    private final Map<DfaState<T>, SortedSet<NfaState<T>>> dfaToNfaSetMap = new HashMap<DfaState<T>, SortedSet<NfaState<T>>>();

    /**
     * A <code>Map</code> that maps a set of states of the <code>Nfa</code>
     * to a corresponding state in the <code>Dfa</code>.
     */
    private final Map<SortedSet<NfaState<T>>, DfaState<T>> nfaSetToDfaMap = new HashMap<SortedSet<NfaState<T>>, DfaState<T>>();

    /**
     * Constructs a state matcher with the provided <code>Nfa</code> and
     * <code>Dfa</code>.
     * 
     * @param dfa
     *            the <code>Dfa</code>.
     * @param nfa
     *            the <code>Nfa</code>.
     * @throws InternalException
     *             if the provided <code>Nfa</code> or <code>Dfa</code> is
     *             <code>null</code>.
     */
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

    /**
     * Returns the <code>DfaState</code> corresponding to the provided
     * <code>SortedSet</code> of <code>NfaSate</code>.
     * 
     * @param nfaStates
     *            the <code>NfaSate</code>.
     * @return the corresponding <code>DfaState</code>.
     * @throws InternalException
     *             if the provided set of <code>NfaState</code> is
     *             <code>null</code> or if a state from it is not part of this
     *             instance's <code>Nfa</code>.
     */
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

    /**
     * Returns the <code>SortedSet</code> of <code>NfaSate</code>
     * corresponding to the provided <code>DfaSate</code>
     * 
     * @param dfaState
     *            the <code>DfaState</code>.
     * @return the corresponding set of <code>NfaState</code>.
     * @throws InternalException
     *             if the provided <code>DfaState<code> is <code>null</code>,
     *             if it is not contained in this instance's <code>Dfa</code>
     *             or if the constructed set of <code>NfaState</code> is
     *             <code>null</code>.
     */
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

    /**
     * Returns whether the provided <code>DfaState</code> and
     * <code>NfaState<code> match.
     *
     * @param dfaState
     *            the <code>DfaState</code>.
     * @param nfaState
     *            the <code>NfaState</code>.
     * @return <code>true</code> if the provided <code>NfaState</code> is
     *         contained in the set of <code>NfaState</code> corresponding to
     *         the provided <code>DfaState</code>; <code>false</code>
     *         otherwise.
     * @throws InternalException
     *             if the provided <code>DfaState</code> or
     *             <code>NfaState</code> is <code>null</code>, if this
     *             instance's <code>Dfa</code>/<code>Nfa</code> does not
     *             contain the provided <code>DfaState</code>/<code>NfaState</code>
     *             or if the constructed set of <code>NfaState</code> is
     *             <code>null</code>.
     */
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
