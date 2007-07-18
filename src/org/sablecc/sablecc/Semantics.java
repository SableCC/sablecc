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

package org.sablecc.sablecc;

import org.sablecc.sablecc.exception.SemanticException;
import org.sablecc.sablecc.syntax3.node.Start;
import org.sablecc.sablecc.syntax3.node.TNumber;
import org.sablecc.sablecc.walkers.SyntaxVersionExtractor;

public class Semantics {

    private int syntaxVersion;

    public Semantics(
            Start ast)
            throws SemanticException {

        extractSyntaxVersion(ast);
    }

    public int getSyntaxVersion() {

        return this.syntaxVersion;
    }

    private void extractSyntaxVersion(
            Start ast)
            throws SemanticException {

        TNumber versionToken = SyntaxVersionExtractor.getSyntaxVersion(ast);

        try {
            int syntaxVersion = Integer.parseInt(versionToken.getText());

            if (syntaxVersion != 4) {
                throw new SemanticException("unsupported syntax version",
                        versionToken);
            }

            this.syntaxVersion = syntaxVersion;
        }
        catch (NumberFormatException e) {
            throw new SemanticException("unsupported syntax version",
                    versionToken, e);
        }
    }

}
