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

package org.sablecc.objectmacro.structure;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.syntax3.node.ADirective;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class External extends
        Param {

    private final AParam declaration;

    private final Map<String, Directive> directives = new HashMap<>();

    private final Set<Directive> allDirectives = new LinkedHashSet<>();

    External(
            AParam declaration,
            Macro macro,
            GlobalIndex globalIndex) {

        super(macro, globalIndex);

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        this.declaration = declaration;
    }

    public Directive newDirective(
            ADirective directive) {

        String optionName = directive.getName().getText();
        if (this.directives.containsKey(optionName)) {
            throw CompilerException.duplicateOption(directive,
                    this.directives.get(optionName).getDeclaration());
        }

        Directive newDirective = new Directive(directive, this);
        this.directives.put(optionName, newDirective);
        this.allDirectives.add(newDirective);

        return newDirective;
    }

    public Set<Directive> getAllDirectives() {

        return this.allDirectives;
    }

    @Override
    public TIdentifier getNameDeclaration() {

        return this.declaration.getName();
    }

    @Override
    public String getName() {

        return this.declaration.getName().getText();
    }

    public AParam getDeclaration() {

        return this.declaration;
    }
}
