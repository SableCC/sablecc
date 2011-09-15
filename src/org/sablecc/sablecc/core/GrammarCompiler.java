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
import org.sablecc.sablecc.syntax3.analysis.*;
import org.sablecc.sablecc.syntax3.node.*;
import org.sablecc.util.*;

public class GrammarCompiler {

    public static final boolean RESTRICTED_SYNTAX = true;

    final private String text;

    final private Strictness strictness;

    final private Trace trace;

    private boolean hasRun;

    public GrammarCompiler(
            String text,
            Strictness strictness,
            Trace trace) {

        if (text == null) {
            throw new InternalException("text may not be null");
        }

        if (strictness == null) {
            throw new InternalException("strictness may not be null");
        }

        if (trace == null) {
            throw new InternalException("trace may not be null");
        }

        this.text = text;
        this.strictness = strictness;
        this.trace = trace;
    }

    public void compileGrammar()
            throws org.sablecc.sablecc.syntax3.parser.ParserException,
            org.sablecc.sablecc.syntax3.lexer.LexerException, IOException {

        if (this.hasRun) {
            throw new InternalException("grammar may only be compiled once");
        }
        else {
            this.hasRun = true;
        }

        this.trace.verboseln(" Parsing");

        Start ast = new org.sablecc.sablecc.syntax3.parser.Parser(
                new org.sablecc.sablecc.syntax3.lexer.Lexer(new PushbackReader(
                        new StringReader(this.text), 1024))).parse();

        this.trace.verboseln(" Verifying semantics");

        if (RESTRICTED_SYNTAX) {
            restrictSyntax(ast);
        }

        Grammar grammar = new Grammar(ast);

        if (RESTRICTED_SYNTAX) {
            this.trace.informativeln();
            this.trace
                    .informativeln("The syntax of this grammar specification is clean.");
            return;
        }

        throw new InternalException("not implemented");
    }

    /** restrict accepted syntax to desired subset */
    private void restrictSyntax(
            Start ast) {

        ast.apply(new DepthFirstAdapter() {

            @Override
            public void caseAInvestigator(
                    AInvestigator node) {

                throw SemanticException.notImplemented(node.getName());
            }

            @Override
            public void caseASelector(
                    ASelector node) {

                throw SemanticException.notImplemented(node.getSelectorName());
            }

            @Override
            public void inALexerContext(
                    ALexerContext node) {

                // reject named contexts
                if (node.getName() != null) {
                    throw SemanticException.notImplemented(node.getName());
                }
            }

            @Override
            public void caseALookback(
                    ALookback node) {

                throw SemanticException.notImplemented(node
                        .getLookbackKeyword());
            }

            @Override
            public void caseAStartUnit(
                    AStartUnit node) {

                throw SemanticException.notImplemented(node.getStartKeyword());
            }

            @Override
            public void caseAParser(
                    AParser node) {

                throw SemanticException.notImplemented(node.getParserKeyword());
            }

            @Override
            public void caseATransformation(
                    ATransformation node) {

                throw SemanticException.notImplemented(node
                        .getTransformationKeyword());
            }

            @Override
            public void caseATree(
                    ATree node) {

                throw SemanticException.notImplemented(node.getTreeKeyword());
            }

            @Override
            public void caseTIdentifier(
                    TIdentifier node) {

                // reject rich identifiers
                if (node.getText().charAt(0) == '<') {
                    throw SemanticException.notImplemented(node);
                }
            }
        });
    }
}
