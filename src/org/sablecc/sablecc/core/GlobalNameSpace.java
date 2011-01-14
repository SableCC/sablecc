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

import java.util.*;

import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class GlobalNameSpace {

    private Map<String, Named> nameMap = new HashMap<String, Named>();

    private void add(
            Named named) {

        String name = named.getName();
        if (this.nameMap.containsKey(name)) {
            throw CompilerException.duplicateDeclaration(named,
                    this.nameMap.get(name));
        }
        this.nameMap.put(name, named);
    }

    public void fillFrom(
            Start ast) {

        // the global name space includes all top-level names, excluding AST
        // names.

        ast.apply(new DepthFirstAdapter() {

            @Override
            public void outAGrammar(
                    AGrammar node) {

                Grammar grammar = new Grammar(node);
                add(grammar);
            }
        });
    }
}
