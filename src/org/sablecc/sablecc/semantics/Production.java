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
        implements Declaration {

    private Grammar grammar;

    private Node declaration;

    private List<Alternative> alternatives;

    private LocalNameSpace<Alternative> localNameSpace;

    // Cached values

    private String name;

    private Token location;

    Production(
            Grammar grammar,
            Node declaration) {

        this.grammar = grammar;
        this.declaration = declaration;
    }

    @Override
    public String getName() {

        if (this.name == null) {
            if (this.declaration instanceof AParserProduction) {
                this.name = ((AParserProduction) this.declaration).getName()
                        .getText();
            }
            else if (this.declaration instanceof ATreeProduction) {
                this.name = ((ATreeProduction) this.declaration).getName()
                        .getText();
            }
            else {
                throw new InternalException("unhandled case: "
                        + this.declaration.getClass().getSimpleName());
            }
        }

        return this.name;
    }

    @Override
    public boolean isInlinedExpression() {

        return false;
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

    void setAlternatives(
            List<Alternative> alternatives) {

        if (this.alternatives != null) {
            throw new InternalException("alternatives is already set");
        }

        this.alternatives = alternatives;
        this.localNameSpace = new LocalNameSpace<Alternative>(alternatives);
    }
}
