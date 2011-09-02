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

package org.sablecc.sablecc.grammar;

import org.sablecc.exception.*;
import org.sablecc.sablecc.grammar.interfaces.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class Element
        implements IElement {

    public abstract String getName();

    @Override
    public abstract Element clone();

    public static class TokenElement
            extends Element {

        private String name;

        public TokenElement(
                PUnit name) {

            if (name instanceof ANameUnit) {
                this.name = ((ANameUnit) name).getIdentifier().getText();
            }
            else if (name instanceof AStringUnit) {
                this.name = ((AStringUnit) name).getString().getText();
            }
            else if (name instanceof ACharacterUnit) {
                PCharacter character = ((ACharacterUnit) name).getCharacter();

                if (character instanceof ACharCharacter) {
                    this.name = ((ACharCharacter) character).getChar()
                            .getText();
                }
                else if (character instanceof ADecCharacter) {
                    this.name = ((ADecCharacter) character).getDecChar()
                            .getText();
                }
                else if (character instanceof AHexCharacter) {
                    this.name = ((AHexCharacter) character).getHexChar()
                            .getText();
                }
            }
            else if (name instanceof AStartUnit) {
                this.name = ((AStartUnit) name).getStartKeyword().getText();
            }
            else if (name instanceof AEndUnit) {
                this.name = ((AEndUnit) name).getEndKeyword().getText();
            }

        }

        public TokenElement(
                String name) {

            if (name == null) {
                throw new InternalException("name shouldn't be null");
            }
            this.name = name;
        }

        @Override
        public String getName() {

            return this.name;
        }

        @Override
        public Element clone() {

            return new TokenElement(this.name);
        }
    }

    public static class ProductionElement
            extends Element {

        private String name;

        private Production reference;

        public ProductionElement(
                String name,
                Production reference) {

            if (name == null) {
                throw new InternalException("name shouldn't be null");
            }

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.name = name;
            this.reference = reference;
        }

        public ProductionElement(
                Production reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.name = reference.getName();
            this.reference = reference;
        }

        @Override
        public String getName() {

            return this.name;
        }

        public Production getReference() {

            return this.reference;
        }

        @Override
        public Element clone() {

            return new ProductionElement(this.name, this.reference);
        }
    }

}
