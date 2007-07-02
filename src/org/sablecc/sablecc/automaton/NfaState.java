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

public final class NfaState<T extends Comparable<? super T>>
        implements Comparable<NfaState<T>> {

    private final Nfa<T> nfa;

    private final int id;

    private SortedMap<Symbol<T>, SortedSet<NfaState<T>>> transitions;

    private boolean isStable;

    private String toString;

    private SortedSet<NfaState<T>> epsilonReach;

    private final SortedSet<NfaState<T>> emptyNfaStateSet = new TreeSet<NfaState<T>>();

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

    public Nfa<T> getNfa() {

        return this.nfa;
    }

    public int getId() {

        return this.id;
    }

    public SortedMap<Symbol<T>, SortedSet<NfaState<T>>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the state is not stable yet");
        }

        return this.transitions;
    }

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

    @Override
    public int hashCode() {

        return this.id;
    }

    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "nfaState" + this.id;
        }

        return this.toString;
    }

    public int compareTo(
            NfaState<T> nfaState) {

        if (this.nfa != nfaState.nfa) {
            throw new InternalException(
                    "cannot compare states from distinct NFAs");
        }

        return this.id - nfaState.id;
    }

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
