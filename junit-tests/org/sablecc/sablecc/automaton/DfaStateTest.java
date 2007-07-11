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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

public class DfaStateTest {

    Dfa<Integer> dfa;

    DfaState<Integer> dfaStartState;

    Symbol<Integer> symbol;

    Interval<Integer> interval;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
            throws Exception {

        this.interval = Realms.getInteger().createInterval(50);

        this.symbol = new Symbol(this.interval);

        this.dfa = new Dfa<Integer>(new Nfa<Integer>(this.interval).oneOrMore());

        this.dfaStartState = this.dfa.getStartState();
    }

    @SuppressWarnings("unused")
    @Test
    public void testDfaState() {

        // Case with null Dfa
        Dfa<Integer> nullDfa = null;
        try {
            DfaState<Integer> dfaState = new DfaState<Integer>(nullDfa);
            fail("dfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical Case
        assertEquals("the state dont have the correct dfa", this.dfa,
                this.dfaStartState.getDfa());
        assertTrue(
                "from the startState, the correct symbol should lead to an acceptionState",
                this.dfaStartState.getTarget(this.symbol) == this.dfa
                        .getAcceptStates().first());
    }

    @Test
    public void testGetTarget() {

        // Case with null symbol
        Symbol<Integer> nullSymbol = null;
        try {
            this.dfaStartState.getTarget(nullSymbol);
            fail("symbol may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with incorrect symbol
        Symbol<Integer> newSymbol = new Symbol<Integer>(Realms.getInteger()
                .createInterval(999));
        try {
            this.dfaStartState.getTarget(newSymbol);
            fail("invalid symbol");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testEqualsObject() {

        assertTrue("a state should be equals to himself", this.dfaStartState
                .equals(this.dfaStartState));

        assertTrue("the two states should be equals", this.dfaStartState
                .equals(this.dfa.getStartState()));
    }

    @Test
    public void testCompareTo() {

        // Case with lower state
        DfaState<Integer> lowerState = this.dfa.getStates().first();
        assertTrue("dfaStartState should be greater than lowerState",
                this.dfaStartState.compareTo(lowerState) > 0);

        // Case with equal state
        assertTrue("the two states should be equals", this.dfaStartState
                .compareTo(this.dfa.getStartState()) == 0);

        // Case with greater state
        DfaState<Integer> greaterState = this.dfa.getStates().last();
        assertTrue("dfaStartState should be lower than greaterState",
                this.dfaStartState.compareTo(greaterState) < 0);
    }

    @Test
    public void testAddTransition() {

        // Case with an already stable State
        try {
            this.dfaStartState.addTransition(this.symbol, this.dfaStartState);
            fail("a stable state may not be modified");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testRemoveTransitions() {

        // Case with an already stable State
        try {
            this.dfaStartState.addTransition(this.symbol, this.dfaStartState);
            fail("a stable state may not be modified");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testStabilize() {

        // Case with an already stable State
        try {
            this.dfaStartState.addTransition(this.symbol, this.dfaStartState);
            fail("this state is already stable");
        }
        catch (InternalException e) {
            // Expected
        }
    }

}
