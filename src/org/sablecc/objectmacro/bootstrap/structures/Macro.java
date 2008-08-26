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

package org.sablecc.objectmacro.bootstrap.structures;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.sablecc.objectmacro.bootstrap.syntax3.node.AMacro;
import org.sablecc.objectmacro.bootstrap.syntax3.node.PMacro;
import org.sablecc.sablecc.exception.InternalException;

public class Macro
        extends Scope {

    private final static Map<AMacro, Macro> definitionMap = new HashMap<AMacro, Macro>();

    private final Set<Macro> subMacros = new LinkedHashSet<Macro>();

    private final AMacro definition;

    private boolean topLevel;

    private final Set<Expand> referringExpands = new LinkedHashSet<Expand>();

    public Macro(
            AMacro definition,
            Scope parentScope) {

        super(parentScope);

        if (parentScope == null) {
            throw new InternalException("parentScope may not be null");
        }

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        this.definition = definition;

        definitionMap.put(definition, this);
    }

    public AMacro getDefinition() {

        return this.definition;
    }

    public boolean isImplicitlyExpanded() {

        return this.referringExpands.size() == 0;
    }

    public void addReferringExpand(
            Expand expand) {

        if (expand == null) {
            throw new InternalException("expand may not be null");
        }

        this.referringExpands.add(expand);
    }

    public Iterator<Expand> getReferringExpandsIterator() {

        return this.referringExpands.iterator();
    }

    public String getName() {

        return this.definition.getName().getText();
    }

    public static Macro getMacro(
            PMacro definition) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        return definitionMap.get(definition);
    }

    public void setTopLevel() {

        this.topLevel = true;
    }

    public boolean isTopLevel() {

        return this.topLevel;
    }

    public void addSubMacro(
            Macro macro) {

        if (macro == null) {
            throw new InternalException("macro may not be null");
        }

        this.subMacros.add(macro);
    }

    public Iterator<Macro> getSubMacrosIterator() {

        return this.subMacros.iterator();
    }
}
