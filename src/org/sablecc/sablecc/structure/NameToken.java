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

import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.sablecc.util.*;

public class NameToken
        extends MatchedToken {

    private final GlobalIndex globalIndex;

    private final TIdentifier declaration;

    private Automaton automaton;

    NameToken(
            GlobalIndex globalIndex,
            ANameUnit nameUnit,
            boolean isIgnored) {

        super(isIgnored);
        this.globalIndex = globalIndex;
        this.declaration = nameUnit.getIdentifier();
    }

    @Override
    public Token getNameToken() {

        return this.declaration;
    }

    public String get_CamelCaseName() {

        return Utils.to_CamelCase(this.declaration);
    }

    @Override
    public Automaton getAutomaton() {

        Automaton automaton = this.automaton;

        if (automaton == null) {
            NormalExpression normalExpression = (NormalExpression) this.globalIndex
                    .getParserResolution(this.declaration);
            automaton = normalExpression.getAutomaton();
            automaton = automaton.accept(getAcceptation()).minimal();
            this.automaton = automaton;
        }

        return automaton;
    }
}
