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

import java.util.Set;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.errormessage.Macros;
import org.sablecc.objectmacro.errormessage.MBodyTokenMisused;
import org.sablecc.objectmacro.errormessage.MCannotCreateDirectory;
import org.sablecc.objectmacro.errormessage.MConflictingOption;
import org.sablecc.objectmacro.errormessage.MParamCyclicReference;
import org.sablecc.objectmacro.errormessage.MDuplicateDeclaration;
import org.sablecc.objectmacro.errormessage.MDuplicateMacroRef;
import org.sablecc.objectmacro.errormessage.MDuplicateOption;
import org.sablecc.objectmacro.errormessage.MEndMismatch;
import org.sablecc.objectmacro.errormessage.MIncorrectArgumentCount;
import org.sablecc.objectmacro.errormessage.MIncorrectArgumentType;
import org.sablecc.objectmacro.errormessage.MIncorrectMacroType;
import org.sablecc.objectmacro.errormessage.MIndentTokenMisused;
import org.sablecc.objectmacro.errormessage.MInputError;
import org.sablecc.objectmacro.errormessage.MInvalidArgument;
import org.sablecc.objectmacro.errormessage.MInvalidArgumentCount;
import org.sablecc.objectmacro.errormessage.MInvalidInsert;
import org.sablecc.objectmacro.errormessage.MInvalidIntermediateSuffix;
import org.sablecc.objectmacro.errormessage.MInvalidLongOption;
import org.sablecc.objectmacro.errormessage.MInvalidObjectmacroSuffix;
import org.sablecc.objectmacro.errormessage.MInvalidShortOption;
import org.sablecc.objectmacro.errormessage.MMacroNotFile;
import org.sablecc.objectmacro.errormessage.MMissingLongOptionOperand;
import org.sablecc.objectmacro.errormessage.MMissingMacroFile;
import org.sablecc.objectmacro.errormessage.MMissingShortOptionOperand;
import org.sablecc.objectmacro.errormessage.MOutputError;
import org.sablecc.objectmacro.errormessage.MPlainText;
import org.sablecc.objectmacro.errormessage.MUnknownVersion;
import org.sablecc.objectmacro.errormessage.MSelfReference;
import org.sablecc.objectmacro.errormessage.MSpuriousLongOptionOperand;
import org.sablecc.objectmacro.errormessage.MSpuriousShortOptionOperand;
import org.sablecc.objectmacro.errormessage.MUnknownMacro;
import org.sablecc.objectmacro.errormessage.MUnknownOption;
import org.sablecc.objectmacro.errormessage.MUnknownParam;
import org.sablecc.objectmacro.errormessage.MUnknownTarget;
import org.sablecc.objectmacro.errormessage.MUnusedParam;
import org.sablecc.objectmacro.structure.MacroInfo;
import org.sablecc.objectmacro.structure.MacroVersion;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.node.ADirective;
import org.sablecc.objectmacro.syntax3.node.AMacroReference;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;
import org.sablecc.objectmacro.syntax3.node.Token;

@SuppressWarnings("serial")
public class CompilerException
        extends
        RuntimeException {

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

        return new CompilerException(
                factory.newInvalidArgument(argumentText).build(), cause);
    }

    public static CompilerException missingLongOptionOperand(
            String optionName,
            String operandName) {

        return new CompilerException(
                factory.newMissingLongOptionOperand(optionName, operandName)
                        .build());
    }

    public static CompilerException missingShortOptionOperand(
            String optionName,
            String operandName) {

        return new CompilerException(
                factory.newMissingShortOptionOperand(optionName, operandName)
                        .build());
    }

    public static CompilerException invalidLongOption(
            String optionName) {

        return new CompilerException(
                factory.newInvalidLongOption(optionName).build());
    }

    public static CompilerException spuriousLongOptionOperand(
            String optionName,
            String operandText) {

        return new CompilerException(
                factory.newSpuriousLongOptionOperand(optionName, operandText)
                        .build());
    }

    public static CompilerException invalidShortOption(
            String optionName) {

        return new CompilerException(
                factory.newInvalidShortOption(optionName).build());
    }

    public static CompilerException spuriousShortOptionOperand(
            String optionName,
            String operandText) {

        return new CompilerException(
                factory.newSpuriousShortOptionOperand(optionName, operandText)
                        .build());
    }

    public static CompilerException unknownTarget(
            String targetLanguage) {

        return new CompilerException(
                factory.newUnknownTarget(targetLanguage).build());
    }

    public static CompilerException invalidArgumentCount() {

        return new CompilerException(factory.newInvalidArgumentCount().build());
    }

    public static CompilerException invalidObjectmacroSuffix(
            String fileName) {

        return new CompilerException(
                factory.newInvalidObjectmacroSuffix(fileName).build());
    }

    public static CompilerException invalidIntermediateSuffix(
            String fileName) {

        return new CompilerException(
                factory.newInvalidIntermediateSuffix(fileName).build());
    }

    public static CompilerException missingMacroFile(
            String fileName) {

        return new CompilerException(
                factory.newMissingMacroFile(fileName).build());
    }

    public static CompilerException macroNotFile(
            String fileName) {

        return new CompilerException(factory.newMacroNotFile(fileName).build());
    }

    public static CompilerException inputError(
            String fileName,
            Throwable cause) {

        return new CompilerException(
                factory.newInputError(fileName, cause.getMessage()).build(),
                cause);
    }

    public static CompilerException outputError(
            String fileName,
            Throwable cause) {

        return new CompilerException(
                factory.newOutputError(fileName, cause.getMessage()).build(),
                cause);
    }

    public static CompilerException unknownMacro(
            TIdentifier identifier) {

        return new CompilerException(factory.newUnknownMacro(identifier.getText(),
                identifier.getLine() + "", identifier.getPos() + "")
                        .build());
    }

    public static CompilerException unknownVersion(
            TIdentifier identifier) {

        return new CompilerException(factory.newUnknownVersion(identifier.getText(),
                identifier.getLine() + "", identifier.getPos() + "")
                .build());
    }

    public static CompilerException duplicateDeclaration(
            TIdentifier duplicateDeclaration,
            TIdentifier firstDeclaration) {

        String name = duplicateDeclaration.getText();
        if (!name.equals(firstDeclaration.getText())) {
            throw new InternalException("name must be identical");
        }

        return new CompilerException(factory.newDuplicateDeclaration(name,
                duplicateDeclaration.getLine() + "",
                duplicateDeclaration.getPos() + "",
                firstDeclaration.getLine() + "", firstDeclaration.getPos() + "")
                        .build());
    }

    public static CompilerException duplicateDeclaration(
            TIdentifier duplicateDeclaration,
            TIdentifier firstDeclaration,
            MacroVersion version) {

        String name = duplicateDeclaration.getText();
        if (!name.equals(firstDeclaration.getText())) {
            throw new InternalException("name must be identical");
        }

        if(version == null){
            throw new InternalException("version may not be null");
        }

        MDuplicateDeclaration mDuplicateDeclaration = factory.newDuplicateDeclaration(name,
                duplicateDeclaration.getLine() + "",
                duplicateDeclaration.getPos() + "",
                firstDeclaration.getLine() + "", firstDeclaration.getPos() + "");

        mDuplicateDeclaration.addVersion(factory.newPlainText(version.getName().getText()));

        return new CompilerException(mDuplicateDeclaration.build());
    }

    public static CompilerException duplicateOption(
            ADirective duplicateOption,
            ADirective firstOption) {

        String name = duplicateOption.getName().getText();
        if (!name.equals(firstOption.getName().getText())) {
            throw new InternalException("name must be identical");
        }

        return new CompilerException(factory.newDuplicateOption(name,
                duplicateOption.getName().getLine() + "",
                duplicateOption.getName().getPos() + "",
                firstOption.getName().getLine() + "",
                firstOption.getName().getPos() + "").build());
    }

    public static CompilerException conflictingOption(
            ADirective conflictingOption,
            ADirective firstOption) {

        String conflictingName = conflictingOption.getName().getText();
        String firstName = conflictingOption.getName().getText();

        return new CompilerException(factory.newConflictingOption(conflictingName,
                conflictingOption.getName().getLine() + "",
                conflictingOption.getName().getPos() + "", firstName,
                firstOption.getName().getLine() + "",
                firstOption.getName().getPos() + "").build());
    }

    public static CompilerException unknownOption(
            ADirective option) {

        TIdentifier nameId = option.getName();
        String name = nameId.getText();

        return new CompilerException(factory.newUnknownOption(name,
                nameId.getLine() + "", nameId.getPos() + "").build());
    }

    public static CompilerException endMismatch(
            TIdentifier name,
            TIdentifier refName) {

        return new CompilerException(factory.newEndMismatch(name.getText(),
                name.getLine() + "", name.getPos() + "", refName.getText(),
                refName.getLine() + "", refName.getPos() + "").build());
    }

    public static CompilerException unknownParam(
            TIdentifier var) {

        String name = var.getText();

        return new CompilerException(
                factory.newUnknownParam(name, var.getLine() + "", var.getPos() + "")
                        .build());
    }

    public static CompilerException cyclicReference(
            TIdentifier reference,
            TIdentifier context) {

        return new CompilerException(factory.newParamCyclicReference(reference.getText(),
                reference.getLine() + "", reference.getPos() + "",
                context.getText(), context.getLine() + "",
                context.getPos() + "").build());
    }

    public static CompilerException selfReference(
            TIdentifier reference,
            TIdentifier context) {

        return new CompilerException(factory.newSelfReference(reference.getText(),
                reference.getLine() + "", reference.getPos() + "",
                context.getText(), context.getLine() + "",
                context.getPos() + "").build());
    }

    public static CompilerException unusedParam(
            Param param) {

        TIdentifier name = param.getNameDeclaration();

        return new CompilerException(factory.newUnusedParam(name.getText(),
                name.getLine() + "", name.getPos() + "").build());
    }

    public static CompilerException incorrectArgumentCount(
            AMacroReference declaration,
            MacroInfo macro_referenced) {

        String line = String.valueOf(declaration.getName().getLine());
        String pos = String.valueOf(declaration.getName().getPos());
        String expectedCount = String
                .valueOf(macro_referenced.getAllInternals().size());
        String currentCount = String.valueOf(declaration.getValues().size());

        return new CompilerException(factory.newIncorrectArgumentCount(line, pos,
                expectedCount, currentCount).build());
    }

    public static CompilerException incorrectArgumentType(
            String expected,
            String found,
            Integer line,
            Integer pos) {

        String stringLine = String.valueOf(line);
        String stringPos = String.valueOf(pos);

        return new CompilerException(factory.newIncorrectArgumentType(expected, found,
                stringLine, stringPos).build());
    }

    public static CompilerException cannotCreateDirectory(
            String location) {

        return new CompilerException(
                factory.newCannotCreateDirectory(location).build());
    }

    public static CompilerException bodyTokenMisused(
            Token body) {

        String line = String.valueOf(body.getLine());
        String pos = String.valueOf(body.getPos());

        return new CompilerException(
                factory.newBodyTokenMisused(line, pos).build());
    }

    public static CompilerException indentTokenMisused(
            Token indent) {

        String line = String.valueOf(indent.getLine());
        String pos = String.valueOf(indent.getPos());

        return new CompilerException(
                factory.newIndentTokenMisused(line, pos).build());
    }

    public static CompilerException duplicateMacroRef(
            Token macroRef,
            Token paramName) {

        String line = String.valueOf(macroRef.getLine());
        String pos = String.valueOf(macroRef.getPos());

        return new CompilerException(factory.newDuplicateMacroRef(paramName.getText(),
                macroRef.getText(), line, pos).build());

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

        return new CompilerException(factory.newIncorrectMacroType(
                expectedBuilder.toString(), providedBuilder.toString(),
                String.valueOf(index), String.valueOf(parameter_name.getLine()),
                String.valueOf(parameter_name.getPos())).build());
    }

    public static CompilerException invalidInsert(
            Token name) {

        String line = String.valueOf(name.getLine());
        String pos = String.valueOf(name.getPos());

        return new CompilerException(
                factory.newInvalidInsert(line, pos, name.getText()).build());
    }
}
