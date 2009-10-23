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

import java.math.*;
import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.structure.*;
import org.sablecc.sablecc.syntax3.node.*;

public class ExpressionVerifier
        extends TreeWalker {

    private final GlobalIndex globalIndex;

    private NormalExpression currentNormalExpression;

    private final Map<PCharacter, Token> characterTokens = new LinkedHashMap<PCharacter, Token>();

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

        NormalExpression normalExpression = this.globalIndex
                .getExpression(node);

        this.currentNormalExpression = normalExpression;
    }

    @Override
    public void outANormalNamedExpression(
            ANormalNamedExpression node) {

        this.currentNormalExpression = null;
    }

    @Override
    public void outAIntervalExponentExpression(
            AIntervalExponentExpression node) {

        BigInteger from = this.globalIndex.getBigIntegerValue(node.getFrom());
        BigInteger to = this.globalIndex.getBigIntegerValue(node.getTo());

        if (from.compareTo(to) > 0) {
            throw CompilerException.invalidInterval(node.getTwoDots(), node
                    .getFrom(), node.getTo());
        }
    }

    @Override
    public void outASeparatedIntervalExponentExpression(
            ASeparatedIntervalExponentExpression node) {

        BigInteger from = this.globalIndex.getBigIntegerValue(node.getFrom());
        BigInteger to = this.globalIndex.getBigIntegerValue(node.getTo());

        if (from.compareTo(to) > 0) {
            throw CompilerException.invalidInterval(node.getTwoDots(), node
                    .getFrom(), node.getTo());
        }
    }

    @Override
    public void outACharCharacter(
            ACharCharacter node) {

        String charText = node.getChar().getText();
        char c = charText.charAt(1);
        if (c == '\\') {
            c = charText.charAt(2);
        }
        BigInteger value = new BigInteger(Integer.toString(c));
        this.globalIndex.setCharacterValue(node, value);
        this.characterTokens.put(node, node.getChar());
    }

    @Override
    public void outADecCharacter(
            ADecCharacter node) {

        BigInteger value = new BigInteger(node.getDecChar().getText()
                .substring(1));
        this.globalIndex.setCharacterValue(node, value);
        this.characterTokens.put(node, node.getDecChar());
    }

    @Override
    public void outAHexCharacter(
            AHexCharacter node) {

        BigInteger value = new BigInteger(node.getHexChar().getText()
                .substring(2), 16);
        this.globalIndex.setCharacterValue(node, value);
        this.characterTokens.put(node, node.getHexChar());
    }

    @Override
    public void outAIntervalExpression(
            AIntervalExpression node) {

        BigInteger from = this.globalIndex.getCharacterValue(node.getFrom());
        BigInteger to = this.globalIndex.getCharacterValue(node.getTo());

        if (from.compareTo(to) > 0) {
            throw CompilerException.invalidInterval(node.getTwoDots(),
                    this.characterTokens.get(node.getFrom()),
                    this.characterTokens.get(node.getTo()));
        }
    }

    @Override
    public void outANameExpression(
            ANameExpression node) {

        TIdentifier identifier = node.getIdentifier();
        NameUnit nameUnit = this.globalIndex.getParserResolution(identifier);

        if (!(nameUnit instanceof NormalExpression)) {
            throw CompilerException.invalidReference(identifier);
        }

        NormalExpression normalExpression = (NormalExpression) nameUnit;

        this.currentNormalExpression.addDependency(normalExpression);
    }

}
