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

class ConcatOperation {

    private Alphabet newAlphabet;

    private Automaton newAutomaton;

    private Map<Pair<State, Set<Pair<State, State>>>, State> stateMap = new HashMap<Pair<State, Set<Pair<State, State>>>, State>();

    private SortedMap<State, Pair<State, Set<Pair<State, State>>>> progressMap = new TreeMap<State, Pair<State, Set<Pair<State, State>>>>();

    private WorkSet<State> workSet;

    private AlphabetMergeResult alphabetMergeResult;

    private Automaton leftAutomaton;

    private Automaton rightAutomaton;

    ConcatOperation(
            Automaton leftAutomaton,
            Automaton rightAutomaton) {

        if (leftAutomaton == null) {
            throw new InternalException("leftAutomaton may not be null");
        }

        if (rightAutomaton == null) {
            throw new InternalException("rightAutomaton may not be null");
        }

        if (leftAutomaton.hasCustomAcceptations()) {
            throw new InternalException("invalid operation");
        }

        if (rightAutomaton.hasCustomAcceptations()) {
            throw new InternalException("invalid operation");
        }

        if (leftAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        if (rightAutomaton.hasMarkers()) {
            throw new InternalException("invalid operation");
        }

        leftAutomaton = leftAutomaton.minimal();
        rightAutomaton = rightAutomaton.minimal();

        this.alphabetMergeResult = leftAutomaton.getAlphabet().mergeWith(
                rightAutomaton.getAlphabet());
        this.newAlphabet = this.alphabetMergeResult.getNewAlphabet();
        this.newAutomaton = new Automaton(this.newAlphabet);

        this.leftAutomaton = leftAutomaton
                .withMergedAlphabet(this.alphabetMergeResult);
        this.rightAutomaton = rightAutomaton
                .withMergedAlphabet(this.alphabetMergeResult);

        this.newAutomaton.addAcceptation(Acceptation.ACCEPT);

        Set<Pair<State, State>> rightProgress = new LinkedHashSet<Pair<State, State>>();
        rightProgress.add(new Pair<State, State>(this.rightAutomaton
                .getStartState(), this.leftAutomaton.getStartState()));
        Pair<State, Set<Pair<State, State>>> progress = new Pair<State, Set<Pair<State, State>>>(
                this.leftAutomaton.getStartState(), rightProgress);

        this.workSet = new WorkSet<State>();

        this.stateMap.put(progress, this.newAutomaton.getStartState());
        this.progressMap.put(this.newAutomaton.getStartState(), progress);
        this.workSet.add(this.newAutomaton.getStartState());

        while (this.workSet.hasNext()) {
            State state = this.workSet.next();

            for (Pair<State, State> rightProgressPair : this.progressMap.get(
                    state).getRight()) {
                if (rightProgressPair.getLeft().isAcceptState()
                        && rightProgressPair.getRight().isAcceptState()) {
                    state.addAcceptation(Acceptation.ACCEPT);
                    break;
                }
            }

            for (Symbol symbol : this.newAlphabet.getSymbols()) {
                addTransition(state, symbol.getNormalRichSymbol());
                addTransition(state, symbol.getLookaheadRichSymbol());
            }
            addTransition(state, RichSymbol.END);
        }

        this.newAutomaton.stabilize();
    }

    private void addTransition(
            State sourceState,
            RichSymbol richSymbol) {

        Pair<State, Set<Pair<State, State>>> sourceProgress = this.progressMap
                .get(sourceState);

        State leftSourceState = sourceProgress.getLeft();
        State leftTargetState;

        if (leftSourceState == null || richSymbol.isLookahead()) {
            leftTargetState = null;
        }
        else {
            leftTargetState = leftSourceState.getSingleTarget(richSymbol);
        }

        Set<Pair<State, State>> rightSourceProgress = sourceProgress.getRight();
        Set<Pair<State, State>> rightTargetProgress = new LinkedHashSet<Pair<State, State>>();

        if (leftTargetState != null) {
            rightTargetProgress.add(new Pair<State, State>(this.rightAutomaton
                    .getStartState(), leftTargetState));
        }

        for (Pair<State, State> sourcePair : rightSourceProgress) {
            State rightSourceState = sourcePair.getLeft();
            State conditionSourceState = sourcePair.getRight();

            State rightTargetState = rightSourceState
                    .getSingleTarget(richSymbol);
            State conditionTargetState;

            if (richSymbol.isLookahead()) {
                conditionTargetState = conditionSourceState
                        .getSingleTarget(richSymbol);
            }
            else {
                conditionTargetState = conditionSourceState
                        .getSingleTarget(richSymbol.getSymbol()
                                .getLookaheadRichSymbol());
            }

            if (rightTargetState != null && conditionTargetState != null) {
                rightTargetProgress.add(new Pair<State, State>(
                        rightTargetState, conditionTargetState));
            }
        }

        if (rightTargetProgress.size() > 0) {
            Pair<State, Set<Pair<State, State>>> targetProgress = new Pair<State, Set<Pair<State, State>>>(
                    leftTargetState, rightTargetProgress);
            State targetState = this.stateMap.get(targetProgress);
            if (targetState == null) {
                targetState = new State(this.newAutomaton);

                this.stateMap.put(targetProgress, targetState);
                this.progressMap.put(targetState, targetProgress);
                this.workSet.add(targetState);
            }
            sourceState.addTransition(richSymbol, targetState);
        }
    }

    Automaton getNewAutomaton() {

        return this.newAutomaton;
    }
}
