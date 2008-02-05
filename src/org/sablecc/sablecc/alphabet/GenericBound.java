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

import org.sablecc.sablecc.exception.InternalException;

/**
 * This class serves to simplify the implementation of bound subclasses.
 */
public abstract class GenericBound<T extends Comparable<? super T>>
        extends Bound {

    /** The value of this generic bound. */
    private final T value;

    /** Construct a generic bound with the provided value. */
    public GenericBound(
            T value) {

        if (value == null) {
            throw new InternalException("value may not be null");
        }

        this.value = value;
    }

    /** Returns the value of this generic bound. */
    protected T getValue() {

        return this.value;
    }

    /**
     * Returns true if the provided object is equal to this generic bound.
     */
    @SuppressWarnings("unchecked")
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

        GenericBound<T> bound = (GenericBound<T>) obj;

        return this.value.equals(bound.value);
    }

    /**
     * Returns the hash code of this generic bound.
     */
    @Override
    public int hashCode() {

        return this.value.hashCode();
    }

    /**
     * Returns the string representation of this interval.
     */
    @Override
    public String toString() {

        return this.value.toString();
    }

    /**
     * Compares this generic bound to the provided generic bound.
     */
    @SuppressWarnings("unchecked")
    public int compareTo(
            Bound bound) {

        if (bound == null) {
            throw new InternalException("bound may not be null");
        }

        if (getClass() != bound.getClass()) {
            throw new InternalException("bound must have the same class");
        }

        GenericBound<T> genericBound = (GenericBound<T>) bound;

        if (this.value.getClass() != genericBound.value.getClass()) {
            throw new InternalException("values must have the same class");
        }

        return this.value.compareTo(genericBound.value);
    }
}
