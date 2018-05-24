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

import java.io.File;
import java.util.*;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.codegeneration.IntermediateRepresentation;
import org.sablecc.objectmacro.codegeneration.java.macro.*;
import org.sablecc.objectmacro.codegeneration.java.structure.SMacro;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.*;

public class CodeGenerationWalker
        extends
        DepthFirstAdapter {

    private final IntermediateRepresentation ir;

    private final File packageDirectory;

    /**
     * List of macros in the file
     */
    private final Map<String, SMacro> macros;

    /**
     * Object Macro with name and list of internals and parameters
     */
    private SMacro currentMacro;

    /**
     * Current parameter name
     */
    private String currentParamName;

    /**
     * Macro representing the super class Macro
     */
    private MSuperMacro superMacro;

    /**
     * Macro representing an internal initializer super class
     */
    private MInternalsInitializer mInternalsInitializer;

    /**
     * Macro representing the class macro
     */
    private MMacro currentMacroToBuild;

    /**
     * Macro representing the constructor of a macro
     */
    private MConstructor currentConstructor;

    /**
     * Macro representing the method builder
     */
    private MMacroBuilder currentMacroBuilder;

    /**
     * Macro representing the apply internal initializer inside the method init
     * internals
     */
    private MApplyInternalsInitializer currentApplyInitializer;

    /**
     * Index of the current builder to avoid creating 2 StringBuilder of the
     * same name
     */
    private Integer indexBuilder = 0;

    /**
     * Index of the current builder to avoid creating 2 macro objects for
     * inserts of the same name
     */
    private Integer indexInsert = 0;

    /**
     * Created StringBuilders in the children of a AParameter node or AInternal
     * node This list is reset in the internal or parameter node's out
     */
    private Set<Integer> createdBuilders = new LinkedHashSet<>();

    /**
     * Created macro objects for inserts in the children of a AParameter node or
     * AInternal node This list is reset in the internal or parameter node's out
     */
    private List<Integer> createdInserts = new ArrayList<>();

    /**
     * Name of the current macro which is referenced
     */
    private String currentMacroRefName;

    /**
     * Name of the current context
     */
    private String currentContextName;

    /**
     * Names of all contexts created for the current Macro
     */
    private Set<String> contextNames = new HashSet<>();

    /**
     * Macro representing a parameter's builder
     */
    private MParamMacroRefBuilder currentParamMacroRefBuilder;

    /**
     * Boolean to test whether the macro has or does not have internals
     */
    private boolean currentMacroHasInternals;

    /**
     * Macro representing the package to use in other Macro
     */
    private MPackageDeclaration currentPackageDeclaration;

    private MMacroFactory macroFactory;

    private MSwitchVersion currentSwitchVersion;

    private MMacroCreatorMethod currentMacroCreatorMethod;

    private final Set<TString> versions = new HashSet<>();

    private List<Integer> previouslyUsed = new LinkedList<>();

    private Set<String> createdFactoryMethods = new HashSet<>();

    private boolean currentMacroIsAllVersionned = false;

    private boolean currentMacroIsAbstract = false;

    private List<Macro> tempMacros = null;

    private final Macros factory;

    CodeGenerationWalker(
            IntermediateRepresentation ir,
            File packageDirectory,
            Map<String, SMacro> macros,
            Macros factory) {

        this.ir = ir;
        this.packageDirectory = packageDirectory;
        this.macros = macros;
        this.factory = factory;
    }

    private List<Macro> evalMacros(
            List<? extends Node> nodes){

        List<Macro> result_macros = new LinkedList<>();
        for(Node node : nodes) {
            this.tempMacros = new LinkedList<>();
            node.apply(this);
            result_macros.addAll(this.tempMacros);
        }
        this.tempMacros = null;
        return result_macros;
    }

    private List<Macro> evalMacros(
            Node node){

        this.tempMacros = new LinkedList<>();
        node.apply(this);
        List<Macro> result_macros = this.tempMacros;
        this.tempMacros = null;
        return result_macros;
    }

    @Override
    public void inAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        this.superMacro = this.factory.newSuperMacro();
        this.mInternalsInitializer = this.factory.newInternalsInitializer();
        this.macroFactory = this.factory.newMacroFactory();
        MVersionEnumeration mVersionEnumeration = this.factory.newVersionEnumeration();

        if (!this.ir.getDestinationPackage().equals("")) {
            String destinationPackage = this.ir.getDestinationPackage();
            this.currentPackageDeclaration = this.factory.newPackageDeclaration(
                    destinationPackage);
            this.superMacro
                    .addPackageDeclaration(this.currentPackageDeclaration);
            this.mInternalsInitializer
                    .addPackageDeclaration(this.currentPackageDeclaration);
            this.macroFactory
                    .addPackageDeclaration(this.currentPackageDeclaration);

            mVersionEnumeration.addPackageDeclaration(this.currentPackageDeclaration);
        }
        boolean first = true;

        for(TString version : node.getVersions()){
            String version_name = GenerationUtils.string(version).toUpperCase();
            if(first){
                this.macroFactory.addDefaultVersion(this.factory.newVersion(version_name));
                first = false;
            }

            mVersionEnumeration.addVersions(this.factory.newPlainText(version_name));
            versions.add(version);
        }

        GenerationUtils.writeFile(this.packageDirectory,
                "VERSIONS.java",
                mVersionEnumeration.build());
    }

    @Override
    public void outAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        GenerationUtils.writeFile(this.packageDirectory, "Macro.java",
                this.superMacro.build());
        GenerationUtils.writeFile(this.packageDirectory,
                "InternalsInitializer.java",
                this.mInternalsInitializer.build());

        GenerationUtils.writeFile(this.packageDirectory,
                "Macros.java",
                this.macroFactory.build());
    }

    @Override
    public void caseAMacro(
            AMacro node) {

        this.currentMacroIsAbstract = node.getIsAbstract() != null;
        this.currentMacroIsAllVersionned = node.getIsAllVersionned() != null;

        if(this.currentMacroIsAbstract){
            inAMacro(node);

            for(PParam parameter : node.getParams()){
                parameter.apply(this);
            }

            outAMacro(node);
        }
        else {
            super.caseAMacro(node);
        }
    }

    @Override
    public void inAMacro(
            AMacro node) {

        String macro_name = GenerationUtils.buildNameCamelCase(node.getNames());
        if (!this.macros.containsKey(macro_name)) {
            throw new InternalException(macro_name + " does not exist");
        }

        String parent_name = null;
        if(node.getParent() != null){
            parent_name = GenerationUtils.buildNameCamelCase(node.getParent());
        }

        this.currentMacro = this.macros.get(macro_name);
        this.currentMacroToBuild = this.currentMacro.getMacro();
        this.contextNames = new HashSet<>();

        if (this.currentMacroToBuild == null) {
            throw new InternalException(
                    "currentMacroToBuild cannot be null here");
        }

        if (this.currentPackageDeclaration != null) {
            this.currentMacroToBuild
                    .addPackageDeclaration(this.currentPackageDeclaration);
        }

        if(!this.createdFactoryMethods.contains(macro_name)
                && !this.createdFactoryMethods.contains(parent_name)){

            String method_class_name;
            if(this.currentMacroIsAbstract
                    || this.currentMacroIsAllVersionned){

                method_class_name = macro_name;
            }
            else {
                method_class_name = parent_name;
            }

            this.createdFactoryMethods.add(method_class_name);
            this.currentMacroCreatorMethod = this.factory.newMacroCreatorMethod(method_class_name);
            this.macroFactory.addNewMacroMethods(this.currentMacroCreatorMethod);

            if(!this.currentMacroIsAllVersionned){
                this.currentSwitchVersion = this.factory.newSwitchVersion();
                this.currentMacroCreatorMethod.addVersionFactory(this.currentSwitchVersion);
            }
        }

        if(!this.currentMacroIsAllVersionned){
            for(TString version : node.getVersions()){
                String version_name = GenerationUtils.string(version).toUpperCase();
                this.currentSwitchVersion.addVersionCases(this.factory.newMacroCaseInit(version_name, macro_name));
            }
        }

        this.currentConstructor = this.factory.newConstructor();
        this.currentMacroToBuild.addConstructor(this.currentConstructor);

        if(this.currentMacroIsAbstract){
            this.currentMacroToBuild.addAbstract(this.factory.newAbstract());
        }
        else {
            this.currentMacroBuilder = this.factory.newMacroBuilder(macro_name);
            this.currentMacroToBuild.addMacroBuilder(this.currentMacroBuilder);
            this.currentMacroToBuild.addInitMacrosMethod(this.factory.newSetMacrosMethod());
            this.currentConstructor.addFieldInitializers(this.factory.newSetMacrosCall());

            this.mInternalsInitializer
                    .addParentInternalSetters(this.factory.newParentInternalsSetter(macro_name));
            this.currentMacroToBuild.addRedefinedApplyInitializer(
                    this.factory.newRedefinedApplyInitializer());
        }

        if(!this.currentMacroIsAbstract
                && !this.currentMacroIsAllVersionned){
            this.currentConstructor.addSuper(this.factory.newSuperCall());
        }

        this.currentMacroHasInternals = node.getInternals().size() > 0;

        if (this.currentMacroHasInternals) {
            // method build is package protected so a context parameter to build
            // the current macro
            this.currentMacroBuilder.addContextParam(this.factory.newContextParam());
            this.currentMacroBuilder
                    .addContextBuildState(this.factory.newContextBuildState());
            this.currentMacroBuilder.addNewBuildState(this.factory.newNewBuildState());
        }
        else {
            if(!this.currentMacroIsAbstract){
                this.currentMacroToBuild
                        .addEmptyBuilderWithContext(this.factory.newEmptyBuilderWithContext());
            }
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        if(!this.currentMacroIsAbstract){
            this.currentConstructor.addParameters(this.factory.newMacrosParam());
        }

        String macroName = GenerationUtils.buildNameCamelCase(node.getNames());
        GenerationUtils.writeFile(this.packageDirectory,
                "M" + macroName + ".java", this.currentMacroToBuild.build());

        this.contextNames = null;
        this.currentMacroToBuild = null;
        this.currentMacroBuilder = null;
        this.currentConstructor = null;
        this.currentMacro = null;
        this.currentMacroHasInternals = false;
        this.indexBuilder = 0;
        this.previouslyUsed = new LinkedList<>();
    }

    @Override
    public void caseAInternal(
            AInternal node) {

        String paramName = GenerationUtils.buildNameCamelCase(node.getNames());

        if (node.getType() instanceof AStringType) {
            this.currentMacroToBuild
                    .addFields(this.factory.newInternalStringField(paramName));
            this.currentMacroToBuild
                    .addSetters(this.factory.newInternalStringSetter(paramName));

            MParamStringRefBuilder mParamStringRefBuilder = this.factory.newParamStringRefBuilder(
                    paramName);
            this.currentMacroToBuild.addBuilders(mParamStringRefBuilder);
            mParamStringRefBuilder.addContextParam(this.factory.newContextParam());
            mParamStringRefBuilder.addGetInternalTail(this.factory.newGetInternalTail());

            MParamStringRef mParamStringRef = this.factory.newParamStringRef(paramName);
            this.currentMacroToBuild.addGetters(mParamStringRef);
            mParamStringRef.addContextParam(this.factory.newContextParam());
            mParamStringRef.addGetInternalTail(this.factory.newGetInternalTail());
        }
        else if (node.getType() instanceof AMacroRefsType) {
            this.currentMacroToBuild
                    .addFields(this.factory.newInternalMacroField(paramName));

            this.currentMacroToBuild
                    .addBuilders(this.factory.newInternalMacroRefBuilder(paramName));
            this.currentMacroToBuild
                    .addGetters(this.factory.newInternalMacroRef(paramName));

            this.indexBuilder = 0;
            this.currentMacroToBuild
                    .addSetters(this.factory.newInternalMacroSetter(paramName));
        }
        else {
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
        this.createdBuilders = new LinkedHashSet<>();
        this.createdInserts = new ArrayList<>();
        this.previouslyUsed = new LinkedList<>();
    }

    @Override
    public void caseAParam(
            AParam node) {

        String param_name = this.currentParamName = GenerationUtils
                .buildNameCamelCase(node.getNames());

        MStringParam mStringParam = this.factory.newStringParam(param_name);
        MParamArg mParamArg = this.factory.newParamArg(param_name);

        if (node.getType() instanceof AStringType) {

            if(this.currentMacroIsAbstract
                    || this.currentMacroIsAllVersionned){

                this.currentMacroToBuild
                        .addFields(this.factory.newParamStringField(param_name));
                this.currentMacroToBuild
                        .addBuilders(this.factory.newParamStringRefBuilder(param_name));
                this.currentMacroToBuild.addGetters(this.factory.newParamStringRef(param_name));
                this.currentMacroCreatorMethod.addParameters(mStringParam);
                this.currentMacroCreatorMethod.addArgs(mParamArg);
            }

            if(!this.currentMacroIsAbstract){
                MParamStringSetter mParamStringSetter = this.factory.newParamStringSetter(
                        param_name);
                mParamStringSetter.addParamArg(mParamArg);
                mParamStringSetter.addStringParam(mStringParam);

                this.currentMacroToBuild.addSetters(mParamStringSetter);

                MSetParam mSetParam = this.factory.newSetParam(param_name);
                mSetParam.addSetParam(mParamArg);

                this.currentConstructor.addFieldInitializers(mSetParam);
                this.currentConstructor.addParameters(mStringParam);
            }
        }
        else if (node.getType() instanceof AMacroRefsType) {

            this.currentParamMacroRefBuilder = this.factory.newParamMacroRefBuilder(
                    param_name);

            if(this.currentMacroIsAbstract
                    || this.currentMacroIsAllVersionned){

                this.currentMacroToBuild.addFields(this.factory.newParamMacroField(param_name));

                this.currentMacroToBuild.addGetters(this.factory.newParamMacroRef(param_name));

                this.currentConstructor
                        .addFieldInitializers(this.factory.newInitMacroParam(param_name));
                this.currentConstructor.addInternalValuesInitializers(
                        this.factory.newInitInternalValue(param_name));

                this.currentContextName = param_name
                        .concat(GenerationUtils.CONTEXT_STRING);
            }

            if(!this.currentMacroIsAbstract) {

                this.currentMacroToBuild
                        .addBuilders(this.currentParamMacroRefBuilder);
                this.currentParamMacroRefBuilder.addContextName(this.factory.newPlainText(
                        param_name.concat(GenerationUtils.CONTEXT_STRING)));

                this.currentMacroToBuild.addFields(this.factory.newDirectiveFields(param_name));
                MInitDirectives mInitDirectives = this.factory.newInitDirectives(param_name);
                this.currentMacroToBuild
                        .addInitDirectives(mInitDirectives);

                List<Macro> directives = evalMacros(node.getDirectives());
                for (Macro directive : directives) {

                    if(directive instanceof MNewDirective){
                        mInitDirectives.addNewDirectives((MNewDirective) directive);
                    }
                    else {
                        throw new InternalException("case unhandled");
                    }
                }

                this.indexBuilder = 0;

                MInitInternalsCall mInitInternalsCall = this.factory.newInitInternalsCall(
                        param_name);

                this.currentMacroBuilder.addInternalsCalls(mInitInternalsCall);
                this.currentMacroBuilder
                        .addDirectivesCalls(this.factory.newInitDirectiveCall(param_name));

                this.currentContextName = param_name
                        .concat(GenerationUtils.CONTEXT_STRING);

                if (!this.contextNames.contains(this.currentContextName)) {
                    this.contextNames.add(this.currentContextName);
                }

                this.currentApplyInitializer = this.factory.newApplyInternalsInitializer();

                MInitInternalsMethod mInitInternalsMethod = this.factory.newInitInternalsMethod(
                        param_name);
                mInitInternalsMethod
                        .addApplyInternalsInitializer(
                                this.currentApplyInitializer);
                this.currentMacroToBuild
                        .addInitInternalsMethods(mInitInternalsMethod);


                if (this.currentMacroHasInternals) {
                    mInitInternalsCall.addContextArg(this.factory.newContextArg());
                }
            }
        }
        else {
            throw new InternalException("case unhandled");
        }

        node.getType().apply(this);
        outAParam(node);
    }

    @Override
    public void outAParam(
            AParam node) {

        this.currentParamName = null;
        this.currentContextName = null;
        this.currentApplyInitializer = null;
        this.indexBuilder = 0;
        this.indexInsert = 0;
        this.createdBuilders = new LinkedHashSet<>();
        this.createdInserts = new ArrayList<>();
        this.previouslyUsed = new LinkedList<>();
        this.currentParamMacroRefBuilder = null;
    }

    @Override
    public void caseADirective(
            ADirective node) {

        String directive_name = GenerationUtils
                .buildNameCamelCase(node.getNames());

        this.previouslyUsed.add(this.indexBuilder);
        do{
            this.indexBuilder++;
        }
        while(this.createdBuilders.contains(this.indexBuilder));

        this.createdBuilders.add(this.indexBuilder);
        MNewDirective mNewDirective = this.factory.newNewDirective(directive_name,
                this.indexBuilder.toString());

        this.tempMacros.add(mNewDirective);
        List<Macro> temp = this.tempMacros;

        List<Macro> text_parts = evalMacros(node.getParts());

        for(Macro text_part : text_parts){

            if (text_part instanceof MEolPart) {
                mNewDirective.addTextParts((MEolPart) text_part);
            }
            else if (text_part instanceof MStringPart) {
                mNewDirective.addTextParts((MStringPart) text_part);
            }
            else if (text_part instanceof MParamInsertPart) {
                mNewDirective.addTextParts((MParamInsertPart) text_part);
            }
            else if (text_part instanceof MInsertMacroPart) {
                mNewDirective.addTextParts((MInsertMacroPart) text_part);
            }
            else {
                throw new InternalException("case unhandled");
            }
        }

        this.tempMacros = temp;
    }

    @Override
    public void caseAMacroRef(
            AMacroRef node) {

        String macro_ref_name = this.currentMacroRefName = GenerationUtils
                .buildNameCamelCase(node.getNames());
        SMacro referenced_macro = this.macros.get(macro_ref_name);

        if(this.currentContextName == null){
            super.caseAMacroRef(node);
        }
        else{
            Set<String> referenced_macros = new HashSet<>();

            if(this.currentMacroIsAllVersionned){
                if(referenced_macro.getChildren().size() == 0){
                    referenced_macros.add(macro_ref_name);
                }
                else {
                    referenced_macros.addAll(referenced_macro.getChildren());
                }
            }
            else if(!this.currentMacroIsAbstract) {
                for(String version : this.currentMacro.getApplied_versions()){
                    String macro_name = referenced_macro.getChildByVersion(version);

                    if(macro_name != null){
                        referenced_macros.add(macro_name);
                    }
                }
            }


            for(String child : referenced_macros){
                this.currentMacroRefName = child;
                MRedefinedInternalsSetter mRedefinedInternalsSetter = this.factory.newRedefinedInternalsSetter(
                        child);
                this.currentApplyInitializer.addRedefinedInternalsSetter(
                        mRedefinedInternalsSetter);

                if(node.getArgs() != null){
                    for(PValue value : node.getArgs()){
                        List<Macro> macros_found = evalMacros(value);

                        for(Macro macro : macros_found){
                            if(macro instanceof MEolPart){
                                mRedefinedInternalsSetter.addTextParts((MEolPart) macro);
                            }
                            else if(macro instanceof MInsertMacroPart){
                                mRedefinedInternalsSetter.addTextParts((MInsertMacroPart) macro);
                            }
                            else if(macro instanceof MInitStringBuilder){
                                mRedefinedInternalsSetter.addTextParts((MInitStringBuilder) macro);
                            }
                            else if(macro instanceof MStringPart){
                                mRedefinedInternalsSetter.addTextParts((MStringPart) macro);
                            }
                            else if(macro instanceof MParamInsertPart){
                                mRedefinedInternalsSetter.addTextParts((MParamInsertPart) macro);
                            }
                            else if(macro instanceof MSetInternal) {
                                mRedefinedInternalsSetter.addSetInternals((MSetInternal) macro);
                            }
                            else{
                                throw new InternalException("case unhandled");
                            }
                        }
                    }
                }
            }

            if(this.currentMacroIsAbstract
                    || this.currentMacroIsAllVersionned){

                MSingleAdd mSingleAdd = this.factory.newSingleAdd(macro_ref_name,
                        this.currentParamName);
                this.currentMacroToBuild.addSetters(mSingleAdd);
                if (!this.currentMacroHasInternals) {
                    mSingleAdd.addIsBuilt(this.factory.newIsBuilt());
                }
            }

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

        String context_name = String.valueOf(this.currentContextName);


        do{
            this.indexBuilder++;
        }
        while(this.createdBuilders.contains(this.indexBuilder));

        this.previouslyUsed.add(this.indexBuilder);
        this.createdBuilders.add(this.indexBuilder);
        this.tempMacros.add(this.factory.newInitStringBuilder(this.indexBuilder.toString()));

        for (PTextPart part : node.getParts()) {
            part.apply(this);
        }

        this.indexBuilder = this.previouslyUsed
                .remove(this.previouslyUsed.size() - 1);

        MSetInternal mSetInternal = this.factory.newSetInternal(
                GenerationUtils.buildNameCamelCase(node.getParamName()),
                context_name);
        this.tempMacros.add(mSetInternal);
        mSetInternal.addSetParams(this.factory.newStringBuilderBuild(this.indexBuilder.toString()));
    }

    @Override
    public void caseAStringTextPart(
            AStringTextPart node) {

        this.tempMacros.add(this.factory.newStringPart(
                GenerationUtils.escapedString(node.getString()),
                String.valueOf(this.indexBuilder)));

    }

    @Override
    public void caseAVarTextPart(
            AVarTextPart node) {

        String index_builder = String.valueOf(this.indexBuilder);
        String param_name = GenerationUtils.buildNameCamelCase(node.getNames());

        this.tempMacros.add(this.factory.newParamInsertPart(param_name, index_builder));

    }

    @Override
    public void caseAEolTextPart(
            AEolTextPart node) {

        String index_builder = String.valueOf(this.indexBuilder);

        this.tempMacros.add(this.factory.newEolPart(index_builder));
    }

    @Override
    public void caseAInsertTextPart(
            AInsertTextPart node) {

        AMacroRef macroRef = (AMacroRef) node.getMacroRef();
        String macro_name = GenerationUtils
                .buildNameCamelCase(macroRef.getNames());

        String index_builder = String.valueOf(this.indexBuilder);

        // Avoid declaring insert of the same name
        while (this.createdInserts
                .contains(this.indexInsert)) {
            this.indexInsert++;
        }

        do{
            this.indexBuilder++;
        }
        while(this.createdBuilders.contains(this.indexBuilder));

        String index_insert = String.valueOf(this.indexInsert);
        this.previouslyUsed.add(this.indexBuilder);
        this.createdBuilders.add(this.indexBuilder);
        MInsertMacroPart mInsertMacroPart = this.factory.newInsertMacroPart(macro_name, index_builder, GenerationUtils.INSERT_VAR_NAME.concat(index_insert));

        this.tempMacros.add(mInsertMacroPart);
        this.createdInserts.add(this.indexInsert);

        String tempContext = this.currentContextName;
        String tempMacroName = this.currentMacroRefName;
        Integer tempIndexInsert = this.indexInsert;
        this.currentContextName = null;
        List<Macro> temp = this.tempMacros;

        List<Macro> macros_found = evalMacros(node.getMacroRef());

        for(Macro macro : macros_found) {
            if(macro instanceof MEolPart) {
                mInsertMacroPart.addMacroBodyParts((MEolPart) macro);
            }
            else if(macro instanceof MInsertMacroPart) {
                mInsertMacroPart.addMacroBodyParts((MInsertMacroPart) macro);
            }
            else if(macro instanceof MInitStringBuilder) {
                mInsertMacroPart.addMacroBodyParts((MInitStringBuilder) macro);
            }
            else if(macro instanceof MStringPart) {
                mInsertMacroPart.addMacroBodyParts((MStringPart) macro);
            }
            else if(macro instanceof MParamInsertPart) {
                mInsertMacroPart.addMacroBodyParts((MParamInsertPart) macro);
            }
            else if(macro instanceof MSetInternal) {
                mInsertMacroPart.addSetInternals((MSetInternal) macro);
            }
            else{
                throw new InternalException("case unhandled");
            }
        }

        this.tempMacros = temp;
        this.indexBuilder = this.previouslyUsed
                .remove(this.previouslyUsed.size() - 1);
        this.indexInsert = tempIndexInsert;
        this.currentContextName = tempContext;
        this.currentMacroRefName = tempMacroName;
    }

    @Override
    public void outAVarValue(
            AVarValue node) {

        String var_name = GenerationUtils.buildNameCamelCase(node.getNames());
        String context_name = String.valueOf(this.currentContextName);

        MSetInternal mSetInternal = this.factory.newSetInternal(
                GenerationUtils.buildNameCamelCase(node.getParamName()),
                context_name);
        MParamRef paramRef = this.factory.newParamRef(var_name);
        mSetInternal.addSetParams(paramRef);

        if (this.currentMacro.getInternalsName().contains(var_name)) {
            paramRef.addGetParams(this.factory.newPlainText(
                    GenerationUtils.CONTEXT_STRING.toLowerCase()));
        }

        this.tempMacros.add(mSetInternal);
    }

    @Override
    public void caseAIndentMacroPart(
            AIndentMacroPart node) {

        this.previouslyUsed.add(this.indexBuilder);

        // Avoid declaring builder of the same name
        do{
            this.indexBuilder++;
        }
        while(this.createdBuilders.contains(this.indexBuilder));

        String index_builder = String.valueOf(this.indexBuilder);
        MInitStringBuilder mInitStringBuilder = this.factory.newInitStringBuilder(index_builder);
        this.createdBuilders.add(this.indexBuilder);
        this.currentMacroBuilder.addMacroBodyParts(mInitStringBuilder);
        this.previouslyUsed.add(this.indexBuilder);

        this.indexBuilder++;
        index_builder = String.valueOf(this.indexBuilder);
        MAddIndent mAddIndent = this.factory.newAddIndent(index_builder);
        this.currentMacroBuilder.addMacroBodyParts(mAddIndent);
        this.createdBuilders.add(this.indexBuilder);

        // To avoid modification on indexes
        Integer tempIndexInsert = this.indexInsert;

        List<Macro> text_parts = evalMacros(node.getTextPart());
        for (Macro text_part : text_parts) {

            if(text_part instanceof MInitStringBuilder){
                mAddIndent.addIndentParts((MInitStringBuilder) text_part);
            }
            else if(text_part instanceof MStringPart) {
                mAddIndent.addIndentParts((MStringPart) text_part);
            }
            else if(text_part instanceof MParamInsertPart) {
                mAddIndent.addIndentParts((MParamInsertPart) text_part);
            }
            else if(text_part instanceof MEolPart) {
                mAddIndent.addIndentParts((MEolPart) text_part);
            }
            else if(text_part instanceof MInsertMacroPart) {
                mAddIndent.addIndentParts((MInsertMacroPart) text_part);
            }
            else {
                throw new InternalException("case unhandled");
            }
        }

        this.indexBuilder = this.previouslyUsed
                .remove(this.previouslyUsed.size() - 1);
        this.indexInsert = tempIndexInsert;
    }

    @Override
    public void caseAEndIndentMacroPart(
            AEndIndentMacroPart node) {

        String index_indent = String.valueOf(this.indexBuilder);
        this.indexBuilder = this.previouslyUsed
                .remove(this.previouslyUsed.size() - 1);
        this.currentMacroBuilder.addMacroBodyParts(this.factory.newIndentPart(
                String.valueOf(this.indexBuilder), index_indent));
    }

    @Override
    public void caseAStringMacroPart(
            AStringMacroPart node) {

        this.currentMacroBuilder.addMacroBodyParts(
                this.factory.newStringPart(GenerationUtils.escapedString(node.getString()),
                        String.valueOf(this.indexBuilder)));
    }

    @Override
    public void caseAEolMacroPart(
            AEolMacroPart node) {

        this.currentMacroBuilder.addMacroBodyParts(
                this.factory.newEolPart(String.valueOf(this.indexBuilder)));
    }

    @Override
    public void caseAInsertMacroPart(
            AInsertMacroPart node) {

        AMacroRef macroRef = (AMacroRef) node.getMacroRef();
        String macro_name = GenerationUtils
                .buildNameCamelCase(macroRef.getNames());
        this.indexInsert++;

        MInsertMacroPart mInsertMacroPart = this.factory.newInsertMacroPart(macro_name,
                String.valueOf(this.indexBuilder),
                String.valueOf(this.indexInsert));
        this.currentMacroBuilder.addMacroBodyParts(mInsertMacroPart);

        this.createdInserts.add(this.indexInsert);
        Integer tempIndexBuilder = this.indexBuilder;
        Integer tempIndexInsert = this.indexInsert;

        List<Macro> macros_found = evalMacros(node.getMacroRef());

        for(Macro macro : macros_found){
            if(macro instanceof MEolPart){
                mInsertMacroPart.addMacroBodyParts((MEolPart) macro);
            }
            else if(macro instanceof MInsertMacroPart){
                mInsertMacroPart.addMacroBodyParts((MInsertMacroPart) macro);
            }
            else if(macro instanceof MInitStringBuilder){
                mInsertMacroPart.addMacroBodyParts((MInitStringBuilder) macro);
            }
            else if(macro instanceof MStringPart){
                mInsertMacroPart.addMacroBodyParts((MStringPart) macro);
            }
            else if(macro instanceof MParamInsertPart){
                mInsertMacroPart.addMacroBodyParts((MParamInsertPart) macro);
            }
            else if(macro instanceof MSetInternal) {
                mInsertMacroPart.addSetInternals((MSetInternal) macro);
            }
            else{
                throw new InternalException("case unhandled");
            }
        }

        this.indexInsert = tempIndexInsert;
        this.indexBuilder = tempIndexBuilder;
    }

    @Override
    public void outAVarMacroPart(
            AVarMacroPart node) {

        String param_name = GenerationUtils.buildNameCamelCase(node.getNames());
        MParamInsertPart mParamInsertPart = this.factory.newParamInsertPart(param_name,
                String.valueOf(this.indexBuilder));
        this.currentMacroBuilder.addMacroBodyParts(mParamInsertPart);

        if (this.currentMacro.getInternalsName().contains(param_name)) {
            mParamInsertPart.addContextArg(this.factory.newContextArg());
        }
    }
}
