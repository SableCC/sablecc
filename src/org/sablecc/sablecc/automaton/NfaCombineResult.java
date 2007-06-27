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

import java.util.SortedMap;
import java.util.TreeMap;

import org.sablecc.sablecc.exception.InternalException;

class NfaCombineResult<T extends Comparable<? super T>> {

    private Nfa<T> newNfa;

    private Nfa<T> oldNfa1;

    private SortedMap<NfaState<T>, NfaState<T>> oldNfa1StateMap = new TreeMap<NfaState<T>, NfaState<T>>();

    private Nfa<T> oldNfa2;

    private SortedMap<NfaState<T>, NfaState<T>> oldNfa2StateMap = new TreeMap<NfaState<T>, NfaState<T>>();

    NfaCombineResult(
            Nfa<T> newNfa,
            Nfa<T> oldNfa1,
            SortedMap<NfaState<T>, NfaState<T>> oldNfa1StateMap,
            Nfa<T> oldNfa2,
            SortedMap<NfaState<T>, NfaState<T>> oldNfa2StateMap) {

        if (newNfa == null) {
            throw new InternalException("newNfa may not be null");
        }

        if (oldNfa1 == null) {
            throw new InternalException("oldNfa1 may not be null");
        }

        if (oldNfa1StateMap == null) {
            throw new InternalException("oldNfa1StateMap may not be null");
        }

        if (oldNfa2 == null) {
            throw new InternalException("oldNfa2 may not be null");
        }

        if (oldNfa2StateMap == null) {
            throw new InternalException("oldNfa2StateMap may not be null");
        }

        for (NfaState<T> oldState : oldNfa1.getStates()) {
            if (oldNfa1StateMap.get(oldState) == null) {
                throw new InternalException("invalid oldNfa1StateMap");
            }
        }

        for (NfaState<T> oldState : oldNfa2.getStates()) {
            if (oldNfa2StateMap.get(oldState) == null) {
                throw new InternalException("invalid oldNfa2StateMap");
            }
        }

        this.newNfa = newNfa;
        this.oldNfa1 = oldNfa1;
        this.oldNfa1StateMap = oldNfa1StateMap;
        this.oldNfa2 = oldNfa2;
        this.oldNfa2StateMap = oldNfa2StateMap;
    }

    Nfa<T> getNewNfa() {

        return this.newNfa;
    }

    NfaState<T> getNewNfa1State(
            NfaState<T> oldState) {

        if (oldState == null) {
            throw new InternalException("oldState may not be null");
        }

        if (oldState.getNfa() != this.oldNfa1) {
            throw new InternalException("invalid oldState");
        }

        return this.oldNfa1StateMap.get(oldState);
    }

    NfaState<T> getNewNfa2State(
            NfaState<T> oldState) {

        if (oldState == null) {
            throw new InternalException("oldState may not be null");
        }

        if (oldState.getNfa() != this.oldNfa2) {
            throw new InternalException("invalid oldState");
        }

        return this.oldNfa2StateMap.get(oldState);
    }

    NfaState<T> getNewNfa1StartState() {

        return this.oldNfa1StateMap.get(this.oldNfa1.getStartState());
    }

    NfaState<T> getNewNfa2StartState() {

        return this.oldNfa2StateMap.get(this.oldNfa2.getStartState());
    }

    NfaState<T> getNewNfa1AcceptState() {

        return this.oldNfa1StateMap.get(this.oldNfa1.getAcceptState());
    }

    NfaState<T> getNewNfa2AcceptState() {

        return this.oldNfa2StateMap.get(this.oldNfa2.getAcceptState());
    }
}
