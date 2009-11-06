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

import org.sablecc.exception.*;
import org.sablecc.sablecc.errormessage.*;
import org.sablecc.sablecc.structure.*;
import org.sablecc.sablecc.syntax3.node.*;

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
            Token duplicateName,
            Token firstName) {

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
            TIdentifier token) {

        return new CompilerException(new MInvalidReference(token.getText(),
                token.getLine() + "", token.getPos() + "").toString());
    }

    public static CompilerException cyclicReference(
            TIdentifier reference,
            TIdentifier context) {

        return new CompilerException(new MCyclicReference(reference.getText(),
                reference.getLine() + "", reference.getPos() + "", context
                        .getText(), context.getLine() + "", context.getPos()
                        + "").toString());
    }

    public static CompilerException invalidPriority(
            TGt gt,
            MatchedToken matchedToken) {

        return new CompilerException(new MInvalidPriority(gt.getLine() + "", gt
                .getPos()
                + "", matchedToken.getName()).toString());
    }

    public static CompilerException conflictingPriorities(
            TGt gt,
            MatchedToken high,
            MatchedToken low,
            TGt prior_gt) {

        return new CompilerException(new MConflictingPriorities(gt.getLine()
                + "", gt.getPos() + "", high.getName(), low.getName(), prior_gt
                .getLine()
                + "", prior_gt.getPos() + "").toString());
    }

    public static CompilerException notAToken(
            Token token) {

        return new CompilerException(new MNotAToken(token.getLine() + "", token
                .getPos()
                + "", token.getText()).toString());
    }

    public static CompilerException lexerConflict(
            MatchedToken matchedToken1,
            MatchedToken matchedToken2) {

        return new CompilerException(new MLexerConflict(
                matchedToken1.getName(), matchedToken2.getName()).toString());
    }

    public static CompilerException parserUselessProduction(
            String name) {

        return new CompilerException(new MParserUselessProduction(name)
                .toString());
    }

    public static CompilerException parserSpuriousPriority(
            TIdentifier identifier) {

        return new CompilerException(
                new MParserSpuriousPriority(identifier.getText(), ""
                        + identifier.getLine(), "" + identifier.getPos())
                        .toString());
    }

    public static CompilerException alternativeNotRecursive(
            TIdentifier identifier) {

        return new CompilerException(
                new MAlternativeNotRecursive(identifier.getText(), ""
                        + identifier.getLine(), "" + identifier.getPos())
                        .toString());
    }

    public static CompilerException recursionNotFollowedByToken(
            TIdentifier identifier) {

        return new CompilerException(new MRecursionNotFollowedByToken(
                identifier.getText(), "" + identifier.getLine(), ""
                        + identifier.getPos()).toString());
    }

    public static CompilerException notImplemented(
            Token token,
            String feature) {

        return new CompilerException(new MNotImplemented(feature, token
                .getLine()
                + "", token.getPos() + "").toString());
    }

    public static CompilerException outputError(
            String fileName,
            Throwable cause) {

        return new CompilerException(new MOutputError(fileName, cause
                .getMessage()).toString(), cause);
    }

}
