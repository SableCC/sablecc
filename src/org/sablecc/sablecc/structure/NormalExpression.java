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
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.syntax3.node.*;

public class NormalExpression
        extends Expression {

    private final ANormalNamedExpression declaration;

    private final Set<NormalExpression> dependencies = new LinkedHashSet<NormalExpression>();

    private Automaton automaton;

    NormalExpression(
            ANormalNamedExpression node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        this.declaration = node;
    }

    @Override
    public TIdentifier getNameToken() {

        return this.declaration.getName();
    }

    public PExpression getExpression() {

        return this.declaration.getExpression();
    }

    public void addDependency(
            NormalExpression normalExpression) {

        if (normalExpression == null) {
            throw new InternalException("normalExpression may not be null");
        }

        this.dependencies.add(normalExpression);
    }

    public Set<NormalExpression> getDependencies() {

        return Collections.unmodifiableSet(this.dependencies);
    }

    public void setAutomaton(
            Automaton automaton) {

        if (this.automaton != null) {
            throw new InternalException("automaton is already set");
        }

        this.automaton = automaton;
    }

    public Automaton getAutomaton() {

        return this.automaton;
    }
}
