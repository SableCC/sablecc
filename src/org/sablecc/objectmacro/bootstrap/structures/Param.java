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

import org.sablecc.objectmacro.bootstrap.syntax3.node.AMacro;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AParam;
import org.sablecc.objectmacro.bootstrap.syntax3.node.PParam;
import org.sablecc.sablecc.exception.InternalException;

public class Param {

    private final static Map<AParam, Param> definitionMap = new HashMap<AParam, Param>();

    private final AParam definition;

    private final Macro macro;

    private final boolean first;

    public Param(
            AParam definition,
            boolean first) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        this.definition = definition;
        this.first = first;

        this.macro = Macro.getMacro((AMacro) definition.parent());

        definitionMap.put(definition, this);
    }

    public AParam getDefinition() {

        return this.definition;
    }

    public boolean isFirst() {

        return this.first;
    }

    public Macro getMacro() {

        return this.macro;
    }

    public String getName() {

        return this.definition.getName().getText();
    }

    public static Param getParam(
            PParam definition) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        return definitionMap.get(definition);
    }
}
