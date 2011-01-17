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

import org.sablecc.exception.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Grammar
        implements Named {

    private AGrammar declaration;

    private NameSpace nameSpace;

    public Grammar(
            Start ast) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        initializeFrom(ast);
    }

    public TIdentifier getNameIdentifier() {

        return this.declaration.getName();
    }

    public String getName() {

        return getNameIdentifier().getText();
    }

    public String getNameType() {

        return "grammar";
    }

    private void initializeFrom(
            Start ast) {

        this.nameSpace = new NameSpace();

        // the global name space includes all top-level names, excluding AST
        // names.

        ast.apply(new DepthFirstAdapter() {

            private final Grammar grammar = Grammar.this;

            private final NameSpace nameSpace = this.grammar.nameSpace;

            @Override
            public void outAGrammar(
                    AGrammar node) {

                this.grammar.declaration = node;
                this.nameSpace.add(this.grammar);
            }
        });

        throw new InternalException("not implemented");
    }

    private static class NameSpace {

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
    }

}
