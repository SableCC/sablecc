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

import org.sablecc.sablecc.GlobalInformation;
import org.sablecc.sablecc.exception.BypassSemanticException;
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.SemanticException;
import org.sablecc.sablecc.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.sablecc.syntax3.node.AGroup;
import org.sablecc.sablecc.syntax3.node.AHelper;
import org.sablecc.sablecc.syntax3.node.ASelectionToken;
import org.sablecc.sablecc.syntax3.node.ASimpleToken;

public class DeclarationExtractor
        extends DepthFirstAdapter {

    private final GlobalInformation globalInformation;

    private AGroup enclosingGroup = null;

    private DeclarationExtractor(
            GlobalInformation globalInformation) {

        this.globalInformation = globalInformation;
    }

    @Override
    public void caseAHelper(
            AHelper node) {

        try {
            this.globalInformation.addHelper(node);
        }
        catch (SemanticException e) {
            throw new BypassSemanticException(e);
        }
    }

    @Override
    public void inAGroup(
            AGroup node) {

        try {
            this.globalInformation.addGroup(node);
        }
        catch (SemanticException e) {
            throw new BypassSemanticException(e);
        }

        this.enclosingGroup = node;
    }

    @Override
    public void outAGroup(
            AGroup node) {

        this.enclosingGroup = null;
    }

    @Override
    public void caseASimpleToken(
            ASimpleToken node) {

        try {
            this.globalInformation.addSimpleToken(node, this.enclosingGroup);
        }
        catch (SemanticException e) {
            throw new BypassSemanticException(e);
        }
    }

    @Override
    public void caseASelectionToken(
            ASelectionToken node) {

        try {
            this.globalInformation.addSelectionToken(node, this.enclosingGroup);
        }
        catch (SemanticException e) {
            throw new BypassSemanticException(e);
        }
    }

    public static void extractDeclarations(
            GlobalInformation globalInformation)
            throws SemanticException {

        if (globalInformation == null) {
            throw new InternalException("ast may not be null");
        }

        DeclarationExtractor declarationExtractor = new DeclarationExtractor(
                globalInformation);

        try {
            globalInformation.getAst().apply(declarationExtractor);
        }
        catch (BypassSemanticException e) {
            throw e.getSemanticException();
        }
    }
}
