package org.sablecc.objectmacro.structure;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.syntax3.node.*;

public class Internal
        extends Param{

    private final AInternal declaration;

    Internal(
            AInternal declaration,
            Macro macro,
            GlobalIndex globalIndex) {

        super(macro, globalIndex);

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        this.declaration = declaration;
    }

    @Override
    public TIdentifier getNameDeclaration() {
        return this.declaration.getName();
    }

    @Override
    public String getName() {
        return this.declaration.getName().getText();
    }

    public AInternal getDeclaration(){
        return this.declaration;
    }
}
