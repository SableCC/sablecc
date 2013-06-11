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

import java.math.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Type {

    // can only be null for 0-length lists
    private Declaration base;

    // is null for non-separated lists
    private Declaration separator;

    // 0 or greater
    private BigInteger minMultiplicity;

    // greater or equal to minMultiplicity, or null for no maximum
    private BigInteger maxMultiplicity;

    Type(
            Production base,
            Production separator,
            BigInteger minMultiplicity,
            BigInteger maxMultiplicity) {

        this.base = base;
        this.separator = separator;
        this.minMultiplicity = minMultiplicity;
        this.maxMultiplicity = maxMultiplicity;

        validate();
    }

    Type(
            final Grammar grammar,
            PElementBody elementBody) {

        elementBody.apply(new TreeWalker() {

            private boolean separatorUnit;

            @Override
            public void caseANormalElementBody(
                    ANormalElementBody node) {

                visit(node.getUnit());
                PUnaryOperator unaryOperator = node.getUnaryOperator();
                if (unaryOperator == null) {
                    Type.this.minMultiplicity = BigInteger.ONE;
                    Type.this.maxMultiplicity = BigInteger.ONE;
                }
                else {
                    visit(unaryOperator);
                }
            }

            @Override
            public void caseASeparatedElementBody(
                    ASeparatedElementBody node) {

                visit(node.getLeft());
                this.separatorUnit = true;
                visit(node.getRight());
                this.separatorUnit = false;
                visit(node.getManyOperator());
            }

            @Override
            public void caseANameUnit(
                    ANameUnit node) {

                grammar.resolveParserNameUnit(node);
                Declaration declaration = grammar.getDeclarationResolution(node
                        .getIdentifier());

                if (this.separatorUnit) {
                    Type.this.separator = declaration;
                    if (Type.this.separator == Type.this.base) {
                        throw SemanticException.semanticError(
                                "The separator must be different.",
                                node.getIdentifier());
                    }
                }
                else {
                    Type.this.base = declaration;
                }
            }

            @Override
            public void caseAIdentifierCharUnit(
                    AIdentifierCharUnit node) {

                grammar.resolveParserIdentifierCharUnit(node);
                Expression expression = grammar.getExpressionResolution(node
                        .getIdentifierChar());

                if (this.separatorUnit) {
                    Type.this.separator = expression;
                    if (Type.this.separator == Type.this.base) {
                        throw SemanticException.semanticError(
                                "The separator must be different.",
                                node.getIdentifierChar());
                    }
                }
                else {
                    Type.this.base = expression;
                }
            }

            @Override
            public void caseACharUnit(
                    ACharUnit node) {

                grammar.resolveParserCharUnit(node);
                Expression expression = grammar.getExpressionResolution(node
                        .getChar());

                if (this.separatorUnit) {
                    Type.this.separator = expression;
                    if (Type.this.separator == Type.this.base) {
                        throw SemanticException.semanticError(
                                "The separator must be different.",
                                node.getChar());
                    }
                }
                else {
                    Type.this.base = expression;
                }
            }

            @Override
            public void caseAIdentifierStringUnit(
                    AIdentifierStringUnit node) {

                grammar.resolveParserIdentifierStringUnit(node);
                Expression expression = grammar.getExpressionResolution(node
                        .getIdentifierString());

                if (this.separatorUnit) {
                    Type.this.separator = expression;
                    if (Type.this.separator == Type.this.base) {
                        throw SemanticException.semanticError(
                                "The separator must be different.",
                                node.getIdentifierString());
                    }
                }
                else {
                    Type.this.base = expression;
                }
            }

            @Override
            public void caseAStringUnit(
                    AStringUnit node) {

                grammar.resolveParserStringUnit(node);
                Expression expression = grammar.getExpressionResolution(node
                        .getString());

                if (this.separatorUnit) {
                    Type.this.separator = expression;
                    if (Type.this.separator == Type.this.base) {
                        throw SemanticException.semanticError(
                                "The separator must be different.",
                                node.getString());
                    }
                }
                else {
                    Type.this.base = expression;
                }
            }

            @Override
            public void caseAEndUnit(
                    AEndUnit node) {

                grammar.resolveParserEndUnit(node);
                Expression expression = grammar.getExpressionResolution(node
                        .getEndKeyword());

                if (this.separatorUnit) {
                    Type.this.separator = expression;
                    if (Type.this.separator == Type.this.base) {
                        throw SemanticException.semanticError(
                                "The separator must be different.",
                                node.getEndKeyword());
                    }
                }
                else {
                    Type.this.base = expression;
                }
            }

            @Override
            public void caseAZeroOrOneUnaryOperator(
                    AZeroOrOneUnaryOperator node) {

                Type.this.minMultiplicity = BigInteger.ZERO;
                Type.this.maxMultiplicity = BigInteger.ONE;
            }

            @Override
            public void caseAZeroOrMoreManyOperator(
                    AZeroOrMoreManyOperator node) {

                Type.this.minMultiplicity = BigInteger.ZERO;
                Type.this.maxMultiplicity = null;
            }

            @Override
            public void caseAOneOrMoreManyOperator(
                    AOneOrMoreManyOperator node) {

                Type.this.minMultiplicity = BigInteger.ONE;
                Type.this.maxMultiplicity = null;
            }

            @Override
            public void caseANumberManyOperator(
                    ANumberManyOperator node) {

                Type.this.minMultiplicity = new BigInteger(node.getNumber()
                        .getText());
                Type.this.maxMultiplicity = Type.this.minMultiplicity;

                if (Type.this.minMultiplicity.compareTo(BigInteger.ONE) < 0) {
                    throw SemanticException.semanticError(
                            "The exponent must be greater or equal to 1.",
                            node.getNumber());
                }
            }

            @Override
            public void caseAIntervalManyOperator(
                    AIntervalManyOperator node) {

                Type.this.minMultiplicity = new BigInteger(node.getFrom()
                        .getText());
                Type.this.maxMultiplicity = new BigInteger(node.getTo()
                        .getText());

                if (Type.this.maxMultiplicity
                        .compareTo(Type.this.minMultiplicity) < 0) {
                    throw SemanticException
                            .semanticError(
                                    "The upper bound must be greater or equal to the lower bound.",
                                    node.getTo());
                }

                if (Type.this.maxMultiplicity.compareTo(BigInteger.ONE) < 0) {
                    throw SemanticException.semanticError(
                            "The upper bound must be greater or equal to 1.",
                            node.getTo());
                }
            }

            @Override
            public void caseAAtLeastManyOperator(
                    AAtLeastManyOperator node) {

                Type.this.minMultiplicity = new BigInteger(node.getNumber()
                        .getText());
                Type.this.maxMultiplicity = null;
            }
        });

        validate();
    }

    private void validate() {

        if (!isValid()) {
            throw new InternalException("invalid type");
        }
    }

    private boolean isValid() {

        if (this.base == null) {
            if (this.separator != null
                    || !this.minMultiplicity.equals(BigInteger.ZERO)
                    || this.maxMultiplicity == null
                    || !this.maxMultiplicity.equals(BigInteger.ZERO)) {
                return false;
            }
        }

        if (this.base != null && this.base == this.separator) {
            // base and separator cannot be the same declaration
            return false;
        }

        if (this.minMultiplicity == null
                || this.minMultiplicity.compareTo(BigInteger.ZERO) < 0) {
            return false;
        }

        if (this.maxMultiplicity != null
                && this.maxMultiplicity.compareTo(this.minMultiplicity) < 0) {
            return false;
        }

        return true;
    }
}
