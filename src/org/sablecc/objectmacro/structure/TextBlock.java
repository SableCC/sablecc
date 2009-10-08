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
import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.syntax3.node.*;

public class TextBlock
        extends Scope {

    private final ATextBlock declaration;

    private final Scope parent;

    private boolean reachable;

    TextBlock(
            GlobalIndex globalIndex,
            ATextBlock declaration,
            Scope parent) {

        super(globalIndex);

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        this.declaration = declaration;
        this.parent = parent;

        if (!declaration.getRepeatName().getText().equals(
                declaration.getName().getText())) {
            throw CompilerException.endMismatch(declaration.getRepeatName(),
                    declaration.getName());
        }
    }

    public ATextBlock getDeclaration() {

        return this.declaration;
    }

    @Override
    public TIdentifier getNameDeclaration() {

        return this.declaration.getName();
    }

    @Override
    public Scope getParent() {

        return this.parent;
    }

    public boolean isReachable() {

        return this.reachable;
    }

    public void setReachable() {

        this.reachable = true;
    }

}
