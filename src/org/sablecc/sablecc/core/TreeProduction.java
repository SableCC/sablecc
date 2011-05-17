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
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class TreeProduction
        implements INameDeclaration {

    private final ATreeProduction declaration;

    private final Grammar grammar;

    private final LocalNamespace namespace = new LocalNamespace();

    private final List<TreeAlternative> alternatives = new LinkedList<TreeAlternative>();

    public TreeProduction(
            ATreeProduction declaration,
            Grammar grammar) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.declaration = declaration;
        this.grammar = grammar;

        findAlternatives(declaration);
    }

    @Override
    public TIdentifier getNameIdentifier() {

        return this.declaration.getName();
    }

    @Override
    public String getName() {

        return getNameIdentifier().getText();
    }

    @Override
    public String getNameType() {

        return "tree production";
    }

    private void findAlternatives(
            Node ast) {

        ast.apply(new DepthFirstAdapter() {

            private final TreeProduction treeProduction = TreeProduction.this;

            private int nextIndex = 1;

            @Override
            public void inATreeAlternative(
                    ATreeAlternative node) {

                if (node.getAlternativeName() != null) {
                    TreeAlternative.NamedTreeAlternative alternative = new TreeAlternative.NamedTreeAlternative(
                            node, TreeProduction.this.grammar,
                            this.treeProduction, this.nextIndex++);

                    this.treeProduction.namespace.addAlternative(alternative);
                    this.treeProduction.alternatives.add(alternative);
                }
                else {
                    TreeAlternative.AnonymousTreeAlternative alternative = new TreeAlternative.AnonymousTreeAlternative(
                            node, TreeProduction.this.grammar,
                            this.treeProduction, this.nextIndex++);

                    this.treeProduction.alternatives.add(alternative);
                }
            }

        });

    }

    public static class LocalNamespace {

        private final Map<String, TreeAlternative.NamedTreeAlternative> nameMap = new HashMap<String, TreeAlternative.NamedTreeAlternative>();

        private void addAlternative(
                TreeAlternative.NamedTreeAlternative alternative) {

            if (alternative == null) {
                throw new InternalException("alternative may not be null");
            }

            String name = alternative.getName();

            if (this.nameMap.containsKey(name)) {
                throw SemanticException.duplicateAlternativeName(alternative,
                        this.nameMap.get(name));
            }

            this.nameMap.put(name, alternative);
        }

    }

}
