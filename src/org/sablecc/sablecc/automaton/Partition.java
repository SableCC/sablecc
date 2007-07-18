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

/**
 * A partition is the division of states of a <code>Dfa</code> into groups.
 * The minimisation of a <code>Dfa</code> tries to generate the partition
 * which contains the less number of groups possible.
 */
class Partition<T extends Comparable<? super T>> {

    /** The <code>Dfa</code> of this partition. */
    private final Dfa<T> dfa;

    /** The set of groups of this partition. */
    private final Set<Group<T>> groups = new LinkedHashSet<Group<T>>();

    /** The elements of this partition. */
    private Set<Element<T>> elements = new LinkedHashSet<Element<T>>();

    /**
     * A <code>Map</code> that maps each states of this partition's
     * <code>Dfa</code> to it's corresponding element.
     */
    private final Map<DfaState<T>, Element<T>> stateToElementMap = new HashMap<DfaState<T>, Element<T>>();

    /**
     * Constructs a partition for the provided <code>Dfa<code>.
     *
     * @param dfa the <code>Dfa</code>.
     * @throws InternalException
     *             if the provided <code>Dfa</code> is <code>null</code>.
     */
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

    /**
     * Refines this partition by refining all of it's groups.
     */
    private void refine() {

        // we get a copy so that modifications to this.groups won't disturb the
        // iterator
        for (Group<T> group : new LinkedHashSet<Group<T>>(this.groups)) {
            group.refine();
        }
    }

    /**
     * Returns the <code>Dfa</code> of this partition.
     * 
     * @return the <code>Dfa</code>.
     */
    Dfa<T> getDfa() {

        return this.dfa;
    }

    /**
     * Returns the a set of the groups of this partition.
     * 
     * @return the set of groups.
     */
    Set<Group<T>> getGroups() {

        return Collections.unmodifiableSet(this.groups);
    }

    /**
     * Returns the string representation of this partition.
     * 
     * @return the string representation.
     */
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

    /**
     * Adds the provided group to this partition.
     * 
     * @param group
     *            the group to add.
     * @throws InternalException
     *             if the provided group is <code>null</code>, if it's
     *             partition is not this instance or if it is already in this
     *             partition.
     */
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

    /**
     * Returns an element of this partition corresponding to the provided
     * <code>DfaState</code>.
     * 
     * @param state
     *            the <code>DfaState</code>.
     * @return the element.
     * @throws InternalException
     *             if the provided <code>DfaState</code> is <code>null</code>,
     *             if it's not in this partition's <code>Dfa</code> or if
     *             corruption is detected.
     */
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

    /**
     * Adds the provided element to this partition.
     * 
     * @param element
     *            the element to add.
     * @throws InternalException
     *             if the provided element is <code>null</code>, if it's
     *             partition is not this one, if it's <code>Dfa</code> is not
     *             the same as the one of this partition or if this element is
     *             already in this partition.
     * 
     */
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
