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

import static org.junit.Assert.*;

import org.junit.*;
import org.sablecc.exception.*;

public class TextArgumentTest {

    TextArgument textArgument;

    String text;

    @Test
    public void testTextArgument() {

        // Case with null text
        this.text = null;
        try {
            this.textArgument = new TextArgument(this.text);
            fail("text may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case
        this.text = "new text";
        this.textArgument = new TextArgument(this.text);
        assertEquals("the text argument does not contain the good text",
                this.text, this.textArgument.getText());
    }

}
