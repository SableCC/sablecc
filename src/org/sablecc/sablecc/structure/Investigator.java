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
 * A investigator is defined by a name and a set of tokens. Each investigator
 * corresponds to a investigator defined in the grammar specification.
 */
public class Investigator
        implements Comparable<Investigator> {

    /** The name of this investigator. */
    private final String name;

    /** The tokens of this investigator. */
    private SortedSet<Token> tokens = new TreeSet<Token>();

    /** A stability status for this investigator. */
    private boolean isStable = false;

    /**
     * Constructs a investigator with the provided name.
     * 
     * @param name
     *            the name.
     * @throws InternalException
     *             if the provided name is <code>null</code>.
     */
    public Investigator(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.name = name;
    }

    /**
     * Returns the name of this investigator.
     * 
     * @return the name.
     */
    public String getName() {

        return this.name;
    }

    /**
     * Returns the set of tokens of this investigator.
     * 
     * @return the set of tokens.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<Token> getTokens() {

        if (!this.isStable) {
            throw new InternalException("this investigator is not stable yet");
        }

        return this.tokens;
    }

    /**
     * Returns whether this investigator is stable or not.
     * 
     * @return <code>true</code> if this investigator is stable,
     *         <code>false</code> otherwise.
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
     * @return <code>true</code> if this investigator and the object are
     *         equal; <code>false</code> otherwise.
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

        Investigator investigator = (Investigator) obj;

        return this.name.equals(investigator.name);
    }

    /**
     * Returns the hash code of this investigator.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {

        return this.name.hashCode();
    }

    /**
     * Compares this investigator to the provided one. The comparison is made by
     * comparing their names.
     * 
     * @param investigator
     *            the investigator to compare with.
     * @return an <code>int</code> value: <code>0</code> if the two
     *         investigators are the equals, a negative value if this
     *         investigator is smaller, and a positive value if it is bigger.
     */
    public int compareTo(
            Investigator investigator) {

        return this.name.compareTo(investigator.name);
    }

    /**
     * Stabilizes this investigator.
     * 
     * @throws InternalException
     *             if this instance is already stable or if this instance does
     *             not have at least 1 token.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this investigator is already stable");
        }

        if (this.tokens.size() < 1) {
            throw new InternalException("there must be at least one token");
        }

        this.tokens = Collections.unmodifiableSortedSet(this.tokens);
        this.isStable = true;
    }

    /**
     * Adds the provided token to this investigator.
     * 
     * @param token
     *            the token.
     * @throws InternalException
     *             if the provided token is <code>null</code> or if it is
     *             already in this investigator.
     */
    public void addToken(
            Token token) {

        if (token == null) {
            throw new InternalException("token may not be null");
        }

        if (this.tokens.contains(token)) {
            throw new InternalException(
                    "the token is already included in the token set");
        }

        this.tokens.add(token);
    }
}
