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

public class MinimalDfaTest {

    MinimalDfa<Integer> minimalDfa;

    Dfa<Integer> dfa;

    @Before
    public void setUp()
            throws Exception {

        this.dfa = new Dfa<Integer>(new Nfa<Integer>(Realms.getInteger()
                .createInterval(10)).oneOrMore());
        this.minimalDfa = new MinimalDfa<Integer>(this.dfa);
    }

    @Test
    public void testMinimalDfa() {

        // Case with null Dfa
        Dfa<Integer> nullDfa = null;
        try {
            this.minimalDfa = new MinimalDfa<Integer>(nullDfa);
            fail("dfa may not be null");
        }
        catch (InternalException e) {
            // Expected
        }
    }
}
