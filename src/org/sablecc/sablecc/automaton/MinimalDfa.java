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

import static org.sablecc.sablecc.util.UsefulStaticImports.lineSeparator;

import java.util.Collections;
import java.util.HashMap;
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

/**
 * A minimal deterministic finite automaton (or MinimalDfa) is a state machine
 * which is minimalist and equivalent to a corresponding <code>Dfa</code>.
 */
public class MinimalDfa {

    /** The alphabet for this <code>MinimalDfa</code>. */
    private Alphabet alphabet;

    /** The states of this <code>MinimalDfa</code>. */
    private SortedSet<MinimalDfaState> states;

    /** The starting state of this <code>MinimalDfa</code>. */
    private MinimalDfaState startState;

    /** The dead end state of this <code>MinimalDfa</code>. */
    private MinimalDfaState deadEndState;

    /** The acceptation states of this <code>MinimalDfa</code>. */
    private SortedSet<MinimalDfaState> acceptStates;

    /** A stability status for this <code>MinimalDfa</code>. */
    private boolean isStable;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /**
     * Constructs a <code>MinimalDfa</code> equivalent to the provided
     * <code>Dfa</code>.
     * 
     * @param dfa
     *            the <code>Dfa</code>.
     * @throws InternalException
     *             if the <code>Dfa</code> is <code>null</code>.
     */
    public MinimalDfa(
            Dfa dfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        // partition states groups
        Partition partition = new Partition(dfa);

        // find dead-end and start groups
        Group deadEndGroup = partition.getElement(dfa.getDeadEndState())
                .getGroup();
        Group startGroup = partition.getElement(dfa.getStartState()).getGroup();

        // create minimal alphabet

        // identify, for each symbol, all group pairs that are joined by a
        // transition on this symbol (ignoring transitions from or to the
        // dead-end group)

        SortedMap<Symbol, Set<GroupPair>> symbolToPairSetMap = new TreeMap<Symbol, Set<GroupPair>>();

        for (Group sourceGroup : partition.getGroups()) {

            if (sourceGroup == deadEndGroup) {
                continue;
            }

            for (Element element : sourceGroup.getElements()) {

                for (Map.Entry<Symbol, DfaState> entry : element.getState()
                        .getTransitions().entrySet()) {

                    Symbol symbol = entry.getKey();
                    DfaState dfaTarget = entry.getValue();

                    Group targetGroup = partition.getElement(dfaTarget)
                            .getGroup();

                    if (targetGroup == deadEndGroup) {
                        continue;
                    }

                    GroupPair pair = new GroupPair(sourceGroup, targetGroup);

                    Set<GroupPair> pairSet = symbolToPairSetMap.get(symbol);

                    if (pairSet == null) {
                        pairSet = new LinkedHashSet<GroupPair>();
                        symbolToPairSetMap.put(symbol, pairSet);
                    }

                    pairSet.add(pair);
                }
            }
        }

        // for each pair set, identify symbols that map to it
        Map<Set<GroupPair>, SortedSet<Symbol>> pairSetToSymbolSetMap = new HashMap<Set<GroupPair>, SortedSet<Symbol>>();
        // build the set of "pair set" (i.e. the key set of
        // pairSetToSymbolSetMap)
        Set<Set<GroupPair>> setOfPairSet = new LinkedHashSet<Set<GroupPair>>();

        for (Map.Entry<Symbol, Set<GroupPair>> entry : symbolToPairSetMap
                .entrySet()) {

            Symbol symbol = entry.getKey();
            Set<GroupPair> pairSet = entry.getValue();

            SortedSet<Symbol> symbolSet = pairSetToSymbolSetMap.get(pairSet);

            if (symbolSet == null) {

                symbolSet = new TreeSet<Symbol>();
                pairSetToSymbolSetMap.put(pairSet, symbolSet);
            }

            symbolSet.add(symbol);
            setOfPairSet.add(pairSet);
        }

        // merge symbols and create the new alphabet
        SortedSet<Symbol> newSymbols = new TreeSet<Symbol>();

        for (Set<GroupPair> pairSet : setOfPairSet) {

            SortedSet<Symbol> symbolSet = pairSetToSymbolSetMap.get(pairSet);

            Symbol newSymbol = Symbol.merge(symbolSet);

            newSymbols.add(newSymbol);

            for (GroupPair pair : pairSet) {
                pair.getGroup1().addTransition(newSymbol, pair.getGroup2());
            }
        }

        this.alphabet = new Alphabet(newSymbols);

        // initialize fields
        this.states = new TreeSet<MinimalDfaState>();
        this.acceptStates = new TreeSet<MinimalDfaState>();
        this.isStable = false;

        // create states and transitions in canonical order

        deadEndGroup.setState(new MinimalDfaState(this));

        // sometimes, the start and dead-end groups are the same group
        if (startGroup.getState() == null) {
            startGroup.setState(new MinimalDfaState(this));
        }

        WorkSet<Group> workSet = new WorkSet<Group>();
        workSet.add(startGroup);

        while (workSet.hasNext()) {

            Group sourceGroup = workSet.next();

            for (Map.Entry<Symbol, Group> entry : sourceGroup.getTransitions()
                    .entrySet()) {

                Symbol symbol = entry.getKey();
                Group destinationGroup = entry.getValue();

                if (destinationGroup.getState() == null) {
                    destinationGroup.setState(new MinimalDfaState(this));
                }

                sourceGroup.getState().addTransition(symbol,
                        destinationGroup.getState());
                workSet.add(destinationGroup);
            }
        }

        // set start, dead-end, and accept states
        this.startState = startGroup.getState();
        this.deadEndState = deadEndGroup.getState();

        for (DfaState dfaState : dfa.getAcceptStates()) {
            this.acceptStates.add(partition.getElement(dfaState).getGroup()
                    .getState());
        }

        stabilize();
    }

    /**
     * Stabilizes this <code>MinimalDfa</code> by stabilizing each of its
     * states.
     */
    private void stabilize() {

        for (MinimalDfaState state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSortedSet(this.states);
        this.acceptStates = Collections
                .unmodifiableSortedSet(this.acceptStates);
        this.isStable = true;
    }

    /**
     * Returns the alphabet of this <code>MinimalDfa</code>.
     * 
     * @return the alphabet.
     */
    public Alphabet getAlphabet() {

        return this.alphabet;
    }

    /**
     * Returns the states of this <code>MinimalDfa</code>.
     * 
     * @return the set of states.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<MinimalDfaState> getStates() {

        if (!this.isStable) {
            throw new InternalException("this MinimalDFA is not stable yet");
        }

        return this.states;
    }

    /**
     * Returns the starting state of this <code>MinimalDfa</code>.
     * 
     * @return the starting state.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public MinimalDfaState getStartState() {

        if (!this.isStable) {
            throw new InternalException("this MinimalDFA is not stable yet");
        }

        return this.startState;
    }

    /**
     * Returns the dead end state of this <code>MinimalDfa</code>.
     * 
     * @return the dead end state.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public MinimalDfaState getDeadEndState() {

        if (!this.isStable) {
            throw new InternalException("this MinimalDFA is not stable yet");
        }

        return this.deadEndState;
    }

    /**
     * Returns the dead end state of this <code>MinimalDfa</code>if it is
     * unstable.
     * 
     * @return the dead end state.
     */
    MinimalDfaState getUnstableDeadEndState() {

        return this.deadEndState;
    }

    /**
     * Returns the acceptation state of this <code>MinimalDfa</code>.
     * 
     * @return the acceptation state.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<MinimalDfaState> getAcceptStates() {

        if (!this.isStable) {
            throw new InternalException("this MinimalDFA is not stable yet");
        }

        return this.acceptStates;
    }

    /**
     * Returns the string representation of this <code>MinimalDfa</code>.
     * 
     * @return the string representation.
     * @throws InternalException
     *             if this instance is not stable.
     */
    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this MinimalDFA is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("MinimalDFA:{");

            for (MinimalDfaState state : this.states) {
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
                for (Map.Entry<Symbol, MinimalDfaState> entry : state
                        .getTransitions().entrySet()) {
                    Symbol symbol = entry.getKey();
                    MinimalDfaState target = entry.getValue();

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

    /**
     * Returns the ID for the following state.
     * 
     * @return the ID of the next state.
     * @throws InternalException
     *             if this <code>MinimalDfa</code> instance is stable.
     */
    int getNextStateId() {

        if (this.isStable) {
            throw new InternalException(
                    "a stable MinimalDFA may not be modified");
        }

        return this.states.size();
    }

    /**
     * Adds a state to this <code>MinimalDfa</code>.
     * 
     * @param state
     *            the state to add.
     * @throws InternalException
     *             if this <code>MinimalDfa</code> is stable or or if the
     *             state is already in the state set.
     */
    void addState(
            MinimalDfaState state) {

        if (this.isStable) {
            throw new InternalException(
                    "a stable MinimalDFA may not be modified");
        }

        if (!this.states.add(state)) {
            throw new InternalException("state is already in state set");
        }
    }
}
