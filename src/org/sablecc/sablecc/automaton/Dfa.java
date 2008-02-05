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

/**
 * This class encapsulates a deterministic finite automaton (DFA).
 */
public class Dfa {

    /** The alphabet of this DFA. */
    private Alphabet alphabet;

    /** The states of this DFA. */
    private SortedSet<DfaState> states;

    /** The start state of this DFA. */
    private DfaState startState;

    /** The dead end state of this DFA. */
    private DfaState deadEndState;

    /** The accept states of this DFA. */
    private SortedSet<DfaState> acceptStates;

    /** The stability status of this DFA. */
    private boolean isStable;

    /**
     * The cached string representation of this DFA. It is <code>null</code>
     * when not yet computed.
     */
    private String toString;

    /**
     * Constructs an empty DFA. The created DFA is not stabilized.
     */
    private Dfa() {

    }

    /**
     * Constructs a DFA which is equivalent to the provided NFA.
     */
    public Dfa(
            Nfa nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        init(nfa.getAlphabet());
        StateMatcher stateMapper = computeDfaStates(nfa);

        // compute accept states
        NfaState nfaAccept = nfa.getAcceptState();
        for (DfaState dfaState : this.states) {
            if (stateMapper.match(dfaState, nfaAccept)) {
                this.acceptStates.add(dfaState);
            }
        }

        stabilize();
    }

    /**
     * Initializes the fields of this DFA.
     */
    private void init(
            Alphabet alphabet) {

        this.alphabet = alphabet;
        this.states = new TreeSet<DfaState>();
        this.acceptStates = new TreeSet<DfaState>();
        this.isStable = false;
    }

    /**
     * Matches the states of this <code>Dfa</code> with those of the provided
     * <code>Nfa</code>.
     * 
     * @param nfa
     *            the <code>Nfa</code>.
     * @return a state matcher instance.
     */
    private StateMatcher computeDfaStates(
            Nfa nfa) {

        StateMatcher stateMatcher = new StateMatcher(this, nfa);
        WorkSet<DfaState> workSet = new WorkSet<DfaState>();

        this.deadEndState = stateMatcher.getDfaState(new TreeSet<NfaState>());
        this.startState = stateMatcher.getDfaState(nfa.getStartState()
                .getEpsilonReach());
        workSet.add(this.startState);

        while (workSet.hasNext()) {
            DfaState sourceDfaState = workSet.next();

            // find direct destinations
            SortedMap<Symbol, SortedSet<NfaState>> directDestinationMap = new TreeMap<Symbol, SortedSet<NfaState>>();

            for (NfaState sourceNfaState : stateMatcher
                    .getNfaStates(sourceDfaState)) {

                for (Map.Entry<Symbol, SortedSet<NfaState>> entry : sourceNfaState
                        .getTransitions().entrySet()) {

                    Symbol symbol = entry.getKey();
                    SortedSet<NfaState> targets = entry.getValue();

                    if (symbol != null) {

                        SortedSet<NfaState> directDesinations = directDestinationMap
                                .get(symbol);

                        if (directDesinations == null) {

                            directDesinations = new TreeSet<NfaState>();
                            directDestinationMap.put(symbol, directDesinations);
                        }

                        directDesinations.addAll(targets);
                    }
                }
            }

            // add transitions
            for (Map.Entry<Symbol, SortedSet<NfaState>> entry : directDestinationMap
                    .entrySet()) {

                Symbol symbol = entry.getKey();
                SortedSet<NfaState> directDestinations = entry.getValue();

                SortedSet<NfaState> epsilonClosure = new TreeSet<NfaState>();

                for (NfaState nfaState : directDestinations) {
                    epsilonClosure.addAll(nfaState.getEpsilonReach());
                }

                DfaState destinationDfaState = stateMatcher
                        .getDfaState(epsilonClosure);

                sourceDfaState.addTransition(symbol, destinationDfaState);

                workSet.add(destinationDfaState);
            }
        }

        return stateMatcher;
    }

    /**
     * Returns the alphabet of this <code>Dfa</code>.
     * 
     * @return the alphabet.
     */
    public Alphabet getAlphabet() {

        return this.alphabet;
    }

    /**
     * Returns the states of this <code>Dfa</code>.
     * 
     * @return the set of states.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<DfaState> getStates() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.states;
    }

    /**
     * Returns the states of this <code>Dfa</code> if it is unstable.
     * 
     * @return the set of states.
     */
    SortedSet<DfaState> getUnstableStates() {

        return this.states;
    }

    /**
     * Returns the starting state of this <code>Dfa</code>.
     * 
     * @return the starting state.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public DfaState getStartState() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.startState;
    }

    /**
     * Returns the dead end state of this <code>Dfa</code>.
     * 
     * @return the dead end state.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public DfaState getDeadEndState() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.deadEndState;
    }

    /**
     * Returns the dead end state of this <code>Dfa</code>if it is unstable.
     * 
     * @return the dead end state.
     */
    DfaState getUnstableDeadEndState() {

        return this.deadEndState;
    }

    /**
     * Returns the acceptation state of this <code>Dfa</code>.
     * 
     * @return the acceptation state.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<DfaState> getAcceptStates() {

        if (!this.isStable) {
            throw new InternalException("this DFA is not stable yet");
        }

        return this.acceptStates;
    }

    /**
     * Returns the string representation of this <code>Dfa</code>.
     * 
     * @return the string representation.
     * @throws InternalException
     *             if this instance is not stable.
     */
    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this DFA is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("DFA:{");

            for (DfaState state : this.states) {
                sb.append(LINE_SEPARATOR);
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
                for (Map.Entry<Symbol, DfaState> entry : state.getTransitions()
                        .entrySet()) {
                    Symbol symbol = entry.getKey();
                    DfaState target = entry.getValue();

                    sb.append(LINE_SEPARATOR);
                    sb.append("        ");
                    sb.append(symbol);
                    sb.append(" -> ");
                    sb.append(target);
                }
            }

            sb.append(LINE_SEPARATOR);
            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Stabilizes this <code>Dfa</code> by stabilizing each of its states.
     * 
     * @throws InternalException
     *             if this <code>Dfa</code> instance is already stable.
     */
    private void stabilize() {

        if (this.isStable) {
            throw new InternalException("this DFA is already stable");
        }

        removeUnreachableStates();

        for (DfaState state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSortedSet(this.states);
        this.acceptStates = Collections
                .unmodifiableSortedSet(this.acceptStates);

        this.isStable = true;
    }

    /**
     * Removes unreachable states from this <code>Dfa</code> instance.
     */
    private void removeUnreachableStates() {

        SortedSet<DfaState> reachableStates = new TreeSet<DfaState>();

        WorkSet<DfaState> workSet = new WorkSet<DfaState>();
        workSet.add(this.startState);

        while (workSet.hasNext()) {
            DfaState state = workSet.next();
            reachableStates.add(state);

            for (DfaState target : state.getUnstableTransitions().values()) {
                workSet.add(target);
            }
        }

        reachableStates.add(this.deadEndState);

        this.states = reachableStates;
    }

    /**
     * Calculates and returns the shortest <code>Dfa</code> corresponding to
     * the provided <code>Nfa</code>.
     * 
     * @param nfa
     *            the <code>Nfa</code>.
     * @return the calculated <code>Dfa</code>.
     * @throws InternalException
     *             if the provided <code>Nfa</code> is <code>null</code>.
     */
    static Dfa shortest(
            Nfa nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        Dfa dfa = new Dfa();

        dfa.init(nfa.getAlphabet());
        StateMatcher stateMapper = dfa.computeDfaStates(nfa);

        // compute accept states
        NfaState nfaAccept = nfa.getAcceptState();
        for (DfaState dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfaAccept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        // remove transitions out of accept states
        for (DfaState state : dfa.acceptStates) {
            state.removeTransitions();
        }

        dfa.stabilize();
        return dfa;
    }

    /**
     * Calculates the difference between the two provided <code>Nfa</code> and
     * returns the corresponding <code>Dfa</code>.
     * 
     * @param nfa1
     *            the first <code>Nfa</code>.
     * @param nfa2
     *            the second <code>Nfa</code>.
     * @return the calculated <code>Dfa</code>.
     * @throws InternalException
     *             if one of the provided <code>Nfa</code> is
     *             <code>null</code>.
     */
    static Dfa difference(
            Nfa nfa1,
            Nfa nfa2) {

        if (nfa1 == null) {
            throw new InternalException("nfa1 may not be null");
        }

        if (nfa2 == null) {
            throw new InternalException("nfa2 may not be null");
        }

        NfaCombineResult nfaCombineResult = nfa1.combineWith(nfa2);
        Nfa newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transitions from start to oldStart
        newNfa.getUnstableStartState().addTransition(null,
                nfaCombineResult.getNewNfa1StartState());
        newNfa.getUnstableStartState().addTransition(null,
                nfaCombineResult.getNewNfa2StartState());

        newNfa.stabilize();

        Dfa dfa = new Dfa();

        dfa.init(newNfa.getAlphabet());
        StateMatcher stateMapper = dfa.computeDfaStates(newNfa);

        // compute accept states
        NfaState nfa1Accept = nfaCombineResult.getNewNfa1AcceptState();
        NfaState nfa2Accept = nfaCombineResult.getNewNfa2AcceptState();
        for (DfaState dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfa1Accept)
                    && !stateMapper.match(dfaState, nfa2Accept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        dfa.stabilize();
        return dfa;
    }

    /**
     * Calculates the intersection between the two provided <code>Nfa</code>
     * and returns the corresponding <code>Dfa</code>.
     * 
     * @param nfa1
     *            the first <code>Nfa</code>.
     * @param nfa2
     *            the second <code>Nfa</code>.
     * @return the calculated <code>Dfa</code>.
     * @throws InternalException
     *             if one of the provided <code>Nfa</code> is
     *             <code>null</code>.
     */
    static Dfa intersection(
            Nfa nfa1,
            Nfa nfa2) {

        if (nfa1 == null) {
            throw new InternalException("nfa1 may not be null");
        }

        if (nfa2 == null) {
            throw new InternalException("nfa2 may not be null");
        }

        NfaCombineResult nfaCombineResult = nfa1.combineWith(nfa2);
        Nfa newNfa = nfaCombineResult.getNewNfa();

        // add epsilon transitions from start to oldStart
        newNfa.getUnstableStartState().addTransition(null,
                nfaCombineResult.getNewNfa1StartState());
        newNfa.getUnstableStartState().addTransition(null,
                nfaCombineResult.getNewNfa2StartState());

        newNfa.stabilize();

        Dfa dfa = new Dfa();

        dfa.init(newNfa.getAlphabet());
        StateMatcher stateMapper = dfa.computeDfaStates(newNfa);

        // compute accept states
        NfaState nfa1Accept = nfaCombineResult.getNewNfa1AcceptState();
        NfaState nfa2Accept = nfaCombineResult.getNewNfa2AcceptState();
        for (DfaState dfaState : dfa.states) {
            if (stateMapper.match(dfaState, nfa1Accept)
                    && stateMapper.match(dfaState, nfa2Accept)) {
                dfa.acceptStates.add(dfaState);
            }
        }

        dfa.stabilize();
        return dfa;
    }

    /**
     * Returns the ID for the following state.
     * 
     * @return the ID of the next state.
     * @throws InternalException
     *             if this <code>Dfa</code> instance is stable.
     */
    int getNextStateId() {

        if (this.isStable) {
            throw new InternalException("a stable DFA may not be modified");
        }

        return this.states.size();
    }

    /**
     * Adds a state to this <code>Dfa</code>.
     * 
     * @param state
     *            the state to add.
     * @throws InternalException
     *             if this <code>Dfa</code> is stable or or if the state is
     *             already in the state set.
     */
    void addState(
            DfaState state) {

        if (this.isStable) {
            throw new InternalException("a stable DFA may not be modified");
        }

        if (!this.states.add(state)) {
            throw new InternalException("state is already in state set");
        }
    }
}
