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
import java.util.Map;

import org.sablecc.objectmacro.bootstrap.exception.SemanticException;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AExpand;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AMacro;
import org.sablecc.objectmacro.bootstrap.syntax3.node.PExpand;
import org.sablecc.objectmacro.bootstrap.syntax3.node.TIdentifier;
import org.sablecc.sablecc.exception.InternalException;

public class Expand {

    private final static Map<AExpand, Expand> definitionMap = new HashMap<AExpand, Expand>();

    private static int nextIndex;

    private final AExpand definition;

    private final Macro macro;

    private final String name = "expand" + nextIndex++;

    private final Map<String, TIdentifier> macroReferenceMap = new HashMap<String, TIdentifier>();

    public Expand(
            AExpand definition) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        this.definition = definition;

        this.macro = Macro.getMacro((AMacro) definition.parent().parent());

        definitionMap.put(definition, this);
    }

    public AExpand getDefinition() {

        return this.definition;
    }

    public String getName() {

        return this.name;
    }

    public Macro getMacro() {

        return this.macro;
    }

    public TIdentifier getMacroReference(
            String macroName) {

        if (macroName == null) {
            throw new InternalException("macroName may not be null");
        }

        return this.macroReferenceMap.get(macroName);
    }

    public void addMacroReference(
            TIdentifier macroReference)
            throws SemanticException {

        if (macroReference == null) {
            throw new InternalException("macroReference may not be null");
        }

        String macroName = macroReference.getText();

        if (getMacroReference(macroName) != null) {
            throw new SemanticException("duplicate reference to macro "
                    + macroName, macroReference);
        }

        this.macroReferenceMap.put(macroName, macroReference);
    }

    public static Expand getExpand(
            PExpand definition) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        return definitionMap.get(definition);
    }
}
