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

package org.sablecc.sablecc.grammar;

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.grammar.transformation.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Production {

    private List<Alternative> alternatives;

    private List<Priority> priorities = new LinkedList<Priority>();

    private final String name;

    private final BigInteger id;

    private SProductionTransformation transformation;

    public Production(
            BigInteger id,
            String name,
            List<Alternative> alternatives) {

        if (id == null) {
            throw new InternalException("id may not be null");
        }

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (alternatives == null) {
            throw new InternalException("alternatives may not be null");
        }

        this.id = id;
        this.alternatives = alternatives;
        this.name = name;

    }

    public Production(
            BigInteger id,
            String name) {

        if (id == null) {
            throw new InternalException("id may not be null");
        }

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.id = id;
        this.alternatives = new LinkedList<Alternative>();
        this.name = name;

    }

    public Production(
            BigInteger id,
            ANameUnit name,
            List<Alternative> alternatives) {

        if (id == null) {
            throw new InternalException("id may not be null");
        }

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (alternatives == null) {
            throw new InternalException("alternatives may not be null");
        }

        this.id = id;
        this.alternatives = alternatives;
        this.name = name.getIdentifier().getText();

    }

    public List<Alternative> getAlternatives() {

        return this.alternatives;
    }

    public void addAlternative(
            Alternative alternative) {

        this.alternatives.add(alternative);
    }

    public void addPriority(
            Priority priority) {

        this.priorities.add(priority);
    }

    public List<Priority> getPriorities() {

        return this.priorities;
    }

    public void removeAlternative(
            Alternative alternative) {

        this.alternatives.remove(alternative);
    }

    public void addAlternatives(
            List<Alternative> alternatives) {

        this.alternatives.addAll(alternatives);
    }

    public void addTransformation(
            SProductionTransformation transformation) {

        this.transformation = transformation;
    }

    public String getName() {

        return this.name;
    }

    public BigInteger getId() {

        return this.id;
    }

    public SProductionTransformation getTransformation() {

        return this.transformation;
    }

    @Override
    public boolean equals(
            Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Production production = (Production) obj;

        return this.name.equals(production.getName());
    }

    @Override
    public String toString() {

        String productionText = getName() + " = " + "\r\n";

        if (this.alternatives.size() > 1) {
            for (Alternative alternative : this.alternatives.subList(0,
                    this.alternatives.size() - 1)) {

                productionText += "  " + alternative.toString() + " | \r\n";
            }
        }

        if (this.alternatives.size() > 0) {
            productionText += "  "
                    + this.alternatives.get(this.alternatives.size() - 1)
                            .toString() + ";";
        }

        return productionText;
    }

}
