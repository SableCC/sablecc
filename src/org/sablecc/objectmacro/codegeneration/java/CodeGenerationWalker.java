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

public class CodeGenerationWalker
        extends DepthFirstAdapter {

    private final IntermediateRepresentation ir;

    private final File packageDirectory;

    /**
     * List of macros in the file
     */
    private final Map<String, Macro> macros;

    /**
     * Object Macro with name and list of internals and parameters
     */
    private Macro currentMacro;

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
     * Macro representing the apply internal initializer inside the method init internals
     */
    private MApplyInternalsInitializer currentApplyInitializer;

    /**
     * Macro representing the redefined internals setter inside in apply initializer
     */
    private MRedefinedInternalsSetter currentRedefinedInternalsSetter;

    /**
     * Index of the current builder to avoid creating 2 StringBuilder of the same name
     */
    private Integer indexBuilder = 0;

    /**
     * Index of the current builder to avoid creating 2 macro objects for inserts of the same name
     */
    private Integer indexInsert = 0;

    /**
     * Created StringBuilders in the children of a AParameter node or AInternal node
     * This list is reset in the internal or parameter node's out
     */
    private List<String> createdBuilders = new ArrayList<>();

    /**
     * Created macro objects for inserts in the children of a AParameter node or AInternal node
     * This list is reset in the internal or parameter node's out
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
    private List<String> contextNames = new ArrayList<>();

    /**
     * Macro representing an insert in a macro body
     */
    private MInsertMacroPart currentInsertMacroPart;

    /**
     * Macro representing a parameter's builder
     */
    private MParamMacroRefBuilder currentParamMacroRefBuilder;

    /**
     * Macro representing the method to initialize directives
     */
    private MInitDirectives currentInitDirectives;

    /**
     * Macro representing the creation of an object Directive
     */
    private MNewDirective currentDirective;

    /**
     * Boolean to test whether the macro has or does not have internals
     */
    private boolean currentMacroHasInternals;

    /**
     * Macro representing the package to use in other Macro
     */
    private MPackageDeclaration currentPackageDeclaration;

    public CodeGenerationWalker(
            IntermediateRepresentation ir,
            File packageDirectory,
            Map<String, Macro> macros) {

        this.ir = ir;
        this.packageDirectory = packageDirectory;
        this.macros = macros;
    }

    @Override
    public void inAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        this.superMacro = new MSuperMacro();
        this.mInternalsInitializer = new MInternalsInitializer();

        if(!this.ir.getDestinationPackage().equals("")){
            String destinationPackage = this.ir.getDestinationPackage();
            this.currentPackageDeclaration = new MPackageDeclaration(destinationPackage);
            this.superMacro.addPackageDeclaration(this.currentPackageDeclaration);
            this.mInternalsInitializer.addPackageDeclaration(this.currentPackageDeclaration);
        }
    }

    @Override
    public void outAIntermediateRepresentation(
            AIntermediateRepresentation node) {

        GenerationUtils.writeFile(this.packageDirectory, "Macro.java", this.superMacro.build());
        GenerationUtils
                .writeFile(this.packageDirectory,"InternalsInitializer.java", this.mInternalsInitializer.build());
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
            this.currentMacroToBuild.addPackageDeclaration(this.currentPackageDeclaration);
        }

        this.currentConstructor = new MConstructor(macroName);
        this.currentMacroToBuild.addListConstructor(this.currentConstructor);
        this.currentMacroBuilder = new MMacroBuilder(macroName);
        this.currentMacroToBuild.addListMacroBuilder(this.currentMacroBuilder);

        this.mInternalsInitializer.addListParentInternalSetters(new MParentInternalsSetter(macroName));
        this.currentMacroToBuild.addListRedefinedApplyInitializer(new MRedefinedApplyInitializer(macroName));

        this.currentMacroHasInternals = node.getInternals().size() > 0;

        if(this.currentMacroHasInternals){
            //method build is package protected so a context parameter to build the current macro
            this.currentMacroBuilder.addListContextParam(new MContextParam());
            this.currentMacroBuilder.addContextBuildState(new MContextBuildState());
            this.currentMacroBuilder.addNewBuildState(new MNewBuildState());
        }
        else{
            this.currentMacroToBuild.addListEmptyBuilderWithContext(new MEmptyBuilderWithContext());
        }
    }

    @Override
    public void caseAInternal(
            AInternal node) {

        String paramName = GenerationUtils.buildNameCamelCase(node.getNames());

        if(node.getType() instanceof AStringType){
            this.currentMacroToBuild.addListField(new MInternalStringField(paramName));
            this.currentMacroToBuild.addListSetter(new MInternalStringSetter(paramName));

            MParamStringRefBuilder mParamStringRefBuilder = new MParamStringRefBuilder(paramName);
            this.currentMacroToBuild.addListBuilder(mParamStringRefBuilder);
            mParamStringRefBuilder.addListContextParam(new MContextParam());
            mParamStringRefBuilder.addListGetInternalTail(new MGetInternalTail());

            MParamStringRef mParamStringRef = new MParamStringRef(paramName);
            this.currentMacroToBuild.addListRef(mParamStringRef);
            mParamStringRef.addContextParam(new MContextParam());
            mParamStringRef.addGetInternalTail(new MGetInternalTail());
        }
        else if(node.getType() instanceof AMacroRefsType){
            this.currentMacroToBuild.addListField(new MInternalMacroField(paramName));

            this.currentMacroToBuild.addListBuilder(new MParamMacroRefBuilder(paramName));
            this.currentMacroToBuild.addListRef(new MInternalMacroRef(paramName));

            this.indexBuilder = 0;
            this.currentMacroToBuild.addListSetter(new MInternalMacroSetter(paramName));
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

        String paramName = this.currentParamName = GenerationUtils.buildNameCamelCase(node.getNames());

        if(node.getType() instanceof AStringType){
            this.currentMacroToBuild.addListField(new MParamStringField(paramName));
            this.currentMacroToBuild.addListBuilder(new MParamStringRefBuilder(paramName));
            this.currentMacroToBuild.addListRef(new MParamStringRef(paramName));

            MParamStringSetter mParamStringSetter = new MParamStringSetter(paramName);
            this.currentMacroToBuild.addListSetter(mParamStringSetter);
            mParamStringSetter.addListParamArg(new MParamArg(paramName));
            mParamStringSetter.addListStringParam(new MStringParam(paramName));

            this.currentConstructor.addListStringParam(new MStringParam(paramName));
            MSetParam mSetParam = new MSetParam(paramName);
            this.currentConstructor.addListSetParam(mSetParam);
            mSetParam.addListParamArg(new MParamArg(paramName));
        }
        else if(node.getType() instanceof AMacroRefsType){

            this.currentMacroToBuild.addListField(new MParamMacroField(paramName));
            this.currentMacroToBuild.addListContextField(new MContextField(paramName));
            this.currentMacroToBuild.addListField(new MDirectiveFields(paramName));
            this.currentMacroToBuild.addListField(new MInternalMacrosValueField(paramName));

            this.currentParamMacroRefBuilder = new MParamMacroRefBuilder(paramName);
            this.currentMacroToBuild.addListBuilder(this.currentParamMacroRefBuilder);
            this.currentParamMacroRefBuilder.addContextName(new MContextName(paramName.concat(
                    GenerationUtils.CONTEXT_STRING)));
            this.currentMacroToBuild.addListRef(new MParamMacroRef(paramName));

            this.currentInitDirectives = new MInitDirectives(paramName);
            this.currentMacroToBuild.addListInitDirectives(this.currentInitDirectives);

            for (PDirective directive : node.getDirectives()) {
                directive.apply(this);
            }

            this.currentContextName = paramName.concat(GenerationUtils.CONTEXT_STRING);
            this.indexBuilder = 0;

            MInitInternalsCall mInitInternalsCall = new MInitInternalsCall(paramName);
            this.currentMacroBuilder.addInitInternalsCall(mInitInternalsCall);


            this.currentApplyInitializer = new MApplyInternalsInitializer(paramName);
            MInitInternalsMethod mInitInternalsMethod = new MInitInternalsMethod(paramName);
            mInitInternalsMethod.addApplyInternalsInitializer(this.currentApplyInitializer);
            this.currentMacroToBuild.addListInitInternalsMethod(mInitInternalsMethod);

            this.contextNames.add(this.currentContextName);
            this.currentConstructor.addListInit(new MInitMacroParam(paramName));
            this.currentConstructor.addListInternal(new MInitInternalValue(paramName));
            this.currentMacroBuilder.addInitDirectiveCall(new MInitDirectiveCall(paramName));

            if(this.currentMacroHasInternals){
                mInitInternalsCall.addContextArg(new MContextArg());
            }
        }
        else{
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
        this.currentRedefinedInternalsSetter = null;
        this.indexBuilder = 0;
        this.indexInsert = 0;
        this.createdBuilders = new ArrayList<>();
        this.createdInserts = new ArrayList<>();
        this.currentParamMacroRefBuilder = null;
        this.currentInitDirectives = null;
    }

    @Override
    public void inADirective(
            ADirective node) {

        String directive_name = GenerationUtils.buildNameCamelCase(node.getNames());

        this.currentDirective = new MNewDirective(directive_name, this.indexBuilder.toString());
        this.currentInitDirectives.addNewDirective(this.currentDirective);
    }

    @Override
    public void outADirective(
            ADirective node) {

        this.indexBuilder++;
        this.currentDirective = null;
    }

    @Override
    public void inAMacroRef(
            AMacroRef node) {

        String macro_ref_name = this.currentMacroRefName = GenerationUtils.buildNameCamelCase(node.getNames());

        if(this.currentContextName != null){
            this.currentRedefinedInternalsSetter = new MRedefinedInternalsSetter(macro_ref_name);
            this.currentApplyInitializer.addListRedefinedInternalsSetter(this.currentRedefinedInternalsSetter);

            MSingleAdd mSingleAdd = new MSingleAdd(macro_ref_name, this.currentParamName);
            this.currentMacroToBuild.addListSetter(mSingleAdd);
            if(!this.currentMacroHasInternals){
                mSingleAdd.addIsBuilt(new MIsBuilt());
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

        this.indexBuilder++;
        String index_builder = String.valueOf(this.indexBuilder);
        boolean anyContext = this.currentContextName != null;

        if(anyContext){
            this.currentRedefinedInternalsSetter.addListPart(new MInitStringBuilder(index_builder));

            MSetInternal mSetInternal = new MSetInternal(
                    this.currentMacroRefName,
                    GenerationUtils.buildNameCamelCase(node.getParamName()),
                    this.currentContextName);
            this.currentRedefinedInternalsSetter.addListSetInternal(mSetInternal);
            mSetInternal.addListParam(new MStringBuilderBuild(index_builder));

            for(PTextPart part : node.getParts()){
                part.apply(this);
            }
        }
        else{
            index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);

            //Avoid declaring stringbuilder of the same name
            while(this.createdBuilders.contains(index_builder)){
                this.indexBuilder++;
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);
            }

            this.currentInsertMacroPart.addListPart(new MInitStringBuilder(index_builder));
            this.createdBuilders.add(index_builder);

            //To avoid modification on indexes
            Integer tempIndexBuilder = this.indexBuilder;
            Integer tempIndexInsert = this.indexInsert;

            for(PTextPart part : node.getParts()){
                part.apply(this);
            }

            this.indexBuilder = tempIndexBuilder;
            this.indexInsert = tempIndexInsert;

            MSetInternal mSetInternal = new MSetInternal(
                    GenerationUtils.INSERT_VAR_NAME.concat(String.valueOf(this.indexInsert)),
                    GenerationUtils.buildNameCamelCase(node.getParamName()),
                    "null");
            this.currentInsertMacroPart.addListSetInternal(mSetInternal);
            mSetInternal.addListParam(new MStringBuilderBuild(index_builder));
        }
    }

    @Override
    public void caseAStringTextPart(
            AStringTextPart node) {

        String index_builder = String.valueOf(this.indexBuilder);

        if(this.currentContextName != null
                && this.currentRedefinedInternalsSetter != null){

            this.currentRedefinedInternalsSetter.addListPart(new MStringPart(
                    GenerationUtils.escapedString(node.getString()),
                    String.valueOf(this.indexBuilder)));
        }
        else {
            String string = GenerationUtils.escapedString(node.getString());

            if(this.currentInsertMacroPart != null){
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.addListPart(new MStringPart(
                        string,
                        index_builder));
            }
            else if(this.currentDirective != null){
                this.currentDirective.addListParts(new MStringPart(
                        string,
                        index_builder));
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

            this.currentRedefinedInternalsSetter.addListPart(new MParamInsertPart(
                    param_name,
                    index_builder));
        }
        else {
            if(this.currentInsertMacroPart != null){
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.addListPart(new MParamInsertPart(
                        param_name,
                        index_builder));
            }
            else if(this.currentDirective != null){
                this.currentDirective.addListParts(new MParamInsertPart(
                        param_name,
                        index_builder));
            }
        }
    }

    @Override
    public void caseAEolTextPart(
            AEolTextPart node) {

        String index_builder = String.valueOf(indexBuilder);

        if(this.currentContextName != null
                && this.currentRedefinedInternalsSetter != null){

            this.currentRedefinedInternalsSetter.addListPart(new MEolPart(index_builder));
        }
        else {

            if(this.currentInsertMacroPart != null){
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.addListPart(new MEolPart(
                        index_builder));
            }
            else if(this.currentDirective != null){
                this.currentDirective.addListParts(new MEolPart(index_builder));
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

            this.currentInsertMacroPart = new MInsertMacroPart(
                    macro_name,
                    index_builder,
                    index_insert);
            this.currentRedefinedInternalsSetter.addListPart(this.currentInsertMacroPart);
        }
        else{
            if(tempInsertMacroPart != null){
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);

                this.currentInsertMacroPart = new MInsertMacroPart(macro_name,
                        index_builder,
                        index_insert);
                tempInsertMacroPart.addListPart(this.currentInsertMacroPart);

            }
            else if(this.currentDirective != null){
                this.currentInsertMacroPart = new MInsertMacroPart(macro_name,
                        index_builder,
                        index_insert);
                this.currentDirective.addListParts(this.currentInsertMacroPart);

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

            MSetInternal mSetInternal = new MSetInternal(
                    this.currentMacroRefName,
                    GenerationUtils.buildNameCamelCase(node.getParamName()),
                    this.currentContextName);
            MParamRef paramRef =new MParamRef(var_name);
            this.currentRedefinedInternalsSetter.addListSetInternal(mSetInternal);
            mSetInternal.addListParam(paramRef);

            if(this.currentMacro.getInternalsName().contains(var_name)){
                paramRef.addListContextArg(new MContextName(GenerationUtils.CONTEXT_STRING.toLowerCase()));
            }
        }
        else{
            MSetInternal mSetInternal = new MSetInternal(
                    GenerationUtils.INSERT_VAR_NAME.concat(String.valueOf(this.indexInsert)),
                    GenerationUtils.buildNameCamelCase(node.getParamName()),
                    "null");
            this.currentInsertMacroPart.addListSetInternal(mSetInternal);
            MParamRef mParamRef = new MParamRef(var_name);
            mSetInternal.addListParam(mParamRef);

            if(this.currentMacro.getInternalsName().contains(var_name)){
                mParamRef.addListContextArg(new MContextArg());
            }
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        String macroName = GenerationUtils.buildNameCamelCase(node.getNames());
        GenerationUtils.writeFile(this.packageDirectory, "M" + macroName + ".java", this.currentMacroToBuild.build());

        this.contextNames = null;
        this.currentMacroToBuild = null;
        this.currentConstructor = null;
        this.currentMacro = null;
        this.currentMacroHasInternals = false;
    }

    @Override
    public void caseAStringMacroPart(
            AStringMacroPart node) {

        this.currentMacroBuilder.addListPart(new MStringPart(
                GenerationUtils.escapedString(node.getString()),
                String.valueOf(indexBuilder)));
    }

    @Override
    public void outAEolMacroPart(
            AEolMacroPart node) {

        this.currentMacroBuilder.addListPart(new MEolPart(String.valueOf(indexBuilder)));
    }

    @Override
    public void caseAInsertMacroPart(
            AInsertMacroPart node) {

        AMacroRef macroRef = (AMacroRef) node.getMacroRef();
        String macro_name = GenerationUtils.buildNameCamelCase(macroRef.getNames());
        this.indexInsert++;

        this.currentInsertMacroPart = new MInsertMacroPart(
                macro_name, String.valueOf(indexBuilder), String.valueOf(indexInsert));
        this.currentMacroBuilder.addListPart(this.currentInsertMacroPart);

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
        MParamInsertPart mParamInsertPart = new MParamInsertPart(
                param_name,
                String.valueOf(indexBuilder));
        this.currentMacroBuilder.addListPart(mParamInsertPart);

        if(this.currentMacro.getInternalsName().contains(param_name)){
            mParamInsertPart.addContextArg(new MContextArg());
        }
    }
}
