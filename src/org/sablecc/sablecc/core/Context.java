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
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.launcher.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Context
        implements IVisitableGrammarPart, INameDeclaration {

    private enum Type {
        NAMED,
        ANONYMOUS
    };

    private final Type type;

    private final Grammar grammar;

    private ATokens tokensDeclaration;

    private AIgnored ignoredDeclaration;

    private Set<IToken> tokenSet = new LinkedHashSet<IToken>();

    private Set<IToken> ignoredSet = new LinkedHashSet<IToken>();

    private ALexerContext lexerDeclaration;

    private AParserContext parserDeclaration;

    public Context(
            Grammar grammar,
            ALexerContext declaration) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        this.grammar = grammar;
        this.lexerDeclaration = declaration;

        if (declaration.getName() == null) {
            this.type = Type.ANONYMOUS;
        }
        else {
            this.type = Type.NAMED;
        }

        findTokensAndIgnored(this, this.lexerDeclaration);
    }

    public Context(
            Grammar grammar,
            AParserContext declaration) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        this.grammar = grammar;
        this.parserDeclaration = declaration;

        if (declaration.getName() == null) {
            this.type = Type.ANONYMOUS;
        }
        else {
            this.type = Type.NAMED;
        }

    }

    public boolean isNamed() {

        return this.type == Type.NAMED;
    }

    void addDeclaration(
            AParserContext declaration) {

        switch (this.type) {

        case ANONYMOUS:
            if (this.parserDeclaration != null) {
                throw new InternalException(
                        "the anonymous context may not have two parser declarations");
            }

            if (declaration.getName() != null) {
                throw new InternalException("invalid context declaration");
            }
            break;

        case NAMED:
            if (this.parserDeclaration != null) {
                throw SemanticException.spuriousParserNamedContextDeclaration(
                        declaration, this);
            }

            if (declaration.getName() == null
                    || !declaration.getName().getText().equals(getName())) {
                throw new InternalException("invalid context declaration");
            }
            break;
        }

        this.parserDeclaration = declaration;
    }

    private void addTokensDeclaration(
            ATokens tokensDeclaration) {

        this.tokensDeclaration = tokensDeclaration;
    }

    private void addIgnoredDeclaration(
            AIgnored ignoredDeclaration) {

        this.ignoredDeclaration = ignoredDeclaration;
    }

    private void resolveUnitsAndAddToSet(
            List<PUnit> units,
            Set<IToken> set) {

        for (PUnit pUnit : units) {

            if (pUnit instanceof ANameUnit) {
                ANameUnit unit = (ANameUnit) pUnit;

                INameDeclaration nameDeclaration = this.grammar
                        .getGlobalReference(unit.getIdentifier().getText());
                if (nameDeclaration instanceof IToken) {
                    set.add((IToken) nameDeclaration);
                }
                else if (nameDeclaration == null) {
                    throw SemanticException.undefinedReference(unit
                            .getIdentifier());
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

                set.add(this.grammar.getStringExpression(unit.getString()
                        .getText()));
            }
            else if (pUnit instanceof ACharacterUnit) {
                ACharacterUnit unit = (ACharacterUnit) pUnit;
                PCharacter pCharacter = unit.getCharacter();

                if (pCharacter instanceof ACharCharacter) {
                    ACharCharacter character = (ACharCharacter) pCharacter;

                    set.add(this.grammar.getCharExpression(character.getChar()
                            .getText()));
                }
                else if (pCharacter instanceof ADecCharacter) {
                    ADecCharacter character = (ADecCharacter) pCharacter;

                    set.add(this.grammar.getDecExpression(character
                            .getDecChar().getText()));
                }
                else if (pCharacter instanceof AHexCharacter) {
                    AHexCharacter character = (AHexCharacter) pCharacter;

                    set.add(this.grammar.getHexExpression(character
                            .getHexChar().getText()));
                }
                else {
                    throw new InternalException("unhandled character type");
                }
            }
            else if (pUnit instanceof AStartUnit) {
                set.add(this.grammar.getStartExpression());
            }
            else if (pUnit instanceof AEndUnit) {
                set.add(this.grammar.getEndExpression());
            }
            else {
                throw new InternalException("unhandled unit type");
            }
        }
    }

    public void resolveTokensAndIgnored() {

        if (this.tokensDeclaration != null) {
            resolveUnitsAndAddToSet(this.tokensDeclaration.getUnits(),
                    this.tokenSet);
        }

        if (this.ignoredDeclaration != null) {
            resolveUnitsAndAddToSet(this.ignoredDeclaration.getUnits(),
                    this.ignoredSet);
        }

        Set<IToken> intersection = new HashSet<IToken>();
        intersection.addAll(this.tokenSet);
        intersection.retainAll(this.ignoredSet);
        if (intersection.size() > 0) {
            if (GrammarCompiler.RESTRICTED_SYNTAX) {
                LexerExpression token = (LexerExpression) intersection
                        .iterator().next();
                throw SemanticException.genericError("The "
                        + token.getExpressionName()
                        + " token is both ignored and not.");
            }
            else {
                LexerExpression token = (LexerExpression) intersection
                        .iterator().next();
                throw SemanticException.genericError("The "
                        + token.getExpressionName()
                        + " token is both ignored and not.");
                // TODO
            }
        }
    }

    Automaton computeAutomaton() {

        Automaton automaton = Automaton.getEmptyAutomaton();

        // TODO this code may only be stable with restricted syntax
        for (IToken iToken : this.tokenSet) {
            if (iToken instanceof LexerExpression) {
                LexerExpression token = (LexerExpression) iToken;
                automaton = automaton.or(token.getAutomaton().accept(
                        token.getAcceptation()));
            }
        }

        for (IToken iToken : this.ignoredSet) {
            if (iToken instanceof LexerExpression) {
                LexerExpression token = (LexerExpression) iToken;
                automaton = automaton.or(token.getAutomaton().accept(
                        token.getAcceptation()));
            }
        }

        return automaton;
    }

    private static void findTokensAndIgnored(
            final Context context,
            Node contextDeclaration) {

        contextDeclaration.apply(new DepthFirstAdapter() {

            @Override
            public void inATokens(
                    ATokens node) {

                context.addTokensDeclaration(node);
            }

            @Override
            public void inAIgnored(
                    AIgnored node) {

                context.addIgnoredDeclaration(node);
            }
        });
    }

    @Override
    public TIdentifier getNameIdentifier() {

        if (!isNamed()) {
            throw new InternalException(
                    "getNamedIdentifier() shouldn't not be used with an anonymous context");
        }

        if (this.lexerDeclaration != null) {
            return this.lexerDeclaration.getName();
        }

        return this.parserDeclaration.getName();
    }

    @Override
    public String getName() {

        if (!isNamed()) {
            throw new InternalException(
                    "getName() shouldn't not be used with an anonymous context");
        }

        return getNameIdentifier().getText();
    }

    @Override
    public String getNameType() {

        return "context";
    }

    public AParserContext getParserDeclaration() {

        return this.parserDeclaration;
    }

    public Set<LexerExpression> getLexerExpressionTokens() {

        Set<LexerExpression> set = new LinkedHashSet<LexerExpression>();

        for (IToken iToken : this.tokenSet) {
            if (iToken instanceof LexerExpression) {
                LexerExpression token = (LexerExpression) iToken;
                set.add(token);
            }
        }
        return set;
    }

    public Set<LexerExpression> getLexerExpressionTokensAndIgnored() {

        Set<LexerExpression> set = getLexerExpressionTokens();

        for (IToken iToken : this.ignoredSet) {
            if (iToken instanceof LexerExpression) {
                LexerExpression token = (LexerExpression) iToken;
                set.add(token);
            }
        }
        return set;
    }

    public boolean isIgnored(
            LexerExpression lexerExpression) {

        return this.ignoredSet.contains(lexerExpression);
    }

    public void addTokenIfNecessary(
            PUnit pUnit) {

        if (pUnit instanceof ANameUnit) {
            ANameUnit unit = (ANameUnit) pUnit;

            INameDeclaration nameDeclaration = this.grammar
                    .getGlobalReference(unit.getIdentifier().getText());
            if (nameDeclaration instanceof IToken) {
                this.tokenSet.add((IToken) nameDeclaration);
            }
        }
        else if (pUnit instanceof AStringUnit) {
            AStringUnit unit = (AStringUnit) pUnit;

            this.tokenSet.add(this.grammar.getStringExpression(unit.getString()
                    .getText()));
        }
        else if (pUnit instanceof ACharacterUnit) {
            ACharacterUnit unit = (ACharacterUnit) pUnit;
            PCharacter pCharacter = unit.getCharacter();

            if (pCharacter instanceof ACharCharacter) {
                ACharCharacter character = (ACharCharacter) pCharacter;

                this.tokenSet.add(this.grammar.getCharExpression(character
                        .getChar().getText()));
            }
            else if (pCharacter instanceof ADecCharacter) {
                ADecCharacter character = (ADecCharacter) pCharacter;

                this.tokenSet.add(this.grammar.getDecExpression(character
                        .getDecChar().getText()));
            }
            else if (pCharacter instanceof AHexCharacter) {
                AHexCharacter character = (AHexCharacter) pCharacter;

                this.tokenSet.add(this.grammar.getHexExpression(character
                        .getHexChar().getText()));
            }
            else {
                throw new InternalException("unhandled character type");
            }
        }
        else if (pUnit instanceof AStartUnit) {
            this.tokenSet.add(this.grammar.getStartExpression());
        }
        else if (pUnit instanceof AEndUnit) {
            this.tokenSet.add(this.grammar.getEndExpression());
        }
        else {
            throw new InternalException("unhandled unit type");
        }
    }

    @Override
    public void apply(
            IGrammarVisitor visitor) {

        visitor.visitContext(this);

    }

}
