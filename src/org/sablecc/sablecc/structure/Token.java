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
import org.sablecc.sablecc.exception.InternalExceptionTest;

/**
 * A token is defined by its name and the group it belongs to. Each token
 * correspond to a token defined in the grammar specification.
 */
public class Token
        implements Comparable<Token> {

    /** The name of this token. */
    private final String name;

    /** The group of this token. */
    private final Group group;

    /**
     * Constructs a new token with the provided name and group.
     * 
     * @param name
     *            the name.
     * @param group
     *            the group.
     * @throws InternalExceptionTest
     *             if the provided name or group is <code>null</code>.
     */
    Token(
            String name,
            Group group) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (group == null) {
            throw new InternalException("group may not be null");
        }

        this.name = name;
        this.group = group;

        group.addToken(this);
    }

    /**
     * Returns the name of this token.
     * 
     * @return the name.
     */
    public String getName() {

        return this.name;
    }

    /**
     * Returns the group of this token.
     * 
     * @return the group.
     */
    public Group getGroup() {

        return this.group;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they are the same object or if they have equal names.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this token and the object are equal;
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

        Token token = (Token) obj;

        return this.name.equals(token.name);
    }

    /**
     * Returns the hash code of this token.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {

        return this.name.hashCode();
    }

    /**
     * Compares this token to the provided one. The comparison is made by
     * comparing their names.
     * 
     * @param token
     *            the token to compare with.
     * @return an <code>int</code> value: <code>0</code> if the two tokens
     *         are the equals, a negative value if this token is smaller, and a
     *         positive value if it is bigger.
     */
    public int compareTo(
            Token token) {

        return this.name.compareTo(token.name);
    }
}
