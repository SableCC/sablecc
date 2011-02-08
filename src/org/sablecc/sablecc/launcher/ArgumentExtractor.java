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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.launcher.syntax3.analysis.*;
import org.sablecc.sablecc.launcher.syntax3.node.*;

/**
 * An argument extractor is an AST walker that extracts information from a
 * command-line argument.
 */
class ArgumentExtractor
        extends DepthFirstAdapter {

    /** The provided list of option arguments. */
    private final List<OptionArgument> optionArguments;

    /** The provided list of text arguments. */
    private final List<TextArgument> textArguments;

    /** The found incomplete option, or <code>null</code>. */
    private Option incompleteOption;

    /**
     * Constructs an argument extractor.
     */
    private ArgumentExtractor(
            final List<OptionArgument> optionArguments,
            final List<TextArgument> textArguments) {

        if (optionArguments == null) {
            throw new InternalException("optionArguments may not be null");
        }

        if (textArguments == null) {
            throw new InternalException("textArguments may not be null");
        }

        this.optionArguments = optionArguments;
        this.textArguments = textArguments;
    }

    /**
     * Adds the plain argument to the list of text arguments.
     */
    @Override
    public void caseAPlainArgument(
            APlainArgument node) {

        this.textArguments.add(new TextArgument(node.getText().getText()));
    }

    /**
     * Adds the long option to the list of option arguments, or stores it as an
     * incompete option.
     */
    @Override
    public void caseALongOption(
            ALongOption node) {

        String longName = node.getLongName().getText();

        // make sure option is valid
        Option option = Option.getLongOption(longName);

        if (option == null) {
            throw LauncherException.invalidLongOption(longName);
        }

        // expects an operand?
        if (option.hasOperand()) {
            // yes

            AOperand operand = (AOperand) node.getOperand();

            // is it there?
            if (operand != null) {
                // yes

                if (operand.getOperandText() != null) {
                    this.optionArguments.add(new OptionArgument(option, operand
                            .getOperandText().getText()));
                }
                else {
                    this.optionArguments.add(new OptionArgument(option, ""));
                }
            }
            else {
                // no, we have an incomplete option
                this.incompleteOption = option;
            }
        }
        else {
            // no

            if (node.getOperand() != null) {
                AOperand operand = (AOperand) node.getOperand();

                throw LauncherException.spuriousLongOptionOperand(longName,
                        operand.getOperandText().getText());
            }

            this.optionArguments.add(new OptionArgument(option, null));
        }
    }

    /**
     * Adds the short option to the list of option arguments, or stores it as an
     * incompete option.
     */
    @Override
    public void caseAShortOption(
            AShortOption node) {

        if (this.incompleteOption != null) {
            throw LauncherException.missingShortOptionOperand(
                    this.incompleteOption.getShortName(),
                    this.incompleteOption.getOperandName());
        }

        String shortName = node.getShortName().getText();

        // make sure option is valid
        Option option = Option.getShortOption(shortName);

        if (option == null) {
            throw LauncherException.invalidShortOption(shortName);
        }

        // expects an operand?
        if (option.hasOperand()) {
            // yes

            AOperand operand = (AOperand) node.getOperand();

            // is it there?
            if (operand != null) {
                // yes

                if (operand.getOperandText() != null) {
                    this.optionArguments.add(new OptionArgument(option, operand
                            .getOperandText().getText()));
                }
                else {
                    this.optionArguments.add(new OptionArgument(option, ""));
                }
            }
            else {
                // no, we have an incomplete option
                this.incompleteOption = option;
            }
        }
        else {
            // no

            if (node.getOperand() != null) {
                AOperand operand = (AOperand) node.getOperand();

                throw LauncherException.spuriousShortOptionOperand(shortName,
                        operand.getOperandText().getText());
            }

            this.optionArguments.add(new OptionArgument(option, null));
        }

    }

    /**
     * Extracts option and arguments from the provided AST and adds them to the
     * provided lists, and returns an incomplete option, when there is one.
     */
    static Option extractArguments(
            Start ast,
            List<OptionArgument> optionArguments,
            List<TextArgument> textArguments) {

        ArgumentExtractor extractor = new ArgumentExtractor(optionArguments,
                textArguments);

        ast.apply(extractor);

        return extractor.incompleteOption;
    }
}
