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

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

public class NfaStateTest {

    Nfa<Integer> stableNfa;

    NfaCombineResult<Integer> result;

    Nfa<Integer> nfa;

    NfaState<Integer> nfaState;

    @Before
    public void setUp()
            throws Exception {

        // In order to get an unstable Nfa
        this.stableNfa = new Nfa<Integer>();
        this.result = this.stableNfa.combineWith(this.stableNfa);

        this.nfa = this.result.getNewNfa();
        this.nfaState = new NfaState<Integer>(this.nfa);
    }

    @SuppressWarnings("unused")
    @Test
    public void testNfaState() {

        // Case with null Nfa.
        try {
            NfaState<Integer> nullNfaState = new NfaState<Integer>(null);
            fail("nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case.

        assertEquals("nfa is not correct.", this.nfa, this.nfaState.getNfa());
    }

    @Test
    public void testGetTransitions() {

        // Case with unstable NfaState.
        try {
            this.nfaState.getTransitions();
            fail("the state is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with one added transition
        NfaState<Integer> newNfaState = new NfaState<Integer>(this.nfa);

        this.nfaState.addTransition(null, newNfaState);
        this.nfaState.stabilize();
        assertEquals("nfaState don't have one new transition", 1, this.nfaState
                .getTransitions().size());
    }

    @Test
    public void testGetTargets() {

        // Case with unstable NfaState.
        try {
            this.nfaState.getTargets(null);
            fail("the state is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case.
        this.nfaState.stabilize();
        final SortedSet<NfaState<Integer>> emptyNfaStateSet = new TreeSet<NfaState<Integer>>();

        assertEquals("the target should be an empty NfaState",
                emptyNfaStateSet, this.nfaState.getTargets(null));

        this.nfaState = new NfaState<Integer>(this.nfa);
        NfaState<Integer> newNfaState = new NfaState<Integer>(this.nfa);
        this.nfaState.addTransition(null, newNfaState);
        this.nfaState.stabilize();

        assertEquals("the target should be newSfaState", newNfaState,
                this.nfaState.getTargets(null).first());
    }

    @Test
    public void testEqualsObject() {

        assertTrue("a state should be equals to itself", this.nfaState
                .equals(this.nfaState));
    }

    @Test
    public void testCompareTo() {

        // Case with wrong Nfa.
        Nfa<Integer> secondStableNfa = new Nfa<Integer>();
        this.result = secondStableNfa.combineWith(secondStableNfa);

        Nfa<Integer> secondNfa = this.result.getNewNfa();
        NfaState<Integer> secondNfaState = new NfaState<Integer>(secondNfa);

        try {
            secondNfaState.compareTo(this.nfaState);
            fail("cannot compare states from distinct NFAs");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with greater state.
        NfaState<Integer> greaterNfaState = new NfaState<Integer>(this.nfa);

        assertTrue("greatetNfaState should be greater than nfaState",
                greaterNfaState.compareTo(this.nfaState) > 0);

        // Case with same state
        assertTrue("the two states are equals", this.nfaState
                .compareTo(this.nfaState) == 0);
    }

    @Test
    public void testAddTransition() {

        // Case with null state.
        try {
            this.nfaState.addTransition(null, null);
            fail("nfaState may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with invalid Symbol
        AdjacencyRealm<Integer> integerRealm = Realms.getInteger();
        Symbol<Integer> symbol = new Symbol<Integer>(integerRealm
                .createInterval(10, 20));

        try {
            this.nfaState.addTransition(symbol, this.nfaState);
            fail("invalid symbol");
        }
        catch (RuntimeException e1) {
            // Expected
        }

        // Case with wrong nfa.
        Nfa<Integer> secondStableNfa = new Nfa<Integer>();
        this.result = secondStableNfa.combineWith(secondStableNfa);

        Nfa<Integer> secondNfa = this.result.getNewNfa();
        NfaState<Integer> secondNfaState = new NfaState<Integer>(secondNfa);

        try {
            this.nfaState.addTransition(null, secondNfaState);
            fail("invalid nfaState");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case.
        this.nfaState.addTransition(null, this.nfaState);

        this.nfaState.stabilize();
        assertTrue("there should be only one transition", this.nfaState
                .getTransitions().size() == 1);

        this.nfaState = new NfaState<Integer>(this.nfa);
        NfaState<Integer> newNfaState = new NfaState<Integer>(this.nfa);
        this.nfaState.addTransition(null, this.nfaState);
        this.nfaState.addTransition(null, newNfaState);

        this.nfaState.stabilize();

        assertTrue("there should be two targets for null transition",
                this.nfaState.getTargets(null).size() == 2);

        // Case with already stable state.
        try {
            this.nfaState.addTransition(null, this.nfaState);
            fail("a stable state may not be modified");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testStabilize() {

        // Case with already stable state.
        this.nfaState.stabilize();
        try {
            this.nfaState.stabilize();
            fail("state is already stabilize");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testGetEpsilonReach() {

        // Case with unstable state.
        try {
            this.nfaState.getEpsilonReach();
            fail("the state is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical Case.
        this.nfaState.stabilize();
        assertTrue("the epsilonReach should be nfaState only", this.nfaState
                .getEpsilonReach().size() == 1);

        // Case with multiple transitions
        this.nfaState = new NfaState<Integer>(this.nfa);
        NfaState<Integer> secondState = new NfaState<Integer>(this.nfa);
        NfaState<Integer> thirdState = new NfaState<Integer>(this.nfa);

        secondState.addTransition(null, thirdState);
        this.nfaState.addTransition(null, secondState);
        this.nfaState.stabilize();
        secondState.stabilize();
        thirdState.stabilize();
        assertTrue(
                "the epsilonReach should be to nfaState, second and third state",
                this.nfaState.getEpsilonReach().size() == 3);
    }

}
