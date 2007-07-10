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

public class MinimalDfaStateTest {

    MinimalDfaState<Integer> minimalDfaStartState;

    MinimalDfa<Integer> minimalDfa;

    Dfa<Integer> dfa;

    Symbol<Integer> symbol;

    Interval<Integer> interval;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
            throws Exception {

        this.interval = Realms.getInteger().createInterval(50);

        this.symbol = new Symbol(this.interval);

        this.dfa = new Dfa<Integer>(new Nfa<Integer>(this.interval).oneOrMore());

        this.minimalDfa = new MinimalDfa<Integer>(this.dfa);

        this.minimalDfaStartState = this.minimalDfa.getStartState();
    }

    @SuppressWarnings("unused")
    @Test
    public void testMinimalDfaState() {

        // Case with null minimalDfa
        MinimalDfa<Integer> nullMinimalDfa = null;
        try {
            MinimalDfaState<Integer> minimalDfaState = new MinimalDfaState<Integer>(
                    nullMinimalDfa);
            fail("minimalDfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical Case
        assertEquals("the state dont have the correct minimalDfa",
                this.minimalDfa, this.minimalDfaStartState.getMinimalDfa());
        assertTrue(
                "from the startState, the correct symbol should lead to an acceptionState",
                this.minimalDfaStartState.getTarget(this.symbol) == this.minimalDfa
                        .getAcceptStates().first());
    }

    @Test
    public void testGetTarget() {

        // Case with null symbol
        Symbol<Integer> nullSymbol = null;
        try {
            this.minimalDfaStartState.getTarget(nullSymbol);
            fail("symbol may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with incorrect symbol
        Symbol<Integer> newSymbol = new Symbol<Integer>(Realms.getInteger()
                .createInterval(999));
        try {
            this.minimalDfaStartState.getTarget(newSymbol);
            fail("invalid symbol");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testEqualsObject() {

        assertTrue("a minimalState should be equals to himself",
                this.minimalDfaStartState.equals(this.minimalDfaStartState));

        assertTrue("the two minimalState should be equals",
                this.minimalDfaStartState.equals(this.minimalDfa
                        .getStartState()));
    }

    @Test
    public void testCompareTo() {

        // Case with lower minimalState
        MinimalDfaState<Integer> lowerMinimalState = this.minimalDfa
                .getStates().first();
        assertTrue(
                "minimalDfaStartState should be greater than lowerMinimalState",
                this.minimalDfaStartState.compareTo(lowerMinimalState) > 0);

        // Case with equal state
        assertTrue("the two minimalState should be equals",
                this.minimalDfaStartState.compareTo(this.minimalDfa
                        .getStartState()) == 0);

        // Case with greater state
        MinimalDfaState<Integer> greaterMinimalState = this.minimalDfa
                .getStates().last();
        assertTrue(
                "minimalDfaStartState should be lower than greaterMinimalState",
                this.minimalDfaStartState.compareTo(greaterMinimalState) < 0);
    }

    @Test
    public void testAddTransition() {

        // Case with an already stable minimalState
        try {
            this.minimalDfaStartState.addTransition(this.symbol,
                    this.minimalDfaStartState);
            fail("a stable state may not be modified");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testStabilize() {

        // Case with an already stable minimalState
        try {
            this.minimalDfaStartState.stabilize();
            fail("the minimalState is already stable");
        }
        catch (InternalException e) {
            // Expected
        }
    }

}
