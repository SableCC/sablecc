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

public class MinimalDfa<T extends Comparable<? super T>> {

    private static final String lineSeparator = System
            .getProperty("line.separator");

    private Alphabet<T> alphabet;

    private SortedSet<MinimalDfaState<T>> states;

    private MinimalDfaState<T> startState;

    private MinimalDfaState<T> deadEndState;

    private SortedSet<MinimalDfaState<T>> acceptStates;

    private boolean isStable;

    private String toString;

    public MinimalDfa(
            Dfa<T> dfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        // partition states groups
        Partition<T> partition = new Partition<T>(dfa);

        // find dead-end and start groups
        Group<T> deadEndGroup = partition.getElement(dfa.getDeadEndState())
                .getGroup();
        Group<T> startGroup = partition.getElement(dfa.getStartState())
                .getGroup();

        // create minimal alphabet

        // identify, for each symbol, all group pairs that are joined by a
        // transition on this symbol (ignoring transitions from or to the
        // dead-end group)

        SortedMap<Symbol<T>, Set<GroupPair<T>>> symbolToPairSetMap = new TreeMap<Symbol<T>, Set<GroupPair<T>>>();

        for (Group<T> sourceGroup : partition.getGroups()) {

            if (sourceGroup == deadEndGroup) {
                continue;
            }

            for (Element<T> element : sourceGroup.getElements()) {

                for (Map.Entry<Symbol<T>, DfaState<T>> entry : element
                        .getState().getTransitions().entrySet()) {

                    Symbol<T> symbol = entry.getKey();
                    DfaState<T> dfaTarget = entry.getValue();

                    Group<T> targetGroup = partition.getElement(dfaTarget)
                            .getGroup();

                    if (targetGroup == deadEndGroup) {
                        continue;
                    }

                    GroupPair<T> pair = new GroupPair<T>(sourceGroup,
                            targetGroup);

                    Set<GroupPair<T>> pairSet = symbolToPairSetMap.get(symbol);

                    if (pairSet == null) {
                        pairSet = new LinkedHashSet<GroupPair<T>>();
                        symbolToPairSetMap.put(symbol, pairSet);
                    }

                    pairSet.add(pair);
                }
            }
        }

        // for each pair set, identify symbols that map to it
        Map<Set<GroupPair<T>>, SortedSet<Symbol<T>>> pairSetToSymbolSetMap = new HashMap<Set<GroupPair<T>>, SortedSet<Symbol<T>>>();
        // build the set of "pair set" (i.e. the key set of
        // pairSetToSymbolSetMap)
        Set<Set<GroupPair<T>>> setOfPairSet = new LinkedHashSet<Set<GroupPair<T>>>();

        for (Map.Entry<Symbol<T>, Set<GroupPair<T>>> entry : symbolToPairSetMap
                .entrySet()) {

            Symbol<T> symbol = entry.getKey();
            Set<GroupPair<T>> pairSet = entry.getValue();

            SortedSet<Symbol<T>> symbolSet = pairSetToSymbolSetMap.get(pairSet);

            if (symbolSet == null) {

                symbolSet = new TreeSet<Symbol<T>>();
                pairSetToSymbolSetMap.put(pairSet, symbolSet);
            }

            symbolSet.add(symbol);
            setOfPairSet.add(pairSet);
        }

        // merge symbols and create the new alphabet
        SortedSet<Symbol<T>> newSymbols = new TreeSet<Symbol<T>>();

        for (Set<GroupPair<T>> pairSet : setOfPairSet) {

            SortedSet<Symbol<T>> symbolSet = pairSetToSymbolSetMap.get(pairSet);

            Symbol<T> newSymbol = Symbol.merge(symbolSet);

            newSymbols.add(newSymbol);

            for (GroupPair<T> pair : pairSet) {
                pair.getGroup1().addTransition(newSymbol, pair.getGroup2());
            }
        }

        this.alphabet = new Alphabet<T>(newSymbols);

        // initialize fields
        this.states = new TreeSet<MinimalDfaState<T>>();
        this.acceptStates = new TreeSet<MinimalDfaState<T>>();
        this.isStable = false;

        // create states and transitions in canonical order

        deadEndGroup.setState(new MinimalDfaState<T>(this));

        // sometimes, the start and dead-end groups are the same group
        if (startGroup.getState() == null) {
            startGroup.setState(new MinimalDfaState<T>(this));
        }

        WorkSet<Group<T>> workSet = new WorkSet<Group<T>>();
        workSet.add(startGroup);

        while (workSet.hasNext()) {

            Group<T> sourceGroup = workSet.next();

            for (Map.Entry<Symbol<T>, Group<T>> entry : sourceGroup
                    .getTransitions().entrySet()) {

                Symbol<T> symbol = entry.getKey();
                Group<T> destinationGroup = entry.getValue();

                if (destinationGroup.getState() == null) {
                    destinationGroup.setState(new MinimalDfaState<T>(this));
                }

                sourceGroup.getState().addTransition(symbol,
                        destinationGroup.getState());
                workSet.add(destinationGroup);
            }
        }

        // set start, dead-end, and accept states
        this.startState = startGroup.getState();
        this.deadEndState = deadEndGroup.getState();

        for (DfaState<T> dfaState : dfa.getAcceptStates()) {
            this.acceptStates.add(partition.getElement(dfaState).getGroup()
                    .getState());
        }

        stabilize();
    }

    private void stabilize() {

        for (MinimalDfaState<T> state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSortedSet(this.states);
        this.acceptStates = Collections
                .unmodifiableSortedSet(this.acceptStates);
        this.isStable = true;
    }

    public Alphabet<T> getAlphabet() {

        return this.alphabet;
    }

    public SortedSet<MinimalDfaState<T>> getStates() {

        if (!this.isStable) {
            throw new InternalException("this MinimalDFA is not stable yet");
        }

        return this.states;
    }

    public MinimalDfaState<T> getStartState() {

        if (!this.isStable) {
            throw new InternalException("this MinimalDFA is not stable yet");
        }

        return this.startState;
    }

    public MinimalDfaState<T> getDeadEndState() {

        if (!this.isStable) {
            throw new InternalException("this MinimalDFA is not stable yet");
        }

        return this.deadEndState;
    }

    MinimalDfaState<T> getUnstableDeadEndState() {

        return this.deadEndState;
    }

    public SortedSet<MinimalDfaState<T>> getAcceptStates() {

        if (!this.isStable) {
            throw new InternalException("this MinimalDFA is not stable yet");
        }

        return this.acceptStates;
    }

    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this MinimalDFA is not stable yet");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("MinimalDFA:{");

            for (MinimalDfaState<T> state : this.states) {
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
                for (Map.Entry<Symbol<T>, MinimalDfaState<T>> entry : state
                        .getTransitions().entrySet()) {
                    Symbol<T> symbol = entry.getKey();
                    MinimalDfaState<T> target = entry.getValue();

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

    int getNextStateId() {

        if (this.isStable) {
            throw new InternalException(
                    "a stable MinimalDFA may not be modified");
        }

        return this.states.size();
    }

    void addState(
            MinimalDfaState<T> state) {

        if (this.isStable) {
            throw new InternalException(
                    "a stable MinimalDFA may not be modified");
        }

        if (!this.states.add(state)) {
            throw new InternalException("state is already in state set");
        }
    }
}
