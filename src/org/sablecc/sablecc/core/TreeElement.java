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
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.interfaces.*;

public abstract class TreeElement
        implements ImplicitExplicit {

    private final Grammar grammar;

    private final TreeAlternative alternative;

    private TreeElement(
            Grammar grammar,
            TreeAlternative alternative) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (alternative == null) {
            throw new InternalException("alternative may not be null");
        }

        this.grammar = grammar;
        this.alternative = alternative;
    }

    public TreeAlternative getAlternative() {

        return this.alternative;
    }

    public abstract String getName();

    public abstract Token getNameToken();

    public static class NormalElement
            extends TreeElement {

        private ANormalElement declaration;

        private String name;

        private Token token;

        public NormalElement(
                ANormalElement declaration,
                Grammar grammar,
                TreeAlternative alternative) {

            super(grammar, alternative);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        @Override
        public String getImplicitName() {

            String implicitName = null;

            if (this.declaration.getUnit() instanceof ANameUnit
                    && (this.declaration.getUnaryOperator() == null || this.declaration
                            .getUnaryOperator() instanceof AZeroOrOneUnaryOperator)) {

                implicitName = ((ANameUnit) this.declaration.getUnit())
                        .getIdentifier().getText();
            }

            return implicitName;
        }

        @Override
        public String getExplicitName() {

            String explicitName = null;

            if (this.declaration.getElementName() != null) {
                explicitName = this.declaration.getElementName().getText();
                explicitName = explicitName.substring(1,
                        explicitName.length() - 2);
            }

            return explicitName;
        }

        @Override
        public void setName(
                String name) {

            this.name = name;
        }

        @Override
        public String getName() {

            // TODO Null ok ?
            return this.name;
        }

        @Override
        public Token getNameToken() {

            if (this.token == null) {
                if (getExplicitName() != null
                        && getExplicitName().equals(this.name)) {
                    this.token = this.declaration.getElementName();
                }
                else if (getImplicitName().equals(this.name)) {
                    if (!(this.declaration.getUnit() instanceof ANameUnit)) {
                        throw new InternalException("unit may not be a "
                                + this.declaration.getUnit().getClass());
                    }
                    this.token = ((ANameUnit) this.declaration.getUnit())
                            .getIdentifier();
                }
            }

            return this.token;
        }

    }

    public static class SeparatedElement
            extends TreeElement {

        private ASeparatedElement declaration;

        private String name;

        public SeparatedElement(
                ASeparatedElement declaration,
                Grammar grammar,
                TreeAlternative alternative) {

            super(grammar, alternative);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;
        }

        @Override
        public String getImplicitName() {

            return null;
        }

        @Override
        public String getExplicitName() {

            String explicitName = null;

            if (this.declaration.getElementName() != null) {
                explicitName = this.declaration.getElementName().getText();
                explicitName = explicitName.substring(1,
                        explicitName.length() - 2);
            }

            return explicitName;
        }

        @Override
        public void setName(
                String name) {

            this.name = name;

        }

        @Override
        public String getName() {

            // TODO Null ok ?
            return this.name;
        }

        @Override
        public Token getNameToken() {

            return this.declaration.getElementName();
        }

    }

    public static class AlternatedElement
            extends TreeElement {

        private AAlternatedElement declaration;

        private String name;

        public AlternatedElement(
                AAlternatedElement declaration,
                Grammar grammar,
                TreeAlternative alternative) {

            super(grammar, alternative);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;

        }

        @Override
        public String getImplicitName() {

            return null;
        }

        @Override
        public String getExplicitName() {

            String explicitName = null;

            if (this.declaration.getElementName() != null) {
                explicitName = this.declaration.getElementName().getText();
                explicitName = explicitName.substring(1,
                        explicitName.length() - 2);
            }

            return explicitName;
        }

        @Override
        public void setName(
                String name) {

            this.name = name;
        }

        @Override
        public String getName() {

            // TODO Null ok ?
            return this.name;
        }

        @Override
        public Token getNameToken() {

            return this.declaration.getElementName();
        }

    }
}
