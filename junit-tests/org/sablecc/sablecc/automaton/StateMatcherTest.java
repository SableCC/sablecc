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

import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.exception.InternalException;

public class StateMatcherTest {

    StateMatcher<Integer> stateMatcher;

    Dfa<Integer> dfa;

    Nfa<Integer> nfa;

    @Before
    public void setUp()
            throws Exception {

        this.nfa = new Nfa<Integer>();

        this.dfa = new Dfa<Integer>(this.nfa);

        this.stateMatcher = new StateMatcher<Integer>(this.dfa, this.nfa);
    }

    @Test
    public void testStateMatcher() {

        // Case with null dfa
        Dfa<Integer> nullDfa = null;
        try {
            this.stateMatcher = new StateMatcher<Integer>(nullDfa, this.nfa);
            fail("dfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
        // Case with null nfa;
        Nfa<Integer> nullNfa = null;
        try {
            this.stateMatcher = new StateMatcher<Integer>(this.dfa, nullNfa);
            fail("nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
    }

    @Test
    public void testGetDfaState() {

        // Case with null nfaStates
        SortedSet<NfaState<Integer>> nfaStates = null;
        try {
            this.stateMatcher.getDfaState(nfaStates);
            fail("nfaStates may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

    }

    @Test
    public void testGetNfaStates() {

        // Case with null dfaState
        DfaState<Integer> nullState = null;
        try {
            this.stateMatcher.getNfaStates(nullState);
            fail("dfaState may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

    }

    @Test
    public void testMatch() {

        // Case with null dfaState
        DfaState<Integer> nullDfaState = null;
        try {
            this.stateMatcher.match(nullDfaState, this.nfa.getAcceptState());
            fail("dfaState may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with null nfaState
        NfaState<Integer> nullNfaState = null;
        try {
            this.stateMatcher.match(this.dfa.getStartState(), nullNfaState);
            fail("nfaState may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
    }

}
