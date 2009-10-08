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

public class PairExtractor<T> {

    private final Set<Pair<T, T>> pairSet;

    public PairExtractor(
            Set<T> set) {

        ArrayList<T> array = new ArrayList<T>(set);
        int size = array.size();

        Set<Pair<T, T>> pairSet = new LinkedHashSet<Pair<T, T>>();

        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                pairSet.add(new Pair<T, T>(array.get(i), array.get(j)));
            }
        }

        this.pairSet = Collections.unmodifiableSet(pairSet);
    }

    public Set<Pair<T, T>> getPairs() {

        return this.pairSet;
    }
}
