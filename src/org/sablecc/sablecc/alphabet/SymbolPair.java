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

/**
 * A symbol pair is a pair <code>(x,y)</code> where each of <code>x</code>
 * and <code>y</code> is a symbol or <code>null</code>.
 */
final class SymbolPair<T extends Comparable<? super T>> {

    /** The first symbol of the pair, possibly <code>null</code>. */
    private final Symbol<T> symbol1;

    /** The second symbol of the pair, possibly <code>null</code>. */
    private final Symbol<T> symbol2;

    /** Cached hashcode. Is <code>null</code> when not yet computed. */
    private Integer hashCode;

    /**
     * Cached string representation. Is <code>null</code> when not yet
     * computed.
     */
    private String toString;

    /**
     * Constructs a symbol pair with two provided symbols.
     * 
     * @param symbol1
     *            the first symbol of the pair.
     * @param symbol2
     *            the second symbol of the pair.
     */
    SymbolPair(
            Symbol<T> symbol1,
            Symbol<T> symbol2) {

        this.symbol1 = symbol1;
        this.symbol2 = symbol2;
    }

    /**
     * Returns the first symbol of the pair.
     * 
     * @return the first symbol of the pair.
     */
    Symbol<T> getSymbol1() {

        return this.symbol1;
    }

    /**
     * Returns the second symbol of the pair.
     * 
     * @return the second symbol of the pair.
     */
    Symbol<T> getSymbol2() {

        return this.symbol2;
    }

    /**
     * Returns whether this instance is equal to the provided object. They are
     * equal if they contain identical symbols.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if this symbol pair and the object are equal;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(
            Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SymbolPair)) {
            return false;
        }

        SymbolPair symbolPair = (SymbolPair) obj;

        if (this.symbol1 == null && symbolPair.symbol1 != null) {
            return false;
        }

        if (this.symbol2 == null && symbolPair.symbol2 != null) {
            return false;
        }

        return (this.symbol1 == null || this.symbol1.equals(symbolPair.symbol1))
                && (this.symbol2 == null || this.symbol2
                        .equals(symbolPair.symbol2));
    }

    /**
     * Returns the hash code of this symbol pair.
     * 
     * @return the hash code.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {

            int hashCode = 0;

            if (this.symbol1 != null) {
                hashCode += this.symbol1.hashCode();
            }

            if (this.symbol2 != null) {
                hashCode += 17 * this.symbol2.hashCode();
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this symbol pair.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "(" + this.symbol1 + "," + this.symbol2 + ")";
        }

        return this.toString;
    }
}
