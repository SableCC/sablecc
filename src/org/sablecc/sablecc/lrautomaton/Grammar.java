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
import java.util.Map.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.exception.*;

public class Grammar {

    private final Map<String, Production> nameToProductionMap = new LinkedHashMap<String, Production>();

    private final Map<String, Token> nameToTokenMap = new LinkedHashMap<String, Token>();

    private boolean isStable;

    private boolean lookComputationDataHasChanged;

    private Map<Production, Map<Integer, Set<Ahead>>> previousLookComputationData;

    private Map<Production, Map<Integer, Set<Ahead>>> currentLookComputationData;

    public Grammar(
            String firstProductionName) {

        Production startProduction = getProduction("$Start");
        Production firstProduction = getProduction(firstProductionName);

        Alternative startAlternative = startProduction.addAlternative("");
        startAlternative.addProductionElement("", firstProduction);
        startAlternative.addTokenElement("", getToken("$end"));
    }

    public Production getProduction(
            String name) {

        Production production = this.nameToProductionMap.get(name);

        if (production == null) {
            if (this.isStable) {
                throw new InternalException("grammar is stable");
            }
            production = new Production(this, name);
            this.nameToProductionMap.put(name, production);
        }

        return production;
    }

    public Token getToken(
            String name) {

        Token token = this.nameToTokenMap.get(name);

        if (token == null) {
            if (this.isStable) {
                throw new InternalException("grammar is stable");
            }
            token = new Token(this, name);
            this.nameToTokenMap.put(name, token);
        }

        return token;
    }

    public void stabilize() {

        if (this.isStable) {
            throw new InternalException("grammar is already stable");
        }
        this.isStable = true;
        for (Production production : this.nameToProductionMap.values()) {
            production.stabilize();
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Grammar{");
        sb.append(System.getProperty("line.separator"));
        for (Production production : this.nameToProductionMap.values()) {
            sb.append(production);
            sb.append(System.getProperty("line.separator"));
        }
        sb.append("}");
        return sb.toString();
    }

    public void computeShortestLengthAndDetectUselessProductions() {

        boolean modified = true;
        while (modified) {
            modified = false;

            for (Production production : this.nameToProductionMap.values()) {
                modified = modified || production.computeShortestLength();
            }
        }

        for (Production production : this.nameToProductionMap.values()) {
            if (production.getShortestLength() == null) {
                throw CompilerException.parserUselessProduction(production
                        .getName());
            }
        }

    }

    void resetLookComputationData() {

        this.lookComputationDataHasChanged = false;
        if (this.currentLookComputationData != null) {
            this.previousLookComputationData = this.currentLookComputationData;
        }
        else {
            this.previousLookComputationData = new LinkedHashMap<Production, Map<Integer, Set<Ahead>>>();
        }
        this.currentLookComputationData = new LinkedHashMap<Production, Map<Integer, Set<Ahead>>>();
    }

    boolean lookComputationDataHasChanged() {

        return this.lookComputationDataHasChanged;
    }

    void storeLookComputationResults() {

        for (Entry<Production, Map<Integer, Set<Ahead>>> productionEntry : this.currentLookComputationData
                .entrySet()) {
            Production production = productionEntry.getKey();
            for (Entry<Integer, Set<Ahead>> distanceEntry : productionEntry
                    .getValue().entrySet()) {
                production.setLook(distanceEntry.getKey(), distanceEntry
                        .getValue());
            }
        }
        this.previousLookComputationData = null;
        this.currentLookComputationData = null;
        this.lookComputationDataHasChanged = false;
    }

    Set<Ahead> getCurrentLookComputationData(
            Production production,
            int distance) {

        Map<Integer, Set<Ahead>> distanceToAheadSetMap = this.currentLookComputationData
                .get(production);
        if (distanceToAheadSetMap == null) {
            return null;
        }
        return distanceToAheadSetMap.get(distance);
    }

    Set<Ahead> getPreviousLookComputationData(
            Production production,
            int distance) {

        Map<Integer, Set<Ahead>> distanceToAheadSetMap = this.previousLookComputationData
                .get(production);
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
            Production production,
            int distance,
            Set<Ahead> lookComputationData) {

        Map<Integer, Set<Ahead>> distanceToAheadSetMap = this.currentLookComputationData
                .get(production);
        if (distanceToAheadSetMap == null) {
            distanceToAheadSetMap = new LinkedHashMap<Integer, Set<Ahead>>();
            this.currentLookComputationData.put(production,
                    distanceToAheadSetMap);
        }
        distanceToAheadSetMap.put(distance, lookComputationData);

        // detect change

        Set<Ahead> previousLookComputationData = getPreviousLookComputationData(
                production, distance);
        if (!lookComputationData.equals(previousLookComputationData)) {
            this.lookComputationDataHasChanged = true;
        }
    }

    public Collection<Production> getProductions() {

        return this.nameToProductionMap.values();
    }
}
