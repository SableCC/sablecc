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

public class Group
        implements Comparable<Group> {

    private final String name;

    private SortedSet<Token> tokens = new TreeSet<Token>();

    private boolean isStable = false;

    Group(
            String name) {

        this.name = name;
    }

    public boolean hasName() {

        return this.name != null;
    }

    public String getName() {

        if (this.name == null) {
            throw new InternalException("this group does not have a name");
        }

        return this.name;
    }

    public SortedSet<Token> getTokens() {

        if (!this.isStable) {
            throw new InternalException("this group is not stable yet");
        }

        return this.tokens;
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

        Group group = (Group) obj;

        if (this.name == null) {
            return this.name == group.name;
        }

        return this.name.equals(group.name);
    }

    @Override
    public int hashCode() {

        if (this.name == null) {
            return 0;
        }

        return this.name.hashCode();
    }

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

    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this group is already stable");
        }

        this.tokens = Collections.unmodifiableSortedSet(this.tokens);
        this.isStable = true;
    }

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
