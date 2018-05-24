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

import java.util.HashSet;
import java.util.Set;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.structure.MacroInfo;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroReference;
import org.sablecc.objectmacro.syntax3.node.AStringStaticValue;
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AVarStaticValue;
import org.sablecc.objectmacro.syntax3.node.AVarStringPart;
import org.sablecc.objectmacro.syntax3.node.PStringPart;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;
import org.sablecc.objectmacro.util.Utils;

public class VarVerifier extends
        DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private final MacroVersion currentVersion;

    private MacroInfo currentMacroInfo;

    private Internal internalsList[];

    private Integer currentIndex = 0;

    public VarVerifier(
            GlobalIndex globalIndex,
            MacroVersion version) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
        this.currentVersion = version;
    }

    @Override
    public void caseAMacro(
            AMacro node) {

        //Looking if this macro contains the current version
        if(this.currentVersion != null
                && node.getVersions().size() > 0
                && !Utils.containsVersion(node.getVersions(), this.currentVersion)){
            return;
        }

        super.caseAMacro(node);
    }


    @Override
    public void inAMacro(
            AMacro node) {

        this.currentMacroInfo = this.globalIndex.getMacro(node.getName(), this.currentVersion);
        if(this.currentMacroInfo == null){
            throw CompilerException.unknownMacro(node.getName());
        }
    }

    @Override
    public void inAMacroReference(
            AMacroReference node) {

        MacroInfo referencedMacro;
        int nbArguments = node.getValues().size();

        if(this.currentMacroInfo.getDeclaration().getVersions().size() == 0){
            for(MacroVersion version : this.globalIndex.getAllVersions()){

                referencedMacro = this.globalIndex.getMacro(node.getName(), version);
                if(nbArguments != referencedMacro.getAllInternals().size()){
                    throw CompilerException.incorrectArgumentCount(node, referencedMacro);
                }
            }
        }

        referencedMacro = this.globalIndex.getMacro(node.getName(), this.currentVersion);
        int nbInternals = referencedMacro.getAllInternals().size();
        if(nbArguments != nbInternals){
            throw CompilerException.incorrectArgumentCount(node, referencedMacro);
        }

        this.internalsList = new Internal[nbInternals];
        referencedMacro.getAllInternals().toArray(this.internalsList);
        this.currentIndex = 0;
    }

    @Override
    public void caseAStringStaticValue(
            AStringStaticValue node) {

        Param currentParam = this.internalsList[this.currentIndex++];

        // The internal corresponding to currentIndex must be of type String
        if (!currentParam.isString()) {
            throw CompilerException.incorrectArgumentType("MacroInfo", "String",
                    node.getLDquote().getLine(), node.getLDquote().getPos());
        }

        // Apply to each part in case of recursive insert
        Integer tempIndex = this.currentIndex;
        Internal tempInternals[] = this.internalsList;

        for (PStringPart value : node.getParts()) {
            value.apply(this);
        }

        this.currentIndex = tempIndex;
        this.internalsList = tempInternals;
    }

    @Override
    public void caseAVarStaticValue(
            AVarStaticValue node) {

        Param expectedParam = this.internalsList[this.currentIndex++];
        Param providedParam = this.currentMacroInfo.getParam(node.getIdentifier());
        Set<String> expectedMacrosType = new HashSet<>();
        Set<String> providedMacrosType = new HashSet<>();

        if (expectedParam.isString() && !providedParam.isString()) {

            throw CompilerException.incorrectArgumentType("String", "MacroInfo",
                    node.getIdentifier().getLine(),
                    node.getIdentifier().getPos());
        }

        for (AMacroReference macroReference : expectedParam
                .getMacroReferences()) {
            expectedMacrosType.add(macroReference.getName().getText());
        }

        for (AMacroReference macroReference : providedParam
                .getMacroReferences()) {
            providedMacrosType.add(macroReference.getName().getText());
        }

        if (!expectedMacrosType.containsAll(providedMacrosType)) {
            throw CompilerException.incorrectMacroType(expectedMacrosType,
                    providedMacrosType, this.currentIndex,
                    node.getIdentifier());
        }

        this.currentMacroInfo.setParamUsed(node.getIdentifier());
    }

    @Override
    public void caseAVarMacroBodyPart(
            AVarMacroBodyPart node) {

        this.currentMacroInfo.setParamUsed(new TIdentifier(
                Utils.getVarName(node.getVariable()),
                node.getVariable().getLine(), node.getVariable().getPos()));
    }

    @Override
    public void caseAVarStringPart(
            AVarStringPart node) {

        this.currentMacroInfo.setParamUsed(new TIdentifier(
                Utils.getVarName(node.getVariable()),
                node.getVariable().getLine(), node.getVariable().getPos()));
    }
}
