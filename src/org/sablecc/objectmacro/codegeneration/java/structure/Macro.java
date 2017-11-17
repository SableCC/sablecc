package org.sablecc.objectmacro.codegeneration.java.structure;

import org.sablecc.objectmacro.codegeneration.java.macro.MMacro;

import java.util.List;

/**
 * Created by lam on 17/11/17.
 */
public class Macro {

    private final MMacro macro;

    private final List<String> parameters;

    private final List<String> internals;

    public Macro(
            MMacro macro,
            List<String> parameters,
            List<String> internals){

        this.macro = macro;
        this.parameters = parameters;
        this.internals = internals;
    }

    public List<String> getInternals() {

        return internals;
    }

    public List<String> getParameters() {

        return parameters;
    }

    public MMacro getMacro() {

        return macro;
    }
}
