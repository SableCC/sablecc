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

import org.sablecc.sablecc.alphabet.Alphabet;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.util.WorkSet;

public class Dfa<T extends Comparable<? super T>> {

    private static final String lineSeparator = System
            .getProperty("line.separator");

    private Alphabet<T> alphabet;

    private SortedSet<DfaState<T>> states;

    private DfaState<T> startState;

    private DfaState<T> deadEndState;

    private SortedSet<DfaState<T>> acceptStates;

    private boolean isStable;

    private String toString;

    private Dfa() {

    }

    public Dfa(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        init(nfa);
        StateMatcher<T> stateMapper = computeDfaStates(nfa);

        // compute accept states
        NfaState<T> nfaAccept = nfa.getAcceptState();
        for (DfaState<T> dfaState : this.states) {
            if (stateMapper.match(dfaState, nfaAccept)) {
                this.acceptStates.add(dfaState);
            }
        }

        stabilize();
    }

    private void init(
            Nfa<T> nfa) {

        this.alphabet = nfa.getAlphabet();
        this.states = new TreeSet<DfaState<T>>();
        this.acceptStates = new TreeSet<DfaState<T>>();
        this.isStable = false;
    }

    private StateMatcher<T> computeDfaStates(
            Nfa<T> nfa) {

        StateMatcher<T> matcher = new StateMatcher<T>(this, nfa);
        WorkSet<DfaState<T>> workSet = new WorkSet<DfaState<T>>();

        this.deadEndState = matcher.getDfaState(new TreeSet<NfaState<T>>());
        this.startState = matcher.getDfaState(nfa.getStartState()
                .getEpsilonReach());
        workSet.add(this.startState);

        while (workSet.hasNext()) {
            DfaState<T> sourceDfaState = workSet.next();

            // find direct destinations
            SortedMap<Symbol<T>, SortedSet<NfaState<T>>> directDestinationMap = new TreeMap<Symbol<T>, SortedSet<NfaState<T>>>();

            for (NfaState<T> sourceNfaState : matcher
                    .getNfaStates(sourceDfaState)) {

                for (Map.Entry<Symbol<T>, SortedSet<NfaState<T>>> entry : sourceNfaState
                        .getTransitions().entrySet()) {

                    Symbol<T> symbol = entry.getKey();
                    SortedSet<NfaState<T>> targets = entry.getValue();

                    if (symbol != null) {

                        SortedSet<NfaState<T>> directDesinations = directDestinationMap
                                .get(symbol);

                        if (directDesinations == null) {

                            directDesinations = new TreeSet<NfaState<T>>();
                            directDestinationMap.put(symbol, directDesinations);
                        }

                        directDesinations.addAll(targets);
                    }
                }
            }

            // add transitions
            for (Map.Entry<Symbol<T>, SortedSet<NfaState<T>>> entry : directDestinationMap
                    .entrySet()) {

                Symbol<T> symbol = entry.getKey();
                SortedSet<NfaState<T>> directDestinations = entry.getValue();

                SortedSet<NfaState<T>> epsilonClosure = new TreeSet<NfaState<T>>();

                for (NfaState<T> nfaState : directDestinations) {
                    epsilonClosure.addAll(nfaState.getEpsilonReach());
                }

                DfaState<T> destinationDfaState = matcher
                        .getDfaState(epsilonClosure);

                sourceDfaState.addTransition(symbol, destinationDfaState);

                workSet.add(destinationDfaState);
            }
        }

        return matcher;
    }

    public Alphabet<T> getAlphabet() {

        return this.alphabet;
    }

    public SortedSet<DfaState<T>> getStates() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.states;
    }

    SortedSet<DfaState<T>> getUnstableStates() {

        return this.states;
    }

    public DfaState<T> getStartState() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.startState;
    }

    public DfaState<T> getDeadEndState() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.deadEndState;
    }

    DfaState<T> getUnstableDeadEndState() {

        return this.deadEndState;
    }

    public SortedSet<DfaState<T>> getAcceptStates() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.acceptStates;
    }

    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this DFA is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("DFA:{");

            for (DfaState<T> state : this.states) {
                sb.append(lineSeparator);
                sb.append("    ");
                sb.append(state);

                if (state == this.startState) {
                    sb.append("(start)");
                }

                if (state == this.deadEndState) {
                    sb.append("(dead-end)");
                }

                if (this.acceptStates.contains(state)) {
                    sb.append("(accept)");
                }

                sb.append(":");
                for (Map.Entry<Symbol<T>, DfaState<T>> entry : state
                        .getTransitions().entrySet()) {
                    Symbol<T> symbol = entry.getKey();
                    DfaState<T> target = entry.getValue();

                    sb.append(lineSeparator);
                    sb.append("        ");
                    sb.append(symbol);
                    sb.append(" -> ");
                    sb.append(target);
                }
            }

            sb.append(lineSeparator);
            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    private void stabilize() {

        if (this.isStable) {
            throw new InternalException("this DFA is already stable");
        }

        removeUnreachableStates();

        for (DfaState<T> state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSortedSet(this.states);
        this.acceptStates = Collections
                .unmodifiableSortedSet(this.acceptStates);

        this.isStable = true;
    }

    private void removeUnreachableStates() {

        SortedSet<DfaState<T>> reachableStates = new TreeSet<DfaState<T>>();

        WorkSet<DfaState<T>> workSet = new WorkSet<DfaState<T>>();
        workSet.add(this.startState);

        while (workSet.hasNext()) {
            DfaState<T> state = workSet.next();
            reachableStates.add(state);

            for (DfaState<T> target : state.getUnstableTransitions().values()) {
                workSet.add(target);
            }
        }

        reachableStates.add(this.deadEndState);

        this.states = reachableStates;
    }

    static <T extends Comparable<? super T>> Dfa<T> shortest(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        Dfa<T> dfa = new Dfa<T>();

        dfa.init(nfa);
        StateMatcher<T> stateMapper = dfa.computeDfaStates(nfa);

        // compute accept states
        NfaState<T> nfaAccept = nfa.getAcceptState();
        for (DfaState<T> dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfaAccept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        // remove transitions out of accept states
        for (DfaState<T> state : dfa.acceptStates) {
            state.removeTransitions();
        }

        dfa.stabilize();
        return dfa;
    }

    static <T extends Comparable<? super T>> Dfa<T> difference(
            Nfa<T> nfa1,
            Nfa<T> nfa2) {

        if (nfa1 == null) {
            throw new InternalException("nfa1 may not be null");
        }

        if (nfa2 == null) {
            throw new InternalException("nfa2 may not be null");
        }

        NfaCombineResult<T> nfaCombineResult = nfa1.combineWith(nfa2);
        Nfa<T> newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transitions from start to oldStart
        newNfa.getUnstableStartState().addTransition(null,
                nfaCombineResult.getNewNfa1StartState());
        newNfa.getUnstableStartState().addTransition(null,
                nfaCombineResult.getNewNfa2StartState());

        newNfa.stabilize();

        Dfa<T> dfa = new Dfa<T>();

        dfa.init(newNfa);
        StateMatcher<T> stateMapper = dfa.computeDfaStates(newNfa);

        // compute accept states
        NfaState<T> nfa1Accept = nfaCombineResult.getNewNfa1AcceptState();
        NfaState<T> nfa2Accept = nfaCombineResult.getNewNfa2AcceptState();
        for (DfaState<T> dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfa1Accept)
                    && !stateMapper.match(dfaState, nfa2Accept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        dfa.stabilize();
        return dfa;
    }

    static <T extends Comparable<? super T>> Dfa<T> intersection(
            Nfa<T> nfa1,
            Nfa<T> nfa2) {

        if (nfa1 == null) {
            throw new InternalException("nfa1 may not be null");
        }

        if (nfa2 == null) {
            throw new InternalException("nfa2 may not be null");
        }

        NfaCombineResult<T> nfaCombineResult = nfa1.combineWith(nfa2);
        Nfa<T> newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transitions from start to oldStart
        newNfa.getUnstableStartState().addTransition(null,
                nfaCombineResult.getNewNfa1StartState());
        newNfa.getUnstableStartState().addTransition(null,
                nfaCombineResult.getNewNfa2StartState());

        newNfa.stabilize();

        Dfa<T> dfa = new Dfa<T>();

        dfa.init(newNfa);
        StateMatcher<T> stateMapper = dfa.computeDfaStates(newNfa);

        // compute accept states
        NfaState<T> nfa1Accept = nfaCombineResult.getNewNfa1AcceptState();
        NfaState<T> nfa2Accept = nfaCombineResult.getNewNfa2AcceptState();
        for (DfaState<T> dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfa1Accept)
                    && stateMapper.match(dfaState, nfa2Accept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        dfa.stabilize();
        return dfa;
    }

    int getNextStateId() {

        if (this.isStable) {
            throw new InternalException("a stable DFA may not be modified");
        }

        return this.states.size();
    }

    void addState(
            DfaState<T> state) {

        if (this.isStable) {
            throw new InternalException("a stable DFA may not be modified");
        }

        if (!this.states.add(state)) {
            throw new InternalException("state is already in state set");
        }
    }
}
