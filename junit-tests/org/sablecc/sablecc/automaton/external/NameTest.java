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

package org.sablecc.sablecc.automaton.external;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sablecc.sablecc.alphabet.AdjacencyRealm;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.automaton.Dfa;
import org.sablecc.sablecc.automaton.MinimalDfa;
import org.sablecc.sablecc.automaton.Nfa;

public class NameTest {

    @Test
    public void nameTest() {

        AdjacencyRealm<Character> charRealm = Realms.getCharacter();

        Nfa<Character> any = new Nfa<Character>(charRealm.createInterval('A',
                'Z'));
        Nfa<Character> r = new Nfa<Character>(charRealm.createInterval('R'));
        Nfa<Character> a = new Nfa<Character>(charRealm.createInterval('A'));
        Nfa<Character> y = new Nfa<Character>(charRealm.createInterval('Y'));

        // Minimal Dfa by subtraction
        Dfa<Character> allButRay = any.zeroOrMore().subtract(
                any.zeroOrMore().concatenateWith(r).concatenateWith(a)
                        .concatenateWith(y).concatenateWith(any.zeroOrMore()));

        MinimalDfa<Character> minimalBySubstraction = new MinimalDfa<Character>(
                allButRay);

        // Minimal Dfa by construction
        Nfa<Character> allButR = new Nfa<Character>(charRealm.createInterval(
                'A', 'Q')).unionWith(new Nfa<Character>(charRealm
                .createInterval('S', 'Z')));

        Nfa<Character> allButRY = new Nfa<Character>(charRealm.createInterval(
                'A', 'Q')).unionWith(
                new Nfa<Character>(charRealm.createInterval('S', 'X')))
                .unionWith(new Nfa<Character>(charRealm.createInterval('Z')));

        Nfa<Character> allButRA = new Nfa<Character>(charRealm.createInterval(
                'B', 'Q')).unionWith(new Nfa<Character>(charRealm
                .createInterval('S', 'Z')));

        // not_ry not_r*
        Nfa<Character> firstPart = allButRY.concatenateWith(allButR
                .zeroOrMore());

        // a firstPart? | not_ra not_r*
        Nfa<Character> secondPart = a.concatenateWith(firstPart.zeroOrOne())
                .unionWith(allButRA.concatenateWith(allButR.zeroOrMore()));

        // r secondPart?
        Nfa<Character> thirdPart = r.concatenateWith(secondPart.zeroOrOne());

        // not_r* (r (a ( not_ry not_r*)? | not_ra not_r* )? )*
        Nfa<Character> allButRayNfa = allButR.zeroOrMore().concatenateWith(
                thirdPart.zeroOrMore());

        allButRay = new Dfa<Character>(allButRayNfa);

        MinimalDfa<Character> minimalByConstruction = new MinimalDfa<Character>(
                allButRay);

        // Testing if the two minimal Dfa's are equals
        assertEquals(minimalBySubstraction.toString(), minimalByConstruction
                .toString());

    }
}
