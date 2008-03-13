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

import org.sablecc.sablecc.exception.InternalException;

/**
 * A symbol pair is a pair <code>(x,y)</code> where each of <code>x</code>
 * and <code>y</code> is a symbol or <code>null</code>, but not both.
 */
class SymbolPair {

    /** The first symbol of the pair, possibly <code>null</code>. */
    private final Symbol symbol1;

    /** The second symbol of the pair, possibly <code>null</code>. */
    private final Symbol symbol2;

    /**
     * The cached hashcode of this symbol pair. It is <code>null</code> when
     * not yet computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this symbol pair. It is
     * <code>null</code> when not yet computed.
     */
    private String toString;

    /**
     * Constructs a symbol pair with the provided symbols.
     */
    SymbolPair(
            Symbol symbol1,
            Symbol symbol2) {

        if (symbol1 == null && symbol2 == null) {
            throw new InternalException(
                    "symbol1 and symbol2 may not both be null");
        }

        this.symbol1 = symbol1;
        this.symbol2 = symbol2;
    }

    /**
     * Returns the first symbol of the pair.
     */
    Symbol getSymbol1() {

        return this.symbol1;
    }

    /**
     * Returns the second symbol of the pair.
     */
    Symbol getSymbol2() {

        return this.symbol2;
    }

    /**
     * Returns true if the provided object is equal to this symbol pair.
     */
    @Override
    public boolean equals(
            Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        SymbolPair symbolPair = (SymbolPair) obj;

        if ((this.symbol1 == null || symbolPair.symbol1 == null)
                && this.symbol1 != symbolPair.symbol1) {
            return false;
        }

        if ((this.symbol2 == null || symbolPair.symbol2 == null)
                && this.symbol2 != symbolPair.symbol2) {
            return false;
        }

        return (this.symbol1 == null || this.symbol1.equals(symbolPair.symbol1))
                && (this.symbol2 == null || this.symbol2
                        .equals(symbolPair.symbol2));
    }

    /**
     * Returns the hash code of this symbol pair.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {

            int hashCode = 0;

            if (this.symbol1 != null) {
                hashCode += 109 * this.symbol1.hashCode();
            }

            if (this.symbol2 != null) {
                hashCode += 113 * this.symbol2.hashCode();
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this symbol pair.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            if (this.symbol1 != null) {
                sb.append(this.symbol1);
            }
            sb.append(",");
            if (this.symbol2 != null) {
                sb.append(this.symbol2);
            }
            sb.append(")");
            this.toString = sb.toString();
        }

        return this.toString;
    }
}
