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

import java.io.File;

import org.sablecc.objectmacro.codegeneration.IntermediateRepresentation;
import org.sablecc.objectmacro.codegeneration.java.macro.*;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.AIntermediateRepresentation;

public class UtilsGenerationWalker extends
        DepthFirstAdapter {

    private final IntermediateRepresentation ir;

    private File packageDirectory;

    private final Macros factory;

    public UtilsGenerationWalker(
            IntermediateRepresentation ir,
            File packageDirectory,
            Macros factory) {

        this.ir = ir;
        this.packageDirectory = packageDirectory;
        this.factory = factory;
    }

    @Override
    public void caseAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        MContext mContext = this.factory.newContext();

        MExParameterNull mParameterNull = this.factory.newExParameterNull();
        MExIncorrectType mIncorrectType = this.factory.newExIncorrectType();
        MExObjectMacroErrorHead mObjectMacroErrorHead = this.factory.newExObjectMacroErrorHead();
        MExMacroNullInList mMacroNullInList = this.factory.newExMacroNullInList();
        MExCyclicReference mCyclicReference = this.factory.newExCyclicReference();
        MExCannotModify mCannotModify = this.factory.newExCannotModify();
        MExObjectMacroException mObjectMacroException = this.factory.newExObjectMacroException();
        MExVersionNull mExVersionNull = this.factory.newExVersionNull();
        MExVersionsDifferent mExVersionsDifferent = this.factory.newExVersionsDifferent();
        MExInternalException mExInternalException = this.factory.newExInternalException();
        MMacroInternalException mMacroInternalException = this.factory.newMacroInternalException();

        MClassInternalValue mClassInternalValue = this.factory.newClassInternalValue();
        MClassBuildState mClassBuildState = this.factory.newClassBuildState();
        MCycleDetectorClass mTarjanClass = this.factory.newCycleDetectorClass();

        MSuperDirective mSuperDirective = this.factory.newSuperDirective();
        MClassAfterLast mClassAfterLast = this.factory.newClassAfterLast();
        MClassBeforeFirst mClassBeforeFirst = this.factory.newClassBeforeFirst();
        MClassNone mClassNone = this.factory.newClassNone();
        MClassSeparator mClassSeparator = this.factory.newClassSeparator();

        if (!this.ir.getDestinationPackage().equals("")) {
            String destinationPackage = this.ir.getDestinationPackage();
            MPackageDeclaration mPackageDeclaration = this.factory.newPackageDeclaration(
                    destinationPackage);
            mContext.addPackageDeclaration(mPackageDeclaration);
            mIncorrectType.addPackageDeclaration(mPackageDeclaration);
            mParameterNull.addPackageDeclaration(mPackageDeclaration);
            mObjectMacroErrorHead.addPackageDeclaration(mPackageDeclaration);
            mMacroNullInList.addPackageDeclaration(mPackageDeclaration);
            mCyclicReference.addPackageDeclaration(mPackageDeclaration);
            mCannotModify.addPackageDeclaration(mPackageDeclaration);
            mObjectMacroException.addPackageDeclaration(mPackageDeclaration);
            mClassInternalValue.addPackageDeclaration(mPackageDeclaration);
            mSuperDirective.addPackageDeclaration(mPackageDeclaration);
            mClassAfterLast.addPackageDeclaration(mPackageDeclaration);
            mClassBeforeFirst.addPackageDeclaration(mPackageDeclaration);
            mClassNone.addPackageDeclaration(mPackageDeclaration);
            mClassSeparator.addPackageDeclaration(mPackageDeclaration);
            mClassBuildState.addPackageDeclaration(mPackageDeclaration);
            mTarjanClass.addPackageDeclaration(mPackageDeclaration);
            mExVersionNull.addPackageDeclaration(mPackageDeclaration);
            mExVersionsDifferent.addPackageDeclaration(mPackageDeclaration);
            mExInternalException.addPackageDeclaration(mPackageDeclaration);
            mMacroInternalException.addPackageDeclaration(mPackageDeclaration);
        }

        GenerationUtils.writeFile(this.packageDirectory, "Context.java",
                mContext.build());
        GenerationUtils.writeFile(this.packageDirectory, "MUserErrorIncorrectType.java",
                mIncorrectType.build());
        GenerationUtils.writeFile(this.packageDirectory, "MUserErrorParameterNull.java",
                mParameterNull.build());
        GenerationUtils.writeFile(this.packageDirectory,
                "MObjectMacroUserErrorHead.java", mObjectMacroErrorHead.build());
        GenerationUtils.writeFile(this.packageDirectory,
                "MUserErrorMacroNullInList.java", mMacroNullInList.build());
        GenerationUtils.writeFile(this.packageDirectory,
                "MUserErrorCyclicReference.java", mCyclicReference.build());
        GenerationUtils.writeFile(this.packageDirectory, "MUserErrorCannotModify.java",
                mCannotModify.build());
        GenerationUtils.writeFile(this.packageDirectory,
                "ObjectMacroException.java", mObjectMacroException.build());
        GenerationUtils.writeFile(this.packageDirectory, "InternalValue.java",
                mClassInternalValue.build());
        GenerationUtils.writeFile(this.packageDirectory, "Directive.java",
                mSuperDirective.build());
        GenerationUtils.writeFile(this.packageDirectory, "DAfterLast.java",
                mClassAfterLast.build());
        GenerationUtils.writeFile(this.packageDirectory, "DBeforeFirst.java",
                mClassBeforeFirst.build());
        GenerationUtils.writeFile(this.packageDirectory, "DNone.java",
                mClassNone.build());
        GenerationUtils.writeFile(this.packageDirectory, "DSeparator.java",
                mClassSeparator.build());

        GenerationUtils.writeFile(this.packageDirectory, "BuildState.java",
                mClassBuildState.build());
        GenerationUtils.writeFile(this.packageDirectory, "CycleDetector.java",
                mTarjanClass.build());

        GenerationUtils.writeFile(this.packageDirectory, "MUserErrorVersionNull.java",
                mExVersionNull.build());

        GenerationUtils.writeFile(this.packageDirectory, "MUserErrorVersionsDifferent.java",
                mExVersionsDifferent.build());

        GenerationUtils.writeFile(this.packageDirectory, "InternalException.java",
                mExInternalException.build());

        GenerationUtils.writeFile(this.packageDirectory, "MUserErrorInternalException.java",
                mMacroInternalException.build());
    }
}
