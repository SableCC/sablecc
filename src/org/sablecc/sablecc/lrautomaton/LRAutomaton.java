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

package org.sablecc.sablecc.lrautomaton;

import java.util.*;

public class LRAutomaton {

    private final Grammar grammar;

    private final Map<Set<Item>, LRState> coreItemSetToLRStateMap = new LinkedHashMap<Set<Item>, LRState>();

    public LRAutomaton(
            Grammar grammar) {

        this.grammar = grammar;

        Set<Item> startSet = new LinkedHashSet<Item>();
        startSet.add(grammar.getProduction("$Start").getAlternatives().get(0)
                .getItem(0));
        LRState startState = new LRState(this, startSet);
        this.coreItemSetToLRStateMap.put(startSet, startState);
        startState.computeTransitions();

        for (LRState state : this.coreItemSetToLRStateMap.values()) {
            state.computeActions();
        }
    }

    public LRState getState(
            Set<Item> itemSet) {

        LRState state = this.coreItemSetToLRStateMap.get(itemSet);
        if (state == null) {
            state = new LRState(this, itemSet);
            this.coreItemSetToLRStateMap.put(itemSet, state);
            state.computeTransitions();
        }
        return state;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (LRState state : this.coreItemSetToLRStateMap.values()) {
            sb.append(state);
        }
        return sb.toString();
    }
}
