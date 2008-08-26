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

package org.sablecc.objectmacro.bootstrap.walkers;

import org.sablecc.objectmacro.bootstrap.structures.Expand;
import org.sablecc.objectmacro.bootstrap.structures.File;
import org.sablecc.objectmacro.bootstrap.structures.Macro;
import org.sablecc.objectmacro.bootstrap.structures.Param;
import org.sablecc.objectmacro.bootstrap.structures.Scope;
import org.sablecc.objectmacro.bootstrap.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AExpand;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AFile;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AMacro;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AParam;

public class FindDefinitions
        extends DepthFirstAdapter {

    private Scope currentScope;

    private boolean first_param;

    @Override
    public void inAFile(
            AFile node) {

        this.currentScope = new File(node);
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.currentScope = new Macro(node, this.currentScope);
        this.first_param = true;
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentScope = Macro.getMacro(node).getParentScope();
    }

    @Override
    public void inAParam(
            AParam node) {

        new Param(node, this.first_param);
        this.first_param = false;
    }

    @Override
    public void inAExpand(
            AExpand node) {

        new Expand(node);
    }

}
