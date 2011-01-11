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

import junit.framework.*;

import org.sablecc.exception.*;

public class OptionArgumentTest
        extends TestCase {

    OptionArgument optionArgument;

    Option option;

    String operand;

    @Override
    protected void setUp()
            throws Exception {

        this.option = Option.DESTINATION;

        this.operand = "operand";

        this.optionArgument = new OptionArgument(this.option, this.operand);
    }

    public void testOptionArgument() {

        // Case with null option
        Option nullOption = null;
        try {
            this.optionArgument = new OptionArgument(nullOption, this.operand);
            fail("option may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        assertEquals("invalid option", this.option,
                this.optionArgument.getOption());
        assertEquals("invalid operand", this.operand,
                this.optionArgument.getOperand());
    }
}
