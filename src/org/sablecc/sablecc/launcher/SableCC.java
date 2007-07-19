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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.LinkedList;
import java.util.List;

import org.sablecc.sablecc.GlobalInformation;
import org.sablecc.sablecc.Semantics;
import org.sablecc.sablecc.Verbosity;
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.InvalidArgumentException;
import org.sablecc.sablecc.exception.SemanticException;
import org.sablecc.sablecc.syntax3.lexer.Lexer;
import org.sablecc.sablecc.syntax3.lexer.LexerException;
import org.sablecc.sablecc.syntax3.node.Start;
import org.sablecc.sablecc.syntax3.parser.Parser;
import org.sablecc.sablecc.syntax3.parser.ParserException;

public class SableCC {

    public static void main(
            String[] args) {

        try {
            boolean check_only = false;
            Verbosity verbosity = Verbosity.NORMAL;

            // default destination directory is current working directory
            File destinationDirectory = new File(System.getProperty("user.dir"))
                    .getAbsoluteFile();

            // parse command line arguments
            Arguments arguments = new Arguments(args);

            // handle option arguments
            for (OptionArgument optionArgument : arguments.getOptionArguments()) {

                switch (optionArgument.getOption()) {

                case CHECK_ONLY:
                    check_only = true;
                    break;

                case DESTINATION:
                    destinationDirectory = new File(optionArgument.getOperand())
                            .getAbsoluteFile();

                    if (!destinationDirectory.isDirectory()) {

                        if (!destinationDirectory.exists()) {
                            System.err.println("ERROR: " + destinationDirectory
                                    + " does not exist");
                            System.exit(1);
                        }

                        System.err.println("ERROR: " + destinationDirectory
                                + " is not a directory");
                        System.exit(1);
                    }

                    break;

                case QUIET:
                    verbosity = Verbosity.QUIET;
                    break;

                case VERBOSE:
                    verbosity = Verbosity.VERBOSE;
                    break;

                case VERSION:
                    System.out.println("SableCC version " + Version.VERSION);
                    System.exit(0);

                case HELP:
                    System.out.println("usage: sablecc "
                            + Option.getShortHelpMessage()
                            + " specification.sablecc ...");
                    System.out.println("options:");
                    System.out.println(Option.getLongHelpMessage());
                    System.exit(0);

                default:
                    throw new InternalException("unhandled option "
                            + optionArgument.getOption());
                }
            }

            // handle text arguments
            if (arguments.getTextArguments().size() == 0) {
                System.err.println("usage: sablecc "
                        + Option.getShortHelpMessage()
                        + " specification.sablecc ...");
                System.err.println("type 'sablecc -h' for more information.");
                System.exit(1);
            }

            switch (verbosity) {
            case NORMAL:
            case VERBOSE:
                System.out.println();
                System.out.println("SableCC version " + Version.VERSION);
                System.out
                        .println("by Etienne M. Gagnon <egagnon@j-meg.com> and contributors");
                System.out.println();
            }

            List<File> specificationFiles = new LinkedList<File>();

            // remember absolute paths, just in case the current working
            // directory gets changed during specification compilation.
            for (TextArgument textArgument : arguments.getTextArguments()) {

                if (!textArgument.getText().endsWith(".sablecc")) {
                    System.err
                            .println("ERROR: specification file name does not end with .sablecc: "
                                    + textArgument.getText());
                    System.exit(1);
                }

                File specificationFile = new File(textArgument.getText())
                        .getAbsoluteFile();

                if (!specificationFile.isFile()) {

                    if (!specificationFile.exists()) {
                        System.err.println("ERROR: " + specificationFile
                                + " does not exist");
                        System.exit(1);
                    }

                    System.err.println("ERROR: " + specificationFile
                            + " is not a file");
                    System.exit(1);
                }

                specificationFiles.add(specificationFile);
            }

            // compile specifications
            for (File specificationFile : specificationFiles) {

                compile(specificationFile, destinationDirectory, check_only,
                        verbosity);
            }
        }
        catch (InvalidArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }
        catch (SemanticException e) {
            System.err.println("ERROR: semantic error on '"
                    + e.getToken().getText() + "' at " + e.getMessage());
        }
        catch (ParserException e) {
            System.err.println("ERROR: syntax error on '"
                    + e.getToken().getText() + "' at " + e.getMessage());
            System.exit(1);
        }
        catch (LexerException e) {
            System.err.println("ERROR: lexical error at " + e.getMessage());
            System.exit(1);
        }
        catch (RuntimeException e) {
            e.printStackTrace(System.err);
            System.err.println("INTERNAL ERROR: " + e.getMessage());
            System.err
                    .println("Please submit a defect ticket with the full error trace above on:");
            System.err.println("    http://sablecc.org/");
            System.exit(1);
        }
        catch (Error e) {
            e.printStackTrace(System.err);
            System.err.println("INTERNAL ERROR: " + e.getMessage());
            System.err
                    .println("Please submit a defect ticket with the full error trace above on:");
            System.err.println("    http://sablecc.org/");
            System.exit(1);
        }

        // finish gracefully
        System.exit(0);
    }

    private static void compile(
            File specificationFile,
            File destinationDirectory,
            boolean check_only,
            Verbosity verbosity)
            throws InvalidArgumentException, ParserException, LexerException,
            SemanticException {

        switch (verbosity) {
        case NORMAL:
        case VERBOSE:
            System.out.println("Compiling " + specificationFile);
        }

        Start ast;

        try {
            FileReader fr = new FileReader(specificationFile);
            BufferedReader br = new BufferedReader(fr);
            PushbackReader pbr = new PushbackReader(br);

            switch (verbosity) {
            case VERBOSE:
                System.out.println(" Parsing");
            }

            ast = new Parser(new Lexer(pbr)).parse();

            pbr.close();
            br.close();
            fr.close();
        }
        catch (IOException e) {
            throw new InvalidArgumentException("cannot read "
                    + specificationFile, e);
        }

        GlobalInformation globalInformation = new GlobalInformation(verbosity,
                ast);
        new Semantics(globalInformation);

        System.err.println("ERROR: unimplemented");
        System.exit(1);
    }
}
