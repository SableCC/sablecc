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

/**
 * An instance of this class encapsulates the result of combining two
 * <code>Nfa</code>. It allows for retrieving the new <code>Nfa</code> and
 * for mapping old states to sets of new states.
 */
class NfaCombineResult {

    /** The new <code>Nfa</code>. */
    private Nfa newNfa;

    /** The first combined <code>Nfa</code>. */
    private Nfa oldNfa1;

    /**
     * A <code>SortedMap</code> that maps each state contained in the first
     * old <code>Nfa</code> to its state in this <code>NfaCombineResult</code>.
     */
    private SortedMap<NfaState, NfaState> oldNfa1StateMap = new TreeMap<NfaState, NfaState>();

    /** The second combined <code>Nfa</code>. */
    private Nfa oldNfa2;

    /**
     * A <code>SortedMap</code> that maps each state contained in the second
     * old <code>Nfa</code> to its state in this <code>NfaCombineResult</code>.
     */
    private SortedMap<NfaState, NfaState> oldNfa2StateMap = new TreeMap<NfaState, NfaState>();

    /**
     * Constructs an instance for the result of combining two <code>Nfa</code>
     * instances.
     */
    NfaCombineResult(
            Nfa newNfa,
            Nfa oldNfa1,
            SortedMap<NfaState, NfaState> oldNfa1StateMap,
            Nfa oldNfa2,
            SortedMap<NfaState, NfaState> oldNfa2StateMap) {

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

        for (NfaState oldState : oldNfa1.getStates()) {
            if (oldNfa1StateMap.get(oldState) == null) {
                throw new InternalException("invalid oldNfa1StateMap");
            }
        }

        for (NfaState oldState : oldNfa2.getStates()) {
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

    /**
     * Returns the new <code>Nfa</code> instance.
     */
    Nfa getNewNfa() {

        return this.newNfa;
    }

    /**
     * Returns the state of this instance corresponding to a provided old state.
     * The provided old state must have the same <code>Nfa</code> as the first
     * old state of this instance.
     */
    NfaState getNewNfa1State(
            NfaState oldState) {

        if (oldState == null) {
            throw new InternalException("oldState may not be null");
        }

        if (oldState.getNfa() != this.oldNfa1) {
            throw new InternalException("invalid oldState");
        }

        return this.oldNfa1StateMap.get(oldState);
    }

    /**
     * Returns the state of this instance corresponding to a provided old state.
     * The provided old state must have the same <code>Nfa</code> as the
     * second old state of this instance.
     */
    NfaState getNewNfa2State(
            NfaState oldState) {

        if (oldState == null) {
            throw new InternalException("oldState may not be null");
        }

        if (oldState.getNfa() != this.oldNfa2) {
            throw new InternalException("invalid oldState");
        }

        return this.oldNfa2StateMap.get(oldState);
    }

    /**
     * Returns the start state of the first old state for the new
     * <code>Nfa</code>.
     */
    NfaState getNewNfa1StartState() {

        return this.oldNfa1StateMap.get(this.oldNfa1.getStartState());
    }

    /**
     * Returns the start state of the second old state for the new
     * <code>Nfa</code>.
     */
    NfaState getNewNfa2StartState() {

        return this.oldNfa2StateMap.get(this.oldNfa2.getStartState());
    }

    /**
     * Returns the acceptation state of the first old state for the new
     * <code>Nfa</code>.
     */
    NfaState getNewNfa1AcceptState() {

        return this.oldNfa1StateMap.get(this.oldNfa1.getAcceptState());
    }

    /**
     * Returns the acceptation state of the second old state for the new
     * <code>Nfa</code>.
     */
    NfaState getNewNfa2AcceptState() {

        return this.oldNfa2StateMap.get(this.oldNfa2.getAcceptState());
    }
}
