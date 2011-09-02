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
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.core.Tree.TreeAlternative;
import org.sablecc.sablecc.grammar.interfaces.*;

public abstract class SAlternativeTransformationListElement {

    public static class ReferenceElement
            extends SAlternativeTransformationListElement {

        private IElement reference;

        public ReferenceElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.reference = reference;
        }

    }

    public static class NewElement
            extends SAlternativeTransformationListElement {

        private Tree.TreeAlternative treeAlternative;

        private List<SAlternativeTransformationElement> elements = new LinkedList<SAlternativeTransformationElement>();

        public NewElement(
                TreeAlternative treeAlternative,
                List<SAlternativeTransformationElement> elements) {

            if (treeAlternative == null) {
                throw new InternalException("reference shouldn't be null");
            }

            if (elements == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.treeAlternative = treeAlternative;
            this.elements = elements;
        }

    }

    public static class NormalListElement
            extends SAlternativeTransformationListElement {

        private IElement reference;

        public NormalListElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.reference = reference;
        }

    }

    public static class LeftListElement
            extends SAlternativeTransformationListElement {

        private IElement reference;

        public LeftListElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.reference = reference;
        }

    }

    public static class RightListElement
            extends SAlternativeTransformationListElement {

        private IElement reference;

        public RightListElement(
                IElement reference) {

            if (reference == null) {
                throw new InternalException("reference shouldn't be null");
            }

            this.reference = reference;
        }

    }

}
