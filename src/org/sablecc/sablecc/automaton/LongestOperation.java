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

class LongestOperation {

    private Automaton newAutomaton;

    private Map<Pair<State, SortedSet<State>>, State> stateMap = new HashMap<Pair<State, SortedSet<State>>, State>();

    private SortedMap<State, Pair<State, SortedSet<State>>> progressMap = new TreeMap<State, Pair<State, SortedSet<State>>>();

    private WorkSet<State> workSet = new WorkSet<State>();

    LongestOperation(
            Automaton oldAutomaton) {

        if (oldAutomaton == null) {
            throw new InternalException("oldAutomaton may not be null");
        }

        if (oldAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        oldAutomaton = oldAutomaton.minimal();

        this.newAutomaton = new Automaton(oldAutomaton.getAlphabet());

        for (Acceptation acceptation : oldAutomaton.getAcceptations()) {
            this.newAutomaton.addAcceptation(acceptation);
        }

        {
            Pair<State, SortedSet<State>> progress = new Pair<State, SortedSet<State>>(
                    oldAutomaton.getStartState(), new TreeSet<State>());

            this.stateMap.put(progress, this.newAutomaton.getStartState());
            this.progressMap.put(this.newAutomaton.getStartState(), progress);
            this.workSet.add(this.newAutomaton.getStartState());
        }

        while (this.workSet.hasNext()) {
            State state = this.workSet.next();

            Pair<State, SortedSet<State>> progress = this.progressMap
                    .get(state);

            if (progress.getLeft().isAcceptState()) {
                boolean reject = false;
                for (State rejectState : progress.getRight()) {
                    if (rejectState.isAcceptState()) {
                        reject = true;
                        break;
                    }
                }
                if (!reject) {
                    for (Acceptation acceptation : progress.getLeft()
                            .getAcceptations()) {
                        state.addAcceptation(acceptation);
                    }
                }
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

        Pair<State, SortedSet<State>> sourceProgress = this.progressMap
                .get(newSourceState);

        State oldSourceState = sourceProgress.getLeft();
        State oldTargetState = oldSourceState.getSingleTarget(richSymbol);

        if (oldTargetState == null) {
            return;
        }

        SortedSet<State> sourceRejectStates = sourceProgress.getRight();
        SortedSet<State> targetRejectStates = new TreeSet<State>();

        if (richSymbol.isLookahead()) {
            RichSymbol normalRichSymbol = richSymbol.equals(RichSymbol.END) ? null
                    : richSymbol.getSymbol().getNormalRichSymbol();

            if (normalRichSymbol != null) {
                State targetRejectState = oldSourceState
                        .getSingleTarget(normalRichSymbol);
                if (targetRejectState != null) {
                    targetRejectStates.add(targetRejectState);
                }
            }

            for (State sourceRejectState : sourceRejectStates) {
                {
                    State targetRejectState = sourceRejectState
                            .getSingleTarget(richSymbol);
                    if (targetRejectState != null) {
                        targetRejectStates.add(targetRejectState);
                    }
                }
                if (normalRichSymbol != null) {
                    State targetRejectState = sourceRejectState
                            .getSingleTarget(normalRichSymbol);
                    if (targetRejectState != null) {
                        targetRejectStates.add(targetRejectState);
                    }
                }
            }
        }

        Pair<State, SortedSet<State>> targetProgress = new Pair<State, SortedSet<State>>(
                oldTargetState, targetRejectStates);

        State newTargetState = this.stateMap.get(targetProgress);
        if (newTargetState == null) {
            newTargetState = new State(this.newAutomaton);

            this.stateMap.put(targetProgress, newTargetState);
            this.progressMap.put(newTargetState, targetProgress);
            this.workSet.add(newTargetState);
        }

        newSourceState.addTransition(richSymbol, newTargetState);
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }
}
