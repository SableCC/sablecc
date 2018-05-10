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

package org.sablecc.objectmacro.codegeneration.java;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sablecc.objectmacro.codegeneration.java.macro.MMacro;
import org.sablecc.objectmacro.codegeneration.java.structure.Macro;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.AInternal;
import org.sablecc.objectmacro.intermediate.syntax3.node.AMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.AParam;

public class MacroCollector
        extends
        DepthFirstAdapter {

    private final Map<String, Macro> macros;

    private List<String> currentParameters = new LinkedList<>();

    private List<String> currentInternals = new LinkedList<>();

    public MacroCollector(
            Map<String, Macro> macros) {

        this.macros = macros;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.currentParameters = new LinkedList<>();
        this.currentInternals = new LinkedList<>();
    }

    @Override
    public void outAMacro(
            AMacro node) {

        String macro_name = GenerationUtils.buildNameCamelCase(node.getNames());
        this.macros.put(macro_name, new Macro(new MMacro(macro_name),
                this.currentParameters, this.currentInternals, macro_name));

    }

    @Override
    public void caseAParam(
            AParam node) {

        String param_name = GenerationUtils.buildNameCamelCase(node.getNames());
        this.currentParameters.add(param_name);
    }

    @Override
    public void caseAInternal(
            AInternal node) {

        String param_name = GenerationUtils.buildNameCamelCase(node.getNames());
        this.currentInternals.add(param_name);
    }

}
