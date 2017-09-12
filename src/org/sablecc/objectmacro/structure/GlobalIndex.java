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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.util.*;

public class GlobalIndex {

    private final Set<Macro> allMacros = new LinkedHashSet<Macro>();

    private final SortedMap<String, Macro> macroMap = new TreeMap<String, Macro>();

    public Macro newMacro(
            PMacro pDeclaration) {

        if (pDeclaration == null) {
            throw new InternalException("pDeclaration may not be null");
        }

        AMacro declaration = (AMacro) pDeclaration;
        String name = declaration.getName().getText();

        TIdentifier duplicateDeclaration = declaration.getName();

        Macro firstMacro = getMacroOrNull(duplicateDeclaration);
        if (firstMacro != null) {
            TIdentifier firstDeclaration = firstMacro.getNameDeclaration();
            throw CompilerException.duplicateDeclaration(duplicateDeclaration,
                    firstDeclaration);
        }

        Macro macro = new Macro(this, declaration);

        this.allMacros.add(macro);
        this.macroMap.put(name, macro);

        return macro;
    }

    private Macro getMacroOrNull(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        String name = identifier.getText();

        return this.macroMap.get(name);
    }

    public Macro getMacro(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        Macro macro = getMacroOrNull(identifier);

        if (macro == null) {
            throw CompilerException.unknownMacro(identifier);
        }

        return macro;

    }

    public Macro getMacroByReference(
            AMacroReference macroReference){

        return this.getMacro(macroReference.getName());
    }

    public void checkReferencedMacro(
            Macro macro,
            Macro referencedMacro,
            Token error){

        if(referencedMacro == macro){

//            throw new CompilerException("Cannot self reference macro", error);
        }else if(referencedMacro.isUsing(macro)) {
            //TODO EXCEPTION
//            throw new CompilerException("Cyclic reference of macros", error);
        }
    }

    public Set<Macro> getAllMacros() {

        return this.allMacros;
    }

}
