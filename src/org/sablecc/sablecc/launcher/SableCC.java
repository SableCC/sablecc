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
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringWriter;

import org.sablecc.sablecc.errormessages.M_internal_error;
import org.sablecc.sablecc.errormessages.M_lexical_error;
import org.sablecc.sablecc.errormessages.M_syntax_error;
import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.structures.GlobalData;
import org.sablecc.sablecc.syntax3.lexer.Lexer;
import org.sablecc.sablecc.syntax3.lexer.LexerException;
import org.sablecc.sablecc.syntax3.node.Start;
import org.sablecc.sablecc.syntax3.parser.Parser;
import org.sablecc.sablecc.syntax3.parser.ParserException;
import org.sablecc.sablecc.util.Strictness;
import org.sablecc.sablecc.util.Verbosity;
import org.sablecc.sablecc.walkers.DeclarationFinder;

/**
 * The main class of SableCC.
 */
public class SableCC {

    /** Prevents instantiation of this class. */
    private SableCC() {

        throw new InternalException("this class may not have instances");
    }

    /** Launches SableCC. */
    public static void main(
            String[] args) {

        try {
            compile(args);
        }
        catch (CompilerException e) {
            System.err.print(e.getMessage());
            System.err.flush();
            System.exit(1);
        }
        catch (ParserException e) {
            int start = e.getMessage().indexOf(' ');
            System.err.print(new M_syntax_error(e.getToken().getLine() + "", e
                    .getToken().getPos()
                    + "", e.getToken().getClass().getSimpleName().substring(1)
                    .toLowerCase(), e.getToken().getText(), e.getMessage()
                    .substring(start)));
            System.err.flush();
            System.exit(1);
        }
        catch (LexerException e) {
            int start = e.getMessage().indexOf('[') + 1;
            int end = e.getMessage().indexOf(',');
            String line = e.getMessage().substring(start, end);

            start = e.getMessage().indexOf(',') + 1;
            end = e.getMessage().indexOf(']');
            String pos = e.getMessage().substring(start, end);

            start = e.getMessage().indexOf(' ') + 1;

            System.err.print(new M_lexical_error(line, pos, e.getMessage()
                    .substring(start)));
            System.err.flush();
            System.exit(1);
        }
        catch (InternalException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            System.err
                    .print(new M_internal_error(sw.toString(), e.getMessage()));
            System.err.flush();
            System.exit(1);
        }
        catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            System.err
                    .print(new M_internal_error(sw.toString(), e.getMessage()));
            System.err.flush();
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
            throws ParserException, LexerException {

        // default destination directory is current working directory
        File destinationDirectory = new File(System.getProperty("user.dir"));

        // default option values
        boolean no_files = false;
        Strictness strictness = Strictness.STRICT;
        Verbosity verbosity = Verbosity.INFORMATIVE;

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
                    throw CompilerException
                            .missing_destination_directory(optionArgument
                                    .getOperand());
                }

                if (!destinationDirectory.isDirectory()) {
                    throw CompilerException
                            .destination_is_not_directory(optionArgument
                                    .getOperand());
                }

                break;

            case NO_FILES:
                no_files = true;
                break;

            case LENIENT:
                strictness = Strictness.LENIENT;
                break;

            case STRICT:
                strictness = Strictness.STRICT;
                break;

            case QUIET:
                verbosity = Verbosity.QUIET;
                break;

            case INFORMATIVE:
                verbosity = Verbosity.INFORMATIVE;
                break;

            case VERBOSE:
                verbosity = Verbosity.VERBOSE;
                break;

            case VERSION:
                System.out.println("SableCC version " + VERSION);
                return;

            case HELP:
                System.out.println("usage: sablecc "
                        + Option.getShortHelpMessage() + " grammar.sablecc");
                System.out.println("options:");
                System.out.println(Option.getLongHelpMessage());
                return;

            default:
                throw new InternalException("unhandled option "
                        + optionArgument.getOption());
            }
        }

        switch (verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println();
            System.out.println("SableCC version " + VERSION);
            System.out
                    .println("by Etienne M. Gagnon <egagnon@j-meg.com> and other contributors.");
            System.out.println();
        }

        // handle text arguments
        if (argumentCollection.getTextArguments().size() == 0) {
            System.out.println("usage: sablecc " + Option.getShortHelpMessage()
                    + " grammar.sablecc");
            return;
        }
        else if (argumentCollection.getTextArguments().size() > 1) {
            throw CompilerException.invalid_argument_count();
        }

        TextArgument textArgument = argumentCollection.getTextArguments()
                .get(0);

        if (!textArgument.getText().endsWith(".sablecc")) {
            throw CompilerException.invalid_suffix(textArgument.getText());
        }

        File grammarFile = new File(textArgument.getText());

        if (!grammarFile.exists()) {
            throw CompilerException
                    .missing_grammar_file(textArgument.getText());
        }

        if (!grammarFile.isFile()) {
            throw CompilerException.grammar_not_file(textArgument.getText());
        }

        compile(grammarFile, destinationDirectory, no_files, strictness,
                verbosity);
    }

    /**
     * Compiles the provided grammar file.
     */
    private static void compile(
            File grammarFile,
            File destinationDirectory,
            boolean no_files,
            Strictness strictness,
            Verbosity verbosity)
            throws ParserException, LexerException {

        switch (verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println("Compiling \"" + grammarFile + "\"");
        }

        Start ast;

        try {
            FileReader fr = new FileReader(grammarFile);
            BufferedReader br = new BufferedReader(fr);
            PushbackReader pbr = new PushbackReader(br, 1024);

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
            throw CompilerException.input_error(grammarFile.toString(), e);
        }

        GlobalData globalData = new GlobalData(ast, grammarFile);

        verifySemantics(globalData, strictness, verbosity);

        throw new InternalException("unimplemented");
    }

    private static void verifySemantics(
            GlobalData globalData,
            Strictness strictness,
            Verbosity verbosity) {

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Verifying semantics");
        }

        globalData.getAst().apply(new DeclarationFinder(globalData));

        throw new InternalException("unimplemented");
    }
}
