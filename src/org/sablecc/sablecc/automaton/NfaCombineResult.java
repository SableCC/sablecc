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

import java.util.HashMap;
import java.util.Map;

import org.sablecc.sablecc.exception.InternalException;

class NfaCombineResult<T extends Comparable<? super T>> {

    private Nfa<T> newNfa;

    private Nfa<T> oldNfa1;

    private Map<NfaState<T>, NfaState<T>> oldNfa1StateMap = new HashMap<NfaState<T>, NfaState<T>>();

    private Nfa<T> oldNfa2;

    private Map<NfaState<T>, NfaState<T>> oldNfa2StateMap = new HashMap<NfaState<T>, NfaState<T>>();

    NfaCombineResult(
            Nfa<T> newNfa,
            Nfa<T> oldNfa1,
            Map<NfaState<T>, NfaState<T>> oldNfa1StateMap,
            Nfa<T> oldNfa2,
            Map<NfaState<T>, NfaState<T>> oldNfa2StateMap) {

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

    NfaState<T> getNewState1(
            NfaState<T> oldState,
            Nfa<T> oldNfa) {

        if (oldState == null) {
            throw new InternalException("oldState may not be null");
        }

        if (oldNfa == null) {
            throw new InternalException("oldNfa may not be null");
        }

        if (!oldNfa.getStates().contains(oldState)) {
            throw new InternalException("oldState is not a state of oldNfa");
        }

        if (oldNfa == this.oldNfa1) {
            return this.oldNfa1StateMap.get(oldState);
        }

        throw new InternalException("invalid oldNfa");
    }

    NfaState<T> getNewState2(
            NfaState<T> oldState,
            Nfa<T> oldNfa) {

        if (oldState == null) {
            throw new InternalException("oldState may not be null");
        }

        if (oldNfa == null) {
            throw new InternalException("oldNfa may not be null");
        }

        if (!oldNfa.getStates().contains(oldState)) {
            throw new InternalException("oldState is not a state of oldNfa");
        }

        if (oldNfa == this.oldNfa2) {
            return this.oldNfa2StateMap.get(oldState);
        }

        throw new InternalException("invalid oldNfa");
    }

    NfaState<T> getStartStateMapping1(
            Nfa<T> oldNfa) {

        if (oldNfa == null) {
            throw new InternalException("oldNfa may not be null");
        }

        if (oldNfa == this.oldNfa1) {
            return this.oldNfa1StateMap.get(oldNfa.getStartState());
        }

        throw new InternalException("invalid oldNfa");
    }

    NfaState<T> getStartStateMapping2(
            Nfa<T> oldNfa) {

        if (oldNfa == null) {
            throw new InternalException("oldNfa may not be null");
        }

        if (oldNfa == this.oldNfa2) {
            return this.oldNfa2StateMap.get(oldNfa.getStartState());
        }

        throw new InternalException("invalid oldNfa");
    }

    NfaState<T> getAcceptStateMapping1(
            Nfa<T> oldNfa) {

        if (oldNfa == null) {
            throw new InternalException("oldNfa may not be null");
        }

        if (oldNfa == this.oldNfa1) {
            return this.oldNfa1StateMap.get(oldNfa.getAcceptState());
        }

        throw new InternalException("invalid oldNfa");
    }

    NfaState<T> getAcceptStateMapping2(
            Nfa<T> oldNfa) {

        if (oldNfa == null) {
            throw new InternalException("oldNfa may not be null");
        }

        if (oldNfa == this.oldNfa2) {
            return this.oldNfa2StateMap.get(oldNfa.getAcceptState());
        }

        throw new InternalException("invalid oldNfa");
    }
}
