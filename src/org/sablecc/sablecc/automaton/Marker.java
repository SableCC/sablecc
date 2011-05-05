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

import org.sablecc.exception.*;

/**
 * A marker is used to mark states as starting point for back counts.
 */
public class Marker
        implements Comparable<Marker> {

    /**
     * The name of this marker.
     */
    private final String name;

    /**
     * The cached hash code of this marker. It is <code>null</code> when not yet
     * computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this marker. It is <code>null</code>
     * when not yet computed.
     */
    private String toString;

    /**
     * Constructs a marker with the provided name.
     */
    public Marker(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.name = name;
    }

    /**
     * Returns the name of this marker.
     */
    public String getName() {

        return this.name;
    }

    /**
     * Returns <code>true</code> when the provided object is equal to this
     * marker.
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

        Marker marker = (Marker) obj;

        return this.name.equals(marker.name);
    }

    /**
     * Returns the hash code of this marker.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            this.hashCode = this.name.hashCode();
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this marker.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("marker");
            sb.append("_");
            sb.append(this.name);

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Compares this marker to the provided marker.
     */
    @Override
    public int compareTo(
            Marker marker) {

        if (marker == null) {
            throw new InternalException("marker may not be null");
        }

        return this.name.compareTo(marker.name);
    }

}
