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
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

/**
 * A non-deterministic finite automaton (or Nfa) is a state machine which as a
 * starting state and an accept state. A NfaState is a state for this kind of
 * automaton.
 */
public final class NfaState<T extends Comparable<? super T>>
        implements Comparable<NfaState<T>> {

    /** The <code>Nfa</code> related to this <code>NfaState</code>. */
    private final Nfa<T> nfa;

    /** The identification number of this <code>NfaState</code>. */
    private final int id;

    /** The sorted map of transitions for this <code>NfaState</code>. */
    private SortedMap<Symbol<T>, SortedSet<NfaState<T>>> transitions;

    /** A stability status for this <code>NfaState</code>. */
    private boolean isStable;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /** The epsilon reach of this <code>NfaState</code>. */
    private SortedSet<NfaState<T>> epsilonReach;

    /** An empty set of <code>NfaState</code>. */
    private final SortedSet<NfaState<T>> emptyNfaStateSet = new TreeSet<NfaState<T>>();

    /**
     * Construct a new <code>NfaState</code> into a provided <code>Nfa</code>.
     * 
     * @param nfa
     *            the <code>Nfa</code>.
     * @throws InternalException
     *             if the provided <code>Nfa</code> is <code>null</code>.
     */
    NfaState(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        this.nfa = nfa;

        this.id = nfa.getNextStateId();
        nfa.addState(this);

        this.transitions = new TreeMap<Symbol<T>, SortedSet<NfaState<T>>>(nfa
                .getSymbolComparator());

        this.isStable = false;
    }

    /**
     * Returns the <code>Nfa</code> of this <code>NfaState</code>.
     * 
     * @return the <code>Nfa</code>.
     */
    public Nfa<T> getNfa() {

        return this.nfa;
    }

    /**
     * Returns the identification number of this <code>NfaState</code>.
     * 
     * @return the identification number.
     */
    public int getId() {

        return this.id;
    }

    /**
     * Returns the transitions of this <code>NfaState</code>.
     * 
     * @return the map of transitions.
     * @throws InternalException
     *             if this <code>NfaState</code> is not stable.
     */
    public SortedMap<Symbol<T>, SortedSet<NfaState<T>>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        return this.transitions;
    }

    /**
     * Returns the targets of this <code>NfaState</code> with a provided
     * symbol.
     * 
     * @param symbol
     *            the provided symbol.
     * 
     * @return the set of targets.
     * 
     * @throws InternalException
     *             if this <code>DfaState</code> is not stable or if the
     *             provided symbol is invalid. A symbol is invalid if it is not
     *             contained in the alphabet of the <code>Dfa</code>.
     */
    public SortedSet<NfaState<T>> getTargets(
            Symbol<T> symbol) {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        if (symbol != null
                && !this.nfa.getAlphabet().getSymbols().contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        SortedSet<NfaState<T>> targets = this.transitions.get(symbol);

        if (targets == null) {
            targets = this.emptyNfaStateSet;
        }

        return targets;
    }

    /**
     * Returns whether this <code>NfaState</code> is equal to the provided
     * object. They are equal if they have the same identification number.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this <code>NfaState</code> and the
     *         object are equal; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(
            Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof NfaState)) {
            return false;
        }

        NfaState nfaState = (NfaState) obj;

        return this.id == nfaState.id;
    }

    /**
     * Return the hashCode of this <code>NfaState</code>.
     * 
     * @return the hashCode.
     */
    @Override
    public int hashCode() {

        return this.id;
    }

    /**
     * Returns the string representation of this <code>NfaState</code>.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "nfaState" + this.id;
        }

        return this.toString;
    }

    /**
     * Compares this <code>NfaState</code> to the provided one.
     * 
     * @param nfaState
     *            the <code>NfaState</code> to compare with.
     * @return an <code>int</code> value: 0 if the two <code>NfaState</code>
     *         are equals, a negative value if this <code>NfaState</code> is
     *         smaller, and a positive value if it is bigger.
     */
    public int compareTo(
            NfaState<T> nfaState) {

        if (this.nfa != nfaState.nfa) {
            throw new InternalException(
                    "cannot compare states from distinct NFAs");
        }

        return this.id - nfaState.id;
    }

    /**
     * Adds a new transition to this <code>NfaState</code>.
     * 
     * @param symbol
     *            the symbol for the new transition.
     * 
     * @param nfaState
     *            the destination of the new transition.
     * 
     * @throws InternalException
     *             if this <code>NfaState</code> is already stable, if the
     *             provided <code>NfaState</code> is <code>null</code> or
     *             invalid, or if the symbol is invalid.
     */
    void addTransition(
            Symbol<T> symbol,
            NfaState<T> nfaState) {

        if (this.isStable) {
            throw new InternalException("a stable state may not be modified");
        }

        if (nfaState == null) {
            throw new InternalException("nfaState may not be null");
        }

        if (symbol != null
                && !this.nfa.getAlphabet().getSymbols().contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        if (this.nfa != nfaState.nfa) {
            throw new InternalException("invalid nfaState");
        }

        SortedSet<NfaState<T>> targets = this.transitions.get(symbol);

        if (targets == null) {
            targets = new TreeSet<NfaState<T>>();
            this.transitions.put(symbol, targets);
        }

        targets.add(nfaState);
    }

    /**
     * Stabilize this <code>NfaState</code>.
     * 
     * @throws InternalException
     *             if this <code>NfaState</code> is already stable.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("state is already stable");
        }

        for (Map.Entry<Symbol<T>, SortedSet<NfaState<T>>> entry : this.transitions
                .entrySet()) {
            entry.setValue(Collections.unmodifiableSortedSet(entry.getValue()));
        }

        this.transitions = Collections.unmodifiableSortedMap(this.transitions);

        this.isStable = true;
    }

    /**
     * Returns the epsilon Reach of this <code>NfaState</code>.
     * 
     * @return a set of NfaStates.
     * 
     * @throws InternalException
     *             if this <code>NfaState</code> is not stable.
     */
    public SortedSet<NfaState<T>> getEpsilonReach() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        if (this.epsilonReach == null) {

            SortedSet<NfaState<T>> epsilonReach = new TreeSet<NfaState<T>>();
            computeEpsilonReach(epsilonReach);
            this.epsilonReach = Collections.unmodifiableSortedSet(epsilonReach);
        }

        return this.epsilonReach;

    }

    /**
     * Compute recursivly the epsilon reach of this <code>NfaState</code>.
     * 
     * @param epsilonReach
     *            a set of NfaState.
     */
    private void computeEpsilonReach(
            SortedSet<NfaState<T>> epsilonReach) {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        // did we already include this state?
        if (!epsilonReach.contains(this)) {

            // no, so add it
            epsilonReach.add(this);

            // do we know its reach?
            if (this.epsilonReach != null) {
                // yes, use it!
                epsilonReach.addAll(this.epsilonReach);
            }
            else {
                // no, we must continue the recursive computation
                for (NfaState<T> nfaState : getTargets(null)) {
                    nfaState.computeEpsilonReach(epsilonReach);
                }
            }
        }

    }
}
