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

package org.sablecc.objectmacro.structures;

import java.util.HashMap;
import java.util.Map;

import org.sablecc.objectmacro.syntax3.node.AFile;
import org.sablecc.objectmacro.syntax3.node.PFile;
import org.sablecc.sablecc.exception.InternalException;

public class File
        extends Scope {

    private final static Map<AFile, File> definitionMap = new HashMap<AFile, File>();

    private final AFile definition;

    public File(
            AFile definition) {

        super(null);

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        this.definition = definition;

        definitionMap.put(definition, this);
    }

    public AFile getDefinition() {

        return this.definition;
    }

    public static File getFile(
            PFile definition) {

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        return definitionMap.get(definition);
    }
}
