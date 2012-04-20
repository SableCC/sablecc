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

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.grammar.transformation.*;
import org.sablecc.sablecc.oldlrautomaton.*;

public class SGrammar {

    private Map<String, Production> productions = new LinkedHashMap<String, Production>();

    private Map<Element, OldElement> sElementToOldElementMap = new LinkedHashMap<Element, OldElement>();

    private final Grammar grammar;

    private LRAutomaton lrAutomaton;

    private BigInteger nextProductionId = BigInteger.ZERO;

    public SGrammar(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar shouldn't be null");
        }

        this.grammar = grammar;

        constructNaturalProductions(grammar.getParser());
        grammar.apply(new GrammarSimplificator(this));

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

            Production production = new Production(getNextProductionId(),
                    coreProd.getName());
            if (this.grammar.hasATree()) {
                production.addTransformation(new SProductionTransformation(
                        coreProd.getTransformation(), production));
            }
            else {
                SProductionTransformation transformation = new SProductionTransformation(
                        production);

                transformation
                        .addElement(new SProductionTransformationElement.NormalElement(
                                transformation));

                production.addTransformation(transformation);
            }

            this.productions.put(production.getName(), production);
        }

    }

    public boolean canBeInlined(
            Alternative targetAlt) {

        Production targetProd = targetAlt.getProduction();

        for (Production production : targetAlt.getOrigins()) {
            for (Alternative alternative : production.getAlternatives()) {
                if (alternative.contains(targetProd)) {
                    return false;
                }
            }
        }

        return true;
    }

    // canBeInined must be call before inline(Alternative)
    public void inline(
            Alternative targetAlt) {

        Production targetProd = targetAlt.getProduction();

        for (Production production : this.productions.values()) {

            LinkedList<Alternative> alternatives = new LinkedList<Alternative>(
                    production.getAlternatives());
            for (Alternative alternative : alternatives) {
                if (!alternative.equals(targetAlt)) {
                    alternative.inline(targetAlt);
                }
            }
        }

        targetAlt.getProduction().removeAlternative(targetAlt);

        if (targetProd.getAlternatives().size() == 0) {
            cleanReferencesTo(targetProd);
        }
    }

    private void cleanReferencesTo(
            Production targetProd) {

        for (Production production : this.productions.values()) {

            int i = 0;
            Alternative alternative;

            while (i < production.getAlternatives().size()) {
                alternative = production.getAlternatives().get(i);

                if (alternative.contains(targetProd)) {
                    alternative.getProduction().removeAlternative(alternative);
                }
                else {
                    i++;
                }
            }
        }

        this.productions.remove(targetProd.getName());
    }

    public Collection<Production> getProductions() {

        return this.productions.values();
    }

    public void setLRAutomaton(
            LRAutomaton lrAutomaton) {

        this.lrAutomaton = lrAutomaton;
    }

    public LRAutomaton getLrAutomaton() {

        return this.lrAutomaton;
    }

    public BigInteger getNextProductionId() {

        BigInteger productionId = this.nextProductionId;
        this.nextProductionId = this.nextProductionId.add(BigInteger.ONE);
        return productionId;
    }

    public void addOldElement(
            Element sElement,
            OldElement oldEement) {

        this.sElementToOldElementMap.put(sElement, oldEement);
    }

    public OldElement getOldElement(
            Element element) {

        return this.sElementToOldElementMap.get(element);
    }

    @Override
    public String toString() {

        String parserPart = "Parser \r\n";
        String productionTransformationPart = "\r\nTransformation \r\n Production \r\n ";
        String alternativeTransformationPart = "\r\n Alternatives \r\n";

        for (Production production : this.productions.values()) {
            parserPart += production.toString() + "\r\n \r\n";
            productionTransformationPart += "  "
                    + production.getTransformation().toString() + "\r\n";
            for (Alternative alternative : production.getAlternatives()) {
                alternativeTransformationPart += "  "
                        + alternative.getTransformation().toString() + "\r\n";
            }
        }

        return parserPart + productionTransformationPart
                + alternativeTransformationPart;
    }
}
