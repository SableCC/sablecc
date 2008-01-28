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
import org.sablecc.sablecc.alphabet.Realms;
import org.sablecc.sablecc.automaton.Dfa;
import org.sablecc.sablecc.automaton.MinimalDfa;
import org.sablecc.sablecc.automaton.Nfa;

// Test the org.sablecc.sablecc.automaton package "externally", using its public
// interface
public class CommentTest {

    // This test creates minimal DFAs for two regular expressions:
    //
    // shortest_comment = Shortest('/*' any* '*/')
    //
    // construction_comment =
    // '/*' not_star* ('*' (not_star_slash not_star*)?)* '*/'
    //
    // It then asserts that the minimal DFAs are identical (comparing their
    // toString() output for lack of better way)
    @Test
    public void commentTest() {

        AdjacencyRealm<Character> charRealm = Realms.getCharacter();

        // Building Nfa instances for various intervals
        Nfa<Character> any = new Nfa<Character>(charRealm.createInterval(
                (char) 0, (char) 255));
        Nfa<Character> slash = new Nfa<Character>(charRealm.createInterval('/'));
        Nfa<Character> star = new Nfa<Character>(charRealm.createInterval('*'));
        Nfa<Character> notStar = new Nfa<Character>(any.subtract(star));
        Nfa<Character> notStarSlash = new Nfa<Character>(notStar
                .subtract(slash));

        // Building minimal dfa for shortest_comment
        MinimalDfa<Character> shortestComment = new MinimalDfa<Character>(slash
                .concatenateWith(star).concatenateWith(any.zeroOrMore())
                .concatenateWith(star).concatenateWith(slash).shortest());

        // Building minimal dfa for construction_comment

        // part1 = not_star_slash not_star*
        Nfa<Character> part1 = notStarSlash.concatenateWith(notStar
                .zeroOrMore());

        // part2 = '*' (part1)?
        Nfa<Character> part2 = star.concatenateWith(part1.zeroOrOne());

        // construction_comment = '/*' not_star* part2* '*/'
        Nfa<Character> constructionCommentNfa = slash.concatenateWith(star)
                .concatenateWith(notStar.zeroOrMore()).concatenateWith(
                        part2.zeroOrMore()).concatenateWith(star)
                .concatenateWith(slash);
        MinimalDfa<Character> constructionComment = new MinimalDfa<Character>(
                new Dfa<Character>(constructionCommentNfa));

        // Testing for equality of toString() representation
        assertEquals("The two minimal Dfa instances must be equal.",
                shortestComment.toString(), constructionComment.toString());
    }
}
