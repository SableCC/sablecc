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

    private boolean isList;

    private Declaration base;

    private Declaration separator;

    private BigInteger minMultiplicity;

    // null for no maximum
    private BigInteger maxMultiplicity;

    // cached values

    private Integer hashCode;

    Type(
            boolean isList,
            Declaration base,
            Declaration separator,
            BigInteger minMultiplicity,
            BigInteger maxMultiplicity) {

        this.isList = isList;
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
                Type.this.isList = true;
            }

            @Override
            public void caseAOneOrMoreManyOperator(
                    AOneOrMoreManyOperator node) {

                Type.this.minMultiplicity = BigInteger.ONE;
                Type.this.maxMultiplicity = null;
                Type.this.isList = true;
            }

            @Override
            public void caseANumberManyOperator(
                    ANumberManyOperator node) {

                Type.this.minMultiplicity = new BigInteger(node.getNumber()
                        .getText());
                Type.this.maxMultiplicity = Type.this.minMultiplicity;
                Type.this.isList = true;
            }

            @Override
            public void caseAIntervalManyOperator(
                    AIntervalManyOperator node) {

                Type.this.minMultiplicity = new BigInteger(node.getFrom()
                        .getText());
                Type.this.maxMultiplicity = new BigInteger(node.getTo()
                        .getText());
                Type.this.isList = true;

                if (Type.this.maxMultiplicity
                        .compareTo(Type.this.minMultiplicity) <= 0) {
                    throw SemanticException
                            .semanticError(
                                    "The upper bound must be greater than the lower bound.",
                                    node.getTo());
                }
            }

            @Override
            public void caseAAtLeastManyOperator(
                    AAtLeastManyOperator node) {

                Type.this.minMultiplicity = new BigInteger(node.getNumber()
                        .getText());
                Type.this.maxMultiplicity = null;
                Type.this.isList = true;
            }
        });

        validate();
    }

    public Declaration getBase() {

        return this.base;
    }

    public Declaration getSeparator() {

        return this.separator;
    }

    public BigInteger getMinMultiplicity() {

        return this.minMultiplicity;
    }

    public BigInteger getMaxMultiplicity() {

        return this.maxMultiplicity;
    }

    public Boolean isSimple() {

        return !this.isList && this.minMultiplicity.equals(BigInteger.ONE)
                && this.maxMultiplicity.equals(BigInteger.ONE);
    }

    public Boolean isNotList() {

        return !this.isList;
    }

    public Boolean isList() {

        return this.isList;
    }

    @Override
    public String toString() {

        if (!this.isList) {
            if (this.minMultiplicity.equals(BigInteger.ZERO)) {
                if (this.maxMultiplicity.equals(BigInteger.ZERO)) {
                    return "Null";
                }

                return this.base.getDisplayName() + "?";
            }

            return this.base.getDisplayName();
        }

        if (this.base == null) {
            return "()^0";
        }

        String name;
        if (this.separator == null) {
            name = this.base.getDisplayName();
        }
        else {
            name = "(" + this.base.getDisplayName() + " Separator "
                    + this.separator.getDisplayName() + ")";
        }

        if (this.minMultiplicity.equals(this.maxMultiplicity)) {
            return name + "^" + this.maxMultiplicity;
        }

        if (this.minMultiplicity.equals(BigInteger.ZERO)) {
            if (this.maxMultiplicity == null) {
                return name + "*";
            }

            return name + "^0.." + this.maxMultiplicity;
        }

        if (this.minMultiplicity.equals(BigInteger.ONE)) {
            if (this.maxMultiplicity == null) {
                return name + "+";
            }

            return name + "^1.." + this.maxMultiplicity;
        }

        if (this.maxMultiplicity == null) {
            return name + "^" + this.minMultiplicity + "...";
        }

        return name + "^" + this.minMultiplicity + ".." + this.maxMultiplicity;
    }

    @Override
    public boolean equals(
            Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        Type type = (Type) obj;

        if (type.isList != this.isList || type.base != this.base
                || type.separator != this.separator) {
            return false;
        }

        if (this.minMultiplicity != null) {
            if (!this.minMultiplicity.equals(type.minMultiplicity)) {
                return false;
            }
        }
        else if (type.minMultiplicity != null) {
            return false;
        }

        if (this.maxMultiplicity != null) {
            if (!this.maxMultiplicity.equals(type.maxMultiplicity)) {
                return false;
            }
        }
        else if (type.maxMultiplicity != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            this.hashCode = this.minMultiplicity.hashCode();
            if (this.maxMultiplicity != null) {
                this.hashCode += this.maxMultiplicity.hashCode();
            }
            if (this.base != null) {
                this.hashCode += this.base.hashCode();
            }
            if (this.separator != null) {
                this.hashCode += this.separator.hashCode();
            }
        }

        return this.hashCode;
    }

    public boolean isAssignableTo(
            Type elementType) {

        if (this.isList != elementType.isList) {
            return false;
        }

        if (this.base != null && this.base != elementType.base) {
            return false;
        }

        if (this.separator != null && this.separator != elementType.separator) {
            return false;
        }

        if (this.minMultiplicity.compareTo(elementType.minMultiplicity) < 0) {
            return false;
        }

        if (this.maxMultiplicity == null) {
            if (elementType.maxMultiplicity != null) {
                return false;
            }
        }
        else if (elementType.maxMultiplicity != null
                && this.maxMultiplicity.compareTo(elementType.maxMultiplicity) > 0) {
            return false;
        }

        if (this.isList) {
            if (this.maxMultiplicity == null
                    || this.maxMultiplicity.compareTo(BigInteger.ONE) > 0) {

                if (this.separator == null && elementType.separator != null) {
                    return false;
                }
            }
        }

        return true;
    }

    private void validate() {

        if (!isValid()) {
            throw new InternalException("invalid type");
        }
    }

    private boolean isValid() {

        if (this.minMultiplicity == null) {
            return false;
        }

        if (this.minMultiplicity.compareTo(BigInteger.ZERO) < 0) {
            return false;
        }

        if (!this.isList) {
            // it's not a list

            if (this.separator != null) {
                return false;
            }

            if (this.minMultiplicity.compareTo(BigInteger.ONE) > 0
                    || this.maxMultiplicity == null
                    || this.maxMultiplicity.compareTo(BigInteger.ONE) > 0
                    || this.maxMultiplicity.compareTo(this.minMultiplicity) < 0) {
                return false;
            }

            if (this.base == null) {
                if (!this.minMultiplicity.equals(BigInteger.ZERO)
                        || !this.maxMultiplicity.equals(BigInteger.ZERO)) {
                    return false;
                }

                return true;
            }

            return true;
        }

        // it's a list

        if (this.base == null) {
            if (this.separator != null
                    || !this.minMultiplicity.equals(BigInteger.ZERO)
                    || this.maxMultiplicity == null
                    || !this.maxMultiplicity.equals(BigInteger.ZERO)) {
                return false;
            }

            return true;
        }

        if (this.base == this.separator) {
            // base and separator cannot be the same declaration
            return false;
        }

        if (this.maxMultiplicity != null
                && this.maxMultiplicity.compareTo(this.minMultiplicity) < 0) {
            return false;
        }

        return true;
    }
}
