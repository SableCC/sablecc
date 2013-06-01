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

import org.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class Element
        extends LocalDeclaration {

    private Grammar grammar;

    private AElement declaration;

    // Cached values

    private String name;

    Element(
            Grammar grammar,
            AElement declaration) {

        this.grammar = grammar;
        this.declaration = declaration;
    }

    @Override
    public String getName() {

        if (this.name == null) {
            TElementName elementName = this.declaration.getElementName();
            if (elementName != null) {
                String text = elementName.getText();
                this.name = text.substring(1, text.length() - 2);
            }
            else {
                this.declaration.apply(new TreeWalker() {

                    @Override
                    public void caseAElement(
                            AElement node) {

                        visit(node.getElementBody());
                    }

                    @Override
                    public void caseANormalElementBody(
                            ANormalElementBody node) {

                        PUnaryOperator unaryOperator = node.getUnaryOperator();
                        if (unaryOperator == null
                                || unaryOperator instanceof AZeroOrOneUnaryOperator) {
                            visit(node.getUnit());
                        }
                        else {
                            Element.this.name = "";
                        }
                    }

                    @Override
                    public void caseASeparatedElementBody(
                            ASeparatedElementBody node) {

                        Element.this.name = "";
                    }

                    @Override
                    public void caseANameUnit(
                            ANameUnit node) {

                        Element.this.name = node.getIdentifier().getText();
                    }

                    @Override
                    public void caseAIdentifierCharUnit(
                            AIdentifierCharUnit node) {

                        String text = node.getIdentifierChar().getText();
                        Element.this.name = text.substring(1, text.length() - 1);
                    }

                    @Override
                    public void caseACharUnit(
                            ACharUnit node) {

                        Element.this.name = node.getChar().getText();
                    }

                    @Override
                    public void caseAIdentifierStringUnit(
                            AIdentifierStringUnit node) {

                        String text = node.getIdentifierString().getText();
                        Element.this.name = text.substring(1, text.length() - 1);
                    }

                    @Override
                    public void caseAStringUnit(
                            AStringUnit node) {

                        Element.this.name = node.getString().getText();
                    }

                    @Override
                    public void caseAEndUnit(
                            AEndUnit node) {

                        Element.this.name = node.getEndKeyword().getText();
                    }
                });

                if (this.name == null) {
                    throw new InternalException("unhandled case: "
                            + this.declaration);
                }
            }
        }

        return this.name;
    }

    @Override
    public String toString() {

        return "(" + getInternalName() + ")";
    }
}
