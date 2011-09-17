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

package org.sablecc.sablecc.alphabet;

import static org.sablecc.util.UsefulStaticImports.*;

import java.math.*;

import org.sablecc.exception.*;

/**
 * A bound is a value that is used to construct intervals.
 */
public class Bound
        implements Comparable<Bound> {

    /**
     * The minimal bound.
     */
    public static final Bound MIN = new Bound();

    /**
     * The maximal bound.
     */
    public static final Bound MAX = new Bound();

    /**
     * The value of this bound. It is <code>null</code> for MIN and MAX.
     */
    private final BigInteger value;

    /**
     * The cached hash code of this bound. It is <code>null</code> when not yet
     * computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this bound. It is <code>null</code>
     * when not yet computed.
     */
    private String toString;

    /**
     * Constructs a constant bound. Serves to construct MIN and MAX.
     */
    private Bound() {

        this.value = null;
    }

    /**
     * Constructs a bound with the provided value.
     */
    public Bound(
            BigInteger value) {

        if (value == null) {
            throw new InternalException("value may not be null");
        }

        this.value = value;
    }

    /**
     * Constructs a bound with the provided value.
     */
    public Bound(
            char value) {

        this(new BigInteger(Integer.toString(value)));
    }

    /**
     * Constructs a bound with the provided value.
     */
    public Bound(
            String value) {

        this(new BigInteger(value));
    }

    /**
     * Constructs a bound with the provided value in the specified radix.
     */
    public Bound(
            String value,
            int radix) {

        this(new BigInteger(value, radix));
    }

    /**
     * Returns the predecessor of this bound.
     */
    public Bound getPredecessor() {

        if (this.value == null) {
            if (this == MIN) {
                throw new InternalException("cannot get predecessor of MIN");
            }
            else {
                assert this == MAX;
                throw new InternalException("cannot get predecessor of MAX");
            }
        }

        return new Bound(this.value.subtract(BigInteger.ONE));
    }

    /**
     * Returns the successor of this bound.
     */
    public Bound getSuccessor() {

        if (this.value == null) {
            if (this == MIN) {
                throw new InternalException("cannot get successor of MIN");
            }
            else {
                assert this == MAX;
                throw new InternalException("cannot get successor of MAX");
            }
        }

        return new Bound(this.value.add(BigInteger.ONE));
    }

    /**
     * Returns <code>true</code> when the provided object is equal to this
     * bound.
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

        Bound bound = (Bound) obj;

        if (this.value == null || bound.value == null) {
            // this != obj, yet this or bound is MIN or MAX.
            return false;
        }

        return this.value.equals(bound.value);
    }

    /**
     * Returns the hash code of this bound.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            if (this.value == null) {
                this.hashCode = System.identityHashCode(this);
            }
            else {
                this.hashCode = this.value.hashCode();
            }
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this bound.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            if (this.value == null) {
                if (this == MIN) {
                    this.toString = "MIN";
                }
                else {
                    this.toString = "MAX";
                }
            }
            else {
                if (this.value.compareTo(BI_32) < 0
                        || this.value.compareTo(BI_126) > 0) {
                    this.toString = "#" + this.value.toString();
                }
                else {
                    char c = (char) Integer.parseInt(this.value.toString());
                    this.toString = "'" + c + "'";
                }
            }
        }

        return this.toString;
    }

    /**
     * Compares this bound to the provided bound.
     */
    @Override
    public int compareTo(
            Bound bound) {

        if (bound == null) {
            throw new InternalException("bound may not be null");
        }

        if (getClass() != bound.getClass()) {
            throw new InternalException("bound must have the same class");
        }

        if (this.value == null || bound.value == null) {
            if (this == MIN) {
                return bound == MIN ? 0 : -1;
            }

            if (this == MAX) {
                return bound == MAX ? 0 : 1;
            }

            if (bound == MIN) {
                return 1;
            }

            return -1;
        }

        return this.value.compareTo(bound.value);
    }

    /**
     * Returns the minimum of two bounds.
     */
    public static Bound min(
            Bound bound1,
            Bound bound2) {

        if (bound1.compareTo(bound2) <= 0) {
            return bound1;
        }

        return bound2;
    }

    /**
     * Returns the maximum of two bounds.
     */
    public static Bound max(
            Bound bound1,
            Bound bound2) {

        if (bound1.compareTo(bound2) >= 0) {
            return bound1;
        }

        return bound2;
    }

    public String getSimpleName() {

        if (this.value == null) {
            if (this == MIN) {
                return "min";
            }

            return "max";
        }

        if (this.value.compareTo(BI_32) < 0 || this.value.compareTo(BI_126) > 0) {
            return this.value.toString();
        }

        char c = (char) Integer.parseInt(this.value.toString());

        if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A'
                && c <= 'Z') {
            return "_" + c;
        }

        return "" + (int) c;
    }

    public BigInteger getValue() {

        if (this.value == null) {
            throw new InternalException("no value");
        }

        return this.value;
    }
}
