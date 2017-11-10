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

import java.io.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.intermediate.macro.*;
import org.sablecc.objectmacro.errormessage.*;
import org.sablecc.objectmacro.exception.*;
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

    private static GlobalIndex globalIndex = null;
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

        // finish gracefully
        System.exit(0);
    }

    /**
     * Parses the provided arguments and launches macro compilation.
     */
    public static void compile(
            String[] arguments)
            throws ParserException, LexerException {

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
                throw new InternalException("unhandled option "
                        + optionArgument.getOption());
            }
        }

        switch (verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println();
            System.out.println("ObjectMacro, part of SableCC version "
                    + Version.VERSION);
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

        compile(macroFile, destinationDirectory,
                destinationPackage, generateCode, strictness, verbosity);
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

        globalIndex = verifySemantics(ast, strictness, verbosity);

        if(generateCode){
            generateIntermediateFile(
                    verbosity, destinationDirectory, macroFile);
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

        for(Macro macro : globalIndex.getAllMacros()){
            Set<Param> allParamsInternals = new LinkedHashSet<>();
            allParamsInternals.addAll(macro.getAllInternals());
            allParamsInternals.addAll(macro.getAllParams());

            macro.detectParamsCyclicReference();
        }

        if(strictness == Strictness.STRICT){
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

        for (Macro macro : globalIndex.getAllMacros()) {
            MMacro macro_macro = createMacro(macro);
            macros_string.append(macro_macro.toString());
            macros_string.append(System.getProperty("line.separator"));
        }

        String macroFileName = macroFile.getName();
        int length = macroFileName.length();

        String name = macroFile.getName()
                .substring(0, length - ".objectmacro".length());

        File destination = new File(destinationDirectory, name.concat(".intermediate"));

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
        String macroNames[] = Utils.splitName(macro.getNameDeclaration());
        for(String part : macroNames){
            mMacro.newSimpleName(part);
        }

        Set<Param> macro_internals = macro.getAllInternals();
        Set<Param> macro_params = macro.getAllParams();

        List<PMacroBodyPart> macroBodyParts = macro.getDeclaration().getMacroBodyParts();
        createMacroBody(mMacro, macroBodyParts);

        for(Param param : macro_params){
            createParam(
                    mMacro.newParam(), param);
        }

        for(Param internal : macro_internals){
            createInternal(
                    mMacro.newInternal(), internal);
        }

        MInitializationOrder mInitializationOrder = mMacro.newInitializationOrder();

        for(Param param : macro.getComponentFinder().getLinearization()){
            mInitializationOrder.newSimpleName(param.getName());
        }

        return mMacro;
    }

    private static void createMacroBody(
            MMacro mMacro,
            List<PMacroBodyPart> macroBodyParts){

        for(PMacroBodyPart bodyPart : macroBodyParts){

            if(bodyPart instanceof AEscapeMacroBodyPart){
                AEscapeMacroBodyPart escapeMacroBodyPart = (AEscapeMacroBodyPart) bodyPart;

                if(escapeMacroBodyPart.getTextEscape().getText().equals("{{")){
                    mMacro.newStringPart("{");
                }else{
                    throw new InternalException("case unhandled");
                }
            }else if(bodyPart instanceof ATextMacroBodyPart){

                ATextMacroBodyPart aTextMacroBodyPart = ((ATextMacroBodyPart) bodyPart);
                String macroTextPart = aTextMacroBodyPart.getTextPart().getText();
                macroTextPart = macroTextPart.replaceAll("'", "\\\\'");

                mMacro.newStringPart(macroTextPart);

            }else if(bodyPart instanceof AInsertMacroBodyPart){

                AInsertMacroBodyPart aInsertMacroBodyPart = (AInsertMacroBodyPart) bodyPart;
                AMacroReference macroRef = (AMacroReference) aInsertMacroBodyPart.getMacroReference();
                MMacroInsert macroInsert = mMacro.newMacroInsert();
                MMacroRef mMacroRef = macroInsert.newMacroRef();

                String macroRefName[] = Utils.splitName(macroRef.getName());
                for(String part : macroRefName){
                    mMacroRef.newSimpleName(part);

                }

                if(macroRef.getValues().size() > 0){
                    createArgs(mMacroRef.newArgs(), macroRef);
                }

            }else if(bodyPart instanceof AVarMacroBodyPart){

                AVarMacroBodyPart aVarMacroBodyPart = (AVarMacroBodyPart) bodyPart;
                String varNames[] = Utils.getVarName(aVarMacroBodyPart.getVariable()).split(Utils.NAME_SEPARATOR);
                MParamInsert mParamInsert = mMacro.newParamInsert();

                for(String part : varNames){
                    mParamInsert.newSimpleName(part);

                }
            }else if(bodyPart instanceof AEolMacroBodyPart){

                mMacro.newEolPart();
            }
            else {
                throw new InternalException("case unhandled");
            }
        }
    }

    private static void createTextParts(
            MTextArgument mTextArgument,
            List<PStringPart> stringParts){

        for(PStringPart stringPart : stringParts){
            if(stringPart instanceof ATextStringPart){
                String stringPartText = ((ATextStringPart) stringPart).getText().getText();

                stringPartText = stringPartText.replaceAll("'", "\\\\'");

                mTextArgument.newStringPart(stringPartText);
            }
            else if(stringPart instanceof AInsertStringPart){

                AMacroReference macro_node = (AMacroReference) ((AInsertStringPart) stringPart).getMacro();
                MMacroInsert mMacroInsert = mTextArgument.newMacroInsert();
                MMacroRef mMacroRef = mMacroInsert.newMacroRef();

                String macroRefName[] = Utils.splitName(macro_node.getName());
                for(String part : macroRefName){
                    mMacroRef.newSimpleName(part);

                }

                if(macro_node.getValues().size() > 0){
                    createArgs(mMacroRef.newArgs(), macro_node);
                }
            }
            else if(stringPart instanceof AVarStringPart){
                TVariable tVariable = ((AVarStringPart) stringPart).getVariable();
                MParamInsert mParamInsert = mTextArgument.newParamInsert();

                String varNames[] = Utils.getVarName(tVariable).split(Utils.NAME_SEPARATOR);
                for(String part : varNames){
                    mParamInsert.newSimpleName(part);

                }
            }
            else if(stringPart instanceof AEscapeStringPart){

                AEscapeStringPart escapeStringPart = (AEscapeStringPart) stringPart;
                if(escapeStringPart.getStringEscape().getText().equals("\\\\")){
                    mTextArgument.newStringPart("\\");
                }else if(escapeStringPart.getStringEscape().getText().equals("\\n")){
                    mTextArgument.newEolPart();
                }
                else{
                    throw new InternalException("case unhandled");
                }
            }
            else{
                throw new InternalException("case unhandled");
            }
        }
    }

    private static void createDirectiveParts(
            MDirective mDirective,
            List<PStringPart> stringParts){

        for(PStringPart stringPart : stringParts){
            if(stringPart instanceof ATextStringPart){
                String stringPartText = ((ATextStringPart) stringPart).getText().getText();

                stringPartText = stringPartText.replaceAll("'", "\\\\'");

                mDirective.newStringPart(stringPartText);
            }
            else if(stringPart instanceof AInsertStringPart){

                AMacroReference macro_node = (AMacroReference) ((AInsertStringPart) stringPart).getMacro();
                MMacroInsert mMacroInsert = mDirective.newMacroInsert();
                MMacroRef mMacroRef = mMacroInsert.newMacroRef();

                String macroRefName[] = Utils.splitName(macro_node.getName());
                for(String part : macroRefName){
                    mMacroRef.newSimpleName(part);

                }

                if(macro_node.getValues().size() > 0){
                    createArgs(mMacroRef.newArgs(), macro_node);
                }
            }
            else if(stringPart instanceof AVarStringPart){
                TVariable tVariable = ((AVarStringPart) stringPart).getVariable();
                MParamInsert mParamInsert = mDirective.newParamInsert();

                String varNames[] = Utils.getVarName(tVariable).split(Utils.NAME_SEPARATOR);
                for(String part : varNames){
                    mParamInsert.newSimpleName(part);

                }
            }
            else if(stringPart instanceof AEscapeStringPart){

                AEscapeStringPart escapeStringPart = (AEscapeStringPart) stringPart;
                if(escapeStringPart.getStringEscape().getText().equals("\\\\")){
                    mDirective.newStringPart("\\");
                }else if(escapeStringPart.getStringEscape().getText().equals("\\n")){
                    mDirective.newEolPart();
                }
                else{
                    throw new InternalException("case unhandled");
                }
            }
            else{
                throw new InternalException("case unhandled");
            }
        }
    }

    private static void createParam(
            MParam macro_param,
            Param param){

        String paramNames[] = Utils.splitName(param.getNameDeclaration());
        for(String part : paramNames){
            macro_param.newSimpleName(part);

        }

        if(param.getDeclaration().getType() instanceof AStringType){
            macro_param.newStringType();

        }else if(param.getDeclaration().getType() instanceof AMacrosType){

            MMacroType macro_param_type = macro_param.newMacroType();
            Set<AMacroReference> macroReferences = param.getMacroReferences();

            for(AMacroReference l_macroRef : macroReferences){

                MMacroRef macroRef = macro_param_type.newMacroRef();
                String macroRefName[] = Utils.splitName(l_macroRef.getName());
                for(String part : macroRefName){
                    macroRef.newSimpleName(part);

                }

                if(l_macroRef.getValues().size() > 0){
                    createArgs(macroRef.newArgs(), l_macroRef);
                }
            }
        }

        Set<Directive> directives = param.getAllDirectives();
        for(Directive l_directive : directives){
            MDirective mDirective = macro_param.newDirective();

            String directiveNames[] = Utils.splitName(l_directive.getDeclaration().getName());
            for(String part : directiveNames){
                mDirective.newSimpleName(part);

            }
            createDirectiveParts(mDirective, l_directive.getDeclaration().getParts());
        }
    }

    private static void createInternal(
            MInternal macro_internal,
            Param param){

        String paramNames[] = Utils.splitName(param.getNameDeclaration());
        for(String part : paramNames){
            macro_internal.newSimpleName(part);

        }

        if(param.getDeclaration().getType() instanceof AStringType){
            macro_internal.newStringType();

        }else if(param.getDeclaration().getType() instanceof AMacrosType){

            MMacroType macro_param_type = macro_internal.newMacroType();
            Set<AMacroReference> macroReferences = param.getMacroReferences();

            for(AMacroReference l_macroRef : macroReferences){

                MMacroRef macroRef = macro_param_type.newMacroRef();
                if(l_macroRef.getValues().size() > 0){
                    createArgs(macroRef.newArgs(), l_macroRef);
                }

                String macroRefNames[] = Utils.splitName(l_macroRef.getName());
                for(String part : macroRefNames){
                    macroRef.newSimpleName(part);
                }
            }
        }

        Set<Directive> directives = param.getAllDirectives();

        for(Directive l_directive : directives){

            MDirective mDirective = macro_internal.newDirective();
            String directiveNames[] = Utils.splitName(l_directive.getDeclaration().getName());
            for(String part : directiveNames){
                mDirective.newSimpleName(part);

            }
            createDirectiveParts(mDirective, l_directive.getDeclaration().getParts());
        }
    }

    private static void createArgs(
            MArgs macro_args,
            AMacroReference aMacroReference){

        Macro macroReferenced = globalIndex.getMacro(aMacroReference.getName());
        List<String> paramNames = macroReferenced.getInternalsName();
        List<PStaticValue> arguments = aMacroReference.getValues();
        int i = 0;

        for(PStaticValue argument : arguments){
            if(argument instanceof AStringStaticValue){

                AStringStaticValue aStringStaticValue = (AStringStaticValue) argument;
                MTextArgument mTextArgument = macro_args.newTextArgument();
                mTextArgument.newParamName(paramNames.get(i));
                createTextParts(mTextArgument, aStringStaticValue.getParts());

            }else if(argument instanceof AVarStaticValue){

                AVarStaticValue aVarStaticValue = (AVarStaticValue) argument;
                MVarArgument mVarArgument = macro_args.newVarArgument();
                mVarArgument.newParamName(paramNames.get(i));

                String macroRefName[] = Utils.splitName(aVarStaticValue.getIdentifier());
                for(String part : macroRefName){
                    mVarArgument.newSimpleName(part);
                }
            }
            i++;
        }
    }
}
