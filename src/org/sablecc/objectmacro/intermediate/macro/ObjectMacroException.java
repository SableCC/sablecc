/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

public class ObjectMacroException
        extends RuntimeException {

    private ObjectMacroException(
            String message) {

        super(message);

        if (message == null) {
            throw new RuntimeException("message may not be null");
        }
    }

    private ObjectMacroException(
            String message,
            Throwable cause) {

        super(message, cause);

        if (message == null) {
            throw new RuntimeException("message may not be null");
        }

        if (cause == null) {
            throw new RuntimeException("cause may not be null");
        }
    }

    static ObjectMacroException incorrectType(
            String type,
            String param_name) {

        return new ObjectMacroException(
                new MUserErrorIncorrectType(type, param_name).build());
    }

    static ObjectMacroException macroNull(
            Integer index,
            String paramName) {

        return new ObjectMacroException(
                new MUserErrorMacroNullInList(String.valueOf(index), paramName)
                        .build());
    }

    static ObjectMacroException parameterNull(
            String paramName) {

        return new ObjectMacroException(
                new MUserErrorParameterNull(paramName).build());
    }

    static ObjectMacroException cyclicReference(
            String macroName) {

        return new ObjectMacroException(
                new MUserErrorCyclicReference(macroName).build());
    }

    static ObjectMacroException cannotModify(
            String macroName) {

        return new ObjectMacroException(
                new MUserErrorCannotModify(macroName).build());
    }

    static ObjectMacroException versionNull() {

        return new ObjectMacroException(new MUserErrorVersionNull().build());
    }

    static ObjectMacroException diffMacros() {

        return new ObjectMacroException(
                new MUserErrorVersionsDifferent().build());
    }
}
