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

package org.sablecc.objectmacro.codegeneration.java;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.codegeneration.*;
import org.sablecc.objectmacro.codegeneration.c.macro.MParam;
import org.sablecc.objectmacro.codegeneration.java.macro.*;
import org.sablecc.objectmacro.codegeneration.java.structure.Macro;
import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.*;
import org.sablecc.objectmacro.intermediate.syntax3.node.*;
import org.sablecc.objectmacro.util.Utils;

public class CodeGenerationWalker
        extends DepthFirstAdapter {

    private static final String CONTEXT_STRING = "Context";

    private static final String INSERT_VAR_NAME = "insert_";

    private static final String SEPARATOR_DIRECTIVE = "separator";

    private static final String AFTER_LAST_DIRECTIVE = "afterlast";

    private static final String NONE_DIRECTIVE = "none";

    private static final String BEFORE_FIRST_DIRECTIVE = "beforefirst";

    private final IntermediateRepresentation ir;

    private final File packageDirectory;

    private MMacro currentMacroToBuild;

    private Macro currentMacro;

    private MConstructor currentConstructor;

    private MSuperMacro superMacro;

    private MInternalsInitializer mInternalsInitializer;

    private MContext mContext;

    private MMacroBuilder currentMacroBuilder;

    private MApplyInternalsInitializer currentApplyInitializer;

    private MRedefinedInternalsSetter currentRedefinedInternalsSetter;

    private Integer indexBuilder = 0;

    private Integer indexInsert = 0;

    private String currentMacroName;

    private final Map<String, Macro> macros;

    private String currentContext;

    private MInsertMacroPart currentInsertMacroPart;

    private List<String> contextNames = new ArrayList<>();

    private List<String> createdBuilders = new ArrayList<>();

    private MSeparator currentSeparator;

    private MAfterLast currentAfterLast;

    private MBeforeFirst currentBeforeFirst;

    private MNone currentNone;

    private MParamMacroRefBuilder currentParamMacroRefBuilder;

    public CodeGenerationWalker(
            IntermediateRepresentation ir,
            File packageDirectory,
            Map<String, Macro> macros) {

        this.ir = ir;
        this.packageDirectory = packageDirectory;
        this.macros = macros;
    }

    private String string(
            TString tString) {

        String string = tString.getText();
        int length = string.length();
        return string.substring(1, length - 1);
    }

    private String escapedString(
            TString tString) {

        StringBuilder sb = new StringBuilder();
        String s = string(tString);
        boolean escaped = false;
        for (char c : s.toCharArray()) {
            if (escaped) {
                escaped = false;

                if (c == '\\') {
                    sb.append('\\');
                    sb.append('\\');
                }
                else if (c == '\'') {
                    sb.append('\'');
                }
                else {
                    throw new InternalException("unhandled case");
                }
            }
            else if (c == '\\') {
                escaped = true;
            }
            else if (c == '\"') {
                sb.append('\\');
                sb.append('\"');
            }
            else {
                sb.append(c);
            }
        }

        if (escaped) {
            throw new InternalException("incomplete escape");
        }

        return sb.toString();
    }

    private String buildNameCamelCase(
            LinkedList<TString> name_parts){

        StringBuilder macroName = new StringBuilder();
        for(TString partName : name_parts){
            macroName.append(Utils.toCamelCase(string(partName)));
        }

        return macroName.toString();
    }

    private String buildName(
            LinkedList<TString> name_parts){

        StringBuilder macroName = new StringBuilder();
        for(TString partName : name_parts){
            macroName.append(string(partName));
        }

        return macroName.toString();
    }

    private void writeFile(
            String fileName,
            String content){

        File destination = new File(this.packageDirectory, fileName);

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(content);
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }
    }

    private String getLetterFromInteger(
            Integer i){

        return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
    }

    @Override
    public void inAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        this.superMacro = new MSuperMacro();
        this.mInternalsInitializer = new MInternalsInitializer();
        this.mContext = new MContext();

        if(!this.ir.getDestinationPackage().equals("")){
            String destinationPackage = this.ir.getDestinationPackage();
            this.superMacro.newPackageDeclaration(destinationPackage);
            this.mInternalsInitializer.newPackageDeclaration(destinationPackage);
            this.mContext.newPackageDeclaration(destinationPackage);
        }

        this.superMacro.newImportJavaUtil();
        this.mInternalsInitializer.newImportJavaUtil();

    }

    @Override
    public void outAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        writeFile("Macro.java", this.superMacro.toString());
        writeFile("Context.java", this.mContext.toString());
        writeFile("InternalsInitializer.java", this.mInternalsInitializer.toString());

        MParameterNull mParameterNull = new MParameterNull();
        MIncorrectType mIncorrectType = new MIncorrectType();
        MObjectMacroErrorHead mObjectMacroErrorHead = new MObjectMacroErrorHead();
        MMacroNullInList mMacroNullInList = new MMacroNullInList();
        MObjectMacroException mObjectMacroException = new MObjectMacroException();

        if(!this.ir.getDestinationPackage().equals("")){
            String destinationPackage = this.ir.getDestinationPackage();
            mIncorrectType.newPackageDeclaration(destinationPackage);
            mParameterNull.newPackageDeclaration(destinationPackage);
            mObjectMacroErrorHead.newPackageDeclaration(destinationPackage);
            mMacroNullInList.newPackageDeclaration(destinationPackage);
            mObjectMacroException.newPackageDeclaration(destinationPackage);

        }

        writeFile("MIncorrectType.java", mIncorrectType.toString());
        writeFile("MParameterNull.java", mParameterNull.toString());
        writeFile("MObjectMacroErrorHead.java", mObjectMacroErrorHead.toString());
        writeFile("MMacroNullInList.java", mMacroNullInList.toString());
        writeFile("ObjectMacroException.java", mObjectMacroException.toString());
    }

    @Override
    public void inAMacro(
            AMacro node) {

        String macroName = buildNameCamelCase(node.getNames());
        if(!this.macros.containsKey(macroName)){
            throw new InternalException(macroName + " does not exist");
        }

        this.currentMacro = this.macros.get(macroName);
        this.currentMacroToBuild = this.currentMacro.getMacro();
        this.contextNames = new ArrayList<>();

        if(this.currentMacroToBuild == null){
            throw new InternalException("currentMacroToBuild cannot be null here");
        }

        if (!this.ir.getDestinationPackage().equals("")) {
            this.currentMacroToBuild.newPackageDeclaration(this.ir.getDestinationPackage());
        }

        this.currentConstructor = this.currentMacroToBuild.newConstructor(macroName);
        this.currentMacroBuilder = this.currentMacroToBuild.newMacroBuilder();

        this.mInternalsInitializer.newParentInternalsSetter(macroName);
        this.currentMacroToBuild.newRedefinedApplyInitializer(macroName);

        for(TString string : node.getInitOrder()){
            String param_name = Utils.toCamelCase(string(string));
            if(this.currentMacro.getParameters().contains(param_name)){
                this.currentConstructor.newSetParam(param_name).newParamArg(param_name);
            }
        }

        if(node.getInternals().size() > 0){
            //method build is package protected so a context parameter to build the current macro
            this.currentMacroBuilder.newContextParam();
            this.currentMacroBuilder.newContextExpansion();
            this.currentMacroBuilder.newNewContextExpansion();
            this.currentMacroToBuild.newImportJavaUtil();
        }
        else{
            this.currentMacroBuilder.newPublic();
        }
    }

    @Override
    public void caseAInternal(
            AInternal node) {

        String paramName = buildNameCamelCase(node.getNames());
        this.indexBuilder = 0;

        if(node.getType() instanceof AStringType){
            this.currentMacroToBuild.newInternalStringField(paramName);
            this.currentMacroToBuild.newInternalStringSetter(paramName);

            MParamStringRefBuilder mParamStringRefBuilder = this.currentMacroToBuild
                    .newParamStringRefBuilder(paramName);
            mParamStringRefBuilder.newContextParam();
            mParamStringRefBuilder.newGetInternalTail();

            MParamStringRef mParamStringRef = this.currentMacroToBuild.newParamStringRef(paramName);
            mParamStringRef.newContextParam();
            mParamStringRef.newGetInternalTail();
        }
        else if(node.getType() instanceof AMacroRefsType){
            this.currentMacroToBuild.newInternalMacroField(paramName);
            this.currentMacroToBuild.newContextField(paramName);

            this.currentParamMacroRefBuilder = this.currentMacroToBuild
                    .newParamMacroRefBuilder(paramName, String.valueOf(this.indexBuilder));
            this.currentParamMacroRefBuilder.newContextParam();
            this.currentParamMacroRefBuilder.newGetInternalTail();
            this.currentParamMacroRefBuilder.newContextName(paramName.concat(CONTEXT_STRING));

            MParamMacroRef mParamMacroRef = this.currentMacroToBuild.newParamMacroRef(paramName);
            mParamMacroRef.newGetInternalTail();
            mParamMacroRef.newContextParam();

            //Initialize directives before type because of conflicts with stringBuilder
            for (PDirective directive : node.getDirectives()) {
                directive.apply(this);
            }

            this.indexBuilder = 0;

            this.currentContext = paramName.concat(CONTEXT_STRING);
            this.contextNames.add(currentContext);
            this.currentApplyInitializer =
                    this.currentMacroToBuild.newInternalMacroSetter(paramName)
                            .newApplyInternalsInitializer(paramName);

        }
        else{
            throw new InternalException("case unhandled");
        }
        node.getType().apply(this);
        outAInternal(node);
    }

    @Override
    public void outAInternal(
            AInternal node) {

        this.currentContext = null;
        this.currentApplyInitializer = null;
        this.indexBuilder = 0;
        this.indexInsert = 0;
        this.currentParamMacroRefBuilder = null;
        this.createdBuilders.clear();
    }

    @Override
    public void caseAParam(
            AParam node) {

        String paramName = buildNameCamelCase(node.getNames());

        if(node.getType() instanceof AStringType){
            this.currentMacroToBuild.newParamStringField(paramName);
            this.currentMacroToBuild.newParamStringRefBuilder(paramName);
            this.currentMacroToBuild.newParamStringRef(paramName);

            MParamStringSetter mParamStringSetter = this.currentMacroToBuild.newParamStringSetter(paramName);
            mParamStringSetter.newParamArg(paramName);
            mParamStringSetter.newStringParam(paramName);


            this.currentConstructor.newStringParam(paramName);
        }
        else if(node.getType() instanceof AMacroRefsType){

            this.currentMacroToBuild.newParamMacroField(paramName);
            this.currentMacroToBuild.newContextField(paramName);

            this.currentParamMacroRefBuilder = this.currentMacroToBuild.newParamMacroRefBuilder(
                    paramName, String.valueOf(this.indexBuilder));
            this.currentParamMacroRefBuilder.newContextName(paramName.concat(CONTEXT_STRING));
            this.currentMacroToBuild.newParamMacroRef(paramName);

            for (PDirective directive : node.getDirectives()) {
                directive.apply(this);
            }

            this.currentContext = paramName.concat(CONTEXT_STRING);
            this.indexBuilder = 0;

            MParamMacroSetter mParamMacroSetter = this.currentMacroToBuild.newParamMacroSetter(paramName);
            mParamMacroSetter.newParamArg(paramName);
            mParamMacroSetter.newMacroParam(paramName);

            this.currentConstructor.newMacroParam(paramName);
            this.currentApplyInitializer = mParamMacroSetter.newApplyInternalsInitializer(paramName);
            this.contextNames.add(currentContext);
        }
        else{
            throw new InternalException("case unhandled");
        }

        node.getType().apply(this);
        outAParam(node);
    }

    @Override
    public void outAParam(AParam node) {

        this.currentContext = null;
        this.currentApplyInitializer = null;
        this.indexBuilder = 0;
        this.indexInsert = 0;
        this.createdBuilders.clear();
        this.currentParamMacroRefBuilder = null;
    }

    @Override
    public void inADirective(
            ADirective node) {

        String directive_name = buildName(node.getNames());
        switch (directive_name) {

            case SEPARATOR_DIRECTIVE:
                this.currentSeparator = this.currentParamMacroRefBuilder.newSeparator();
                break;

            case AFTER_LAST_DIRECTIVE:
                this.currentAfterLast = this.currentParamMacroRefBuilder.newAfterLast();
                break;

            case BEFORE_FIRST_DIRECTIVE:
                this.currentBeforeFirst = this.currentParamMacroRefBuilder
                        .newBeforeFirst();
                break;

            case NONE_DIRECTIVE:
                this.currentNone = this.currentParamMacroRefBuilder.newNone();
                break;

            default:
                throw new InternalException("case unhandled");
        }
    }

    @Override
    public void outADirective(
            ADirective node) {

        this.currentSeparator = null;
        this.currentAfterLast = null;
        this.currentBeforeFirst = null;
        this.currentNone = null;
    }

    @Override
    public void inAMacroRef(
            AMacroRef node) {

        this.currentMacroName = buildNameCamelCase(node.getNames());

        if(this.currentContext != null){
            this.currentRedefinedInternalsSetter = this.currentApplyInitializer.newRedefinedInternalsSetter(
                    currentMacroName);
        }
    }

    @Override
    public void outAMacroRef(
            AMacroRef node) {

        this.currentMacroName = null;
    }

    @Override
    public void inAStringValue(
            AStringValue node) {

        this.indexBuilder++;
        String index_builder = String.valueOf(this.indexBuilder);
        boolean anyContext = this.currentContext != null;

        if(anyContext){
            this.currentRedefinedInternalsSetter.newInitStringBuilder(index_builder);

            this.currentRedefinedInternalsSetter.newSetInternal(
                    this.currentMacroName,
                    buildNameCamelCase(node.getParamName()),
                    this.currentContext).newStringBuilderBuild(index_builder);
        }
        else{

            index_builder = getLetterFromInteger(this.indexBuilder);

            //Avoid declaring stringbuilder of the same name
            if(this.createdBuilders.contains(index_builder)){
                this.indexBuilder++;
                index_builder = getLetterFromInteger(this.indexBuilder);
            }

            this.currentInsertMacroPart.newInitStringBuilder(index_builder);
            this.currentInsertMacroPart.newSetInternal(
                    INSERT_VAR_NAME.concat(String.valueOf(this.indexInsert)),
                    buildNameCamelCase(node.getParamName()),
                    "null").newStringBuilderBuild(index_builder);

            this.createdBuilders.add(index_builder);
        }
    }

    @Override
    public void caseAStringTextPart(
            AStringTextPart node) {

        String index_builder = String.valueOf(this.indexBuilder);

        if(this.currentContext != null
                && this.currentRedefinedInternalsSetter != null){

            this.currentRedefinedInternalsSetter.newStringPart(
                    escapedString(node.getString()),
                    String.valueOf(this.indexBuilder));

        }
        else {

            String string = escapedString(node.getString());

            if(this.currentInsertMacroPart != null){
                index_builder = getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.newStringPart(
                        string,
                        index_builder);
            }
            else if(this.currentNone != null){
                this.currentNone.newStringPart(
                        string,
                        index_builder);
            }
            else if(this.currentBeforeFirst != null){
                this.currentBeforeFirst.newStringPart(
                        string,
                        index_builder);
            }
            else if(this.currentAfterLast != null){
                this.currentAfterLast.newStringPart(
                        string,
                        index_builder);
            }
            else if(this.currentSeparator != null){
                this.currentSeparator.newStringPart(
                        string,
                        index_builder);
            }
        }
    }

    @Override
    public void caseAVarTextPart(
            AVarTextPart node) {

        String index_builder = String.valueOf(this.indexBuilder);
        String param_name = buildNameCamelCase(node.getNames());
        if(this.currentContext != null
                && this.currentRedefinedInternalsSetter != null){

            this.currentRedefinedInternalsSetter.newParamInsertPart(
                    param_name,
                    index_builder);
        }
        else {

            if(this.currentInsertMacroPart != null){
                index_builder = getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.newParamInsertPart(
                        param_name,
                        index_builder);
            }
            else if(this.currentNone != null){
                this.currentNone.newParamInsertPart(
                        param_name,
                        index_builder);
            }
            else if(this.currentBeforeFirst != null){
                this.currentBeforeFirst.newParamInsertPart(
                        param_name,
                        index_builder);
            }
            else if(this.currentAfterLast != null){
                this.currentAfterLast.newParamInsertPart(
                        param_name,
                        index_builder);
            }
            else if(this.currentSeparator != null){
                this.currentSeparator.newParamInsertPart(
                        param_name,
                        index_builder);
            }
        }
    }

    @Override
    public void caseAEolTextPart(
            AEolTextPart node) {

        String index_builder = String.valueOf(indexBuilder);

        if(this.currentContext != null
                && this.currentRedefinedInternalsSetter != null){

            this.currentRedefinedInternalsSetter.newEolPart(index_builder);

        }
        else {

            if(this.currentInsertMacroPart != null){
                index_builder = getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.newEolPart(
                        index_builder);
            }
            else if(this.currentNone != null){
                this.currentNone.newEolPart(index_builder);

            }else if(this.currentBeforeFirst != null){
                this.currentBeforeFirst.newEolPart(index_builder);

            }else if(this.currentAfterLast != null){
                this.currentAfterLast.newEolPart(index_builder);

            }else if(this.currentSeparator != null){
                this.currentSeparator.newEolPart(index_builder);
            }
        }
    }

    @Override
    public void caseAInsertTextPart(
            AInsertTextPart node) {

        MInsertMacroPart tempInsertMacroPart = this.currentInsertMacroPart;
        AMacroRef macroRef = (AMacroRef) node.getMacroRef();
        String macro_name = buildNameCamelCase(macroRef.getNames());
        String index_builder = String.valueOf(this.indexBuilder);

        this.indexInsert++;
        String index_insert = String.valueOf(this.indexInsert);

        if(this.currentContext != null
                && this.currentRedefinedInternalsSetter != null){
            this.currentInsertMacroPart =
                    this.currentRedefinedInternalsSetter.newInsertMacroPart(
                            macro_name,
                            index_builder,
                            index_insert);

        }else{
            if(tempInsertMacroPart != null){
                this.currentInsertMacroPart =
                        tempInsertMacroPart.newInsertMacroPart(
                                macro_name,
                                index_builder,
                                index_insert);
            }
            else if(this.currentNone != null){
                this.currentInsertMacroPart =
                    this.currentNone.newInsertMacroPart(macro_name,
                            index_builder,
                            index_insert);

            }else if(this.currentBeforeFirst != null){
                this.currentInsertMacroPart =
                        this.currentBeforeFirst.newInsertMacroPart(macro_name,
                            index_builder,
                            index_insert);

            }else if(this.currentAfterLast != null){
                this.currentInsertMacroPart =
                    this.currentAfterLast.newInsertMacroPart(macro_name,
                            index_builder,
                            index_insert);

            }else if(this.currentSeparator != null){
                this.currentInsertMacroPart =
                    this.currentSeparator.newInsertMacroPart(macro_name,
                            index_builder,
                            index_insert);
            }
        }

        String tempContext = this.currentContext;
        String tempMacroName = this.currentMacroName;
        Integer tempIndex = this.indexBuilder;
        this.currentContext = null;

        node.getMacroRef().apply(this);

        this.indexBuilder = tempIndex;
        this.currentContext = tempContext;
        this.currentMacroName = tempMacroName;
        this.currentInsertMacroPart = tempInsertMacroPart;

    }

    @Override
    public void outAVarValue(
            AVarValue node) {

        String var_name = buildNameCamelCase(node.getNames());

        if(this.currentContext != null){
            MParamRef paramRef = this.currentRedefinedInternalsSetter.newSetInternal(
                    this.currentMacroName,
                    buildNameCamelCase(node.getParamName()),
                    this.currentContext)
                        .newParamRef(var_name);

            if(this.currentMacro.getInternals().contains(var_name)){
                paramRef.newContextName(this.currentContext);
            }
        }
        else{
            MParamRef mParamRef =
                    this.currentInsertMacroPart.newSetInternal(
                        INSERT_VAR_NAME.concat(String.valueOf(this.indexInsert)),
                        buildNameCamelCase(node.getParamName()),
                        "null").newParamRef(var_name);

            if(this.currentMacro.getInternals().contains(var_name)){
                mParamRef.newContextArg();
            }
        }


    }

    @Override
    public void outAMacro(
            AMacro node) {

        String macroName = buildNameCamelCase(node.getNames());
        writeFile("M" + macroName + ".java", this.currentMacroToBuild.toString());

        this.contextNames = null;
        this.currentMacroToBuild = null;
        this.currentConstructor = null;
    }

    @Override
    public void caseAStringMacroPart(AStringMacroPart node) {

        this.currentMacroBuilder.newStringPart(
                escapedString(node.getString()),
                String.valueOf(indexBuilder));
    }

    @Override
    public void outAEolMacroPart(
            AEolMacroPart node) {

        this.currentMacroBuilder.newEolPart(String.valueOf(indexBuilder));
    }

    @Override
    public void caseAInsertMacroPart(
            AInsertMacroPart node) {

        AMacroRef macroRef = (AMacroRef) node.getMacroRef();
        String macro_name = buildNameCamelCase(macroRef.getNames());
        this.indexInsert++;

        this.currentInsertMacroPart =
                this.currentMacroBuilder.newInsertMacroPart(
                        macro_name, String.valueOf(indexBuilder), String.valueOf(indexInsert));

        Integer tempIndexBuilder = this.indexBuilder;

        node.getMacroRef().apply(this);

        this.indexBuilder = tempIndexBuilder;
        this.currentInsertMacroPart = null;
    }

    @Override
    public void outAVarMacroPart(
            AVarMacroPart node) {

        String param_name = buildNameCamelCase(node.getNames());
        MParamInsertPart mParamInsertPart =
                this.currentMacroBuilder.newParamInsertPart(
                        param_name,
                        String.valueOf(indexBuilder));

        if(this.currentMacro.getInternals().contains(param_name)){
            mParamInsertPart.newContextArg();
        }
    }
}
