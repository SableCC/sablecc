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
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Test;
import org.sablecc.sablecc.exception.InternalException;

public class AdjacencyRealmTest {

    // Initiating global interval variables for the 3 types
    private Interval<Integer> intervalInteger;

    private Interval<BigInteger> intervalBigInt;

    @Test
    public void testMin() {

        // =========================================
        // Testing with Integer
        // =========================================

        Integer intBound1;
        Integer intBound2;

        // Test when bound1 is null
        intBound1 = null;
        intBound2 = 0;
        try {
            AdjacencyRealm.min(intBound1, intBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound2 is null
        intBound1 = 0;
        intBound2 = null;
        try {
            AdjacencyRealm.min(intBound1, intBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound1 and bound2 are null
        intBound1 = null;
        intBound2 = null;
        try {
            AdjacencyRealm.min(intBound1, intBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound1 > bound2
        intBound1 = 10;
        intBound2 = 1;
        try {
            assertEquals("bound2 not returned when bound1 > bound2", intBound2,
                    AdjacencyRealm.min(intBound1, intBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // Test when bound1 < bound2
        intBound1 = 1;
        intBound2 = 10;
        try {
            assertEquals("bound1 not returned when bound1 < bound2", intBound1,
                    AdjacencyRealm.min(intBound1, intBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // Test when bound1 = bound2
        intBound1 = 10;
        intBound2 = 10;
        try {
            assertEquals("bound2 not returned when bound1 = bound2", intBound2,
                    AdjacencyRealm.min(intBound1, intBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // =========================================
        // Testing with String
        // =========================================

        String stringBound1;
        String stringBound2;

        // Test when bound1 is null
        stringBound1 = null;
        stringBound2 = "aaa";
        try {
            AdjacencyRealm.min(stringBound1, stringBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound2 is null
        stringBound1 = "aaa";
        stringBound2 = null;
        try {
            AdjacencyRealm.min(stringBound1, stringBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound1 and bound2 are null
        stringBound1 = null;
        stringBound2 = null;
        try {
            AdjacencyRealm.min(stringBound1, stringBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound1 > bound2
        stringBound1 = "ccc";
        stringBound2 = "aaa";
        try {
            assertEquals("bound2 not returned when bound1 > bound2",
                    stringBound2, AdjacencyRealm
                            .min(stringBound1, stringBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // Test when bound1 < bound2
        stringBound1 = "aaa";
        stringBound2 = "ccc";
        try {
            assertEquals("bound1 not returned when bound1 < bound2",
                    stringBound1, AdjacencyRealm
                            .min(stringBound1, stringBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // Test when bound1 = bound2
        stringBound1 = "ccc";
        stringBound2 = "ccc";
        try {
            assertEquals("bound2 not returned when bound1 = bound2",
                    stringBound2, AdjacencyRealm
                            .min(stringBound1, stringBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

    }

    @Test
    public void testMax() {

        // =========================================
        // Testing with Integer
        // =========================================

        Integer intBound1;
        Integer intBound2;

        // Test when bound1 is null
        intBound1 = null;
        intBound2 = 0;
        try {
            AdjacencyRealm.max(intBound1, intBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound2 is null
        intBound1 = 0;
        intBound2 = null;
        try {
            AdjacencyRealm.max(intBound1, intBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound1 and bound2 are null
        intBound1 = null;
        intBound2 = null;
        try {
            AdjacencyRealm.max(intBound1, intBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound1 > bound2
        intBound1 = 10;
        intBound2 = 1;
        try {
            assertEquals("bound1 not returned when bound1 > bound2", intBound1,
                    AdjacencyRealm.max(intBound1, intBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // Test when bound1 < bound2
        intBound1 = 1;
        intBound2 = 10;
        try {
            assertEquals("bound2 not returned when bound1 < bound2", intBound2,
                    AdjacencyRealm.max(intBound1, intBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // Test when bound1 = bound2
        intBound1 = 10;
        intBound2 = 10;
        try {
            assertEquals("bound2 not returned when bound1 = bound2", intBound2,
                    AdjacencyRealm.max(intBound1, intBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // =========================================
        // Testing with String
        // =========================================

        String stringBound1;
        String stringBound2;

        // Test when bound1 is null
        stringBound1 = null;
        stringBound2 = "aaa";
        try {
            AdjacencyRealm.max(stringBound1, stringBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound2 is null
        stringBound1 = "aaa";
        stringBound2 = null;
        try {
            AdjacencyRealm.max(stringBound1, stringBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound1 and bound2 are null
        stringBound1 = null;
        stringBound2 = null;
        try {
            AdjacencyRealm.max(stringBound1, stringBound2);
            fail("An InternalException should be thrown for a null bound.");

        }
        catch (InternalException e) {
            // Expected
        }

        // Test when bound1 > bound2
        stringBound1 = "ccc";
        stringBound2 = "aaa";
        try {
            assertEquals("bound1 not returned when bound1 > bound2",
                    stringBound1, AdjacencyRealm
                            .max(stringBound1, stringBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // Test when bound1 < bound2
        stringBound1 = "aaa";
        stringBound2 = "ccc";
        try {
            assertEquals("bound2 not returned when bound1 < bound2",
                    stringBound2, AdjacencyRealm
                            .max(stringBound1, stringBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

        // Test when bound1 = bound2
        stringBound1 = "ccc";
        stringBound2 = "ccc";
        try {
            assertEquals("bound2 not returned when bound1 = bound2",
                    stringBound2, AdjacencyRealm
                            .max(stringBound1, stringBound2));
        }
        catch (InternalException e) {
            fail("An InternalException should not be thrown for non-null bounds.");
        }

    }

    @Test
    public void testCreateIntervalTT() {

        // Testing createInterval(T, T) with type Integer
        this.intervalInteger = new Interval<Integer>(0, 10, Realms.getInteger());
        Interval<Integer> createdIntervalInt = Realms.getInteger()
                .createInterval(0, 10);
        assertEquals(
                "Created interval with integer (2 bounds) not the same as expected",
                this.intervalInteger, createdIntervalInt);

        // Testing createInterval(T, T) with type BigInteger
        this.intervalBigInt = new Interval<BigInteger>(new BigInteger("1000"),
                new BigInteger("100000"), Realms.getBigInteger());
        Interval<BigInteger> createdIntervalBigInt = Realms.getBigInteger()
                .createInterval(new BigInteger("1000"),
                        new BigInteger("100000"));
        assertEquals(
                "Created interval with big integer (2 bounds) not the same as expected",
                this.intervalBigInt, createdIntervalBigInt);
    }

    @Test
    public void testCreateIntervalT() {

        // Testing createInterval(T) with type Integer
        this.intervalInteger = new Interval<Integer>(10, Realms.getInteger());
        Interval<Integer> createdIntervalInt = Realms.getInteger()
                .createInterval(10);
        assertEquals(
                "Created interval with integer (1 bound) not the same as expected",
                this.intervalInteger, createdIntervalInt);

        // Testing createInterval(T) with type BigInteger
        this.intervalBigInt = new Interval<BigInteger>(
                new BigInteger("100000"), Realms.getBigInteger());
        Interval<BigInteger> createdIntervalBigInt = Realms.getBigInteger()
                .createInterval(new BigInteger("100000"));
        assertEquals(
                "Created interval with big integer (1 bound) not the same as expected",
                this.intervalBigInt, createdIntervalBigInt);

    }

}
