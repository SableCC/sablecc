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

import org.sablecc.exception.*;
import org.sablecc.objectmacro.launcher.syntax3.lexer.*;
import org.sablecc.objectmacro.launcher.syntax3.node.*;
import org.sablecc.objectmacro.launcher.syntax3.parser.*;

/**
 * The Option enum encapsulates command-line options. This enum provides short
 * and long command-line help messages, and is used by the ArgumentCollection
 * class to parse command-line arguments.
 */
enum Option {

    /** List available target languages and exit. */
    LIST_TARGETS(null, "list-targets", null,
            "display target languages and exit"),

    /** Set target language. */
    TARGET("t", "target", "language", "set target language (default=java)"),

    /** Set destination directory. */
    DESTINATION("d", "destination", "directory", "set destination directory"),

    /** Set destination package. */
    PACKAGE("p", "package", "packagename", "set destination package"),

    /** Do not generate files. */
    GENERATE("g", "generate-code", null, "generate code (default)"),

    /** Do not generate files. */
    NO_CODE("n", "no-code", null, "do not generate code"),

    /** Ignore unused constructs. */
    LENIENT("l", "lenient", null, "ignore unused constructs"),

    /** Detect unused constructs. */
    STRICT("s", "strict", null, "detect unused constructs (default)"),

    /** Only display errors. */
    QUIET("q", "quiet", null, "only display errors"),

    /** Display progress. */
    INFORMATIVE("i", "informative", null, "display progress (default)"),

    /** Display detailed progress. */
    VERBOSE("v", "verbose", null, "display detailed progress"),

    /** Display version and exit. */
    VERSION(null, "version", null, "display version and exit"),

    /** Display help and exit. */
    HELP("h", "help", null, "display help and exit");

    /** The short name. */
    private String shortName;

    /** The long name. */
    private String longName;

    /** The operand name. */
    private String operandName;

    /** The help message. */
    private String helpMessage;

    /** A mapping from each short name to its option. */
    private static final SortedMap<String, Option> shortNameMap = new TreeMap<String, Option>();

    /** A mapping from each long name to its option. */
    private static final SortedMap<String, Option> longNameMap = new TreeMap<String, Option>();

    /**
     * Checks that there are no duplicate option names.
     */
    static {
        for (Option option : Option.values()) {

            if (option.shortName != null) {

                if (shortNameMap.containsKey(option.shortName)) {
                    throw new InternalException("duplicate short name: "
                            + option.shortName);
                }

                shortNameMap.put(option.shortName, option);
            }

            if (option.longName != null) {

                if (longNameMap.containsKey(option.longName)) {
                    throw new InternalException("duplicate long name: "
                            + option.longName);
                }

                longNameMap.put(option.longName, option);
            }
        }
    }

    /**
     * Constructs an option.
     */
    private Option(
            String shortName,
            String longName,
            String operandName,
            String helpMessage) {

        if (shortName == null && longName == null) {
            throw new InternalException(
                    "at least one of shortName and longName must not be null");
        }

        if (shortName != null) {
            validateShortName(shortName);
        }

        if (longName != null) {

            if (longName.length() < 2) {
                throw new InternalException(
                        "longName must be at least two characters long");
            }

            validateLongName(longName);
        }

        if (helpMessage == null) {
            throw new InternalException("helpMessage may not be null");
        }

        this.shortName = shortName;
        this.longName = longName;
        this.operandName = operandName;
        this.helpMessage = helpMessage;
    }

    /**
     * Validates the provided short name.
     */
    private void validateShortName(
            String shortName) {

        // validate by parsing "-name"
        String argument = "-" + shortName;

        try {
            Start ast = new Parser(new Lexer(new PushbackReader(
                    new StringReader(argument), 1024))).parse();

            // argument should be short options
            AShortOptionsArgument shortOptionsArgument = (AShortOptionsArgument) ast
                    .getPArgument();

            // single
            if (shortOptionsArgument.getShortOptions().size() != 1) {
                throw new InternalException("invalid shortName");
            }

            AShortOption shortOption = (AShortOption) shortOptionsArgument
                    .getShortOptions().getFirst();

            // no operand
            if (shortOption.getOperand() != null) {
                throw new InternalException("invalid shortName");
            }
        }
        catch (Exception e) {
            throw new InternalException("invalid shortName", e);
        }
    }

    /**
     * Validates the provided long name.
     */
    private void validateLongName(
            String longName) {

        // validate by parsing "--name"
        String argument = "--" + longName;

        try {
            Start ast = new Parser(new Lexer(new PushbackReader(
                    new StringReader(argument), 1024))).parse();

            // argument should be long option
            ALongOptionArgument longOptionArgument = (ALongOptionArgument) ast
                    .getPArgument();

            ALongOption longOption = (ALongOption) longOptionArgument
                    .getLongOption();

            // no operand
            if (longOption.getOperand() != null) {
                throw new InternalException("invalid longName");
            }
        }
        catch (Exception e) {
            throw new InternalException("invalid longName", e);
        }
    }

    /**
     * Returns the short name, or <code>null</code>.
     */
    String getShortName() {

        return this.shortName;
    }

    /**
     * Returns the long name, or <code>null</code>.
     */
    String getLongName() {

        return this.longName;
    }

    /**
     * Returns the operand name, or <code>null</code>.
     */
    String getOperandName() {

        return this.operandName;
    }

    /**
     * Returns the help message.
     */
    String getHelpMessage() {

        return this.helpMessage;
    }

    /**
     * Returns <code>true</code> when this option has an operand.
     */
    boolean hasOperand() {

        return this.operandName != null;
    }

    /**
     * Returns the option that has the provided short name, or <code>null</code>
     * .
     */
    static Option getShortOption(
            String shortName) {

        return shortNameMap.get(shortName);
    }

    /**
     * Returns the option that has the provided long name, or <code>null</code>.
     */
    static Option getLongOption(
            String longName) {

        return longNameMap.get(longName);
    }

    /**
     * Returns a short help message, listing all options.
     */
    static String getShortHelpMessage() {

        StringBuilder sb = new StringBuilder();

        boolean first = true;

        {
            boolean hasShortOptions = false;
            for (Option option : Option.values()) {
                if (option.shortName != null && option.operandName == null) {

                    if (!hasShortOptions) {

                        sb.append("[-");
                        hasShortOptions = true;
                    }

                    sb.append(option.shortName);
                }
            }

            if (hasShortOptions) {
                sb.append("]");
                first = false;
            }
        }

        for (Option option : Option.values()) {
            if (option.shortName == null || option.operandName != null) {

                if (first) {
                    first = false;
                }
                else {
                    sb.append(" ");
                }

                if (option.shortName != null) {
                    sb.append("[-");
                    sb.append(option.shortName);
                }
                else {
                    sb.append("[--");
                    sb.append(option.longName);
                }

                if (option.operandName != null) {
                    sb.append(" ");
                    sb.append(option.operandName);
                }

                sb.append("]");
            }
        }

        return sb.toString();
    }

    /**
     * Returns a long help message, listing all options.
     */
    static String getLongHelpMessage() {

        int longestPrefixLength = 0;

        for (Option option : Option.values()) {

            int prefixLength = 0;

            if (option.shortName != null) {
                prefixLength += 2 + option.shortName.length();

                if (option.operandName != null) {
                    prefixLength += 1 + option.operandName.length();
                }
            }

            if (option.longName != null) {

                if (option.shortName != null) {
                    prefixLength += 4;
                }
                else {
                    prefixLength += 3;
                }

                prefixLength += option.longName.length();

                if (option.operandName != null) {
                    prefixLength += 1 + option.operandName.length();
                }
            }

            if (prefixLength > longestPrefixLength) {
                longestPrefixLength = prefixLength;
            }
        }

        String lineSeparator = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Option option : Option.values()) {

            StringBuilder line = new StringBuilder();

            if (option.shortName != null) {
                line.append(" -");
                line.append(option.shortName);

                if (option.operandName != null) {
                    line.append(" ");
                    line.append(option.operandName);
                }
            }

            if (option.longName != null) {

                if (option.shortName != null) {
                    line.append(", --");
                }
                else {
                    line.append(" --");
                }

                line.append(option.longName);

                if (option.operandName != null) {
                    line.append("=");
                    line.append(option.operandName);
                }
            }

            for (int i = line.toString().length(); i < longestPrefixLength; i++) {
                line.append(" ");
            }

            line.append(" : ");
            line.append(option.helpMessage);

            if (first) {
                first = false;
            }
            else {
                sb.append(lineSeparator);
            }

            sb.append(line);
        }

        return sb.toString();
    }
}
