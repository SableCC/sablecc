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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.sablecc.sablecc.exception.InvalidArgumentException;

@SuppressWarnings("unused")
public class ArgumentsTest {

    private String[] args;

    private final String invalidOption = "-z";

    private final String textArgument = "textArgument";

    private String validOption;

    private final Option checkOnly = Option.CHECK_ONLY;

    private final Option verbose = Option.VERBOSE;

    private ArgumentCollection argumentCollection;

    @Test
    public void testArguments()
            throws InvalidArgumentException {

        this.args = new String[0];

        // Case with no args
        this.argumentCollection = new ArgumentCollection(this.args);

        // Case with null args
        this.args = new String[1];
        this.args[0] = null;
        try {
            this.argumentCollection = new ArgumentCollection(this.args);
            fail("invalid argument : null");
        }
        catch (InvalidArgumentException e) {
            // Expected
        }

        // Case with incorrect args
        this.args[0] = this.invalidOption;
        try {
            this.argumentCollection = new ArgumentCollection(this.args);
            fail("invalid argument : invalid option");
        }
        catch (InvalidArgumentException e) {
            // Expected
        }

        // Case with missing operand
        Option destination = Option.DESTINATION;
        this.args[0] = "--".concat(destination.getLongName());
        try {
            this.argumentCollection = new ArgumentCollection(this.args);
            fail("invalid argument : destination is missing a directory operand");
        }
        catch (InvalidArgumentException e) {
            // Expected
        }

        // Case with valid args
        this.args = new String[2];
        this.args[0] = "-".concat(this.checkOnly.getShortName());

        this.args[1] = "--".concat(this.verbose.getLongName());

        this.argumentCollection = new ArgumentCollection(this.args);

        assertEquals("The first argument is invalid", this.checkOnly,
                this.argumentCollection.getOptionArguments().get(0).getOption());

        assertEquals("The second argument is invalid", this.verbose,
                this.argumentCollection.getOptionArguments().get(1).getOption());

        assertEquals("some of the argumentCollection have not been treated",
                this.args.length, this.argumentCollection.getOptionArguments()
                        .size());

        // Case with text argumentCollection
        this.args = new String[1];
        this.args[0] = this.textArgument;
        this.argumentCollection = new ArgumentCollection(this.args);
        assertEquals(
                "the argumentCollection should not contain optionArguments", 0,
                this.argumentCollection.getOptionArguments().size());
        assertEquals("the textArgument has not been treated",
                this.textArgument, this.argumentCollection.getTextArguments()
                        .get(0).getText());

        // Case with valid option an a text argument
        this.args = new String[2];
        this.args[0] = "--".concat(this.verbose.getLongName());
        this.args[1] = this.textArgument;
        this.argumentCollection = new ArgumentCollection(this.args);
        assertEquals("The first argument is invalid", this.verbose,
                this.argumentCollection.getOptionArguments().get(0).getOption());

        assertEquals("the textArgument has not been treated",
                this.textArgument, this.argumentCollection.getTextArguments()
                        .get(0).getText());
    }
}
