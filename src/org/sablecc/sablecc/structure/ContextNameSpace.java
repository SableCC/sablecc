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
import org.sablecc.sablecc.syntax3.node.*;

class ContextNameSpace {

    private final GlobalIndex globalIndex;

    private final Map<String, Context> nameToContextMap = new LinkedHashMap<String, Context>();

    private final Map<Node, Context> nodeToContextMap = new LinkedHashMap<Node, Context>();

    private final Set<Context> contexts = new LinkedHashSet<Context>();

    ContextNameSpace(
            GlobalIndex globalIndex) {

        this.globalIndex = globalIndex;
    }

    void addContext(
            ALexerContext node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getName();
        String name = nameToken == null ? null : nameToken.getText();
        Context context = this.nameToContextMap.get(name);

        if (context == null) {
            context = new Context(this.globalIndex, name);
            this.nameToContextMap.put(name, context);
            this.contexts.add(context);
        }

        this.nodeToContextMap.put(node, context);
        context.setDeclaration(node);
    }

    Context getContext(
            ALexerContext node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        return getContext((Node) node);
    }

    void addContext(
            AParserContext node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        TIdentifier nameToken = node.getName();
        String name = nameToken == null ? null : nameToken.getText();
        Context context = this.nameToContextMap.get(name);

        if (context == null) {
            context = new Context(this.globalIndex, name);
            this.nameToContextMap.put(name, context);
            this.contexts.add(context);
        }

        this.nodeToContextMap.put(node, context);
        context.setDeclaration(node);
    }

    Context getContext(
            AParserContext node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        return getContext((Node) node);
    }

    private Context getContext(
            Node node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        Context context = this.nodeToContextMap.get(node);

        if (context == null) {
            throw new InternalException("context is missing");
        }

        return context;
    }

    Set<Context> getContexts() {

        return Collections.unmodifiableSet(this.contexts);
    }
}
