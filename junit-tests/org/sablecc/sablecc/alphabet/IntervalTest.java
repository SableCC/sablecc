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

    private Interval<BigInteger> intervalBig;

    @Before
    public void setUp()
            throws Exception {

        this.intervalInt = Realms.getInteger().createInterval(10, 20);
        this.intervalBig = Realms.getBigInteger().createInterval(
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
                    10, Realms.getInteger());
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with incorrect bounds with BigInteger
        try {
            Interval<BigInteger> wrongBoundIntervalBig = new Interval<BigInteger>(
                    new BigInteger("9999"), BigInteger.ZERO, Realms
                            .getBigInteger());
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // expected
        }
    }

    @Test
    public void testIntervalTAdjacencyRealmOfT() {

        // With Integer
        Interval<Integer> oneBoundInterval = new Interval<Integer>(10, Realms
                .getInteger());
        assertTrue("The lower and upper bounds aren't and should be the same.",
                oneBoundInterval.getUpperBound() == oneBoundInterval
                        .getLowerBound());

        // With BigInteger
        Interval<BigInteger> oneBoundIntervalBig = new Interval<BigInteger>(
                new BigInteger(1000, new Random()), Realms.getBigInteger());
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
        Interval<Integer> differentInterval = Realms.getInteger()
                .createInterval(10, 50);
        Interval<Integer> sameInterval = Realms.getInteger().createInterval(10,
                20);
        assertFalse("The two intervals should not be equals.", this.intervalInt
                .equals(differentInterval));
        assertTrue("The two intervals should be equals.", this.intervalInt
                .equals(sameInterval));

        // With BigInteger
        Interval<BigInteger> differentIntervalBig = Realms.getBigInteger()
                .createInterval(BigInteger.ONE,
                        new BigInteger(1000, new Random()));
        Interval<BigInteger> sameIntervalBig = Realms.getBigInteger()
                .createInterval(new BigInteger("100"), new BigInteger("200"));
        assertFalse("The two intervals should not be equals.", this.intervalBig
                .equals(differentIntervalBig));
        assertTrue("The two intervals should be equals.", this.intervalBig
                .equals(sameIntervalBig));
    }

    @Test
    public void testHashCode() {

        // With Integer
        Interval<Integer> sameIntervalInt = Realms.getInteger().createInterval(
                10, 20);
        assertEquals(
                "Two instance of the same symbol should have the same hashcode",
                this.intervalInt.hashCode(), sameIntervalInt.hashCode());

        // With BigInteger
        Interval<BigInteger> sameIntervalBig = Realms.getBigInteger()
                .createInterval(new BigInteger("100"), new BigInteger("200"));
        assertEquals(
                "Two instance of the same symbol should have the same hashcode",
                this.intervalBig.hashCode(), sameIntervalBig.hashCode());

    }

    @Test
    public void testCompareTo() {

        // With Integer
        Interval<Integer> lowerInterval = Realms.getInteger().createInterval(0,
                10);
        Interval<Integer> greaterInterval = Realms.getInteger().createInterval(
                10, 50);

        assertTrue("intervalInt should be greater than lowerInterval.",
                this.intervalInt.compareTo(lowerInterval) > 0);
        assertTrue("intervalInt should be lower than greaterInterval.",
                this.intervalInt.compareTo(greaterInterval) < 0);
        assertTrue("The two intervals should be equals.", this.intervalInt
                .compareTo(this.intervalInt) == 0);

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = Realms.getBigInteger()
                .createInterval(BigInteger.ZERO, new BigInteger("400"));
        Interval<BigInteger> greaterIntervalBig = Realms
                .getBigInteger()
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
        Interval<Integer> adjacentInterval = Realms.getInteger()
                .createInterval(21, 40);
        assertTrue("The two intervals should be adjacent.", this.intervalInt
                .isAdjacentTo(adjacentInterval));
        assertFalse("The two intervals should not be adjacent.",
                this.intervalInt.isAdjacentTo(Realms.getInteger()
                        .createInterval(200, 400)));

        // Implementation of AdjacencyRealm<BigInteger>

        // Test with BigInteger
        Interval<BigInteger> adjacentIntervalBig = Realms.getBigInteger()
                .createInterval(new BigInteger("201"), new BigInteger("999"));
        assertTrue("The two intervals should be adjacent.", this.intervalBig
                .isAdjacentTo(adjacentIntervalBig));
        assertFalse("The two intervals should not be adjacent.",
                this.intervalBig.isAdjacentTo(Realms.getBigInteger()
                        .createInterval(BigInteger.ONE)));

    }

    @Test
    public void testIntersects() {

        // With Integer
        Interval<Integer> intersectingInterval = Realms.getInteger()
                .createInterval(15, 50);
        assertTrue("The two intervals should intersect.", this.intervalInt
                .intersects(intersectingInterval));
        assertFalse("They should not intersect.", this.intervalInt
                .intersects(Realms.getInteger().createInterval(200)));

        // With BigInteger
        Interval<BigInteger> intersectingIntervalBig = Realms.getBigInteger()
                .createInterval(new BigInteger("150"));
        assertTrue("The two intervals should intersect.", this.intervalBig
                .intersects(intersectingIntervalBig));
        assertFalse("They should not intersect.", this.intervalBig
                .intersects(Realms.getBigInteger().createInterval(
                        BigInteger.ZERO, BigInteger.TEN)));
    }

    @Test
    public void testIntersection() {

        // With Integer
        Interval<Integer> intersectingInterval = Realms.getInteger()
                .createInterval(15, 50);
        Interval<Integer> resultInterval = this.intervalInt
                .intersectWith(intersectingInterval);

        assertEquals("The lowerBound of the intersection should be 15.", 15,
                resultInterval.getLowerBound());
        assertEquals("The upperBound of the intersection should be 20.", 20,
                resultInterval.getUpperBound());

        assertNull("No intersection expected.", this.intervalInt
                .intersectWith(Realms.getInteger().createInterval(40, 60)));

        // With BigInteger
        Interval<BigInteger> intersectingIntervalBig = Realms.getBigInteger()
                .createInterval(new BigInteger("150"), new BigInteger("9999"));
        Interval<BigInteger> resultIntervalBig = this.intervalBig
                .intersectWith(intersectingIntervalBig);

        assertEquals("The lowerBound of the intersection should be 150.",
                "150", resultIntervalBig.getLowerBound().toString());
        assertEquals("The upperBound of the intersection should be 200.",
                "200", resultIntervalBig.getUpperBound().toString());

        assertNull("No intersection expected.", this.intervalBig
                .intersectWith(Realms.getBigInteger().createInterval(
                        new BigInteger(1000, new Random()))));
    }

    @Test
    public void testMergeWith() {

        // Test with Integer
        Interval<Integer> adjacentInterval = Realms.getInteger()
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
            resultMerge = this.intervalInt.mergeWith(Realms.getInteger()
                    .createInterval(999, 9999));
            fail("An InternalException should be thrown because the two intervals are not adjacent.");
        }
        catch (InternalException e) {
            // expected
        }

        // With BigInteger
        Interval<BigInteger> adjacentIntervalBig = Realms.getBigInteger()
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
            resultMergeBig = this.intervalBig.mergeWith(Realms.getBigInteger()
                    .createInterval(BigInteger.ONE, BigInteger.TEN));
            fail("An InternalException should be thrown because the two intervals are not adjacent.");
        }
        catch (InternalException e) {
            // expected
        }

    }

    @Test
    public void testMin() {

        // With Integer
        Interval<Integer> lowerInterval = Realms.getInteger().createInterval(0,
                5);
        Interval<Integer> greaterInterval = Realms.getInteger().createInterval(
                50, 200);
        Interval<Integer> resultInterval = Interval.min(this.intervalInt,
                lowerInterval);

        assertTrue("The minimum should be lowerInterval.", lowerInterval
                .equals(resultInterval));

        resultInterval = Interval.min(this.intervalInt, greaterInterval);
        assertTrue("The minimum should be intervalInt.", resultInterval
                .equals(this.intervalInt));

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = Realms.getBigInteger()
                .createInterval(BigInteger.ZERO, BigInteger.TEN);
        Interval<BigInteger> greaterIntervalBig = Realms.getBigInteger()
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
        Interval<Integer> lowerInterval = Realms.getInteger().createInterval(0,
                5);
        Interval<Integer> greaterInterval = Realms.getInteger().createInterval(
                50, 200);
        Interval<Integer> resultInterval = Interval.max(this.intervalInt,
                lowerInterval);

        assertTrue("The maximum should be intervalInt.", this.intervalInt
                .equals(resultInterval));

        resultInterval = Interval.max(this.intervalInt, greaterInterval);
        assertTrue("The maximum should be greaterInterval.", greaterInterval
                .equals(resultInterval));

        // With BigInteger
        Interval<BigInteger> lowerIntervalBig = Realms.getBigInteger()
                .createInterval(BigInteger.ZERO, BigInteger.TEN);
        Interval<BigInteger> greaterIntervalBig = Realms.getBigInteger()
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
