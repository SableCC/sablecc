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
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.grammar.transformation.*;

public class SGrammar {

    private Map<String, Production> productions = new HashMap<String, Production>();

    public SGrammar(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar shouldn't be null");
        }

        constructNaturalProductions(grammar.getParser());
        grammar.apply(new GrammarSimplificator());

    }

    public boolean containsProduction(
            String name) {

        return this.productions.containsKey(name);
    }

    public void addProduction(
            Production production) {

        this.productions.put(production.getName(), production);
    }

    public Production getProduction(
            String name) {

        return this.productions.get(name);
    }

    private void constructNaturalProductions(
            Parser parser) {

        for (Parser.ParserProduction coreProd : parser.getProductions()) {

            Production production = new Production(coreProd.getName());
            production.addTransformation(new SProductionTransformation(coreProd
                    .getTransformation(), production));
            this.productions.put(production.getName(), production);
        }

    }

}
