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

package org.sablecc.sablecc.core;

import java.util.*;

import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;

public class Lexer
        implements IVisitableGrammarPart {

    private final List<LexerExpression.NamedExpression> namedExpressions = new LinkedList<LexerExpression.NamedExpression>();

    private final Map<String, LexerExpression> stringToExpression = new HashMap<String, LexerExpression>();

    private final List<Investigator.LexerInvestigator> investigators = new LinkedList<Investigator.LexerInvestigator>();

    private final List<Selector.LexerSelector> selectors = new LinkedList<Selector.LexerSelector>();

    private Automaton automaton;

    public Automaton getAutomaton() {

        return this.automaton;
    }

    public void setAutomaton(
            Automaton automaton) {

        this.automaton = automaton;
    }

    public Lexer() {

    }

    public void addNamedExpression(
            LexerExpression.NamedExpression namedExpression) {

        this.namedExpressions.add(namedExpression);
        this.stringToExpression.put(namedExpression.getExpressionName(),
                namedExpression);
    }

    public void addInlineExpression(
            LexerExpression.InlineExpression inlineExpression) {

        this.stringToExpression.put(inlineExpression.getExpressionName(),
                inlineExpression);
    }

    public void addInvestigator(
            Investigator.LexerInvestigator investigator) {

        this.investigators.add(investigator);
    }

    public void addSelector(
            Selector.LexerSelector selector) {

        this.selectors.add(selector);
    }

    public List<LexerExpression.NamedExpression> getNamedExpressions() {

        return this.namedExpressions;
    }

    public LexerExpression getExpression(
            String name) {

        return this.stringToExpression.get(name);
    }

    public Collection<LexerExpression> getExpressions() {

        return this.stringToExpression.values();
    }

    public List<Investigator.LexerInvestigator> getInvestigators() {

        return this.investigators;
    }

    public List<Selector.LexerSelector> getSelectors() {

        return this.selectors;
    }

    @Override
    public void apply(
            IGrammarVisitor visitor) {

        visitor.visitLexer(this);
    }
}
