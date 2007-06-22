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

package org.sablecc.sablecc.alphabet;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class AlphabetMergeResultTest {

    // Integer
    private AlphabetMergeResult<Integer> resultMergeInt;

    private Alphabet<Integer> oldAlphabetInt1;

    private Alphabet<Integer> oldAlphabetInt2;

    private AdjacencyRealm<Integer> integerAdjacencyRealm = Realms.getInteger();

    private Collection<Interval<Integer>> intervalsInt = new LinkedList<Interval<Integer>>();

    private Symbol<Integer> symbolInt;

    private SortedSet<Symbol<Integer>> symbolsInt1 = new TreeSet<Symbol<Integer>>();

    private SortedSet<Symbol<Integer>> symbolsInt2 = new TreeSet<Symbol<Integer>>();

    private SortedSet<Symbol<Integer>> newSymbolsInt = new TreeSet<Symbol<Integer>>();

    // BigInteger
    private AlphabetMergeResult<BigInteger> resultMergeBig;

    private Alphabet<BigInteger> oldAlphabetBig1;

    private Alphabet<BigInteger> oldAlphabetBig2;

    private AdjacencyRealm<BigInteger> bigIntegerAdjacencyRealm = Realms
            .getBigInteger();

    private Collection<Interval<BigInteger>> intervalsBig = new LinkedList<Interval<BigInteger>>();

    private Symbol<BigInteger> symbolBig;

    private SortedSet<Symbol<BigInteger>> symbolsBig1 = new TreeSet<Symbol<BigInteger>>();

    private SortedSet<Symbol<BigInteger>> symbolsBig2 = new TreeSet<Symbol<BigInteger>>();

    private SortedSet<Symbol<BigInteger>> newSymbolsBig = new TreeSet<Symbol<BigInteger>>();

    @Before
    public void setUp()
            throws Exception {

        // For Integer
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(0, 10,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(20, 30,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(40, 500,
                this.integerAdjacencyRealm));
        this.symbolInt = new Symbol<Integer>(this.intervalsInt);

        this.symbolsInt1.add(this.symbolInt);
        this.oldAlphabetInt1 = new Alphabet<Integer>(this.symbolsInt1);

        this.newSymbolsInt.add(new Symbol<Integer>(this.intervalsInt));

        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(100, 110,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(120, 130,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(140, 150,
                this.integerAdjacencyRealm));
        this.symbolInt = new Symbol<Integer>(this.intervalsInt);
        this.symbolsInt2.add(this.symbolInt);
        this.oldAlphabetInt2 = new Alphabet<Integer>(this.symbolsInt2);

        this.newSymbolsInt.add(new Symbol<Integer>(this.intervalsInt));

        // For BigInteger
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(BigInteger.ZERO,
                BigInteger.TEN, this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("50"),
                new BigInteger("100"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("200"),
                new BigInteger("400"), this.bigIntegerAdjacencyRealm));

        this.symbolBig = new Symbol<BigInteger>(this.intervalsBig);

        this.symbolsBig1.add(this.symbolBig);
        this.oldAlphabetBig1 = new Alphabet<BigInteger>(this.symbolsBig1);

        this.newSymbolsBig.add(new Symbol<BigInteger>(this.intervalsBig));

        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("600"),
                new BigInteger("800"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("150"),
                new BigInteger("170"), this.bigIntegerAdjacencyRealm));

        this.symbolBig = new Symbol<BigInteger>(this.intervalsBig);

        this.symbolsBig2.add(this.symbolBig);
        this.oldAlphabetBig2 = new Alphabet<BigInteger>(this.symbolsBig2);

        this.newSymbolsBig.add(new Symbol<BigInteger>(this.intervalsBig));
    }

    @Test
    public void testGetNewSymbols() {

        // With Integer
        this.resultMergeInt = this.oldAlphabetInt1
                .mergeWith(this.oldAlphabetInt2);

        this.newSymbolsInt = this.resultMergeInt.getNewSymbols(this.symbolInt,
                this.oldAlphabetInt2);
        assertEquals("The newSymbols and the old symbols should be equals.",
                this.symbolsInt2, this.newSymbolsInt);

        // With BigInteger
        this.resultMergeBig = this.oldAlphabetBig1
                .mergeWith(this.oldAlphabetBig2);

        this.newSymbolsBig = this.resultMergeBig.getNewSymbols(this.symbolBig,
                this.oldAlphabetBig2);
        assertEquals("The newSymbols and the old symbols should be equals.",
                this.symbolsBig2, this.newSymbolsBig);
    }

}
