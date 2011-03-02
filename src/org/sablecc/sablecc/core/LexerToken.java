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

public abstract class LexerToken {

    private final Grammar grammar;

    LexerToken(
            Grammar grammar) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.grammar = grammar;
    }

    private static class ExpressionToken
            extends LexerToken {

        private final LexerExpression lexerExpression;

        ExpressionToken(
                LexerExpression lexerExpression,
                Grammar grammar) {

            super(grammar);

            if (lexerExpression == null) {
                throw new InternalException("lexerExpression may not be null");
            }

            this.lexerExpression = lexerExpression;
        }

    }

    private static class SelectionToken
            extends LexerToken {

        private final Selector.LexerSelector.Selection selection;

        SelectionToken(
                Selector.LexerSelector.Selection selection,
                Grammar grammar) {

            super(grammar);

            if (selection == null) {
                throw new InternalException("selection may not be null");
            }

            this.selection = selection;
        }

    }

    private static class ProductionToken
            extends LexerToken {

        private final ParserProduction production;

        ProductionToken(
                ParserProduction production,
                Grammar grammar) {

            super(grammar);

            if (production == null) {
                throw new InternalException("production may not be null");
            }

            this.production = production;
        }

    }

}
