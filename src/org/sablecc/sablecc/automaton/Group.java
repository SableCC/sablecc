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
class Group {

    /** The partition related to this group. */
    private final Partition partition;

    /** The set of elements in this group. */
    private final Set<Element> elements = new LinkedHashSet<Element>();

    /** The minimal state of this group. */
    private MinimalDfaState state;

    /** The map of transitions of this group. */
    private final SortedMap<Symbol, Group> transitions = new TreeMap<Symbol, Group>();

    /**
     * Constructs a group with the provided partition.
     */
    Group(
            final Partition partition) {

        if (partition == null) {
            throw new InternalException("partition may not be null");
        }

        this.partition = partition;

        this.partition.addGroup(this);
    }

    /**
     * Returns the partition of this group.
     */
    Partition getPartition() {

        return this.partition;
    }

    /**
     * Returns the set of elements of this group.
     */
    Set<Element> getElements() {

        return Collections.unmodifiableSet(this.elements);
    }

    /**
     * Returns the state of this group.
     */
    MinimalDfaState getState() {

        return this.state;
    }

    /**
     * Returns the transitions of this group.
     */
    SortedMap<Symbol, Group> getTransitions() {

        return Collections.unmodifiableSortedMap(this.transitions);
    }

    /**
     * Returns the string representation of this group.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Group: {");
        for (Element element : this.elements) {
            sb.append(element.getState());
            sb.append(",");
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * Set the state of this group with the provided state.
     */
    void setState(
            MinimalDfaState state) {

        this.state = state;
    }

    /**
     * Adds a new element to this group.
     */
    void addElement(
            Element element) {

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
     */
    void removeElement(
            Element element) {

        if (element == null) {
            throw new InternalException("element may not be null");
        }

        if (!this.elements.remove(element)) {
            throw new InternalException("element is not in this group");
        }
    }

    /**
     * Adds a new transition to this group.
     */
    void addTransition(
            Symbol symbol,
            Group group) {

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
     */
    void refine() {

        for (Symbol symbol : this.partition.getDfa().getAlphabet().getSymbols()) {

            // for each new target, remember the first source (or
            // representative) element that targeted to it
            Map<Group, Element> targetToRepresentative = new LinkedHashMap<Group, Element>();
            Map<Element, Element> elementToRepresentative = new LinkedHashMap<Element, Element>();

            for (Element element : this.elements) {
                Group target = element.getTarget(symbol);

                Element representative = targetToRepresentative.get(target);

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
                for (Map.Entry<Group, Element> entry : targetToRepresentative
                        .entrySet()) {
                    if (first) {
                        // skip
                        first = false;
                    }
                    else {
                        entry.getValue().setGroup(new Group(this.partition));
                    }
                }

                // attach elements to new groups

                // we get a copy so that modifications to this.elements won't
                // disturb the iterator
                for (Element element : new LinkedHashSet<Element>(this.elements)) {
                    // set the group to the represetative's group
                    element.setGroup(elementToRepresentative.get(element)
                            .getGroup());
                }

                break;
            }
        }
    }
}
