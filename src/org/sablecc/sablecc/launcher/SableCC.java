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

import static org.sablecc.sablecc.launcher.Version.*;
import static org.sablecc.util.Strictness.*;
import static org.sablecc.util.Verbosity.*;

import java.io.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.launcher.errormessage.*;
import org.sablecc.sablecc.syntax3.lexer.*;
import org.sablecc.sablecc.syntax3.parser.*;
import org.sablecc.util.*;

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
            try {
                processCommandLine(args);
            }
            finally {
                System.out.flush();
            }
        }
        catch (CompilerException e) {
            System.err.print(e.getMessage());
            System.err.flush();
            System.exit(1);
        }
        catch (ParserException e) {
            int start = e.getMessage().indexOf(' ');
            System.err.print(new MSyntaxError(e.getToken().getLine() + "", e
                    .getToken().getPos() + "", e.getToken().getClass()
                    .getSimpleName().substring(1), e.getToken().getText(), e
                    .getMessage().substring(start)));
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

            System.err.print(new MLexicalError(line, pos, e.getMessage()
                    .substring(start)));
            System.err.flush();
            System.exit(1);
        }
        catch (InternalException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            System.err.print(new MInternalError(sw.toString(), e.getMessage()));
            System.err.flush();
            System.exit(1);
        }
        catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            String message = e.getMessage() == null ? "" : e.getMessage();
            System.err.print(new MInternalError(sw.toString(), message));
            System.err.flush();
            System.exit(1);
        }

        // finish gracefully
        System.exit(0);
    }

    /**
     * Parses the provided arguments and launches grammar compilation.
     */
    public static void processCommandLine(
            String[] arguments)
            throws ParserException, LexerException {

        // default target is java
        final String defaultTarget = "java";

        // default destination directory is current working directory
        File destinationDirectory = new File(System.getProperty("user.dir"));

        // default destination package is anonymous
        String destinationPackage = "";

        // default option values
        String targetLanguage = defaultTarget;
        boolean generateCode = true;
        Verbosity verbosity = INFORMATIVE;
        Strictness strictness = STRICT;

        // supported targets
        SortedSet<String> supportedTargets = new TreeSet<String>();
        supportedTargets.add("java");

        // parse command line arguments
        ArgumentCollection argumentCollection = new ArgumentCollection(
                arguments);

        // handle option arguments
        for (OptionArgument optionArgument : argumentCollection
                .getOptionArguments()) {

            switch (optionArgument.getOption()) {

            case LIST_TARGETS:
                System.out.println("Available targets:");
                for (String target : supportedTargets) {
                    System.out.print(" " + target);
                    if (target.equals(defaultTarget)) {
                        System.out.print(" (default)");
                    }
                    System.out.println();
                }
                return;

            case TARGET:
                targetLanguage = optionArgument.getOperand();

                // check that the target language is supported
                if (!supportedTargets.contains(targetLanguage)) {
                    throw LauncherException.unknownTarget(targetLanguage);
                }
                break;

            case DESTINATION:
                String destination = optionArgument.getOperand();
                destinationDirectory = new File(destination);

                // if the destination exists, check that it is a directory
                if (destinationDirectory.exists()
                        && !destinationDirectory.isDirectory()) {
                    throw LauncherException
                            .invalidDesinationDirectory(destination);
                }
                break;

            case PACKAGE:
                destinationPackage = optionArgument.getOperand();
                break;

            case GENERATE:
                generateCode = true;
                break;

            case NO_CODE:
                generateCode = false;
                break;

            case LENIENT:
                strictness = LENIENT;
                break;

            case STRICT:
                strictness = STRICT;
                break;

            case QUIET:
                verbosity = QUIET;
                break;

            case INFORMATIVE:
                verbosity = INFORMATIVE;
                break;

            case VERBOSE:
                verbosity = VERBOSE;
                break;

            case VERSION:
                System.out.println("SableCC version " + VERSION);
                return;

            case HELP:
                System.out.println("Usage: sablecc "
                        + Option.getShortHelpMessage() + " grammar.sablecc");
                System.out.println("Options:");
                System.out.println(Option.getLongHelpMessage());
                return;

            default:
                throw new InternalException("unhandled option "
                        + optionArgument.getOption());
            }
        }

        // handle text arguments
        if (argumentCollection.getTextArguments().size() == 0) {
            System.out.println("Usage: sablecc " + Option.getShortHelpMessage()
                    + " grammar.sablecc");
            return;
        }
        else if (argumentCollection.getTextArguments().size() > 1) {
            throw LauncherException.invalidArgumentCount();
        }

        // check argument
        TextArgument fileNameArgument = argumentCollection.getTextArguments()
                .get(0);

        if (!fileNameArgument.getText().endsWith(".sablecc")) {
            throw LauncherException.invalidSuffix(fileNameArgument.getText());
        }

        File grammarFile = new File(fileNameArgument.getText());

        if (!grammarFile.exists()) {
            throw LauncherException.missingGrammarFile(fileNameArgument
                    .getText());
        }

        if (!grammarFile.isFile()) {
            throw LauncherException.grammarNotFile(fileNameArgument.getText());
        }

        Trace trace = new Trace(verbosity);
        CompilationTask compilationTask = new CompilationTask(grammarFile,
                targetLanguage, destinationDirectory, destinationPackage,
                generateCode, strictness, trace);

        compilationTask.run();
    }
}
