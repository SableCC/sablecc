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

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.errormessage.MCannotCreateDirectory;
import org.sablecc.objectmacro.errormessage.MConflictingOption;
import org.sablecc.objectmacro.errormessage.MCyclicReference;
import org.sablecc.objectmacro.errormessage.MDuplicateDeclaration;
import org.sablecc.objectmacro.errormessage.MDuplicateOption;
import org.sablecc.objectmacro.errormessage.MEndMismatch;
import org.sablecc.objectmacro.errormessage.MIncorrectArgumentCount;
import org.sablecc.objectmacro.errormessage.MInputError;
import org.sablecc.objectmacro.errormessage.MInvalidArgument;
import org.sablecc.objectmacro.errormessage.MInvalidArgumentCount;
import org.sablecc.objectmacro.errormessage.MInvalidLongOption;
import org.sablecc.objectmacro.errormessage.MInvalidShortOption;
import org.sablecc.objectmacro.errormessage.MInvalidSuffix;
import org.sablecc.objectmacro.errormessage.MMacroNotFile;
import org.sablecc.objectmacro.errormessage.MMissingLongOptionOperand;
import org.sablecc.objectmacro.errormessage.MMissingMacroFile;
import org.sablecc.objectmacro.errormessage.MMissingShortOptionOperand;
import org.sablecc.objectmacro.errormessage.MOutputError;
import org.sablecc.objectmacro.errormessage.MSpuriousLongOptionOperand;
import org.sablecc.objectmacro.errormessage.MSpuriousShortOptionOperand;
import org.sablecc.objectmacro.errormessage.MUnknownMacro;
import org.sablecc.objectmacro.errormessage.MUnknownOption;
import org.sablecc.objectmacro.errormessage.MUnknownParam;
import org.sablecc.objectmacro.errormessage.MUnknownTarget;
import org.sablecc.objectmacro.errormessage.MUnknownTextBlock;
import org.sablecc.objectmacro.errormessage.MUnusedParam;
import org.sablecc.objectmacro.errormessage.MUnusedTextBlock;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.structure.TextBlock;
import org.sablecc.objectmacro.structure.TextInsert;
import org.sablecc.objectmacro.syntax3.node.AOption;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;
import org.sablecc.objectmacro.syntax3.node.TVar;
import org.sablecc.objectmacro.util.Utils;

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

        return new CompilerException(new MInvalidArgument(argumentText)
                .toString(), cause);
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

        return new CompilerException(new MInvalidLongOption(optionName)
                .toString());
    }

    public static CompilerException spuriousLongOptionOperand(
            String optionName,
            String operandText) {

        return new CompilerException(new MSpuriousLongOptionOperand(optionName,
                operandText).toString());
    }

    public static CompilerException invalidShortOption(
            String optionName) {

        return new CompilerException(new MInvalidShortOption(optionName)
                .toString());
    }

    public static CompilerException spuriousShortOptionOperand(
            String optionName,
            String operandText) {

        return new CompilerException(new MSpuriousShortOptionOperand(
                optionName, operandText).toString());
    }

    public static CompilerException unknownTarget(
            String targetLanguage) {

        return new CompilerException(new MUnknownTarget(targetLanguage)
                .toString());
    }

    public static CompilerException invalidArgumentCount() {

        return new CompilerException(new MInvalidArgumentCount().toString());
    }

    public static CompilerException invalidSuffix(
            String fileName) {

        return new CompilerException(new MInvalidSuffix(fileName).toString());
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

        return new CompilerException(new MInputError(fileName, cause
                .getMessage()).toString(), cause);
    }

    public static CompilerException outputError(
            String fileName,
            Throwable cause) {

        return new CompilerException(new MOutputError(fileName, cause
                .getMessage()).toString(), cause);
    }

    public static CompilerException unknownMacro(
            TIdentifier identifier) {

        return new CompilerException(new MUnknownMacro(identifier.getText(),
                identifier.getLine() + "", identifier.getPos() + "").toString());
    }

    public static CompilerException unknownTextBlock(
            TIdentifier identifier) {

        return new CompilerException(new MUnknownTextBlock(
                identifier.getText(), identifier.getLine() + "", identifier
                        .getPos()
                        + "").toString());
    }

    public static CompilerException duplicateDeclaration(
            TIdentifier duplicateDeclaration,
            TIdentifier firstDeclaration) {

        String name = duplicateDeclaration.getText();
        if (!name.equals(firstDeclaration.getText())) {
            throw new InternalException("name must be identical");
        }

        return new CompilerException(new MDuplicateDeclaration(name,
                duplicateDeclaration.getLine() + "", duplicateDeclaration
                        .getPos()
                        + "", firstDeclaration.getLine() + "", firstDeclaration
                        .getPos()
                        + "").toString());
    }

    public static CompilerException duplicateOption(
            AOption duplicateOption,
            AOption firstOption) {

        String name = duplicateOption.getName().getText();
        if (!name.equals(firstOption.getName().getText())) {
            throw new InternalException("name must be identical");
        }

        return new CompilerException(new MDuplicateOption(name, duplicateOption
                .getName().getLine()
                + "", duplicateOption.getName().getPos() + "", firstOption
                .getName().getLine()
                + "", firstOption.getName().getPos() + "").toString());
    }

    public static CompilerException conflictingOption(
            AOption conflictingOption,
            AOption firstOption) {

        String conflictingName = conflictingOption.getName().getText();
        String firstName = conflictingOption.getName().getText();

        return new CompilerException(new MConflictingOption(conflictingName,
                conflictingOption.getName().getLine() + "", conflictingOption
                        .getName().getPos()
                        + "", firstName, firstOption.getName().getLine() + "",
                firstOption.getName().getPos() + "").toString());
    }

    public static CompilerException unknownOption(
            AOption option) {

        TIdentifier nameId = option.getName();
        String name = nameId.getText();

        return new CompilerException(new MUnknownOption(name, nameId.getLine()
                + "", nameId.getPos() + "").toString());
    }

    public static CompilerException endMismatch(
            TIdentifier name,
            TIdentifier refName) {

        return new CompilerException(new MEndMismatch(name.getText(), name
                .getLine()
                + "", name.getPos() + "", refName.getText(), refName.getLine()
                + "", refName.getPos() + "").toString());
    }

    public static CompilerException unknownParam(
            TVar var) {

        String name = Utils.getVarName(var);

        return new CompilerException(new MUnknownParam(name,
                var.getLine() + "", var.getPos() + "").toString());
    }

    public static CompilerException cyclicReference(
            TIdentifier reference,
            TIdentifier context) {

        return new CompilerException(new MCyclicReference(reference.getText(),
                reference.getLine() + "", reference.getPos() + "", context
                        .getText(), context.getLine() + "", context.getPos()
                        + "").toString());
    }

    public static CompilerException unusedTextBlock(
            TextBlock textBlock) {

        TIdentifier name = textBlock.getNameDeclaration();

        return new CompilerException(new MUnusedTextBlock(name.getText(), name
                .getLine()
                + "", name.getPos() + "").toString());
    }

    public static CompilerException unusedParam(
            Param param) {

        TIdentifier name = param.getNameDeclaration();

        return new CompilerException(new MUnusedParam(name.getText(), name
                .getLine()
                + "", name.getPos() + "").toString());
    }

    public static CompilerException incorrectArgumentCount(
            TextInsert textInsert) {

        TIdentifier insertName = textInsert.getDeclaration().getName();
        TIdentifier textBlockName = textInsert.getInsertedTextBlock()
                .getNameDeclaration();
        int argCount = textInsert.getDeclaration().getStaticValues().size();
        int paramCount = textInsert.getInsertedTextBlock().getDeclaration()
                .getParams().size();

        return new CompilerException(new MIncorrectArgumentCount(insertName
                .getLine()
                + "", insertName.getPos() + "", argCount + "", textBlockName
                .getText(), textBlockName.getLine() + "", +textBlockName
                .getPos()
                + "", paramCount + "").toString());
    }

    public static CompilerException cannotCreateDirectory(
            String location) {

        return new CompilerException(new MCannotCreateDirectory(location)
                .toString());
    }
}
