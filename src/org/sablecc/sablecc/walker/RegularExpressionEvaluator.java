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
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.automaton.*;
import org.sablecc.sablecc.structure.*;
import org.sablecc.sablecc.syntax3.node.*;

public class RegularExpressionEvaluator
        extends TreeWalker {

    private final GlobalIndex globalIndex;

    private Map<PExpression, Automaton> expressionBuffer = new LinkedHashMap<PExpression, Automaton>();

    private Map<PCharacter, Bound> characterBuffer = new LinkedHashMap<PCharacter, Bound>();

    public static Automaton evaluateExpression(
            GlobalIndex globalIndex,
            PExpression expression) {

        RegularExpressionEvaluator evaluator = new RegularExpressionEvaluator(
                globalIndex);
        expression.apply(evaluator);
        return evaluator.retrieve(expression).minimal();
    }

    private RegularExpressionEvaluator(
            GlobalIndex globalIndex) {

        this.globalIndex = globalIndex;
    }

    private void save(
            PExpression node,
            Automaton automaton) {

        this.expressionBuffer.put(node, automaton);
    }

    private Automaton retrieve(
            PExpression node) {

        return this.expressionBuffer.remove(node);
    }

    private void save(
            PCharacter node,
            Bound bound) {

        this.characterBuffer.put(node, bound);
    }

    private Bound retrieve(
            PCharacter node) {

        return this.characterBuffer.remove(node);
    }

    @Override
    public void outAHexCharacter(
            AHexCharacter node) {

        save(node, new Bound(node.getHexChar().getText().substring(2), 16));
    }

    @Override
    public void outADecCharacter(
            ADecCharacter node) {

        save(node, new Bound(node.getDecChar().getText().substring(1)));
    }

    @Override
    public void outACharCharacter(
            ACharCharacter node) {

        String charText = node.getChar().getText();
        char c = charText.charAt(1);
        if (c == '\\') {
            c = charText.charAt(2);
        }

        save(node, new Bound(c));
    }

    @Override
    public void outAEndExpression(
            AEndExpression node) {

        save(node, Automaton.getEpsilonLookEnd());
    }

    @Override
    public void outAAnyExpression(
            AAnyExpression node) {

        save(node, Automaton.getSymbolLookAnyStarEnd(new Symbol(new Interval(
                Bound.MIN, Bound.MAX))));
    }

    @Override
    public void outAIntervalExpression(
            AIntervalExpression node) {

        save(node, Automaton.getSymbolLookAnyStarEnd(new Symbol(new Interval(
                retrieve(node.getFrom()), retrieve(node.getTo())))));
    }

    @Override
    public void outAHexExpression(
            AHexExpression node) {

        save(node, Automaton.getSymbolLookAnyStarEnd(new Symbol(node
                .getHexChar().getText().substring(2), 16)));
    }

    @Override
    public void outADecExpression(
            ADecExpression node) {

        save(node, Automaton.getSymbolLookAnyStarEnd(new Symbol(node
                .getDecChar().getText().substring(1))));
    }

    @Override
    public void outAEpsilonExpression(
            AEpsilonExpression node) {

        save(node, Automaton.getEpsilonLookAnyStarEnd());
    }

    @Override
    public void outACharExpression(
            ACharExpression node) {

        String charText = node.getChar().getText();
        char c = charText.charAt(1);
        if (c == '\\') {
            c = charText.charAt(2);
        }

        save(node, Automaton.getSymbolLookAnyStarEnd(new Symbol(c)));
    }

    @Override
    public void outAStringExpression(
            AStringExpression node) {

        String text = node.getString().getText();
        int length = text.length();

        text = text.substring(1, length - 1);
        length -= 2;

        if (length < 1) {
            throw new InternalException("invalid string");
        }

        Automaton automaton = null;

        int i = 0;
        while (i < length) {
            char c = text.charAt(i++);
            if (c == '\\') {
                c = text.charAt(i++);
            }

            if (automaton == null) {
                automaton = Automaton.getSymbolLookAnyStarEnd(new Symbol(c));
            }
            else {
                automaton = automaton.concat(Automaton
                        .getSymbolLookAnyStarEnd(new Symbol(c)));
            }
        }

        save(node, automaton);
    }

    @Override
    public void outANameExpression(
            ANameExpression node) {

        NormalExpression normalExpression = (NormalExpression) this.globalIndex
                .getParserResolution(node.getIdentifier());
        save(node, normalExpression.getAutomaton());
    }

    @Override
    public void outASeparatedAtLeastExpression(
            ASeparatedAtLeastExpression node) {

        Automaton base = retrieve(node.getBase());
        Automaton separator = retrieve(node.getSeparator());
        BigInteger number = this.globalIndex.getBigIntegerValue(node
                .getNumber());
        save(node, base.nOrMoreWithSeparator(separator, number));
    }

    @Override
    public void outAAtLeastExpression(
            AAtLeastExpression node) {

        Automaton expression = retrieve(node.getExpression());
        BigInteger number = this.globalIndex.getBigIntegerValue(node
                .getNumber());
        save(node, expression.nOrMore(number));
    }

    @Override
    public void outASeparatedIntervalExponentExpression(
            ASeparatedIntervalExponentExpression node) {

        Automaton base = retrieve(node.getBase());
        Automaton separator = retrieve(node.getSeparator());
        BigInteger from = this.globalIndex.getBigIntegerValue(node.getFrom());
        BigInteger to = this.globalIndex.getBigIntegerValue(node.getTo());
        save(node, base.nToMWithSeparator(separator, from, to));
    }

    @Override
    public void outAIntervalExponentExpression(
            AIntervalExponentExpression node) {

        Automaton expression = retrieve(node.getExpression());
        BigInteger from = this.globalIndex.getBigIntegerValue(node.getFrom());
        BigInteger to = this.globalIndex.getBigIntegerValue(node.getTo());
        save(node, expression.nToM(from, to));
    }

    @Override
    public void outASeparatedNumberExponentExpression(
            ASeparatedNumberExponentExpression node) {

        Automaton base = retrieve(node.getBase());
        Automaton separator = retrieve(node.getSeparator());
        BigInteger number = this.globalIndex.getBigIntegerValue(node
                .getNumber());
        save(node, base.nTimesWithSeparator(separator, number));
    }

    @Override
    public void outANumberExponentExpression(
            ANumberExponentExpression node) {

        Automaton expression = retrieve(node.getExpression());
        BigInteger number = this.globalIndex.getBigIntegerValue(node
                .getNumber());
        save(node, expression.nTimes(number));
    }

    @Override
    public void outASeparatedOneOrMoreExpression(
            ASeparatedOneOrMoreExpression node) {

        Automaton base = retrieve(node.getBase());
        Automaton separator = retrieve(node.getSeparator());
        save(node, base.oneOrMoreWithSeparator(separator));
    }

    @Override
    public void outAOneOrMoreExpression(
            AOneOrMoreExpression node) {

        Automaton expression = retrieve(node.getExpression());
        save(node, expression.oneOrMore());
    }

    @Override
    public void outASeparatedZeroOrMoreExpression(
            ASeparatedZeroOrMoreExpression node) {

        Automaton base = retrieve(node.getBase());
        Automaton separator = retrieve(node.getSeparator());
        save(node, base.zeroOrMoreWithSeparator(separator));
    }

    @Override
    public void outAZeroOrMoreExpression(
            AZeroOrMoreExpression node) {

        Automaton expression = retrieve(node.getExpression());
        save(node, expression.zeroOrMore());
    }

    @Override
    public void outAZeroOrOneExpression(
            AZeroOrOneExpression node) {

        Automaton expression = retrieve(node.getExpression());
        save(node, expression.zeroOrOne());
    }

    @Override
    public void outAAndExpression(
            AAndExpression node) {

        Automaton left = retrieve(node.getLeft());
        Automaton right = retrieve(node.getRight());
        save(node, left.and(right));
    }

    @Override
    public void outADifferenceExpression(
            ADifferenceExpression node) {

        Automaton left = retrieve(node.getLeft());
        Automaton right = retrieve(node.getRight());
        save(node, left.diff(right));
    }

    @Override
    public void outASubtractionExpression(
            ASubtractionExpression node) {

        Automaton left = retrieve(node.getLeft());
        Automaton right = retrieve(node.getRight());
        save(node, left.subtract(right));
    }

    @Override
    public void outALongestExpression(
            ALongestExpression node) {

        Automaton expression = retrieve(node.getExpression());
        save(node, expression.longest());
    }

    @Override
    public void outAShortestExpression(
            AShortestExpression node) {

        Automaton expression = retrieve(node.getExpression());
        save(node, expression.shortest());
    }

    @Override
    public void outALookNotExpression(
            ALookNotExpression node) {

        Automaton left = retrieve(node.getLeft());
        Automaton right = retrieve(node.getRight());
        save(node, left.lookNot(right));
    }

    @Override
    public void outALookExpression(
            ALookExpression node) {

        Automaton left = retrieve(node.getLeft());
        Automaton right = retrieve(node.getRight());
        save(node, left.look(right));
    }

    @Override
    public void outAConcatenationExpression(
            AConcatenationExpression node) {

        Automaton left = retrieve(node.getLeft());
        Automaton right = retrieve(node.getRight());
        save(node, left.concat(right));
    }

    @Override
    public void outAOrExpression(
            AOrExpression node) {

        Automaton left = retrieve(node.getLeft());
        Automaton right = retrieve(node.getRight());
        save(node, left.or(right));
    }

}
