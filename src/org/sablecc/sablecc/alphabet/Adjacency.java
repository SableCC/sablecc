/* This file is part of SableCC (http://sablecc.org/).
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

package org.sablecc.sablecc.alphabet;

import org.sablecc.sablecc.exception.InternalException;

/**
 * Instances implementing this interface are used to merge adjacent intervals.
 */

public interface Adjacency<T extends Comparable<? super T>> {

    /**
     * Returns whether an element T can be adjacent to another T.
     * 
     * In situations where there is no intuitive definition of adjacency, this
     * method must return <code>false</code>.
     * 
     * This method must return <code>true</code> in order for other methods of
     * this interface to work.
     * 
     * The result of this method is always the same (in other words: constant)
     * on a given instance of this interface.
     * 
     * @return <code>true</code> if the two T can be adjacent;
     *         <code>false</code> otherwise.
     */
    boolean isSequential();

    /**
     * Returns whether two bounds are adjacents. For example, if T is an integer
     * type, it returns <code>true</code> when
     * <code>(bound1 + 1) == bound2</code>.
     * 
     * @param bound1
     *            a bound to compare.
     * @param bound2
     *            a bound to compare.
     * @return <code>true</code> if the two bounds are adjacents;
     *         <code>false</code> otherwise.
     * @throws InternalException
     *             if <code>isSequential</code> is <code>false</code>.
     */
    boolean isAdjacent(
            T bound1,
            T bound2);

    /**
     * Returns the element T preceding the current instance. Generally bound - 1
     * (or its equivalent).
     * 
     * Throws an exception when <code>isSequential</code> returns
     * <code>false</code>.
     * 
     * @param bound
     *            a bound to compare.
     * @return a T previous to the current instance.
     * @throws InternalException
     *             if <code>isSequential</code> is <code>false</code>.
     */
    T previous(
            T bound);

    /**
     * Returns the element T following the current instance. Generally bound + 1
     * (or its equivalent).
     * 
     * Throws an exception when <code>isSequential</code> returns
     * <code>false</code>.
     * 
     * @param bound
     *            a bound to compare.
     * @return a T following the current instance.
     * @throws InternalException
     *             if <code>isSequential</code> is <code>false</code>.
     */
    T next(
            T bound);
}
