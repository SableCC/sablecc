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

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.exception.InternalException;

public class PartitionTest {

    Partition<Integer> partition;

    Dfa<Integer> dfa;

    Nfa<Integer> nfa;

    @Before
    public void setUp()
            throws Exception {

        this.nfa = new Nfa<Integer>(Realms.getInteger().createInterval(50))
                .zeroOrMore();

        this.dfa = new Dfa<Integer>(this.nfa);

        this.partition = new Partition<Integer>(this.dfa);

    }

    @Test
    public void testPartition() {

        // Case with null dfa
        Dfa<Integer> nullDfa = null;
        try {
            this.partition = new Partition<Integer>(nullDfa);
            fail("dfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testAddGroup() {

        // Case with null Group
        Group<Integer> nullGroup = null;
        try {
            this.partition.addGroup(nullGroup);
            fail("group may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with invalid Group
        Partition<Integer> newPartition = new Partition<Integer>(this.dfa);
        Group<Integer> invalidGroup = new Group<Integer>(newPartition);
        try {
            this.partition.addGroup(invalidGroup);
            fail("invalid group");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with a group already added
        Group<Integer> group = new Group<Integer>(this.partition);
        try {
            this.partition.addGroup(group);
            fail("group is already in this partiton");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testGetElement() {

        // Case with null DfaState
        DfaState<Integer> nullState = null;
        try {
            this.partition.getElement(nullState);
            fail("state may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with invalid DfaState
        Dfa<Integer> newDfa = new Dfa<Integer>(this.nfa);
        DfaState<Integer> dfaState = newDfa.getStartState();
        try {
            this.partition.getElement(dfaState);
            fail("invalid state");
        }
        catch (InternalException e) {
            // Expected
        }
        dfaState = this.dfa.getStartState();
        this.partition.getElement(dfaState);
    }

    @Test
    public void testAddElement() {

        Element<Integer> element;
        // Case with null Element
        Element<Integer> nullElement = null;
        try {
            this.partition.addElement(nullElement);
            fail("element may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with invalid Element
        Partition<Integer> newPartition = new Partition<Integer>(this.dfa);
        element = newPartition.getElement(this.dfa.getStartState());
        try {
            this.partition.addElement(element);
            fail("invalid element");
        }
        catch (InternalException e) {
            // Expected
        }
    }

}
