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

package org.sablecc.sablecc.alphabet;

import java.util.Collection;
import java.util.LinkedList;

public class BugMerge {

    @SuppressWarnings("unused")
    public static void main(
            String[] args) {

        Collection<Interval<Integer>> intervalsInt = new LinkedList<Interval<Integer>>();
        Symbol<Integer> firstSymbol;

        Symbol<Integer> secondSymbol;

        Alphabet<Integer> completeAlphabet;
        Alphabet<Integer> firstPartAlphabet;
        Alphabet<Integer> secondPartAlphabet;
        AlphabetMergeResult<Integer> mergeResult;

        intervalsInt.add(new Interval<Integer>(0, 5, Realms.getInteger()));
        intervalsInt.add(new Interval<Integer>(10, 15, Realms.getInteger()));
        intervalsInt.add(new Interval<Integer>(20, 25, Realms.getInteger()));
        firstPartAlphabet = new Alphabet<Integer>(new Symbol<Integer>(
                intervalsInt));

        intervalsInt.clear();
        intervalsInt.add(new Interval<Integer>(30, 35, Realms.getInteger()));
        intervalsInt.add(new Interval<Integer>(40, 45, Realms.getInteger()));
        intervalsInt.add(new Interval<Integer>(50, 55, Realms.getInteger()));
        secondPartAlphabet = new Alphabet<Integer>(new Symbol<Integer>(
                intervalsInt));

        System.out.println(firstPartAlphabet + "\n" + secondPartAlphabet);

        // Line that cause the bug.
        mergeResult = firstPartAlphabet.mergeWith(secondPartAlphabet);
        System.out.print(mergeResult);

    }

}
