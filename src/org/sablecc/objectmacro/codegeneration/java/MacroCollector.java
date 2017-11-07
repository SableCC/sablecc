package org.sablecc.objectmacro.codegeneration.java;

import org.sablecc.objectmacro.codegeneration.java.macro.MMacro;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.AMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.TString;
import org.sablecc.objectmacro.util.Utils;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by lam on 05/10/17.
 */
public class MacroCollector
        extends DepthFirstAdapter{

    private final Map<String, MMacro> macros;

    public MacroCollector(
            Map<String, MMacro> macros){

        this.macros = macros;
    }

    private String string(
            TString tString) {

        String string = tString.getText();
        int length = string.length();
        return string.substring(1, length - 1);
    }

    private String buildMacroName(
            LinkedList<TString> names){

        StringBuilder paramName = new StringBuilder();
        for(TString partName : names){
            paramName.append(Utils.toCamelCase(string(partName)));
        }

        return paramName.toString();
    }

    @Override
    public void inAMacro(
            AMacro node) {

        String macroName = buildMacroName(node.getNames());
        this.macros.put(macroName, new MMacro(macroName));
    }
}
