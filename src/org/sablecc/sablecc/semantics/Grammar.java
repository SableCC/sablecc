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

package org.sablecc.sablecc.semantics;

import java.util.*;

import org.sablecc.sablecc.syntax3.node.*;

public class Grammar
        implements Declaration {

    private AGrammar declaration;

    private Map<Node, Object> nodeMap = new HashMap<Node, Object>();

    private NameSpace parserNameSpace = new NameSpace();

    private NameSpace treeNameSpace = new NameSpace();

    // Cached values

    private String name;

    private Token location;

    Grammar(
            AGrammar declaration) {

        this.declaration = declaration;
        this.nodeMap.put(declaration, this);
        this.parserNameSpace.add(this);
        this.treeNameSpace.add(this);
    }

    @Override
    public String getName() {

        if (this.name == null) {
            this.name = this.declaration.getName().getText();
        }

        return this.name;
    }

    @Override
    public boolean hasQuotedName() {

        return false;
    }

    @Override
    public Token getLocation() {

        if (this.location == null) {
            this.location = this.declaration.getName();
        }

        return this.location;
    }

    void addExpression(
            Node declaration) {

        Expression expression = new Expression(this, declaration);
        this.nodeMap.put(declaration, expression);
        this.parserNameSpace.add(expression);
        this.treeNameSpace.add(expression);
    }

    void addParserProduction(
            AParserProduction declaration) {

        Production production = new Production(this, declaration);
        this.nodeMap.put(declaration, production);
        this.parserNameSpace.add(production);
    }

    void addInlineExpression(
            Node declaration) {

        Expression expression = new Expression(this, declaration);
        this.nodeMap.put(declaration, expression);
        Declaration previousDeclaration = this.parserNameSpace.get(expression
                .getName());
        if (previousDeclaration == null || !previousDeclaration.hasQuotedName()) {
            this.parserNameSpace.add(expression);
            this.treeNameSpace.add(expression);
        }
    }

    void addTreeProduction(
            ATreeProduction declaration) {

        Production production = new Production(this, declaration);
        this.nodeMap.put(declaration, production);
        this.treeNameSpace.add(production);
    }
}
