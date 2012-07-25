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

import static org.sablecc.util.CamelCase.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.sablecc.exception.*;
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.alphabet.Bound;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.codegeneration.java.macro.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.Parser.ParserElement.ElementType;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.grammar.*;
import org.sablecc.sablecc.grammar.transformation.*;
import org.sablecc.sablecc.launcher.*;
import org.sablecc.sablecc.oldlrautomaton.*;
import org.sablecc.util.*;

public class CodeGenerator {

    final private Grammar grammar;

    final private File destinationDirectory;

    final private String destinationPackage;

    final private Trace trace;

    private boolean hasRun;

    public CodeGenerator(
            Grammar grammar,
            String targetLanguage,
            File destinationDirectory,
            String destinationPackage,
            Trace trace) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (targetLanguage == null) {
            throw new InternalException("targetLanguage may not be null");
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
        MParserException mParserException = new MParserException();
        MWalker mWalker = new MWalker();
        MParser mParser = new MParser();
        MParseStack mParseStack = new MParseStack();
        MLrState mLrState = new MLrState();
        MCstProductionType mCstName = new MCstProductionType();
        MAbstractForest mAbstractForest = new MAbstractForest();
        MEntry mEntry = new MEntry();
        MNodeList mNodeList = new MNodeList();
        MPairNodeList mPairNodeList = new MPairNodeList();
        MSeparatedNodeList mSeparatedNodeList = new MSeparatedNodeList();

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
            mParserException
                    .newDefaultPackage(this.grammar.getName_camelCase());
            mWalker.newDefaultPackage(this.grammar.getName_camelCase());
            mParser.newDefaultPackage(this.grammar.getName_camelCase());
            mParseStack.newDefaultPackage(this.grammar.getName_camelCase());
            mLrState.newDefaultPackage(this.grammar.getName_camelCase());
            mCstName.newDefaultPackage(this.grammar.getName_camelCase());
            mAbstractForest.newDefaultPackage(this.grammar.getName_camelCase());
            mNodeList.newDefaultPackage(this.grammar.getName_camelCase());
            mPairNodeList.newDefaultPackage(this.grammar.getName_camelCase());
            mSeparatedNodeList.newDefaultPackage(this.grammar
                    .getName_camelCase());
            mEntry.newDefaultPackage(this.grammar.getName_camelCase());
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
            mParserException.newSpecifiedPackage(
                    this.grammar.getName_camelCase(), this.destinationPackage);
            mWalker.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mParser.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mParseStack.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mLrState.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mCstName.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mAbstractForest.newSpecifiedPackage(
                    this.grammar.getName_camelCase(), this.destinationPackage);
            mNodeList.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mPairNodeList.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
            mSeparatedNodeList.newSpecifiedPackage(
                    this.grammar.getName_camelCase(), this.destinationPackage);
            mEntry.newSpecifiedPackage(this.grammar.getName_camelCase(),
                    this.destinationPackage);
        }

        packageDirectory.mkdirs();

        Context context = this.grammar.getGlobalAnonymousContext();
        Automaton lexer = this.grammar.getLexer().getAutomaton();

        /*
         * Generate token
         */

        for (LexerExpression token : context.getLexerExpressionTokens()) {
            if (token instanceof LexerExpression.NamedExpression) {
                LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) token;

                mNode.newNodeTypeEnumEntry(namedToken.getName_CamelCase());
                mToken.newNodeInternalTypeEnumEntry(namedToken
                        .getName_CamelCase());

                mWalker.newWalkerIn(namedToken.getName_CamelCase());
                mWalker.newWalkerCase(namedToken.getName_CamelCase());
                mWalker.newWalkerOut(namedToken.getName_CamelCase());

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

                mToken.newNodeInternalTypeEnumEntry(""
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

        /*
         * Generate Symbol
         */

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

        /*
         * Generate lexer states
         */

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

        /*
         * Generate AST Class
         * */

        Map<IReferencable, String> alternativeToCamelFullName = new HashMap<IReferencable, String>();

        if (this.grammar.hasATree()) {
            for (Tree.TreeProduction production : this.grammar.getTree()
                    .getProductions()) {
                String production_CamelCaseName = production
                        .getName_CamelCase();

                mNode.newNodeProductionTypeEnumEntry(production_CamelCaseName);

                // if production is not a single anonymous alternative
                if (production.getAlternatives().size() > 1
                        || !production.getAlternatives().iterator().next()
                                .getName().equals("")) {

                    MProduction mProduction = new MProduction(
                            production_CamelCaseName);

                    if (this.destinationPackage.equals("")) {
                        mProduction.newDefaultPackage(this.grammar
                                .getName_camelCase());
                    }
                    else {
                        mProduction.newSpecifiedPackage(
                                this.grammar.getName_camelCase(),
                                this.destinationPackage);
                    }

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                new File(packageDirectory, "N"
                                        + production_CamelCaseName + ".java")));

                        bw.write(mProduction.toString());
                        bw.close();
                    }
                    catch (IOException e) {
                        throw new InternalException("TODO: raise error " + "N"
                                + production_CamelCaseName + ".java", e);
                    }
                }

                for (Tree.TreeAlternative alternative : production
                        .getAlternatives()) {
                    String alt_CamelCaseName = alternative.getName_CamelCase();
                    boolean altIsPublic = alt_CamelCaseName != null;
                    boolean altIsProd = altIsPublic
                            && alt_CamelCaseName.equals("");
                    String alt_CamelCaseFullName;

                    if (altIsProd) {
                        alt_CamelCaseFullName = production_CamelCaseName;
                    }
                    else if (altIsPublic) {
                        alt_CamelCaseFullName = production_CamelCaseName + "_"
                                + alt_CamelCaseName;
                    }
                    else {
                        alt_CamelCaseFullName = production_CamelCaseName + "_$"
                                + alternative.getIndex();

                    }

                    MAlternative mAlternative = new MAlternative(
                            alt_CamelCaseFullName);

                    alternativeToCamelFullName.put(alternative,
                            alt_CamelCaseFullName);

                    if (altIsPublic) {
                        mWalker.newWalkerIn(alt_CamelCaseFullName);
                        mWalker.newWalkerCase(alt_CamelCaseFullName);
                        mWalker.newWalkerOut(alt_CamelCaseFullName);
                        mAlternative.newAltNormalApply();
                    }
                    else {
                        mAlternative.newAltAnonymousApply();
                    }

                    if (this.destinationPackage.equals("")) {
                        mAlternative.newDefaultPackage(this.grammar
                                .getName_camelCase());
                    }
                    else {
                        mAlternative.newSpecifiedPackage(
                                this.grammar.getName_camelCase(),
                                this.destinationPackage);
                    }

                    if (altIsPublic) {
                        mNode.newNodeTypeEnumEntry(alt_CamelCaseFullName);
                        mAlternative.newPublic();
                        mAlternative.newNamedAltType();
                    }
                    else {
                        mAlternative.newAnonymousAltType();
                    }

                    if (altIsProd) {
                        mAlternative.newAlternativeNodeParent();
                    }
                    else {
                        mAlternative
                                .newAlternativeNamedParent(production_CamelCaseName);
                    }

                    for (Tree.TreeElement treeElement : alternative
                            .getElements()) {

                        if (treeElement instanceof Tree.TreeElement.SingleElement) {

                            Tree.TreeElement.SingleElement normalElement = (Tree.TreeElement.SingleElement) treeElement;
                            String element_CamelCaseName = normalElement
                                    .getName_CamelCase();
                            boolean elementIsPublicReadable;
                            if (element_CamelCaseName == null) {
                                element_CamelCaseName = "$"
                                        + normalElement.getIndex();
                                elementIsPublicReadable = false;
                            }
                            else {
                                elementIsPublicReadable = true;
                            }
                            String element_CamelCaseType;
                            String element_CamelCaseInternalType;

                            IReferencable reference = normalElement
                                    .getReference();
                            if (reference instanceof LexerExpression.NamedExpression) {
                                LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) reference;
                                element_CamelCaseType = namedToken
                                        .getName_CamelCase();
                                element_CamelCaseInternalType = element_CamelCaseType;
                            }
                            else if (reference instanceof LexerExpression.InlineExpression) {
                                LexerExpression.InlineExpression inlineToken = (LexerExpression.InlineExpression) reference;
                                element_CamelCaseType = null;
                                element_CamelCaseInternalType = inlineToken
                                        .getInternalName_CamelCase();
                            }
                            else {
                                Tree.TreeProduction referencedProduction = (Tree.TreeProduction) reference;
                                element_CamelCaseType = referencedProduction
                                        .getName_CamelCase();
                                element_CamelCaseInternalType = element_CamelCaseType;
                            }

                            if (treeElement.getType().getCardinality()
                                    .equals(CardinalityInterval.ONE_ONE)
                                    || treeElement
                                            .getType()
                                            .getCardinality()
                                            .equals(CardinalityInterval.ZERO_ONE)) {
                                mAlternative.newNormalConstructorParameter(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);
                                mAlternative
                                        .newNormalContructorInitialization(element_CamelCaseName);

                                mAlternative.newNormalElementDeclaration(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);
                                mAlternative.newNormalElementAccessor(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);

                                if (elementIsPublicReadable) {
                                    MPublicElementAccessor publicElementAccessor = mAlternative
                                            .newPublicElementAccessor(element_CamelCaseName);
                                    if (element_CamelCaseType != null) {
                                        publicElementAccessor
                                                .newPublicElementType(element_CamelCaseType);
                                    }
                                    else {
                                        publicElementAccessor
                                                .newTokenElementType();
                                    }
                                }
                            }
                            else {
                                mAlternative.newListConstructorParameter(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);
                                mAlternative
                                        .newNormalContructorInitialization(element_CamelCaseName);

                                mAlternative.newListElementDeclaration(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);
                                mAlternative.newListElementAccessor(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);

                                if (elementIsPublicReadable) {
                                    MPublicElementAccessor publicElementAccessor = mAlternative
                                            .newPublicElementAccessor(element_CamelCaseName);
                                    publicElementAccessor
                                            .newPublicListElementType(element_CamelCaseInternalType);

                                }

                            }
                            mAlternative
                                    .newNormalChildApply(element_CamelCaseName);

                        }
                        else {
                            Tree.TreeElement.DoubleElement doubleElement = (Tree.TreeElement.DoubleElement) treeElement;
                            String element_CamelCaseName = doubleElement
                                    .getName_CamelCase();

                            boolean elementIsPublicReadable;
                            if (element_CamelCaseName == null) {
                                element_CamelCaseName = "$"
                                        + doubleElement.getIndex();
                                elementIsPublicReadable = false;
                            }
                            else {
                                elementIsPublicReadable = true;
                            }
                            String leftElement_CamelCaseType;
                            String leftElement_CamelCaseInternalType;

                            IReferencable leftReference = doubleElement
                                    .getLeftReference();
                            if (leftReference instanceof LexerExpression.NamedExpression) {
                                LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) leftReference;
                                leftElement_CamelCaseType = namedToken
                                        .getName_CamelCase();
                                leftElement_CamelCaseInternalType = leftElement_CamelCaseType;
                            }
                            else if (leftReference instanceof LexerExpression.InlineExpression) {
                                LexerExpression.InlineExpression inlineToken = (LexerExpression.InlineExpression) leftReference;
                                leftElement_CamelCaseType = null;
                                leftElement_CamelCaseInternalType = inlineToken
                                        .getInternalName_CamelCase();
                            }
                            else {
                                Parser.ParserProduction referencedProduction = (Parser.ParserProduction) leftReference;
                                leftElement_CamelCaseType = referencedProduction
                                        .getName_CamelCase();
                                leftElement_CamelCaseInternalType = leftElement_CamelCaseType;
                            }

                            String rightElement_CamelCaseType;
                            String rightElement_CamelCaseInternalType;

                            IReferencable rightReference = doubleElement
                                    .getRightReference();
                            if (rightReference instanceof LexerExpression.NamedExpression) {
                                LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) rightReference;
                                rightElement_CamelCaseType = namedToken
                                        .getName_CamelCase();
                                rightElement_CamelCaseInternalType = rightElement_CamelCaseType;
                            }
                            else if (rightReference instanceof LexerExpression.InlineExpression) {
                                LexerExpression.InlineExpression inlineToken = (LexerExpression.InlineExpression) rightReference;
                                rightElement_CamelCaseType = null;
                                rightElement_CamelCaseInternalType = inlineToken
                                        .getInternalName_CamelCase();
                            }
                            else {
                                Parser.ParserProduction referencedProduction = (Parser.ParserProduction) rightReference;
                                rightElement_CamelCaseType = referencedProduction
                                        .getName_CamelCase();
                                rightElement_CamelCaseInternalType = rightElement_CamelCaseType;
                            }

                            if (doubleElement.getElementType() == Tree.TreeElement.ElementType.SEPARATED) {

                                mAlternative
                                        .newSeparatedListConstructorParameter(
                                                leftElement_CamelCaseInternalType,
                                                rightElement_CamelCaseInternalType,
                                                element_CamelCaseName);
                                mAlternative
                                        .newNormalContructorInitialization(element_CamelCaseName);

                                mAlternative
                                        .newSeparatedListElementDeclaration(
                                                leftElement_CamelCaseInternalType,
                                                rightElement_CamelCaseInternalType,
                                                element_CamelCaseName);
                                mAlternative.newSeparatedListElementAccessor(
                                        leftElement_CamelCaseInternalType,
                                        rightElement_CamelCaseInternalType,
                                        element_CamelCaseName);

                                if (elementIsPublicReadable) {
                                    MPublicElementAccessor publicElementAccessor = mAlternative
                                            .newPublicElementAccessor(element_CamelCaseName);
                                    publicElementAccessor
                                            .newPublicSeparatedListElementType(
                                                    leftElement_CamelCaseInternalType,
                                                    rightElement_CamelCaseInternalType);

                                }
                            }
                            else {
                                mAlternative
                                        .newAlternatedListConstructorParameter(
                                                leftElement_CamelCaseInternalType,
                                                rightElement_CamelCaseInternalType,
                                                element_CamelCaseName);
                                mAlternative
                                        .newNormalContructorInitialization(element_CamelCaseName);

                                mAlternative
                                        .newAlternatedListElementDeclaration(
                                                leftElement_CamelCaseInternalType,
                                                rightElement_CamelCaseInternalType,
                                                element_CamelCaseName);
                                mAlternative.newAlternatedListElementAccessor(
                                        leftElement_CamelCaseInternalType,
                                        rightElement_CamelCaseInternalType,
                                        element_CamelCaseName);

                                if (elementIsPublicReadable) {
                                    MPublicElementAccessor publicElementAccessor = mAlternative
                                            .newPublicElementAccessor(element_CamelCaseName);
                                    publicElementAccessor
                                            .newPublicAlternatedListElementType(
                                                    leftElement_CamelCaseInternalType,
                                                    rightElement_CamelCaseInternalType);

                                }
                            }

                            mAlternative
                                    .newNormalChildApply(element_CamelCaseName);
                        }
                    }

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                new File(packageDirectory, "N"
                                        + alt_CamelCaseFullName + ".java")));

                        bw.write(mAlternative.toString());
                        bw.close();
                    }
                    catch (IOException e) {
                        throw new InternalException("TODO: raise error " + "N"
                                + alt_CamelCaseFullName + ".java", e);
                    }

                }

            }
        }
        else { // Grammar hasn't tree
            for (Parser.ParserProduction production : this.grammar.getParser()
                    .getProductions()) {

                String production_CamelCaseName = production
                        .getName_CamelCase();

                mNode.newNodeProductionTypeEnumEntry(production_CamelCaseName);

                // if production is not a single anonymous alternative
                if (production.getAlternatives().size() > 1
                        || !production.getAlternatives().iterator().next()
                                .getName().equals("")) {

                    MProduction mProduction = new MProduction(
                            production_CamelCaseName);

                    if (this.destinationPackage.equals("")) {
                        mProduction.newDefaultPackage(this.grammar
                                .getName_camelCase());
                    }
                    else {
                        mProduction.newSpecifiedPackage(
                                this.grammar.getName_camelCase(),
                                this.destinationPackage);
                    }

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                new File(packageDirectory, "N"
                                        + production_CamelCaseName + ".java")));

                        bw.write(mProduction.toString());
                        bw.close();
                    }
                    catch (IOException e) {
                        throw new InternalException("TODO: raise error " + "N"
                                + production_CamelCaseName + ".java", e);
                    }
                }

                for (Parser.ParserAlternative alternative : production
                        .getAlternatives()) {
                    String alt_CamelCaseName = alternative.getName_CamelCase();
                    boolean altIsPublic = alt_CamelCaseName != null;
                    boolean altIsProd = altIsPublic
                            && alt_CamelCaseName.equals("");
                    String alt_CamelCaseFullName;
                    if (altIsProd) {
                        alt_CamelCaseFullName = production_CamelCaseName;
                    }
                    else if (altIsPublic) {
                        alt_CamelCaseFullName = production_CamelCaseName + "_"
                                + alt_CamelCaseName;
                    }
                    else {
                        alt_CamelCaseFullName = production_CamelCaseName + "_$"
                                + alternative.getIndex();

                    }

                    MAlternative mAlternative = new MAlternative(
                            alt_CamelCaseFullName);

                    alternativeToCamelFullName.put(alternative,
                            alt_CamelCaseFullName);

                    if (altIsPublic) {
                        mWalker.newWalkerIn(alt_CamelCaseFullName);
                        mWalker.newWalkerCase(alt_CamelCaseFullName);
                        mWalker.newWalkerOut(alt_CamelCaseFullName);
                        mAlternative.newAltNormalApply();
                    }
                    else {
                        mAlternative.newAltAnonymousApply();
                    }

                    if (this.destinationPackage.equals("")) {
                        mAlternative.newDefaultPackage(this.grammar
                                .getName_camelCase());
                    }
                    else {
                        mAlternative.newSpecifiedPackage(
                                this.grammar.getName_camelCase(),
                                this.destinationPackage);
                    }

                    if (altIsPublic) {
                        mNode.newNodeTypeEnumEntry(alt_CamelCaseFullName);
                        mAlternative.newPublic();
                        mAlternative.newNamedAltType();
                    }
                    else {
                        mAlternative.newAnonymousAltType();
                    }

                    if (altIsProd) {
                        mAlternative.newAlternativeNodeParent();
                    }
                    else {
                        mAlternative
                                .newAlternativeNamedParent(production_CamelCaseName);
                    }

                    for (Parser.ParserElement parserElement : alternative
                            .getElements()) {
                        if (parserElement instanceof Parser.ParserElement.SingleElement) {

                            Parser.ParserElement.SingleElement normalElement = (Parser.ParserElement.SingleElement) parserElement;
                            String element_CamelCaseName = normalElement
                                    .getName_CamelCase();
                            boolean elementIsPublicReadable;
                            if (element_CamelCaseName == null) {
                                element_CamelCaseName = "$"
                                        + normalElement.getIndex();
                                elementIsPublicReadable = false;
                            }
                            else {
                                elementIsPublicReadable = true;
                            }
                            String element_CamelCaseType;
                            String element_CamelCaseInternalType;

                            IReferencable reference = normalElement
                                    .getReference();
                            if (reference instanceof LexerExpression.NamedExpression) {
                                LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) reference;
                                element_CamelCaseType = namedToken
                                        .getName_CamelCase();
                                element_CamelCaseInternalType = element_CamelCaseType;
                            }
                            else if (reference instanceof LexerExpression.InlineExpression) {
                                LexerExpression.InlineExpression inlineToken = (LexerExpression.InlineExpression) reference;
                                element_CamelCaseType = null;
                                element_CamelCaseInternalType = inlineToken
                                        .getInternalName_CamelCase();
                            }
                            else {
                                Parser.ParserProduction referencedProduction = (Parser.ParserProduction) reference;
                                element_CamelCaseType = referencedProduction
                                        .getName_CamelCase();
                                element_CamelCaseInternalType = element_CamelCaseType;
                            }

                            if (parserElement.getType().getCardinality()
                                    .equals(CardinalityInterval.ONE_ONE)
                                    || parserElement
                                            .getType()
                                            .getCardinality()
                                            .equals(CardinalityInterval.ZERO_ONE)) {
                                mAlternative.newNormalConstructorParameter(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);
                                mAlternative
                                        .newNormalContructorInitialization(element_CamelCaseName);

                                mAlternative.newNormalElementDeclaration(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);
                                mAlternative.newNormalElementAccessor(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);

                                if (elementIsPublicReadable) {
                                    MPublicElementAccessor publicElementAccessor = mAlternative
                                            .newPublicElementAccessor(element_CamelCaseName);
                                    if (element_CamelCaseType != null) {
                                        publicElementAccessor
                                                .newPublicElementType(element_CamelCaseType);
                                    }
                                    else {
                                        publicElementAccessor
                                                .newTokenElementType();
                                    }
                                }
                            }
                            else {
                                mAlternative.newListConstructorParameter(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);
                                mAlternative
                                        .newNormalContructorInitialization(element_CamelCaseName);

                                mAlternative.newListElementDeclaration(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);
                                mAlternative.newListElementAccessor(
                                        element_CamelCaseInternalType,
                                        element_CamelCaseName);

                                if (elementIsPublicReadable) {
                                    MPublicElementAccessor publicElementAccessor = mAlternative
                                            .newPublicElementAccessor(element_CamelCaseName);
                                    publicElementAccessor
                                            .newPublicListElementType(element_CamelCaseInternalType);

                                }

                            }

                            mAlternative
                                    .newNormalChildApply(element_CamelCaseName);

                        }
                        else {
                            Parser.ParserElement.DoubleElement doubleElement = (Parser.ParserElement.DoubleElement) parserElement;
                            String element_CamelCaseName = doubleElement
                                    .getName_CamelCase();

                            boolean elementIsPublicReadable;
                            if (element_CamelCaseName == null) {
                                element_CamelCaseName = "$"
                                        + doubleElement.getIndex();
                                elementIsPublicReadable = false;
                            }
                            else {
                                elementIsPublicReadable = true;
                            }
                            String leftElement_CamelCaseType;
                            String leftElement_CamelCaseInternalType;

                            IReferencable leftReference = doubleElement
                                    .getLeftReference();
                            if (leftReference instanceof LexerExpression.NamedExpression) {
                                LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) leftReference;
                                leftElement_CamelCaseType = namedToken
                                        .getName_CamelCase();
                                leftElement_CamelCaseInternalType = leftElement_CamelCaseType;
                            }
                            else if (leftReference instanceof LexerExpression.InlineExpression) {
                                LexerExpression.InlineExpression inlineToken = (LexerExpression.InlineExpression) leftReference;
                                leftElement_CamelCaseType = null;
                                leftElement_CamelCaseInternalType = inlineToken
                                        .getInternalName_CamelCase();
                            }
                            else {
                                Parser.ParserProduction referencedProduction = (Parser.ParserProduction) leftReference;
                                leftElement_CamelCaseType = referencedProduction
                                        .getName_CamelCase();
                                leftElement_CamelCaseInternalType = leftElement_CamelCaseType;
                            }

                            String rightElement_CamelCaseType;
                            String rightElement_CamelCaseInternalType;

                            IReferencable rightReference = doubleElement
                                    .getRightReference();
                            if (rightReference instanceof LexerExpression.NamedExpression) {
                                LexerExpression.NamedExpression namedToken = (LexerExpression.NamedExpression) rightReference;
                                rightElement_CamelCaseType = namedToken
                                        .getName_CamelCase();
                                rightElement_CamelCaseInternalType = rightElement_CamelCaseType;
                            }
                            else if (rightReference instanceof LexerExpression.InlineExpression) {
                                LexerExpression.InlineExpression inlineToken = (LexerExpression.InlineExpression) rightReference;
                                rightElement_CamelCaseType = null;
                                rightElement_CamelCaseInternalType = inlineToken
                                        .getInternalName_CamelCase();
                            }
                            else {
                                Parser.ParserProduction referencedProduction = (Parser.ParserProduction) rightReference;
                                rightElement_CamelCaseType = referencedProduction
                                        .getName_CamelCase();
                                rightElement_CamelCaseInternalType = rightElement_CamelCaseType;
                            }

                            if (doubleElement.getElementType() == ElementType.SEPARATED) {

                                mAlternative
                                        .newSeparatedListConstructorParameter(
                                                leftElement_CamelCaseInternalType,
                                                rightElement_CamelCaseInternalType,
                                                element_CamelCaseName);
                                mAlternative
                                        .newNormalContructorInitialization(element_CamelCaseName);

                                mAlternative
                                        .newSeparatedListElementDeclaration(
                                                leftElement_CamelCaseInternalType,
                                                rightElement_CamelCaseInternalType,
                                                element_CamelCaseName);
                                mAlternative.newSeparatedListElementAccessor(
                                        leftElement_CamelCaseInternalType,
                                        rightElement_CamelCaseInternalType,
                                        element_CamelCaseName);

                                if (elementIsPublicReadable) {
                                    MPublicElementAccessor publicElementAccessor = mAlternative
                                            .newPublicElementAccessor(element_CamelCaseName);
                                    publicElementAccessor
                                            .newPublicSeparatedListElementType(
                                                    leftElement_CamelCaseInternalType,
                                                    rightElement_CamelCaseInternalType);

                                }
                            }
                            else {
                                mAlternative
                                        .newAlternatedListConstructorParameter(
                                                leftElement_CamelCaseInternalType,
                                                rightElement_CamelCaseInternalType,
                                                element_CamelCaseName);
                                mAlternative
                                        .newNormalContructorInitialization(element_CamelCaseName);

                                mAlternative
                                        .newAlternatedListElementDeclaration(
                                                leftElement_CamelCaseInternalType,
                                                rightElement_CamelCaseInternalType,
                                                element_CamelCaseName);
                                mAlternative.newAlternatedListElementAccessor(
                                        leftElement_CamelCaseInternalType,
                                        rightElement_CamelCaseInternalType,
                                        element_CamelCaseName);

                                if (elementIsPublicReadable) {
                                    MPublicElementAccessor publicElementAccessor = mAlternative
                                            .newPublicElementAccessor(element_CamelCaseName);
                                    publicElementAccessor
                                            .newPublicAlternatedListElementType(
                                                    leftElement_CamelCaseInternalType,
                                                    rightElement_CamelCaseInternalType);

                                }
                            }

                            mAlternative
                                    .newNormalChildApply(element_CamelCaseName);

                        }

                    }

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                new File(packageDirectory, "N"
                                        + alt_CamelCaseFullName + ".java")));

                        bw.write(mAlternative.toString());
                        bw.close();
                    }
                    catch (IOException e) {
                        throw new InternalException("TODO: raise error " + "N"
                                + alt_CamelCaseFullName + ".java", e);
                    }
                }
            }
        }

        /*
         * Generate CST production name
         *
         **/

        for (Production production : this.grammar.getSimplifiedGrammar()
                .getProductions()) {
            mCstName.newCstProductionTypeDeclaration(production.getName());

        }

        mCstName.newCstProductionTypeDeclaration("NOT_A_PRODUCTION");

        /*
         * Generate LRState
         */

        for (LRState state : this.grammar.getSimplifiedGrammar()
                .getLrAutomaton().getStates()) {
            MLrStateSingleton mLrStateSingleton = new MLrStateSingleton(
                    state.getName());

            for (Entry<OldToken, LRState> entry : state.getTokenTransitions()
                    .entrySet()) {
                OldToken oldToken = entry.getKey();
                LRState target = entry.getValue();

                if (oldToken.getName().equals("$end")) {
                    mLrStateSingleton.newEndTokenLrTransitionTarget(target
                            .getName());
                }
                else {
                    LexerExpression lexerExpression = this.grammar
                            .getLexerExpression(oldToken.getName());
                    String element_CamelCaseType;
                    if (lexerExpression instanceof LexerExpression.NamedExpression) {
                        LexerExpression.NamedExpression namedExpression = (LexerExpression.NamedExpression) lexerExpression;
                        element_CamelCaseType = namedExpression
                                .getName_CamelCase();
                    }
                    else {
                        LexerExpression.InlineExpression inlineExpression = (LexerExpression.InlineExpression) lexerExpression;

                        element_CamelCaseType = ""
                                + inlineExpression.getInternalName_CamelCase();
                    }

                    mLrStateSingleton.newNormalTokenLrTransitionTarget(
                            element_CamelCaseType, target.getName());
                }
            }

            for (Entry<OldProduction, LRState> entry : state
                    .getProductionTransitions().entrySet()) {
                OldProduction oldProduction = entry.getKey();
                LRState target = entry.getValue();

                String production_camelCaseName = oldProduction.getName();
                mLrStateSingleton.newProductionLrTransitionTarget(
                        production_camelCaseName, target.getName());
            }

            Map<Integer, MDistance> distanceMap = new LinkedHashMap<Integer, MDistance>();
            boolean isLr1OrMore = false;
            for (Action action : state.getActions()) {
                int maxLookahead = action.getMaxLookahead();
                while (maxLookahead > distanceMap.size() - 1) {
                    int distance = distanceMap.size();
                    distanceMap.put(distance,
                            mLrStateSingleton.newDistance("" + distance));
                }

                MDistance mDistance = distanceMap.get(maxLookahead);
                MAction mAction = mDistance.newAction();
                if (maxLookahead > 0) {
                    isLr1OrMore = true;
                    for (Entry<Integer, Set<Item>> entry : action
                            .getDistanceToItemSetMap().entrySet()) {
                        String ahead = "" + entry.getKey();
                        Set<Item> items = entry.getValue();
                        Set<OldToken> tokens = new LinkedHashSet<OldToken>();
                        for (Item item : items) {
                            tokens.add(item.getTokenElement().getToken());
                        }

                        if (tokens.size() == 0) {
                            mAction.newFalseGroup();
                        }
                        else {
                            MNormalGroup mNormalGroup = mAction
                                    .newNormalGroup();

                            for (OldToken token : tokens) {
                                if (token.getName().equals("$end")) {
                                    mNormalGroup.newEndCondition(ahead);
                                }
                                else {
                                    LexerExpression lexerExpression = this.grammar
                                            .getLexerExpression(token.getName());
                                    String element_CamelCaseType;
                                    if (lexerExpression instanceof LexerExpression.NamedExpression) {
                                        LexerExpression.NamedExpression namedExpression = (LexerExpression.NamedExpression) lexerExpression;
                                        element_CamelCaseType = namedExpression
                                                .getName_CamelCase();
                                    }
                                    else {
                                        LexerExpression.InlineExpression inlineExpression = (LexerExpression.InlineExpression) lexerExpression;

                                        element_CamelCaseType = ""
                                                + inlineExpression
                                                        .getInternalName_CamelCase();
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
                    OldAlternative alternative = reduceAction.getAlternative();
                    OldProduction production = alternative.getProduction();

                    String production_CamelCaseName = to_CamelCase(production
                            .getName());
                    String alt_CamelCaseName = to_CamelCase(alternative
                            .getName());
                    String alt_CamelCaseFullName = production_CamelCaseName
                            + (alt_CamelCaseName.equals("") ? "" : "_"
                                    + alt_CamelCaseName);

                    MReduce mReduce = mAction.newReduce(production.getName());

                    ArrayList<OldElement> elements = alternative.getElements();

                    int elementCount = elements.size();

                    for (int i = elementCount - 1; i >= 0; i--) {

                        OldElement element = elements.get(i);
                        String element_CamelCaseName = to_camelCase(element
                                .getName());

                        boolean elementIsEndToken;

                        if (element instanceof OldTokenElement) {

                            elementIsEndToken = ((OldTokenElement) element)
                                    .getToken().getName().equals("$end");
                        }
                        else {
                            elementIsEndToken = false;
                        }

                        if (elementIsEndToken) {
                            mReduce.newReduceEndPop();
                        }
                        else {

                            mReduce.newReduceNormalPop(element_CamelCaseName);
                        }
                    }

                    if (alt_CamelCaseFullName.equals("$Start")) {
                        mReduce.newAcceptDecision(to_CamelCase(elements.get(0)
                                .getName()));
                    }
                    else {
                        MReduceDecision mReduceDecision = mReduce
                                .newReduceDecision();

                        SAlternativeTransformation transformation = alternative
                                .getOrigin().getTransformation();

                        for (SAlternativeTransformationElement transformationElement : transformation
                                .getElements()) {

                            transformationElement
                                    .apply(new TransformationGeneration(
                                            this.grammar, alternative,
                                            mReduceDecision,
                                            alternativeToCamelFullName));

                        }

                        for (OldElement element : elements) {

                            boolean elementIsEndToken;
                            if (element instanceof OldTokenElement) {
                                OldTokenElement tokenElement = (OldTokenElement) element;

                                if (tokenElement.getToken().getName()
                                        .equals("$end")) {
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
                                mReduce.newEndParameter();
                            }
                        }
                    }
                }

            }

            if (isLr1OrMore) {
                mLrStateSingleton.newLr1OrMore();
            }

            if (this.destinationPackage.equals("")) {
                mLrStateSingleton.newDefaultPackage(this.grammar
                        .getName_camelCase());
            }
            else {
                mLrStateSingleton.newSpecifiedPackage(
                        this.grammar.getName_camelCase(),
                        this.destinationPackage);
            }

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                        packageDirectory, "L" + state.getName() + ".java")));

                bw.write(mLrStateSingleton.toString());
                bw.close();
            }
            catch (IOException e) {
                throw new InternalException("TODO: raise error " + "N"
                        + state.getName() + ".java", e);
            }
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
                    packageDirectory, "CSTProductionType.java")));

            bw.write(mCstName.toString());
            bw.close();
        }
        catch (IOException e) {
            throw new InternalException("TODO: raise error "
                    + "CSTProductionTypeName.java", e);
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

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "ParserException.java")));

            bw.write(mParserException.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException(
                    "TODO: raise error " + "ParserException.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Walker.java")));

            bw.write(mWalker.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "Walker.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Parser.java")));

            bw.write(mParser.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "Parser.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "ParseStack.java")));

            bw.write(mParseStack.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "ParseStack.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "LRState.java")));

            bw.write(mLrState.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "LRState.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "AbstractForest.java")));

            bw.write(mAbstractForest.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "AbstractForest.java",
                    e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "NodeList.java")));

            bw.write(mNodeList.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "NodeList.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "Entry.java")));

            bw.write(mEntry.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "Entry.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "PairNodeList.java")));

            bw.write(mPairNodeList.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error " + "PairNodeList.java", e);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                    packageDirectory, "SeparatedNodeList.java")));

            bw.write(mSeparatedNodeList.toString());
            bw.close();
        }
        catch (IOException e) {
            new InternalException("TODO: raise error "
                    + "SeparatedNodeList.java", e);
        }
    }
}
