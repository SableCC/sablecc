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
import org.sablecc.sablecc.syntax3.node.*;

public class LexerDeclarationCollector
        extends TreeWalker {

    private final GlobalIndex globalIndex;

    private Context currentContext;

    private boolean isIgnored;

    public LexerDeclarationCollector(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    @Override
    public void caseAGrammar(
            AGrammar node) {

        visit(node.getLexer());
    }

    @Override
    public void caseALexer(
            ALexer node) {

        for (PLexerContext lexerContext : node.getLexerContexts()) {
            visit(lexerContext);
        }
    }

    @Override
    public void caseALexerContext(
            ALexerContext node) {

        this.currentContext = this.globalIndex.getContext(node);
        this.isIgnored = false;
        visit(node.getTokens());
        this.isIgnored = true;
        visit(node.getIgnored());
        this.currentContext = null;
    }

    @Override
    public void outANameUnit(
            ANameUnit node) {

        this.currentContext.addMatchedToken(node, this.isIgnored);
    }

    @Override
    public void outAStringUnit(
            AStringUnit node) {

        this.currentContext.addMatchedToken(node, this.isIgnored);
    }

    @Override
    public void outAEpsilonUnit(
            AEpsilonUnit node) {

        this.currentContext.addMatchedToken(node, this.isIgnored);
    }

    @Override
    public void outAAnyUnit(
            AAnyUnit node) {

        this.currentContext.addMatchedToken(node, this.isIgnored);
    }

    @Override
    public void outACharCharacter(
            ACharCharacter node) {

        this.currentContext.addMatchedToken(node, this.isIgnored);
    }

    @Override
    public void outADecCharacter(
            ADecCharacter node) {

        this.currentContext.addMatchedToken(node, this.isIgnored);
    }

    @Override
    public void outAHexCharacter(
            AHexCharacter node) {

        this.currentContext.addMatchedToken(node, this.isIgnored);
    }
}
