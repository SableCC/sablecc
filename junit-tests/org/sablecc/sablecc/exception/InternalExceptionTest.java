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

import org.junit.Test;

public class InternalExceptionTest {

    @Test
    @SuppressWarnings("unused")
    public void testInternalExceptionString() {

        // Initializing a good internal exception.
        InternalException goodInternalException = new InternalException(
                "Not null");

        // Testing no thrown exception when the message is not null.
        try {
            goodInternalException = new InternalException("Valid message");
        }
        catch (InternalException e) {
            fail("InternalException not expected here.");
        }

        // Testing thrown exception when the message is null.
        try {
            InternalException nullMessageInternalException = new InternalException(
                    null);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // Expected
        }

        // Verifying validity provided message is taken correctly.
        assertEquals("Invalid exception message", "Valid message",
                goodInternalException.getMessage());
    }

    @Test
    @SuppressWarnings("unused")
    public void testInternalExceptionStringThrowable() {

        Throwable cause = new Throwable();

        // Initializing a good internal exception.
        InternalException goodInternalException = new InternalException(
                "Not null", cause);

        // Testing no thrown exception when message and cause not null.
        try {
            goodInternalException = new InternalException("Valid message",
                    cause);
        }
        catch (InternalException e) {
            fail("InternalException not expected here.");
        }

        // Testing thrown exception when message is null, cause is good.
        try {
            InternalException nullMessageInternalException = new InternalException(
                    null, cause);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // Expected
        }

        // Testing thrown exception when cause is null, message is good.
        try {
            InternalException nullCauseInternalException = new InternalException(
                    "Message", null);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // Expected
        }

        // Testing thrown exception when message and cause are null.
        try {
            InternalException nullMessageAndCauseInternalException = new InternalException(
                    null, null);
            fail("An InternalException should be thrown.");
        }
        catch (InternalException e) {
            // Expected
        }

        // Verifying validity provided message is taken correctly.
        assertEquals("Invalid exception message", "Valid message",
                goodInternalException.getMessage());

        // Verifying validity provided message is taken correctly.
        assertEquals("Invalid exception cause", cause, goodInternalException
                .getCause());

    }

}
