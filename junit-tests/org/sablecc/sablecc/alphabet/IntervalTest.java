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
        try {

            Interval<Integer> nullBoundInterval = new Interval<Integer>(null,
                    null);
            fail("An IllegalArgumentException should be thrown.");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        try {
            Interval<Integer> wrongBoundInterval = new Interval<Integer>(20, 10);
            fail("An IllegalArgumentException should be thrown.");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        try {
            Interval<String> wrongBoundIntervalString = new Interval<String>(
                    "zzz", "abc");
            fail("An IllegalArgumentException should be thrown.");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
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

        Interval<Integer> oneBoundinterval = new Interval<Integer>(10);
        Interval<String> oneBoundIntervalString = new Interval<String>("abc");
        Interval<BigInteger> oneBoundIntervalBig = new Interval<BigInteger>(
                new BigInteger(1000, new Random()));
        assertTrue("The lower and upper bounds aren't and should be the same.",
                oneBoundinterval.getUpperBound() == oneBoundinterval
                        .getLowerBound());
        assertTrue(
                "The lower and upper bounds aren't and should be the same.",
                oneBoundIntervalString.getUpperBound() == oneBoundIntervalString
                        .getLowerBound());
        assertTrue("The lower and upper bounds aren't and should be the same.",
                oneBoundIntervalBig.getUpperBound() == oneBoundIntervalBig
                        .getLowerBound());
    }

    @Test
    public void testEqualsObject() {

        assertFalse("The objects should not be comparable.", interval
                .equals(new String()));
        Interval<Integer> differentInterval = new Interval<Integer>(10, 50);
        Interval<String> differentIntervalString = new Interval<String>(
                "new String", "new String two");
        Interval<BigInteger> differentIntervalBig = new Interval<BigInteger>(
                BigInteger.ONE, new BigInteger(1000, new Random()));
        assertFalse("The two intervals should not be equals.", interval
                .equals(differentInterval));
        assertFalse("The two intervals should not be equals.", intervalString
                .equals(differentIntervalString));
        assertFalse("The two intervals should not be equals.", intervalBig
                .equals(differentIntervalBig));
        Interval<Integer> sameInterval = new Interval<Integer>(10, 20);
        Interval<String> sameIntervalString = new Interval<String>("def", "qrs");
        Interval<BigInteger> sameIntervalBig = new Interval<BigInteger>(
                new BigInteger("100"), new BigInteger("200"));
        assertTrue("The two intervals should be equals.", interval
                .equals(sameInterval));
        assertTrue("The two intervals should be equals.", intervalString
                .equals(sameIntervalString));
        assertTrue("The two intervals should be equals.", intervalBig
                .equals(sameIntervalBig));
    }

    @Test
    public void testHashCode() {

        int lowerBoundHash;
        int upperBoundHash;

        lowerBoundHash = interval.getLowerBound().hashCode();
        upperBoundHash = interval.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, interval.hashCode());
        lowerBoundHash = intervalString.getLowerBound().hashCode();
        upperBoundHash = intervalString.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, intervalString.hashCode());
        lowerBoundHash = intervalBig.getLowerBound().hashCode();
        upperBoundHash = intervalBig.getUpperBound().hashCode();
        assertEquals(
                "The hashCode of an interval should be the sum of the hashCode of its bounds.",
                lowerBoundHash + upperBoundHash, intervalBig.hashCode());

    }

    @Test
    public void testCompareTo() {

        int result;
        int resultString;
        int resultBig;
        Interval<Integer> lowerInterval = new Interval<Integer>(0, 10);
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "abc");
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, new BigInteger("400"));
        Interval<Integer> greaterInterval = new Interval<Integer>(10, 50);
        Interval<String> greaterIntervalString = new Interval<String>("xyz",
                "zzz");
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("999999"));
        result = interval.compareTo(lowerInterval);
        resultString = intervalString.compareTo(lowerIntervalString);
        resultBig = intervalBig.compareTo(lowerIntervalBig);
        assertTrue("interval should be greater than lowerInterval.", result > 0);
        assertTrue("intervalString should be greater than lowerIntervalString.", resultString > 0);
        assertTrue("intervalBig should be greater than lowerIntervalBig.", resultBig > 0);
        result = interval.compareTo(greaterInterval);
        resultString = intervalString.compareTo(greaterIntervalString);
        resultBig = intervalBig.compareTo(greaterIntervalBig);
        assertTrue("interval should be lower than greaterInterval.", result < 0);
        assertTrue("intervalString should be lower than greaterIntervalString.", resultString < 0);
        assertTrue("intervalBig should be lower than greaterIntervalBig.", resultBig < 0);
        result = interval.compareTo(interval);
        resultString = intervalString.compareTo(intervalString);
        resultBig = intervalBig.compareTo(intervalBig);
        assertEquals("The two intervals should be equals.", 0, result);
        assertEquals("The two intervals should be equals.", 0, resultString);
        assertEquals("The two intervals should be equals.", 0, resultBig);
    }

    @Test
    public void testIsAdjacentTo() {

        Adjacency<String> stringAdjacency = new Adjacency<String>() {

            public boolean isSequential() {

                return true;
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
        Interval<String> secondIntervalString = new Interval<String>("abc",
                "ttt");
        try {
            intervalString.isAdjacentTo(secondIntervalString, stringAdjacency);
            fail("A RunTimeException should be thrown.");
        }
        catch (RuntimeException e) {
            // expected
        }
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
        Interval<Integer> adjacentInterval = new Interval<Integer>(21, 40);
        assertTrue("The two intervals should be adjacent.", interval
                .isAdjacentTo(adjacentInterval, integerAdjacency));
        assertFalse("The two intervals should not be adjacent.",
                interval.isAdjacentTo(new Interval<Integer>(200, 400),
                        integerAdjacency));

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

        Interval<Integer> intersectingInterval = new Interval<Integer>(15, 50);
        Interval<String> intersectingIntervalString = new Interval<String>(
                "klm", "zzz");
        Interval<BigInteger> intersectingIntervalBig = new Interval<BigInteger>(
                new BigInteger("150"));
        assertTrue("The two intervals should intersect.", interval
                .intersects(intersectingInterval));
        assertTrue("The two intervals should intersect.", intervalString
                .intersects(intersectingIntervalString));
        assertTrue("The two intervals should intersect.", intervalBig
                .intersects(intersectingIntervalBig));
        assertFalse("They should not intersect.", interval
                .intersects(new Interval<Integer>(200)));
        assertFalse("They should not intersect.", intervalString
                .intersects(new Interval<String>("zzz")));
        assertFalse("They should not intersect.", intervalBig
                .intersects(new Interval<BigInteger>(BigInteger.ZERO,
                        BigInteger.TEN)));
    }

    @Test
    public void testIntersection() {

        Interval<Integer> intersectingInterval = new Interval<Integer>(15, 50);
        Interval<String> intersectingIntervalString = new Interval<String>(
                "klm", "zzz");
        Interval<BigInteger> intersectingIntervalBig = new Interval<BigInteger>(
                new BigInteger("150"), new BigInteger("9999"));
        Interval<Integer> resultInterval;
        Interval<String> resultIntervalString;
        Interval<BigInteger> resultIntervalBig;
        resultInterval = interval.intersection(intersectingInterval);
        resultIntervalString = intervalString
                .intersection(intersectingIntervalString);
        resultIntervalBig = intervalBig.intersection(intersectingIntervalBig);
        assertEquals("The lowerBound of the intersection should be 15.", 15,
                resultInterval.getLowerBound());
        assertEquals("The upperBound of the intersection should be 20.", 20,
                resultInterval.getUpperBound());
        assertEquals("The lowerBound of the intersection should be klm.", "klm",
                resultIntervalString.getLowerBound());
        assertEquals("The upperBound of the intersection should be qrs.", "qrs",
                resultIntervalString.getUpperBound());
        assertEquals("The lowerBound of the intersection should be 150.", "150",
                resultIntervalBig.getLowerBound().toString());
        assertEquals("The upperBound of the intersection should be 200.", "200",
                resultIntervalBig.getUpperBound().toString());
        assertNull("No intersection expected.", interval
                .intersection(new Interval<Integer>(40, 60)));
        assertNull("No intersection expected.", intervalString
                .intersection(new Interval<String>("zzz")));
        assertNull("No intersection expected.", intervalBig
                .intersection(new Interval<BigInteger>(new BigInteger(1000,
                        new Random()))));
    }

    @Test
    public void testMergeWith() {

        Adjacency<String> stringAdjacency = new Adjacency<String>() {

            public boolean isSequential() {

                return true;
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
        try {
            intervalString.mergeWith(new Interval<String>("abc", "ttt"),
                    stringAdjacency);
            fail();
        }
        catch (RuntimeException e) {
            // excepted
        }
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
        Interval<Integer> resultMerge;
        Interval<Integer> adjacentInterval = new Interval<Integer>(21, 40);
        resultMerge = interval.mergeWith(adjacentInterval, integerAdjacency);
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
        Interval<BigInteger> resultMergeBig;
        Interval<BigInteger> adjacentIntervalBig = new Interval<BigInteger>(
                new BigInteger("201"), new BigInteger("999"));
        resultMergeBig = intervalBig.mergeWith(adjacentIntervalBig,
                bigIntegerAdjacency);
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

        Interval<Integer> lowerInterval = new Interval<Integer>(0, 5);
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "ccc");
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, BigInteger.TEN);
        Interval<Integer> greaterInterval = new Interval<Integer>(50, 200);
        Interval<String> greaterIntervalString = new Interval<String>("uvw",
                "zzz");
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("99999"));
        Interval<Integer> resultInterval;
        Interval<String> resultIntervalString;
        Interval<BigInteger> resultIntervalBig;
        resultInterval = Interval.min(interval, lowerInterval);
        resultIntervalString = Interval
                .min(intervalString, lowerIntervalString);
        resultIntervalBig = Interval.min(intervalBig, lowerIntervalBig);
        assertTrue("The minimum should be lowerInterval.", lowerInterval
                .equals(resultInterval));
        assertTrue("The minimum should be lowerIntervalString.",
                lowerIntervalString.equals(resultIntervalString));
        assertTrue("The minimum should be lowerIntervalBig.", lowerIntervalBig
                .equals(resultIntervalBig));
        resultInterval = Interval.min(interval, greaterInterval);
        resultIntervalString = Interval.min(intervalString,
                greaterIntervalString);
        resultIntervalBig = Interval.min(intervalBig, greaterIntervalBig);
        assertTrue("The minimum should be interval.", intervalString
                .equals(resultIntervalString));
        assertTrue("The minimum should be intervalString.", intervalString
                .equals(resultIntervalString));
        assertTrue("The minimum should be intervalBig.", intervalBig
                .equals(resultIntervalBig));
    }

    @Test
    public void testMax() {

        Interval<Integer> lowerInterval = new Interval<Integer>(0, 5);
        Interval<String> lowerIntervalString = new Interval<String>("aaa",
                "ccc");
        Interval<BigInteger> lowerIntervalBig = new Interval<BigInteger>(
                BigInteger.ZERO, BigInteger.TEN);
        Interval<Integer> greaterInterval = new Interval<Integer>(50, 200);
        Interval<String> greaterIntervalString = new Interval<String>("uvw",
                "zzz");
        Interval<BigInteger> greaterIntervalBig = new Interval<BigInteger>(
                new BigInteger("999"), new BigInteger("99999"));
        Interval<Integer> resultInterval;
        Interval<String> resultIntervalString;
        Interval<BigInteger> resultIntervalBig;
        resultInterval = Interval.max(interval, lowerInterval);
        resultIntervalString = Interval
                .max(intervalString, lowerIntervalString);
        resultIntervalBig = Interval.max(intervalBig, lowerIntervalBig);
        assertTrue("The maximum should be interval.", interval
                .equals(resultInterval));
        assertTrue("The maximum should be intervalString.", intervalString
                .equals(resultIntervalString));
        assertTrue("The maximum should be intervalBig.", intervalBig
                .equals(resultIntervalBig));
        resultInterval = Interval.max(interval, greaterInterval);
        resultIntervalString = Interval.max(intervalString,
                greaterIntervalString);
        resultIntervalBig = Interval.max(intervalBig, greaterIntervalBig);
        assertTrue("The maximum should be greaterInterval.", greaterInterval
                .equals(resultInterval));
        assertTrue("The maximum should be greaterIntervalString.",
                greaterIntervalString.equals(resultIntervalString));
        assertTrue("The maximum should be greaterIntervalBig.",
                greaterIntervalBig.equals(resultIntervalBig));
    }

}
