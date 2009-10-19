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

import org.sablecc.exception.*;
import org.sablecc.util.*;

public class LRState {

    private final LRAutomaton automaton;

    private final Set<Item> coreItemSet = new LinkedHashSet<Item>();

    private final Set<Item> items = new LinkedHashSet<Item>();

    private final Map<Token, LRState> tokenTransitions = new LinkedHashMap<Token, LRState>();

    private final Map<Production, LRState> productionTransitions = new LinkedHashMap<Production, LRState>();

    private final Set<Item> shiftItems = new LinkedHashSet<Item>();

    private final Set<Item> reduceItems = new LinkedHashSet<Item>();

    private final Set<Action> actions = new LinkedHashSet<Action>();

    public LRState(
            LRAutomaton automaton,
            Set<Item> coreItemSet) {

        this.automaton = automaton;
        this.coreItemSet.addAll(coreItemSet);

        WorkSet<Item> workSet = new WorkSet<Item>();
        for (Item item : this.coreItemSet) {
            this.items.add(item);
            workSet.add(item);
        }

        while (workSet.hasNext()) {
            Item item = workSet.next();

            if (item.getType() == ItemType.BEFORE_PRODUCTION) {
                Production production = item.getProductionElement()
                        .getProduction();
                for (Alternative alternative : production.getAlternatives()) {
                    Item newItem = alternative.getItem(0);
                    this.items.add(newItem);
                    workSet.add(newItem);
                }
            }
        }

        for (Item item : this.items) {
            switch (item.getType()) {
            case BEFORE_TOKEN:
                this.shiftItems.add(item);
                break;
            case END:
                this.reduceItems.add(item);
                break;
            }
        }
    }

    public void computeTransitions() {

        Map<Token, Set<Item>> tokenToItemSetMap = new LinkedHashMap<Token, Set<Item>>();
        Map<Production, Set<Item>> productionToItemSetMap = new LinkedHashMap<Production, Set<Item>>();

        for (Item sourceItem : this.items) {
            switch (sourceItem.getType()) {
            case BEFORE_TOKEN: {
                Token token = sourceItem.getTokenElement().getToken();
                Set<Item> itemSet = tokenToItemSetMap.get(token);
                if (itemSet == null) {
                    itemSet = new LinkedHashSet<Item>();
                    tokenToItemSetMap.put(token, itemSet);
                }
                itemSet.add(sourceItem.next());
                break;
            }
            case BEFORE_PRODUCTION: {
                Production production = sourceItem.getProductionElement()
                        .getProduction();
                Set<Item> itemSet = productionToItemSetMap.get(production);
                if (itemSet == null) {
                    itemSet = new LinkedHashSet<Item>();
                    productionToItemSetMap.put(production, itemSet);
                }
                itemSet.add(sourceItem.next());
                break;
            }
            default:
                break;
            }
        }

        for (Map.Entry<Token, Set<Item>> entry : tokenToItemSetMap.entrySet()) {
            Token token = entry.getKey();
            Set<Item> itemSet = entry.getValue();
            LRState destinationState = this.automaton.getState(itemSet);
            this.tokenTransitions.put(token, destinationState);
        }

        for (Map.Entry<Production, Set<Item>> entry : productionToItemSetMap
                .entrySet()) {
            Production production = entry.getKey();
            Set<Item> itemSet = entry.getValue();
            LRState destinationState = this.automaton.getState(itemSet);
            this.productionTransitions.put(production, destinationState);
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Item item : this.coreItemSet) {
            if (first) {
                first = false;
            }
            else {
                sb.append(",");
            }

            sb.append(item);
        }
        sb.append("}");
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

    public void computeActions(
            Verbosity verbosity) {

        // LR(0) shift state
        if (this.reduceItems.size() == 0) {
            this.actions.add(new ShiftAction(null));

            switch (verbosity) {
            case VERBOSE:
                System.out.println("   - LR(0) shift");
                break;
            }

            return;
        }

        // LR(0) reduce state
        if (this.shiftItems.size() == 0 && this.reduceItems.size() == 1) {
            this.actions.add(new ReduceAction(null, this.reduceItems.iterator()
                    .next().getAlternative()));

            switch (verbosity) {
            case VERBOSE:
                System.out.println("   - LR(0) reduce");
                break;
            }

            return;
        }

        throw new InternalException("LR(0) conflict in state" + this);
    }
}
