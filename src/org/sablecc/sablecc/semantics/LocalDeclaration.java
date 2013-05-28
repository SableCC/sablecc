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

package org.sablecc.sablecc.semantics;

import org.sablecc.exception.*;

public abstract class LocalDeclaration {

    private String unambiguousName;

    private String internalName;

    public abstract String getName();

    public String getUnambiguousName() {

        return this.unambiguousName;
    }

    public String getInternalName() {

        return this.internalName;
    }

    void setUnambiguousName(
            String unambiguousName) {

        if (this.unambiguousName != null) {
            throw new InternalException("unambiguousName is already set");
        }

        this.unambiguousName = unambiguousName;
    }

    void setInternalName(
            String internalName) {

        if (this.internalName != null) {
            throw new InternalException("internalName is already set");
        }

        this.internalName = internalName;
    }
}
