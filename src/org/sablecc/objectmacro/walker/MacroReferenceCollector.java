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

import java.util.ArrayList;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.MacroInfo;
import org.sablecc.objectmacro.structure.MacroVersion;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AIdentifiersInternalType;
import org.sablecc.objectmacro.syntax3.node.AInsertMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AInsertStringPart;
import org.sablecc.objectmacro.syntax3.node.AInternal;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroReference;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.AStringInternalType;
import org.sablecc.objectmacro.syntax3.node.AStringType;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;
import org.sablecc.objectmacro.util.Utils;

public class MacroReferenceCollector extends
        DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private final MacroVersion currentVersion;

    private MacroInfo currentMacroInfo;

    private Param currentParam;

    public MacroReferenceCollector(
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
    public void outAMacro(
            AMacro node) {

        this.currentMacroInfo = null;
    }

    @Override
    public void caseAInsertMacroBodyPart(
            AInsertMacroBodyPart node) {

        // getAnyMacro verify if macro exists
        AMacroReference macroReference = (AMacroReference) node
                .getMacroReference();
        MacroInfo referenced_macro = this.globalIndex
                .getMacro(macroReference.getName(), this.currentVersion);

        if (!referenced_macro.getAllParams().isEmpty()) {
            throw CompilerException.invalidInsert(macroReference.getName());
        }

        // Delete currentParam reference to verify macro references in the
        // children and avoiding adding duplicate macro references
        Param tempParam = this.currentParam;
        this.currentParam = null;

        node.getMacroReference().apply(this);

        this.currentParam = tempParam;
    }

    @Override
    public void caseAInsertStringPart(
            AInsertStringPart node) {

        // getAnyMacro verify if macro exists
        AMacroReference macroReference = (AMacroReference) node.getMacro();
        MacroInfo referenced_macro = this.globalIndex
                .getMacro(macroReference.getName(), this.currentVersion);

        if (!referenced_macro.getAllParams().isEmpty()) {
            throw CompilerException.invalidInsert(macroReference.getName());
        }

        // Delete currentParam reference to verify macro references in the
        // children and avoiding adding duplicate macro references
        Param tempParam = this.currentParam;
        this.currentParam = null;

        node.getMacro().apply(this);

        this.currentParam = tempParam;
    }

    @Override
    public void inAMacroReference(
            AMacroReference node) {

        if (this.currentParam != null) {
            this.currentParam.addMacroReference(node);
        }
    }

    @Override
    public void caseTIdentifier(
            TIdentifier node) {

        if (node.parent() instanceof AIdentifiersInternalType) {
            AMacroReference aMacroReference = new AMacroReference(node,
                    new ArrayList<>());
            this.currentParam.addMacroReference(aMacroReference);
        }
    }

    @Override
    public void inAParam(
            AParam node) {

        this.currentParam = this.currentMacroInfo.getParam(node.getName());
    }

    @Override
    public void outAParam(
            AParam node) {

        this.currentParam = null;
    }

    @Override
    public void inAInternal(
            AInternal node) {

        this.currentParam = this.currentMacroInfo.getParam(node.getName());
    }

    @Override
    public void outAInternal(
            AInternal node) {

        this.currentParam = null;
    }

    @Override
    public void caseAStringType(
            AStringType node) {

        this.currentMacroInfo
                .setParamToString(this.currentParam.getNameDeclaration());
    }

    @Override
    public void caseAStringInternalType(
            AStringInternalType node) {

        this.currentMacroInfo
                .setParamToString(this.currentParam.getNameDeclaration());
    }
}
