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

import java.util.*;

import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class MatchedToken {

    private String name;

    private final boolean isIgnored;

    private final Map<MatchedToken, LexerPriority> overPriorities = new LinkedHashMap<MatchedToken, LexerPriority>();

    private final Map<MatchedToken, LexerPriority> underPriorities = new LinkedHashMap<MatchedToken, LexerPriority>();

    private final Set<MatchedToken> naturalOverPriorities = new LinkedHashSet<MatchedToken>();

    private Acceptation acceptation;

    MatchedToken(
            boolean isIgnored) {

        this.isIgnored = isIgnored;
    }

    public abstract Token getNameToken();

    public String getName() {

        String name = this.name;

        if (name == null) {
            name = getNameToken().getText();
            this.name = name;
        }

        return name;
    }

    public boolean isIgnored() {

        return this.isIgnored;
    }

    public void addPriorityOver(
            MatchedToken low,
            LexerPriority priority) {

        if (this.underPriorities.containsKey(low)) {
            LexerPriority formerPriority = this.underPriorities.get(low);
            throw CompilerException.conflictingPriorities(priority
                    .getDeclaration().getGt(), this, low, formerPriority
                    .getDeclaration().getGt());
        }

        if (!this.overPriorities.containsKey(low)) {
            this.overPriorities.put(low, priority);
        }
    }

    public void addPriorityUnder(
            MatchedToken high,
            LexerPriority priority) {

        if (this.overPriorities.containsKey(high)) {
            LexerPriority formerPriority = this.underPriorities.get(high);
            throw CompilerException.conflictingPriorities(priority
                    .getDeclaration().getGt(), high, this, formerPriority
                    .getDeclaration().getGt());
        }

        if (!this.underPriorities.containsKey(high)) {
            this.underPriorities.put(high, priority);
        }
    }

    public void addNaturalPriorityOver(
            MatchedToken low) {

        this.naturalOverPriorities.add(low);
    }

    public boolean hasPriorityOver(
            MatchedToken matchedToken) {

        return this.overPriorities.containsKey(matchedToken)
                || this.naturalOverPriorities.contains(matchedToken);
    }

    public abstract Automaton getAutomaton();

    public Acceptation getAcceptation() {

        Acceptation acceptation = this.acceptation;

        if (acceptation == null) {
            acceptation = new Acceptation(getName());
            this.acceptation = acceptation;
        }

        return acceptation;
    }
}
