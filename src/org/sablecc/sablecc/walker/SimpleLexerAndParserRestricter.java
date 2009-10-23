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

import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class SimpleLexerAndParserRestricter
        extends TreeWalker {

    @Override
    public void inASelectionNamedExpression(
            ASelectionNamedExpression node) {

        throw CompilerException.notImplemented(node.getSelectorName(),
                "lexer selector");
    }

    @Override
    public void inAGroup(
            AGroup node) {

        throw CompilerException.notImplemented(node.getName(), "group");
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
    public void inAStart(
            AStart node) {

        throw CompilerException.notImplemented(node.getStartKeyword(),
                "parser start");
    }

    @Override
    public void inARestartable(
            ARestartable node) {

        throw CompilerException.notImplemented(node.getRestartableKeyword(),
                "parser restartable");
    }

    @Override
    public void inAParserContext(
            AParserContext node) {

        if (node.getName() != null) {
            throw CompilerException.notImplemented(node.getName(),
                    "named parser context");
        }
    }

    @Override
    public void inASelectionParserProduction(
            ASelectionParserProduction node) {

        throw CompilerException.notImplemented(node.getSelectorName(),
                "parser selector");
    }

    @Override
    public void inADanglingQualifier(
            ADanglingQualifier node) {

        throw CompilerException.notImplemented(node.getDanglingKeyword(),
                "parser dangling production");
    }

    @Override
    public void inATokenQualifier(
            ATokenQualifier node) {

        throw CompilerException.notImplemented(node.getTokenKeyword(),
                "parser token production");
    }

    @Override
    public void inASeparatedElement(
            ASeparatedElement node) {

        throw CompilerException.notImplemented(node.getSeparatorKeyword(),
                "parser separated element");
    }

    @Override
    public void inAZeroOrOneUnaryOperator(
            AZeroOrOneUnaryOperator node) {

        throw CompilerException.notImplemented(node.getQMark(),
                "parser question mark");
    }

    @Override
    public void inAZeroOrMoreManyOperator(
            AZeroOrMoreManyOperator node) {

        throw CompilerException.notImplemented(node.getStar(),
                "parser star exponent");
    }

    @Override
    public void inAOneOrMoreManyOperator(
            AOneOrMoreManyOperator node) {

        throw CompilerException.notImplemented(node.getPlus(),
                "parser plus exponent");
    }

    @Override
    public void inANumberManyOperator(
            ANumberManyOperator node) {

        throw CompilerException.notImplemented(node.getNumber(),
                "parser number exponent");
    }

    @Override
    public void inAIntervalManyOperator(
            AIntervalManyOperator node) {

        throw CompilerException.notImplemented(node.getTwoDots(),
                "parser interval exponent");
    }

    @Override
    public void inAAtLeastManyOperator(
            AAtLeastManyOperator node) {

        throw CompilerException.notImplemented(node.getThreeDots(),
                "parser open interval exponent");
    }

    @Override
    public void inADanglingElement(
            ADanglingElement node) {

        throw CompilerException.notImplemented(node.getDanglingKeyword(),
                "parser dangling element");
    }

    @Override
    public void inAParserInvestigator(
            AParserInvestigator node) {

        throw CompilerException.notImplemented(node.getName(),
                "parser investigator");
    }

    @Override
    public void inAInlined(
            AInlined node) {

        throw CompilerException.notImplemented(node.getInlinedKeyword(),
                "parser inlining");
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
