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
public final class NfaState
        implements Comparable<NfaState> {

    /** The <code>Nfa</code> related to this <code>NfaState</code>. */
    private final Nfa nfa;

    /** The identification number of this <code>NfaState</code>. */
    private final int id;

    /** The sorted map of transitions for this <code>NfaState</code>. */
    private SortedMap<Symbol, SortedSet<NfaState>> transitions;

    /** A stability status for this <code>NfaState</code>. */
    private boolean isStable;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /** The epsilon reach of this <code>NfaState</code>. */
    private SortedSet<NfaState> epsilonReach;

    /** An empty set of <code>NfaState</code>. */
    private final SortedSet<NfaState> emptyNfaStateSet = new TreeSet<NfaState>();

    /**
     * Construct a new <code>NfaState</code> into a provided <code>Nfa</code>.
     */
    NfaState(
            Nfa nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        this.nfa = nfa;

        this.id = nfa.getNextStateId();
        nfa.addState(this);

        this.transitions = new TreeMap<Symbol, SortedSet<NfaState>>(nfa
                .getSymbolComparator());

        this.isStable = false;
    }

    /**
     * Returns the <code>Nfa</code> of this <code>NfaState</code>.
     */
    public Nfa getNfa() {

        return this.nfa;
    }

    /**
     * Returns the identification number of this <code>NfaState</code>.
     */
    public int getId() {

        return this.id;
    }

    /**
     * Returns the transitions of this <code>NfaState</code>.
     */
    public SortedMap<Symbol, SortedSet<NfaState>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        return this.transitions;
    }

    /**
     * Returns the targets of this <code>NfaState</code> with a provided
     * symbol.
     */
    public SortedSet<NfaState> getTargets(
            Symbol symbol) {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        if (symbol != null
                && !this.nfa.getAlphabet().getSymbols().contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        SortedSet<NfaState> targets = this.transitions.get(symbol);

        if (targets == null) {
            targets = this.emptyNfaStateSet;
        }

        return targets;
    }

    /**
     * Returns whether this <code>NfaState</code> is equal to the provided
     * object. They are equal if they have the same identification number.
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

        NfaState nfaState = (NfaState) obj;

        return this.id == nfaState.id;
    }

    /**
     * Return the hashCode of this <code>NfaState</code>.
     */
    @Override
    public int hashCode() {

        return this.id;
    }

    /**
     * Returns the string representation of this <code>NfaState</code>.
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
     */
    public int compareTo(
            NfaState nfaState) {

        if (this.nfa != nfaState.nfa) {
            throw new InternalException(
                    "cannot compare states from distinct NFAs");
        }

        return this.id - nfaState.id;
    }

    /**
     * Adds a new transition to this <code>NfaState</code>.
     */
    void addTransition(
            Symbol symbol,
            NfaState nfaState) {

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

        SortedSet<NfaState> targets = this.transitions.get(symbol);

        if (targets == null) {
            targets = new TreeSet<NfaState>();
            this.transitions.put(symbol, targets);
        }

        targets.add(nfaState);
    }

    /**
     * Stabilize this <code>NfaState</code>.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("state is already stable");
        }

        for (Map.Entry<Symbol, SortedSet<NfaState>> entry : this.transitions
                .entrySet()) {
            entry.setValue(Collections.unmodifiableSortedSet(entry.getValue()));
        }

        this.transitions = Collections.unmodifiableSortedMap(this.transitions);

        this.isStable = true;
    }

    /**
     * Returns the epsilon Reach of this <code>NfaState</code>.
     */
    public SortedSet<NfaState> getEpsilonReach() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        if (this.epsilonReach == null) {

            SortedSet<NfaState> epsilonReach = new TreeSet<NfaState>();
            computeEpsilonReach(epsilonReach);
            this.epsilonReach = Collections.unmodifiableSortedSet(epsilonReach);
        }

        return this.epsilonReach;

    }

    /**
     * Compute recursivly the epsilon reach of this <code>NfaState</code>.
     */
    private void computeEpsilonReach(
            SortedSet<NfaState> epsilonReach) {

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
                for (NfaState nfaState : getTargets(null)) {
                    nfaState.computeEpsilonReach(epsilonReach);
                }
            }
        }

    }
}
