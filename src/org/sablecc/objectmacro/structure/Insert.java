package org.sablecc.objectmacro.structure;

import org.sablecc.objectmacro.syntax3.node.AMacroReference;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lam on 06/09/17.
 */
public class Insert {
    private Macro referencedMacro;

    private Macro parentMacro;

    private AMacroReference declaration;

    public Insert(
            Macro referencedMacro,
            Macro parentMacro,
            AMacroReference declaration){

        this.referencedMacro = referencedMacro;
        this.parentMacro = parentMacro;
        this.declaration = declaration;
    }

    public Macro getReferencedMacro(){

        return this.referencedMacro;
    }

    public AMacroReference getDeclaration(){

        return this.declaration;
    }
}
