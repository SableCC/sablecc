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
import java.util.SortedSet;
import java.util.TreeSet;

import org.sablecc.sablecc.alphabet.Alphabet;
import org.sablecc.sablecc.alphabet.AlphabetMergeResult;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

public final class Nfa<T extends Comparable<? super T>> {

    private Alphabet<T> alphabet;

    private Set<NfaState<T>> states;

    private SortedSet<NfaTransition<T>> transitions;

    private NfaState<T> startState;

    private NfaState<T> acceptState;

    private boolean isStable;

    private String toString;

    /**
     * Constructs a NFA for the empty language.
     */
    public Nfa() {

        init();

        // empty alphabet and no transition
        this.alphabet = new Alphabet<T>();

        stabilize();
    }

    /**
     * Constructs a NFA for the language <code>{"s"}</code> where
     * <code>s</code> is a symbol representing a single interval.
     */
    public Nfa(
            Interval<T> interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        init();

        // Add a transition: start->(symbol)->accept
        Symbol<T> symbol = new Symbol<T>(interval);
        this.alphabet = new Alphabet<T>(symbol);
        this.transitions.add(new NfaTransition<T>(this.startState,
                this.acceptState, symbol));

        stabilize();
    }

    /**
     * Constructs an incomplete NFA. This private constructor returns a NFA to
     * which new states and transitions can be added. The
     * <code>stabilize()</code> method should be called on this instance
     * before exposing it publicly.
     */
    private Nfa(
            Alphabet<T> alphabet) {

        init();
        this.alphabet = alphabet;
    }

    private void init() {

        this.states = new LinkedHashSet<NfaState<T>>();
        this.transitions = new TreeSet<NfaTransition<T>>();

        this.startState = new NfaState<T>("start");
        this.states.add(this.startState);

        this.acceptState = new NfaState<T>("accept");
        this.states.add(this.acceptState);

        this.isStable = false;
    }

    public Alphabet<T> getAlphabet() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        return this.alphabet;
    }

    public Set<NfaState<T>> getStates() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        return this.states;
    }

    public Set<NfaTransition<T>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        return this.transitions;
    }

    Set<NfaTransition<T>> getUnstableTransitions() {

        return this.transitions;
    }

    public NfaState<T> getStartState() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        return this.startState;
    }

    NfaState<T> getUnstableStartState() {

        return this.startState;
    }

    public NfaState<T> getAcceptState() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        return this.acceptState;
    }

    NfaState<T> getUnstableAcceptState() {

        return this.acceptState;
    }

    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this NFA is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("NFA:{ ");

            for (NfaTransition<T> transition : this.transitions) {
                sb.append(transition);
                sb.append(" ");
            }

            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this NFA is already stable");
        }

        for (NfaState<T> state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSet(this.states);
        this.transitions = Collections.unmodifiableSortedSet(this.transitions);

        // sanity check
        Set<String> stateNames = new HashSet<String>();

        for (NfaState<T> state : this.states) {
            if (!stateNames.add(state.getName())) {
                throw new InternalException(
                        "two states of the same NFA may not have the same name");
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

        this.isStable = true;
    }

    public Nfa<T> unionWith(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        NfaCombineResult<T> nfaCombineResult = this.combineWith(nfa);
        Nfa<T> newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transitions from start to oldStart
        newNfa.transitions.add(new NfaTransition<T>(newNfa.startState,
                nfaCombineResult.getStartStateMapping1(this), null));
        newNfa.transitions.add(new NfaTransition<T>(newNfa.startState,
                nfaCombineResult.getStartStateMapping2(nfa), null));

        // add epsilon transitions from oldAccept to accept
        newNfa.transitions.add(new NfaTransition<T>(nfaCombineResult
                .getAcceptStateMapping1(this), newNfa.acceptState, null));
        newNfa.transitions.add(new NfaTransition<T>(nfaCombineResult
                .getAcceptStateMapping2(nfa), newNfa.acceptState, null));

        newNfa.stabilize();
        return newNfa;
    }

    public Nfa<T> concatenateWith(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        NfaCombineResult<T> nfaCombineResult = this.combineWith(nfa);
        Nfa<T> newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transition from start to start(this)
        newNfa.transitions.add(new NfaTransition<T>(newNfa.startState,
                nfaCombineResult.getStartStateMapping1(this), null));

        // add epsilon transition from this accept(this) to start(nfa)
        newNfa.transitions.add(new NfaTransition<T>(nfaCombineResult
                .getAcceptStateMapping1(this), nfaCombineResult
                .getStartStateMapping2(nfa), null));

        // add epsilon transition from accept(nfa) to accept
        newNfa.transitions.add(new NfaTransition<T>(nfaCombineResult
                .getAcceptStateMapping2(nfa), newNfa.acceptState, null));

        newNfa.stabilize();
        return newNfa;
    }

    public Nfa<T> zeroOrMore() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new NFA
        Map<NfaState<T>, NfaState<T>> oldNfaStateMap = new HashMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState<T> startThis = oldNfaStateMap.get(this.startState);
        NfaState<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to accept
        newNfa.transitions.add(new NfaTransition<T>(newNfa.startState,
                newNfa.acceptState, null));

        // add epsilon transition from start to start(this)
        newNfa.transitions.add(new NfaTransition<T>(newNfa.startState,
                startThis, null));

        // add epsilon transition from accept(this) to accept
        newNfa.transitions.add(new NfaTransition<T>(acceptThis,
                newNfa.acceptState, null));

        // add epsilon transition from accept(this) to start(this)
        newNfa.transitions
                .add(new NfaTransition<T>(acceptThis, startThis, null));

        newNfa.stabilize();
        return newNfa;
    }

    public Nfa<T> zeroOrOne() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new NFA
        Map<NfaState<T>, NfaState<T>> oldNfaStateMap = new HashMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState<T> startThis = oldNfaStateMap.get(this.startState);
        NfaState<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to accept
        newNfa.transitions.add(new NfaTransition<T>(newNfa.startState,
                newNfa.acceptState, null));

        // add epsilon transition from start to start(this)
        newNfa.transitions.add(new NfaTransition<T>(newNfa.startState,
                startThis, null));

        // add epsilon transition from accept(this) to accept
        newNfa.transitions.add(new NfaTransition<T>(acceptThis,
                newNfa.acceptState, null));

        newNfa.stabilize();
        return newNfa;
    }

    public Nfa<T> oneOrMore() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new NFA
        Map<NfaState<T>, NfaState<T>> oldNfaStateMap = new HashMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState<T> startThis = oldNfaStateMap.get(this.startState);
        NfaState<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to start(this)
        newNfa.transitions.add(new NfaTransition<T>(newNfa.startState,
                startThis, null));

        // add epsilon transition from accept(this) to accept
        newNfa.transitions.add(new NfaTransition<T>(acceptThis,
                newNfa.acceptState, null));

        // add epsilon transition from accept(this) to start(this)
        newNfa.transitions
                .add(new NfaTransition<T>(acceptThis, startThis, null));

        newNfa.stabilize();
        return newNfa;
    }

    public Dfa<T> shortest() {

        return Dfa.shortest(this);
    }

    public Dfa<T> subtract(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        return Dfa.difference(this, nfa);
    }

    public Dfa<T> intersect(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        return Dfa.intersection(this, nfa);
    }

    NfaCombineResult<T> combineWith(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        // Create a new NFA
        AlphabetMergeResult<T> alphabetMergeResult = this.alphabet
                .mergeWith(nfa.getAlphabet());
        Nfa<T> newNfa = new Nfa<T>(alphabetMergeResult.getNewAlphabet());

        // add old states and transitions to new NFA
        Map<NfaState<T>, NfaState<T>> oldNfa1StateMap = new HashMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfa1StateMap,
                alphabetMergeResult);

        Map<NfaState<T>, NfaState<T>> oldNfa2StateMap = new HashMap<NfaState<T>, NfaState<T>>();
        nfa.addStatesAndTransitionsTo(newNfa, oldNfa2StateMap,
                alphabetMergeResult);

        return new NfaCombineResult<T>(newNfa, this, oldNfa1StateMap, nfa,
                oldNfa2StateMap);
    }

    private void addStatesAndTransitionsTo(
            Nfa<T> newNfa,
            Map<NfaState<T>, NfaState<T>> nfaStateMap,
            AlphabetMergeResult<T> alphabetMergeResult) {

        // add states
        for (NfaState<T> oldState : this.states) {

            NfaState<T> newState = new NfaState<T>("state"
                    + (newNfa.states.size() - 1));

            newNfa.states.add(newState);
            nfaStateMap.put(oldState, newState);
        }

        // add transitions
        for (NfaTransition<T> oldTransition : this.transitions) {

            NfaState<T> newSource = nfaStateMap.get(oldTransition.getSource());
            NfaState<T> newDestination = nfaStateMap.get(oldTransition
                    .getDestination());

            if (oldTransition.getSymbol() != null) {
                for (Symbol<T> newSymbol : alphabetMergeResult.getNewSymbols(
                        oldTransition.getSymbol(), this.alphabet)) {

                    newNfa.transitions.add(new NfaTransition<T>(newSource,
                            newDestination, newSymbol));
                }
            }
            else {
                newNfa.transitions.add(new NfaTransition<T>(newSource,
                        newDestination, null));
            }
        }
    }

    private void addStatesAndTransitionsTo(
            Nfa<T> newNfa,
            Map<NfaState<T>, NfaState<T>> nfaStateMap) {

        if (!this.alphabet.equals(newNfa.alphabet)) {
            throw new InternalException("different alphabets are not allowed");
        }

        // add states
        for (NfaState<T> oldState : this.states) {

            NfaState<T> newState = new NfaState<T>("state"
                    + (newNfa.states.size() - 1));

            newNfa.states.add(newState);
            nfaStateMap.put(oldState, newState);
        }

        // add transitions
        for (NfaTransition<T> oldTransition : this.transitions) {

            NfaState<T> newSource = nfaStateMap.get(oldTransition.getSource());
            NfaState<T> newDestination = nfaStateMap.get(oldTransition
                    .getDestination());

            newNfa.transitions.add(new NfaTransition<T>(newSource,
                    newDestination, oldTransition.getSymbol()));
        }
    }
}
