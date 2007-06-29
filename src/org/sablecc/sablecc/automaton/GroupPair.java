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

class GroupPair<T extends Comparable<? super T>> {

    private final Group<T> group1;

    private final Group<T> group2;

    private Integer hashcode;

    private String toString;

    GroupPair(
            final Group<T> group1,
            final Group<T> group2) {

        if (group1 == null) {
            throw new InternalException("group1 may not be null");
        }

        if (group2 == null) {
            throw new InternalException("group2 may not be null");
        }

        this.group1 = group1;
        this.group2 = group2;
    }

    Group<T> getGroup1() {

        return this.group1;
    }

    Group<T> getGroup2() {

        return this.group2;
    }

    @Override
    public boolean equals(
            Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof GroupPair)) {
            return false;
        }

        GroupPair groupPair = (GroupPair) obj;

        return this.group1.equals(groupPair.group1)
                && this.group2.equals(groupPair.group2);
    }

    @Override
    public int hashCode() {

        if (this.hashcode == null) {
            this.hashcode = this.group1.hashCode() + 29
                    * this.group2.hashCode();
        }

        return this.hashcode;
    }

    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "(" + this.group1 + "," + this.group2 + ")";
        }

        return this.toString;
    }
}
