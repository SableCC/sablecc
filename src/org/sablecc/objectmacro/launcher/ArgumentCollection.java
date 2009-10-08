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

package org.sablecc.objectmacro.launcher;

import java.io.*;
import java.util.*;

import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.launcher.syntax3.lexer.*;
import org.sablecc.objectmacro.launcher.syntax3.node.*;
import org.sablecc.objectmacro.launcher.syntax3.parser.*;

/**
 * Encapsulates a collection of option and text command-line arguments.
 */
class ArgumentCollection {

    /** The list of option arguments. */
    private final List<OptionArgument> optionArguments;

    /** The list of text arguments. */
    private final List<TextArgument> textArguments;

    /**
     * Constructs an argument collection instance. It parses the provided array
     * and extracts options and their operand, as well as text arguments.
     */
    ArgumentCollection(
            String[] arguments) {

        List<OptionArgument> optionArguments = new LinkedList<OptionArgument>();

        List<TextArgument> textArguments = new LinkedList<TextArgument>();

        int currentArgIndex = 0;

        // process options and text arguments, until a double hyphen is found
        while (currentArgIndex < arguments.length) {

            if (arguments[currentArgIndex].equals("")) {
                textArguments.add(new TextArgument(arguments[currentArgIndex]));
                currentArgIndex++;
                continue;
            }

            if (arguments[currentArgIndex].equals("--")) {
                currentArgIndex++;
                break;
            }

            Start ast;
            try {
                ast = new Parser(new Lexer(new PushbackReader(new StringReader(
                        arguments[currentArgIndex]), 1024))).parse();
            }
            catch (Exception e) {
                throw CompilerException.invalidArgument(
                        arguments[currentArgIndex], e);
            }

            Option incompleteOption = ArgumentExtractor.extractArguments(ast,
                    optionArguments, textArguments);

            if (incompleteOption != null) {

                if (currentArgIndex + 1 >= arguments.length) {

                    if (arguments[currentArgIndex].startsWith("--")) {
                        throw CompilerException.missingLongOptionOperand(
                                incompleteOption.getLongName(),
                                incompleteOption.getOperandName());
                    }
                    else {
                        throw CompilerException.missingShortOptionOperand(
                                incompleteOption.getShortName(),
                                incompleteOption.getOperandName());
                    }
                }

                currentArgIndex++;

                optionArguments.add(new OptionArgument(incompleteOption,
                        arguments[currentArgIndex]));
            }

            currentArgIndex++;
        }

        // process remaining text arguments
        while (currentArgIndex < arguments.length) {

            textArguments.add(new TextArgument(arguments[currentArgIndex]));

            currentArgIndex++;
        }

        this.optionArguments = Collections.unmodifiableList(optionArguments);
        this.textArguments = Collections.unmodifiableList(textArguments);
    }

    /**
     * Returns the list of option arguments.
     */
    List<OptionArgument> getOptionArguments() {

        return this.optionArguments;
    }

    /**
     * Returns the list of text arguments.
     */
    List<TextArgument> getTextArguments() {

        return this.textArguments;
    }

}
