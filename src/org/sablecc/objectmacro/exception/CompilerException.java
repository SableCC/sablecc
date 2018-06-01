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

package org.sablecc.objectmacro.exception;

import java.util.*;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.errormessage.*;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.syntax3.node.*;

@SuppressWarnings("serial")
public class CompilerException
        extends RuntimeException {

    private static final Macros factory = new Macros();

    private CompilerException(
            String message) {

        super(message);

        if (message == null) {
            throw new InternalException("message may not be null");
        }
    }

    private CompilerException(
            String message,
            Throwable cause) {

        super(message, cause);

        if (message == null) {
            throw new InternalException("message may not be null");
        }

        if (cause == null) {
            throw new InternalException("cause may not be null");
        }
    }

    public static CompilerException invalidArgument(
            String argumentText,
            Throwable cause) {

        MInvalidArgument invalidArgument = factory.newInvalidArgument();
        invalidArgument.addArgumentText(argumentText);

        return new CompilerException(invalidArgument.build(), cause);
    }

    public static CompilerException missingLongOptionOperand(
            String optionName,
            String operandName) {

        MMissingLongOptionOperand missingLongOptionOperand
                = factory.newMissingLongOptionOperand();
        missingLongOptionOperand.addOptionName(optionName);
        missingLongOptionOperand.addOptionName(operandName);

        return new CompilerException(missingLongOptionOperand.build());
    }

    public static CompilerException missingShortOptionOperand(
            String optionName,
            String operandName) {

        MMissingShortOptionOperand missingShortOptionOperand
                = factory.newMissingShortOptionOperand();
        missingShortOptionOperand.addOperandName(operandName);
        missingShortOptionOperand.addOptionName(optionName);

        return new CompilerException(missingShortOptionOperand.build());
    }

    public static CompilerException invalidLongOption(
            String optionName) {

        MInvalidLongOption mInvalidLongOption = factory.newInvalidLongOption();
        mInvalidLongOption.addOptionName(optionName);

        return new CompilerException(mInvalidLongOption.build());
    }

    public static CompilerException spuriousLongOptionOperand(
            String optionName,
            String operandText) {

        MSpuriousLongOptionOperand spuriousLongOptionOperand
                = factory.newSpuriousLongOptionOperand();
        spuriousLongOptionOperand.addOperandText(operandText);
        spuriousLongOptionOperand.addOptionName(optionName);

        return new CompilerException(spuriousLongOptionOperand.build());
    }

    public static CompilerException invalidShortOption(
            String optionName) {

        MInvalidShortOption invalidShortOption
                = factory.newInvalidShortOption();
        invalidShortOption.addOptionName(optionName);

        return new CompilerException(invalidShortOption.build());
    }

    public static CompilerException spuriousShortOptionOperand(
            String optionName,
            String operandText) {

        MSpuriousShortOptionOperand mSpuriousShortOptionOperand
                = factory.newSpuriousShortOptionOperand();
        mSpuriousShortOptionOperand.addOperandText(operandText);
        mSpuriousShortOptionOperand.addOptionName(optionName);

        return new CompilerException(mSpuriousShortOptionOperand.build());
    }

    public static CompilerException unknownTarget(
            String targetLanguage) {

        MUnknownTarget mUnknownTarget = factory.newUnknownTarget();
        mUnknownTarget.addTarget(targetLanguage);

        return new CompilerException(mUnknownTarget.build());
    }

    public static CompilerException invalidArgumentCount() {

        return new CompilerException(factory.newInvalidArgumentCount().build());
    }

    public static CompilerException invalidObjectmacroSuffix(
            String fileName) {

        MInvalidObjectmacroSuffix mInvalidObjectmacroSuffix
                = factory.newInvalidObjectmacroSuffix();
        mInvalidObjectmacroSuffix.addFileName(fileName);

        return new CompilerException(mInvalidObjectmacroSuffix.build());
    }

    public static CompilerException invalidIntermediateSuffix(
            String fileName) {

        MInvalidIntermediateSuffix mInvalidIntermediateSuffix
                = factory.newInvalidIntermediateSuffix();
        mInvalidIntermediateSuffix.addFileName(fileName);

        return new CompilerException(mInvalidIntermediateSuffix.build());
    }

    public static CompilerException missingMacroFile(
            String fileName) {

        MMissingMacroFile missingMacroFile = factory.newMissingMacroFile();
        missingMacroFile.addFileName(fileName);

        return new CompilerException(missingMacroFile.build());
    }

    public static CompilerException macroNotFile(
            String fileName) {

        MMacroNotFile mMacroNotFile = factory.newMacroNotFile();
        mMacroNotFile.addFileName(fileName);

        return new CompilerException(mMacroNotFile.build());
    }

    public static CompilerException inputError(
            String fileName,
            Throwable cause) {

        MInputError mInputError = factory.newInputError();
        mInputError.addFileName(fileName);
        mInputError.addMessage(cause.getMessage());

        return new CompilerException(mInputError.build(), cause);
    }

    public static CompilerException outputError(
            String fileName,
            Throwable cause) {

        MOutputError mOutputError = factory.newOutputError();
        mOutputError.addFileName(fileName);
        mOutputError.addMessage(cause.getMessage());

        return new CompilerException(mOutputError.build(), cause);
    }

    public static CompilerException unknownMacro(
            TIdentifier identifier) {

        MUnknownMacro unknownMacro = factory.newUnknownMacro();
        unknownMacro.addChar(identifier.getPos() + "");
        unknownMacro.addLine(identifier.getLine() + "");
        unknownMacro.addName(identifier.getText());

        return new CompilerException(unknownMacro.build());
    }

    public static CompilerException unknownMacro(
            TIdentifier identifier,
            MacroVersion version) {

        if (version == null) {
            return unknownMacro(identifier);
        }

        MUnknownMacro unknownMacro = factory.newUnknownMacro();
        unknownMacro.addChar(identifier.getPos() + "");
        unknownMacro.addLine(identifier.getLine() + "");
        unknownMacro.addName(identifier.getText());
        unknownMacro.addVersions(version.getName().getText());

        return new CompilerException(unknownMacro.build());
    }

    public static CompilerException unknownVersion(
            TIdentifier identifier) {

        MUnknownVersion unknownVersion = factory.newUnknownVersion();
        unknownVersion.addChar(identifier.getPos() + "");
        unknownVersion.addLine(identifier.getLine() + "");
        unknownVersion.addName(identifier.getText());

        return new CompilerException(unknownVersion.build());
    }

    public static CompilerException duplicateDeclaration(
            TIdentifier duplicateDeclaration,
            TIdentifier firstDeclaration) {

        String name = duplicateDeclaration.getText();
        if (!name.equals(firstDeclaration.getText())) {
            throw new InternalException("name must be identical");
        }

        MDuplicateDeclaration mDuplicateDeclaration
                = factory.newDuplicateDeclaration();
        mDuplicateDeclaration.addChar(duplicateDeclaration.getPos() + "");
        mDuplicateDeclaration.addLine(duplicateDeclaration.getLine() + "");
        mDuplicateDeclaration.addName(name);
        mDuplicateDeclaration.addRefChar(firstDeclaration.getPos() + "");
        mDuplicateDeclaration.addRefLine(firstDeclaration.getLine() + "");

        return new CompilerException(mDuplicateDeclaration.build());
    }

    public static CompilerException duplicateDeclaration(
            TIdentifier duplicateDeclaration,
            TIdentifier firstDeclaration,
            MacroVersion version) {

        String name = duplicateDeclaration.getText();
        if (!name.equals(firstDeclaration.getText())) {
            throw new InternalException("name must be identical");
        }

        if (version == null) {
            throw new InternalException("version may not be null");
        }

        MDuplicateDeclaration mDuplicateDeclaration
                = factory.newDuplicateDeclaration();
        mDuplicateDeclaration.addChar(duplicateDeclaration.getPos() + "");
        mDuplicateDeclaration.addLine(duplicateDeclaration.getLine() + "");
        mDuplicateDeclaration.addName(name);
        mDuplicateDeclaration.addRefChar(firstDeclaration.getPos() + "");
        mDuplicateDeclaration.addRefLine(firstDeclaration.getLine() + "");
        mDuplicateDeclaration.addVersion(version.getName().getText());

        return new CompilerException(mDuplicateDeclaration.build());
    }

    public static CompilerException duplicateOption(
            ADirective duplicateOption,
            ADirective firstOption) {

        String name = duplicateOption.getName().getText();
        if (!name.equals(firstOption.getName().getText())) {
            throw new InternalException("name must be identical");
        }

        MDuplicateOption mDuplicateOption = factory.newDuplicateOption();
        mDuplicateOption.addChar(duplicateOption.getName().getPos() + "");
        mDuplicateOption.addLine(duplicateOption.getName().getLine() + "");
        mDuplicateOption.addRefLine(firstOption.getName().getLine() + "");
        mDuplicateOption.addRefChar(firstOption.getName().getPos() + "");

        return new CompilerException(mDuplicateOption.build());
    }

    public static CompilerException conflictingOption(
            ADirective conflictingOption,
            ADirective firstOption) {

        String conflictingName = conflictingOption.getName().getText();
        String firstName = conflictingOption.getName().getText();

        MConflictingOption mConflictingOption = factory.newConflictingOption();
        mConflictingOption.addChar(conflictingOption.getName().getPos() + "");
        mConflictingOption.addLine(conflictingOption.getName().getLine() + "");
        mConflictingOption.addRefChar(firstOption.getName().getPos() + "");
        mConflictingOption.addRefLine(firstOption.getName().getLine() + "");
        mConflictingOption.addName(conflictingName);

        return new CompilerException(mConflictingOption.build());
    }

    public static CompilerException unknownOption(
            ADirective option) {

        TIdentifier nameId = option.getName();
        String name = nameId.getText();

        MUnknownOption mUnknownOption = factory.newUnknownOption();
        mUnknownOption.addChar(nameId.getPos() + "");
        mUnknownOption.addLine(nameId.getLine() + "");
        mUnknownOption.addName(name);

        return new CompilerException(mUnknownOption.build());
    }

    public static CompilerException endMismatch(
            TIdentifier name,
            TIdentifier refName) {

        MEndMismatch mEndMismatch = factory.newEndMismatch();
        mEndMismatch.addName(name.getText());
        mEndMismatch.addChar(name.getPos() + "");
        mEndMismatch.addLine(name.getLine() + "");
        mEndMismatch.addRefName(refName.getText());
        mEndMismatch.addRefChar(refName.getPos() + "");
        mEndMismatch.addRefLine(refName.getLine() + "");

        return new CompilerException(mEndMismatch.build());
    }

    public static CompilerException unknownParam(
            TIdentifier var) {

        String name = var.getText();
        MUnknownParam mUnknownParam = factory.newUnknownParam();
        mUnknownParam.addChar(var.getPos() + "");
        mUnknownParam.addLine(var.getLine() + "");
        mUnknownParam.addName(name);

        return new CompilerException(mUnknownParam.build());
    }

    public static CompilerException cyclicReference(
            TIdentifier reference,
            TIdentifier context) {

        MParamCyclicReference mParamCyclicReference
                = factory.newParamCyclicReference();
        mParamCyclicReference.addReference(reference.getText() + "");
        mParamCyclicReference.addChar(reference.getPos() + "");
        mParamCyclicReference.addLine(reference.getLine() + "");

        mParamCyclicReference.addContext(context.getText() + "");
        mParamCyclicReference.addContextChar(context.getPos() + "");
        mParamCyclicReference.addContextLine(context.getLine() + "");

        return new CompilerException(mParamCyclicReference.build());
    }

    public static CompilerException selfReference(
            TIdentifier reference,
            TIdentifier context) {

        MSelfReference mSelfReference = factory.newSelfReference();
        mSelfReference.addReference(reference.getText());
        mSelfReference.addChar(reference.getPos() + "");
        mSelfReference.addLine(reference.getLine() + "");
        mSelfReference.addContext(context.getText());
        mSelfReference.addContextChar(context.getPos() + "");
        mSelfReference.addContextLine(context.getLine() + "");

        return new CompilerException(mSelfReference.build());
    }

    public static CompilerException unusedParam(
            Param param) {

        TIdentifier name = param.getNameDeclaration();
        MUnusedParam mUnusedParam = factory.newUnusedParam();
        mUnusedParam.addChar(name.getPos() + "");
        mUnusedParam.addLine(name.getLine() + "");
        mUnusedParam.addName(name.getText());

        return new CompilerException(mUnusedParam.build());
    }

    public static CompilerException incorrectNumberArgument(
            AMacroReference declaration,
            MacroInfo macro_referenced) {

        String line = String.valueOf(declaration.getName().getLine());
        String pos = String.valueOf(declaration.getName().getPos());
        String expectedCount
                = String.valueOf(macro_referenced.getAllInternals().size());
        String currentCount = String.valueOf(declaration.getValues().size());

        MIncorrectNumberArgument mIncorrectNumberArgument
                = factory.newIncorrectNumberArgument();
        mIncorrectNumberArgument.addLine(line);
        mIncorrectNumberArgument.addChar(pos);
        mIncorrectNumberArgument.addExpectedCount(expectedCount);
        mIncorrectNumberArgument.addCurrentCount(currentCount);

        return new CompilerException(mIncorrectNumberArgument.build());
    }

    public static CompilerException incorrectNumberArgument(
            AMacroReference declaration,
            MacroInfo macro_referenced,
            MacroVersion version) {

        String line = String.valueOf(declaration.getName().getLine());
        String pos = String.valueOf(declaration.getName().getPos());
        String expectedCount
                = String.valueOf(macro_referenced.getAllInternals().size());
        String currentCount = String.valueOf(declaration.getValues().size());

        MIncorrectNumberArgument mIncorrectNumberArgument
                = factory.newIncorrectNumberArgument();
        mIncorrectNumberArgument.addLine(line);
        mIncorrectNumberArgument.addChar(pos);
        mIncorrectNumberArgument.addExpectedCount(expectedCount);
        mIncorrectNumberArgument.addCurrentCount(currentCount);
        mIncorrectNumberArgument.addVersion(version.getName().getText());

        return new CompilerException(mIncorrectNumberArgument.build());
    }

    public static CompilerException incorrectArgumentType(
            String expected,
            String found,
            Integer line,
            Integer pos) {

        String stringLine = String.valueOf(line);
        String stringPos = String.valueOf(pos);

        MIncorrectArgumentType mIncorrectArgumentType
                = factory.newIncorrectArgumentType();
        mIncorrectArgumentType.addChar(stringPos);
        mIncorrectArgumentType.addLine(stringLine);
        mIncorrectArgumentType.addExpected(expected);
        mIncorrectArgumentType.addFound(found);

        return new CompilerException(mIncorrectArgumentType.build());
    }

    public static CompilerException cannotCreateDirectory(
            String location) {

        MCannotCreateDirectory mCannotCreateDirectory
                = factory.newCannotCreateDirectory();
        mCannotCreateDirectory.addLocation(location);

        return new CompilerException(mCannotCreateDirectory.build());
    }

    public static CompilerException bodyTokenMisused(
            Token body) {

        String line = String.valueOf(body.getLine());
        String pos = String.valueOf(body.getPos());

        MBodyTokenMisused mBodyTokenMisused = factory.newBodyTokenMisused();
        mBodyTokenMisused.addChar(pos);
        mBodyTokenMisused.addLine(line);

        return new CompilerException(mBodyTokenMisused.build());
    }

    public static CompilerException indentTokenMisused(
            Token indent) {

        String line = String.valueOf(indent.getLine());
        String pos = String.valueOf(indent.getPos());

        MIndentTokenMisused mIndentTokenMisused
                = factory.newIndentTokenMisused();
        mIndentTokenMisused.addChar(pos);
        mIndentTokenMisused.addLine(line);

        return new CompilerException(mIndentTokenMisused.build());
    }

    public static CompilerException duplicateMacroRef(
            Token macroRef,
            Token paramName) {

        String line = String.valueOf(macroRef.getLine());
        String pos = String.valueOf(macroRef.getPos());

        MDuplicateMacroRef mDuplicateMacroRef = factory.newDuplicateMacroRef();
        mDuplicateMacroRef.addChar(pos);
        mDuplicateMacroRef.addLine(line);
        mDuplicateMacroRef.addParam(paramName.getText());
        mDuplicateMacroRef.addMacro(macroRef.getText());

        return new CompilerException(mDuplicateMacroRef.build());

    }

    public static CompilerException incorrectMacroType(
            Set<String> expectedMacros,
            Set<String> providedMacros,
            Integer index,
            Token parameter_name) {

        StringBuilder expectedBuilder = new StringBuilder();

        for (String l_expected : expectedMacros) {
            expectedBuilder.append(l_expected);
        }

        StringBuilder providedBuilder = new StringBuilder();

        for (String l_provided : providedMacros) {
            providedBuilder.append(l_provided);
        }

        MIncorrectMacroType mIncorrectMacroType
                = factory.newIncorrectMacroType();
        mIncorrectMacroType.addChar(parameter_name.getPos() + "");
        mIncorrectMacroType.addLine(parameter_name.getLine() + "");
        mIncorrectMacroType.addIndex(index + "");
        mIncorrectMacroType.addExpected(expectedBuilder.toString());
        mIncorrectMacroType.addFound(providedBuilder.toString());

        return new CompilerException(mIncorrectMacroType.build());
    }

    public static CompilerException invalidInsert(
            Token name) {

        String line = String.valueOf(name.getLine());
        String pos = String.valueOf(name.getPos());

        MInvalidInsert mInvalidInsert = factory.newInvalidInsert();
        mInvalidInsert.addLine(line);
        mInvalidInsert.addChar(pos);
        mInvalidInsert.addName(name.getText());

        return new CompilerException(mInvalidInsert.build());
    }

    public static CompilerException missingParameter(
            TIdentifier macro_name,
            MacroVersion version,
            Param param) {

        MMissingParameter missing_parameter = factory.newMissingParameter();
        missing_parameter.addLine(macro_name.getLine() + "");
        missing_parameter.addChar(macro_name.getPos() + "");
        missing_parameter.addMacroName(macro_name.getText());
        missing_parameter.addVersion(version.getName().getText());
        missing_parameter.addParameterName(param.getName());

        if (param.isString()) {
            missing_parameter.addType("String");
        }
        else {
            for (AMacroReference macro_reference : param.getMacroReferences()) {
                missing_parameter.addType(macro_reference.getName().getText());
            }
        }

        return new CompilerException(missing_parameter.build());
    }

    public static CompilerException missingInternal(
            TIdentifier macro_name,
            MacroVersion version,
            Param param) {

        MMissingInternal missing_internal = factory.newMissingInternal();
        missing_internal.addLine(macro_name.getLine() + "");
        missing_internal.addChar(macro_name.getPos() + "");
        missing_internal.addMacroName(macro_name.getText());
        missing_internal.addVersion(version.getName().getText());
        missing_internal.addInternalName(param.getName());

        if (param.isString()) {
            missing_internal.addType("String");
        }
        else {
            for (AMacroReference macro_reference : param.getMacroReferences()) {
                missing_internal.addType(macro_reference.getName().getText());
            }
        }

        return new CompilerException(missing_internal.build());
    }

    public static CompilerException incorrectParameterType(
            Param param,
            TIdentifier macro_name,
            MacroVersion macro_version,
            List<String> expected_types) {

        MIncorrectParameterType incorrectParameterType
                = factory.newIncorrectParameterType();
        incorrectParameterType
                .addChar(param.getNameDeclaration().getPos() + "");
        incorrectParameterType
                .addLine(param.getNameDeclaration().getLine() + "");
        incorrectParameterType.addMacroName(macro_name.getText());
        incorrectParameterType.addParameter(param.getName());
        incorrectParameterType.addVersion(macro_version.getName().getText());

        for (String type : expected_types) {
            incorrectParameterType.addType(type);
        }

        return new CompilerException(incorrectParameterType.build());
    }
}
