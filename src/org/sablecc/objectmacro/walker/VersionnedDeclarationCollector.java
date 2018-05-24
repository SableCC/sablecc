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

import org.sablecc.exception.*;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.syntax3.analysis.*;
import org.sablecc.objectmacro.syntax3.node.*;

import java.util.LinkedList;
import java.util.List;

public class VersionnedDeclarationCollector
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    public VersionnedDeclarationCollector(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    @Override
    public void caseAMacro(
            AMacro node) {

        if (node.getBody().getPos() != 1) {
            throw CompilerException.bodyTokenMisused(node.getBody());
        }

        List<MacroVersion> versions = new LinkedList<>();
        Macro newMacro = this.globalIndex.createMacro(node);
        boolean isAllVersionned = false;

        if(node.getVersions().size() > 0){
            for(TIdentifier version_name : node.getVersions()){
                versions.add(this.globalIndex.getVersion(version_name));
            }

            isAllVersionned = node.getVersions().size() == this.globalIndex.getAllVersions().size();
        }
        else{
            isAllVersionned = true;
            versions.addAll(this.globalIndex.getAllVersions());
        }

        for(MacroVersion version : versions){
            newMacro = this.globalIndex.createMacro(node);
            version.newMacro(newMacro);

            List<PParam> params = newMacro.getDeclaration().getParams();
            List<PInternal> internals = newMacro.getDeclaration().getInternals();

            for (PParam param_production : params) {
                AParam param_node = (AParam) param_production;
                newMacro.newParam(param_node);
            }

            for (PInternal param_production : internals) {
                AInternal param_node = (AInternal) param_production;
                newMacro.newInternal(param_node);
            }
        }

        if(isAllVersionned){
            this.globalIndex.addAllVersionnedMacro(newMacro);
        }

        for(PMacroBodyPart pMacroBodyPart : node.getMacroBodyParts()){
            pMacroBodyPart.apply(this);
        }
    }

    @Override
    public void caseAIndentMacroBodyPart(
            AIndentMacroBodyPart node) {

        if(node.getCommand().getPos() != 1){
            throw CompilerException.indentTokenMisused(node.getCommand());
        }
    }
}
