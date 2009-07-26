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

package org.sablecc.sablecc.structure;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.syntax3.node.TIdentifier;

public class SelectionExpression
        extends Expression {

    private final TIdentifier nameDeclaration;

    private final LexerSelector lexerSelector;

    SelectionExpression(
            TIdentifier nameDeclaration,
            LexerSelector lexerSelector) {

        if (nameDeclaration == null) {
            throw new InternalException("nameDeclaration may not be null");
        }

        if (lexerSelector == null) {
            throw new InternalException("lexerSelector may not be null");
        }

        this.nameDeclaration = nameDeclaration;
        this.lexerSelector = lexerSelector;
    }

    @Override
    public TIdentifier getNameDeclaration() {

        return this.nameDeclaration;
    }

    public LexerSelector getLexerSelector() {

        return this.lexerSelector;
    }
}
