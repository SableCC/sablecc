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
 * An acceptation represents the nature of an accept automaton state. A
 * constant, <code>ACCEPT</code>, is provided for generic acceptance.
 */
public class Acceptation
        implements Comparable<Acceptation> {

    /**
     * A generic acceptation.
     */
    public static final Acceptation ACCEPT = new Acceptation();

    /**
     * The name of this acceptation. It is <code>null</code> for ACCEPT.
     */
    private final String name;

    /**
     * The back count of this acceptation.
     */
    private final Integer backCount;

    /**
     * The marker of this acceptation.
     */
    private final Marker marker;

    /**
     * The cached hash code of this acceptation. It is <code>null</code> when
     * not yet computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this acceptation. It is
     * <code>null</code> when not yet computed.
     */
    private String toString;

    /**
     * Constructs a constant acceptation. Serves to construct ACCEPT.
     */
    private Acceptation() {

        this.name = null;
        this.backCount = null;
        this.marker = null;
    }

    /**
     * Constructs an acceptation with the provided name.
     */
    public Acceptation(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.name = name;
        this.backCount = null;
        this.marker = null;
    }

    /**
     * Constructs an acceptation with the provided name, back count, and marker.
     */
    public Acceptation(
            String name,
            int backCount,
            Marker marker) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (backCount < 0) {
            throw new InternalException("invalid back count");
        }

        this.name = name;
        this.backCount = backCount;
        this.marker = marker;
    }

    /**
     * Returns the name of this acceptation.
     */
    public String getName() {

        if (this.name == null) {
            throw new InternalException("this acceptation does not have a name");
        }

        return this.name;
    }

    /**
     * Returns the back count of this acceptation.
     */
    public int getBackCount() {

        if (this.backCount == null) {
            throw new InternalException(
                    "this accepation does not have a back count");
        }

        return this.backCount;
    }

    /**
     * Returns the marker of this acceptation.
     */
    public Marker getMarker() {

        return this.marker;
    }

    /**
     * Returns <code>true</code> when the provided object is equal to this
     * acceptation.
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

        Acceptation acceptation = (Acceptation) obj;

        if (this.name == null) {
            // this instance is ACCEPT we already know that acceptation is
            // neither ACCEPT nor null
            throw new InternalException(
                    "this acceptation is incomparable to the provided acceptation");
        }

        if (this.backCount == null || acceptation.backCount == null) {
            if (this.backCount != acceptation.backCount) {
                throw new InternalException(
                        "this acceptation is incomparable to the provided acceptation");
            }

            return this.name.equals(acceptation.name);
        }

        if (!this.name.equals(acceptation.name)) {
            return false;
        }

        if (!this.backCount.equals(acceptation.backCount)) {
            return false;
        }

        if (this.marker == null) {
            return acceptation.marker == null;
        }

        return this.marker.equals(acceptation.marker);
    }

    /**
     * Returns the hash code of this acceptation.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            int hashCode = 0;

            if (this.name != null) {
                hashCode = this.name.hashCode();
            }

            hashCode *= 211;

            if (this.backCount != null) {
                hashCode += this.backCount;
            }

            hashCode *= 223;

            if (this.marker != null) {
                hashCode += this.marker.hashCode();
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this acceptation.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("accept");
            if (this.name != null) {
                sb.append("_");
                sb.append(this.name);
            }

            if (this.backCount != null) {
                sb.append("(");
                sb.append(this.backCount);

                if (this.marker != null) {
                    sb.append(",");
                    sb.append(this.marker);
                }

                sb.append(")");
            }

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Compares this acceptation to the provided acceptation.
     */
    @Override
    public int compareTo(
            Acceptation acceptation) {

        if (acceptation == null) {
            throw new InternalException("acceptation may not be null");
        }

        if (this.name == null) {
            if (acceptation.name != null) {
                throw new InternalException(
                        "this acceptation is incomparable to the provided acceptation");
            }

            return 0;
        }

        if (this.backCount == null || acceptation.backCount == null) {
            if (this.backCount != acceptation.backCount) {
                throw new InternalException(
                        "this acceptation is incomparable to the provided acceptation");
            }

            return this.name.compareTo(acceptation.name);
        }

        int result = this.name.compareTo(acceptation.name);

        if (result == 0) {
            result = this.backCount.compareTo(acceptation.backCount);
        }

        if (result == 0) {
            if (this.marker == null) {
                result = acceptation.marker == null ? 0 : -1;
            }
            else if (acceptation.marker == null) {
                result = 1;
            }
            else {
                result = this.marker.compareTo(acceptation.marker);
            }
        }

        return result;
    }
}
