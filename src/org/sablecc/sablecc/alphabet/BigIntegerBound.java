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

import java.math.BigInteger;

/**
 * This class encapsulates a big integer bound.
 */
public class BigIntegerBound
        extends GenericBound<BigInteger> {

    /** Constructs a big integer bound with the provided value. */
    public BigIntegerBound(
            BigInteger value) {

        super(value);
    }

    /** Constructs a big integer bound with the provided value. */
    public BigIntegerBound(
            String value) {

        super(new BigInteger(value));
    }

    /**
     * Constructs a integer bound with the provided value in the specified
     * radix.
     */
    public BigIntegerBound(
            String value,
            int radix) {

        super(new BigInteger(value, radix));
    }

    /** Returns the predecessor of this big integer bound. */
    @Override
    public Bound getPredecessor() {

        return new BigIntegerBound(getValue().subtract(BigInteger.ONE));
    }

    /** Returns the successor of this integer bound. */
    @Override
    public Bound getSuccessor() {

        return new BigIntegerBound(getValue().add(BigInteger.ONE));
    }
}
