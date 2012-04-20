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

package org.sablecc.sablecc.oldlrautomaton;

import org.sablecc.exception.*;
import org.sablecc.sablecc.grammar.*;

public abstract class OldElement {

    private final OldAlternative oldAlternative;

    private final int position;

    private final String shortName;

    private String name;

    private boolean isStable;

    OldElement(
            OldAlternative oldAlternative,
            int position,
            String shortName) {

        this.oldAlternative = oldAlternative;
        this.position = position;
        this.shortName = shortName;
    }

    public String getShortName() {

        return this.shortName;
    }

    abstract public String getTypeName();

    void setName(
            String name) {

        if (this.isStable) {
            throw new InternalException("element is stable");
        }
        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    void stabilize() {

        if (this.isStable) {
            throw new InternalException("element is already stable");
        }

        this.isStable = true;
    }

    public String getFullName() {

        return this.oldAlternative.getFullName() + "." + getName();
    }

    public int getPosition() {

        return this.position;
    }

    @Override
    public abstract String toString();

    abstract public Element getOrigin();
}
