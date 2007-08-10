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

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.exception.InternalException;

// Only testing constructor with null parameters since it's hard to create valid
// state maps manually.
public class NfaCombineResultTest {

    Nfa<Integer> newNfa;

    Nfa<Integer> oldNfa1;

    Nfa<Integer> oldNfa2;

    SortedMap<NfaState<Integer>, NfaState<Integer>> oldNfa1StateMap;

    SortedMap<NfaState<Integer>, NfaState<Integer>> oldNfa2StateMap;

    NfaCombineResult combinedNfa;

    @Test
    public void testNfaCombineResult() {

        this.newNfa = new Nfa<Integer>(Realms.getInteger().createInterval(10,
                20));
        this.oldNfa1 = new Nfa<Integer>(Realms.getInteger().createInterval(100,
                200));
        this.oldNfa2 = new Nfa<Integer>(Realms.getInteger().createInterval(
                1000, 2000));
        this.oldNfa1StateMap = new TreeMap<NfaState<Integer>, NfaState<Integer>>();
        this.oldNfa2StateMap = new TreeMap<NfaState<Integer>, NfaState<Integer>>();

        // Testing for null newNfa.
        this.newNfa = null;
        try {
            this.combinedNfa = new NfaCombineResult<Integer>(this.newNfa,
                    this.oldNfa1, this.oldNfa1StateMap, this.oldNfa1,
                    this.oldNfa2StateMap);
            fail("An InternalException should be thrown: null newNfa");
        }
        catch (InternalException e) {
            // Expected
        }

        // Testing for null oldNfa1.
        this.newNfa = new Nfa<Integer>(Realms.getInteger().createInterval(10,
                20));
        this.oldNfa1 = null;
        try {
            this.combinedNfa = new NfaCombineResult<Integer>(this.newNfa,
                    this.oldNfa1, this.oldNfa1StateMap, this.oldNfa1,
                    this.oldNfa2StateMap);
            fail("An InternalException should be thrown: null oldNfa1");
        }
        catch (InternalException e) {
            // Expected
        }

        // Testing for null oldNfa2.
        this.oldNfa1 = new Nfa<Integer>(Realms.getInteger().createInterval(100,
                200));
        this.oldNfa2 = null;
        try {
            this.combinedNfa = new NfaCombineResult<Integer>(this.newNfa,
                    this.oldNfa1, this.oldNfa1StateMap, this.oldNfa1,
                    this.oldNfa2StateMap);
            fail("An InternalException should be thrown: null oldNfa2");
        }
        catch (InternalException e) {
            // Expected
        }

        // Only testing for first null oldNfa1StateMap since it won't get to the
        // second one.
        try {
            this.combinedNfa = new NfaCombineResult<Integer>(this.newNfa,
                    this.oldNfa1, this.oldNfa1StateMap, this.oldNfa1,
                    this.oldNfa2StateMap);
            fail("An InternalException should be thrown: null oldNfa1StateMap");
        }
        catch (InternalException e) {
            // Expected
        }
    }
}
