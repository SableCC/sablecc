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

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.errormessage.*;
import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.intermediate.macro.*;
import org.sablecc.objectmacro.intermediate.macro.Macro;
import org.sablecc.objectmacro.intermediate.macro.Macros;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.structure.Directive;
import org.sablecc.objectmacro.syntax3.lexer.*;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.syntax3.parser.*;
import org.sablecc.objectmacro.util.*;
import org.sablecc.objectmacro.walker.*;
import org.sablecc.util.*;

/**
 * The main class of ObjectMacro.
 */
public class ObjectMacro {

    private static GlobalIndex globalIndex = null;

    private static MacroInfo macroInfo = null;

    private static String DEFAULT_TABULATION = "    ";

    private static final Map<String, Integer> indexes_concrete_class
            = new HashMap<>();

    private static final Macros factory = new Macros();

    /** Prevents instantiation of this class. */
    private ObjectMacro() {

        throw new InternalException("this class may not have instances");
    }

    /** Launches ObjectMacro. */
    public static void main(
            String[] args) {

        org.sablecc.objectmacro.errormessage.Macros errorFactory
                = new org.sablecc.objectmacro.errormessage.Macros();
        try {
            ObjectMacro.compile(args);
        }
        catch (CompilerException e) {
            System.err.print(e.getMessage());
            System.err.println();
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
            mSyntaxError.addTokenType(e.getToken().getClass().getSimpleName()
                    .substring(1).toLowerCase());

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
     * Parses the provided arguments and launches macroInfo compilation.
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

        if (globalIndex.hasVersions()) {
            ast.apply(new VersionnedDeclarationCollector(globalIndex));
            for (MacroVersion version : globalIndex.getAllVersions()) {
                ast.apply(new MacroReferenceCollector(globalIndex, version));
                ast.apply(new ParamReferenceCollector(globalIndex, version));
                ast.apply(new DirectiveCollector(globalIndex, version));
                ast.apply(new VarVerifier(globalIndex, version));
            }
        }
        else {
            ast.apply(new DeclarationCollector(globalIndex));
            ast.apply(new MacroReferenceCollector(globalIndex, null));
            ast.apply(new ParamReferenceCollector(globalIndex, null));
            ast.apply(new DirectiveCollector(globalIndex, null));
            ast.apply(new VarVerifier(globalIndex, null));
        }

        ast.apply(new IntermediateObjectCollector(globalIndex));

        for (MacroInfo macroInfo : globalIndex.getAllMacroInfos()) {
            macroInfo.detectParamsCyclicReference();
        }

        if (strictness == Strictness.STRICT) {
            for (MacroInfo macroInfo : globalIndex.getAllMacroInfos()) {
                Set<Param> allParamsInternals = new LinkedHashSet<>();
                allParamsInternals.addAll(macroInfo.getAllInternals());
                allParamsInternals.addAll(macroInfo.getAllParams());

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

        MIntermediateRepresentation mIntermediateRepresentation
                = factory.newIntermediateRepresentation();
        Set<String> generated_abstract_macros = new HashSet<>();

        MVersions mVersions = factory.newVersions();

        if (globalIndex.getAllVersions().size() > 0) {
            mIntermediateRepresentation.addVersionDefinition(mVersions);

            for (MacroVersion version : globalIndex.getAllVersions()) {
                MSimpleName mSimpleName = factory.newSimpleName();
                mSimpleName.addName(version.getName().getText());
                mVersions.addVersions(mSimpleName);
            }
        }

        for (MacroInfo l_macroInfo : globalIndex.getAllMacroInfos()) {
            macroInfo = l_macroInfo;
            if (!globalIndex.isAllVersionned(l_macroInfo.getName())
                    && !generated_abstract_macros
                            .contains(l_macroInfo.getName())) {

                mIntermediateRepresentation
                        .addDefinedMacros(createAbstractMacro(l_macroInfo));
                generated_abstract_macros.add(l_macroInfo.getName());
            }

            mIntermediateRepresentation
                    .addDefinedMacros(createMacro(l_macroInfo));
        }

        String macroFileName = macroFile.getName();
        int length = macroFileName.length();

        String name = macroFile.getName().substring(0,
                length - ".objectmacro".length());

        File destination
                = new File(destinationDirectory, name.concat(".intermediate"));

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
            String concrete_class_name) {

        if (indexes_concrete_class.containsKey(concrete_class_name)) {
            Integer index_concrete_class
                    = indexes_concrete_class.get(concrete_class_name);
            indexes_concrete_class.put(concrete_class_name,
                    index_concrete_class + 1);
            return index_concrete_class + 1;
        }
        else {
            indexes_concrete_class.put(concrete_class_name, 1);
            return 1;
        }
    }

    private static MName buildName(
            TIdentifier identifier) {

        MName name = factory.newName();
        String splittedMacroName[] = Utils.splitName(identifier);
        for (String part : splittedMacroName) {
            MSimpleName simpleName = factory.newSimpleName();
            simpleName.addName(part);
            name.addValue(simpleName);
        }

        return name;
    }

    private static MMacro createAbstractMacro(
            MacroInfo macroInfo) {

        MMacro mMacro = factory.newMacro();
        mMacro.addIsAbstract(factory.newIsAbstract());
        mMacro.addMacroName(buildName(macroInfo.getNameDeclaration()));
        Set<External> macro_params = macroInfo.getAllParams();

        for (External param : macro_params) {
            mMacro.addParameters(createParam(param));
        }

        return mMacro;
    }

    private static MMacro createMacro(
            MacroInfo macroInfo) {

        MMacro mMacro = factory.newMacro();
        MName mMacroName;
        Integer index_concrete_class
                = getIndexConcreteClass(macroInfo.getName());
        MVersions mVersions = factory.newVersions();

        if (globalIndex.isAllVersionned(macroInfo.getName())) {
            mMacroName = buildName(macroInfo.getNameDeclaration());
            mMacro.addIsAllVersionned(factory.newIsAllVersionned());
        }
        else {
            mMacroName = buildName(new TIdentifier(macroInfo.getName()
                    .concat(index_concrete_class.toString())));
            MParentName mParentName = factory.newParentName();
            mParentName.addParent(buildName(macroInfo.getNameDeclaration()));
            mMacro.addParentName(mParentName);
        }

        mMacro.addMacroName(mMacroName);

        Set<Internal> macro_internals = macroInfo.getAllInternals();
        Set<External> macro_params = macroInfo.getAllParams();
        Set<MacroVersion> versions = macroInfo.getVersions();

        List<PMacroBodyPart> macroBodyParts
                = macroInfo.getDeclaration().getMacroBodyParts();

        createMacroBody(mMacro, macroBodyParts);

        for (External param : macro_params) {
            mMacro.addParameters(createParam(param));
        }

        for (Internal internal : macro_internals) {
            mMacro.addInternals(createInternal(internal));
        }

        if (versions.size() > 0
                && !globalIndex.isAllVersionned(macroInfo.getName())) {

            mMacro.addVersions(mVersions);
            for (MacroVersion version : versions) {
                MSimpleName mSimpleName = factory.newSimpleName();
                mSimpleName.addName(version.getName().getText());
                mVersions.addVersions(mSimpleName);
            }
        }

        return mMacro;
    }

    private static MParam createParam(
            External param) {

        MParam macro_param = factory.newParam();
        macro_param.addParamName(buildName(param.getNameDeclaration()));

        if (param.isString()) {
            macro_param.addType(factory.newStringType());
        }
        else {
            MMacroType macro_param_type = factory.newMacroType();
            Set<AMacroReference> macroReferences = param.getMacroReferences();

            for (AMacroReference l_macroRef : macroReferences) {
                macro_param_type
                        .addReferences(createMacroReference(l_macroRef));
            }

            macro_param.addType(macro_param_type);
        }

        Set<Directive> directives = param.getAllDirectives();
        for (Directive l_directive : directives) {
            macro_param.addDirectives(createDirective(l_directive));
        }

        return macro_param;
    }

    private static MInternal createInternal(
            Internal internal) {

        MInternal macro_internal = factory.newInternal();
        macro_internal
                .addInternalName(buildName(internal.getNameDeclaration()));

        if (internal.isString()) {
            macro_internal.addType(factory.newStringType());
        }
        else {
            MMacroType macro_param_type = factory.newMacroType();
            Set<AMacroReference> macroReferences
                    = internal.getMacroReferences();

            for (AMacroReference l_macroRef : macroReferences) {
                macro_param_type
                        .addReferences(createMacroReference(l_macroRef));
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
                AEscapeMacroBodyPart escapeMacroBodyPart
                        = (AEscapeMacroBodyPart) bodyPart;

                if (escapeMacroBodyPart.getTextEscape().getText()
                        .equals("{{")) {
                    MStringPart mStringPart = factory.newStringPart();
                    mStringPart.addText("{");
                    mMacro.addBody(mStringPart);
                }
                else {
                    throw new InternalException("case unhandled");
                }
            }
            else if (bodyPart instanceof ATextMacroBodyPart) {
                ATextMacroBodyPart textBodyPart = (ATextMacroBodyPart) bodyPart;

                String macroTextPart = textBodyPart.getTextPart().getText();
                macroTextPart = macroTextPart.replace("\\", "\\\\");
                macroTextPart = macroTextPart.replace("'", "\\'");
                MStringPart mStringPart = factory.newStringPart();
                mStringPart.addText(macroTextPart);

                mMacro.addBody(mStringPart);
            }
            else if (bodyPart instanceof AInsertMacroBodyPart) {
                AInsertMacroBodyPart insertPart
                        = (AInsertMacroBodyPart) bodyPart;
                AMacroReference macroReference
                        = (AMacroReference) insertPart.getMacroReference();
                MMacroInsert macroInsert = factory.newMacroInsert();

                macroInsert.addReferencedMacro(
                        createMacroReference(macroReference));
                mMacro.addBody(macroInsert);
            }
            else if (bodyPart instanceof AVarMacroBodyPart) {
                AVarMacroBodyPart aVarMacroBodyPart
                        = (AVarMacroBodyPart) bodyPart;
                String varNames[]
                        = Utils.getVarName(aVarMacroBodyPart.getVariable())
                                .split(Utils.NAME_SEPARATOR);
                MName mName = factory.newName();

                for (String part : varNames) {
                    MSimpleName mSimpleName = factory.newSimpleName();
                    mSimpleName.addName(part);
                    mName.addValue(mSimpleName);
                }

                MParamInsert mParamInsert = factory.newParamInsert();
                mParamInsert.addReferencedParam(mName);

                mMacro.addBody(mParamInsert);
            }
            else if (bodyPart instanceof AEolMacroBodyPart) {
                mMacro.addBody(factory.newEolPart());
            }
            else if (bodyPart instanceof AIndentMacroBodyPart) {
                AIndentMacroBodyPart indentPart
                        = (AIndentMacroBodyPart) bodyPart;
                MIndentPart mIndentPart = factory.newIndentPart();
                mMacro.addBody(mIndentPart);
                List<Macro> text_parts
                        = createTextParts(indentPart.getStringPart());

                mIndentPart.addAllIndentationText(text_parts);
                createMacroBody(mMacro, indentPart.getMacroBodyPart());
                mMacro.addBody(factory.newEndIndentPart());
            }
            else {
                throw new InternalException("case unhandled");
            }
        }
    }

    private static List<Macro> createTextParts(
            List<PStringPart> stringParts) {

        List<Macro> macro_string_parts = new LinkedList<>();

        for (PStringPart stringPart : stringParts) {
            if (stringPart instanceof ATextStringPart) {
                String stringPartText
                        = ((ATextStringPart) stringPart).getText().getText();

                stringPartText = stringPartText.replaceAll("'", "\\\\'");
                MStringPart mStringPart = factory.newStringPart();
                mStringPart.addText(stringPartText);
                macro_string_parts.add(mStringPart);
            }
            else if (stringPart instanceof AInsertStringPart) {
                AMacroReference macro_node
                        = (AMacroReference) ((AInsertStringPart) stringPart)
                                .getMacro();
                MMacroInsert mMacroInsert = factory.newMacroInsert();
                mMacroInsert
                        .addReferencedMacro(createMacroReference(macro_node));
                macro_string_parts.add(mMacroInsert);
            }
            else if (stringPart instanceof AVarStringPart) {
                TVariable tVariable
                        = ((AVarStringPart) stringPart).getVariable();

                MParamInsert mParamInsert = factory.newParamInsert();
                String splittedVarName[] = Utils.getVarName(tVariable)
                        .split(Utils.NAME_SEPARATOR);
                MName name = factory.newName();
                mParamInsert.addReferencedParam(name);

                for (String part : splittedVarName) {
                    MSimpleName mSimpleName = factory.newSimpleName();
                    mSimpleName.addName(part);
                    name.addValue(mSimpleName);
                }

                macro_string_parts.add(mParamInsert);
            }
            else if (stringPart instanceof AEscapeStringPart) {
                AEscapeStringPart escapeStringPart
                        = (AEscapeStringPart) stringPart;
                String text = escapeStringPart.getStringEscape().getText();
                MStringPart mStringPart = factory.newStringPart();

                if (text.equals("\\\\")) {
                    mStringPart.addText("\\");
                    macro_string_parts.add(mStringPart);
                }
                else if (text.equals("\\n")) {
                    macro_string_parts.add(factory.newEolPart());
                }
                else if (text.equals("\\t")) {
                    mStringPart.addText(DEFAULT_TABULATION);
                    macro_string_parts.add(mStringPart);
                }
                else if (text.startsWith("\\")) {
                    mStringPart.addText(text.substring(text.length() - 1));
                    macro_string_parts.add(mStringPart);
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
            AMacroReference node) {

        MArgs mArgs = factory.newArgs();
        MacroInfo macro_referenced;

        if (globalIndex.hasVersions()) {
            macro_referenced = getMacro(node.getName());
        }
        else {
            macro_referenced = globalIndex.getMacro(node.getName(), null);
        }

        List<String> paramNames = macro_referenced.getInternalsName();
        List<PStaticValue> arguments = node.getValues();
        int i = 0;

        for (PStaticValue argument : arguments) {
            if (argument instanceof AStringStaticValue) {
                AStringStaticValue stringValue = (AStringStaticValue) argument;

                MTextArgument textArgument = factory.newTextArgument();
                textArgument.addParamName(paramNames.get(i));
                List<Macro> text_parts
                        = createTextParts(stringValue.getParts());

                textArgument.addAllTextParts(text_parts);

                mArgs.addArguments(textArgument);
            }
            else if (argument instanceof AVarStaticValue) {
                AVarStaticValue varValue = (AVarStaticValue) argument;

                MVarArgument varArgument = factory.newVarArgument();
                varArgument.addParamName(paramNames.get(i));
                varArgument.addReferencedParam(
                        buildName(varValue.getIdentifier()));
                mArgs.addArguments(varArgument);
            }
            i++;
        }

        return mArgs;
    }

    private static MMacroRef createMacroReference(
            AMacroReference node) {

        MMacroRef macroRef = factory.newMacroRef();
        macroRef.addReferencedMacroName(buildName(node.getName()));

        if (node.getValues().size() > 0) {
            macroRef.addArguments(createArgs(node));
        }

        return macroRef;
    }

    private static MDirective createDirective(
            Directive directive) {

        MDirective mDirective = factory.newDirective();
        mDirective.addDirectiveName(
                buildName(directive.getDeclaration().getName()));
        List<Macro> text_parts
                = createTextParts(directive.getDeclaration().getParts());
        mDirective.addAllDirectiveTextParts(text_parts);

        return mDirective;
    }

    private static MacroInfo getMacro(
            TIdentifier name) {

        Iterator<MacroVersion> versionIterator
                = macroInfo.getVersions().iterator();
        MacroInfo toReturn = null;
        while (versionIterator.hasNext()) {
            MacroVersion macroVersion = versionIterator.next();
            toReturn = macroVersion.getMacroOrNull(name);
            if (toReturn != null && toReturn.getAllInternals().size() > 0) {
                break;
            }
        }

        return toReturn;
    }
}
