package org.sablecc.objectmacro.walker;

import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.util.Utils;

/**
 * Created by lam on 06/09/17.
 */
public class ParamReferenceCollector
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private Macro currentMacro;

    private Param currentParam;

    public ParamReferenceCollector(
            GlobalIndex globalIndex) {

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
    public void caseAVarStringPart(
            AVarStringPart node) {

        if(this.currentParam != null){
            this.currentParam.addParamReference(
                    new TIdentifier(
                            Utils.getVarName(
                                    node.getVariable()), node.getVariable().getLine(), node.getVariable().getPos()));
        }
    }

    @Override
    public void caseAVarStaticValue(
            AVarStaticValue node) {

        if(this.currentParam != null){
            this.currentParam.addParamReference(node.getIdentifier());
        }
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
    public void caseAStringType(
            AStringType node) {

        this.currentMacro.setParamToString(
                this.currentParam.getNameDeclaration());
    }
}
