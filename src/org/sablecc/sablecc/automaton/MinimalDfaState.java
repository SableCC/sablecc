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

public final class MinimalDfaState<T extends Comparable<? super T>>
        implements Comparable<MinimalDfaState<T>> {

    private final MinimalDfa<T> minimalDfa;

    private final int id;

    private SortedMap<Symbol<T>, MinimalDfaState<T>> transitions;

    private boolean isStable;

    private String toString;

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

    public MinimalDfa<T> getMinimalDfa() {

        return this.minimalDfa;
    }

    public int getId() {

        return this.id;
    }

    public SortedMap<Symbol<T>, MinimalDfaState<T>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        return this.transitions;
    }

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

    @Override
    public int hashCode() {

        return this.id;
    }

    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "dfaState" + this.id;
        }

        return this.toString;
    }

    public int compareTo(
            MinimalDfaState<T> minimalDfaState) {

        if (this.minimalDfa != minimalDfaState.minimalDfa) {
            throw new InternalException(
                    "cannot compare states from distinct MinimalDFAs");
        }

        return this.id - minimalDfaState.id;
    }

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

    void stabilize() {

        if (this.isStable) {
            throw new InternalException("state is already stable");
        }

        this.transitions = Collections.unmodifiableSortedMap(this.transitions);

        this.isStable = true;
    }
}
