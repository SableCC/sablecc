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

import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.syntax3.node.*;

public class HexToken
        extends AnonymousToken {

    private final THexChar declaration;

    private Automaton automaton;

    HexToken(
            AHexCharacter hexCharacter,
            boolean isIgnored) {

        super(isIgnored);
        this.declaration = hexCharacter.getHexChar();
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
                    this.declaration.getText().substring(2), 16));
            automaton = automaton.accept(getAcceptation()).minimal();
            this.automaton = automaton;
        }

        return automaton;
    }

}
