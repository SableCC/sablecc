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

    private final List<Investigator.LexerInvestigator> investigators = new LinkedList<Investigator.LexerInvestigator>();

    private final List<Selector.LexerSelector> selectors = new LinkedList<Selector.LexerSelector>();

    private final List<Lexer.LexerPriority> priorities = new LinkedList<Lexer.LexerPriority>();

    public Lexer() {

    }

    public void addNamedExpression(
            LexerExpression.NamedExpression namedExpression) {

        this.namedExpressions.add(namedExpression);
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

        private LexerExpression high;

        private LexerExpression low;

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

        public void resolveReferences() {

            this.high = resolveUnit(this.declaration.getHigh());
            this.low = resolveUnit(this.declaration.getLow());
        }

        private LexerExpression resolveUnit(
                PUnit pUnit) {

            if (pUnit instanceof ANameUnit) {
                ANameUnit unit = (ANameUnit) pUnit;

                INameDeclaration nameDeclaration = this.grammar
                        .getGlobalReference(unit.getIdentifier().getText());
                if (nameDeclaration instanceof LexerExpression) {
                    return (LexerExpression) nameDeclaration;
                }
                else {
                    throw SemanticException.badReference(
                            nameDeclaration.getNameIdentifier(),
                            nameDeclaration.getNameType(),
                            new String[] { "token" });
                }
            }
            else if (pUnit instanceof AStringUnit) {
                AStringUnit unit = (AStringUnit) pUnit;

                return this.grammar.getStringExpression(unit.getString()
                        .getText());
            }
            else if (pUnit instanceof ACharacterUnit) {
                ACharacterUnit unit = (ACharacterUnit) pUnit;
                PCharacter pCharacter = unit.getCharacter();

                if (pCharacter instanceof ACharCharacter) {
                    ACharCharacter character = (ACharCharacter) pCharacter;

                    return this.grammar.getCharExpression(character.getChar()
                            .getText());
                }
                else if (pCharacter instanceof ADecCharacter) {
                    ADecCharacter character = (ADecCharacter) pCharacter;

                    return this.grammar.getDecExpression(character.getDecChar()
                            .getText());
                }
                else if (pCharacter instanceof AHexCharacter) {
                    AHexCharacter character = (AHexCharacter) pCharacter;

                    return this.grammar.getHexExpression(character.getHexChar()
                            .getText());
                }
                else {
                    throw new InternalException("unhandled character type");
                }
            }
            else if (pUnit instanceof AStartUnit) {
                return this.grammar.getStartExpression();
            }
            else if (pUnit instanceof AEndUnit) {
                return this.grammar.getEndExpression();
            }
            else {
                throw new InternalException("unhandled unit type");
            }
        }
    }
}
