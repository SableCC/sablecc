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

package org.sablecc.sablecc.oldlrautomaton;

import java.util.*;

import org.sablecc.sablecc.launcher.*;

public class LRAutomaton {

    private final OldGrammar oldGrammar;

    private final Map<Set<Item>, LRState> coreItemSetToLRStateMap = new LinkedHashMap<Set<Item>, LRState>();

    private boolean lookComputationDataHasChanged;

    private Map<LRState, Map<OldProduction, Map<Integer, Set<Item>>>> previousLookComputationData;

    private Map<LRState, Map<OldProduction, Map<Integer, Set<Item>>>> currentLookComputationData;

    public LRAutomaton(
            OldGrammar oldGrammar,
            Trace trace) {

        this.oldGrammar = oldGrammar;

        trace.verboseln("  Computing LR(0) automaton");

        Set<Item> startSet = new LinkedHashSet<Item>();
        startSet.add(oldGrammar.getProduction("$Start", null).getAlternatives()
                .get(0).getItem(0));
        LRState startState = new LRState(this, startSet);
        this.coreItemSetToLRStateMap.put(startSet, startState);
        startState.computeTransitions();

        trace.verboseln("  Analyzing " + this.coreItemSetToLRStateMap.size()
                + " states");

        for (LRState state : this.coreItemSetToLRStateMap.values()) {
            state.computeOrigins();
        }

        for (LRState state : this.coreItemSetToLRStateMap.values()) {
            state.computeActions(trace);
        }
    }

    public Collection<LRState> getStates() {

        return this.coreItemSetToLRStateMap.values();
    }

    LRState getState(
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

    void resetLookComputationData() {

        this.lookComputationDataHasChanged = false;
        if (this.currentLookComputationData != null) {
            this.previousLookComputationData = this.currentLookComputationData;
        }
        else {
            this.previousLookComputationData = new LinkedHashMap<LRState, Map<OldProduction, Map<Integer, Set<Item>>>>();
        }
        this.currentLookComputationData = new LinkedHashMap<LRState, Map<OldProduction, Map<Integer, Set<Item>>>>();
    }

    boolean lookComputationDataHasChanged() {

        return this.lookComputationDataHasChanged;
    }

    void storeLookComputationResults() {

        for (Map.Entry<LRState, Map<OldProduction, Map<Integer, Set<Item>>>> stateEntry : this.currentLookComputationData
                .entrySet()) {
            LRState state = stateEntry.getKey();
            for (Map.Entry<OldProduction, Map<Integer, Set<Item>>> productionEntry : stateEntry
                    .getValue().entrySet()) {
                OldProduction oldProduction = productionEntry.getKey();
                for (Map.Entry<Integer, Set<Item>> distanceEntry : productionEntry
                        .getValue().entrySet()) {
                    state.setLook(oldProduction, distanceEntry.getKey(),
                            distanceEntry.getValue());
                }
            }
        }
        this.previousLookComputationData = null;
        this.currentLookComputationData = null;
        this.lookComputationDataHasChanged = false;
    }

    Set<Item> getCurrentLookComputationData(
            LRState lrState,
            OldProduction oldProduction,
            int distance) {

        Map<OldProduction, Map<Integer, Set<Item>>> productionToLookaheadMap = this.currentLookComputationData
                .get(lrState);
        if (productionToLookaheadMap == null) {
            return null;
        }
        Map<Integer, Set<Item>> lookahead = productionToLookaheadMap
                .get(oldProduction);
        if (lookahead == null) {
            return null;
        }
        return lookahead.get(distance);
    }

    Set<Item> getPreviousLookComputationData(
            LRState lrState,
            OldProduction oldProduction,
            int distance) {

        Map<OldProduction, Map<Integer, Set<Item>>> productionToLookaheadMap = this.previousLookComputationData
                .get(lrState);
        if (productionToLookaheadMap == null) {
            return new LinkedHashSet<Item>();
        }
        Map<Integer, Set<Item>> lookahead = productionToLookaheadMap
                .get(oldProduction);
        if (lookahead == null) {
            return new LinkedHashSet<Item>();
        }
        Set<Item> items = lookahead.get(distance);
        if (items == null) {
            return new LinkedHashSet<Item>();
        }
        return items;
    }

    void setCurrentLookComputationData(
            LRState lrState,
            OldProduction oldProduction,
            int distance,
            Set<Item> lookComputationData) {

        Map<OldProduction, Map<Integer, Set<Item>>> productionToLookaheadMap = this.currentLookComputationData
                .get(lrState);
        if (productionToLookaheadMap == null) {
            productionToLookaheadMap = new LinkedHashMap<OldProduction, Map<Integer, Set<Item>>>();
            this.currentLookComputationData.put(lrState,
                    productionToLookaheadMap);
        }
        Map<Integer, Set<Item>> lookahead = productionToLookaheadMap
                .get(oldProduction);
        if (lookahead == null) {
            lookahead = new LinkedHashMap<Integer, Set<Item>>();
            productionToLookaheadMap.put(oldProduction, lookahead);
        }
        lookahead.put(distance, lookComputationData);

        // detect change

        Set<Item> previousLookComputationData = getPreviousLookComputationData(
                lrState, oldProduction, distance);
        if (!lookComputationData.equals(previousLookComputationData)) {
            this.lookComputationDataHasChanged = true;
        }
    }

    public OldGrammar getGrammar() {

        return this.oldGrammar;
    }
}
