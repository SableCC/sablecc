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

import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroHead;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;
import org.sablecc.objectmacro.syntax3.node.TVar;

public class Macro
        implements MacroParent {

    private static final Map<AMacro, Macro> macroMap = new HashMap<AMacro, Macro>();

    private final AMacro declaration;

    private final Macro parent;

    private TIdentifier name;

    private final Map<String, TIdentifier> parameters = new HashMap<String, TIdentifier>();

    private final Map<String, AMacro> macros = new HashMap<String, AMacro>();

    public Macro(
            final AMacro declaration,
            Macro parent) {

        if (declaration == null) {
            throw new InternalException();
        }
        this.declaration = declaration;
        if (macroMap.containsKey(declaration)) {
            throw new InternalException();
        }
        macroMap.put(declaration, this);

        this.parent = parent;
    }

    public static Macro getMacro(
            AMacro declaration) {

        if (declaration == null) {
            throw new InternalException();
        }
        Macro result = macroMap.get(declaration);
        if (result == null) {
            throw new InternalException();
        }
        return result;
    }

    public void addParameter(
            TIdentifier parameter) {

        if (parameter == null) {
            throw new InternalException();
        }
        String name = parameter.getText();
        TIdentifier otherParameter = this.parameters.get(name);
        if (otherParameter != null) {
            throw new SemanticException("parameter name \"" + name
                    + "\" is already used at (line:" + otherParameter.getLine()
                    + ",pos:" + otherParameter.getPos() + ")", parameter);
        }
        this.parameters.put(name, parameter);
    }

    private String getName(
            TVar var) {

        String text = var.getText();
        if (text.charAt(1) != '(') {
            return text.substring(1);
        }
        int length = text.length();
        return text.substring(2, length);
    }

    public void checkVar(
            TVar var) {

        if (var == null) {
            throw new InternalException();
        }
        TIdentifier result = this.parameters.get(getName(var));
        if (result == null) {
            if (this.parent != null) {
                this.parent.checkVar(var);
            }
            else {
                throw new SemanticException("unknown parameter \""
                        + getName(var) + "\"", var);
            }
        }
    }

    public void addMacro(
            AMacro macro) {

        if (macro == null) {
            throw new InternalException();
        }
        TIdentifier identifier = ((AMacroHead) macro.getMacroHead()).getName();
        String name = identifier.getText();
        AMacro otherMacro = this.macros.get(name);
        if (otherMacro != null) {
            TIdentifier otherIdentifier = ((AMacroHead) macro.getMacroHead())
                    .getName();
            throw new SemanticException("macro name \"" + name
                    + "\" is already used at (line:"
                    + otherIdentifier.getLine() + ",pos:"
                    + otherIdentifier.getPos() + ")", identifier);
        }
        this.macros.put(name, macro);
    }

    public AMacro getDeclaration() {

        return this.declaration;
    }

    public void setName(
            TIdentifier name) {

        if (name == null) {
            throw new InternalException();
        }
        if (this.name != null) {
            throw new InternalException();
        }
        this.name = name;
    }

    public TIdentifier getName() {

        if (this.name == null) {
            throw new InternalException();
        }
        return this.name;
    }
}
