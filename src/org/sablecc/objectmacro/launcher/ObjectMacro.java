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

import static org.sablecc.objectmacro.launcher.Version.*;

import java.io.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.codegeneration.*;
import org.sablecc.objectmacro.codegeneration.c.*;
import org.sablecc.objectmacro.codegeneration.intermediate.*;
import org.sablecc.objectmacro.codegeneration.java.*;
import org.sablecc.objectmacro.codegeneration.scala.*;
import org.sablecc.objectmacro.errormessage.*;
import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.intermediate.syntax3.node.*;
import org.sablecc.objectmacro.intermediate.syntax3.node.AMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.AOption;
import org.sablecc.objectmacro.intermediate.syntax3.node.PMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.POption;
import org.sablecc.objectmacro.intermediate.syntax3.node.PParam;
import org.sablecc.objectmacro.intermediate.syntax3.node.PType;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.syntax3.lexer.*;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.syntax3.node.AStringType;
import org.sablecc.objectmacro.syntax3.node.Start;
import org.sablecc.objectmacro.syntax3.parser.*;
import org.sablecc.objectmacro.util.Utils;
import org.sablecc.objectmacro.walker.*;
import org.sablecc.util.*;

/**
 * The main class of ObjectMacro.
 */
public class ObjectMacro {

    /** Prevents instantiation of this class. */
    private ObjectMacro() {

        throw new InternalException("this class may not have instances");
    }

    /** Launches ObjectMacro. */
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
            System.err.print(new MSyntaxError(e.getToken().getLine() + "", e
                    .getToken().getPos() + "", e.getToken().getClass()
                    .getSimpleName().substring(1).toLowerCase(), e.getToken()
                    .getText(), e.getMessage().substring(start)));
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
            throws ParserException, LexerException {

        // default target is java
        String targetLanguage = "java";

        // default destination directory is current working directory
        File destinationDirectory = new File(System.getProperty("user.dir"));

        // default destination package is anonymous
        String destinationPackage = "";

        // default option values
        boolean generateCode = true;
        Verbosity verbosity = Verbosity.INFORMATIVE;
        Strictness strictness = Strictness.STRICT;

        // parse command line arguments
        ArgumentCollection argumentCollection = new ArgumentCollection(
                arguments);

        // handle option arguments
        for (OptionArgument optionArgument : argumentCollection
                .getOptionArguments()) {

            switch (optionArgument.getOption()) {

            case LIST_TARGETS:
                System.out.println("Available targets:");
                System.out.println(" java (default)");
                System.out.println(" c");
                System.out.println(" scala");
                System.out.println(" intermediate");
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
                System.out.println("ObjectMacro, part of SableCC version "
                        + VERSION);
                return;

            case HELP:
                System.out.println("Usage: objectmacro "
                        + Option.getShortHelpMessage() + " file.objectmacro");
                System.out.println("Options:");
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
            System.out.println("ObjectMacro, part of SableCC version "
                    + VERSION);
            System.out
                    .println("by Etienne M. Gagnon <egagnon@j-meg.com> and other contributors.");
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

        // check target
        if (!(targetLanguage.equals("java")
                || targetLanguage.equals("intermediate")
                || targetLanguage.equals("c") || targetLanguage.equals("scala"))) {
            throw CompilerException.unknownTarget(targetLanguage);
        }

        // check argument
        TextArgument textArgument = argumentCollection.getTextArguments()
                .get(0);

        if (!textArgument.getText().endsWith(".objectmacro")) {
            throw CompilerException.invalidSuffix(textArgument.getText());
        }

        File macroFile = new File(textArgument.getText());

        if (!macroFile.exists()) {
            throw CompilerException.missingMacroFile(textArgument.getText());
        }

        if (!macroFile.isFile()) {
            throw CompilerException.macroNotFile(textArgument.getText());
        }

        compile(macroFile, targetLanguage, destinationDirectory,
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
            throws ParserException, LexerException {

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

        GlobalIndex globalIndex = verifySemantics(ast, strictness, verbosity);

        processSemantics(globalIndex, verbosity);

        PIntermediateRepresentation intermediateAST = generateIntermediateAST(
                globalIndex, verbosity);

        IntermediateRepresentation ir = new IntermediateRepresentation(
                intermediateAST, macroFile, destinationDirectory,
                destinationPackage);

        CodeGenerator codeGenerator;

        if (targetLanguage.equals("java")) {
            codeGenerator = new JavaCodeGenerator(ir);
        }
        else if (targetLanguage.equals("intermediate")) {
            codeGenerator = new IntermediateCodeGenerator(ir);
        }
        else if (targetLanguage.equals("c")) {
            codeGenerator = new CCodeGenerator(ir);
        }
        else if (targetLanguage.equals("scala")) {
            codeGenerator = new ScalaCodeGenerator(ir);
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
        ast.apply(new DefinitionCollector(globalIndex));
        ast.apply(new OptionCollector(globalIndex));
        ast.apply(new VarVerifier(globalIndex));

        if (strictness == Strictness.STRICT) {
            for (Macro macro : globalIndex.getAllMacros()) {
                for (Param param : macro.getAllParams()) {
                    if (!param.isUsed()) {
                        throw CompilerException.unusedParam(param);
                    }
                }
            }
        }

        return globalIndex;
    }

    private static void processSemantics(
            GlobalIndex globalIndex,
            Verbosity verbosity) {

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Processing semantics");
            break;
        }
    }

    private static PIntermediateRepresentation generateIntermediateAST(
            GlobalIndex globalIndex,
            Verbosity verbosity) {

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Creating intermediate structure");
            break;
        }

        List<PMacro> macros = new LinkedList<PMacro>();

        for (Macro macro : globalIndex.getAllMacros()) {

            macros.add(createMacro(macro));
        }

        return new AIntermediateRepresentation(macros);
    }

    private static AMacro createMacro(
            Macro macro) {

        TString name = new TString(macro.getName());
        List<PParam> params = new LinkedList<>();
        Set<Param> macro_contexts = macro.getAllContexts();
        Set<Param> macro_params = macro.getAllParams();
        List<PMacroBodyPart> macroBodyParts = macro.getDeclaration().getMacroBodyParts();
        List<PMacroPart> macroParts = createMacroBody(macroBodyParts);

        for(Param context : macro_contexts){
            params.add(createContext(context));
        }

        for(Param param : macro_params){
            params.add(createParam(param));
        }

        return new AMacro(name, params, macroParts);
    }

    private static List<PMacroPart> createMacroBody(
            List<PMacroBodyPart> macroBodyParts){

        List<PMacroPart> macroParts = new LinkedList<>();
        StringBuilder macroBuilder = null;

        for(PMacroBodyPart bodyPart : macroBodyParts){

            if(bodyPart instanceof AEolMacroBodyPart){

                if(macroBuilder != null){
                    macroParts.add(
                            new AStringMacroPart(
                                    new TString(macroBuilder.toString())));

                    macroBuilder = null;
                }

                macroParts.add(new AEolMacroPart());

            }else if(bodyPart instanceof AEscapeMacroBodyPart){

                AEscapeMacroBodyPart escapeMacroBodyPart = (AEscapeMacroBodyPart) bodyPart;

                if(escapeMacroBodyPart.getTextEscape().getText().equals("{{")){
                    if(macroBuilder == null){
                        macroBuilder = new StringBuilder();
                    }

                    macroBuilder.append("{");
                }else{
                    throw new InternalException("case unhandled");
                }

            }else if(bodyPart instanceof AInsertMacroBodyPart){

                if(macroBuilder != null){
                    macroParts.add(
                            new AStringMacroPart(
                                    new TString(macroBuilder.toString())));

                    macroBuilder = null;
                }

                AInsertMacroBodyPart insertMacroBodyPart = (AInsertMacroBodyPart) bodyPart;
                AMacroRef macroRef = createMacroRef((AMacroReference)insertMacroBodyPart.getMacroReference());
                AInsertMacroPart insertMacroPart = new AInsertMacroPart(macroRef);
                macroParts.add(insertMacroPart);

            }else if(bodyPart instanceof ATextMacroBodyPart){

                if(macroBuilder == null){
                    macroBuilder = new StringBuilder();

                }

                macroBuilder.append(((ATextMacroBodyPart) bodyPart).getTextPart().getText());

            }else if(bodyPart instanceof AVarMacroBodyPart){

                if(macroBuilder != null){
                    macroParts.add(
                            new AStringMacroPart(
                                    new TString(macroBuilder.toString())));

                    macroBuilder = null;
                }

                AVarMacroBodyPart varBodyPart = (AVarMacroBodyPart) bodyPart;
                String varName = Utils.getVarName(varBodyPart.getVariable());
                macroParts.add(new AVarMacroPart(new TString(varName)));

            }else{
                throw new InternalException("case not handled");
            }
        }

        if(macroBuilder != null){
            macroParts.add(
                    new AStringMacroPart(
                            new TString(macroBuilder.toString())));
        }

        return macroParts;
    }

    private static List<PTextPart> createTextParts(
            List<PStringPart> stringParts){

        List<PTextPart> text_parts = new LinkedList<>();
        {
            StringBuilder text_builder = null;
            for(PStringPart stringPart : stringParts){
                if(stringPart instanceof AEscapeStringPart){
                    AEscapeStringPart aEscapeStringPart = (AEscapeStringPart) stringPart;
                    String escape = aEscapeStringPart.getStringEscape().getText();

                    if(escape.equals("{{")){
                        if(text_builder == null){
                            text_builder = new StringBuilder();
                        }
                        text_builder.append("{");

                    }else if(escape.equals("\\n")){
                        if(text_builder != null){
                            text_parts.add(
                                    new AStringTextPart(
                                            new TString(text_builder.toString())));

                            text_builder = null;
                        }

                        text_parts.add(new AEolTextPart());
                    }else{
                        throw new InternalException("case not handled");
                    }

                }else if(stringPart instanceof ATextStringPart){

                    if(text_builder == null){
                        text_builder = new StringBuilder();
                    }

                    ATextStringPart aTextStringPart = (ATextStringPart) stringPart;
                    text_builder.append(aTextStringPart.getText().getText());

                }else if(stringPart instanceof AVarStringPart){
                    AVarStringPart aVarStringPart = (AVarStringPart) stringPart;

                    if(text_builder != null){
                        text_parts.add(new AStringTextPart(
                                new TString(text_builder.toString())
                        ));

                        text_builder = null;
                    }

                    text_parts.add(new AVarTextPart(
                            new TString(aVarStringPart.getVariable().getText())));
                }else {
                    throw new InternalException("case not handled");
                }
            }

            if(text_builder != null){
                text_parts.add(new AStringTextPart(
                        new TString(text_builder.toString())
                ));

            }
        }

        return text_parts;
    }

    private static PParam createContext(
            Param param){

        List<POption> options = createOptions(param.getAllDirectives());
        TString pName = new TString(param.getDeclaration().getName().getText());
        PType type = createParamType(param);

        return new AContextParam(pName, type, options);
    }

    private static AParamParam createParam(
            Param param){

        List<POption> options = createOptions(param.getAllDirectives());
        TString pName = new TString(param.getDeclaration().getName().getText());
        PType type = createParamType(param);

        return new AParamParam(pName, type, options);
    }

    private static PType createParamType(
            Param param){

        PType type = null;
        if(param.getDeclaration().getType() instanceof AStringType){
            type = new org.sablecc.objectmacro.intermediate.syntax3.node.AStringType();

        }else if(param.getDeclaration().getType() instanceof AMacrosType){
            Set<AMacroReference> macroReferences = param.getMacroReferences();
            List<AMacroRef> macroRefs = new LinkedList<>();

            for(AMacroReference l_macroRef : macroReferences){

                macroRefs.add(createMacroRef(l_macroRef));
            }

            type = new AMacroRefsType(macroRefs);

        }

        return type;
    }

    private static List<POption> createOptions(Set<Directive> directives){

        List<POption> options_node = new LinkedList<>();

        for(Directive l_directive : directives){
            TString optionName = new TString(
                    l_directive.getName());
            List<PTextPart> text_parts = createTextParts(
                    l_directive.getDeclaration().getParts());
            AStringValue value = new AStringValue(text_parts);
            options_node.add(new AOption(optionName, value));

        }

        return options_node;
    }

    private static List<PValue> createValues(
            AMacroReference macroReference){

        List<PValue> values = new LinkedList<>();
        List<PStaticValue> args = macroReference.getValues();

        for(PStaticValue argument : args){
            if(argument instanceof AStringStaticValue){
                AStringStaticValue stringStaticValue = (AStringStaticValue) argument;
                List<PTextPart> text_parts = createTextParts(stringStaticValue.getParts());
                AStringValue value = new AStringValue(text_parts);
                values.add(value);

            }else if(argument instanceof AVarStaticValue){
                AVarStaticValue aVarStaticValue = (AVarStaticValue) argument;
                AVarValue value = new AVarValue(
                        new TString(aVarStaticValue.getIdentifier().getText()));
                values.add(value);
            }
        }

        return values;
    }

    private static AMacroRef createMacroRef(
            AMacroReference macroReference){

        List<PValue> values = createValues(macroReference);
        TString macroRefName = new TString(macroReference.getName().getText());

        return new AMacroRef(macroRefName, values);
    }
}
