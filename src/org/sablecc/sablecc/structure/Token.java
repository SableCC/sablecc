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

public class Token
        implements Comparable<Token> {

    private final String name;

    private final Group group;

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

    public String getName() {

        return this.name;
    }

    public Group getGroup() {

        return this.group;
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

        Token token = (Token) obj;

        return this.name.equals(token.name);
    }

    @Override
    public int hashCode() {

        return this.name.hashCode();
    }

    public int compareTo(
            Token token) {

        return this.name.compareTo(token.name);
    }

}
