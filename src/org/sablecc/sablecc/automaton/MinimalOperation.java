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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.util.*;

class MinimalOperation {

    private Set<Group> groups = new LinkedHashSet<Group>();

    private Group deadEndGroup;

    private WorkSet<Group> workSet = new WorkSet<Group>();

    private boolean isModified;

    private SortedMap<State, Group> groupMap = new TreeMap<State, Group>();

    private Alphabet newAlphabet;

    private SortedMap<Symbol, Symbol> oldSymbolToNewSymbolMap = new TreeMap<Symbol, Symbol>();

    private SortedMap<Symbol, SortedSet<Symbol>> newSymbolToOldSymbolsMap = new TreeMap<Symbol, SortedSet<Symbol>>();

    private SortedSet<RichSymbol> newRichSymbols = new TreeSet<RichSymbol>();

    private Automaton newAutomaton;

    private SortedMap<State, Group> newStateToGroupMap = new TreeMap<State, Group>();

    private Map<Group, State> groupToNewStateMap = new HashMap<Group, State>();

    private SortedSet<State> newStates = new TreeSet<State>();

    MinimalOperation(
            Automaton oldAutomaton) {

        if (oldAutomaton == null) {
            throw new InternalException("oldAutomaton may not be null");
        }

        if (!oldAutomaton.hasMarkers()) {
            oldAutomaton = oldAutomaton.deterministic();
        }

        computeGroups(oldAutomaton);
        computeMinimalAlphabet(oldAutomaton);
        computeNewAutomaton(oldAutomaton);
    }

    private void computeGroups(
            Automaton oldAutomaton) {

        Set<Pair<Marker, SortedSet<Acceptation>>> specificities = new LinkedHashSet<Pair<Marker, SortedSet<Acceptation>>>();
        Map<Pair<Marker, SortedSet<Acceptation>>, SortedSet<State>> specificityMap = new HashMap<Pair<Marker, SortedSet<Acceptation>>, SortedSet<State>>();

        Pair<Marker, SortedSet<Acceptation>> deadEndSpecificity = new Pair<Marker, SortedSet<Acceptation>>(
                null, new TreeSet<Acceptation>());

        specificities.add(deadEndSpecificity);
        specificityMap.put(deadEndSpecificity, new TreeSet<State>());

        for (State state : oldAutomaton.getStates()) {
            Marker marker = state.getMarker();
            SortedSet<Acceptation> acceptations = state.getAcceptations();

            Pair<Marker, SortedSet<Acceptation>> specificity = new Pair<Marker, SortedSet<Acceptation>>(
                    marker, acceptations);

            SortedSet<State> specificityStates = specificityMap
                    .get(specificity);

            if (specificityStates == null) {
                specificityStates = new TreeSet<State>();

                specificities.add(specificity);
                specificityMap.put(specificity, specificityStates);
            }

            specificityStates.add(state);
        }

        for (Pair<Marker, SortedSet<Acceptation>> specificity : specificities) {

            SortedSet<State> states = specificityMap.get(specificity);

            if (specificity.equals(deadEndSpecificity)) {
                new Group(this, states, true);
            }
            else {
                new Group(this, states, false);
            }
        }

        while (this.isModified) {
            this.isModified = false;

            this.workSet = new WorkSet<Group>();
            for (Group group : this.groups) {
                this.workSet.add(group);
            }

            while (this.workSet.hasNext()) {
                Group group = this.workSet.next();
                group.splitIfNecessary(oldAutomaton.getAlphabet());
            }
        }
    }

    private void computeMinimalAlphabet(
            Automaton oldAutomaton) {

        // identify, for each symbol, all group pairs that are joined by a
        // transition on this symbol

        SortedMap<RichSymbol, Set<Pair<Group, Group>>> richSymbolToGroupPairSetMap = new TreeMap<RichSymbol, Set<Pair<Group, Group>>>();

        for (Group sourceGroup : this.groups) {
            for (Symbol symbol : oldAutomaton.getAlphabet().getSymbols()) {
                SortedSet<RichSymbol> richSymbols = new TreeSet<RichSymbol>();
                richSymbols.add(symbol.getNormalRichSymbol());
                richSymbols.add(symbol.getLookaheadRichSymbol());
                for (RichSymbol richSymbol : richSymbols) {
                    Group targetGroup;

                    if (sourceGroup.equals(this.deadEndGroup)) {
                        targetGroup = this.deadEndGroup;
                    }
                    else {
                        State targetState = sourceGroup.getStates().first()
                                .getSingleTarget(richSymbol);
                        if (targetState == null) {
                            targetGroup = this.deadEndGroup;
                        }
                        else {
                            targetGroup = getGroup(targetState);
                        }
                    }

                    Set<Pair<Group, Group>> groupPairSet = richSymbolToGroupPairSetMap
                            .get(richSymbol);

                    if (groupPairSet == null) {
                        groupPairSet = new LinkedHashSet<Pair<Group, Group>>();
                        richSymbolToGroupPairSetMap.put(richSymbol,
                                groupPairSet);
                    }

                    groupPairSet.add(new Pair<Group, Group>(sourceGroup,
                            targetGroup));
                }
            }
        }

        Set<Pair<Set<Pair<Group, Group>>, Set<Pair<Group, Group>>>> groupPairSetPairs = new LinkedHashSet<Pair<Set<Pair<Group, Group>>, Set<Pair<Group, Group>>>>();
        Map<Pair<Set<Pair<Group, Group>>, Set<Pair<Group, Group>>>, SortedSet<Symbol>> groupPairSetPairToSymbolsMap = new HashMap<Pair<Set<Pair<Group, Group>>, Set<Pair<Group, Group>>>, SortedSet<Symbol>>();

        for (Symbol symbol : oldAutomaton.getAlphabet().getSymbols()) {
            Set<Pair<Group, Group>> normalSet = richSymbolToGroupPairSetMap
                    .get(symbol.getNormalRichSymbol());
            Set<Pair<Group, Group>> lookaheadSet = richSymbolToGroupPairSetMap
                    .get(symbol.getLookaheadRichSymbol());

            Pair<Set<Pair<Group, Group>>, Set<Pair<Group, Group>>> groupPairSetPair = new Pair<Set<Pair<Group, Group>>, Set<Pair<Group, Group>>>(
                    normalSet, lookaheadSet);

            SortedSet<Symbol> symbols = groupPairSetPairToSymbolsMap
                    .get(groupPairSetPair);

            if (symbols == null) {
                symbols = new TreeSet<Symbol>();

                groupPairSetPairs.add(groupPairSetPair);
                groupPairSetPairToSymbolsMap.put(groupPairSetPair, symbols);
            }

            symbols.add(symbol);
        }

        // merge symbols and create the new alphabet
        SortedSet<Symbol> newSymbols = new TreeSet<Symbol>();

        for (Pair<Set<Pair<Group, Group>>, Set<Pair<Group, Group>>> pair : groupPairSetPairs) {
            SortedSet<Symbol> symbols = groupPairSetPairToSymbolsMap.get(pair);

            Set<Set<Pair<Group, Group>>> set = new LinkedHashSet<Set<Pair<Group, Group>>>();
            set.add(pair.getLeft());
            set.add(pair.getRight());

            boolean useless = true;

            for (Set<Pair<Group, Group>> groupPairSet : set) {
                for (Pair<Group, Group> groupPair : groupPairSet) {
                    if (!groupPair.getRight().equals(this.deadEndGroup)) {
                        useless = false;
                    }
                }
            }

            if (useless) {
                continue;
            }

            Symbol newSymbol = Symbol.merge(symbols);
            newSymbols.add(newSymbol);

            this.newSymbolToOldSymbolsMap.put(newSymbol, symbols);
            for (Symbol oldSymbol : symbols) {
                this.oldSymbolToNewSymbolMap.put(oldSymbol, newSymbol);
            }
        }

        this.newAlphabet = new Alphabet(newSymbols);

        this.newRichSymbols.add(RichSymbol.END);
        for (Symbol newSymbol : this.newAlphabet.getSymbols()) {
            this.newRichSymbols.add(newSymbol.getNormalRichSymbol());
            this.newRichSymbols.add(newSymbol.getLookaheadRichSymbol());
        }
    }

    private void computeNewAutomaton(
            Automaton oldAutomaton) {

        this.newAutomaton = new Automaton(this.newAlphabet);

        WorkSet<State> workSet = new WorkSet<State>();

        {
            Group startGroup = getGroup(oldAutomaton.getStartState());
            State newStartState = this.newAutomaton.getStartState();

            this.newStates.add(newStartState);
            this.newStateToGroupMap.put(newStartState, startGroup);
            this.groupToNewStateMap.put(startGroup, newStartState);

            if (!startGroup.equals(this.deadEndGroup)) {
                workSet.add(newStartState);
            }
        }

        while (workSet.hasNext()) {
            State newSourceState = workSet.next();
            Group sourceGroup = this.newStateToGroupMap.get(newSourceState);

            for (RichSymbol newRichSymbol : this.newRichSymbols) {
                RichSymbol oldRichSymbol = getOldRichSymbol(newRichSymbol);
                State oldTargetState = sourceGroup.getStates().first()
                        .getSingleTarget(oldRichSymbol);

                if (oldTargetState == null) {
                    continue;
                }

                Group targetGroup = getGroup(oldTargetState);

                if (targetGroup.equals(this.deadEndGroup)) {
                    continue;
                }

                State newTargetState = this.groupToNewStateMap.get(targetGroup);

                if (newTargetState == null) {
                    newTargetState = new State(this.newAutomaton);

                    this.newStates.add(newTargetState);
                    this.newStateToGroupMap.put(newTargetState, targetGroup);
                    this.groupToNewStateMap.put(targetGroup, newTargetState);

                    workSet.add(newTargetState);
                }

                newSourceState.addTransition(newRichSymbol, newTargetState);
            }
        }

        for (Marker marker : oldAutomaton.getMarkers()) {
            this.newAutomaton.addMarker(marker);
        }

        for (Acceptation acceptation : oldAutomaton.getAcceptations()) {
            this.newAutomaton.addAcceptation(acceptation);
        }

        for (State newState : this.newStates) {
            State oldState = this.newStateToGroupMap.get(newState).getStates()
                    .first();

            if (oldState.getMarker() != null) {
                newState.setMarker(oldState.getMarker());
            }

            for (Acceptation acceptation : oldState.getAcceptations()) {
                newState.addAcceptation(acceptation);
            }
        }

        this.newAutomaton.stabilize();
    }

    private RichSymbol getOldRichSymbol(
            RichSymbol newRichSymbol) {

        if (newRichSymbol == null) {
            throw new InternalException("newRichSymbol may not be null");
        }

        if (newRichSymbol == RichSymbol.END) {
            return RichSymbol.END;
        }

        Symbol newSymbol = newRichSymbol.getSymbol();
        Symbol oldSymbol = this.newSymbolToOldSymbolsMap.get(newSymbol).first();

        if (newRichSymbol.isLookahead()) {
            return oldSymbol.getLookaheadRichSymbol();
        }

        return oldSymbol.getNormalRichSymbol();
    }

    void setGroup(
            State state,
            Group group) {

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        if (group.getMinimalAutomatonBuilder() != this) {
            throw new InternalException("invalid group");
        }

        this.groupMap.put(state, group);
    }

    Group getGroup(
            State state) {

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        Group group = this.groupMap.get(state);

        if (group == null) {
            throw new InternalException("corruption detected");
        }

        return group;
    }

    void addGroup(
            Group group) {

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        if (group.getMinimalAutomatonBuilder() != this) {
            throw new InternalException("invalid group");
        }

        if (this.groups.contains(group)) {
            throw new InternalException("group cannot be added twice");
        }

        this.groups.add(group);
        this.workSet.add(group);
        this.isModified = true;
    }

    void removeGroup(
            Group group) {

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        if (group.getMinimalAutomatonBuilder() != this) {
            throw new InternalException("invalid group");
        }

        if (!this.groups.contains(group)) {
            throw new InternalException("group cannot be removed");
        }

        this.groups.remove(group);
    }

    void setDeadEnd(
            Group group) {

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        if (group.getMinimalAutomatonBuilder() != this) {
            throw new InternalException("invalid group");
        }

        this.deadEndGroup = group;
    }

    Group getDeadEndGroup() {

        return this.deadEndGroup;
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }
}
