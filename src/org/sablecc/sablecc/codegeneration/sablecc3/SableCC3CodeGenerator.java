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

package org.sablecc.sablecc.codegeneration.sablecc3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MCustomToken;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MEof;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MFinalState;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MFinalStateSingleton;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MLexer;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MLexerException;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MState;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MSymbol;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MTransitionState;
import org.sablecc.sablecc.codegeneration.sablecc3.macro.MTransitionStateSingleton;
import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.structure.AnonymousToken;
import org.sablecc.sablecc.structure.Context;
import org.sablecc.sablecc.structure.GlobalIndex;
import org.sablecc.sablecc.structure.MatchedToken;
import org.sablecc.sablecc.structure.NameToken;

public class SableCC3CodeGenerator {

    public static void generateJavaLexer(
            File destinationDirectory,
            String destinationPackage,
            GlobalIndex globalIndex,
            Automaton lexer) {

        File nodePackageDirectory;
        File lexerPackageDirectory;
        MState mState = new MState();
        MTransitionState mTransitionState = new MTransitionState();
        MFinalState mFinalState = new MFinalState();
        MSymbol mSymbol = new MSymbol();
        MLexer mLexer = new MLexer();
        MLexerException mLexerException = new MLexerException();
        MEof mEof = new MEof();

        if (destinationPackage.equals("")) {
            nodePackageDirectory = new File(destinationDirectory, "node");
            lexerPackageDirectory = new File(destinationDirectory, "lexer");
        }
        else {
            nodePackageDirectory = new File(destinationDirectory,
                    destinationPackage.replace('.', '/') + "/node");
            lexerPackageDirectory = new File(destinationDirectory,
                    destinationPackage.replace('.', '/') + "/lexer");
            mState.newPackage(destinationPackage);
            mTransitionState.newPackage(destinationPackage);
            mFinalState.newPackage(destinationPackage);
            mSymbol.newPackage(destinationPackage);
            mLexer.newPackage(destinationPackage);
            mLexerException.newPackage(destinationPackage);
            mEof.newPackage(destinationPackage);
        }

        nodePackageDirectory.mkdirs();
        lexerPackageDirectory.mkdirs();

        Context context = globalIndex.getContexts().iterator().next();

        for (MatchedToken matchedToken : context.getMatchedTokens()) {
            if (!matchedToken.isIgnored()) {
                if (matchedToken instanceof NameToken) {
                    NameToken nameToken = (NameToken) matchedToken;

                    MCustomToken mCustomToken = new MCustomToken(nameToken
                            .get_CamelCaseName());

                    if (!destinationPackage.equals("")) {
                        mCustomToken.newPackage(destinationPackage);
                    }

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                new File(nodePackageDirectory, "T"
                                        + nameToken.get_CamelCaseName()
                                        + ".java")));

                        bw.write(mCustomToken.toString());
                        bw.close();
                    }
                    catch (IOException e) {
                        throw CompilerException.outputError("T"
                                + nameToken.get_CamelCaseName() + ".java", e);
                    }
                }
                else {
                    throw new InternalException("not supported");
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

                if (!destinationPackage.equals("")) {
                    mFinalStateSingleton.newPackage(destinationPackage);
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
                            new File(lexerPackageDirectory, "S_"
                                    + state.getId() + ".java")));

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

                if (!destinationPackage.equals("")) {
                    mTransitionStateSingleton.newPackage(destinationPackage);
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
                            new File(lexerPackageDirectory, "S_"
                                    + state.getId() + ".java")));

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
                    lexerPackageDirectory, "State.java")));

            bw.write(mState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("State.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    lexerPackageDirectory, "TransitionState.java")));

            bw.write(mTransitionState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("TransitionState.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    lexerPackageDirectory, "FinalState.java")));

            bw.write(mFinalState.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("FinalState.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    lexerPackageDirectory, "Symbol.java")));

            bw.write(mSymbol.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Symbol.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    lexerPackageDirectory, "Lexer.java")));

            bw.write(mLexer.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("Lexer.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    lexerPackageDirectory, "LexerException.java")));

            bw.write(mLexerException.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("LexerException.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    nodePackageDirectory, "EOF.java")));

            bw.write(mEof.toString());
            bw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError("EOF.java", e);
        }
    }

}
