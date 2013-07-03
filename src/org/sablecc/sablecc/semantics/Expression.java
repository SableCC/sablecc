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

package org.sablecc.sablecc.semantics;

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Expression
        extends Declaration {

    private Grammar grammar;

    private Node declaration;

    // Cached values

    private String name;

    private String lookupName;

    private Token location;

    Expression(
            Grammar grammar,
            Node declaration) {

        this.grammar = grammar;
        this.declaration = declaration;
    }

    @Override
    public String getName() {

        if (this.name == null) {

            Token nameToken = getLocation();

            if (nameToken instanceof TIdentifier) {
                this.name = nameToken.getText();
            }
            else if (nameToken instanceof TEndKeyword) {
                this.name = "end";
            }
            else if (nameToken instanceof TIdentifierChar
                    || nameToken instanceof TIdentifierString) {
                String text = nameToken.getText();
                this.lookupName = text.substring(1, text.length() - 1);
            }
            else {
                this.name = null;
            }
        }

        return this.name;
    }

    @Override
    public String getLookupName() {

        if (this.lookupName == null) {

            Token nameToken = getLocation();

            if (nameToken instanceof TIdentifier || nameToken instanceof TChar
                    || nameToken instanceof TString) {
                this.lookupName = nameToken.getText();
            }
            else if (nameToken instanceof TEndKeyword) {
                this.lookupName = "end";
            }
            else if (nameToken instanceof TIdentifierChar
                    || nameToken instanceof TIdentifierString) {
                String text = nameToken.getText();
                this.lookupName = text.substring(1, text.length() - 1);
            }
            else {
                throw new InternalException("unhandled case: "
                        + nameToken.getClass().getSimpleName());
            }
        }

        return this.lookupName;
    }

    @Override
    public String getDisplayName() {

        return getLocation().getText();
    }

    @Override
    public Token getLocation() {

        if (this.location == null) {
            if (this.declaration instanceof ANamedExpression) {
                this.location = ((ANamedExpression) this.declaration).getName();
            }
            else if (this.declaration instanceof AIdentifierCharUnit) {
                this.location = ((AIdentifierCharUnit) this.declaration)
                        .getIdentifierChar();
            }
            else if (this.declaration instanceof ACharUnit) {
                this.location = ((ACharUnit) this.declaration).getChar();
            }
            else if (this.declaration instanceof AIdentifierStringUnit) {
                this.location = ((AIdentifierStringUnit) this.declaration)
                        .getIdentifierString();
            }
            else if (this.declaration instanceof AStringUnit) {
                this.location = ((AStringUnit) this.declaration).getString();
            }
            else if (this.declaration instanceof AEndUnit) {
                this.location = ((AEndUnit) this.declaration).getEndKeyword();
            }
            else {
                throw new InternalException("unhandled case: "
                        + this.declaration.getClass().getSimpleName());
            }
        }

        return this.location;
    }
}
