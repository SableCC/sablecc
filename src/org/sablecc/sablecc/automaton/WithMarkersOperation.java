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
import org.sablecc.util.*;

class WithMarkersOperation {

    private Automaton newAutomaton;

    private SortedSet<State> optimizedStates = new TreeSet<State>();

    private Map<Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>>, State> stateMap = new HashMap<Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>>, State>();

    private SortedMap<State, Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>>> progressMap = new TreeMap<State, Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>>>();

    private WorkSet<State> workSet = new WorkSet<State>();

    WithMarkersOperation(
            Automaton oldAutomaton) {

        if (oldAutomaton == null) {
            throw new InternalException("oldAutomaton may not be null");
        }

        if (oldAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        if (oldAutomaton.getAcceptations().size() > 0
                && oldAutomaton.getAcceptations().first() == Acceptation.ACCEPT) {
            throw new InternalException("invalid operation");
        }

        oldAutomaton = getOptimizedAutomaton(oldAutomaton.longest().minimal())
                .minimal();

        oldAutomaton.identifyCyclicStatesOnLookaheadTransitions();

        this.newAutomaton = new Automaton(oldAutomaton.getAlphabet());

        {
            Set<Pair<State, Pair<Integer, Marker>>> progressSet = new LinkedHashSet<Pair<State, Pair<Integer, Marker>>>();
            progressSet.add(new Pair<State, Pair<Integer, Marker>>(oldAutomaton
                    .getStartState(), null));
            Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>> progress = new Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>>(
                    null, progressSet);
            this.stateMap.put(progress, this.newAutomaton.getStartState());
            this.progressMap.put(this.newAutomaton.getStartState(), progress);
            this.workSet.add(this.newAutomaton.getStartState());
        }

        while (this.workSet.hasNext()) {
            State state = this.workSet.next();

            Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>> progress = this.progressMap
                    .get(state);

            {
                boolean acceptFound = false;

                for (Pair<State, Pair<Integer, Marker>> progressElement : progress
                        .getRight()) {
                    State oldState = progressElement.getLeft();
                    Pair<Integer, Marker> accept = progressElement.getRight();
                    if (oldState.isAcceptState()) {
                        if (acceptFound) {
                            throw new InternalException(
                                    "there shouldn't be more than one accepting progress");
                        }
                        acceptFound = true;

                        for (Acceptation acceptation : oldState
                                .getAcceptations()) {
                            Acceptation markedAcceptation;
                            if (accept == null) {
                                markedAcceptation = new Acceptation(
                                        acceptation.getName(), 0, null);
                            }
                            else {
                                markedAcceptation = new Acceptation(
                                        acceptation.getName(),
                                        accept.getLeft(), accept.getRight());
                            }

                            if (!this.newAutomaton.getUnstableAcceptations()
                                    .contains(markedAcceptation)) {
                                this.newAutomaton
                                        .addAcceptation(markedAcceptation);
                            }

                            state.addAcceptation(markedAcceptation);
                        }
                    }
                }
            }

            Marker marker = progress.getLeft();
            if (marker != null) {
                state.setMarker(marker);
            }

            for (Symbol symbol : this.newAutomaton.getAlphabet().getSymbols()) {
                addTransition(state, symbol.getNormalRichSymbol());
            }
            addTransition(state, RichSymbol.END);
        }

        this.newAutomaton.stabilize();
    }

    private void addTransition(
            State newSourceState,
            RichSymbol richSymbol) {

        RichSymbol normalRichSymbol = richSymbol.isLookahead() ? null
                : richSymbol;
        RichSymbol lookaheadRichSymbol = richSymbol.isLookahead() ? richSymbol
                : richSymbol.getSymbol().getLookaheadRichSymbol();

        Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>> sourceProgress = this.progressMap
                .get(newSourceState);

        Set<Pair<State, Pair<Integer, Marker>>> targetProgressSet = new LinkedHashSet<Pair<State, Pair<Integer, Marker>>>();

        SortedSet<Marker> targetMarkers = new TreeSet<Marker>();

        for (Pair<State, Pair<Integer, Marker>> sourceProgressElement : sourceProgress
                .getRight()) {
            State oldSourceState = sourceProgressElement.getLeft();
            Pair<Integer, Marker> sourceAccept = sourceProgressElement
                    .getRight();
            State oldTargetState = oldSourceState
                    .getSingleTarget(lookaheadRichSymbol);
            if (oldTargetState != null) {
                if (sourceAccept != null) {
                    Marker marker = sourceAccept.getRight();
                    if (marker != null) {
                        targetMarkers.add(marker);
                    }
                }
            }
        }

        Marker nextMarker = null;
        boolean nextMarkerIsUsed = false;

        {
            int i = 0;

            do {
                nextMarker = new Marker("" + i++);
                if (targetMarkers.contains(nextMarker)) {
                    nextMarker = null;
                }
            }
            while (nextMarker == null);
        }

        for (Pair<State, Pair<Integer, Marker>> sourceProgressElement : sourceProgress
                .getRight()) {
            State oldSourceState = sourceProgressElement.getLeft();
            Pair<Integer, Marker> sourceAccept = sourceProgressElement
                    .getRight();

            if (normalRichSymbol != null) {
                State oldTargetState = oldSourceState
                        .getSingleTarget(normalRichSymbol);
                if (oldTargetState != null) {
                    if (sourceAccept != null) {
                        throw new InternalException(
                                "a normal symbol is not valid after a lookahead symbol");
                    }

                    Pair<State, Pair<Integer, Marker>> targetProgressElement = new Pair<State, Pair<Integer, Marker>>(
                            oldTargetState, null);
                    targetProgressSet.add(targetProgressElement);
                }
            }

            {
                State oldTargetState = oldSourceState
                        .getSingleTarget(lookaheadRichSymbol);
                if (oldTargetState != null) {
                    Pair<Integer, Marker> targetAccept;

                    if (sourceAccept != null) {
                        if (sourceAccept.getRight() != null) {
                            targetAccept = sourceAccept;
                        }
                        else if (oldTargetState.isCyclic()) {
                            targetAccept = new Pair<Integer, Marker>(
                                    sourceAccept.getLeft() + 1, nextMarker);
                            nextMarkerIsUsed = true;
                        }
                        else {
                            targetAccept = new Pair<Integer, Marker>(
                                    sourceAccept.getLeft() + 1, null);
                        }
                    }
                    else if (oldTargetState.isCyclic()) {
                        targetAccept = new Pair<Integer, Marker>(1, nextMarker);
                        nextMarkerIsUsed = true;
                    }
                    else {
                        targetAccept = new Pair<Integer, Marker>(1, null);
                    }

                    Pair<State, Pair<Integer, Marker>> targetProgressElement = new Pair<State, Pair<Integer, Marker>>(
                            oldTargetState, targetAccept);
                    targetProgressSet.add(targetProgressElement);
                }
            }
        }

        if (targetProgressSet.size() == 0) {
            return;
        }

        if (nextMarkerIsUsed) {
            if (!this.newAutomaton.getUnstableMarkers().contains(nextMarker)) {
                this.newAutomaton.addMarker(nextMarker);
            }
        }

        Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>> targetProgress = new Pair<Marker, Set<Pair<State, Pair<Integer, Marker>>>>(
                nextMarkerIsUsed ? nextMarker : null, targetProgressSet);

        State newTargetState = this.stateMap.get(targetProgress);
        if (newTargetState == null) {
            newTargetState = new State(this.newAutomaton);

            this.stateMap.put(targetProgress, newTargetState);
            this.progressMap.put(newTargetState, targetProgress);
            this.workSet.add(newTargetState);
        }

        newSourceState.addTransition(richSymbol, newTargetState);
    }

    private Automaton getOptimizedAutomaton(
            Automaton oldAutomaton) {

        Automaton optimizedAutomaton = new Automaton(oldAutomaton.getAlphabet());

        for (Acceptation acceptation : oldAutomaton.getAcceptations()) {
            optimizedAutomaton.addAcceptation(acceptation);
        }

        Map<State, State> oldStateToOptimizedStateMap = new HashMap<State, State>();

        for (State oldState : oldAutomaton.getStates()) {
            State optimizedState;
            if (oldState.equals(oldAutomaton.getStartState())) {
                optimizedState = optimizedAutomaton.getStartState();
            }
            else {
                optimizedState = new State(optimizedAutomaton);
            }

            State optimization = getOptimization(oldState);

            if (optimization != null) {
                for (Acceptation acceptation : optimization.getAcceptations()) {
                    optimizedState.addAcceptation(acceptation);
                }

                this.optimizedStates.add(optimizedState);
            }
            else if (oldState.isAcceptState()) {
                for (Acceptation acceptation : oldState.getAcceptations()) {
                    optimizedState.addAcceptation(acceptation);
                }
            }

            oldStateToOptimizedStateMap.put(oldState, optimizedState);
        }

        for (State oldSourceState : oldAutomaton.getStates()) {
            State optimizedSourceState = oldStateToOptimizedStateMap
                    .get(oldSourceState);

            if (this.optimizedStates.contains(optimizedSourceState)) {
                continue;
            }

            for (Map.Entry<RichSymbol, SortedSet<State>> entry : oldSourceState
                    .getTransitions().entrySet()) {
                RichSymbol richSymbol = entry.getKey();

                for (State oldTargetState : entry.getValue()) {
                    State optimizedTargetState = oldStateToOptimizedStateMap
                            .get(oldTargetState);
                    optimizedSourceState.addTransition(richSymbol,
                            optimizedTargetState);
                }
            }
        }

        optimizedAutomaton.stabilize();

        return optimizedAutomaton;
    }

    private State getOptimization(
            State sourceState) {

        Alphabet alphabet = sourceState.getAutomaton().getAlphabet();
        State targetState;

        if (alphabet.getSymbols().size() == 0) {
            return null;
        }

        for (Symbol symbol : alphabet.getSymbols()) {
            targetState = sourceState.getSingleTarget(symbol
                    .getNormalRichSymbol());
            if (targetState != null) {
                return null;
            }
            targetState = sourceState.getSingleTarget(symbol
                    .getLookaheadRichSymbol());
            if (targetState == null || !targetState.equals(sourceState)) {
                return null;
            }
        }

        targetState = sourceState.getSingleTarget(RichSymbol.END);
        if (targetState == null) {
            return null;
        }

        for (Symbol symbol : alphabet.getSymbols()) {
            if (targetState.getSingleTarget(symbol.getNormalRichSymbol()) != null) {
                return null;
            }
            if (targetState.getSingleTarget(symbol.getLookaheadRichSymbol()) != null) {
                return null;
            }
        }
        if (targetState.getSingleTarget(RichSymbol.END) != null) {
            return null;
        }

        return targetState;
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }
}
