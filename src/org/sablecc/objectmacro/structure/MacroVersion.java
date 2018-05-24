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
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.syntax3.node.*;

import java.util.*;

public class MacroVersion {

    private TIdentifier name;

    private final Map<String, Macro> macros = new LinkedHashMap<>();

    public MacroVersion(
            TIdentifier name){

        if(name == null){
            throw new InternalException("name may not be null");
        }
        this.name = name;
    }

    public TIdentifier getName() {
        return name;
    }

    public void newMacro(
            Macro macro){

        if(macro == null){
            throw new InternalException("macro may not be null");
        }

        Macro firstDeclaration = this.macros.get(macro.getName());
        if(firstDeclaration != null){
            throw CompilerException.duplicateDeclaration(
                    macro.getNameDeclaration(), firstDeclaration.getNameDeclaration());
        }

        this.macros.put(macro.getName(), macro);
        macro.addVersion(this);
    }

    Macro getMacro(
            TIdentifier macro_name){

        if(macro_name == null){
            throw new InternalException("macro_name may not be null");
        }

        Macro found = this.macros.get(macro_name.getText());
        if(found == null){
            throw new InternalException("macro should be initialized before");
        }

        return found;
    }

    public Macro getMacroOrNull(
            TIdentifier macro_name){

        return this.macros.get(macro_name.getText());
    }

    public Collection<Macro> getMacros() {

        return macros.values();
    }
}
