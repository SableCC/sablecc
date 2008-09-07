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
 * A <code>MinimalDfaState<code> is a minimalist state.
 */
public final class MinimalDfaState
        implements Comparable<MinimalDfaState> {

    /** The <code>MinimalDfa<code> of this <code>MinimalDfaState<code>. */
    private final MinimalDfa minimalDfa;

    /** The identification number of this <code>MinimalDfaState<code>. */
    private final int id;

    /**
     * A <code>SortedMap</code> that maps each symbol to its corresponding
     * <code>MinimalDfaState<code>. Represents the transitions.
     */
    private SortedMap<Symbol, MinimalDfaState> transitions;

    /** A stability status for this <code>MinimalDfaState<code>. */
    private boolean isStable;

    /**
     * Cached string representation. Is <code>null</code> when not yet computed.
     */
    private String toString;

    /**
     * Constructs a <code>MinimalDfaState<code> with the provided
     * <code>MinimalDfa</code>.
     */
    MinimalDfaState(
            MinimalDfa minimalDfa) {

        if (minimalDfa == null) {
            throw new InternalException("minimalDfa may not be null");
        }

        this.minimalDfa = minimalDfa;

        this.id = minimalDfa.getNextStateId();
        minimalDfa.addState(this);

        this.transitions = new TreeMap<Symbol, MinimalDfaState>();

        this.isStable = false;
    }

    /**
     * Returns the <code>MinimalDfa<code> of this <code>MinimalDfaState<code>.
     */
    public MinimalDfa getMinimalDfa() {

        return this.minimalDfa;
    }

    /**
     * Returns the identification number of this <code>MinimalDfaState<code>.
     */
    public int getId() {

        return this.id;
    }

    /**
     * Returns a set of the transitions of this <code>MinimalDfaState<code>.
     */
    public SortedMap<Symbol, MinimalDfaState> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        return this.transitions;
    }

    /**
     * Returns a <code>MinimalDfaState<code> representing the target of the
     * provided symbol.
     */
    public MinimalDfaState getTarget(
            Symbol symbol) {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        if (!this.minimalDfa.getAlphabet().getSymbols().contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        MinimalDfaState target = this.transitions.get(symbol);

        if (target == null) {
            target = this.minimalDfa.getDeadEndState();
        }

        return target;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they have equal IDs
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

        MinimalDfaState nfaState = (MinimalDfaState) obj;

        return this.id == nfaState.id;
    }

    /**
     * Returns the hash code of this <code>MinimalDfaState<code>.
     */
    @Override
    public int hashCode() {

        return this.id;
    }

    /**
     * Returns the string representation of this <code>MinimalDfaState<code>.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "dfaState" + this.id;
        }

        return this.toString;
    }

    /**
     * Compares this <code>MinimalDfaState</code> to the provided one. It
     * compares the identification number.
     */
    public int compareTo(
            MinimalDfaState minimalDfaState) {

        if (this.minimalDfa != minimalDfaState.minimalDfa) {
            throw new InternalException(
                    "cannot compare states from distinct MinimalDFAs");
        }

        return this.id - minimalDfaState.id;
    }

    /**
     * Adds a transition to this <code>MinimalDfaState<code>
     * with the provided symbol and <code>MinimalDfaState<code>.
     */
    void addTransition(
            Symbol symbol,
            MinimalDfaState minimalDfaState) {

        if (this.isStable) {
            throw new InternalException("a stable state may not be modified");
        }

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        if (minimalDfaState == null) {
            throw new InternalException("minimalDfaState may not be null");
        }

        if (!this.minimalDfa.getAlphabet().getSymbols().contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        if (this.minimalDfa != minimalDfaState.minimalDfa) {
            throw new InternalException("invalid dfaState");
        }

        if (minimalDfaState == this.minimalDfa.getUnstableDeadEndState()) {
            // Don't add transition to dead-end state
            if (this.transitions.get(symbol) != null) {
                throw new InternalException("target was already set");
            }
        }
        else if (this.transitions.put(symbol, minimalDfaState) != null) {
            throw new InternalException("target was already set");
        }
    }

    /**
     * Stabilizes this <code>MinimalDfaState<code>.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("state is already stable");
        }

        this.transitions = Collections.unmodifiableSortedMap(this.transitions);

        this.isStable = true;
    }
}
