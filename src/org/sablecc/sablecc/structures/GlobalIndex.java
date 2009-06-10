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

package org.sablecc.sablecc.structures;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.syntax3.node.AGroup;
import org.sablecc.sablecc.syntax3.node.ALexerContext;
import org.sablecc.sablecc.syntax3.node.ALexerInvestigator;
import org.sablecc.sablecc.syntax3.node.ANameExpression;
import org.sablecc.sablecc.syntax3.node.ANormalNamedExpression;
import org.sablecc.sablecc.syntax3.node.ANormalParserProduction;
import org.sablecc.sablecc.syntax3.node.AParserContext;
import org.sablecc.sablecc.syntax3.node.AParserInvestigator;
import org.sablecc.sablecc.syntax3.node.AParserProductionBody;
import org.sablecc.sablecc.syntax3.node.ASelectionNamedExpression;
import org.sablecc.sablecc.syntax3.node.ASelectionParserProduction;
import org.sablecc.sablecc.syntax3.node.ATreeProduction;
import org.sablecc.sablecc.syntax3.node.TChar;
import org.sablecc.sablecc.syntax3.node.TDecChar;
import org.sablecc.sablecc.syntax3.node.THexChar;
import org.sablecc.sablecc.syntax3.node.TIdentifier;
import org.sablecc.sablecc.syntax3.node.Token;

public class GlobalIndex {

    private Language language;

    private final Map<String, Expression> expressions = new LinkedHashMap<String, Expression>();

    private final Map<String, LexerSelector> lexerSelectors = new LinkedHashMap<String, LexerSelector>();

    private final Map<String, Group> groups = new LinkedHashMap<String, Group>();

    private final Map<String, LexerContext> lexerContexts = new LinkedHashMap<String, LexerContext>();

    private final Map<String, LexerInvestigator> lexerInvestigators = new LinkedHashMap<String, LexerInvestigator>();

    private final Map<String, ParserContext> parserContexts = new LinkedHashMap<String, ParserContext>();

    private final Map<String, ParserProduction> parserProductions = new LinkedHashMap<String, ParserProduction>();

    private final Map<String, ParserSelector> parserSelectors = new LinkedHashMap<String, ParserSelector>();

    private final Map<String, ParserInvestigator> parserInvestigators = new LinkedHashMap<String, ParserInvestigator>();

    private final Map<String, TreeProduction> treeProductions = new LinkedHashMap<String, TreeProduction>();

    private final Map<Token, BigInteger> characterValues = new LinkedHashMap<Token, BigInteger>();

    private final Map<ANameExpression, NormalExpression> nameExpressionResolution = new LinkedHashMap<ANameExpression, NormalExpression>();

    private final Set<NormalExpression> normalExpressions = new LinkedHashSet<NormalExpression>();

    public void setLanguage(
            TIdentifier declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (this.language != null) {
            throw new InternalException("language is already set");
        }

        this.language = new Language(declaration);
    }

    public void addExpression(
            ANormalNamedExpression declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        String name = declaration.getName().getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.expressions.get(name).getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerSelectors.get(name).getNameDeclaration());
        }

        NormalExpression normalExpression = new NormalExpression(declaration);
        this.expressions.put(name, normalExpression);
        this.normalExpressions.add(normalExpression);
    }

    public LexerSelector addLexerSelector(
            ASelectionNamedExpression declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        String name = declaration.getSelectorName().getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.expressions.get(name)
                    .getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.lexerSelectors.get(name)
                    .getNameDeclaration());
        }

        LexerSelector lexerSelector = new LexerSelector(declaration);
        this.lexerSelectors.put(name, lexerSelector);
        return lexerSelector;
    }

    public void addExpression(
            TIdentifier nameDeclaration,
            LexerSelector lexerSelector) {

        if (nameDeclaration == null) {
            throw new InternalException("nameDeclaration may not be null");
        }

        if (lexerSelector == null) {
            throw new InternalException("lexerSelector may not be null");
        }

        String name = nameDeclaration.getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.expressions.get(name).getNameDeclaration());
        }

        if (lexerSelector.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(lexerSelector
                    .getNameDeclaration(), nameDeclaration);
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.lexerSelectors.get(name).getNameDeclaration());
        }

        this.expressions.put(name, new SelectionExpression(nameDeclaration,
                lexerSelector));
    }

    public void addGroup(
            AGroup declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        String name = declaration.getName().getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.expressions.get(name).getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerSelectors.get(name).getNameDeclaration());
        }

        if (this.groups.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.groups.get(name).getNameDeclaration());
        }

        this.groups.put(name, new Group(declaration));
    }

    public void addLexerContext(
            ALexerContext declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (declaration.getName() != null) {
            String name = declaration.getName().getText();

            if (this.language.getName().equals(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.language.getNameDeclaration());
            }

            if (this.expressions.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.expressions.get(name)
                        .getNameDeclaration());
            }

            if (this.lexerSelectors.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.lexerSelectors.get(name)
                        .getNameDeclaration());
            }

            if (this.groups.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.groups.get(name).getNameDeclaration());
            }

            if (this.lexerContexts.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.lexerContexts.get(name)
                        .getNameDeclaration());
            }

            this.lexerContexts.put(name, new LexerContext(declaration));
        }
        else {
            this.lexerContexts.put(null, new LexerContext(declaration));
        }
    }

    public void addLexerInvestigator(
            ALexerInvestigator declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        String name = declaration.getName().getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.expressions.get(name).getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerSelectors.get(name).getNameDeclaration());
        }

        if (this.groups.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.groups.get(name).getNameDeclaration());
        }

        if (this.lexerContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.lexerInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        this.lexerInvestigators.put(name, new LexerInvestigator(declaration));
    }

    public ParserContext addParserContext(
            AParserContext declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (declaration.getName() != null) {
            String name = declaration.getName().getText();

            if (this.language.getName().equals(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.language.getNameDeclaration());
            }

            if (this.expressions.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.expressions.get(name)
                        .getNameDeclaration());
            }

            if (this.lexerSelectors.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.lexerSelectors.get(name)
                        .getNameDeclaration());
            }

            if (this.groups.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.groups.get(name).getNameDeclaration());
            }

            if (this.lexerContexts.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.lexerContexts.get(name)
                        .getNameDeclaration());
            }

            if (this.lexerInvestigators.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.lexerContexts.get(name)
                        .getNameDeclaration());
            }

            if (this.parserContexts.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.parserContexts.get(name)
                        .getNameDeclaration());
            }

            if (this.parserProductions.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.parserProductions.get(name)
                        .getNameDeclaration());
            }

            if (this.parserSelectors.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.parserSelectors.get(name)
                        .getNameDeclaration());
            }

            if (this.parserInvestigators.containsKey(name)) {
                throw CompilerException.duplicateDeclaration(declaration
                        .getName(), this.parserInvestigators.get(name)
                        .getNameDeclaration());
            }

            ParserContext parserContext = new ParserContext(this, declaration);
            this.parserContexts.put(name, parserContext);
            return parserContext;
        }
        else {
            ParserContext parserContext = new ParserContext(this, declaration);
            this.parserContexts.put(null, parserContext);
            return parserContext;
        }
    }

    ParserNormalProduction addParserProduction(
            ANormalParserProduction declaration,
            ParserContext parserContext) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        TIdentifier nameDeclaration = ((AParserProductionBody) declaration
                .getParserProductionBody()).getName();
        String name = nameDeclaration.getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.expressions.get(name).getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.lexerSelectors.get(name).getNameDeclaration());
        }

        if (this.groups.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.groups.get(name).getNameDeclaration());
        }

        if (this.lexerContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.lexerInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.parserContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.parserContexts.get(name).getNameDeclaration());
        }

        if (this.parserProductions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.parserProductions.get(name).getNameDeclaration());
        }

        if (this.parserSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.parserSelectors.get(name).getNameDeclaration());
        }

        if (this.parserInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.parserInvestigators.get(name).getNameDeclaration());
        }

        ParserNormalProduction parserProduction = new ParserNormalProduction(
                this, declaration, parserContext);
        this.parserProductions.put(name, parserProduction);
        return parserProduction;
    }

    ParserSelector addParserSelector(
            ASelectionParserProduction declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        String name = declaration.getSelectorName().getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.expressions.get(name)
                    .getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.lexerSelectors.get(name)
                    .getNameDeclaration());
        }

        if (this.groups.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.groups.get(name)
                    .getNameDeclaration());
        }

        if (this.lexerContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.lexerContexts.get(name)
                    .getNameDeclaration());
        }

        if (this.lexerInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.lexerContexts.get(name)
                    .getNameDeclaration());
        }

        if (this.parserContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.parserContexts.get(name)
                    .getNameDeclaration());
        }

        if (this.parserProductions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.parserProductions.get(name)
                    .getNameDeclaration());
        }

        if (this.parserSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.parserSelectors.get(name)
                    .getNameDeclaration());
        }

        if (this.parserInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration
                    .getSelectorName(), this.parserInvestigators.get(name)
                    .getNameDeclaration());
        }

        ParserSelector parserSelector = new ParserSelector(this, declaration);
        this.parserSelectors.put(name, parserSelector);
        return parserSelector;
    }

    ParserSelectionProduction addParserProduction(
            TIdentifier nameDeclaration,
            ParserSelector parserSelector) {

        if (nameDeclaration == null) {
            throw new InternalException("nameDeclaration may not be null");
        }

        if (parserSelector == null) {
            throw new InternalException("parserSelector may not be null");
        }

        String name = nameDeclaration.getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.expressions.get(name).getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.lexerSelectors.get(name).getNameDeclaration());
        }

        if (this.groups.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.groups.get(name).getNameDeclaration());
        }

        if (this.lexerContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.lexerInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.parserContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.parserContexts.get(name).getNameDeclaration());
        }

        if (this.parserProductions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.parserProductions.get(name).getNameDeclaration());
        }

        if (parserSelector.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(parserSelector
                    .getNameDeclaration(), nameDeclaration);
        }

        if (this.parserSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.parserSelectors.get(name).getNameDeclaration());
        }

        if (this.parserInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(nameDeclaration,
                    this.parserInvestigators.get(name).getNameDeclaration());
        }

        ParserSelectionProduction parserProduction = new ParserSelectionProduction(
                nameDeclaration, parserSelector);
        this.parserProductions.put(name, parserProduction);
        return parserProduction;
    }

    ParserInvestigator addParserInvestigator(
            AParserInvestigator declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        String name = declaration.getName().getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.expressions.get(name).getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerSelectors.get(name).getNameDeclaration());
        }

        if (this.groups.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.groups.get(name).getNameDeclaration());
        }

        if (this.lexerContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.lexerInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.parserContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.parserContexts.get(name).getNameDeclaration());
        }

        if (this.parserProductions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.parserProductions.get(name).getNameDeclaration());
        }

        if (this.parserSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.parserSelectors.get(name).getNameDeclaration());
        }

        if (this.parserInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.parserInvestigators.get(name).getNameDeclaration());
        }

        ParserInvestigator parserInvestigator = new ParserInvestigator(
                declaration);
        this.parserInvestigators.put(name, parserInvestigator);
        return parserInvestigator;
    }

    public void addTreeProduction(
            ATreeProduction declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        String name = declaration.getName().getText();

        if (this.language.getName().equals(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.language.getNameDeclaration());
        }

        if (this.expressions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.expressions.get(name).getNameDeclaration());
        }

        if (this.lexerSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerSelectors.get(name).getNameDeclaration());
        }

        if (this.groups.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.groups.get(name).getNameDeclaration());
        }

        if (this.lexerContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.lexerInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.lexerContexts.get(name).getNameDeclaration());
        }

        if (this.parserContexts.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.parserContexts.get(name).getNameDeclaration());
        }

        if (this.parserSelectors.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.parserSelectors.get(name).getNameDeclaration());
        }

        if (this.parserInvestigators.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.parserInvestigators.get(name).getNameDeclaration());
        }

        if (this.treeProductions.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(declaration.getName(),
                    this.treeProductions.get(name).getNameDeclaration());
        }

        this.treeProductions.put(name, new TreeProduction(declaration));
    }

    private Object getDeclarationNoTree(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (this.language.getName().equals(name)) {
            return this.language.getNameDeclaration();
        }

        if (this.expressions.containsKey(name)) {
            return this.expressions.get(name);
        }

        if (this.lexerSelectors.containsKey(name)) {
            return this.lexerSelectors.get(name);
        }

        if (this.groups.containsKey(name)) {
            return this.groups.get(name);
        }

        if (this.lexerContexts.containsKey(name)) {
            return this.lexerContexts.get(name);
        }

        if (this.lexerInvestigators.containsKey(name)) {
            return this.lexerInvestigators.get(name);
        }

        if (this.parserContexts.containsKey(name)) {
            return this.parserContexts.get(name);
        }

        if (this.parserProductions.containsKey(name)) {
            return this.parserProductions.get(name);
        }

        if (this.parserSelectors.containsKey(name)) {
            return this.parserSelectors.get(name);
        }

        if (this.parserInvestigators.containsKey(name)) {
            return this.parserInvestigators.get(name);
        }

        return null;
    }

    public void addCharacter(
            TChar character) {

        if (character == null) {
            throw new InternalException("character may not be null");
        }

        String text = character.getText();
        if (text.charAt(1) == '\\') {
            this.characterValues.put(character, new BigInteger(text.charAt(2)
                    + 0 + ""));
        }
        else {
            this.characterValues.put(character, new BigInteger(text.charAt(1)
                    + 0 + ""));
        }
    }

    public void addCharacter(
            TDecChar character) {

        if (character == null) {
            throw new InternalException("character may not be null");
        }

        this.characterValues.put(character, new BigInteger(character.getText()
                .substring(1)));
    }

    public void addCharacter(
            THexChar character) {

        if (character == null) {
            throw new InternalException("character may not be null");
        }

        this.characterValues.put(character, new BigInteger(character.getText()
                .substring(2), 16));
    }

    public BigInteger getCharacterValue(
            Token token) {

        if (token == null) {
            throw new InternalException("token may not be null");
        }

        if (!this.characterValues.containsKey(token)) {
            throw new InternalException("invalid token");
        }

        return this.characterValues.get(token);
    }

    public void addResolution(
            ANameExpression node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        Object declaration = getDeclarationNoTree(node.getIdentifier()
                .getText());
        if (declaration == null) {
            throw CompilerException.undefinedReference(node.getIdentifier());
        }
        else if (!(declaration instanceof NormalExpression)) {
            throw CompilerException.invalidReference(node.getIdentifier());
        }

        NormalExpression expression = (NormalExpression) declaration;
        this.nameExpressionResolution.put(node, expression);
    }

    public NormalExpression getResolution(
            ANameExpression node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        return this.nameExpressionResolution.get(node);
    }

    public NormalExpression getExpression(
            ANormalNamedExpression node) {

        return (NormalExpression) this.expressions
                .get(node.getName().getText());
    }
}
