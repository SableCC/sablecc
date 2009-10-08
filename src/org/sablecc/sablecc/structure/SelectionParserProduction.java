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

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class SelectionParserProduction
        extends Production {

    private final TIdentifier nameToken;

    private final ParserSelectorMethod parserSelectorMethod;

    SelectionParserProduction(
            TIdentifier nameToken,
            ParserSelectorMethod parserSelectorMethod) {

        if (nameToken == null) {
            throw new InternalException("nameToken may not be null");
        }

        if (parserSelectorMethod == null) {
            throw new InternalException("parserSelectorMethod may not be null");
        }

        if (!parserSelectorMethod.getDeclaration().getNames().contains(
                nameToken)) {
            throw new InternalException("invalid nameToken");
        }

        this.nameToken = nameToken;
        this.parserSelectorMethod = parserSelectorMethod;
    }

    @Override
    public TIdentifier getNameToken() {

        return this.nameToken;
    }

    public ParserSelectorMethod getParserSelectorMethod() {

        return this.parserSelectorMethod;
    }

}
