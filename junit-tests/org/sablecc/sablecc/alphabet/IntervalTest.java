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

    private Interval<Integer> interval;

    private Interval<String> intervalString;

    private Interval<BigInteger> intervalBig;

    private final Adjacency<Integer> integerAdjacency = new Adjacency<Integer>() {

        public boolean isAdjacent(
                Integer bound1,
                Integer bound2) {

            return bound1 == bound2 + 1 || bound1 == bound2 - 1;
        }

        public boolean isSequential() {

            return true;
        }

        public Integer next(
                Integer bound) {

            return bound++;
        }

        public Integer previous(
                Integer bound) {

            return bound--;
        }

    };

    private final Adjacency<String> stringAdjacency = new Adjacency<String>() {

        public boolean isSequential() {

            return false;
        }

        public boolean isAdjacent(
                String bound1,
                String bound2) {

            throw new InternalException("instance is not sequential");
        }

        public String next(
                String bound) {

            throw new InternalException("instance is not sequential");
        }

        public String previous(
                String bound) {

            throw new InternalException("instance is not sequential");
        }
    };

    private final Adjacency<BigInteger> bigIntegerAdjacency = new Adjacency<BigInteger>() {

        public boolean isAdjacent(
                BigInteger bound1,
                BigInteger bound2) {

            return bound1.equals(next(bound2))
                    || bound1.equals(previous(bound2));
        }

        public boolean isSequential() {

            return true;
        }

        public BigInteger next(
                BigInteger bound) {

            return bound.add(BigInteger.ONE);
        }

        public BigInteger previous(
                BigInteger bound) {

            return bound.subtract(BigInteger.ONE);
        }

    };

    @Before
    public void setUp()
            throws Exception {

        this.interval = new Interval<Integer>(10, 20);
        this.intervalString = new Interval<String>("def", "qrs");
        this.intervalBig = new Interval<BigInteger>(new BigInteger("100"),
                new BigInteger("200"));
    }

    @Test
    @SuppressWarnings("unused")
    public void testIntervalTT() {

        assertEquals(10, this.interval.getLowerBound());
        assertEquals(20, this.interval.getUpperBound());

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
            Interval<Integer> wrongBoundInterval = new Interval<Integer>(20, 10);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with incorrect bounds with String
        try {
            Interval<String> wrongBoundIntervalString = new Interval<String>(
                    "zzz", "abc");
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with incorrect bounds with BigInteger
        try {
            Interval<BigInteger> wrongBoundIntervalBig = new Interval<BigInteger>(
                    new BigInteger("9999"), BigInteger.ZERO);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }
    }

    @Test
    public void testIntervalT() {

        // With Integer
        Interval<Integer> oneBoundInterval = new Interval<Integer>(10);
        assertTrue("The lower and upper bounds aren't and should be the same.",
                oneBoundInterval.getUpperBound() == oneBoundInterval
                        .getLowerBound());

        // With String
        Interval<String> oneBoundIntervalString = new Interval<String>("abc");
        assertTrue(
                "The lower and upper bounds aren't and should be the same.",
                oneBoundIntervalString.getUpperBound() == oneBoundIntervalString
                        .getLowerBound());

        // With BigInteger
        Interval<BigInteger> oneBoundIntervalBig = new Interval<BigInteger>(
                new BigInteger(1000, new Random()));
        assertTrue("The lower and upper bounds aren't and should be the same.",
                oneBoundIntervalBig.getUpperBound() == oneBoundIntervalBig
                        .getLowerBound());
    }

    @Test
    public void testEqualsObject() {

        // Case when two interval are not of the same instance
        assertFalse("The objects should not be comparable.", this.interval
                .equals(new String()));

        // With Integer
        Interval<Integer> differentInterval = new Interval<Integer>(10, 50);
        Interval<Integer> sameInterval = new Interval<Integer>(10, 20);
        assertFalse("The two intervals should not be equals.", this.interval
                .equals(differentInterval));
        assertTrue("The two intervals should be equals.", this.interval
                .equals(sameInterval));

        // With String
        Interval<String> differentIntervalString = new Interval<String>(
                "new String", "new String two");
        Interval<String> sameIntervalString = new Interval<String>("def", "qrs");
        assertFalse("The two intervals should not be equals.",
                this.intervalString.equals(differentIntervalString));
        assertTrue("The two intervals should be equals.", this.intervalString
                .equals(sameIntervalString));

        // With BigInteger
        Interval<BigInteger> differentIntervalBig = new Interval<BigInteger>(
                BigInteger.ONE, new BigInteger(1000, new Random()));
        Interval<BigInteger> sameIntervalBig = new Interval<BigInteger>(
                new BigInteger("100"), new BigInteger("200"));
        assertFalse("The two intervals should not be equals.", this.intervalBig
                .equals(differentIntervalBig));
        assertTrue("The two intervals should be equals.", this.intervalBig
                .equals(sameIntervalBig));
    }

    @Test
    public void testHashCode() {

        int lowerBoundHash;
        int upperBoundHash;

        // With Integer
        lowerBoundHash = this.interval.getLowerBound().hashCode();
        upperBoundHash = this.interval.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, this.interval.hashCode());

        // With String
        lowerBoundHash = this.intervalString.getLowerBound().hashCode();
        upperBoundHash = this.intervalString.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, this.intervalString.hashCode());

        // With BigInteger
        lowerBoundHash = this.intervalBig.getLowerBound().hashCode();
        upperBoundHash = this.intervalBig.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, this.intervalBig.hashCode());

    }

    @Test
    public void testCompareTo() {

        // With Integer
        Interval<Integer> lowerInterval = new Interval<Integer>(0, 10);
        Interval<Integer> greaterInterval = new Interval<Integer>(10, 50);

        assertTrue("interval should be greater than lowerInterval.",
                this.interval.compareTo(lowerInterval) > 0);
        assertTrue("interval should be lower than greaterInterval.",
                this.interval.compareTo(greaterInterval) < 0);
        assertTrue("The two intervals should be equals.", this.interval
                .compareTo(this.interval) == 0);

        // With String
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "abc");
        Interval<String> greaterIntervalString = new Interval<String>("xyz",
                "zzz");

        assertTrue(
                "intervalString should be greater than lowerIntervalString.",
                this.intervalString.compareTo(lowerIntervalString) > 0);
        assertTrue(
                "intervalString should be lower than greaterIntervalString.",
                this.intervalString.compareTo(greaterIntervalString) < 0);
        assertTrue("The two intervals should be equals.", this.intervalString
                .compareTo(this.intervalString) == 0);

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, new BigInteger("400"));
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("999999"));

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
        Interval<Integer> adjacentInterval = new Interval<Integer>(21, 40);
        assertTrue("The two intervals should be adjacent.", this.interval
                .isAdjacentTo(adjacentInterval, this.integerAdjacency));
        assertFalse("The two intervals should not be adjacent.", this.interval
                .isAdjacentTo(new Interval<Integer>(200, 400),
                        this.integerAdjacency));

        // Test with String
        Interval<String> secondIntervalString = new Interval<String>("abc",
                "ttt");
        try {
            this.intervalString.isAdjacentTo(secondIntervalString,
                    this.stringAdjacency);
            fail("A InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Implementation of Adjacency<BigInteger>

        // Test with BigInteger
        Interval<BigInteger> adjacentIntervalBig = new Interval<BigInteger>(
                new BigInteger("201"), new BigInteger("999"));
        assertTrue("The two intervals should be adjacent.", this.intervalBig
                .isAdjacentTo(adjacentIntervalBig, this.bigIntegerAdjacency));
        assertFalse("The two intervals should not be adjacent.",
                this.intervalBig.isAdjacentTo(new Interval<BigInteger>(
                        BigInteger.ONE), this.bigIntegerAdjacency));

    }

    @Test
    public void testIntersects() {

        // With Integer
        Interval<Integer> intersectingInterval = new Interval<Integer>(15, 50);
        assertTrue("The two intervals should intersect.", this.interval
                .intersects(intersectingInterval));
        assertFalse("They should not intersect.", this.interval
                .intersects(new Interval<Integer>(200)));

        // With String
        Interval<String> intersectingIntervalString = new Interval<String>(
                "klm", "zzz");
        assertTrue("The two intervals should intersect.", this.intervalString
                .intersects(intersectingIntervalString));
        assertFalse("They should not intersect.", this.intervalString
                .intersects(new Interval<String>("zzz")));

        // With BigInteger
        Interval<BigInteger> intersectingIntervalBig = new Interval<BigInteger>(
                new BigInteger("150"));
        assertTrue("The two intervals should intersect.", this.intervalBig
                .intersects(intersectingIntervalBig));
        assertFalse("They should not intersect.", this.intervalBig
                .intersects(new Interval<BigInteger>(BigInteger.ZERO,
                        BigInteger.TEN)));
    }

    @Test
    public void testIntersection() {

        // With Integer
        Interval<Integer> intersectingInterval = new Interval<Integer>(15, 50);
        Interval<Integer> resultInterval = this.interval
                .intersection(intersectingInterval);

        assertEquals("The lowerBound of the intersection should be 15.", 15,
                resultInterval.getLowerBound());
        assertEquals("The upperBound of the intersection should be 20.", 20,
                resultInterval.getUpperBound());

        assertNull("No intersection expected.", this.interval
                .intersection(new Interval<Integer>(40, 60)));

        // With String
        Interval<String> intersectingIntervalString = new Interval<String>(
                "klm", "zzz");
        Interval<String> resultIntervalString = this.intervalString
                .intersection(intersectingIntervalString);

        assertEquals("The lowerBound of the intersection should be klm.",
                "klm", resultIntervalString.getLowerBound());
        assertEquals("The upperBound of the intersection should be qrs.",
                "qrs", resultIntervalString.getUpperBound());

        assertNull("No intersection expected.", this.intervalString
                .intersection(new Interval<String>("zzz")));

        // With BigInteger
        Interval<BigInteger> intersectingIntervalBig = new Interval<BigInteger>(
                new BigInteger("150"), new BigInteger("9999"));
        Interval<BigInteger> resultIntervalBig = this.intervalBig
                .intersection(intersectingIntervalBig);

        assertEquals("The lowerBound of the intersection should be 150.",
                "150", resultIntervalBig.getLowerBound().toString());
        assertEquals("The upperBound of the intersection should be 200.",
                "200", resultIntervalBig.getUpperBound().toString());

        assertNull("No intersection expected.", this.intervalBig
                .intersection(new Interval<BigInteger>(new BigInteger(1000,
                        new Random()))));
    }

    @Test
    public void testMergeWith() {

        // Test with Integer
        Interval<Integer> adjacentInterval = new Interval<Integer>(21, 40);
        Interval<Integer> resultMerge = this.interval.mergeWith(
                adjacentInterval, this.integerAdjacency);

        assertEquals(
                "The lower bound of the merging should be the lower bound of interval.",
                this.interval.getLowerBound(), resultMerge.getLowerBound());
        assertEquals(
                "The upper bound of the merging should be the upper bound of adjacentInterval.",
                adjacentInterval.getUpperBound(), resultMerge.getUpperBound());

        try {
            resultMerge = this.interval.mergeWith(new Interval<Integer>(999,
                    9999), this.integerAdjacency);
            fail("An InternalException should be thrown because the two intervals are not adjacent.");
        }
        catch (InternalException e) {
            // expected
        }

        // Test with String
        try {
            this.intervalString.mergeWith(new Interval<String>("abc", "ttt"),
                    this.stringAdjacency);
            fail();
        }
        catch (InternalException e) {
            // excepted
        }

        // With BigInteger
        Interval<BigInteger> adjacentIntervalBig = new Interval<BigInteger>(
                new BigInteger("201"), new BigInteger("999"));
        Interval<BigInteger> resultMergeBig = this.intervalBig.mergeWith(
                adjacentIntervalBig, this.bigIntegerAdjacency);

        assertEquals(
                "The lower bound of the merging should be the lower bound of intervalBig.",
                this.intervalBig.getLowerBound(), resultMergeBig
                        .getLowerBound());
        assertEquals(
                "The upper bound of the merging should be the upper bound of adjacentIntervalBig.",
                adjacentIntervalBig.getUpperBound(), resultMergeBig
                        .getUpperBound());

        try {
            resultMergeBig = this.intervalBig.mergeWith(
                    new Interval<BigInteger>(BigInteger.ONE, BigInteger.TEN),
                    this.bigIntegerAdjacency);
            fail("An InternalException should be thrown because the two intervals are not adjacent.");
        }
        catch (InternalException e) {
            // expected
        }

    }

    @Test
    public void testMin() {

        // With Integer
        Interval<Integer> lowerInterval = new Interval<Integer>(0, 5);
        Interval<Integer> greaterInterval = new Interval<Integer>(50, 200);
        Interval<Integer> resultInterval = Interval.min(this.interval,
                lowerInterval);

        assertTrue("The minimum should be lowerInterval.", lowerInterval
                .equals(resultInterval));

        resultInterval = Interval.min(this.interval, greaterInterval);
        assertTrue("The minimum should be interval.", resultInterval
                .equals(this.interval));

        // With String
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "ccc");
        Interval<String> greaterIntervalString = new Interval<String>("uvw",
                "zzz");
        Interval<String> resultIntervalString = Interval.min(
                this.intervalString, lowerIntervalString);

        assertTrue("The minimum should be lowerIntervalString.",
                lowerIntervalString.equals(resultIntervalString));

        resultIntervalString = Interval.min(this.intervalString,
                greaterIntervalString);
        assertTrue("The minimum should be intervalString.", this.intervalString
                .equals(resultIntervalString));

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, BigInteger.TEN);
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("99999"));
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
        Interval<Integer> lowerInterval = new Interval<Integer>(0, 5);
        Interval<Integer> greaterInterval = new Interval<Integer>(50, 200);
        Interval<Integer> resultInterval = Interval.max(this.interval,
                lowerInterval);

        assertTrue("The maximum should be interval.", this.interval
                .equals(resultInterval));

        resultInterval = Interval.max(this.interval, greaterInterval);
        assertTrue("The maximum should be greaterInterval.", greaterInterval
                .equals(resultInterval));

        // With String
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "ccc");
        Interval<String> greaterIntervalString = new Interval<String>("uvw",
                "zzz");
        Interval<String> resultIntervalString = Interval.max(
                this.intervalString, lowerIntervalString);

        assertTrue("The maximum should be intervalString.", this.intervalString
                .equals(resultIntervalString));

        resultIntervalString = Interval.max(this.intervalString,
                greaterIntervalString);
        assertTrue("The maximum should be greaterIntervalString.",
                greaterIntervalString.equals(resultIntervalString));

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, BigInteger.TEN);
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("99999"));
        Interval<BigInteger> resultIntervalBig = Interval.max(this.intervalBig,
                lowerIntervalBig);

        assertTrue("The maximum should be intervalBig.", this.intervalBig
                .equals(resultIntervalBig));

        resultIntervalBig = Interval.max(this.intervalBig, greaterIntervalBig);
        assertTrue("The maximum should be greaterIntervalBig.",
                greaterIntervalBig.equals(resultIntervalBig));
    }

}
