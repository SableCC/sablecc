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

public class State
        implements Comparable<State> {

    private final String name;

    State(
            String name) {

        this.name = name;
    }

    public boolean hasName() {

        return this.name != null;
    }

    public String getName() {

        if (this.name == null) {
            throw new InternalException("this state does not have a name");
        }

        return this.name;
    }

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

    @Override
    public int hashCode() {

        if (this.name == null) {
            return 0;
        }

        return this.name.hashCode();
    }

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
