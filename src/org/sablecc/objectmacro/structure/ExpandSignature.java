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
import org.sablecc.objectmacro.util.*;

public class ExpandSignature {

    private final Set<Macro> macroSet;

    ExpandSignature(
            Set<Macro> macroSet) {

        if (macroSet == null) {
            throw new InternalException("macroSet may not be null");
        }

        this.macroSet = Collections.unmodifiableSet(macroSet);
    }

    public Set<Macro> getMacroSet() {

        return this.macroSet;
    }

    public String toCamelCase() {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Macro macro : this.macroSet) {
            if (first) {
                first = false;
            }
            else {
                sb.append('_');
            }

            sb.append(Utils.toCamelCase(macro.getNameDeclaration()));
        }

        return sb.toString();
    }
}
