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
import org.sablecc.util.*;

public class TreeProduction
        implements INameDeclaration {

    private final ATreeProduction declaration;

    private final Grammar grammar;

    private final LocalNamespace namespace;

    private final LinkedList<TreeAlternative> alternatives = new LinkedList<TreeAlternative>();

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

        findAlternatives();

        // When there is only one alternative, it doesn't require an implicit
        // name
        if (this.alternatives.size() == 1) {
            this.alternatives.get(0).setName(
                    this.alternatives.get(0).getExplicitName());
            this.namespace = null;
        }
        else {
            this.namespace = new LocalNamespace(this.alternatives);
        }

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

    private void findAlternatives() {

        this.declaration.apply(new DepthFirstAdapter() {

            private final TreeProduction treeProduction = TreeProduction.this;

            private int nextIndex = 1;

            @Override
            public void inATreeAlternative(
                    ATreeAlternative node) {

                TreeAlternative alternative = new TreeAlternative(node,
                        TreeProduction.this.grammar, this.treeProduction,
                        this.nextIndex);

                this.nextIndex += 1;

                this.treeProduction.alternatives.add(alternative);
            }
        });

    }

    private static class LocalNamespace
            extends ImplicitExplicitNamespace<TreeAlternative> {

        public LocalNamespace(
                final LinkedList<TreeAlternative> declarations) {

            super(declarations);
        }

        @Override
        protected void raiseDuplicateError(
                TreeAlternative declaration,
                TreeAlternative previousDeclaration) {

            throw SemanticException.duplicateAlternativeName(declaration,
                    previousDeclaration);

        }

    }

}
