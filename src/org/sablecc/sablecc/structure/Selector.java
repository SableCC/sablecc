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
 * A selector is defined by a name and a selection (set of tokens). Each
 * selector corresponds to a selector defined in the grammar specification.
 */
public class Selector
        implements Comparable<Selector> {

    /** The name of this selector. */
    private final String name;

    /** The selection of token of this selector. */
    private SortedSet<Token> selection = new TreeSet<Token>();

    /** A stability status for this selector. */
    private boolean isStable = false;

    /**
     * Constructs a selector with the provided name.
     * 
     * @param name
     *            the name.
     * @throws InternalException
     *             if the provided name is <code>null</code>.
     */
    Selector(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.name = name;
    }

    /**
     * Returns the name of this selector.
     * 
     * @return the name.
     */
    public String getName() {

        return this.name;
    }

    /**
     * Returns the set of tokens of this instance's selection.
     * 
     * @return the set of tokens.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<Token> getSelection() {

        if (!this.isStable) {
            throw new InternalException("this selector is not stable yet");
        }

        return this.selection;
    }

    /**
     * Returns whether this selector is stable or not.
     * 
     * @return <code>true</code> if this selector is stable,
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
     * @return <code>true</code> if this selector and the object are equal;
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

        Selector selector = (Selector) obj;

        return this.name.equals(selector.name);
    }

    /**
     * Returns the hash code of this selector.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {

        return this.name.hashCode();
    }

    /**
     * Compares this selector to the provided one. The comparison is made by
     * comparing their names.
     * 
     * @param selector
     *            the selector to compare with.
     * @return an <code>int</code> value: <code>0</code> if the two
     *         selectors are the equals, a negative value if this selector is
     *         smaller, and a positive value if it is bigger.
     */
    public int compareTo(
            Selector selector) {

        return this.name.compareTo(selector.name);
    }

    /**
     * Stabilizes this selector.
     * 
     * @throws InternalException
     *             if this instance is already stable or if this instance's
     *             selection has less than 2 tokens.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this selector is already stable");
        }

        if (this.selection.size() < 2) {
            throw new InternalException("the selection is too small");
        }

        this.selection = Collections.unmodifiableSortedSet(this.selection);
        this.isStable = true;
    }

    /**
     * Adds the provided token to this instance's selection.
     * 
     * @param token
     *            the token.
     * @throws InternalException
     *             if the provided token is <code>null</code> or if it is
     *             already in this selection.
     */
    public void addToken(
            Token token) {

        if (token == null) {
            throw new InternalException("token may not be null");
        }

        if (this.selection.contains(token)) {
            throw new InternalException(
                    "the token is already included in the selection");
        }

        this.selection.add(token);
    }
}
