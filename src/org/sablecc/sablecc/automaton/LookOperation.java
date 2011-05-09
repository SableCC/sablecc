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

class LookOperation {

    private Automaton newAutomaton;

    LookOperation(
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
                .concat(getLookaheadAutomation(rightAutomaton.deterministic()));
    }

    private Automaton getLookaheadAutomation(
            Automaton normalAutomaton) {

        Automaton lookaheadAutomaton = new Automaton(
                normalAutomaton.getAlphabet());
        lookaheadAutomaton.addAcceptation(Acceptation.ACCEPT);

        Map<State, State> normalStateToLookaheadStateMap = new HashMap<State, State>();

        for (State normalState : normalAutomaton.getStates()) {
            State lookaheadState;
            if (normalState.equals(normalAutomaton.getStartState())) {
                lookaheadState = lookaheadAutomaton.getStartState();
            }
            else {
                lookaheadState = new State(lookaheadAutomaton);
            }

            if (normalState.isAcceptState()) {
                lookaheadState.addAcceptation(Acceptation.ACCEPT);
            }

            normalStateToLookaheadStateMap.put(normalState, lookaheadState);
        }

        for (State normalSourceState : normalAutomaton.getStates()) {
            State lookaheadSourceState = normalStateToLookaheadStateMap
                    .get(normalSourceState);
            for (Map.Entry<RichSymbol, SortedSet<State>> entry : normalSourceState
                    .getTransitions().entrySet()) {
                RichSymbol richSymbol = entry.getKey();

                if (!richSymbol.isLookahead()) {
                    richSymbol = richSymbol.getSymbol()
                            .getLookaheadRichSymbol();
                }

                for (State normalTargetState : entry.getValue()) {
                    State lookaheadTargetState = normalStateToLookaheadStateMap
                            .get(normalTargetState);
                    lookaheadSourceState.addTransition(richSymbol,
                            lookaheadTargetState);
                }
            }
        }

        lookaheadAutomaton.stabilize();

        return lookaheadAutomaton;
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }
}
