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

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Lexer
        implements IVisitableGrammarPart {

    private final List<LexerExpression.NamedExpression> namedExpressions = new LinkedList<LexerExpression.NamedExpression>();

    private final List<Group> groups = new LinkedList<Group>();

    private final List<Investigator.LexerInvestigator> investigators = new LinkedList<Investigator.LexerInvestigator>();

    private final List<Selector.LexerSelector> selectors = new LinkedList<Selector.LexerSelector>();

    private final List<Lexer.LexerPriority> priorities = new LinkedList<Lexer.LexerPriority>();

    public Lexer() {

    }

    public void addNamedExpression(
            LexerExpression.NamedExpression namedExpression) {

        this.namedExpressions.add(namedExpression);
    }

    public void addGroup(
            Group group) {

        this.groups.add(group);
    }

    public void addInvestigator(
            Investigator.LexerInvestigator investigator) {

        this.investigators.add(investigator);
    }

    public void addSelector(
            Selector.LexerSelector selector) {

        this.selectors.add(selector);
    }

    public void addPriority(
            Lexer.LexerPriority priority) {

        this.priorities.add(priority);
    }

    public List<LexerExpression.NamedExpression> getNamedExpressions() {

        return this.namedExpressions;
    }

    public List<Group> getGroups() {

        return this.groups;
    }

    public List<Investigator.LexerInvestigator> getInvestigators() {

        return this.investigators;
    }

    public List<Selector.LexerSelector> getSelectors() {

        return this.selectors;
    }

    public List<Lexer.LexerPriority> getPriorities() {

        return this.priorities;
    }

    @Override
    public void apply(
            IGrammarVisitor visitor) {

        visitor.visitLexer(this);
    }

    public static class LexerPriority
            implements IVisitableGrammarPart {

        private final Grammar grammar;

        private final ALexerPriority declaration;

        public LexerPriority(
                ALexerPriority declaration,
                Grammar grammar) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.declaration = declaration;
            this.grammar = grammar;
        }

        public ALexerPriority getDeclaration() {

            return this.declaration;
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitLexerPriority(this);

        }

    }
}