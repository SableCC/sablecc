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

package org.sablecc.sablecc.walkers;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.structures.GlobalIndex;
import org.sablecc.sablecc.structures.LexerSelector;
import org.sablecc.sablecc.structures.ParserAlternative;
import org.sablecc.sablecc.structures.ParserContext;
import org.sablecc.sablecc.structures.ParserNormalProduction;
import org.sablecc.sablecc.structures.ParserSelector;
import org.sablecc.sablecc.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.sablecc.syntax3.node.AGrammar;
import org.sablecc.sablecc.syntax3.node.AGroup;
import org.sablecc.sablecc.syntax3.node.ALexerContext;
import org.sablecc.sablecc.syntax3.node.ALexerInvestigator;
import org.sablecc.sablecc.syntax3.node.ANormalNamedExpression;
import org.sablecc.sablecc.syntax3.node.ANormalParserProduction;
import org.sablecc.sablecc.syntax3.node.AParserAlternative;
import org.sablecc.sablecc.syntax3.node.AParserContext;
import org.sablecc.sablecc.syntax3.node.AParserInvestigator;
import org.sablecc.sablecc.syntax3.node.ASelectionNamedExpression;
import org.sablecc.sablecc.syntax3.node.ASelectionParserProduction;
import org.sablecc.sablecc.syntax3.node.ATreeProduction;
import org.sablecc.sablecc.syntax3.node.AUnitElement;
import org.sablecc.sablecc.syntax3.node.TIdentifier;

public class DeclarationCollector
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private ParserContext currentParserContext;

    private ParserNormalProduction currentNormalParserProduction;

    private ParserAlternative currentParserAlternative;

    public DeclarationCollector(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    @Override
    public void inAGrammar(
            AGrammar node) {

        this.globalIndex.setLanguage(node.getLanguageName());
    }

    @Override
    public void inANormalNamedExpression(
            ANormalNamedExpression node) {

        this.globalIndex.addExpression(node);
    }

    @Override
    public void inASelectionNamedExpression(
            ASelectionNamedExpression node) {

        LexerSelector lexerSelector = this.globalIndex.addLexerSelector(node);

        for (TIdentifier name : node.getNames()) {
            this.globalIndex.addExpression(name, lexerSelector);
        }
    }

    @Override
    public void inAGroup(
            AGroup node) {

        this.globalIndex.addGroup(node);
    }

    @Override
    public void inALexerContext(
            ALexerContext node) {

        this.globalIndex.addLexerContext(node);
    }

    @Override
    public void inALexerInvestigator(
            ALexerInvestigator node) {

        this.globalIndex.addLexerInvestigator(node);
    }

    @Override
    public void inAParserContext(
            AParserContext node) {

        this.currentParserContext = this.globalIndex.addParserContext(node);
    }

    @Override
    public void outAParserContext(
            AParserContext node) {

        this.currentParserContext = null;
    }

    @Override
    public void inANormalParserProduction(
            ANormalParserProduction node) {

        this.currentNormalParserProduction = this.currentParserContext
                .addParserProduction(node);
    }

    @Override
    public void outANormalParserProduction(
            ANormalParserProduction node) {

        this.currentNormalParserProduction = null;
    }

    @Override
    public void inASelectionParserProduction(
            ASelectionParserProduction node) {

        ParserSelector parserSelector = this.currentParserContext
                .addParserSelector(node);
        for (TIdentifier name : node.getNames()) {
            parserSelector.addParserProduction(name);
        }
    }

    @Override
    public void inAParserAlternative(
            AParserAlternative node) {

        this.currentParserAlternative = this.currentNormalParserProduction
                .addParserAlternative(node);
    }

    @Override
    public void outAParserAlternative(
            AParserAlternative node) {

        this.currentParserAlternative = null;
    }

    @Override
    public void inAUnitElement(
            AUnitElement node) {

        if (this.currentParserAlternative != null) {
            this.currentParserAlternative.addParserElement(node);
        }
    }

    @Override
    public void inAParserInvestigator(
            AParserInvestigator node) {

        this.currentNormalParserProduction.addParserInvestigator(node);
    }

    @Override
    public void inATreeProduction(
            ATreeProduction node) {

        this.globalIndex.addTreeProduction(node);
    }
}
