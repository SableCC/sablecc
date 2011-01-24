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

import java.io.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.launcher.*;
import org.sablecc.sablecc.syntax3.lexer.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.sablecc.syntax3.parser.*;
import org.sablecc.util.*;

public class GrammarCompiler {

    final private String grammar;

    final private Strictness strictness;

    final private Trace trace;

    private boolean hasRun;

    public GrammarCompiler(
            String grammar,
            Strictness strictness,
            Trace trace) {

        if (grammar == null) {
            throw new InternalException("grammar may not be null");
        }

        if (strictness == null) {
            throw new InternalException("strictness may not be null");
        }

        if (trace == null) {
            throw new InternalException("trace may not be null");
        }

        this.grammar = grammar;
        this.strictness = strictness;
        this.trace = trace;
    }

    public void compileGrammar()
            throws ParserException, LexerException, IOException {

        if (this.hasRun) {
            throw new InternalException("grammar may only be compiled once");
        }
        else {
            this.hasRun = true;
        }

        Start ast = new Parser(new Lexer(new PushbackReader(new StringReader(
                this.grammar), 1024))).parse();

        this.trace.verboseln(" Verifying semantics");

        Grammar grammar = new Grammar(ast);

        throw new InternalException("not implemented");
    }
}
