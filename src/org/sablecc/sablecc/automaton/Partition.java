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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.sablecc.exception.InternalException;

class Partition<T extends Comparable<? super T>> {

    private final Dfa<T> dfa;

    private final Set<Group<T>> groups = new LinkedHashSet<Group<T>>();

    private Set<Element<T>> elements = new LinkedHashSet<Element<T>>();

    private final Map<DfaState<T>, Element<T>> stateToElementMap = new HashMap<DfaState<T>, Element<T>>();

    Partition(
            Dfa<T> dfa) {

        if (dfa == null) {
            throw new InternalException("dfa may not be null");
        }

        this.dfa = dfa;

        // create elements
        for (DfaState<T> state : dfa.getStates()) {
            new Element<T>(this, state);
        }

        // prevent against accidental creation of other elements
        this.elements = Collections.unmodifiableSet(this.elements);

        // create initial partition

        // non-accept group has at least one element, the dead-end state
        Group<T> nonAcceptGroup = new Group<T>(this);

        // we don't want empty groups; beware of empty accept group
        Group<T> acceptGroup = null;

        if (!dfa.getAcceptStates().isEmpty()) {
            acceptGroup = new Group<T>(this);
        }

        for (Element<T> element : this.elements) {
            if (dfa.getAcceptStates().contains(element.getState())) {
                element.setGroup(acceptGroup);
            }
            else {
                element.setGroup(nonAcceptGroup);
            }
        }

        // refine partition

        int groupSize;

        do {
            groupSize = this.groups.size();
            refine();
        }
        while (this.groups.size() != groupSize);
    }

    private void refine() {

        // we get a copy so that modifications to this.groups won't disturb the
        // iterator
        for (Group<T> group : new LinkedHashSet<Group<T>>(this.groups)) {
            group.refine();
        }
    }

    Dfa<T> getDfa() {

        return this.dfa;
    }

    Set<Group<T>> getGroups() {

        return Collections.unmodifiableSet(this.groups);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Partition:");
        sb.append(System.getProperty("line.separator"));
        for (Group<T> group : this.groups) {
            sb.append("    ");
            sb.append(group);
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

    void addGroup(
            Group<T> group) {

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        if (group.getPartition() != this) {
            throw new InternalException("invalid group");
        }

        if (!this.groups.add(group)) {
            throw new InternalException("group is already in this partition");
        }
    }

    Element<T> getElement(
            DfaState<T> state) {

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        if (state.getDfa() != this.dfa) {
            throw new InternalException("invalid state");
        }

        Element<T> element = this.stateToElementMap.get(state);

        if (element == null) {
            throw new InternalException("corruption detected");
        }

        return element;
    }

    void addElement(
            Element<T> element) {

        if (element == null) {
            throw new InternalException("element may not be null");
        }

        if (element.getPartition() != this) {
            throw new InternalException("invalid element");
        }

        if (element.getState().getDfa() != this.dfa) {
            throw new InternalException("invalid element");
        }

        if (!this.elements.add(element)) {
            throw new InternalException("element is already in this partition");
        }

        this.stateToElementMap.put(element.getState(), element);
    }

}
