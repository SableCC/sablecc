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
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Grammar
        implements NamedItem {

    private Start ast;

    private AGrammar declaration;

    // global name space : contains the grammar, expressions, groups, selectors,
    // investigators, contexts, and productions.
    private Map<String, NamedItem> globalNameMap = new LinkedHashMap<String, NamedItem>();

    public Grammar(
            Start ast) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        this.ast = ast;
        this.declaration = (AGrammar) this.ast.getPGrammar();

        initialize();
    }

    private void initialize() {

        // add grammar to global name space
        addGlobalName(this);

        throw new InternalException("not implemented");
    }

    public TIdentifier getName() {

        return this.declaration.getName();
    }

    private void addGlobalName(
            NamedItem namedItem) {

        String nameString = namedItem.getName().getText();

        NamedItem olderNamedItem = this.globalNameMap.get(nameString);
        if (olderNamedItem != null) {
            throw CompilerException.duplicateDeclaration(namedItem.getName(),
                    olderNamedItem.getName());
        }

        this.globalNameMap.put(nameString, namedItem);
    }
}
