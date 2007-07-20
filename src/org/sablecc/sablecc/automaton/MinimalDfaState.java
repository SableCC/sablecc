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
public final class MinimalDfaState<T extends Comparable<? super T>>
        implements Comparable<MinimalDfaState<T>> {

    /** The <code>MinimalDfa<code> of this <code>MinimalDfaState<code>. */
    private final MinimalDfa<T> minimalDfa;

    /** The identification number of this <code>MinimalDfaState<code>. */
    private final int id;

    /**
     * A <code>SortedMap</code> that maps each symbol to its corresponding
     * <code>MinimalDfaState<code>. Represents the transitions.
     */
    private SortedMap<Symbol<T>, MinimalDfaState<T>> transitions;

    /** A stability status for this <code>MinimalDfaState<code>. */
    private boolean isStable;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /**
     * Constructs a <code>MinimalDfaState<code> with the provided
     * <code>MinimalDfa</code>.
     *
     * @param minimalDfa
     *             the <code>MinimalDfa<code>.
     * @throws InternalException
     *             if the provided <code>MinimalDfa<code> is <code>null</code>.
     */
    MinimalDfaState(
            MinimalDfa<T> minimalDfa) {

        if (minimalDfa == null) {
            throw new InternalException("minimalDfa may not be null");
        }

        this.minimalDfa = minimalDfa;

        this.id = minimalDfa.getNextStateId();
        minimalDfa.addState(this);

        this.transitions = new TreeMap<Symbol<T>, MinimalDfaState<T>>();

        this.isStable = false;
    }

    /**
     * Returns the <code>MinimalDfa<code> of this <code>MinimalDfaState<code>.
     *
     * @return the <code>MinimalDfa<code>.
     */
    public MinimalDfa<T> getMinimalDfa() {

        return this.minimalDfa;
    }

    /**
     * Returns the identification number of this <code>MinimalDfaState<code>.
     *
     * @return the identification number.
     */
    public int getId() {

        return this.id;
    }

    /**
     * Returns a set of the transitions of this <code>MinimalDfaState<code>.
     *
     * @return the <code>SortedMap</code> of the transitions.
     * @throws InternalException
     *             if this <code>MinimalDfaState<code> is not stable.
     */
    public SortedMap<Symbol<T>, MinimalDfaState<T>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        return this.transitions;
    }

    /**
     * Returns a <code>MinimalDfaState<code> representing
     * the target of the provided symbol.
     *
     * @param symbol the symbol.
     * @return the target <code>MinimalDfaState<code>.
     * @throws InternalException
     *             if this instance is stable,
     *             if the provided symbol is <code>null</code> or
     *             if this instance's <code>MinimalDfa</code> contains the symbol.
     */
    public MinimalDfaState<T> getTarget(
            Symbol<T> symbol) {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        if (!this.minimalDfa.getAlphabet().getSymbols().contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        MinimalDfaState<T> target = this.transitions.get(symbol);

        if (target == null) {
            target = this.minimalDfa.getDeadEndState();
        }

        return target;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they have equal IDs
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this
     *         <code>MinimalDfaState<code> and the object are equal;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(
            Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof MinimalDfaState)) {
            return false;
        }

        MinimalDfaState nfaState = (MinimalDfaState) obj;

        return this.id == nfaState.id;
    }

    /**
     * Returns the hash code of this <code>MinimalDfaState<code>.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {

        return this.id;
    }

    /**
     * Returns the string representation of this <code>MinimalDfaState<code>.
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
     * Compares this <code>MinimalDfaState</code> to the provided one. It
     * compares the identification number.
     * 
     * @param minimalDfaState
     *            the <code>minimalDfaState</code> to compare with.
     * @return an <code>int</code> value: 0 if the two instances are equals, a
     *         negative value if this <code>MinimalDfaState<code> is
     *         smaller, and a positive value if it is bigger.
     * @throws InternalException
     *             if the provided <code>MinimalDfaState<code> is not in
     *             the same <code>MinimalDfa</code> as this one.
     */
    public int compareTo(
            MinimalDfaState<T> minimalDfaState) {

        if (this.minimalDfa != minimalDfaState.minimalDfa) {
            throw new InternalException(
                    "cannot compare states from distinct MinimalDFAs");
        }

        return this.id - minimalDfaState.id;
    }

    /**
     * Adds a transition to this <code>MinimalDfaState<code>
     * with the provided symbol and <code>MinimalDfaState<code>.
     *
     * @param symbol the symbol.
     * @param minimalDfaState the <code>MinimalDfaState<code>.
     * @throws InternalException
     *             if this <code>MinimalDfaState<code> is stable,
     *             if the provided symbol or <code>MinimalDfaState<code>
     *             is <code>null</code>, if this instance's <code>MinimalDfa</code>
     *             already contains the provided symbol or
     *             if it is not equal to the provided <code>MinimalDfaState</code>'s
     *             <code>MinimalDfa</code> or if the target is already set.
     */
    void addTransition(
            Symbol<T> symbol,
            MinimalDfaState<T> minimalDfaState) {

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
     *
     * @throws InternalException
     *             if this <code>MinimalDfaState<code> is already stable.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("state is already stable");
        }

        this.transitions = Collections.unmodifiableSortedMap(this.transitions);

        this.isStable = true;
    }
}
