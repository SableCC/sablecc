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

public final class DfaState<T extends Comparable<? super T>>
        implements Comparable<DfaState<T>> {

    private final Dfa<T> dfa;

    private final int id;

    private SortedMap<Symbol<T>, DfaState<T>> transitions;

    private boolean isStable;

    private String toString;

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

    public Dfa<T> getDfa() {

        return this.dfa;
    }

    public int getId() {

        return this.id;
    }

    public SortedMap<Symbol<T>, DfaState<T>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        return this.transitions;
    }

    SortedMap<Symbol<T>, DfaState<T>> getUnstableTransitions() {

        return this.transitions;
    }

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

    @Override
    public boolean equals(
            Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof DfaState)) {
            return false;
        }

        DfaState nfaState = (DfaState) obj;

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
            DfaState<T> dfaState) {

        if (this.dfa != dfaState.dfa) {
            throw new InternalException(
                    "cannot compare states from distinct DFAs");
        }

        return this.id - dfaState.id;
    }

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

    void removeTransitions() {

        if (this.isStable) {
            throw new InternalException("a stable state may not be modified");
        }

        this.transitions.clear();
    }

    void stabilize() {

        if (this.isStable) {
            throw new InternalException("state is already stable");
        }

        this.transitions = Collections.unmodifiableSortedMap(this.transitions);

        this.isStable = true;
    }
}
