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
import org.sablecc.sablecc.syntax3.node.Start;

public class GlobalInformation {

    private Verbosity verbosity;

    private Start ast;

    private Semantics semantics;

    public GlobalInformation(
            Verbosity verbosity,
            Start ast) {

        if (verbosity == null) {
            throw new InternalException("verbosity may not be null");
        }

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        this.verbosity = verbosity;
        this.ast = ast;
    }

    public Verbosity getVerbosity() {

        return this.verbosity;
    }

    public Start getAst() {

        return this.ast;
    }

    public Semantics getSemantics() {

        return this.semantics;
    }

    public void setSemantics(
            Semantics semantics) {

        if (this.semantics != null) {
            throw new InternalException("semantics is already set");
        }

        if (semantics == null) {
            throw new InternalException("semantics may not be null");
        }

        this.semantics = semantics;
    }
}
