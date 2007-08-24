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

/**
 * A non-deterministic finite automaton (or Nfa) is a state machine which as a
 * starting state and an accept state. It also have an alphabet of its available
 * symbols.
 */
public final class Nfa<T extends Comparable<? super T>> {

    /** Only used for line separation in method toString. */
    private static final String lineSeparator = System
            .getProperty("line.separator");

    /** The alphabet for this <code>Nfa</code>. */
    private Alphabet<T> alphabet;

    /** The states of this <code>Nfa</code>. */
    private SortedSet<NfaState<T>> states;

    /** The starting state of this <code>Nfa</code>. */
    private NfaState<T> startState;

    /** The acceptation state of this <code>Nfa</code>. */
    private NfaState<T> acceptState;

    /** A stability status for this <code>Nfa</code>. */
    private boolean isStable;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /** A comparator for symbols. */
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
     * Constructs a <code>Nfa</code> for the language <code>{""}</code>,
     * containing the empty string.
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
     * Constructs a <code>Nfa</code> for the language <code>{"s"}</code>
     * where <code>s</code> is a symbol representing a single interval.
     * 
     * @param interval
     *            the interval.
     * @throws InternalException
     *             if the interval is <code>null</code>.
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
     * Constructs a <code>Nfa</code> for the language <code>{"s"}</code>
     * where <code>s</code> is the provided symbol.
     * 
     * @param symbol
     *            the symbol.
     * @throws InternalException
     *             if the symbol is <code>null</code>.
     */
    public Nfa(
            Symbol<T> symbol) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        init();

        this.alphabet = new Alphabet<T>(symbol);

        // transition: start->(symbol)->accept
        this.startState.addTransition(symbol, this.acceptState);

        stabilize();
    }

    /**
     * Constructs a <code>Nfa</code> which is similar to the provided
     * <code>Dfa</code>.
     * 
     * @param dfa
     *            the <code>Dfa</code>.
     * @throws InternalException
     *             if the <code>Dfa</code> is <code>null</code>.
     */
    public Nfa(
            Dfa<T> dfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        init();

        this.alphabet = dfa.getAlphabet();

        SortedMap<DfaState<T>, NfaState<T>> dfaToNfaStateMap = new TreeMap<DfaState<T>, NfaState<T>>();

        // Map dfa start to this start.
        dfaToNfaStateMap.put(dfa.getStartState(), this.startState);

        // Create a state for every dfa state, except start and dead-end.
        for (DfaState<T> dfaState : dfa.getStates()) {

            if (dfaState == dfa.getDeadEndState()
                    || dfaState == dfa.getStartState()) {
                continue;
            }

            // add mapping to new state
            dfaToNfaStateMap.put(dfaState, new NfaState<T>(this));
        }

        // Create transitions
        for (Map.Entry<DfaState<T>, NfaState<T>> stateEntry : dfaToNfaStateMap
                .entrySet()) {
            DfaState<T> startDfaState = stateEntry.getKey();
            NfaState<T> startNfaState = stateEntry.getValue();

            for (Map.Entry<Symbol<T>, DfaState<T>> transitionEntry : startDfaState
                    .getTransitions().entrySet()) {
                Symbol<T> symbol = transitionEntry.getKey();
                DfaState<T> destinationDfaState = transitionEntry.getValue();
                NfaState<T> destinationNfaState = dfaToNfaStateMap
                        .get(destinationDfaState);

                startNfaState.addTransition(symbol, destinationNfaState);
            }
        }

        // Add transitions to accept state
        for (DfaState<T> dfaState : dfa.getAcceptStates()) {
            NfaState<T> nfaState = dfaToNfaStateMap.get(dfaState);

            nfaState.addTransition(null, this.acceptState);
        }

        stabilize();
    }

    /**
     * Constructs a NFA which is similar to the provided minimal DFA.
     * 
     * @param minimalDfa
     *            the minimal DFA.
     * @throws InternalException
     *             if the minimal DFA is <code>null</code>.
     */
    public Nfa(
            MinimalDfa<T> minimalDfa) {

        if (minimalDfa == null) {
            throw new InternalException("minimalDfa may not be null");
        }

        init();

        this.alphabet = minimalDfa.getAlphabet();

        SortedMap<MinimalDfaState<T>, NfaState<T>> minimalDfaToNfaStateMap = new TreeMap<MinimalDfaState<T>, NfaState<T>>();

        // Map minimalDfa start to this start.
        minimalDfaToNfaStateMap
                .put(minimalDfa.getStartState(), this.startState);

        // Create a state for every minimalDfa state, except start and dead-end.
        for (MinimalDfaState<T> minimalDfaState : minimalDfa.getStates()) {

            if (minimalDfaState == minimalDfa.getDeadEndState()
                    || minimalDfaState == minimalDfa.getStartState()) {
                continue;
            }

            // add mapping to new state
            minimalDfaToNfaStateMap.put(minimalDfaState, new NfaState<T>(this));
        }

        // Create transitions
        for (Map.Entry<MinimalDfaState<T>, NfaState<T>> stateEntry : minimalDfaToNfaStateMap
                .entrySet()) {
            MinimalDfaState<T> startMinimalDfaState = stateEntry.getKey();
            NfaState<T> startNfaState = stateEntry.getValue();

            for (Map.Entry<Symbol<T>, MinimalDfaState<T>> transitionEntry : startMinimalDfaState
                    .getTransitions().entrySet()) {
                Symbol<T> symbol = transitionEntry.getKey();
                MinimalDfaState<T> destinationMinimalDfaState = transitionEntry
                        .getValue();
                NfaState<T> destinationNfaState = minimalDfaToNfaStateMap
                        .get(destinationMinimalDfaState);

                startNfaState.addTransition(symbol, destinationNfaState);
            }
        }

        // Add transitions to accept state
        for (MinimalDfaState<T> minimalDfaState : minimalDfa.getAcceptStates()) {
            NfaState<T> nfaState = minimalDfaToNfaStateMap.get(minimalDfaState);

            nfaState.addTransition(null, this.acceptState);
        }

        stabilize();
    }

    /**
     * Constructs an incomplete <code>Nfa</code>. This private constructor
     * returns a <code>Nfa</code> to which new states can be added. The
     * <code>stabilize()</code> method should be called on this instance
     * before exposing it publicly.
     * 
     * @param alphabet
     *            the alphabet.
     */
    private Nfa(
            Alphabet<T> alphabet) {

        init();

        this.alphabet = alphabet;
    }

    /**
     * Initializes this <code>Nfa</code>. This method must be called by all
     * constructors.
     */
    private void init() {

        this.states = new TreeSet<NfaState<T>>();

        this.startState = new NfaState<T>(this);
        this.acceptState = new NfaState<T>(this);

        this.isStable = false;
    }

    /**
     * Returns the alphabet of this <code>Nfa</code>.
     * 
     * @return the alphabet.
     */
    public Alphabet<T> getAlphabet() {

        return this.alphabet;
    }

    /**
     * Returns the states of this <code>Nfa</code>.
     * 
     * @return the set of states.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<NfaState<T>> getStates() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        return this.states;
    }

    /**
     * Returns the starting state of this <code>Nfa</code>.
     * 
     * @return the starting state.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public NfaState<T> getStartState() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        return this.startState;
    }

    /**
     * Returns the starting state of this <code>Nfa</code> if it is unstable.
     * 
     * @return the starting state.
     */
    NfaState<T> getUnstableStartState() {

        return this.startState;
    }

    /**
     * Returns the acceptation state of this <code>Nfa</code>.
     * 
     * @return the acceptation state.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public NfaState<T> getAcceptState() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        return this.acceptState;
    }

    /**
     * Returns the symbols comparator for <code>Nfa</code> instances.
     * 
     * @return the symbol comparator.
     */
    Comparator<Symbol<T>> getSymbolComparator() {

        return this.symbolComparator;
    }

    /**
     * Returns the string representation of this <code>Nfa</code>.
     * 
     * @return the string representation.
     * @throws InternalException
     *             if this instance is not stable.
     */
    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this Nfa is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("Nfa:{");

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

    /**
     * Stabilizes this <code>Nfa</code> by stabilizing each of its states.
     * 
     * @throws InternalException
     *             if this <code>Nfa</code> is already stable.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this Nfa is already stable");
        }

        for (NfaState<T> state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSortedSet(this.states);

        this.isStable = true;
    }

    /**
     * Returns a new <code>Nfa</code> instance which represents the union of
     * this <code>Nfa</code> instance with the provided <code>Nfa</code>
     * instance.
     * 
     * @param nfa
     *            the nfa.
     * @return the new <code>Nfa</code> after union.
     * @throws InternalException
     *             if one of the two <code>Nfa</code> instances is not stable
     *             or if the provided one is <code>null</code>.
     */
    public Nfa<T> unionWith(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
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

    /**
     * Returns a new <code>Nfa</code> instance which represents the
     * concatenation of this <code>Nfa</code> instance with the provided
     * <code>Nfa</code> instance.
     * 
     * @param nfa
     *            the nfa.
     * @return the new <code>Nfa</code> after concatenation.
     * @throws InternalException
     *             if one of the two <code>Nfa</code> instances is not stable
     *             or if the provided one is <code>null</code>.
     */
    public Nfa<T> concatenateWith(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
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

    /**
     * Returns a new <code>Nfa</code> instance which represents the repetition
     * of zero or more of this <code>Nfa</code> instance.
     * 
     * @return the new <code>Nfa</code>.
     * @throws InternalException
     *             if this <code>Nfa</code> is not stable.
     */
    public Nfa<T> zeroOrMore() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new Nfa
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

    /**
     * Returns a new <code>Nfa</code> instance which represents the presence
     * of zero or one of this <code>Nfa</code> instance.
     * 
     * @return the new <code>Nfa</code>.
     * @throws InternalException
     *             if this <code>Nfa</code> is not stable.
     */
    public Nfa<T> zeroOrOne() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new Nfa
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

    /**
     * Returns a new <code>Nfa</code> instance which represents the repetition
     * of at least one or more of this <code>Nfa</code> instance.
     * 
     * @return the new <code>Nfa</code>.
     * @throws InternalException
     *             if this <code>Nfa</code> is not stable.
     */
    public Nfa<T> oneOrMore() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // add old states and transitions to new Nfa
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

    /**
     * Returns a new <code>Nfa</code> instance which represents <code>n</code>
     * repetitions of this <code>Nfa</code> instance.
     * 
     * @param n
     *            the number of repetitions.
     * @return the new <code>Nfa</code>.
     * @throws InternalException
     *             if this <code>Nfa</code> is not stable, or if
     *             <code>n</code> is negative.
     */
    public Nfa<T> simpleExponent(
            int n) {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        if (n < 0) {
            throw new InternalException("n must be greater or equal to zero");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // initialize "last state"
        NfaState<T> lastState = newNfa.startState;

        for (int i = 0; i < n; i++) {

            // add old states and transitions
            SortedMap<NfaState<T>, NfaState<T>> oldNfaStateMap = new TreeMap<NfaState<T>, NfaState<T>>();
            this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

            // link from "last state" to newly added "old start"
            lastState.addTransition(null, oldNfaStateMap.get(this.startState));

            // update "last state"
            lastState = oldNfaStateMap.get(this.acceptState);
        }

        // link from "last state" to accept state.
        lastState.addTransition(null, newNfa.acceptState);

        newNfa.stabilize();
        return newNfa;
    }

    /**
     * Returns a new <code>Nfa</code> instance which represents at least
     * <code>lowerBound</code> and at most <code>upperBound</code>
     * repetitions of this <code>Nfa</code> instance.
     * 
     * @param lowerBound
     *            the minimal number of repetitions.
     * @param upperBound
     *            the maximal number of repetitions.
     * @return the new <code>Nfa</code>.
     * @throws InternalException
     *             if this <code>Nfa</code> is not stable, if any bound is
     *             negative, or if <code>lowerBound</code> is greater than
     *             <code>upperBound</code>.
     */
    public Nfa<T> rangeExponent(
            int lowerBound,
            int upperBound) {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        if (lowerBound < 0) {
            throw new InternalException(
                    "lowerBound must be greater or equal to zero");
        }

        if (upperBound < 0) {
            throw new InternalException(
                    "upperBound must be greater or equal to zero");
        }

        if (upperBound < lowerBound) {
            throw new InternalException(
                    "upperBound must be greater or equal to lowerBound");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // initialize "last state"
        NfaState<T> lastState = newNfa.startState;

        for (int i = 0; i < lowerBound; i++) {

            // add old states and transitions
            SortedMap<NfaState<T>, NfaState<T>> oldNfaStateMap = new TreeMap<NfaState<T>, NfaState<T>>();
            this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

            // link from "last state" to newly added "old start"
            lastState.addTransition(null, oldNfaStateMap.get(this.startState));

            // update "last state"
            lastState = oldNfaStateMap.get(this.acceptState);
        }

        for (int i = lowerBound; i < upperBound; i++) {

            // link from "last state" to accept state.
            lastState.addTransition(null, newNfa.acceptState);

            // add old states and transitions
            SortedMap<NfaState<T>, NfaState<T>> oldNfaStateMap = new TreeMap<NfaState<T>, NfaState<T>>();
            this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

            // link from "last state" to newly added "old start"
            lastState.addTransition(null, oldNfaStateMap.get(this.startState));

            // update "last state"
            lastState = oldNfaStateMap.get(this.acceptState);
        }

        // link from "last state" to accept state.
        lastState.addTransition(null, newNfa.acceptState);

        newNfa.stabilize();
        return newNfa;
    }

    /**
     * Returns a new <code>Nfa</code> instance which represents at least
     * <code>n</code> repetitions of this <code>Nfa</code> instance.
     * 
     * @param n
     *            the minimal number of repetitions.
     * @return the new <code>Nfa</code>.
     * @throws InternalException
     *             if this <code>Nfa</code> is not stable, if any bound is
     *             negative, or if <code>lowerBound</code> is greater than
     *             <code>upperBound</code>.
     */
    public Nfa<T> atLeastExponent(
            int n) {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        if (n < 0) {
            throw new InternalException(
                    "lowerBound must be greater or equal to zero");
        }

        Nfa<T> newNfa = new Nfa<T>(this.alphabet);

        // initialize "last state"
        NfaState<T> lastState = newNfa.startState;

        for (int i = 0; i < n; i++) {

            // add old states and transitions
            SortedMap<NfaState<T>, NfaState<T>> oldNfaStateMap = new TreeMap<NfaState<T>, NfaState<T>>();
            this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

            // link from "last state" to newly added "old start"
            lastState.addTransition(null, oldNfaStateMap.get(this.startState));

            // update "last state"
            lastState = oldNfaStateMap.get(this.acceptState);
        }

        {
            // link from "last state" to accept state.
            lastState.addTransition(null, newNfa.acceptState);

            // add old states and transitions
            SortedMap<NfaState<T>, NfaState<T>> oldNfaStateMap = new TreeMap<NfaState<T>, NfaState<T>>();
            this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

            // link from "last state" to newly added "old start"
            lastState.addTransition(null, oldNfaStateMap.get(this.startState));

            // link from "old accept" to "old start"
            oldNfaStateMap.get(this.acceptState).addTransition(null,
                    oldNfaStateMap.get(this.startState));

            // update "last state"
            lastState = oldNfaStateMap.get(this.acceptState);
        }

        // link from "last state" to accept state.
        lastState.addTransition(null, newNfa.acceptState);

        newNfa.stabilize();
        return newNfa;
    }

    /**
     * Returns a <code>Dfa</code> instance which represents the shortest
     * possible <code>Dfa</code> for this <code>Nfa</code> instance.
     * 
     * @return the new <code>Dfa</code>.
     */
    public Dfa<T> shortest() {

        return Dfa.shortest(this);
    }

    /**
     * Returns a <code>Dfa</code> instance which represents the substraction
     * of this <code>Nfa</code> with the provided one.
     * 
     * @param nfa
     *            the nfa to substract.
     * @return the new <code>Dfa</code>.
     * @throws InternalException
     *             if the provided <code>Nfa</code> is <code>null</code>.
     */
    public Dfa<T> subtract(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        return Dfa.difference(this, nfa);
    }

    /**
     * Returns a <code>Dfa</code> instance which represents the intersection
     * of this <code>Nfa</code> with the provided one.
     * 
     * @param nfa
     *            the nfa to substract.
     * @return the new <code>Dfa</code>.
     * @throws InternalException
     *             if the provided <code>Nfa</code> is <code>null</code>.
     */
    public Dfa<T> intersect(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        return Dfa.intersection(this, nfa);
    }

    /**
     * Returns a new <code>NfaCombineResult</code>, the result of the
     * combination of this <code>Nfa</code> with the provided one.
     * 
     * @param nfa
     *            the nfa to combine with.
     * @return the new <code>NfaCombineResult</code>.
     * @throws InternalException
     *             if one of the two <code>Nfa</code> instances is not stable
     *             or if the provided one is <code>null</code>.
     */
    NfaCombineResult<T> combineWith(
            Nfa<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        // Create a new Nfa
        AlphabetMergeResult<T> alphabetMergeResult = this.alphabet
                .mergeWith(nfa.getAlphabet());
        Nfa<T> newNfa = new Nfa<T>(alphabetMergeResult.getNewAlphabet());

        // add old states and transitions to new Nfa
        SortedMap<NfaState<T>, NfaState<T>> oldNfa1StateMap = new TreeMap<NfaState<T>, NfaState<T>>();
        this.addStatesAndTransitionsTo(newNfa, oldNfa1StateMap,
                alphabetMergeResult);

        SortedMap<NfaState<T>, NfaState<T>> oldNfa2StateMap = new TreeMap<NfaState<T>, NfaState<T>>();
        nfa.addStatesAndTransitionsTo(newNfa, oldNfa2StateMap,
                alphabetMergeResult);

        return new NfaCombineResult<T>(newNfa, this, oldNfa1StateMap, nfa,
                oldNfa2StateMap);
    }

    /**
     * Adds the states and transitions of this <code>Nfa</code> instance to
     * the new one provided.
     * 
     * @param newNfa
     *            the new nfa to add states and transitions.
     * @param nfaStateMap
     *            a map of states.
     * @throws InternalException
     *             if the two <code>Nfa</code> instances does not have the
     *             same alphabet.
     */
    private void addStatesAndTransitionsTo(
            Nfa<T> newNfa,
            SortedMap<NfaState<T>, NfaState<T>> nfaStateMap) {

        if (newNfa.alphabet != this.alphabet) {
            throw new InternalException(
                    "this Nfa and newNfa must share the same alphabet");
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

    /**
     * Adds the states and transitions of this <code>Nfa</code> instance to
     * the new one provided.
     * 
     * @param newNfa
     *            the new nfa to add states and transitions.
     * @param nfaStateMap
     *            a map of states.
     * @param alphabetMergeResult
     *            the merge result of the alphabets of the two <code>Nfa</code>
     *            instances.
     */
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

    /**
     * Returns the ID for the following state.
     * 
     * @return the ID of the next state.
     * @throws InternalException
     *             if this <code>Nfa</code> instance is stable.
     */
    int getNextStateId() {

        if (this.isStable) {
            throw new InternalException("a stable Nfa may not be modified");
        }

        return this.states.size();
    }

    /**
     * Adds a state to this <code>Nfa</code>.
     * 
     * @param state
     *            the state to add.
     * @throws InternalException
     *             if this <code>Nfa</code> is stable or or if the state is
     *             already in the state set.
     */
    void addState(
            NfaState<T> state) {

        if (this.isStable) {
            throw new InternalException("a stable Nfa may not be modified");
        }

        if (!this.states.add(state)) {
            throw new InternalException("state is already in state set");
        }
    }

}
