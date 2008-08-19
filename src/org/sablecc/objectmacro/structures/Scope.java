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

package org.sablecc.objectmacro.structures;

import java.util.HashMap;
import java.util.Map;

import org.sablecc.objectmacro.exception.SemanticException;
import org.sablecc.sablecc.exception.InternalException;

public abstract class Scope {

    private final Scope parentScope;

    private final Map<String, Macro> macroMap = new HashMap<String, Macro>();

    private final Map<String, Param> paramMap = new HashMap<String, Param>();

    protected Scope(
            final Scope parentScope) {

        this.parentScope = parentScope;
    }

    public Macro getMacro(
            String macroName) {

        if (macroName == null) {
            throw new InternalException("macroName may not be null");
        }

        Macro macro = this.macroMap.get(macroName);

        if (macro == null) {
            if (this.parentScope != null) {
                return this.parentScope.getMacro(macroName);
            }
        }

        return macro;
    }

    public void addMacro(
            Macro macro)
            throws SemanticException {

        if (macro == null) {
            throw new InternalException("macro may not be null");
        }

        String macroName = macro.getName();

        if (getMacro(macroName) != null) {
            throw new SemanticException("duplicate definition of macro "
                    + macroName, macro.getDefinition().getName());
        }

        this.macroMap.put(macro.getName(), macro);
    }

    public Param getParam(
            String paramName) {

        if (paramName == null) {
            throw new InternalException("paramName may not be null");
        }

        Param param = this.paramMap.get(paramName);

        if (param == null) {
            if (this.parentScope != null) {
                return this.parentScope.getParam(paramName);
            }
        }

        return param;
    }

    public void addParam(
            Param param)
            throws SemanticException {

        if (param == null) {
            throw new InternalException("param may not be null");
        }

        String paramName = param.getName();

        if (getParam(paramName) != null) {
            throw new SemanticException("duplicate definition of parameter "
                    + paramName, param.getDefinition().getName());
        }

        this.paramMap.put(param.getName(), param);
    }

    public Scope getParentScope() {

        return this.parentScope;
    }
}
