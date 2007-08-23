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

package org.sablecc.sablecc.structure;

import org.sablecc.sablecc.exception.InternalException;

/**
 * A state is defined by its name. Each state correspond to a state defined in
 * the grammar specification.
 */
public class State
        implements Comparable<State> {

    /** The name of this state. */
    private final String name;

    /**
     * Constructs a state with the provided name.
     * 
     * @param name
     *            the name.
     */
    State(
            String name) {

        this.name = name;
    }

    /**
     * Returns whether this state has a name or not.
     * 
     * @return <code>true</code> if this state has a name, <code>false</code>
     *         otherwise.
     */
    public boolean hasName() {

        return this.name != null;
    }

    /**
     * Returns the name of this state.
     * 
     * @return the name.
     * @throws InternalException
     *             if this state has no name.
     */
    public String getName() {

        if (this.name == null) {
            throw new InternalException("this state does not have a name");
        }

        return this.name;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they are the same object or if they have equal names.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this state and the object are equal;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(
            Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        State state = (State) obj;

        if (this.name == null) {
            return this.name == state.name;
        }

        return this.name.equals(state.name);
    }

    /**
     * Returns the hash code of this state.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {

        if (this.name == null) {
            return 0;
        }

        return this.name.hashCode();
    }

    /**
     * Compares this state to the provided one. The comparison is made by
     * comparing their names.
     * 
     * @param state
     *            the state to compare with.
     * @return an <code>int</code> value: <code>0</code> if the two states
     *         are the equals, a negative value if this state is smaller, and a
     *         positive value if it is bigger.
     */
    public int compareTo(
            State state) {

        if (this.name == null) {
            if (state.name == null) {
                return 0;
            }

            return -1;
        }
        else if (state == null) {
            return 1;
        }

        return this.name.compareTo(state.name);
    }
}
