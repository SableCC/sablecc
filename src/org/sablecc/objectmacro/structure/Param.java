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

import org.sablecc.exception.*;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.syntax3.node.*;

import java.util.*;

public class Param {

    private final GlobalIndex globalIndex;

    private final AParam declaration;

    private final Macro parent;

    private final Set<AMacroReference> macroReferences = new LinkedHashSet<>();

    private final Map<String, AMacroReference> macroReferencesName = new HashMap<>();

    private final Map<String, Param> paramReferences = new LinkedHashMap<>();

    private final Map<String, Directive> directives = new HashMap<>();

    private final Set<Directive> allDirectives = new LinkedHashSet<>();

    private boolean isUsed;

    private boolean isString;

    Param(
            AParam declaration,
            Macro macro,
            GlobalIndex globalIndex) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (macro == null) {
            throw new InternalException("scope may not be null");
        }

        if(globalIndex == null){
            throw new InternalException("globalIndex may not be null");
        }

        this.declaration = declaration;
        this.parent = macro;
        this.globalIndex = globalIndex;
    }

    public Directive newDirective(
            ADirective directive) {

        String optionName = directive.getName().getText();
        if (this.directives.containsKey(optionName)) {
            throw CompilerException.duplicateOption(
                    directive, this.directives.get(optionName).getDeclaration());
        }

        Directive newDirective = new Directive(directive, this);
        this.directives.put(
                optionName, newDirective);
        this.allDirectives.add(newDirective);

        return newDirective;
    }

    public void addMacroReference(
            AMacroReference macroRef){

        if(macroRef == null){
            throw new InternalException("Macro reference cannot be null");
        }

        TIdentifier identifier = macroRef.getName();

        if(this.globalIndex.getMacro(identifier) == null){
            throw CompilerException.unknownMacro(identifier);
        }

        if(this.macroReferencesName.containsKey(identifier.getText())){
            throw CompilerException.duplicateMacroRef(macroRef.getName(), getDeclaration().getName());
        }

        this.macroReferences.add(macroRef);
        this.macroReferencesName.put(identifier.getText(), macroRef);

    }

    public void addParamReference(
            TIdentifier paramName){

        if(paramName == null){
            throw new InternalException("param cannot be null");
        }

        String name = paramName.getText();
        if(name.equals(getName())){
            throw CompilerException.selfReference(paramName, getNameDeclaration());
        }

        Param newParamRef = this.parent.getParam(paramName);
        if(newParamRef == null){
            throw new InternalException("parameter may not be null");
        }

        this.paramReferences.put(name, newParamRef);
    }

    public Set<Directive> getAllDirectives(){
        return this.allDirectives;
    }

    public Set<AMacroReference> getMacroReferences(){
        return this.macroReferences;
    }

    public TIdentifier getNameDeclaration() {
        return this.declaration.getName();
    }

    public String getName() {
        return this.declaration.getName().getText();
    }

    public AParam getDeclaration(){
        return this.declaration;
    }

    public boolean isUsed() {
        return this.isUsed;
    }

    void setUsed() {
        this.isUsed = true;
    }

    public boolean isString(){
        return this.isString;
    }

    void setString(){
        this.isString = true;
    }

    Set<Param> getDirectParamReferences(){

        Set<Param> directlyParams = new HashSet<>();
        for(Param param : this.paramReferences.values()){
            directlyParams.add(param);
        }

        return Collections.unmodifiableSet(directlyParams);
    }

    public Macro getParent(){
        return this.parent;
    }
}
