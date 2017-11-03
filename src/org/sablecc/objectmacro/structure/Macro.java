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
        Param newParam = new Param(param, this);

        if(containsKeyInInternals(stringName) || containsKeyInParams(stringName)){
            throw CompilerException.duplicateDeclaration(name, getNameDeclaration());
        }

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
        Param newInternal = new Param(param, this);

        if(containsKeyInInternals(stringName) || containsKeyInParams(stringName)){
            throw CompilerException.duplicateDeclaration(name, getNameDeclaration());
        }
        this.allInternals.add(newInternal);
        this.namedInternals.put(stringName, newInternal);

        return newInternal;
    }

    public Param getParam(
            TIdentifier variable){

        String name = variable.getText();
        if(containsKeyInParams(name)){
            return this.namedParams.get(name);

        }

        if(containsKeyInInternals(name)){
            return this.namedInternals.get(name);
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

    public void computeIndirectParamReferences(){

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
            Set<Param> reach = new LinkedHashSet<>();
            for(Param reachedParam : this.paramsComponentFinder.getReach(
                    this.paramsComponentFinder.getRepresentative(param))){

                reach.add(reachedParam);
            }

            param.setIndirectParamReferences(reach);
        }
    }

    public ComponentFinder<Param> getComponentFinder(){
        return this.paramsComponentFinder;
    }
}
