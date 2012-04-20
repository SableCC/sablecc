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

    private final List<Production> origins = new LinkedList<Production>();

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
        this.origins.add(production);
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
        this.origins.add(production);

    }

    public Alternative(
            Production production) {

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        this.production = production;
        this.elements = new LinkedList<Element>();
        this.transformation = new SAlternativeTransformation(this);
        this.origins.add(production);

    }

    public Production getProduction() {

        return this.production;
    }

    public List<Element> getElements() {

        return this.elements;
    }

    public List<Production> getOrigins() {

        return this.origins;
    }

    public SAlternativeTransformation getTransformation() {

        return this.transformation;
    }

    public void addElement(
            Element element) {

        this.elements.add(element);
    }

    public void addTransformation(
            SAlternativeTransformation transformation) {

        this.transformation = transformation;
    }

    public void addOrigin(
            Production production) {

        if (this.origins.contains(production)) {
            throw new InternalException(
                    "The same origin shouldn't be added twice to an alternative");
        }

        this.origins.add(production);
    }

    public boolean contains(
            Production production) {

        for (Element element : this.elements) {
            if (element.getTypeName().equals(production.getName())) {
                return true;
            }
        }

        return false;
    }

    public void inline(
            Alternative alternative) {

        for (int i = 0; i < this.elements.size(); i++) {
            if (this.elements.get(i).getTypeName()
                    .equals(alternative.getProduction().getName())) {
                buildInlinedAlternative(i, alternative);
            }
        }
    }

    private void buildInlinedAlternative(
            int index,
            Alternative inlinedAlt) {

        LinkedList<Element> newAlternativeElements = new LinkedList<Element>();
        Map<Element, Element> oldToNewElements = new HashMap<Element, Element>();

        Element newElement;

        for (int j = 0; j < index; j++) {
            newElement = this.elements.get(j).clone();
            newAlternativeElements.add(newElement);
            oldToNewElements.put(this.elements.get(j), newElement);
        }

        for (Element element : inlinedAlt.getElements()) {
            newElement = element.clone();
            newAlternativeElements.add(newElement);
            oldToNewElements.put(element, newElement);
        }

        for (int j = index + 1; j < this.elements.size(); j++) {
            newElement = this.elements.get(j).clone();
            newAlternativeElements.add(newElement);
            oldToNewElements.put(this.elements.get(j), newElement);
        }

        Alternative newAlternative = new Alternative(this.production,
                newAlternativeElements);

        newAlternative.addTransformation(this.transformation
                .buildInlinedTransformation(inlinedAlt, oldToNewElements));

        newAlternative.inline(inlinedAlt);

        newAlternative.addOrigin(inlinedAlt.getProduction());

        this.production.addAlternative(newAlternative);

        // Duplicate priorities

        for (Priority priority : this.production.getPriorities()) {
            if (priority.getAlternatives().contains(this)) {
                priority.addAlternative(newAlternative, priority
                        .getAlternatives().indexOf(this));
            }
        }
    }

    @Override
    public String toString() {

        String alternativeText = "";

        for (Element element : this.elements) {
            alternativeText += element.toString() + " ";
        }

        return alternativeText;
    }

}
