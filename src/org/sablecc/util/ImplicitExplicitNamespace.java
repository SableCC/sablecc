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

package org.sablecc.util;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.util.interfaces.*;

/*
 * An Implicit/Explicit name space has the same functionality as a classic
 * name space but also support implicit names. Implicit names have the
 * priority over implicit name. If there is 2 elements with the same implicit
 * name in the name space they are considered as anonymous. Conflicts comes up
 * with 2(or more) explicit names or with 1(or more) explicit and 1(or more)
 * implicit name.
 */
public abstract class ImplicitExplicitNamespace<T extends ImplicitExplicit> {

    private LinkedList<T> declarations;

    private final Map<String, T> nameMap = new HashMap<String, T>();

    private Map<String, Integer> implicitOccurrenceCounter = new HashMap<String, Integer>();

    private Map<String, Integer> explicitOccurrenceCounter = new HashMap<String, Integer>();

    public ImplicitExplicitNamespace(
            final LinkedList<T> declarations) {

        if (declarations == null) {
            throw new InternalException("declarations may not be null");
        }

        this.declarations = declarations;

        buildNamespace();
    }

    public void add(
            final T declaration) {

        this.declarations.add(declaration);
        reBuildNamespace();
    }

    public T get(
            final String name) {

        return this.nameMap.get(name);
    }

    private void reBuildNamespace() {

        this.explicitOccurrenceCounter.clear();
        this.implicitOccurrenceCounter.clear();
        this.nameMap.clear();
        this.buildNamespace();
    }

    private void buildNamespace() {

        buildOccurrencesTable();

        for (T declaration : this.declarations) {
            String name = declaration.getExplicitName();

            if (name != null) {

                declaration.setName(name);
                if (this.nameMap.containsKey(name)) {
                    raiseDuplicateError(declaration, this.nameMap.get(name));
                }
                else {
                    this.nameMap.put(name, declaration);
                }

            }
            else {
                name = declaration.getImplicitName();

                Integer implicitCount = this.implicitOccurrenceCounter
                        .get(name);
                if (implicitCount == null) {
                    implicitCount = 0;
                }

                Integer explicitCount = this.explicitOccurrenceCounter
                        .get(name);
                if (explicitCount == null) {
                    explicitCount = 0;
                }

                if (name != null && !(implicitCount > 1 && explicitCount == 0)) {
                    if (implicitCount == 1 && explicitCount == 0) {
                        this.nameMap.put(name, declaration);
                        declaration.setName(name);
                    }
                    else {
                        /* 1 implicit and 1 explicit conflict : we add the name in the
                         * name space only if there's no entry yet for this name or if
                         * the entry is an explicit name. In order to raise an error between
                         * the implicit and the explicit name token. */
                        declaration.setName(name);
                        if (this.nameMap.containsKey(name)) {
                            T previousDeclaration = this.nameMap.get(name);

                            if (previousDeclaration.getExplicitName() != null) {
                                raiseDuplicateError(declaration,
                                        previousDeclaration);
                            }
                        }
                        else {
                            this.nameMap.put(name, declaration);
                        }
                    }
                }
            }
        }
    }

    protected abstract void raiseDuplicateError(
            T declaration,
            T previousDeclaration);

    private void buildOccurrencesTable() {

        for (T declaration : this.declarations) {
            String name = declaration.getExplicitName();

            if (name != null) {
                Integer nbOccurrences = this.explicitOccurrenceCounter
                        .get(name);
                if (nbOccurrences == null) {
                    nbOccurrences = new Integer(1);
                }
                else {
                    nbOccurrences = nbOccurrences + 1;
                }
                this.explicitOccurrenceCounter.put(name, nbOccurrences);
            }
            else {
                name = declaration.getImplicitName();
                if (name != null) {
                    Integer nbOccurrences = this.implicitOccurrenceCounter
                            .get(name);
                    if (nbOccurrences == null) {
                        nbOccurrences = new Integer(1);
                    }
                    else {
                        nbOccurrences = nbOccurrences + 1;
                    }
                    this.implicitOccurrenceCounter.put(name, nbOccurrences);
                }
            }
        }
    }

}
