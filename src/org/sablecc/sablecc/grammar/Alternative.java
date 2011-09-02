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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.grammar.transformation.*;

public class Alternative {

    private List<Element> elements;

    private Production production;

    private SAlternativeTransformation transformation;

    public Alternative(
            Production production,
            List<Element> elements) {

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        if (elements == null) {
            throw new InternalException("elements may not be null");
        }

        this.production = production;
        this.elements = elements;
    }

    public Alternative(
            Production production,
            Element element) {

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        if (element == null) {
            throw new InternalException("element may not be null");
        }

        this.production = production;
        this.elements = new LinkedList<Element>();
        this.elements.add(element);

    }

    public Alternative(
            Production production) {

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        this.production = production;
        this.elements = new LinkedList<Element>();
        this.transformation = new SAlternativeTransformation(this);

    }

    public Production getProduction() {

        return this.production;
    }

    public List<Element> getElements() {

        return this.elements;
    }

    public void addElement(
            Element element) {

        this.elements.add(element);
    }

    public void addTransformation(
            SAlternativeTransformation transformation) {

        this.transformation = transformation;
    }

}
