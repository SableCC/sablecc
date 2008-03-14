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

/**
 * An invalid argument exception is thrown when the user execute the software
 * providing an unappropriate argument.
 * <p>
 * In the source code of the software, this exception should be thrown when we
 * detect that the program was launched with a bogus option or an invalid
 * argument.
 */
@SuppressWarnings("serial")
public class InvalidArgumentException
        extends Exception {

    /**
     * Constructs a new invalid argument exception with the provided error
     * message.
     */
    public InvalidArgumentException(
            String message) {

        super(message);

        if (message == null) {
            throw new InternalException("message may not be null");
        }
    }

    /**
     * Constructs a new invalid argument exception with the provided error
     * message and cause.
     */
    public InvalidArgumentException(
            String message,
            Throwable cause) {

        super(message, cause);

        if (message == null) {
            throw new InternalException("message may not be null");
        }

        if (cause == null) {
            throw new InternalException("cause may not be null");
        }
    }

}
