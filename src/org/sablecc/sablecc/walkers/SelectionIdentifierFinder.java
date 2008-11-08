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

import java.util.LinkedList;
import java.util.List;

import org.sablecc.sablecc.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.sablecc.syntax3.node.ASelection;
import org.sablecc.sablecc.syntax3.node.ASelectionAdditionalIdentifier;
import org.sablecc.sablecc.syntax3.node.PSelection;
import org.sablecc.sablecc.syntax3.node.TIdentifier;

public class SelectionIdentifierFinder
        extends DepthFirstAdapter {

    private static final SelectionIdentifierFinder instance = new SelectionIdentifierFinder();

    private List<TIdentifier> identifiers;

    private SelectionIdentifierFinder() {

    }

    @Override
    public void inASelection(
            ASelection node) {

        this.identifiers.add(node.getIdentifier());
    }

    @Override
    public void inASelectionAdditionalIdentifier(
            ASelectionAdditionalIdentifier node) {

        this.identifiers.add(node.getIdentifier());
    }

    public static List<TIdentifier> getIdentifiers(
            PSelection selection) {

        List<TIdentifier> identifiers = new LinkedList<TIdentifier>();
        instance.identifiers = identifiers;

        selection.apply(instance);

        instance.identifiers = null;
        return identifiers;
    }
}
