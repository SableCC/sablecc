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
        implements Declaration {

    private Grammar grammar;

    private Node declaration;

    // Cached values

    private String name;

    private Boolean isInlinedExpression;

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
            if (this.declaration instanceof ANamedExpression) {
                this.name = ((ANamedExpression) this.declaration).getName()
                        .getText();
                this.isInlinedExpression = false;
            }
            else if (this.declaration instanceof AIdentifierCharUnit) {
                this.name = removeQuotes(((AIdentifierCharUnit) this.declaration)
                        .getIdentifierChar().getText());
                this.isInlinedExpression = true;
            }
            else if (this.declaration instanceof ACharUnit) {
                this.name = ((ACharUnit) this.declaration).getChar().getText();
                this.isInlinedExpression = true;
            }
            else if (this.declaration instanceof AIdentifierStringUnit) {
                this.name = removeQuotes(((AIdentifierStringUnit) this.declaration)
                        .getIdentifierString().getText());
                this.isInlinedExpression = true;
            }
            else if (this.declaration instanceof AStringUnit) {
                this.name = ((AStringUnit) this.declaration).getString()
                        .getText();
                this.isInlinedExpression = true;
            }
            else if (this.declaration instanceof AEndUnit) {
                this.name = ((AEndUnit) this.declaration).getEndKeyword()
                        .getText();
                this.isInlinedExpression = true;
            }
            else {
                throw new InternalException("unhandled case: "
                        + this.declaration.getClass().getSimpleName());
            }
        }

        return this.name;
    }

    @Override
    public boolean isInlinedExpression() {

        return this.isInlinedExpression;
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
            else {
                throw new InternalException("unhandled case: "
                        + this.declaration.getClass().getSimpleName());
            }
        }

        return this.location;
    }

    private String removeQuotes(
            String string) {

        return string.substring(1, string.length() - 1);
    }
}
