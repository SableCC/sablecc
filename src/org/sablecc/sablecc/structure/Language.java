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

/**
 * A language is defined by a name.It can also contain tokens, groups,
 * selectors, investigators and staes. There is only one language declaration in
 * each grammar specification.
 */
public class Language {

    /** The name of this language. */
    private final String name;

    /** The tokens of this language. */
    private SortedSet<Token> tokens = new TreeSet<Token>();

    /** A sorted map that maps each token with its name. */
    private SortedMap<String, Token> tokenMap = new TreeMap<String, Token>();

    /** The groups of this language. */
    private SortedSet<Group> groups = new TreeSet<Group>();

    /** A sorted map that maps each group with its name. */
    private SortedMap<String, Group> groupMap = new TreeMap<String, Group>(
            stringComparator);

    /** The selectors of this language. */
    private SortedSet<Selector> selectors = new TreeSet<Selector>();

    /** A sorted map that maps each selector with its name. */
    private SortedMap<String, Selector> selectorMap = new TreeMap<String, Selector>();

    /** The investigators of this language. */
    private SortedSet<Investigator> investigators = new TreeSet<Investigator>();

    /** A sorted map that maps each investigator with its name. */
    private SortedMap<String, Investigator> investigatorMap = new TreeMap<String, Investigator>();

    /** The states of this language. */
    private SortedSet<State> states = new TreeSet<State>();

    /** A sorted map that maps each state with its name. */
    private SortedMap<String, State> stateMap = new TreeMap<String, State>(
            stringComparator);

    /** A stability status for this language. */
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

    /**
     * Constructs a new language with the provided name.
     * 
     * @param name
     *            the name.
     * @throws InternalException
     *             if the provided name is <code>null</code>.
     */
    public Language(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        this.name = name;
    }

    /**
     * Returns the name of this language.
     * 
     * @return the name.
     */
    public String getName() {

        return this.name;
    }

    /**
     * Returns whether this language has a token corresponding to the provided
     * name.
     * 
     * @param name
     *            the name.
     * @return <code>true</code> if this instance has a token with the same
     *         name, <code>false</code> otherwise.
     * @throws InternalException
     *             if the provided name is <code>null</code>.
     */
    public boolean hasToken(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        return this.tokenMap.containsKey(name);
    }

    /**
     * Returns the token corresponding to the provided name.
     * 
     * @param name
     *            the name.
     * @return the corresponding token.
     * @throws InternalException
     *             if the provided name is <code>null</code> or if there's no
     *             corresponding token.
     */
    public Token getToken(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        Token token = this.tokenMap.get(name);

        if (token == null) {
            throw new InternalException(
                    "this language does not contain token '" + name + "'");
        }

        return token;
    }

    /**
     * Returns the tokens of this language.
     * 
     * @return the tokens.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<Token> getTokens() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.tokens;
    }

    /**
     * Returns whether this language has a group corresponding to the provided
     * name.
     * 
     * @param name
     *            the name.
     * @return <code>true</code> if this instance has a group with the same
     *         name, <code>false</code> otherwise.
     */
    public boolean hasGroup(
            String name) {

        return this.groupMap.containsKey(name);
    }

    /**
     * Returns the group corresponding to the provided name.
     * 
     * @param name
     *            the name.
     * @return the corresponding group.
     * @throws InternalException
     *             if there's no corresponding group.
     */
    public Group getGroup(
            String name) {

        Group group = this.groupMap.get(name);

        if (group == null) {
            throw new InternalException(
                    "this language does not contain group '" + name + "'");
        }

        return group;
    }

    /**
     * Returns the groups of this language.
     * 
     * @return the groups.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<Group> getGroups() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.groups;
    }

    /**
     * Returns whether this language has a selector corresponding to the
     * provided name.
     * 
     * @param name
     *            the name.
     * @return <code>true</code> if this instance has a selector with the same
     *         name, <code>false</code> otherwise.
     * @throws InternalException
     *             if the provided name is <code>null</code>.
     */
    public boolean hasSelector(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        return this.selectorMap.containsKey(name);
    }

    /**
     * Returns the selector corresponding to the provided name.
     * 
     * @param name
     *            the name.
     * @return the corresponding selector.
     * @throws InternalException
     *             if the provided name is <code>null</code> or if there's no
     *             corresponding selector.
     */
    public Selector getSelector(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        Selector selector = this.selectorMap.get(name);

        if (selector == null) {
            throw new InternalException(
                    "this language does not contain selector '" + name + "'");
        }

        return selector;
    }

    /**
     * Returns the selectors of this language.
     * 
     * @return the selectors.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<Selector> getSelectors() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.selectors;
    }

    /**
     * Returns whether this language has an investigator corresponding to the
     * provided name.
     * 
     * @param name
     *            the name.
     * @return <code>true</code> if this instance has an investigator with the
     *         same name, <code>false</code> otherwise.
     * @throws InternalException
     *             if the provided name is <code>null</code>.
     */
    public boolean hasInvestigator(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        return this.investigatorMap.containsKey(name);
    }

    /**
     * Returns the investigator corresponding to the provided name.
     * 
     * @param name
     *            the name.
     * @return the corresponding investigator.
     * @throws InternalException
     *             if the provided name is <code>null</code> or if there's no
     *             corresponding investigator.
     */
    public Investigator getInvestigator(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        Investigator investigator = this.investigatorMap.get(name);

        if (investigator == null) {
            throw new InternalException(
                    "this language does not contain investigator '" + name
                            + "'");
        }

        return investigator;
    }

    /**
     * Returns the investigators of this language.
     * 
     * @return the investigators.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<Investigator> getInvestigators() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.investigators;
    }

    /**
     * Returns whether this language has a state corresponding to the provided
     * name.
     * 
     * @param name
     *            the name.
     * @return <code>true</code> if this instance has a state with the same
     *         name, <code>false</code> otherwise.
     */
    public boolean hasState(
            String name) {

        return this.stateMap.containsKey(name);
    }

    /**
     * Returns the state corresponding to the provided name.
     * 
     * @param name
     *            the name.
     * @return the corresponding state.
     * @throws InternalException
     *             if there's no corresponding state.
     */
    public State getState(
            String name) {

        State state = this.stateMap.get(name);

        if (state == null) {
            throw new InternalException(
                    "this language does not contain state '" + name + "'");
        }

        return state;
    }

    /**
     * Returns the states of this language.
     * 
     * @return the states.
     * @throws InternalException
     *             if this instance is not stable.
     */
    public SortedSet<State> getStates() {

        if (!this.isStable) {
            throw new InternalException("this language is not stable yet");
        }

        return this.states;
    }

    /**
     * Returns whether this language is stable or not.
     * 
     * @return <code>true</code> if this language is stable,
     *         <code>false</code> otherwise.
     */
    public boolean isStable() {

        return this.isStable;
    }

    /**
     * Stabilizes this language
     * 
     * @throws InternalException
     *             if this instance is already stable.
     */
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

    /**
     * Adds a token to this language.
     * 
     * @param name
     *            the name of the token.
     * @param group
     *            the group of the token.
     * @throws InternalException
     *             if the provided name or group is <code>null</code>, if
     *             this language is stable or if it already contains a token
     *             with the same name.
     */
    public void addToken(
            String name,
            Group group) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

        if (group == null) {
            throw new InternalException("group may not be null");
        }

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

    /**
     * Adds a group to this language.
     * 
     * @param name
     *            the name of the group.
     * @throws InternalException
     *             if this language is stable or if it already contains a group
     *             with the same name.
     */
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

    /**
     * Adds a selector to this language.
     * 
     * @param name
     *            the name of the selector.
     * @throws InternalException
     *             if the provided name <code>null</code>, if this language
     *             is stable or if it already contains a selector with the same
     *             name.
     */
    public void addSelector(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

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

    /**
     * Adds a investigator to this language.
     * 
     * @param name
     *            the name of the investigator.
     * @throws InternalException
     *             if the provided name <code>null</code>, if this language
     *             is stable or if it already contains a investigator with the
     *             same name.
     */
    public void addInvestigator(
            String name) {

        if (name == null) {
            throw new InternalException("name may not be null");
        }

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

    /**
     * Adds a state to this language.
     * 
     * @param name
     *            the name of the state.
     * @throws InternalException
     *             if this language is stable or if it already contains a state
     *             with the same name.
     */
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
