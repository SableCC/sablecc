package org.sablecc.objectmacro.exception;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.errormessage.MIncorrectType;
import org.sablecc.objectmacro.errormessage.MMacroNullInList;
import org.sablecc.objectmacro.errormessage.MParameterNull;

/**
 * Created by lam on 10/10/17.
 */
public class ObjectMacroException
        extends RuntimeException{

    private ObjectMacroException(
            String message){

        super(message);

        if(message == null){
            throw new InternalException("message may not be null");
        }
    }

    private ObjectMacroException(
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

    public static ObjectMacroException incorrectType(
            String type,
            String param_name){


        return new ObjectMacroException(
                new MIncorrectType(type, param_name).toString());
    }

    public static ObjectMacroException macroNull(
            Integer index,
            String paramName){

        return new ObjectMacroException(
                new MMacroNullInList(String.valueOf(index), paramName).toString());
    }

    public static ObjectMacroException parameterNull(
            String paramName){

        return new ObjectMacroException(new MParameterNull(paramName).toString());
    }

}
