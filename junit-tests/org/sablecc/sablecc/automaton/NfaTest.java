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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

public class NfaTest {

    Nfa<Integer> nfa;

    NfaCombineResult<Integer> result;

    Nfa<Integer> unstableNfa;

    Interval<Integer> interval;

    Symbol<Integer> symbol;

    @Before
    public void setUp()
            throws Exception {

        this.nfa = new Nfa<Integer>();

        // In order to get an unstable Nfa
        this.result = this.nfa.combineWith(this.nfa);
        this.unstableNfa = this.result.getNewNfa();

        this.interval = Realms.getInteger().createInterval(5);
        this.symbol = new Symbol<Integer>(this.interval);
        this.nfa = new Nfa<Integer>(this.interval);

    }

    @Test
    public void testNfa() {

        assertTrue("the first state should be startState", this.nfa.getStates()
                .first() == this.nfa.getStartState());
        assertTrue("the last state should be an acceptation state", this.nfa
                .getStates().last() == this.nfa.getAcceptState());
        assertTrue("there should only be two states", this.nfa.getStates()
                .size() == 2);

    }

    @Test
    public void testNfaIntervalOfT() {

        // Case with null interval
        Interval<Integer> nullInterval = null;
        try {
            this.nfa = new Nfa<Integer>(nullInterval);
            fail("inteval may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case

        NfaState<Integer> expectedTarget = this.nfa.getStartState().getTargets(
                this.symbol).first();
        assertTrue(
                "the target of the startState with the right symbol should be the acceptionState.",
                this.nfa.getAcceptState() == expectedTarget);

    }

    @Test
    public void testGetStates() {

        // Case with unstable Nfa
        try {
            this.unstableNfa.getStates();
            fail("the Nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testGetStartState() {

        // Case with unstable Nfa
        try {
            this.unstableNfa.getStartState();
            fail("the Nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

    }

    @Test
    public void testGetAcceptState() {

        // Case with unstable Nfa
        try {
            this.unstableNfa.getStartState();
            fail("the Nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testStabilize() {

        // Case with already stable Nfa
        try {
            this.nfa.stabilize();
            fail("this Nfa is already stable");
        }
        catch (InternalException e) {
            // Expected
        }

    }

    @SuppressWarnings("unused")
    @Test
    public void testUnionWith() {

        // Case with null Nfa
        Nfa<Integer> nullNfa = null;
        try {
            this.nfa.unionWith(nullNfa);
            fail("nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with unstable Nfa
        try {
            this.nfa.unionWith(this.unstableNfa);
            fail("nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        try {
            this.unstableNfa.unionWith(this.nfa);
            fail("this Nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case
        Interval<Integer> secondInterval = Realms.getInteger().createInterval(
                10);
        Nfa<Integer> secondNfa = new Nfa<Integer>(secondInterval);
        Nfa<Integer> unionNfa = this.nfa.unionWith(secondNfa);
        MinimalDfa<Integer> minimalUnion = new MinimalDfa<Integer>(
                new Dfa<Integer>(unionNfa));

        Collection<Interval<Integer>> intervals = new LinkedList<Interval<Integer>>();
        intervals.add(this.interval);
        intervals.add(secondInterval);

        Symbol<Integer> symbolUnion = new Symbol<Integer>(intervals);

        assertTrue(
                "from the startState, the unionSymbol should lead to the acceptionState",
                minimalUnion.getStartState().getTarget(symbolUnion) == minimalUnion
                        .getAcceptStates().first());
    }

    @Test
    public void testConcatenateWith() {

        // Case with null Nfa
        Nfa<Integer> nullNfa = null;
        try {
            this.nfa.concatenateWith(nullNfa);
            fail("nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with unstable Nfa
        try {
            this.nfa.concatenateWith(this.unstableNfa);
            fail("nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        try {
            this.unstableNfa.concatenateWith(this.nfa);
            fail("this Nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case
        Interval<Integer> secondInterval = Realms.getInteger().createInterval(
                10);
        Symbol<Integer> secondSymbol = new Symbol<Integer>(secondInterval);

        Nfa<Integer> secondNfa = new Nfa<Integer>(secondInterval);
        Nfa<Integer> concatenateNfa = this.nfa.concatenateWith(secondNfa);

        MinimalDfa<Integer> minimalConcatenate = new MinimalDfa<Integer>(
                new Dfa<Integer>(concatenateNfa));

        assertTrue(
                "from the startState, the two symbols should lead to the acceptionState",
                minimalConcatenate.getStartState().getTarget(this.symbol)
                        .getTarget(secondSymbol) == minimalConcatenate
                        .getAcceptStates().first());
    }

    @Test
    public void testZeroOrMore() {

        // Case with unstable Nfa
        try {
            this.unstableNfa.zeroOrMore();
            fail("this Nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case
        this.nfa = this.nfa.zeroOrMore();
        MinimalDfa<Integer> minimalDfa = new MinimalDfa<Integer>(
                new Dfa<Integer>(this.nfa));

        // test with no transition
        assertTrue(
                "from the startState, no transition should lead to an AcceptionState",
                minimalDfa.getStartState() == minimalDfa.getAcceptStates()
                        .first());

        // test with only one transition
        assertTrue(
                "from the startState, one transition of the same symbol should lead to the acceptionState",
                minimalDfa.getStartState().getTarget(this.symbol) == minimalDfa
                        .getAcceptStates().first());

        // test with more than one transition
        assertTrue(
                "from the startState, one or more transition of the same symbol should lead to the acceptionState",
                minimalDfa.getStartState().getTarget(this.symbol).getTarget(
                        this.symbol) == minimalDfa.getAcceptStates().first());
    }

    @Test
    public void testZeroOrOne() {

        // Case with unstable Nfa
        try {
            this.unstableNfa.zeroOrOne();
            fail("this Nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        this.nfa = this.nfa.zeroOrOne();
        MinimalDfa<Integer> minimalDfa = new MinimalDfa<Integer>(
                new Dfa<Integer>(this.nfa));

        // test with no transition
        assertTrue(
                "from the startState, no transition should lead to an AcceptionState",
                minimalDfa.getStartState() == minimalDfa.getAcceptStates()
                        .first());

        // test with only one transition
        assertTrue(
                "from the startState, one transition of the same symbol should lead to the acceptionState",
                minimalDfa.getStartState().getTarget(this.symbol) == minimalDfa
                        .getAcceptStates().last());

        // test with more than one transition
        assertFalse(
                "from the startState, one or more transition of the same symbol should not lead to the acceptionState",
                minimalDfa.getStartState().getTarget(this.symbol).getTarget(
                        this.symbol) == minimalDfa.getAcceptStates().first());
    }

    @Test
    public void testOneOrMore() {

        this.nfa = this.nfa.oneOrMore();

        MinimalDfa<Integer> minimalDfa = new MinimalDfa<Integer>(
                new Dfa<Integer>(this.nfa));

        // test with no transition
        assertFalse(
                "from the startState, no transition should not lead to an AcceptionState",
                minimalDfa.getStartState() == minimalDfa.getAcceptStates()
                        .first());

        // test with only one transition
        assertTrue(
                "from the startState, one transition of the same symbol should lead to the acceptionState",
                minimalDfa.getStartState().getTarget(this.symbol) == minimalDfa
                        .getAcceptStates().first());

        // test with more than one transition
        assertTrue(
                "from the startState, one or more transition of the same symbol should lead to the acceptionState",
                minimalDfa.getStartState().getTarget(this.symbol).getTarget(
                        this.symbol) == minimalDfa.getAcceptStates().first());
    }

    @Test
    public void testShortest() {

        // Case with null nfa
        try {
            Dfa.shortest(null);
            fail("the nfa cannot be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // TODO Typical case
    }

    @Test
    public void testSubtract() {

        // Case with null nfa
        Nfa<Integer> nullNfa = null;
        try {
            this.nfa.subtract(nullNfa);
            fail("the nfa cannot be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case
        Dfa<Integer> dfa = new Dfa<Integer>(this.nfa);
        Symbol<Integer> subtractSymbol = new Symbol<Integer>(Realms
                .getInteger().createInterval(10));
        Nfa<Integer> secondNfa = new Nfa<Integer>(Realms.getInteger()
                .createInterval(10));

        dfa = this.nfa.subtract(secondNfa);

        // test with the symbol of the substacted nfa
        assertFalse(
                "a transition to a state using a substracted symbol should not lead to the acceptationState.",
                dfa.getStartState().getTarget(subtractSymbol) == dfa
                        .getAcceptStates().first());

        // test with the correct symbol
        assertTrue(
                "a transition to a state using a good symbol should lead to the acceptationState.",
                dfa.getStartState().getTarget(this.symbol) == dfa
                        .getAcceptStates().first());
    }

    @Test
    public void testIntersect() {

        // Case with null nfa
        Nfa<Integer> nullNfa = null;
        try {
            this.nfa.intersect(nullNfa);
            fail("the nfa cannot be null");
        }
        catch (InternalException e) {
            // Expected
        }

        Dfa<Integer> dfa = new Dfa<Integer>(this.nfa);
        Nfa<Integer> secondNfa = new Nfa<Integer>(Realms.getInteger()
                .createInterval(0, 100));

        dfa = this.nfa.intersect(secondNfa);

        // test with the symbol of the intersecting nfa
        assertTrue(
                "a transition to a state using the intersecting symbol should lead to the acceptationState.",
                dfa.getStartState().getTarget(this.symbol) == dfa
                        .getAcceptStates().first());
    }

    @Test
    public void testCombineWith() {

        // Case with null nfa
        Nfa<Integer> nullNfa = null;
        try {
            this.nfa.combineWith(nullNfa);
            fail("nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with unstable nfa
        try {
            this.nfa.combineWith(this.unstableNfa);
            fail("nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }

        try {
            this.unstableNfa.combineWith(this.nfa);
            fail("this Nfa is not stable yet");
        }
        catch (InternalException e) {
            // Expected
        }
        Symbol<Integer> secondSymbol = new Symbol<Integer>(Realms.getInteger()
                .createInterval(10));
        Nfa<Integer> secondNfa = new Nfa<Integer>(Realms.getInteger()
                .createInterval(10));

        NfaCombineResult<Integer> combinedResult = this.nfa
                .combineWith(secondNfa);

        Nfa<Integer> combinedNfa = combinedResult.getNewNfa();

        combinedNfa.stabilize();
        SortedSet<Symbol<Integer>> symbols = combinedNfa.getAlphabet()
                .getSymbols();

        assertTrue(
                "the merging of the two alphabets should contains the two symbols",
                symbols.contains(this.symbol) == true
                        && symbols.contains(secondSymbol));
    }

    @Test
    public void testGetNextStateId() {

        // Case with already stable nfa
        try {
            this.nfa.getNextStateId();
            fail("a stable Nfa may not be modified");
        }
        catch (InternalException e) {
            // Expected
        }

    }

    @Test
    public void testAddState() {

        NfaState<Integer> newState = new NfaState<Integer>(this.unstableNfa);
        newState.stabilize();

        // Case with already stable nfa
        try {
            this.nfa.addState(newState);
            fail("a stable Nfa may not be modified");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with a state already in stateSet
        try {
            this.unstableNfa.addState(newState);
            fail("state is already in state set");
        }
        catch (InternalException e) {
            // Expected
        }
    }
}
