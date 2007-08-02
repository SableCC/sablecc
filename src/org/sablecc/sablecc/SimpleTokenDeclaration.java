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

package org.sablecc.sablecc;

import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.syntax3.node.ASimpleToken;

public class SimpleTokenDeclaration
        extends Declaration {

    private final ASimpleToken simpleToken;

    SimpleTokenDeclaration(
            ASimpleToken simpleToken) {

        if (simpleToken == null) {
            throw new InternalException("simpleToken may not be null");
        }

        this.simpleToken = simpleToken;
    }

    ASimpleToken getSimpleToken() {

        return this.simpleToken;
    }

    @Override
    DeclarationType getType() {

        return DeclarationType.SIMPLE_TOKEN;
    }
}
