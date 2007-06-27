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

public class WorkSet<T> {

    /** The list of the elements already treated. */
    private final Set<T> done = new HashSet<T>();

    /** The list of the elements left to be treated. */
    private final LinkedHashSet<T> toDo = new LinkedHashSet<T>();

    /**
     * Indicates if an element has a next one.
     * 
     * @return boolean a boolean value indicating whether the element has a next
     *         one or not.
     */
    public boolean hasNext() {

        return !this.toDo.isEmpty();
    }

    /**
     * Treats a new element, adding it to the done list.
     * 
     * @return T the treated element.
     */
    public T next() {

        T next = this.toDo.iterator().next();

        this.toDo.remove(next);
        this.done.add(next);

        return next;
    }

    /**
     * Adds a new element to be treated.
     * 
     * @param element
     *            the new element to be treated.
     * @throws InternalException
     *             if the element is <code>null</code>.
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
