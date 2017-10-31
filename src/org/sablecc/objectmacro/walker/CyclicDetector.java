package org.sablecc.objectmacro.walker;

import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.structure.Directive;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.util.Utils;
import org.sablecc.util.ComponentFinder;
import org.sablecc.util.Progeny;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lam on 27/10/17.
 */
public class CyclicDetector
        extends DepthFirstAdapter{

    private GlobalIndex globalIndex;

    private Macro currentMacro;

    private Param currentParam;

    public CyclicDetector(
            GlobalIndex globalIndex){

        this.globalIndex = globalIndex;
    }

    @Override
    public void inAMacro(
            AMacro node) {

        this.currentMacro = this.globalIndex.getMacro(node.getName());
        this.currentMacro.computeIndirectParamReferences();
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.currentMacro = null;
    }

    @Override
    public void inAParam(
            AParam node) {

        this.currentParam = this.currentMacro
                .getParam(node.getName());
    }

    @Override
    public void outAParam(
            AParam node) {

        this.currentParam = null;
    }

    @Override
    public void caseAVarStringPart(
            AVarStringPart node) {

        TIdentifier param_name = new TIdentifier(
                Utils.getVarName(node.getVariable()),
                node.getVariable().getLine(),
                node.getVariable().getPos());

        Param referencedParam = this.currentMacro.getParam(param_name);

        if(this.currentParam != null){

            if(this.currentParam == referencedParam){
                throw CompilerException.selfReference(
                        param_name,
                        this.currentParam.getNameDeclaration());

            }

            if(referencedParam.getIndirectParamReferences().contains(this.currentParam)){
                throw CompilerException.cyclicReference(
                        param_name,
                        this.currentParam.getNameDeclaration());
            }
        }
    }

    @Override
    public void caseAVarStaticValue(
            AVarStaticValue node) {

        Param referencedParam = this.currentMacro.getParam(node.getIdentifier());

        if(this.currentParam != null){

            if(this.currentParam == referencedParam){
                throw CompilerException.selfReference(
                        node.getIdentifier(),
                        this.currentParam.getNameDeclaration());

            }

            if(referencedParam.getIndirectParamReferences().contains(this.currentParam)){
                throw CompilerException.cyclicReference(
                        node.getIdentifier(),
                        this.currentParam.getNameDeclaration());
            }
        }
    }
}
