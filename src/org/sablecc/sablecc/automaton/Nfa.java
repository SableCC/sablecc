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
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sablecc.sablecc.alphabet.Alphabet;
import org.sablecc.sablecc.alphabet.AlphabetMergeResult;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

public final class Nfa<T extends Comparable<? super T>> {

    private static final String lineSeparator = System
            .getProperty("line.separator");

    private Alphabet<T> alphabet;

    private SortedSet<NfaState<T>> states;

    private NfaState<T> startState;

    private NfaState<T> acceptState;

    private boolean isStable;

    private String toString;

    private final Comparator<Symbol<T>> symbolComparator = new Comparator<Symbol<T>>() {

        // allows comparison of null symbols
        public int compare(
                Symbol<T> symbol1,
                Symbol<T> symbol2) {

            if (symbol1 == null) {
                return symbol2 == null ? 0 : -1;
            }

            if (symbol2 == null) {
                return 1;
            }

            return symbol1.compareTo(symbol2);
        }
    };

    /**
     * Constructs a NFA for the language <code>{""}</code>, containing the
     * empty string.
     */
    public Nfa() {

        init();

        // empty alphabet
        this.alphabet = new Alphabet<T>();

        // transition: start->(epsilon)->accept
        this.startState.addTransition(null, this.acceptState);

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

        Symbol<T> symbol = new Symbol<T>(interval);
        this.alphabet = new Alphabet<T>(symbol);

        // transition: start->(symbol)->accept
        this.startState.addTransition(symbol, this.acceptState);

        stabilize();
    }

    /**
     * Constructs an incomplete NFA. This private constructor returns a NFA to
     * which new states can be added. The <code>stabilize()</code> method
     * should be called on this instance before exposing it publicly.
     */
    private Nfa(
            Alphabet<T> alphabet) {

        init();

        this.alphabet = alphabet;
    }

    private void init() {

        this.states = new TreeSet<NfaState<T>>();

        this.startState = new NfaState<T>(this);
        this.acceptState = new NfaState<T>(this);

        this.isStable = false;
    }

    public Alphabet<T> getAlphabet() {

        return this.alphabet;
    }

    public SortedSet<NfaState<T>> getStates() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        return this.states;
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

    Comparator<Symbol<T>> getSymbolComparator() {

        return this.symbolComparator;
    }

    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this NFA is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("NFA:{");

            for (NfaState<T> state : this.states) {
                sb.append(lineSeparator);
                sb.append("    ");
                sb.append(state);

                if (state == this.startState) {
                    sb.append("(start)");
                }

                if (state == this.acceptState) {
                    sb.append("(accept)");
                }

                sb.append(":");
                for (Map.Entry<Symbol<T>, SortedSet<NfaState<T>>> entry : state
                        .getTransitions().entrySet()) {
                    Symbol<T> symbol = entry.getKey();
                    SortedSet<NfaState<T>> targets = entry.getValue();

                    for (NfaState<T> target : targets) {
                        sb.append(lineSeparator);
                        sb.append("        ");
                        sb.append(symbol);
                        sb.append(" -> ");
                        sb.append(target);
                    }
                }
            }

            sb.append(lineSeparator);
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

        this.states = Collections.unmodifiableSortedSet(this.states);

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
        newNfa.startState.addTransition(null, nfaCombineResult
                .getNewNfa1StartState());
        newNfa.startState.addTransition(null, nfaCombineResult
                .getNewNfa2StartState());

        // add epsilon transitions from oldAccept to accept
        nfaCombineResult.getNewNfa1AcceptState().addTransition(null,
                newNfa.acceptState);
        nfaCombineResult.getNewNfa2AcceptState().addTransition(null,
                newNfa.acceptState);

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
        newNfa.startState.addTransition(null, nfaCombineResult
                .getNewNfa1StartState());

        // add epsilon transition from this accept(this) to start(nfa)
        nfaCombineResult.getNewNfa1AcceptState().addTransition(null,
                nfaCombineResult.getNewNfa2StartState());

        // add epsilon transition from accept(nfa) to accept
        nfaCombineResult.getNewNfa2AcceptState().addTransition(null,
                newNfa.acceptState);

        newNfa.stabilize();
        return newNfa;
    }

    public Nfa<T> zeroOrMore() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new NFA
        SortedMap<NfaState<T>, NfaState<T>> oldNfaStateMap = new TreeMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState<T> startThis = oldNfaStateMap.get(this.startState);
        NfaState<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to accept
        newNfa.startState.addTransition(null, newNfa.acceptState);

        // add epsilon transition from start to start(this)
        newNfa.startState.addTransition(null, startThis);

        // add epsilon transition from accept(this) to accept
        acceptThis.addTransition(null, newNfa.acceptState);

        // add epsilon transition from accept(this) to start(this)
        acceptThis.addTransition(null, startThis);

        newNfa.stabilize();
        return newNfa;
    }

    public Nfa<T> zeroOrOne() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new NFA
        SortedMap<NfaState<T>, NfaState<T>> oldNfaStateMap = new TreeMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState<T> startThis = oldNfaStateMap.get(this.startState);
        NfaState<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to accept
        newNfa.startState.addTransition(null, newNfa.acceptState);

        // add epsilon transition from start to start(this)
        newNfa.startState.addTransition(null, startThis);

        // add epsilon transition from accept(this) to accept
        acceptThis.addTransition(null, newNfa.acceptState);

        newNfa.stabilize();
        return newNfa;
    }

    public Nfa<T> oneOrMore() {

        if (!this.isStable) {
            throw new InternalException("this NFA is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new NFA
        SortedMap<NfaState<T>, NfaState<T>> oldNfaStateMap = new TreeMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState<T> startThis = oldNfaStateMap.get(this.startState);
        NfaState<T> acceptThis = oldNfaStateMap.get(this.acceptState);

        // add epsilon transition from start to start(this)
        newNfa.startState.addTransition(null, startThis);

        // add epsilon transition from accept(this) to accept
        acceptThis.addTransition(null, newNfa.acceptState);

        // add epsilon transition from accept(this) to start(this)
        acceptThis.addTransition(null, startThis);

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
        SortedMap<NfaState<T>, NfaState<T>> oldNfa1StateMap = new TreeMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfa1StateMap,
                alphabetMergeResult);

        SortedMap<NfaState<T>, NfaState<T>> oldNfa2StateMap = new TreeMap<NfaState<T>, NfaState<T>>();
        nfa.addStatesAndTransitionsTo(newNfa, oldNfa2StateMap,
                alphabetMergeResult);

        return new NfaCombineResult<T>(newNfa, this, oldNfa1StateMap, nfa,
                oldNfa2StateMap);
    }

    private void addStatesAndTransitionsTo(
            Nfa<T> newNfa,
            SortedMap<NfaState<T>, NfaState<T>> nfaStateMap) {

        if (newNfa.alphabet != this.alphabet) {
            throw new InternalException(
                    "this NFA and newNfa must share the same alphabet");
        }

        for (NfaState<T> oldState : this.states) {

            NfaState<T> newState = new NfaState<T>(newNfa);
            nfaStateMap.put(oldState, newState);
        }

        for (NfaState<T> oldState : this.states) {
            for (Map.Entry<Symbol<T>, SortedSet<NfaState<T>>> entry : oldState
                    .getTransitions().entrySet()) {

                Symbol<T> symbol = entry.getKey();
                SortedSet<NfaState<T>> oldTargets = entry.getValue();

                for (NfaState<T> oldTarget : oldTargets) {

                    nfaStateMap.get(oldState).addTransition(symbol,
                            nfaStateMap.get(oldTarget));
                }
            }
        }
    }

    private void addStatesAndTransitionsTo(
            Nfa<T> newNfa,
            SortedMap<NfaState<T>, NfaState<T>> nfaStateMap,
            AlphabetMergeResult<T> alphabetMergeResult) {

        for (NfaState<T> oldState : this.states) {

            NfaState<T> newState = new NfaState<T>(newNfa);
            nfaStateMap.put(oldState, newState);
        }

        for (NfaState<T> oldState : this.states) {
            for (Map.Entry<Symbol<T>, SortedSet<NfaState<T>>> entry : oldState
                    .getTransitions().entrySet()) {

                Symbol<T> oldSymbol = entry.getKey();
                SortedSet<NfaState<T>> oldTargets = entry.getValue();

                for (NfaState<T> oldTarget : oldTargets) {

                    if (oldSymbol != null) {
                        for (Symbol<T> newSymbol : alphabetMergeResult
                                .getNewSymbols(oldSymbol, this.alphabet)) {

                            nfaStateMap.get(oldState).addTransition(newSymbol,
                                    nfaStateMap.get(oldTarget));
                        }
                    }
                    else {
                        nfaStateMap.get(oldState).addTransition(null,
                                nfaStateMap.get(oldTarget));
                    }
                }
            }
        }
    }

    int getNextStateId() {

        if (this.isStable) {
            throw new InternalException("a stable NFA may not be modified");
        }

        return this.states.size();
    }

    void addState(
            NfaState<T> state) {

        if (this.isStable) {
            throw new InternalException("a stable NFA may not be modified");
        }

        if (!this.states.add(state)) {
            throw new InternalException("state is already in state set");
        }
    }

}
