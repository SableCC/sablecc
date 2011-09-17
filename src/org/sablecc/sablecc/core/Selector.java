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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.analysis.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class Selector
        implements INameDeclaration, IVisitableGrammarPart {

    private final ASelector declaration;

    private final Grammar grammar;

    private final List<Selection> selections;

    Selector(
            ASelector declaration,
            Grammar grammar) {

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        this.declaration = declaration;
        this.grammar = grammar;

        List<Selection> selections = new LinkedList<Selector.Selection>();

        for (TIdentifier name : declaration.getNames()) {
            selections.add(newSelection(name));
        }

        this.selections = Collections.unmodifiableList(selections);
    }

    abstract Selection newSelection(
            TIdentifier name);

    public ASelector getDeclaration() {

        return this.declaration;
    }

    @Override
    public TIdentifier getNameIdentifier() {

        return this.declaration.getSelectorName();
    }

    @Override
    public String getName() {

        return getNameIdentifier().getText();
    }

    @Override
    public String getNameType() {

        return "lexer selector";
    }

    public List<Selection> getSelections() {

        return this.selections;
    }

    public abstract class Selection
            implements INameDeclaration, IVisitableGrammarPart {

        private final TIdentifier declaration;

        private Selection(
                TIdentifier declaration) {

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        @Override
        public TIdentifier getNameIdentifier() {

            return this.declaration;
        }

        @Override
        public String getName() {

            return getNameIdentifier().getText();
        }

        @Override
        public String getNameType() {

            return "lexer selection";
        }
    }

    public static class LexerSelector
            extends Selector {

        LexerSelector(
                ASelector declaration,
                Grammar grammar) {

            super(declaration, grammar);
        }

        @Override
        Selector.Selection newSelection(
                TIdentifier name) {

            return new Selection(name);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitLexerSelector(this);

        }

        public class Selection
                extends Selector.Selection
                implements INamedToken, IReferencable {

            private Selection(
                    TIdentifier declaration) {

                super(declaration);
            }

            @Override
            public void apply(
                    IGrammarVisitor visitor) {

                visitor.visitLexerSelectorSelection(this);

            }

            @Override
            public Token getLocation() {

                return getDeclaration().getSelectorName();
            }
        }
    }

    public static class ParserSelector
            extends Selector {

        ParserSelector(
                ASelector declaration,
                Grammar grammar) {

            super(declaration, grammar);
        }

        @Override
        Selector.Selection newSelection(
                TIdentifier name) {

            return new Selection(name);
        }

        @Override
        public void apply(
                IGrammarVisitor visitor) {

            visitor.visitParserSelector(this);

        }

        public class Selection
                extends Selector.Selection {

            private Selection(
                    TIdentifier declaration) {

                super(declaration);
            }

            @Override
            public void apply(
                    IGrammarVisitor visitor) {

                visitor.visitParserSelectorSelection(this);

            }

        }
    }
}
