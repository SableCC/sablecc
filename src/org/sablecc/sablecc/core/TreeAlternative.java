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
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;
import org.sablecc.util.interfaces.*;

public class TreeAlternative
        implements ImplicitExplicit {

    private final Grammar grammar;

    private final TreeProduction production;

    private final ATreeAlternative declaration;

    private int index;

    private String name;

    private Token token;

    private final LocalNamespace namespace;

    private final LinkedList<TreeElement> elements = new LinkedList<TreeElement>();

    public TreeAlternative(
            ATreeAlternative declaration,
            Grammar grammar,
            TreeProduction production,
            int index) {

        if (declaration == null) {
            throw new InternalException("grammar may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        this.declaration = declaration;
        this.grammar = grammar;
        this.production = production;
        this.index = index;

        findElements();

        this.namespace = new LocalNamespace(this.elements);
    }

    public TreeProduction getProduction() {

        return this.production;
    }

    public int getIndex() {

        return this.index;
    }

    public ATreeAlternative getDeclaration() {

        return this.declaration;
    }

    @Override
    public String getImplicitName() {

        String implicitName = null;

        if (this.declaration.getElements().getFirst() instanceof ANormalElement) {
            ANormalElement firstElement = (ANormalElement) this.declaration
                    .getElements().getFirst();

            if (firstElement.getElementName() != null) {
                implicitName = firstElement.getElementName().getText();
                implicitName = implicitName.substring(1,
                        implicitName.length() - 2);
            }
            else if (firstElement.getUnit() instanceof ANameUnit
                    && (firstElement.getUnaryOperator() == null || firstElement
                            .getUnaryOperator() instanceof AZeroOrOneUnaryOperator)) {
                implicitName = ((ANameUnit) firstElement.getUnit())
                        .getIdentifier().getText();
            }

        }
        else if (this.declaration.getElements().getFirst() instanceof ASeparatedElement) {
            ASeparatedElement firstElement = (ASeparatedElement) this.declaration
                    .getElements().getFirst();

            if (firstElement.getElementName() != null) {
                implicitName = firstElement.getElementName().getText();
                implicitName = implicitName.substring(1,
                        implicitName.length() - 2);
            }// else : separated element can't have an implicit name
        }
        else if (this.declaration.getElements().getFirst() instanceof AAlternatedElement) {
            AAlternatedElement firstElement = (AAlternatedElement) this.declaration
                    .getElements().getFirst();

            if (firstElement.getElementName() != null) {
                implicitName = firstElement.getElementName().getText();
                implicitName = implicitName.substring(1,
                        implicitName.length() - 2);
            }// else : alternated element can't have an implicit name
        }

        return implicitName;
    }

    @Override
    public String getExplicitName() {

        String explicitName = null;

        if (this.declaration.getAlternativeName() != null) {
            explicitName = this.declaration.getAlternativeName().getText();
            explicitName = explicitName.substring(1, explicitName.length() - 2);
            return explicitName;
        }

        return explicitName;

    }

    @Override
    public void setName(
            String name) {

        this.name = name;

    }

    public String getName() {

        // TODO Null ok ?
        return this.name;
    }

    public Token getNameToken() {

        if (this.token == null) {
            if (getExplicitName() != null
                    && getExplicitName().equals(this.name)) {
                this.token = this.declaration.getAlternativeName();
            }
            else if (getImplicitName().equals(this.name)) {
                ANormalElement firstElement = (ANormalElement) this.declaration
                        .getElements().getFirst();

                this.token = ((ANameUnit) firstElement.getUnit())
                        .getIdentifier();
            }
        }

        return this.token;
    }

    protected void findElements() {

        this.declaration.apply(new DepthFirstAdapter() {

            @Override
            public void inANormalElement(
                    ANormalElement node) {

                TreeElement.NormalElement element = new TreeElement.NormalElement(
                        node, TreeAlternative.this.grammar,
                        TreeAlternative.this);

                TreeAlternative.this.elements.add(element);
            }

            @Override
            public void inASeparatedElement(
                    ASeparatedElement node) {

                TreeElement.SeparatedElement element = new TreeElement.SeparatedElement(
                        node, TreeAlternative.this.grammar,
                        TreeAlternative.this);

                TreeAlternative.this.elements.add(element);

            }

            @Override
            public void inAAlternatedElement(
                    AAlternatedElement node) {

                TreeElement.AlternatedElement element = new TreeElement.AlternatedElement(
                        node, TreeAlternative.this.grammar,
                        TreeAlternative.this);

                TreeAlternative.this.elements.add(element);
            }
        });

    }

    private static class LocalNamespace
            extends ImplicitExplicitNamespace<TreeElement> {

        public LocalNamespace(
                final LinkedList<TreeElement> declarations) {

            super(declarations);
        }

        @Override
        protected void raiseDuplicateError(
                TreeElement declaration,
                TreeElement previousDeclaration) {

            throw SemanticException.duplicateElementName(declaration,
                    previousDeclaration);

        }

    }

}
