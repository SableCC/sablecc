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
import org.sablecc.objectmacro.bootstrap.structures.Macro;
import org.sablecc.objectmacro.bootstrap.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AExpand;
import org.sablecc.objectmacro.bootstrap.syntax3.node.AMacro;
import org.sablecc.objectmacro.bootstrap.syntax3.node.Switch;
import org.sablecc.objectmacro.bootstrap.syntax3.node.TIdentifier;

public class FindSubMacros
        extends DepthFirstAdapter
        implements Switch {

    @Override
    public void outAMacro(
            AMacro node) {

        Macro macro = Macro.getMacro(node);

        if (macro.isImplicitlyExpanded()) {
            if (macro.getParentScope() instanceof Macro) {
                Macro parentMacro = (Macro) macro.getParentScope();

                parentMacro.addSubMacro(macro);
            }
        }
    }

    @Override
    public void outAExpand(
            AExpand node) {

        Expand expand = Expand.getExpand(node);

        for (TIdentifier id : node.getMacroNames()) {
            String macroName = id.getText();

            Macro subMacro = expand.getMacro().getMacro(macroName);
            expand.getMacro().addSubMacro(subMacro);
        }
    }
}
