/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

public class InternalException
        extends RuntimeException {

    InternalException(
            String message) {

        super(message);

        if (message == null) {
            throw new RuntimeException("message may not be null");
        }
    }

    InternalException(
            String message,
            Throwable cause) {

        super(new MUserErrorInternalException(message).build(), cause);

        if (message == null) {
            throw new RuntimeException("message may not be null");
        }

        if (cause == null) {
            throw new RuntimeException("cause may not be null");
        }
    }
}
