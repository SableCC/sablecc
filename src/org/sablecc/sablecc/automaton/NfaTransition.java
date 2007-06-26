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

package org.sablecc.sablecc.automaton;

import org.sablecc.sablecc.alphabet.Symbol;
import org.sablecc.sablecc.exception.InternalException;

public final class NfaTransition<T extends Comparable<? super T>>
        implements Comparable<NfaTransition<T>> {

    private final NfaState<T> source;

    private final NfaState<T> destination;

    private final Symbol<T> symbol;

    private Integer hashCode;

    private String toString;

    private boolean isDeleted = false;

    public NfaTransition(
            NfaState<T> source,
            NfaState<T> destination,
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

    public NfaState<T> getDestination() {

        if (this.isDeleted) {
            throw new InternalException("transition is deleted");
        }

        return this.destination;
    }

    public NfaState<T> getSource() {

        if (this.isDeleted) {
            throw new InternalException("transition is deleted");
        }

        return this.source;
    }

    public Symbol<T> getSymbol() {

        if (this.isDeleted) {
            throw new InternalException("transition is deleted");
        }

        return this.symbol;
    }

    @Override
    public boolean equals(
            Object obj) {

        if (this.isDeleted) {
            throw new InternalException("transition is deleted");
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof NfaTransition)) {
            return false;
        }

        NfaTransition transition = (NfaTransition) obj;

        if (!this.source.equals(transition.source)) {
            return false;
        }

        if (!this.destination.equals(transition.destination)) {
            return false;
        }

        if (this.symbol == null) {
            return transition.symbol == null;
        }

        return this.symbol.equals(transition.symbol);
    }

    @Override
    public int hashCode() {

        if (this.isDeleted) {
            throw new InternalException("transition is deleted");
        }

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

        if (this.isDeleted) {
            throw new InternalException("transition is deleted");
        }

        if (this.toString == null) {
            this.toString = this.source + "->("
                    + (this.symbol == null ? "epsilon" : this.symbol) + ")->"
                    + this.destination;
        }

        return this.toString;
    }

    public int compareTo(
            NfaTransition<T> transition) {

        if (this.isDeleted) {
            throw new InternalException("transition is deleted");
        }

        int result = 0;

        result = this.source.getName().compareTo(transition.source.getName());

        if (result == 0) {
            if (this.symbol == null) {
                result = transition.symbol == null ? 0 : -1;
            }
            else if (transition.symbol == null) {
                result = 1;
            }
            else {
                result = this.symbol.compareTo(transition.symbol);
            }
        }

        if (result == 0) {
            result = this.destination.getName().compareTo(
                    transition.destination.getName());
        }

        return result;
    }

    public void delete() {

        if (this.isDeleted) {
            throw new InternalException("transition is deleted");
        }
        this.source.removeForwardTransition(this);
        this.destination.removeBackwardTransition(this);
        this.isDeleted = true;
    }
}
