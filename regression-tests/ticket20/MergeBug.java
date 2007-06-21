/* This file is part of SableCC (http://sablecc.org/).
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

package ticket20;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.sablecc.sablecc.alphabet.Alphabet;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.alphabet.Symbol;

public class MergeBug {

    @Test
    public void bug() {

        Collection<Interval<Integer>> intervalsInt = new LinkedList<Interval<Integer>>();

        Alphabet<Integer> firstPartAlphabet;
        Alphabet<Integer> secondPartAlphabet;

        intervalsInt.add(Realms.getInteger().createInterval(0, 5));
        intervalsInt.add(Realms.getInteger().createInterval(10, 15));
        intervalsInt.add(Realms.getInteger().createInterval(20, 25));
        firstPartAlphabet = new Alphabet<Integer>(new Symbol<Integer>(
                intervalsInt));

        intervalsInt.clear();
        intervalsInt.add(Realms.getInteger().createInterval(30, 35));
        intervalsInt.add(Realms.getInteger().createInterval(40, 45));
        intervalsInt.add(Realms.getInteger().createInterval(50, 55));
        secondPartAlphabet = new Alphabet<Integer>(new Symbol<Integer>(
                intervalsInt));

        // Line that cause the bug.
        firstPartAlphabet.mergeWith(secondPartAlphabet);
    }
}
