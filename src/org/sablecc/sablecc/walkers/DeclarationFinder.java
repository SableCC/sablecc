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

import org.sablecc.sablecc.structures.GlobalData;
import org.sablecc.sablecc.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.sablecc.syntax3.node.ALanguageName;
import org.sablecc.sablecc.syntax3.node.ANormalNamedExpression;
import org.sablecc.sablecc.syntax3.node.ASelectionNamedExpression;

public class DeclarationFinder
        extends DepthFirstAdapter {

    private GlobalData globalData;

    public DeclarationFinder(
            GlobalData globalData) {

        this.globalData = globalData;
    }

    @Override
    public void outALanguageName(
            ALanguageName node) {

        this.globalData.setLanguageName(node);
    }

    @Override
    public void outANormalNamedExpression(
            ANormalNamedExpression node) {

        this.globalData.addExpression(node);
    }

    @Override
    public void outASelectionNamedExpression(
            ASelectionNamedExpression node) {

        this.globalData.addExpression(node);
    }

}
