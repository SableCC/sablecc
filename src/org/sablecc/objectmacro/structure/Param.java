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

import org.sablecc.exception.*;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.util.*;

public class Param {

    private final AParam declaration;

    private final Scope scope;

    private boolean isUsed;

    Param(
            AParam declaration,
            Scope scope) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (scope == null) {
            throw new InternalException("scope may not be null");
        }

        this.declaration = declaration;
        this.scope = scope;
    }

    public TIdentifier getNameDeclaration() {

        return this.declaration.getName();
    }

    public String getName() {

        return this.declaration.getName().getText();
    }

    public String getCamelCaseName() {

        return Utils.toCamelCase(this.declaration.getName());
    }

    public Scope getScope() {

        return this.scope;
    }

    public boolean isUsed() {

        return this.isUsed;
    }

    void setUsed() {

        this.isUsed = true;
    }
}
