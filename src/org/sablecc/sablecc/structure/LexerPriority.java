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

package org.sablecc.sablecc.structure;

import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class LexerPriority {

    private final ALexerPriority declaration;

    private final MatchedToken high;

    private final MatchedToken low;

    LexerPriority(
            ALexerPriority declaration,
            MatchedToken high,
            MatchedToken low) {

        if (high == low) {
            throw CompilerException.invalidPriority(declaration.getGt(), high);
        }
        this.declaration = declaration;
        this.high = high;
        this.low = low;

    }

    public ALexerPriority getDeclaration() {

        return this.declaration;
    }
}
