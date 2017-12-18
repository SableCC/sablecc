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
import org.sablecc.util.ComponentFinder;
import org.sablecc.util.Progeny;

public class Macro{

    private final GlobalIndex globalIndex;

    private final AMacro declaration;

    private final Set<Param> allParams = new LinkedHashSet<>();

    private final Map<String, Param> namedParams = new HashMap<>();

    private final Set<Param> allInternals = new LinkedHashSet<>();

    private final Map<String, Param> namedInternals = new HashMap<>();

    private ComponentFinder<Param> paramsComponentFinder;

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

        if(containsKeyInInternals(stringName) || containsKeyInParams(stringName)){
            throw CompilerException.duplicateDeclaration(name, getNameDeclaration());
        }

        Param newParam = new Param(param, this, this.globalIndex);
        this.namedParams.put(stringName, newParam);
        this.allParams.add(newParam);

        return newParam;
    }

    public Param newInternal(
            AParam param){

        if(param == null){
            throw new InternalException("AParam should not be null");
        }

        TIdentifier name = param.getName();
        String stringName = name.getText();

        Param duplicateDeclaration = getParamOrNull(name);
        if(duplicateDeclaration != null){
            throw CompilerException.duplicateDeclaration(name, duplicateDeclaration.getNameDeclaration());
        }

        Param newInternal = new Param(param, this, this.globalIndex);
        this.allInternals.add(newInternal);
        this.namedInternals.put(stringName, newInternal);

        return newInternal;
    }

    private Param getParamOrNull(
            TIdentifier var){

        Param toReturn = null;
        String name = var.getText();
        if(containsKeyInParams(name)){
            toReturn = this.namedParams.get(name);
        }

        if(containsKeyInInternals(name)){
            toReturn = this.namedInternals.get(name);
        }

        return toReturn;
    }

    public Param getParam(
            TIdentifier variable){

        Param param = getParamOrNull(variable);
        if(param == null){
            throw CompilerException.unknownParam(variable);
        }

        return param;
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

    public String getName(){
        return this.declaration.getName().getText();
    }

    public Set<Param> getAllParams(){
        return this.allParams;
    }

    public Set<Param> getAllInternals(){
        return this.allInternals;
    }

    private boolean containsKeyInInternals(
            String name){

        if(name == null){
            throw new InternalException("Name should not be null");
        }

        return this.namedInternals.containsKey(name);
    }

    private boolean containsKeyInParams(
            String name){

        if(name == null){
            throw new InternalException("Name should not be null");
        }

        return this.namedParams.containsKey(name);
    }

    public List<String> getInternalsName(){
        List<String> paramsName = new LinkedList<>();
        for(Param internal : this.getAllInternals()){
            paramsName.add(internal.getName());
        }

        return paramsName;
    }

    public void detectParamsCyclicReference(){
        Progeny<Param> referencedParamProgeny = new Progeny<Param>() {

            @Override
            protected Set<Param> getChildrenNoCache(
                    Param param) {

                Set<Param> children = new LinkedHashSet<>();
                children.addAll(param.getDirectParamReferences());
                return children;
            }
        };

        Set<Param> params = new LinkedHashSet<>();
        params.addAll(this.getAllInternals());
        params.addAll(this.getAllParams());
        this.paramsComponentFinder =
                new ComponentFinder<>(params, referencedParamProgeny);

        for(Param param : params){
            Param representative = this.paramsComponentFinder.getRepresentative(param);
            if(param != representative){
                throw CompilerException.cyclicReference(param.getNameDeclaration(), representative.getNameDeclaration());
            }
        }
    }

    public ComponentFinder<Param> getComponentFinder(){
        return this.paramsComponentFinder;
    }
}
