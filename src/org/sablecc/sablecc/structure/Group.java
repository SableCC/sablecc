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

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.sablecc.sablecc.exception.InternalException;

/**
 * A group is defined by a name and a set of tokens. Each group corresponds to a
 * group defined in the grammar specification.
 */
public class Group
        implements Comparable<Group> {

    /** The name of this group. */
    private final String name;

    /** The tokens of this group. */
    private SortedSet<Token> tokens = new TreeSet<Token>();

    /** A stability status for this group. */
    private boolean isStable = false;

    /**
     * Constructs a group with the provided name.
     * 
     * @param name
     *            the name.
     */
    Group(
            String name) {

        this.name = name;
    }

    /**
     * Returns whether this group has a name or not.
     * 
     * @return <code>true</code> if this group has a name, <code>false</code>
     *         otherwise.
     */
    public boolean hasName() {

        return this.name != null;
    }

    /**
     * Returns the name of this group.
     * 
     * @return the name.
     * @throws InternalException
     *             if this group has no name.
     */
    public String getName() {

        if (this.name == null) {
            throw new InternalException("this group does not have a name");
        }

        return this.name;
    }

    /**
     * Returns the tokens of this group.
     * 
     * @return the set of tokens.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<Token> getTokens() {

        if (!this.isStable) {
            throw new InternalException("this group is not stable yet");
        }

        return this.tokens;
    }

    /**
     * Returns whether this group is stable or not.
     * 
     * @return <code>true</code> if this group is stable, <code>false</code>
     *         otherwise.
     */
    boolean isStable() {

        return this.isStable;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they are the same object or if they have equal names.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this group and the object are equal;
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

        Group group = (Group) obj;

        if (this.name == null) {
            return this.name == group.name;
        }

        return this.name.equals(group.name);
    }

    /**
     * Returns the hash code of this group.
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
     * Compares this group to the provided one. The comparison is made by
     * comparing their names.
     * 
     * @param group
     *            the group to compare with.
     * @return an <code>int</code> value: <code>0</code> if the two groups
     *         are the equals, a negative value if this group is smaller, and a
     *         positive value if it is bigger.
     */
    public int compareTo(
            Group group) {

        if (this.name == null) {
            if (group.name == null) {
                return 0;
            }

            return -1;
        }
        else if (group == null) {
            return 1;
        }

        return this.name.compareTo(group.name);
    }

    /**
     * Stabilizes this group.
     * 
     * @throws InternalException
     *             if this instance is already stable.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this group is already stable");
        }

        this.tokens = Collections.unmodifiableSortedSet(this.tokens);
        this.isStable = true;
    }

    /**
     * Adds the provided token to this group.
     * 
     * @param token
     *            the token.
     * @throws InternalException
     *             if this instance is stable or if the provided token is
     *             <code>null</code>, is already in this group or does not
     *             belong to it.
     */
    void addToken(
            Token token) {

        if (token == null) {
            throw new InternalException("token may not be null");
        }

        if (this.isStable) {
            throw new InternalException("a stable group may not be modified");
        }

        if (this.tokens.contains(token)) {
            throw new InternalException(
                    "the token is already included in this group");
        }

        if (token.getGroup() != this) {
            throw new InternalException(
                    "the token does not belong to this group");
        }

        this.tokens.add(token);
    }
}
