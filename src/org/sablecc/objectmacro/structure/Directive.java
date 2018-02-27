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

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.syntax3.node.ADirective;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class Directive {

    private final Param parent;

    private final ADirective declaration;

    Directive(
            ADirective declaration,
            Param parent){

        if(parent == null){
            throw new InternalException("parent may not be null here");
        }

        if(declaration == null){
            throw new InternalException("declaration may not be null here");
        }

        this.declaration = declaration;
        this.parent = parent;

    }

    public ADirective getDeclaration(){
        return this.declaration;
    }

    public String getName(){
        return this.declaration.getName().getText();
    }
}
