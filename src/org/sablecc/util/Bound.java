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

package org.sablecc.util;

import java.math.*;

import org.sablecc.exception.*;

public class Bound
        implements Comparable<Bound> {

    public static final Bound MIN = new Bound();

    public static final Bound MAX = new Bound();

    public static final Bound ZERO = new Bound(BigInteger.ZERO);

    public static final Bound ONE = new Bound(BigInteger.ONE);

    private final BigInteger value;

    private Bound() {

        this.value = null;
    }

    public Bound(
            BigInteger value) {

        if (value == null) {
            throw new InternalException("value may not be null");
        }

        this.value = value;
    }

    public Bound(
            char value) {

        this(new BigInteger(Long.toString(value)));
    }

    public Bound(
            String value) {

        this(new BigInteger(value));
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

        Bound bound = (Bound) obj;

        if (this.value == null || bound.value == null) {
            // this != obj, yet this or bound is MIN or MAX.
            return false;
        }

        return this.value.equals(bound.value);
    }

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

    public BigInteger getValue() {

        if (this.value == null) {
            throw new InternalException("no value");
        }

        return this.value;
    }

    public Bound add(
            BigInteger value) {

        if (value == null) {
            throw new InternalError("value shouldn't be null");
        }

        if (equals(MAX) || equals(MIN)) {

            return this;
        }
        return new Bound(this.value.add(value));
    }

    public Bound add(
            Bound bound) {

        if (bound == null) {
            throw new InternalError("value shouldn't be null");
        }

        if (equals(MAX) && bound.equals(MIN) || equals(MIN)
                && bound.equals(MAX)) {
            throw new InternalError("Canno't add + infinite adn - infinite");
        }

        if (bound.equals(MAX) || equals(MAX)) {
            return MAX;
        }
        else if (bound.equals(MIN) || equals(MIN)) {
            return MIN;
        }
        else {
            return new Bound(this.value.add(bound.getValue()));
        }
    }

    public Bound subtract(
            BigInteger value) {

        if (value == null) {
            throw new InternalError("value shouldn't be null");
        }

        if (equals(MAX) || equals(MIN)) {

            return this;
        }
        return new Bound(this.value.subtract(value));
    }

    public Bound subtract(
            Bound bound) {

        if (bound == null) {
            throw new InternalError("value shouldn't be null");
        }

        if (equals(MAX) && bound.equals(MAX) || equals(MIN)
                && bound.equals(MIN)) {
            throw new InternalError(
                    "Canno't subtract + infinite and - infinite");
        }

        if (bound.equals(MAX) || equals(MIN)) {
            return MIN;
        }
        else if (bound.equals(MIN) || equals(MAX)) {
            return MAX;
        }
        else {
            return new Bound(this.value.subtract(bound.getValue()));
        }
    }

    public Bound multiply(
            BigInteger value) {

        if (value == null) {
            throw new InternalError("value shouldn't be null");
        }

        if (equals(MAX) || equals(MIN)) {

            return this;
        }
        return new Bound(this.value.multiply(value));
    }

    public Bound divide(
            BigInteger value) {

        if (value == null) {
            throw new InternalError("value shouldn't be null");
        }

        if (equals(MAX) || equals(MIN)) {

            return this;
        }
        return new Bound(this.value.divide(value));
    }

    public Bound min(
            Bound aBound) {

        if (equals(MIN)) {
            return this;
        }

        if (aBound.equals(MIN)) {
            return aBound;
        }

        if (this.value.min(aBound.getValue()).equals(this.value)) {
            return this;
        }
        else {
            return aBound;
        }
    }

    public Bound max(
            Bound aBound) {

        if (equals(MAX)) {
            return this;
        }

        if (aBound.equals(MAX)) {
            return aBound;
        }

        if (this.value.max(aBound.getValue()).equals(this.value)) {
            return this;
        }
        else {
            return aBound;
        }
    }

}
