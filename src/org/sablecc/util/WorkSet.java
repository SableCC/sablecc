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

package org.sablecc.util;

import java.util.*;

import org.sablecc.exception.*;

/**
 * A work set is a special set that returns each of its elements only once
 * through the <code>next()</code> method.
 */
public class WorkSet<T> {

    /** The set of the already returned element. */
    private final Set<T> done = new HashSet<T>();

    /** The set of elements that have not been returned. */
    private final LinkedHashSet<T> toDo = new LinkedHashSet<T>();

    /**
     * Returns <code>true</code> if there is a next element.
     */
    public boolean hasNext() {

        return !this.toDo.isEmpty();
    }

    /**
     * Returns the next element.
     */
    public T next() {

        T next = this.toDo.iterator().next();

        this.toDo.remove(next);
        this.done.add(next);

        return next;
    }

    /**
     * Adds a new element to the work list.
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
