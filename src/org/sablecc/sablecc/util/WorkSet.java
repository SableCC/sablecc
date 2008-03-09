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

package org.sablecc.sablecc.util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.sablecc.sablecc.exception.InternalException;

/**
 * A work set is a set of element to work on. This class provides various
 * methods to maniplutate work sets.
 */
public class WorkSet<T> {

    /** The list of the elements already treated. */
    private final Set<T> done = new HashSet<T>();

    /** The list of the elements left to be treated. */
    private final LinkedHashSet<T> toDo = new LinkedHashSet<T>();

    /**
     * Indicates if an element has a next one.
     */
    public boolean hasNext() {

        return !this.toDo.isEmpty();
    }

    /**
     * Treats a new element, adding it to the done list.
     */
    public T next() {

        T next = this.toDo.iterator().next();

        this.toDo.remove(next);
        this.done.add(next);

        return next;
    }

    /**
     * Adds a new element to be treated.
     */
    public void add(
            T element) {

        if (element == null) {
            throw new InternalException("element may not be null");
        }

        if (!this.done.contains(element)) {
            this.toDo.add(element);
        }
    }
}
