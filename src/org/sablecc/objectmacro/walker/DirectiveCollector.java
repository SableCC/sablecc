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
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.structure.External;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.structure.MacroVersion;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.ADirective;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.util.Utils;

public class DirectiveCollector extends
        DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private final MacroVersion currentVersion;

    private Macro currentMacro;

    private External currentParam;

    public DirectiveCollector(
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

        //if currentMacro is not of version 'currentVersion' then go to next macro node
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

        this.currentMacro = this.globalIndex.getMacro(node.getName(), this.currentVersion);
        if(this.currentMacro == null){
            throw CompilerException.unknownMacro(node.getName());
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentMacro = null;
    }

    @Override
    public void inAParam(
            AParam node) {

        this.currentParam = (External) this.currentMacro
                .getParam(node.getName());
    }

    @Override
    public void outAParam(
            AParam node) {

        this.currentParam = null;
    }

    @Override
    public void inADirective(
            ADirective node) {

        String directive_name = node.getName().getText();
        if (!directive_name.equals("separator")
                && !directive_name.equals("after_last")
                && !directive_name.equals("before_first")
                && !directive_name.equals("none")) {

            throw CompilerException.unknownOption(node);
        }

        this.currentParam.newDirective(node);
    }
}
