/* This file is part of SableCC ( http://sablecc.org ).
 *
 * See the NOTICE file distributed with this work for copyright information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sablecc.objectmacro.codegeneration.java;

import org.sablecc.objectmacro.codegeneration.java.macro.MMacro;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.intermediate.syntax3.node.AMacro;
import org.sablecc.objectmacro.intermediate.syntax3.node.TString;
import org.sablecc.objectmacro.util.Utils;

import java.util.LinkedList;
import java.util.Map;

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
