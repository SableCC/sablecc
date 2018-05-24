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
import java.util.*;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.errormessage.MInternalError;
import org.sablecc.objectmacro.errormessage.MLexicalError;
import org.sablecc.objectmacro.errormessage.MSyntaxError;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.intermediate.macro.*;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.syntax3.lexer.LexerException;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.syntax3.parser.Parser;
import org.sablecc.objectmacro.syntax3.parser.ParserException;
import org.sablecc.objectmacro.util.Utils;
import org.sablecc.objectmacro.walker.*;
import org.sablecc.util.Strictness;
import org.sablecc.util.Verbosity;

/**
 * The main class of ObjectMacro.
 */
public class ObjectMacro {

    private static GlobalIndex globalIndex = null;

    private static Macro macro = null;

    private static String DEFAULT_TABULATION = "    ";

    private static final Map<String, Integer> indexes_concrete_class = new HashMap<>();

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
                    e.getToken().getText(), e.getMessage().substring(start)).build());
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
                    e.getMessage().substring(start)).build());
            System.err.flush();
            System.exit(1);
        }
        catch (InternalException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            System.err.print(new MInternalError(sw.toString(), e.getMessage()).build());
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
        ast.apply(new VersionCollector(globalIndex));

        if(globalIndex.hasVersions()){
            ast.apply(new VersionnedDeclarationCollector(globalIndex));
            for(MacroVersion version : globalIndex.getAllVersions()){
                ast.apply(new MacroReferenceCollector(globalIndex, version));
                ast.apply(new ParamReferenceCollector(globalIndex, version));
                ast.apply(new DirectiveCollector(globalIndex, version));
                ast.apply(new VarVerifier(globalIndex, version));
            }
        }
        else{
            ast.apply(new DeclarationCollector(globalIndex));
            ast.apply(new MacroReferenceCollector(globalIndex, null));
            ast.apply(new ParamReferenceCollector(globalIndex, null));
            ast.apply(new DirectiveCollector(globalIndex, null));
            ast.apply(new VarVerifier(globalIndex, null));
        }


        ast.apply(new IntermediateObjectCollector(globalIndex));

        for (Macro macro : globalIndex.getAllMacros()) {
            macro.detectParamsCyclicReference();
        }


//        if(strictness == Strictness.STRICT){
//            for (Macro macro : globalIndex.getAllMacros()) {
//                Set<Param> allParamsInternals = new LinkedHashSet<>();
//                allParamsInternals.addAll(macro.getAllInternals());
//                allParamsInternals.addAll(macro.getAllParams());
//
//                for (Param param : allParamsInternals) {
//                    if (!param.isUsed()) {
//                        throw CompilerException.unusedParam(param);
//                    }
//                }
//            }
//        }

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

        MIntermediateRepresentation mIntermediateRepresentation = new MIntermediateRepresentation();
        Set<String> generated_abstract_macros = new HashSet<>();

        MVersions mVersions = new MVersions();

        if(globalIndex.getAllVersions().size() > 0){
            mIntermediateRepresentation.addVersionDefinition(mVersions);

            for(MacroVersion version : globalIndex.getAllVersions()){
                mVersions.addVersions(new MSimpleName(version.getName().getText()));
            }
        }

        for (Macro l_macro : globalIndex.getAllMacros()) {
            macro = l_macro;
            if((!globalIndex.isAllVersionned(l_macro.getName()))
                    && !generated_abstract_macros.contains(l_macro.getName())){

                mIntermediateRepresentation.addMacros(createAbstractMacro(l_macro));
                generated_abstract_macros.add(l_macro.getName());
            }

            mIntermediateRepresentation.addMacros(createMacro(l_macro));
        }

        String macroFileName = macroFile.getName();
        int length = macroFileName.length();

        String name = macroFile.getName().substring(0,
                length - ".objectmacro".length());

        File destination = new File(destinationDirectory,
                name.concat(".intermediate"));

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(mIntermediateRepresentation.build());
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }
    }

    private static Integer getIndexConcreteClass(
            String concrete_class_name){

        if(indexes_concrete_class.containsKey(concrete_class_name)){
            Integer index_concrete_class = indexes_concrete_class.get(concrete_class_name);
            indexes_concrete_class.put(concrete_class_name, index_concrete_class + 1);
            return index_concrete_class + 1;
        }
        else {
            indexes_concrete_class.put(concrete_class_name, 1);
            return 1;
        }
    }

    private static MName buildName(TIdentifier identifier){
        MName name = new MName();
        String splittedMacroName[] = Utils.splitName(identifier);
        for(String part : splittedMacroName){
            MSimpleName simpleName = new MSimpleName(part);
            name.addValue(simpleName);
        }

        return name;
    }

    private static MMacro createAbstractMacro(
            Macro macro){

        MMacro mMacro = new MMacro();
        mMacro.addIsAbstract(new MIsAbstract());
        mMacro.addMacroName(buildName(macro.getNameDeclaration()));
        Set<External> macro_params = macro.getAllParams();

        for(External param : macro_params){
            mMacro.addParameters(createParam(param));
        }

        return mMacro;
    }

    private static MMacro createMacro(
            Macro macro) {

        MMacro mMacro = new MMacro();
        MName mMacroName;
        Integer index_concrete_class = getIndexConcreteClass(macro.getName());
        MVersions mVersions = new MVersions();

        if(globalIndex.isAllVersionned(macro.getName())) {
            mMacroName = buildName(macro.getNameDeclaration());
            mMacro.addIsAllVersionned(new MIsAllVersionned());
        }
        else {
            mMacroName = buildName(new TIdentifier(macro.getName().concat(index_concrete_class.toString())));
            MParentName mParentName = new MParentName();
            mParentName.addParent(buildName(macro.getNameDeclaration()));
            mMacro.addParentName(mParentName);
        }

        mMacro.addMacroName(mMacroName);

        Set<Internal> macro_internals = macro.getAllInternals();
        Set<External> macro_params = macro.getAllParams();
        Set<MacroVersion> versions = macro.getVersions();

        List<PMacroBodyPart> macroBodyParts = macro.getDeclaration().getMacroBodyParts();

        createMacroBody(mMacro, macroBodyParts);

        for(External param : macro_params){
            mMacro.addParameters(createParam(param));
        }

        for(Internal internal : macro_internals){
            mMacro.addInternals(createInternal(internal));
        }

        if(versions.size() > 0
                && !globalIndex.isAllVersionned(macro.getName())){

            mMacro.addVersions(mVersions);
            for(MacroVersion version : versions){
                mVersions.addVersions(new MSimpleName(version.getName().getText()));
            }
        }

        return mMacro;
    }

    private static MParam createParam(
            External param){

        MParam macro_param = new MParam();
        macro_param.addParamName(buildName(param.getNameDeclaration()));

        if(param.isString()){
            macro_param.addType(new MStringType());
        }
        else{
            MMacroType macro_param_type = new MMacroType();
            Set<AMacroReference> macroReferences = param.getMacroReferences();

            for(AMacroReference l_macroRef : macroReferences){
                macro_param_type.addReferences(createMacroReference(l_macroRef));
            }

            macro_param.addType(macro_param_type);
        }

        Set<Directive> directives = param.getAllDirectives();
        for(Directive l_directive : directives){
            macro_param.addDirectives(createDirective(l_directive));
        }

        return macro_param;
    }

    private static MInternal createInternal(
            Internal internal){

        MInternal macro_internal = new MInternal();
        macro_internal.addInternalName(buildName(internal.getNameDeclaration()));

        if(internal.isString()){
            macro_internal.addType(new MStringType());
        }
        else{
            MMacroType macro_param_type = new MMacroType();
            Set<AMacroReference> macroReferences = internal.getMacroReferences();

            for(AMacroReference l_macroRef : macroReferences){
                macro_param_type.addReferences(createMacroReference(l_macroRef));
            }

            macro_internal.addType(macro_param_type);
        }

        return macro_internal;
    }

    private static void createMacroBody(
            MMacro mMacro,
            List<PMacroBodyPart> macroBodyParts) {

        for (PMacroBodyPart bodyPart : macroBodyParts) {
            if (bodyPart instanceof AEscapeMacroBodyPart) {
                AEscapeMacroBodyPart escapeMacroBodyPart = (AEscapeMacroBodyPart) bodyPart;

                if(escapeMacroBodyPart.getTextEscape().getText().equals("{{")){
                    mMacro.addBody(new MStringPart("{"));
                }
                else {
                    throw new InternalException("case unhandled");
                }
            }
            else if (bodyPart instanceof ATextMacroBodyPart){
                ATextMacroBodyPart textBodyPart = (ATextMacroBodyPart) bodyPart;

                String macroTextPart = textBodyPart.getTextPart().getText().replaceAll("'","\\\\'");
                mMacro.addBody(new MStringPart(macroTextPart));
            }
            else if (bodyPart instanceof AInsertMacroBodyPart){
                AInsertMacroBodyPart insertPart = (AInsertMacroBodyPart) bodyPart;
                AMacroReference macroReference = (AMacroReference) insertPart.getMacroReference();
                MMacroInsert macroInsert = new MMacroInsert();

                macroInsert.addReferencedMacro(createMacroReference(macroReference));
                mMacro.addBody(macroInsert);
            }
            else if (bodyPart instanceof AVarMacroBodyPart){
                AVarMacroBodyPart aVarMacroBodyPart = (AVarMacroBodyPart) bodyPart;
                String varNames[] = Utils.getVarName(aVarMacroBodyPart.getVariable()).split(Utils.NAME_SEPARATOR);
                MName mName = new MName();

                for(String part : varNames){
                    mName.addValue(new MSimpleName(part));
                }

                MParamInsert mParamInsert = new MParamInsert();
                mParamInsert.addReferencedParam(mName);

                mMacro.addBody(mParamInsert);
            }
            else if (bodyPart instanceof AEolMacroBodyPart){
                mMacro.addBody(new MEolPart());
            }
            else if (bodyPart instanceof AIndentMacroBodyPart){
                AIndentMacroBodyPart indentPart = (AIndentMacroBodyPart) bodyPart;
                MIndentPart mIndentPart = new MIndentPart();
                mMacro.addBody(mIndentPart);
                List<org.sablecc.objectmacro.intermediate.macro.Macro> text_parts = createTextParts(indentPart.getStringPart());

                for(org.sablecc.objectmacro.intermediate.macro.Macro macro : text_parts){
                    if(macro instanceof MStringPart){
                        mIndentPart.addIndentationText((MStringPart) macro);
                    }
                    else if(macro instanceof MEolPart){
                        mIndentPart.addIndentationText((MEolPart) macro);
                    }
                    else if(macro instanceof MMacroInsert){
                        mIndentPart.addIndentationText((MMacroInsert) macro);
                    }
                    else if(macro instanceof MParamInsert){
                        mIndentPart.addIndentationText((MParamInsert) macro);
                    }
                }
                createMacroBody(mMacro, indentPart.getMacroBodyPart());
                mMacro.addBody(new MEndIndentPart());
            }
            else {
                throw new InternalException("case unhandled");
            }
        }
    }

    private static List<org.sablecc.objectmacro.intermediate.macro.Macro> createTextParts(
            List<PStringPart> stringParts){

        List<org.sablecc.objectmacro.intermediate.macro.Macro> macro_string_parts = new LinkedList<>();

        for(PStringPart stringPart : stringParts){
            if(stringPart instanceof ATextStringPart){
                String stringPartText = ((ATextStringPart) stringPart).getText().getText();

                stringPartText = stringPartText.replaceAll("'", "\\\\'");
                macro_string_parts.add(new MStringPart(stringPartText));
            }
            else if(stringPart instanceof AInsertStringPart){
                AMacroReference macro_node = (AMacroReference) ((AInsertStringPart) stringPart).getMacro();
                MMacroInsert mMacroInsert = new MMacroInsert();
                mMacroInsert.addReferencedMacro(createMacroReference(macro_node));
                macro_string_parts.add(mMacroInsert);
            }
            else if (stringPart instanceof AVarStringPart) {
                TVariable tVariable = ((AVarStringPart) stringPart)
                        .getVariable();

                MParamInsert mParamInsert = new MParamInsert();
                String splittedVarName[] = Utils.getVarName(tVariable).split(Utils.NAME_SEPARATOR);
                MName name = new MName();
                mParamInsert.addReferencedParam(name);

                for(String part : splittedVarName){
                    name.addValue(new MSimpleName(part));
                }

                macro_string_parts.add(mParamInsert);
            }
            else if (stringPart instanceof AEscapeStringPart) {
                AEscapeStringPart escapeStringPart = (AEscapeStringPart) stringPart;
                String text = escapeStringPart.getStringEscape().getText();

                if(text.equals("\\\\")){
                    macro_string_parts.add(new MStringPart("\\"));
                }
                else if(text.equals("\\n")){
                    macro_string_parts.add(new MEolPart());
                }
                else if(text.equals("\\t")){
                    macro_string_parts.add(new MStringPart(DEFAULT_TABULATION));
                }
                else if(text.startsWith("\\")){
                    macro_string_parts.add(new MStringPart(text.substring(text.length() - 1)));
                }
                else {
                    throw new InternalException("case unhandled");
                }
            }
            else {
                throw new InternalException("case unhandled");
            }
        }

        return macro_string_parts;
    }

    private static MArgs createArgs(
            AMacroReference node){

        MArgs mArgs = new MArgs();
        Macro macroReferenced;

        if(globalIndex.hasVersions()){
            macroReferenced = getMacro(node.getName());
        }
        else{
            macroReferenced = globalIndex.getMacro(node.getName(), null);
        }

        List<String> paramNames = macroReferenced.getInternalsName();
        List<PStaticValue> arguments = node.getValues();
        int i = 0;

        for (PStaticValue argument : arguments) {
            if (argument instanceof AStringStaticValue) {
                AStringStaticValue stringValue = (AStringStaticValue) argument;

                MTextArgument textArgument = new MTextArgument(paramNames.get(i));

                List<org.sablecc.objectmacro.intermediate.macro.Macro> text_parts = createTextParts(stringValue.getParts());

                for(org.sablecc.objectmacro.intermediate.macro.Macro macro : text_parts){
                    if(macro instanceof MStringPart){
                        textArgument.addTextParts((MStringPart) macro);
                    }
                    else if(macro instanceof MEolPart){
                        textArgument.addTextParts((MEolPart) macro);
                    }
                    else if(macro instanceof MMacroInsert){
                        textArgument.addTextParts((MMacroInsert) macro);
                    }
                    else if(macro instanceof MParamInsert){
                        textArgument.addTextParts((MParamInsert) macro);
                    }
                }

                mArgs.addArguments(textArgument);
            }
            else if (argument instanceof AVarStaticValue) {
                AVarStaticValue varValue = (AVarStaticValue) argument;

                MVarArgument varArgument = new MVarArgument(paramNames.get(i));
                varArgument.addReferencedParam(buildName(varValue.getIdentifier()));
                mArgs.addArguments(varArgument);
            }
            i++;
        }

        return mArgs;
    }

    private static MMacroRef createMacroReference(
            AMacroReference node){

        MMacroRef macroRef = new MMacroRef();
        macroRef.addReferencedMacroName(buildName(node.getName()));

        if(node.getValues().size() > 0){
            macroRef.addArguments(createArgs(node));
        }

        return macroRef;
    }

    private static MDirective createDirective(
            Directive directive){

        MDirective mDirective = new MDirective();

        mDirective.addDirectiveName(buildName(directive.getDeclaration().getName()));

        List<org.sablecc.objectmacro.intermediate.macro.Macro> text_parts = createTextParts(directive.getDeclaration().getParts());

        for(org.sablecc.objectmacro.intermediate.macro.Macro macro : text_parts){
            if(macro instanceof MStringPart){
                mDirective.addDirectiveTextParts((MStringPart) macro);
            }
            else if(macro instanceof MEolPart){
                mDirective.addDirectiveTextParts((MEolPart) macro);
            }
            else if(macro instanceof MMacroInsert){
                mDirective.addDirectiveTextParts((MMacroInsert) macro);
            }
            else if(macro instanceof MParamInsert){
                mDirective.addDirectiveTextParts((MParamInsert) macro);
            }
        }
        return mDirective;
    }

    private static Macro getMacro(
            TIdentifier name){

        Iterator<MacroVersion> versionIterator = macro.getVersions().iterator();
        Macro toReturn = null;
        while(versionIterator.hasNext()){
            MacroVersion macroVersion = versionIterator.next();
            toReturn = macroVersion.getMacroOrNull(name);
            if(toReturn != null
                    && toReturn.getAllInternals().size() > 0){
                break;
            }
        }

        return toReturn;
    }
}
