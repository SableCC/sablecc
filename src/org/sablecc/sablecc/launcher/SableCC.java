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
import static org.sablecc.sablecc.util.Utils.*;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.automaton.State;
import org.sablecc.sablecc.codegeneration.java.macro.*;
import org.sablecc.sablecc.errormessage.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.lrautomaton.*;
import org.sablecc.sablecc.lrautomaton.Alternative;
import org.sablecc.sablecc.lrautomaton.Element;
import org.sablecc.sablecc.lrautomaton.Production;
import org.sablecc.sablecc.lrautomaton.Token;
import org.sablecc.sablecc.structure.*;
import org.sablecc.sablecc.syntax3.lexer.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.sablecc.syntax3.parser.*;
import org.sablecc.sablecc.walker.*;
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
                    .getToken().getPos()
                    + "", e.getToken().getClass().getSimpleName().substring(1),
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

        switch (verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println();
            System.out.println("SableCC version " + VERSION);
            System.out
                    .println("by Etienne M. Gagnon <egagnon@j-meg.com> and other contributors.");
            System.out.println();
            break;
        }

        // handle text arguments
        if (argumentCollection.getTextArguments().size() == 0) {
            System.out.println("Usage: sablecc " + Option.getShortHelpMessage()
                    + " grammar.sablecc");
            return;
        }
        else if (argumentCollection.getTextArguments().size() > 1) {
            throw CompilerException.invalidArgumentCount();
        }

        // check target
        if (!targetLanguage.equals("java")) {
            throw CompilerException.unknownTarget(targetLanguage);
        }

        // check argument
        TextArgument textArgument = argumentCollection.getTextArguments()
                .get(0);

        if (!textArgument.getText().endsWith(".sablecc")) {
            throw CompilerException.invalidSuffix(textArgument.getText());
        }

        File grammarFile = new File(textArgument.getText());

        if (!grammarFile.exists()) {
            throw CompilerException.missingGrammarFile(textArgument.getText());
        }

        if (!grammarFile.isFile()) {
            throw CompilerException.grammarNotFile(textArgument.getText());
        }

        compile(grammarFile, targetLanguage, destinationDirectory,
                destinationPackage, generateCode, strictness, verbosity);
    }

    /**
     * Compiles the provided grammar file.
     */
    private static void compile(
            File grammarFile,
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
            System.out.println("Compiling \"" + grammarFile + "\"");
            break;
        }

        Start ast;

        try {
            FileReader fr = new FileReader(grammarFile);
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
            throw CompilerException.inputError(grammarFile.toString(), e);
        }

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Verifying semantics");
            break;
        }

        GlobalIndex globalIndex = verifySemantics(ast, strictness);

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Computing lexer");
            break;
        }

        Automaton lexer = computeLexer(globalIndex, verbosity);

        switch (verbosity) {
        case VERBOSE:
            System.out.println(" Computing parser");
            break;
        }

        LRAutomaton parser = computeParser(globalIndex, verbosity);

        if (generateCode) {
            switch (verbosity) {
            case VERBOSE:
                System.out.println(" Generating code");
                break;
            }

            if (targetLanguage.equals("java")) {
                generateJavaCode(destinationDirectory, destinationPackage,
                        globalIndex, lexer, parser);
            }
            else {
                throw new InternalException("unimplemented");
            }
        }

        switch (verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println("Done compiling \"" + grammarFile + "\"");
            break;
        }
    }

    private static GlobalIndex verifySemantics(
            Start ast,
            Strictness strictness) {

        GlobalIndex globalIndex = new GlobalIndex();

        new SimpleLexerAndParserRestricter().visit(ast);

        new GlobalDeclarationCollector(globalIndex).visit(ast);
        new LexerDeclarationCollector(globalIndex).visit(ast);
        new LexerPriorityCollector(globalIndex).visit(ast);
        new ParserDeclarationCollector(globalIndex).visit(ast);
        new ParserPriorityCollector(globalIndex).visit(ast);

        new ExpressionVerifier(globalIndex).visit(ast);
        new CyclicExpressionDetector(globalIndex).visit(ast);

        return globalIndex;
    }

    private static Automaton computeLexer(
            GlobalIndex globalIndex,
            Verbosity verbosity) {

        for (NormalExpression normalExpression : globalIndex
                .getNormalNamedExpressionLinearization()) {

            switch (verbosity) {
            case VERBOSE:
                System.out.println("  - "
                        + normalExpression.getNameToken().getText());
                break;
            }

            Automaton automaton = RegularExpressionEvaluator
                    .evaluateExpression(globalIndex, normalExpression
                            .getExpression());

            normalExpression.setAutomaton(automaton);
        }

        switch (verbosity) {
        case VERBOSE:
            System.out.println("  Computing automaton");
            break;
        }

        Context context = globalIndex.getContexts().iterator().next();
        Automaton lexerAutomaton = Automaton.getEmptyAutomaton();

        for (MatchedToken matchedToken : context.getMatchedTokens()) {
            lexerAutomaton = lexerAutomaton.or(matchedToken.getAutomaton());
        }

        switch (verbosity) {
        case VERBOSE:
            System.out.println("  Minimizing automaton");
            break;
        }

        lexerAutomaton = lexerAutomaton.withPriorities(context).withMarkers()
                .minimal();

        return lexerAutomaton;
    }

    private static LRAutomaton computeParser(
            GlobalIndex globalIndex,
            Verbosity verbosity) {

        Grammar grammar = globalIndex.getGrammar();

        switch (verbosity) {
        case VERBOSE:
            System.out.println("  Detecting useless productions");
            break;
        }

        grammar.computeShortestLengthAndDetectUselessProductions();

        return new LRAutomaton(grammar, verbosity);
    }

    private static void generateJavaCode(
            File destinationDirectory,
            String destinationPackage,
            GlobalIndex globalIndex,
            Automaton lexer,
            LRAutomaton parser) {

        String languagePackageName = "language_"
                + globalIndex.getLanguage().get_camelCaseName();
        File packageDirectory;
        MNode mNode = new MNode();
        MToken mToken = new MToken();
        MState mState = new MState();
        MTransitionState mTransitionState = new MTransitionState();
        MFinalState mFinalState = new MFinalState();
        MSymbol mSymbol = new MSymbol();
        MLexer mLexer = new MLexer();
        MLexerException mLexerException = new MLexerException();
        MParserException mParserException = new MParserException();
        MTest mTest = new MTest();
        MEnd mEnd = new MEnd();
        MWalker mWalker = new MWalker();
        MParser mParser = new MParser();

        if (destinationPackage.equals("")) {
            packageDirectory = new File(destinationDirectory,
                    languagePackageName);
            mNode.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mToken.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mState.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mTransitionState.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mFinalState.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mSymbol.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mLexer.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mLexerException.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mParserException.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mTest.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mEnd.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mWalker.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mParser.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
        }
        else {
            packageDirectory = new File(destinationDirectory,
                    destinationPackage.replace('.', '/') + "/"
                            + languagePackageName);
            mNode.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mToken.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mState.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mTransitionState.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mFinalState.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mSymbol.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mLexer.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mLexerException.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mParserException.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mTest.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mEnd.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mWalker.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mParser.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
        }

        packageDirectory.mkdirs();

        Context context = globalIndex.getContexts().iterator().next();

        for (MatchedToken matchedToken : context.getMatchedTokens()) {
            if (!matchedToken.isIgnored()) {
                if (matchedToken instanceof NameToken) {
                    NameToken nameToken = (NameToken) matchedToken;

                    mNode.newNodeTypeEnumEntry(nameToken.get_CamelCaseName());
                    mNode.newNodeInternalTypeEnumEntry(nameToken
                            .get_CamelCaseName());

                    mWalker.newWalkerIn(nameToken.get_CamelCaseName());
                    mWalker.newWalkerCase(nameToken.get_CamelCaseName());
                    mWalker.newWalkerOut(nameToken.get_CamelCaseName());

                    MCustomToken mCustomToken = new MCustomToken(nameToken
                            .get_CamelCaseName());

                    if (destinationPackage.equals("")) {
                        mCustomToken.newDefaultPackage(globalIndex
                                .getLanguage().get_camelCaseName());
                    }
                    else {
                        mCustomToken.newSpecifiedPackage(globalIndex
                                .getLanguage().get_camelCaseName(),
                                destinationPackage);
                    }

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                new File(packageDirectory, "N"
                                        + nameToken.get_CamelCaseName()
                                        + ".java")));

                        bw.write(mCustomToken.toString());
                        bw.close();
                    }
                    catch (IOException e) {
                        throw CompilerException.outputError("N"
                                + nameToken.get_CamelCaseName() + ".java", e);
                    }
                }
                else {
                    AnonymousToken anonymousToken = (AnonymousToken) matchedToken;

                    mNode.newNodeInternalTypeEnumEntry(""
                            + anonymousToken.get_CamelCaseName());

                    MAnonymousToken mAnonymousToken = new MAnonymousToken(""
                            + anonymousToken.get_CamelCaseName());

                    if (destinationPackage.equals("")) {
                        mAnonymousToken.newDefaultPackage(globalIndex
                                .getLanguage().get_camelCaseName());
                    }
                    else {
                        mAnonymousToken.newSpecifiedPackage(globalIndex
                                .getLanguage().get_camelCaseName(),
                                destinationPackage);
                    }

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                new File(packageDirectory, "N"
                                        + anonymousToken.get_CamelCaseName()
                                        + ".java")));

                        bw.write(mAnonymousToken.toString());
                        bw.close();
                    }
                    catch (IOException e) {
                        throw CompilerException.outputError("N"
                                + anonymousToken.get_CamelCaseName() + ".java",
                                e);
                    }
                }
            }
        }

        for (Symbol symbol : lexer.getAlphabet().getSymbols()) {
            mSymbol.newSymbolDeclaration(symbol.getSimpleName());
        }

        for (Map.Entry<Interval, Symbol> entry : lexer.getAlphabet()
                .getIntervalToSymbolMap().entrySet()) {
            Interval interval = entry.getKey();
            Symbol symbol = entry.getValue();

            if (interval.getLowerBound() == Bound.MIN) {
                if (interval.getUpperBound() == Bound.MAX) {
                    mSymbol.newOpenInterval(symbol.getSimpleName());
                }
                else {
                    mSymbol.newOpenLeftInterval(interval.getUpperBound()
                            .getValue().toString(), symbol.getSimpleName());
                }
            }
            else if (interval.getUpperBound() == Bound.MAX) {
                mSymbol.newOpenRightInterval(interval.getLowerBound()
                        .getValue().toString(), symbol.getSimpleName());
            }
            else if (interval.getLowerBound().equals(interval.getUpperBound())) {
                mSymbol.newSingleChar(interval.getLowerBound().getValue()
                        .toString(), symbol.getSimpleName());
            }
            else {
                mSymbol.newInterval(interval.getLowerBound().getValue()
                        .toString(), interval.getUpperBound().getValue()
                        .toString(), symbol.getSimpleName());
            }
        }

        for (State state : lexer.getStates()) {
            if (state.isAcceptState()) {
                Acceptation acceptation = state.getAcceptations().first();
                MFinalStateSingleton mFinalStateSingleton = new MFinalStateSingleton(
                        "" + state.getId(), "" + acceptation.getBackCount());

                if (destinationPackage.equals("")) {
                    mFinalStateSingleton.newDefaultPackage(globalIndex
                            .getLanguage().get_camelCaseName());
                }
                else {
                    mFinalStateSingleton.newSpecifiedPackage(globalIndex
                            .getLanguage().get_camelCaseName(),
                            destinationPackage);
                }

                Marker marker = acceptation.getMarker();

                if (marker == null) {
                    mFinalStateSingleton.newAcceptTokenNoMarker();
                }
                else {
                    mFinalStateSingleton.newAcceptTokenWithMarker(marker
                            .getName());
                }

                MatchedToken matchedToken = context.getMatchedToken(acceptation
                        .getName());

                if (matchedToken.isIgnored()) {
                    mFinalStateSingleton.newAcceptIgnoredToken();
                }
                else {
                    if (matchedToken instanceof NameToken) {
                        NameToken nameToken = (NameToken) matchedToken;

                        mFinalStateSingleton.newAcceptNormalToken(nameToken
                                .get_CamelCaseName());
                    }
                    else {
                        AnonymousToken anonymousToken = (AnonymousToken) matchedToken;

                        mFinalStateSingleton.newAcceptNormalToken(""
                                + anonymousToken.get_CamelCaseName());
                    }
                }

                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            new File(packageDirectory, "S_" + state.getId()
                                    + ".java")));

                    bw.write(mFinalStateSingleton.toString());
                    bw.close();
                }
                catch (IOException e) {
                    throw CompilerException.outputError("S_" + state.getId()
                            + ".java", e);
                }
            }
            else {
                MTransitionStateSingleton mTransitionStateSingleton = new MTransitionStateSingleton(
                        "" + state.getId());

                if (destinationPackage.equals("")) {
                    mTransitionStateSingleton.newDefaultPackage(globalIndex
                            .getLanguage().get_camelCaseName());
                }
                else {
                    mTransitionStateSingleton.newSpecifiedPackage(globalIndex
                            .getLanguage().get_camelCaseName(),
                            destinationPackage);
                }

                Marker marker = state.getMarker();

                if (marker == null) {
                    mTransitionStateSingleton.newNoMarker();
                }
                else {
                    mTransitionStateSingleton.newSetMarker(marker.getName());
                }

                for (Entry<RichSymbol, SortedSet<State>> entry : state
                        .getTransitions().entrySet()) {
                    RichSymbol richSymbol = entry.getKey();
                    State target = state.getSingleTarget(richSymbol);
                    String symbolName = richSymbol == RichSymbol.END ? "end"
                            : richSymbol.getSymbol().getSimpleName();

                    mTransitionStateSingleton.newTransitionTarget(symbolName,
                            "" + target.getId());
                }

                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            new File(packageDirectory, "S_" + state.getId()
                                    + ".java")));

                    bw.write(mTransitionStateSingleton.toString());
                    bw.close();
                }
                catch (IOException e) {
                    throw CompilerException.outputError("S_" + state.getId()
                            + ".java", e);
                }
            }
        }

        for (Marker marker : lexer.getMarkers()) {
            mLexer.newMarkerDeclaration(marker.getName());
            mLexer.newSetMarkerDeclaration(marker.getName());
            mLexer.newAcceptMarkerDeclaration(marker.getName());
        }

        for (Production production : parser.getGrammar().getProductions()) {

            String production_CamelCaseName = to_CamelCase(production.getName());

            mNode.newNodeProductionTypeEnumEntry(production_CamelCaseName);

            // if production is not a single anonymous alternative
            if (production.getAlternatives().size() > 1
                    || !production.getAlternatives().iterator().next()
                            .getName().equals("")) {

                MProduction mProduction = new MProduction(
                        production_CamelCaseName);

                if (destinationPackage.equals("")) {
                    mProduction.newDefaultPackage(globalIndex.getLanguage()
                            .get_camelCaseName());
                }
                else {
                    mProduction.newSpecifiedPackage(globalIndex.getLanguage()
                            .get_camelCaseName(), destinationPackage);
                }

                if (production_CamelCaseName.indexOf('$') == -1) {
                    mProduction.newNamedProductionHeader();
                }
                else {
                    mProduction.newAnonymousProductionHeader();
                }

                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            new File(packageDirectory, "N"
                                    + production_CamelCaseName + ".java")));

                    bw.write(mProduction.toString());
                    bw.close();
                }
                catch (IOException e) {
                    throw CompilerException.outputError("N"
                            + production_CamelCaseName + ".java", e);
                }
            }

            for (Alternative alternative : production.getAlternatives()) {
                String alt_CamelCaseName = to_CamelCase(alternative.getName());
                String alt_CamelCaseFullName = production_CamelCaseName
                        + (alt_CamelCaseName.equals("") ? "" : "_"
                                + alt_CamelCaseName);
                boolean altIsPublic = alt_CamelCaseFullName.indexOf('$') == -1;
                boolean altExtendsNode = alt_CamelCaseFullName.indexOf('_') == -1;

                MAlternative mAlternative = new MAlternative(
                        alt_CamelCaseFullName);

                mAlternative.newAltProdType(production_CamelCaseName);

                if (altIsPublic) {
                    mWalker.newWalkerIn(alt_CamelCaseFullName);
                    mWalker.newWalkerCase(alt_CamelCaseFullName);
                    mWalker.newWalkerOut(alt_CamelCaseFullName);
                    mAlternative.newAltNormalApply();
                }
                else {
                    mAlternative.newAltAnonymousApply();
                }

                if (destinationPackage.equals("")) {
                    mAlternative.newDefaultPackage(globalIndex.getLanguage()
                            .get_camelCaseName());
                }
                else {
                    mAlternative.newSpecifiedPackage(globalIndex.getLanguage()
                            .get_camelCaseName(), destinationPackage);
                }

                mNode.newNodeInternalTypeEnumEntry(alt_CamelCaseFullName);
                if (altIsPublic) {
                    mNode.newNodeTypeEnumEntry(alt_CamelCaseFullName);
                    mAlternative.newPublic();
                    mAlternative.newNamedAltType();
                }
                else {
                    mAlternative.newAnonymousAltType();
                }

                if (altExtendsNode) {
                    mAlternative.newAlternativeNodeParent();
                }
                else {
                    mAlternative
                            .newAlternativeNamedParent(production_CamelCaseName);
                }

                boolean altHasPublicConstructor = true;
                for (Element element : alternative.getElements()) {
                    String element_CamelCaseName = to_CamelCase(element
                            .getName());
                    String element_CamelCaseType = null;
                    boolean elementIsEndToken;
                    boolean elementIsPublicReadable;
                    boolean elementIsPublicWritable;
                    if (element instanceof TokenElement) {
                        TokenElement tokenElement = (TokenElement) element;
                        if (tokenElement.getToken().getName().equals("$end")) {
                            elementIsEndToken = true;
                            elementIsPublicReadable = false;
                            elementIsPublicWritable = false;
                        }
                        else {
                            MatchedToken matchedToken = context
                                    .getMatchedToken(tokenElement.getToken()
                                            .getName());
                            if (matchedToken instanceof NameToken) {
                                NameToken nameToken = (NameToken) matchedToken;
                                element_CamelCaseType = nameToken
                                        .get_CamelCaseName();
                            }
                            else {
                                AnonymousToken anonymousToken = (AnonymousToken) matchedToken;

                                element_CamelCaseType = ""
                                        + anonymousToken.get_CamelCaseName();
                            }

                            elementIsEndToken = false;
                            elementIsPublicReadable = altIsPublic
                                    && element_CamelCaseName.indexOf('$') == -1;
                            elementIsPublicWritable = elementIsPublicReadable
                                    && element_CamelCaseType.indexOf('$') == -1;
                        }
                    }
                    else {
                        ProductionElement productionElement = (ProductionElement) element;
                        element_CamelCaseType = to_CamelCase(productionElement
                                .getProduction().getName());

                        elementIsEndToken = false;
                        elementIsPublicReadable = altIsPublic
                                && element_CamelCaseName.indexOf('$') == -1;
                        elementIsPublicWritable = elementIsPublicReadable
                                && element_CamelCaseType.indexOf('$') == -1;
                    }

                    if (!elementIsPublicWritable) {
                        altHasPublicConstructor = false;
                    }

                    if (elementIsEndToken) {
                        mAlternative.newEndConstructorParameter();
                        mAlternative.newEndContructorInitialization();

                        mAlternative.newEndElementDeclaration();
                        mAlternative.newEndElementAccessor();

                        mAlternative.newEndChildApply();
                    }
                    else {
                        mAlternative.newNormalConstructorParameter(
                                element_CamelCaseType, element_CamelCaseName);
                        mAlternative
                                .newNormalContructorInitialization(element_CamelCaseName);

                        mAlternative.newNormalElementDeclaration(
                                element_CamelCaseType, element_CamelCaseName);
                        mAlternative.newNormalElementAccessor(
                                element_CamelCaseType, element_CamelCaseName);

                        mAlternative.newNormalChildApply(element_CamelCaseName);

                        if (elementIsPublicReadable) {
                            MPublicElementAccessor publicElementAccessor = mAlternative
                                    .newPublicElementAccessor(element_CamelCaseName);
                            if (elementIsPublicWritable) {
                                publicElementAccessor
                                        .newPublicElementType(element_CamelCaseType);
                            }
                            else {
                                publicElementAccessor.newTokenElementType();
                            }
                        }
                    }
                }

                if (altHasPublicConstructor) {
                    mAlternative.newPublicConstructor();
                }

                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            new File(packageDirectory, "N"
                                    + alt_CamelCaseFullName + ".java")));

                    bw.write(mAlternative.toString());
                    bw.close();
                }
                catch (IOException e) {
                    throw CompilerException.outputError("N"
                            + alt_CamelCaseFullName + ".java", e);
                }
            }
        }

        for (LRState state : parser.getStates()) {
            MLrStateSingleton mLrStateSingleton = mParser
                    .newLrStateSingleton(state.getName());

            for (Entry<Token, LRState> entry : state.getTokenTransitions()
                    .entrySet()) {
                Token token = entry.getKey();
                LRState target = entry.getValue();

                if (token.getName().equals("$end")) {
                    mLrStateSingleton.newEndTokenLrTransitionTarget(target
                            .getName());
                }
                else {
                    MatchedToken matchedToken = context.getMatchedToken(token
                            .getName());
                    String element_CamelCaseType;
                    if (matchedToken instanceof NameToken) {
                        NameToken nameToken = (NameToken) matchedToken;
                        element_CamelCaseType = nameToken.get_CamelCaseName();
                    }
                    else {
                        AnonymousToken anonymousToken = (AnonymousToken) matchedToken;

                        element_CamelCaseType = ""
                                + anonymousToken.get_CamelCaseName();
                    }

                    mLrStateSingleton.newNormalTokenLrTransitionTarget(
                            element_CamelCaseType, target.getName());
                }
            }

            for (Entry<Production, LRState> entry : state
                    .getProductionTransitions().entrySet()) {
                Production production = entry.getKey();
                LRState target = entry.getValue();

                String production_CamelCaseName = to_CamelCase(production
                        .getName());
                mLrStateSingleton.newProductionLrTransitionTarget(
                        production_CamelCaseName, target.getName());
            }

            Map<Integer, MDistance> distanceMap = new LinkedHashMap<Integer, MDistance>();
            boolean isLr1OrMore = false;
            for (Action action : state.getActions()) {
                int maxLookahead = action.getMaxLookahead();
                while (maxLookahead > distanceMap.size() - 1) {
                    int distance = distanceMap.size();
                    distanceMap.put(distance, mLrStateSingleton.newDistance(""
                            + distance));
                }

                MDistance mDistance = distanceMap.get(maxLookahead);
                MAction mAction = mDistance.newAction();
                if (maxLookahead > 0) {
                    isLr1OrMore = true;
                    for (Entry<Integer, Set<Item>> entry : action
                            .getDistanceToItemSetMap().entrySet()) {
                        String ahead = "" + entry.getKey();
                        Set<Item> items = entry.getValue();
                        Set<Token> tokens = new LinkedHashSet<Token>();
                        for (Item item : items) {
                            tokens.add(item.getTokenElement().getToken());
                        }

                        if (tokens.size() == 0) {
                            mAction.newFalseGroup();
                        }
                        else {
                            MNormalGroup mNormalGroup = mAction
                                    .newNormalGroup();

                            for (Token token : tokens) {
                                if (token.getName().equals("$end")) {
                                    mNormalGroup.newEndCondition(ahead);
                                }
                                else {
                                    MatchedToken matchedToken = context
                                            .getMatchedToken(token.getName());
                                    String element_CamelCaseType;
                                    if (matchedToken instanceof NameToken) {
                                        NameToken nameToken = (NameToken) matchedToken;
                                        element_CamelCaseType = nameToken
                                                .get_CamelCaseName();
                                    }
                                    else {
                                        AnonymousToken anonymousToken = (AnonymousToken) matchedToken;

                                        element_CamelCaseType = ""
                                                + anonymousToken
                                                        .get_CamelCaseName();
                                    }

                                    mNormalGroup.newNormalCondition(ahead,
                                            element_CamelCaseType);
                                }
                            }
                        }
                    }
                }

                if (action.getType() == ActionType.SHIFT) {
                    mAction.newShift();
                }
                else {
                    ReduceAction reduceAction = (ReduceAction) action;
                    Alternative alternative = reduceAction.getAlternative();
                    Production production = alternative.getProduction();
                    String production_CamelCaseName = to_CamelCase(production
                            .getName());
                    String alt_CamelCaseName = to_CamelCase(alternative
                            .getName());
                    String alt_CamelCaseFullName = production_CamelCaseName
                            + (alt_CamelCaseName.equals("") ? "" : "_"
                                    + alt_CamelCaseName);

                    MReduce mReduce = mAction.newReduce(alt_CamelCaseFullName);

                    ArrayList<Element> elements = alternative.getElements();
                    int elementCount = elements.size();
                    for (int i = elementCount - 1; i >= 0; i--) {
                        Element element = elements.get(i);
                        String element_CamelCaseName = to_CamelCase(element
                                .getName());
                        String element_CamelCaseType = null;
                        boolean elementIsEndToken;
                        if (element instanceof TokenElement) {
                            TokenElement tokenElement = (TokenElement) element;
                            if (tokenElement.getToken().getName()
                                    .equals("$end")) {
                                elementIsEndToken = true;
                            }
                            else {
                                MatchedToken matchedToken = context
                                        .getMatchedToken(tokenElement
                                                .getToken().getName());
                                if (matchedToken instanceof NameToken) {
                                    NameToken nameToken = (NameToken) matchedToken;
                                    element_CamelCaseType = nameToken
                                            .get_CamelCaseName();
                                }
                                else {
                                    AnonymousToken anonymousToken = (AnonymousToken) matchedToken;

                                    element_CamelCaseType = ""
                                            + anonymousToken
                                                    .get_CamelCaseName();
                                }

                                elementIsEndToken = false;
                            }
                        }
                        else {
                            ProductionElement productionElement = (ProductionElement) element;
                            element_CamelCaseType = to_CamelCase(productionElement
                                    .getProduction().getName());

                            elementIsEndToken = false;
                        }

                        if (elementIsEndToken) {
                            mReduce.newReduceEndPop();
                        }
                        else {
                            mReduce.newReduceNormalPop(element_CamelCaseType,
                                    element_CamelCaseName);
                        }
                    }

                    if (alt_CamelCaseFullName.equals("$Start")) {
                        mReduce.newAcceptDecision(to_CamelCase(elements.get(0)
                                .getName()));
                    }
                    else {
                        MReduceDecision mReduceDecision = mReduce
                                .newReduceDecision();

                        for (Element element : elements) {
                            String element_CamelCaseName = to_CamelCase(element
                                    .getName());
                            boolean elementIsEndToken;
                            if (element instanceof TokenElement) {
                                TokenElement tokenElement = (TokenElement) element;
                                if (tokenElement.getToken().getName().equals(
                                        "$end")) {
                                    elementIsEndToken = true;
                                }
                                else {
                                    elementIsEndToken = false;
                                }
                            }
                            else {
                                elementIsEndToken = false;
                            }
                            if (elementIsEndToken) {
                                mReduceDecision.newEndParameter();
                            }
                            else {
                                mReduceDecision
                                        .newNormalParameter(element_CamelCaseName);
                            }
                        }
                    }
                }
            }

            if (isLr1OrMore) {
                mLrStateSingleton.newLr1OrMore();
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Node.java")));

            bw.write(mNode.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Node.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Token.java")));

            bw.write(mToken.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Token.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "State.java")));

            bw.write(mState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("State.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "TransitionState.java")));

            bw.write(mTransitionState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("TransitionState.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "FinalState.java")));

            bw.write(mFinalState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("FinalState.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Symbol.java")));

            bw.write(mSymbol.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Symbol.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Lexer.java")));

            bw.write(mLexer.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Lexer.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "LexerException.java")));

            bw.write(mLexerException.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("LexerException.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "ParserException.java")));

            bw.write(mParserException.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("ParserException.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Test.java")));

            bw.write(mTest.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Test.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "End.java")));

            bw.write(mEnd.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("End.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Walker.java")));

            bw.write(mWalker.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Walker.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Parser.java")));

            bw.write(mParser.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Parser.java", e);
        }
    }
}
