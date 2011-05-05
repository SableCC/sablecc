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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.alphabet.*;

/**
 * An instance of this class represents a state in a finite automaton.
 */
public final class State
        implements Comparable<State> {

    /**
     * An empty set of states.
     */
    private static final SortedSet<State> emptyStateSet = new TreeSet<State>();

    /**
     * The automaton of this state.
     */
    private final Automaton automaton;

    /**
     * The ID of this state.
     */
    private final int id;

    /**
     * The transitions of this state.
     */
    private SortedMap<RichSymbol, SortedSet<State>> transitions;

    /**
     * The marker of this state.
     */
    private Marker marker;

    /**
     * The acceptations of this state.
     */
    private SortedSet<Acceptation> acceptations = new TreeSet<Acceptation>();

    /**
     * The stability status of this state.
     */
    private boolean isStable;

    /**
     * The cached string representation of this state. It is <code>null</code>
     * when not yet computed.
     */
    private String toString;

    /**
     * The epsilon reach of this state.
     */
    private SortedSet<State> epsilonReach;

    private Integer discoveryTime;

    private Integer lowestReachableDiscoveryTime;

    private boolean isOnStack = false;

    private Boolean isCyclic;

    /**
     * Constructs a state associated with the provided automaton.
     */
    State(
            Automaton automaton) {

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        this.automaton = automaton;

        this.id = automaton.getNextStateId();
        automaton.addState(this);

        this.transitions = new TreeMap<RichSymbol, SortedSet<State>>(
                Automaton.getRichSymbolComparator());

        this.isStable = false;
    }

    /**
     * Returns the automaton of this state.
     */
    public Automaton getAutomaton() {

        return this.automaton;
    }

    /**
     * Returns the ID of this state.
     */
    public int getId() {

        return this.id;
    }

    /**
     * Returns the transitions of this state.
     */
    public SortedMap<RichSymbol, SortedSet<State>> getTransitions() {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        return this.transitions;
    }

    /**
     * Returns the target states of transitions from this state on the given
     * rich symbol.
     */
    public SortedSet<State> getTargets(
            RichSymbol richSymbol) {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        if (richSymbol != null
                && richSymbol != RichSymbol.END
                && !this.automaton.getAlphabet().getSymbols()
                        .contains(richSymbol.getSymbol())) {
            throw new InternalException("invalid symbol");
        }

        SortedSet<State> targets = this.transitions.get(richSymbol);

        if (targets == null) {
            targets = State.emptyStateSet;
        }

        return targets;
    }

    /**
     * Returns the target state of transition from this state on the given rich
     * symbol.
     */
    public State getSingleTarget(
            RichSymbol richSymbol) {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        if (!this.automaton.isDeterministic()) {
            throw new InternalException("the automaton is not deterministic");
        }

        if (richSymbol != null
                && richSymbol != RichSymbol.END
                && !this.automaton.getAlphabet().getSymbols()
                        .contains(richSymbol.getSymbol())) {
            throw new InternalException("invalid symbol");
        }

        SortedSet<State> targets = this.transitions.get(richSymbol);

        if (targets == null) {
            return null;
        }

        return targets.first();
    }

    /**
     * Returns the marker of this state.
     */
    public Marker getMarker() {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        return this.marker;
    }

    /**
     * Returns <code>true</code> when this state is an accept state.
     */
    public boolean isAcceptState() {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        return this.acceptations.size() > 0;
    }

    /**
     * Returns the acceptations of this state.
     */
    public SortedSet<Acceptation> getAcceptations() {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        return this.acceptations;
    }

    /**
     * Returns the string representation of this state.
     */
    @Override
    public String toString() {

        if (this.toString == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("state_");
            sb.append(this.id);

            if (this.marker != null) {
                sb.append("(");
                sb.append(this.marker);
                sb.append(")");
            }

            if (this.acceptations.size() > 0) {
                boolean first = true;
                sb.append("[");
                for (Acceptation acceptation : this.acceptations) {
                    if (first) {
                        first = false;
                    }
                    else {
                        sb.append(",");
                    }
                    sb.append(acceptation);
                }
                sb.append("]");
            }

            if (this.isCyclic != null && this.isCyclic == true) {
                sb.append("(cyclic)");
            }

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Compares this state to the provided state.
     */
    @Override
    public int compareTo(
            State state) {

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        if (this.automaton != state.automaton) {
            throw new InternalException(
                    "cannot compare states from distinct automatons");
        }

        return this.id - state.id;
    }

    /**
     * Stabilizes this state.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this state is already stable");
        }

        for (Map.Entry<RichSymbol, SortedSet<State>> entry : this.transitions
                .entrySet()) {
            entry.setValue(Collections.unmodifiableSortedSet(entry.getValue()));
        }
        this.transitions = Collections.unmodifiableSortedMap(this.transitions);

        this.acceptations = Collections
                .unmodifiableSortedSet(this.acceptations);

        this.isStable = true;
    }

    /**
     * Adds a transition from this state on the given rich symbol to the
     * provided state.
     */
    void addTransition(
            RichSymbol richSymbol,
            State state) {

        if (this.isStable) {
            throw new InternalException(
                    "this state is stable and may not be modified");
        }

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        if (richSymbol != null
                && richSymbol != RichSymbol.END
                && !this.automaton.getAlphabet().getSymbols()
                        .contains(richSymbol.getSymbol())) {
            throw new InternalException("invalid symbol");
        }

        if (this.automaton != state.automaton) {
            throw new InternalException("invalid state");
        }

        SortedSet<State> targets = this.transitions.get(richSymbol);

        if (targets == null) {
            targets = new TreeSet<State>();
            this.transitions.put(richSymbol, targets);
        }

        targets.add(state);
    }

    /**
     * Adds the provided marker to the markers of this state.
     */
    void setMarker(
            Marker marker) {

        if (this.isStable) {
            throw new InternalException(
                    "this state is stable and may not be modified");
        }

        if (marker == null) {
            throw new InternalException("marker may not be null");
        }

        if (this.marker != null) {
            throw new InternalException("marker already set");
        }

        this.marker = marker;
    }

    /**
     * Adds the provided acceptation to the acceptations of this state.
     */
    void addAcceptation(
            Acceptation acceptation) {

        if (this.isStable) {
            throw new InternalException(
                    "this state is stable and may not be modified");
        }

        if (acceptation == null) {
            throw new InternalException("acceptation may not be null");
        }

        if (!this.automaton.getUnstableAcceptations().contains(acceptation)) {
            throw new InternalException("invalid acceptation");
        }

        this.acceptations.add(acceptation);
    }

    /**
     * Returns the epsilon reach of this state.
     */
    public SortedSet<State> getEpsilonReach() {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        if (this.epsilonReach == null) {

            SortedSet<State> epsilonReach = new TreeSet<State>();
            fillEpsilonReach(epsilonReach);
            this.epsilonReach = Collections.unmodifiableSortedSet(epsilonReach);
        }

        return this.epsilonReach;
    }

    /**
     * Fill the given epsilon reach. Uses a depth first search.
     */
    private void fillEpsilonReach(
            SortedSet<State> epsilonReach) {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        // is this state already included?
        if (epsilonReach.contains(this)) {
            return;
        }

        epsilonReach.add(this);

        if (this.epsilonReach != null) {
            // avoid further recursion using the already known epsilon reach of
            // this state
            epsilonReach.addAll(this.epsilonReach);
        }
        else {
            // proceed with the depth first search
            for (State state : getTargets(null)) {
                state.fillEpsilonReach(epsilonReach);
            }
        }
    }

    public boolean isCyclic() {

        if (!this.isStable) {
            throw new InternalException("this state is not yet stable");
        }

        if (this.isCyclic == null) {
            this.automaton.identifyCyclicStatesOnLookaheadTransitions();
        }

        return this.isCyclic;
    }

    Integer getDiscoveryTime() {

        return this.discoveryTime;
    }

    void setDiscoveryTime(
            int discoveryTime) {

        this.discoveryTime = discoveryTime;
    }

    int getLowestReachableDiscoveryTime() {

        return this.lowestReachableDiscoveryTime;
    }

    void setLowestReachableDiscoveryTime(
            int lowestReachableDiscoveryTime) {

        this.lowestReachableDiscoveryTime = lowestReachableDiscoveryTime;
    }

    boolean isOnStack() {

        return this.isOnStack;
    }

    void setOnStack(
            boolean isOnStack) {

        this.isOnStack = isOnStack;
    }

    void setIsCyclic(
            boolean isCyclic) {

        this.isCyclic = isCyclic;
    }
}
