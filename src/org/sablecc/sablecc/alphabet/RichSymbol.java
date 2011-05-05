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

import org.sablecc.exception.*;

/**
 * A rich symbol is a symbol augmented with lookahead information.
 */
public class RichSymbol
        implements Comparable<RichSymbol> {

    /**
     * The end lookahead rich symbol.
     */
    public static final RichSymbol END = new RichSymbol(true);

    /**
     * The symbol of this rich symbol.
     */
    private final Symbol symbol;

    /**
     * The lookahead status of this rich symbol.
     */
    private final boolean isLookahead;

    /**
     * The cached hash code of this rich symbol. It is <code>null</code> when
     * not yet computed.
     */
    private Integer hashCode;

    /**
     * The cached string representation of this rich symbol. It is
     * <code>null</code> when not yet computed.
     */
    private String toString;

    /**
     * Constructs a constant rich symbol. Serves to construct END.
     */
    private RichSymbol(
            boolean isLookahead) {

        this.symbol = null;
        this.isLookahead = isLookahead;
    }

    /**
     * Constructs a rich symbol with the provided symbol and lookahead status.
     */
    RichSymbol(
            Symbol symbol,
            boolean isLookahead) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        this.symbol = symbol;
        this.isLookahead = isLookahead;
    }

    /**
     * Returns the symbol of this rich symbol.
     */
    public Symbol getSymbol() {

        return this.symbol;
    }

    /**
     * Returns the lookahead status of this rich symbol.
     */
    public boolean isLookahead() {

        return this.isLookahead;
    }

    /**
     * Returns <code>true</code> when the provided object is equal to this rich
     * symbol.
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

        RichSymbol richSymbol = (RichSymbol) obj;

        if (this.isLookahead != richSymbol.isLookahead) {
            return false;
        }

        return this.symbol.equals(richSymbol.symbol);
    }

    /**
     * Returns the hash code of this rich symbol.
     */
    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            int hashCode = this.symbol.hashCode();

            if (this.isLookahead) {
                hashCode *= 109;
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    /**
     * Returns the string representation of this rich symbol.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            if (this.symbol != null) {
                sb.append("{");
                sb.append(this.isLookahead ? "lookahead" : "normal");
                sb.append(",");
                sb.append(this.symbol);
                sb.append("}");
            }
            else {
                sb.append("{lookahead,END}");
            }

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Compares this rich symbol to the provided one.
     */
    @Override
    public int compareTo(
            RichSymbol richSymbol) {

        if (richSymbol == null) {
            throw new InternalException("richSymbol may not be null");
        }

        if (this.isLookahead != richSymbol.isLookahead) {
            return this.isLookahead ? 1 : -1;
        }

        if (this.symbol == null) {
            return richSymbol.symbol == null ? 0 : 1;
        }

        if (richSymbol.symbol == null) {
            return -1;
        }

        return this.symbol.compareTo(richSymbol.symbol);
    }

    /**
     * Returns the minimum of two rich symbols.
     */
    public static RichSymbol min(
            RichSymbol richSymbol1,
            RichSymbol richSymbol2) {

        if (richSymbol1 == null) {
            throw new InternalException("richSymbol1 may not be null");
        }

        if (richSymbol2 == null) {
            throw new InternalException("richSymbol2 may not be null");
        }

        if (richSymbol1.compareTo(richSymbol2) <= 0) {
            return richSymbol1;
        }

        return richSymbol2;
    }

    /**
     * Returns the maximum of two rich symbols.
     */
    public static RichSymbol max(
            RichSymbol richSymbol1,
            RichSymbol richSymbol2) {

        if (richSymbol1 == null) {
            throw new InternalException("richSymbol1 may not be null");
        }

        if (richSymbol2 == null) {
            throw new InternalException("richSymbol2 may not be null");
        }

        if (richSymbol1.compareTo(richSymbol2) >= 0) {
            return richSymbol1;
        }

        return richSymbol2;
    }
}
