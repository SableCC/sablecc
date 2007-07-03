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

package ticket39;

import org.junit.Test;
import org.sablecc.sablecc.alphabet.AdjacencyRealm;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.automaton.Dfa;
import org.sablecc.sablecc.automaton.MinimalDfa;
import org.sablecc.sablecc.automaton.Nfa;

public class PartitioningBug {

    @Test
    public void bug() {

        AdjacencyRealm<Character> charRealm = Realms.getCharacter();

        Nfa<Character> any = new Nfa<Character>(charRealm.createInterval('A',
                'Z'));
        Nfa<Character> e = new Nfa<Character>(charRealm.createInterval('E'));
        Nfa<Character> t = new Nfa<Character>(charRealm.createInterval('T'));
        Nfa<Character> i = new Nfa<Character>(charRealm.createInterval('I'));
        Nfa<Character> n = new Nfa<Character>(charRealm.createInterval('N'));

        Dfa<Character> allButEtienne = any.zeroOrMore().subtract(
                e.concatenateWith(t).concatenateWith(i).concatenateWith(e)
                        .concatenateWith(n).concatenateWith(n).concatenateWith(
                                e));

        // Line that causes the bug
        new MinimalDfa<Character>(allButEtienne);
    }
}
