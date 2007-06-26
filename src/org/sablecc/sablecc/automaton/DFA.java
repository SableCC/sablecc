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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sablecc.sablecc.alphabet.Alphabet;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.automaton.graph.State;
import org.sablecc.sablecc.automaton.graph.Transition;
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.util.WorkSet;

public class DFA<T extends Comparable<? super T>> {

    private Alphabet<T> alphabet;

    private Set<State<T>> states;

    private SortedSet<Transition<T>> transitions;

    private State<T> startState;

    private Set<State<T>> acceptStates;

    private boolean isStable;

    private String toString;

    private DFA() {

    }

    public DFA(
            NFA<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        init(nfa);
        StateMatcher<T> stateMapper = computeDFAStates(nfa);

        // compute accept states
        State<T> nfaAccept = nfa.getAcceptState();
        for (State<T> dfaState : this.states) {
            if (stateMapper.match(dfaState, nfaAccept)) {
                this.acceptStates.add(dfaState);
            }
        }

        stabilize();
    }

    private void init(
            NFA<T> nfa) {

        this.alphabet = nfa.getAlphabet();
        this.states = new HashSet<State<T>>();
        this.transitions = new TreeSet<Transition<T>>();
        this.acceptStates = new HashSet<State<T>>();
        this.isStable = false;
    }

    private StateMatcher<T> computeDFAStates(
            NFA<T> nfa) {

        StateMatcher<T> matcher = new StateMatcher<T>(this, nfa);
        EpsilonReach<T> reach = new EpsilonReach<T>(nfa);
        WorkSet<State<T>> workSet = new WorkSet<State<T>>();

        this.startState = matcher.getDFAState(reach.getEpsilonReach(nfa
                .getStartState()));
        workSet.add(this.startState);

        while (workSet.hasNext()) {
            State<T> sourceDfaState = workSet.next();

            // find direct destinations
            SortedMap<Symbol<T>, LinkedHashSet<State<T>>> directDestinationMap = new TreeMap<Symbol<T>, LinkedHashSet<State<T>>>();

            for (State<T> sourceNfaState : matcher.getNFAStates(sourceDfaState)) {
                for (Transition<T> transition : sourceNfaState
                        .getForwardTransitions()) {
                    if (transition.getSymbol() != null) {
                        LinkedHashSet<State<T>> directDesinations = directDestinationMap
                                .get(transition.getSymbol());

                        if (directDesinations == null) {
                            directDesinations = new LinkedHashSet<State<T>>();
                            directDestinationMap.put(transition.getSymbol(),
                                    directDesinations);
                        }

                        directDesinations.add(transition.getDestination());
                    }
                }
            }

            // add transitions
            for (Map.Entry<Symbol<T>, LinkedHashSet<State<T>>> entry : directDestinationMap
                    .entrySet()) {
                Symbol<T> symbol = entry.getKey();
                LinkedHashSet<State<T>> directDestinations = entry.getValue();

                Set<State<T>> epsilonClosure = new HashSet<State<T>>();

                for (State<T> nfaState : directDestinations) {
                    epsilonClosure.addAll(reach.getEpsilonReach(nfaState));
                }

                State<T> destinationDfaState = matcher
                        .getDFAState(epsilonClosure);

                this.transitions.add(new Transition<T>(sourceDfaState,
                        destinationDfaState, symbol));

                workSet.add(destinationDfaState);
            }
        }

        return matcher;
    }

    public Alphabet<T> getAlphabet() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.alphabet;
    }

    public Set<State<T>> getStates() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.states;
    }

    Set<State<T>> getUnstableStates() {

        return this.states;
    }

    public Set<Transition<T>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.transitions;
    }

    public State<T> getStartState() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.startState;
    }

    public Set<State<T>> getAcceptStates() {

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

            sb.append("DFA:{ start = ");

            sb.append(this.startState);

            sb.append("; accept = ");

            {
                boolean first = true;
                for (State<T> state : this.acceptStates) {
                    if (first) {
                        first = false;
                    }
                    else {
                        sb.append(",");
                    }
                    sb.append(state);
                }
            }

            sb.append("; transitions : ");

            for (Transition<T> transition : this.transitions) {
                sb.append(transition);
                sb.append(" ");
            }

            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    private void stabilize() {

        if (this.isStable) {
            throw new InternalException("this DFA is already stable");
        }

        for (State<T> state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSet(this.states);
        this.transitions = Collections.unmodifiableSortedSet(this.transitions);
        this.acceptStates = Collections.unmodifiableSet(this.acceptStates);

        // sanity check
        Set<String> stateNames = new HashSet<String>();

        for (State<T> state : this.states) {
            if (!stateNames.add(state.getName())) {
                throw new InternalException(
                        "two states of the same DFA may not have the same name");
            }
        }

        for (Transition<T> transition : this.transitions) {
            if (!this.states.contains(transition.getSource())) {
                throw new InternalException(
                        "invalid source state in transition");
            }

            if (!this.states.contains(transition.getSource())) {
                throw new InternalException(
                        "invalid source state in transition");
            }

            if (transition.getSymbol() != null) {
                if (!this.alphabet.getSymbols()
                        .contains(transition.getSymbol())) {
                    throw new InternalException("invalid symbol in transition");
                }
            }
        }

        for (State<T> state : this.acceptStates) {
            if (!this.states.contains(state)) {
                throw new InternalException("invalid accept state");
            }
        }

        this.isStable = true;
    }

    static <T extends Comparable<? super T>> DFA<T> shortest(
            NFA<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        DFA<T> dfa = new DFA<T>();

        dfa.init(nfa);
        StateMatcher<T> stateMapper = dfa.computeDFAStates(nfa);

        // compute accept states
        State<T> nfaAccept = nfa.getAcceptState();
        for (State<T> dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfaAccept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        // remove transitions out of accept states
        LinkedHashSet<Transition<T>> transitionsToDelete = new LinkedHashSet<Transition<T>>();

        for (Transition<T> transition : dfa.transitions) {
            if (dfa.acceptStates.contains(transition.getSource())) {
                transitionsToDelete.add(transition);
            }
        }

        dfa.transitions.removeAll(transitionsToDelete);

        for (Transition<T> transition : transitionsToDelete) {
            transition.delete();
        }

        dfa.stabilize();
        return dfa;
    }

    static <T extends Comparable<? super T>> DFA<T> difference(
            NFA<T> nfa1,
            NFA<T> nfa2) {

        if (nfa1 == null) {
            throw new InternalException("nfa1 may not be null");
        }

        if (nfa2 == null) {
            throw new InternalException("nfa2 may not be null");
        }

        NFACombineResult<T> nfaCombineResult = nfa1.combineWith(nfa2);
        NFA<T> newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transitions from start to oldStart
        newNfa.getUnstableTransitions().add(
                new Transition<T>(newNfa.getUnstableStartState(),
                        nfaCombineResult.getStartStateMapping1(nfa1), null));
        newNfa.getUnstableTransitions().add(
                new Transition<T>(newNfa.getUnstableStartState(),
                        nfaCombineResult.getStartStateMapping2(nfa2), null));

        newNfa.stabilize();

        DFA<T> dfa = new DFA<T>();

        dfa.init(newNfa);
        StateMatcher<T> stateMapper = dfa.computeDFAStates(newNfa);

        // compute accept states
        State<T> nfa1Accept = nfaCombineResult.getAcceptStateMapping1(nfa1);
        State<T> nfa2Accept = nfaCombineResult.getAcceptStateMapping2(nfa2);
        for (State<T> dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfa1Accept)
                    && !stateMapper.match(dfaState, nfa2Accept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        dfa.stabilize();
        return dfa;
    }

    static <T extends Comparable<? super T>> DFA<T> intersection(
            NFA<T> nfa1,
            NFA<T> nfa2) {

        if (nfa1 == null) {
            throw new InternalException("nfa1 may not be null");
        }

        if (nfa2 == null) {
            throw new InternalException("nfa2 may not be null");
        }

        NFACombineResult<T> nfaCombineResult = nfa1.combineWith(nfa2);
        NFA<T> newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transitions from start to oldStart
        newNfa.getUnstableTransitions().add(
                new Transition<T>(newNfa.getUnstableStartState(),
                        nfaCombineResult.getStartStateMapping1(nfa1), null));
        newNfa.getUnstableTransitions().add(
                new Transition<T>(newNfa.getUnstableStartState(),
                        nfaCombineResult.getStartStateMapping2(nfa2), null));

        newNfa.stabilize();

        DFA<T> dfa = new DFA<T>();

        dfa.init(newNfa);
        StateMatcher<T> stateMapper = dfa.computeDFAStates(newNfa);

        // compute accept states
        State<T> nfa1Accept = nfaCombineResult.getAcceptStateMapping1(nfa1);
        State<T> nfa2Accept = nfaCombineResult.getAcceptStateMapping2(nfa2);
        for (State<T> dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfa1Accept)
                    && stateMapper.match(dfaState, nfa2Accept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        dfa.stabilize();
        return dfa;
    }
}
