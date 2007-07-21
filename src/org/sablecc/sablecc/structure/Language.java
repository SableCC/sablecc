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

package org.sablecc.sablecc.structure;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sablecc.sablecc.exception.InternalException;

public class Language {

    private final String name;

    private SortedSet<Token> tokens = new TreeSet<Token>();

    private SortedMap<String, Token> tokenMap = new TreeMap<String, Token>();

    private SortedSet<Group> groups = new TreeSet<Group>();

    private SortedMap<String, Group> groupMap = new TreeMap<String, Group>(
            stringComparator);

    private SortedSet<Selector> selectors = new TreeSet<Selector>();

    private SortedMap<String, Selector> selectorMap = new TreeMap<String, Selector>();

    private SortedSet<Investigator> investigators = new TreeSet<Investigator>();

    private SortedMap<String, Investigator> investigatorMap = new TreeMap<String, Investigator>();

    private SortedSet<State> states = new TreeSet<State>();

    private SortedMap<String, State> stateMap = new TreeMap<String, State>(
            stringComparator);

    private boolean isStable = false;

    /** A comparator for strings. */
    private static final Comparator<String> stringComparator = new Comparator<String>() {

        // allows comparison of null strings
        public int compare(
                String string1,
                String string2) {

            if (string1 == null) {
                return string2 == null ? 0 : -1;
            }

            if (string2 == null) {
                return 1;
            }

            return string1.compareTo(string2);
        }
    };

    public Language(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    public Token getToken(
            String name) {

        return this.tokenMap.get(name);
    }

    public SortedSet<Token> getTokens() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.tokens;
    }

    public Group getGroup(
            String name) {

        return this.groupMap.get(name);
    }

    public SortedSet<Group> getGroups() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.groups;
    }

    public Selector getSelector(
            String name) {

        return this.selectorMap.get(name);
    }

    public SortedSet<Selector> getSelectors() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.selectors;
    }

    public Investigator getInvestigator(
            String name) {

        return this.investigatorMap.get(name);
    }

    public SortedSet<Investigator> getInvestigators() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.investigators;
    }

    public boolean isStable() {

        return this.isStable;
    }

    public State getState(
            String name) {

        return this.stateMap.get(name);
    }

    public SortedSet<State> getStates() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.states;
    }

    public void stabilize() {

        if (this.isStable) {
            throw new InternalException("this language is already stable");
        }

        for (Group group : this.groups) {
            group.stabilize();
        }

        this.tokens = Collections.unmodifiableSortedSet(this.tokens);
        this.tokenMap = Collections.unmodifiableSortedMap(this.tokenMap);
        this.groups = Collections.unmodifiableSortedSet(this.groups);
        this.groupMap = Collections.unmodifiableSortedMap(this.groupMap);
        this.selectors = Collections.unmodifiableSortedSet(this.selectors);
        this.selectorMap = Collections.unmodifiableSortedMap(this.selectorMap);
        this.investigators = Collections
                .unmodifiableSortedSet(this.investigators);
        this.investigatorMap = Collections
                .unmodifiableSortedMap(this.investigatorMap);
        this.states = Collections.unmodifiableSortedSet(this.states);
        this.stateMap = Collections.unmodifiableSortedMap(this.stateMap);

        this.isStable = true;
    }

    public void addGroup(
            String name) {

        if (this.isStable) {
            throw new InternalException("a stable language may not be modified");
        }

        if (this.groupMap.containsKey(name)) {
            throw new InternalException(
                    "this language already includes the group");
        }

        Group group = new Group(name);

        this.groups.add(group);
        this.groupMap.put(name, group);
    }

    public void addToken(
            String name,
            Group group) {

        if (this.isStable) {
            throw new InternalException("a stable language may not be modified");
        }

        if (this.tokenMap.containsKey(name)) {
            throw new InternalException(
                    "this language already includes the token");
        }

        Token token = new Token(name, group);

        this.tokens.add(token);
        this.tokenMap.put(name, token);
    }

    public void addSelector(
            String name) {

        if (this.isStable) {
            throw new InternalException("a stable language may not be modified");
        }

        if (this.selectorMap.containsKey(name)) {
            throw new InternalException(
                    "this language already includes the selector");
        }

        Selector selector = new Selector(name);

        this.selectors.add(selector);
        this.selectorMap.put(name, selector);
    }

    public void addInvestigator(
            String name) {

        if (this.isStable) {
            throw new InternalException("a stable language may not be modified");
        }

        if (this.investigatorMap.containsKey(name)) {
            throw new InternalException(
                    "this language already includes the investigator");
        }

        Investigator investigator = new Investigator(name);

        this.investigators.add(investigator);
        this.investigatorMap.put(name, investigator);
    }

    public void addState(
            String name) {

        if (this.isStable) {
            throw new InternalException("a stable language may not be modified");
        }

        if (this.stateMap.containsKey(name)) {
            throw new InternalException(
                    "this language already includes the state");
        }

        State state = new State(name);

        this.states.add(state);
        this.stateMap.put(name, state);
    }
}
