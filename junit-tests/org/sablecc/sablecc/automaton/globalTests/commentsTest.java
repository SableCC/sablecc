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
        Nfa<Character> comment = new Nfa<Character>();
        comment = slash.concatenateWith(star).concatenateWith(any.oneOrMore())
                .concatenateWith(star).concatenateWith(slash);
        Dfa<Character> shortestComment = comment.shortest();
        MinimalDfa<Character> minimalDfaShortestComment = new MinimalDfa<Character>(
                shortestComment);

        // Building minimal dfa comment by construction
        Nfa<Character> constructionComment = new Nfa<Character>();
        constructionComment = slash.concatenateWith(star).concatenateWith(
                noStar.oneOrMore()).concatenateWith(
                star.concatenateWith(
                        noStarOrSlash.concatenateWith(noStar.oneOrMore()))
                        .zeroOrOne()).oneOrMore().concatenateWith(star)
                .concatenateWith(slash);
        Dfa<Character> constructedDfaComment = new Dfa<Character>(
                constructionComment);
        MinimalDfa<Character> minimalDfaConstructionComment = new MinimalDfa<Character>(
                constructedDfaComment);

        // Testing for equality between the 2 minimalDfa for equivalent Dfa
        assertEquals("The two minimal Dfa insstances should be equals.",
                minimalDfaConstructionComment, minimalDfaShortestComment);
    }
}
