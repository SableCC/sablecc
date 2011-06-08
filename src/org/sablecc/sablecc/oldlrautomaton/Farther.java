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

package org.sablecc.sablecc.oldlrautomaton;

import java.util.*;

public class Farther
        implements Ahead {

    private static Map<Integer, Farther> distanceToFartherMap = new LinkedHashMap<Integer, Farther>();

    private final int distance;

    private Farther(
            int distance) {

        this.distance = distance;
    }

    public static Farther get(
            int distance) {

        Farther farther = distanceToFartherMap.get(distance);
        if (farther == null) {
            farther = new Farther(distance);
            distanceToFartherMap.put(distance, farther);
        }
        return farther;
    }

    public int getDistance() {

        return this.distance;
    }

    @Override
    public String toString() {

        return "Farther(" + this.distance + ")";
    }
}
