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
import org.sablecc.sablecc.exception.InternalException;

public class DfaTest {

    Dfa<Integer> dfa;

    Nfa<Integer> nfa;

    @Before
    public void setUp()
            throws Exception {

        this.nfa = new Nfa<Integer>();
        this.dfa = new Dfa<Integer>(this.nfa);
    }

    @SuppressWarnings("unused")
    @Test
    public void testDfa() {

        // Case with null Nfa
        Nfa<Integer> nullNfa = null;

        try {
            Dfa<Integer> dfa = new Dfa<Integer>(nullNfa);
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical Case
        assertTrue("the startState should be an acceptationState", this.dfa
                .getAcceptStates().first() == this.dfa.getStartState());
        assertEquals("there should only be one acceptationState", 1, this.dfa
                .getAcceptStates().size());
    }

    @Test
    public void testShortest() {

        // Case with null Nfa
        Nfa<Integer> nullNfa = null;

        try {
            this.dfa = Dfa.shortest(nullNfa);
            fail("nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

    }

    @Test
    public void testDifference() {

        // Case with null Nfa
        Nfa<Integer> nullNfa = null;

        try {
            this.dfa = Dfa.difference(nullNfa, this.nfa);
            fail("this Nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        try {
            this.dfa = Dfa.difference(this.nfa, nullNfa);
            fail("nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

    }

    @Test
    public void testIntersection() {

        // Case with null Nfa
        Nfa<Integer> nullNfa = null;

        try {
            this.dfa = Dfa.intersection(nullNfa, this.nfa);
            fail("this Nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        try {
            this.dfa = Dfa.intersection(this.nfa, nullNfa);
            fail("nfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

    }
}
