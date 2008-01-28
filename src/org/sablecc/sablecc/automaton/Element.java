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

import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

/**
 * An element represents a state. It is especially used in methods related to
 * transitions since they return groups.
 */
class Element {

    /** The partition of this element. */
    private final Partition partition;

    /** The state of this element. */
    private final DfaState state;

    /** The group of this element. */
    private Group group;

    /**
     * Constructs an element with the provided partition and state.
     * 
     * @param partition
     *            the partition.
     * @param state
     *            the state.
     * @throws InternalException
     *             if the provided partition or state is <code>null</code> or
     *             if the state is not from same <code>Dfa</code> as the
     *             partition.
     */
    Element(
            final Partition partition,
            final DfaState state) {

        if (partition == null) {
            throw new InternalException("partition may not be null");
        }

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        if (state.getDfa() != partition.getDfa()) {
            throw new InternalException("invalid state");
        }

        this.partition = partition;
        this.state = state;
        this.group = null;

        partition.addElement(this);
    }

    /**
     * Returns the partition of this element.
     * 
     * @return the partition.
     */
    Partition getPartition() {

        return this.partition;
    }

    /**
     * Returns the state of this element.
     * 
     * @return the state.
     */
    DfaState getState() {

        return this.state;
    }

    /**
     * Returns the group of this element.
     * 
     * @return the group.
     */
    Group getGroup() {

        return this.group;
    }

    /**
     * Sets the group to wich this instance belongs.
     * 
     * @param group
     *            the group.
     * @throws InternalException
     *             if the provided group is <code>null</code> or if it has a
     *             different partition as the one of this instance.
     */
    void setGroup(
            Group group) {

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        if (group.getPartition() != this.partition) {
            throw new InternalException("invalid group");
        }

        // Don't forget that this.group is initially null!
        if (this.group != null) {
            this.group.removeElement(this);
        }

        this.group = group;
        group.addElement(this);
    }

    /**
     * Returns the target group of the provided symbol.
     * 
     * @param symbol
     *            the symbol.
     * @return the target group.
     * @throws InternalException
     *             if the provided symbol is <code>null</code> or if it not
     *             part of this instance's partition.
     */
    Group getTarget(
            Symbol symbol) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        if (!this.partition.getDfa().getAlphabet().getSymbols()
                .contains(symbol)) {
            throw new InternalException("invalid symbol");
        }

        return this.partition.getElement(this.state.getTarget(symbol)).group;
    }
}
