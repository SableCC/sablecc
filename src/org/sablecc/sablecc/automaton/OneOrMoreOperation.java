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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.alphabet.RichSymbol;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.util.Pair;
import org.sablecc.util.WorkSet;

class OneOrMoreOperation {

    private Automaton newAutomaton;

    private Automaton oldAutomaton;

    private Map<Set<Pair<State, SortedSet<State>>>, State> stateMap
            = new HashMap<>();

    private SortedMap<State, Set<Pair<State, SortedSet<State>>>> progressMap
            = new TreeMap<>();

    private WorkSet<State> workSet = new WorkSet<>();

    OneOrMoreOperation(
            Automaton oldAutomaton) {

        if (oldAutomaton == null) {
            throw new InternalException("oldAutomaton may not be null");
        }

        if (oldAutomaton.hasCustomAcceptations()) {
            throw new InternalException("invalid operation");
        }

        if (oldAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        this.oldAutomaton = oldAutomaton.minimal();
        this.newAutomaton = new Automaton(this.oldAutomaton.getAlphabet());
        this.newAutomaton.addAcceptation(Acceptation.ACCEPT);

        {
            Set<Pair<State, SortedSet<State>>> progressSet
                    = new LinkedHashSet<>();
            Pair<State, SortedSet<State>> progress = new Pair<>(
                    this.oldAutomaton.getStartState(), new TreeSet<State>());
            progressSet.add(progress);

            this.stateMap.put(progressSet, this.newAutomaton.getStartState());
            this.progressMap.put(this.newAutomaton.getStartState(),
                    progressSet);
            this.workSet.add(this.newAutomaton.getStartState());
        }

        while (this.workSet.hasNext()) {
            State state = this.workSet.next();

            outer_loop:
            for (Pair<State, SortedSet<State>> progress : this.progressMap
                    .get(state)) {
                if (!progress.getLeft().isAcceptState()) {
                    continue;
                }

                for (State conditionState : progress.getRight()) {
                    if (!conditionState.isAcceptState()) {
                        continue outer_loop;
                    }
                }

                state.addAcceptation(Acceptation.ACCEPT);
                break;
            }

            for (Symbol symbol : this.newAutomaton.getAlphabet().getSymbols()) {
                addTransition(state, symbol.getNormalRichSymbol());
                addTransition(state, symbol.getLookaheadRichSymbol());
            }
            addTransition(state, RichSymbol.END);
        }

        this.newAutomaton.stabilize();
    }

    private void addTransition(
            State newSourceState,
            RichSymbol richSymbol) {

        Set<Pair<State, SortedSet<State>>> sourceProgressSet
                = this.progressMap.get(newSourceState);
        Set<Pair<State, SortedSet<State>>> targetProgressSet
                = new LinkedHashSet<>();

        outer_loop:
        for (Pair<State, SortedSet<State>> sourceProgress : sourceProgressSet) {
            State oldTargetState
                    = sourceProgress.getLeft().getSingleTarget(richSymbol);

            if (oldTargetState == null) {
                continue;
            }

            SortedSet<State> targetConditionStates = new TreeSet<>();

            for (State sourceConditionState : sourceProgress.getRight()) {
                State targetConditionState;
                if (richSymbol.isLookahead()) {
                    targetConditionState
                            = sourceConditionState.getSingleTarget(richSymbol);
                }
                else {
                    targetConditionState = sourceConditionState.getSingleTarget(
                            richSymbol.getSymbol().getLookaheadRichSymbol());
                }

                if (targetConditionState == null) {
                    continue outer_loop;
                }

                targetConditionStates.add(targetConditionState);
            }

            Pair<State, SortedSet<State>> targetProgress
                    = new Pair<>(oldTargetState, targetConditionStates);

            targetProgressSet.add(targetProgress);

            if (!richSymbol.isLookahead()) {
                targetConditionStates = new TreeSet<>();
                targetConditionStates.add(targetProgress.getLeft());
                targetConditionStates.addAll(targetProgress.getRight());

                targetProgress = new Pair<>(this.oldAutomaton.getStartState(),
                        targetConditionStates);

                targetProgressSet.add(targetProgress);
            }
        }

        if (targetProgressSet.size() > 0) {
            State newTargetState = this.stateMap.get(targetProgressSet);
            if (newTargetState == null) {
                newTargetState = new State(this.newAutomaton);

                this.stateMap.put(targetProgressSet, newTargetState);
                this.progressMap.put(newTargetState, targetProgressSet);
                this.workSet.add(newTargetState);
            }
            newSourceState.addTransition(richSymbol, newTargetState);
        }
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }
}
