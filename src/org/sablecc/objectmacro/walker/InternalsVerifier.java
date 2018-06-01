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

import java.util.*;

import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.syntax3.analysis.*;
import org.sablecc.objectmacro.syntax3.node.*;

public class InternalsVerifier
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private MacroInfo currentMacroInfo;

    private List<String> temporaryMacroReferences;

    public InternalsVerifier(
            GlobalIndex globalIndex) {

        this.globalIndex = globalIndex;
    }

    private List<String> getMacroReferences(
            Set<AMacroReference> macro_references) {

        this.temporaryMacroReferences = new ArrayList<>();

        for (AMacroReference macro_reference : macro_references) {
            macro_reference.apply(this);
        }

        List<String> result = this.temporaryMacroReferences;
        this.temporaryMacroReferences = null;
        return result;
    }

    @Override
    public void caseAMacro(
            AMacro node) {

        if (this.globalIndex.isAllVersionned(node.getName().getText())) {
            super.caseAMacro(node);
        }
        else if (this.currentMacroInfo != null) {
            for (PInternal internal : node.getInternals()) {
                internal.apply(this);
            }
        }
    }

    @Override
    public void caseAInternal(
            AInternal node) {

        Param expected_param = this.currentMacroInfo.getParam(node.getName());

        for (MacroVersion version : this.globalIndex.getAllVersions()) {
            MacroInfo macroInfo = this.globalIndex.getMacro(
                    this.currentMacroInfo.getNameDeclaration(), version);

            if (macroInfo != this.currentMacroInfo) {
                if (!macroInfo.containsParam(expected_param)) {
                    throw CompilerException.missingParameter(
                            macroInfo.getNameDeclaration(), version,
                            expected_param);
                }

                Param found_param = macroInfo.getParam(node.getName());
                if (expected_param.isString() && !found_param.isString()) {

                    throw CompilerException.incorrectParameterType(found_param,
                            macroInfo.getNameDeclaration(), version,
                            Collections.singletonList("String"));
                }
                else {
                    List<String> expected_types = getMacroReferences(
                            expected_param.getMacroReferences());
                    List<String> found_types = getMacroReferences(
                            found_param.getMacroReferences());

                    if (!found_types.containsAll(expected_types)) {
                        throw CompilerException.incorrectParameterType(
                                found_param, macroInfo.getNameDeclaration(),
                                version, expected_types);
                    }
                }
            }
        }
    }

    @Override
    public void caseAMacroReference(
            AMacroReference node) {

        if (!this.globalIndex.isAllVersionned(node.getName().getText())
                && this.temporaryMacroReferences == null) {

            for (MacroVersion version : this.globalIndex.getAllVersions()) {
                this.currentMacroInfo
                        = this.globalIndex.getMacro(node.getName(), version);
                this.currentMacroInfo.getDeclaration().apply(this);
            }

            this.currentMacroInfo = null;
        }

        if (this.temporaryMacroReferences != null) {
            this.temporaryMacroReferences.add(node.getName().getText());
        }

    }
}
