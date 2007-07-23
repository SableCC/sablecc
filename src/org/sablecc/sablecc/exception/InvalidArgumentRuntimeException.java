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
 * An invalid argument runtime exception is thrown during the execution of the
 * software when the user provided a bogus option or argument when starting the
 * program.
 */

public class InvalidArgumentRuntimeException
        extends RuntimeException {

    /** Serial version identifier for serialization */
    private static final long serialVersionUID = -1526259558916228310L;

    /**
     * Constructs a new invalid argument runtime exception with the provided
     * error message.
     * 
     * @param message
     *            the error message.
     * @throws InternalException
     *             if the message is <code>null</code>.
     */
    public InvalidArgumentRuntimeException(
            String message) {

        super(message);

        if (message == null) {
            throw new InternalException("message may not be null");
        }
    }

    /**
     * Constructs a new invalid argument runtime exception with the provided
     * error message and cause.
     * 
     * @param message
     *            the error message.
     * @param cause
     *            the cause.
     * @throws InternalException
     *             if the message or the cause is <code>null</code>.
     */
    public InvalidArgumentRuntimeException(
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
