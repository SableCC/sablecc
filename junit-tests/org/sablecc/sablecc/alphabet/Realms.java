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

public class Realms {

    private static final AdjacencyRealm<Integer> integer = new AdjacencyRealm<Integer>() {

        @Override
        public boolean isAdjacent(
                Integer bound1,
                Integer bound2) {

            return bound1 + 1 == bound2;
        }

        @Override
        public Integer next(
                Integer bound) {

            return bound + 1;
        }

        @Override
        public Integer previous(
                Integer bound) {

            return bound - 1;
        }
    };

    private static final AdjacencyRealm<BigInteger> bigInteger = new AdjacencyRealm<BigInteger>() {

        @Override
        public boolean isAdjacent(
                BigInteger bound1,
                BigInteger bound2) {

            return bound1.add(BigInteger.ONE).equals(bound2);
        }

        @Override
        public BigInteger next(
                BigInteger bound) {

            return bound.add(BigInteger.ONE);
        }

        @Override
        public BigInteger previous(
                BigInteger bound) {

            return bound.subtract(BigInteger.ONE);
        }

    };

    private static final AdjacencyRealm<Character> character = new AdjacencyRealm<Character>() {

        @Override
        public boolean isAdjacent(
                Character bound1,
                Character bound2) {

            return bound1 + 1 == bound2;
        }

        @Override
        public Character next(
                Character bound) {

            return (char) (bound + 1);
        }

        @Override
        public Character previous(
                Character bound) {

            return (char) (bound - 1);
        }

    };

    public static AdjacencyRealm<BigInteger> getBigInteger() {

        return bigInteger;
    }

    public static AdjacencyRealm<Integer> getInteger() {

        return integer;
    }

    public static AdjacencyRealm<Character> getCharacter() {

        return character;
    }

}
