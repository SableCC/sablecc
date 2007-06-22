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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.exception.InternalException;

public class AlphabetTest {

    private Alphabet<Integer> alphaInt;

    private Collection<Symbol<Integer>> symbolsInt = new LinkedList<Symbol<Integer>>();

    private Collection<Interval<Integer>> intervalsInt = new LinkedList<Interval<Integer>>();

    private Symbol<Integer> firstSymbolInt;

    private Symbol<Integer> secondSymbolInt;

    private AdjacencyRealm<Integer> integerAdjacencyRealm = Realms.getInteger();

    private Alphabet<BigInteger> alphaBig;

    private Collection<Symbol<BigInteger>> symbolsBig = new LinkedList<Symbol<BigInteger>>();

    private Collection<Interval<BigInteger>> intervalsBig = new LinkedList<Interval<BigInteger>>();

    private Symbol<BigInteger> firstSymbolBig;

    private Symbol<BigInteger> secondSymbolBig;

    private AdjacencyRealm<BigInteger> bigIntegerAdjacencyRealm = Realms
            .getBigInteger();

    @Before
    public void setUp()
            throws Exception {

        // For Integer
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(10, 20,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(50, 100,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(200, 400,
                this.integerAdjacencyRealm));

        this.firstSymbolInt = new Symbol<Integer>(this.intervalsInt);

        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(30, 40,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(450, 500,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(600, 800,
                this.integerAdjacencyRealm));

        this.secondSymbolInt = new Symbol<Integer>(this.intervalsInt);

        this.symbolsInt.clear();
        this.symbolsInt.add(this.firstSymbolInt);
        this.symbolsInt.add(this.secondSymbolInt);
        this.alphaInt = new Alphabet<Integer>(this.symbolsInt);

        // For BigInteger
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(BigInteger.ZERO,
                BigInteger.TEN, this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("50"),
                new BigInteger("100"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("200"),
                new BigInteger("400"), this.bigIntegerAdjacencyRealm));

        this.firstSymbolBig = new Symbol<BigInteger>(this.intervalsBig);

        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("600"),
                new BigInteger("800"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("150"),
                new BigInteger("170"), this.bigIntegerAdjacencyRealm));

        this.secondSymbolBig = new Symbol<BigInteger>(this.intervalsBig);

        this.symbolsBig.clear();
        this.symbolsBig.add(this.firstSymbolBig);
        this.symbolsBig.add(this.secondSymbolBig);
        this.alphaBig = new Alphabet<BigInteger>(this.symbolsBig);
    }

    @Test
    public void testHashCode() {

        // With Integer
        Alphabet<Integer> sameAlphaInt = new Alphabet<Integer>(this.symbolsInt);
        assertEquals(
                "Two instance of the same alphabet should have the same hashCode.",
                this.alphaInt.hashCode(), sameAlphaInt.hashCode());

        // With BigInteger
        Alphabet<BigInteger> sameAlphaBig = new Alphabet<BigInteger>(
                this.symbolsBig);

        assertEquals(
                "Two instance of the same alphabet should have the same hashCode.",
                this.alphaBig.hashCode(), sameAlphaBig.hashCode());
    }

    @SuppressWarnings("unused")
    @Test
    public void testAlphabetCollectionOfSymbolOfT() {

        // Case with null Collection.
        Collection<Symbol<Integer>> nullCollection = null;
        try {

            Alphabet<Integer> alphabetNullCollection = new Alphabet<Integer>(
                    nullCollection);
            fail("Symbols may not be null.");
        }
        catch (InternalException e) {
            // exception
        }

        // Case with overlapping intervals with Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(15, 70,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(120, 150,
                this.integerAdjacencyRealm));
        Symbol<Integer> crossingIntervalSymbolInt = new Symbol<Integer>(
                this.intervalsInt);

        Collection<Symbol<Integer>> symbolsOverLappingInt = new LinkedList<Symbol<Integer>>();
        symbolsOverLappingInt.add(this.firstSymbolInt);
        symbolsOverLappingInt.add(crossingIntervalSymbolInt);

        try {
            this.alphaInt = new Alphabet<Integer>(symbolsOverLappingInt);
            fail("Distinct symbols may not have overlapping intervals.");
        }
        catch (InternalException e) {
            // expected
        }

        // Case with overlapping intervals with BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("5"),
                new BigInteger("70"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("160"),
                new BigInteger("700"), this.bigIntegerAdjacencyRealm));
        Symbol<BigInteger> crossingIntervalSymbolBig = new Symbol<BigInteger>(
                this.intervalsBig);

        Collection<Symbol<BigInteger>> symbolsOverLappingBig = new LinkedList<Symbol<BigInteger>>();
        symbolsOverLappingBig.add(this.firstSymbolBig);
        symbolsOverLappingBig.add(crossingIntervalSymbolBig);

        try {
            this.alphaBig = new Alphabet<BigInteger>(symbolsOverLappingBig);
            fail("Distinct symbols may not have overlapping intervals.");
        }
        catch (InternalException e) {
            // expected
        }

        // Typical case with Integer.
        assertTrue("alphaInt should contains firstSymbolInt. ", this.alphaInt
                .getSymbols().contains(this.firstSymbolInt));
        assertTrue("alphaInt should contains secondSymbolInt. ", this.alphaInt
                .getSymbols().contains(this.secondSymbolInt));

        // Typical case with BigInteger.
        assertTrue("alphaBig should contains firstSymbolBig. ", this.alphaBig
                .getSymbols().contains(this.firstSymbolBig));
        assertTrue("alphaBig should contains secondSymbolBig. ", this.alphaBig
                .getSymbols().contains(this.secondSymbolBig));
    }

    @Test
    public void testAlphabetSymbolOfT() {

        // Case with null Symbol.
        Symbol<Integer> nullSymbol = null;
        try {
            this.alphaInt = new Alphabet<Integer>(nullSymbol);
            fail("symbol may not be null");
        }
        catch (InternalException e) {
            // expected
        }
        // Typical Case with Integer.
        this.alphaInt = new Alphabet<Integer>(this.firstSymbolInt);

        assertTrue("alphaInt should contains firstSymbolInt.", this.alphaInt
                .getSymbols().contains(this.firstSymbolInt));

        // Typical Case with BigInteger.
        this.alphaBig = new Alphabet<BigInteger>(this.firstSymbolBig);

        assertTrue("alphaBig should contains firstSymbolBig.", this.alphaBig
                .getSymbols().contains(this.firstSymbolBig));
    }

    @Test
    public void testAlphabetIntervalOfT() {

        // Case with null Interval.
        Interval<Integer> nullInterval = null;
        try {
            this.alphaInt = new Alphabet<Integer>(nullInterval);
            fail("interval may not be null");
        }
        catch (InternalException e) {
            // expected
        }

        // Typical case with Integer.
        this.alphaInt = new Alphabet<Integer>(new Interval<Integer>(10, 20,
                this.integerAdjacencyRealm));

        // Typical case with BigInteger.
        this.alphaBig = new Alphabet<BigInteger>(new Interval<BigInteger>(
                new BigInteger("10"), new BigInteger("20"),
                this.bigIntegerAdjacencyRealm));
    }

    @Test
    public void testEqualsObject() {

        // Case with null Object.
        assertFalse("An Alphabet and a null Object should not be equals.",
                this.alphaInt.equals(null));

        // Case with not an instance of Alpabet.
        assertFalse(
                "A symbol and an object with instance other than Alphabet should not be equals.",
                this.alphaInt.equals(2));

        // Case with same alphabets using Integer.
        Alphabet<Integer> sameAlphaInt = new Alphabet<Integer>(this.symbolsInt);

        assertTrue("The two alphabets should be equals", this.alphaInt
                .equals(sameAlphaInt));

        // Case with same alphabets using BigInteger.
        Alphabet<BigInteger> sameAlphaBig = new Alphabet<BigInteger>(
                this.symbolsBig);

        assertTrue("The two alphabets should be equals", this.alphaBig
                .equals(sameAlphaBig));

        // Case with different alphabets using Integer
        Alphabet<Integer> differentAlphaInt = new Alphabet<Integer>(
                new Interval<Integer>(999, this.integerAdjacencyRealm));

        assertFalse("The two alphabets should not be equals", this.alphaInt
                .equals(differentAlphaInt));

        // Case with different alphabets using BigInteger
        Alphabet<BigInteger> differentAlphaBig = new Alphabet<BigInteger>(
                new Interval<BigInteger>(new BigInteger("999"),
                        this.bigIntegerAdjacencyRealm));

        assertFalse("The two alphabets should not be equals", this.alphaBig
                .equals(differentAlphaBig));

    }

    @Test
    public void testCompareTo() {

        // Case with smaller alphabets using Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(0, 10,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(30, 50,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(100, 200,
                this.integerAdjacencyRealm));
        Alphabet<Integer> smallerAlphaInt = new Alphabet<Integer>(
                new Symbol<Integer>(this.intervalsInt));

        assertTrue("smallerAlphaInt should be smaller.", smallerAlphaInt
                .compareTo(this.alphaInt) < 0);

        // Case with smaller alphabets using BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(BigInteger.ZERO,
                new BigInteger("5"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("50"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("100"),
                new BigInteger("200"), this.bigIntegerAdjacencyRealm));
        Alphabet<BigInteger> smallerAlphaBig = new Alphabet<BigInteger>(
                new Symbol<BigInteger>(this.intervalsBig));

        assertTrue("smallerAlphaBig should be smaller.", smallerAlphaBig
                .compareTo(this.alphaBig) < 0);

        // Case with greater alphabets using Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(100, 200, Realms
                .getInteger()));
        this.intervalsInt.add(new Interval<Integer>(500, 1000, Realms
                .getInteger()));
        this.intervalsInt.add(new Interval<Integer>(2000, 4000, Realms
                .getInteger()));
        Alphabet<Integer> greaterAlphaInt = new Alphabet<Integer>(
                new Symbol<Integer>(this.intervalsInt));

        assertTrue("greaterAlphaInt should be greater", greaterAlphaInt
                .compareTo(this.alphaInt) > 0);

        // Case with greater alphabets using BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("120"),
                new BigInteger("180"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("500"),
                new BigInteger("99999"), this.bigIntegerAdjacencyRealm));
        Alphabet<BigInteger> greaterAlphaBig = new Alphabet<BigInteger>(
                new Symbol<BigInteger>(this.intervalsBig));

        assertTrue("greaterAlphaBig should be greater", greaterAlphaBig
                .compareTo(this.alphaBig) > 0);

        // Case with equals alphabets using Integer.
        Alphabet<Integer> sameAlphaInt = new Alphabet<Integer>(this.symbolsInt);

        assertTrue("The two alphabets should be equals", this.alphaInt
                .equals(sameAlphaInt));

        // Case with equals alphabets using BigInteger.
        Alphabet<BigInteger> sameAlphaBig = new Alphabet<BigInteger>(
                this.symbolsBig);

        assertTrue("The two alphabets should be equals", this.alphaBig
                .equals(sameAlphaBig));

    }

    @Test
    public void testMerge() {

        // With Integer
        Alphabet<Integer> firstPartAlphabetInt;
        Alphabet<Integer> secondPartAlphabetInt;

        firstPartAlphabetInt = new Alphabet<Integer>(this.firstSymbolInt);
        secondPartAlphabetInt = new Alphabet<Integer>(this.secondSymbolInt);
        AlphabetMergeResult<Integer> mergeResultInt = firstPartAlphabetInt
                .mergeWith(secondPartAlphabetInt);

        assertEquals("The merge result should equals the complete alphabet.",
                this.alphaInt.toString(), mergeResultInt.getNewAlphabet()
                        .toString());

        // With BigInteger
        Alphabet<BigInteger> firstPartAlphabetBig;
        Alphabet<BigInteger> secondPartAlphabetBig;

        firstPartAlphabetBig = new Alphabet<BigInteger>(this.firstSymbolBig);
        secondPartAlphabetBig = new Alphabet<BigInteger>(this.secondSymbolBig);
        AlphabetMergeResult<BigInteger> mergeResultBig = firstPartAlphabetBig
                .mergeWith(secondPartAlphabetBig);

        assertEquals("The merge result should equals the complete alphabet.",
                this.alphaBig.toString(), mergeResultBig.getNewAlphabet()
                        .toString());
    }

    @Test
    public void testMin() {

        // Typical test with Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(0, 10,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(30, 50,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(100, 200, Realms
                .getInteger()));
        Alphabet<Integer> smallerAlphaInt = new Alphabet<Integer>(
                new Symbol<Integer>(this.intervalsInt));

        assertEquals("smallerAlphaInt should be the minimum.", smallerAlphaInt,
                Alphabet.min(this.alphaInt, smallerAlphaInt));

        // Typical test with BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(BigInteger.ZERO,
                new BigInteger("5"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("50"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("100"),
                new BigInteger("200"), this.bigIntegerAdjacencyRealm));
        Alphabet<BigInteger> smallerAlphaBig = new Alphabet<BigInteger>(
                new Symbol<BigInteger>(this.intervalsBig));

        assertEquals("smallerAlphaBig should be the minimum.", smallerAlphaBig,
                Alphabet.min(this.alphaBig, smallerAlphaBig));
    }

    @Test
    public void testMax() {

        // Typical test with Integer.
        this.intervalsInt.clear();
        this.intervalsInt.add(new Interval<Integer>(100, 200,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(500, 1000,
                this.integerAdjacencyRealm));
        this.intervalsInt.add(new Interval<Integer>(2000, 4000,
                this.integerAdjacencyRealm));
        Alphabet<Integer> greaterAlphaInt = new Alphabet<Integer>(
                new Symbol<Integer>(this.intervalsInt));

        assertEquals("greaterAlphaInt should be the maximum", greaterAlphaInt,
                Alphabet.max(this.alphaInt, greaterAlphaInt));

        // Typical test with BigInteger.
        this.intervalsBig.clear();
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("30"),
                new BigInteger("40"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("120"),
                new BigInteger("180"), this.bigIntegerAdjacencyRealm));
        this.intervalsBig.add(new Interval<BigInteger>(new BigInteger("500"),
                new BigInteger("99999"), this.bigIntegerAdjacencyRealm));
        Alphabet<BigInteger> greaterAlphaBig = new Alphabet<BigInteger>(
                new Symbol<BigInteger>(this.intervalsBig));

        assertEquals("greaterAlphaBig should be the maximum", greaterAlphaBig,
                Alphabet.max(this.alphaBig, greaterAlphaBig));
    }

}
