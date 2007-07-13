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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

public class GroupTest {

    private Group<Integer> group;

    private Interval<Integer> interval;

    private Nfa<Integer> nfa;

    private Dfa<Integer> dfa;

    private DfaState<Integer> dfaStartState;

    private Partition<Integer> partition;

    @Before
    public void setUp()
            throws Exception {

        this.interval = Realms.getInteger().createInterval(50);

        this.nfa = new Nfa<Integer>(this.interval).zeroOrMore();

        this.dfa = new Dfa<Integer>(this.nfa);

        this.partition = new Partition<Integer>(this.dfa);

        this.group = new Group<Integer>(this.partition);

        this.dfaStartState = this.dfa.getStartState();
    }

    @Test
    public void testGroup() {

        // Case with null partition
        Partition<Integer> nullPartition = null;
        try {
            this.group = new Group<Integer>(nullPartition);
            fail("partition may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        assertEquals("the group has the wrong partition", this.partition,
                this.group.getPartition());
    }

    @Test
    public void testAddElement() {

        // Case with null Element
        Element<Integer> nullElement = null;
        try {
            this.group.addElement(nullElement);
            fail("element may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testRemoveElement() {

        // Case with null Element
        Element<Integer> nullElement = null;
        try {
            this.group.removeElement(nullElement);
            fail("element may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testAddTransition() {

        // Case with null Symbol
        Symbol<Integer> nullSymbol = null;
        try {
            this.group.addTransition(nullSymbol, this.group);
            fail("symbol may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
        // Case with null Group
        Symbol<Integer> symbol = new Symbol<Integer>(this.interval);
        try {
            this.group.addTransition(nullSymbol, this.group);
            fail("group may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        this.group.addTransition(symbol, this.group);
        assertFalse("The set of transition of the group should not be empty",
                this.group.getTransitions().isEmpty());
    }
}
