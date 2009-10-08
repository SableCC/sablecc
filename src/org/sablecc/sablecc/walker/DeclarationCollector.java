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

import org.sablecc.exception.*;
import org.sablecc.sablecc.structure.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class DeclarationCollector
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private Context currentContext;

    private Production currentProduction;

    private Alternative currentAlternative;

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

        this.globalIndex.setLanguage(node);
    }

    @Override
    public void inANormalNamedExpression(
            ANormalNamedExpression node) {

        this.globalIndex.addExpression(node);
    }

    @Override
    public void inASelectionNamedExpression(
            ASelectionNamedExpression node) {

        this.globalIndex.addMethod(node);
    }

    @Override
    public void inAGroup(
            AGroup node) {

        this.globalIndex.addGroup(node);
    }

    @Override
    public void inALexerContext(
            ALexerContext node) {

        this.globalIndex.addContext(node);
    }

    @Override
    public void inALexerInvestigator(
            ALexerInvestigator node) {

        this.globalIndex.addMethod(node);
    }

    @Override
    public void inAParserContext(
            AParserContext node) {

        this.globalIndex.addContext(node);
        this.currentContext = this.globalIndex.getContext(node);
    }

    @Override
    public void outAParserContext(
            AParserContext node) {

        this.currentContext = null;
    }

    @Override
    public void inANormalParserProduction(
            ANormalParserProduction node) {

        this.globalIndex.addProduction(node);
        this.currentProduction = this.globalIndex.getProduction(node);
    }

    @Override
    public void outANormalParserProduction(
            ANormalParserProduction node) {

        this.currentProduction = null;
    }

    @Override
    public void inASelectionParserProduction(
            ASelectionParserProduction node) {

        this.globalIndex.addMethod(node);
    }

    // @Override
    // public void outAParserAlternative(
    // AParserAlternative node) {
    //
    // this.currentParserAlternative = null;
    // }
    //
    // @Override
    // public void inAUnitElement(
    // AUnitElement node) {
    //
    // if (this.currentParserAlternative != null) {
    // this.currentParserAlternative.addParserElement(node);
    // }
    // }
    //
    // @Override
    // public void inAParserInvestigator(
    // AParserInvestigator node) {
    //
    // this.currentNormalParserProduction.addParserInvestigator(node);
    // }
    //
    // @Override
    // public void inATreeProduction(
    // ATreeProduction node) {
    //
    // this.globalIndex.addTreeProduction(node);
    // }
}
