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

import org.sablecc.exception.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

class ParserNameSpace {

    private final Map<String, NameUnit> nameToNameUnitMap = new LinkedHashMap<String, NameUnit>();

    private final Map<Node, NameUnit> nodeToNameUnitMap = new LinkedHashMap<Node, NameUnit>();

    private final Set<NormalExpression> normalExpressions = new LinkedHashSet<NormalExpression>();

    void addExpression(
            ANormalNamedExpression node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getName();
        String name = nameToken.getText();

        {
            NameUnit nameUnit = this.nameToNameUnitMap.get(name);

            if (nameUnit != null) {
                throw CompilerException.duplicateDeclaration(nameToken,
                        nameUnit.getNameToken());
            }
        }

        NormalExpression normalExpression = new NormalExpression(node);
        this.nameToNameUnitMap.put(name, normalExpression);
        this.nodeToNameUnitMap.put(node, normalExpression);
        this.normalExpressions.add(normalExpression);
    }

    NormalExpression getExpression(
            ANormalNamedExpression node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        NameUnit nameUnit = getNameUnit(node);

        if (!(nameUnit instanceof NormalExpression)) {
            throw new InternalException("node is not a normal expression");
        }

        return (NormalExpression) nameUnit;
    }

    void addExpression(
            TIdentifier nameToken,
            LexerSelectorMethod lexerSelectorMethod) {

        if (nameToken == null) {
            throw new InternalException("nameToken may not be null");
        }

        if (lexerSelectorMethod == null) {
            throw new InternalException("lexerSelectorMethod may not be null");
        }

        String name = nameToken.getText();
        NameUnit nameUnit = this.nameToNameUnitMap.get(name);

        if (nameUnit != null) {
            throw CompilerException.duplicateDeclaration(nameToken, nameUnit
                    .getNameToken());
        }

        nameUnit = new SelectionExpression(nameToken, lexerSelectorMethod);
        this.nameToNameUnitMap.put(name, nameUnit);
        this.nodeToNameUnitMap.put(nameToken, nameUnit);
    }

    Expression getExpression(
            TIdentifier nameToken) {

        if (nameToken == null) {
            throw new InternalException("nameToken may not be null");
        }

        NameUnit nameUnit = getNameUnit(nameToken);

        if (!(nameUnit instanceof Expression)) {
            throw new InternalException("node is not an expression");
        }

        return (Expression) nameUnit;
    }

    void addGroup(
            AGroup node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getName();
        String name = nameToken.getText();
        NameUnit nameUnit = this.nameToNameUnitMap.get(name);

        if (nameUnit != null) {
            throw CompilerException.duplicateDeclaration(nameToken, nameUnit
                    .getNameToken());
        }

        nameUnit = new Group(node);
        this.nameToNameUnitMap.put(name, nameUnit);
        this.nodeToNameUnitMap.put(node, nameUnit);
    }

    Group getGroup(
            AGroup node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        NameUnit nameUnit = getNameUnit(node);

        if (!(nameUnit instanceof Group)) {
            throw new InternalException("node is not a group");
        }

        return (Group) nameUnit;
    }

    void addProduction(
            ANormalParserProduction node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getName();
        String name = nameToken.getText();
        NameUnit nameUnit = this.nameToNameUnitMap.get(name);

        if (nameUnit != null) {
            throw CompilerException.duplicateDeclaration(nameToken, nameUnit
                    .getNameToken());
        }

        nameUnit = new NormalParserProduction(node);
        this.nameToNameUnitMap.put(name, nameUnit);
        this.nodeToNameUnitMap.put(node, nameUnit);
    }

    Production getProduction(
            ANormalParserProduction node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        NameUnit nameUnit = getNameUnit(node);

        if (!(nameUnit instanceof Production)) {
            throw new InternalException("node is not a production");
        }

        return (Production) nameUnit;
    }

    void addProduction(
            TIdentifier nameToken,
            ParserSelectorMethod parserSelectorMethod) {

        if (nameToken == null) {
            throw new InternalException("nameToken may not be null");
        }

        if (parserSelectorMethod == null) {
            throw new InternalException("parserSelectorMethod may not be null");
        }

        String name = nameToken.getText();
        NameUnit nameUnit = this.nameToNameUnitMap.get(name);

        if (nameUnit != null) {
            throw CompilerException.duplicateDeclaration(nameToken, nameUnit
                    .getNameToken());
        }

        nameUnit = new SelectionParserProduction(nameToken,
                parserSelectorMethod);
        this.nameToNameUnitMap.put(name, nameUnit);
        this.nodeToNameUnitMap.put(nameToken, nameUnit);
    }

    Production getProduction(
            TIdentifier nameToken) {

        if (nameToken == null) {
            throw new InternalException("nameToken may not be null");
        }

        NameUnit nameUnit = getNameUnit(nameToken);

        if (!(nameUnit instanceof Production)) {
            throw new InternalException("node is not a production");
        }

        return (Production) nameUnit;
    }

    private NameUnit getNameUnit(
            Node node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        NameUnit nameUnit = this.nodeToNameUnitMap.get(node);

        if (nameUnit == null) {
            throw new InternalException("nameUnit is missing");
        }

        return nameUnit;
    }

    NameUnit resolve(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        NameUnit nameUnit = this.nameToNameUnitMap.get(identifier.getText());

        if (nameUnit == null) {
            throw CompilerException.undefinedReference(identifier);
        }

        return nameUnit;
    }

    Set<NormalExpression> getNormalExpressions() {

        return Collections.unmodifiableSet(this.normalExpressions);
    }

}
