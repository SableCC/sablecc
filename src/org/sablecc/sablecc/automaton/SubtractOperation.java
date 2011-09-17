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

class SubtractOperation {

    private Automaton newAutomaton;

    SubtractOperation(
            Automaton leftAutomaton,
            Automaton rightAutomaton) {

        if (leftAutomaton == null) {
            throw new InternalException("leftAutomaton may not be null");
        }

        if (rightAutomaton == null) {
            throw new InternalException("rightAutomaton may not be null");
        }

        if (leftAutomaton.hasCustomAcceptations()) {
            throw new InternalException("invalid operation");
        }

        if (rightAutomaton.hasCustomAcceptations()) {
            throw new InternalException("invalid operation");
        }

        if (leftAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        if (rightAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        this.newAutomaton = leftAutomaton
                .except(getRejectAutomaton(rightAutomaton.minimal()));
    }

    private Automaton getRejectAutomaton(
            Automaton baseAutomaton) {

        Automaton rejectAutomaton = new Automaton(baseAutomaton.getAlphabet());
        rejectAutomaton.addAcceptation(Acceptation.ACCEPT);

        Map<State, State> baseStateToRejectStateMap = new HashMap<State, State>();

        for (State baseState : baseAutomaton.getStates()) {
            State rejectState;
            if (baseState.equals(baseAutomaton.getStartState())) {
                rejectState = rejectAutomaton.getStartState();
            }
            else {
                rejectState = new State(rejectAutomaton);
            }

            if (baseState.isAcceptState()
                    && !baseState.equals(baseAutomaton.getStartState())) {
                rejectState.addAcceptation(Acceptation.ACCEPT);
            }

            baseStateToRejectStateMap.put(baseState, rejectState);
        }

        for (State baseSourceState : baseAutomaton.getStates()) {
            State rejectSourceState = baseStateToRejectStateMap
                    .get(baseSourceState);
            for (Map.Entry<RichSymbol, SortedSet<State>> entry : baseSourceState
                    .getTransitions().entrySet()) {
                RichSymbol richSymbol = entry.getKey();

                for (State baseTargetState : entry.getValue()) {
                    State rejectTargetState = baseStateToRejectStateMap
                            .get(baseTargetState);

                    if (baseSourceState.equals(baseAutomaton.getStartState())) {
                        rejectSourceState.addTransition(richSymbol,
                                rejectTargetState);
                        if (richSymbol.isLookahead()
                                && !richSymbol.equals(RichSymbol.END)) {
                            rejectSourceState.addTransition(richSymbol
                                    .getSymbol().getNormalRichSymbol(),
                                    rejectTargetState);
                        }
                    }
                    else {
                        rejectSourceState.addTransition(richSymbol,
                                rejectTargetState);
                        if (richSymbol.isLookahead()) {
                            if (!richSymbol.equals(RichSymbol.END)) {
                                rejectSourceState.addTransition(richSymbol
                                        .getSymbol().getNormalRichSymbol(),
                                        rejectTargetState);
                            }
                        }
                        else {
                            rejectSourceState.addTransition(richSymbol
                                    .getSymbol().getLookaheadRichSymbol(),
                                    rejectTargetState);
                        }
                    }
                }
            }
        }

        rejectAutomaton.stabilize();

        Symbol any = new Symbol(new Interval(Bound.MIN, Bound.MAX));
        Automaton anyStar = Automaton.getSymbolLookAnyStarEnd(any).zeroOrMore();

        return anyStar.concat(rejectAutomaton);
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }
}
