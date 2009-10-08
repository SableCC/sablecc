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

class MethodNameSpace {

    private final GlobalIndex globalIndex;

    private final Map<String, Method> nameToMethodMap = new LinkedHashMap<String, Method>();

    private final Map<Node, Method> nodeToMethodMap = new LinkedHashMap<Node, Method>();

    MethodNameSpace(
            GlobalIndex globalIndex) {

        this.globalIndex = globalIndex;
    }

    void addMethod(
            ASelectionNamedExpression node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getSelectorName();
        String name = nameToken == null ? null : nameToken.getText();
        Method method = this.nameToMethodMap.get(name);

        if (method != null) {
            throw CompilerException.duplicateDeclaration(nameToken, method
                    .getNameToken());
        }

        method = new LexerSelectorMethod(node, this.globalIndex);
        this.nameToMethodMap.put(name, method);
        this.nodeToMethodMap.put(node, method);
    }

    Method getMethod(
            ASelectionNamedExpression node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        return getMethod((Node) node);
    }

    void addMethod(
            ALexerInvestigator node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getName();
        String name = nameToken == null ? null : nameToken.getText();
        Method method = this.nameToMethodMap.get(name);

        if (method != null) {
            throw CompilerException.duplicateDeclaration(nameToken, method
                    .getNameToken());
        }

        method = new LexerInvesigatorMethod(node, this.globalIndex);
        this.nameToMethodMap.put(name, method);
        this.nodeToMethodMap.put(node, method);
    }

    Method getMethod(
            ALexerInvestigator node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        return getMethod((Node) node);
    }

    void addMethod(
            ASelectionParserProduction node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getSelectorName();
        String name = nameToken == null ? null : nameToken.getText();
        Method method = this.nameToMethodMap.get(name);

        if (method != null) {
            throw CompilerException.duplicateDeclaration(nameToken, method
                    .getNameToken());
        }

        method = new ParserSelectorMethod(node, this.globalIndex);
        this.nameToMethodMap.put(name, method);
        this.nodeToMethodMap.put(node, method);
    }

    Method getMethod(
            ASelectionParserProduction node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        return getMethod((Node) node);
    }

    void addMethod(
            AParserInvestigator node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getName();
        String name = nameToken == null ? null : nameToken.getText();
        Method method = this.nameToMethodMap.get(name);

        if (method != null) {
            throw CompilerException.duplicateDeclaration(nameToken, method
                    .getNameToken());
        }

        method = new ParserInvesigatorMethod(node, this.globalIndex);
        this.nameToMethodMap.put(name, method);
        this.nodeToMethodMap.put(node, method);
    }

    Method getMethod(
            AParserInvestigator node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        return getMethod((Node) node);
    }

    private Method getMethod(
            Node node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        Method method = this.nodeToMethodMap.get(node);

        if (method == null) {
            throw new InternalException("method is missing");
        }

        return method;
    }

}
