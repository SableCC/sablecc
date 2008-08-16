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

package org.sablecc.objectmacro.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.sablecc.objectmacro.bootstrap.syntax3.node.AMacro;
import org.sablecc.objectmacro.bootstrap.syntax3.node.TIdentifier;

public class File
        implements MacroParent {

    private final static File instance = new File();

    private final Map<String, AMacro> macros = new HashMap<String, AMacro>();

    private File() {

    }

    public static File getFile() {

        return instance;
    }

    public void addMacro(
            AMacro macro) {

        if (macro == null) {
            throw new InternalException();
        }
        TIdentifier identifier = macro.getName();
        String name = identifier.getText();
        AMacro otherMacro = this.macros.get(name);
        if (otherMacro != null) {
            TIdentifier otherIdentifier = otherMacro.getName();
            throw new SemanticException("macro name \"" + name
                    + "\" is already used at (line:"
                    + otherIdentifier.getLine() + ",pos:"
                    + otherIdentifier.getPos() + ")", identifier);
        }
        this.macros.put(name, macro);
    }
}
