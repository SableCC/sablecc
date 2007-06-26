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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.sablecc.automaton.graph.State;
import org.sablecc.sablecc.automaton.graph.Transition;
import org.sablecc.sablecc.exception.InternalException;

class EpsilonReach<T extends Comparable<? super T>> {

    private NFA<T> nfa;

    private final Map<State<T>, Set<State<T>>> reachMap = new HashMap<State<T>, Set<State<T>>>();

    EpsilonReach(
            NFA<T> nfa) {

        if (nfa == null) {
            throw new InternalException("nfa may not be null");
        }

        this.nfa = nfa;
    }

    Set<State<T>> getEpsilonReach(
            State<T> state) {

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        if (!this.nfa.getStates().contains(state)) {
            throw new InternalException("invalid state");
        }

        Set<State<T>> reach = this.reachMap.get(state);

        if (reach == null) {
            reach = computeReach(state);
            this.reachMap.put(state, reach);
        }

        return reach;
    }

    private Set<State<T>> computeReach(
            State<T> state) {

        Set<State<T>> reach = new HashSet<State<T>>();

        visit(state, reach);

        return Collections.unmodifiableSet(reach);
    }

    private void visit(
            State<T> state,
            Set<State<T>> reach) {

        // did we visit it before?
        if (!reach.contains(state)) {

            // no, so add it
            reach.add(state);

            // do we know its reach?
            Set<State<T>> stateReach = this.reachMap.get(state);
            if (stateReach != null) {
                // yes, use it!
                reach.addAll(stateReach);
            }
            else {
                // no, we need to visit
                for (Transition<T> transition : state.getForwardTransitions()) {
                    if (transition.getSymbol() == null) {
                        visit(transition.getDestination(), reach);
                    }
                }
            }
        }
    }
}
