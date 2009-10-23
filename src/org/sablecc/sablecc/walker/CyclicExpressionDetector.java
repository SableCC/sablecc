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

import org.sablecc.exception.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.structure.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public class CyclicExpressionDetector
        extends TreeWalker {

    private final GlobalIndex globalIndex;

    private NormalExpression currentNormalExpression;

    private ComponentFinder<NormalExpression> componentFinder;

    public CyclicExpressionDetector(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    @Override
    public void inAGrammar(
            AGrammar node) {

        Progeny<NormalExpression> progeny = new Progeny<NormalExpression>() {

            public Set<NormalExpression> getChildren(
                    NormalExpression node) {

                return node.getDependencies();
            }

        };

        this.componentFinder = new ComponentFinder<NormalExpression>(
                this.globalIndex.getNormalExpressions(), progeny);

        this.globalIndex
                .setNormalNamedExpressionLinearization(this.componentFinder
                        .getLinearization());
    }

    @Override
    public void inANormalNamedExpression(
            ANormalNamedExpression node) {

        this.currentNormalExpression = this.globalIndex.getExpression(node);
    }

    @Override
    public void outANormalNamedExpression(
            ANormalNamedExpression node) {

        this.currentNormalExpression = null;
    }

    @Override
    public void outANameExpression(
            ANameExpression node) {

        TIdentifier identifier = node.getIdentifier();
        NormalExpression referredExpression;

        {
            NameUnit nameUnit = this.globalIndex
                    .getParserResolution(identifier);

            if (!(nameUnit instanceof NormalExpression)) {
                throw CompilerException.invalidReference(identifier);
            }

            referredExpression = (NormalExpression) nameUnit;
        }

        if (this.componentFinder.getReach(
                this.componentFinder.getRepresentative(referredExpression))
                .contains(this.currentNormalExpression)) {
            throw CompilerException.cyclicReference(identifier,
                    this.currentNormalExpression.getNameToken());
        }
    }

}
