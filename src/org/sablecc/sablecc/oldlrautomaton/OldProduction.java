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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.grammar.*;

public class OldProduction {

    private final OldGrammar oldGrammar;

    private final String name;

    private final LinkedList<OldAlternative> oldAlternatives = new LinkedList<OldAlternative>();

    private boolean isStable;

    private Integer shortestLength;

    private final Map<Integer, Set<Ahead>> distanceToAheadSetMap = new LinkedHashMap<Integer, Set<Ahead>>();

    private final Production origin;

    OldProduction(
            OldGrammar oldGrammar,
            String name,
            Production production) {

        this.oldGrammar = oldGrammar;
        this.name = name;
        this.origin = production;
    }

    public OldAlternative addAlternative(
            String shortName,
            Alternative alternative) {

        if (this.isStable) {
            throw new InternalException("production is stable");
        }
        OldAlternative oldAlternative = new OldAlternative(this, shortName,
                alternative);
        this.oldAlternatives.add(oldAlternative);
        return oldAlternative;
    }

    void stabilize() {

        if (this.isStable) {
            throw new InternalException("production is already stable");
        }
        this.isStable = true;

        Map<String, List<OldAlternative>> nameToAlternativeListMap = new LinkedHashMap<String, List<OldAlternative>>();
        for (OldAlternative oldAlternative : this.oldAlternatives) {
            String shortName = oldAlternative.getShortName();
            List<OldAlternative> alternativeList = nameToAlternativeListMap
                    .get(shortName);
            if (alternativeList == null) {
                alternativeList = new LinkedList<OldAlternative>();
                nameToAlternativeListMap.put(shortName, alternativeList);
            }
            alternativeList.add(oldAlternative);
        }
        for (List<OldAlternative> alternativeList : nameToAlternativeListMap
                .values()) {
            if (alternativeList.size() == 1) {
                OldAlternative oldAlternative = alternativeList.get(0);
                if (oldAlternative.getShortName().equals("")
                        && this.oldAlternatives.size() > 1) {
                    oldAlternative.setName("$1");
                }
                else {
                    oldAlternative.setName(oldAlternative.getShortName());
                }
            }
            else {
                int index = 1;
                for (OldAlternative oldAlternative : alternativeList) {
                    oldAlternative.setName(oldAlternative.getShortName() + "$"
                            + index++);
                }
            }
        }
        for (OldAlternative oldAlternative : this.oldAlternatives) {
            oldAlternative.stabilize();
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
        for (OldAlternative oldAlternative : this.oldAlternatives) {
            if (first) {
                first = false;
                sb.append(" =");
            }
            else {
                sb.append(" |");
            }
            sb.append(System.getProperty("line.separator"));
            sb.append("    ");
            sb.append(oldAlternative);
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

        for (OldAlternative oldAlternative : this.oldAlternatives) {
            modified = modified || oldAlternative.computeShortestLength();
            Integer length = oldAlternative.getShortestLength();
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

    public LinkedList<OldAlternative> getAlternatives() {

        return this.oldAlternatives;
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
            this.oldGrammar.resetLookComputationData();
            tryLook(distance);
        }
        while (this.oldGrammar.lookComputationDataHasChanged());

        this.oldGrammar.storeLookComputationResults();
    }

    Set<Ahead> tryLook(
            int distance) {

        Set<Ahead> currentLookComputationData = this.distanceToAheadSetMap
                .get(distance);

        if (currentLookComputationData != null) {
            return currentLookComputationData;
        }

        currentLookComputationData = this.oldGrammar
                .getCurrentLookComputationData(this, distance);

        if (currentLookComputationData == null) {
            this.oldGrammar.setCurrentLookComputationData(this, distance,
                    this.oldGrammar.getPreviousLookComputationData(this,
                            distance));
            currentLookComputationData = new LinkedHashSet<Ahead>();
            for (OldAlternative oldAlternative : this.oldAlternatives) {
                currentLookComputationData.addAll(oldAlternative
                        .tryLook(distance));
            }
            this.oldGrammar.setCurrentLookComputationData(this, distance,
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

    public Production getOrigin() {

        return this.origin;
    }
}
