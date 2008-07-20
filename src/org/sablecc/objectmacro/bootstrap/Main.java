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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import org.sablecc.objectmacro.syntax3.lexer.Lexer;
import org.sablecc.objectmacro.syntax3.lexer.LexerException;
import org.sablecc.objectmacro.syntax3.node.Start;
import org.sablecc.objectmacro.syntax3.parser.Parser;
import org.sablecc.objectmacro.syntax3.parser.ParserException;

public class Main {

    public static void main(
            String[] args)
            throws ParserException, LexerException, IOException {

        Start ast = new Parser(new Lexer(new PushbackReader(
                new InputStreamReader(System.in), 1024))).parse();
        ast.apply(new SemanticVerifications());
    }

}
