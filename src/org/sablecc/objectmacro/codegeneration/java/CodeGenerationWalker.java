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
     * Macro representing the apply internal initializer inside the method init
     * internals
     */
    private MApplyInternalsInitializer currentAddAllTypeVerifier;

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

    /**
     * Macro representing the factory Macros
     */
    private MMacroFactory macroFactory;

    /**
     * Macro representing the macro creating method
     */
    private MMacroCreatorMethod currentMacroCreatorMethod;

    /**
     * Macro representing the switch statement in a macro creating method contained in the factory
     */
    private MSwitchVersion currentSwitchVersion;

    /**
     * Previously used string builder index
     */
    private List<Integer> previouslyUsed = new LinkedList<>();

    /**
     * Set of method names created through the walker
     */
    private Set<String> createdFactoryMethods = new HashSet<>();

    /**
     * Boolean that designates if the current macro is all versionned
     */
    private boolean currentMacroIsAllVersionned = false;

    /**
     * Boolean that designates if the current macro is the abstract macro
     */
    private boolean currentMacroIsAbstract = false;

    /**
     * Temporary macros collected during the walk
     */
    private List<Macro> tempMacros = null;

    /**
     * Factory used to create macro
     */
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
            this.currentPackageDeclaration = this.factory.newPackageDeclaration();
            this.currentPackageDeclaration.addPackageName(destinationPackage);
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
                MVersion mVersion = this.factory.newVersion();
                mVersion.addDefaultCase(version_name);
                this.macroFactory.addDefaultVersion(mVersion);
                first = false;
            }

            mVersionEnumeration.addVersions(version_name);
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
        this.currentMacroHasInternals = node.getInternals().size() > 0;

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
            this.currentMacroCreatorMethod = this.factory.newMacroCreatorMethod();
            this.currentMacroCreatorMethod.addClassName(method_class_name);
            this.macroFactory.addNewMacroMethods(this.currentMacroCreatorMethod);

            if(!this.currentMacroIsAllVersionned){
                this.currentSwitchVersion = this.factory.newSwitchVersion();
                this.currentMacroCreatorMethod.addVersionFactory(this.currentSwitchVersion);
            }
        }

        if(!this.currentMacroIsAllVersionned){
            for(TString version : node.getVersions()){
                String version_name = GenerationUtils.string(version).toUpperCase();
                MMacroCaseInit mMacroCaseInit = this.factory.newMacroCaseInit();
                mMacroCaseInit.addVersion(version_name);
                mMacroCaseInit.addVersionClassName(macro_name);
                this.currentSwitchVersion.addVersionCases(mMacroCaseInit);
            }
        }

        this.currentConstructor = this.factory.newConstructor();
        this.currentMacroToBuild.addConstructor(this.currentConstructor);

        if(this.currentMacroIsAbstract){
            this.currentMacroToBuild.addAbstract(this.factory.newAbstract());

            for(String child : this.currentMacro.getChildren()){
                SMacro macro = this.macros.get(child);
                if(macro.getInternalsName().size() > 0){
                    this.currentMacro.setChildrenHasInternals(true);
                    break;
                }
            }

            if(!this.currentMacro.isChildrenHasInternals()){
                this.currentMacroToBuild.addMacroBuilders(this.factory.newAbstractBuilder());
            }
        }
        else {
            this.currentMacroBuilder = this.factory.newMacroBuilder();
            this.currentMacroToBuild.addMacroBuilders(this.currentMacroBuilder);
            this.currentMacroToBuild.addInitMacrosMethod(this.factory.newSetMacrosMethod());
            this.currentConstructor.addFieldInitializers(this.factory.newSetMacrosCall());

            MParentInternalsSetter mParentInternalsSetter = this.factory.newParentInternalsSetter();
            mParentInternalsSetter.addName(macro_name);
            this.mInternalsInitializer
                    .addParentInternalSetters(mParentInternalsSetter);
            this.currentMacroToBuild.addRedefinedApplyInitializer(
                    this.factory.newRedefinedApplyInitializer());
        }

        if(!this.currentMacroIsAbstract
                && !this.currentMacroIsAllVersionned){

            this.currentConstructor.addSuper(this.factory.newSuperCall());
            MAppliedVersion appliedVersion = this.factory.newAppliedVersion();

            for(TString version : node.getVersions()){
                String version_name = GenerationUtils.string(version).toUpperCase();
                appliedVersion.addVersions(version_name);
            }

            this.currentMacroToBuild.addAppliedVersion(appliedVersion);
        }
        else {
            this.currentMacroToBuild.addPublic(this.factory.newPublic());
        }

        if (this.currentMacroHasInternals) {
            // method build is package protected so a context parameter is needed to build
            // the current macro
            this.currentMacroBuilder.addContextParam(this.factory.newContextParam());
            this.currentMacroBuilder
                    .addContextCacheBuilder(this.factory.newContextCacheBuilder());
            this.currentMacroBuilder.addNewCacheBuilder(this.factory.newNewCacheBuilder());
        }
        else {
            if(!this.currentMacroIsAbstract){
                this.currentMacroToBuild
                        .addMacroBuilders(this.factory.newEmptyBuilderWithContext());

                if((this.currentMacro.getParent() != null
                        && !this.currentMacro.getParent().isChildrenHasInternals())
                        || this.currentMacroIsAllVersionned){

                    this.currentMacroBuilder.addPublic(this.factory.newPublic());
                    if(!this.currentMacroIsAllVersionned){
                        this.currentMacroBuilder.addOverride(this.factory.newOverride());
                    }
                }
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

        String param_name = GenerationUtils.buildNameCamelCase(node.getNames());

        if (node.getType() instanceof AStringType) {
            MInternalStringField mInternalStringField = this.factory.newInternalStringField();
            MInternalStringSetter mInternalStringSetter = this.factory.newInternalStringSetter();
            MInternalStringRefBuilder mInternalStringRefBuilder = this.factory.newInternalStringRefBuilder();
            MInternalStringRef mInternalStringRef = this.factory.newInternalStringRef();

            this.currentMacroToBuild.addFields(mInternalStringField);
            this.currentMacroToBuild.addSetters(mInternalStringSetter);
            this.currentMacroToBuild.addBuilders(mInternalStringRefBuilder);
            this.currentMacroToBuild.addGetters(mInternalStringRef);

            mInternalStringField.addName(param_name);
            mInternalStringSetter.addName(param_name);
            mInternalStringRefBuilder.addInternalName(param_name);
            mInternalStringRef.addName(param_name);
        }
        else if (node.getType() instanceof AMacroRefsType) {

            MInternalMacroField mInternalMacroField = this.factory.newInternalMacroField();
            MInternalMacroSetter mInternalMacroSetter = this.factory.newInternalMacroSetter();
            MInternalMacroRefBuilder mInternalMacroRefBuilder = this.factory.newInternalMacroRefBuilder();
            MInternalMacroRef mInternalMacroRef = this.factory.newInternalMacroRef();

            this.currentMacroToBuild.addFields(mInternalMacroField);
            this.currentMacroToBuild.addSetters(mInternalMacroSetter);
            this.currentMacroToBuild.addBuilders(mInternalMacroRefBuilder);
            this.currentMacroToBuild.addGetters(mInternalMacroRef);

            mInternalMacroField.addName(param_name);
            mInternalMacroSetter.addParamName(param_name);
            mInternalMacroRefBuilder.addInternalName(param_name);
            mInternalMacroRef.addParamName(param_name);
        }
        else {
            throw new InternalException("case unhandled");
        }

        if(!this.currentMacroIsAbstract){
            MInitInternal mInitInternal = this.factory.newInitInternal();
            mInitInternal.addName(param_name);
            this.currentConstructor.addFieldInitializers(mInitInternal);
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

        if(!this.currentMacroIsAbstract){
            MDirectiveFields mDirectiveFields = this.factory.newDirectiveFields();
            MInitDirectives mInitDirectives = this.factory.newInitDirectives();
            MInitDirectiveCall mInitDirectiveCall = this.factory.newInitDirectiveCall();

            mDirectiveFields.addParamName(param_name);
            mInitDirectives.addParamName(param_name);
            mInitDirectiveCall.addParamName(param_name);

            mInitDirectives.addAllNewDirectives(evalMacros(node.getDirectives()));

            this.currentMacroToBuild
                    .addInitDirectives(mInitDirectives);
            this.currentMacroToBuild.addFields(mDirectiveFields);
            this.currentMacroBuilder.addDirectivesCalls(mInitDirectiveCall);
        }

        if(this.currentMacroIsAllVersionned
                || this.currentMacroIsAbstract){

            MInitParam mInitParam = this.factory.newInitParam();
            mInitParam.addName(param_name);
            this.currentConstructor
                    .addFieldInitializers(mInitParam);
        }

        if (node.getType() instanceof AStringType) {

            if(this.currentMacroIsAbstract
                    || this.currentMacroIsAllVersionned) {

                MParamStringField mParamStringField = this.factory.newParamStringField();
                mParamStringField.addParamName(param_name);
                this.currentMacroToBuild
                        .addFields(mParamStringField);

                MAddAllString mAddAllString = this.factory.newAddAllString();
                MSingleStringAdd mSingleStringAdd = this.factory.newSingleStringAdd();
                MParamStringRef mParamStringRef = this.factory.newParamStringRef();
                MInitStringValue mInitStringValue = this.factory.newInitStringValue();

                this.currentMacroToBuild.addSetters(mAddAllString);
                this.currentMacroToBuild.addSetters(mSingleStringAdd);
                this.currentMacroToBuild.addGetters(mParamStringRef);
                this.currentConstructor.addValuesInitializers(mInitStringValue);

                mAddAllString.addParamName(param_name);
                mSingleStringAdd.addParamName(param_name);
                mParamStringRef.addName(param_name);
                mInitStringValue.addParamName(param_name);
            }

            if(!this.currentMacroIsAbstract){
                MParamStringRefBuilder mParamStringRefBuilder = this.factory.newParamStringRefBuilder();
                mParamStringRefBuilder.addName(param_name);
                this.currentMacroToBuild.addBuilders(mParamStringRefBuilder);
            }

        }
        else if (node.getType() instanceof AMacroRefsType) {

            this.currentParamMacroRefBuilder = this.factory.newParamMacroRefBuilder();
            this.currentParamMacroRefBuilder.addName(param_name);

            if(this.currentMacroIsAbstract
                    || this.currentMacroIsAllVersionned){

                MParamMacroField mParamMacroField = this.factory.newParamMacroField();
                MParamMacroRef mParamMacroRef = this.factory.newParamMacroRef();
                MInitMacroValue mInitMacroValue = this.factory.newInitMacroValue();
                MAddAllMacro mAddAllMmParamMacroFieldacro = this.factory.newAddAllMacro();

                mParamMacroField.addParamName(param_name);
                mParamMacroRef.addName(param_name);
                mInitMacroValue.addParamName(param_name);
                mAddAllMmParamMacroFieldacro.addParamName(param_name);

                this.currentMacroToBuild.addFields(mParamMacroField);

                this.currentMacroToBuild.addGetters(mParamMacroRef);

                this.currentConstructor.addValuesInitializers(mInitMacroValue);

                this.currentContextName = param_name
                        .concat(GenerationUtils.CONTEXT_STRING);

                this.currentMacroToBuild.addSetters(mAddAllMmParamMacroFieldacro);
            }

            if(this.currentMacroIsAbstract){
                MAbstractTypeVerifier mAbstractTypeVerifier = this.factory.newAbstractTypeVerifier();
                this.currentMacroToBuild.addSetters(mAbstractTypeVerifier);
                mAbstractTypeVerifier.addParamName(param_name);
            }

            if(!this.currentMacroIsAbstract) {

                this.currentMacroToBuild
                        .addBuilders(this.currentParamMacroRefBuilder);
                this.currentParamMacroRefBuilder.addContextName(param_name.concat(GenerationUtils.CONTEXT_STRING));

                this.indexBuilder = 0;

                MInitInternalsCall mInitInternalsCall = this.factory.newInitInternalsCall();
                mInitInternalsCall.addParamName(param_name);
                this.currentMacroBuilder.addInternalsCalls(mInitInternalsCall);

                this.currentContextName = param_name
                        .concat(GenerationUtils.CONTEXT_STRING);

                if (!this.contextNames.contains(this.currentContextName)) {
                    this.contextNames.add(this.currentContextName);
                }

                this.currentApplyInitializer = this.factory.newApplyInternalsInitializer();

                MInitInternalsMethod mInitInternalsMethod = this.factory.newInitInternalsMethod();
                mInitInternalsMethod.addParamName(param_name);
                mInitInternalsMethod
                        .addApplyInternalsInitializer(
                                this.currentApplyInitializer);

                this.currentMacroToBuild
                        .addInitInternalsMethods(mInitInternalsMethod);

                this.currentAddAllTypeVerifier = this.factory.newApplyInternalsInitializer();
                MTypeVerifier mTypeVerifier = this.factory.newTypeVerifier();
                mTypeVerifier.addParamName(param_name);
                mTypeVerifier.addTypeVerification(this.currentAddAllTypeVerifier);

                if(!this.currentMacroIsAllVersionned){
                    mTypeVerifier.addOverride(this.factory.newOverride());
                }

                this.currentMacroToBuild.addSetters(mTypeVerifier);

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
        MNewDirective mNewDirective = this.factory.newNewDirective();
        mNewDirective.addDirectiveName(directive_name);
        mNewDirective.addIndexBuilder(this.indexBuilder.toString());

        this.tempMacros.add(mNewDirective);
        List<Macro> temp = this.tempMacros;

        List<Macro> text_parts = evalMacros(node.getParts());
        mNewDirective.addAllTextParts(text_parts);

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
            else {
                for(String version : this.currentMacro.getApplied_versions()){
                    String macro_name = referenced_macro.getChildByVersion(version);

                    if(macro_name != null){
                        referenced_macros.add(macro_name);
                    }
                }
            }

            for(String child : referenced_macros){
                this.currentMacroRefName = child;
                MRedefinedInternalsSetter mRedefinedInternalsSetter = this.factory.newRedefinedInternalsSetter();
                mRedefinedInternalsSetter.addMacroName(child);

                if(this.currentAddAllTypeVerifier != null){
                    MRedefinedInternalsSetter mAddAllRedefinedInternalsSetter = this.factory.newRedefinedInternalsSetter();
                    mAddAllRedefinedInternalsSetter.addMacroName(child);
                    this.currentAddAllTypeVerifier.addRedefinedInternalsSetter(mAddAllRedefinedInternalsSetter);
                }

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
                                MInsertMacroPart macro_part = (MInsertMacroPart) macro;
                                mRedefinedInternalsSetter.addTextParts(macro_part);
                                macro_part.addEnclosingClassName(this.currentMacro.getName());
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
                            else if(macro instanceof MNewStringValue) {
                                mRedefinedInternalsSetter.addSingleStringElements((MNewStringValue) macro);
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

                MSingleMacroAdd mSingleAdd = this.factory.newSingleMacroAdd();
                mSingleAdd.addParamName(this.currentParamName);
                mSingleAdd.addReferencedMacroName(macro_ref_name);
                this.currentMacroToBuild.addSetters(mSingleAdd);
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
        MInitStringBuilder mInitStringBuilder = this.factory.newInitStringBuilder();
        mInitStringBuilder.addIndexBuilder(this.indexBuilder.toString());
        this.tempMacros.add(mInitStringBuilder);

        for (PTextPart part : node.getParts()) {
            part.apply(this);
        }

        this.indexBuilder = this.previouslyUsed
                .remove(this.previouslyUsed.size() - 1);

        MNewStringValue mNewStringValue = this.factory.newNewStringValue();
        mNewStringValue.addIndexBuilder(this.indexBuilder.toString());
        this.tempMacros.add(mNewStringValue);

        MStringValueArg mStringValueArg = this.factory.newStringValueArg();
        mStringValueArg.addIndex(this.indexBuilder.toString());

        MSetInternal mSetInternal = this.factory.newSetInternal();
        mSetInternal.addContext(context_name);
        mSetInternal.addParamName(GenerationUtils.buildNameCamelCase(node.getParamName()));
        mSetInternal.addSetParams(mStringValueArg);

        this.tempMacros.add(mSetInternal);
    }

    @Override
    public void caseAStringTextPart(
            AStringTextPart node) {

        MStringPart mStringPart = this.factory.newStringPart();
        mStringPart.addString(GenerationUtils.escapedString(node.getString()));
        mStringPart.addIndexBuilder(String.valueOf(this.indexBuilder));

        this.tempMacros.add(mStringPart);
    }

    @Override
    public void caseAVarTextPart(
            AVarTextPart node) {

        String index_builder = String.valueOf(this.indexBuilder);
        String param_name = GenerationUtils.buildNameCamelCase(node.getNames());
        MParamInsertPart mParamInsertPart = this.factory.newParamInsertPart();
        mParamInsertPart.addIndexBuilder(index_builder);
        mParamInsertPart.addParamName(param_name);

        this.tempMacros.add(mParamInsertPart);
    }

    @Override
    public void caseAEolTextPart(
            AEolTextPart node) {

        String index_builder = String.valueOf(this.indexBuilder);
        MEolPart mEolPart = this.factory.newEolPart();
        mEolPart.addIndexBuilder(index_builder);

        this.tempMacros.add(mEolPart);
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

        MInsertMacroPart mInsertMacroPart = this.factory.newInsertMacroPart();
        mInsertMacroPart.addIndexBuilder(index_builder);
        mInsertMacroPart.addIndexInsert(GenerationUtils.INSERT_VAR_NAME.concat(index_insert));
        mInsertMacroPart.addReferencedMacroName(macro_name);

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
            else if(macro instanceof MNewStringValue) {
                mInsertMacroPart.addSingleElementLists((MNewStringValue) macro);
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

        MSetInternal mSetInternal = this.factory.newSetInternal();
        mSetInternal.addParamName(GenerationUtils.buildNameCamelCase(node.getParamName()));
        mSetInternal.addContext(context_name);

        MParamRef paramRef = this.factory.newParamRef();
        paramRef.addName(var_name);
        mSetInternal.addSetParams(paramRef);

        if (this.currentMacro.getInternalsName().contains(var_name)) {
            paramRef.addGetParams(GenerationUtils.CONTEXT_STRING.toLowerCase());
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
        MInitStringBuilder mInitStringBuilder = this.factory.newInitStringBuilder();
        mInitStringBuilder.addIndexBuilder(index_builder);
        this.createdBuilders.add(this.indexBuilder);
        this.currentMacroBuilder.addMacroBodyParts(mInitStringBuilder);
        this.previouslyUsed.add(this.indexBuilder);

        this.indexBuilder++;
        index_builder = String.valueOf(this.indexBuilder);
        MAddIndent mAddIndent = this.factory.newAddIndent();
        mAddIndent.addIndexBuilder(index_builder);
        this.currentMacroBuilder.addMacroBodyParts(mAddIndent);
        this.createdBuilders.add(this.indexBuilder);

        // To avoid modification on indexes
        Integer tempIndexInsert = this.indexInsert;

        List<Macro> text_parts = evalMacros(node.getTextPart());
        mAddIndent.addAllIndentParts(text_parts);

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
        MIndentPart mIndentPart = this.factory.newIndentPart();
        mIndentPart.addIndexBuilder(String.valueOf(this.indexBuilder));
        mIndentPart.addIndexIndent(index_indent);
        this.currentMacroBuilder.addMacroBodyParts(mIndentPart);
    }

    @Override
    public void caseAStringMacroPart(
            AStringMacroPart node) {

        MStringPart mStringPart = this.factory.newStringPart();
        mStringPart.addIndexBuilder(String.valueOf(this.indexBuilder));
        mStringPart.addString(GenerationUtils.escapedString(node.getString()));

        this.currentMacroBuilder.addMacroBodyParts(mStringPart);
    }

    @Override
    public void caseAEolMacroPart(
            AEolMacroPart node) {

        MEolPart mEolPart = this.factory.newEolPart();
        mEolPart.addIndexBuilder(String.valueOf(this.indexBuilder));

        this.currentMacroBuilder.addMacroBodyParts(mEolPart);
    }

    @Override
    public void caseAInsertMacroPart(
            AInsertMacroPart node) {

        AMacroRef macroRef = (AMacroRef) node.getMacroRef();
        String macro_name = GenerationUtils
                .buildNameCamelCase(macroRef.getNames());
        this.indexInsert++;

        MInsertMacroPart mInsertMacroPart = this.factory.newInsertMacroPart();
        mInsertMacroPart.addReferencedMacroName(macro_name);
        mInsertMacroPart.addIndexBuilder(String.valueOf(this.indexBuilder));
        mInsertMacroPart.addIndexInsert(String.valueOf(this.indexInsert));

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
            else if(macro instanceof MNewStringValue) {
                mInsertMacroPart.addSingleElementLists((MNewStringValue) macro);
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
        MParamInsertPart mParamInsertPart = this.factory.newParamInsertPart();
        mParamInsertPart.addParamName(param_name);
        mParamInsertPart.addIndexBuilder(String.valueOf(this.indexBuilder));

        this.currentMacroBuilder.addMacroBodyParts(mParamInsertPart);

        if (this.currentMacro.getInternalsName().contains(param_name)) {
            mParamInsertPart.addContextArg(this.factory.newContextArg());
        }
    }
}
