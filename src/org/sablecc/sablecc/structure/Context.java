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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.syntax3.node.AAnyUnit;
import org.sablecc.sablecc.syntax3.node.ACharCharacter;
import org.sablecc.sablecc.syntax3.node.ADecCharacter;
import org.sablecc.sablecc.syntax3.node.AEpsilonUnit;
import org.sablecc.sablecc.syntax3.node.AHexCharacter;
import org.sablecc.sablecc.syntax3.node.ALexerContext;
import org.sablecc.sablecc.syntax3.node.ALexerPriority;
import org.sablecc.sablecc.syntax3.node.ANameUnit;
import org.sablecc.sablecc.syntax3.node.AParserContext;
import org.sablecc.sablecc.syntax3.node.AStringUnit;
import org.sablecc.sablecc.syntax3.node.TIdentifier;
import org.sablecc.sablecc.syntax3.node.Token;

public class Context {

    private final GlobalIndex globalIndex;

    private final String name;

    private ALexerContext lexerDeclaration;

    private AParserContext parserDeclaration;

    private final Map<String, MatchedToken> nameToMatchedTokenMap = new LinkedHashMap<String, MatchedToken>();

    private final Set<MatchedToken> matchedTokens = new LinkedHashSet<MatchedToken>();

    Context(
            GlobalIndex globalIndex,
            String name) {

        this.globalIndex = globalIndex;
        this.name = name;
    }

    void setDeclaration(
            ALexerContext node) {

        if (this.lexerDeclaration != null) {

            throw CompilerException.duplicateDeclaration(node.getName(),
                    this.lexerDeclaration.getName());
        }

        if (this.name == null) {
            if (node.getName() != null) {
                throw new InternalException("incorrect name");
            }
        }
        else {
            if (node.getName() == null
                    || !node.getName().getText().equals(this.name)) {
                throw new InternalException("incorrect name");
            }
        }

        this.lexerDeclaration = node;
    }

    void setDeclaration(
            AParserContext node) {

        if (this.parserDeclaration != null) {

            throw CompilerException.duplicateDeclaration(node.getName(),
                    this.parserDeclaration.getName());
        }

        if (this.name == null) {
            if (node.getName() != null) {
                throw new InternalException("incorrect name");
            }
        }
        else {
            if (node.getName() == null
                    || !node.getName().getText().equals(this.name)) {
                throw new InternalException("incorrect name");
            }
        }

        this.parserDeclaration = node;
    }

    public void addMatchedToken(
            ANameUnit unit,
            boolean isIgnored) {

        TIdentifier nameToken = unit.getIdentifier();
        String name = nameToken.getText();

        NameUnit nameUnit = this.globalIndex.getParserResolution(nameToken);
        if (!(nameUnit instanceof NormalExpression)) {
            throw CompilerException.invalidReference(nameToken);
        }

        if (this.nameToMatchedTokenMap.containsKey(name)) {
            MatchedToken firstMatchedToken = this.nameToMatchedTokenMap
                    .get(name);
            throw CompilerException.duplicateDeclaration(nameToken,
                    firstMatchedToken.getNameToken());
        }

        MatchedToken matchedToken = new NameToken(this.globalIndex, unit,
                isIgnored);
        this.nameToMatchedTokenMap.put(name, matchedToken);
        this.matchedTokens.add(matchedToken);
    }

    public void addMatchedToken(
            AStringUnit unit,
            boolean isIgnored) {

        Token nameToken = unit.getString();
        String name = nameToken.getText();

        if (this.nameToMatchedTokenMap.containsKey(name)) {
            MatchedToken firstMatchedToken = this.nameToMatchedTokenMap
                    .get(name);
            throw CompilerException.duplicateDeclaration(nameToken,
                    firstMatchedToken.getNameToken());
        }

        MatchedToken matchedToken = new StringToken(unit, isIgnored);
        this.nameToMatchedTokenMap.put(name, matchedToken);
        this.matchedTokens.add(matchedToken);
    }

    public void addMatchedToken(
            AEpsilonUnit unit,
            boolean isIgnored) {

        Token nameToken = unit.getEpsilon();
        String name = nameToken.getText();

        if (this.nameToMatchedTokenMap.containsKey(name)) {
            MatchedToken firstMatchedToken = this.nameToMatchedTokenMap
                    .get(name);
            throw CompilerException.duplicateDeclaration(nameToken,
                    firstMatchedToken.getNameToken());
        }

        MatchedToken matchedToken = new EpsilonToken(unit, isIgnored);
        this.nameToMatchedTokenMap.put(name, matchedToken);
        this.matchedTokens.add(matchedToken);
    }

    public void addMatchedToken(
            AAnyUnit unit,
            boolean isIgnored) {

        Token nameToken = unit.getAnyKeyword();
        String name = nameToken.getText();

        if (this.nameToMatchedTokenMap.containsKey(name)) {
            MatchedToken firstMatchedToken = this.nameToMatchedTokenMap
                    .get(name);
            throw CompilerException.duplicateDeclaration(nameToken,
                    firstMatchedToken.getNameToken());
        }

        MatchedToken matchedToken = new AnyToken(unit, isIgnored);
        this.nameToMatchedTokenMap.put(name, matchedToken);
        this.matchedTokens.add(matchedToken);
    }

    public void addMatchedToken(
            ACharCharacter character,
            boolean isIgnored) {

        Token nameToken = character.getChar();
        String name = nameToken.getText();

        if (this.nameToMatchedTokenMap.containsKey(name)) {
            MatchedToken firstMatchedToken = this.nameToMatchedTokenMap
                    .get(name);
            throw CompilerException.duplicateDeclaration(nameToken,
                    firstMatchedToken.getNameToken());
        }

        MatchedToken matchedToken = new CharToken(character, isIgnored);
        this.nameToMatchedTokenMap.put(name, matchedToken);
        this.matchedTokens.add(matchedToken);
    }

    public void addMatchedToken(
            ADecCharacter character,
            boolean isIgnored) {

        Token nameToken = character.getDecChar();
        String name = nameToken.getText();

        if (this.nameToMatchedTokenMap.containsKey(name)) {
            MatchedToken firstMatchedToken = this.nameToMatchedTokenMap
                    .get(name);
            throw CompilerException.duplicateDeclaration(nameToken,
                    firstMatchedToken.getNameToken());
        }

        MatchedToken matchedToken = new DecToken(character, isIgnored);
        this.nameToMatchedTokenMap.put(name, matchedToken);
        this.matchedTokens.add(matchedToken);
    }

    public void addMatchedToken(
            AHexCharacter character,
            boolean isIgnored) {

        Token nameToken = character.getHexChar();
        String name = nameToken.getText();

        if (this.nameToMatchedTokenMap.containsKey(name)) {
            MatchedToken firstMatchedToken = this.nameToMatchedTokenMap
                    .get(name);
            throw CompilerException.duplicateDeclaration(nameToken,
                    firstMatchedToken.getNameToken());
        }

        MatchedToken matchedToken = new HexToken(character, isIgnored);
        this.nameToMatchedTokenMap.put(name, matchedToken);
        this.matchedTokens.add(matchedToken);
    }

    public MatchedToken getMatchedToken(
            ANameUnit node) {

        TIdentifier nameToken = node.getIdentifier();
        MatchedToken matchedToken = this.nameToMatchedTokenMap.get(nameToken
                .getText());

        if (matchedToken == null) {
            // check that it's a valid name
            this.globalIndex.getParserResolution(nameToken);

            throw CompilerException.notAToken(nameToken);
        }

        return matchedToken;
    }

    public MatchedToken getMatchedToken(
            AStringUnit node) {

        Token nameToken = node.getString();
        MatchedToken matchedToken = this.nameToMatchedTokenMap.get(nameToken
                .getText());

        if (matchedToken == null) {
            throw CompilerException.notAToken(nameToken);
        }

        return matchedToken;
    }

    public MatchedToken getMatchedToken(
            AEpsilonUnit node) {

        Token nameToken = node.getEpsilon();
        MatchedToken matchedToken = this.nameToMatchedTokenMap.get(nameToken
                .getText());

        if (matchedToken == null) {
            throw CompilerException.notAToken(nameToken);
        }

        return matchedToken;
    }

    public MatchedToken getMatchedToken(
            AAnyUnit node) {

        Token nameToken = node.getAnyKeyword();
        MatchedToken matchedToken = this.nameToMatchedTokenMap.get(nameToken
                .getText());

        if (matchedToken == null) {
            throw CompilerException.notAToken(nameToken);
        }

        return matchedToken;
    }

    public MatchedToken getMatchedToken(
            ACharCharacter node) {

        Token nameToken = node.getChar();
        MatchedToken matchedToken = this.nameToMatchedTokenMap.get(nameToken
                .getText());

        if (matchedToken == null) {
            throw CompilerException.notAToken(nameToken);
        }

        return matchedToken;
    }

    public MatchedToken getMatchedToken(
            ADecCharacter node) {

        Token nameToken = node.getDecChar();
        MatchedToken matchedToken = this.nameToMatchedTokenMap.get(nameToken
                .getText());

        if (matchedToken == null) {
            throw CompilerException.notAToken(nameToken);
        }

        return matchedToken;
    }

    public MatchedToken getMatchedToken(
            AHexCharacter node) {

        Token nameToken = node.getHexChar();
        MatchedToken matchedToken = this.nameToMatchedTokenMap.get(nameToken
                .getText());

        if (matchedToken == null) {
            throw CompilerException.notAToken(nameToken);
        }

        return matchedToken;
    }

    public void addPriority(
            ALexerPriority node,
            MatchedToken high,
            MatchedToken low) {

        Priority priority = new Priority(node, high, low);

        high.addPriorityOver(low, priority);
        low.addPriorityUnder(high, priority);
    }

    public Set<MatchedToken> getMatchedTokens() {

        return Collections.unmodifiableSet(this.matchedTokens);
    }

    public MatchedToken getMatchedToken(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        MatchedToken matchedToken = this.nameToMatchedTokenMap.get(name);

        if (matchedToken == null) {
            throw new InternalException("invalid name");
        }

        return matchedToken;
    }
}
