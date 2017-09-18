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

package org.sablecc.objectmacro.structure;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.util.Utils;

public class Macro{

    private final GlobalIndex globalIndex;

    private final AMacro declaration;

    private final Set<Param> allParams = new LinkedHashSet<>();

    private final Map<String, Param> namedParams = new HashMap<>();

    private final Set<Param> allContexts = new LinkedHashSet<>();

    private final Map<String, Param> namedContexts = new HashMap<>();

    private Set<Insert> inserts = new LinkedHashSet<>();

    Macro(
            GlobalIndex globalIndex,
            AMacro declaration) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        this.globalIndex = globalIndex;
        this.declaration = declaration;
    }

    public Param newParam(
            AParam param){

        if(param == null){
            throw new InternalException("AParam should not be null");
        }

        TIdentifier name = param.getName();
        String stringName = name.getText();
        Param newParam = new Param(param, this);

        if(containsKeyInContexts(stringName) || containsKeyInParams(stringName)){
            throw CompilerException.duplicateDeclaration(name, getNameDeclaration());
        }

        this.namedParams.put(stringName, newParam);
        this.allParams.add(newParam);

        return newParam;
    }

    public Param newContext(
            AParam param){

        if(param == null){
            throw new InternalException("AParam should not be null");
        }

        TIdentifier name = param.getName();
        String stringName = name.getText();
        Param newContext = new Param(param, this);

        if(containsKeyInContexts(stringName) || containsKeyInParams(stringName)){
            throw CompilerException.duplicateDeclaration(name, getNameDeclaration());
        }
        this.allContexts.add(newContext);
        this.namedContexts.put(stringName, newContext);

        return newContext;
    }

    public Insert newInsert(
            AMacroReference macroReference){

        Macro referencedMacro = this.globalIndex.getMacro(macroReference.getName());

        if(referencedMacro == this){
            throw CompilerException.cyclicReference(
                    macroReference.getName(), getNameDeclaration());
        }

        Insert newInsert = new Insert(
                referencedMacro, this, macroReference);

        this.inserts.add(newInsert);

        return newInsert;
    }

    public int getNbStringContexts(){

        int nbString = 0;

        for(Param context : getAllContexts()){
            if(context.getDeclaration().getType() instanceof AStringType){
                nbString++;
            }
        }

        return nbString;
    }

    public Param getParam(
            TIdentifier variable){

        String name = variable.getText();
        if(containsKeyInParams(name)){
            return this.namedParams.get(name);

        }else if(containsKeyInContexts(name)){
            return this.namedContexts.get(name);
        }

        throw CompilerException.unknownParam(variable);
    }

    public void setParamUsed(
            TIdentifier variable){

        this.getParam(variable).setUsed();
    }

    public void setParamToString(
            TIdentifier variable){

        this.getParam(variable).setString();
    }


    public AMacro getDeclaration() {

        return this.declaration;
    }

    public TIdentifier getNameDeclaration() {

        return this.declaration.getName();
    }

    GlobalIndex getGlobalIndex(){
        return this.globalIndex;
    }

    public String getName(){

        return this.declaration.getName().getText();
    }

    public Set<Param> getAllParams(){

        return this.allParams;
    }

    public Set<Param> getAllContexts(){

        return this.allContexts;
    }

    public Set<Insert> getInserts() { return this.inserts; }

    public boolean containsKeyInContexts(
            String name){

        if(name == null){
            throw new InternalException("Name should not be null");
        }

        return this.namedContexts.containsKey(name);
    }

    public boolean containsKeyInParams(
            String name){

        if(name == null){
            throw new InternalException("Name should not be null");
        }

        return this.namedParams.containsKey(name);
    }

    public boolean isUsing(
            Macro macro){

        return isReferencedInParams(macro) || isReferencedInInserts(macro) || isReferencedInContexts(macro);
    }

    private boolean isReferencedInParams(
            Macro macro){

        for(Param parameter : getAllParams()){
            if(parameter.getMacroReferenceOrNull(macro.getName()) != null){
                return true;
            }
        }

        return false;
    }

    private boolean isReferencedInContexts(
            Macro macro){

        for(Param parameter : getAllContexts()){
            if(parameter.getMacroReferenceOrNull(macro.getName()) != null){
                return true;
            }
        }

        return false;
    }

    private boolean isReferencedInInserts(
            Macro macro){

        for(Insert insert : getInserts()){
            if(insert.getReferencedMacro() == macro){
                return true;
            }
        }

        return false;
    }
}
