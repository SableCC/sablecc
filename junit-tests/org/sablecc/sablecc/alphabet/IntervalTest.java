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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.exception.InternalException;

public class IntervalTest {

    private Interval<Integer> intervalInt;

    private Interval<String> intervalString;

    private Interval<BigInteger> intervalBig;

    private final AdjacencyRealm<Integer> integerAdjacencyRealm = new AdjacencyRealm<Integer>() {

        @Override
        public boolean canBeAdjacent() {

            return true;
        }

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

    private final AdjacencyRealm<String> stringAdjacencyRealm = new AdjacencyRealm<String>() {

        @Override
        public boolean canBeAdjacent() {

            return false;
        }

        @Override
        public boolean isAdjacent(
                String bound1,
                String bound2) {

            throw new InternalException("instance is not sequential");
        }

        @Override
        public String next(
                String bound) {

            throw new InternalException("instance is not sequential");
        }

        @Override
        public String previous(
                String bound) {

            throw new InternalException("instance is not sequential");
        }
    };

    private final AdjacencyRealm<BigInteger> bigIntegerAdjacencyRealm = new AdjacencyRealm<BigInteger>() {

        @Override
        public boolean canBeAdjacent() {

            return true;
        }

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

        this.intervalInt = this.integerAdjacencyRealm.createInterval(10, 20);
        this.intervalString = this.stringAdjacencyRealm.createInterval("def",
                "qrs");
        this.intervalBig = this.bigIntegerAdjacencyRealm.createInterval(
                new BigInteger("100"), new BigInteger("200"));
    }

    @Test
    @SuppressWarnings("unused")
    public void testIntervalTTAdjacencyRealmOfT() {

        assertEquals(10, this.intervalInt.getLowerBound());
        assertEquals(20, this.intervalInt.getUpperBound());

        // Case with null bounds
        try {

            Interval<Integer> nullBoundInterval = new Interval<Integer>(null,
                    null);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with incorrect bounds with Integer
        try {
            Interval<Integer> wrongBoundInterval = new Interval<Integer>(20,
                    10, this.integerAdjacencyRealm);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with incorrect bounds with String
        try {
            Interval<String> wrongBoundIntervalString = new Interval<String>(
                    "zzz", "abc", this.stringAdjacencyRealm);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with incorrect bounds with BigInteger
        try {
            Interval<BigInteger> wrongBoundIntervalBig = new Interval<BigInteger>(
                    new BigInteger("9999"), BigInteger.ZERO,
                    this.bigIntegerAdjacencyRealm);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }
    }

    @Test
    public void testIntervalTAdjacencyRealmOfT() {

        // With Integer
        Interval<Integer> oneBoundInterval = new Interval<Integer>(10,
                this.integerAdjacencyRealm);
        assertTrue("The lower and upper bounds aren't and should be the same.",
                oneBoundInterval.getUpperBound() == oneBoundInterval
                        .getLowerBound());

        // With String
        Interval<String> oneBoundIntervalString = new Interval<String>("abc",
                this.stringAdjacencyRealm);
        assertTrue(
                "The lower and upper bounds aren't and should be the same.",
                oneBoundIntervalString.getUpperBound() == oneBoundIntervalString
                        .getLowerBound());

        // With BigInteger
        Interval<BigInteger> oneBoundIntervalBig = new Interval<BigInteger>(
                new BigInteger(1000, new Random()),
                this.bigIntegerAdjacencyRealm);
        assertTrue("The lower and upper bounds aren't and should be the same.",
                oneBoundIntervalBig.getUpperBound() == oneBoundIntervalBig
                        .getLowerBound());
    }

    @Test
    public void testEqualsObject() {

        // Case when two interval are not of the same instance
        assertFalse("The objects should not be comparable.", this.intervalInt
                .equals(new String()));

        // With Integer
        Interval<Integer> differentInterval = this.integerAdjacencyRealm
                .createInterval(10, 50);
        Interval<Integer> sameInterval = this.integerAdjacencyRealm
                .createInterval(10, 20);
        assertFalse("The two intervals should not be equals.", this.intervalInt
                .equals(differentInterval));
        assertTrue("The two intervals should be equals.", this.intervalInt
                .equals(sameInterval));

        // With String
        Interval<String> differentIntervalString = this.stringAdjacencyRealm
                .createInterval("new String", "new String two");
        Interval<String> sameIntervalString = this.stringAdjacencyRealm
                .createInterval("def", "qrs");
        assertFalse("The two intervals should not be equals.",
                this.intervalString.equals(differentIntervalString));
        assertTrue("The two intervals should be equals.", this.intervalString
                .equals(sameIntervalString));

        // With BigInteger
        Interval<BigInteger> differentIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(BigInteger.ONE, new BigInteger(1000,
                        new Random()));
        Interval<BigInteger> sameIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(new BigInteger("100"), new BigInteger("200"));
        assertFalse("The two intervals should not be equals.", this.intervalBig
                .equals(differentIntervalBig));
        assertTrue("The two intervals should be equals.", this.intervalBig
                .equals(sameIntervalBig));
    }

    @Test
    public void testHashCode() {

        int lowerBoundHash;
        int upperBoundHash;
        int adjacencyRealmHash;

        // With Integer
        lowerBoundHash = this.intervalInt.getLowerBound().hashCode();
        upperBoundHash = this.intervalInt.getUpperBound().hashCode();
        adjacencyRealmHash = this.integerAdjacencyRealm.hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds and adjacency realm.",
                lowerBoundHash + upperBoundHash + adjacencyRealmHash,
                this.intervalInt.hashCode());

        // With String
        lowerBoundHash = this.intervalString.getLowerBound().hashCode();
        upperBoundHash = this.intervalString.getUpperBound().hashCode();
        adjacencyRealmHash = this.stringAdjacencyRealm.hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds and adjacency realm.",
                lowerBoundHash + upperBoundHash + adjacencyRealmHash,
                this.intervalString.hashCode());

        // With BigInteger
        lowerBoundHash = this.intervalBig.getLowerBound().hashCode();
        upperBoundHash = this.intervalBig.getUpperBound().hashCode();
        adjacencyRealmHash = this.bigIntegerAdjacencyRealm.hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds and adjacency realm.",
                lowerBoundHash + upperBoundHash + adjacencyRealmHash,
                this.intervalBig.hashCode());

    }

    @Test
    public void testCompareTo() {

        // With Integer
        Interval<Integer> lowerInterval = this.integerAdjacencyRealm
                .createInterval(0, 10);
        Interval<Integer> greaterInterval = this.integerAdjacencyRealm
                .createInterval(10, 50);

        assertTrue("intervalInt should be greater than lowerInterval.",
                this.intervalInt.compareTo(lowerInterval) > 0);
        assertTrue("intervalInt should be lower than greaterInterval.",
                this.intervalInt.compareTo(greaterInterval) < 0);
        assertTrue("The two intervals should be equals.", this.intervalInt
                .compareTo(this.intervalInt) == 0);

        // With String
        Interval<String> lowerIntervalString = this.stringAdjacencyRealm
                .createInterval("aaa", "abc");
        Interval<String> greaterIntervalString = this.stringAdjacencyRealm
                .createInterval("xyz", "zzz");

        assertTrue(
                "intervalString should be greater than lowerIntervalString.",
                this.intervalString.compareTo(lowerIntervalString) > 0);
        assertTrue(
                "intervalString should be lower than greaterIntervalString.",
                this.intervalString.compareTo(greaterIntervalString) < 0);
        assertTrue("The two intervals should be equals.", this.intervalString
                .compareTo(this.intervalString) == 0);

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(BigInteger.ZERO, new BigInteger("400"));
        Interval<BigInteger> greaterIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(new BigInteger("999"), new BigInteger("999999"));

        assertTrue("intervalBig should be greater than lowerIntervalBig.",
                this.intervalBig.compareTo(lowerIntervalBig) > 0);
        assertTrue("intervalBig should be lower than greaterIntervalBig.",
                this.intervalBig.compareTo(greaterIntervalBig) < 0);
        assertTrue("The two intervals should be equals.", this.intervalBig
                .compareTo(this.intervalBig) == 0);
    }

    @Test
    public void testIsAdjacentTo() {

        // Test with Integer
        Interval<Integer> adjacentInterval = this.integerAdjacencyRealm
                .createInterval(21, 40);
        assertTrue("The two intervals should be adjacent.", this.intervalInt
                .isAdjacentTo(adjacentInterval));
        assertFalse("The two intervals should not be adjacent.",
                this.intervalInt.isAdjacentTo(this.integerAdjacencyRealm
                        .createInterval(200, 400)));

        // Test with String
        Interval<String> secondIntervalString = this.stringAdjacencyRealm
                .createInterval("abc", "ttt");
        try {
            this.intervalString.isAdjacentTo(secondIntervalString);
            fail("A InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Implementation of AdjacencyRealm<BigInteger>

        // Test with BigInteger
        Interval<BigInteger> adjacentIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(new BigInteger("201"), new BigInteger("999"));
        assertTrue("The two intervals should be adjacent.", this.intervalBig
                .isAdjacentTo(adjacentIntervalBig));
        assertFalse("The two intervals should not be adjacent.",
                this.intervalBig.isAdjacentTo(this.bigIntegerAdjacencyRealm
                        .createInterval(BigInteger.ONE)));

    }

    @Test
    public void testIntersects() {

        // With Integer
        Interval<Integer> intersectingInterval = this.integerAdjacencyRealm
                .createInterval(15, 50);
        assertTrue("The two intervals should intersect.", this.intervalInt
                .intersects(intersectingInterval));
        assertFalse("They should not intersect.", this.intervalInt
                .intersects(this.integerAdjacencyRealm.createInterval(200)));

        // With String
        Interval<String> intersectingIntervalString = this.stringAdjacencyRealm
                .createInterval("klm", "zzz");
        assertTrue("The two intervals should intersect.", this.intervalString
                .intersects(intersectingIntervalString));
        assertFalse("They should not intersect.", this.intervalString
                .intersects(this.stringAdjacencyRealm.createInterval("zzz")));

        // With BigInteger
        Interval<BigInteger> intersectingIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(new BigInteger("150"));
        assertTrue("The two intervals should intersect.", this.intervalBig
                .intersects(intersectingIntervalBig));
        assertFalse("They should not intersect.", this.intervalBig
                .intersects(this.bigIntegerAdjacencyRealm.createInterval(
                        BigInteger.ZERO, BigInteger.TEN)));
    }

    @Test
    public void testIntersection() {

        // With Integer
        Interval<Integer> intersectingInterval = this.integerAdjacencyRealm
                .createInterval(15, 50);
        Interval<Integer> resultInterval = this.intervalInt
                .intersection(intersectingInterval);

        assertEquals("The lowerBound of the intersection should be 15.", 15,
                resultInterval.getLowerBound());
        assertEquals("The upperBound of the intersection should be 20.", 20,
                resultInterval.getUpperBound());

        assertNull("No intersection expected.",
                this.intervalInt.intersection(this.integerAdjacencyRealm
                        .createInterval(40, 60)));

        // With String
        Interval<String> intersectingIntervalString = this.stringAdjacencyRealm
                .createInterval("klm", "zzz");
        Interval<String> resultIntervalString = this.intervalString
                .intersection(intersectingIntervalString);

        assertEquals("The lowerBound of the intersection should be klm.",
                "klm", resultIntervalString.getLowerBound());
        assertEquals("The upperBound of the intersection should be qrs.",
                "qrs", resultIntervalString.getUpperBound());

        assertNull("No intersection expected.", this.intervalString
                .intersection(this.stringAdjacencyRealm.createInterval("zzz")));

        // With BigInteger
        Interval<BigInteger> intersectingIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(new BigInteger("150"), new BigInteger("9999"));
        Interval<BigInteger> resultIntervalBig = this.intervalBig
                .intersection(intersectingIntervalBig);

        assertEquals("The lowerBound of the intersection should be 150.",
                "150", resultIntervalBig.getLowerBound().toString());
        assertEquals("The upperBound of the intersection should be 200.",
                "200", resultIntervalBig.getUpperBound().toString());

        assertNull("No intersection expected.", this.intervalBig
                .intersection(this.bigIntegerAdjacencyRealm
                        .createInterval(new BigInteger(1000, new Random()))));
    }

    @Test
    public void testMergeWith() {

        // Test with Integer
        Interval<Integer> adjacentInterval = this.integerAdjacencyRealm
                .createInterval(21, 40);
        Interval<Integer> resultMerge = this.intervalInt
                .mergeWith(adjacentInterval);

        assertEquals(
                "The lower bound of the merging should be the lower bound of intervalInt.",
                this.intervalInt.getLowerBound(), resultMerge.getLowerBound());
        assertEquals(
                "The upper bound of the merging should be the upper bound of adjacentInterval.",
                adjacentInterval.getUpperBound(), resultMerge.getUpperBound());

        try {
            resultMerge = this.intervalInt.mergeWith(this.integerAdjacencyRealm
                    .createInterval(999, 9999));
            fail("An InternalException should be thrown because the two intervals are not adjacent.");
        }
        catch (InternalException e) {
            // expected
        }

        // Test with String
        try {
            this.intervalString.mergeWith(this.stringAdjacencyRealm
                    .createInterval("abc", "ttt"));
            fail();
        }
        catch (InternalException e) {
            // excepted
        }

        // With BigInteger
        Interval<BigInteger> adjacentIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(new BigInteger("201"), new BigInteger("999"));
        Interval<BigInteger> resultMergeBig = this.intervalBig
                .mergeWith(adjacentIntervalBig);

        assertEquals(
                "The lower bound of the merging should be the lower bound of intervalBig.",
                this.intervalBig.getLowerBound(), resultMergeBig
                        .getLowerBound());
        assertEquals(
                "The upper bound of the merging should be the upper bound of adjacentIntervalBig.",
                adjacentIntervalBig.getUpperBound(), resultMergeBig
                        .getUpperBound());

        try {
            resultMergeBig = this.intervalBig
                    .mergeWith(this.bigIntegerAdjacencyRealm.createInterval(
                            BigInteger.ONE, BigInteger.TEN));
            fail("An InternalException should be thrown because the two intervals are not adjacent.");
        }
        catch (InternalException e) {
            // expected
        }

    }

    @Test
    public void testMin() {

        // With Integer
        Interval<Integer> lowerInterval = this.integerAdjacencyRealm
                .createInterval(0, 5);
        Interval<Integer> greaterInterval = this.integerAdjacencyRealm
                .createInterval(50, 200);
        Interval<Integer> resultInterval = Interval.min(this.intervalInt,
                lowerInterval);

        assertTrue("The minimum should be lowerInterval.", lowerInterval
                .equals(resultInterval));

        resultInterval = Interval.min(this.intervalInt, greaterInterval);
        assertTrue("The minimum should be intervalInt.", resultInterval
                .equals(this.intervalInt));

        // With String
        Interval<String> lowerIntervalString = this.stringAdjacencyRealm
                .createInterval("aaa", "ccc");
        Interval<String> greaterIntervalString = this.stringAdjacencyRealm
                .createInterval("uvw", "zzz");
        Interval<String> resultIntervalString = Interval.min(
                this.intervalString, lowerIntervalString);

        assertTrue("The minimum should be lowerIntervalString.",
                lowerIntervalString.equals(resultIntervalString));

        resultIntervalString = Interval.min(this.intervalString,
                greaterIntervalString);
        assertTrue("The minimum should be intervalString.", this.intervalString
                .equals(resultIntervalString));

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(BigInteger.ZERO, BigInteger.TEN);
        Interval<BigInteger> greaterIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(new BigInteger("999"), new BigInteger("99999"));
        Interval<BigInteger> resultIntervalBig = Interval.min(this.intervalBig,
                lowerIntervalBig);

        assertTrue("The minimum should be lowerIntervalBig.", lowerIntervalBig
                .equals(resultIntervalBig));

        resultIntervalBig = Interval.min(this.intervalBig, greaterIntervalBig);
        assertTrue("The minimum should be intervalBig.", this.intervalBig
                .equals(resultIntervalBig));
    }

    @Test
    public void testMax() {

        // With Integer
        Interval<Integer> lowerInterval = this.integerAdjacencyRealm
                .createInterval(0, 5);
        Interval<Integer> greaterInterval = this.integerAdjacencyRealm
                .createInterval(50, 200);
        Interval<Integer> resultInterval = Interval.max(this.intervalInt,
                lowerInterval);

        assertTrue("The maximum should be intervalInt.", this.intervalInt
                .equals(resultInterval));

        resultInterval = Interval.max(this.intervalInt, greaterInterval);
        assertTrue("The maximum should be greaterInterval.", greaterInterval
                .equals(resultInterval));

        // With String
        Interval<String> lowerIntervalString = this.stringAdjacencyRealm
                .createInterval("aaa", "ccc");
        Interval<String> greaterIntervalString = this.stringAdjacencyRealm
                .createInterval("uvw", "zzz");
        Interval<String> resultIntervalString = Interval.max(
                this.intervalString, lowerIntervalString);

        assertTrue("The maximum should be intervalString.", this.intervalString
                .equals(resultIntervalString));

        resultIntervalString = Interval.max(this.intervalString,
                greaterIntervalString);
        assertTrue("The maximum should be greaterIntervalString.",
                greaterIntervalString.equals(resultIntervalString));

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(BigInteger.ZERO, BigInteger.TEN);
        Interval<BigInteger> greaterIntervalBig = this.bigIntegerAdjacencyRealm
                .createInterval(new BigInteger("999"), new BigInteger("99999"));
        Interval<BigInteger> resultIntervalBig = Interval.max(this.intervalBig,
                lowerIntervalBig);

        assertTrue("The maximum should be intervalBig.", this.intervalBig
                .equals(resultIntervalBig));

        resultIntervalBig = Interval.max(this.intervalBig, greaterIntervalBig);
        assertTrue("The maximum should be greaterIntervalBig.",
                greaterIntervalBig.equals(resultIntervalBig));
    }

}
