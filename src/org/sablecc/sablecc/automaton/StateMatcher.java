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
import java.util.Set;

import org.sablecc.sablecc.automaton.graph.State;
import org.sablecc.sablecc.exception.InternalException;

class StateMatcher<T extends Comparable<? super T>> {

    private final DFA<T> dfa;

    private final NFA<T> nfa;

    private final Map<State<T>, Set<State<T>>> dfaToNfaSetMap = new HashMap<State<T>, Set<State<T>>>();

    private final Map<Set<State<T>>, State<T>> nfaSetToDfaMap = new HashMap<Set<State<T>>, State<T>>();

    StateMatcher(
            final DFA<T> dfa,
            final NFA<T> nfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        this.dfa = dfa;
        this.nfa = nfa;
    }

    State<T> getDFAState(
            Set<State<T>> nfaStates) {

        if (nfaStates == null) {
            throw new InternalException("nfaStates may not be null");
        }

        State<T> dfaState = this.nfaSetToDfaMap.get(nfaStates);

        if (dfaState == null) {

            for (State<T> state : nfaStates) {
                if (!this.nfa.getStates().contains(state)) {
                    throw new InternalException(
                            "invalid nfa state in nfaStates");
                }
            }

            dfaState = new State<T>("state"
                    + this.dfa.getUnstableStates().size());

            this.dfa.getUnstableStates().add(dfaState);

            Set<State<T>> unmodifiableNfaStates = Collections
                    .unmodifiableSet(nfaStates);
            this.nfaSetToDfaMap.put(unmodifiableNfaStates, dfaState);
            this.dfaToNfaSetMap.put(dfaState, unmodifiableNfaStates);
        }

        return dfaState;
    }

    Set<State<T>> getNFAStates(
            State<T> dfaState) {

        if (dfaState == null) {
            throw new InternalException("dfaState may not be null");
        }

        if (!this.dfa.getUnstableStates().contains(dfaState)) {
            throw new InternalException("invalid dfaState");
        }

        Set<State<T>> nfaStates = this.dfaToNfaSetMap.get(dfaState);
        if (nfaStates == null) {
            throw new InternalException(
                    "corrupted internal data structures detected");
        }

        return nfaStates;
    }

    boolean match(
            State<T> dfaState,
            State<T> nfaState) {

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

        Set<State<T>> nfaStates = this.dfaToNfaSetMap.get(dfaState);
        if (nfaStates == null) {
            throw new InternalException(
                    "corrupted internal data structures detected");
        }

        return nfaStates.contains(nfaState);
    }
}
