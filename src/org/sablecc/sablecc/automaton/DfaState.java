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
import java.util.SortedMap;
import java.util.TreeMap;

import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

/**
 * A deterministic finite automaton (DFA) is a state machine which permit one
 * and only one transition from one state to another with an input symbol. A
 * DfaState is a state for this kind of automaton.
 */
public final class DfaState<T extends Comparable<? super T>>
        implements Comparable<DfaState<T>> {

    /** The <code>Dfa</code> related to this <code>DfaState</code>. */
    private final Dfa<T> dfa;

    /** The identification number of this <code>DfaState</code>. */
    private final int id;

    /** The sorted map of transitions for this <code>DfaState</code>. */
    private SortedMap<Symbol<T>, DfaState<T>> transitions;

    /** A stability status for this <code>DfaState</code>. */
    private boolean isStable;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /**
     * Construct a new <code>DfaState</code> into a provided <code>Dfa</code>.
     * 
     * @param dfa
     *            the <code>Dfa</code>.
     * @throws InternalException
     *             if the provided <code>Dfa</code> is <code>null</code>.
     */
    DfaState(
            Dfa<T> dfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        this.dfa = dfa;

        this.id = dfa.getNextStateId();
        dfa.addState(this);

        this.transitions = new TreeMap<Symbol<T>, DfaState<T>>();

        this.isStable = false;
    }

    /**
     * Returns the <code>Dfa</code> of this <code>DfaState</code>.
     * 
     * @return the <code>Dfa</code>.
     */
    public Dfa<T> getDfa() {

        return this.dfa;
    }

    /**
     * Returns the identification number of this <code>DfaState</code>.
     * 
     * @return the identification number.
     */
    public int getId() {

        return this.id;
    }

    /**
     * Returns the transitions of this <code>DfaState</code>.
     * 
     * @return the map of transitions.
     * @throws InternalException
     *             if this <code>DfaState</code> is not stable.
     */
    public SortedMap<Symbol<T>, DfaState<T>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        return this.transitions;
    }

    /**
     * Returns the unstable transitions of this <code>DfaState</code>.
     * 
     * @return the map of unstable transitions.
     */
    SortedMap<Symbol<T>, DfaState<T>> getUnstableTransitions() {

        return this.transitions;
    }

    /**
     * Returns the target of this <code>DfaState</code> with a provided
     * symbol.
     * 
     * @param symbol
     *            the provided symbol.
     * 
     * @return the target.
     * 
     * @throws InternalException
     *             if this <code>DfaState</code> is not stable, if the
     *             provided symbol is <code>null</code> or if the symbol is
     *             not contained in the alphabet of the <code>Dfa</code>.
     */
    public DfaState<T> getTarget(
            Symbol<T> symbol) {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        if (!this.dfa.getAlphabet().getSymbols().contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        DfaState<T> target = this.transitions.get(symbol);

        if (target == null) {
            target = this.dfa.getDeadEndState();
        }

        return target;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they have an identical identification number.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this <code>DfaState</code> and the
     *         object are equal; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(
            Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        DfaState nfaState = (DfaState) obj;

        return this.id == nfaState.id;
    }

    /**
     * Return the hashCode of this <code>DfaState</code>, based on its
     * identification number.
     * 
     * @return the hashCode.
     */
    @Override
    public int hashCode() {

        return this.id;
    }

    /**
     * Returns the string representation of this <code>DfaState</code>.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "dfaState" + this.id;
        }

        return this.toString;
    }

    /**
     * Compares this <code>DfaState</code> to the provided one. It compares
     * the identification number.
     * 
     * @param dfaState
     *            the <code>DfaState</code> to compare with.
     * @return an <code>int</code> value: 0 if the two <code>DfaState</code>
     *         are equals, a negative value if this <code>DfaState</code> is
     *         smaller, and a positive value if it is bigger.
     */
    public int compareTo(
            DfaState<T> dfaState) {

        if (this.dfa != dfaState.dfa) {
            throw new InternalException(
                    "cannot compare states from distinct DFAs");
        }

        return this.id - dfaState.id;
    }

    /**
     * Adds a new transition to this <code>DfaState</code>.
     * 
     * @param symbol
     *            the symbol for the new transition.
     * 
     * @param dfaState
     *            the destination of the new transition.
     * 
     * @throws InternalException
     *             if this <code>DfaState</code> is already stable, if the
     *             provided symbol is <code>null</code> or invalid, if the
     *             provided <code>DfaState</code> is <code>null</code> or
     *             invalid, or if the transition already exists.
     */
    void addTransition(
            Symbol<T> symbol,
            DfaState<T> dfaState) {

        if (this.isStable) {
            throw new InternalException("a stable state may not be modified");
        }

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        if (dfaState == null) {
            throw new InternalException("dfaState may not be null");
        }

        if (!this.dfa.getAlphabet().getSymbols().contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        if (this.dfa != dfaState.dfa) {
            throw new InternalException("invalid dfaState");
        }

        if (dfaState == this.dfa.getUnstableDeadEndState()) {
            // Don't add transition to dead-end state
            if (this.transitions.get(symbol) != null) {
                throw new InternalException("target was already set");
            }
        }
        else if (this.transitions.put(symbol, dfaState) != null) {
            throw new InternalException("target was already set");
        }
    }

    /**
     * Removes all the transitions of this <code>DfaState</code>.
     * 
     * @throws InternalException
     *             if this <code>DfaState</code> is already stable.
     */
    void removeTransitions() {

        if (this.isStable) {
            throw new InternalException("a stable state may not be modified");
        }

        this.transitions.clear();
    }

    /**
     * Stabilize this <code>DfaState</code>.
     * 
     * @throws InternalException
     *             if this <code>DfaState</code> is already stable.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("state is already stable");
        }

        this.transitions = Collections.unmodifiableSortedMap(this.transitions);

        this.isStable = true;
    }
}
