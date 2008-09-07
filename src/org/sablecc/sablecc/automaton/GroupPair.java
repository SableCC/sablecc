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

import org.sablecc.sablecc.exception.InternalException;

/**
 * A group pair is a pair <code>(x,y)</code> where each of <code>x</code> and
 * <code>y</code> is a group.
 */
class GroupPair {

    /** The first group of this pair. */
    private final Group group1;

    /** The second group of this pair. */
    private final Group group2;

    /** Cached hashcode. Is <code>null</code> when not yet computed. */
    private Integer hashcode;

    /**
     * Cached string representation. Is <code>null</code> when not yet computed.
     */
    private String toString;

    /**
     * Constructs a group pair with the two provided groups.
     */
    GroupPair(
            final Group group1,
            final Group group2) {

        if (group1 == null) {
            throw new InternalException("group1 may not be null");
        }

        if (group2 == null) {
            throw new InternalException("group2 may not be null");
        }

        this.group1 = group1;
        this.group2 = group2;
    }

    /**
     * Returns the first group of this group pair.
     */
    Group getGroup1() {

        return this.group1;
    }

    /**
     * Returns the second group of this group pair.
     */
    Group getGroup2() {

        return this.group2;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they both have equal groups.
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

        GroupPair groupPair = (GroupPair) obj;

        return this.group1.equals(groupPair.group1)
                && this.group2.equals(groupPair.group2);
    }

    /**
     * Returns the hash code of this group pair.
     */
    @Override
    public int hashCode() {

        if (this.hashcode == null) {
            this.hashcode = this.group1.hashCode() + 29
                    * this.group2.hashCode();
        }

        return this.hashcode;
    }

    /**
     * Returns the string representation of this group pair.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "(" + this.group1 + "," + this.group2 + ")";
        }

        return this.toString;
    }
}
