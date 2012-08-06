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

public abstract class Priority {

    private Production production;

    public Priority(
            Production production) {

        if (production == null) {
            throw new InternalException("Production shouldn't be null");
        }

        this.production = production;
    }

    public abstract void addAlternative(
            Alternative alternative);

    public abstract void addAlternative(
            Alternative alternative,
            int index);

    public abstract List<Alternative> getAlternatives();

    public Production getProduction() {

        return this.production;
    }

    public static class LeftPriority
            extends Priority {

        private List<Alternative> alternatives = new LinkedList<Alternative>();

        public LeftPriority(
                Production production) {

            super(production);
        }

        @Override
        public void addAlternative(
                Alternative alternative) {

            this.alternatives.add(alternative);
        }

        @Override
        public List<Alternative> getAlternatives() {

            return this.alternatives;
        }

        @Override
        public void addAlternative(
                Alternative alternative,
                int index) {

            this.alternatives.add(index, alternative);

        }
    }

    public static class RightPriority
            extends Priority {

        private List<Alternative> alternatives = new LinkedList<Alternative>();

        public RightPriority(
                Production production) {

            super(production);
        }

        @Override
        public void addAlternative(
                Alternative alternative) {

            this.alternatives.add(alternative);
        }

        @Override
        public List<Alternative> getAlternatives() {

            return this.alternatives;
        }

        @Override
        public void addAlternative(
                Alternative alternative,
                int index) {

            this.alternatives.add(index, alternative);

        }
    }

    public static class UnaryPriority
            extends Priority {

        private List<Alternative> alternatives = new LinkedList<Alternative>();

        public UnaryPriority(
                Production production) {

            super(production);
        }

        @Override
        public void addAlternative(
                Alternative alternative) {

            this.alternatives.add(alternative);
        }

        @Override
        public List<Alternative> getAlternatives() {

            return this.alternatives;
        }

        @Override
        public void addAlternative(
                Alternative alternative,
                int index) {

            this.alternatives.add(index, alternative);

        }
    }

}
