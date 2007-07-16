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
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

public class ElementTest {

    Partition<Integer> partition;

    Dfa<Integer> dfa;

    DfaState<Integer> startState;

    Nfa<Integer> nfa;

    Element<Integer> element;

    @Before
    public void setUp()
            throws Exception {

        this.nfa = new Nfa<Integer>(Realms.getInteger().createInterval(50))
                .zeroOrMore();

        this.dfa = new Dfa<Integer>(this.nfa);

        this.startState = this.dfa.getStartState();

        this.partition = new Partition<Integer>(this.dfa);

        this.element = this.partition.getElement(this.startState);

    }

    @SuppressWarnings("unused")
    @Test
    public void testElement() {

        // Case with null Partition
        Partition<Integer> nullPartition = null;
        try {
            Element element = new Element<Integer>(nullPartition,
                    this.startState);
            fail("partition may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
        // Case with null State
        DfaState<Integer> nullState = null;
        try {
            Element element = new Element<Integer>(this.partition, nullState);
            fail("state may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with incorrect State
        Dfa<Integer> newDfa = new Dfa<Integer>(new Nfa<Integer>(Realms
                .getInteger().createInterval(999)));

        Partition newPartition = new Partition<Integer>(newDfa);

        DfaState<Integer> newState = newDfa.getStartState();

        try {
            Element element = new Element<Integer>(this.partition, newState);
            fail("invalid state");
        }
        catch (InternalException e) {
            // Expected
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetGroup() {

        // Case with null Group
        Group<Integer> nullGroup = null;
        try {
            this.element.setGroup(nullGroup);
            fail("group may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with invalid Group
        Dfa<Integer> newDfa = new Dfa<Integer>(new Nfa<Integer>(Realms
                .getInteger().createInterval(999)));

        Partition newPartition = new Partition<Integer>(newDfa);

        Group<Integer> invalidGroup = new Group<Integer>(newPartition);

        try {
            this.element.setGroup(invalidGroup);
            fail("the group is invalid");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testGetTarget() {

        // Case with null Symbol
        Symbol<Integer> nullSymbol = null;
        try {
            this.element.getTarget(nullSymbol);
            fail("symbol may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with invalid Symbol
        Symbol<Integer> invalidSymbol = new Symbol<Integer>(Realms.getInteger()
                .createInterval(999));
        try {
            this.element.getTarget(invalidSymbol);
            fail("the symbol is invalid");
        }
        catch (InternalException e) {
            // Expected
        }
    }
}
