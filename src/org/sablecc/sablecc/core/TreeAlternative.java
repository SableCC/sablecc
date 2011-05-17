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

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class TreeAlternative {

    private final Grammar grammar;

    private final TreeProduction production;

    private int index;

    private TreeAlternative(
            Grammar grammar,
            TreeProduction production,
            int index) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        this.grammar = grammar;
        this.production = production;
        this.index = index;
    }

    public TreeProduction getProduction() {

        return this.production;
    }

    public static class NamedTreeAlternative
            extends TreeAlternative {

        private final ATreeAlternative declaration;

        private String name;

        public NamedTreeAlternative(
                ATreeAlternative declaration,
                Grammar grammar,
                TreeProduction production,
                int index) {

            super(grammar, production, index);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        public TAlternativeName getNameToken() {

            return this.declaration.getAlternativeName();
        }

        public String getName() {

            if (this.name == null) {
                String name = getNameToken().getText();
                name = name.substring(1, name.length() - 2);
                this.name = name;
            }

            return this.name;
        }
    }

    public static class AnonymousTreeAlternative
            extends TreeAlternative {

        private final ATreeAlternative declaration;

        public AnonymousTreeAlternative(
                ATreeAlternative declaration,
                Grammar grammar,
                TreeProduction production,
                int index) {

            super(grammar, production, index);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }
    }

}
