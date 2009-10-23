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

public class LexerPriorityCollector
        extends TreeWalker {

    private final GlobalIndex globalIndex;

    private Context currentContext;

    private MatchedToken matchedToken;

    private MatchedToken getMatchedToken(
            PUnit node) {

        visit(node);
        MatchedToken matchedToken = this.matchedToken;
        this.matchedToken = null;
        return matchedToken;
    }

    public LexerPriorityCollector(
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
        for (PLexerPriority lexerPriority : node.getLexerPriorities()) {
            visit(lexerPriority);
        }
        this.currentContext = null;
    }

    @Override
    public void caseALexerPriority(
            ALexerPriority node) {

        MatchedToken high = getMatchedToken(node.getHigh());
        MatchedToken low = getMatchedToken(node.getLow());

        this.currentContext.addPriority(node, high, low);
    }

    @Override
    public void outANameUnit(
            ANameUnit node) {

        this.matchedToken = this.currentContext.getMatchedToken(node);
    }

    @Override
    public void outAStringUnit(
            AStringUnit node) {

        this.matchedToken = this.currentContext.getMatchedToken(node);
    }

    @Override
    public void outAEpsilonUnit(
            AEpsilonUnit node) {

        this.matchedToken = this.currentContext.getMatchedToken(node);
    }

    @Override
    public void outAAnyUnit(
            AAnyUnit node) {

        this.matchedToken = this.currentContext.getMatchedToken(node);
    }

    @Override
    public void outACharCharacter(
            ACharCharacter node) {

        this.matchedToken = this.currentContext.getMatchedToken(node);
    }

    @Override
    public void outADecCharacter(
            ADecCharacter node) {

        this.matchedToken = this.currentContext.getMatchedToken(node);
    }

    @Override
    public void outAHexCharacter(
            AHexCharacter node) {

        this.matchedToken = this.currentContext.getMatchedToken(node);
    }
}
