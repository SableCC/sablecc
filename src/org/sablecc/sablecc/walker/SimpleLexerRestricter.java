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

package org.sablecc.sablecc.walker;

import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.sablecc.syntax3.node.AGroup;
import org.sablecc.sablecc.syntax3.node.ALexerContext;
import org.sablecc.sablecc.syntax3.node.ALexerInvestigator;
import org.sablecc.sablecc.syntax3.node.AParser;
import org.sablecc.sablecc.syntax3.node.ASelectionNamedExpression;
import org.sablecc.sablecc.syntax3.node.ATransformation;
import org.sablecc.sablecc.syntax3.node.ATree;

public class SimpleLexerRestricter
        extends DepthFirstAdapter {

    @Override
    public void inASelectionNamedExpression(
            ASelectionNamedExpression node) {

        throw CompilerException.notImplemented(node.getSelectorName(),
                "lexer selector");
    }

    @Override
    public void inAGroup(
            AGroup node) {

        throw CompilerException.notImplemented(node.getGroupKeyword(), "group");
    }

    @Override
    public void inALexerContext(
            ALexerContext node) {

        if (node.getName() != null) {
            throw CompilerException.notImplemented(node.getName(),
                    "named lexer context");
        }
    }

    @Override
    public void inALexerInvestigator(
            ALexerInvestigator node) {

        throw CompilerException.notImplemented(node.getName(),
                "lexer investigator");
    }

    @Override
    public void inAParser(
            AParser node) {

        throw CompilerException.notImplemented(node.getParserKeyword(),
                "parser");
    }

    @Override
    public void inATransformation(
            ATransformation node) {

        throw CompilerException.notImplemented(node.getTransformationKeyword(),
                "transformation");
    }

    @Override
    public void inATree(
            ATree node) {

        throw CompilerException.notImplemented(node.getTreeKeyword(), "tree");
    }

}
