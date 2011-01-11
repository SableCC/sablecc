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

package org.sablecc.sablecc.util;

import static org.junit.Assert.*;

import org.junit.*;
import org.sablecc.exception.*;
import org.sablecc.util.*;

public class WorkSetTest {

    private WorkSet<Integer> workSet;

    @Before
    public void setUp()
            throws Exception {

        this.workSet = new WorkSet<Integer>();
    }

    @Test
    public void testHasNext() {

        // Case with empty workSet.
        assertFalse("the WorkSet is empty.", this.workSet.hasNext());

        // Case with at least one element.
        this.workSet.add(5);
        this.workSet.add(10);
        assertTrue("the WorkSet contains one element.", this.workSet.hasNext());
    }

    @Test
    public void testNext() {

        // Adding elements
        this.workSet.add(100);
        this.workSet.add(25);
        this.workSet.add(50);

        assertEquals("the first element should be 100", (Integer) 100,
                this.workSet.next());
        assertEquals("the first element should be 25", (Integer) 25,
                this.workSet.next());
        assertEquals("the first element should be 50", (Integer) 50,
                this.workSet.next());

        assertFalse("the WorkSet is empty.", this.workSet.hasNext());

        // Case adding the same elements
        this.workSet.add(100);
        this.workSet.add(25);
        this.workSet.add(50);

        assertFalse("the WorkSet should still be empty.",
                this.workSet.hasNext());
    }

    @Test
    public void testAdd() {

        // Case with null element.
        try {
            this.workSet.add(null);
            fail("element may not be null");
        }
        catch (InternalException e) {
            // Excepted
        }

    }
}
