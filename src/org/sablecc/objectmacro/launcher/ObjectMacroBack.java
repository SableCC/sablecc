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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringWriter;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.codegeneration.CodeGenerator;
import org.sablecc.objectmacro.codegeneration.IntermediateRepresentation;
import org.sablecc.objectmacro.codegeneration.java.JavaCodeGenerator;
import org.sablecc.objectmacro.errormessage.MInternalError;
import org.sablecc.objectmacro.errormessage.MLexicalError;
import org.sablecc.objectmacro.errormessage.MSyntaxError;
import org.sablecc.objectmacro.errormessage.Macros;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.intermediate.syntax3.lexer.Lexer;
import org.sablecc.objectmacro.intermediate.syntax3.lexer.LexerException;
import org.sablecc.objectmacro.intermediate.syntax3.node.Start;
import org.sablecc.objectmacro.intermediate.syntax3.parser.Parser;
import org.sablecc.objectmacro.intermediate.syntax3.parser.ParserException;
import org.sablecc.util.Strictness;
import org.sablecc.util.Verbosity;

/**
 * The main class of ObjectMacroBack.
 */
public class ObjectMacroBack {

    /** Prevents instantiation of this class. */
    private ObjectMacroBack() {

        throw new InternalException("this class may not have instances");
    }

    /** Launches ObjectMacroBack. */
    public static void main(
            String[] args) {

        Macros errorFactory = new Macros();

        try {
            ObjectMacroBack.compile(args);
        }
        catch (CompilerException e) {
            System.err.print(e.getMessage());
            System.err.flush();
            System.exit(1);
        }
        catch (ParserException e) {
            int start = e.getMessage().indexOf(' ');
            MSyntaxError mSyntaxError = errorFactory.newSyntaxError();
            mSyntaxError.addChar(e.getToken().getPos() + "");
            mSyntaxError.addLine(e.getToken().getLine() + "");
            mSyntaxError.addMessage(e.getMessage().substring(start));
            mSyntaxError.addTokenText(e.getToken().getText());
            mSyntaxError.addTokenType(e.getToken().getClass().getSimpleName().substring(1).toLowerCase());

            System.err.print(mSyntaxError.build());
            System.err.println();
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
            MLexicalError lexical_error = errorFactory.newLexicalError();
            lexical_error.addLine(line);
            lexical_error.addChar(pos);
            lexical_error.addMessage(e.getMessage().substring(start));

            System.err.print(lexical_error.build());
            System.err.println();
            System.err.flush();
            System.exit(1);
        }
        catch (InternalException e) {
            int start = e.getMessage().indexOf(' ') + 1;

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            MInternalError mInternalError = errorFactory.newInternalError();
            mInternalError.addStackTrace(sw.toString());
            mInternalError.addStackTrace(e.getMessage().substring(start));

            System.err.print(mInternalError.build());
            System.err.println();
            System.err.flush();
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
            throws ParserException,
                LexerException {

        // default target is java-constructor
        String targetLanguage = "java-constructor";

        // default destination directory is current working directory
        File destinationDirectory = new File(System.getProperty("user.dir"));

        // default destination package is anonymous
        String destinationPackage = "";

        // default option values
        boolean generateCode = true;
        Verbosity verbosity = Verbosity.INFORMATIVE;
        Strictness strictness = Strictness.STRICT;

        // parse command line arguments
        ArgumentCollection argumentCollection
                = new ArgumentCollection(arguments);

        // handle option arguments
        for (OptionArgument optionArgument : argumentCollection
                .getOptionArguments()) {

            switch (optionArgument.getOption()) {

            case LIST_TARGETS:
                System.out.println("Available targets:");
                System.out.println(" java-constructor (default)");
                return;

            case TARGET:
                targetLanguage = optionArgument.getOperand();
                break;

            case DESTINATION:
                destinationDirectory = new File(optionArgument.getOperand());
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
                System.out.println("ObjectMacro-back, part of SableCC version "
                        + Version.VERSION);
                return;

            case HELP:
                System.out.println("Usage: objectmacro backend "
                        + Option.getShortHelpMessage() + " file.intermediate");
                System.out.println("Options:");
                System.out.println(Option.getLongHelpMessage());
                return;

            default:
                throw new InternalException(
                        "unhandled option " + optionArgument.getOption());
            }
        }

        switch (verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println();
            System.out.println("ObjectMacroBack, part of SableCC version "
                    + Version.VERSION);
            System.out.println(
                    "by Etienne M. Gagnon <egagnon@j-meg.com> and other contributors.");
            System.out.println();
            break;
        }

        // handle text arguments
        if (argumentCollection.getTextArguments().size() == 0) {
            System.out.println("Usage: objectmacro "
                    + Option.getShortHelpMessage() + " file.intermediate");
            return;
        }
        else if (argumentCollection.getTextArguments().size() > 1) {
            throw CompilerException.invalidArgumentCount();
        }

        // check target
        if (!targetLanguage.equals("java-constructor")) {
            throw CompilerException.unknownTarget(targetLanguage);
        }

        // check argument
        TextArgument textArgument
                = argumentCollection.getTextArguments().get(0);

        File macroFile = new File(textArgument.getText());

        if (!textArgument.getText().endsWith(".intermediate")) {
            throw CompilerException
                    .invalidIntermediateSuffix(textArgument.getText());
        }

        if (!macroFile.exists()) {
            throw CompilerException.missingMacroFile(textArgument.getText());
        }

        if (!macroFile.isFile()) {
            throw CompilerException.macroNotFile(textArgument.getText());
        }

        ObjectMacroBack.compile(macroFile, targetLanguage, destinationDirectory,
                destinationPackage, generateCode, strictness, verbosity);
    }

    /**
     * Compiles the provided macro file.
     */
    private static void compile(
            File macroFile,
            String targetLanguage,
            File destinationDirectory,
            String destinationPackage,
            boolean generateCode,
            Strictness strictness,
            Verbosity verbosity)
            throws ParserException,
                LexerException {

        switch (verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println("Compiling \"" + macroFile + "\"");
            break;
        }

        Start ast;

        try {
            FileReader fr = new FileReader(macroFile);
            BufferedReader br = new BufferedReader(fr);
            PushbackReader pbr = new PushbackReader(br, 1024);

            switch (verbosity) {
            case VERBOSE:
                System.out.println(" Parsing");
                break;
            }

            ast = new Parser(new Lexer(pbr)).parse();

            pbr.close();
            br.close();
            fr.close();
        }
        catch (IOException e) {
            throw CompilerException.inputError(macroFile.toString(), e);
        }

        IntermediateRepresentation ir = new IntermediateRepresentation(
                ast.getPIntermediateRepresentation(), macroFile,
                destinationDirectory, destinationPackage);

        CodeGenerator codeGenerator = new JavaCodeGenerator(ir);
        codeGenerator.generateCode();

        if (targetLanguage.equals("java-constructor")) {
            codeGenerator = new JavaCodeGenerator(ir);
        }
        else {
            throw new InternalException("unhandled case");
        }

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Verifying target-specific semantics");
            break;
        }

        codeGenerator.verifyTargetSpecificSemantics(strictness);

        if (generateCode) {
            switch (verbosity) {
            case VERBOSE:
                System.out.println(" Generating code");
                break;
            }

            codeGenerator.generateCode();
        }
    }
}
