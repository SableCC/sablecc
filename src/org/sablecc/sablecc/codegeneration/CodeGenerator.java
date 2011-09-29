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

package org.sablecc.sablecc.codegeneration;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.sablecc.exception.*;
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.codegeneration.java.macro.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.launcher.*;

public class CodeGenerator {

    final private Grammar grammar;

    final private File destinationDirectory;

    final private String destinationPackage;

    final private Trace trace;

    private boolean hasRun;

    public CodeGenerator(
            Grammar grammar,
            File destinationDirectory,
            String destinationPackage,
            Trace trace) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (destinationDirectory == null) {
            throw new InternalException("destinationDirectory may not be null");
        }

        if (destinationPackage == null) {
            throw new InternalException("destinationPackage may not be null");
        }

        if (trace == null) {
            throw new InternalException("trace may not be null");
        }

        this.grammar = grammar;
        this.destinationDirectory = destinationDirectory;
        this.destinationPackage = destinationPackage;
        this.trace = trace;
    }

    public void run() {

        if (this.hasRun) {
            throw new InternalException("compilation task may only run once");
        }
        else {
            this.hasRun = true;
        }

        String languagePackageName = this.grammar.getName_camelCase();
        File packageDirectory;
        MNode mNode = new MNode();
        MToken mToken = new MToken();
        MState mState = new MState();
        MTransitionState mTransitionState = new MTransitionState();
        MFinalState mFinalState = new MFinalState();
        MSymbol mSymbol = new MSymbol();
        MLexer mLexer = new MLexer();
        MLexerException mLexerException = new MLexerException();
        MEnd mEnd = new MEnd();
        MTester mTester = new MTester();

        if (this.destinationPackage.equals("")) {
            packageDirectory = new File(this.destinationDirectory,
                    languagePackageName);
            mNode.newDefaultPackage(this.grammar.getName_camelCase());
            mToken.newDefaultPackage(this.grammar.getName_camelCase());
            mState.newDefaultPackage(this.grammar.getName_camelCase());
            mTransitionState
                    .newDefaultPackage(this.grammar.getName_camelCase());
            mFinalState.newDefaultPackage(this.grammar.getName_camelCase());
            mSymbol.newDefaultPackage(this.grammar.getName_camelCase());
            mLexer.newDefaultPackage(this.grammar.getName_camelCase());
            mLexerException.newDefaultPackage(this.grammar.getName_camelCase());
            mEnd.newDefaultPackage(this.grammar.getName_camelCase());
            mTester.newDefaultPackage(this.grammar.getName_camelCase());
        }
        else {
            packageDirectory = new File(this.destinationDirectory,
                    this.destinationPackage.replace('.', '/') + "/"
                            + languagePackageName);
            mNode.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mToken.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mState.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mTransitionState.newSpecifiedPackage(
                    this.grammar.getName_camelCase(), this.destinationPackage);
            mFinalState.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mSymbol.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mLexer.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mLexerException.newSpecifiedPackage(
                    this.grammar.getName_camelCase(), this.destinationPackage);
            mEnd.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mTester.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
        }

        packageDirectory.mkdirs();

        Context context = this.grammar.getGlobalAnonymousContext();

        for (LexerExpression token : context.getLexerExpressionTokens()) {
            if (token instanceof LexerExpression.NamedExpression) {
                LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) token;

                mNode.newNodeTypeEnumEntry(namedToken.getName_CamelCase());
                mNode.newNodeInternalTypeEnumEntry(namedToken
                        .getName_CamelCase());

                MCustomToken mCustomToken = new MCustomToken(
                        namedToken.getName_CamelCase());

                if (this.destinationPackage.equals("")) {
                    mCustomToken.newDefaultPackage(this.grammar
                            .getName_camelCase());
                }
                else {
                    mCustomToken.newSpecifiedPackage(
                            this.grammar.getName_camelCase(),
                            this.destinationPackage);
                }

                try {
                    BufferedWriter bw = new BufferedWriter(
                            new FileWriter(new File(packageDirectory, "N"
                                    + namedToken.getName_CamelCase() + ".java")));

                    bw.write(mCustomToken.toString());
                    bw.close();
                }
                catch (IOException e) {
                    // TODO : return proper exception
                    throw new InternalException("TODO: raise error " + "N"
                            + namedToken.getName_CamelCase() + ".java", e);
                }
            }
            else {
                LexerExpression.InlineExpression inlineToken = (LexerExpression.InlineExpression) token;

                mNode.newNodeInternalTypeEnumEntry(""
                        + inlineToken.getInternalName_CamelCase());

                MAnonymousToken mAnonymousToken = new MAnonymousToken(""
                        + inlineToken.getInternalName_CamelCase());

                if (this.destinationPackage.equals("")) {
                    mAnonymousToken.newDefaultPackage(this.grammar
                            .getName_camelCase());
                }
                else {
                    mAnonymousToken.newSpecifiedPackage(
                            this.grammar.getName_camelCase(),
                            this.destinationPackage);
                }

                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            new File(packageDirectory, "N"
                                    + inlineToken.getInternalName_CamelCase()
                                    + ".java")));

                    bw.write(mAnonymousToken.toString());
                    bw.close();
                }
                catch (IOException e) {
                    // TODO : return proper exception
                    throw new InternalException(
                            "TODO: raise error " + "N"
                                    + inlineToken.getInternalName_CamelCase()
                                    + ".java", e);
                }
            }
        }

        Automaton lexer = this.grammar.getLexer().getAutomaton();

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

                if (this.destinationPackage.equals("")) {
                    mFinalStateSingleton.newDefaultPackage(this.grammar
                            .getName_camelCase());
                }
                else {
                    mFinalStateSingleton.newSpecifiedPackage(
                            this.grammar.getName_camelCase(),
                            this.destinationPackage);
                }

                Marker marker = acceptation.getMarker();

                if (marker == null) {
                    mFinalStateSingleton.newAcceptTokenNoMarker();
                }
                else {
                    mFinalStateSingleton.newAcceptTokenWithMarker(marker
                            .getName());
                }

                LexerExpression token = this.grammar
                        .getLexerExpression(acceptation.getName());

                if (context.isIgnored(token)) {
                    mFinalStateSingleton.newAcceptIgnoredToken();
                }
                else {
                    if (token instanceof LexerExpression.NamedExpression) {
                        LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) token;

                        mFinalStateSingleton.newAcceptNormalToken(namedToken
                                .getName_CamelCase());
                    }
                    else {
                        LexerExpression.InlineExpression anonymousToken = (LexerExpression.InlineExpression) token;

                        mFinalStateSingleton.newAcceptNormalToken(""
                                + anonymousToken.getInternalName_CamelCase());
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
                    throw new InternalException("TODO: raise error " + "S_"
                            + state.getId() + ".java", e);
                }
            }
            else {
                MTransitionStateSingleton mTransitionStateSingleton = new MTransitionStateSingleton(
                        "" + state.getId());

                if (this.destinationPackage.equals("")) {
                    mTransitionStateSingleton.newDefaultPackage(this.grammar
                            .getName_camelCase());
                }
                else {
                    mTransitionStateSingleton.newSpecifiedPackage(
                            this.grammar.getName_camelCase(),
                            this.destinationPackage);
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
                    new InternalException("TODO: raise error " + "S_"
                            + state.getId() + ".java", e);
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
            new InternalException("TODO: raise error " + "Node.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Token.java")));

            bw.write(mToken.toString());
            bw.close();
        }
        catch (IOException e) {
            throw new InternalException("TODO: raise error " + "Token.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "State.java")));

            bw.write(mState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw new InternalException("TODO: raise error " + "State.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "TransitionState.java")));

            bw.write(mTransitionState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw new InternalException("TODO: raise error "
                    + "TransitionState.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "FinalState.java")));

            bw.write(mFinalState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw new InternalException("TODO: raise error "
                    + "FinalState.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Symbol.java")));

            bw.write(mSymbol.toString());
            bw.close();
        }
        catch (IOException e) {
            throw new InternalException("TODO: raise error " + "Symbol.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Lexer.java")));

            bw.write(mLexer.toString());
            bw.close();
        }
        catch (IOException e) {
            throw new InternalException("TODO: raise error " + "Lexer.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "LexerException.java")));

            bw.write(mLexerException.toString());
            bw.close();
        }
        catch (IOException e) {
            throw new InternalException("TODO: raise error "
                    + "LexerException.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "End.java")));

            bw.write(mEnd.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "End.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Tester.java")));

            bw.write(mTester.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "Tester.java", e);
        }
    }

}
