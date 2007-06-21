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
 * This class represents a pair of symbols.
 * 
 * A symbol pair is defined by two distinct symbols.
 */
class SymbolPair<T extends Comparable<? super T>> {

    /** The first unmodifiable symbol of the pair */
    private final Symbol<T> symbol1;

    /** The second unmodifiable symbol of the pair */
    private final Symbol<T> symbol2;

    /** Cached hashcode. Is <code>null</code> when not yet computed. */
    private Integer hashCode;

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
     * Compares this symbol pair with an object for equality. Returns
     * <code>true</code> if the object is a symbol pair and if it as the same
     * two symbols as those of this instance.
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
     * Returns a hash code value for this object.
     * 
     * @return a hash code for this object.
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
}
