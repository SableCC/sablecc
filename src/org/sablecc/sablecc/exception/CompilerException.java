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

package org.sablecc.sablecc.exception;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.errormessage.MCyclicReference;
import org.sablecc.sablecc.errormessage.MDuplicateDeclaration;
import org.sablecc.sablecc.errormessage.MGrammarNotFile;
import org.sablecc.sablecc.errormessage.MInputError;
import org.sablecc.sablecc.errormessage.MInvalidArgument;
import org.sablecc.sablecc.errormessage.MInvalidArgumentCount;
import org.sablecc.sablecc.errormessage.MInvalidInterval;
import org.sablecc.sablecc.errormessage.MInvalidLongOption;
import org.sablecc.sablecc.errormessage.MInvalidReference;
import org.sablecc.sablecc.errormessage.MInvalidShortOption;
import org.sablecc.sablecc.errormessage.MInvalidSuffix;
import org.sablecc.sablecc.errormessage.MMissingGrammarFile;
import org.sablecc.sablecc.errormessage.MMissingLongOptionOperand;
import org.sablecc.sablecc.errormessage.MMissingShortOptionOperand;
import org.sablecc.sablecc.errormessage.MNotImplemented;
import org.sablecc.sablecc.errormessage.MSpuriousLongOptionOperand;
import org.sablecc.sablecc.errormessage.MSpuriousShortOptionOperand;
import org.sablecc.sablecc.errormessage.MUndefinedReference;
import org.sablecc.sablecc.errormessage.MUnknownTarget;
import org.sablecc.sablecc.syntax3.node.TIdentifier;
import org.sablecc.sablecc.syntax3.node.TTwoDots;
import org.sablecc.sablecc.syntax3.node.Token;

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
            String argument_text,
            Throwable cause) {

        return new CompilerException(new MInvalidArgument(argument_text)
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

    public static CompilerException invalidShortOption(
            String optionName) {

        return new CompilerException(new MInvalidShortOption(optionName)
                .toString());
    }

    public static CompilerException spuriousLongOptionOperand(
            String optionName,
            String operand_text) {

        return new CompilerException(new MSpuriousLongOptionOperand(optionName,
                operand_text).toString());
    }

    public static CompilerException spuriousShortOptionOperand(
            String optionName,
            String operand_text) {

        return new CompilerException(new MSpuriousShortOptionOperand(
                optionName, operand_text).toString());
    }

    public static CompilerException inputError(
            String fileName,
            Throwable cause) {

        return new CompilerException(new MInputError(fileName, cause
                .getMessage()).toString(), cause);
    }

    public static CompilerException invalidArgumentCount() {

        return new CompilerException(new MInvalidArgumentCount().toString());
    }

    public static CompilerException invalidSuffix(
            String fileName) {

        return new CompilerException(new MInvalidSuffix(fileName).toString());
    }

    public static CompilerException missingGrammarFile(
            String fileName) {

        return new CompilerException(new MMissingGrammarFile(fileName)
                .toString());
    }

    public static CompilerException grammarNotFile(
            String fileName) {

        return new CompilerException(new MGrammarNotFile(fileName).toString());
    }

    public static CompilerException duplicateDeclaration(
            TIdentifier duplicateName,
            TIdentifier firstName) {

        String name = duplicateName.getText();
        if (!name.equals(firstName.getText())) {
            throw new InternalException("names must be identical");
        }

        return new CompilerException(new MDuplicateDeclaration(name,
                duplicateName.getLine() + "", duplicateName.getPos() + "",
                firstName.getLine() + "", firstName.getPos() + "").toString());
    }

    public static CompilerException unknownTarget(
            String targetLanguage) {

        return new CompilerException(new MUnknownTarget(targetLanguage)
                .toString());
    }

    public static CompilerException invalidInterval(
            TTwoDots twoDots,
            Token from,
            Token to) {

        return new CompilerException(new MInvalidInterval(twoDots.getLine()
                + "", twoDots.getPos() + "", from.getText(), to.getText())
                .toString());
    }

    public static CompilerException undefinedReference(
            TIdentifier identifier) {

        return new CompilerException(
                new MUndefinedReference(identifier.getText(), identifier
                        .getLine()
                        + "", identifier.getPos() + "").toString());
    }

    public static CompilerException invalidReference(
            TIdentifier identifier) {

        return new CompilerException(new MInvalidReference(
                identifier.getText(), identifier.getLine() + "", identifier
                        .getPos()
                        + "").toString());
    }

    public static CompilerException cyclicReference(
            TIdentifier reference,
            TIdentifier context) {

        return new CompilerException(new MCyclicReference(reference.getText(),
                reference.getLine() + "", reference.getPos() + "", context
                        .getText(), context.getLine() + "", context.getPos()
                        + "").toString());
    }

    public static CompilerException notImplemented(
            Token token,
            String feature) {

        return new CompilerException(new MNotImplemented(feature, token
                .getLine()
                + "", token.getPos() + "").toString());
    }
}
