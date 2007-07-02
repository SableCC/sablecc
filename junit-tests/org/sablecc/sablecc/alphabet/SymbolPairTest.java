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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class SymbolPairTest {

    private Symbol<Integer> symbol1Int;

    private Symbol<Integer> symbol2Int;

    private Collection<Interval<Integer>> intervalsInt = new LinkedList<Interval<Integer>>();

    private Symbol<BigInteger> symbol1BigInt;

    private Symbol<BigInteger> symbol2BigInt;

    private Collection<Interval<BigInteger>> intervalsBigInt = new LinkedList<Interval<BigInteger>>();

    @Before
    public void setUp() {

        // For Integer.
        this.intervalsInt.clear();
        this.intervalsInt
                .add(new Interval<Integer>(10, 20, Realms.getInteger()));
        this.intervalsInt.add(new Interval<Integer>(50, 100, Realms
                .getInteger()));
        this.intervalsInt.add(new Interval<Integer>(200, 400, Realms
                .getInteger()));

        this.symbol1Int = new Symbol<Integer>(this.intervalsInt);

        this.intervalsInt.clear();
        this.intervalsInt
                .add(new Interval<Integer>(30, 40, Realms.getInteger()));
        this.intervalsInt
                .add(new Interval<Integer>(75, 80, Realms.getInteger()));
        this.intervalsInt.add(new Interval<Integer>(150, 175, Realms
                .getInteger()));

        this.symbol2Int = new Symbol<Integer>(this.intervalsInt);

        // For BigInteger.
        this.intervalsBigInt.clear();
        this.intervalsBigInt.add(new Interval<BigInteger>(BigInteger.TEN,
                new BigInteger("20"), Realms.getBigInteger()));
        this.intervalsBigInt.add(new Interval<BigInteger>(new BigInteger("50"),
                new BigInteger("100"), Realms.getBigInteger()));
        this.intervalsBigInt.add(new Interval<BigInteger>(
                new BigInteger("200"), new BigInteger("400"), Realms
                        .getBigInteger()));

        this.symbol1BigInt = new Symbol<BigInteger>(this.intervalsBigInt);

        this.intervalsBigInt.clear();
        this.intervalsBigInt.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), Realms.getBigInteger()));
        this.intervalsBigInt.add(new Interval<BigInteger>(new BigInteger("75"),
                new BigInteger("80"), Realms.getBigInteger()));
        this.intervalsBigInt.add(new Interval<BigInteger>(
                new BigInteger("150"), new BigInteger("175"), Realms
                        .getBigInteger()));

        this.symbol2BigInt = new Symbol<BigInteger>(this.intervalsBigInt);

    }

    @Test
    public void testSymbolPair() {

        // Testing constructor with type Integer
        SymbolPair<Integer> symbolPairInt = new SymbolPair<Integer>(
                this.symbol1Int, this.symbol2Int);
        assertEquals("Constructed SymbolPair doesn't return good symbol1Int",
                this.symbol1Int, symbolPairInt.getSymbol1());
        assertEquals("Constructed SymbolPair doesn't return good symbol1Int",
                this.symbol2Int, symbolPairInt.getSymbol2());

        // Testing constructor with type BigInteger
        SymbolPair<BigInteger> symbolPairBigInt = new SymbolPair<BigInteger>(
                this.symbol1BigInt, this.symbol2BigInt);
        assertEquals(
                "Constructed SymbolPair doesn't return good symbol1BigInt",
                this.symbol1BigInt, symbolPairBigInt.getSymbol1());
        assertEquals(
                "Constructed SymbolPair doesn't return good symbol1BIgInt",
                this.symbol2BigInt, symbolPairBigInt.getSymbol2());

    }

    @Test
    public void testEqualsObject() {

        // =====================
        // Testing with integer.
        SymbolPair<Integer> symbolPairInt = new SymbolPair<Integer>(
                this.symbol1Int, this.symbol2Int);

        // Case with null object.
        assertFalse("A symbol pair and a null Object should not be equals.",
                symbolPairInt.equals(null));

        // Case with non symbol pair object.
        assertFalse(
                "A symbol pair and a non symbol pair object should not be equals.",
                symbolPairInt.equals(0));

        SymbolPair<Integer> symbolPairInt2 = new SymbolPair<Integer>(
                this.symbol1Int, this.symbol2Int);

        // Cases with null symbols in a pair.
        symbolPairInt = new SymbolPair<Integer>(null, this.symbol1Int);
        assertFalse(
                "A symbol pair with a null symbol and a non null object of type symbol pair should not be equals.",
                symbolPairInt.equals(symbolPairInt2));

        symbolPairInt = new SymbolPair<Integer>(this.symbol1Int, null);
        assertFalse(
                "A symbol pair with a null symbol and a non null object of type symbol pair should not be equals.",
                symbolPairInt.equals(symbolPairInt2));

        // Case with different symbol pairs.
        symbolPairInt = new SymbolPair<Integer>(this.symbol1Int,
                this.symbol2Int);
        symbolPairInt2 = new SymbolPair<Integer>(this.symbol2Int,
                this.symbol1Int);
        assertFalse("Two different symbol pairs should not be equals,",
                symbolPairInt.equals(symbolPairInt2));

        // Case with same symbol pairs.
        symbolPairInt2 = new SymbolPair<Integer>(this.symbol1Int,
                this.symbol2Int);
        assertTrue("Two identical symbol pairs should be equals.",
                symbolPairInt.equals(symbolPairInt2));

        // ========================
        // Testing with BigInteger.
        SymbolPair<BigInteger> symbolPairBigInt = new SymbolPair<BigInteger>(
                this.symbol1BigInt, this.symbol2BigInt);

        // Case with null object.
        assertFalse("A symbol pair and a null Object should not be equals.",
                symbolPairBigInt.equals(null));

        // Case with non symbol pair object.
        assertFalse(
                "A symbol pair and a non symbol pair object should not be equals.",
                symbolPairBigInt.equals(0));

        SymbolPair<BigInteger> symbolPairBigInt2 = new SymbolPair<BigInteger>(
                this.symbol1BigInt, this.symbol2BigInt);

        // Cases with null symbols in a pair.
        symbolPairBigInt = new SymbolPair<BigInteger>(null, this.symbol1BigInt);
        assertFalse(
                "A symbol pair with a null symbol and a non null object of type symbol pair should not be equals.",
                symbolPairBigInt.equals(symbolPairBigInt2));

        symbolPairBigInt = new SymbolPair<BigInteger>(this.symbol1BigInt, null);
        assertFalse(
                "A symbol pair with a null symbol and a non null object of type symbol pair should not be equals.",
                symbolPairBigInt.equals(symbolPairBigInt2));

        // Case with different symbol pairs.
        symbolPairBigInt = new SymbolPair<BigInteger>(this.symbol1BigInt,
                this.symbol2BigInt);
        symbolPairBigInt2 = new SymbolPair<BigInteger>(this.symbol2BigInt,
                this.symbol1BigInt);
        assertFalse("Two different symbol pairs should not be equals,",
                symbolPairBigInt.equals(symbolPairBigInt2));

        // Case with same symbol pairs.
        symbolPairBigInt2 = new SymbolPair<BigInteger>(this.symbol1BigInt,
                this.symbol2BigInt);
        assertTrue("Two identical symbol pairs should be equals.",
                symbolPairBigInt.equals(symbolPairBigInt2));

    }

}
