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
import org.sablecc.objectmacro.syntax3.node.PMacroBodyPart;

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

    private List<MAddIndent> indentations = new LinkedList<>();

    private MAddIndent currentAddIndent;

    private List<Integer> currentlyUsed = new LinkedList<>();

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
        this.currentMacroBuilder = this.currentMacroToBuild.newMacroBuilder(macroName);

        this.mInternalsInitializer.newParentInternalsSetter(macroName);
        this.currentMacroToBuild.newRedefinedApplyInitializer(macroName);

        this.currentMacroToBuild.newImportJavaUtil();
        this.currentMacroHasInternals = node.getInternals().size() > 0;

        if(this.currentMacroHasInternals){
            //method build is package protected so a context parameter to build the current macro
            this.currentMacroBuilder.newContextParam();
            this.currentMacroBuilder.newContextBuildState();
            this.currentMacroBuilder.newNewBuildState();
        }
        else{
            this.currentMacroBuilder.newPublic();
            this.currentMacroToBuild.newEmptyBuilderWithContext();
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
        this.currentMacro = null;
        this.currentMacroHasInternals = false;
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

            this.currentMacroToBuild.newInternalMacroRefBuilder(paramName);
            this.currentMacroToBuild.newInternalMacroRef(paramName);

            this.indexBuilder = 0;
            this.currentMacroToBuild.newInternalMacroSetter(paramName);
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
            this.currentMacroToBuild.newDirectiveFields(paramName);
            this.currentMacroToBuild.newInternalMacrosValueField(paramName);

            this.currentParamMacroRefBuilder = this.currentMacroToBuild.newParamMacroRefBuilder(paramName);
            this.currentParamMacroRefBuilder.newContextName(paramName.concat(
                    GenerationUtils.CONTEXT_STRING));
            this.currentMacroToBuild.newParamMacroRef(paramName);

            this.currentInitDirectives = this.currentMacroToBuild.newInitDirectives(paramName);

            for (PDirective directive : node.getDirectives()) {
                directive.apply(this);
            }

            this.currentContextName = paramName.concat(GenerationUtils.CONTEXT_STRING);
            this.indexBuilder = 0;

            MInitInternalsCall mInitInternalsCall = this.currentMacroBuilder.newInitInternalsCall(paramName);

            this.currentApplyInitializer = this.currentMacroToBuild.newInitInternalsMethod(paramName)
                                                .newApplyInternalsInitializer(paramName);

            this.contextNames.add(this.currentContextName);
            this.currentConstructor.newInitMacroParam(paramName);
            this.currentConstructor.newInitInternalValue(paramName);
            this.currentMacroBuilder.newInitDirectivesCall(paramName);

            if(this.currentMacroHasInternals){
                mInitInternalsCall.newContextArg();
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

        this.currentDirective = this.currentInitDirectives
                .newNewDirective(directive_name, this.indexBuilder.toString());
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
            this.currentRedefinedInternalsSetter =
                    this.currentApplyInitializer.newRedefinedInternalsSetter(macro_ref_name);

            MSingleAdd mSingleAdd = this.currentMacroToBuild.newSingleAdd(macro_ref_name, this.currentParamName);
            if(!this.currentMacroHasInternals){
                mSingleAdd.newIsBuilt(this.currentMacro.getName());
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
            //Letters are used in builders for inserts
            index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);

            //Avoid declaring stringbuilder of the same name
            while(this.createdBuilders.contains(index_builder)){
                this.indexBuilder++;
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);
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
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.newStringPart(
                        string,
                        index_builder);
            }
            else if(this.currentDirective != null){
                this.currentDirective.newStringPart(
                        string,
                        index_builder);
            }
            else if(this.currentAddIndent != null){
                this.currentAddIndent.newStringPart(
                        string,
                        GenerationUtils.INDENTATION);
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
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.newParamInsertPart(
                        param_name,
                        index_builder);
            }
            else if(this.currentDirective != null){
                this.currentDirective.newParamInsertPart(
                        param_name,
                        index_builder);
            }
            else if(this.currentAddIndent != null){
                this.currentAddIndent.newParamInsertPart(
                        param_name,
                        GenerationUtils.INDENTATION);
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
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);
                this.currentInsertMacroPart.newEolPart(
                        index_builder);
            }
            else if(this.currentDirective != null){
                this.currentDirective.newEolPart(index_builder);
            }
            else if(this.currentAddIndent != null){
                this.currentAddIndent.newEolPart(GenerationUtils.INDENTATION);
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
                index_builder = GenerationUtils.getLetterFromInteger(this.indexBuilder);

                this.currentInsertMacroPart =
                        tempInsertMacroPart.newInsertMacroPart(
                                macro_name,
                                index_builder,
                                index_insert);

            }
            else if(this.currentDirective != null){
                this.currentInsertMacroPart =
                    this.currentDirective.newInsertMacroPart(macro_name,
                            index_builder,
                            index_insert);

            }
            else if(this.currentAddIndent != null){
                this.currentInsertMacroPart =
                        this.currentAddIndent.newInsertMacroPart(macro_name,
                                GenerationUtils.INDENTATION,
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
                        GenerationUtils.buildNameCamelCase(node.getParamName()),
                        this.currentContextName)
                        .newParamRef(var_name);

            if(this.currentMacro.getInternalsName().contains(var_name)){
                paramRef.newContextName(GenerationUtils.CONTEXT_STRING.toLowerCase());
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
    public void caseAIndentMacroPart(
            AIndentMacroPart node) {

        this.currentlyUsed.add(this.indexBuilder);
        this.indexBuilder++;
        //Avoid declaring insert of the same name
        while(this.createdBuilders.contains(String.valueOf(this.indexBuilder))){
            this.indexBuilder++;
        }
        String index_builder = String.valueOf(this.indexBuilder);
        this.currentMacroBuilder.newInitStringBuilder(index_builder);
        this.currentAddIndent = this.currentMacroBuilder.newAddIndent();
        this.createdBuilders.add(index_builder);

        //To avoid modification on indexes
        Integer tempIndexBuilder = this.indexBuilder;
        Integer tempIndexInsert = this.indexInsert;

        for(PTextPart part : node.getTextPart()){
            part.apply(this);
        }

        this.indexBuilder = tempIndexBuilder;
        this.indexInsert = tempIndexInsert;

        this.indentations.add(this.currentAddIndent);
        this.currentAddIndent = null;
    }

    @Override
    public void caseAEndIndentMacroPart(
            AEndIndentMacroPart node) {

        String index_indent = String.valueOf(this.indexBuilder);
        this.indexBuilder = this.currentlyUsed.remove(this.currentlyUsed.size() - 1);
        this.indentations.remove(this.indentations.size() - 1);
        this.currentMacroBuilder.newIndentPart(String.valueOf(this.indexBuilder), index_indent);
    }

    @Override
    public void caseAStringMacroPart(
            AStringMacroPart node) {

        this.currentMacroBuilder.newStringPart(GenerationUtils.escapedString(node.getString()),
                String.valueOf(indexBuilder));
    }

    @Override
    public void caseAEolMacroPart(
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
