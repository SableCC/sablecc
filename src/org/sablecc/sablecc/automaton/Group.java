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

class Group {

    private final MinimalOperation minimalAutomatonBuilder;

    private final SortedSet<State> states;

    private final boolean isDeadEnd;

    private boolean isDeleted = false;

    private String toString;

    Group(
            MinimalOperation minimalAutomatonBuilder,
            SortedSet<State> states,
            boolean isDeadEnd) {

        if (minimalAutomatonBuilder == null) {
            throw new InternalException(
                    "minimalAutomatonBuilder may not be null");
        }

        if (states == null) {
            throw new InternalException("states may not be null");
        }

        this.minimalAutomatonBuilder = minimalAutomatonBuilder;
        this.states = states;
        this.isDeadEnd = isDeadEnd;

        minimalAutomatonBuilder.addGroup(this);

        if (isDeadEnd) {
            minimalAutomatonBuilder.setDeadEnd(this);
        }

        for (State state : states) {
            setGroup(state, this);
        }
    }

    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("Group(");
            boolean first = true;
            for (State state : this.states) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(",");
                }
                sb.append(state);
            }
            sb.append(")");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    MinimalOperation getMinimalAutomatonBuilder() {

        return this.minimalAutomatonBuilder;
    }

    SortedSet<State> getStates() {

        return this.states;
    }

    boolean isDeadEnd() {

        return this.isDeadEnd;
    }

    void delete() {

        this.minimalAutomatonBuilder.removeGroup(this);
        this.isDeleted = true;
    }

    boolean isDeleted() {

        return this.isDeleted;
    }

    private void setGroup(
            State state,
            Group group) {

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        this.minimalAutomatonBuilder.setGroup(state, group);
    }

    private Group getGroup(
            State state) {

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        return this.minimalAutomatonBuilder.getGroup(state);
    }

    private Group getDeadEndGroup() {

        return this.minimalAutomatonBuilder.getDeadEndGroup();
    }

    boolean splitOnRichSymbol(
            RichSymbol richSymbol) {

        Set<Group> destinations = new LinkedHashSet<Group>();

        for (State state : this.states) {

            State target = state.getSingleTarget(richSymbol);

            Group destination;
            if (target == null) {
                destination = getDeadEndGroup();
            }
            else {
                destination = getGroup(target);
            }

            destinations.add(destination);
        }

        if (isDeadEnd()) {
            destinations.add(this);
        }

        if (destinations.size() < 2) {
            return false;
        }

        Map<Group, SortedSet<State>> stateMap = new HashMap<Group, SortedSet<State>>();

        if (isDeadEnd()) {
            stateMap.put(this, new TreeSet<State>());
        }

        for (State state : this.states) {

            State target = state.getSingleTarget(richSymbol);

            Group destination;
            if (target == null) {
                destination = getDeadEndGroup();
            }
            else {
                destination = getGroup(target);
            }

            SortedSet<State> states = stateMap.get(destination);

            if (states == null) {
                states = new TreeSet<State>();
                stateMap.put(destination, states);
            }

            states.add(state);
        }

        for (Group destination : destinations) {
            SortedSet<State> states = stateMap.get(destination);

            if (isDeadEnd() && destination == this) {
                new Group(this.minimalAutomatonBuilder, states, true);
            }
            else {
                new Group(this.minimalAutomatonBuilder, states, false);
            }
        }

        delete();

        return true;
    }

    void splitIfNecessary(
            Alphabet alphabet) {

        for (Symbol symbol : alphabet.getSymbols()) {

            if (splitOnRichSymbol(symbol.getNormalRichSymbol())) {
                return;
            }

            if (splitOnRichSymbol(symbol.getLookaheadRichSymbol())) {
                return;
            }
        }

        splitOnRichSymbol(RichSymbol.END);
    }
}
