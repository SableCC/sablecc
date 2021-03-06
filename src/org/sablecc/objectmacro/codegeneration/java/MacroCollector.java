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

import java.util.*;

import org.sablecc.objectmacro.codegeneration.java.macro.*;
import org.sablecc.objectmacro.codegeneration.java.structure.*;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.*;
import org.sablecc.objectmacro.intermediate.syntax3.node.*;

public class MacroCollector
        extends DepthFirstAdapter {

    private final Map<String, SMacro> macros;

    private List<String> currentParameters = new LinkedList<>();

    private List<String> currentInternals = new LinkedList<>();

    private final Macros factory;

    public MacroCollector(
            Map<String, SMacro> macros,
            Macros factory) {

        this.macros = macros;
        this.factory = factory;
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
        String parent_name;
        Set<String> applied_versions = new HashSet<>();

        if (node.getIsAllVersionned() != null || node.getIsAbstract() != null) {

            parent_name = "acro";
        }
        else {
            parent_name = GenerationUtils.buildNameCamelCase(node.getParent());
        }

        for (TString version : node.getVersions()) {
            applied_versions.add(GenerationUtils.string(version).toUpperCase());
        }

        MMacro macro = this.factory.newMacro();
        macro.addClassName(macro_name);
        macro.addParentClass(parent_name);

        this.macros.put(macro_name, new SMacro(macro, this.currentParameters,
                this.currentInternals, macro_name, applied_versions));
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
