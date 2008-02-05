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
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.automaton.Dfa;
import org.sablecc.sablecc.automaton.MinimalDfa;
import org.sablecc.sablecc.automaton.Nfa;

public class PartitioningBug {

    @Test
    public void bug() {

        Nfa any = new Nfa(new Interval('A', 'Z'));
        Nfa e = new Nfa('E');
        Nfa t = new Nfa('T');
        Nfa i = new Nfa('I');
        Nfa n = new Nfa('N');

        Dfa allButEtienne = any.zeroOrMore().subtract(
                e.concatenateWith(t).concatenateWith(i).concatenateWith(e)
                        .concatenateWith(n).concatenateWith(n).concatenateWith(
                                e));

        // Line that causes the bug
        new MinimalDfa(allButEtienne);
    }
}
