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

class DeterministicOperation {

    private Automaton newAutomaton;

    private SortedMap<State, SortedSet<State>> stateMap = new TreeMap<State, SortedSet<State>>();

    private Map<SortedSet<State>, State> oldStatesToNewStateMap = new HashMap<SortedSet<State>, State>();

    private Map<State, SortedSet<State>> newStateToOldStatesMap = new HashMap<State, SortedSet<State>>();

    DeterministicOperation(
            Automaton oldAutomaton) {

        if (oldAutomaton == null) {
            throw new InternalException("oldAutomaton may not be null");
        }

        if (oldAutomaton.hasMarkers()) {
            throw new InternalException("oldAutomaton is invalid");
        }

        oldAutomaton = oldAutomaton.withoutUnreachableStates();

        this.newAutomaton = new Automaton(oldAutomaton.getAlphabet());
        WorkSet<State> workSet = new WorkSet<State>();

        {
            SortedSet<State> epsilonReach = oldAutomaton.getStartState()
                    .getEpsilonReach();
            State newState = this.newAutomaton.getStartState();

            map(epsilonReach, newState);

            workSet.add(newState);
        }

        while (workSet.hasNext()) {
            State newFromState = workSet.next();

            SortedSet<State> oldFromStates = getOldStates(newFromState);
            SortedMap<RichSymbol, SortedSet<State>> newTransitions = new TreeMap<RichSymbol, SortedSet<State>>();

            for (State oldFromState : oldFromStates) {
                for (Map.Entry<RichSymbol, SortedSet<State>> entry : oldFromState
                        .getTransitions().entrySet()) {
                    RichSymbol richSymbol = entry.getKey();

                    if (richSymbol != null) {
                        SortedSet<State> newTargetStates = newTransitions
                                .get(richSymbol);

                        if (newTargetStates == null) {

                            newTargetStates = new TreeSet<State>();
                            newTransitions.put(richSymbol, newTargetStates);
                        }

                        newTargetStates.addAll(entry.getValue());
                    }
                }
            }

            for (Map.Entry<RichSymbol, SortedSet<State>> entry : newTransitions
                    .entrySet()) {
                State newToState = getNewState(entry.getValue());
                newFromState.addTransition(entry.getKey(), newToState);

                workSet.add(newToState);
            }
        }

        for (Acceptation acceptation : oldAutomaton.getAcceptations()) {
            this.newAutomaton.addAcceptation(acceptation);
        }

        for (State oldState : oldAutomaton.getStates()) {
            for (Acceptation acceptation : oldState.getAcceptations()) {
                for (State newState : getNewStates(oldState)) {
                    newState.addAcceptation(acceptation);
                }
            }
        }

        this.newAutomaton.stabilize();
    }

    private State getNewState(
            SortedSet<State> oldStates) {

        SortedSet<State> epsilonReach = new TreeSet<State>();

        for (State oldState : oldStates) {
            epsilonReach.addAll(oldState.getEpsilonReach());
        }

        State newState = this.oldStatesToNewStateMap.get(epsilonReach);

        if (newState == null) {
            newState = new State(this.newAutomaton);
        }

        map(epsilonReach, newState);

        return newState;
    }

    private SortedSet<State> getOldStates(
            State newState) {

        return this.newStateToOldStatesMap.get(newState);
    }

    private void map(
            SortedSet<State> oldStates,
            State newState) {

        if (oldStates == null) {
            throw new InternalException("oldStates may not be null");
        }

        if (newState == null) {
            throw new InternalException("newState may not be null");
        }

        this.oldStatesToNewStateMap.put(oldStates, newState);
        this.newStateToOldStatesMap.put(newState, oldStates);

        for (State oldState : oldStates) {
            SortedSet<State> newStates = this.stateMap.get(oldState);

            if (newStates == null) {
                newStates = new TreeSet<State>();
                this.stateMap.put(oldState, newStates);
            }

            newStates.add(newState);
        }
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }

    private SortedSet<State> getNewStates(
            State oldState) {

        if (oldState == null) {
            throw new InternalException("oldState may not be null");
        }

        if (!this.stateMap.containsKey(oldState)) {
            throw new InternalException("invalid oldState");
        }

        return this.stateMap.get(oldState);
    }
}
