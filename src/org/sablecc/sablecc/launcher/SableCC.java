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

import static org.sablecc.sablecc.launcher.Version.VERSION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.LinkedList;
import java.util.List;

import org.sablecc.sablecc.exception.ExitException;
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.InvalidArgumentException;
import org.sablecc.sablecc.exception.SemanticException;
import org.sablecc.sablecc.syntax3.lexer.Lexer;
import org.sablecc.sablecc.syntax3.lexer.LexerException;
import org.sablecc.sablecc.syntax3.node.Start;
import org.sablecc.sablecc.syntax3.parser.Parser;
import org.sablecc.sablecc.syntax3.parser.ParserException;
import org.sablecc.sablecc.util.Verbosity;

/**
 * The main class of SableCC.
 */
public class SableCC {

    /** Prevents instanciation of this class. */
    private SableCC() {

        throw new InternalException("this class may not have instances");
    }

    /** Launches SableCC. */
    public static void main(
            String[] args) {

        try {
            compile(args);
        }
        catch (ExitException e) {
            System.exit(1);
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
        catch (InternalException e) {
            e.printStackTrace(System.err);
            System.err.println("INTERNAL ERROR: " + e.getMessage());
            System.err
                    .println("Please submit a defect ticket with the full error trace above on:");
            System.err.println("    http://sablecc.org/");
            System.exit(1);
        }
        catch (Error e) {
            e.printStackTrace(System.err);
            System.err.println("INTERNAL ERROR: (" + e.getClass().getName()
                    + ") " + e.getMessage());
            System.err
                    .println("Please submit a defect ticket with the full error trace above on:");
            System.err.println("    http://sablecc.org/");
            System.exit(1);
        }

        // finish gracefully
        System.exit(0);
    }

    /**
     * Parses the provided arguments and launches grammar compilation.
     */
    public static void compile(
            String[] arguments)
            throws InvalidArgumentException, ParserException, LexerException,
            SemanticException {

        // default destination directory is current working directory
        File destinationDirectory = new File(System.getProperty("user.dir"))
                .getAbsoluteFile();

        // default option values
        boolean check_only = false;
        boolean strict = false;
        Verbosity verbosity = Verbosity.NORMAL;

        // parse command line arguments
        ArgumentCollection argumentCollection = new ArgumentCollection(
                arguments);

        // handle option arguments
        for (OptionArgument optionArgument : argumentCollection
                .getOptionArguments()) {

            switch (optionArgument.getOption()) {

            case DESTINATION:
                destinationDirectory = new File(optionArgument.getOperand());

                if (!destinationDirectory.exists()) {
                    throw new InvalidArgumentException(destinationDirectory
                            + " does not exist");
                }

                if (!destinationDirectory.isDirectory()) {
                    throw new InvalidArgumentException(destinationDirectory
                            + " is not a directory");
                }

                break;

            case CHECK_ONLY:
                check_only = true;
                break;

            case STRICT:
                strict = true;
                break;

            case QUIET:
                verbosity = Verbosity.QUIET;
                break;

            case VERBOSE:
                verbosity = Verbosity.VERBOSE;
                break;

            case VERSION:
                System.out.println("SableCC " + VERSION);
                return;

            case HELP:
                System.out
                        .println("usage: sablecc "
                                + Option.getShortHelpMessage()
                                + " grammar.sablecc ...");
                System.out.println("options:");
                System.out.println(Option.getLongHelpMessage());
                return;

            default:
                throw new InternalException("unhandled option "
                        + optionArgument.getOption());
            }
        }

        // handle text arguments
        if (argumentCollection.getTextArguments().size() == 0) {
            System.err.println("usage: sablecc " + Option.getShortHelpMessage()
                    + " grammar.sablecc ...");
            System.err.println("type 'sablecc -h' for more information");

            throw new ExitException();
        }

        switch (verbosity) {
        case NORMAL:
        case VERBOSE:
            System.out.println();
            System.out.println("SableCC version " + VERSION);
            System.out
                    .println("by Etienne M. Gagnon <egagnon@j-meg.com> and other contributors.");
            System.out.println();
        }

        List<File> grammarFiles = new LinkedList<File>();

        for (TextArgument textArgument : argumentCollection.getTextArguments()) {

            if (!textArgument.getText().endsWith(".sablecc")) {
                throw new InvalidArgumentException(
                        "grammar file name does not end with .sablecc: "
                                + textArgument.getText());
            }

            File grammarFile = new File(textArgument.getText());

            if (!grammarFile.exists()) {
                throw new InvalidArgumentException(grammarFile
                        + " does not exist");
            }

            if (!grammarFile.isFile()) {
                throw new InvalidArgumentException(grammarFile
                        + " is not a file");
            }

            grammarFiles.add(grammarFile);
        }

        // compile grammars
        for (File grammarFile : grammarFiles) {

            compile(grammarFile, destinationDirectory, check_only, strict,
                    verbosity);
        }
    }

    /**
     * Compiles the provided grammar file.
     */
    private static void compile(
            File grammarFile,
            File destinationDirectory,
            boolean check_only,
            boolean strict,
            Verbosity verbosity)
            throws InvalidArgumentException, ParserException, LexerException,
            SemanticException {

        switch (verbosity) {
        case NORMAL:
        case VERBOSE:
            System.out.println("Compiling " + grammarFile);
        }

        @SuppressWarnings("unused")
        Start ast;

        try {
            FileReader fr = new FileReader(grammarFile);
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
            throw new InvalidArgumentException("cannot read " + grammarFile, e);
        }

        throw new InternalException("unimplemented");
    }
}
