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

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Production
        extends Declaration {

    private Grammar grammar;

    private Node declaration;

    private List<Alternative> alternatives;

    private LocalNameSpace<Alternative> localNameSpace;

    private ProductionTransformation productionTransformation;

    // Cached values

    private String name;

    private Token location;

    private boolean isRoot;

    private boolean isIncremental;

    private boolean isToken;

    private boolean isDangling;

    private boolean isAction;

    private boolean isSelection;

    Production(
            Grammar grammar,
            Node declaration) {

        this.grammar = grammar;
        this.declaration = declaration;

        detectQualifiers();
    }

    @Override
    public String getName() {

        if (this.name == null) {
            this.name = getLocation().getText();
        }

        return this.name;
    }

    @Override
    public String getLookupName() {

        return getName();
    }

    @Override
    public String getDisplayName() {

        return getLocation().getText();
    }

    @Override
    public Token getLocation() {

        if (this.location == null) {
            if (this.declaration instanceof AParserProduction) {
                this.location = ((AParserProduction) this.declaration)
                        .getName();
            }
            else if (this.declaration instanceof ATreeProduction) {
                this.location = ((ATreeProduction) this.declaration).getName();
            }
            else {
                throw new InternalException("unhandled case: "
                        + this.declaration.getClass().getSimpleName());
            }
        }

        return this.location;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" = ");
        for (Alternative alternative : this.alternatives) {
            sb.append("\n  " + alternative);
        }
        return sb.toString();
    }

    public Alternative getAlternative(
            String name) {

        return this.localNameSpace.get(name);
    }

    public boolean hasAlternative(
            String name) {

        return this.localNameSpace.has(name);
    }

    public ProductionTransformation getTransformation() {

        return this.productionTransformation;
    }

    void setAlternatives(
            List<Alternative> alternatives) {

        if (this.alternatives != null) {
            throw new InternalException("alternatives is already set");
        }

        this.alternatives = alternatives;
        this.localNameSpace = new LocalNameSpace<Alternative>(alternatives);
    }

    void setDeclaredTransformation(
            ProductionTransformation productionTransformation) {

        if (this.productionTransformation != null) {
            Token location = this.productionTransformation.getLocation();
            throw SemanticException.semanticError("The production " + getName()
                    + " was already transformed on line " + location.getLine()
                    + " char " + location.getPos() + ".",
                    productionTransformation.getLocation());
        }

        this.productionTransformation = productionTransformation;
    }

    void setImplicitTransformation(
            ProductionTransformation productionTransformation) {

        if (this.productionTransformation != null) {
            throw new InternalException("Already transformed");
        }

        this.productionTransformation = productionTransformation;
    }

    private void detectQualifiers() {

        if (this.declaration instanceof AParserProduction) {
            ((AParserProduction) this.declaration).apply(new TreeWalker() {

                @Override
                public void caseAParserProduction(
                        AParserProduction node) {

                    for (PQualifier qualifier : node.getQualifiers()) {
                        visit(qualifier);
                    }
                }

                @Override
                public void caseARootQualifier(
                        ARootQualifier node) {

                    if (Production.this.isRoot) {
                        throw SemanticException.semanticError(
                                "The Root qualifier has already been applied.",
                                node.getRootKeyword());
                    }

                    Production.this.isRoot = true;

                    throw SemanticException.notImplementedError(node
                            .getRootKeyword());
                }

                @Override
                public void caseAIncrementalQualifier(
                        AIncrementalQualifier node) {

                    if (Production.this.isIncremental) {
                        throw SemanticException
                                .semanticError(
                                        "The Incremental qualifier has already been applied.",
                                        node.getIncrementalKeyword());
                    }

                    Production.this.isIncremental = true;

                    throw SemanticException.notImplementedError(node
                            .getIncrementalKeyword());
                }

                @Override
                public void caseATokenQualifier(
                        ATokenQualifier node) {

                    if (Production.this.isToken) {
                        throw SemanticException
                                .semanticError(
                                        "The Token qualifier has already been applied.",
                                        node.getTokenKeyword());
                    }

                    Production.this.isToken = true;

                    throw SemanticException.notImplementedError(node
                            .getTokenKeyword());
                }

                @Override
                public void caseADanglingQualifier(
                        ADanglingQualifier node) {

                    if (Production.this.isDangling) {
                        throw SemanticException
                                .semanticError(
                                        "The Dangling qualifier has already been applied.",
                                        node.getDanglingKeyword());
                    }

                    Production.this.isDangling = true;

                    throw SemanticException.notImplementedError(node
                            .getDanglingKeyword());
                }

                @Override
                public void caseAActionQualifier(
                        AActionQualifier node) {

                    if (Production.this.isAction) {
                        throw SemanticException
                                .semanticError(
                                        "The Action qualifier has already been applied.",
                                        node.getActionKeyword());
                    }

                    Production.this.isAction = true;

                    throw SemanticException.notImplementedError(node
                            .getActionKeyword());
                }

                @Override
                public void caseASelectionQualifier(
                        ASelectionQualifier node) {

                    if (Production.this.isSelection) {
                        throw SemanticException
                                .semanticError(
                                        "The Selection qualifier has already been applied.",
                                        node.getSelectionKeyword());
                    }

                    Production.this.isSelection = true;

                    throw SemanticException.notImplementedError(node
                            .getSelectionKeyword());
                }
            });
        }
    }
}
