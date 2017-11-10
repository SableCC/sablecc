package org.sablecc.objectmacro.structure;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.syntax3.node.ADirective;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

/**
 * Created by lam on 06/09/17.
 */
public class Directive {

    private final Param parent;

    private final ADirective declaration;

    Directive(
            ADirective declaration,
            Param parent){

        if(parent == null){
            throw new InternalException("parent may not be null here");
        }

        if(declaration == null){
            throw new InternalException("declaration may not be null here");
        }

        this.declaration = declaration;
        this.parent = parent;

    }

    public void addParamReference(
            TIdentifier name){

        this.parent.getParent().getParam(name);
    }

    public ADirective getDeclaration(){

        return this.declaration;
    }

    public String getName(){

        return this.declaration.getName().getText();
    }
}
