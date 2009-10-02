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

package org.sablecc.sablecc.structure;

import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.automaton.Automaton;
import org.sablecc.sablecc.syntax3.node.ADecCharacter;
import org.sablecc.sablecc.syntax3.node.TDecChar;
import org.sablecc.sablecc.syntax3.node.Token;

public class DecToken
        extends AnonymousToken {

    private final TDecChar declaration;

    private Automaton automaton;

    DecToken(
            ADecCharacter decCharacter,
            boolean isIgnored) {

        super(isIgnored);
        this.declaration = decCharacter.getDecChar();
    }

    @Override
    public Token getNameToken() {

        return this.declaration;
    }

    @Override
    public Automaton getAutomaton() {

        Automaton automaton = this.automaton;

        if (automaton == null) {
            automaton = Automaton.getSymbolLookAnyStarEnd(new Symbol(
                    this.declaration.getText().substring(1)));
            automaton = automaton.accept(getAcceptation()).minimal();
            this.automaton = automaton;
        }

        return automaton;
    }

}
