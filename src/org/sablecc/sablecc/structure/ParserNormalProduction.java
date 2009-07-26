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

import java.util.LinkedHashMap;
import java.util.Map;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.syntax3.node.ANormalParserProduction;
import org.sablecc.sablecc.syntax3.node.AParserAlternative;
import org.sablecc.sablecc.syntax3.node.AParserInvestigator;
import org.sablecc.sablecc.syntax3.node.AParserProductionBody;
import org.sablecc.sablecc.syntax3.node.TIdentifier;

public class ParserNormalProduction
        extends ParserProduction {

    private final GlobalIndex globalIndex;

    private final ANormalParserProduction declaration;

    private final ParserContext parserContext;

    private ParserInvestigator parserInvestigator;

    private Map<AParserAlternative, ParserAlternative> astAlternativesMap = new LinkedHashMap<AParserAlternative, ParserAlternative>();

    ParserNormalProduction(
            GlobalIndex globalIndex,
            ANormalParserProduction declaration,
            ParserContext parserContext) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (parserContext == null) {
            throw new InternalException("parserContext may not be null");
        }

        this.globalIndex = globalIndex;
        this.declaration = declaration;
        this.parserContext = parserContext;
    }

    public ParserInvestigator addParserInvestigator(
            AParserInvestigator declaration) {

        if (this.parserInvestigator != null) {
            throw new InternalException("parser investigator is already set");
        }

        this.parserInvestigator = this.globalIndex
                .addParserInvestigator(declaration);
        return this.parserInvestigator;
    }

    public ParserAlternative addParserAlternative(
            AParserAlternative declaration) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (this.astAlternativesMap.containsKey(declaration)) {
            throw new InternalException("alternative has already been added");
        }

        ParserAlternative parserAlternative = new ParserAlternative(
                declaration, this);
        this.astAlternativesMap.put(declaration, parserAlternative);
        return parserAlternative;
    }

    @Override
    public TIdentifier getNameDeclaration() {

        return ((AParserProductionBody) this.declaration
                .getParserProductionBody()).getName();
    }

    public ParserContext getParserContext() {

        return this.parserContext;
    }
}
