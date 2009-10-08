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

import static java.math.BigInteger.*;
import static org.sablecc.sablecc.util.UsefulStaticImports.*;

import java.math.*;
import java.util.*;
import java.util.Map.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.alphabet.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.structure.*;
import org.sablecc.util.*;

/**
 * An instance of this class represents a finite automaton.
 */
public final class Automaton {

    /**
     * A comparator for rich symbols which can handle epsilon (null)
     * comparisons.
     */
    private static final Comparator<RichSymbol> richSymbolComparator = new Comparator<RichSymbol>() {

        // allows comparison of null symbols
        public int compare(
                RichSymbol richSymbol1,
                RichSymbol richSymbol2) {

            if (richSymbol1 == null) {
                return richSymbol2 == null ? 0 : -1;
            }

            if (richSymbol2 == null) {
                return 1;
            }

            return richSymbol1.compareTo(richSymbol2);
        }
    };

    private static final Progeny<State> lookaheadProgeny = new Progeny<State>() {

        public Set<State> getChildren(
                State sourceState) {

            Set<State> children = new LinkedHashSet<State>();

            for (RichSymbol richSymbol : sourceState.getTransitions().keySet()) {
                if (!richSymbol.isLookahead()) {
                    continue;
                }
                State targetState = sourceState.getSingleTarget(richSymbol);
                children.add(targetState);
            }

            return Collections.unmodifiableSet(children);
        }
    };

    /**
     * The alphabet of this automaton.
     */
    private final Alphabet alphabet;

    /**
     * The states of this automaton.
     */
    private SortedSet<State> states;

    /**
     * The start state of this automaton.
     */
    private State startState;

    /**
     * The markers of this automaton.
     */
    private SortedSet<Marker> markers;

    /**
     * The acceptations of this automaton.
     */
    private SortedSet<Acceptation> acceptations;

    private Boolean isDeterministic;

    /**
     * The stability status of this automaton.
     */
    private boolean isStable;

    /**
     * The cached string representation of this automaton. It is
     * <code>null</code> when not yet computed.
     */
    private String toString;

    /**
     * Initializes this automaton. This method must be called at the beginning
     * of every constructors.
     */
    private void init() {

        this.states = new TreeSet<State>();
        this.startState = new State(this);
        this.markers = new TreeSet<Marker>();
        this.acceptations = new TreeSet<Acceptation>();
        this.isStable = false;
    }

    /**
     * Constructs an incomplete automaton. This private constructor returns an
     * automaton to which new states can be added. The <code>stabilize()</code>
     * method should be called on this instance before exposing it publicly.
     */
    Automaton(
            Alphabet alphabet) {

        init();

        this.alphabet = alphabet;
    }

    /**
     * Returns the alphabet of this automaton.
     */
    public Alphabet getAlphabet() {

        return this.alphabet;
    }

    /**
     * Returns the states of this automaton.
     */
    public SortedSet<State> getStates() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return this.states;
    }

    /**
     * Returns the start state of this automaton.
     */
    public State getStartState() {

        return this.startState;
    }

    /**
     * Returns the markers of this automaton.
     */
    public SortedSet<Marker> getMarkers() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return this.markers;
    }

    /**
     * Returns the markers of this unstable automaton.
     */
    SortedSet<Marker> getUnstableMarkers() {

        if (this.isStable) {
            throw new InternalException("this automaton is stable");
        }

        return this.markers;
    }

    /**
     * Returns the acceptations of this automaton.
     */
    public SortedSet<Acceptation> getAcceptations() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return this.acceptations;
    }

    /**
     * Returns the acceptations of this unstable automaton.
     */
    SortedSet<Acceptation> getUnstableAcceptations() {

        if (this.isStable) {
            throw new InternalException("this automaton is stable");
        }

        return this.acceptations;
    }

    /**
     * Returns the string representation of this automaton.
     */
    @Override
    public String toString() {

        if (this.toString == null) {

            if (!this.isStable) {
                throw new InternalException("this automaton is not yet stable");
            }

            StringBuilder sb = new StringBuilder();

            sb.append("Automaton:{");

            for (State state : this.states) {
                sb.append(LINE_SEPARATOR);
                sb.append("    ");

                if (state == this.startState) {
                    sb.append("(start)");
                }

                sb.append(state);

                sb.append(":");
                for (Map.Entry<RichSymbol, SortedSet<State>> entry : state
                        .getTransitions().entrySet()) {
                    RichSymbol richSymbol = entry.getKey();
                    SortedSet<State> targets = entry.getValue();

                    for (State target : targets) {
                        sb.append(LINE_SEPARATOR);
                        sb.append("        ");
                        sb.append(richSymbol == null ? "EPSILON" : richSymbol
                                .toString());
                        sb.append(" -> state_");
                        sb.append(target.getId());
                    }
                }
            }

            sb.append(LINE_SEPARATOR);
            sb.append("}");

            this.toString = sb.toString();
        }

        return this.toString;
    }

    /**
     * Stabilizes this automaton by stabilizing each of its states.
     */
    void stabilize() {

        if (this.isStable) {
            throw new InternalException("this automaton is already stable");
        }

        for (State state : this.states) {
            state.stabilize();
        }

        this.states = Collections.unmodifiableSortedSet(this.states);
        this.markers = Collections.unmodifiableSortedSet(this.markers);
        this.acceptations = Collections
                .unmodifiableSortedSet(this.acceptations);

        this.isStable = true;
    }

    /**
     * Returns the next available state ID. This is useful for the construction
     * of a new state.
     */
    int getNextStateId() {

        if (this.isStable) {
            throw new InternalException(
                    "this automaton is stable and may not be modified");
        }

        return this.states.size();
    }

    /**
     * Adds the provided state to this automaton.
     */
    void addState(
            State state) {

        if (this.isStable) {
            throw new InternalException(
                    "this automaton is stable and may not be modified");
        }

        if (state == null) {
            throw new InternalException("state may not be null");
        }

        if (state.getId() != this.states.size()) {
            throw new InternalException("invalid state ID");
        }

        if (!this.states.add(state)) {
            throw new InternalException("state is already in states");
        }
    }

    /**
     * Adds the provided marker to this automaton.
     */
    void addMarker(
            Marker marker) {

        if (this.isStable) {
            throw new InternalException(
                    "this automaton is stable and may not be modified");
        }

        if (marker == null) {
            throw new InternalException("marker may not be null");
        }

        if (!this.markers.add(marker)) {
            throw new InternalException("marker is already in markers");
        }
    }

    /**
     * Adds the provided state as an accept state of this automaton according to
     * the provided acceptation.
     */
    void addAcceptation(
            Acceptation acceptation) {

        if (this.isStable) {
            throw new InternalException(
                    "this automaton is stable and may not be modified");
        }

        if (acceptation == null) {
            throw new InternalException("acceptation may not be null");
        }

        if (acceptation.getMarker() != null
                && !this.markers.contains(acceptation.getMarker())) {
            throw new InternalException("acceptation has invalid marker");
        }

        if (!this.acceptations.add(acceptation)) {
            throw new InternalException(
                    "acceptation is already in acceptations");
        }
    }

    public boolean isDeterministic() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (this.isDeterministic == null) {

            outer_loop: for (State state : this.states) {
                for (Map.Entry<RichSymbol, SortedSet<State>> entry : state
                        .getTransitions().entrySet()) {
                    if (entry.getKey() == null || entry.getValue().size() > 1) {
                        this.isDeterministic = false;
                        break outer_loop;
                    }
                }
            }

            if (this.isDeterministic == null) {
                this.isDeterministic = true;
            }
        }

        return this.isDeterministic;
    }

    public boolean hasMarkers() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return this.markers.size() > 0;
    }

    public boolean hasCustomAcceptations() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        for (Acceptation acceptation : this.acceptations) {
            if (acceptation != Acceptation.ACCEPT) {
                return true;
            }
        }

        return false;
    }

    public boolean hasEndTransition() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        for (State state : this.states) {
            if (state.getTransitions().containsKey(RichSymbol.END)) {
                return true;
            }
        }

        return false;
    }

    public Automaton withMergedAlphabet(
            AlphabetMergeResult alphabetMergeResult) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (alphabetMergeResult == null) {
            throw new InternalException("alphabetMergeResult may not be null");
        }

        if (hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        Automaton newAutomaton = new Automaton(alphabetMergeResult
                .getNewAlphabet());

        for (Acceptation acceptation : getAcceptations()) {
            newAutomaton.addAcceptation(acceptation);
        }

        SortedMap<State, State> oldStateToNewStateMap = new TreeMap<State, State>();
        SortedMap<State, State> newStateToOldStateMap = new TreeMap<State, State>();

        oldStateToNewStateMap
                .put(getStartState(), newAutomaton.getStartState());
        newStateToOldStateMap
                .put(newAutomaton.getStartState(), getStartState());

        for (State oldState : getStates()) {
            if (oldState.equals(getStartState())) {
                continue;
            }

            State newState = new State(newAutomaton);

            oldStateToNewStateMap.put(oldState, newState);
            newStateToOldStateMap.put(newState, oldState);
        }

        for (State oldSourceState : getStates()) {
            State newSourceState = oldStateToNewStateMap.get(oldSourceState);

            for (Entry<RichSymbol, SortedSet<State>> entry : oldSourceState
                    .getTransitions().entrySet()) {
                RichSymbol oldRichSymbol = entry.getKey();
                SortedSet<State> oldTargetStates = entry.getValue();

                for (State oldTargetState : oldTargetStates) {
                    State newTargetState = oldStateToNewStateMap
                            .get(oldTargetState);

                    if (oldRichSymbol == null) {
                        RichSymbol newRichSymbol = null;

                        newSourceState.addTransition(newRichSymbol,
                                newTargetState);
                    }
                    else {
                        for (RichSymbol newRichSymbol : alphabetMergeResult
                                .getNewRichSymbols(oldRichSymbol)) {
                            newSourceState.addTransition(newRichSymbol,
                                    newTargetState);
                        }
                    }
                }
            }

            for (Acceptation acceptation : oldSourceState.getAcceptations()) {
                newSourceState.addAcceptation(acceptation);
            }
        }

        newAutomaton.stabilize();

        return newAutomaton;
    }

    public Automaton withoutUnreachableStates() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        SortedSet<State> reachableStates = new TreeSet<State>();
        SortedSet<Acceptation> usefulAcceptations = new TreeSet<Acceptation>();

        WorkSet<State> workSet = new WorkSet<State>();
        workSet.add(getStartState());

        Automaton newAutomaton = new Automaton(getAlphabet());
        SortedMap<State, State> oldStatetoNewStateMap = new TreeMap<State, State>();

        while (workSet.hasNext()) {
            State reachableState = workSet.next();

            reachableStates.add(reachableState);

            State newState = reachableState == getStartState() ? newAutomaton
                    .getStartState() : new State(newAutomaton);
            oldStatetoNewStateMap.put(reachableState, newState);

            for (Acceptation usefulAcceptation : reachableState
                    .getAcceptations()) {
                usefulAcceptations.add(usefulAcceptation);
            }

            for (Entry<RichSymbol, SortedSet<State>> entry : reachableState
                    .getTransitions().entrySet()) {
                for (State targetState : entry.getValue()) {
                    workSet.add(targetState);
                }
            }
        }

        for (Acceptation usefulAcceptation : usefulAcceptations) {
            newAutomaton.addAcceptation(usefulAcceptation);
        }

        for (State oldSourceState : reachableStates) {
            State newSourceState = oldStatetoNewStateMap.get(oldSourceState);
            for (Entry<RichSymbol, SortedSet<State>> entry : oldSourceState
                    .getTransitions().entrySet()) {
                SortedSet<State> oldTargetStates = entry.getValue();
                for (State oldTargetState : oldTargetStates) {
                    State newTargetState = oldStatetoNewStateMap
                            .get(oldTargetState);
                    newSourceState
                            .addTransition(entry.getKey(), newTargetState);
                }
            }

            for (Acceptation acceptation : oldSourceState.getAcceptations()) {
                newSourceState.addAcceptation(acceptation);
            }
        }

        newAutomaton.stabilize();

        return newAutomaton;
    }

    public Automaton accept(
            Acceptation acceptation) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (hasCustomAcceptations()) {
            throw new InternalException("invalid operation");
        }

        if (hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        if (acceptation == null) {
            throw new InternalException("acceptation may not be null");
        }

        if (acceptation.equals(Acceptation.ACCEPT)) {
            throw new InternalException("acceptation is invalid");
        }

        Automaton newAutomaton = new Automaton(getAlphabet());
        newAutomaton.addAcceptation(acceptation);

        SortedMap<State, State> oldStatetoNewStateMap = new TreeMap<State, State>();

        for (State oldState : getStates()) {
            State newState = oldState == getStartState() ? newAutomaton
                    .getStartState() : new State(newAutomaton);

            oldStatetoNewStateMap.put(oldState, newState);

            if (oldState.isAcceptState()) {
                newState.addAcceptation(acceptation);
            }
        }

        for (State oldSourceState : getStates()) {
            State newSourceState = oldStatetoNewStateMap.get(oldSourceState);
            for (Entry<RichSymbol, SortedSet<State>> entry : oldSourceState
                    .getTransitions().entrySet()) {
                RichSymbol richSymbol = entry.getKey();
                for (State oldTargetState : entry.getValue()) {
                    State newTargetState = oldStatetoNewStateMap
                            .get(oldTargetState);
                    newSourceState.addTransition(richSymbol, newTargetState);
                }
            }
        }

        newAutomaton.stabilize();

        return newAutomaton;
    }

    public Automaton deterministic() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return new DeterministicOperation(this).getNewAutomaton();
    }

    public Automaton minimal() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return new MinimalOperation(this).getNewAutomaton();
    }

    public Automaton or(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        return new OrOperation(this, automaton).getNewAutomaton();
    }

    public Automaton concat(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        return new ConcatOperation(this, automaton).getNewAutomaton();
    }

    public Automaton zeroOrOne() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return or(getEpsilonLookAnyStarEnd());
    }

    public Automaton oneOrMore() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return new OneOrMoreOperation(this).getNewAutomaton();
    }

    public Automaton oneOrMoreWithSeparator(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        return concat(automaton.concat(this).zeroOrMore());
    }

    public Automaton zeroOrMore() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return oneOrMore().zeroOrOne();
    }

    public Automaton zeroOrMoreWithSeparator(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        return oneOrMoreWithSeparator(automaton).zeroOrOne();
    }

    public Automaton nTimes(
            BigInteger n) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (n.compareTo(ZERO) < 0) {
            throw new InternalException("n may not be negative");
        }

        if (n.compareTo(ZERO) == 0) {
            return getEpsilonLookAnyStarEnd();
        }

        Automaton newAutomaton = this;
        for (BigInteger i = ONE; i.compareTo(n) < 0; i = i.add(ONE)) {
            newAutomaton = newAutomaton.concat(this);
        }

        return newAutomaton;
    }

    public Automaton nTimesWithSeparator(
            Automaton automaton,
            BigInteger n) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        if (n.compareTo(ZERO) < 0) {
            throw new InternalException("n may not be negative");
        }

        if (n.compareTo(ZERO) == 0) {
            return getEpsilonLookAnyStarEnd();
        }

        if (n.compareTo(ONE) == 0) {
            return this;
        }

        return concat(automaton.concat(this).nTimes(n.subtract(ONE)));
    }

    public Automaton nOrMore(
            BigInteger n) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (n.compareTo(ZERO) < 0) {
            throw new InternalException("n may not be negative");
        }

        return nTimes(n).concat(zeroOrMore());
    }

    public Automaton nOrMoreWithSeparator(
            Automaton automaton,
            BigInteger n) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        if (n.compareTo(ZERO) < 0) {
            throw new InternalException("n may not be negative");
        }

        if (n.compareTo(ZERO) == 0) {
            return zeroOrMoreWithSeparator(automaton);
        }

        if (n.compareTo(ONE) == 0) {
            return oneOrMoreWithSeparator(automaton);
        }

        return concat(automaton.concat(this).nOrMore(n.subtract(ONE)));
    }

    public Automaton nToM(
            BigInteger n,
            BigInteger m) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (n.compareTo(ZERO) < 0) {
            throw new InternalException("n may not be negative");
        }

        if (m.compareTo(n) < 0) {
            throw new InternalException("m may not be smaller than n");
        }

        Automaton tailAutomaton = getEpsilonLookAnyStarEnd();

        for (BigInteger i = n; i.compareTo(m) < 0; i = i.add(ONE)) {
            tailAutomaton = tailAutomaton.concat(this).zeroOrOne();
        }

        return nTimes(n).concat(tailAutomaton);
    }

    public Automaton nToMWithSeparator(
            Automaton automaton,
            BigInteger n,
            BigInteger m) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        if (n.compareTo(ZERO) < 0) {
            throw new InternalException("n may not be negative");
        }

        if (m.compareTo(n) < 0) {
            throw new InternalException("m may not be smaller than n");
        }

        if (m.compareTo(n) == 0) {
            return nTimesWithSeparator(automaton, n);
        }

        Automaton tailAutomaton = getEpsilonLookAnyStarEnd();

        if (n.compareTo(ZERO) == 0) {
            for (BigInteger i = ONE; i.compareTo(m) < 0; i = i.add(ONE)) {
                tailAutomaton = tailAutomaton.concat(automaton.concat(this))
                        .zeroOrOne();
            }

            return concat(tailAutomaton).zeroOrOne();
        }

        for (BigInteger i = n; i.compareTo(m) < 0; i = i.add(ONE)) {
            tailAutomaton = tailAutomaton.concat(automaton.concat(this))
                    .zeroOrOne();
        }

        return nTimesWithSeparator(automaton, n).concat(tailAutomaton);
    }

    public Automaton look(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        return new LookOperation(this, automaton).getNewAutomaton();
    }

    public Automaton lookNot(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        Automaton lookAutomaton = getEpsilonLookAnyStarEnd().diff(
                getEpsilonLookAnyStarEnd().look(automaton));
        return look(lookAutomaton);
    }

    public Automaton diff(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        Acceptation leftAcceptation = new Acceptation("left");
        Acceptation rightAcceptation = new Acceptation("right");

        Automaton leftAutomaton = accept(leftAcceptation).minimal();
        Automaton rightAutomaton = automaton.accept(rightAcceptation).minimal();
        Automaton combinedAutomaton = leftAutomaton.or(rightAutomaton)
                .minimal();

        Automaton newAutomaton = new Automaton(combinedAutomaton.getAlphabet());
        newAutomaton.addAcceptation(Acceptation.ACCEPT);

        SortedMap<State, State> oldStatetoNewStateMap = new TreeMap<State, State>();

        for (State oldState : combinedAutomaton.getStates()) {
            State newState = oldState == combinedAutomaton.getStartState() ? newAutomaton
                    .getStartState()
                    : new State(newAutomaton);

            oldStatetoNewStateMap.put(oldState, newState);

            if (oldState.getAcceptations().size() == 1
                    && oldState.getAcceptations().first().equals(
                            leftAcceptation)) {
                newState.addAcceptation(Acceptation.ACCEPT);
            }
        }

        for (State oldSourceState : combinedAutomaton.getStates()) {
            State newSourceState = oldStatetoNewStateMap.get(oldSourceState);
            for (Entry<RichSymbol, SortedSet<State>> entry : oldSourceState
                    .getTransitions().entrySet()) {
                RichSymbol richSymbol = entry.getKey();
                for (State oldTargetState : entry.getValue()) {
                    State newTargetState = oldStatetoNewStateMap
                            .get(oldTargetState);
                    newSourceState.addTransition(richSymbol, newTargetState);
                }
            }
        }

        newAutomaton.stabilize();

        return newAutomaton;
    }

    public Automaton and(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        Acceptation leftAcceptation = new Acceptation("left");
        Acceptation rightAcceptation = new Acceptation("right");

        Automaton leftAutomaton = accept(leftAcceptation).minimal();
        Automaton rightAutomaton = automaton.accept(rightAcceptation).minimal();
        Automaton combinedAutomaton = leftAutomaton.or(rightAutomaton)
                .minimal();

        Automaton newAutomaton = new Automaton(combinedAutomaton.getAlphabet());
        newAutomaton.addAcceptation(Acceptation.ACCEPT);

        SortedMap<State, State> oldStatetoNewStateMap = new TreeMap<State, State>();

        for (State oldState : combinedAutomaton.getStates()) {
            State newState = oldState == combinedAutomaton.getStartState() ? newAutomaton
                    .getStartState()
                    : new State(newAutomaton);

            oldStatetoNewStateMap.put(oldState, newState);

            if (oldState.getAcceptations().size() == 2) {
                newState.addAcceptation(Acceptation.ACCEPT);
            }
        }

        for (State oldSourceState : combinedAutomaton.getStates()) {
            State newSourceState = oldStatetoNewStateMap.get(oldSourceState);
            for (Entry<RichSymbol, SortedSet<State>> entry : oldSourceState
                    .getTransitions().entrySet()) {
                RichSymbol richSymbol = entry.getKey();
                for (State oldTargetState : entry.getValue()) {
                    State newTargetState = oldStatetoNewStateMap
                            .get(oldTargetState);
                    newSourceState.addTransition(richSymbol, newTargetState);
                }
            }
        }

        newAutomaton.stabilize();

        return newAutomaton;
    }

    public Automaton subtract(
            Automaton automaton) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (automaton == null) {
            throw new InternalException("automaton may not be null");
        }

        if (!automaton.isStable) {
            throw new InternalException("automaton is not yet stable");
        }

        return new SubtractOperation(this, automaton).getNewAutomaton();
    }

    public Automaton shortest() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return new ShortestOperation(this).getNewAutomaton();
    }

    public Automaton longest() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return new LongestOperation(this).getNewAutomaton();
    }

    public Automaton withMarkers() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        return new WithMarkersOperation(this).getNewAutomaton();
    }

    public static Automaton getEpsilonLookAnyStarEnd() {

        Symbol any = new Symbol(new Interval(Bound.MIN, Bound.MAX));
        Alphabet alphabet = new Alphabet(any);
        Automaton automaton = new Automaton(alphabet);

        State start = automaton.startState;
        State end = new State(automaton);

        start.addTransition(any.getLookaheadRichSymbol(), start);
        start.addTransition(RichSymbol.END, end);

        automaton.addAcceptation(Acceptation.ACCEPT);
        end.addAcceptation(Acceptation.ACCEPT);

        automaton.stabilize();

        return automaton;
    }

    public static Automaton getSymbolLookAnyStarEnd(
            Symbol symbol) {

        if (symbol == null) {
            throw new InternalException("symbol may not be null");
        }

        Symbol any = new Symbol(new Interval(Bound.MIN, Bound.MAX));
        Alphabet anyAlphabet = new Alphabet(any);
        Alphabet symbolAlphabet = new Alphabet(symbol);

        AlphabetMergeResult alphabetMergeResult = anyAlphabet
                .mergeWith(symbolAlphabet);
        Alphabet alphabet = alphabetMergeResult.getNewAlphabet();
        Automaton automaton = new Automaton(alphabet);

        State start = automaton.startState;
        State middle = new State(automaton);
        State end = new State(automaton);

        for (Symbol newSymbol : alphabetMergeResult.getNewSymbols(symbol)) {
            start.addTransition(newSymbol.getNormalRichSymbol(), middle);
        }

        for (Symbol newSymbol : alphabetMergeResult.getNewSymbols(any)) {
            middle.addTransition(newSymbol.getLookaheadRichSymbol(), middle);
        }

        middle.addTransition(RichSymbol.END, end);

        automaton.addAcceptation(Acceptation.ACCEPT);
        end.addAcceptation(Acceptation.ACCEPT);

        automaton.stabilize();

        return automaton;
    }

    public static Automaton getEpsilonLookEnd() {

        Alphabet alphabet = new Alphabet();
        Automaton automaton = new Automaton(alphabet);

        State start = automaton.startState;
        State end = new State(automaton);

        start.addTransition(RichSymbol.END, end);

        automaton.addAcceptation(Acceptation.ACCEPT);
        end.addAcceptation(Acceptation.ACCEPT);

        automaton.stabilize();

        return automaton;
    }

    public static Automaton getEmptyAutomaton() {

        Automaton automaton = new Automaton(new Alphabet());
        automaton.stabilize();
        return automaton;
    }

    /**
     * Returns a comparator for rich symbols which can handle epsilon (null)
     * comparisons.
     */
    static Comparator<RichSymbol> getRichSymbolComparator() {

        return Automaton.richSymbolComparator;
    }

    void identifyCyclicStatesOnLookaheadTransitions() {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (!isDeterministic()) {
            throw new InternalException("invalid operation");
        }

        ComponentFinder<State> componentFinder = new ComponentFinder<State>(
                getStates(), lookaheadProgeny);

        for (State state : getStates()) {
            State representative = componentFinder.getRepresentative(state);
            state.setIsCyclic(componentFinder.getReach(representative)
                    .contains(state));
        }
    }

    public Automaton withPriorities(
            Context context) {

        if (!this.isStable) {
            throw new InternalException("this automaton is not yet stable");
        }

        if (this.acceptations.size() == 1
                && this.acceptations.first() == Acceptation.ACCEPT) {
            throw new InternalException("invalid operation");
        }

        if (hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        if (context == null) {
            throw new InternalException("context may not be null");
        }

        Automaton oldAutomaton = minimal();
        Automaton newAutomaton = new Automaton(oldAutomaton.getAlphabet());

        Map<Acceptation, MatchedToken> acceptationToMatchedTokenMap = new LinkedHashMap<Acceptation, MatchedToken>();

        for (Acceptation acceptation : oldAutomaton.acceptations) {
            newAutomaton.addAcceptation(acceptation);

            MatchedToken matchedToken = context.getMatchedToken(acceptation
                    .getName());
            acceptationToMatchedTokenMap.put(acceptation, matchedToken);
        }

        SortedMap<State, State> oldStatetoNewStateMap = new TreeMap<State, State>();
        Map<Acceptation, Set<State>> acceptationToStateSetMap = new LinkedHashMap<Acceptation, Set<State>>();

        for (State oldState : oldAutomaton.getStates()) {
            State newState = oldState == oldAutomaton.getStartState() ? newAutomaton
                    .getStartState()
                    : new State(newAutomaton);

            oldStatetoNewStateMap.put(oldState, newState);

            for (Acceptation acceptation : oldState.getAcceptations()) {
                Set<State> stateSet = acceptationToStateSetMap.get(acceptation);

                if (stateSet == null) {
                    stateSet = new LinkedHashSet<State>();
                    acceptationToStateSetMap.put(acceptation, stateSet);
                }

                stateSet.add(oldState);
            }
        }

        for (State oldSourceState : oldAutomaton.getStates()) {
            State newSourceState = oldStatetoNewStateMap.get(oldSourceState);
            for (Entry<RichSymbol, SortedSet<State>> entry : oldSourceState
                    .getTransitions().entrySet()) {
                RichSymbol richSymbol = entry.getKey();
                for (State oldTargetState : entry.getValue()) {
                    State newTargetState = oldStatetoNewStateMap
                            .get(oldTargetState);
                    newSourceState.addTransition(richSymbol, newTargetState);
                }
            }
        }

        // set acceptations

        for (State oldState : oldAutomaton.getStates()) {
            State newState = oldStatetoNewStateMap.get(oldState);

            // no acceptation
            if (oldState.getAcceptations().size() == 0) {
                continue;
            }

            // one acceptation
            if (oldState.getAcceptations().size() == 1) {
                newState.addAcceptation(oldState.getAcceptations().first());
                continue;
            }

            // more acceptations

            PairExtractor<Acceptation> pairExtractor = new PairExtractor<Acceptation>(
                    oldState.getAcceptations());

            // check for a total order between matched tokens
            for (Pair<Acceptation, Acceptation> pair : pairExtractor.getPairs()) {

                Acceptation leftAcceptation = pair.getLeft();
                Acceptation rightAcceptation = pair.getRight();
                MatchedToken leftMatchedToken = acceptationToMatchedTokenMap
                        .get(leftAcceptation);
                MatchedToken rightMatchedToken = acceptationToMatchedTokenMap
                        .get(rightAcceptation);

                if (leftMatchedToken.hasPriorityOver(rightMatchedToken)
                        || rightMatchedToken.hasPriorityOver(leftMatchedToken)) {
                    continue;
                }

                Set<State> leftStateSet = acceptationToStateSetMap
                        .get(leftAcceptation);
                Set<State> rightStateSet = acceptationToStateSetMap
                        .get(rightAcceptation);

                if (leftStateSet.size() == rightStateSet.size()) {
                    throw CompilerException.lexerConflict(leftMatchedToken,
                            rightMatchedToken);
                }

                if (leftStateSet.size() < rightStateSet.size()) {
                    if (rightStateSet.containsAll(leftStateSet)) {
                        leftMatchedToken
                                .addNaturalPriorityOver(rightMatchedToken);
                    }
                    else {
                        throw CompilerException.lexerConflict(leftMatchedToken,
                                rightMatchedToken);
                    }
                }
                else {
                    if (leftStateSet.containsAll(rightStateSet)) {
                        rightMatchedToken
                                .addNaturalPriorityOver(leftMatchedToken);
                    }
                    else {
                        throw CompilerException.lexerConflict(leftMatchedToken,
                                rightMatchedToken);
                    }
                }
            }

            // select highest priority acceptation
            Acceptation winningAcceptation = null;
            MatchedToken winningMatchedToken = null;
            for (Acceptation acceptation : oldState.getAcceptations()) {
                MatchedToken matchedToken = acceptationToMatchedTokenMap
                        .get(acceptation);

                if (winningAcceptation == null) {
                    winningAcceptation = acceptation;
                    winningMatchedToken = matchedToken;
                }
                else if (matchedToken.hasPriorityOver(winningMatchedToken)) {
                    winningAcceptation = acceptation;
                    winningMatchedToken = matchedToken;
                }
            }

            newState.addAcceptation(winningAcceptation);
        }

        newAutomaton.stabilize();

        return newAutomaton;

    }
}
