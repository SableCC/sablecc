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
import org.sablecc.objectmacro.util.Utils;

public class VarVerifier
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private Macro currentMacro;

    private Param currentParam;

    private Param paramsList[];

    private Integer currentIndex = 0;

    public VarVerifier(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.currentMacro = this.globalIndex.getMacro(node.getName());
    }

    @Override
    public void inAParam(
            AParam node) {

        this.currentParam = this.currentMacro.getParam(node.getName());
    }

    @Override
    public void outAParam(
            AParam node) {

        this.currentParam = null;
    }

    @Override
    public void inAMacroReference(
            AMacroReference node) {

        Macro referencedMacro = this.globalIndex.getMacro(node.getName());

        this.paramsList = new Param[referencedMacro.getAllInternals().size()];
        referencedMacro.getAllInternals().toArray(this.paramsList);
        this.currentIndex = 0;
    }

    @Override
    public void caseAStringStaticValue(
            AStringStaticValue node) {

        Param param = this.paramsList[this.currentIndex++];
        AMacroReference macroReference = (AMacroReference) node.parent();
        if(!param.isString()){

            throw CompilerException.incorrectArgumentType("Macro", "String",
                    macroReference.getName().getLine(), macroReference.getName().getPos());
        }

        //Verify type of args if there is an insert
        Integer tempIndex = this.currentIndex;
        Param tempParams[] = this.paramsList;

        for(PStringPart value : node.getParts()){
            value.apply(this);
        }

        this.currentIndex = tempIndex;
        this.paramsList = tempParams;
    }

    @Override
    public void caseAVarStaticValue(
            AVarStaticValue node) {

        Param param = this.paramsList[this.currentIndex++];
        if(param.isString()){

            throw CompilerException.incorrectArgumentType(
                    "String", "Macro",
                    node.getIdentifier().getLine(), node.getIdentifier().getPos());
        }

        this.currentMacro.setParamUsed(node.getIdentifier());
    }

    @Override
    public void caseAVarMacroBodyPart(
            AVarMacroBodyPart node) {

        this.currentMacro.setParamUsed(
                new TIdentifier(
                        Utils.getVarName(
                                node.getVariable())));

    }
}
