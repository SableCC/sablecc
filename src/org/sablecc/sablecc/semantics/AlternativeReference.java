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

import org.sablecc.sablecc.syntax3.node.*;

public class AlternativeReference {

    private Grammar grammar;

    private Alternative alternative;

    private Token location;

    private AlternativeReference(
            Grammar grammar,
            Alternative alternative) {

        this.grammar = grammar;
        this.alternative = alternative;
    }

    public Alternative getAlternative() {

        return this.alternative;
    }

    public Token getLocation() {

        return this.location;
    }

    static AlternativeReference createDeclaredAlternativeReference(
            Grammar grammar,
            Alternative alternative,
            Token location) {

        AlternativeReference alternativeReference = new AlternativeReference(
                grammar, alternative);
        alternativeReference.location = location;
        return alternativeReference;
    }
}
