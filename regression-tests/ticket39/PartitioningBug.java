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

import org.junit.*;
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;

public class PartitioningBug {

    @Test
    public void bug() {

        Automaton any = Automaton.getSymbolLookAnyStarEnd(new Symbol(
                new Interval(Bound.MIN, Bound.MAX)));
        Automaton e = Automaton.getSymbolLookAnyStarEnd(new Symbol('E'));
        Automaton t = Automaton.getSymbolLookAnyStarEnd(new Symbol('T'));
        Automaton i = Automaton.getSymbolLookAnyStarEnd(new Symbol('I'));
        Automaton n = Automaton.getSymbolLookAnyStarEnd(new Symbol('N'));

        Automaton allButEtienne = any.zeroOrMore().diff(
                e.concat(t).concat(i).concat(e).concat(n).concat(n).concat(e));

        // Line that causes the bug
        allButEtienne.minimal();
    }
}
