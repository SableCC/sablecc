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

class Element<T extends Comparable<? super T>> {

    private final Partition<T> partition;

    private final DfaState<T> state;

    private Group<T> group;

    Element(
            final Partition<T> partition,
            final DfaState<T> state) {

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

    Partition<T> getPartition() {

        return this.partition;
    }

    DfaState<T> getState() {

        return this.state;
    }

    Group<T> getGroup() {

        return this.group;
    }

    void setGroup(
            Group<T> group) {

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

    Group<T> getTarget(
            Symbol<T> symbol) {

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
