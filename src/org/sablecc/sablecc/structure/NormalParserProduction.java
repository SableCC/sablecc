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
import org.sablecc.sablecc.lrautomaton.Production;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.sablecc.util.*;

public class NormalParserProduction
        extends org.sablecc.sablecc.structure.Production {

    private final ANormalParserProduction declaration;

    private final Map<String, Alternative> nameToAlternativeMap = new LinkedHashMap<String, Alternative>();

    private final Map<Node, Alternative> nodeToAlternativeMap = new LinkedHashMap<Node, Alternative>();

    private Production grammarProduction;

    NormalParserProduction(
            ANormalParserProduction node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        this.declaration = node;

        Set<NamedItem> namedItems = new LinkedHashSet<NamedItem>();

        for (PParserAlternative pParserAlternative : node
                .getParserAlternatives()) {
            AParserAlternative parserAlternative = (AParserAlternative) pParserAlternative;

            Alternative alternative = new ParserAlternative(parserAlternative);
            namedItems.add(alternative);
            this.nodeToAlternativeMap.put(parserAlternative, alternative);
        }

        if (namedItems.size() == 1) {
            Alternative alternative = (Alternative) namedItems.iterator()
                    .next();

            alternative.setInternalName(alternative.getPublicName());
        }
        else {
            ItemNameGiver itemNameGiver = new ItemNameGiver(namedItems);
            for (NamedItem namedItem : namedItems) {
                Alternative alternative = (Alternative) namedItem;
                alternative.setInternalName(itemNameGiver
                        .getInternalName(namedItem));
            }
        }

        for (Alternative alternative : this.nodeToAlternativeMap.values()) {
            this.nameToAlternativeMap.put(alternative.getInternalName(),
                    alternative);
        }
    }

    @Override
    public TIdentifier getNameToken() {

        return this.declaration.getName();
    }

    public Alternative getAlternativeOrNull(
            String name) {

        return this.nameToAlternativeMap.get(name);
    }

    public void setGrammarProduction(
            Production grammarProduction) {

        this.grammarProduction = grammarProduction;
    }

    public Production getGrammarProduction() {

        return this.grammarProduction;
    }

    public ParserAlternative getAlternative(
            AParserAlternative node) {

        return (ParserAlternative) this.nodeToAlternativeMap.get(node);
    }
}
