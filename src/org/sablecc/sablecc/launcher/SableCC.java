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
import org.sablecc.sablecc.semantics.*;
import org.sablecc.sablecc.syntax3.lexer.*;
import org.sablecc.sablecc.syntax3.node.*;
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
            String line = "" + e.getToken().getLine();
            String pos = "" + e.getToken().getPos();
            int start = e.getMessage().indexOf(' ') + 1;

            System.err.print(new MSyntaxError(line, pos, e.getToken()
                    .getClass().getSimpleName().substring(1), e.getToken()
                    .getText(), e.getMessage().substring(start)));
            System.err.flush();
            System.exit(1);
        }
        catch (LexerException e) {
            String line = "" + e.getToken().getLine();
            String pos = "" + e.getToken().getPos();
            int start = e.getMessage().indexOf(' ') + 1;

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

        trace.informativeln();
        trace.informativeln("SableCC version " + VERSION);
        trace.informativeln("by Etienne M. Gagnon <egagnon@j-meg.com> and other contributors.");
        trace.informativeln();

        compileFile(grammarFile, targetLanguage, destinationDirectory,
                destinationPackage, generateCode, strictness, trace);
    }

    public static void compileFile(
            final File grammarFile,
            final String targetLanguage,
            final File destinationDirectory,
            final String destinationPackage,
            final boolean generateCode,
            final Strictness strictness,
            final Trace trace)
            throws ParserException, LexerException {

        if (grammarFile == null) {
            throw new InternalException("grammarFile may not be null");
        }

        if (targetLanguage == null) {
            throw new InternalException("targetLanguage may not be null");
        }

        if (destinationDirectory == null) {
            throw new InternalException("destinationDirectory may not be null");
        }

        if (destinationPackage == null) {
            throw new InternalException("destinationPackage may not be null");
        }

        if (strictness == null) {
            throw new InternalException("strictness may not be null");
        }

        if (trace == null) {
            throw new InternalException("trace may not be null");
        }

        trace.informativeln("Compiling \"" + grammarFile.toString() + "\"");

        try {
            FileReader fr = new FileReader(grammarFile);
            BufferedReader br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = br.read()) != -1) {
                sb.append((char) c);
            }

            br.close();
            fr.close();

            compileGrammar(sb.toString(), strictness, trace);

            // TODO: implement

            /*
            Grammar grammar = compileGrammar(sb.toString(), strictness, trace);

            if (generateCode) {
                CodeGenerator codeGenerator = new CodeGenerator(grammar,
                        targetLanguage, destinationDirectory,
                        destinationPackage, trace);
                codeGenerator.run();
            }
             */

            trace.informativeln();
            trace.informativeln("SableCC has successfully compiled \""
                    + grammarFile.toString() + "\".");
        }
        catch (IOException e) {
            throw LauncherException.inputError(grammarFile.toString(), e);
        }
    }

    // TODO: change return type and return structure
    public static void compileGrammar(
            final String text,
            final Strictness strictness,
            final Trace trace)
            throws ParserException, LexerException, IOException {

        if (text == null) {
            throw new InternalException("text may not be null");
        }

        if (strictness == null) {
            throw new InternalException("strictness may not be null");
        }

        if (trace == null) {
            throw new InternalException("trace may not be null");
        }

        trace.verboseln(" Parsing");

        Start ast = new Parser(new Lexer(new PushbackReader(new StringReader(
                text), 1024))).parse();

        trace.verboseln(" Verifying semantics");

        SemanticVerifier.verify(ast);

        // TODO: implement

        /*
        Grammar grammar = new Grammar(ast);

        trace.verboseln(" Compiling lexer");

        grammar.compileLexer(trace, strictness);

        trace.verboseln(" Compiling parser");

        if (grammar.getParser().getProductions().size() > 0) {
            grammar.compileParser(trace, strictness);
        }

        return grammar;
         */
    }
}
