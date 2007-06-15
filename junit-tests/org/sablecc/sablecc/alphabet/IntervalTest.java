/* This file is part of SableCC (http://sablecc.org/).
 * 
 * See the NOTICE file distributed with this work for copyright information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sablecc.sablecc.alphabet;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.math.*;
import java.util.Random;

public class IntervalTest {

    Interval<Integer> interval;

    Interval<String> intervalString;

    Interval<BigInteger> intervalBig;

    Adjacency<Integer> integerAdjacency = new Adjacency<Integer>() {

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

    Adjacency<String> stringAdjacency = new Adjacency<String>() {

        public boolean isSequential() {

            return false;
        }

        public boolean isAdjacent(
                String bound1,
                String bound2) {

            throw new RuntimeException();
        }

        public String next(
                String bound) {

            throw new RuntimeException();
        }

        public String previous(
                String bound) {

            throw new RuntimeException();
        }
    };

    Adjacency<BigInteger> bigIntegerAdjacency = new Adjacency<BigInteger>() {

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

        interval = new Interval<Integer>(10, 20);
        intervalString = new Interval<String>("def", "qrs");
        intervalBig = new Interval<BigInteger>(new BigInteger("100"),
                new BigInteger("200"));
    }

    @Test
    @SuppressWarnings("unused")
    public void testIntervalTT() {

        assertEquals(10, interval.getLowerBound());
        assertEquals(20, interval.getUpperBound());

        // Case with null bounds
        try {

            Interval<Integer> nullBoundInterval = new Interval<Integer>(null,
                    null);
            fail("An IllegalArgumentException should be thrown.");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        // Case with incorrect bounds with Integer
        try {
            Interval<Integer> wrongBoundInterval = new Interval<Integer>(20, 10);
            fail("An IllegalArgumentException should be thrown.");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        // Case with incorrect bounds with String
        try {
            Interval<String> wrongBoundIntervalString = new Interval<String>(
                    "zzz", "abc");
            fail("An IllegalArgumentException should be thrown.");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        // Case with incorrect bounds with BigInteger
        try {
            Interval<BigInteger> wrongBoundIntervalBig = new Interval<BigInteger>(
                    new BigInteger("9999"), BigInteger.ZERO);
            fail("An IllegalArgumentException should be thrown.");
        }
        catch (IllegalArgumentException e) {
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
        assertFalse("The objects should not be comparable.", interval
                .equals(new String()));

        // With Integer
        Interval<Integer> differentInterval = new Interval<Integer>(10, 50);
        Interval<Integer> sameInterval = new Interval<Integer>(10, 20);
        assertFalse("The two intervals should not be equals.", interval
                .equals(differentInterval));
        assertTrue("The two intervals should be equals.", interval
                .equals(sameInterval));

        // With String
        Interval<String> differentIntervalString = new Interval<String>(
                "new String", "new String two");
        Interval<String> sameIntervalString = new Interval<String>("def", "qrs");
        assertFalse("The two intervals should not be equals.", intervalString
                .equals(differentIntervalString));
        assertTrue("The two intervals should be equals.", intervalString
                .equals(sameIntervalString));

        // With BigInteger
        Interval<BigInteger> differentIntervalBig = new Interval<BigInteger>(
                BigInteger.ONE, new BigInteger(1000, new Random()));
        Interval<BigInteger> sameIntervalBig = new Interval<BigInteger>(
                new BigInteger("100"), new BigInteger("200"));
        assertFalse("The two intervals should not be equals.", intervalBig
                .equals(differentIntervalBig));
        assertTrue("The two intervals should be equals.", intervalBig
                .equals(sameIntervalBig));
    }

    @Test
    public void testHashCode() {

        int lowerBoundHash;
        int upperBoundHash;

        // With Integer
        lowerBoundHash = interval.getLowerBound().hashCode();
        upperBoundHash = interval.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, interval.hashCode());

        // With String
        lowerBoundHash = intervalString.getLowerBound().hashCode();
        upperBoundHash = intervalString.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, intervalString.hashCode());

        // With BigInteger
        lowerBoundHash = intervalBig.getLowerBound().hashCode();
        upperBoundHash = intervalBig.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, intervalBig.hashCode());

    }

    @Test
    public void testCompareTo() {

        // With Integer
        Interval<Integer> lowerInterval = new Interval<Integer>(0, 10);
        Interval<Integer> greaterInterval = new Interval<Integer>(10, 50);

        assertTrue("interval should be greater than lowerInterval.", interval
                .compareTo(lowerInterval) > 0);
        assertTrue("interval should be lower than greaterInterval.", interval
                .compareTo(greaterInterval) < 0);
        assertTrue("The two intervals should be equals.", interval
                .compareTo(interval) == 0);

        // With String
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "abc");
        Interval<String> greaterIntervalString = new Interval<String>("xyz",
                "zzz");

        assertTrue(
                "intervalString should be greater than lowerIntervalString.",
                intervalString.compareTo(lowerIntervalString) > 0);
        assertTrue(
                "intervalString should be lower than greaterIntervalString.",
                intervalString.compareTo(greaterIntervalString) < 0);
        assertTrue("The two intervals should be equals.", intervalString
                .compareTo(intervalString) == 0);

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, new BigInteger("400"));
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("999999"));

        assertTrue("intervalBig should be greater than lowerIntervalBig.",
                intervalBig.compareTo(lowerIntervalBig) > 0);
        assertTrue("intervalBig should be lower than greaterIntervalBig.",
                intervalBig.compareTo(greaterIntervalBig) < 0);
        assertTrue("The two intervals should be equals.", intervalBig
                .compareTo(intervalBig) == 0);
    }

    @Test
    public void testIsAdjacentTo() {

        // Test with Integer
        Interval<Integer> adjacentInterval = new Interval<Integer>(21, 40);
        assertTrue("The two intervals should be adjacent.", interval
                .isAdjacentTo(adjacentInterval, integerAdjacency));
        assertFalse("The two intervals should not be adjacent.",
                interval.isAdjacentTo(new Interval<Integer>(200, 400),
                        integerAdjacency));

        // Test with String
        Interval<String> secondIntervalString = new Interval<String>("abc",
                "ttt");
        try {
            intervalString.isAdjacentTo(secondIntervalString, stringAdjacency);
            fail("A RunTimeException should be thrown.");
        }
        catch (RuntimeException e) {
            // expected
        }

        // Implementation of Adjacency<BigInteger>

        // Test with BigInteger
        Interval<BigInteger> adjacentIntervalBig = new Interval<BigInteger>(
                new BigInteger("201"), new BigInteger("999"));
        assertTrue("The two intervals should be adjacent.", intervalBig
                .isAdjacentTo(adjacentIntervalBig, bigIntegerAdjacency));
        assertFalse("The two intervals should not be adjacent.", intervalBig
                .isAdjacentTo(new Interval<BigInteger>(BigInteger.ONE),
                        bigIntegerAdjacency));

    }

    @Test
    public void testIntersects() {

        // With Integer
        Interval<Integer> intersectingInterval = new Interval<Integer>(15, 50);
        assertTrue("The two intervals should intersect.", interval
                .intersects(intersectingInterval));
        assertFalse("They should not intersect.", interval
                .intersects(new Interval<Integer>(200)));

        // With String
        Interval<String> intersectingIntervalString = new Interval<String>(
                "klm", "zzz");
        assertTrue("The two intervals should intersect.", intervalString
                .intersects(intersectingIntervalString));
        assertFalse("They should not intersect.", intervalString
                .intersects(new Interval<String>("zzz")));

        // With BigInteger
        Interval<BigInteger> intersectingIntervalBig = new Interval<BigInteger>(
                new BigInteger("150"));
        assertTrue("The two intervals should intersect.", intervalBig
                .intersects(intersectingIntervalBig));
        assertFalse("They should not intersect.", intervalBig
                .intersects(new Interval<BigInteger>(BigInteger.ZERO,
                        BigInteger.TEN)));
    }

    @Test
    public void testIntersection() {

        // With Integer
        Interval<Integer> intersectingInterval = new Interval<Integer>(15, 50);
        Interval<Integer> resultInterval = interval
                .intersection(intersectingInterval);

        assertEquals("The lowerBound of the intersection should be 15.", 15,
                resultInterval.getLowerBound());
        assertEquals("The upperBound of the intersection should be 20.", 20,
                resultInterval.getUpperBound());

        assertNull("No intersection expected.", interval
                .intersection(new Interval<Integer>(40, 60)));

        // With String
        Interval<String> intersectingIntervalString = new Interval<String>(
                "klm", "zzz");
        Interval<String> resultIntervalString = intervalString
                .intersection(intersectingIntervalString);

        assertEquals("The lowerBound of the intersection should be klm.",
                "klm", resultIntervalString.getLowerBound());
        assertEquals("The upperBound of the intersection should be qrs.",
                "qrs", resultIntervalString.getUpperBound());

        assertNull("No intersection expected.", intervalString
                .intersection(new Interval<String>("zzz")));

        // With BigInteger
        Interval<BigInteger> intersectingIntervalBig = new Interval<BigInteger>(
                new BigInteger("150"), new BigInteger("9999"));
        Interval<BigInteger> resultIntervalBig = intervalBig
                .intersection(intersectingIntervalBig);

        assertEquals("The lowerBound of the intersection should be 150.",
                "150", resultIntervalBig.getLowerBound().toString());
        assertEquals("The upperBound of the intersection should be 200.",
                "200", resultIntervalBig.getUpperBound().toString());

        assertNull("No intersection expected.", intervalBig
                .intersection(new Interval<BigInteger>(new BigInteger(1000,
                        new Random()))));
    }

    @Test
    public void testMergeWith() {

        // Test with Integer
        Interval<Integer> adjacentInterval = new Interval<Integer>(21, 40);
        Interval<Integer> resultMerge = interval.mergeWith(adjacentInterval,
                integerAdjacency);

        assertEquals(
                "The lower bound of the merging should be the lower bound of interval.",
                interval.getLowerBound(), resultMerge.getLowerBound());
        assertEquals(
                "The upper bound of the merging should be the upper bound of adjacentInterval.",
                adjacentInterval.getUpperBound(), resultMerge.getUpperBound());

        try {
            resultMerge = interval.mergeWith(new Interval<Integer>(999, 9999),
                    integerAdjacency);
            fail("An IllegalArgumentException should be thrown because the two intervals are not adjacent.");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        // Test with String
        try {
            intervalString.mergeWith(new Interval<String>("abc", "ttt"),
                    stringAdjacency);
            fail();
        }
        catch (RuntimeException e) {
            // excepted
        }

        // With BigInteger
        Interval<BigInteger> adjacentIntervalBig = new Interval<BigInteger>(
                new BigInteger("201"), new BigInteger("999"));
        Interval<BigInteger> resultMergeBig = intervalBig.mergeWith(
                adjacentIntervalBig, bigIntegerAdjacency);

        assertEquals(
                "The lower bound of the merging should be the lower bound of intervalBig.",
                intervalBig.getLowerBound(), resultMergeBig.getLowerBound());
        assertEquals(
                "The upper bound of the merging should be the upper bound of adjacentIntervalBig.",
                adjacentIntervalBig.getUpperBound(), resultMergeBig
                        .getUpperBound());

        try {
            resultMergeBig = intervalBig.mergeWith(new Interval<BigInteger>(
                    BigInteger.ONE, BigInteger.TEN), bigIntegerAdjacency);
            fail("An IllegalArgumentException should be thrown because the two intervals are not adjacent.");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

    }

    @Test
    public void testMin() {

        // With Integer
        Interval<Integer> lowerInterval = new Interval<Integer>(0, 5);
        Interval<Integer> greaterInterval = new Interval<Integer>(50, 200);
        Interval<Integer> resultInterval = Interval
                .min(interval, lowerInterval);

        assertTrue("The minimum should be lowerInterval.", lowerInterval
                .equals(resultInterval));

        resultInterval = Interval.min(interval, greaterInterval);
        assertTrue("The minimum should be interval.", resultInterval
                .equals(interval));

        // With String
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "ccc");
        Interval<String> greaterIntervalString = new Interval<String>("uvw",
                "zzz");
        Interval<String> resultIntervalString = Interval.min(intervalString,
                lowerIntervalString);

        assertTrue("The minimum should be lowerIntervalString.",
                lowerIntervalString.equals(resultIntervalString));

        resultIntervalString = Interval.min(intervalString,
                greaterIntervalString);
        assertTrue("The minimum should be intervalString.", intervalString
                .equals(resultIntervalString));

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, BigInteger.TEN);
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("99999"));
        Interval<BigInteger> resultIntervalBig = Interval.min(intervalBig,
                lowerIntervalBig);

        assertTrue("The minimum should be lowerIntervalBig.", lowerIntervalBig
                .equals(resultIntervalBig));

        resultIntervalBig = Interval.min(intervalBig, greaterIntervalBig);
        assertTrue("The minimum should be intervalBig.", intervalBig
                .equals(resultIntervalBig));
    }

    @Test
    public void testMax() {

        // With Integer
        Interval<Integer> lowerInterval = new Interval<Integer>(0, 5);
        Interval<Integer> greaterInterval = new Interval<Integer>(50, 200);
        Interval<Integer> resultInterval = Interval
                .max(interval, lowerInterval);

        assertTrue("The maximum should be interval.", interval
                .equals(resultInterval));

        resultInterval = Interval.max(interval, greaterInterval);
        assertTrue("The maximum should be greaterInterval.", greaterInterval
                .equals(resultInterval));

        // With String
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "ccc");
        Interval<String> greaterIntervalString = new Interval<String>("uvw",
                "zzz");
        Interval<String> resultIntervalString = Interval.max(intervalString,
                lowerIntervalString);

        assertTrue("The maximum should be intervalString.", intervalString
                .equals(resultIntervalString));

        resultIntervalString = Interval.max(intervalString,
                greaterIntervalString);
        assertTrue("The maximum should be greaterIntervalString.",
                greaterIntervalString.equals(resultIntervalString));

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, BigInteger.TEN);
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("99999"));
        Interval<BigInteger> resultIntervalBig = Interval.max(intervalBig,
                lowerIntervalBig);

        assertTrue("The maximum should be intervalBig.", intervalBig
                .equals(resultIntervalBig));

        resultIntervalBig = Interval.max(intervalBig, greaterIntervalBig);
        assertTrue("The maximum should be greaterIntervalBig.",
                greaterIntervalBig.equals(resultIntervalBig));
    }

}
