/* This file is part of SableCC (http://sablecc.org/).
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

package org.sablecc.sablecc.automaton;

import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

class Transition<T extends Comparable<? super T>> {

    private State<T> source;

    private State<T> destination;

    private Symbol<T> symbol;

    private Integer hashCode;

    private String toString;

    Transition(
            State<T> source,
            State<T> destination,
            Symbol<T> symbol) {

        if (source == null) {
            throw new InternalException("source may not be null");
        }
        if (destination == null) {
            throw new InternalException("destination may not be null");
        }

        this.source = source;
        this.destination = destination;
        this.symbol = symbol;

        source.addForwardTransition(this);
        destination.addBackwardTransition(this);
    }

    State<T> getDestination() {

        return this.destination;
    }

    State<T> getSource() {

        return this.source;
    }

    Symbol<T> getSymbol() {

        return this.symbol;
    }

    @Override
    public boolean equals(
            Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Transition)) {
            return false;
        }

        Transition transition = (Transition) obj;

        if (!this.source.equals(transition.source)) {
            return false;
        }

        if (!this.destination.equals(transition.destination)) {
            return false;
        }

        if (this.symbol == null && transition.symbol != null) {
            return false;
        }

        return this.symbol.equals(transition.symbol);
    }

    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            int hashCode = this.source.hashCode();

            hashCode += hashCode * 23 + this.destination.hashCode();

            if (this.symbol != null) {
                hashCode += hashCode * 23 + this.symbol.hashCode();
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = this.source + "-(" + this.symbol + ")->"
                    + this.destination;
        }

        return this.toString;
    }
}
