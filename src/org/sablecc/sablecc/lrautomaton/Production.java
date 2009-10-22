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

package org.sablecc.sablecc.lrautomaton;

import java.util.*;

import org.sablecc.exception.*;

public class Production {

    private final Grammar grammar;

    private final String name;

    private final LinkedList<Alternative> alternatives = new LinkedList<Alternative>();

    private boolean isStable;

    private Integer shortestLength;

    private final Map<Integer, Set<Ahead>> distanceToAheadSetMap = new LinkedHashMap<Integer, Set<Ahead>>();

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

    public Integer getShortestLength() {

        return this.shortestLength;
    }

    boolean computeShortestLength() {

        Integer minLength = null;
        boolean modified = false;

        for (Alternative alternative : this.alternatives) {
            modified = modified || alternative.computeShortestLength();
            Integer length = alternative.getShortestLength();
            if (length != null) {
                if (minLength == null || length.compareTo(minLength) < 0) {
                    minLength = length;
                }
            }
        }

        if (minLength != null) {
            if (this.shortestLength == null
                    || minLength.compareTo(this.shortestLength) < 0) {
                this.shortestLength = minLength;
                return true;
            }
        }

        return modified;
    }

    public LinkedList<Alternative> getAlternatives() {

        return this.alternatives;
    }

    public Set<Ahead> look(
            int distance) {

        Set<Ahead> result = this.distanceToAheadSetMap.get(distance);

        if (result == null) {
            computeLook(distance);
            result = this.distanceToAheadSetMap.get(distance);
        }

        return result;
    }

    void computeLook(
            int distance) {

        do {
            this.grammar.resetLookComputationData();
            tryLook(distance);
        }
        while (this.grammar.lookComputationDataHasChanged());

        this.grammar.storeLookComputationResults();
    }

    Set<Ahead> tryLook(
            int distance) {

        Set<Ahead> currentLookComputationData = this.distanceToAheadSetMap
                .get(distance);

        if (currentLookComputationData != null) {
            return currentLookComputationData;
        }

        currentLookComputationData = this.grammar
                .getCurrentLookComputationData(this, distance);

        if (currentLookComputationData == null) {
            this.grammar
                    .setCurrentLookComputationData(this, distance, this.grammar
                            .getPreviousLookComputationData(this, distance));
            currentLookComputationData = new LinkedHashSet<Ahead>();
            for (Alternative alternative : this.alternatives) {
                currentLookComputationData
                        .addAll(alternative.tryLook(distance));
            }
            this.grammar.setCurrentLookComputationData(this, distance,
                    currentLookComputationData);
        }

        return currentLookComputationData;
    }

    void setLook(
            Integer distance,
            Set<Ahead> aheadSet) {

        if (this.distanceToAheadSetMap.containsKey(distance)) {
            throw new InternalException("look data is already set");
        }

        this.distanceToAheadSetMap.put(distance, aheadSet);
    }
}
