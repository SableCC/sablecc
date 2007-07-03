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
class NfaCombineResult<T extends Comparable<? super T>> {

    /** The new <code>Nfa</code>. */
    private Nfa<T> newNfa;

    /** The first combined <code>Nfa</code>. */
    private Nfa<T> oldNfa1;

    /**
     * A <code>SortedMap</code> that maps each state contained in the first
     * old <code>Nfa</code> to its state in this <code>NfaCombineResult</code>.
     */
    private SortedMap<NfaState<T>, NfaState<T>> oldNfa1StateMap = new TreeMap<NfaState<T>, NfaState<T>>();

    /** The second combined <code>Nfa</code>. */
    private Nfa<T> oldNfa2;

    /**
     * A <code>SortedMap</code> that maps each state contained in the second
     * old <code>Nfa</code> to its state in this <code>NfaCombineResult</code>.
     */
    private SortedMap<NfaState<T>, NfaState<T>> oldNfa2StateMap = new TreeMap<NfaState<T>, NfaState<T>>();

    /**
     * Constructs a new instance for the result of combining two
     * <code>Nfa</code> instances.
     * 
     * @param newNfa
     *            the new <code>Nfa</code>.
     * @param oldNfa1
     *            the first combined <code>Nfa</code>.
     * @param oldNfa1StateMap
     *            the states of first <code>Nfa</code>.
     * @param oldNfa2
     *            the second combined <code>Nfa</code>.
     * @param oldNfa2StateMap
     *            the states of second <code>Nfa</code>.
     * @throws InternalException
     *             if one of the <code>Nfa</code> instances, state map or
     *             states is <code>null</code>.
     */
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

    /**
     * Returns the new <code>Nfa</code> instance.
     * 
     * @return the new <code>Nfa</code>.
     */
    Nfa<T> getNewNfa() {

        return this.newNfa;
    }

    /**
     * Returns the state of this instance corresponding to a provided old state.
     * The provided old state must have the same <code>Nfa</code> as the first
     * old state of this instance.
     * 
     * @param oldState
     *            the old state.
     * @return the corresponding state.
     * @throws InternalException
     *             if the <code>oldState</code> is <code>null</code> or
     *             invalid.
     */
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

    /**
     * Returns the state of this instance corresponding to a provided old state.
     * The provided old state must have the same <code>Nfa</code> as the
     * second old state of this instance.
     * 
     * @param oldState
     *            the old state.
     * @return the corresponding state.
     * @throws InternalException
     *             if the <code>oldState</code> is <code>null</code> or
     *             invalid.
     */
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

    /**
     * Returns the start state of the first old state for the new
     * <code>Nfa</code>.
     * 
     * @return the first start state.
     */
    NfaState<T> getNewNfa1StartState() {

        return this.oldNfa1StateMap.get(this.oldNfa1.getStartState());
    }

    /**
     * Returns the start state of the second old state for the new
     * <code>Nfa</code>.
     * 
     * @return the second start state.
     */
    NfaState<T> getNewNfa2StartState() {

        return this.oldNfa2StateMap.get(this.oldNfa2.getStartState());
    }

    /**
     * Returns the acceptation state of the first old state for the new
     * <code>Nfa</code>.
     * 
     * @return the first acceptation state.
     */
    NfaState<T> getNewNfa1AcceptState() {

        return this.oldNfa1StateMap.get(this.oldNfa1.getAcceptState());
    }

    /**
     * Returns the acceptation state of the second old state for the new
     * <code>Nfa</code>.
     * 
     * @return the second acceptation state.
     */
    NfaState<T> getNewNfa2AcceptState() {

        return this.oldNfa2StateMap.get(this.oldNfa2.getAcceptState());
    }
}
