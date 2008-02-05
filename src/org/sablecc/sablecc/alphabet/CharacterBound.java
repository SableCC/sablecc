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

package org.sablecc.sablecc.alphabet;

import org.sablecc.sablecc.exception.InternalException;

/**
 * This class encapsulates a character bound.
 */
public class CharacterBound
        extends GenericBound<Character> {

    /** Constructs a character bound with the provided value. */
    public CharacterBound(
            char value) {

        super(value);
    }

    /** Returns the predecessor of this character bound. */
    @Override
    public Bound getPredecessor() {

        if ((char) (getValue() - 1) > getValue()) {
            throw new InternalException("underflow");
        }

        return new CharacterBound((char) (getValue() - 1));
    }

    /** Returns the successor of this character bound. */
    @Override
    public Bound getSuccessor() {

        if ((char) (getValue() + 1) < getValue()) {
            throw new InternalException("overflow");
        }

        return new CharacterBound((char) (getValue() + 1));
    }
}
