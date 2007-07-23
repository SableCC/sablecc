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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;
import org.sablecc.sablecc.syntax3.lexer.Lexer;
import org.sablecc.sablecc.syntax3.lexer.LexerException;
import org.sablecc.sablecc.syntax3.node.Token;

public class SemanticExceptionTest {

    SemanticException exception;

    Lexer lexer = new Lexer(new PushbackReader(new StringReader("token")));

    Token token;

    String message = "valid message";

    Throwable cause = new Throwable();

    @Before
    public void setUp() {

        try {
            this.token = this.lexer.next();
        }
        catch (IOException e) {
            fail("IOException has been thrown");
        }
        catch (LexerException e) {
            fail("LexerException has been thrown");
        }

    }

    @Test
    public void testSemanticExceptionStringToken() {

        // Case with null Message
        String nullMessage = null;
        try {
            this.exception = new SemanticException(nullMessage, this.token);
            fail("message may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical Case
        this.exception = new SemanticException(this.message, this.token);
        assertEquals("semanticException has an invalid token", this.token,
                this.exception.getToken());
    }

    @Test
    public void testSemanticExceptionStringTokenThrowable() {

        // Case with null Message
        String nullMessage = null;
        try {
            this.exception = new SemanticException(nullMessage, this.token,
                    this.cause);
            fail("message may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Case with null Cause
        Throwable nullCause = null;
        try {
            this.exception = new SemanticException(this.message, this.token,
                    nullCause);
            fail("cause may not be null");
        }
        catch (InternalException e) {
            // Expected
        }

        // Typical Case
        this.exception = new SemanticException(this.message, this.token,
                this.cause);
        assertEquals("semanticException has an invalid token", this.token,
                this.exception.getToken());

    }

}
