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

import org.sablecc.objectmacro.codegeneration.java.structure.SMacro;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.AIntermediateRepresentation;
import org.sablecc.objectmacro.intermediate.syntax3.node.AMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.PMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.TString;

import java.util.Map;

class ChildrenCollector
        extends DepthFirstAdapter{

    private final Map<String, SMacro> macros;

    private SMacro currentMacro;

    ChildrenCollector(
            Map<String, SMacro> macros){

        this.macros = macros;
    }

    @Override
    public void caseAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        if(node.getVersions() != null){
            for(PMacro macro : node.getMacros()){
                macro.apply(this);

                for(PMacro child : node.getMacros()){
                    child.apply(this);
                }

                this.currentMacro = null;
            }
        }
    }

    @Override
    public void caseAMacro(
            AMacro node) {

        String macro_name = GenerationUtils.buildNameCamelCase(node.getNames());
        if(this.currentMacro == null){
            this.currentMacro = this.macros.get(macro_name);
        }

        if(node.getParent() != null){
            String parent = GenerationUtils.buildNameCamelCase(node.getParent());
            if(parent.equals(this.currentMacro.getName())){

                for(TString version : node.getVersions()){
                    String version_name = GenerationUtils.string(version).toUpperCase();
                    this.currentMacro.addChild(version_name, macro_name);
                }
            }
        }
    }
}
