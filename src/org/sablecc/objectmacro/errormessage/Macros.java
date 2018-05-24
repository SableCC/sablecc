/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;
import java.util.*;

public class Macros{
    private VERSIONS version;

    public Macros(
            VERSIONS version){

        if(version == null){
            throw ObjectMacroException.versionNull();
        }

        this.version = version;
    }

    public Macros(){
        this.version = null;
    }
    
    public MSyntaxError newSyntaxError(String pLine, String pChar, String pTokenType, String pTokenText, String pMessage){
        MSyntaxError mSyntaxError;
    
        mSyntaxError = new MSyntaxError(pLine, pChar, pTokenType, pTokenText, pMessage, this);
    
        return mSyntaxError;
    }
    
    public MLexicalError newLexicalError(String pLine, String pChar, String pMessage){
        MLexicalError mLexicalError;
    
        mLexicalError = new MLexicalError(pLine, pChar, pMessage, this);
    
        return mLexicalError;
    }
    
    public MInternalError newInternalError(String pStackTrace, String pMessage){
        MInternalError mInternalError;
    
        mInternalError = new MInternalError(pStackTrace, pMessage, this);
    
        return mInternalError;
    }
    
    public MCommandLineErrorHead newCommandLineErrorHead(){
        MCommandLineErrorHead mCommandLineErrorHead;
    
        mCommandLineErrorHead = new MCommandLineErrorHead(this);
    
        return mCommandLineErrorHead;
    }
    
    public MCommandLineErrorTail newCommandLineErrorTail(){
        MCommandLineErrorTail mCommandLineErrorTail;
    
        mCommandLineErrorTail = new MCommandLineErrorTail(this);
    
        return mCommandLineErrorTail;
    }
    
    public MInvalidArgument newInvalidArgument(String pArgumentText){
        MInvalidArgument mInvalidArgument;
    
        mInvalidArgument = new MInvalidArgument(pArgumentText, this);
    
        return mInvalidArgument;
    }
    
    public MMissingLongOptionOperand newMissingLongOptionOperand(String pOptionName, String pOperandName){
        MMissingLongOptionOperand mMissingLongOptionOperand;
    
        mMissingLongOptionOperand = new MMissingLongOptionOperand(pOptionName, pOperandName, this);
    
        return mMissingLongOptionOperand;
    }
    
    public MMissingShortOptionOperand newMissingShortOptionOperand(String pOptionName, String pOperandName){
        MMissingShortOptionOperand mMissingShortOptionOperand;
    
        mMissingShortOptionOperand = new MMissingShortOptionOperand(pOptionName, pOperandName, this);
    
        return mMissingShortOptionOperand;
    }
    
    public MInvalidLongOption newInvalidLongOption(String pOptionName){
        MInvalidLongOption mInvalidLongOption;
    
        mInvalidLongOption = new MInvalidLongOption(pOptionName, this);
    
        return mInvalidLongOption;
    }
    
    public MSpuriousLongOptionOperand newSpuriousLongOptionOperand(String pOptionName, String pOperandText){
        MSpuriousLongOptionOperand mSpuriousLongOptionOperand;
    
        mSpuriousLongOptionOperand = new MSpuriousLongOptionOperand(pOptionName, pOperandText, this);
    
        return mSpuriousLongOptionOperand;
    }
    
    public MInvalidShortOption newInvalidShortOption(String pOptionName){
        MInvalidShortOption mInvalidShortOption;
    
        mInvalidShortOption = new MInvalidShortOption(pOptionName, this);
    
        return mInvalidShortOption;
    }
    
    public MSpuriousShortOptionOperand newSpuriousShortOptionOperand(String pOptionName, String pOperandText){
        MSpuriousShortOptionOperand mSpuriousShortOptionOperand;
    
        mSpuriousShortOptionOperand = new MSpuriousShortOptionOperand(pOptionName, pOperandText, this);
    
        return mSpuriousShortOptionOperand;
    }
    
    public MUnknownTarget newUnknownTarget(String pTarget){
        MUnknownTarget mUnknownTarget;
    
        mUnknownTarget = new MUnknownTarget(pTarget, this);
    
        return mUnknownTarget;
    }
    
    public MInvalidArgumentCount newInvalidArgumentCount(){
        MInvalidArgumentCount mInvalidArgumentCount;
    
        mInvalidArgumentCount = new MInvalidArgumentCount(this);
    
        return mInvalidArgumentCount;
    }
    
    public MInvalidObjectmacroSuffix newInvalidObjectmacroSuffix(String pFileName){
        MInvalidObjectmacroSuffix mInvalidObjectmacroSuffix;
    
        mInvalidObjectmacroSuffix = new MInvalidObjectmacroSuffix(pFileName, this);
    
        return mInvalidObjectmacroSuffix;
    }
    
    public MInvalidIntermediateSuffix newInvalidIntermediateSuffix(String pFileName){
        MInvalidIntermediateSuffix mInvalidIntermediateSuffix;
    
        mInvalidIntermediateSuffix = new MInvalidIntermediateSuffix(pFileName, this);
    
        return mInvalidIntermediateSuffix;
    }
    
    public MMissingMacroFile newMissingMacroFile(String pFileName){
        MMissingMacroFile mMissingMacroFile;
    
        mMissingMacroFile = new MMissingMacroFile(pFileName, this);
    
        return mMissingMacroFile;
    }
    
    public MMacroNotFile newMacroNotFile(String pFileName){
        MMacroNotFile mMacroNotFile;
    
        mMacroNotFile = new MMacroNotFile(pFileName, this);
    
        return mMacroNotFile;
    }
    
    public MInputError newInputError(String pFileName, String pMessage){
        MInputError mInputError;
    
        mInputError = new MInputError(pFileName, pMessage, this);
    
        return mInputError;
    }
    
    public MOutputError newOutputError(String pFileName, String pMessage){
        MOutputError mOutputError;
    
        mOutputError = new MOutputError(pFileName, pMessage, this);
    
        return mOutputError;
    }
    
    public MSemanticErrorHead newSemanticErrorHead(){
        MSemanticErrorHead mSemanticErrorHead;
    
        mSemanticErrorHead = new MSemanticErrorHead(this);
    
        return mSemanticErrorHead;
    }
    
    public MUnknownMacro newUnknownMacro(String pName, String pLine, String pChar){
        MUnknownMacro mUnknownMacro;
    
        mUnknownMacro = new MUnknownMacro(pName, pLine, pChar, this);
    
        return mUnknownMacro;
    }
    
    public MPlainText newPlainText(String pString){
        MPlainText mPlainText;
    
        mPlainText = new MPlainText(pString, this);
    
        return mPlainText;
    }
    
    public MUnknownVersion newUnknownVersion(String pName, String pLine, String pChar){
        MUnknownVersion mUnknownVersion;
    
        mUnknownVersion = new MUnknownVersion(pName, pLine, pChar, this);
    
        return mUnknownVersion;
    }
    
    public MDuplicateDeclaration newDuplicateDeclaration(String pName, String pLine, String pChar, String pRefLine, String pRefChar){
        MDuplicateDeclaration mDuplicateDeclaration;
    
        mDuplicateDeclaration = new MDuplicateDeclaration(pName, pLine, pChar, pRefLine, pRefChar, this);
    
        return mDuplicateDeclaration;
    }
    
    public MDuplicateMacroVersionDeclaration newDuplicateMacroVersionDeclaration(String pName, String pLine, String pChar, String pRefLine, String pRefChar, String pVersion){
        MDuplicateMacroVersionDeclaration mDuplicateMacroVersionDeclaration;
    
        mDuplicateMacroVersionDeclaration = new MDuplicateMacroVersionDeclaration(pName, pLine, pChar, pRefLine, pRefChar, pVersion, this);
    
        return mDuplicateMacroVersionDeclaration;
    }
    
    public MDuplicateOption newDuplicateOption(String pName, String pLine, String pChar, String pRefLine, String pRefChar){
        MDuplicateOption mDuplicateOption;
    
        mDuplicateOption = new MDuplicateOption(pName, pLine, pChar, pRefLine, pRefChar, this);
    
        return mDuplicateOption;
    }
    
    public MConflictingOption newConflictingOption(String pName, String pLine, String pChar, String pRefName, String pRefLine, String pRefChar){
        MConflictingOption mConflictingOption;
    
        mConflictingOption = new MConflictingOption(pName, pLine, pChar, pRefName, pRefLine, pRefChar, this);
    
        return mConflictingOption;
    }
    
    public MUnknownOption newUnknownOption(String pName, String pLine, String pChar){
        MUnknownOption mUnknownOption;
    
        mUnknownOption = new MUnknownOption(pName, pLine, pChar, this);
    
        return mUnknownOption;
    }
    
    public MEndMismatch newEndMismatch(String pName, String pLine, String pChar, String pRefName, String pRefLine, String pRefChar){
        MEndMismatch mEndMismatch;
    
        mEndMismatch = new MEndMismatch(pName, pLine, pChar, pRefName, pRefLine, pRefChar, this);
    
        return mEndMismatch;
    }
    
    public MUnknownParam newUnknownParam(String pName, String pLine, String pChar){
        MUnknownParam mUnknownParam;
    
        mUnknownParam = new MUnknownParam(pName, pLine, pChar, this);
    
        return mUnknownParam;
    }
    
    public MParamCyclicReference newParamCyclicReference(String pReference, String pLine, String pChar, String pContext, String pContextLine, String pContextChar){
        MParamCyclicReference mParamCyclicReference;
    
        mParamCyclicReference = new MParamCyclicReference(pReference, pLine, pChar, pContext, pContextLine, pContextChar, this);
    
        return mParamCyclicReference;
    }
    
    public MSelfReference newSelfReference(String pReference, String pLine, String pChar, String pContext, String pContextLine, String pContextChar){
        MSelfReference mSelfReference;
    
        mSelfReference = new MSelfReference(pReference, pLine, pChar, pContext, pContextLine, pContextChar, this);
    
        return mSelfReference;
    }
    
    public MUnusedTextBlock newUnusedTextBlock(String pName, String pLine, String pChar){
        MUnusedTextBlock mUnusedTextBlock;
    
        mUnusedTextBlock = new MUnusedTextBlock(pName, pLine, pChar, this);
    
        return mUnusedTextBlock;
    }
    
    public MUnusedParam newUnusedParam(String pName, String pLine, String pChar){
        MUnusedParam mUnusedParam;
    
        mUnusedParam = new MUnusedParam(pName, pLine, pChar, this);
    
        return mUnusedParam;
    }
    
    public MIncorrectArgumentCount newIncorrectArgumentCount(String pLine, String pChar, String pExpectedCount, String pCurrentCount){
        MIncorrectArgumentCount mIncorrectArgumentCount;
    
        mIncorrectArgumentCount = new MIncorrectArgumentCount(pLine, pChar, pExpectedCount, pCurrentCount, this);
    
        return mIncorrectArgumentCount;
    }
    
    public MIncorrectArgumentType newIncorrectArgumentType(String pExpected, String pFound, String pLine, String pChar){
        MIncorrectArgumentType mIncorrectArgumentType;
    
        mIncorrectArgumentType = new MIncorrectArgumentType(pExpected, pFound, pLine, pChar, this);
    
        return mIncorrectArgumentType;
    }
    
    public MCannotCreateDirectory newCannotCreateDirectory(String pLocation){
        MCannotCreateDirectory mCannotCreateDirectory;
    
        mCannotCreateDirectory = new MCannotCreateDirectory(pLocation, this);
    
        return mCannotCreateDirectory;
    }
    
    public MBodyTokenMisused newBodyTokenMisused(String pLine, String pChar){
        MBodyTokenMisused mBodyTokenMisused;
    
        mBodyTokenMisused = new MBodyTokenMisused(pLine, pChar, this);
    
        return mBodyTokenMisused;
    }
    
    public MIndentTokenMisused newIndentTokenMisused(String pLine, String pChar){
        MIndentTokenMisused mIndentTokenMisused;
    
        mIndentTokenMisused = new MIndentTokenMisused(pLine, pChar, this);
    
        return mIndentTokenMisused;
    }
    
    public MDuplicateMacroRef newDuplicateMacroRef(String pParam, String pMacro, String pLine, String pChar){
        MDuplicateMacroRef mDuplicateMacroRef;
    
        mDuplicateMacroRef = new MDuplicateMacroRef(pParam, pMacro, pLine, pChar, this);
    
        return mDuplicateMacroRef;
    }
    
    public MIncorrectMacroType newIncorrectMacroType(String pExpected, String pFound, String pIndex, String pLine, String pChar){
        MIncorrectMacroType mIncorrectMacroType;
    
        mIncorrectMacroType = new MIncorrectMacroType(pExpected, pFound, pIndex, pLine, pChar, this);
    
        return mIncorrectMacroType;
    }
    
    public MInvalidInsert newInvalidInsert(String pLine, String pChar, String pName){
        MInvalidInsert mInvalidInsert;
    
        mInvalidInsert = new MInvalidInsert(pLine, pChar, pName, this);
    
        return mInvalidInsert;
    }
}