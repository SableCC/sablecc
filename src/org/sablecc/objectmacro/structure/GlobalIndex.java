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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.PMacro;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class GlobalIndex {

    private boolean hasVersions = true;

    private final Set<Macro> allMacros = new LinkedHashSet<>();

    private final Map<String, MacroVersion> allVersions = new LinkedHashMap<>();

    private final SortedMap<String, Macro> macrosAllVersionned = new TreeMap<>();

    public Macro createMacro(
            AMacro pDeclaration){

        if(pDeclaration == null){
            throw new InternalException("pDeclaration may not be null");
        }

        return new Macro(this, pDeclaration);
    }

    public void addAllVersionnedMacro(
            Macro macro){

        if(macro == null){
            throw new InternalException("declaration may not be null");
        }

        TIdentifier macro_name = macro.getNameDeclaration();
        Macro first_declaration = this.macrosAllVersionned.get(macro_name.getText());

        if(first_declaration != null){
            throw CompilerException.duplicateDeclaration(macro_name, first_declaration.getNameDeclaration());
        }

        this.macrosAllVersionned.put(macro_name.getText(), macro);
        this.allMacros.add(macro);
    }

    public void addIntermediateMacro(
            Macro macro){

        if(macro == null){
            throw new InternalException("macro_name should not be null");
        }

        if(this.allMacros.contains(macro)){
            throw new InternalException("macro should not be contained in the set");
        }

        this.allMacros.add(macro);
    }

    private Macro getMacroOrNull(
            TIdentifier identifier) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        return this.macrosAllVersionned.get(identifier.getText());
    }

    public Macro getMacro(
            TIdentifier identifier,
            MacroVersion version) {

        if (identifier == null) {
            throw new InternalException("identifier may not be null");
        }

        if(version != null){
            return version.getMacro(identifier);
        }

        Macro macro = getMacroOrNull(identifier);
        if (macro == null) {
            throw CompilerException.unknownMacro(identifier);
        }

        return macro;
    }

    boolean macroExists(
            TIdentifier macro_name){

        for(MacroVersion version : this.allVersions.values()){
            if(version.getMacroOrNull(macro_name) != null){
                return true;
            }
        }

        for(String macro : this.macrosAllVersionned.keySet()){
            if(macro.equals(macro_name.getText())){
                return true;
            }
        }

        return false;
    }

    public Set<Macro> getAllMacros() {

        return this.allMacros;
    }

    public void newVersion(
            TIdentifier identifier){

        if(identifier == null){
            throw new InternalException("identifier may not be null");
        }

        String version_name = identifier.getText();
        MacroVersion first_declaration = this.allVersions.get(version_name);
        if(first_declaration != null){
            throw CompilerException.duplicateDeclaration(identifier, first_declaration.getName());
        }

        this.allVersions.put(version_name, new MacroVersion(identifier));
    }

    public Collection<MacroVersion> getAllVersions() {

        return this.allVersions.values();
    }

    public MacroVersion getVersion(
            TIdentifier version_name){

        if(version_name == null){
            throw new InternalException("name may not be null");
        }

        if(!this.allVersions.containsKey(version_name.getText())){
            //TODO exception for version
            throw CompilerException.unknownMacro(version_name);
        }

        return this.allVersions.get(version_name.getText());
    }

    public boolean hasVersions(){
        return this.hasVersions;
    }

    public void setHasVersions(
            boolean hasVersions) {

        this.hasVersions = hasVersions;
    }

    public boolean isAllVersionned(
            String macro_name){

        return this.macrosAllVersionned.containsKey(macro_name);
    }
}
