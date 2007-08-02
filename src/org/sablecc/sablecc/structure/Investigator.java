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

public class Investigator
        implements Comparable<Investigator> {

    private final String name;

    private SortedSet<Token> tokens = new TreeSet<Token>();

    private boolean isStable = false;

    public Investigator(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    public SortedSet<Token> getTokens() {

        if (!this.isStable) {
            throw new InternalException("this investigator is not stable yet");
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

        Investigator investigator = (Investigator) obj;

        return this.name.equals(investigator.name);
    }

    @Override
    public int hashCode() {

        return this.name.hashCode();
    }

    public int compareTo(
            Investigator investigator) {

        return this.name.compareTo(investigator.name);
    }

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
