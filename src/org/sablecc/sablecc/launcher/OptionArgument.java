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

package org.sablecc.sablecc.launcher;

import org.sablecc.sablecc.exception.InternalException;

/**
 * An option argument is composed of the option and its operand.
 */
class OptionArgument {

    /** The option for this option argument. */
    private Option option;

    /** The operand for this option argument. */
    private String operand;

    /**
     * Constructs an option argument with the provided option and operand.
     */
    OptionArgument(
            Option option,
            String operand) {

        if (option == null) {
            throw new InternalException("option may not be null");
        }

        this.option = option;
        this.operand = operand;
    }

    /**
     * Returns the option of this option argument.
     */
    Option getOption() {

        return this.option;
    }

    /**
     * Returns the operand of this option argument.
     */
    String getOperand() {

        return this.operand;
    }
}
