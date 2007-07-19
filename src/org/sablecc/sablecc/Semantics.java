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

import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.SemanticException;
import org.sablecc.sablecc.syntax3.node.TNumber;
import org.sablecc.sablecc.walkers.LanguageNameExtractor;
import org.sablecc.sablecc.walkers.SyntaxVersionExtractor;

public class Semantics {

    private String languageName;

    private int syntaxVersion;

    public Semantics(
            GlobalInformation globalInformation)
            throws SemanticException {

        if (globalInformation == null) {
            throw new InternalException("globalInformation may not be null");
        }

        extractLanguageName(globalInformation);

        switch (globalInformation.getVerbosity()) {
        case NORMAL:
        case VERBOSE:
            System.out.println(" Analyzing language '" + this.languageName
                    + "'");
        }

        extractSyntaxVersion(globalInformation);
    }

    public String getLanguageName() {

        return this.languageName;
    }

    public int getSyntaxVersion() {

        return this.syntaxVersion;
    }

    private void extractLanguageName(
            GlobalInformation globalInformation) {

        this.languageName = LanguageNameExtractor.getLanguageName(
                globalInformation).getText();
    }

    private void extractSyntaxVersion(
            GlobalInformation globalInformation)
            throws SemanticException {

        TNumber versionToken = SyntaxVersionExtractor
                .getSyntaxVersion(globalInformation);

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
