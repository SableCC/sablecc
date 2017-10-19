package org.sablecc.objectmacro.walker;

import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.structure.Insert;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.node.*;
import org.sablecc.objectmacro.util.Utils;

import java.util.List;

/**
 * Created by lam on 06/09/17.
 */
public class MacroReferenceCollector
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private Macro currentMacro;

    private Param currentParam;

    public MacroReferenceCollector(
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
    public void inAInsertMacroBodyPart(
            AInsertMacroBodyPart node) {

        AMacroReference macroReference = (AMacroReference) node.getMacroReference();
        this.currentMacro.newInsert(macroReference);
    }

    @Override
    public void inAInsertStringPart(
            AInsertStringPart node) {

        AMacroReference macroReference = (AMacroReference) node.getMacro();
        this.currentMacro.newInsert(macroReference);
    }

    @Override
    public void caseAMacroReference(
            AMacroReference node) {

        if(this.currentParam != null){
            this.currentParam.addMacroReference(node);
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
