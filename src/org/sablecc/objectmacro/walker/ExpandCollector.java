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

package org.sablecc.objectmacro.walker;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.structure.*;
import org.sablecc.objectmacro.syntax3.analysis.*;
import org.sablecc.objectmacro.syntax3.node.*;

public class ExpandCollector
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private Macro currentMacro;

    private Expand currentExpand;

    public ExpandCollector(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        if (this.currentMacro != null) {
            this.currentMacro = this.currentMacro.getMacro(node.getName());
        }
        else {
            this.currentMacro = this.globalIndex.getTopMacro(node.getName());
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentMacro = this.currentMacro.getParent();
    }

    @Override
    public void inAExpand(
            AExpand node) {

        this.currentExpand = this.currentMacro.getExpand(node);
    }

    @Override
    public void outAExpand(
            AExpand node) {

        this.currentExpand = null;
    }

    @Override
    public void inAOption(
            AOption node) {

        this.currentExpand.setOption(node);
    }

}
