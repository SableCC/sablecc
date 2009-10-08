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

public class LexerContextVerifier
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private Context currentContext;

    private boolean isMatchedToken;

    private boolean isIgnored;

    private boolean isPriority;

    private MatchedToken foundMatchedToken;

    public LexerContextVerifier(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    @Override
    public void inALexerContext(
            ALexerContext node) {

        this.currentContext = this.globalIndex.getContext(node);
    }

    @Override
    public void outALexerContext(
            ALexerContext node) {

        this.currentContext = null;
    }

    @Override
    public void inATokens(
            ATokens node) {

        this.isMatchedToken = true;
        this.isIgnored = false;
    }

    @Override
    public void outATokens(
            ATokens node) {

        this.isMatchedToken = false;
    }

    @Override
    public void inAIgnored(
            AIgnored node) {

        this.isMatchedToken = true;
        this.isIgnored = true;
    }

    @Override
    public void outAIgnored(
            AIgnored node) {

        this.isMatchedToken = false;
    }

    @Override
    public void caseALexerPriority(
            ALexerPriority node) {

        this.isPriority = true;

        node.getHigh().apply(this);
        MatchedToken high = this.foundMatchedToken;
        this.foundMatchedToken = null;

        node.getLow().apply(this);
        MatchedToken low = this.foundMatchedToken;
        this.foundMatchedToken = null;

        this.isPriority = false;

        this.currentContext.addPriority(node, high, low);
    }

    @Override
    public void outANameUnit(
            ANameUnit node) {

        if (this.isMatchedToken) {
            this.currentContext.addMatchedToken(node, this.isIgnored);
        }
        else if (this.isPriority) {
            this.foundMatchedToken = this.currentContext.getMatchedToken(node);
        }
    }

    @Override
    public void outAStringUnit(
            AStringUnit node) {

        if (this.isMatchedToken) {
            this.currentContext.addMatchedToken(node, this.isIgnored);
        }
        else if (this.isPriority) {
            this.foundMatchedToken = this.currentContext.getMatchedToken(node);
        }

    }

    @Override
    public void outAEpsilonUnit(
            AEpsilonUnit node) {

        if (this.isMatchedToken) {
            this.currentContext.addMatchedToken(node, this.isIgnored);
        }
        else if (this.isPriority) {
            this.foundMatchedToken = this.currentContext.getMatchedToken(node);
        }

    }

    @Override
    public void outAAnyUnit(
            AAnyUnit node) {

        if (this.isMatchedToken) {
            this.currentContext.addMatchedToken(node, this.isIgnored);
        }
        else if (this.isPriority) {
            this.foundMatchedToken = this.currentContext.getMatchedToken(node);
        }

    }

    @Override
    public void outACharCharacter(
            ACharCharacter node) {

        if (this.isMatchedToken) {
            this.currentContext.addMatchedToken(node, this.isIgnored);
        }
        else if (this.isPriority) {
            this.foundMatchedToken = this.currentContext.getMatchedToken(node);
        }

    }

    @Override
    public void outADecCharacter(
            ADecCharacter node) {

        if (this.isMatchedToken) {
            this.currentContext.addMatchedToken(node, this.isIgnored);
        }
        else if (this.isPriority) {
            this.foundMatchedToken = this.currentContext.getMatchedToken(node);
        }

    }

    @Override
    public void outAHexCharacter(
            AHexCharacter node) {

        if (this.isMatchedToken) {
            this.currentContext.addMatchedToken(node, this.isIgnored);
        }
        else if (this.isPriority) {
            this.foundMatchedToken = this.currentContext.getMatchedToken(node);
        }

    }

}
