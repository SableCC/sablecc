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
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.util.WorkSet;

public class Dfa<T extends Comparable<? super T>> {

    private Alphabet<T> alphabet;

    private Set<NfaState<T>> states;

    private SortedSet<NfaTransition<T>> transitions;

    private NfaState<T> startState;

    private Set<NfaState<T>> acceptStates;

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
        for (NfaState<T> dfaState : this.states) {
            if (stateMapper.match(dfaState, nfaAccept)) {
                this.acceptStates.add(dfaState);
            }
        }

        stabilize();
    }

    private void init(
            Nfa<T> nfa) {

        this.alphabet = nfa.getAlphabet();
        this.states = new HashSet<NfaState<T>>();
        this.transitions = new TreeSet<NfaTransition<T>>();
        this.acceptStates = new HashSet<NfaState<T>>();
        this.isStable = false;
    }

    private StateMatcher<T> computeDfaStates(
            Nfa<T> nfa) {

        StateMatcher<T> matcher = new StateMatcher<T>(this, nfa);
        EpsilonReach<T> reach = new EpsilonReach<T>(nfa);
        WorkSet<NfaState<T>> workSet = new WorkSet<NfaState<T>>();

        this.startState = matcher.getDfaState(reach.getEpsilonReach(nfa
                .getStartState()));
        workSet.add(this.startState);

        while (workSet.hasNext()) {
            NfaState<T> sourceDfaState = workSet.next();

            // find direct destinations
            SortedMap<Symbol<T>, LinkedHashSet<NfaState<T>>> directDestinationMap = new TreeMap<Symbol<T>, LinkedHashSet<NfaState<T>>>();

            for (NfaState<T> sourceNfaState : matcher
                    .getNfaStates(sourceDfaState)) {
                for (NfaTransition<T> transition : sourceNfaState
                        .getForwardTransitions()) {
                    if (transition.getSymbol() != null) {
                        LinkedHashSet<NfaState<T>> directDesinations = directDestinationMap
                                .get(transition.getSymbol());

                        if (directDesinations == null) {
                            directDesinations = new LinkedHashSet<NfaState<T>>();
                            directDestinationMap.put(transition.getSymbol(),
                                    directDesinations);
                        }

                        directDesinations.add(transition.getDestination());
                    }
                }
            }

            // add transitions
            for (Map.Entry<Symbol<T>, LinkedHashSet<NfaState<T>>> entry : directDestinationMap
                    .entrySet()) {
                Symbol<T> symbol = entry.getKey();
                LinkedHashSet<NfaState<T>> directDestinations = entry
                        .getValue();

                Set<NfaState<T>> epsilonClosure = new HashSet<NfaState<T>>();

                for (NfaState<T> nfaState : directDestinations) {
                    epsilonClosure.addAll(reach.getEpsilonReach(nfaState));
                }

                NfaState<T> destinationDfaState = matcher
                        .getDfaState(epsilonClosure);

                this.transitions.add(new NfaTransition<T>(sourceDfaState,
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

    public Set<NfaState<T>> getStates() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.states;
    }

    Set<NfaState<T>> getUnstableStates() {

        return this.states;
    }

    public Set<NfaTransition<T>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.transitions;
    }

    public NfaState<T> getStartState() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.startState;
    }

    public Set<NfaState<T>> getAcceptStates() {

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
                for (NfaState<T> state : this.acceptStates) {
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

            for (NfaTransition<T> transition : this.transitions) {
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

        for (NfaState<T> state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSet(this.states);
        this.transitions = Collections.unmodifiableSortedSet(this.transitions);
        this.acceptStates = Collections.unmodifiableSet(this.acceptStates);

        // sanity check
        Set<String> stateNames = new HashSet<String>();

        for (NfaState<T> state : this.states) {
            if (!stateNames.add(state.getName())) {
                throw new InternalException(
                        "two states of the same DFA may not have the same name");
            }
        }

        for (NfaTransition<T> transition : this.transitions) {
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

        for (NfaState<T> state : this.acceptStates) {
            if (!this.states.contains(state)) {
                throw new InternalException("invalid accept state");
            }
        }

        this.isStable = true;
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
        for (NfaState<T> dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfaAccept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        // remove transitions out of accept states
        LinkedHashSet<NfaTransition<T>> transitionsToDelete = new LinkedHashSet<NfaTransition<T>>();

        for (NfaTransition<T> transition : dfa.transitions) {
            if (dfa.acceptStates.contains(transition.getSource())) {
                transitionsToDelete.add(transition);
            }
        }

        dfa.transitions.removeAll(transitionsToDelete);

        for (NfaTransition<T> transition : transitionsToDelete) {
            transition.delete();
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
        newNfa.getUnstableTransitions().add(
                new NfaTransition<T>(newNfa.getUnstableStartState(),
                        nfaCombineResult.getStartStateMapping1(nfa1), null));
        newNfa.getUnstableTransitions().add(
                new NfaTransition<T>(newNfa.getUnstableStartState(),
                        nfaCombineResult.getStartStateMapping2(nfa2), null));

        newNfa.stabilize();

        Dfa<T> dfa = new Dfa<T>();

        dfa.init(newNfa);
        StateMatcher<T> stateMapper = dfa.computeDfaStates(newNfa);

        // compute accept states
        NfaState<T> nfa1Accept = nfaCombineResult.getAcceptStateMapping1(nfa1);
        NfaState<T> nfa2Accept = nfaCombineResult.getAcceptStateMapping2(nfa2);
        for (NfaState<T> dfaState : dfa.states) {
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
        newNfa.getUnstableTransitions().add(
                new NfaTransition<T>(newNfa.getUnstableStartState(),
                        nfaCombineResult.getStartStateMapping1(nfa1), null));
        newNfa.getUnstableTransitions().add(
                new NfaTransition<T>(newNfa.getUnstableStartState(),
                        nfaCombineResult.getStartStateMapping2(nfa2), null));

        newNfa.stabilize();

        Dfa<T> dfa = new Dfa<T>();

        dfa.init(newNfa);
        StateMatcher<T> stateMapper = dfa.computeDfaStates(newNfa);

        // compute accept states
        NfaState<T> nfa1Accept = nfaCombineResult.getAcceptStateMapping1(nfa1);
        NfaState<T> nfa2Accept = nfaCombineResult.getAcceptStateMapping2(nfa2);
        for (NfaState<T> dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfa1Accept)
                    && stateMapper.match(dfaState, nfa2Accept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        dfa.stabilize();
        return dfa;
    }
}
