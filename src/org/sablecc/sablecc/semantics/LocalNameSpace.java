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

    private Map<String, T> nameMap;

    LocalNameSpace(
            List<T> localDeclarations) {

        Map<String, List<T>> declarationMap = new TreeMap<String, List<T>>();

        for (T localDeclaration : localDeclarations) {
            String name = localDeclaration.getName();
            List<T> declarations = declarationMap.get(name);
            if (declarations == null) {
                declarations = new LinkedList<T>();
                declarationMap.put(name, declarations);
            }
            declarations.add(localDeclaration);
        }

        for (Entry<String, List<T>> entry : declarationMap.entrySet()) {
            String name = entry.getKey();
            List<T> declarations = entry.getValue();
            if (declarations.size() == 1) {
                T declaration = declarations.get(0);
                this.nameMap.put(name, declaration);
                declaration.setUnambiguousName(name);
                declaration.setInternalName(name);
            }
            else {
                int index = 1;
                for (T declaration : declarations) {
                    String unambiguousName = name + "." + index++;
                    declaration.setUnambiguousName("");
                    declaration.setInternalName(unambiguousName);
                }
            }
        }
    }

    public T get(
            String name) {

        return this.nameMap.get(name);
    }
}
