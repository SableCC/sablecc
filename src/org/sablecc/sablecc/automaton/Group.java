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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

/**
 * A group is a collection of state or element which have the same transitions,
 * so we can merge them.
 */
class Group<T extends Comparable<? super T>> {

    /** The partition related to this group. */
    private final Partition<T> partition;

    /** The set of elements in this group. */
    private final Set<Element<T>> elements = new LinkedHashSet<Element<T>>();

    /** The minimal state of this group. */
    private MinimalDfaState<T> state;

    /** The map of transitions of this group. */
    private final SortedMap<Symbol<T>, Group<T>> transitions = new TreeMap<Symbol<T>, Group<T>>();

    /**
     * Constructs a new group with a provided partition.
     * 
     * @param partition
     *            the partition.
     * 
     * @throws InternalException
     *             if the partition is <code>null</code>
     */
    Group(
            final Partition<T> partition) {

        if (partition == null) {
            throw new InternalException("partition may not be null");
        }

        this.partition = partition;

        this.partition.addGroup(this);
    }

    /**
     * Returns the partition of this group.
     * 
     * @return the partition.
     */
    Partition<T> getPartition() {

        return this.partition;
    }

    /**
     * Returns the set of elements of this group.
     * 
     * @return the set of elements.
     */
    Set<Element<T>> getElements() {

        return Collections.unmodifiableSet(this.elements);
    }

    /**
     * Returns the state of this group.
     * 
     * @return the state.
     */
    MinimalDfaState<T> getState() {

        return this.state;
    }

    /**
     * Returns the transitions of this group.
     * 
     * @return the map of transitions.
     */
    SortedMap<Symbol<T>, Group<T>> getTransitions() {

        return Collections.unmodifiableSortedMap(this.transitions);
    }

    /**
     * Returns the string representation of this group.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Group: {");
        for (Element<T> element : this.elements) {
            sb.append(element.getState());
            sb.append(",");
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * Set the state of this group with the provided state.
     * 
     * @param state
     *            the provided state.
     */
    void setState(
            MinimalDfaState<T> state) {

        this.state = state;
    }

    /**
     * Adds a new element to this group.
     * 
     * @param element
     *            the provided element.
     * 
     * @throws InternalException
     *             if the element is <code>null</code>, invalid, or already
     *             added in this group.
     */
    void addElement(
            Element<T> element) {

        if (element == null) {
            throw new InternalException("element may not be null");
        }

        if (element.getGroup() != this) {
            throw new InternalException("invalid element");
        }

        if (!this.elements.add(element)) {
            throw new InternalException("element is already in this group");
        }
    }

    /**
     * Removes an element of this group.
     * 
     * @param element
     *            the element we want to remove.
     * 
     * @throws InternalException
     *             if the element is <code>null</code> of if it not in this
     *             group.
     */
    void removeElement(
            Element<T> element) {

        if (element == null) {
            throw new InternalException("element may not be null");
        }

        if (!this.elements.remove(element)) {
            throw new InternalException("element is not in this group");
        }
    }

    /**
     * Adds a new transition to this group.
     * 
     * @param symbol
     *            the symbol of the transition.
     * 
     * @param group
     *            the destination group of the transition.
     * 
     * @throws InternalException
     *             if the symbol or the group is <code>null</code>, or if the
     *             transition is invalid.
     */
    void addTransition(
            Symbol<T> symbol,
            Group<T> group) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        if (this.transitions.containsKey(symbol)
                && !this.transitions.get(symbol).equals(group)) {
            throw new InternalException(
                    "distinct transitions on a single symbol are not allowed");
        }

        this.transitions.put(symbol, group);
    }

    /**
     * Refine this group by making a copy of it.
     * 
     * @throws InternalException
     *             if corruption has been detected in this group.
     */
    void refine() {

        for (Symbol<T> symbol : this.partition.getDfa().getAlphabet()
                .getSymbols()) {

            // for each new target, remember the first source (or
            // representative) element that targeted to it
            Map<Group<T>, Element<T>> targetToRepresentative = new LinkedHashMap<Group<T>, Element<T>>();
            Map<Element<T>, Element<T>> elementToRepresentative = new LinkedHashMap<Element<T>, Element<T>>();

            for (Element<T> element : this.elements) {
                Group<T> target = element.getTarget(symbol);

                Element<T> representative = targetToRepresentative.get(target);

                if (representative == null) {
                    representative = element;
                    targetToRepresentative.put(target, representative);
                }

                elementToRepresentative.put(element, representative);
            }

            if (targetToRepresentative.isEmpty()) {
                throw new InternalException("corruption detected");
            }

            // if there were many targets, we must split the group
            if (targetToRepresentative.size() > 1) {

                // create the new groups
                boolean first = true;
                for (Map.Entry<Group<T>, Element<T>> entry : targetToRepresentative
                        .entrySet()) {
                    if (first) {
                        // skip
                        first = false;
                    }
                    else {
                        entry.getValue().setGroup(new Group<T>(this.partition));
                    }
                }

                // attach elements to new groups

                // we get a copy so that modifications to this.elements won't
                // disturb the iterator
                for (Element<T> element : new LinkedHashSet<Element<T>>(
                        this.elements)) {
                    // set the group to the represetative's group
                    element.setGroup(elementToRepresentative.get(element)
                            .getGroup());
                }

                break;
            }
        }
    }
}
