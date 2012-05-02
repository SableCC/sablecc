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

package org.sablecc.sablecc.core.walker;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.transformation.*;
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public class DeclarationFinder
        extends DepthFirstAdapter {

    public static class InlineExpressionsFinder
            extends DeclarationFinder {

        private final Grammar grammar;

        public InlineExpressionsFinder(
                Grammar grammar) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
        }

        @Override
        public void caseANamedExpression(
                ANamedExpression node) {

            // Do not visit subtree
        }

        @Override
        public void inAStringUnit(
                AStringUnit node) {

            LexerExpression.declareInlineExpression(node, this.grammar);
        }

        @Override
        public void inACharCharacter(
                ACharCharacter node) {

            LexerExpression.declareInlineExpression(node, this.grammar);
        }

        @Override
        public void inADecCharacter(
                ADecCharacter node) {

            LexerExpression.declareInlineExpression(node, this.grammar);
        }

        @Override
        public void inAHexCharacter(
                AHexCharacter node) {

            LexerExpression.declareInlineExpression(node, this.grammar);
        }

        @Override
        public void inAStartUnit(
                AStartUnit node) {

            LexerExpression.declareInlineExpression(node, this.grammar);
        }

        @Override
        public void inAEndUnit(
                AEndUnit node) {

            LexerExpression.declareInlineExpression(node, this.grammar);
        }
    }

    public static class TransformationsFinder
            extends DeclarationFinder {

        private final Grammar grammar;

        public TransformationsFinder(
                Grammar grammar) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
        }

        @Override
        public void inAProductionTransformation(
                AProductionTransformation node) {

            ProductionTransformation transformation = new ProductionTransformation.ExplicitProductionTransformation(
                    node, this.grammar);

            this.grammar.getTransformation().addProductionTransformation(
                    transformation);

        }

        @Override
        public void inAAlternativeTransformation(
                AAlternativeTransformation node) {

            this.grammar
                    .getTransformation()
                    .addAlternativeTransformation(
                            new AlternativeTransformation.ExplicitAlternativeTransformation(
                                    node, this.grammar));
        }
    }

    public static class LexerPrioritiesFinder
            extends DeclarationFinder {

        private final Grammar grammar;

        public LexerPrioritiesFinder(
                Grammar grammar) {

            if (grammar == null) {
                throw new InternalException("grammar may not be null");
            }

            this.grammar = grammar;
        }
    }

}
