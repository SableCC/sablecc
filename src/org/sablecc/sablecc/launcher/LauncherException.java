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

package org.sablecc.sablecc.launcher;

import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.launcher.errormessage.*;

public class LauncherException
        extends CompilerException {

    private LauncherException(
            String message) {

        super(message);
    }

    public static LauncherException inputError(
            String fileName,
            Throwable cause) {

        return new LauncherException(new MInputError(fileName,
                cause.getMessage()).toString());
    }

    public static LauncherException unknownTarget(
            String targetLanguage) {

        return new LauncherException(
                new MUnknownTarget(targetLanguage).toString());
    }

    public static LauncherException invalidDesinationDirectory(
            String destination) {

        return new LauncherException(new MInvalidDesinationDirectory(
                destination).toString());
    }

    public static LauncherException invalidArgumentCount() {

        return new LauncherException(new MInvalidArgumentCount().toString());
    }

    public static LauncherException invalidSuffix(
            String fileName) {

        return new LauncherException(new MInvalidSuffix(fileName).toString());
    }

    public static LauncherException missingGrammarFile(
            String fileName) {

        return new LauncherException(
                new MMissingGrammarFile(fileName).toString());
    }

    public static LauncherException grammarNotFile(
            String fileName) {

        return new LauncherException(new MGrammarNotFile(fileName).toString());
    }

    public static LauncherException invalidArgument(
            String argument_text) {

        return new LauncherException(
                new MInvalidArgument(argument_text).toString());
    }

    public static LauncherException missingLongOptionOperand(
            String optionName,
            String operandName) {

        return new LauncherException(new MMissingLongOptionOperand(optionName,
                operandName).toString());
    }

    public static LauncherException missingShortOptionOperand(
            String optionName,
            String operandName) {

        return new LauncherException(new MMissingShortOptionOperand(optionName,
                operandName).toString());
    }

    public static LauncherException invalidLongOption(
            String optionName) {

        return new LauncherException(
                new MInvalidLongOption(optionName).toString());
    }

    public static LauncherException spuriousLongOptionOperand(
            String optionName,
            String operand_text) {

        return new LauncherException(new MSpuriousLongOptionOperand(optionName,
                operand_text).toString());
    }

    public static LauncherException invalidShortOption(
            String optionName) {

        return new LauncherException(
                new MInvalidShortOption(optionName).toString());
    }

    public static LauncherException spuriousShortOptionOperand(
            String optionName,
            String operand_text) {

        return new LauncherException(new MSpuriousShortOptionOperand(
                optionName, operand_text).toString());
    }

}
