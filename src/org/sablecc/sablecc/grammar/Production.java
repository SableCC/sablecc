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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.grammar.transformation.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Production {

    private List<Alternative> alternatives;

    private final String name;

    private SProductionTransformation transformation;

    public Production(
            String name,
            List<Alternative> alternatives) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (alternatives == null) {
            throw new InternalException("alternatives may not be null");
        }

        this.alternatives = alternatives;
        this.name = name;

    }

    public Production(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.alternatives = new LinkedList<Alternative>();
        this.name = name;

    }

    public Production(
            ANameUnit name,
            List<Alternative> alternatives) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (alternatives == null) {
            throw new InternalException("alternatives may not be null");
        }

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

    public SProductionTransformation getTransformation() {

        return this.transformation;
    }

}
