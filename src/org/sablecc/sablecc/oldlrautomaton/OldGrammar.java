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
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.grammar.*;

public class OldGrammar {

    private final Map<String, OldProduction> nameToProductionMap = new LinkedHashMap<String, OldProduction>();

    private final Map<String, OldToken> nameToTokenMap = new LinkedHashMap<String, OldToken>();

    private boolean isStable;

    private boolean lookComputationDataHasChanged;

    private Map<OldProduction, Map<Integer, Set<Ahead>>> previousLookComputationData;

    private Map<OldProduction, Map<Integer, Set<Ahead>>> currentLookComputationData;

    public OldGrammar(
            Grammar grammar,
            String firstProductionName,
            Production production) {

        OldProduction startProduction = getProduction("$Start", null);
        OldProduction firstProduction = getProduction(firstProductionName,
                production);

        OldAlternative startAlternative = startProduction.addAlternative("",
                null);
        startAlternative.addProductionElement("", firstProduction, null);
        startAlternative.addTokenElement(grammar, "", getToken("$end"), null);
    }

    public OldProduction getProduction(
            String name,
            Production production) {

        OldProduction oldProduction = this.nameToProductionMap.get(name);

        if (oldProduction == null) {
            if (this.isStable) {
                throw new InternalException("grammar is stable");
            }
            oldProduction = new OldProduction(this, name, production);
            this.nameToProductionMap.put(name, oldProduction);
        }

        return oldProduction;
    }

    public OldToken getToken(
            String name) {

        OldToken oldToken = this.nameToTokenMap.get(name);

        if (oldToken == null) {
            if (this.isStable) {
                throw new InternalException("grammar is stable");
            }
            oldToken = new OldToken(this, name);
            this.nameToTokenMap.put(name, oldToken);
        }

        return oldToken;
    }

    public void stabilize() {

        if (this.isStable) {
            throw new InternalException("grammar is already stable");
        }
        this.isStable = true;
        for (OldProduction oldProduction : this.nameToProductionMap.values()) {
            oldProduction.stabilize();
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Grammar{");
        sb.append(System.getProperty("line.separator"));
        for (OldProduction oldProduction : this.nameToProductionMap.values()) {
            sb.append(oldProduction);
            sb.append(System.getProperty("line.separator"));
        }
        sb.append("}");
        return sb.toString();
    }

    public void computeShortestLengthAndDetectUselessProductions() {

        boolean modified = true;
        while (modified) {
            modified = false;

            for (OldProduction production : this.nameToProductionMap.values()) {
                modified = modified || production.computeShortestLength();
            }
        }

        for (OldProduction production : this.nameToProductionMap.values()) {
            if (production.getShortestLength() == null) {
                throw SemanticException.genericError("The "
                        + production.getName() + " production is useless.");
            }
        }

    }

    void resetLookComputationData() {

        this.lookComputationDataHasChanged = false;
        if (this.currentLookComputationData != null) {
            this.previousLookComputationData = this.currentLookComputationData;
        }
        else {
            this.previousLookComputationData = new LinkedHashMap<OldProduction, Map<Integer, Set<Ahead>>>();
        }
        this.currentLookComputationData = new LinkedHashMap<OldProduction, Map<Integer, Set<Ahead>>>();
    }

    boolean lookComputationDataHasChanged() {

        return this.lookComputationDataHasChanged;
    }

    void storeLookComputationResults() {

        for (Map.Entry<OldProduction, Map<Integer, Set<Ahead>>> productionEntry : this.currentLookComputationData
                .entrySet()) {
            OldProduction oldProduction = productionEntry.getKey();
            for (Map.Entry<Integer, Set<Ahead>> distanceEntry : productionEntry
                    .getValue().entrySet()) {
                oldProduction.setLook(distanceEntry.getKey(),
                        distanceEntry.getValue());
            }
        }
        this.previousLookComputationData = null;
        this.currentLookComputationData = null;
        this.lookComputationDataHasChanged = false;
    }

    Set<Ahead> getCurrentLookComputationData(
            OldProduction oldProduction,
            int distance) {

        Map<Integer, Set<Ahead>> distanceToAheadSetMap = this.currentLookComputationData
                .get(oldProduction);
        if (distanceToAheadSetMap == null) {
            return null;
        }
        return distanceToAheadSetMap.get(distance);
    }

    Set<Ahead> getPreviousLookComputationData(
            OldProduction oldProduction,
            int distance) {

        Map<Integer, Set<Ahead>> distanceToAheadSetMap = this.previousLookComputationData
                .get(oldProduction);
        if (distanceToAheadSetMap == null) {
            return new LinkedHashSet<Ahead>();
        }
        Set<Ahead> aheadSet = distanceToAheadSetMap.get(distance);
        if (aheadSet == null) {
            return new LinkedHashSet<Ahead>();
        }
        return aheadSet;
    }

    void setCurrentLookComputationData(
            OldProduction oldProduction,
            int distance,
            Set<Ahead> lookComputationData) {

        Map<Integer, Set<Ahead>> distanceToAheadSetMap = this.currentLookComputationData
                .get(oldProduction);
        if (distanceToAheadSetMap == null) {
            distanceToAheadSetMap = new LinkedHashMap<Integer, Set<Ahead>>();
            this.currentLookComputationData.put(oldProduction,
                    distanceToAheadSetMap);
        }
        distanceToAheadSetMap.put(distance, lookComputationData);

        // detect change

        Set<Ahead> previousLookComputationData = getPreviousLookComputationData(
                oldProduction, distance);
        if (!lookComputationData.equals(previousLookComputationData)) {
            this.lookComputationDataHasChanged = true;
        }
    }

    public Collection<OldProduction> getProductions() {

        return this.nameToProductionMap.values();
    }
}
