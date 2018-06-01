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

package org.sablecc.objectmacro.walker;

import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.MacroInfo;
import org.sablecc.objectmacro.structure.MacroVersion;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.*;

import java.util.*;

public class ParametersVerifier
        extends DepthFirstAdapter{

    private final GlobalIndex globalIndex;

    private MacroInfo currentMacroInfo;

    private List<String> temporaryMacroReferences = new ArrayList<>();

    public ParametersVerifier(
            GlobalIndex globalIndex){

        this.globalIndex = globalIndex;
    }

    private List<String> getMacroReferences(
            Set<AMacroReference> macro_references){

        this.temporaryMacroReferences = new ArrayList<>();

        for(AMacroReference macro_reference : macro_references){
            macro_reference.apply(this);
        }

        List<String> result = this.temporaryMacroReferences;
        this.temporaryMacroReferences = null;
        return result;
    }

    @Override
    public void caseAMacro(
            AMacro node) {

        if(node.getVersions().size() > 0
                && !this.globalIndex.isAllVersionned(node.getName().getText())){

            Iterator<TIdentifier> iterator = node.getVersions().iterator();
            TIdentifier version_identifier = iterator.next();
            MacroVersion version = this.globalIndex.getVersion(version_identifier);
            this.currentMacroInfo = this.globalIndex.getMacro(node.getName(), version);

            for(PParam param : node.getParams()){
                param.apply(this);
            }
        }
    }

    @Override
    public void caseAParam(
            AParam node) {

        Param expected_param = this.currentMacroInfo.getParam(node.getName());

        for(MacroVersion version : this.globalIndex.getAllVersions()){
            MacroInfo versionned_macro = version.getMacroOrNull(this.currentMacroInfo
                    .getNameDeclaration());

            if(this.currentMacroInfo != versionned_macro){
                if(!versionned_macro.containsParam(expected_param)){
                    throw CompilerException.missingParameter(versionned_macro.getNameDeclaration(), version, expected_param);
                }

                Param found_param = versionned_macro.getParam(node.getName());
                if(expected_param.isString()
                        && !found_param.isString()) {

                    throw CompilerException.incorrectParameterType(found_param, versionned_macro.getNameDeclaration(), version, Collections.singletonList("String"));
                }
                else {
                    List<String> expected_types = getMacroReferences(expected_param.getMacroReferences());
                    List<String> found_types = getMacroReferences(found_param.getMacroReferences());

                    if(!found_types.containsAll(expected_types)){
                        throw CompilerException.incorrectParameterType(found_param, versionned_macro.getNameDeclaration(), version, expected_types);
                    }
                }
            }
        }
    }

    @Override
    public void caseAMacroReference(
            AMacroReference node) {

        this.temporaryMacroReferences.add(node.getName().getText());
    }
}
