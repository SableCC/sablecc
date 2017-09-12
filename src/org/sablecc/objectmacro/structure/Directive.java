package org.sablecc.objectmacro.structure;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.syntax3.node.ADirective;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lam on 06/09/17.
 */
public class Directive {

    private ADirective declaration;

    //Use to check if param exist
    private Map<String, Param> referencedParams = new LinkedHashMap<>();

    public Directive(
            ADirective declaration){

        this.declaration = declaration;
    }

    public void addReferencedParam(
            Param referencedParam){

        if(this.referencedParams.containsKey(referencedParam.getName())){
            throw new InternalException("Param of name "+ referencedParam.getName() + " cannot be referenced multiple times");
        }

        this.referencedParams.put(referencedParam.getName(), referencedParam);
    }

    public Map<String, Param> getReferencedParams(){

        return this.referencedParams;
    }

    public ADirective getDeclaration(){

        return this.declaration;
    }

    public String getName(){

        return this.declaration.getName().getText();
    }
}
