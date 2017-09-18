package org.sablecc.objectmacro.walker;

import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.structure.Insert;
import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.structure.GlobalIndex;
import org.sablecc.objectmacro.structure.Macro;
import org.sablecc.objectmacro.structure.Param;
import org.sablecc.objectmacro.syntax3.node.*;

import java.util.List;

/**
 * Created by lam on 06/09/17.
 */
public class DefinitionCollector
        extends DepthFirstAdapter {

    private final GlobalIndex globalIndex;

    private Macro currentMacro;

    private Param currentParam;

    private Insert currentInsert;

    public DefinitionCollector(
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
        this.currentInsert = this.currentMacro.newInsert(macroReference);
    }

    @Override
    public void inAInsertStringPart(
            AInsertStringPart node) {

        AMacroReference macroReference = (AMacroReference) node.getMacro();
        if(this.currentInsert != null){
            Macro macroReferenced = this.globalIndex.getMacro(
                    this.currentInsert.getDeclaration().getName());

            this.currentInsert = macroReferenced.newInsert(macroReference);
        }else{

            this.currentInsert = this.currentMacro.newInsert(macroReference);
        }

    }

    @Override
    public void outAInsertMacroBodyPart(
            AInsertMacroBodyPart node) {

        this.currentInsert = null;
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
    public void caseAMacroReference(
            AMacroReference node) {

        Macro referencedMacro = this.globalIndex.getMacro(node.getName());
        int nbStaticValues = node.getValues().size();

        if(nbStaticValues != referencedMacro.getAllContexts().size()){
            //TODO Exception
//                throw new CompilerException(
//                        "Incorrect number of arguments", node.getName());
        }
        /**
         *
         * TODO verify macro arguments and string arguments if it corresponds by index order?
         *
         */

        if(this.currentParam != null){
            this.currentParam.addMacroReference(node);
        }

        //In order to verify if there is a cyclic reference in inserts
        for(PStaticValue value : node.getValues()){
            if(value instanceof AStringStaticValue){
                AStringStaticValue stringStaticValue = (AStringStaticValue) value;

                for(PStringPart stringPart : stringStaticValue.getParts()){
                    stringPart.apply(this);
                }
            }
        }
    }

    @Override
    public void caseAStringType(
            AStringType node) {

        this.currentMacro.setParamToString(
                this.currentParam.getNameDeclaration());
    }
}
