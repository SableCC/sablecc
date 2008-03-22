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

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.InvalidArgumentException;
import org.sablecc.sablecc.launcher.syntax3.lexer.Lexer;
import org.sablecc.sablecc.launcher.syntax3.node.Start;
import org.sablecc.sablecc.launcher.syntax3.parser.Parser;

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
            String[] arguments)
            throws InvalidArgumentException {

        List<OptionArgument> optionArguments = new LinkedList<OptionArgument>();

        List<TextArgument> textArguments = new LinkedList<TextArgument>();

        int currentArgIndex = 0;

        // process options and text arguments, until a double hyphen is found
        while (currentArgIndex < arguments.length) {

            if (arguments[currentArgIndex] == null) {
                throw new InternalException("argument may not be null");
            }

            if (arguments[currentArgIndex].equals("")) {
                throw new InvalidArgumentException("argument may not be empty");
            }

            if (arguments[currentArgIndex].equals("--")) {
                currentArgIndex++;
                break;
            }

            try {
                Start ast = new Parser(new Lexer(new PushbackReader(
                        new StringReader(arguments[currentArgIndex]), 1024)))
                        .parse();

                Option incompleteOption = ArgumentExtractor.extractArguments(
                        ast, optionArguments, textArguments);

                if (incompleteOption != null) {

                    if (currentArgIndex + 1 >= arguments.length) {

                        if (arguments[currentArgIndex].startsWith("--")) {
                            throw new InvalidArgumentException("option --"
                                    + incompleteOption.getLongName()
                                    + " is missing a "
                                    + incompleteOption.getOperandName()
                                    + " operand");
                        }
                        else {
                            throw new InvalidArgumentException("option -"
                                    + incompleteOption.getShortName()
                                    + " is missing a "
                                    + incompleteOption.getOperandName()
                                    + " operand");
                        }
                    }

                    currentArgIndex++;

                    if (arguments[currentArgIndex] == null) {
                        throw new InternalException("argument may not be null");
                    }

                    if (arguments[currentArgIndex].equals("")) {
                        throw new InvalidArgumentException(
                                "argument may not be empty");
                    }

                    optionArguments.add(new OptionArgument(incompleteOption,
                            arguments[currentArgIndex]));
                }
            }
            catch (InvalidArgumentException e) {
                throw new InvalidArgumentException("invalid argument \""
                        + arguments[currentArgIndex] + "\": " + e.getMessage(),
                        e);
            }
            catch (Exception e) {
                throw new InvalidArgumentException("invalid argument \""
                        + arguments[currentArgIndex] + "\"", e);
            }

            currentArgIndex++;
        }

        // process remaining text arguments
        while (currentArgIndex < arguments.length) {

            if (arguments[currentArgIndex] == null) {
                throw new InternalException("argument may not be null");
            }

            if (arguments[currentArgIndex].equals("")) {
                throw new InvalidArgumentException("argument may not be empty");
            }

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
