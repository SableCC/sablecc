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

import static org.sablecc.sablecc.launcher.Version.VERSION;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.alphabet.Bound;
import org.sablecc.sablecc.alphabet.Interval;
import org.sablecc.sablecc.alphabet.RichSymbol;
import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.automaton.Acceptation;
import org.sablecc.sablecc.automaton.Automaton;
import org.sablecc.sablecc.automaton.Marker;
import org.sablecc.sablecc.automaton.State;
import org.sablecc.sablecc.codegeneration.java.macro.MAnonymousToken;
import org.sablecc.sablecc.codegeneration.java.macro.MCustomToken;
import org.sablecc.sablecc.codegeneration.java.macro.MEnd;
import org.sablecc.sablecc.codegeneration.java.macro.MFinalState;
import org.sablecc.sablecc.codegeneration.java.macro.MFinalStateSingleton;
import org.sablecc.sablecc.codegeneration.java.macro.MLexer;
import org.sablecc.sablecc.codegeneration.java.macro.MLexerException;
import org.sablecc.sablecc.codegeneration.java.macro.MNode;
import org.sablecc.sablecc.codegeneration.java.macro.MState;
import org.sablecc.sablecc.codegeneration.java.macro.MSymbol;
import org.sablecc.sablecc.codegeneration.java.macro.MTest;
import org.sablecc.sablecc.codegeneration.java.macro.MToken;
import org.sablecc.sablecc.codegeneration.java.macro.MTransitionState;
import org.sablecc.sablecc.codegeneration.java.macro.MTransitionStateSingleton;
import org.sablecc.sablecc.codegeneration.sablecc3.SableCC3CodeGenerator;
import org.sablecc.sablecc.errormessage.MInternalError;
import org.sablecc.sablecc.errormessage.MLexicalError;
import org.sablecc.sablecc.errormessage.MSyntaxError;
import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.structure.AnonymousToken;
import org.sablecc.sablecc.structure.Context;
import org.sablecc.sablecc.structure.GlobalIndex;
import org.sablecc.sablecc.structure.MatchedToken;
import org.sablecc.sablecc.structure.NameToken;
import org.sablecc.sablecc.structure.NormalExpression;
import org.sablecc.sablecc.syntax3.lexer.Lexer;
import org.sablecc.sablecc.syntax3.lexer.LexerException;
import org.sablecc.sablecc.syntax3.node.Start;
import org.sablecc.sablecc.syntax3.parser.Parser;
import org.sablecc.sablecc.syntax3.parser.ParserException;
import org.sablecc.sablecc.walker.CyclicExpressionDetector;
import org.sablecc.sablecc.walker.DeclarationCollector;
import org.sablecc.sablecc.walker.ExpressionVerifier;
import org.sablecc.sablecc.walker.LexerContextVerifier;
import org.sablecc.sablecc.walker.RegularExpressionEvaluator;
import org.sablecc.sablecc.walker.SimpleLexerRestricter;
import org.sablecc.util.Strictness;
import org.sablecc.util.Verbosity;

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
            System.err.print(new MInternalError(sw.toString(), e.getMessage()));
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
                System.out.println(" sablecc3");
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
        if (!(targetLanguage.equals("java") || targetLanguage
                .equals("sablecc3"))) {
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

        GlobalIndex globalIndex = verifySemantics(ast, strictness, verbosity);
        Automaton lexer = computeLexer(globalIndex);

        if (generateCode) {
            if (targetLanguage.equals("java")) {
                generateJavaLexer(destinationDirectory, destinationPackage,
                        globalIndex, lexer);
            }
            else if (targetLanguage.equals("sablecc3")) {
                SableCC3CodeGenerator.generateJavaLexer(destinationDirectory,
                        destinationPackage, globalIndex, lexer);
            }
            else {
                throw new InternalException("unimplemented");
            }
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

        ast.apply(new SimpleLexerRestricter());
        ast.apply(new DeclarationCollector(globalIndex));
        ast.apply(new ExpressionVerifier(globalIndex));
        ast.apply(new CyclicExpressionDetector(globalIndex));
        ast.apply(new LexerContextVerifier(globalIndex));

        return globalIndex;
    }

    private static Automaton computeLexer(
            GlobalIndex globalIndex) {

        for (NormalExpression normalExpression : globalIndex
                .getNormalNamedExpressionLinearization()) {

            Automaton automaton = RegularExpressionEvaluator
                    .evaluateExpression(globalIndex, normalExpression
                            .getExpression());

            normalExpression.setAutomaton(automaton);
        }

        Context context = globalIndex.getContexts().iterator().next();
        Automaton lexerAutomaton = Automaton.getEmptyAutomaton();

        for (MatchedToken matchedToken : context.getMatchedTokens()) {
            lexerAutomaton = lexerAutomaton.or(matchedToken.getAutomaton());
        }

        lexerAutomaton = lexerAutomaton.withPriorities(context).withMarkers()
                .minimal();

        return lexerAutomaton;
    }

    private static void generateJavaLexer(
            File destinationDirectory,
            String destinationPackage,
            GlobalIndex globalIndex,
            Automaton lexer) {

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
        MTest mTest = new MTest();
        MEnd mEnd = new MEnd();

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
            mTest.newDefaultPackage(globalIndex.getLanguage()
                    .get_camelCaseName());
            mEnd.newDefaultPackage(globalIndex.getLanguage()
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
            mTest.newSpecifiedPackage(globalIndex.getLanguage()
                    .get_camelCaseName(), destinationPackage);
            mEnd.newSpecifiedPackage(globalIndex.getLanguage()
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
                            + anonymousToken.getId());

                    MAnonymousToken mAnonymousToken = new MAnonymousToken(""
                            + anonymousToken.getId());

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
                                        + anonymousToken.getId() + ".java")));

                        bw.write(mAnonymousToken.toString());
                        bw.close();
                    }
                    catch (IOException e) {
                        throw CompilerException.outputError("N"
                                + anonymousToken.getId() + ".java", e);
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
                                + anonymousToken.getId());
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
    }

}
