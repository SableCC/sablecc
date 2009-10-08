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

import org.sablecc.exception.*;
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.syntax3.node.*;

public class StringToken
        extends AnonymousToken {

    private final TString declaration;

    private Automaton automaton;

    StringToken(
            AStringUnit stringUnit,
            boolean isIgnored) {

        super(isIgnored);
        this.declaration = stringUnit.getString();
    }

    @Override
    public Token getNameToken() {

        return this.declaration;
    }

    @Override
    public Automaton getAutomaton() {

        Automaton automaton = this.automaton;

        if (automaton == null) {

            String text = this.declaration.getText();
            int length = text.length();

            text = text.substring(1, length - 1);
            length -= 2;

            if (length < 1) {
                throw new InternalException("invalid string");
            }

            int i = 0;
            while (i < length) {
                char c = text.charAt(i++);
                if (c == '\\') {
                    c = text.charAt(i++);
                }

                if (automaton == null) {
                    automaton = Automaton
                            .getSymbolLookAnyStarEnd(new Symbol(c));
                }
                else {
                    automaton = automaton.concat(Automaton
                            .getSymbolLookAnyStarEnd(new Symbol(c)));
                }
            }

            automaton = automaton.accept(getAcceptation()).minimal();
            this.automaton = automaton;
        }

        return automaton;
    }

}
