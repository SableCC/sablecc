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
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.sablecc.syntax3.node.ASpecification;
import org.sablecc.sablecc.syntax3.node.ASyntax;
import org.sablecc.sablecc.syntax3.node.TNumber;

public class SyntaxVersionExtractor
        extends DepthFirstAdapter {

    private TNumber syntaxVersion;

    private SyntaxVersionExtractor() {

        // do nothing
    }

    @Override
    public void caseASpecification(
            ASpecification node) {

        // no need to walk anything other than the header
        node.getHeader().apply(this);
    }

    @Override
    public void caseASyntax(
            ASyntax node) {

        this.syntaxVersion = node.getVersion();
    }

    public static TNumber getSyntaxVersion(
            GlobalInformation globalInformation) {

        if (globalInformation == null) {
            throw new InternalException("ast may not be null");
        }

        SyntaxVersionExtractor syntaxVersionExtractor = new SyntaxVersionExtractor();
        globalInformation.getAst().apply(syntaxVersionExtractor);

        if (syntaxVersionExtractor.syntaxVersion == null) {
            throw new InternalException("there must be a syntax version");
        }

        return syntaxVersionExtractor.syntaxVersion;
    }
}
