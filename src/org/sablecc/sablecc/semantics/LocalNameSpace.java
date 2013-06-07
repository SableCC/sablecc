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
import java.util.Map.Entry;

public class LocalNameSpace<T extends LocalDeclaration> {

    private Map<String, T> nameMap = new TreeMap<String, T>();

    private Set<String> nameSet = new TreeSet<String>();

    LocalNameSpace(
            List<T> localDeclarations) {

        Map<String, List<T>> declarationMap = new TreeMap<String, List<T>>();
        List<T> anonymousList = new LinkedList<T>();

        for (T localDeclaration : localDeclarations) {
            String name = localDeclaration.getName();
            if (name == null) {
                anonymousList.add(localDeclaration);
            }
            else {
                List<T> declarations = declarationMap.get(name);
                if (declarations == null) {
                    declarations = new LinkedList<T>();
                    declarationMap.put(name, declarations);
                }
                declarations.add(localDeclaration);
            }
        }

        for (Entry<String, List<T>> entry : declarationMap.entrySet()) {
            String name = entry.getKey();
            this.nameSet.add(name);
            List<T> declarations = entry.getValue();
            if (declarations.size() == 1) {
                T declaration = declarations.get(0);
                declaration.setUnambiguousAndInternalNames(name, name);
                this.nameMap.put(name, declaration);
            }
            else {
                int index = 1;
                for (T declaration : declarations) {
                    String internalName = name + "." + index++;
                    declaration.setUnambiguousAndInternalNames(null,
                            internalName);
                }
            }
        }

        int index = 1;
        for (T declaration : anonymousList) {
            String internalName = "." + index++;
            declaration.setUnambiguousAndInternalNames(null, internalName);
        }
    }

    public T get(
            String name) {

        return this.nameMap.get(name);
    }

    public boolean has(
            String name) {

        return this.nameSet.contains(name);
    }
}
