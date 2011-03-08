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

package org.sablecc.sablecc.core;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class ParserProduction
        implements INameDeclaration {

    private final AParserProduction declaration;

    private final Grammar grammar;

    private ParserProduction(
            AParserProduction declaration,
            Grammar grammar) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.declaration = declaration;
        grammar.addMapping(declaration, this);
        this.grammar = grammar;
    }

    public TIdentifier getNameIdentifier() {

        return this.declaration.getName();
    }

    public String getName() {

        return getNameIdentifier().getText();
    }

    public String getNameType() {

        return "parser production";
    }

    static ParserProduction newParserProduction(
            AParserProduction declaration,
            Grammar grammar) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (declaration.getQualifier() == null) {
            return new NormalProduction(declaration, grammar);
        }

        if (declaration.getQualifier() instanceof ADanglingQualifier) {
            return new DanglingProduction(declaration, grammar);
        }

        if (declaration.getQualifier() instanceof ATokenQualifier) {
            return new TokenProduction(declaration, grammar);
        }

        throw new InternalException("unhandled case");
    }

    public static class NormalProduction
            extends ParserProduction {

        private NormalProduction(
                AParserProduction declaration,
                Grammar grammar) {

            super(declaration, grammar);
        }

    }

    public static class DanglingProduction
            extends ParserProduction {

        private DanglingProduction(
                AParserProduction declaration,
                Grammar grammar) {

            super(declaration, grammar);
        }

    }

    public static class TokenProduction
            extends ParserProduction
            implements INamedToken {

        private TokenProduction(
                AParserProduction declaration,
                Grammar grammar) {

            super(declaration, grammar);
        }

    }
}
