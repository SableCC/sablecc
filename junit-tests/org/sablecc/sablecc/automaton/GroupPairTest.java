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

package org.sablecc.sablecc.automaton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.exception.InternalException;

public class GroupPairTest {

    GroupPair<Integer> groupPair;

    Group<Integer> group1;

    Group<Integer> group2;

    Partition<Integer> partition1;

    Partition<Integer> partition2;

    Dfa<Integer> dfa1;

    Dfa<Integer> dfa2;

    Nfa<Integer> nfa1;

    Nfa<Integer> nfa2;

    @Before
    public void setUp()
            throws Exception {

        // For group 1
        this.nfa1 = new Nfa<Integer>(Realms.getInteger().createInterval(50))
                .zeroOrMore();

        this.dfa1 = new Dfa<Integer>(this.nfa1);

        this.partition1 = new Partition<Integer>(this.dfa1);

        this.group1 = new Group<Integer>(this.partition1);

        // For group 2
        this.nfa2 = new Nfa<Integer>(Realms.getInteger().createInterval(25))
                .zeroOrMore();

        this.dfa2 = new Dfa<Integer>(this.nfa2);

        this.partition2 = new Partition<Integer>(this.dfa2);

        this.group2 = new Group<Integer>(this.partition2);

        this.groupPair = new GroupPair<Integer>(this.group1, this.group2);
    }

    @SuppressWarnings("unused")
    @Test
    public void testGroupPair() {

        // Case with null groups
        Group<Integer> nullGroup = null;

        try {
            GroupPair<Integer> groupPair = new GroupPair<Integer>(nullGroup,
                    this.group2);
            fail("group1 may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        try {
            GroupPair<Integer> groupPair = new GroupPair<Integer>(this.group1,
                    nullGroup);
            fail("group2 may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        assertEquals("group1 has not been entered correctly", this.group1,
                this.groupPair.getGroup1());

        assertEquals("group2 has not been entered correctly", this.group2,
                this.groupPair.getGroup2());
    }

    @Test
    public void testEqualsObject() {

        // Case with null object
        Object nullObject = null;

        assertFalse("groupPair should not be equal to null", this.groupPair
                .equals(nullObject));

        // Case with wrong instance
        assertFalse("groupPair should not be equal to an Integer",
                this.groupPair.equals(10));

        // Typical cases
        assertTrue("a groupPair should be equal to himself", this.groupPair
                .equals(this.groupPair));

        GroupPair<Integer> sameGroupPair = new GroupPair<Integer>(this.group1,
                this.group2);

        assertTrue("groupPair should be equal to this instance", this.groupPair
                .equals(sameGroupPair));
    }

}
