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

import static org.sablecc.objectmacro.launcher.Version.VERSION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.LinkedList;
import java.util.List;

import org.sablecc.objectmacro.exception.ExitException;
import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.exception.InvalidArgumentException;
import org.sablecc.objectmacro.exception.SemanticException;
import org.sablecc.objectmacro.exception.SemanticRuntimeException;
import org.sablecc.objectmacro.structures.GlobalData;
import org.sablecc.objectmacro.structures.Macro;
import org.sablecc.objectmacro.syntax3.lexer.LexerException;
import org.sablecc.objectmacro.syntax3.node.AMacroSourceFilePart;
import org.sablecc.objectmacro.syntax3.node.PSourceFilePart;
import org.sablecc.objectmacro.syntax3.node.Start;
import org.sablecc.objectmacro.syntax3.parser.Parser;
import org.sablecc.objectmacro.syntax3.parser.ParserException;
import org.sablecc.objectmacro.util.Strictness;
import org.sablecc.objectmacro.util.Verbosity;
import org.sablecc.objectmacro.walkers.CodeGenerator;
import org.sablecc.objectmacro.walkers.CyclicInsertDetector;
import org.sablecc.objectmacro.walkers.ExpandSignatureFinder;
import org.sablecc.objectmacro.walkers.ImplicitReferenceFinder;
import org.sablecc.objectmacro.walkers.NameVerifier;
import org.sablecc.objectmacro.walkers.UnusedTextBlocksDetector;

/**
 * The main class of ObjectMacro.
 */
public class ObjectMacro {

    /** Prevents instanciation of this class. */
    private ObjectMacro() {

        throw new InternalException("this class may not have instances");
    }

    /** Launches ObjectMacro. */
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
        catch (Throwable e) {
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
     * Parses the provided arguments and launches macro compilation.
     */
    public static void compile(
            String[] arguments)
            throws InvalidArgumentException, ParserException, LexerException,
            SemanticException {

        // default destination directory is current working directory
        File destinationDirectory = new File(System.getProperty("user.dir"))
                .getAbsoluteFile();

        // default destination package is anonymous
        String destinationPackage = "";

        // parse command line arguments
        ArgumentCollection argumentCollection = new ArgumentCollection(
                arguments);

        Strictness strictness = Strictness.STRICT;
        Verbosity verbosity = Verbosity.NORMAL;

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

            case PACKAGE:
                destinationPackage = optionArgument.getOperand();
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
                verbosity = Verbosity.NORMAL;
                break;

            case VERBOSE:
                verbosity = Verbosity.VERBOSE;
                break;

            case VERSION:
                System.out.println("ObjectMacro of SableCC version " + VERSION);
                return;

            case HELP:
                System.out.println("usage: objectmacro "
                        + Option.getShortHelpMessage()
                        + " file.objectmacro ...");
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
            System.err.println("usage: objectmacro "
                    + Option.getShortHelpMessage() + " file.objectmacro ...");
            System.err.println("type 'objectmacro -h' for more information");

            throw new ExitException();
        }

        switch (verbosity) {
        case NORMAL:
        case VERBOSE:
            System.out.println();
            System.out.println("ObjectMacro of SableCC version " + VERSION);
            System.out
                    .println("by Etienne M. Gagnon <egagnon@j-meg.com> and other contributors.");
            System.out.println();
            break;
        }

        List<File> macroFiles = new LinkedList<File>();

        for (TextArgument textArgument : argumentCollection.getTextArguments()) {

            if (!textArgument.getText().endsWith(".objectmacro")) {
                throw new InvalidArgumentException(
                        "macro file name does not end with .objectmacro: "
                                + textArgument.getText());
            }

            File macroFile = new File(textArgument.getText());

            if (!macroFile.exists()) {
                throw new InvalidArgumentException(macroFile
                        + " does not exist");
            }

            if (!macroFile.isFile()) {
                throw new InvalidArgumentException(macroFile + " is not a file");
            }

            macroFiles.add(macroFile);
        }

        // compile macros
        for (File macroFile : macroFiles) {

            compile(macroFile, destinationDirectory, destinationPackage,
                    verbosity, strictness);
        }
    }

    /**
     * Compiles the provided macro file.
     */
    private static void compile(
            File macroFile,
            File destinationDirectory,
            String destinationPackage,
            Verbosity verbosity,
            Strictness strictness)
            throws InvalidArgumentException, ParserException, LexerException,
            SemanticException {

        switch (verbosity) {
        case NORMAL:
        case VERBOSE:
            System.out.println("Compiling " + macroFile);
            break;
        }

        @SuppressWarnings("unused")
        Start ast;

        try {
            FileReader fr = new FileReader(macroFile);
            BufferedReader br = new BufferedReader(fr);
            PushbackReader pbr = new PushbackReader(br);

            switch (verbosity) {
            case VERBOSE:
                System.out.println(" Parsing");
                break;
            }

            ast = new Parser(new CustomLexer(pbr)).parse();

            pbr.close();
            br.close();
            fr.close();
        }
        catch (IOException e) {
            throw new InvalidArgumentException("cannot read " + macroFile, e);
        }

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Verifying semantics");
            break;
        }

        GlobalData globalData = verifySemantics(ast, verbosity, strictness);

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Processing semantics");
            break;
        }

        processSemantics(ast, globalData, verbosity);

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Generating code");
            break;
        }

        generateCode(ast, globalData, destinationDirectory, destinationPackage);
    }

    private static GlobalData verifySemantics(
            Start ast,
            Verbosity verbosity,
            Strictness strictness)
            throws SemanticException {

        GlobalData globalData;

        try {
            globalData = new GlobalData(ast.getPSourceFile());

            switch (verbosity) {
            case VERBOSE:
                System.out.println("  Checking names");
                break;
            }

            ast.apply(new NameVerifier(globalData));

            switch (verbosity) {
            case VERBOSE:
                System.out.println("  Detecting cyclic inserts");
                break;
            }

            ast.apply(new CyclicInsertDetector(globalData));

            switch (verbosity) {
            case VERBOSE:
                System.out.println("  Detecting missing top-level macro");
                break;
            }

            detectMissingTopLevelMacro(globalData);

            switch (strictness) {
            case STRICT:
                switch (verbosity) {
                case VERBOSE:
                    System.out.println("  Detecting unused text blocks");
                    break;
                }

                ast.apply(new UnusedTextBlocksDetector(globalData));
                break;
            }

        }
        catch (SemanticRuntimeException e) {
            throw e.getSemanticException();
        }

        return globalData;
    }

    private static void detectMissingTopLevelMacro(
            GlobalData globalData) {

        for (PSourceFilePart part : globalData.getSourceFile().getDefinition()
                .getParts()) {
            if (part instanceof AMacroSourceFilePart) {
                AMacroSourceFilePart macroPart = (AMacroSourceFilePart) part;
                Macro macro = globalData.getMacro(macroPart.getMacro());
                if (macro.isAutoexpand()) {
                    // there is at least one top level macro
                    return;
                }
            }
        }

        System.err
                .println("ERROR: semantic error at [0,0] there is no top-level macro");
        throw new ExitException();
    }

    private static void processSemantics(
            Start ast,
            GlobalData globalData,
            Verbosity verbosity) {

        switch (verbosity) {
        case VERBOSE:
            System.out.println("  Computing references");
            break;
        }

        ast.apply(new ImplicitReferenceFinder(globalData));

        switch (verbosity) {
        case VERBOSE:
            System.out.println("  Computing expand signatures");
            break;
        }

        ast.apply(new ExpandSignatureFinder(globalData));
    }

    private static void generateCode(
            Start ast,
            GlobalData globalData,
            File destinationDirectory,
            String destinationPackage)
            throws SemanticException {

        try {
            ast.apply(new CodeGenerator(globalData, destinationDirectory,
                    destinationPackage));
        }
        catch (SemanticRuntimeException e) {
            throw e.getSemanticException();
        }
    }

}
