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

public class ParserSelectionProduction
        extends ParserProduction {

    private final TIdentifier nameDeclaration;

    private final ParserSelector parserSelector;

    ParserSelectionProduction(
            TIdentifier nameDeclaration,
            ParserSelector parserSelector) {

        if (nameDeclaration == null) {
            throw new InternalException("nameDeclaration may not be null");
        }

        if (parserSelector == null) {
            throw new InternalException("parserSelector may not be null");
        }

        this.nameDeclaration = nameDeclaration;
        this.parserSelector = parserSelector;
    }

    @Override
    public TIdentifier getNameDeclaration() {

        return this.nameDeclaration;
    }

    public ParserSelector getParserSelector() {

        return this.parserSelector;
    }

}
