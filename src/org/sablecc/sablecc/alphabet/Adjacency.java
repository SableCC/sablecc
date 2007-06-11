/* This file is part of SableCC (http://sablecc.org/).
 * 
 * See the NOTICE file distributed with this work for copyright information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sablecc.sablecc.alphabet;

/*
 * Instances implementing this interface are used to merge adjacent intervals.
 */

public interface Adjacency<T extends Comparable<? super T>> {
    /*
     * Returns true if a T can be adjacent to another T.
     * 
     * In situations where there is no intuitive definition of adjacency, this
     * method must return false.
     * 
     * This method must return true in order for other methods of this interface
     * to work.
     * 
     * The result of this method is always the same (in other words: constant)
     * on a given instance of this interface
     */
    boolean isSequential();

    /*
     * Returns true if bound1 is adjacent to bound2. For example, if T is an
     * integer type, it returns true when (bound1 + 1) == bound2.
     * 
     * Throws a RuntimeException when isSequential is false;
     */
    boolean isAdjacent(
            T bound1,
            T bound2);

    /*
     * Returns bound - 1 (or its equivalent).
     * 
     * Throws a RuntimeException when isSequential is false.
     */
    T previous(
            T bound);

    /*
     * Returns bound + 1 (or its equivalent).
     * 
     * Throws a RuntimeException when isSequential is false.
     */
    T next(
            T bound);
}
