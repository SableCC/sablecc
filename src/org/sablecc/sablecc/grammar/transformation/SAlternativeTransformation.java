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

package org.sablecc.sablecc.grammar.transformation;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.grammar.*;

public class SAlternativeTransformation {

    private Alternative alternative;

    private List<SAlternativeTransformationElement> elements = new LinkedList<SAlternativeTransformationElement>();

    public SAlternativeTransformation(
            Alternative alternative,
            List<SAlternativeTransformationElement> elements) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        if (elements == null) {
            throw new InternalException("treeAlternative shouldn't be null");
        }

        this.alternative = alternative;
        this.elements = elements;
    }

    public SAlternativeTransformation(
            Alternative alternative,
            Element element) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        if (element == null) {
            throw new InternalException("element shouldn't be null");
        }

        this.elements
                .add(new SAlternativeTransformationElement.ReferenceElement(
                        element));
    }

    public SAlternativeTransformation(
            Alternative alternative,
            LinkedList<Element> elements) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        if (elements == null) {
            throw new InternalException("elements shouldn't be null");
        }

        for (Element element : elements) {
            this.elements
                    .add(new SAlternativeTransformationElement.ReferenceElement(
                            element));
        }
    }

    public SAlternativeTransformation(
            Alternative alternative) {

        if (alternative == null) {
            throw new InternalException("alternative shouldn't be null");
        }

        this.elements.add(new SAlternativeTransformationElement.NullElement());
    }

    public Alternative getAlternative() {

        return this.alternative;
    }

    public List<SAlternativeTransformationElement> getElements() {

        return this.elements;
    }

}
