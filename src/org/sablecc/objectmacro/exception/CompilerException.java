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

import org.sablecc.exception.*;
import org.sablecc.objectmacro.errormessage.*;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.util.*;

import java.util.Set;

@SuppressWarnings("serial")
public class CompilerException
        extends RuntimeException {

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
                new MInvalidArgument(argumentText).toString(), cause);
    }

    public static CompilerException missingLongOptionOperand(
            String optionName,
            String operandName) {

        return new CompilerException(new MMissingLongOptionOperand(optionName,
                operandName).toString());
    }

    public static CompilerException missingShortOptionOperand(
            String optionName,
            String operandName) {

        return new CompilerException(new MMissingShortOptionOperand(optionName,
                operandName).toString());
    }

    public static CompilerException invalidLongOption(
            String optionName) {

        return new CompilerException(
                new MInvalidLongOption(optionName).toString());
    }

    public static CompilerException spuriousLongOptionOperand(
            String optionName,
            String operandText) {

        return new CompilerException(new MSpuriousLongOptionOperand(optionName,
                operandText).toString());
    }

    public static CompilerException invalidShortOption(
            String optionName) {

        return new CompilerException(
                new MInvalidShortOption(optionName).toString());
    }

    public static CompilerException spuriousShortOptionOperand(
            String optionName,
            String operandText) {

        return new CompilerException(new MSpuriousShortOptionOperand(
                optionName, operandText).toString());
    }

    public static CompilerException unknownTarget(
            String targetLanguage) {

        return new CompilerException(
                new MUnknownTarget(targetLanguage).toString());
    }

    public static CompilerException invalidArgumentCount() {

        return new CompilerException(new MInvalidArgumentCount().toString());
    }

    public static CompilerException invalidObjectmacroSuffix(
            String fileName) {

        return new CompilerException(new MInvalidObjectmacroSuffix(fileName).toString());
    }

    public static CompilerException invalidIntermediateSuffix(
            String fileName) {

        return new CompilerException(new MInvalidIntermediateSuffix(fileName).toString());
    }

    public static CompilerException missingMacroFile(
            String fileName) {

        return new CompilerException(new MMissingMacroFile(fileName).toString());
    }

    public static CompilerException macroNotFile(
            String fileName) {

        return new CompilerException(new MMacroNotFile(fileName).toString());
    }

    public static CompilerException inputError(
            String fileName,
            Throwable cause) {

        return new CompilerException(new MInputError(fileName,
                cause.getMessage()).toString(), cause);
    }

    public static CompilerException outputError(
            String fileName,
            Throwable cause) {

        return new CompilerException(new MOutputError(fileName,
                cause.getMessage()).toString(), cause);
    }

    public static CompilerException unknownMacro(
            TIdentifier identifier) {

        return new CompilerException(new MUnknownMacro(identifier.getText(),
                identifier.getLine() + "", identifier.getPos() + "").toString());
    }

    public static CompilerException duplicateDeclaration(
            TIdentifier duplicateDeclaration,
            TIdentifier firstDeclaration) {

        String name = duplicateDeclaration.getText();
        if (!name.equals(firstDeclaration.getText())) {
            throw new InternalException("name must be identical");
        }

        return new CompilerException(new MDuplicateDeclaration(name,
                duplicateDeclaration.getLine() + "",
                duplicateDeclaration.getPos() + "", firstDeclaration.getLine()
                        + "", firstDeclaration.getPos() + "").toString());
    }

    public static CompilerException duplicateOption(
            ADirective duplicateOption,
            ADirective firstOption) {

        String name = duplicateOption.getName().getText();
        if (!name.equals(firstOption.getName().getText())) {
            throw new InternalException("name must be identical");
        }

        return new CompilerException(new MDuplicateOption(name, duplicateOption
                .getName().getLine() + "", duplicateOption.getName().getPos()
                + "", firstOption.getName().getLine() + "", firstOption
                .getName().getPos() + "").toString());
    }

    public static CompilerException conflictingOption(
            ADirective conflictingOption,
            ADirective firstOption) {

        String conflictingName = conflictingOption.getName().getText();
        String firstName = conflictingOption.getName().getText();

        return new CompilerException(new MConflictingOption(conflictingName,
                conflictingOption.getName().getLine() + "", conflictingOption
                        .getName().getPos() + "", firstName, firstOption
                        .getName().getLine() + "", firstOption.getName()
                        .getPos() + "").toString());
    }

    public static CompilerException unknownOption(
            ADirective option) {

        TIdentifier nameId = option.getName();
        String name = nameId.getText();

        return new CompilerException(new MUnknownOption(name, nameId.getLine()
                + "", nameId.getPos() + "").toString());
    }

    public static CompilerException endMismatch(
            TIdentifier name,
            TIdentifier refName) {

        return new CompilerException(new MEndMismatch(name.getText(),
                name.getLine() + "", name.getPos() + "", refName.getText(),
                refName.getLine() + "", refName.getPos() + "").toString());
    }

    public static CompilerException unknownParam(
            TIdentifier var) {

        String name = var.getText();

        return new CompilerException(new MUnknownParam(name,
                var.getLine() + "", var.getPos() + "").toString());
    }

    public static CompilerException cyclicReference(
            TIdentifier reference,
            TIdentifier context) {

        return new CompilerException(new MCyclicReference(reference.getText(),
                reference.getLine() + "", reference.getPos() + "",
                context.getText(), context.getLine() + "", context.getPos()
                        + "").toString());
    }

    public static CompilerException selfReference(
            TIdentifier reference,
            TIdentifier context) {

        return new CompilerException(new MSelfReference(reference.getText(),
                reference.getLine() + "", reference.getPos() + "",
                context.getText(), context.getLine() + "", context.getPos()
                + "").toString());
    }

    public static CompilerException unusedParam(
            Param param) {

        TIdentifier name = param.getNameDeclaration();

        return new CompilerException(new MUnusedParam(name.getText(),
                name.getLine() + "", name.getPos() + "").toString());
    }

    public static CompilerException incorrectArgumentCount(
            AMacroReference declaration, Macro macroReferenced) {

        String line = String.valueOf(declaration.getName().getLine());
        String pos = String.valueOf(declaration.getName().getPos());
        String expectedCount = String.valueOf(macroReferenced.getAllInternals().size());
        String currentCount = String.valueOf(declaration.getValues().size());

        return new CompilerException(
                new MIncorrectArgumentCount(line, pos, expectedCount, currentCount).toString());
    }

    public static CompilerException incorrectArgumentType(
            String expected,
            String found,
            Integer line,
            Integer pos){

        String stringLine = String.valueOf(line);
        String stringPos = String.valueOf(pos);

        return new CompilerException(
                new MIncorrectArgumentType(expected, found, stringLine, stringPos).toString());
    }

    public static CompilerException cannotCreateDirectory(
            String location) {

        return new CompilerException(
                new MCannotCreateDirectory(location).toString());
    }

    public static CompilerException beginTokenMisused(
            Token begin){

        String line = String.valueOf(begin.getLine());
        String pos = String.valueOf(begin.getPos());

        return new CompilerException(
                new MBeginTokenMisused(line, pos).toString());
    }

    public static CompilerException duplicateMacroRef(
            Token macroRef,
            Token paramName){

        String line = String.valueOf(macroRef.getLine());
        String pos = String.valueOf(macroRef.getPos());

        return new CompilerException(
                new MDuplicateMacroRef(
                        paramName.getText(), macroRef.getText(), line, pos).toString());

    }

    public static CompilerException incorrectMacroType(
            Set<String> expectedMacros,
            Set<String> providedMacros,
            Integer index,
            Token parameter_name){

        StringBuilder expectedBuilder = new StringBuilder();

        for(String l_expected : expectedMacros){
            expectedBuilder.append(l_expected);
        }

        StringBuilder providedBuilder = new StringBuilder();

        for(String l_provided : providedMacros){
            providedBuilder.append(l_provided);
        }

        return new CompilerException(
                new MIncorrectMacroType(
                        expectedBuilder.toString(), providedBuilder.toString(),
                        String.valueOf(index), String.valueOf(parameter_name.getLine()),
                        String.valueOf(parameter_name.getPos())).toString());
    }

    public static CompilerException invalidInsert(
            Token name){

        String line = String.valueOf(name.getLine());
        String pos = String.valueOf(name.getPos());

        return new CompilerException(new MInvalidInsert(line, pos, name.getText()).toString());
    }
}
