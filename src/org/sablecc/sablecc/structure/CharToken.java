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
import org.sablecc.sablecc.syntax3.node.ACharCharacter;
import org.sablecc.sablecc.syntax3.node.TChar;
import org.sablecc.sablecc.syntax3.node.Token;

public class CharToken
        extends AnonymousToken {

    private final TChar declaration;

    private Automaton automaton;

    CharToken(
            ACharCharacter charCharacter,
            boolean isIgnored) {

        super(isIgnored);
        this.declaration = charCharacter.getChar();
    }

    @Override
    public Token getNameToken() {

        return this.declaration;
    }

    @Override
    public Automaton getAutomaton() {

        Automaton automaton = this.automaton;

        if (automaton == null) {
            char c = this.declaration.getText().charAt(1);
            if (c == '\\') {
                c = this.declaration.getText().charAt(2);
            }
            automaton = Automaton.getSymbolLookAnyStarEnd(new Symbol(c));
            automaton = automaton.accept(getAcceptation()).minimal();
            this.automaton = automaton;
        }

        return automaton;
    }

}
