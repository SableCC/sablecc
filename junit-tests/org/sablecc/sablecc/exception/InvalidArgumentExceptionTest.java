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

package org.sablecc.sablecc.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class InvalidArgumentExceptionTest {

    InvalidArgumentException exception;

    String message;

    Throwable cause;

    @Before
    public void setUp() {

        this.message = "valid message";
        this.cause = new Throwable();
    }

    @Test
    public void testInvalidArgumentExceptionString() {

        // Case with null message
        String nullMessage = null;
        try {
            this.exception = new InvalidArgumentException(nullMessage);
            fail("message may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case
        this.exception = new InvalidArgumentException(this.message);
        assertEquals("Invalid exception message", this.message, this.exception
                .getMessage());
    }

    @Test
    public void testInvalidArgumentExceptionStringThrowable() {

        // Case with null message
        String nullMessage = null;

        try {
            this.exception = new InvalidArgumentException(nullMessage,
                    this.cause);
            fail("message may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with null cause
        Throwable nullCause = null;
        try {
            this.exception = new InvalidArgumentException(this.message,
                    nullCause);
            fail("a cause may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical case
        this.exception = new InvalidArgumentException(this.message, this.cause);
        assertEquals("Invalid exception message", this.message, this.exception
                .getMessage());
        assertEquals("Invalid exception cause", this.cause, this.exception
                .getCause());
    }

}
