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

package org.sablecc.sablecc.grammar;

import java.util.*;

import org.sablecc.exception.*;

public class Production {

    private final Grammar grammar;

    private final String name;

    private final LinkedList<Alternative> alternatives = new LinkedList<Alternative>();

    private boolean isStable;

    Production(
            Grammar grammar,
            String name) {

        this.grammar = grammar;
        this.name = name;
    }

    public Alternative addAlternative(
            String shortName) {

        if (this.isStable) {
            throw new InternalException("production is stable");
        }
        Alternative alternative = new Alternative(this, shortName);
        this.alternatives.add(alternative);
        return alternative;
    }

    void stabilize() {

        if (this.isStable) {
            throw new InternalException("production is already stable");
        }
        this.isStable = true;

        Map<String, List<Alternative>> nameToAlternativeListMap = new LinkedHashMap<String, List<Alternative>>();
        for (Alternative alternative : this.alternatives) {
            String shortName = alternative.getShortName();
            List<Alternative> alternativeList = nameToAlternativeListMap
                    .get(shortName);
            if (alternativeList == null) {
                alternativeList = new LinkedList<Alternative>();
                nameToAlternativeListMap.put(shortName, alternativeList);
            }
            alternativeList.add(alternative);
        }
        for (List<Alternative> alternativeList : nameToAlternativeListMap
                .values()) {
            if (alternativeList.size() == 1) {
                Alternative alternative = alternativeList.get(0);
                if (alternative.getShortName().equals("")
                        && this.alternatives.size() > 1) {
                    alternative.setName("$1");
                }
                else {
                    alternative.setName(alternative.getShortName());
                }
            }
            else {
                int index = 1;
                for (Alternative alternative : alternativeList) {
                    alternative.setName(alternative.getShortName() + "$"
                            + index++);
                }
            }
        }
        for (Alternative alternative : this.alternatives) {
            alternative.stabilize();
        }
    }

    public String getName() {

        return this.name;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("  ");
        sb.append(getName());
        for (Alternative alternative : this.alternatives) {
            if (first) {
                first = false;
                sb.append(" =");
            }
            else {
                sb.append(" |");
            }
            sb.append(System.getProperty("line.separator"));
            sb.append("    ");
            sb.append(alternative);
        }
        sb.append(";");
        return sb.toString();
    }
}
