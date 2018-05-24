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

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.MacroInfo;
import org.sablecc.objectmacro.structure.MacroVersion;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.AInternal;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.AVarStaticValue;
import org.sablecc.objectmacro.syntax3.node.AVarStringPart;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;
import org.sablecc.objectmacro.util.Utils;

public class ParamReferenceCollector extends
        DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private final MacroVersion currentVersion;

    private MacroInfo currentMacroInfo;

    private Param currentParam;

    public ParamReferenceCollector(
            GlobalIndex globalIndex,
            MacroVersion version) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
        this.currentVersion = version;
    }

    @Override
    public void caseAMacro(AMacro node) {

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
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentMacroInfo = null;
    }

    @Override
    public void caseAVarStringPart(
            AVarStringPart node) {

        if (this.currentParam != null) {
            this.currentParam.addParamReference(new TIdentifier(
                    Utils.getVarName(node.getVariable()),
                    node.getVariable().getLine(), node.getVariable().getPos()));
        }
    }

    @Override
    public void caseAVarStaticValue(
            AVarStaticValue node) {

        if (this.currentParam != null) {
            this.currentParam.addParamReference(node.getIdentifier());
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
}
