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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.sablecc.alphabet.Alphabet;
import org.sablecc.sablecc.alphabet.AlphabetMergeResult;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.automaton.graph.State;
import org.sablecc.sablecc.automaton.graph.Transition;
import org.sablecc.sablecc.exception.InternalException;

public final class NFA<T extends Comparable<? super T>> {

    private Alphabet<T> alphabet;

    private Set<State<T>> states;

    private Set<Transition<T>> transitions;

    private State<T> startState;

    private State<T> acceptState;

    private boolean isStable;

    private String toString;

    /**
     * Constructs a NFA for the empty language.
     */
    public NFA() {

        init();

        // empty alphabet and no transition
        this.alphabet = new Alphabet<T>();

        stabilize();
    }

    /**
     * Constructs a NFA for the language <code>{"s"}</code> where
     * <code>s</code> is a symbol representing a single interval.
     */
    public NFA(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        init();

        // Add a transition: start->(symbol)->accept
        Symbol<T> symbol = new Symbol<T>(interval);
        this.alphabet = new Alphabet<T>(symbol);
        this.transitions.add(new Transition<T>(this.startState,
                this.acceptState, symbol));

        stabilize();
    }

    /**
     * Constructs an incomplete NFA. This private constructor returns a NFA to
     * which new states and transitions can be added. The
     * <code>stabilize()</code> method should be called on this instance
     * before exposing it publicly.
     */
    private NFA(
            Alphabet<T> alphabet) {

        init();
        this.alphabet = alphabet;
    }

    private void init() {

        this.states = new LinkedHashSet<State<T>>();
        this.transitions = new LinkedHashSet<Transition<T>>();

        this.startState = new State<T>("start");
        this.states.add(this.startState);

        this.acceptState = new State<T>("accept");
        this.states.add(this.acceptState);

        this.isStable = false;
    }

    public Alphabet<T> getAlphabet() {

        if (!this.isStable) {
            throw new InternalException("the NFA is not stable yet");
        }

        return this.alphabet;
    }

    public Set<State<T>> getStates() {

        if (!this.isStable) {
            throw new InternalException("the NFA is not stable yet");
        }

        return this.states;
    }

    public Set<Transition<T>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("the NFA is not stable yet");
        }

        return this.transitions;
    }

    public State<T> getStartState() {

        if (!this.isStable) {
            throw new InternalException("the NFA is not stable yet");
        }

        return this.startState;
    }

    public State<T> getAcceptState() {

        if (!this.isStable) {
            throw new InternalException("the NFA is not stable yet");
        }

        return this.acceptState;
    }

    @Override
    public String toString() {

        if (this.toString == null) {
            if (!this.isStable) {
                throw new InternalException("the NFA is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("NFA:{ ");

            for (Transition<T> transition : this.transitions) {
                sb.append(transition);
                sb.append(" ");
            }

            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    public void stabilize() {

        if (this.isStable) {
            throw new InternalException("this NFA is already stable");
        }

        for (State<T> state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSet(this.states);
        this.transitions = Collections.unmodifiableSet(this.transitions);

        // sanity check
        Set<String> stateNames = new HashSet<String>();

        for (State<T> state : this.states) {
            if (!stateNames.add(state.getName())) {
                throw new InternalException(
                        "two states of the same NFA may not have the same name");
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

        this.isStable = true;
    }

    public NFA<T> unionWith(
            NFA<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this instance is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        NFACombineResult<T> nfaCombineResult = this.combineWith(nfa);
        NFA<T> newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transitions from start to oldStart
        newNfa.transitions.add(new Transition<T>(newNfa.startState,
                nfaCombineResult.getStartStateMapping(this), null));
        newNfa.transitions.add(new Transition<T>(newNfa.startState,
                nfaCombineResult.getStartStateMapping(nfa), null));

        // add epsilon transitions from oldAccept to accept
        newNfa.transitions.add(new Transition<T>(nfaCombineResult
                .getAcceptStateMapping(this), newNfa.acceptState, null));
        newNfa.transitions.add(new Transition<T>(nfaCombineResult
                .getAcceptStateMapping(nfa), newNfa.acceptState, null));

        newNfa.stabilize();
        return newNfa;
    }

    public NFA<T> concatenateWith(
            NFA<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this instance is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        NFACombineResult<T> nfaCombineResult = this.combineWith(nfa);
        NFA<T> newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transition from start to start(this)
        newNfa.transitions.add(new Transition<T>(newNfa.startState,
                nfaCombineResult.getStartStateMapping(this), null));

        // add epsilon transition from this accept(this) to start(nfa)
        newNfa.transitions.add(new Transition<T>(nfaCombineResult
                .getAcceptStateMapping(this), nfaCombineResult
                .getStartStateMapping(nfa), null));

        // add epsilon transition from accept(nfa) to accept
        newNfa.transitions.add(new Transition<T>(nfaCombineResult
                .getAcceptStateMapping(nfa), newNfa.acceptState, null));

        newNfa.stabilize();
        return newNfa;
    }

    public NFA<T> zeroOrMore() {

        NFA<T> newNfa = new NFA<T>(this.alphabet);

        // add old states and transitions to new NFA
        Map<State<T>, State<T>> oldNfaStateMap = new HashMap<State<T>, State<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        State<T> startThis = oldNfaStateMap.get(this.startState);
        State<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to accept
        newNfa.transitions.add(new Transition<T>(newNfa.startState,
                newNfa.acceptState, null));

        // add epsilon transition from start to start(this)
        newNfa.transitions.add(new Transition<T>(newNfa.startState, startThis,
                null));

        // add epsilon transition from accept(this) to accept
        newNfa.transitions.add(new Transition<T>(acceptThis,
                newNfa.acceptState, null));

        // add epsilon transition from accept(this) to start(this)
        newNfa.transitions.add(new Transition<T>(acceptThis, startThis, null));

        newNfa.stabilize();
        return newNfa;
    }

    public NFA<T> zeroOrOne() {

        NFA<T> newNfa = new NFA<T>(this.alphabet);

        // add old states and transitions to new NFA
        Map<State<T>, State<T>> oldNfaStateMap = new HashMap<State<T>, State<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        State<T> startThis = oldNfaStateMap.get(this.startState);
        State<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to accept
        newNfa.transitions.add(new Transition<T>(newNfa.startState,
                newNfa.acceptState, null));

        // add epsilon transition from start to start(this)
        newNfa.transitions.add(new Transition<T>(newNfa.startState, startThis,
                null));

        // add epsilon transition from accept(this) to accept
        newNfa.transitions.add(new Transition<T>(acceptThis,
                newNfa.acceptState, null));

        newNfa.stabilize();
        return newNfa;
    }

    public NFA<T> oneOrMore() {

        NFA<T> newNfa = new NFA<T>(this.alphabet);

        // add old states and transitions to new NFA
        Map<State<T>, State<T>> oldNfaStateMap = new HashMap<State<T>, State<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        State<T> startThis = oldNfaStateMap.get(this.startState);
        State<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to start(this)
        newNfa.transitions.add(new Transition<T>(newNfa.startState, startThis,
                null));

        // add epsilon transition from accept(this) to accept
        newNfa.transitions.add(new Transition<T>(acceptThis,
                newNfa.acceptState, null));

        // add epsilon transition from accept(this) to start(this)
        newNfa.transitions.add(new Transition<T>(acceptThis, startThis, null));

        newNfa.stabilize();
        return newNfa;
    }

    public NFACombineResult<T> combineWith(
            NFA<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this instance is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        // Create a new NFA
        AlphabetMergeResult<T> alphabetMergeResult = this.alphabet
                .mergeWith(nfa.getAlphabet());
        NFA<T> newNfa = new NFA<T>(alphabetMergeResult.getNewAlphabet());

        // add old states and transitions to new NFA
        Map<State<T>, State<T>> oldNfa1StateMap = new HashMap<State<T>, State<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfa1StateMap,
                alphabetMergeResult);

        Map<State<T>, State<T>> oldNfa2StateMap = new HashMap<State<T>, State<T>>();
        nfa.addStatesAndTransitionsTo(newNfa, oldNfa2StateMap,
                alphabetMergeResult);

        return new NFACombineResult<T>(newNfa, this, oldNfa1StateMap, nfa,
                oldNfa2StateMap);
    }

    private void addStatesAndTransitionsTo(
            NFA<T> newNfa,
            Map<State<T>, State<T>> nfaStateMap,
            AlphabetMergeResult<T> alphabetMergeResult) {

        // add states
        for (State<T> oldState : this.states) {

            State<T> newState = new State<T>("state"
                    + (newNfa.states.size() - 1));

            newNfa.states.add(newState);
            nfaStateMap.put(oldState, newState);
        }

        // add transitions
        for (Transition<T> oldTransition : this.transitions) {

            State<T> newSource = nfaStateMap.get(oldTransition.getSource());
            State<T> newDestination = nfaStateMap.get(oldTransition
                    .getDestination());

            if (oldTransition.getSymbol() != null) {
                for (Symbol<T> newSymbol : alphabetMergeResult.getNewSymbols(
                        oldTransition.getSymbol(), this.alphabet)) {

                    newNfa.transitions.add(new Transition<T>(newSource,
                            newDestination, newSymbol));
                }
            }
            else {
                newNfa.transitions.add(new Transition<T>(newSource,
                        newDestination, null));
            }
        }
    }

    private void addStatesAndTransitionsTo(
            NFA<T> newNfa,
            Map<State<T>, State<T>> nfaStateMap) {

        if (!this.alphabet.equals(newNfa.alphabet)) {
            throw new InternalException("different alphabets are not allowed");
        }

        // add states
        for (State<T> oldState : this.states) {

            State<T> newState = new State<T>("state"
                    + (newNfa.states.size() - 1));

            newNfa.states.add(newState);
            nfaStateMap.put(oldState, newState);
        }

        // add transitions
        for (Transition<T> oldTransition : this.transitions) {

            State<T> newSource = nfaStateMap.get(oldTransition.getSource());
            State<T> newDestination = nfaStateMap.get(oldTransition
                    .getDestination());

            newNfa.transitions.add(new Transition<T>(newSource, newDestination,
                    oldTransition.getSymbol()));
        }
    }
}
