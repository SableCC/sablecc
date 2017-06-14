package org.sablecc.objectmacro.structure;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.syntax3.node.*;

import java.util.*;

public class External
        extends Param {

    private final AParam declaration;

    private final Map<String, Directive> directives = new HashMap<>();

    private final Set<Directive> allDirectives = new LinkedHashSet<>();

    External(
            AParam declaration,
            Macro macro,
            GlobalIndex globalIndex) {

        super(macro, globalIndex);

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        this.declaration = declaration;
    }

    public Directive newDirective(
            ADirective directive) {

        String optionName = directive.getName().getText();
        if (this.directives.containsKey(optionName)) {
            throw CompilerException.duplicateOption(
                    directive, this.directives.get(optionName).getDeclaration());
        }

        Directive newDirective = new Directive(directive, this);
        this.directives.put(
                optionName, newDirective);
        this.allDirectives.add(newDirective);

        return newDirective;
    }

    public Set<Directive> getAllDirectives(){
        return this.allDirectives;
    }

    @Override
    public TIdentifier getNameDeclaration() {
        return this.declaration.getName();
    }

    @Override
    public String getName() {
        return this.declaration.getName().getText();
    }

    public AParam getDeclaration(){
        return this.declaration;
    }
}
