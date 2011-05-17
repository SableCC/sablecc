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
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;

public abstract class TreeAlternative {

    private final Grammar grammar;

    private final TreeProduction production;

    private int index;

    private final LocalNamespace namespace = new LocalNamespace();

    private final List<TreeElement> elements = new LinkedList<TreeElement>();

    private TreeAlternative(
            Grammar grammar,
            TreeProduction production,
            int index) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (production == null) {
            throw new InternalException("production may not be null");
        }

        this.grammar = grammar;
        this.production = production;
        this.index = index;
    }

    public TreeProduction getProduction() {

        return this.production;
    }

    public LocalNamespace getNamespace() {

        return this.namespace;
    }

    public Grammar getGrammar() {

        return this.grammar;
    }

    public List<TreeElement> getElements() {

        return this.elements;
    }

    public int getIndex() {

        return this.index;
    }

    public static class NamedTreeAlternative
            extends TreeAlternative {

        private final ATreeAlternative declaration;

        private String name;

        private final LocalNamespace namespace;

        public NamedTreeAlternative(
                ATreeAlternative declaration,
                Grammar grammar,
                TreeProduction production,
                int index) {

            super(grammar, production, index);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;

            this.namespace = getNamespace();

            findNamedElement(this.declaration, this.namespace, getElements(),
                    getGrammar(), this);
        }

        public TAlternativeName getNameToken() {

            return this.declaration.getAlternativeName();
        }

        public String getName() {

            if (this.name == null) {
                String name = getNameToken().getText();
                name = name.substring(1, name.length() - 2);
                this.name = name;
            }

            return this.name;
        }
    }

    public static class AnonymousTreeAlternative
            extends TreeAlternative {

        private final ATreeAlternative declaration;

        private final LocalNamespace namespace;

        public AnonymousTreeAlternative(
                ATreeAlternative declaration,
                Grammar grammar,
                TreeProduction production,
                int index) {

            super(grammar, production, index);

            if (declaration == null) {
                throw new InternalException("declaration may not be null");
            }

            this.declaration = declaration;

            this.namespace = getNamespace();

            findNamedElement(this.declaration, this.namespace, getElements(),
                    getGrammar(), this);
        }
    }

    private static void findNamedElement(
            Node ast,
            final LocalNamespace namespace,
            final List<TreeElement> elements,
            final Grammar grammar,
            final TreeAlternative alternative) {

        final OccurrenceNameCounter occurenceNameCounter = new OccurrenceNameCounter();

        fillOccurrencesTable(occurenceNameCounter, ast);

        ast.apply(new DepthFirstAdapter() {

            @Override
            public void inAAlternatedElement(
                    AAlternatedElement node) {

                if (node.getElementName() != null) {
                    TreeElement.Named.Alternated alternatedElement = new TreeElement.Named.Alternated(
                            node, grammar, alternative);
                    namespace.addElement(alternatedElement);
                    elements.add(alternatedElement);
                }
                else {
                    TreeElement.Anonymous.Alternated alternatedElement = new TreeElement.Anonymous.Alternated(
                            node, grammar, alternative);
                    elements.add(alternatedElement);

                }

            }

            @Override
            public void inASeparatedElement(
                    ASeparatedElement node) {

                if (node.getElementName() != null) {
                    TreeElement.Named.Separated separatedElement = new TreeElement.Named.Separated(
                            node, grammar, alternative);
                    namespace.addElement(separatedElement);
                    elements.add(separatedElement);
                }
                else {
                    TreeElement.Anonymous.Separated separatedElement = new TreeElement.Anonymous.Separated(
                            node, grammar, alternative);
                    elements.add(separatedElement);

                }

            }

            @Override
            public void inANormalElement(
                    ANormalElement node) {

                if (node.getElementName() != null) {
                    TreeElement.Named.ExplicitNormal normalElement = new TreeElement.Named.ExplicitNormal(
                            node, grammar, alternative);
                    namespace.addElement(normalElement);
                    elements.add(normalElement);
                }
                else {
                    if (node.getUnit() instanceof ANameUnit
                            && (node.getUnaryOperator() == null || node
                                    .getUnaryOperator() instanceof AZeroOrOneUnaryOperator)) {

                        String name = ((ANameUnit) node.getUnit())
                                .getIdentifier().getText();

                        if (occurenceNameCounter.getImplicitCount(name) > 0
                                && occurenceNameCounter.getExplicitCount(name) > 0) {
                            TreeElement element = namespace.getElement(name);

                            if (element == null
                                    || !(element instanceof TreeElement.Named.ImplicitNormal)) {
                                TreeElement.Named.ImplicitNormal normalElement = new TreeElement.Named.ImplicitNormal(
                                        node, grammar, alternative);
                                namespace.addElement(normalElement);
                                elements.add(normalElement);
                            }
                        }
                        else if (occurenceNameCounter.getImplicitCount(name) == 1
                                && occurenceNameCounter.getExplicitCount(name) == 0) {
                            TreeElement.Named.ImplicitNormal normalElement = new TreeElement.Named.ImplicitNormal(
                                    node, grammar, alternative);
                            namespace.addElement(normalElement);
                            elements.add(normalElement);

                        }
                        else if (occurenceNameCounter.getImplicitCount(name) > 1
                                && occurenceNameCounter.getExplicitCount(name) == 0) {
                            TreeElement.Anonymous.Normal normalElement = new TreeElement.Anonymous.Normal(
                                    node, grammar, alternative);
                            elements.add(normalElement);
                        }
                    }
                    else {
                        TreeElement.Anonymous.Normal normalElement = new TreeElement.Anonymous.Normal(
                                node, grammar, alternative);
                        elements.add(normalElement);
                    }
                }

            }
        });

    }

    private static void fillOccurrencesTable(
            final OccurrenceNameCounter occurenceNameCounter,
            Node ast) {

        ast.apply(new DepthFirstAdapter() {

            @Override
            public void inAAlternatedElement(
                    AAlternatedElement node) {

                if (node.getElementName() != null) {
                    String name = node.getElementName().getText();
                    name = name.substring(1, name.length() - 2);
                    occurenceNameCounter.addExplicitOccurrence(name);
                }
            }

            @Override
            public void inASeparatedElement(
                    ASeparatedElement node) {

                if (node.getElementName() != null) {
                    String name = node.getElementName().getText();
                    name = name.substring(1, name.length() - 2);
                    occurenceNameCounter.addExplicitOccurrence(name);
                }

            }

            @Override
            public void inANormalElement(
                    ANormalElement node) {

                if (node.getElementName() != null) {
                    String name = node.getElementName().getText();
                    name = name.substring(1, name.length() - 2);
                    occurenceNameCounter.addExplicitOccurrence(name);
                }
                else {

                    if (node.getUnit() instanceof ANameUnit
                            && (node.getUnaryOperator() == null || node
                                    .getUnaryOperator() instanceof AZeroOrOneUnaryOperator)) {

                        String occurrenceName = ((ANameUnit) node.getUnit())
                                .getIdentifier().getText();
                        occurenceNameCounter
                                .addImplicitOccurrence(occurrenceName);
                    }
                }

            }
        });
    }

    private static class OccurrenceNameCounter {

        final Map<String, Integer> implicitOccurrenceCounter = new HashMap<String, Integer>();

        final Map<String, Integer> explicitOccurrenceCounter = new HashMap<String, Integer>();

        public OccurrenceNameCounter() {

        }

        public void addImplicitOccurrence(
                String text) {

            Integer nbOccurences = this.implicitOccurrenceCounter.get(text);
            if (nbOccurences == null) {
                nbOccurences = 1;
            }
            else {
                nbOccurences++;
            }
            this.implicitOccurrenceCounter.put(text, nbOccurences);
        }

        public void addExplicitOccurrence(
                String text) {

            Integer nbOccurences = this.explicitOccurrenceCounter.get(text);
            if (nbOccurences == null) {
                nbOccurences = 1;
            }
            else {
                nbOccurences++;
            }
            this.explicitOccurrenceCounter.put(text, nbOccurences);
        }

        public int getImplicitCount(
                String text) {

            if (this.implicitOccurrenceCounter.get(text) == null) {
                return 0;
            }
            else {
                return this.implicitOccurrenceCounter.get(text);
            }
        }

        public int getExplicitCount(
                String text) {

            if (this.explicitOccurrenceCounter.get(text) == null) {
                return 0;
            }
            else {
                return this.explicitOccurrenceCounter.get(text);
            }
        }

    }

    public static class LocalNamespace {

        private final Map<String, TreeElement.Named> nameMap = new HashMap<String, TreeElement.Named>();

        private void addElement(
                TreeElement.Named element) {

            if (element == null) {
                throw new InternalException("element may not be null");
            }

            String name = element.getName();

            if (this.nameMap.containsKey(name)) {
                throw SemanticException.duplicateElementName(element,
                        this.nameMap.get(name));
            }

            this.nameMap.put(name, element);
        }

        public TreeElement getElement(
                String name) {

            return this.nameMap.get(name);
        }

    }

}
