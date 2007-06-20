/* This file is part of SableCC (http://sablecc.org/).
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
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.exception.InternalException;

public class SymbolTest {

    private Symbol<Integer> symbolInt;

    private Collection<Interval<Integer>> intervalsInt = new LinkedList<Interval<Integer>>();

    private final AdjacencyRealm<Integer> integerAdjacencyRealm = new AdjacencyRealm<Integer>() {

        @Override
        public boolean isAdjacent(
                Integer bound1,
                Integer bound2) {

            return bound1 + 1 == bound2;
        }

        @Override
        public Integer next(
                Integer bound) {

            return bound++;
        }

        @Override
        public Integer previous(
                Integer bound) {

            return bound--;
        }
    };

    private Symbol<BigInteger> symbolBig;

    private Collection<Interval<BigInteger>> intervalsBig = new LinkedList<Interval<BigInteger>>();

    private final AdjacencyRealm<BigInteger> bigIntegerAdjacencyRealm = new AdjacencyRealm<BigInteger>() {

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

    @Before
    public void setUp()
            throws Exception {

        // For Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(10, 20,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(50, 100,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(200, 400,
                this.integerAdjacencyRealm));

        this.symbolInt = new Symbol<Integer>(this.intervalsInt);

        // For BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(BigInteger.ZERO,
                BigInteger.TEN, this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("50"),
                new BigInteger("100"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("200"),
                new BigInteger("400"), this.bigIntegerAdjacencyRealm));

        this.symbolBig = new Symbol<BigInteger>(this.intervalsBig);
    }

    @Test
    public void testHashCode() {

        // For Integer.
        Symbol<Integer> sameSymbolInt = new Symbol<Integer>(this.intervalsInt);
        assertEquals(
                "Two instance of the same symbol should have the same hashcode",
                this.symbolInt.hashCode(), sameSymbolInt.hashCode());

        // For BigInteger.
        Symbol<BigInteger> sameSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);
        assertEquals(
                "Two instance of the same symbol should have the same hashcode",
                this.symbolBig.hashCode(), sameSymbolBig.hashCode());
    }

    @SuppressWarnings("unused")
    @Test
    public void testSymbolCollectionOfIntervalOfT() {

        // Case with null collection.
        Collection<Interval<Integer>> intervalsNull = null;

        try {
            Symbol<Integer> symbolNullCollection = new Symbol<Integer>(
                    intervalsNull);

            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with empty collection.
        Collection<Interval<Integer>> intervalsEmpty = new LinkedList<Interval<Integer>>();

        try {
            Symbol<Integer> symbolEmptyCollection = new Symbol<Integer>(
                    intervalsEmpty);

            fail("an InternalException should be thrown");
        }
        catch (InternalException e) {
            // excepted
        }

        // Case with collection having a null interval.
        Collection<Interval<Integer>> intervalsNullInterval = new LinkedList<Interval<Integer>>();

        try {
            intervalsNullInterval.add(new Interval<Integer>(null,
                    this.integerAdjacencyRealm));

            fail("an InternalException should be thrown");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with collection having intersecting intervals with Integer.
        Collection<Interval<Integer>> intervalsIntersectingIntervalsInt = new LinkedList<Interval<Integer>>();
        intervalsIntersectingIntervalsInt.add(new Interval<Integer>(0, 50,
                this.integerAdjacencyRealm));
        intervalsIntersectingIntervalsInt.add(new Interval<Integer>(25, 999,
                this.integerAdjacencyRealm));

        try {
            Symbol<Integer> symbolIntersectingIntervalsInt = new Symbol<Integer>(
                    intervalsIntersectingIntervalsInt);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with collection having intersecting intervals with BigInteger.
        Collection<Interval<BigInteger>> intervalsIntersectingIntervalsBig = new LinkedList<Interval<BigInteger>>();
        intervalsIntersectingIntervalsBig.add(new Interval<BigInteger>(
                new BigInteger("0"), new BigInteger("50"),
                this.bigIntegerAdjacencyRealm));
        intervalsIntersectingIntervalsBig.add(new Interval<BigInteger>(
                new BigInteger("25"), new BigInteger("999"),
                this.bigIntegerAdjacencyRealm));

        try {
            Symbol<BigInteger> symbolIntersectingIntervalsBig = new Symbol<BigInteger>(
                    intervalsIntersectingIntervalsBig);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with collection having adjacent intervals using Integer.
        Collection<Interval<Integer>> intervalsAdjacentIntervalsInt = new LinkedList<Interval<Integer>>();
        intervalsAdjacentIntervalsInt.add(new Interval<Integer>(0, 10,
                this.integerAdjacencyRealm));
        intervalsAdjacentIntervalsInt.add(new Interval<Integer>(11, 30,
                this.integerAdjacencyRealm));
        intervalsAdjacentIntervalsInt.add(new Interval<Integer>(31, 40,
                this.integerAdjacencyRealm));
        Symbol<Integer> symbolAdjacentIntervalsInt = new Symbol<Integer>(
                intervalsAdjacentIntervalsInt);

        assertTrue(
                "there should be only this interval in the symbol :[0..40].",
                symbolAdjacentIntervalsInt.getIntervals().size() == 1);

        // Case with collecting having adjacent intervals using BigInteger.
        Collection<Interval<BigInteger>> intervalsAdjacentIntervalsBig = new LinkedList<Interval<BigInteger>>();
        intervalsAdjacentIntervalsBig
                .add(new Interval<BigInteger>(BigInteger.ZERO, BigInteger.ONE,
                        this.bigIntegerAdjacencyRealm));
        intervalsAdjacentIntervalsBig.add(new Interval<BigInteger>(
                new BigInteger("2"), BigInteger.TEN,
                this.bigIntegerAdjacencyRealm));
        intervalsAdjacentIntervalsBig.add(new Interval<BigInteger>(
                new BigInteger("11"), new BigInteger("50"),
                this.bigIntegerAdjacencyRealm));
        Symbol<BigInteger> symbolAdjacentIntervalsBig = new Symbol<BigInteger>(
                intervalsAdjacentIntervalsBig);

        assertTrue(
                "There should be only this interval in the symbol :[0..50].",
                symbolAdjacentIntervalsBig.getIntervals().size() == 1);
    }

    @SuppressWarnings("unused")
    @Test
    public void testSymbolIntervalOfT() {

        // Case with collection having a null interval.
        Collection<Interval<Integer>> intervalsNullInterval = new LinkedList<Interval<Integer>>();

        try {
            intervalsNullInterval.add(new Interval<Integer>(null,
                    this.integerAdjacencyRealm));

            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Typical Case using Integer.
        Symbol<Integer> intervalsSingleIntervalInt = new Symbol<Integer>(
                new Interval<Integer>(10, 30, this.integerAdjacencyRealm));

        assertTrue("There should be only one Interval in the symbol",
                intervalsSingleIntervalInt.getIntervals().size() == 1);

        // Typical Case using BigInteger.
        Symbol<BigInteger> intervalsSingleIntervalBig = new Symbol<BigInteger>(
                new Interval<BigInteger>(new BigInteger("1000"),
                        new BigInteger("50000"), this.bigIntegerAdjacencyRealm));

        assertTrue("There should be only one Interval in the symbol",
                intervalsSingleIntervalBig.getIntervals().size() == 1);
    }

    @Test
    public void testEqualsObject() {

        // Case with null object.
        assertFalse("A symbol and a null Object should not be equals.",
                this.symbolInt.equals(null));

        // Case with not an instance of symbol.
        assertFalse(
                "A symbol and an object with instance other than Symbol should not be equals.",
                this.symbolInt.equals(2));

        // Case with same symbols using Integer.
        Symbol<Integer> sameSymbolInt = new Symbol<Integer>(this.intervalsInt);
        assertTrue("The two symbols should be equals.", this.symbolInt
                .equals(sameSymbolInt));

        // Case with same symbols using BigInteger.
        Symbol<BigInteger> sameSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);
        assertTrue("The two symbols should be equals.", this.symbolBig
                .equals(sameSymbolBig));

        // Case with different symbol using Integer.
        this.intervalsInt.add(new Interval<Integer>(500, 1000,
                this.integerAdjacencyRealm));
        Symbol<Integer> differentSymbolInt = new Symbol<Integer>(
                this.intervalsInt);

        assertFalse("Two symbols having different size should not be equals",
                this.symbolInt.equals(differentSymbolInt));

        // Case with different symbol using BigInteger.
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("999"),
                new BigInteger("9999"), this.bigIntegerAdjacencyRealm));
        Symbol<BigInteger> differentSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);

        assertFalse("Two symbols having different size should not be equals",
                this.symbolBig.equals(differentSymbolBig));

    }

    @Test
    public void testCompareTo() {

        // Case with smaller symbol using Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(0, 10,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(30, 50,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(100, 200,
                this.integerAdjacencyRealm));
        Symbol<Integer> smallerSymbolInt = new Symbol<Integer>(
                this.intervalsInt);

        assertTrue("smallerSymbolInt should be smaller.", smallerSymbolInt
                .compareTo(this.symbolInt) < 0);

        // Case with smaller symbol using BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(BigInteger.ZERO,
                BigInteger.ONE, this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("10"),
                new BigInteger("20"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), this.bigIntegerAdjacencyRealm));

        Symbol<BigInteger> smallerSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);

        assertTrue("smallerSymbolBig should be smaller.", smallerSymbolBig
                .compareTo(this.symbolBig) < 0);

        // Case with greater Symbol using Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(100, 200,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(500, 1000,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(2000, 4000,
                this.integerAdjacencyRealm));
        Symbol<Integer> greaterSymbolInt = new Symbol<Integer>(
                this.intervalsInt);

        assertTrue("greaterSymbolInt should be greater", greaterSymbolInt
                .compareTo(this.symbolInt) > 0);

        // Case with greater symbol using BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("120"),
                new BigInteger("180"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("500"),
                new BigInteger("99999"), this.bigIntegerAdjacencyRealm));

        Symbol<BigInteger> greaterSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);

        assertTrue("greaterSymbolBig should be greater.", greaterSymbolBig
                .compareTo(this.symbolBig) > 0);

        // Case with equals symbol using Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(10, 20,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(50, 100,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(200, 400,
                this.integerAdjacencyRealm));

        Symbol<Integer> sameSymbolInt = new Symbol<Integer>(this.intervalsInt);
        assertTrue("The two symbols should be equals.", sameSymbolInt
                .compareTo(this.symbolInt) == 0);

        // Case with equals symbol using BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(BigInteger.ZERO,
                BigInteger.TEN, this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("50"),
                new BigInteger("100"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("200"),
                new BigInteger("400"), this.bigIntegerAdjacencyRealm));

        Symbol<BigInteger> sameSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);
        assertTrue("The two symbols should be equals.", sameSymbolBig
                .compareTo(this.symbolBig) == 0);
    }

    @SuppressWarnings("unused")
    @Test
    public void testMerge() {

        // Case with null collection.
        Collection<Symbol<Integer>> nullCollection = null;

        try {
            this.symbolInt = Symbol.merge(nullCollection);
            fail("An InternalException should have been thrown");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with empty collection.
        Collection<Symbol<Integer>> emptyCollection = new LinkedList<Symbol<Integer>>();

        try {
            this.symbolInt = Symbol.merge(nullCollection);
            fail("An InternalException should have been thrown");
        }
        catch (InternalException e) {
            // expected
        }

        // Case typical merging using Integer.
        Collection<Symbol<Integer>> symbolCollectionInt = new LinkedList<Symbol<Integer>>();
        symbolCollectionInt.add(this.symbolInt);
        this.intervalsInt.add(new Interval<Integer>(500, 1000,
                this.integerAdjacencyRealm));
        symbolCollectionInt.add(new Symbol<Integer>(this.intervalsInt));

        Symbol<Integer> mergeSymbolInt = Symbol.merge(symbolCollectionInt);
        assertTrue(
                "mergeSymbolInt should contain more elements than symbolInt",
                mergeSymbolInt.compareTo(this.symbolInt) > 0);

        // Case typical merging using BigInteger.
        Collection<Symbol<BigInteger>> symbolCollectionBig = new LinkedList<Symbol<BigInteger>>();
        symbolCollectionBig.add(this.symbolBig);
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("500"),
                new BigInteger("1000"), this.bigIntegerAdjacencyRealm));
        symbolCollectionBig.add(new Symbol<BigInteger>(this.intervalsBig));

        Symbol<BigInteger> mergeSymbolBig = Symbol.merge(symbolCollectionBig);
        assertTrue(
                "mergeSymbolBig should contain more elements than symbolBig",
                mergeSymbolBig.compareTo(this.symbolBig) > 0);
    }

    @Test
    public void testMin() {

        Symbol<Integer> resultSymbolInt;

        Symbol<BigInteger> resultSymbolBig;

        // Case with null symbol.
        try {
            resultSymbolInt = Symbol.min(this.symbolInt, null);
            fail("An InternalException should have been thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Test typical min using Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(0, 10,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(30, 50,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(100, 200,
                this.integerAdjacencyRealm));
        Symbol<Integer> smallerSymbolInt = new Symbol<Integer>(
                this.intervalsInt);

        resultSymbolInt = Symbol.min(this.symbolInt, smallerSymbolInt);
        assertTrue("smallerSymbolInt should be the min.", smallerSymbolInt
                .equals(resultSymbolInt));

        // Test typical min using BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(BigInteger.ZERO,
                BigInteger.ONE, this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("10"),
                new BigInteger("20"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), this.bigIntegerAdjacencyRealm));
        Symbol<BigInteger> smallerSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);

        resultSymbolBig = Symbol.min(this.symbolBig, smallerSymbolBig);
        assertTrue("smallerSymbolBig should be the min.", smallerSymbolBig
                .equals(resultSymbolBig));

    }

    @Test
    public void testMax() {

        Symbol<Integer> resultSymbolInt;

        Symbol<BigInteger> resultSymbolBig;

        // Case with null symbol.
        try {
            resultSymbolInt = Symbol.max(this.symbolInt, null);
            fail("An InternalException should have been thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case typical use of max using Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(100, 200,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(500, 1000,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(2000, 4000,
                this.integerAdjacencyRealm));
        Symbol<Integer> greaterSymbolInt = new Symbol<Integer>(
                this.intervalsInt);

        resultSymbolInt = Symbol.max(this.symbolInt, greaterSymbolInt);
        assertTrue("greaterSymbolInt should be the max.", greaterSymbolInt
                .equals(resultSymbolInt));

        // Case typical use of max using BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("120"),
                new BigInteger("180"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("500"),
                new BigInteger("99999"), this.bigIntegerAdjacencyRealm));
        Symbol<BigInteger> greaterSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);

        resultSymbolBig = Symbol.max(this.symbolBig, greaterSymbolBig);
        assertTrue("greaterSymbolBig should be the max.", greaterSymbolBig
                .equals(resultSymbolBig));
    }

}
