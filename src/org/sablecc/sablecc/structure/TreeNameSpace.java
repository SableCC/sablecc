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

class TreeNameSpace {

    private final Map<String, NameUnit> nameToNameUnitMap = new LinkedHashMap<String, NameUnit>();

    private final Map<Node, NameUnit> nodeToNameUnitMap = new LinkedHashMap<Node, NameUnit>();

    void addExpression(
            ANormalNamedExpression node) {

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

        nameUnit = new NormalExpression(node);
        this.nameToNameUnitMap.put(name, nameUnit);
        this.nodeToNameUnitMap.put(node, nameUnit);
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

    void addProduction(
            ATreeProduction node) {

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

        nameUnit = new TreeProduction(node);
        this.nameToNameUnitMap.put(name, nameUnit);
        this.nodeToNameUnitMap.put(node, nameUnit);
    }

    Production getProduction(
            ATreeProduction node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        NameUnit nameUnit = getNameUnit(node);

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
}
