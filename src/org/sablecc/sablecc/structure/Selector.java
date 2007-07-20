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

public class Selector
        implements Comparable<Selector> {

    private final String name;

    private SortedSet<Token> selection = new TreeSet<Token>();

    private boolean isStable = false;

    Selector(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    public SortedSet<Token> getSelection() {

        if (!this.isStable) {
            throw new InternalException("this selector is not stable yet");
        }

        return this.selection;
    }

    boolean isStable() {

        return this.isStable;
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

        Selector selector = (Selector) obj;

        return this.name.equals(selector.name);
    }

    @Override
    public int hashCode() {

        return this.name.hashCode();
    }

    public int compareTo(
            Selector selector) {

        return this.name.compareTo(selector.name);
    }

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
