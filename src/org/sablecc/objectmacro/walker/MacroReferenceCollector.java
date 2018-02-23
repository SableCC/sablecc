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
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.node.*;

public class MacroReferenceCollector
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private Macro currentMacro;

    private Param currentParam;

    public MacroReferenceCollector(
            GlobalIndex globalIndex) {

        this.globalIndex = globalIndex;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.currentMacro = this.globalIndex.getMacro(node.getName());
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentMacro = null;
    }

    @Override
    public void caseAInsertMacroBodyPart(
            AInsertMacroBodyPart node) {

        //Call to verify if the macro exist
        AMacroReference macroReference = (AMacroReference) node.getMacroReference();
        Macro referenced_macro = this.globalIndex.getMacro(macroReference.getName());

        if(!referenced_macro.getAllParams().isEmpty()){
            throw CompilerException.invalidInsert(macroReference.getName());
        }

        //Delete currentParam reference to verify macro references in the children and avoiding adding duplicate macro references
        Param tempParam = this.currentParam;
        this.currentParam = null;

        node.getMacroReference().apply(this);

        this.currentParam = tempParam;
    }

    @Override
    public void caseAInsertStringPart(
            AInsertStringPart node) {

        //Call to verify if the macro exist
        AMacroReference macroReference = (AMacroReference) node.getMacro();
        Macro referenced_macro = this.globalIndex.getMacro(macroReference.getName());

        if(!referenced_macro.getAllParams().isEmpty()){
            throw CompilerException.invalidInsert(macroReference.getName());
        }

        //Delete currentParam reference to verify macro references in the children and avoiding adding duplicate macro references
        Param tempParam = this.currentParam;
        this.currentParam = null;

        node.getMacro().apply(this);

        this.currentParam = tempParam;
    }

    @Override
    public void inAMacroReference(
            AMacroReference node) {

        if(this.currentParam != null){
            this.currentParam.addMacroReference(node);
        }
    }

    @Override
    public void inAParam(
            AParam node) {

        this.currentParam = this.currentMacro
                .getParam(node.getName());
    }

    @Override
    public void outAParam(
            AParam node) {

        this.currentParam = null;
    }

    @Override
    public void caseAStringType(
            AStringType node) {

        this.currentMacro.setParamToString(
                this.currentParam.getNameDeclaration());
    }
}
