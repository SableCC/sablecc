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

package org.sablecc.sablecc.automaton.globalTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sablecc.sablecc.alphabet.AdjacencyRealm;
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.automaton.Dfa;
import org.sablecc.sablecc.automaton.MinimalDfa;
import org.sablecc.sablecc.automaton.Nfa;

public class commentsTest {

    @Test
    public void commentTest() {

        AdjacencyRealm<Character> charRealm = Realms.getCharacter();

        // Building Nfa instances for differents useful intervals
        Nfa<Character> any = new Nfa<Character>(charRealm.createInterval('#',
                '}'));
        Nfa<Character> slash = new Nfa<Character>(charRealm.createInterval('/'));
        Nfa<Character> star = new Nfa<Character>(charRealm.createInterval('*'));
        Nfa<Character> noStar = new Nfa<Character>(charRealm.createInterval(
                '#', ')')).unionWith(new Nfa<Character>(charRealm
                .createInterval('+', '}')));
        Nfa<Character> noStarOrSlash = new Nfa<Character>(charRealm
                .createInterval('#', ')')).unionWith(
                new Nfa<Character>(charRealm.createInterval('+', '.')))
                .unionWith(
                        new Nfa<Character>(charRealm.createInterval('0', '}')));

        // Building minimal dfa comment using method shortest
        MinimalDfa<Character> shortestComment = new MinimalDfa<Character>(slash
                .concatenateWith(star).concatenateWith(any.zeroOrMore())
                .concatenateWith(star).concatenateWith(slash).shortest());

        // Building minimal dfa comment by construction
        Nfa<Character> part1 = noStarOrSlash.concatenateWith(noStar
                .zeroOrMore());
        Nfa<Character> part2 = star.concatenateWith(part1.zeroOrOne());

        Nfa<Character> constructionCommentNfa = slash.concatenateWith(star)
                .concatenateWith(noStar.zeroOrMore()).concatenateWith(
                        part2.zeroOrMore()).concatenateWith(star)
                .concatenateWith(slash);
        MinimalDfa<Character> constructionComment = new MinimalDfa<Character>(
                new Dfa<Character>(constructionCommentNfa));

        // Testing for equality between the 2 minimalDfa for equivalent Dfa
        assertEquals("The two minimal Dfa insstances should be equals.",
                shortestComment.toString(), constructionComment.toString());
    }
}
