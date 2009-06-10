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

import java.util.Set;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.structures.GlobalIndex;
import org.sablecc.sablecc.structures.NormalExpression;
import org.sablecc.sablecc.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.sablecc.syntax3.node.AGrammar;
import org.sablecc.sablecc.syntax3.node.ANameExpression;
import org.sablecc.sablecc.syntax3.node.ANormalNamedExpression;
import org.sablecc.util.ComponentFinder;
import org.sablecc.util.Progeny;

public class CyclicExpressionDetector
        extends DepthFirstAdapter {

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

        NormalExpression referredExpression = this.globalIndex
                .getResolution(node);

        if (this.componentFinder.getReach(
                this.componentFinder.getRepresentative(referredExpression))
                .contains(this.currentNormalExpression)) {
            throw CompilerException.cyclicReference(node.getIdentifier(),
                    this.currentNormalExpression.getNameDeclaration());
        }
    }

}
