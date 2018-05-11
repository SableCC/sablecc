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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.errormessage.MInternalError;
import org.sablecc.objectmacro.errormessage.MLexicalError;
import org.sablecc.objectmacro.errormessage.MSyntaxError;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.intermediate.macro.MArgs;
import org.sablecc.objectmacro.intermediate.macro.MDirective;
import org.sablecc.objectmacro.intermediate.macro.MIndentPart;
import org.sablecc.objectmacro.intermediate.macro.MInitializationOrder;
import org.sablecc.objectmacro.intermediate.macro.MInternal;
import org.sablecc.objectmacro.intermediate.macro.MMacro;
import org.sablecc.objectmacro.intermediate.macro.MMacroInsert;
import org.sablecc.objectmacro.intermediate.macro.MMacroRef;
import org.sablecc.objectmacro.intermediate.macro.MMacroType;
import org.sablecc.objectmacro.intermediate.macro.MParam;
import org.sablecc.objectmacro.intermediate.macro.MParamInsert;
import org.sablecc.objectmacro.intermediate.macro.MTextArgument;
import org.sablecc.objectmacro.intermediate.macro.MVarArgument;
import org.sablecc.objectmacro.structure.Directive;
import org.sablecc.objectmacro.structure.External;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.Internal;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.lexer.LexerException;
import org.sablecc.objectmacro.syntax3.node.AEolMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEscapeMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEscapeStringPart;
import org.sablecc.objectmacro.syntax3.node.AIdentifiersInternalType;
import org.sablecc.objectmacro.syntax3.node.AIndentMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AInsertMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AInsertStringPart;
import org.sablecc.objectmacro.syntax3.node.AMacroReference;
import org.sablecc.objectmacro.syntax3.node.AMacrosType;
import org.sablecc.objectmacro.syntax3.node.AStringInternalType;
import org.sablecc.objectmacro.syntax3.node.AStringStaticValue;
import org.sablecc.objectmacro.syntax3.node.AStringType;
import org.sablecc.objectmacro.syntax3.node.ATextMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.ATextStringPart;
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AVarStaticValue;
import org.sablecc.objectmacro.syntax3.node.AVarStringPart;
import org.sablecc.objectmacro.syntax3.node.PMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.PStaticValue;
import org.sablecc.objectmacro.syntax3.node.PStringPart;
import org.sablecc.objectmacro.syntax3.node.Start;
import org.sablecc.objectmacro.syntax3.node.TVariable;
import org.sablecc.objectmacro.syntax3.parser.Parser;
import org.sablecc.objectmacro.syntax3.parser.ParserException;
import org.sablecc.objectmacro.util.Utils;
import org.sablecc.objectmacro.walker.DeclarationCollector;
import org.sablecc.objectmacro.walker.DirectiveCollector;
import org.sablecc.objectmacro.walker.MacroReferenceCollector;
import org.sablecc.objectmacro.walker.ParamReferenceCollector;
import org.sablecc.objectmacro.walker.VarVerifier;
import org.sablecc.util.Strictness;
import org.sablecc.util.Verbosity;

/**
 * The main class of ObjectMacro.
 */
public class ObjectMacro {

    private static GlobalIndex globalIndex = null;

    private static String DEFAULT_TABULATION = "    ";

    /** Prevents instantiation of this class. */
    private ObjectMacro() {

        throw new InternalException("this class may not have instances");
    }

    /** Launches ObjectMacro. */
    public static void main(
            String[] args) {

        try {
            ObjectMacro.compile(args);
        }
        catch (CompilerException e) {
            System.err.print(e.getMessage());
            System.err.flush();
            System.exit(1);
        }
        catch (ParserException e) {
            int start = e.getMessage().indexOf(' ');
            System.err.print(new MSyntaxError(e.getToken().getLine() + "",
                    e.getToken().getPos() + "",
                    e.getToken().getClass().getSimpleName().substring(1)
                            .toLowerCase(),
                    e.getToken().getText(), e.getMessage().substring(start)));
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

            System.err.print(new MLexicalError(line, pos,
                    e.getMessage().substring(start)));
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
                System.out.println("ObjectMacro, part of SableCC version "
                        + Version.VERSION);
                return;

            case HELP:
                System.out.println("Usage: objectmacro "
                        + Option.getShortHelpMessage() + " file.objectmacro");
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
            System.out.println(
                    "ObjectMacro, part of SableCC version " + Version.VERSION);
            System.out.println(
                    "by Etienne M. Gagnon <egagnon@j-meg.com> and other contributors.");
            System.out.println();
            break;
        }

        // handle text arguments
        if (argumentCollection.getTextArguments().size() == 0) {
            System.out.println("Usage: objectmacro "
                    + Option.getShortHelpMessage() + " file.objectmacro");
            return;
        }
        else if (argumentCollection.getTextArguments().size() > 1) {
            throw CompilerException.invalidArgumentCount();
        }

        // check argument
        TextArgument textArgument
                = argumentCollection.getTextArguments().get(0);

        if (!textArgument.getText().endsWith(".objectmacro")) {
            throw CompilerException
                    .invalidObjectmacroSuffix(textArgument.getText());
        }

        File macroFile = new File(textArgument.getText());

        if (!macroFile.exists()) {
            throw CompilerException.missingMacroFile(textArgument.getText());
        }

        if (!macroFile.isFile()) {
            throw CompilerException.macroNotFile(textArgument.getText());
        }

        ObjectMacro.compile(macroFile, destinationDirectory, destinationPackage,
                generateCode, strictness, verbosity);
    }

    /**
     * Compiles the provided macro file.
     */
    private static void compile(
            File macroFile,
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

            ast = new Parser(new CustomLexer(pbr)).parse();

            pbr.close();
            br.close();
            fr.close();
        }
        catch (IOException e) {
            throw CompilerException.inputError(macroFile.toString(), e);
        }

        ObjectMacro.globalIndex
                = ObjectMacro.verifySemantics(ast, strictness, verbosity);

        if (generateCode) {
            ObjectMacro.generateIntermediateFile(verbosity,
                    destinationDirectory, macroFile);
        }

    }

    private static GlobalIndex verifySemantics(
            Start ast,
            Strictness strictness,
            Verbosity verbosity) {

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Verifying semantics");
            break;
        }

        GlobalIndex globalIndex = new GlobalIndex();

        ast.apply(new DeclarationCollector(globalIndex));
        ast.apply(new MacroReferenceCollector(globalIndex));
        ast.apply(new ParamReferenceCollector(globalIndex));
        ast.apply(new DirectiveCollector(globalIndex));
        ast.apply(new VarVerifier(globalIndex));

        for (Macro macro : globalIndex.getAllMacros()) {
            macro.detectParamsCyclicReference();
        }

        if (strictness == Strictness.STRICT) {
            for (Macro macro : globalIndex.getAllMacros()) {
                Set<Param> allParamsInternals = new LinkedHashSet<>();
                allParamsInternals.addAll(macro.getAllInternals());
                allParamsInternals.addAll(macro.getAllParams());

                for (Param param : allParamsInternals) {
                    if (!param.isUsed()) {
                        throw CompilerException.unusedParam(param);
                    }
                }
            }
        }

        return globalIndex;
    }

    private static void generateIntermediateFile(
            Verbosity verbosity,
            File destinationDirectory,
            File macroFile) {

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Creating intermediate structure");
            break;
        }

        StringBuilder macros_string = new StringBuilder();

        for (Macro macro : ObjectMacro.globalIndex.getAllMacros()) {
            MMacro macro_macro = ObjectMacro.createMacro(macro);
            macros_string.append(macro_macro.toString());
            macros_string.append(System.getProperty("line.separator"));
        }

        String macroFileName = macroFile.getName();
        int length = macroFileName.length();

        String name = macroFile.getName().substring(0,
                length - ".objectmacro".length());

        File destination
                = new File(destinationDirectory, name.concat(".intermediate"));

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(macros_string.toString());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }
    }

    private static MMacro createMacro(
            Macro macro) {

        MMacro mMacro = new MMacro();
        String splittedMacroName[]
                = Utils.splitName(macro.getNameDeclaration());
        for (String part : splittedMacroName) {
            mMacro.newSimpleName(part);
        }

        Set<Internal> macro_internals = macro.getAllInternals();
        Set<External> macro_params = macro.getAllParams();

        List<PMacroBodyPart> macroBodyParts
                = macro.getDeclaration().getMacroBodyParts();
        ObjectMacro.createMacroBody(mMacro, macroBodyParts);

        for (External param : macro_params) {
            ObjectMacro.createParam(mMacro.newParam(), param);
        }

        for (Internal internal : macro_internals) {
            ObjectMacro.createInternal(mMacro.newInternal(), internal);
        }

        MInitializationOrder mInitializationOrder
                = mMacro.newInitializationOrder();

        for (Param param : macro.getComponentFinder().getLinearization()) {
            if (!param.isString()) {
                mInitializationOrder.newSimpleName(param.getName());
            }
        }

        return mMacro;
    }

    private static void createMacroBody(
            MMacro mMacro,
            List<PMacroBodyPart> macroBodyParts) {

        for (PMacroBodyPart bodyPart : macroBodyParts) {
            if (bodyPart instanceof AEscapeMacroBodyPart) {
                AEscapeMacroBodyPart escapeMacroBodyPart
                        = (AEscapeMacroBodyPart) bodyPart;

                if (escapeMacroBodyPart.getTextEscape().getText()
                        .equals("{{")) {
                    mMacro.newStringPart("{");
                }
                else {
                    throw new InternalException("case unhandled");
                }
            }
            else if (bodyPart instanceof ATextMacroBodyPart) {
                ATextMacroBodyPart textBodyPart = (ATextMacroBodyPart) bodyPart;

                String macroTextPart = textBodyPart.getTextPart().getText()
                        .replaceAll("'", "\\\\'");
                mMacro.newStringPart(macroTextPart);
            }
            else if (bodyPart instanceof AInsertMacroBodyPart) {
                AInsertMacroBodyPart insertPart
                        = (AInsertMacroBodyPart) bodyPart;

                AMacroReference macroReference
                        = (AMacroReference) insertPart.getMacroReference();
                MMacroInsert macroInsert = mMacro.newMacroInsert();
                MMacroRef mMacroRef = macroInsert.newMacroRef();

                String macroRefName[]
                        = Utils.splitName(macroReference.getName());
                for (String part : macroRefName) {
                    mMacroRef.newSimpleName(part);
                }

                if (macroReference.getValues().size() > 0) {
                    ObjectMacro.createArgs(mMacroRef.newArgs(), macroReference);
                }

            }
            else if (bodyPart instanceof AVarMacroBodyPart) {
                AVarMacroBodyPart aVarMacroBodyPart
                        = (AVarMacroBodyPart) bodyPart;

                String varNames[]
                        = Utils.getVarName(aVarMacroBodyPart.getVariable())
                                .split(Utils.NAME_SEPARATOR);
                MParamInsert mParamInsert = mMacro.newParamInsert();

                for (String part : varNames) {
                    mParamInsert.newSimpleName(part);
                }
            }
            else if (bodyPart instanceof AEolMacroBodyPart) {
                mMacro.newEolPart();
            }
            else if (bodyPart instanceof AIndentMacroBodyPart) {
                AIndentMacroBodyPart indentPart
                        = (AIndentMacroBodyPart) bodyPart;
                ObjectMacro.createIndentTextParts(mMacro.newIndentPart(),
                        indentPart.getStringPart());
                ObjectMacro.createMacroBody(mMacro,
                        indentPart.getMacroBodyPart());
                mMacro.newEndIndentPart();
            }
//            else if(bodyPart instanceof AEndIndentMacroBodyPart){
//                mMacro.newEndIndentPart();
//            }
            else {
                throw new InternalException("case unhandled");
            }
        }
    }

    private static void createTextParts(
            MTextArgument mTextArgument,
            List<PStringPart> stringParts) {

        for (PStringPart stringPart : stringParts) {
            if (stringPart instanceof ATextStringPart) {
                ATextStringPart textPart = (ATextStringPart) stringPart;

                String text
                        = textPart.getText().getText().replaceAll("'", "\\\\'");
                mTextArgument.newStringPart(text);
            }
            else if (stringPart instanceof AInsertStringPart) {
                AMacroReference macro_node
                        = (AMacroReference) ((AInsertStringPart) stringPart)
                                .getMacro();

                MMacroRef macro_ref
                        = mTextArgument.newMacroInsert().newMacroRef();

                String macroRefName[] = Utils.splitName(macro_node.getName());
                for (String part : macroRefName) {
                    macro_ref.newSimpleName(part);
                }

                if (macro_node.getValues().size() > 0) {
                    ObjectMacro.createArgs(macro_ref.newArgs(), macro_node);
                }
            }
            else if (stringPart instanceof AVarStringPart) {
                TVariable var_token
                        = ((AVarStringPart) stringPart).getVariable();
                MParamInsert mParamInsert = mTextArgument.newParamInsert();

                String splittedVarName[] = Utils.getVarName(var_token)
                        .split(Utils.NAME_SEPARATOR);
                for (String part : splittedVarName) {
                    mParamInsert.newSimpleName(part);
                }
            }
            else if (stringPart instanceof AEscapeStringPart) {
                AEscapeStringPart escapeStringPart
                        = (AEscapeStringPart) stringPart;

                String text = escapeStringPart.getStringEscape().getText();
                if (text.equals("\\\\")) {
                    mTextArgument.newStringPart("\\");
                }
                else if (text.equals("\\n")) {
                    mTextArgument.newEolPart();
                }
                else if (text.equals("\\t")) {
                    mTextArgument.newStringPart(ObjectMacro.DEFAULT_TABULATION);
                }
                else if (text.startsWith("\\")) {
                    mTextArgument
                            .newStringPart(text.substring(text.length() - 1));
                }
                else {
                    throw new InternalException("case unhandled");
                }
            }
            else {
                throw new InternalException("case unhandled");
            }
        }
    }

    private static void createIndentTextParts(
            MIndentPart mIndentPart,
            List<PStringPart> stringParts) {

        for (PStringPart stringPart : stringParts) {
            if (stringPart instanceof ATextStringPart) {
                ATextStringPart textPart = (ATextStringPart) stringPart;

                String text
                        = textPart.getText().getText().replaceAll("'", "\\\\'");
                mIndentPart.newStringPart(text);
            }
            else if (stringPart instanceof AInsertStringPart) {
                AMacroReference macro_node
                        = (AMacroReference) ((AInsertStringPart) stringPart)
                                .getMacro();

                MMacroRef macro_ref
                        = mIndentPart.newMacroInsert().newMacroRef();

                String macroRefName[] = Utils.splitName(macro_node.getName());
                for (String part : macroRefName) {
                    macro_ref.newSimpleName(part);
                }

                if (macro_node.getValues().size() > 0) {
                    ObjectMacro.createArgs(macro_ref.newArgs(), macro_node);
                }
            }
            else if (stringPart instanceof AVarStringPart) {
                TVariable var_token
                        = ((AVarStringPart) stringPart).getVariable();
                MParamInsert mParamInsert = mIndentPart.newParamInsert();

                String splittedVarName[] = Utils.getVarName(var_token)
                        .split(Utils.NAME_SEPARATOR);
                for (String part : splittedVarName) {
                    mParamInsert.newSimpleName(part);
                }
            }
            else if (stringPart instanceof AEscapeStringPart) {
                AEscapeStringPart escapeStringPart
                        = (AEscapeStringPart) stringPart;

                String text = escapeStringPart.getStringEscape().getText();
                if (text.equals("\\\\")) {
                    mIndentPart.newStringPart("\\");
                }
                else if (text.equals("\\n")) {
                    mIndentPart.newEolPart();
                }
                else if (text.equals("\\t")) {
                    mIndentPart.newStringPart(ObjectMacro.DEFAULT_TABULATION);
                }
                else if (text.startsWith("\\")) {
                    mIndentPart
                            .newStringPart(text.substring(text.length() - 1));
                }
                else {
                    throw new InternalException("case unhandled");
                }
            }
            else {
                throw new InternalException("case unhandled");
            }
        }
    }

    private static void createDirectiveParts(
            MDirective mDirective,
            List<PStringPart> stringParts) {

        for (PStringPart stringPart : stringParts) {
            if (stringPart instanceof ATextStringPart) {
                String stringPartText
                        = ((ATextStringPart) stringPart).getText().getText();

                stringPartText = stringPartText.replaceAll("'", "\\\\'");
                mDirective.newStringPart(stringPartText);
            }
            else if (stringPart instanceof AInsertStringPart) {
                AMacroReference macro_node
                        = (AMacroReference) ((AInsertStringPart) stringPart)
                                .getMacro();

                MMacroRef macro_ref = mDirective.newMacroInsert().newMacroRef();

                String macroRefName[] = Utils.splitName(macro_node.getName());
                for (String part : macroRefName) {
                    macro_ref.newSimpleName(part);
                }

                if (macro_node.getValues().size() > 0) {
                    ObjectMacro.createArgs(macro_ref.newArgs(), macro_node);
                }
            }
            else if (stringPart instanceof AVarStringPart) {
                TVariable tVariable
                        = ((AVarStringPart) stringPart).getVariable();

                MParamInsert mParamInsert = mDirective.newParamInsert();
                String splittedVarName[] = Utils.getVarName(tVariable)
                        .split(Utils.NAME_SEPARATOR);

                for (String part : splittedVarName) {
                    mParamInsert.newSimpleName(part);
                }
            }
            else if (stringPart instanceof AEscapeStringPart) {
                AEscapeStringPart escapeStringPart
                        = (AEscapeStringPart) stringPart;
                String text = escapeStringPart.getStringEscape().getText();

                if (text.equals("\\\\")) {
                    mDirective.newStringPart("\\");
                }
                else if (text.equals("\\n")) {
                    mDirective.newEolPart();
                }
                else {
                    throw new InternalException("case unhandled");
                }
            }
            else {
                throw new InternalException("case unhandled");
            }
        }
    }

    private static void createParam(
            MParam macro_param,
            External param) {

        String paramNames[] = Utils.splitName(param.getNameDeclaration());
        for (String part : paramNames) {
            macro_param.newSimpleName(part);
        }

        if (param.getDeclaration().getType() instanceof AStringType) {
            macro_param.newStringType();
        }
        else if (param.getDeclaration().getType() instanceof AMacrosType) {
            MMacroType macro_param_type = macro_param.newMacroType();

            Set<AMacroReference> macroReferences = param.getMacroReferences();

            for (AMacroReference l_macroRef : macroReferences) {
                MMacroRef macroRef = macro_param_type.newMacroRef();

                String splittedMacroName[]
                        = Utils.splitName(l_macroRef.getName());
                for (String part : splittedMacroName) {
                    macroRef.newSimpleName(part);
                }

                if (l_macroRef.getValues().size() > 0) {
                    ObjectMacro.createArgs(macroRef.newArgs(), l_macroRef);
                }
            }
        }

        Set<Directive> directives = param.getAllDirectives();
        for (Directive l_directive : directives) {
            MDirective mDirective = macro_param.newDirective();

            String splittedDirectiveName[]
                    = Utils.splitName(l_directive.getDeclaration().getName());
            for (String part : splittedDirectiveName) {
                mDirective.newSimpleName(part);
            }

            ObjectMacro.createDirectiveParts(mDirective,
                    l_directive.getDeclaration().getParts());
        }
    }

    private static void createInternal(
            MInternal macro_internal,
            Internal param) {

        String paramNames[] = Utils.splitName(param.getNameDeclaration());
        for (String part : paramNames) {
            macro_internal.newSimpleName(part);
        }

        if (param.getDeclaration().getType() instanceof AStringInternalType) {
            macro_internal.newStringType();
        }
        else if (param.getDeclaration()
                .getType() instanceof AIdentifiersInternalType) {
            MMacroType macro_param_type = macro_internal.newMacroType();

            Set<AMacroReference> macroReferences = param.getMacroReferences();

            for (AMacroReference l_macroRef : macroReferences) {
                MMacroRef macroRef = macro_param_type.newMacroRef();

                String splittedMacroName[]
                        = Utils.splitName(l_macroRef.getName());
                for (String part : splittedMacroName) {
                    macroRef.newSimpleName(part);
                }
            }
        }
    }

    private static void createArgs(
            MArgs macro_args,
            AMacroReference aMacroReference) {

        Macro macroReferenced
                = ObjectMacro.globalIndex.getMacro(aMacroReference.getName());
        List<String> paramNames = macroReferenced.getInternalsName();
        List<PStaticValue> arguments = aMacroReference.getValues();
        int i = 0;

        for (PStaticValue argument : arguments) {
            if (argument instanceof AStringStaticValue) {
                AStringStaticValue stringValue = (AStringStaticValue) argument;

                MTextArgument textArgument = macro_args.newTextArgument();
                textArgument.newParamName(paramNames.get(i));

                ObjectMacro.createTextParts(textArgument,
                        stringValue.getParts());
            }
            else if (argument instanceof AVarStaticValue) {
                AVarStaticValue varValue = (AVarStaticValue) argument;

                MVarArgument varArgument = macro_args.newVarArgument();
                varArgument.newParamName(paramNames.get(i));

                String macroRefName[]
                        = Utils.splitName(varValue.getIdentifier());
                for (String part : macroRefName) {
                    varArgument.newSimpleName(part);
                }
            }
            i++;
        }
    }
}
