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
import java.util.SortedMap;
import java.util.TreeMap;

import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.launcher.syntax3.lexer.Lexer;
import org.sablecc.sablecc.launcher.syntax3.node.ALongOption;
import org.sablecc.sablecc.launcher.syntax3.node.ALongOptionArgument;
import org.sablecc.sablecc.launcher.syntax3.node.AShortOption;
import org.sablecc.sablecc.launcher.syntax3.node.AShortOptionsArgument;
import org.sablecc.sablecc.launcher.syntax3.node.Start;
import org.sablecc.sablecc.launcher.syntax3.parser.Parser;

/**
 * TODO
 * 
 */
public enum Option {

    /** Does not generate files. */
    CHECK_ONLY("c", "check-only", null, "do not generate files"),
    /** Change the destination directory. */
    DESTINATION("d", "destination", "directory", "set destination directory"),
    /** Does not report errors unless they are critical. */
    LENIENT("l", "lenient", null, "do not report non-fatal errors (implies -c)"),
    /** Display less information. */
    QUIET("q", "quiet", null, "be as quiet as possible"),
    /** Display more information. */
    VERBOSE("v", "verbose", null, "display as much information as possible"),
    /** Display the version and exit. */
    VERSION(null, "version", null, "display version information and exit"),
    /** Display an information message and exit. */
    HELP("h", "help", null, "display help information and exit");

    /** The short name of this option. */
    private String shortName;

    /** The long name of this option. */
    private String longName;

    /** The operand name of this option. */
    private String operandName;

    /** The help message of this option. */
    private String helpMessage;

    /** The short name map of the options. */
    private static final SortedMap<String, Option> shortNameMap = new TreeMap<String, Option>();

    /** The long name map of the options. */
    private static final SortedMap<String, Option> longNameMap = new TreeMap<String, Option>();

    /**
     * Compiles the list of options.
     * 
     * @throws InternalException
     *             if two options have the same long or short name.
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
     * Construct a new option.
     * 
     * @param shortName
     *            the short name of the option.
     * @param longName
     *            the long name of the option.
     * @param operandName
     *            the operand name of the option.
     * @param helpMessage
     *            the help message of the option.
     * 
     * @throws InternalException
     *             if the short name and the long name are <code>null</code>,
     *             if the help message is <code>null</code> or if the long
     *             name too short.
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
     * Validates the short name.
     * 
     * @param shortName
     *            the short name.
     * 
     * @throws InternalException
     *             if the short name is invalid.
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
     * Validates the long name.
     * 
     * @param longName
     *            the long name.
     * 
     * @throws InternalException
     *             if the long name is invalid.
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
     * Returns the short name of this option.
     * 
     * @return the short name.
     */
    public String getShortName() {

        return this.shortName;
    }

    /**
     * Returns the short name of this option.
     * 
     * @return the short name.
     */
    public String getLongName() {

        return this.longName;
    }

    /**
     * Returns the operand name of this option.
     * 
     * @return the operand name.
     */
    public String getOperandName() {

        return this.operandName;
    }

    /**
     * Returns the help message of this option.
     * 
     * @return the help message.
     */
    public String getHelpMessage() {

        return this.helpMessage;
    }

    /**
     * Returns whether this option has an operand or not.
     * 
     * @return <code>true</code> if this option has an operand;
     *         <code>false</code> otherwise.
     */
    public boolean hasOperand() {

        return this.operandName != null;
    }

    /**
     * Returns the option having the provided short name.
     * 
     * @return the short name.
     */
    public static Option getShortOption(
            String shortName) {

        return shortNameMap.get(shortName);
    }

    /**
     * Returns the option having the provided long name.
     * 
     * @return the long name.
     */
    public static Option getLongOption(
            String longName) {

        return longNameMap.get(longName);
    }

    /**
     * Prints a short help message.
     * 
     * @return the help message.
     */
    public static String getShortHelpMessage() {

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
     * Prints a long help message.
     * 
     * @return the help message.
     */
    public static String getLongHelpMessage() {

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
