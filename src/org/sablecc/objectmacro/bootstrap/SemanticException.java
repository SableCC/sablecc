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

package org.sablecc.objectmacro.bootstrap;

import org.sablecc.objectmacro.syntax3.node.Token;

@SuppressWarnings("serial")
public class SemanticException
        extends RuntimeException {

    private final Token token;

    public SemanticException(
            String message,
            Token token) {

        super(message);
        this.token = token;
        if (message == null) {
            throw new InternalException();
        }
        if (token == null) {
            throw new InternalException();
        }
    }

    public Token getToken() {

        return this.token;
    }

    @Override
    public String toString() {

        return "Semantic error at (line:" + this.token.getLine() + ",pos:"
                + this.token.getPos() + "): " + getMessage() + ".";
    }
}
