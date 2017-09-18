package org.sablecc.objectmacro.walker;

import org.sablecc.objectmacro.structure.Directive;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.util.Utils;

/**
 * Created by lam on 07/09/17.
 */
public class DirectiveCollector
        extends DepthFirstAdapter {

    private GlobalIndex globalIndex;

    private Macro currentMacro;

    private Param currentParam;

    private Directive currentDirective;

    public DirectiveCollector(
            GlobalIndex globalIndex){

        this.globalIndex = globalIndex;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.currentMacro = this.globalIndex.getMacro(node.getName());
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentMacro = null;
    }

    @Override
    public void inAParam(
            AParam node) {

        this.currentParam = this.currentMacro.getParam(node.getName());
    }

    @Override
    public void outAParam(
            AParam node) {

        this.currentParam = null;
    }

    @Override
    public void inADirective(
            ADirective node) {

        this.currentDirective = this.currentParam.newDirective(node);
    }

    @Override
    public void outADirective(
            ADirective node) {

        this.currentDirective = null;
    }

    @Override
    public void caseAVarStaticValue(
            AVarStaticValue node) {

        if(this.currentDirective == null){
            return;
        }

        Param param = this.currentMacro.getParam(node.getIdentifier());
        this.currentDirective.addReferencedParam(param);
    }

    @Override
    public void caseAVarStringPart(
            AVarStringPart node) {

        if(this.currentDirective == null){
            return;
        }

        String name = Utils.getVarName(node.getVariable());
        TIdentifier varName = new TIdentifier(name);
        Param param = this.currentMacro.getParam(varName);

        this.currentDirective.addReferencedParam(param);
    }
}
