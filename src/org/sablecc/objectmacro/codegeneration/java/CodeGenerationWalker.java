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
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.codegeneration.*;
import org.sablecc.objectmacro.codegeneration.java.macro.*;
import org.sablecc.objectmacro.codegeneration.java.structure.Macro;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.*;
import org.sablecc.objectmacro.intermediate.syntax3.node.*;
import org.sablecc.objectmacro.util.Utils;

public class CodeGenerationWalker
        extends DepthFirstAdapter {

    private final IntermediateRepresentation ir;

    private final File packageDirectory;

    private MMacro currentMacroToBuild;

    private Macro currentMacro;

    private MConstructor currentConstructor;

    private MSuperMacro superMacro;

    private MInternalsInitializer mInternalsInitializer;

    private MMacroBuilder currentMacroBuilder;

    //Macro inside init_internals_method
    private MApplyInternalsInitializer currentApplyInitializer;

    //Macro in add and addAll to only do type verification
    private MApplyInternalsInitializer currentEmptyApplyInitializer;

    private MRedefinedInternalsSetter currentRedefinedInternalsSetter;

    private MRedefinedInternalsSetter currentEmptyRedefinedInternalsSetter;

    private Integer indexBuilder = 0;

    private Integer indexInsert = 0;

    private String currentMacroRefName;

    private final Map<String, Macro> macros;

    private String currentContextName;

    private MInsertMacroPart currentInsertMacroPart;

    private List<String> contextNames = new ArrayList<>();

    private List<String> createdBuilders = new ArrayList<>();

    private List<Integer> createdInserts = new ArrayList<>();

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



    private String getLetterFromInteger(
            Integer i){

        return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
    }

    @Override
    public void inAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        this.superMacro = new MSuperMacro();
        this.mInternalsInitializer = new MInternalsInitializer();

        if(!this.ir.getDestinationPackage().equals("")){
            String destinationPackage = this.ir.getDestinationPackage();
            this.superMacro.newPackageDeclaration(destinationPackage);
            this.mInternalsInitializer.newPackageDeclaration(destinationPackage);
        }

        this.superMacro.newImportJavaUtil();
        this.mInternalsInitializer.newImportJavaUtil();

    }

    @Override
    public void outAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        GenerationUtils.writeFile(this.packageDirectory, "Macro.java", this.superMacro.toString());
        GenerationUtils
                .writeFile(this.packageDirectory,"InternalsInitializer.java", this.mInternalsInitializer.toString());
    }

    @Override
    public void inAMacro(
            AMacro node) {

        String macroName = GenerationUtils.buildNameCamelCase(node.getNames());
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
            //Verify if parameter exist
            if(this.currentMacro.getParameters().contains(param_name)){
                this.currentMacroBuilder.newInitInternalsCall(param_name);
            }
        }

        this.currentMacroToBuild.newImportJavaUtil();

        if(node.getInternals().size() > 0){
            //method build is package protected so a context parameter to build the current macro
            this.currentMacroBuilder.newContextParam();
            this.currentMacroBuilder.newContextExpansion();
            this.currentMacroBuilder.newNewContextExpansion();
        }
        else{
            this.currentMacroBuilder.newPublic();
            this.currentMacroToBuild.newEmptyBuilderWithContext();
            this.currentMacroBuilder.newBuildVerification(macroName);
        }
    }

    @Override
    public void caseAInternal(
            AInternal node) {

        String paramName = GenerationUtils.buildNameCamelCase(node.getNames());

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
            this.currentParamMacroRefBuilder.newContextName(paramName.concat(
                    GenerationUtils.CONTEXT_STRING));

            MParamMacroRef mParamMacroRef = this.currentMacroToBuild.newParamMacroRef(paramName);
            mParamMacroRef.newGetInternalTail();
            mParamMacroRef.newContextParam();

            //Initialize directives before type because of conflicts with stringBuilder
            for (PDirective directive : node.getDirectives()) {
                directive.apply(this);
            }

            this.indexBuilder = 0;

            this.currentContextName = paramName.concat(GenerationUtils.CONTEXT_STRING);
            this.contextNames.add(currentContextName);
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

        this.currentContextName = null;
        this.currentApplyInitializer = null;
        this.indexBuilder = 0;
        this.indexInsert = 0;
        this.currentParamMacroRefBuilder = null;
        this.createdBuilders = new ArrayList<>();
        this.createdInserts = new ArrayList<>();
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
            this.currentConstructor.newSetParam(paramName).newParamArg(paramName);
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

            this.currentContextName = this.currentParamName.concat(
                    GenerationUtils.CONTEXT_STRING);
            this.indexBuilder = 0;

            MParamMacroSetter mParamMacroSetter = this.currentMacroToBuild.newParamMacroSetter(paramName, this.currentMacro.getName());

            this.currentEmptyApplyInitializer = mParamMacroSetter.newApplyInternalsInitializer(paramName);
            this.currentApplyInitializer = this.currentMacroToBuild.newInitInternalsMethod(paramName)
                            .newApplyInternalsInitializer(paramName);
            this.contextNames.add(currentContext);
            this.currentConstructor.newInitMacroParam(paramName);
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
        this.currentEmptyApplyInitializer = null;
        this.indexBuilder = 0;
        this.indexInsert = 0;
        this.createdBuilders = new ArrayList<>();
        this.createdInserts = new ArrayList<>();
        this.currentParamMacroRefBuilder = null;
    }

    @Override
    public void inADirective(
            ADirective node) {

        String directive_name = GenerationUtils.buildName(node.getNames());
        switch (directive_name) {

            case GenerationUtils.SEPARATOR_DIRECTIVE:
                this.currentSeparator = this.currentParamMacroRefBuilder.newSeparator();
                break;

            case GenerationUtils.AFTER_LAST_DIRECTIVE:
                this.currentAfterLast = this.currentParamMacroRefBuilder.newAfterLast();
                break;

            case GenerationUtils.BEFORE_FIRST_DIRECTIVE:
                this.currentBeforeFirst = this.currentParamMacroRefBuilder
                        .newBeforeFirst();
                break;

            case GenerationUtils.NONE_DIRECTIVE:
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

        this.currentMacroRefName = GenerationUtils.buildNameCamelCase(node.getNames());

        if(this.currentContextName != null){
            this.currentRedefinedInternalsSetter =
                    this.currentApplyInitializer.newRedefinedInternalsSetter(
                            this.currentMacroRefName);
        }

        if(this.currentEmptyApplyInitializer != null){
            this.currentEmptyRedefinedInternalsSetter =
                    this.currentEmptyApplyInitializer.newRedefinedInternalsSetter(
                            currentMacroRefName);
        }
    }

    @Override
    public void outAMacroRef(
            AMacroRef node) {

        this.currentMacroRefName = null;
    }

    @Override
    public void caseAStringValue(
            AStringValue node) {

        this.indexBuilder++;
        String index_builder = String.valueOf(this.indexBuilder);
        boolean anyContext = this.currentContextName != null;

        if(anyContext){
            this.currentRedefinedInternalsSetter.newInitStringBuilder(index_builder);

            this.currentRedefinedInternalsSetter.newSetInternal(
                    this.currentMacroRefName,
                    GenerationUtils.buildNameCamelCase(node.getParamName()),
                    this.currentContextName).newStringBuilderBuild(index_builder);

            for(PTextPart part : node.getParts()){
                part.apply(this);
            }
        }
        else{
            index_builder = getLetterFromInteger(this.indexBuilder);

            //Avoid declaring stringbuilder of the same name
            while(this.createdBuilders.contains(index_builder)){
                this.indexBuilder++;
                index_builder = getLetterFromInteger(this.indexBuilder);
            }

            this.currentInsertMacroPart.newInitStringBuilder(index_builder);
            this.createdBuilders.add(index_builder);

            //To avoid modification on indexes
            Integer tempIndexBuilder = this.indexBuilder;
            Integer tempIndexInsert = this.indexInsert;

            for(PTextPart part : node.getParts()){
                part.apply(this);
            }

            this.indexBuilder = tempIndexBuilder;
            this.indexInsert = tempIndexInsert;

            this.currentInsertMacroPart.newSetInternal(
                    GenerationUtils.INSERT_VAR_NAME.concat(String.valueOf(this.indexInsert)),
                    GenerationUtils.buildNameCamelCase(node.getParamName()),
                    "null").newStringBuilderBuild(index_builder);
        }
    }

    @Override
    public void caseAStringTextPart(
            AStringTextPart node) {

        String index_builder = String.valueOf(this.indexBuilder);

        if(this.currentContextName != null
                && this.currentRedefinedInternalsSetter != null){

            this.currentRedefinedInternalsSetter.newStringPart(
                    GenerationUtils.escapedString(node.getString()),
                    String.valueOf(this.indexBuilder));

        }
        else {

            String string = GenerationUtils.escapedString(node.getString());

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
        String param_name = GenerationUtils.buildNameCamelCase(node.getNames());
        if(this.currentContextName != null
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

        if(this.currentContextName != null
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
            }
            else if(this.currentBeforeFirst != null){
                this.currentBeforeFirst.newEolPart(index_builder);
            }
            else if(this.currentAfterLast != null){
                this.currentAfterLast.newEolPart(index_builder);
            }
            else if(this.currentSeparator != null){
                this.currentSeparator.newEolPart(index_builder);
            }
        }
    }

    @Override
    public void caseAInsertTextPart(
            AInsertTextPart node) {

        MInsertMacroPart tempInsertMacroPart = this.currentInsertMacroPart;
        AMacroRef macroRef = (AMacroRef) node.getMacroRef();
        String macro_name = GenerationUtils.buildNameCamelCase(macroRef.getNames());
        String index_builder = String.valueOf(this.indexBuilder);

        //Avoid declaring insert of the same name
        while(this.createdInserts.contains(this.indexInsert)){
            this.indexInsert++;
        }

        String index_insert = String.valueOf(this.indexInsert);

        if(this.currentContextName != null
                && this.currentRedefinedInternalsSetter != null){

            this.currentInsertMacroPart =
                    this.currentRedefinedInternalsSetter.newInsertMacroPart(
                            macro_name,
                            index_builder,
                            index_insert);
        }
        else{
            if(tempInsertMacroPart != null){
                index_builder = getLetterFromInteger(this.indexBuilder);

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
        this.createdInserts.add(this.indexInsert);

        String tempContext = this.currentContextName;
        String tempMacroName = this.currentMacroRefName;
        Integer tempIndex = this.indexBuilder;
        Integer tempIndexInsert = this.indexInsert;
        this.currentContextName = null;

        node.getMacroRef().apply(this);

        this.indexBuilder = tempIndex;
        this.indexInsert = tempIndexInsert;
        this.currentContextName = tempContext;
        this.currentMacroRefName = tempMacroName;
        this.currentInsertMacroPart = tempInsertMacroPart;

    }

    @Override
    public void outAVarValue(
            AVarValue node) {

        String var_name = GenerationUtils.buildNameCamelCase(node.getNames());

        if(this.currentContextName != null){

            MParamRef paramRef = this.currentRedefinedInternalsSetter.newSetInternal(
                    this.currentMacroRefName,
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
                            GenerationUtils.INSERT_VAR_NAME.concat(String.valueOf(this.indexInsert)),
                            GenerationUtils.buildNameCamelCase(node.getParamName()),
                        "null").newParamRef(var_name);

            if(this.currentMacro.getInternalsName().contains(var_name)){
                mParamRef.newContextArg();
            }
        }


    }

    @Override
    public void outAMacro(
            AMacro node) {

        String macroName = GenerationUtils.buildNameCamelCase(node.getNames());
        GenerationUtils.writeFile(this.packageDirectory, "M" + macroName + ".java", this.currentMacroToBuild.toString());

        this.contextNames = null;
        this.currentMacroToBuild = null;
        this.currentConstructor = null;
    }

    @Override
    public void caseAStringMacroPart(AStringMacroPart node) {

        this.currentMacroBuilder.newStringPart(
                GenerationUtils.escapedString(node.getString()),
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
        String macro_name = GenerationUtils.buildNameCamelCase(macroRef.getNames());
        this.indexInsert++;

        this.currentInsertMacroPart =
                this.currentMacroBuilder.newInsertMacroPart(
                        macro_name, String.valueOf(indexBuilder), String.valueOf(indexInsert));

        this.createdInserts.add(this.indexInsert);
        Integer tempIndexBuilder = this.indexBuilder;
        Integer tempIndexInsert = this.indexInsert;

        node.getMacroRef().apply(this);

        this.indexInsert = tempIndexInsert;
        this.indexBuilder = tempIndexBuilder;
        this.currentInsertMacroPart = null;
    }

    @Override
    public void outAVarMacroPart(
            AVarMacroPart node) {

        String param_name = GenerationUtils.buildNameCamelCase(node.getNames());
        MParamInsertPart mParamInsertPart =
                this.currentMacroBuilder.newParamInsertPart(
                        param_name,
                        String.valueOf(indexBuilder));

        if(this.currentMacro.getInternalsName().contains(param_name)){
            mParamInsertPart.newContextArg();
        }
    }
}
