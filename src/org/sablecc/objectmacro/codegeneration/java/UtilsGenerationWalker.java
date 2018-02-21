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

import org.sablecc.objectmacro.codegeneration.IntermediateRepresentation;
import org.sablecc.objectmacro.codegeneration.java.macro.*;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.*;

import java.io.File;

public class UtilsGenerationWalker
        extends DepthFirstAdapter {

    private final IntermediateRepresentation ir;

    private File packageDirectory;

    public UtilsGenerationWalker(
            IntermediateRepresentation ir,
            File packageDirectory) {

        this.ir = ir;
        this.packageDirectory = packageDirectory;
    }

    @Override
    public void caseAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        MContext mContext = new MContext();

        MParameterNull mParameterNull = new MParameterNull();
        MIncorrectType mIncorrectType = new MIncorrectType();
        MObjectMacroErrorHead mObjectMacroErrorHead = new MObjectMacroErrorHead();
        MMacroNullInList mMacroNullInList = new MMacroNullInList();
        MCyclicReference mCyclicReference = new MCyclicReference();
        MCannotModify mCannotModify = new MCannotModify();
        MObjectMacroException mObjectMacroException = new MObjectMacroException();
        MClassInternalValue mClassInternalValue = new MClassInternalValue();
        mClassInternalValue.newImportJavaUtil();
        MClassBuildState mClassBuildState = new MClassBuildState();

        if(!this.ir.getDestinationPackage().equals("")){
            String destinationPackage = this.ir.getDestinationPackage();
            mContext.newPackageDeclaration(destinationPackage);
            mIncorrectType.newPackageDeclaration(destinationPackage);
            mParameterNull.newPackageDeclaration(destinationPackage);
            mObjectMacroErrorHead.newPackageDeclaration(destinationPackage);
            mMacroNullInList.newPackageDeclaration(destinationPackage);
            mCyclicReference.newPackageDeclaration(destinationPackage);
            mCannotModify.newPackageDeclaration(destinationPackage);
            mObjectMacroException.newPackageDeclaration(destinationPackage);
            mClassInternalValue.newPackageDeclaration(destinationPackage);
            mClassBuildState.newPackageDeclaration(destinationPackage);
        }

        GenerationUtils.writeFile(this.packageDirectory,"Context.java", mContext.toString());
        GenerationUtils.writeFile(this.packageDirectory,"MIncorrectType.java", mIncorrectType.toString());
        GenerationUtils.writeFile(this.packageDirectory,"MParameterNull.java", mParameterNull.toString());
        GenerationUtils
                .writeFile(this.packageDirectory,"MObjectMacroErrorHead.java", mObjectMacroErrorHead.toString());
        GenerationUtils.writeFile(this.packageDirectory,"MMacroNullInList.java", mMacroNullInList.toString());
        GenerationUtils.writeFile(this.packageDirectory,"MCyclicReference.java", mCyclicReference.toString());
        GenerationUtils.writeFile(this.packageDirectory,"MCannotModify.java", mCannotModify.toString());
        GenerationUtils
                .writeFile(this.packageDirectory,"ObjectMacroException.java", mObjectMacroException.toString());
        GenerationUtils.writeFile(this.packageDirectory, "InternalValue.java", mClassInternalValue.toString());

        GenerationUtils.writeFile(this.packageDirectory, "BuildState.java", mClassBuildState.toString());
    }
}
