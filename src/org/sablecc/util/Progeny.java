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

public abstract class Progeny<T> {

    private final Map<T, Set<T>> childrenMap = new HashMap<T, Set<T>>();

    public Set<T> getChildren(
            T node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        Set<T> children = this.childrenMap.get(node);
        if (children == null) {
            children = Collections.unmodifiableSet(getChildrenNoCache(node));
            this.childrenMap.put(node, children);
        }

        return children;
    }

    protected abstract Set<T> getChildrenNoCache(
            T node);
}
