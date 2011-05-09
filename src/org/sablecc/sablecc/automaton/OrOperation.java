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

class OrOperation {

    private Alphabet newAlphabet;

    private Automaton newAutomaton;

    private Automaton leftAutomaton;

    private Automaton rightAutomaton;

    private Map<State, State> oldStateToNewStateMap = new HashMap<State, State>();

    private AlphabetMergeResult alphabetMergeResult;

    OrOperation(
            Automaton leftAutomaton,
            Automaton rightAutomaton) {

        if (leftAutomaton == null) {
            throw new InternalException("leftAutomaton may not be null");
        }

        if (rightAutomaton == null) {
            throw new InternalException("rightAutomaton may not be null");
        }

        if (leftAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        if (rightAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        this.alphabetMergeResult = leftAutomaton.getAlphabet().mergeWith(
                rightAutomaton.getAlphabet());
        this.newAlphabet = this.alphabetMergeResult.getNewAlphabet();
        this.newAutomaton = new Automaton(this.newAlphabet);

        this.leftAutomaton = leftAutomaton
                .withMergedAlphabet(this.alphabetMergeResult);
        this.rightAutomaton = rightAutomaton
                .withMergedAlphabet(this.alphabetMergeResult);

        SortedSet<Acceptation> acceptations = new TreeSet<Acceptation>();
        acceptations.addAll(this.leftAutomaton.getAcceptations());
        acceptations.addAll(this.rightAutomaton.getAcceptations());
        for (Acceptation acceptation : acceptations) {
            this.newAutomaton.addAcceptation(acceptation);
        }

        addStatesAndTransitions(this.leftAutomaton);
        addStatesAndTransitions(this.rightAutomaton);

        this.newAutomaton.getStartState().addTransition(
                null,
                this.oldStateToNewStateMap.get(this.leftAutomaton
                        .getStartState()));
        this.newAutomaton.getStartState().addTransition(
                null,
                this.oldStateToNewStateMap.get(this.rightAutomaton
                        .getStartState()));

        this.newAutomaton.stabilize();
    }

    private void addStatesAndTransitions(
            Automaton automaton) {

        for (State oldState : automaton.getStates()) {
            State newState = new State(this.newAutomaton);

            for (Acceptation acceptation : oldState.getAcceptations()) {
                newState.addAcceptation(acceptation);
            }

            this.oldStateToNewStateMap.put(oldState, newState);
        }

        for (State oldSourceState : automaton.getStates()) {
            State newSourceState = this.oldStateToNewStateMap
                    .get(oldSourceState);
            for (Map.Entry<RichSymbol, SortedSet<State>> entry : oldSourceState
                    .getTransitions().entrySet()) {
                RichSymbol richSymbol = entry.getKey();
                for (State oldTargetState : entry.getValue()) {
                    State newTargetState = this.oldStateToNewStateMap
                            .get(oldTargetState);
                    newSourceState.addTransition(richSymbol, newTargetState);
                }
            }
        }
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }
}
