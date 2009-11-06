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

package org.sablecc.sablecc.walker;

import java.util.*;

import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.lrautomaton.*;
import org.sablecc.sablecc.lrautomaton.Alternative;
import org.sablecc.sablecc.lrautomaton.Element;
import org.sablecc.sablecc.structure.*;
import org.sablecc.sablecc.syntax3.node.*;

public class ParserPriorityCollector
        extends TreeWalker {

    private final GlobalIndex globalIndex;

    private Context currentContext;

    private NormalParserProduction currentProduction;

    private ParserPriorityLevel currentPriorityLevel;

    public ParserPriorityCollector(
            GlobalIndex globalIndex) {

        this.globalIndex = globalIndex;
    }

    @Override
    public void caseAGrammar(
            AGrammar node) {

        visit(node.getParser());
    }

    @Override
    public void caseAParser(
            AParser node) {

        for (PParserContext parserContext : node.getParserContexts()) {
            visit(parserContext);
        }
    }

    @Override
    public void caseAParserContext(
            AParserContext node) {

        this.currentContext = this.globalIndex.getContext(node);
        for (PParserProduction parserProduction : node.getParserProductions()) {
            visit(parserProduction);
        }
        this.currentContext = null;
    }

    @Override
    public void caseANormalParserProduction(
            ANormalParserProduction node) {

        this.currentProduction = (NormalParserProduction) this.globalIndex
                .getProduction(node);
        for (PParserPriority pParserPriority : node.getParserPriorities()) {
            visit(pParserPriority);
        }
        this.currentPriorityLevel = null;
        this.currentProduction = null;
    }

    private void addToCurrentPriorityLevel(
            LinkedList<TIdentifier> identifiers) {

        for (TIdentifier identifier : identifiers) {
            ParserAlternative alternative = (ParserAlternative) this.currentProduction
                    .getAlternativeOrNull(identifier.getText());

            if (alternative == null) {
                throw CompilerException.invalidReference(identifier);
            }

            Alternative grammarAlternative = alternative
                    .getGrammarAlternative();

            int size = grammarAlternative.getElements().size();
            if (size == 0) {
                throw CompilerException.alternativeNotRecursive(identifier);
            }

            String prodName = this.currentProduction.getNameToken().getText();

            // is it left recursive?
            Element element = grammarAlternative.getElement(0);
            if (element instanceof ProductionElement
                    && ((ProductionElement) element).getProduction().getName()
                            .equals(prodName)) {
                // check that recursion is followed by token
                if (grammarAlternative.getElements().size() < 2
                        || !(grammarAlternative.getElement(1) instanceof TokenElement)) {
                    throw CompilerException
                            .recursionNotFollowedByToken(identifier);
                }
            }
            else {
                // not left recursive => check that it is right recursive
                element = grammarAlternative.getElement(size - 1);
                if (!(element instanceof ProductionElement)
                        || !((ProductionElement) element).getProduction()
                                .getName().equals(prodName)) {
                    throw CompilerException.alternativeNotRecursive(identifier);
                }
            }

            this.currentPriorityLevel.addAlternative(grammarAlternative);
            grammarAlternative.setPriorityLevel(this.currentPriorityLevel,
                    identifier);
        }
    }

    @Override
    public void caseALeftParserPriority(
            ALeftParserPriority node) {

        this.currentPriorityLevel = new ParserPriorityLevel(PriorityType.LEFT,
                this.currentPriorityLevel);

        addToCurrentPriorityLevel(node.getIdentifiers());
    }

    @Override
    public void caseARightParserPriority(
            ARightParserPriority node) {

        this.currentPriorityLevel = new ParserPriorityLevel(PriorityType.RIGHT,
                this.currentPriorityLevel);

        addToCurrentPriorityLevel(node.getIdentifiers());
    }
}
