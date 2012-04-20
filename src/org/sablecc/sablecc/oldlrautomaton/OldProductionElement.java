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

import org.sablecc.sablecc.grammar.*;
import org.sablecc.sablecc.grammar.Element.ProductionElement;

public class OldProductionElement
        extends OldElement {

    private final OldProduction oldProduction;

    private final ProductionElement origin;

    OldProductionElement(
            OldAlternative oldAlternative,
            int position,
            String shortName,
            OldProduction oldProduction,
            ProductionElement element) {

        super(oldAlternative, position, shortName);
        this.oldProduction = oldProduction;
        this.origin = element;
    }

    public OldProduction getProduction() {

        return this.oldProduction;
    }

    @Override
    public String getTypeName() {

        return this.origin.getTypeName();
    }

    @Override
    public String toString() {

        return "[" + getName() + ":]" + this.oldProduction.getName();
    }

    @Override
    public Element.ProductionElement getOrigin() {

        return this.origin;
    }
}
