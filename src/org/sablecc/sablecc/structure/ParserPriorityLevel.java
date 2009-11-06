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

package org.sablecc.sablecc.structure;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.lrautomaton.Alternative;

public class ParserPriorityLevel {

    private final PriorityType priorityType;

    private final Set<Alternative> alternatives = new LinkedHashSet<Alternative>();

    private final ParserPriorityLevel nextHigherPriorityLevel;

    private ParserPriorityLevel nextLowerPriorityLevel;

    public ParserPriorityLevel(
            PriorityType priorityType,
            ParserPriorityLevel higherPriorityLevel) {

        this.priorityType = priorityType;
        this.nextHigherPriorityLevel = higherPriorityLevel;
        if (higherPriorityLevel != null) {
            if (higherPriorityLevel.nextLowerPriorityLevel != null) {
                throw new InternalException(
                        "lower priority level is already set");
            }

            higherPriorityLevel.nextLowerPriorityLevel = this;
        }
    }

    public void addAlternative(
            Alternative grammarAlternative) {

        this.alternatives.add(grammarAlternative);
    }

    public PriorityType getType() {

        return this.priorityType;
    }

    public ParserPriorityLevel getNextLowerPriorityLevel() {

        return this.nextLowerPriorityLevel;
    }

}
