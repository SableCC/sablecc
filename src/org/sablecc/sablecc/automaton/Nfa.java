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

import static org.sablecc.sablecc.util.UsefulStaticImports.LINE_SEPARATOR;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sablecc.sablecc.alphabet.Alphabet;
import org.sablecc.sablecc.alphabet.AlphabetMergeResult;
import org.sablecc.sablecc.alphabet.Bound;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

/**
 * A non-deterministic finite automaton (or Nfa) is a state machine which as a
 * starting state and an accept state. It also have an alphabet of its available
 * symbols.
 */
public final class Nfa {

    /** The alphabet for this <code>Nfa</code>. */
    private Alphabet alphabet;

    /** The states of this <code>Nfa</code>. */
    private SortedSet<NfaState> states;

    /** The starting state of this <code>Nfa</code>. */
    private NfaState startState;

    /** The acceptation state of this <code>Nfa</code>. */
    private NfaState acceptState;

    /** A stability status for this <code>Nfa</code>. */
    private boolean isStable;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /** A comparator for symbols. */
    private final Comparator<Symbol> symbolComparator = new Comparator<Symbol>() {

        // allows comparison of null symbols
        public int compare(
                Symbol symbol1,
                Symbol symbol2) {

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
        this.alphabet = new Alphabet();

        // transition: start->(epsilon)->accept
        this.startState.addTransition(null, this.acceptState);

        stabilize();
    }

    /**
     * Constructs a <code>Nfa</code> for the language <code>{"s"}</code>
     * where <code>s</code> is the provided symbol.
     */
    public Nfa(
            Symbol symbol) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        init();

        this.alphabet = new Alphabet(symbol);

        // transition: start->(symbol)->accept
        this.startState.addTransition(symbol, this.acceptState);

        stabilize();
    }

    /**
     * Constructs a <code>Nfa</code> for the language <code>{"s"}</code>
     * where <code>s</code> is a symbol representing a single interval.
     */
    public Nfa(
            Interval interval) {

        if (interval == null) {
            throw new InternalException("interval may not be null");
        }

        init();

        Symbol symbol = new Symbol(interval);
        this.alphabet = new Alphabet(symbol);

        // transition: start->(symbol)->accept
        this.startState.addTransition(symbol, this.acceptState);

        stabilize();
    }

    public Nfa(
            Bound bound) {

        if (bound == null) {
            throw new InternalException("bound may not be null");
        }

        init();

        Symbol symbol = new Symbol(bound);
        this.alphabet = new Alphabet(symbol);

        // transition: start->(symbol)->accept
        this.startState.addTransition(symbol, this.acceptState);

        stabilize();
    }

    public Nfa(
            char bound) {

        this(new Interval(bound));
    }

    public Nfa(
            int bound) {

        this(new Interval(bound));
    }

    public Nfa(
            BigInteger bound) {

        this(new Interval(bound));
    }

    public Nfa(
            String bound) {

        this(new Interval(bound));
    }

    /**
     * Constructs a <code>Nfa</code> which is similar to the provided
     * <code>Dfa</code>.
     */
    public Nfa(
            Dfa dfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        init();

        this.alphabet = dfa.getAlphabet();

        SortedMap<DfaState, NfaState> dfaToNfaStateMap = new TreeMap<DfaState, NfaState>();

        // Map dfa start to this start.
        dfaToNfaStateMap.put(dfa.getStartState(), this.startState);

        // Create a state for every dfa state, except start and dead-end.
        for (DfaState dfaState : dfa.getStates()) {

            if (dfaState == dfa.getDeadEndState()
                    || dfaState == dfa.getStartState()) {
                continue;
            }

            // add mapping to new state
            dfaToNfaStateMap.put(dfaState, new NfaState(this));
        }

        // Create transitions
        for (Map.Entry<DfaState, NfaState> stateEntry : dfaToNfaStateMap
                .entrySet()) {
            DfaState startDfaState = stateEntry.getKey();
            NfaState startNfaState = stateEntry.getValue();

            for (Map.Entry<Symbol, DfaState> transitionEntry : startDfaState
                    .getTransitions().entrySet()) {
                Symbol symbol = transitionEntry.getKey();
                DfaState destinationDfaState = transitionEntry.getValue();
                NfaState destinationNfaState = dfaToNfaStateMap
                        .get(destinationDfaState);

                startNfaState.addTransition(symbol, destinationNfaState);
            }
        }

        // Add transitions to accept state
        for (DfaState dfaState : dfa.getAcceptStates()) {
            NfaState nfaState = dfaToNfaStateMap.get(dfaState);

            nfaState.addTransition(null, this.acceptState);
        }

        stabilize();
    }

    /**
     * Constructs a NFA which is similar to the provided minimal DFA.
     */
    public Nfa(
            MinimalDfa minimalDfa) {

        if (minimalDfa == null) {
            throw new InternalException("minimalDfa may not be null");
        }

        init();

        this.alphabet = minimalDfa.getAlphabet();

        SortedMap<MinimalDfaState, NfaState> minimalDfaToNfaStateMap = new TreeMap<MinimalDfaState, NfaState>();

        // Map minimalDfa start to this start.
        minimalDfaToNfaStateMap
                .put(minimalDfa.getStartState(), this.startState);

        // Create a state for every minimalDfa state, except start and dead-end.
        for (MinimalDfaState minimalDfaState : minimalDfa.getStates()) {

            if (minimalDfaState == minimalDfa.getDeadEndState()
                    || minimalDfaState == minimalDfa.getStartState()) {
                continue;
            }

            // add mapping to new state
            minimalDfaToNfaStateMap.put(minimalDfaState, new NfaState(this));
        }

        // Create transitions
        for (Map.Entry<MinimalDfaState, NfaState> stateEntry : minimalDfaToNfaStateMap
                .entrySet()) {
            MinimalDfaState startMinimalDfaState = stateEntry.getKey();
            NfaState startNfaState = stateEntry.getValue();

            for (Map.Entry<Symbol, MinimalDfaState> transitionEntry : startMinimalDfaState
                    .getTransitions().entrySet()) {
                Symbol symbol = transitionEntry.getKey();
                MinimalDfaState destinationMinimalDfaState = transitionEntry
                        .getValue();
                NfaState destinationNfaState = minimalDfaToNfaStateMap
                        .get(destinationMinimalDfaState);

                startNfaState.addTransition(symbol, destinationNfaState);
            }
        }

        // Add transitions to accept state
        for (MinimalDfaState minimalDfaState : minimalDfa.getAcceptStates()) {
            NfaState nfaState = minimalDfaToNfaStateMap.get(minimalDfaState);

            nfaState.addTransition(null, this.acceptState);
        }

        stabilize();
    }

    /**
     * Constructs an incomplete <code>Nfa</code>. This private constructor
     * returns a <code>Nfa</code> to which new states can be added. The
     * <code>stabilize()</code> method should be called on this instance
     * before exposing it publicly.
     */
    private Nfa(
            Alphabet alphabet) {

        init();

        this.alphabet = alphabet;
    }

    /**
     * Initializes this <code>Nfa</code>. This method must be called by all
     * constructors.
     */
    private void init() {

        this.states = new TreeSet<NfaState>();

        this.startState = new NfaState(this);
        this.acceptState = new NfaState(this);

        this.isStable = false;
    }

    /**
     * Returns the alphabet of this <code>Nfa</code>.
     */
    public Alphabet getAlphabet() {

        return this.alphabet;
    }

    /**
     * Returns the states of this <code>Nfa</code>.
     */
    public SortedSet<NfaState> getStates() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        return this.states;
    }

    /**
     * Returns the starting state of this <code>Nfa</code>.
     */
    public NfaState getStartState() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        return this.startState;
    }

    /**
     * Returns the starting state of this <code>Nfa</code> if it is unstable.
     */
    NfaState getUnstableStartState() {

        return this.startState;
    }

    /**
     * Returns the acceptation state of this <code>Nfa</code>.
     */
    public NfaState getAcceptState() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        return this.acceptState;
    }

    /**
     * Returns the symbols comparator for <code>Nfa</code> instances.
     */
    Comparator<Symbol> getSymbolComparator() {

        return this.symbolComparator;
    }

    /**
     * Returns the string representation of this <code>Nfa</code>.
     */
    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this Nfa is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("Nfa:{");

            for (NfaState state : this.states) {
                sb.append(LINE_SEPARATOR);
                sb.append("    ");
                sb.append(state);

                if (state == this.startState) {
                    sb.append("(start)");
                }

                if (state == this.acceptState) {
                    sb.append("(accept)");
                }

                sb.append(":");
                for (Map.Entry<Symbol, SortedSet<NfaState>> entry : state
                        .getTransitions().entrySet()) {
                    Symbol symbol = entry.getKey();
                    SortedSet<NfaState> targets = entry.getValue();

                    for (NfaState target : targets) {
                        sb.append(LINE_SEPARATOR);
                        sb.append("        ");
                        sb.append(symbol);
                        sb.append(" -> ");
                        sb.append(target);
                    }
                }
            }

            sb.append(LINE_SEPARATOR);
            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Stabilizes this <code>Nfa</code> by stabilizing each of its states.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this Nfa is already stable");
        }

        for (NfaState state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSortedSet(this.states);

        this.isStable = true;
    }

    /**
     * Returns a new <code>Nfa</code> instance which represents the union of
     * this <code>Nfa</code> instance with the provided <code>Nfa</code>
     * instance.
     */
    public Nfa unionWith(
            Nfa nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        NfaCombineResult nfaCombineResult = combineWith(nfa);
        Nfa newNfa = nfaCombineResult.getNewNfa();

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
     */
    public Nfa concatenateWith(
            Nfa nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        if (!nfa.isStable) {
            throw new InternalException("nfa is not stable yet");
        }

        NfaCombineResult nfaCombineResult = combineWith(nfa);
        Nfa newNfa = nfaCombineResult.getNewNfa();

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
     */
    public Nfa zeroOrMore() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        Nfa newNfa = new Nfa(this.alphabet);

        // add old states and transitions to new Nfa
        SortedMap<NfaState, NfaState> oldNfaStateMap = new TreeMap<NfaState, NfaState>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState startThis = oldNfaStateMap.get(this.startState);
        NfaState acceptThis = oldNfaStateMap.get(this.acceptState);

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
     */
    public Nfa zeroOrOne() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        Nfa newNfa = new Nfa(this.alphabet);

        // add old states and transitions to new Nfa
        SortedMap<NfaState, NfaState> oldNfaStateMap = new TreeMap<NfaState, NfaState>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState startThis = oldNfaStateMap.get(this.startState);
        NfaState acceptThis = oldNfaStateMap.get(this.acceptState);

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
     */
    public Nfa oneOrMore() {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        Nfa newNfa = new Nfa(this.alphabet);

        // add old states and transitions to new Nfa
        SortedMap<NfaState, NfaState> oldNfaStateMap = new TreeMap<NfaState, NfaState>();
        this.addStatesAndTransitionsTo(newNfa, oldNfaStateMap);

        NfaState startThis = oldNfaStateMap.get(this.startState);
        NfaState acceptThis = oldNfaStateMap.get(this.acceptState);

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
     */
    public Nfa simpleExponent(
            int n) {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        if (n < 0) {
            throw new InternalException("n must be greater or equal to zero");
        }

        Nfa newNfa = new Nfa(this.alphabet);

        // initialize "last state"
        NfaState lastState = newNfa.startState;

        for (int i = 0; i < n; i++) {

            // add old states and transitions
            SortedMap<NfaState, NfaState> oldNfaStateMap = new TreeMap<NfaState, NfaState>();
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
     */
    public Nfa rangeExponent(
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

        Nfa newNfa = new Nfa(this.alphabet);

        // initialize "last state"
        NfaState lastState = newNfa.startState;

        for (int i = 0; i < lowerBound; i++) {

            // add old states and transitions
            SortedMap<NfaState, NfaState> oldNfaStateMap = new TreeMap<NfaState, NfaState>();
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
            SortedMap<NfaState, NfaState> oldNfaStateMap = new TreeMap<NfaState, NfaState>();
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
     */
    public Nfa atLeastExponent(
            int n) {

        if (!this.isStable) {
            throw new InternalException("this Nfa is not stable yet");
        }

        if (n < 0) {
            throw new InternalException(
                    "lowerBound must be greater or equal to zero");
        }

        Nfa newNfa = new Nfa(this.alphabet);

        // initialize "last state"
        NfaState lastState = newNfa.startState;

        for (int i = 0; i < n; i++) {

            // add old states and transitions
            SortedMap<NfaState, NfaState> oldNfaStateMap = new TreeMap<NfaState, NfaState>();
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
            SortedMap<NfaState, NfaState> oldNfaStateMap = new TreeMap<NfaState, NfaState>();
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
     */
    public Dfa shortest() {

        return Dfa.shortest(this);
    }

    /**
     * Returns a <code>Dfa</code> instance which represents the substraction
     * of this <code>Nfa</code> with the provided one.
     */
    public Dfa subtract(
            Nfa nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        return Dfa.difference(this, nfa);
    }

    /**
     * Returns a <code>Dfa</code> instance which represents the intersection
     * of this <code>Nfa</code> with the provided one.
     */
    public Dfa intersect(
            Nfa nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        return Dfa.intersection(this, nfa);
    }

    /**
     * Returns a new <code>NfaCombineResult</code>, the result of the
     * combination of this <code>Nfa</code> with the provided one.
     */
    NfaCombineResult combineWith(
            Nfa nfa) {

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
        AlphabetMergeResult alphabetMergeResult = this.alphabet.mergeWith(nfa
                .getAlphabet());
        Nfa newNfa = new Nfa(alphabetMergeResult.getNewAlphabet());

        // add old states and transitions to new Nfa
        SortedMap<NfaState, NfaState> oldNfa1StateMap = new TreeMap<NfaState, NfaState>();
        this.addStatesAndTransitionsTo(newNfa, oldNfa1StateMap,
                alphabetMergeResult);

        SortedMap<NfaState, NfaState> oldNfa2StateMap = new TreeMap<NfaState, NfaState>();
        nfa.addStatesAndTransitionsTo(newNfa, oldNfa2StateMap,
                alphabetMergeResult);

        return new NfaCombineResult(newNfa, this, oldNfa1StateMap, nfa,
                oldNfa2StateMap);
    }

    /**
     * Adds the states and transitions of this <code>Nfa</code> instance to
     * the new one provided.
     */
    private void addStatesAndTransitionsTo(
            Nfa newNfa,
            SortedMap<NfaState, NfaState> nfaStateMap) {

        if (newNfa.alphabet != this.alphabet) {
            throw new InternalException(
                    "this Nfa and newNfa must share the same alphabet");
        }

        for (NfaState oldState : this.states) {

            NfaState newState = new NfaState(newNfa);
            nfaStateMap.put(oldState, newState);
        }

        for (NfaState oldState : this.states) {
            for (Map.Entry<Symbol, SortedSet<NfaState>> entry : oldState
                    .getTransitions().entrySet()) {

                Symbol symbol = entry.getKey();
                SortedSet<NfaState> oldTargets = entry.getValue();

                for (NfaState oldTarget : oldTargets) {

                    nfaStateMap.get(oldState).addTransition(symbol,
                            nfaStateMap.get(oldTarget));
                }
            }
        }
    }

    /**
     * Adds the states and transitions of this <code>Nfa</code> instance to
     * the new one provided.
     */
    private void addStatesAndTransitionsTo(
            Nfa newNfa,
            SortedMap<NfaState, NfaState> nfaStateMap,
            AlphabetMergeResult alphabetMergeResult) {

        for (NfaState oldState : this.states) {

            NfaState newState = new NfaState(newNfa);
            nfaStateMap.put(oldState, newState);
        }

        for (NfaState oldState : this.states) {
            for (Map.Entry<Symbol, SortedSet<NfaState>> entry : oldState
                    .getTransitions().entrySet()) {

                Symbol oldSymbol = entry.getKey();
                SortedSet<NfaState> oldTargets = entry.getValue();

                for (NfaState oldTarget : oldTargets) {

                    if (oldSymbol != null) {
                        for (Symbol newSymbol : alphabetMergeResult
                                .getNewSymbols(oldSymbol)) {

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
     */
    int getNextStateId() {

        if (this.isStable) {
            throw new InternalException("a stable Nfa may not be modified");
        }

        return this.states.size();
    }

    /**
     * Adds a state to this <code>Nfa</code>.
     */
    void addState(
            NfaState state) {

        if (this.isStable) {
            throw new InternalException("a stable Nfa may not be modified");
        }

        if (!this.states.add(state)) {
            throw new InternalException("state is already in state set");
        }
    }

}
