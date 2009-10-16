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

public class Grammar {

    private final Map<String, Production> nameToProductionMap = new LinkedHashMap<String, Production>();

    private final Map<String, Token> nameToTokenMap = new LinkedHashMap<String, Token>();

    private boolean isStable;

    public Grammar(
            String firstProductionName) {

        Production firstProduction = getProduction(firstProductionName);

        Production startProduction = getProduction("$Start");
        Alternative startAlternative = startProduction.addAlternative("");
        startAlternative.addProductionElement("", firstProduction);
        startAlternative.addTokenElement("", getToken("$End"));
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
}
