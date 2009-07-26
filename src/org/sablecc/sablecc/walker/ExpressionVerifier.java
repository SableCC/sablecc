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

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.structure.GlobalIndex;
import org.sablecc.sablecc.structure.NormalExpression;
import org.sablecc.sablecc.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.sablecc.syntax3.node.ACharCharacter;
import org.sablecc.sablecc.syntax3.node.ACharExpression;
import org.sablecc.sablecc.syntax3.node.ADecCharacter;
import org.sablecc.sablecc.syntax3.node.ADecExpression;
import org.sablecc.sablecc.syntax3.node.AHexCharacter;
import org.sablecc.sablecc.syntax3.node.AHexExpression;
import org.sablecc.sablecc.syntax3.node.AIntervalExponentExpression;
import org.sablecc.sablecc.syntax3.node.AIntervalExpression;
import org.sablecc.sablecc.syntax3.node.ANameExpression;
import org.sablecc.sablecc.syntax3.node.ANormalNamedExpression;
import org.sablecc.sablecc.syntax3.node.ASeparatedIntervalExponentExpression;
import org.sablecc.sablecc.syntax3.node.PCharacter;
import org.sablecc.sablecc.syntax3.node.PExpression;
import org.sablecc.sablecc.syntax3.node.Token;

public class ExpressionVerifier
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private final Map<PCharacter, Token> chars = new LinkedHashMap<PCharacter, Token>();

    private NormalExpression currentNormalExpression;

    public ExpressionVerifier(
            GlobalIndex globalIndex) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        this.globalIndex = globalIndex;
    }

    @Override
    public void inANormalNamedExpression(
            ANormalNamedExpression node) {

        this.currentNormalExpression = this.globalIndex.getExpression(node);
    }

    @Override
    public void outANormalNamedExpression(
            ANormalNamedExpression node) {

        this.currentNormalExpression = null;
    }

    @Override
    public void outAIntervalExponentExpression(
            AIntervalExponentExpression node) {

        BigInteger from = new BigInteger(node.getFrom().getText());
        BigInteger to = new BigInteger(node.getTo().getText());

        if (from.compareTo(to) > 0) {
            throw CompilerException.invalidInterval(node.getTwoDots(), node
                    .getFrom(), node.getTo());
        }
    }

    @Override
    public void outASeparatedIntervalExponentExpression(
            ASeparatedIntervalExponentExpression node) {

        BigInteger from = new BigInteger(node.getFrom().getText());
        BigInteger to = new BigInteger(node.getTo().getText());

        if (from.compareTo(to) > 0) {
            throw CompilerException.invalidInterval(node.getTwoDots(), node
                    .getFrom(), node.getTo());
        }
    }

    @Override
    public void outACharExpression(
            ACharExpression node) {

        this.globalIndex.addCharacter(node.getChar());
    }

    @Override
    public void outADecExpression(
            ADecExpression node) {

        this.globalIndex.addCharacter(node.getDecChar());
    }

    @Override
    public void outAHexExpression(
            AHexExpression node) {

        this.globalIndex.addCharacter(node.getHexChar());
    }

    @Override
    public void outAIntervalExpression(
            AIntervalExpression node) {

        Token fromToken = this.chars.get(node.getFrom());
        Token toToken = this.chars.get(node.getTo());

        BigInteger from = this.globalIndex.getCharacterValue(fromToken);
        BigInteger to = this.globalIndex.getCharacterValue(toToken);

        if (from.compareTo(to) > 0) {
            throw CompilerException.invalidInterval(node.getTwoDots(),
                    fromToken, toToken);
        }
    }

    @Override
    public void outACharCharacter(
            ACharCharacter node) {

        this.globalIndex.addCharacter(node.getChar());
        if (node.parent() instanceof PExpression) {
            this.chars.put(node, node.getChar());
        }
    }

    @Override
    public void outADecCharacter(
            ADecCharacter node) {

        this.globalIndex.addCharacter(node.getDecChar());
        if (node.parent() instanceof PExpression) {
            this.chars.put(node, node.getDecChar());
        }
    }

    @Override
    public void outAHexCharacter(
            AHexCharacter node) {

        this.globalIndex.addCharacter(node.getHexChar());
        if (node.parent() instanceof PExpression) {
            this.chars.put(node, node.getHexChar());
        }
    }

    @Override
    public void outANameExpression(
            ANameExpression node) {

        this.globalIndex.addResolution(node);
        this.currentNormalExpression.addDependency(this.globalIndex
                .getResolution(node));
    }

}
